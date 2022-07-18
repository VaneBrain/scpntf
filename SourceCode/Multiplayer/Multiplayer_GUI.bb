
Function DrawGUIMP()
	CatchErrors("DrawGUIMP")
	Local i%,x%,y%,j%
	Local yawvalue#,pitchvalue#
	Local g.Guns, fb.FuseBox, ge.Generator
	Local actualtime#
	
	If MenuOpen Lor ConsoleOpen Lor InLobby() Lor mp_I\ChatOpen Lor IsPlayerListOpen() Lor IsModerationOpen() Lor IsInVote() Then
		ShowPointer()
	Else
		HidePointer()
	EndIf
	
	If ClosestItem <> Null Then
		If ClosestItem\collider <> 0 Then
			yawvalue# = -DeltaYaw(mpl\CameraPivot, ClosestItem\collider)
			If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
			If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
			pitchvalue# = -DeltaPitch(mpl\CameraPivot, ClosestItem\collider)
			If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
			If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
			
			DrawImage(HandIcon2, opt\GraphicWidth / 2 + Sin(yawvalue) * (opt\GraphicWidth / 3) - 32, opt\GraphicHeight / 2 - Sin(pitchvalue) * (opt\GraphicHeight / 3) - 32)
		EndIf
	EndIf
	
	If DrawHandIcon Then DrawImage(HandIcon, opt\GraphicWidth / 2 - 32, opt\GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon[i] Then
			x = opt\GraphicWidth / 2 - 32
			y = opt\GraphicHeight / 2 - 32		
			Select i
				Case 0
					y = y - 64 - 5
				Case 1
					x = x + 64 + 5
				Case 2
					y = y + 64 + 5
				Case 3
					x = x - 5 - 64
			End Select
			DrawImage(HandIcon, x, y)
			Color 0, 0, 0
			Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(I_MIG\ArrowIMG[i], x + 21, y + 21)
			DrawArrowIcon[i] = False
		EndIf
	Next
	
	Local playerid% = mp_I\PlayerID
	If Players[mp_I\PlayerID]\CurrHP <= 0.0 And mp_I\SpectatePlayer >= 0 Then
		playerid = mp_I\SpectatePlayer
	EndIf
	
	Local plAmount%
	If HUDenabled And (Not InLobby()) And Players[playerid]\Team > Team_Unknown Then ;CHECK FOR IMPLEMENTATION
		Local width% = 204, height% = 20
		
		SetFont fo\Font%[Font_Digital_Medium]
		x = 80
		y = opt\GraphicHeight - 55
		;Health
		Color 0,0,0
		Rect(x - 50, y, 30, 30)
		
		If Players[playerid]\CurrHP > 20 Then
			Color 255,255,255
		Else
			Color 255,0,0
		EndIf
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		DrawImage mpl\HealthIcon, x - 50, y
		
		If Players[playerid]\CurrHP > 20
			Color 0,255,0
		Else
			Color 255,0,0
		EndIf
		TextWithAlign x + 30, y + 5, Int(Players[playerid]\CurrHP), 2
		
		;Kevlar
		Color 0,0,0
		Rect(x + 100, y, 30, 30)
		
		If Players[playerid]\CurrKevlar > 20 Then
			Color 255,255,255
		Else
			Color 255,0,0
		EndIf
		Rect(x + 100 - 1, y - 1, 30 + 2, 30 + 2, False)
		DrawImage mpl\KevlarIcon, x + 100, y
		
		If Players[playerid]\CurrKevlar > 20
			Color 0,255,0
		Else
			Color 255,0,0
		EndIf
		TextWithAlign x + 180, y + 5, Int(Players[playerid]\CurrKevlar), 2
		
		;Stamina Bar
		If Players[playerid]\CurrStamina < 100.0 Then
			y = opt\GraphicHeight - 55
			x = (opt\GraphicWidth / 2) - (width / 2) + 20
			If Players[playerid]\StaminaEffectTimer > 0 Then
				Color 0, 255, 0
			ElseIf Players[playerid]\CurrStamina <= 20.0 Then
				Color 255, 0, 0	
			Else
				Color 255, 255, 255
			EndIf
			Rect (x, y, width, height, False)
			For i = 1 To Int(((width - 2) * (Players[playerid]\CurrStamina / 100.0)) / 10)
				DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
			Next
			
			Color 0, 0, 0
			Rect(x - 50, y, 30, 30)
			
			If Players[playerid]\StaminaEffectTimer > 0 Then
				Color 0, 255, 0
			ElseIf Players[playerid]\CurrStamina <= 0.0 Then
				Color 255, 0, 0
			Else
				Color 255, 255, 255
			EndIf
			Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
			If Players[playerid]\Crouch Then
				DrawImage CrouchIcon, x - 50, y
			Else
				DrawImage SprintIcon, x - 50, y
			EndIf
		EndIf
		
		If Players[mp_I\PlayerID]\CurrHP <= 0.0 And mp_I\SpectatePlayer >= 0 Then
			Color 255, 255, 255
			SetFont fo\Font%[Font_Default_Medium]
			Text opt\GraphicWidth / 2.0, y - 50, GetLocalStringR("Multiplayer", "spectating", Players[playerid]\Name), True, False
		EndIf
		
		;Gun Stuff
		DrawMPGunsInHud(playerid)
		
		x = 55
		y = 55
		width = 64
		height = 64
		If mpl\SlotsDisplayTimer > 0.0 Then
			For i = 0 To MaxSlots-1
				DrawFrame((x-3)+(128*i),y-3,width+6,height+6)
				If i = Players[mp_I\PlayerID]\SelectedSlot Then
					Color 0,255,0
					Rect (x-3)+(128*i),y-3,width+6,height+6,True
				EndIf
				If Players[mp_I\PlayerID]\WeaponInSlot[i]<>GUN_UNARMED Then
					For g = Each Guns
						If g\ID = Players[mp_I\PlayerID]\WeaponInSlot[i] Then
							DrawImage g\IMG,x+(128*i),y
							Color 255,255,255
							If i = Players[mp_I\PlayerID]\SelectedSlot Then
								SetFont fo\Font[Font_Default]
								Text(x+(width/2)+(128*i),y+height+10,g\DisplayName,True,False)
							EndIf
							Exit
						EndIf
					Next
				EndIf
			Next
		EndIf
		
		;Boss HP bar
		If mp_I\BossNPC <> Null Then
			If mp_I\BossNPC\HP > 0 Then
				x = (opt\GraphicWidth / 2) - 202
				y = 50
				width = 404
				height = 20
				
				Color 255,255,255
				Rect(x, y, width, height, False)
				
				Color 255,0,0
				SetFont fo\Font[Font_Digital_Medium]
				Text opt\GraphicWidth/2,15,mp_I\BossNPC\NVName,True,False
				Color 255,255,255
				
				For i = 1 To Int(((width - 2) * (mp_I\BossNPC\HP / Float(mp_I\MaxBossHealth))) / 10)
					DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
				Next
			EndIf
		EndIf
		
		;Extra item display (for the fuses)
		If Players[playerid]\Item <> Null Then
			x = opt\GraphicWidth - 100
			y = opt\GraphicHeight - 200
			width = 64
			height = 64
			
			DrawFrame((x-3),y-3,width+6,height+6)
			DrawImage Players[playerid]\Item\itemtemplate\invimg,x,y
		EndIf
		
		Select mp_I\Gamemode\ID
			Case Gamemode_Waves
				;[Block]
				x = opt\GraphicWidth - 50
				y = 50
				If (mp_I\Gamemode\Phase Mod 2) = 1 Then
					Color 0,255,0
					SetFont fo\Font[Font_Digital_Medium]
					actualtime = mp_I\Gamemode\PhaseTimer
					If mp_I\Gamemode\Phase = GamemodeEnd Then
						If (Not IsInVote()) Then
							actualtime = mp_I\Gamemode\PhaseTimer - GamemodeEndTimeTotal + GamemodeEndTime
						ElseIf (Not IsVoteCompleted())
							actualtime = mp_I\Gamemode\PhaseTimer - GamemodeEndTimeAfterVote
						EndIf
					EndIf
					If TurnIntoSeconds(actualtime) < 10 Then
						TextWithAlign x,y,"0:0"+TurnIntoSeconds(actualtime),2,0
					Else
						TextWithAlign x,y,"0:"+TurnIntoSeconds(actualtime),2,0
					EndIf	
				ElseIf (mp_I\Gamemode\Phase > 0) Then
					If mp_I\Gamemode\PhaseTimer > 0 Then
						Color 0,Min(mp_I\Gamemode\PhaseTimer,127.5)*2,0
						SetFont fo\Font[Font_Digital_Large]
						Text opt\GraphicWidth/2,y*2,GetLocalString("Multiplayer", "wave")+" "+(mp_I\Gamemode\Phase/2),1,0
					EndIf
					Color 0,255,0
					SetFont fo\Font[Font_Digital_Medium]
					If mp_I\BossNPC <> Null Then
						TextWithAlign x,y,"X ???",2,1
					Else
					TextWithAlign x,y,"X "+mp_I\Gamemode\EnemyCount,2,1
					EndIf	
					DrawImage mp_I\Gamemode\img[0],x-100,y
					TextWithAlign x,y+60,GetLocalString("Multiplayer", "wave")+" "+(mp_I\Gamemode\Phase/2)+"/"+(mp_I\Gamemode\MaxPhase),2,1
				EndIf
				
				y = y + 120
				Local FusesAmount% = 0
				Local FusesActivatedAmount% = 0
				For fb = Each FuseBox
					FusesAmount% = FusesAmount + MaxFuseAmount
					FusesActivatedAmount = FusesActivatedAmount + fb\fuses
				Next
				If FusesActivatedAmount < FusesAmount Then
					TextWithAlign x,y,GetLocalString("Multiplayer", "fuses")+": " + FusesActivatedAmount + "/" + FusesAmount,2,1
				Else
					i = 1
					For ge = Each Generator
						TextWithAlign x,y+(60 * (i - 1)),GetLocalStringR("Multiplayer","generator",i)+": " + Int(ge\progress / GEN_CHARGE_TIME * 100.0) + "%/100%", 2, 1
						i = i + 1
					Next
				EndIf
				If mp_I\Gamemode\Phase = GamemodeEnd Then
					Local maxPeopleOnTeamMTF = 0
					Local peopleOnTeamMTFDead = 0
					For i = 0 To (mp_I\MaxPlayers-1)
						If Players[i]<>Null Then
							maxPeopleOnTeamMTF = maxPeopleOnTeamMTF + 1
							If Players[i]\CurrHP <= 0 Then
								peopleOnTeamMTFDead = peopleOnTeamMTFDead + 1
							EndIf
						EndIf
					Next
					x = opt\GraphicWidth / 2
					y = opt\GraphicHeight / 2
					Color 255,255,255
					If maxPeopleOnTeamMTF > 0 And peopleOnTeamMTFDead = maxPeopleOnTeamMTF Then
						SetFont fo\Font[Font_Default_Large]
						Text x,y,GetLocalString("Multiplayer", "waves_gameover_1"),True,True
						SetFont fo\Font[Font_Default_Medium]
						Text x,y+50*MenuScale,GetLocalString("Multiplayer", "contact_lost"),True,True
					Else
						SetFont fo\Font[Font_Default_Large]
						Text x,y,GetLocalStringR("Multiplayer","waves_win_1",mp_I\MapInList\BossNPC),True,True
						SetFont fo\Font[Font_Default_Medium]
						Text x,y+50*MenuScale,GetLocalString("Multiplayer", "waves_win_2"),True,True
					EndIf
				EndIf
				;[End Block]
			Case Gamemode_Deathmatch
				;[Block]
				x = opt\GraphicWidth / 2
				y = opt\GraphicHeight / 2
				If mp_I\Gamemode\Phase > Deathmatch_Game Then
					Color 255,255,255
					SetFont fo\Font[Font_Default_Large]
					Select mp_I\Gamemode\Phase
						Case Deathmatch_MTFLost
							Text x,y,GetLocalString("Multiplayer", "deathmatch_ci_win"),True,True
						Case Deathmatch_CILost
							Text x,y,GetLocalString("Multiplayer", "deathmatch_ntf_win"),True,True
						Case Deathmatch_TeamSwitch
							Text x,y,GetLocalString("Multiplayer", "switching"),True,True
							SetFont fo\Font[Font_Default_Medium]
							If Players[mp_I\PlayerID]\Team = Team_MTF Then
								Text x,y+50*MenuScale,GetLocalString("Multiplayer", "deathmatch_to_ci"),True,True
							Else
								Text x,y+50*MenuScale,GetLocalString("Multiplayer", "deathmatch_to_ntf"),True,True
							EndIf
						Case GamemodeEnd
							If mp_I\Gamemode\RoundWins[Team_MTF-1] >= 16 Then
								Text x,y,GetLocalString("Multiplayer", "deathmatch_ntf_win"),True,True
								SetFont fo\Font[Font_Default_Medium]
								Text x,y+50*MenuScale,GetLocalString("Multiplayer", "ci_neutralized"),True,True
							ElseIf mp_I\Gamemode\RoundWins[Team_CI-1] >= 16 Then
								Text x,y,GetLocalString("Multiplayer", "deathmatch_ci_win"),True,True
								SetFont fo\Font[Font_Default_Medium]
								Text x,y+50*MenuScale,GetLocalString("Multiplayer", "contact_lost"),True,True
							EndIf	
					End Select		
					SetFont fo\Font[Font_Default_Medium]
					Text x,100,GetLocalString("Multiplayer", "ntf")+"  "+mp_I\Gamemode\RoundWins[Team_MTF-1]+"  -  "+mp_I\Gamemode\RoundWins[Team_CI-1]+"  "+GetLocalString("Multiplayer", "ci"),True,True
				EndIf
				x = opt\GraphicWidth - 50
				y = 50
				If mp_I\Gamemode\PhaseTimer > 0 Then
					Color 0,255,0
					SetFont fo\Font[Font_Digital_Medium]
					actualtime = mp_I\Gamemode\PhaseTimer
					If mp_I\Gamemode\Phase = GamemodeEnd Then
						If (Not IsInVote()) Then
							actualtime = mp_I\Gamemode\PhaseTimer - GamemodeEndTimeTotal + GamemodeEndTime
						ElseIf (Not IsVoteCompleted())
							actualtime = mp_I\Gamemode\PhaseTimer - GamemodeEndTimeAfterVote
						EndIf
					EndIf
					If TurnIntoSeconds(actualtime) < 10 Then
						TextWithAlign x,y,"0:0"+TurnIntoSeconds(actualtime),2,0
					Else
						TextWithAlign x,y,"0:"+TurnIntoSeconds(actualtime),2,0
					EndIf
					If mp_I\Gamemode\Phase = Deathmatch_Game Then
						Color 0,Min(mp_I\Gamemode\PhaseTimer,127.5)*2,0
						SetFont fo\Font[Font_Digital_Large]
						Text opt\GraphicWidth/2,y*2,GetLocalString("Multiplayer", "round")+" "+(mp_I\Gamemode\RoundWins[Team_MTF-1]+mp_I\Gamemode\RoundWins[Team_CI-1]+1),1,0
					EndIf	
				EndIf
				;[End Block]
		End Select
		
		If DebugHUD Then
			Color 255, 255, 255
			SetFont fo\ConsoleFont
			
			Text 50, 20, "Delta time: "+ft\DeltaTime
			Text 50, 50, "Player Position: (" + f2s(EntityX(Players[mp_I\PlayerID]\Collider), 3) + ", " + f2s(EntityY(Players[mp_I\PlayerID]\Collider), 3) + ", " + f2s(EntityZ(Players[mp_I\PlayerID]\Collider), 3) + ")"
			Text 50, 70, "Camera Position: (" + f2s(EntityX(mpl\CameraPivot), 3) + ", " + f2s(EntityY(mpl\CameraPivot), 3) + ", " + f2s(EntityZ(mpl\CameraPivot), 3) + ")"
			Text 50, 100, "Player Rotation: (" + f2s(EntityPitch(Players[mp_I\PlayerID]\Collider), 3) + ", " + f2s(EntityYaw(Players[mp_I\PlayerID]\Collider), 3) + ", " + f2s(EntityRoll(Players[mp_I\PlayerID]\Collider), 3) + ")"
			Text 50, 120, "Camera Rotation: (" + f2s(EntityPitch(mpl\CameraPivot), 3) + ", " + f2s(EntityYaw(mpl\CameraPivot), 3) +", " + f2s(EntityRoll(mpl\CameraPivot), 3) + ")"
			
			Text 50, 300, "Stamina: " + f2s(Players[mp_I\PlayerID]\CurrStamina, 3)
			
			Text 400, 100, "Triangles rendered: "+CurrTrisAmount
			Text 400, 120, "Active textures: "+ActiveTextures()
			
			SetFont fo\Font[Font_Default]
		EndIf
	EndIf
	
	RenderCommunicationAndSocialWheel()
	
	DrawVoting()
	
	DrawPlayerList()
	
	CatchErrors("Uncaught (DrawGUIMP)")
End Function

Function UpdateGUIMP()
	Local i%
	Local plAmount%
	
	If Players[mp_I\PlayerID]\CurrHP > 0.0 Then
		For i = 0 To MaxSlots-1
			If KeyHit(i+2) Then
				If (Not InLobby()) And (Not ConsoleOpen) And (Not mp_I\ChatOpen) Then
					If Players[mp_I\PlayerID]\WeaponInSlot[i]<>GUN_UNARMED Then
						If i<>Players[mp_I\PlayerID]\SelectedSlot Then
							If mp_I\PlayState = GAME_SERVER Then
								Players[mp_I\PlayerID]\SelectedSlot = i
								g_I\GunChangeFLAG = False
							Else
								Players[mp_I\PlayerID]\WantsSlot = i
							EndIf
							mpl\SlotsDisplayTimer = 70*3
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		
		UpdateCommunicationAndSocialWheel()
	Else
		If (Not EntityHidden(mpl\WheelSprite)) Then
			UpdateCommunicationAndSocialWheel()
		EndIf
	EndIf
	mpl\SlotsDisplayTimer = Max(mpl\SlotsDisplayTimer-FPSfactor,0.0)
	
	UpdateVoting()
	
	UpdatePlayerList()
	
	If (((Not mp_I\ChatOpen) And InteractHit(1,CK_Pause)) Lor ((Not InFocus()) And (Not MenuOpen)) Lor (Steam_GetOverlayUpdated() = 1 And (Not MenuOpen))) And EndingTimer = 0 And (Not InLobby()) And (Not IsModerationOpen()) Then
		If MenuOpen Then
			If OptionsMenu <> 0 Then SaveOptionsINI()
			DeleteMenuGadgets()
			ResetInput()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMSG = 0
		mp_I\ChatOpen = False
	EndIf
	
End Function

Function DrawMPGunsInHud(playerid%)
	Local g.Guns
	Local x# = 50, x2# = 150
	Local y# = 50
	Local width#=64,height#=64
	
	Local width2%
	Local i%
	width# = 204
	height# = 20
	
	x# = opt\GraphicWidth - 60
	y# = opt\GraphicHeight - 55
	
	For g = Each Guns
		If g\ID = Players[playerid]\WeaponInSlot[Players[playerid]\SelectedSlot] Then
			If (g\GunType <> GUNTYPE_MELEE) Then
				If Players[playerid]\Ammo[Players[playerid]\SelectedSlot] > 0 Then
					Color 255,255,255
				Else
					Color 255,0,0
				EndIf
				Rect(x - 50 - 1 - 30, y - 1, 30 + 2, 30 + 2, False)
				DrawImage BulletIcon, x - 50 - 30, y
				
				SetFont fo\Font%[Font_Digital_Medium]
				If Players[playerid]\Ammo[Players[playerid]\SelectedSlot] > g\MaxCurrAmmo/5
					Color 0,255,0
				Else
					Color 255,0,0
				EndIf
				TextWithAlign x,y + 5,Players[playerid]\Ammo[Players[playerid]\SelectedSlot],2
				Color 0,255,0
				Text x,y + 5,"/"
				width2% = StringWidth("/")
				If Players[playerid]\ReloadAmmo[Players[playerid]\SelectedSlot] > 0
					Color 0,255,0
				Else
					Color 255,0,0
				EndIf
				Text x+width2,y + 5,Players[playerid]\ReloadAmmo[Players[playerid]\SelectedSlot]
			EndIf
			Exit
		EndIf
	Next
	
	Color 255,255,255
	
End Function

Function DrawMPMenu()
	CatchErrors("Uncaught (DrawMenu)")
	
	Local x%, y%, width%, height%
	Local i%
	
;	If KeyDown(15) Then
;		
;	EndIf
	
	If MenuOpen Then
		CameraProjMode Camera, 0
		CameraProjMode m_I\Cam, 1
		PositionEntity m_I\Cam,0,-1000,0
		ShowEntity m_I\MenuLogo\logo
		ShowEntity m_I\MenuLogo\gradient
		RenderWorld
		CameraProjMode Camera, 1
		CameraProjMode m_I\Cam, 0
		HideEntity m_I\MenuLogo\logo
		HideEntity m_I\MenuLogo\gradient
		
		Color 255, 255, 255
		SetFont fo\ConsoleFont
		Text 20, opt\GraphicHeight-70, "Username: "+mp_O\PlayerName
		Text 20, opt\GraphicHeight-50, "Server Name: "+mp_I\ServerName
		Text 20, opt\GraphicHeight-30, "v"+VersionNumber
		
		RenderMainMenu()
		
		;[Block]
;		width = ImageWidth(PauseMenuIMG)
;		height = ImageHeight(PauseMenuIMG)
;		x = opt\GraphicWidth / 2 - width / 2
;		y = opt\GraphicHeight / 2 - height / 2
;		
;		DrawImage PauseMenuIMG, x, y
;		
;		Color(255, 255, 255)
;		
;		x = x+132*MenuScale
;		y = y+122*MenuScale	
;		
;		If AchievementsMenu > 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "ACHIEVEMENTS",False,True)
;			SetFont fo\Font[Font_Default]
;		ElseIf OptionsMenu > 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "OPTIONS",False,True)
;			SetFont fo\Font[Font_Default]
;		ElseIf QuitMSG > 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "ARE YOU SURE?",False,True)
;			SetFont fo\Font[Font_Default]
;		ElseIf KillTimer >= 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "PAUSED",False,True)
;			SetFont fo\Font[Font_Default]
;		Else
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "YOU DIED",False,True)
;			SetFont fo\Font[Font_Default]
;		End If		
;		
;		Local AchvXIMG% = (x + (22*MenuScale))
;		Local scale# = opt\GraphicHeight/768.0
;		Local SeparationConst% = 76*scale
;		Local imgsize% = 64
;		
;		If AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG <= 0
;			SetFont fo\Font[Font_Default]
;			Text x, y, "Username: "+mp_I\PlayerName
;			;Text x, y+20*MenuScale, "Server IP: "+DottedIP(Players[0]\IP)+":"+Players[0]\Port
;			;Text x, y+40*MenuScale, "Server Name: "+
;		ElseIf AchievementsMenu <= 0 And OptionsMenu > 0 And QuitMSG <= 0 And KillTimer >= 0
;			Color 0,255,0
;			If OptionsMenu = 1
;				Rect(x-10*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
;			ElseIf OptionsMenu = 2
;				Rect(x+100*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
;			ElseIf OptionsMenu = 3
;				Rect(x+210*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
;			ElseIf OptionsMenu = 4
;				Rect(x+320*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
;			EndIf
;			
;			Local tx# = (opt\GraphicWidth/2)+(width/2)
;			Local ty# = y
;			Local tw# = 400*MenuScale
;			Local th# = 150*MenuScale
;			
;			Color 255,255,255
;			Select OptionsMenu
;				Case 1 ;Graphics
;					;[Block]
;					SetFont fo\Font[Font_Default]
;					
;					y=y+50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "VSync:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
;						DrawOptionsTooltip("vsync")
;					EndIf
;					
;					y=y+30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Anti-aliasing:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
;						DrawOptionsTooltip("antialias")
;					EndIf
;					
;					y=y+30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Enable room lights:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
;						DrawOptionsTooltip("roomlights")
;					EndIf
;					
;					y=y+30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Screen gamma")
;					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20) And OnSliderID=0
;						DrawOptionsTooltip("gamma")
;					EndIf
;					
;					y=y+50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Particle amount:")
;					If (MouseOn(x + 270 * MenuScale, y-6*MenuScale, 100*MenuScale+14, 20) And OnSliderID=0) Lor OnSliderID=2
;						DrawOptionsTooltip("particleamount",ParticleAmount)
;					EndIf
;					
;					y=y+50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Texture LOD Bias:")
;					If (MouseOn(x+270*MenuScale,y-6*MenuScale,100*MenuScale+14,20) And OnSliderID=0) Lor OnSliderID=3
;						DrawOptionsTooltip("texquality")
;					EndIf
;					
;					y=y+50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Cubemap render mode:")
;					If (MouseAndControllerSelectBox(x + 270 * MenuScale, y-6*MenuScale, 100*MenuScale+14, 20, 8, MainMenuTab) And OnSliderID=0) Lor OnSliderID=4
;						DrawOptionsTooltip("cubemap",RenderCubeMapMode)
;					EndIf
;					
;					y=y+50*MenuScale
;					Color 255,255,255
;					Text(x, y, "Field of view:")
;					Color 255,255,0
;					Text(x + 5 * MenuScale, y + 25 * MenuScale, FOV+" FOV")
;					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
;						DrawOptionsTooltip("fov")
;					EndIf
;					;[End Block]
;				Case 2 ;Audio
;					;[Block]
;					SetFont fo\Font[Font_Default]
;					
;					y = y + 50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Music volume:")
;					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
;						DrawOptionsTooltip("musicvol")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Sound volume:")
;					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
;						DrawOptionsTooltip("soundvol")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 100,100,100
;					Text x, y, "Sound auto-release:"
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("sfxautorelease")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 100,100,100
;					Text x, y, "Enable user tracks:"
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("usertrack")
;					EndIf
;					
;					If EnableUserTracks
;						y = y + 30 * MenuScale
;						Color 255,255,255
;						Text x, y, "User track mode:"
;						If UserTrackMode
;							Text x, y + 20 * MenuScale, "Repeat"
;						Else
;							Text x, y + 20 * MenuScale, "Random"
;						EndIf
;						If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;							DrawOptionsTooltip("usertrackmode")
;						EndIf
;					EndIf
;					;[End Block]
;				Case 3 ;Controls
;					;[Block]
;					SetFont fo\Font[Font_Default]
;					
;					y = y + 50*MenuScale
;					
;					Color(255, 255, 255)
;					Text(x, y, "Mouse sensitivity:")
;					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale,20)
;						DrawOptionsTooltip("mousesensitivity")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color(255, 255, 255)
;					Text(x, y, "Invert mouse Y-axis:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("mouseinvert")
;					EndIf
;					
;					y = y + 30*MenuScale
;					Text(x, y, "Control configuration:")
;					y = y + 10*MenuScale
;					
;					Text(x, y + 20 * MenuScale, "Move Forward")
;					
;					Text(x, y + 40 * MenuScale, "Strafe Left")
;					
;					Text(x, y + 60 * MenuScale, "Move Backward")
;					
;					Text(x, y + 80 * MenuScale, "Strafe Right")
;					
;					
;					Text(x, y + 100 * MenuScale, "Manual Blink")
;					
;					Text(x, y + 120 * MenuScale, "Sprint")
;					
;					Text(x, y + 140 * MenuScale, "Open/Close Inventory")
;					
;					Text(x, y + 160 * MenuScale, "Crouch")
;					
;					Text(x, y + 180 * MenuScale, "Quick Save")
;					
;					Text(x, y + 200 * MenuScale, "Open/Close Console")
;					
;					If MouseOn(x,y,300*MenuScale,220*MenuScale)
;						DrawOptionsTooltip("controls")
;					EndIf
;					;[End Block]
;				Case 4 ;Advanced
;					;[Block]
;					SetFont fo\Font[Font_Default]
;					
;					y = y + 50*MenuScale
;					
;					Color 255,255,255				
;					Text(x, y, "Show HUD:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("hud")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Enable console:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("consoleenable")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Open console on error:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("consoleerror")
;					EndIf
;					
;					y = y + 50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Achievement popups:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("achpopup")
;					EndIf
;					
;					y = y + 50*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Show FPS:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("opt\ShowFPS")
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Framelimit:")
;					
;					Color 255,255,255
;					If CurrFrameLimit>0.0
;						Color 255,255,0
;						Text(x + 5 * MenuScale, y + 25 * MenuScale, Framelimit%+" FPS")
;					EndIf
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("framelimit",Framelimit)
;					EndIf
;					If MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale,20)
;						DrawOptionsTooltip("framelimit",Framelimit)
;					EndIf
;					
;					y = y + 80*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Antialiased text:")
;					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
;						DrawOptionsTooltip("antialiastext")
;					EndIf
;					;[End Block]
;			End Select
;		ElseIf AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG > 0 And KillTimer >= 0
;;			Local QuitButton% = 60 
;;			If SelectedDifficulty\saveType = SAVEONQUIT Lor SelectedDifficulty\saveType = SAVEANYWHERE Then
;;				Local RN$ = PlayerRoom\RoomTemplate\Name$
;;				Local AbleToSave% = True
;;				If RN$ = "173" Lor RN$ = "exit1" Lor RN$ = "gatea" Then AbleToSave = False
;;				If (Not CanSave) Then AbleToSave = False
;;				If AbleToSave
;;					QuitButton = 140
;;				EndIf
;;			EndIf
;		Else
;			If AchievementsMenu>0 Then
;				For i=0 To 11
;					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
;						DrawAchvIMG(AchvXIMG,y+((i/4)*120*MenuScale),i+((AchievementsMenu-1)*12))
;					Else
;						Exit
;					EndIf
;				Next
;				
;				For i=0 To 11
;					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
;						If MouseOn(AchvXIMG+((i Mod 4)*SeparationConst),y+((i/4)*120*MenuScale),64*scale,64*scale) Then
;							AchievementTooltip(i+((AchievementsMenu-1)*12))
;							Exit
;						EndIf
;					Else
;						Exit
;					EndIf
;				Next
;				
;			EndIf
;		EndIf
;		
;		y = y+10
;		
;		If AchievementsMenu<=0 And OptionsMenu<=0 And QuitMSG<=0 Then
;			If KillTimer >= 0 Then	
;				
;				y = y+ 72*MenuScale
;				
;				y = y + 75*MenuScale
;				If (Not SelectedDifficulty\permaDeath) Then
;					If GameSaved Then
;						
;					Else
;						DrawFrame(x,y,390*MenuScale, 60*MenuScale)
;						Color (100, 100, 100)
;						SetFont fo\Font[Font_Menu]
;						Text(x + (390*MenuScale) / 2, y + (60*MenuScale) / 2, "Load Game", True, True)
;					EndIf
;					y = y + 75*MenuScale
;				EndIf
;				
;				y = y + 75*MenuScale
;				
;				y = y + 75*MenuScale
;			Else
;				y = y+104*MenuScale
;				If GameSaved And (Not SelectedDifficulty\permaDeath) Then
;					
;				Else
;					Color 50,50,50
;					Text(x + 185*MenuScale, y + 30*MenuScale, "Load Game", True, True)
;				EndIf
;				y= y + 80*MenuScale
;			EndIf
;			
;			If KillTimer >= 0 And (Not MainMenuOpen)
;				
;			EndIf
;			
;			SetFont fo\Font[Font_Default]
;			If KillTimer < 0 Then RowText(DeathMSG$, x, y + 80*MenuScale, 390*MenuScale, 600*MenuScale)
;		EndIf
;		
;		DrawAllMenuButtons()
;		DrawAllMenuTicks()
;		DrawAllMenuInputBoxes()
;		DrawAllMenuSlideBars()
;		DrawAllMenuSliders()
;		
;		If opt\DisplayMode=2 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		;[End Block]
	EndIf
	
	SetFont fo\Font[Font_Default]
	
	CatchErrors("DrawMenu")
End Function

Function UpdateMPMenu()
	CatchErrors("Uncaught (UpdateMenu)")
;	Local x%, y%, width%, height%
	
	If MenuOpen
		ShowEntity m_I\Sprite
		UpdateMainMenu()
		
		;[Block]
;		If ShouldDeleteGadgets
;			DeleteMenuGadgets()
;		EndIf
;		ShouldDeleteGadgets = False
;		
;		InvOpen = False
;		ConsoleOpen = False
;		
;		width = ImageWidth(PauseMenuIMG)
;		height = ImageHeight(PauseMenuIMG)
;		x = opt\GraphicWidth / 2 - width / 2
;		y = opt\GraphicHeight / 2 - height / 2
;		
;		x = x+132*MenuScale
;		y = y+122*MenuScale	
;		
;		If (Not MouseDown1)
;			OnSliderID = 0
;		EndIf
;		
;		Local AchvXIMG% = (x + (22*MenuScale))
;		Local scale# = opt\GraphicHeight/768.0
;		Local SeparationConst% = 76*scale
;		Local imgsize% = 64
;		
;		If AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG <= 0
;			
;		ElseIf AchievementsMenu <= 0 And OptionsMenu > 0 And QuitMSG <= 0 And KillTimer >= 0
;			If DrawButton(x + 101 * MenuScale, y + 390 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
;				AchievementsMenu = 0
;				OptionsMenu = 0
;				QuitMSG = 0
;				MouseHit1 = False
;				SaveOptionsINI()
;				
;				AntiAlias Opt_AntiAlias
;				TextureLodBias TextureFloat#
;				ShouldDeleteGadgets = True
;			EndIf
;			
;			If DrawButton(x-5*MenuScale,y,100*MenuScale,30*MenuScale,"GRAPHICS",False)
;				OptionsMenu = 1
;				ShouldDeleteGadgets = True
;			EndIf
;			If DrawButton(x+105*MenuScale,y,100*MenuScale,30*MenuScale,"AUDIO",False)
;				OptionsMenu = 2
;				ShouldDeleteGadgets = True
;			EndIf
;			If DrawButton(x+215*MenuScale,y,100*MenuScale,30*MenuScale,"CONTROLS",False)
;				OptionsMenu = 3
;				ShouldDeleteGadgets = True
;			EndIf
;			If DrawButton(x+325*MenuScale,y,100*MenuScale,30*MenuScale,"ADVANCED",False)
;				OptionsMenu = 4
;				ShouldDeleteGadgets = True
;			EndIf
;			
;			Local tx# = (opt\GraphicWidth/2)+(width/2)
;			Local ty# = y
;			Local tw# = 400*MenuScale
;			Local th# = 150*MenuScale
;			
;			Select OptionsMenu
;				Case 1 ;Graphics
;					;[Block]
;					y=y+50*MenuScale
;					
;					Vsync% = DrawTick(x + 270 * MenuScale, y + MenuScale, Vsync%)
;					
;					y=y+30*MenuScale
;					
;					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias%)
;					
;					y=y+30*MenuScale
;					
;					opt\EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, opt\EnableRoomLights)
;					
;					y=y+30*MenuScale
;					
;					ScreenGamma = (SlideBar(x + 270*MenuScale, y+6*MenuScale, 100*MenuScale, ScreenGamma*50.0)/50.0)
;					
;					y=y+50*MenuScale
;					
;					ParticleAmount = Slider3(x+270*MenuScale,y+6*MenuScale,100*MenuScale,ParticleAmount,2,"MINIMAL","REDUCED","FULL")
;					
;					y=y+50*MenuScale
;					
;					opt\TextureDetails = Slider5(x+270*MenuScale,y+6*MenuScale,100*MenuScale,opt\TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
;					Select opt\TextureDetails%
;						Case 0
;							TextureFloat# = 0.8
;						Case 1
;							TextureFloat# = 0.4
;						Case 2
;							TextureFloat# = 0.0
;						Case 3
;							TextureFloat# = -0.4
;						Case 4
;							TextureFloat# = -0.8
;					End Select
;					TextureLodBias TextureFloat
;					
;					y=y+50*MenuScale
;					
;					RenderCubeMapMode = Slider3(x+270*MenuScale,y+6*MenuScale,100*MenuScale,RenderCubeMapMode,4,"OFF","MODE 1","MODE 2")
;					
;					y=y+50*MenuScale
;					
;					Local SlideBarFOV# = FOV-40
;                    SlideBarFOV = (SlideBar(x + 270*MenuScale, y+6*MenuScale,100*MenuScale, SlideBarFOV*2.0)/2.0)
;                    FOV = SlideBarFOV+40
;					CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / (Tan((2*ATan(Tan(Float(FOV)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0)))
;					;[End Block]
;				Case 2 ;Audio
;					;[Block]
;					y = y + 50*MenuScale
;					
;					MusicVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, MusicVolume*100.0)/100.0)
;					
;					y = y + 30*MenuScale
;					
;					PrevSFXVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, opt\SFXVolume*100.0)/100.0)
;					If (Not DeafPlayer) Then opt\SFXVolume# = PrevSFXVolume#
;					
;					y = y + 30*MenuScale
;					
;					opt\EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, opt\EnableSFXRelease,True)
;					
;					y = y + 30*MenuScale
;					
;					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks,True)
;					
;					If EnableUserTracks
;						y = y + 30 * MenuScale
;						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
;					EndIf
;					;[End Block]
;				Case 3 ;Controls
;					;[Block]
;					y = y + 50*MenuScale
;					
;					MouseSens = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
;					
;					y = y + 30*MenuScale
;					
;					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
;					
;					y = y + 30*MenuScale
;					
;					y = y + 10*MenuScale
;					
;					InputBox(x + 200 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_UP,210)],5)		
;					
;					InputBox(x + 200 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_LEFT,210)],3)	
;					
;					InputBox(x + 200 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_DOWN,210)],6)				
;					
;					InputBox(x + 200 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_RIGHT,210)],4)
;					
;					
;					InputBox(x + 200 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_BLINK,210)],7)				
;					
;					InputBox(x + 200 * MenuScale, y + 120 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_SPRINT,210)],8)
;					
;					InputBox(x + 200 * MenuScale, y + 140 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_INV,210)],9)
;					
;					InputBox(x + 200 * MenuScale, y + 160 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_CROUCH,210)],10)
;					
;					InputBox(x + 200 * MenuScale, y + 180 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_SAVE,210)],11)	
;					
;					InputBox(x + 200 * MenuScale, y + 200 * MenuScale,100*MenuScale,20*MenuScale,KeyName[Min(KEY_CONSOLE,210)],12)
;					
;					
;					For i = 0 To 227
;						If KeyHit(i) Then key = i : Exit
;					Next
;					If key <> 0 Then
;						Select SelectedInputBox
;							Case 3
;								KEY_LEFT = key
;							Case 4
;								KEY_RIGHT = key
;							Case 5
;								KEY_UP = key
;							Case 6
;								KEY_DOWN = key
;							Case 7
;								KEY_BLINK = key
;							Case 8
;								KEY_SPRINT = key
;							Case 9
;								KEY_INV = key
;							Case 10
;								KEY_CROUCH = key
;							Case 11
;								KEY_SAVE = key
;							Case 12
;								KEY_CONSOLE = key
;						End Select
;						SelectedInputBox = 0
;					EndIf
;					;[End Block]
;				Case 4 ;Advanced
;					;[Block]
;					y = y + 50*MenuScale
;					
;					HUDenabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDenabled)
;					
;					y = y + 30*MenuScale
;					
;					opt\ConsoleEnabled = DrawTick(x +270 * MenuScale, y + MenuScale, opt\ConsoleEnabled)
;					
;					y = y + 30*MenuScale
;					
;					opt\ConsoleOpening = DrawTick(x + 270 * MenuScale, y + MenuScale, opt\ConsoleOpening)
;					
;					y = y + 50*MenuScale
;					
;					AchvMSGenabled% = DrawTick(x + 270 * MenuScale, y, AchvMSGenabled%)
;					
;					y = y + 50*MenuScale
;					
;					opt\ShowFPS% = DrawTick(x + 270 * MenuScale, y, opt\ShowFPS%)
;					
;					y = y + 30*MenuScale
;					
;					Local prevCurrFrameLimit = (CurrFrameLimit>0.0)
;					
;					If DrawTick(x + 270 * MenuScale, y, CurrFrameLimit > 0.0) Then
;						CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*99.0)/99.0)
;						CurrFrameLimit# = Max(CurrFrameLimit, 0.01)
;						Framelimit% = 19+(CurrFrameLimit*100.0)
;					Else
;						CurrFrameLimit# = 0.0
;						Framelimit = 0
;					EndIf
;					
;					If prevCurrFrameLimit
;						If prevCurrFrameLimit<>CurrFrameLimit
;							ShouldDeleteGadgets=True
;						EndIf
;					EndIf
;					;[End Block]
;			End Select
;		ElseIf AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG > 0 And KillTimer >= 0
;			Local QuitButton% = 60 
;			If DrawButton(x, y + QuitButton*MenuScale, 390*MenuScale, 60*MenuScale, "Yes") Then
;				NullMPGame()
;				MenuOpen = False
;				MainMenuOpen = True
;				MainMenuTab = MenuTab_Serverlist
;				FlushKeys()
;				Return
;			EndIf
;			QuitButton = 140
;			If DrawButton(x, y + QuitButton*MenuScale, 390*MenuScale, 60*MenuScale, "No") Then
;				AchievementsMenu = 0
;				OptionsMenu = 0
;				QuitMSG = 0
;				MouseHit1 = False
;				ShouldDeleteGadgets = True
;			EndIf
;		Else
;			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, "Back") Then
;				AchievementsMenu = 0
;				OptionsMenu = 0
;				QuitMSG = 0
;				MouseHit1 = False
;				ShouldDeleteGadgets = True
;			EndIf
;			
;			If AchievementsMenu>0 Then
;				If AchievementsMenu <= Floor(Float(MAXACHIEVEMENTS-1)/12.0) Then 
;					If DrawButton(x+341*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "▶", 2) Then
;						AchievementsMenu = AchievementsMenu+1
;						ShouldDeleteGadgets=True
;					EndIf
;				EndIf
;				If AchievementsMenu > 1 Then
;					If DrawButton(x+41*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "◀", 2) Then
;						AchievementsMenu = AchievementsMenu-1
;						ShouldDeleteGadgets=True
;					EndIf
;				EndIf
;			EndIf
;		EndIf
;		
;		y = y+10
;		
;		If AchievementsMenu<=0 And OptionsMenu<=0 And QuitMSG<=0 Then
;			y = y+ 72*MenuScale
;			
;			If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Resume", True, True) Then
;				MenuOpen = False
;				ResumeSounds()
;				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
;				DeleteMenuGadgets()
;				Return
;			EndIf
;			
;			y = y + 75*MenuScale
;			If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Options")
;				OptionsMenu = 1
;				ShouldDeleteGadgets = True
;			EndIf
;			y = y + 75*MenuScale
;			
;			If KillTimer >= 0 And (Not MainMenuOpen)
;				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Disconnect") Then
;					QuitMSG = 1
;					ShouldDeleteGadgets = True
;				EndIf
;			EndIf
;		EndIf
		;[End Block]
	Else
		HideEntity m_I\Sprite
	EndIf
	
	CatchErrors("UpdateMenu")
