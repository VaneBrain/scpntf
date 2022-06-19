Type ConsoleMsg
	Field txt$
	Field isCommand%
	Field r%,g%,b%
End Type

Function CreateConsoleMsg(txt$,r%=-1,g%=-1,b%=-1,isCommand%=False)
	Local c.ConsoleMsg = New ConsoleMsg
	Insert c Before First ConsoleMsg
	
	c\txt = txt
	c\isCommand = isCommand
	
	c\r = r
	c\g = g
	c\b = b
	
	If (c\r<0) Then c\r = ConsoleR
	If (c\g<0) Then c\g = ConsoleG
	If (c\b<0) Then c\b = ConsoleB
End Function

;	1: Ingame
;	2: 3D Menu
;	3: Multiplayer
Function UpdateConsole(commandSet%)
	If opt\ConsoleEnabled = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		Local x% = 0, y% = opt\GraphicHeight-300*MenuScale, width% = opt\GraphicWidth, height% = 300*MenuScale-30*MenuScale
		Local StrTemp$, temp%,  i%
		Local ev.Events, r.Rooms, it.Items, g.Guns
		
		DrawFrame x,y,width,height+30*MenuScale
		
		Local consoleHeight% = 0
		Local scrollbarHeight% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			consoleHeight = consoleHeight + 15*MenuScale
		Next
		scrollbarHeight = (Float(height)/Float(consoleHeight))*height
		If scrollbarHeight>height Then scrollbarHeight = height
		If consoleHeight<height Then consoleHeight = height
		
		Color 50,50,50
		inBar% = MouseOn(x+width-26*MenuScale,y,26*MenuScale,height)
		If inBar Then Color 70,70,70
		Rect x+width-26*MenuScale,y,26*MenuScale,height,True
		
		
		Color 120,120,120
		inBox% = MouseOn(x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight)
		If inBox Then Color 200,200,200
		If ConsoleScrollDragging Then Color 255,255,255
		Rect x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight,True
		
		If Not MouseDown(1) Then
			ConsoleScrollDragging=False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll+((ScaledMouseY()-ConsoleMouseMem)*height/scrollbarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If MouseHit1 Then
				If inBox Then
					ConsoleScrollDragging=True
					ConsoleMouseMem = ScaledMouseY()
				ElseIf inBar Then
					ConsoleScroll = ConsoleScroll+((ScaledMouseY()-(y+height))*consoleHeight/height+(height/2))
					ConsoleScroll = ConsoleScroll/2
				EndIf
			EndIf
		EndIf
		
		mouseScroll = MouseZSpeed()
		If mouseScroll=1 Then
			ConsoleScroll = ConsoleScroll - 15*MenuScale
		ElseIf mouseScroll=-1 Then
			ConsoleScroll = ConsoleScroll + 15*MenuScale
		EndIf
		
		Local reissuePos%
		If KeyHit(200) Then
			reissuePos% = 0
			If (ConsoleReissue=Null) Then
				ConsoleReissue=First ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos-15*MenuScale
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				reissuePos = reissuePos-15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=First ConsoleMsg
						reissuePos = 0
					EndIf
					
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
				CursorPos = Len(ConsoleInput)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			reissuePos% = -consoleHeight+15*MenuScale
			If (ConsoleReissue=Null) Then
				ConsoleReissue=Last ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos+15*MenuScale
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				reissuePos = reissuePos+15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=Last ConsoleMsg
						reissuePos=-consoleHeight+15*MenuScale
					EndIf
					
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
				CursorPos = Len(ConsoleInput)
			EndIf
		EndIf
		
		If ConsoleScroll<-consoleHeight+height Then ConsoleScroll = -consoleHeight+height
		If ConsoleScroll>0 Then ConsoleScroll = 0
		
		Color 255, 255, 255
		
		SelectedInputBox = 2
		Local oldConsoleInput$ = ConsoleInput
		ConsoleInput = InputBoxConsole(x, y + height, width, 30*MenuScale, ConsoleInput, 2, 100,Font_Default)
		If oldConsoleInput<>ConsoleInput Then
			ConsoleReissue = Null
		EndIf
		
		If KeyHit(28) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput,255,255,0,True)
			If Instr(ConsoleInput, " ") > 0 Then
				StrTemp$ = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp$ = Lower(ConsoleInput)
			End If
			
			Select commandSet
				Case 1
					Local Cheat.Cheats = First Cheats
					Local e.Events
					Local n.NPCs, n2.NPCs
					Select Lower(StrTemp)
						Case "help"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp$ = ""
							EndIf
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
							
							Select Lower(StrTemp)
								Case "1",""
									CreateConsoleMsg("LIST OF COMMANDS - PAGE 1/3")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("- asd")
									CreateConsoleMsg("- status")
									CreateConsoleMsg("- camerapick")
									CreateConsoleMsg("- ending")
									CreateConsoleMsg("- noclipspeed")
									CreateConsoleMsg("- noclip")
									CreateConsoleMsg("- injure [value]")
									CreateConsoleMsg("- infect [value]")
									CreateConsoleMsg("- heal")
									CreateConsoleMsg("- teleport [room name]")
									CreateConsoleMsg("- spawnitem [item name]")
									CreateConsoleMsg("- wireframe")
									CreateConsoleMsg("- 173speed")
									CreateConsoleMsg("- 106speed")
									CreateConsoleMsg("- 173state")
									CreateConsoleMsg("- 106state")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Use "+Chr(34)+"help 2/3"+Chr(34)+" to find more commands.")
									CreateConsoleMsg("Use "+Chr(34)+"help [command name]"+Chr(34)+" to get more information about a command.")
									CreateConsoleMsg("******************************")
								Case "2"
									CreateConsoleMsg("LIST OF COMMANDS - PAGE 2/3")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("- spawn [npc type] [state]")
									CreateConsoleMsg("- reset096")
									CreateConsoleMsg("- disable173")
									CreateConsoleMsg("- enable173")
									CreateConsoleMsg("- disable106")
									CreateConsoleMsg("- enable106")
									CreateConsoleMsg("- halloween")
									CreateConsoleMsg("- sanic")
									CreateConsoleMsg("- scp-420-j")
									CreateConsoleMsg("- godmode")
									CreateConsoleMsg("- revive")
									CreateConsoleMsg("- noclip")
									CreateConsoleMsg("- showFPS")
									CreateConsoleMsg("- 096state")
									CreateConsoleMsg("- debughud")
									CreateConsoleMsg("- camerafog [near] [far]")
									CreateConsoleMsg("- gamma [value]")
									CreateConsoleMsg("- infinitestamina")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Use "+Chr(34)+"help [command name]"+Chr(34)+" to get more information about a command.")
									CreateConsoleMsg("******************************")
								Case "3"
									CreateConsoleMsg("- playmusic [clip + .wav/.ogg]")
									CreateConsoleMsg("- notarget")
									CreateConsoleMsg("- unlockexits")
									CreateConsoleMsg("- infiniteammo")
									CreateConsoleMsg("- instantkill")
									CreateConsoleMsg("- noblink")
									CreateConsoleMsg("- sethp")
									CreateConsoleMsg("- setkevlar")
									CreateConsoleMsg("- setweaponammo")
									CreateConsoleMsg("- setreserveammo")
									CreateConsoleMsg("- setspeedmult")
									CreateConsoleMsg("- setstaminamult")
									CreateConsoleMsg("- setblinkmult")
									CreateConsoleMsg("- setdamagemult")
									CreateConsoleMsg("- setknockbackmult")
									CreateConsoleMsg("- setaccuracymult")
									CreateConsoleMsg("- setfireratemult")
									CreateConsoleMsg("- zoneroomlist")
									CreateConsoleMsg("- zoneitemlist")
								Case "asd"
									CreateConsoleMsg("HELP - asd")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Actives godmode, noclip, wireframe and")
									CreateConsoleMsg("sets fog distance to 20 near, 30 far")
									CreateConsoleMsg("******************************")
								Case "camerafog"
									CreateConsoleMsg("HELP - camerafog")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the draw distance of the fog.")
									CreateConsoleMsg("The fog begins generating at 'CameraFogNear' units")
									CreateConsoleMsg("away from the camera and becomes completely opaque")
									CreateConsoleMsg("at 'CameraFogFar' units away from the camera.")
									CreateConsoleMsg("Example: camerafog 20 40")
									CreateConsoleMsg("******************************")
								Case "gamma"
									CreateConsoleMsg("HELP - gamma")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the gamma correction.")
									CreateConsoleMsg("Should be set to a value between 0.0 and 2.0.")
									CreateConsoleMsg("Default is 1.0.")
									CreateConsoleMsg("******************************")
								Case "noclip","fly"
									CreateConsoleMsg("HELP - noclip")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles noclip, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Allows the camera to move in any direction while")
									CreateConsoleMsg("bypassing collision.")
									CreateConsoleMsg("******************************")
								Case "godmode","god"
									CreateConsoleMsg("HELP - godmode")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles godmode, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Prevents player death under normal circumstances.")
									CreateConsoleMsg("******************************")
								Case "wireframe"
									CreateConsoleMsg("HELP - wireframe")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles wireframe, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Allows only the edges of geometry to be rendered,")
									CreateConsoleMsg("making everything else transparent.")
									CreateConsoleMsg("******************************")
								Case "spawnitem"
									CreateConsoleMsg("HELP - spawnitem")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Spawns an item at the player's location.")
									CreateConsoleMsg("Any name that can appear in your inventory")
									CreateConsoleMsg("is a valid parameter.")
									CreateConsoleMsg("Example: spawnitem Key Card Omni")
									CreateConsoleMsg("******************************")
								Case "spawn"
									CreateConsoleMsg("HELP - spawn")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Spawns an NPC at the player's location.")
									CreateConsoleMsg("Valid parameters are:")
									CreateConsoleMsg("008zombie / 049 / 049-2 / 066 / 096 / 106 / 173")
									CreateConsoleMsg("/ 178-1 / 372 / 513-1 / 966 / 1499-1 / class-d")
									CreateConsoleMsg("/ guard / mtf / apache / tentacle")
									CreateConsoleMsg("******************************")
								Case "revive","undead","resurrect"
									CreateConsoleMsg("HELP - revive")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Resets the player's death timer after the dying")
									CreateConsoleMsg("animation triggers.")
									CreateConsoleMsg("Does not affect injury, blood loss")
									CreateConsoleMsg("or 008 infection values.")
									CreateConsoleMsg("******************************")
								Case "teleport"
									CreateConsoleMsg("HELP - teleport")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Teleports the player to the first instance")
									CreateConsoleMsg("of the specified room. Any room that appears")
									CreateConsoleMsg("in rooms.ini is a valid parameter.")
									CreateConsoleMsg("******************************")
								Case "stopsound", "stfu"
									CreateConsoleMsg("HELP - stopsound")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Stops all currently playing sounds.")
									CreateConsoleMsg("******************************")
								Case "camerapick"
									CreateConsoleMsg("HELP - camerapick")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Prints the texture name and coordinates of")
									CreateConsoleMsg("the model the camera is pointing at.")
									CreateConsoleMsg("******************************")
								Case "status"
									CreateConsoleMsg("HELP - status")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Prints player, camera, and room information.")
									CreateConsoleMsg("******************************")
								Case "weed","scp-420-j","420"
									CreateConsoleMsg("HELP - 420")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Generates dank memes.")
									CreateConsoleMsg("******************************")
								Case "playmusic"
									CreateConsoleMsg("HELP - playmusic")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Will play tracks in .ogg/.wav format")
									CreateConsoleMsg("from "+Chr(34)+"SFX\Music\Custom\"+Chr(34)+".")
									CreateConsoleMsg("******************************")
								Case "infiniteammo"
									CreateConsoleMsg("HELP - infiniteammo")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles infiniteammo, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Allows the user to shoot any weapon")
									CreateConsoleMsg("without losing ammo")
									CreateConsoleMsg("******************************")
								Case "instantkill"
									CreateConsoleMsg("HELP - instantkill")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles instantkill, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Allows the user to kill any NPC")
									CreateConsoleMsg("with one hit/shot")
									CreateConsoleMsg("******************************")
								Case "noblink"
									CreateConsoleMsg("HELP - noblink")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles noblink, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Allows the user to not blink entirely")
									CreateConsoleMsg("******************************")
								Case "sethp"
									CreateConsoleMsg("HELP - sethp")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users current HP.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Default is 100. 0 will kill you")
									CreateConsoleMsg("******************************")
								Case "setkevlar"
									CreateConsoleMsg("HELP - setkevlar")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users current kevlar amount.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Default is 100.")
									CreateConsoleMsg("******************************")
								Case "setweaponammo"
									CreateConsoleMsg("HELP - setweaponammo")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the magazine ammo for a weapon the user specifies.")
									CreateConsoleMsg("First parameter should be a weapon name (no spaces)")
									CreateConsoleMsg("Second parameter should be set to a value at 0 or above")
									CreateConsoleMsg("Default for second parameter is 'default'.")
									CreateConsoleMsg("******************************")
								Case "setreserveammo"
									CreateConsoleMsg("HELP - setreserveammo")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the reserve magazine amount for a weapon the user specifies.")
									CreateConsoleMsg("First parameter should be a weapon name (no spaces)")
									CreateConsoleMsg("Second parameter should be set to a value at 0 or above")
									CreateConsoleMsg("Default for second parameter is 'default'.")
									CreateConsoleMsg("******************************")
								Case "setspeedmult"
									CreateConsoleMsg("HELP - setspeedmult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users speed multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher speed = faster movement")
									CreateConsoleMsg("Default is 1 or 'default'")
									CreateConsoleMsg("******************************")
								Case "setstaminamult"
									CreateConsoleMsg("HELP - setstaminamult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users stamina multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = more stamina usage")
									CreateConsoleMsg("Default is 1 or 'default'")
									CreateConsoleMsg("******************************")
								Case "setblinkmult"
									CreateConsoleMsg("HELP - setblinkmult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users blinking time multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = faster blinking time")
									CreateConsoleMsg("Default is 1 or 'default'")
									CreateConsoleMsg("******************************")
								Case "setdamagemult"
									CreateConsoleMsg("HELP - setdamagemult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users gun damage multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = stronger damage against NPCs")
									CreateConsoleMsg("Default is 1 or 'default'")
									CreateConsoleMsg("******************************")
								Case "setknockbackmult"
									CreateConsoleMsg("HELP - setknockbackmult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users gun knockback (recoil) multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = stronger camera shake and recoil")
									CreateConsoleMsg("Default is 'default'")
									CreateConsoleMsg("******************************")
								Case "setaccuracymult"
									CreateConsoleMsg("HELP - setaccuracymult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users gun accuracy multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = more accuracy")
									CreateConsoleMsg("Default is 'default'")
									CreateConsoleMsg("******************************")
								Case "setfireratemult"
									CreateConsoleMsg("HELP - setfireratemult")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Sets the users fire rate multiplier.")
									CreateConsoleMsg("Should be set to a value at 0 or above")
									CreateConsoleMsg("Higher multiplier = faster firing")
									CreateConsoleMsg("Default is 'default'")
									CreateConsoleMsg("******************************")
								Case "zoneroomlist"
									CreateConsoleMsg("HELP - zoneroomlist")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Shows the current loaded rooms for the zone you are in.")
									CreateConsoleMsg("Going to different zones will show different names")
									CreateConsoleMsg("Room names may be duplicated when displaying")
									CreateConsoleMsg("******************************")
								Case "zoneitemlist"
									CreateConsoleMsg("HELP - zoneitemlist")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Shows the current loaded items for the zone you are in.")
									CreateConsoleMsg("Going to different zones will show different items")
									CreateConsoleMsg("Room items may be duplicated when displaying")
								Case "notarget"
									CreateConsoleMsg("HELP - notarget")
									CreateConsoleMsg("******************************")
									CreateConsoleMsg("Toggles notarget, unless a valid parameter")
									CreateConsoleMsg("is specified (on/off).")
									CreateConsoleMsg("Prevents NPC's from targeting the player")
									CreateConsoleMsg("******************************")
								Default
									CreateConsoleMsg("There is no help available for that command.",255,150,0)
							End Select
							
							;[End Block]
						Case "asd"
							;[Block]
							WireFrame 1
							WireframeState=1
							GodMode = 1
							NoClip = 1
							CameraFogNear = 15
							CameraFogFar = 20
							;[End Block]
						Case "status"
							;[Block]
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Status: ")
							CreateConsoleMsg("Coordinates: ")
							CreateConsoleMsg("    - collider: "+EntityX(Collider)+", "+EntityY(Collider)+", "+EntityZ(Collider))
							CreateConsoleMsg("    - camera: "+EntityX(Camera)+", "+EntityY(Camera)+", "+EntityZ(Camera))
							
							CreateConsoleMsg("Rotation: ")
							CreateConsoleMsg("    - collider: "+EntityPitch(Collider)+", "+EntityYaw(Collider)+", "+EntityRoll(Collider))
							CreateConsoleMsg("    - camera: "+EntityPitch(Camera)+", "+EntityYaw(Camera)+", "+EntityRoll(Camera))
							
							CreateConsoleMsg("Room: "+PlayerRoom\RoomTemplate\Name)
							For ev.Events = Each Events
								If ev\room = PlayerRoom Then
									CreateConsoleMsg("Room event: "+ev\EventName)	
									CreateConsoleMsg("-    state: "+ev\EventState)
									CreateConsoleMsg("-    state2: "+ev\EventState2)	
									CreateConsoleMsg("-    state3: "+ev\EventState3)
									Exit
								EndIf
							Next
							
							CreateConsoleMsg("Room coordinates: "+Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5)+", "+ Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5))
							CreateConsoleMsg("Stamina: "+Stamina)
							CreateConsoleMsg("Death timer: "+KillTimer)					
							CreateConsoleMsg("Blinktimer: "+BlinkTimer)
							CreateConsoleMsg("Health: "+psp\Health)
							CreateConsoleMsg("Kevlar: "+psp\Kevlar)
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "camerapick"
							;[Block]
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
							c = CameraPick(Camera,opt\GraphicWidth/2, opt\GraphicHeight/2)
							If c = 0 Then
								CreateConsoleMsg("******************************")
								CreateConsoleMsg("No entity  picked")
								CreateConsoleMsg("******************************")								
							Else
								CreateConsoleMsg("******************************")
								CreateConsoleMsg("Picked entity:")
								sf = GetSurface(c,1)
								b = GetSurfaceBrush( sf )
								t = GetBrushTexture(b,0)
								texname$ =  StripPath(TextureName(t))
								CreateConsoleMsg("Texture name: "+texname)
								CreateConsoleMsg("Coordinates: "+EntityX(c)+", "+EntityY(c)+", "+EntityZ(c))
								CreateConsoleMsg("******************************")							
							EndIf
							;[End Block]
						Case "hidedistance"
							;[Block]
							HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							CreateConsoleMsg("Hidedistance set to "+HideDistance)
							;[End Block]
						Case "ending"
							;[Block]
							SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							KillTimer = -0.1
							;EndingTimer = -0.1
							;[End Block]
						Case "noclipspeed"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							NoClipSpeed = Float(StrTemp)
							;[End Block]
						Case "injure"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							psp\Health = psp\Health - Float(StrTemp)
							;[End Block]
						Case "infect"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Infect = Float(StrTemp)
							;[End Block]
						Case "heal"
							;[Block]
							HealSPPlayer(100)
							psp\Kevlar = 100
							;[End Block]
						Case "teleport"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "895", "scp-895"
									StrTemp = "coffin"
								Case "scp-914"
									StrTemp = "914"
								Case "offices", "office"
									StrTemp = "room2offices"
							End Select
							
							For r.Rooms = Each Rooms
								If r\RoomTemplate\Name = StrTemp Then
									;PositionEntity (Collider, EntityX(r\obj), 0.7, EntityZ(r\obj))
									PositionEntity (Collider, EntityX(r\obj), EntityY(r\obj)+0.7, EntityZ(r\obj))
									ResetEntity(Collider)
									UpdateDoors()
									UpdateRooms()
									For it.Items = Each Items
										it\disttimer = 0
									Next
									PlayerRoom = r
									Exit
								EndIf
							Next
							
							If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("Room not found.",255,150,0)
							;[End Block]
						Case "spawnitem"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							temp = False
							
							For itt.Itemtemplates = Each ItemTemplates
								If (Lower(itt\name) = StrTemp) Then
									temp = True
									CreateConsoleMsg(itt\name + " spawned.")
									it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
									EntityType(it\collider, HIT_ITEM)
									Exit
								ElseIf (Lower(itt\tempname) = StrTemp) Then
									temp = True
									CreateConsoleMsg(itt\name + " spawned.")
									it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
									EntityType(it\collider, HIT_ITEM)
									Exit
								EndIf
							Next
							
							If temp = False Then CreateConsoleMsg("Item not found.",255,150,0)
							;[End Block]
						Case "wireframe"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									WireframeState = True							
								Case "off", "0", "false"
									WireframeState = False
								Default
									WireframeState = Not WireframeState
							End Select
							
							If WireframeState Then
								CreateConsoleMsg("WIREFRAME ON")
							Else
								CreateConsoleMsg("WIREFRAME OFF")	
							EndIf
							
							WireFrame WireframeState
							;[End Block]
						Case "173speed"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Curr173\Speed = Float(StrTemp)
							CreateConsoleMsg("173's speed set to " + StrTemp)
							;[End Block]
						Case "106speed"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Curr106\Speed = Float(StrTemp)
							CreateConsoleMsg("106's speed set to " + StrTemp)
							;[End Block]
						Case "173state"
							;[Block]
							CreateConsoleMsg("SCP-173")
							CreateConsoleMsg("Position: " + EntityX(Curr173\obj) + ", " + EntityY(Curr173\obj) + ", " + EntityZ(Curr173\obj))
							CreateConsoleMsg("Idle: " + Curr173\Idle)
							CreateConsoleMsg("State: " + Curr173\State)
							;[End Block]
						Case "106state"
							;[Block]
							CreateConsoleMsg("SCP-106")
							CreateConsoleMsg("Position: " + EntityX(Curr106\obj) + ", " + EntityY(Curr106\obj) + ", " + EntityZ(Curr106\obj))
							CreateConsoleMsg("Idle: " + Curr106\Idle)
							CreateConsoleMsg("State: " + Curr106\State)
							;[End Block]
						Case "reset096"
							;[Block]
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtype096 Then
									n\State = 0
									StopStream_Strict(n\SoundChn) : n\SoundChn=0
									If n\SoundChn2<>0
										StopStream_Strict(n\SoundChn2) : n\SoundChn2=0
									EndIf
									Exit
								EndIf
							Next
							;[End Block]
						Case "disable173"
							;[Block]
							Curr173\Idle = SCP173_DISABLED
							HideEntity Curr173\obj
							HideEntity Curr173\obj2
							HideEntity Curr173\Collider
							;[End Block]
						Case "enable173"
							;[Block]
							Curr173\Idle = SCP173_ACTIVE
							ShowEntity Curr173\obj
							ShowEntity Curr173\obj2
							ShowEntity Curr173\Collider
							;[End Block]
						Case "disable106"
							;[Block]
							Curr106\Idle = True
							Curr106\State = 200000
							Contained106 = True
							;[End Block]
						Case "enable106"
							;[Block]
							Curr106\Idle = False
							Contained106 = False
							ShowEntity Curr106\Collider
							ShowEntity Curr106\obj
							;[End Block]
						Case "halloween"
							;[Block]
							HalloweenTex = Not HalloweenTex
							If HalloweenTex Then
								Local tex = LoadTexture_Strict("GFX\npcs\173h.pt", 1)
								TextureBlend(tex,5)
								EntityTexture Curr173\obj, tex, 0, 0
								DeleteSingleTextureEntryFromCache tex
								CreateConsoleMsg("173 JACK-O-LANTERN ON")
							Else
								Local tex2 = LoadTexture_Strict("GFX\npcs\173texture.jpg", 1)
								EntityTexture Curr173\obj, tex2, 0, 0
								DeleteSingleTextureEntryFromCache tex2
								CreateConsoleMsg("173 JACK-O-LANTERN OFF")
							EndIf
							;[End Block]
						Case "sanic"
							;[Block]
							SuperMan = Not SuperMan
							If SuperMan = True Then
								CreateConsoleMsg("GOTTA GO FAST")
							Else
								Speed = 0.018
								CreateConsoleMsg("WHOA SLOW DOWN")
							EndIf
							;[End Block]
						Case "scp-420-j","420","weed"
							;[Block]
							For i = 1 To 20
								If Rand(2)=1 Then
									it.Items = CreateItem("Some SCP-420-J","420", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
								Else
									it.Items = CreateItem("Joint","420s", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
								EndIf
								EntityType (it\collider, HIT_ITEM)
							Next
							PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
							;[End Block]
						Case "godmode","god"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									GodMode = True						
								Case "off", "0", "false"
									GodMode = False
								Default
									GodMode = Not GodMode
							End Select	
							If GodMode Then
								CreateConsoleMsg("GODMODE ON")
							Else
								CreateConsoleMsg("GODMODE OFF")	
							EndIf
							;[End Block]
						Case "revive","undead","resurrect"
							;[Block]
							DropSpeed = -0.1
							HeadDropSpeed = 0.0
							Shake = 0
							CurrSpeed = 0
							
							HeartBeatVolume = 0
							
							CameraShake = 0
							Shake = 0
							LightFlash = 0
							BlurTimer = 0
							
							FallTimer = 0
							MenuOpen = False
							
							GodMode = 0
							NoClip = 0
							
							ShowEntity Collider
							
							KillTimer = 0
							KillAnim = 0
							;[End Block]
						Case "noclip","fly"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									NoClip = True
									Playable = True
								Case "off", "0", "false"
									NoClip = False	
									RotateEntity Collider, 0, EntityYaw(Collider), 0
								Default
									NoClip = Not NoClip
									If NoClip = False Then		
										RotateEntity Collider, 0, EntityYaw(Collider), 0
									Else
										Playable = True
									EndIf
							End Select
							
							If NoClip Then
								CreateConsoleMsg("NOCLIP ON")
							Else
								CreateConsoleMsg("NOCLIP OFF")
							EndIf
							
							DropSpeed = 0
							;[End Block]
						Case "showFPS"
							;[Block]
							opt\ShowFPS = Not opt\ShowFPS
							CreateConsoleMsg("ShowFPS: "+Str(opt\ShowFPS))
							;[End Block]
						Case "096state"
							;[Block]
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtype096 Then
									CreateConsoleMsg("SCP-096")
									CreateConsoleMsg("Position: " + EntityX(n\obj) + ", " + EntityY(n\obj) + ", " + EntityZ(n\obj))
									CreateConsoleMsg("Idle: " + n\Idle)
									CreateConsoleMsg("State: " + n\State)
									Exit
								EndIf
							Next
							CreateConsoleMsg("SCP-096 has not spawned.")
							;[End Block]
						Case "debughud"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Select StrTemp
								Case "on", "1", "true"
									DebugHUD = True
								Case "off", "0", "false"
									DebugHUD = False
								Default
									DebugHUD = Not DebugHUD
							End Select
							
							If DebugHUD Then
								CreateConsoleMsg("Debug Mode On")
							Else
								CreateConsoleMsg("Debug Mode Off")
							EndIf
							;[End Block]
						Case "stopsound", "stfu"
							;[Block]
							For snd.Sound = Each Sound
								For i = 0 To 31
									If snd\channels[i]<>0 Then
										StopChannel snd\channels[i]
									EndIf
								Next
							Next
							
							For e.Events = Each Events
								If e\EventName = "alarm" Then 
									If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
									If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
									If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
									
									e\room\Objects[0] = FreeEntity_Strict(e\room\Objects[0])
									e\room\Objects[1] = FreeEntity_Strict(e\room\Objects[1])
									PositionEntity Curr173\Collider, 0,0,0
									ResetEntity Curr173\Collider
									ShowEntity Curr173\obj
									RemoveEvent(e)
									Exit
								EndIf
							Next
							CreateConsoleMsg("Stopped all sounds.")
							;[End Block]
						Case "camerafog"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							CameraFogNear = Float(Left(args, Len(args) - Instr(args, " ")))
							CameraFogFar = Float(Right(args, Len(args) - Instr(args, " ")))
							CreateConsoleMsg("Near set to: " + CameraFogNear + ", far set to: " + CameraFogFar)
							;[End Block]
						Case "gamma"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							ScreenGamma = Int(StrTemp)
							CreateConsoleMsg("Gamma set to " + ScreenGamma)
							;[End Block]
						Case "spawn"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							StrTemp$ = Piece$(args$, 1)
							StrTemp2$ = Piece$(args$, 2)
							
							;Hacky fix for when the user doesn't input a second parameter.
							If (StrTemp <> StrTemp2) Then
								Console_SpawnNPC(StrTemp, StrTemp2)
							Else
								Console_SpawnNPC(StrTemp)
							EndIf
							;[End Block]
						;new Console Commands in SCP:CB 1.3 - ENDSHN
						Case "infinitestamina","infstam"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									InfiniteStamina% = True						
								Case "off", "0", "false"
									InfiniteStamina% = False
								Default
									InfiniteStamina% = Not InfiniteStamina%
							End Select
							
							If InfiniteStamina
								CreateConsoleMsg("INFINITE STAMINA ON")
							Else
								CreateConsoleMsg("INFINITE STAMINA OFF")	
							EndIf
							;[End Block]
						Case "asd2"
							;[Block]
							GodMode = 1
							InfiniteStamina = 1
							Curr173\Idle = SCP173_DISABLED
							Curr106\Idle = True
							Curr106\State = 200000
							Contained106 = True
							;[End Block]
						Case "toggle_warhead_lever"
							;[Block]
							For e.Events = Each Events
								If e\EventName = "room2nuke" Then
									e\EventState = (Not e\EventState)
									Exit
								EndIf
							Next
							;[End Block]
						Case "unlockexits"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "a"
									For e.Events = Each Events
										If e\EventName = "gateaentrance" Then
											e\EventState3 = 1
											e\room\RoomDoors[1]\open = True
											Exit
										EndIf
									Next
									CreateConsoleMsg("Gate A is now unlocked.")	
								Case "b"
									For e.Events = Each Events
										If e\EventName = "exit1" Then
											e\EventState3 = 1
											e\room\RoomDoors[4]\open = True
											Exit
										EndIf
									Next	
									CreateConsoleMsg("Gate B is now unlocked.")	
								Default
									For e.Events = Each Events
										If e\EventName = "gateaentrance" Then
											e\EventState3 = 1
											e\room\RoomDoors[1]\open = True
										ElseIf e\EventName = "exit1" Then
											e\EventState3 = 1
											e\room\RoomDoors[4]\open = True
										EndIf
									Next
									CreateConsoleMsg("Gate A and B are now unlocked.")	
							End Select
							
							RemoteDoorOn = True
							;[End Block]
						Case "kill","suicide"
							;[Block]
							If (Not GodMode) Then
								KillTimer = -1
								Select Rand(4)
									Case 1
										DeathMSG = "[REDACTED]"
									Case 2
										DeathMSG = Designation+" found dead in Sector [REDACTED]. "
										DeathMSG = DeathMSG + "The subject appears to have attained no physical damage, and there is no visible indication as to what killed him. "
										DeathMSG = DeathMSG + "Body was sent for autopsy."
									Case 3
										DeathMSG = "EXCP_ACCESS_VIOLATION"
									Case 4
										DeathMSG = Designation+" found dead in Sector [REDACTED]. "
										DeathMSG = DeathMSG + "The subject appears to have scribbled the letters "+Chr(34)+"kys"+Chr(34)+" in his own blood beside him. "
										DeathMSG = DeathMSG + "No other signs of physical trauma or struggle can be observed. Body was sent for autopsy."
								End Select
							Else
								CreateConsoleMsg("You cannot use this command if you have godmode on!", 255, 0, 0)	
							EndIf
							;[End Block]
						Case "playmusic"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp$ = ""
							EndIf
							
							If StrTemp$ <> ""
								PlayCustomMusic% = True
								If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
								If MusicCHN <> 0 Then StopChannel MusicCHN
								CustomMusic = LoadSound_Strict("SFX\Music\Custom\"+StrTemp$)
								If CustomMusic = 0
									PlayCustomMusic% = False
								EndIf
							Else
								PlayCustomMusic% = False
								If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
								If MusicCHN <> 0 Then StopChannel MusicCHN
							EndIf
							;[End Block]
;						Case "tp"
;							;[Block]
;							For n.NPCs = Each NPCs
;								If n\NPCtype = NPCtypeMTF
;									If n\MTFLeader = Null
;										PositionEntity Collider,EntityX(n\Collider),EntityY(n\Collider)+5,EntityZ(n\Collider)
;										ResetEntity Collider
;										Exit
;									EndIf
;								EndIf
;							Next
;							;[End Block]
						Case "tele"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							StrTemp$ = Piece$(args$,1," ")
							StrTemp2$ = Piece$(args$,2," ")
							StrTemp3$ = Piece$(args$,3," ")
							PositionEntity Collider,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
							PositionEntity Camera,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
							ResetEntity Collider
							ResetEntity Camera
							CreateConsoleMsg("Teleported to coordinates (X|Y|Z): "+EntityX(Collider)+"|"+EntityY(Collider)+"|"+EntityZ(Collider))
							;[End Block]
						Case "notarget"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									NoTarget% = True						
								Case "off", "0", "false"
									NoTarget% = False	
								Default
									NoTarget% = Not NoTarget%
							End Select
							
							If NoTarget% = False Then
								CreateConsoleMsg("NOTARGET OFF")
							Else
								CreateConsoleMsg("NOTARGET ON")	
							EndIf
							;[End Block]
						Case "spawnradio"
							;[Block]
							it.Items = CreateItem("Radio Transceiver", "fineradio", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							it\state = 101
							;[End Block]
						Case "spawnnvg"
							;[Block]
							it.Items = CreateItem("Night Vision Goggles", "nvgoggles", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							it\state = 1000
							;[End Block]
						Case "spawnpumpkin","pumpkin"
							;[Block]
							CreateConsoleMsg("What pumpkin?")
							;[End Block]
						Case "spawnnav"
							;[Block]
							it.Items = CreateItem("S-NAV Navigator Ultimate", "nav", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							it\state = 101
							;[End Block]
						Case "teleport173"
							;[Block]
							PositionEntity Curr173\Collider,EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider)
							ResetEntity Curr173\Collider
							;[End Block]
						Case "seteventstate"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							StrTemp$ = Piece$(args$,1," ")
							StrTemp2$ = Piece$(args$,2," ")
							StrTemp3$ = Piece$(args$,3," ")
							Local pl_room_found% = False
							If StrTemp="" Lor StrTemp2="" Lor StrTemp3=""
								CreateConsoleMsg("Too few parameters. This command requires 3.",255,150,0)
							Else
								For e.Events = Each Events
									If e\room = PlayerRoom
										If Lower(StrTemp)<>"keep"
											e\EventState = Float(StrTemp)
										EndIf
										If Lower(StrTemp2)<>"keep"
											e\EventState2 = Float(StrTemp2)
										EndIf
										If Lower(StrTemp3)<>"keep"
											e\EventState3 = Float(StrTemp3)
										EndIf
										CreateConsoleMsg("Changed event states from current player room to: "+e\EventState+"|"+e\EventState2+"|"+e\EventState3)
										pl_room_found = True
										Exit
									EndIf
								Next
								If (Not pl_room_found)
									CreateConsoleMsg("The current room doesn't has any event applied.",255,150,0)
								EndIf
							EndIf
						Case "infiniteammo"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									InfiniteAmmo% = True						
								Case "off", "0", "false"
									InfiniteAmmo% = False	
								Default
									InfiniteAmmo% = Not InfiniteAmmo%
							End Select
							
							If InfiniteAmmo% = False Then
								CreateConsoleMsg("INFINITE AMMO OFF")
							Else
								CreateConsoleMsg("INFINITE AMMO ON")	
							EndIf
						Case "instantkill"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									InstantKill% = True						
								Case "off", "0", "false"
									InstantKill% = False	
								Default
									InstantKill% = Not InstantKill%
							End Select
							
							If InstantKill% = False Then
								CreateConsoleMsg("INSTANT KILL OFF")
							Else
								CreateConsoleMsg("INSTANT KILL ON")	
							EndIf
						Case "noblink"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									NoBlink% = True						
								Case "off", "0", "false"
									NoBlink% = False	
								Default
									NoBlink% = Not NoBlink%
							End Select
							
							If NoBlink% = False Then
								CreateConsoleMsg("NO BLINK OFF")
							Else
								CreateConsoleMsg("NO BLINK ON")	
							EndIf
						Case "sethp"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: sethp (amount)", 255, 0, 0)
							Else
								Local HPInt = Int(StrTemp)
								If HPInt < 0 Then
									CreateConsoleMsg("You cannot have your health less than 0", 255, 0, 0)
								ElseIf HPInt = 0 Then
									Kill()
								Else
									psp\Health = HPInt
								EndIf
								CreateConsoleMsg("HP set to " + HPInt)
							EndIf
						Case "setkevlar"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setkevlar (amount)", 255, 0, 0)
							Else
								Local KevlarInt = Int(StrTemp)
								If KevlarInt < 0 Then
									CreateConsoleMsg("You cannot have your kevlar less than 0", 255, 0, 0)
								Else
									psp\Kevlar = Int(StrTemp)
								EndIf
								CreateConsoleMsg("Kevlar set to " + Int(psp\Kevlar))
							EndIf
						Case "setweaponammo"
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Local GunName$ = Piece$(args$, 1, " ")
							Local MagazineAmount$ = Piece$(args$, 2, " ")
							Local GunFound = False
							If GunName = "" Lor MagazineAmount = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setweaponammo (gun) (amount)", 255, 0, 0)
							Else
								If MagazineAmount = "default"
									For g = Each Guns
										If g\name = GunName Then
											g\CurrAmmo = GetINIInt("Data\weapons.ini", g\name, "ammo")
											g\MaxCurrAmmo = GetINIInt("Data\weapons.ini", g\name, "ammo")
											GunFound = True
										EndIf
									Next
									If GunFound Then
										CreateConsoleMsg(GunName + " magazine ammo set to default")
									EndIf
								Else
									Local MagazineAmountInt = Int(MagazineAmount)
									If MagazineAmountInt < 0 Then
										CreateConsoleMsg("You cannot have a magazine ammount less than 0", 255, 0, 0)
									Else
										For g = Each Guns
											If g\name = GunName Then
												g\CurrAmmo = MagazineAmountInt
												g\MaxCurrAmmo = MagazineAmountInt
												GunFound = True
											EndIf
										Next
										If GunFound Then
											CreateConsoleMsg(GunName + " magazine ammo set to " + MagazineAmountInt)
										EndIf
									EndIf
								EndIf
								If (Not GunFound) Then
									CreateConsoleMsg("Weapon " + GunName + " does not exist", 255, 0, 0)
								EndIf
							EndIf
						Case "setreserveammo"
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Local GunName2$ = Piece$(args$,1," ")
							Local ReserveAmount$ = Piece$(args$,2," ")
							Local GunFound2 = False
							If GunName2 = "" Lor ReserveAmount = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setreserveammo (gun) (amount)", 255, 0, 0)
							Else
								If ReserveAmount = "default"
									For g = Each Guns
										If g\name = GunName2 Then
											g\CurrReloadAmmo = GetINIInt("Data\weapons.ini", g\name, "reload_ammo")
											g\MaxReloadAmmo = GetINIInt("Data\weapons.ini", g\name, "reload_ammo")
											GunFound2 = True
										EndIf
									Next
									If GunFound2 Then
										CreateConsoleMsg(GunName2 + " reserve ammo set to default")
									EndIf
								Else
									Local ReserveAmountInt = Int(ReserveAmount)
									If ReserveAmountInt < 0 Then
										CreateConsoleMsg("You cannot have a reserve magazine ammount less than 0", 255, 0, 0)
									Else
										For g = Each Guns
											If g\name = GunName2 Then
												g\CurrReloadAmmo = ReserveAmountInt
												g\MaxReloadAmmo = ReserveAmountInt
												GunFound2 = True
											EndIf
										Next
										If GunFound2 Then
											CreateConsoleMsg(GunName2 + " reserve ammo set to " + ReserveAmountInt)
										EndIf
									EndIf
								EndIf
								If (Not GunFound2) Then
									CreateConsoleMsg("Weapon " + GunName2 + " does not exist", 255, 0, 0)
								EndIf
							EndIf
						Case "setspeedmult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setspeedmult (amount)", 255, 0, 0)
							Else
								If Lower(StrTemp) = "default" Then
									Speed = 0.018
									CreateConsoleMsg("Speed multiplier set to default")
								Else
									Local SpeedTemp# = Float(StrTemp)
									If SpeedTemp < 0.0 Then
										CreateConsoleMsg("You cannot have a speed multiplier less than 0", 255, 0, 0)
									Else
										Speed = SpeedTemp * 0.018
										CreateConsoleMsg("Speed multiplier set to " + SpeedTemp + "x")
									EndIf
								EndIf
							EndIf
						Case "setstaminamult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setstaminamult (amount)", 255, 0, 0)
							Else
								If Lower(StrTemp) = "default" Then
									StaminaEffect = 1.0
									StaminaEffectTimer = 2147483647
									CreateConsoleMsg("Stamina use multiplier set to default")
								Else
									StaminaEffect = Float(StrTemp)
									If StaminaEffect < 0.0 Then
										StaminaEffect = 1.0
										CreateConsoleMsg("You cannot have a stamina use multiplier less than 0", 255, 0, 0)
									Else
										StaminaEffectTimer = 2147483647
										CreateConsoleMsg("Stamina use multiplier set to " + StaminaEffect + "x")
									EndIf
								EndIf
							EndIf
							
						Case "setblinkmult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setblinkmult (amount)", 255, 0, 0)
							Else
								If Lower(StrTemp) = "default" Then
									BlinkEffect = 1.0
									BlinkEffectTimer = 2147483647
									CreateConsoleMsg("Blinking time multiplier set to default")
								Else
									BlinkEffect = Float(StrTemp)
									If BlinkEffect < 0.0 Then
										BlinkEffect = 1.0
										CreateConsoleMsg("You cannot have a blink multiplier less than 0", 255, 0, 0)
									Else
										BlinkEffectTimer = 2147483647
										CreateConsoleMsg("Blinking time multiplier set to " + BlinkEffect + "x")
									EndIf
								EndIf
							EndIf
						Case "setdamagemult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setdamagemult (amount)", 255, 0, 0)
							Else
								If Lower(StrTemp) = "default" Then
									DamageMultiplier = 1.0
									CreateConsoleMsg("Damage multiplier set to default")
								Else
									DamageMultiplier = Float(StrTemp)
									If DamageMultiplier < 0.0 Then
										DamageMultiplier = 1.0
										CreateConsoleMsg("You cannot have a damage multiplier less than 0", 255, 0, 0)
									Else
										CreateConsoleMsg("Damage multiplier set to " + DamageMultiplier + "x")
									EndIf
								EndIf
							EndIf
						Case "setknockbackmult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setknockbackmult (amount)", 255, 0, 0)
							Else
								Local KnockbackResult# = Float(StrTemp)
								If Lower(StrTemp) = "default" Then
									For g = Each Guns
										g\Knockback = GetINIFloat("Data\weapons.ini", g\name, "knockback")
									Next
									CreateConsoleMsg("Knockback multiplier set to default")
								Else
									If KnockbackResult < 0.0 Then
										CreateConsoleMsg("You cannot have a knockback multiplier less than 0", 255, 0, 0)
									Else
										For g = Each Guns
											g\Knockback = GetINIFloat("Data\weapons.ini", g\name, "knockback") * KnockbackResult
										Next
										CreateConsoleMsg("Knockback multiplier set to " + KnockbackResult + "x")
									EndIf
								EndIf
							EndIf
						Case "setaccuracymult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setaccuracymult (amount)", 255, 0, 0)
							Else
								Local AccuracyResult# = Float(StrTemp)
								If Lower(StrTemp) = "default" Then
									For g = Each Guns
										g\Accuracy = GetINIFloat("Data\weapons.ini", g\name, "accuracy")
									Next
									CreateConsoleMsg("Knockback multiplier set to default")
								Else
									If AccuracyResult < 0.0 Then
										CreateConsoleMsg("You cannot have a accuracy multiplier less than 0", 255, 0, 0)
									Else
										For g = Each Guns
											g\Accuracy = GetINIFloat("Data\weapons.ini", g\name, "accuracy") * (Float(1) / AccuracyResult)
										Next
										CreateConsoleMsg("Accuracy multiplier set to " + AccuracyResult + "x")
									EndIf
								EndIf
							EndIf
						Case "setfireratemult"
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							If StrTemp = "" Then
								CreateConsoleMsg("Too few parameters. Usage: setfireratemult (amount)", 255, 0, 0)
							Else
								Local RateOfFireResult# = Float(StrTemp)
								If Lower(StrTemp) = "default" Then
									For g = Each Guns
										g\Rate_Of_Fire = GetINIFloat("Data\weapons.ini", g\name, "rate_of_fire")
									Next
									CreateConsoleMsg("Rate of fire multiplier set to default")
								Else
									If RateOfFireResult < 0.0 Then
										CreateConsoleMsg("You cannot have a rate of fire multiplier less than 0", 255, 0, 0)
									Else
										For g = Each Guns
											g\Rate_Of_Fire = GetINIFloat("Data\weapons.ini", g\name, "rate_of_fire") * (Float(1) / RateOfFireResult)
										Next
										CreateConsoleMsg("Rate of fire multiplier set to " + RateOfFireResult + "x")
									EndIf
								EndIf
							EndIf
						Case "zoneroomlist"
							CreateConsoleMsg("List of all rooms loaded in this current zone:")
							For r = Each Rooms
								CreateConsoleMsg("- " + r\roomtemplate.roomtemplates\Name)
							Next
						Case "zoneitemlist"
							CreateConsoleMsg("List of all items loaded in this current zone:")
							For it = Each Items
								CreateConsoleMsg("- " + it\itemtemplate\tempname + " | " + it\itemtemplate\name)
							Next
							;[End Block]
