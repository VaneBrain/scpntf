
;OLD
;[Block]
;Function UpdateLobby()
;	Local mp_I.MultiplayerInstance = First MultiplayerInstance
;	
;	Local getconn,giveID,TempID%,j%,i%
;	Local mfl.MapForList
;	
;	Local password$, name$
;	Local currMSGSync%
;	
;	getconn = RecvUDPMsg(mp_I\Server)
;	While getconn ;The server has received a message from a client
;		currMSGSync = ReadByte(mp_I\Server)
;		Select currMSGSync
;			Case PACKET_LOAD,PACKET_AUTHORIZE
;				;[Block]
;				CheckForConnectingPlayer(getconn, currMSGSync, 0)
;				getconn = RecvUDPMsg(mp_I\Server)
;				;[End Block]
;			Case PACKET_QUIT
;				;[Block]
;				TempID%=ReadByte(mp_I\Server)
;				If Players[TempID]<>Null Then ;this player exists: remove it
;					If Players[TempID]\IP = getconn And Players[TempID]\Port = UDPMsgPort(mp_I\Server) Then
;						mp_I\PlayerCount=mp_I\PlayerCount-1
;						UpdateServer(mp_I\Gamemode\ID,mp_I\MapInList\Name,mp_I\PlayerCount)
;						Delete Players[TempID]
;						Players[TempID]=Null
;					EndIf
;				EndIf
;				getconn = RecvUDPMsg(mp_I\Server)
;				;[End Block]
;			Case PACKET_LOBBY
;				;[Block]
;				TempID%=ReadByte(mp_I\Server)
;				If Players[TempID]<>Null Then ;player exists
;					If Players[TempID]\IP = getconn Then
;						Players[TempID]\Connected = True
;						Local ready% = ReadByte(mp_I\Server)
;						Players[TempID]\IsReady = ready%
;						Local hasSentMSG% = ReadByte(mp_I\Server)
;						If hasSentMSG Then
;							Local NewMSG$ = ReadLine(mp_I\Server)
;							AddChatMSG(NewMSG,TempID)
;							Players[TempID]\SendChatMSG = NewMSG
;						EndIf
;						Players[TempID]\LastMsgTime = MilliSecs()
;					EndIf
;				EndIf
;				getconn = RecvUDPMsg(mp_I\Server)
;				;[End Block]
;		End Select
;	Wend
;	
;	Local allPlayersReady% = True
;	For i = 0 To (mp_I\MaxPlayers-1)
;		If Players[i]<>Null Then
;			If (Not Players[i]\IsReady) Then
;				allPlayersReady = False
;				Exit
;			EndIf
;		EndIf
;	Next
;	If allPlayersReady Then
;		If mp_I\ReadyTimer > 0.0 Then
;			mp_I\ReadyTimer = Max(mp_I\ReadyTimer-FPSfactor,0)
;		Else
;			mp_I\ReadyTimer = 0.0
;		EndIf
;	Else
;		mp_I\ReadyTimer = 5*70
;	EndIf
;	
;	For i=1 To (mp_I\MaxPlayers-1)
;		If Players[i]<>Null Then
;			If Players[i]\Connected Then
;				WriteByte mp_I\Server,PACKET_LOBBY
;				For j=0 To (mp_I\MaxPlayers-1)
;					If Players[j]<>Null Then
;						WriteByte(mp_I\Server,j+1)
;						WriteLine(mp_I\Server,Players[j]\Name)
;						If j=0 Then
;							WriteByte(mp_I\Server,mp_I\IsReady)
;							If mp_I\SendChatMSG<>"" And mp_I\ShouldSendMSG Then
;								WriteByte mp_I\Server,1
;								WriteLine mp_I\Server,mp_I\SendChatMSG
;							Else
;								WriteByte mp_I\Server,0
;							EndIf
;						Else
;							WriteByte(mp_I\Server,Players[j]\IsReady)
;							If Players[j]\SendChatMSG<>"" And (j<>i) Then
;								WriteByte mp_I\Server,1
;								WriteLine mp_I\Server,Players[j]\SendChatMSG
;							Else
;								WriteByte mp_I\Server,0
;							EndIf
;						EndIf
;					Else
;						WriteByte(mp_I\Server,0)
;					EndIf
;				Next
;				WriteByte(mp_I\Server,mp_I\PlayerCount)
;				WriteFloat(mp_I\Server,mp_I\ReadyTimer)
;				SendUDPMsg mp_I\Server,Players[i]\IP,Players[i]\Port
;			EndIf
;			
;			If (MilliSecs()-Players[i]\LastMsgTime>(mp_I\TimeOut*1000)) Then ;remove client after X seconds of inactivity: assume connection was unexpectedly lost
;				;MSG
;				mp_I\PlayerCount=mp_I\PlayerCount-1
;				UpdateServer(mp_I\Gamemode\ID,mp_I\MapInList\Name,mp_I\PlayerCount)
;				Delete Players[i]
;				Players[i]=Null
;			EndIf
;		EndIf
;	Next
;	mp_I\SendChatMSG = "" ;Reset chat message (from SERVER)
;	For i=1 To (mp_I\MaxPlayers-1)
;		If Players[i]<>Null Then
;			Players[i]\SendChatMSG = "" ;Reset chat message (from CLIENT)
;		EndIf
;	Next
;	Players[0]\IsReady = mp_I\IsReady
;	
;End Function
;
;Function UpdateLobbyClient()
;	Local mp_I.MultiplayerInstance = First MultiplayerInstance
;	
;	Local getconn,i,ttmp,ttmp2$,ready%,ttmp3%
;	
;	Local currMSGSync%
;	
;	Local allPlayersReady%
;	getconn = RecvUDPMsg(mp_I\Server)
;	While getconn ;the server has given you a message
;		Players[0]\LastMsgTime = MilliSecs()
;		currMSGSync = ReadByte(mp_I\Server)
;		Select currMSGSync
;			Case PACKET_KICK
;				;[Block]
;				For i=0 To (mp_I\MaxPlayers-1)
;					If Players[i]<>Null Then
;						Delete Players[i]
;						Players[i]=Null
;					EndIf
;				Next
;				Disconnect()
;				MainMenuTab = MenuTab_Serverlist
;				SaveMSG = KICK_KICK
;				getconn = 0
;				;[End Block]
;			Case PACKET_QUIT
;				;[Block]
;				For i=0 To (mp_I\MaxPlayers-1)
;					If Players[i]<>Null
;						Delete Players[i]
;						Players[i]=Null
;					EndIf
;				Next
;				Disconnect()
;				MainMenuTab = MenuTab_Serverlist
;				SaveMSG = KICK_QUIT
;				getconn = 0
;				;[End Block]
;			Case PACKET_LOBBY
;				;[Block]
;				For i=0 To (mp_I\MaxPlayers-1)
;					ttmp% = ReadByte(mp_I\Server)
;					If ttmp% Then
;						ttmp2$ = ReadLine(mp_I\Server)
;						If Players[i]=Null
;							Players[i] = New Player
;						EndIf
;						Players[i]\Name = ttmp2
;						ready = ReadByte(mp_I\Server)
;						Players[i]\IsReady = ready
;						Local hasSentMSG% = ReadByte(mp_I\Server)
;						If hasSentMSG
;							Local NewMSG$ = ReadLine(mp_I\Server)
;							AddChatMSG(NewMSG,i)
;						EndIf
;					Else
;						If Players[i]<>Null Then
;							Delete Players[i]
;							Players[i] = Null
;						EndIf
;					EndIf
;				Next
;				mp_I\PlayerCount = ReadByte(mp_I\Server)
;				mp_I\ReadyTimer = ReadFloat(mp_I\Server)
;				
;				getconn = RecvUDPMsg(mp_I\Server)
;				;[End Block]
;		End Select
;	Wend
;	
;	If mp_I\Server<>0 Then
;		WriteByte mp_I\Server,PACKET_LOBBY
;		WriteByte mp_I\Server,mp_I\PlayerID
;		WriteByte mp_I\Server,mp_I\IsReady
;		If mp_I\SendChatMSG<>"" And mp_I\ShouldSendMSG Then
;			WriteByte mp_I\Server,1
;			WriteLine mp_I\Server,mp_I\SendChatMSG
;		Else
;			WriteByte mp_I\Server,0
;		EndIf
;		;Reset chat message, so the chat won't be spammed with the same message automatically
;		mp_I\SendChatMSG = ""
;		SendUDPMsg(mp_I\Server,Players[0]\IP,Players[0]\Port)
;		If (MilliSecs()-Players[0]\LastMsgTime>(mp_I\TimeOut*1000)) Then ;disconnect after X seconds of inactivity: assume connection was unexpectedly lost
;			For i=0 To (mp_I\MaxPlayers-1)
;				If Players[i]<>Null Then
;					Delete Players[i]
;					Players[i]=Null
;				EndIf
;			Next
;			Disconnect()
;			MainMenuTab = MenuTab_Serverlist
;			SaveMSG = KICK_TIMEOUT
;		EndIf
;	EndIf
;	
;End Function
;[End Block]