End Function

Type PlayerList
	Field IsOpen%
	Field Icons%
	Field Moderation%
	Field Reason$
End Type

Function LoadPlayerList()
	
	pll = New PlayerList
	pll\Icons = LoadAnimImage("GFX\menu\playerlist_icons.png", 30, 30, 0, 5)
	ResizeImage(pll\Icons, 30 * MenuScale, 30 * MenuScale)
	pll\Moderation = -1
	
End Function

Function UnLoadPlayerList()
	
	Delete pll
	
End Function

Function DrawPlayerList()
	Local x%, y%, width%, height%
	Local i%, j%, k%, temp$, tempheight%, lastindex%, previndex%, isfriend%
	Local drawfriendbutton%, drawmodbutton%
	
	If IsPlayerListOpen() Then
		Color 255,255,255
		
		width = opt\GraphicHeight / 1.125
		height = 40.0 * MenuScale
		
		x = (opt\GraphicWidth / 2) - (width / 2)
		y = height * 4
		If mp_I\Gamemode\ID <> Gamemode_Deathmatch Then
			y = y + height * 4
		EndIf
		
		DrawFrame(x, y, width, height * 1.5)
		SetFont fo\Font[Font_Default_Medium]
		Text(opt\GraphicWidth / 2, y + (height * 1.5 / 2), mp_I\Gamemode\name + " - " + mp_I\MapInList\Name, True, True)
		
		For i = 0 To ((MaxPlayerTeams - 1) And (mp_I\Gamemode\ID = Gamemode_Deathmatch))
			SetFont fo\Font[Font_Default_Medium]
			y = y + height * 2
			DrawFrame(x, y, width, height * 1.5)
			If i = 0 Then
				temp = GetLocalString("Multiplayer", "ntf")
			Else
				temp = GetLocalString("Multiplayer", "ci")
			EndIf
			Text(opt\GraphicWidth / 2, y + (height * 1.5 / 2), temp + " - " + mp_I\Gamemode\RoundWins[i], True, True)
			
			SetFont fo\Font[Font_Default]
			y = y + height * 1.5
			lastindex = -1
			For j = 0 To 6
				If j > 0 And lastindex > -2 Then
					previndex = lastindex
					For k = (lastindex + 1) To (mp_I\MaxPlayers - 1)
						If Players[k] <> Null Then
							If Players[k]\Team = (i+1) Then
								lastindex = k
								Exit
							EndIf
						EndIf
					Next
					If previndex = lastindex Then
						lastindex = -2
					EndIf
				EndIf
				
				tempheight = height * (1.0 - 0.4 * (j = 0))
				
				;width: 90 + 90 + 110 + 70 + 100 = 470
				DrawFrame(x, y, width - 470.0 * MenuScale, tempheight)
				If lastindex = mp_I\PlayerID Then
					Color 40,40,40
					Rect x + 2.0 * MenuScale, y + 2.0 * MenuScale, (width - 470.0 * MenuScale) - 4.0 * MenuScale, tempheight - 4.0 * MenuScale, True
					Color 255,255,255
				EndIf
				If j = 0 Then
					temp = GetLocalString("Multiplayer", "pl_players")
				ElseIf lastindex >= 0 Then
					temp = Players[lastindex]\Name
					If Players[lastindex]\CurrHP <= 0 Then
						DrawImage(pll\Icons, x + (width - 470.0 * MenuScale) - (5.0 * MenuScale) - (ImageWidth(pll\Icons)), y + 5.0 * MenuScale, 4)
						Color 100,100,100
					EndIf
				Else
					temp = ""
				EndIf
				Text(x + ((10.0 * MenuScale) * (j > 0)) + (((width - 470.0 * MenuScale) / 2) * (j = 0)), y + tempheight / 2, temp, (j = 0), True)
				x = x + (width - 470.0 * MenuScale)
				
				For k = 0 To 1
					DrawFrame(x, y, 90.0 * MenuScale, tempheight)
					If lastindex = mp_I\PlayerID Then
						Color 40,40,40
						Rect x + 2.0 * MenuScale, y + 2.0 * MenuScale, (90.0 * MenuScale) - 4.0 * MenuScale, tempheight - 4.0 * MenuScale, True
						Color 255,255,255
					EndIf
					If k = 0 Then
						If j = 0 Then
							temp = GetLocalString("Multiplayer", "pl_kills")
						ElseIf lastindex >= 0 Then
							temp = Players[lastindex]\Kills
						Else
							temp = ""
						EndIf
					Else
						If j = 0 Then
							temp = GetLocalString("Multiplayer", "pl_deaths")
						ElseIf lastindex >= 0 Then
							temp = Players[lastindex]\Deaths
						Else
							temp = ""
						EndIf
					EndIf
					Text(x + (90.0 * MenuScale / 2), y + tempheight / 2, temp, True, True)
					x = x + 90.0 * MenuScale
				Next
				
				DrawFrame(x, y, 110.0 * MenuScale, tempheight)
				If lastindex = mp_I\PlayerID Then
					Color 40,40,40
					Rect x + 2.0 * MenuScale, y + 2.0 * MenuScale, (110.0 * MenuScale) - 4.0 * MenuScale, tempheight - 4.0 * MenuScale, True
					Color 255,255,255
				EndIf
				If j = 0 Then
					temp = GetLocalString("Multiplayer", "pl_score")
				ElseIf lastindex >= 0 Then
					temp = Str(Int(500.0 * (Float(Players[lastindex]\Kills) / Float(Max(Players[lastindex]\Deaths, 1)))))
				Else
					temp = ""
				EndIf
				Text(x + (110.0 * MenuScale / 2), y + tempheight / 2, temp, True, True)
				x = x + 110.0 * MenuScale
				
				DrawFrame(x, y, 70.0 * MenuScale, tempheight)
				If lastindex = mp_I\PlayerID Then
					Color 40,40,40
					Rect x + 2.0 * MenuScale, y + 2.0 * MenuScale, (70.0 * MenuScale) - 4.0 * MenuScale, tempheight - 4.0 * MenuScale, True
					Color 255,255,255
				EndIf
				If j = 0 Then
					temp = GetLocalString("Multiplayer", "pl_ping")
				ElseIf lastindex >= 0 Then
					temp = Players[lastindex]\Ping
				Else
					temp = ""
				EndIf
				Text(x + (70.0 * MenuScale / 2), y + tempheight / 2, temp, True, True)
				x = x + 70.0 * MenuScale
				
				DrawFrame(x, y, 110.0 * MenuScale, tempheight)
				If lastindex = mp_I\PlayerID Then
					Color 40,40,40
					Rect x + 2.0 * MenuScale, y + 2.0 * MenuScale, (110.0 * MenuScale) - 4.0 * MenuScale, tempheight - 4.0 * MenuScale, True
					Color 255,255,255
				EndIf
				
				drawfriendbutton = True
				drawmodbutton = True
				If lastindex >= 0 And lastindex <> mp_I\PlayerID Then
					If mp_I\PlayState = GAME_CLIENT Then
						drawmodbutton = False
						x = x + (2 * MenuScale) + (ImageWidth(pll\Icons) / 2)
					EndIf
					
					isfriend = Steam_GetFriendRelationship(Players[lastindex]\SteamIDUpper, Players[lastindex]\SteamIDLower)
					If isfriend <> k_EFriendRelationshipNone And isfriend <> k_EFriendRelationshipBlocked And isfriend <> k_EFriendRelationshipIgnoredFriend Then
						drawfriendbutton = False
						x = x + (2 * MenuScale) + (ImageWidth(pll\Icons) / 2)
					EndIf
					
					DrawImage pll\Icons, x + 5.0 * MenuScale, y + 5.0 * MenuScale, Players[lastindex]\Ignored
					If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
						Color 150,150,150
					Else
						Color 255,255,255
					EndIf
					Rect x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons), False
					
					If drawfriendbutton Then
						x = x + 5.0 * MenuScale + ImageWidth(pll\Icons)
						DrawImage pll\Icons, x + 5.0 * MenuScale, y + 5.0 * MenuScale, 2
						If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
							Color 150,150,150
						Else
							Color 255,255,255
						EndIf
						Rect x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons), False
					EndIf
					
					If drawmodbutton Then
						x = x + 5.0 * MenuScale + ImageWidth(pll\Icons)
						DrawImage pll\Icons, x + 5.0 * MenuScale, y + 5.0 * MenuScale, 3
						If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
							Color 150,150,150
						Else
							Color 255,255,255
						EndIf
						Rect x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons), False
					EndIf
				EndIf
				
				If j < 6 Then
					y = y + tempheight
				EndIf
				x = (opt\GraphicWidth / 2) - (width / 2)
			Next
		Next
		
		If (pll\Moderation >= 0) Then
			width = opt\GraphicHeight / 1.3
			height = 150.0 * MenuScale
			x = (opt\GraphicWidth / 2) - (width / 2)
			y = (opt\GraphicHeight / 2) - (height / 2)
			
			DrawFrame(x, y, width, height)
			
			Color 255,255,255
			SetFont(fo\Font[Font_Default_Medium])
			Text(opt\GraphicWidth / 2, y + 15.0 * MenuScale, GetLocalString("Multiplayer", "kick")+"/"+GetLocalString("Multiplayer", "ban")+" "+ Players[pll\Moderation]\Name + "?", True)
			
			SetFont(fo\Font[Font_Default])
			Text(x + 15.0 * MenuScale, y + (height / 2) - 25.0 * MenuScale, GetLocalString("Multiplayer", "reason")+":")
		EndIf
	EndIf
	
	DrawAllMenuButtons()
	DrawAllMenuInputBoxes()
	