;						Case "giveachievement"
							;[Block]
;							If Instr(ConsoleInput, " ")<>0 Then
;								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
;							Else
;								StrTemp$ = ""
;							EndIf
;							
;							If Int(StrTemp)>=0 And Int(StrTemp)<MAXACHIEVEMENTS
;								Achievements[Int(StrTemp)]\Unlocked=True
;								CreateConsoleMsg("Achievemt "+Achievements[Int(StrTemp)]\Name+" unlocked.")
;							Else
;								CreateConsoleMsg("Achievement with ID "+Int(StrTemp)+" doesn't exist.",255,150,0)
;							EndIf
							;[End Block]
						Case Chr($6A)+Chr($6F)+Chr($72)+Chr($67)+Chr($65)
							;[Block]
							ConsoleFlush = True 
							
							If ConsoleFlushSnd = 0 Then
								ConsoleFlushSnd = LoadSound(Chr(83)+Chr(70)+Chr(88)+Chr(92)+Chr(83)+Chr(67)+Chr(80)+Chr(92)+Chr(57)+Chr(55)+Chr(48)+Chr(92)+Chr(116)+Chr(104)+Chr(117)+Chr(109)+Chr(98)+Chr(115)+Chr(46)+Chr(100)+Chr(98))
								;FMOD_Pause(MusicCHN)
								;FMOD_Pause(MusicCHN)
								;FSOUND_Stream_Stop()
								ConsoleMusFlush% = LoadSound(Chr(83)+Chr(70)+Chr(88)+Chr(92)+Chr(77)+Chr(117)+Chr(115)+Chr(105)+Chr(99)+Chr(92)+Chr(116)+Chr(104)+Chr(117)+Chr(109)+Chr(98)+Chr(115)+Chr(46)+Chr(100)+Chr(98))
								ConsoleMusPlay = PlaySound(ConsoleMusFlush)
								CreateConsoleMsg(Chr(74)+Chr(79)+Chr(82)+Chr(71)+Chr(69)+Chr(32)+Chr(72)+Chr(65)+Chr(83)+Chr(32)+Chr(66)+Chr(69)+Chr(69)+Chr(78)+Chr(32)+Chr(69)+Chr(88)+Chr(80)+Chr(69)+Chr(67)+Chr(84)+Chr(73)+Chr(78)+Chr(71)+Chr(32)+Chr(89)+Chr(79)+Chr(85)+Chr(46))
							Else
								CreateConsoleMsg(Chr(74)+Chr(32)+Chr(79)+Chr(32)+Chr(82)+Chr(32)+Chr(71)+Chr(32)+Chr(69)+Chr(32)+Chr(32)+Chr(67)+Chr(32)+Chr(65)+Chr(32)+Chr(78)+Chr(32)+Chr(78)+Chr(32)+Chr(79)+Chr(32)+Chr(84)+Chr(32)+Chr(32)+Chr(66)+Chr(32)+Chr(69)+Chr(32)+Chr(32)+Chr(67)+Chr(32)+Chr(79)+Chr(32)+Chr(78)+Chr(32)+Chr(84)+Chr(32)+Chr(65)+Chr(32)+Chr(73)+Chr(32)+Chr(78)+Chr(32)+Chr(69)+Chr(32)+Chr(68)+Chr(46))
							EndIf
							;[End Block]
						;new Console Commands in SCP:NTF 0.2.0
						Case "teleport106"
							;[Block]
							Curr106\State = 0
							;[End Block]	
						Case "skipscene"
							;[Block]
							If PlayerRoom\RoomTemplate\Name = "gate_a_intro" Then
								CreateConsoleMsg("Skipped the intro helicopter scene.")
								For e.Events = Each Events
									If e\room = PlayerRoom Then
										EntityParent Camera,0
										PositionEntity Collider,EntityX(e\room\Objects[3],True)-0.5,EntityY(e\room\Objects[3],True)+0.5,EntityZ(e\room\Objects[3],True)+2.2
										ResetEntity Collider
										RotateEntity Camera,0,0,0
										MouseXSpeed() : MouseYSpeed()
										e\EventState = 4
									EndIf
								Next
							Else
								CreateConsoleMsg("No scene found that can be skipped.",255,150,0)
							EndIf
							;[End Block]
						Case "cd","xd"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Select StrTemp
								Case "on", "1", "true"
									Cheat\CDScream = True
								Case "off", "0", "false"
									Cheat\CDScream = False
								Default
									Cheat\CDScream = Not Cheat\CDScream
							End Select
							If Cheat\CDScream Then
								CreateConsoleMsg("CD SCREAMS ON")
							Else
								CreateConsoleMsg("CD SCREAMS OFF")	
							EndIf
							;[End Block]
						Case "mini173"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Select StrTemp
								Case "on", "1", "true"
									Cheat\Mini173 = True
								Case "off", "0", "false"
									Cheat\Mini173 = False
								Default
									Cheat\Mini173 = Not Cheat\Mini173
							End Select
							Local scale# = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "scale") / MeshDepth(Curr173\obj))
							If Cheat\Mini173 Then
								CreateConsoleMsg("MINI SCP-173 ON")
								scale# = scale# / 3.0
							Else
								CreateConsoleMsg("MINI SCP-173 OFF")	
							EndIf
							ScaleEntity Curr173\obj, scale#,scale#,scale#
							ScaleEntity Curr173\obj2, scale#,scale#,scale#
							;[End Block]
						Case "jeff"
							;[Block]
							ConsoleFlush = True 
							
							If ConsoleFlushSnd = 0 Then
								ConsoleFlushSnd = LoadSound("SFX\Player\XD\Jeff.ogg")
								ConsoleMusFlush% = LoadSound("SFX\Player\XD\JeffMusic.ogg")
								ConsoleMusPlay = PlaySound(ConsoleMusFlush)
								CreateConsoleMsg("THE INFECTION HAS STARTED")
							Else
								CreateConsoleMsg("THE INFECTION CANNOT BE CLEANSED")
							EndIf
							;[End Block]
						Case "spawnmtfzombie"
							;[Block]
							n.NPCs = CreateNPC(NPCtypeZombie,EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider))
							n\State = Z_STATE_STANDUP
							FreeNPCHitBoxes(n)
							n\obj = FreeEntity_Strict(n\obj)
							FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict("SFX\SCP\049\0492MTFBreath.ogg")
							n\Model = "GFX\npcs\zombies\MTFzombie.b3d"
							For n2.NPCs = Each NPCs
								If n2<>n And n2\NPCtype = n\NPCtype And n2\Model = n\Model Then
									n\obj = CopyEntity(n2\obj)
									Exit
								EndIf
							Next
							If n\obj = 0 Then 
								n\obj = LoadAnimMesh_Strict(n\Model)
								
								Local temp2# = (GetINIFloat("DATA\NPCs.ini", "SCP-049-2", "scale") / 2.5)
								ScaleEntity n\obj, temp2#, temp2#, temp2#
								
								MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
							EndIf
							CopyHitBoxes(n)
							;[End Block]
						Case "loadzone"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp2$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp2$ = ""
							EndIf
							If Int(StrTemp2)=NTF_CurrZone Then
								CreateConsoleMsg("You are already in that zone!",255,150,0)
							ElseIf Int(StrTemp2)>-1 And Int(StrTemp2)<6
								SaveGame(SavePath + CurrSave\Name + "\", Int(StrTemp2))
								NTF_CurrZone = Int(StrTemp2)
								SeedRnd GenerateSeedNumber(RandomSeed)
								ResetControllerSelections()
								DropSpeed = 0
								NullGame(True,False)
								LoadEntities()
								LoadAllSounds()
								If FileType(SavePath + CurrSave\Name + "\" + Int(StrTemp2) + ".ntf") = 1 Then
									LoadGame(SavePath + CurrSave\Name + "\")
									InitLoadGame()
								Else
									InitNewGame()
								EndIf
								MainMenuOpen = False
								FlushKeys()
								FlushMouse()
								FlushJoy()
								If NTF_CurrZone > 0 And NTF_CurrZone < 4 Then RotateEntity Collider,0,180,0
								If NTF_CurrZone <> EZ Then
									psp\Checkpoint106Passed = True
								EndIf
							Else
								CreateConsoleMsg("Cannot recognize zone number "+Int(StrTemp2),255,150,0)
							EndIf
							;[End Block]
						Case "owo"
							;[Block]
							Cheat\OwO = Not Cheat\OwO
							Local scaleX# = EntityScaleX(Curr173\obj)
							Local scaleY# = EntityScaleY(Curr173\obj)
							Local scaleZ# = EntityScaleZ(Curr173\obj)
							If Cheat\OwO Then
								Curr173\obj = FreeEntity_Strict(Curr173\obj)
								Curr173\obj = LoadAnimMesh_Strict("GFX\npcs\173\173body_owo.b3d")
								Animate Curr173\obj, 1, 0.5
								Curr173\obj2 = FreeEntity_Strict(Curr173\obj2)
								Curr173\obj2 = LoadAnimMesh_Strict("GFX\npcs\173\173head_owo.b3d")
								Animate Curr173\obj2, 1, 0.5
								ScaleEntity Curr173\obj, scaleX,scaleY,scaleZ
								ScaleEntity Curr173\obj2, scaleX,scaleY,scaleZ
								CreateConsoleMsg("OwO")
							Else
								Curr173\obj = FreeEntity_Strict(Curr173\obj)
								Curr173\obj = LoadMesh_Strict("GFX\npcs\173\173body.b3d")
								Curr173\obj2 = FreeEntity_Strict(Curr173\obj2)
								Curr173\obj2 = LoadMesh_Strict("GFX\npcs\173\173head.b3d")
								ScaleEntity Curr173\obj, scaleX,scaleY,scaleZ
								ScaleEntity Curr173\obj2, scaleX,scaleY,scaleZ
								CreateConsoleMsg("Aww sad...")
							EndIf
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg("Command not found.",255,0,0)
							;[End Block]
					End Select
				Case 2
					If ConsoleInput = "MemoryAccessViolation" Then RuntimeError("No")
					
					Select Lower(StrTemp)
						Case "help" ;Needs to be changed for 3D Menu TODO
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp$ = ""
							EndIf
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
							
							Select Lower(StrTemp)
								;Case "1",""
								;	CreateConsoleMsg("
								;Default
								;	CreateConsoleMsg("There is no help available for that command.",255,150,0)
							End Select
							
							;[End Block]
						Case "reinit","reload"
							;[Block]
							Reload()
							CreateConsoleMsg("Reloaded 3D menu")
							;[End Block]
						Case "reinitall","reloadall"
							;[Block]
							ReloadAll()
							CreateConsoleMsg("Reloaded entire menu")
							;[End Block]
						Case "loadmenubackground"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp$ = ""
							EndIf
							If StrTemp <> ""
								PutINIValue(gv\OptionFile,"options","progress",StrTemp)
								Null3DMenu()
								Load3DMenu(StrTemp)
								CreateConsoleMsg("Loaded 3D menu background: "+StrTemp)
							Else
								CreateConsoleMsg("A second parameter is required",255,150,0)
							EndIf
							;[End Block]
						Case "loadzone"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp2$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp2$ = ""
							EndIf
							If Int(StrTemp2)>-1 And Int(StrTemp2)<6
								CurrSave = New Save
								CurrSave\Name = "MemoryAccessFuck"
								RandomSeed = 666
								;StrTemp$ = ""
								;For i = 1 To Len(RandomSeed)
								;	StrTemp = StrTemp+Asc(Mid(RandomSeed,i,1))
								;Next
								;SeedRnd Abs(Int(StrTemp))
								
								Local SameFound% = 0
								Local LowestPossible% = 2
								
								For  I_SAV.Save = Each Save
									If (CurrSave <> I_SAV And CurrSave\Name = I_SAV\Name) Then SameFound = 1 : Exit
								Next
									
								While SameFound = 1
									SameFound = 2
									For I_SAV.Save = Each Save
										If (I_SAV\Name = (CurrSave\Name + " (" + LowestPossible + ")")) Then LowestPossible = LowestPossible + 1 : SameFound = True : Exit
									Next
								Wend
								
								If SameFound = 2 Then CurrSave\Name = CurrSave\Name + " (" + LowestPossible + ")"
								
								ResetControllerSelections()
								gopt\GameMode = gopt\SingleplayerGameMode
								MainMenuOpen = False
								Null3DMenu()
								NTF_CurrZone = Int(StrTemp2)
								DebugLog "zone loading: "+NTF_CurrZone
								LoadEntities()
								LoadAllSounds()
								InitNewGame()
								FlushKeys()
								FlushMouse()
								ConsoleOpen = False
								;Just to make sure that the game knows the player cheated already :P
								UsedConsole = True
								If NTF_CurrZone <> EZ Then
									psp\Checkpoint106Passed = True
								EndIf
								
								PutINIValue(gv\OptionFile, "options", "intro enabled", IntroEnabled%)
							Else
								CreateConsoleMsg("Cannot recognize zone number "+Int(StrTemp2),255,150,0)
							EndIf
							;[End Block]
						Case "freeroam"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									m3d\FreeRoam = True
								Case "off", "0", "false"
									m3d\FreeRoam = False	
								Default
									m3d\FreeRoam = Not m3d\FreeRoam
							End Select
							
							If m3d\FreeRoam Then
								CreateConsoleMsg("FREEROAM ON")
							Else
								CreateConsoleMsg("FREEROAM OFF")
							EndIf
							;[End Block]
						Case "loadmission"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp2$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp2$ = ""
							EndIf
							ResetControllerSelections()
							Null3DMenu()
							InitMission(Int(StrTemp2))
							LoadEntities()
							LoadAllSounds()
							gopt\GameMode = GAMEMODE_UNKNOWN
							InitMissionGameMode(Int(StrTemp2))
							MainMenuOpen = False
							FlushKeys()
							FlushMouse()
							ConsoleOpen = False
							;[End Block]
						Case "openmenutab"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp2$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp2$ = ""
							EndIf
							MainMenuTab = Int(StrTemp2)
							ConsoleOpen = False
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg("Command not found.",255,0,0)
							;[End Block]
					End Select
				Case 3
					Select Lower(StrTemp) ;Needs to be written TODO
						Case "help"
							;[Block]
							If Instr(ConsoleInput, " ")<>0 Then
								StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Else
								StrTemp$ = ""
							EndIf
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
							
							Select Lower(StrTemp)
								Case ""
									
								Default
									CreateConsoleMsg("There is no help available for that command.",255,150,0)
							End Select
							;[End Block]
						Case "camerapick"
							;[Block]
							ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
							c = CameraPick(Camera,opt\GraphicWidth/2, opt\GraphicHeight/2)
							If c = 0 Then
								CreateConsoleMsg("******************************")
								CreateConsoleMsg("No entity  picked")
								CreateConsoleMsg("******************************")								
							Else
								CreateConsoleMsg("******************************")
								CreateConsoleMsg("Picked entity:")
								sf = GetSurface(c,1)
								b = GetSurfaceBrush( sf )
								t = GetBrushTexture(b,0)
								texname$ =  StripPath(TextureName(t))
								CreateConsoleMsg("Texture name: "+texname)
								CreateConsoleMsg("Coordinates: "+EntityX(c)+", "+EntityY(c)+", "+EntityZ(c))
								CreateConsoleMsg("******************************")							
							EndIf
							;[End Block]
						Case "hidedistance"
							;[Block]
							HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							CreateConsoleMsg("Hidedistance set to "+HideDistance)
							;[End Block]
						Case "spawnitem"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							temp = False
							
							For itt.Itemtemplates = Each ItemTemplates
								If (Lower(itt\name) = StrTemp) Then
									temp = True
									CreateConsoleMsg(itt\name + " spawned.")
									it.Items = CreateItem(itt\name, itt\tempname, EntityX(Players[mp_I\PlayerID]\Collider), EntityY(mpl\CameraPivot,True), EntityZ(Players[mp_I\PlayerID]\Collider))
									EntityType(it\collider, HIT_ITEM)
									Exit
								ElseIf (Lower(itt\tempname) = StrTemp) Then
									temp = True
									CreateConsoleMsg(itt\name + " spawned.")
									it.Items = CreateItem(itt\name, itt\tempname, EntityX(Players[mp_I\PlayerID]\Collider), EntityY(mpl\CameraPivot,True), EntityZ(Players[mp_I\PlayerID]\Collider))
									EntityType(it\collider, HIT_ITEM)
									Exit
								EndIf
							Next
							
							If temp = False Then CreateConsoleMsg("Item not found.",255,150,0)
							;[End Block]
						Case "wireframe"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							Select StrTemp
								Case "on", "1", "true"
									WireframeState = True							
								Case "off", "0", "false"
									WireframeState = False
								Default
									WireframeState = Not WireframeState
							End Select
							
							If WireframeState Then
								CreateConsoleMsg("WIREFRAME ON")
							Else
								CreateConsoleMsg("WIREFRAME OFF")	
							EndIf
							
							WireFrame WireframeState
							;[End Block]
						Case "debughud"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							Select StrTemp
								Case "on", "1", "true"
									DebugHUD = True
								Case "off", "0", "false"
									DebugHUD = False
								Default
									DebugHUD = Not DebugHUD
							End Select
							
							If DebugHUD Then
								CreateConsoleMsg("Debug Mode On")
							Else
								CreateConsoleMsg("Debug Mode Off")
							EndIf
							;[End Block]
						Case "spawn"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							StrTemp$ = Piece$(args$, 1)
							StrTemp2$ = Piece$(args$, 2)
							
							;Hacky fix for when the user doesn't input a second parameter.
							If (StrTemp <> StrTemp2) Then
								Console_SpawnNPC(StrTemp, StrTemp2)
							Else
								Console_SpawnNPC(StrTemp)
							EndIf
							;[End Block]
						Case "setteam"
							;[Block]
							args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							StrTemp$ = Piece$(args$, 1)
							StrTemp2$ = Piece$(args$, 2)
							
							;StrTemp will be username
							;StrTemp2 is the team
							For i = 0 To mp_I\MaxPlayers-1
								If Players[i]<>Null Then
									If Lower(Players[i]\Name) = Trim(StrTemp2) Then
										If mp_I\Gamemode\ID <> Gamemode_Waves Lor (StrTemp = Team_MTF) Then ;CHECK FOR IMPLEMENTATION
											CreateConsoleMsg("Switched.")
											Players[i]\Team = Int(StrTemp)
											Players[i]\ForceTeam = Players[i]\Team
											SetupTeam(i)
											Exit
										Else
											CreateConsoleMsg("This team is unsupported in this gamemode.",255,0,0)
										EndIf
									EndIf
								EndIf
							Next
							;[End Block]
						Case "spectatorspeed"
							;[Block]
							StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
							
							NoClipSpeed = Float(StrTemp)
							;[End Block]
						Case "killenemies"
							;[Block]
							For n = Each NPCs
								n\HP = 0
							Next
							;[End Block]
						Case "restart"
							;[Block]
							mp_I\ResetGame = True
							;[End Block]
						Case "fillgenerators"
							;[Block]
							Local ge.Generator, fb.FuseBox
							For fb = Each FuseBox
								fb\fuses = MaxFuseAmount
							Next
							For ge = Each Generator
								ge\progress = GEN_CHARGE_TIME
							Next
							;[End Block]
						Case "endround"
							;[Block]
							If mp_I\Gamemode\ID = Gamemode_Deathmatch Then 
								If Rand(0,1) Then
									mp_I\Gamemode\Phase = Deathmatch_CILost
									mp_I\Gamemode\RoundWins[Team_MTF-1] = mp_I\Gamemode\RoundWins[Team_MTF-1] + 1
								Else
									mp_I\Gamemode\Phase = Deathmatch_MTFLost
									mp_I\Gamemode\RoundWins[Team_CI-1] = mp_I\Gamemode\RoundWins[Team_CI-1] + 1
								EndIf
								mp_I\Gamemode\PhaseTimer = 70*5
							EndIf
							;[End block]
						Case "kill","suicide"
							;[Block]
							Players[mp_I\PlayerID]\CurrHP = 0
							;[End block]
						Case "gotovote"
							;[Block]
							EndGameForVoting()
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg("Command not found.",255,0,0)
							;[End Block]
					End Select
			End Select
			
			ConsoleInput = ""
			CursorPos = 0
		End If
		
		Local TempY% = y + height - 25*MenuScale - ConsoleScroll
		Local count% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			count = count+1
			If count>1000 Then
				Delete cm
			Else
				If TempY >= y And TempY < y + height - 20*MenuScale Then
					If cm=ConsoleReissue Then
						Color cm\r/4,cm\g/4,cm\b/4
						Rect x,TempY-2*MenuScale,width-30*MenuScale,24*MenuScale,True
					EndIf
					Color cm\r,cm\g,cm\b
					If cm\isCommand Then
						Text(x + 20*MenuScale, TempY, "> "+cm\txt)
					Else
						Text(x + 20*MenuScale, TempY, cm\txt)
					EndIf
				EndIf
				TempY = TempY - 15*MenuScale
			EndIf
			
		Next
		
		Color 255,255,255
		
	EndIf
	
	SetFont fo\Font[Font_Default]
	
End Function

Function InitConsole(commandSet%)
	For c.ConsoleMsg = Each ConsoleMsg
		Delete c
	Next
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("Console commands: ")
	
	Select commandSet
		Case 1
			CreateConsoleMsg("  - teleport [room name]")
			CreateConsoleMsg("  - godmode [on/off]")
			CreateConsoleMsg("  - noclip [on/off]")
			CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
			CreateConsoleMsg("  - wireframe [on/off]")
			CreateConsoleMsg("  - debughud [on/off]")
			CreateConsoleMsg("  - camerafog [near] [far]")
			CreateConsoleMsg(" ")
			CreateConsoleMsg("  - status")
			CreateConsoleMsg("  - heal")
			CreateConsoleMsg(" ")
			CreateConsoleMsg("  - spawnitem [item name]")
			CreateConsoleMsg(" ")
			CreateConsoleMsg("  - 173speed [x] (default = 35)")
			CreateConsoleMsg("  - disable173/enable173")
			CreateConsoleMsg("  - disable106/enable106")
			CreateConsoleMsg("  - 173state/106state/096state")
			CreateConsoleMsg("  - spawn [npc type]")
		Case 2
			CreateConsoleMsg("  - reload")
			CreateConsoleMsg("  - reloadall")
			CreateConsoleMsg("  - loadmenubackground [progress]")
			CreateConsoleMsg("  - loadzone [zone number]")
		Case 3
			CreateConsoleMsg("  - camerapick")
			CreateConsoleMsg("  - spawnitem [item name]")
			CreateConsoleMsg("  - wireframe [on/off]")
			CreateConsoleMsg("  - spawn [npc type]")
			CreateConsoleMsg("  - setteam [username] [team]")
			CreateConsoleMsg("  - spectatorspeed [speed]")
	End Select
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D