Function UpdateLobby()
	Local x#,y#,width#,height#,i%,j%,buttontext$
	Local allReady%, plAmount%
	
	;[Block]
;	Select mp_I\Gamemode\ID
;		Case Gamemode_Deathmatch
;			;[Block]
;			x = opt\GraphicWidth / 2
;			y = opt\GraphicHeight / 2
;			;TODO: Add the ability to select it with controller
;			;TODO: Make it so the server needs to verify if the player can join the team
;			;TODO: When more guns are implemented, then there should be a function to reset their shooting states properly
;			If Players[mp_I\PlayerID]\Team = Team_Unknown Then
;				If MouseHit1 Then
;					;MTF
;					If MouseOn(x - 325 * MenuScale, y - 325 * MenuScale, ImageWidth(mp_I\Gamemode\img[Team_MTF-1]), ImageHeight(mp_I\Gamemode\img[Team_MTF-1])) Then
;						plAmount = 0
;						For i = 0 To (mp_I\MaxPlayers-1)
;							If Players[i]<>Null Then
;								If Players[i]\Team = Team_MTF Then
;									plAmount = plAmount + 1
;								EndIf
;							EndIf
;						Next
;						If plAmount < Ceil(mp_I\MaxPlayers/2) Then
;							Players[mp_I\PlayerID]\Team = Team_MTF
;							ResetControllerSelections()
;							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90" Then
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
;							SetupTeam(mp_I\PlayerID)
;						EndIf
;					EndIf
;					;CI
;					If MouseOn(x + 25 * MenuScale, y - 325 * MenuScale, ImageWidth(mp_I\Gamemode\img[Team_CI-1]), ImageHeight(mp_I\Gamemode\img[Team_CI-1])) Then
;						plAmount = 0
;						For i = 0 To (mp_I\MaxPlayers-1)
;							If Players[i]<>Null Then
;								If Players[i]\Team = Team_CI Then
;									plAmount = plAmount + 1
;								EndIf
;							EndIf
;						Next
;						If plAmount < Ceil(mp_I\MaxPlayers/2) Then
;							Players[mp_I\PlayerID]\Team = Team_CI
;							ResetControllerSelections()
;							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90" Then
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
;							SetupTeam(mp_I\PlayerID)
;						EndIf
;					EndIf
;				EndIf
;			EndIf
;			;[End Block]
;	End Select
	;[End Block]
	
	If InLobby() Then
		width = 800.0 * MenuScale
		height = 800.0 * MenuScale
		x = opt\GraphicWidth / 2.0 - (width / 2.0)
		y = opt\GraphicHeight / 2.0 - (height / 2.0)
		
		mp_I\CurrChatMSG = InputBox(x, y + (height - 30.0 * MenuScale), width - 330.0 * MenuScale, 30.0 * MenuScale, mp_I\CurrChatMSG, 1, 50)
		If DrawButton(x + (width - 331.0 * MenuScale), y + (height - 30.0 * MenuScale), 30.0 * MenuScale, 30.0 * MenuScale, Chr(62), False, False, True) Lor (SelectedInputBox=1 And KeyHit(28)) Then
			If mp_I\CurrChatMSG <> "" Then
				CreateChatMSG()
				SelectedInputBox = 1
			EndIf
		EndIf
		
		If DrawButton(x, y + height + 10.0 * MenuScale, 195.0 * MenuScale, 60.0 * MenuScale, GetLocalString("Menu", "disconnect"), 3) Then
			LeaveMPGame(True)
			Return
		EndIf
		;TODO: When in team selection, all the buttons below should not be clickable anymore
		If mp_I\PlayState = GAME_SERVER Then
			allReady = True
			For i = 0 To (mp_I\MaxPlayers-1)
				If Players[i]<>Null Then
					If (Not Players[i]\IsReady) Then
						allReady = False
						Exit
					EndIf
				EndIf
			Next
			If allReady Then
				mp_I\ReadyTimer = Max(mp_I\ReadyTimer - FPSfactor, 0.0)
			Else
				mp_I\ReadyTimer = 70*5
			EndIf
			
			If DrawButton(x + 201.667 * MenuScale, y + height + 10.0 * MenuScale, 195.0 * MenuScale, 60.0 * MenuScale, GetLocalString("Lobby", "force_start"), 3) Then
				mp_I\ReadyTimer = 0.0
			EndIf
			
			If mp_I\ReadyTimer = 0.0 Then
				ResetControllerSelections()
				ResetInput()
			EndIf
		EndIf
		If mp_I\IsReady Then
			buttontext = GetLocalString("Lobby", "notready")
		Else
			buttontext = GetLocalString("Lobby", "ready")
		EndIf
		If mp_I\Gamemode\ID <> Gamemode_Deathmatch Lor Players[mp_I\PlayerID]\Team > Team_Unknown Then
			If DrawButton(x + 403.333 * MenuScale, y + height + 10.0 * MenuScale, 195.0 * MenuScale, 60.0 * MenuScale, buttontext, 3) Then
				mp_I\IsReady = (Not mp_I\IsReady)
			EndIf
		EndIf
		If mp_I\Gamemode\ID = Gamemode_Deathmatch Then
			If DrawButton(x + 605.0 * MenuScale, y + height + 10.0 * MenuScale, 195.0 * MenuScale, 60.0 * MenuScale, GetLocalString("Lobby", "change_team"), 3) Then
				If mp_I\PlayState = GAME_SERVER Then
					Players[mp_I\PlayerID]\Team = Team_Unknown
				Else
					Players[mp_I\PlayerID]\WantsTeam = Team_Unknown
				EndIf
				mp_I\IsReady = False
				DeleteMenuGadgets()
			EndIf
		EndIf
		
		If ((mp_I\PlayState = GAME_CLIENT And Players[mp_I\PlayerID]\WantsTeam = Team_Unknown) Lor (mp_I\PlayState = GAME_SERVER And Players[mp_I\PlayerID]\Team = Team_Unknown)) Then
			y = y + (height / 4.0) + 100.0 * MenuScale
			For i = 0 To MaxPlayerTeams-1
				x = (opt\GraphicWidth / 2.0) - (width / 3.2) + 15.0 * MenuScale + ((ImageWidth(mp_I\Gamemode\img[i]) + 10.0 * MenuScale) * i)
				If MouseHit1 And MouseOn(x, y, ImageWidth(mp_I\Gamemode\img[i]), ImageHeight(mp_I\Gamemode\img[i])) Then
					plAmount = 0
					For j = 0 To (mp_I\MaxPlayers-1)
						If Players[j]<>Null Then
							If Players[j]\Team = (i+1) Then
								plAmount = plAmount + 1
							EndIf
						EndIf
					Next
					If plAmount < Ceil(mp_I\MaxPlayers/2) Then
						If mp_I\PlayState = GAME_SERVER Then
							Players[mp_I\PlayerID]\Team = Team_MTF + i
							SetupTeam(mp_I\PlayerID)
						Else
							Players[mp_I\PlayerID]\WantsTeam = Team_MTF + i
						EndIf
					EndIf
				EndIf
			Next
		EndIf
		
		CountdownBeep(mp_I\ReadyTimer, 3)
		
		If mp_I\ReadyTimer = 0.0 And ((mp_I\PlayState = GAME_CLIENT And Players[mp_I\PlayerID]\WantsTeam > Team_Unknown) Lor (mp_I\PlayState = GAME_SERVER And Players[mp_I\PlayerID]\Team > Team_Unknown)) Then
			ShouldDeleteGadgets = True
			DeleteMenuGadgets()
		EndIf
	EndIf
	