End Function

Function UpdatePlayerList()
	Local x%, y%, width%, height%
	Local i%, j%, k%, tempheight%, lastindex%, previndex%, isfriend%
	Local drawfriendbutton%, drawmodbutton%
	
	If KeyHit(KEY_INV) Then
		pll\IsOpen = Not pll\IsOpen
		pll\Moderation = -1
		pll\Reason = ""
		mp_I\ChatOpen = False
		ShouldDeleteGadgets = True
		DeleteMenuGadgets()
		ResetInput()
	EndIf
	
	If (MenuOpen Lor InLobby()) And (pll\IsOpen) Then
		pll\IsOpen = False
		pll\Moderation = -1
		pll\Reason = ""
		ShouldDeleteGadgets = True
		DeleteMenuGadgets()
	EndIf
	
	If IsPlayerListOpen() Then
		Color 255,255,255
		
		width = opt\GraphicHeight / 1.125
		height = 40.0 * MenuScale
		
		x = (opt\GraphicWidth / 2) - (width / 2)
		y = height * 4
		If mp_I\Gamemode\ID <> Gamemode_Deathmatch Then
			y = y + height * 4
		EndIf
		
		For i = 0 To ((MaxPlayerTeams - 1) And (mp_I\Gamemode\ID = Gamemode_Deathmatch))
			y = y + height * 2
			
			y = y + height * 1.5
			lastindex = -1
			For j = 0 To 6
				If j > 0 And lastindex > -2 Then
					previndex = lastindex
					For k = (lastindex + 1) To (mp_I\MaxPlayers - 1)
						If Players[k] <> Null Then
							If Players[k]\Team = (i+1) Then
								lastindex = k
								Exit
							EndIf
						EndIf
					Next
					If previndex = lastindex Then
						lastindex = -2
					EndIf
				EndIf
				
				tempheight = height * (1.0 - 0.4 * (j = 0))
				
				x = x + (width - 470.0 * MenuScale) + (90.0 * MenuScale * 2) + (110.0 * MenuScale) + (70.0 * MenuScale)
				
				drawfriendbutton = True
				drawmodbutton = True
				If lastindex >= 0 And lastindex <> mp_I\PlayerID Then
					If mp_I\PlayState = GAME_CLIENT Then
						drawmodbutton = False
						x = x + (2 * MenuScale) + (ImageWidth(pll\Icons) / 2)
					EndIf
					
					isfriend = Steam_GetFriendRelationship(Players[lastindex]\SteamIDUpper, Players[lastindex]\SteamIDLower)
					If isfriend <> k_EFriendRelationshipNone And isfriend <> k_EFriendRelationshipBlocked And isfriend <> k_EFriendRelationshipIgnoredFriend Then
						drawfriendbutton = False
						x = x + (2 * MenuScale) + (ImageWidth(pll\Icons) / 2)
					EndIf
					
					If MouseHit1 Then
						If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
							PlaySound_Strict ButtonSFX
							Players[lastindex]\Ignored = Not Players[lastindex]\Ignored
						EndIf
					EndIf
					
					If drawfriendbutton Then
						x = x + 5.0 * MenuScale + ImageWidth(pll\Icons)
						If MouseHit1 Then
							If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
								PlaySound_Strict ButtonSFX
								Steam_ActivateOverlayToUser("friendadd", Players[lastindex]\SteamIDUpper, Players[lastindex]\SteamIDLower)
							EndIf
						EndIf
					EndIf
					
					If drawmodbutton Then
						x = x + 5.0 * MenuScale + ImageWidth(pll\Icons)
						If MouseHit1 Then
							If MouseOn(x + 5.0 * MenuScale, y + 5.0 * MenuScale, ImageWidth(pll\Icons), ImageHeight(pll\Icons)) And (pll\Moderation < 0) Then
								PlaySound_Strict ButtonSFX
								pll\Moderation = lastindex
							EndIf
						EndIf
					EndIf
				EndIf
				
				If j < 6 Then
					y = y + tempheight
				EndIf
				x = (opt\GraphicWidth / 2) - (width / 2)
			Next
		Next
		
		If (pll\Moderation >= 0) Then
			width = opt\GraphicHeight / 1.3
			height = 150.0 * MenuScale
			x = (opt\GraphicWidth / 2) - (width / 2)
			y = (opt\GraphicHeight / 2) - (height / 2)
			
			pll\Reason = InputBox(x + 15.0 * MenuScale, y + (height / 2) - 10.0 * MenuScale, width - 30.0 * MenuScale, 20.0 * MenuScale, pll\Reason)
			
			If DrawButton(x + width - 45.0 * MenuScale, y + 15.0 * MenuScale, 30.0 * MenuScale, 30.0 * MenuScale, "X", False) Then
				pll\Moderation = -1
			EndIf
			
			If DrawButton(x + (width / 2) - 200.0 * MenuScale, y + height - 45.0 * MenuScale, 150.0 * MenuScale, 30.0 * MenuScale, GetLocalString("Multiplayer", "kick"), False) Then
				Steam_PushByte(PACKET_KICK)
				Steam_PushByte(SERVER_MSG_KICK_KICKED)
				Steam_PushString(pll\Reason)
				Steam_SendPacketToUser(Players[pll\Moderation]\SteamIDUpper, Players[pll\Moderation]\SteamIDLower, k_EP2PSendUnreliable)
				DeletePlayerAsServer(Steam_GetPlayerIDUpper(), Steam_GetPlayerIDLower(), pll\Moderation, "user_kicked")
				pll\Moderation = -1
			EndIf
			
			If DrawButton(x + (width / 2) + 50.0 * MenuScale, y + height - 45.0 * MenuScale, 150.0 * MenuScale, 30.0 * MenuScale, GetLocalString("Multiplayer", "ban"), False) Then
				Steam_PushByte(PACKET_KICK)
				Steam_PushByte(SERVER_MSG_KICK_BANNED)
				Steam_PushString(pll\Reason)
				Steam_SendPacketToUser(Players[pll\Moderation]\SteamIDUpper, Players[pll\Moderation]\SteamIDLower, k_EP2PSendUnreliable)
				AddBan(Players[pll\Moderation]\SteamIDUpper, Players[pll\Moderation]\SteamIDLower, pll\Reason)
				DeletePlayerAsServer(Steam_GetPlayerIDUpper(), Steam_GetPlayerIDLower(), pll\Moderation, "user_banned")
				pll\Moderation = -1
			EndIf
			
			If pll\Moderation = -1 Then
				ShouldDeleteGadgets = True
				DeleteMenuGadgets()
			EndIf
		EndIf
	EndIf
	
End Function

Function IsPlayerListOpen%()
	
	If (pll = Null) Then
		Return False
	EndIf
	Return pll\IsOpen
	
End Function

Function IsModerationOpen%()
	
	If (pll = Null) Then
		Return False
	EndIf
	Return (pll\Moderation >= 0)
	
End Function

Function UpdateVoting()
	Local x%, y%, width%, height%
	Local mfl.MapForList
	Local i%, found%
	
	If (Not IsInVote()) Then
		Return
	EndIf
	
	If (Not IsVoteCompleted()) Then
		width = ImageWidth(mp_I\NoMapImage) * 3 + 60 * MenuScale
		height = ImageHeight(mp_I\NoMapImage) + 110 * MenuScale
		x = opt\GraphicWidth / 2 - width / 2
		y = opt\GraphicHeight / 2 - height / 2
		
		x = x + 20 * MenuScale
		For i = 0 To (MAX_VOTED_MAPS - 1)
			found = False
			For mfl = Each MapForList
				If mfl\Name = mp_I\MapsToVote[i] Then
					found = True
					Exit
				EndIf
			Next
			If MouseOn(x, y + 70 * MenuScale, ImageWidth(mp_I\NoMapImage), ImageHeight(mp_I\NoMapImage)) And MouseHit1 Then
				If found Then
					Players[mp_I\PlayerID]\MapVote = (i + 1)
					PlaySound_Strict(ButtonSFX)
				Else
					PlaySound_Strict(ButtonSFX2)
				EndIf
				Exit
			EndIf
			
			x = x + ImageWidth(mp_I\NoMapImage) + 10 * MenuScale
		Next
	EndIf
	