End Function

Function RenderLobby()
	Local x#,y#,width#,height#,i%,j%
	Local plAmount%,plAmount2%
	Local temp#
	Local cmsg.ChatMSG
	Local ChatMSGAmount% = 0
	Local ChatBaseY# = 0.0, ChatExtraSpace# = 0.0
	
	If InLobby() Then
		width = 800.0 * MenuScale
		height = 800.0 * MenuScale
		x = opt\GraphicWidth / 2.0 - (width / 2.0)
		y = opt\GraphicHeight / 2.0 - (height / 2.0)
		
		DrawFrame(x, y, width - 300.0 * MenuScale, height - 30.0 * MenuScale)
		
		SetFont fo\Font[Font_Default]
		
		For cmsg = Each ChatMSG
			ChatMSGAmount = ChatMSGAmount + 1
		Next
		
		;Temporary, will probably be removed/tweaked once a scrollbar exists
		If ChatMSGAmount > 11
			Delete First ChatMSG
		EndIf
		
		ChatBaseY = y + (height - 40.0 * MenuScale) - (30.0 * MenuScale) * (ChatMSGAmount * 2) - (10.0 * MenuScale) * (ChatMSGAmount - 1)
		ChatMSGAmount = 1
		For cmsg = Each ChatMSG
			Color 200,200,200
			If cmsg\PlayerID = 0 Then Color 100,100,255
			If cmsg\IsServerMSG > SERVER_MSG_NO Then Color 255,255,0
			If cmsg\IsServerMSG = SERVER_MSG_NO Then
				Text x + 10.0 * MenuScale, (ChatBaseY + (30.0 * MenuScale) * (ChatMSGAmount - 1)) + ChatExtraSpace, cmsg\PlayerName + ":"
			Else
				Text x + 10.0 * MenuScale, (ChatBaseY + (30.0 * MenuScale) * (ChatMSGAmount - 1)) + ChatExtraSpace, GetLocalString("Chat", "server") + ":"
			EndIf
			
			Color 255,255,255
			If cmsg\IsServerMSG > SERVER_MSG_NO Then Color 255,255,0
			Text x + 10.0 * MenuScale, (ChatBaseY + (30.0 * MenuScale) * ChatMSGAmount) + ChatExtraSpace, Steam_FilterText(cmsg\SteamIDUpper, cmsg\SteamIDLower, AssembleChatMSG(cmsg))
			ChatMSGAmount = ChatMSGAmount + 2
			ChatExtraSpace = ChatExtraSpace + 10.0 * MenuScale
		Next
		
		Color 255,255,255
		SetFont fo\Font[Font_Default_Large]
		Text opt\GraphicWidth / 2.0, y - 50.0 * MenuScale, mp_I\Gamemode\name + " - " + mp_I\MapInList\Name, True
		
		For i = 0 To 1
			DrawFrame(x + width - 280.0 * MenuScale, y + (i * (height / 2.0 + 10.0 * MenuScale)), 280.0 * MenuScale, 60.0 * MenuScale)
			DrawFrame(x + width - 280.0 * MenuScale, (y + (59.0 * MenuScale)) + (i * (height / 2.0 + 10.0 * MenuScale)), 280.0 * MenuScale, (height / 2.0) - 70.0 * MenuScale)
		Next
		
		SetFont fo\Font[Font_Default_Medium]
		If mp_I\ReadyTimer < 70*5 Then
			TextWithAlign opt\GraphicWidth - 10.0 * MenuScale, 10.0 * MenuScale, GetLocalStringR("Multiplayer","starting_in",TurnIntoSeconds(mp_I\ReadyTimer)), 2
		EndIf
		Select mp_I\Gamemode\ID
			Case Gamemode_Deathmatch
				SetFont fo\Font[Font_Default]
				For i = 0 To (mp_I\MaxPlayers-1)
					If Players[i]<>Null Then
						temp = 0
						Select Players[i]\Team
							Case Team_MTF
								plAmount = plAmount + 1
								temp = y + (60.0 * MenuScale) + ((plAmount - 1) * (55.0 * MenuScale))
							Case Team_CI
								plAmount2 = plAmount2 + 1
								temp = y + (60.0 * MenuScale) + (height / 2.0 + 10.0 * MenuScale) + ((plAmount2 - 1) * (55.0 * MenuScale))
						End Select
						If temp>0 Then
							DrawFrame(x + width - 280.0 * MenuScale, temp, 55.0 * MenuScale, 55.0 * MenuScale)
							DrawFrame(x + width - 226.0 * MenuScale, temp, 225.0 * MenuScale, 55.0 * MenuScale)
							Text x + width - 216.0 * MenuScale, temp + 27.5 * MenuScale, LimitText(Players[i]\Name,20), False, True
							If Players[i]\IsReady Then
								DrawImage(mp_I\TickIcon, x + width - 280.0 * MenuScale, temp)
							EndIf
						EndIf
					EndIf
				Next
				Text x + width - 140.0 * MenuScale, y + 30.0 * MenuScale, GetLocalString("Multiplayer", "ntf")+": "+plAmount, True, True
				Text x + width - 140.0 * MenuScale, y + (height / 2.0 + 40.0 * MenuScale), GetLocalString("Multiplayer", "ci")+": "+plAmount2, True, True
			Default
				SetFont fo\Font[Font_Default]
				For i = 0 To (mp_I\MaxPlayers-1)
					If Players[i]<>Null Then
						plAmount = plAmount + 1
						temp = y + (60.0 * MenuScale) + ((plAmount - 1) * (55.0 * MenuScale))
						DrawFrame(x + width - 280.0 * MenuScale, temp, 55.0 * MenuScale, 55.0 * MenuScale)
						DrawFrame(x + width - 226.0 * MenuScale, temp, 225.0 * MenuScale, 55.0 * MenuScale)
						Text x + width - 216.0 * MenuScale, temp + 27.5 * MenuScale, LimitText(Players[i]\Name,20), False, True
						If Players[i]\IsReady Then
							DrawImage(mp_I\TickIcon, x + width - 280.0 * MenuScale, temp)
						EndIf
					EndIf
				Next
				Text x + width - 140.0 * MenuScale, y + 30.0 * MenuScale, GetLocalString("Multiplayer", "ntf")+": "+plAmount, True, True
				Text x + width - 140.0 * MenuScale, y + (height / 2.0 + 40.0 * MenuScale), GetLocalString("Multiplayer", "info"), True, True
				
				Local tempstr$
				If mp_I\Gamemode\Difficulty = MP_DIFFICULTY_KETER Then
					tempstr = GetLocalString("Difficulty", "keter")
				ElseIf mp_I\Gamemode\Difficulty = MP_DIFFICULTY_EUCLID Then
					tempstr = GetLocalString("Difficulty", "euclid")
				Else
					tempstr = GetLocalString("Difficulty", "safe")
				EndIf
				Text x + width - 260.0 * MenuScale, y + (height / 2.0 + 100.0 * MenuScale), GetLocalString("Menu", "difficulty")+": "+tempstr, False, True
				Text x + width - 260.0 * MenuScale, y + (height / 2.0 + 120.0 * MenuScale), GetLocalString("Multiplayer", "wave_amount")+": "+mp_I\Gamemode\MaxPhase, False, True
				Text x + width - 260.0 * MenuScale, y + (height / 2.0 + 140.0 * MenuScale), GetLocalString("Multiplayer", "scp_to_contain")+": "+mp_I\MapInList\BossNPC, False, True
		End Select
		
		If ((mp_I\PlayState = GAME_CLIENT And Players[mp_I\PlayerID]\WantsTeam = Team_Unknown) Lor (mp_I\PlayState = GAME_SERVER And Players[mp_I\PlayerID]\Team = Team_Unknown)) Then
			DrawFrame((opt\GraphicWidth / 2.0) - (width / 3.2), (opt\GraphicHeight / 2.0) - (height / 5.0), width / 1.6, height / 2.5)
			SetFont fo\Font[Font_Default_Large]
			Text opt\GraphicWidth / 2.0, y + (height / 4.0) + 45.0 * MenuScale, GetLocalString("Multiplayer", "select"), True, False
			y = y + (height / 4.0) + 100.0 * MenuScale
			For i = 0 To 1
				x = (opt\GraphicWidth / 2.0) - (width / 3.2) + 15.0 * MenuScale + ((ImageWidth(mp_I\Gamemode\img[i]) + 10.0 * MenuScale) * i)
				If MouseOn(x, y, ImageWidth(mp_I\Gamemode\img[i]), ImageHeight(mp_I\Gamemode\img[i])) Then
					;TODO: Add check when team is full
					Color 0,255,0
					Rect x - 3.0 * MenuScale, y - 3.0 * MenuScale, ImageWidth(mp_I\Gamemode\img[i]) + 6.0 *MenuScale, ImageHeight(mp_I\Gamemode\img[i]) + 6.0 * MenuScale, True
					Color 255,255,255
				EndIf
				DrawImage mp_I\Gamemode\img[i], x, y
			Next
		EndIf
		
		DrawAllMenuButtons()
		DrawAllMenuInputBoxes()
		
	EndIf
	
End Function

Function InLobby()
	
	If mp_I\ReadyTimer > 0.0 Lor (mp_I\PlayState = GAME_CLIENT And Players[mp_I\PlayerID]\WantsTeam = Team_Unknown) Lor (mp_I\PlayState = GAME_SERVER And Players[mp_I\PlayerID]\Team = Team_Unknown) Then
		Return True
	EndIf
	Return False
	
End Function

;~IDEal Editor Parameters:
;~F#2#E8
;~C#Blitz3D