End Function

Function DrawVoting()
	Local x%, y%, width%, height%
	Local mfl.MapForList
	Local i%, j%, found%, playerCount%, drawRect%
	
	If (Not IsInVote()) Then
		Return
	EndIf
	
	width = ImageWidth(mp_I\NoMapImage) * 3 + 60 * MenuScale
	height = ImageHeight(mp_I\NoMapImage) + 110 * MenuScale
	x = opt\GraphicWidth / 2 - width / 2
	y = opt\GraphicHeight / 2 - height / 2
	
	DrawFrame(x, y, width, height)
	
	SetFont fo\Font[Font_Default_Medium]
	Text opt\GraphicWidth / 2, y + 20 * MenuScale, Upper(GetLocalString("Multiplayer", "vote")), True
	
	x = x + 20 * MenuScale
	For i = 0 To (MAX_VOTED_MAPS - 1)
		drawRect = False
		If IsVoteCompleted() And i = (mp_I\VotedMap - 1) Then
			Color 0, 255, 0
			drawRect = True
		ElseIf i = (Players[mp_I\PlayerID]\MapVote - 1) Then
			Color 255, 255, 255
			drawRect = True
		ElseIf MouseOn(x, y + 70 * MenuScale, ImageWidth(mp_I\NoMapImage), ImageHeight(mp_I\NoMapImage)) And (Not IsVoteCompleted()) Then
			Color 100, 100, 100
			drawRect = True
		EndIf
		
		If drawRect Then
			Rect x - FRAME_THICK * MenuScale, y + 70 * MenuScale - FRAME_THICK * MenuScale, ImageWidth(mp_I\NoMapImage) + 2 * FRAME_THICK * MenuScale, ImageHeight(mp_I\NoMapImage) + 2 * FRAME_THICK * MenuScale, True
		EndIf
		Color 255, 255, 255
		
		found = False
		For mfl = Each MapForList
			If mfl\Name = mp_I\MapsToVote[i] Then
				found = True
				DrawBlock mfl\Image, x, y + 70 * MenuScale
				SetFont fo\Font[Font_Default]
				Text x + ImageWidth(mfl\Image) / 2, y + 50 * MenuScale, mfl\Name, True
				playerCount = 0
				For j = 0 To (mp_I\MaxPlayers - 1)
					If Players[j] <> Null Then
						If Players[j]\MapVote = (i + 1) Then
							playerCount = playerCount + 1
						EndIf
					EndIf
				Next
				SetFont fo\Font[Font_Default_Medium]
				Text x + ImageWidth(mfl\Image) / 2, y + height - 30 * MenuScale, playerCount, True
				Exit
			EndIf
		Next
		If (Not found) Then
			DrawBlock mp_I\NoMapImage, x, y + 70 * MenuScale
		EndIf
		
		x = x + ImageWidth(mp_I\NoMapImage) + 10 * MenuScale
	Next
	
End Function

Function IsInVote%()
	
	If mp_I\Gamemode\Phase = GamemodeEnd And mp_I\Gamemode\PhaseTimer <= GamemodeEndTimeTotal - GamemodeEndTime Then
		Return True
	EndIf
	Return False
	
End Function

Function IsVoteCompleted%()
	
	If IsInVote() And mp_I\Gamemode\PhaseTimer <= GamemodeEndTimeAfterVote Then
		Return True
	EndIf
	Return False
	
End Function

;~IDEal Editor Parameters:
;~F#1A8#1DA#1FE#388#4D3#4DA#4E3#655#65E
;~C#Blitz3D