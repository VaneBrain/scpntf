
;Player constants
Global Designation$ = GetLocalString("Singleplayer", "designation")
Const MaxStepSounds% = 8
Const MaxMaterialSounds% = 3

;Wheel constants
Const WHEEL_CLOSED = 0
Const WHEEL_COMMAND = 1
Const WHEEL_SOCIAL = 2
Const WHEEL_MAX = 9
Const WHEEL_OUTPUT_UNKNOWN = 254 ;Will change it back once that bug is fixed where 255 seems to create another byte

;Command wheel constants
Const COMMAND_OVERHERE = 0
Const COMMAND_HELPME = 2
Const COMMAND_WAITHERE = 3
Const COMMAND_TESLA = 4 ;Singleplayer Only
Const COMMAND_CANCEL = 5
Const COMMAND_CAMERA = 6 ;Singleplayer Only
Const COMMAND_FOLLOWME = 7
Const COMMAND_COVERME = 8

;Social wheel constants
Const SOCIAL_LETSGO = 0
Const SOCIAL_ACTRUDE = 2
Const SOCIAL_NEGATIVE = 3
Const SOCIAL_SORRY = 4
Const SOCIAL_CANCEL = 5
Const SOCIAL_THANKYOU = 6
Const SOCIAL_AFFIRMATIVE = 7
Const SOCIAL_GOODJOB = 8

;Command wheel look here constants
Const ICON_LOOK_DEFAULT = 0
Const ICON_LOOK_AMMO = 1
Const ICON_LOOK_GUN = 2
Const ICON_LOOK_ENEMY = 3
Const WHEEL_MAX_LOOK_ICONS = 4

;MTF Dialogue constants
Const MTF_DIALOGUE_TIMER_MIN = 70*45
Const MTF_DIALOGUE_TIMER_MAX = 70*120
Const MTF_DIALOGUE_MAX = 5
Const MTF_DIALOGUE_NUM_OF_BITS = 2

Type MainPlayer
	Field Camera
	Field CameraPivot%
	Field StepSoundWalk%[MaxStepSounds*MaxMaterialSounds]
	Field StepSoundRun%[MaxStepSounds*MaxMaterialSounds]
	Field HasNTFGasmask%
	Field NightVisionEnabled%
	Field SlotsDisplayTimer#
	;Communication and Social wheel
	Field WheelOpened%
	Field WheelSprite%
	Field WheelMiddle%
	Field WheelSelect%
	Field WheelOutput%[WHEEL_MAX]
	Field WheelSelectedOutput%
	Field WheelCurrentMouseXSpeed#
	Field WheelCurrentMouseYSpeed#
	Field WheelLookHereIcons%[WHEEL_MAX_LOOK_ICONS]
;	Field WheelLookHereIcon%
	Field WheelLookHereSelectedIcon%
	Field HealthIcon
	Field KevlarIcon
	Field DamageOverlay
	Field DamageTimer#
End Type

Type PlayerSP
	Field SharedPlayer.MainPlayer
	Field Pos.Vector3D
	Field Rot.Vector3D
	Field PlayerRoom.Rooms
	Field Collider
	Field DropSpeed#
	Field DeployState#
	Field ShootState#
	Field ReloadState#
	Field SoundCHN%
	Field Kevlar#
	Field Health#
	Field Checkpoint106Passed%
	Field NoMove%
	Field NoRotation%
End Type

Function CreateMainPlayer()
	Local i
	;Create player type
	mpl = New MainPlayer
	
	;Init player step sounds
	For i = 0 To (MaxStepSounds-1)
		mpl\StepSoundWalk[i] = LoadSound_Strict("SFX\Player\StepSounds\Concrete_Walk"+(i+1)+".ogg")
		mpl\StepSoundRun[i] = LoadSound_Strict("SFX\Player\StepSounds\Concrete_Run"+(i+1)+".ogg")
		mpl\StepSoundWalk[i+MaxStepSounds] = LoadSound_Strict("SFX\Player\StepSounds\Metal_Walk"+(i+1)+".ogg")
		mpl\StepSoundRun[i+MaxStepSounds] = LoadSound_Strict("SFX\Player\StepSounds\Metal_Run"+(i+1)+".ogg")
		mpl\StepSoundWalk[i+MaxStepSounds*2] = LoadSound_Strict("SFX\Player\StepSounds\Water_Walk"+(i+1)+".ogg")
		mpl\StepSoundRun[i+MaxStepSounds*2] = LoadSound_Strict("SFX\Player\StepSounds\Water_Run"+(i+1)+".ogg")
	Next
	
	mpl\CameraPivot = CreatePivot()
	mpl\HealthIcon = LoadImage_Strict("GFX\hpicon.png")
	mpl\KevlarIcon = LoadImage_Strict("GFX\kevlarIcon.png")
	mpl\HasNTFGasmask = True
	
End Function

Function CreateCommunicationAndSocialWheel()
	Local i%
	
	mpl\WheelOpened% = WHEEL_CLOSED
	mpl\WheelSelectedOutput = WHEEL_OUTPUT_UNKNOWN
	For i = 0 To (WHEEL_MAX-1)
		mpl\WheelOutput[i] = WHEEL_OUTPUT_UNKNOWN
	Next
	If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
		Players[mp_I\PlayerID]\VoiceLine = WHEEL_OUTPUT_UNKNOWN
	EndIf
	
	mpl\WheelSprite% = LoadSprite("GFX\menu\communication_wheel\com_wheel.png", 1+2, ark_blur_cam)
	MoveEntity mpl\WheelSprite,0,0,1
	ScaleSprite mpl\WheelSprite,0.5,0.5
	EntityOrder mpl\WheelSprite,-5001
	HideEntity mpl\WheelSprite
	
	mpl\WheelMiddle% = LoadSprite("GFX\menu\communication_wheel\com_wheel_center.png", 1+2, mpl\WheelSprite)
	ScaleSprite mpl\WheelMiddle,0.5,0.5
	EntityOrder mpl\WheelMiddle,-5000
	
	mpl\WheelSelect% = LoadSprite("GFX\menu\communication_wheel\com_wheel_selection.png", 1+2, mpl\WheelSprite)
	ScaleSprite mpl\WheelSelect,0.5,0.5
	EntityOrder mpl\WheelSelect,-5000
	
	mpl\WheelLookHereIcons[ICON_LOOK_DEFAULT] = LoadTexture_Strict("GFX\menu\communication_wheel\look_default.png",1+2,1)
	mpl\WheelLookHereIcons[ICON_LOOK_AMMO] = LoadTexture_Strict("GFX\menu\communication_wheel\look_ammo.png",1+2,1)
	mpl\WheelLookHereIcons[ICON_LOOK_GUN] = LoadTexture_Strict("GFX\menu\communication_wheel\look_guns.png",1+2,1)
	mpl\WheelLookHereIcons[ICON_LOOK_ENEMY] = LoadTexture_Strict("GFX\menu\communication_wheel\look_enemy.png",1+2,1)
	
;	mpl\WheelLookHereIcon = CreateSprite()
;	ScaleSprite mpl\WheelLookHereIcon,0.125,0.125
;	EntityOrder mpl\WheelLookHereIcon,-2
;	EntityTexture mpl\WheelLookHereIcon,mpl\WheelLookHereIcons[ICON_LOOK_DEFAULT]
;	HideEntity mpl\WheelLookHereIcon
	
End Function

Function UpdateCommunicationAndSocialWheel()
	Local it.Items, n.NPCs, p.Particles
	Local closeWheel% = False
	Local i%
	Local direction%, prevSelectedOutput%
	Local goesUp% = False
	Local voiceLine%, voiceLineNumber%
	Local voiceLineStr$, commandLineID%
	
	;This is only temporary for 0.2.0, will be unlocked for singleplayer in versions afterwards
	If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
		Return
	EndIf
	
	;CHECK FOR IMPLEMENTATION
	If ((gopt\GameMode = GAMEMODE_MULTIPLAYER And (Not InLobby()) And (Not mp_I\ChatOpen) And (Not IsSpectator(mp_I\PlayerID)) And Players[mp_I\PlayerID]\CurrHP > 0) Lor (gopt\GameMode <> GAMEMODE_MULTIPLAYER And EndingTimer >= 0 And KillTimer >= 0 And (Not InvOpen) And (Not MenuOpen))) And (Not ConsoleOpen) And (Not IsModerationOpen()) And (Not IsInVote()) Then
		If KeyDown(kb\CommandWheelKey) Then
			If mpl\WheelOpened = WHEEL_CLOSED Then
				MoveMouse viewport_center_x, viewport_center_y
				ResetInput()
				mpl\WheelOpened = WHEEL_COMMAND
				mpl\WheelSelectedOutput = COMMAND_OVERHERE
			EndIf
		ElseIf KeyDown(kb\SocialWheelKey) Then
			If mpl\WheelOpened = WHEEL_CLOSED Then
				MoveMouse viewport_center_x, viewport_center_y
				ResetInput()
				mpl\WheelOpened = WHEEL_SOCIAL
				mpl\WheelSelectedOutput = SOCIAL_LETSGO
			EndIf
		ElseIf mpl\WheelOpened <> WHEEL_CLOSED Then
			closeWheel = True
		EndIf
	Else
		closeWheel = True
		mpl\WheelOpened = WHEEL_CLOSED
	EndIf
	
	If closeWheel And mpl\WheelOpened <> WHEEL_CLOSED Then
		voiceLine% = mpl\WheelSelectedOutput+(WHEEL_MAX*(mpl\WheelOpened = WHEEL_SOCIAL))
		voiceLineNumber% = Rand(1,4)
		If voiceLine = COMMAND_OVERHERE Then
			For it = Each Items
				EntityRadius it\collider,0.2
			Next
			For n = Each NPCs
				ShowNPCHitBoxes(n)
			Next
			For i = 0 To (mp_I\MaxPlayers-1)
				If Players[i]<>Null And i<>mp_I\PlayerID Then
					ShowPlayerHitBoxes(i)
				EndIf
			Next
			CameraPick(Camera,opt\GraphicWidth/2,opt\GraphicHeight/2)
			For it = Each Items
				EntityRadius it\collider,0.01
			Next
			For n = Each NPCs
				HideNPCHitBoxes(n)
			Next
			For i = 0 To (mp_I\MaxPlayers-1)
				If Players[i]<>Null And i<>mp_I\PlayerID Then
					HidePlayerHitBoxes(i)
				EndIf
			Next
			If PickedEntity()<>0 Then
				CreateOverHereParticle(PickedX(), PickedY(), PickedZ())
				If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
					Players[mp_I\PlayerID]\OverHerePosition = CreateVector3D(PickedX(), PickedY(), PickedZ())
				EndIf
			EndIf
			
			commandLineID = GetPlayerCommandLineCategory()
			voiceLineStr = "SFX\Player\Voice\Chat\"
			Select commandLineID
				Case ICON_LOOK_DEFAULT
					voiceLineStr = voiceLineStr + "Look"
					mp_I\CurrChatMSG = "wheel_look"
				Case ICON_LOOK_ENEMY
					voiceLineStr = voiceLineStr + "Enemy"
					mp_I\CurrChatMSG = "wheel_enemy"
				Case ICON_LOOK_AMMO
					voiceLineStr = voiceLineStr + "Ammo"
					mp_I\CurrChatMSG = "wheel_ammo"
				Case ICON_LOOK_GUN
					voiceLineStr = voiceLineStr + "Gun"
					mp_I\CurrChatMSG = "wheel_gun"
			End Select
			CreateChatMSG(True)
			voiceLineStr = voiceLineStr + "Here" + voiceLineNumber + ".ogg"
;			p = CreateParticle(PickedX(), PickedY(), PickedZ(), 10, 0.125, 0, 500)
;			EntityTexture p\obj,mpl\WheelLookHereIcons[commandLineID]
;			EntityOrder p\obj,-2
;			SpriteViewMode p\obj,1
			mpl\WheelLookHereSelectedIcon = commandLineID
		Else
			voiceLineStr = GetPlayerVoiceLine(voiceLine, voiceLineNumber, mp_I\PlayerID)
		EndIf
		If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
			If voiceLine <> COMMAND_CANCEL And voiceLine <> (SOCIAL_CANCEL+WHEEL_MAX) Then
				If ChannelPlaying(Players[mp_I\PlayerID]\Sound_CHN) Then
					StopChannel(Players[mp_I\PlayerID]\Sound_CHN)
				EndIf
				Players[mp_I\PlayerID]\Sound_CHN = PlaySound_Strict(LoadTempSound(voiceLineStr))
				ChannelVolume(Players[mp_I\PlayerID]\Sound_CHN,opt\VoiceVol*opt\MasterVol)
				Players[mp_I\PlayerID]\VoiceLine = voiceLine
				Players[mp_I\PlayerID]\VoiceLineNumber = voiceLineNumber
			EndIf
			If voiceLine = COMMAND_OVERHERE Then
				EntityTexture Players[mp_I\PlayerID]\OverHereSprite,mpl\WheelLookHereIcons[commandLineID]
				ShowEntity Players[mp_I\PlayerID]\OverHereSprite
				PositionEntity Players[mp_I\PlayerID]\OverHereSprite,PickedX(),PickedY(),PickedZ()
				Players[mp_I\PlayerID]\OverHereSpriteTime = 70*5
			EndIf
		Else
			If voiceLine <> COMMAND_CANCEL And voiceLine <> (SOCIAL_CANCEL+WHEEL_MAX) Then
				If ChannelPlaying(psp\SoundCHN) Then
					StopChannel(psp\SoundCHN)
				EndIf
				psp\SoundCHN = PlaySound_Strict(LoadTempSound(voiceLineStr))
				SingleplayerVoiceActions(voiceLine)
				If voiceLine = COMMAND_OVERHERE Then
					
				EndIf
			EndIf	
		EndIf
		mpl\WheelOpened = WHEEL_CLOSED
		ResetInput()
	EndIf
	
	If mpl\WheelOpened <> WHEEL_CLOSED Then
		ShowEntity mpl\WheelSprite
		
		If mpl\WheelOpened = WHEEL_COMMAND Then
			mpl\WheelOutput[0] = COMMAND_OVERHERE
			mpl\WheelOutput[2] = COMMAND_COVERME
			mpl\WheelOutput[3] = COMMAND_FOLLOWME
			mpl\WheelOutput[5] = COMMAND_CANCEL
			mpl\WheelOutput[7] = COMMAND_WAITHERE
			mpl\WheelOutput[8] = COMMAND_HELPME
			If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
				mpl\WheelOutput[4] = COMMAND_TESLA
				mpl\WheelOutput[6] = COMMAND_CAMERA
			EndIf
		ElseIf mpl\WheelOpened = WHEEL_SOCIAL Then
			mpl\WheelOutput[0] = SOCIAL_LETSGO
			mpl\WheelOutput[2] = SOCIAL_GOODJOB
			mpl\WheelOutput[3] = SOCIAL_AFFIRMATIVE
			mpl\WheelOutput[4] = SOCIAL_THANKYOU
			mpl\WheelOutput[5] = SOCIAL_CANCEL
			mpl\WheelOutput[6] = SOCIAL_SORRY
			mpl\WheelOutput[7] = SOCIAL_NEGATIVE
			mpl\WheelOutput[8] = SOCIAL_ACTRUDE
		EndIf
		
		direction = -1
		If (MouseY() < mouse_top_limit) Then
			direction = 0
			goesUp = True
		EndIf
		If (MouseY() > mouse_bottom_limit) Then
			direction = 180
			goesUp = True
		EndIf
		If goesUp Then
			If (MouseX() > (opt\GraphicWidth/2.0) + ((opt\GraphicWidth-mouse_right_limit) / 2.0)) Then
				direction = direction - 45 + (90*(direction=180))
			ElseIf (MouseX() < (opt\GraphicWidth/2.0) - (mouse_left_limit / 2.0)) Then
				direction = direction + 45 - (90*(direction=180))
			EndIf
			
			If direction = -45 Then
				direction = 315
			EndIf
		EndIf
		If direction = -1 Then
			If (MouseX() > mouse_right_limit) Then
				direction = 270
			EndIf
			If (MouseX() < mouse_left_limit) Then
				direction = 90
			EndIf
		EndIf
		
		If direction > -1 Then
			prevSelectedOutput = mpl\WheelSelectedOutput
			If mpl\WheelSelectedOutput = 0 Then
				mpl\WheelSelectedOutput = (direction/45)+1
			Else
				If Abs(((mpl\WheelSelectedOutput-1)*45.0) - direction) >= 135.0 Then
					mpl\WheelSelectedOutput = 0
				Else
					mpl\WheelSelectedOutput = (direction/45)+1
				EndIf
			EndIf
			If mpl\WheelOutput[mpl\WheelSelectedOutput] = WHEEL_OUTPUT_UNKNOWN Then
				mpl\WheelSelectedOutput = prevSelectedOutput
			EndIf
		EndIf
		
		If (MouseX() > mouse_right_limit) Lor (MouseX() < mouse_left_limit) Lor (MouseY() > mouse_bottom_limit) Lor (MouseY() < mouse_top_limit) Then
			MoveMouse viewport_center_x, viewport_center_y
		EndIf
		
		If mpl\WheelSelectedOutput = 0 Then
			HideEntity mpl\WheelSelect
			ShowEntity mpl\WheelMiddle
		Else
			ShowEntity mpl\WheelSelect
			HideEntity mpl\WheelMiddle
			RotateSprite mpl\WheelSelect,(mpl\WheelSelectedOutput-1)*45.0
		EndIf
	Else
		HideEntity mpl\WheelSprite
		HideEntity mpl\WheelSelect
		ShowEntity mpl\WheelMiddle
		mpl\WheelSelectedOutput = WHEEL_OUTPUT_UNKNOWN
		For i = 0 To (WHEEL_MAX-1)
			mpl\WheelOutput[i] = WHEEL_OUTPUT_UNKNOWN
		Next
		mpl\WheelCurrentMouseXSpeed = 0.0
		mpl\WheelCurrentMouseYSpeed = 0.0
;		If NTF_GameModeFlag = 3 Then
;			Players[mp_I\PlayerID]\VoiceLine = WHEEL_OUTPUT_UNKNOWN
;		EndIf
	EndIf
	
	If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
		For i = 0 To (mp_I\MaxPlayers-1)
			If Players[i]<>Null Then
				Players[i]\OverHereSpriteTime = Max(Players[i]\OverHereSpriteTime - FPSfactor, 0.0)
				If Players[i]\OverHereSpriteTime > 0.0 Then
					ShowEntity Players[i]\OverHereSprite
				Else
					HideEntity Players[i]\OverHereSprite
				EndIf
			EndIf
		Next
	Else
		;TODO
	EndIf
	
End Function

Function GetPlayerVoiceLine$(voiceLine%, voiceLineNumber%, playerID%=-1)
	Local voice$
	Local n.NPCs
	
	voice = "SFX\Player\Voice\Chat\"
	Select voiceLine
		;Case COMMAND_OVERHERE
		;	voice = voice + "Look"
		;	If playerID >= 0 Then
		;		mp_I\CurrChatMSG = "wheel_look"
		;	EndIf
		Case COMMAND_HELPME
			voice = voice + "HelpMe"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_helpme"
			EndIf
		Case COMMAND_WAITHERE
			voice = voice + "Wait"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_wait"
			EndIf
		Case COMMAND_FOLLOWME
			voice = voice + "FollowMe"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_followme"
			EndIf
		Case COMMAND_COVERME
			voice = voice + "CoverMe"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_coverme"
			EndIf
		Case COMMAND_TESLA
			voice = voice + "Tesla"
		Case COMMAND_CAMERA
			voice = voice + "Camera"
		Case SOCIAL_LETSGO+WHEEL_MAX
			voice = voice + "LetsGo"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_letsgo"
			EndIf
		Case SOCIAL_ACTRUDE+WHEEL_MAX
			voice = voice + "Rude"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_actsrude"
			EndIf
		Case SOCIAL_NEGATIVE+WHEEL_MAX
			voice = voice + "Negative"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_negative"
			EndIf
		Case SOCIAL_SORRY+WHEEL_MAX
			voice = voice + "Sorry"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_sorry"
			EndIf
		Case SOCIAL_THANKYOU+WHEEL_MAX
			voice = voice + "Thanks"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_thanks"
			EndIf
		Case SOCIAL_AFFIRMATIVE+WHEEL_MAX
			voice = voice + "Affirmative"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_affirmative"
			EndIf
		Case SOCIAL_GOODJOB+WHEEL_MAX
			voice = voice + "GoodJob"
			If playerID >= 0 Then
				mp_I\CurrChatMSG = "wheel_goodjob"
			EndIf
	End Select
	
	If playerID >= 0 Then
		CreateChatMSG(True)
	EndIf
	
	voice = voice + voiceLineNumber + ".ogg"
	
;	If NTF_GameModeFlag=3 Then
;		
;	Else
;		voice = "SFX\Player\Voice\"
;		
;		Select voiceLine
;			Case COMMAND_MEDICAL
;				voice = voice + "Playerneedsmedicalattention" + Rand(1, 2) + ".ogg"
;			Case COMMAND_ENEMYSPOTTED
;				For n = Each NPCs
;					If n<>Null Then
;						If EntityDistanceSquared(Collider, n\Collider) < PowTwo(7) Then
;							If EntityVisible(Collider, n\Collider) Then
;								Select n\NPCtype
;									Case NPCtypeD2
;										voice = voice + "Class-D Spotted STOP" + Rand(1, 3) + ".ogg"
;									Case NPCtypeZombie
;										voice = voice + "SCP-049-2Spotted" + Rand(1, 2) + ".ogg"
;									Case NPCtype173
;										voice = voice + "SCP-173Spotted" + Rand(1, 3) + ".ogg"
;									Case NPCtypeOldMan
;										voice = voice + "SCP-106Spotted" + Rand(1, 3) + ".ogg"
;									Case NPCtype049
;										voice = voice + "SCP-049Spotted" + Rand(1, 3) + ".ogg"
;									Case NPCtype096
;										voice = voice + "SCP-096Spotted" + Rand(1, 3) + ".ogg"
;								End Select
;							Else
;								If n\NPCtype = NPCtypeD2 Then
;									voice = voice + "Class-D Detected" + Rand(1, 2) + ".ogg"
;								EndIf
;								Exit
;							EndIf
;						EndIf
;					EndIf
;				Next
;			Case COMMAND_ENEMYLOST
;				;voice = voice + 
;			Case COMMAND_TESLA
;				voice = voice + "TeslaGateDeactivation" + Rand(1, 2) + ".ogg"
;			Case COMMAND_CAMERA
;				;voice = voice + 
;			Case COMMAND_SPLIT
;				voice = voice + "StopFollowingtoTeam" + Rand(1, 4) + ".ogg"
;			Case COMMAND_SCP
;				If Contained106 Then
;					voice = voice + "SCP-106 Contained.ogg"
;				ElseIf Curr173\Idle = SCP173_CONTAIN Then
;					voice = voice + "scp173containmentbox"+Rand(1, 2)+".ogg"
;				EndIf
;		End Select
;	EndIf
	
	Return voice
End Function

Function GetPlayerCommandLineCategory$()
	Local n.NPCs, p.Player, it.Items
	Local i%
	
	;Check for the correct voice line to be played
	;First: Check the NPCs and players (aka enemy)
	For n = Each NPCs
		For i = 0 To (MaxHitBoxes-1)
			If n\HP > 0 Then
				If n\HitBox1[i]<>0 And n\HitBox1[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				ElseIf n\HitBox2[i]<>0 And n\HitBox2[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				ElseIf n\HitBox3[i]<>0 And n\HitBox3[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				EndIf
			EndIf
		Next
		
		If n\obj = PickedEntity() Then
			Return ICON_LOOK_ENEMY
		EndIf
	Next
	;Check if a player is an enemy
	For p = Each Player
		If p\Team <> Players[mp_I\PlayerID]\Team Then
			For i = 0 To (MaxHitBoxes-1)
				If p\HitBox1[i]<>0 And p\HitBox1[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				ElseIf p\HitBox2[i]<>0 And p\HitBox2[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				ElseIf p\HitBox3[i]<>0 And p\HitBox3[i] = PickedEntity() Then
					Return ICON_LOOK_ENEMY
				EndIf
			Next
		EndIf
	Next
	;Check for ammo crate or weapon items
	For it = Each Items
		If it\collider = PickedEntity() Then
			Select it\itemtemplate\tempname
				Case "ammocrate"
					Return ICON_LOOK_AMMO
				Default
					If it\itemtemplate\isGun Then
						Return ICON_LOOK_GUN
					EndIf
			End Select
		EndIf
	Next
	;Last resort: No targetted object found, use default
	Return ICON_LOOK_DEFAULT
	
End Function

Function CreateOverHereParticle(x#, y#, z#)
	
	Local p.Particles = CreateParticle(x,y,z,10,0.05,0.0,500)
	p\SizeChange = 0.01
	p\Achange = -0.01
	SpriteViewMode p\obj,1
	EntityOrder p\obj,-1
	
End Function

Function RenderCommunicationAndSocialWheel()
	Local x# = opt\GraphicWidth / 2.0
	Local y# = opt\GraphicHeight / 2.0
	Local i%
	Local namespace$
	
	If mpl\WheelOpened <> WHEEL_CLOSED Then
		Color 255,255,255
		SetFont fo\Font[Font_Default_Medium]
		If mpl\WheelOpened = WHEEL_COMMAND Then
			namespace = "command"
		Else
			namespace = "social"
		EndIf
		For i = 0 To (WHEEL_MAX-1)
			If mpl\WheelOutput[i] <> WHEEL_OUTPUT_UNKNOWN Then
				If i > 0 Then
					x = (opt\GraphicWidth/2.0) + 300.0 * MenuScale * Sin((mpl\WheelOutput[i]-1) * 45.0)
					y = (opt\GraphicHeight/2.0) - 300.0 * MenuScale * Cos((mpl\WheelOutput[i]-1) * 45.0)
				EndIf
				Text x,y,GetLocalString("Chatwheel", namespace+i),True,True
			EndIf
		Next
	EndIf
	
End Function

Function SingleplayerVoiceActions(voiceLine%)
	Local ev.Events, n.NPCs
	
;	Select voiceLine
;		Case COMMAND_MEDICAL
;		Case COMMAND_ENEMYSPOTTED
;		Case COMMAND_ENEMYLOST
;		Case COMMAND_TESLA
;			For ev = Each Events
;				If ev\EventName = "room2_tesla"
;					If ev\room=PlayerRoom Then
;						If ev\EventState2 <= 70*3.5 Then
;							ev\EventState2 = 70*100
;							ev\EventState3=ev\EventState3+140
;							Exit
;						EndIf
;					EndIf
;				EndIf
;			Next
;		Case COMMAND_CAMERA
;		Case COMMAND_SPLIT
;			For n = Each NPCs
;				If n<>Null Then
;					If n\NPCtype = NPCtypeMTF Then
;						n\State = MTF_SPLIT_UP
;						Exit
;					EndIf
;				EndIf
;			Next
;		Case COMMAND_SCP
;			If EntityDistanceSquared(Collider, Curr173\Collider) < PowTwo(7) Then
;				If EntityVisible(Collider, Curr173\Collider) Then
;					Curr173\ContainmentState = -1
;				EndIf
;			EndIf
;	End Select
	
End Function

Function DestroyMainPlayer()
	Local i,n
	
	For i = 0 To (MaxStepSounds-1)
		For n = 0 To (MaxMaterialSounds-1)
			FreeSound_Strict(mpl\StepSoundWalk[i+(n*MaxStepSounds)])
			FreeSound_Strict(mpl\StepSoundRun[i+(n*MaxStepSounds)])
		Next
	Next
	FreeImage mpl\HealthIcon
	FreeImage mpl\KevlarIcon
	
	Delete mpl
	
End Function

Function UpdateNightVision()
	Local dist#
	
	;CHECK FOR IMPLEMENTATION
	If (gopt\GameMode = GAMEMODE_MULTIPLAYER And IsSpectator(mp_I\PlayerID)) Lor (Not mpl\HasNTFGasmask) Then
		If mpl\NightVisionEnabled Then
			TurnNVOff()
		EndIf
		mpl\NightVisionEnabled = False
		Return
	EndIf
	
	;TODO: Change this to InteractHit to add the controller key as well!
	If KeyHit(kb\NVToggleKey) Then
		mpl\NightVisionEnabled = Not mpl\NightVisionEnabled
		If mpl\NightVisionEnabled Then
			PlaySound_Strict LoadTempSound("SFX\Interact\NVGOn.ogg")
			TurnNVOn()
		Else
			PlaySound_Strict LoadTempSound("SFX\Interact\NVGOff.ogg")
			TurnNVOff()
		EndIf
	EndIf
	
End Function

Function TurnNVOn()
	
	AmbientLightRooms(100)
	EntityColor GasMaskOverlay2,220,30,30
	If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
		If mp_O\Gamemode\ID = Gamemode_Deathmatch Then
			CameraFogRange(Camera,CameraFogFar*4,CameraFogFar*5)
		ElseIf mp_O\Gamemode\ID = Gamemode_Waves Then
			CameraFogRange(Camera,CameraFogFar*2,CameraFogFar*3)
		Else
			CameraFogRange(Camera,CameraFogFar,CameraFogFar)
		EndIf
	Else
		CameraFogRange(Camera,CameraFogFar,CameraFogFar)
	EndIf
	CameraRange(Camera,0.01,GetCameraFogRangeFar(Camera)*2.0)
	
End Function

Function TurnNVOff()
	
	AmbientLightRooms(0)
	EntityColor GasMaskOverlay2,255,255,255
	If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
		If mp_O\Gamemode\ID = Gamemode_Deathmatch Then
			CameraFogRange(Camera,CameraFogNear,CameraFogFar*5)
		ElseIf mp_O\Gamemode\ID = Gamemode_Waves Then
			CameraFogRange(Camera,CameraFogNear,CameraFogFar*3)
		Else
			CameraFogRange(Camera,CameraFogNear,CameraFogFar)
		EndIf
	Else
		CameraFogRange(Camera,CameraFogFar,CameraFogFar)
	EndIf
	CameraRange(Camera,0.01,GetCameraFogRangeFar(Camera)*2.0)
	
End Function

Function CreateDamageOverlay()
	
	mpl\DamageOverlay = LoadSprite("GFX\BloodOverlay.jpg",1+2,ark_blur_cam)
	ScaleSprite mpl\DamageOverlay,1.0,Float(opt\GraphicHeight)/Float(opt\GraphicWidth)
	EntityFX(mpl\DamageOverlay, 1)
	EntityOrder(mpl\DamageOverlay, -2001)
	MoveEntity(mpl\DamageOverlay, 0, 0, 1.0)
	HideEntity(mpl\DamageOverlay)
	
End Function

Function UpdateDamageOverlay()
	
	If mpl\DamageTimer > 0.0 Then
		ShowEntity mpl\DamageOverlay
		EntityAlpha mpl\DamageOverlay,Clamp(mpl\DamageTimer / 70.0, 0.0, 1.0)
		mpl\DamageTimer = Max(mpl\DamageTimer - FPSfactor, 0)
	Else
		HideEntity mpl\DamageOverlay
	EndIf
	
End Function

Function CreateSPPlayer()
	
	psp = New PlayerSP
	psp\Health = 100
	psp\Kevlar = 100
	
	mtfd = New MTFDialogue
	mtfd\CurrentDialogue = -1
	mtfd\Timer = Rand(MTF_DIALOGUE_TIMER_MIN, MTF_DIALOGUE_TIMER_MAX) ;Starting timer
	mtfd\Dialogues[0] = %10
	mtfd\Dialogues[1] = %111011
	mtfd\Dialogues[2] = %111011
	mtfd\Dialogues[3] = %1011
	mtfd\Dialogues[4] = %111011
	
End Function

Function DestroySPPlayer()
	
	Delete psp
	
	Delete mtfd
	
End Function

Function UpdateSPPlayer()
	Local n.NPCs
	Local contain173% = False
	
	If Curr173 <> Null And Curr173\Idle = SCP173_STATIONARY And Curr173\IdleTimer > 0.0 Then
		;Check if at least one MTF unit is alive and looking at 173
		For n = Each NPCs
			If n\NPCtype = NPCtypeMTF And (Not n\IsDead) And n\State = MTF_CONTAIN173 Then
				contain173 = True
				Exit
			EndIf
		Next
		
		If contain173 Then
			If (BlinkTimer <= -6 And (BlinkTimer + FPSfactor) > -6) Then
				PlayPlayerSPVoiceLine("Blinking" + Rand(1, 2))
			EndIf
		Else
			Curr173\Idle = SCP173_ACTIVE
			Curr173\IdleTimer = 0.0
		EndIf
	EndIf
	
End Function

Function IsSPPlayerAlive()
	
	If psp\Health > 0 Then Return True
	Return False
	
End Function

Function DamageSPPlayer(amount#, only_health%=False, kevlar_protect_factor#=4.0)
	
	If only_health Lor psp\Kevlar = 0.0 Then
		psp\Health = Max(psp\Health - amount, 0.0)
	Else
		psp\Kevlar = Max(psp\Kevlar - amount, 0.0)
		If kevlar_protect_factor > 0.0 Then
			psp\Health = Max(psp\Health - amount / kevlar_protect_factor, 0.0)
		EndIf
	EndIf
	
End Function

Function HealSPPlayer(amount#)
	
	psp\Health = Min(psp\Health + amount, 100.0)
	
End Function

Type MTFDialogue
	Field Enabled%
	Field Timer#
	Field IsPlaying%
	Field CurrentDialogue%
	Field Dialogues%[MTF_DIALOGUE_MAX]
	Field CurrentSequence%
	Field CurrentProgress%
	Field CurrentChannel%
	Field EntityReference%
	Field PrevDialogue%
End Type

Function UpdateMTFDialogue()
	Local value%, n.NPCs, suffix$
	
	If (mtfd\Enabled And psp\Health > 0) Then
		mtfd\Timer = mtfd\Timer - FPSfactor
	EndIf
	
	If mtfd\Timer <= 0.0 Then
		If (Not mtfd\IsPlaying) Then
			mtfd\CurrentProgress = 0
			mtfd\CurrentChannel = 0
			mtfd\PrevDialogue = mtfd\CurrentDialogue
			While mtfd\CurrentDialogue = mtfd\PrevDialogue
				mtfd\CurrentDialogue = Rand(0, MTF_DIALOGUE_MAX-1)
			Wend
			mtfd\CurrentSequence = mtfd\Dialogues[mtfd\CurrentDialogue]
			mtfd\IsPlaying = True
		EndIf
		
		If (Not mtfd\CurrentChannel) Lor (Not ChannelPlaying(mtfd\CurrentChannel)) Then
			value = ((mtfd\CurrentSequence Shr mtfd\CurrentProgress*MTF_DIALOGUE_NUM_OF_BITS) Mod (2^MTF_DIALOGUE_NUM_OF_BITS))
			
			Select value
				Case 1
					suffix = "player"
				Case 2
					suffix = "regular"
				Case 3
					suffix = "medic"
			End Select
			
			If value > 0 Then
				mtfd\CurrentChannel = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Dialogue\line_" + (mtfd\CurrentDialogue+1) + "_" + (mtfd\CurrentProgress+1) + "_" + suffix + ".ogg"))
				
				If value = 1 Then
					;Player
					psp\SoundCHN = mtfd\CurrentChannel
				Else
					;NPC
					For n = Each NPCs
						If n\NPCtype = NPCtypeMTF And n\PrevState = (value-1) Then
							n\SoundChn = mtfd\CurrentChannel
							mtfd\EntityReference = n\Collider
							Exit
						EndIf
					Next
				EndIf
			Else
				mtfd\Timer = Rand(MTF_DIALOGUE_TIMER_MIN, MTF_DIALOGUE_TIMER_MAX)
				mtfd\IsPlaying = False
			EndIf
			
			mtfd\CurrentProgress = mtfd\CurrentProgress + 1
		EndIf
		
		If ChannelPlaying(mtfd\CurrentChannel) And mtfd\CurrentChannel <> psp\SoundCHN Then
			UpdateSoundOrigin(mtfd\CurrentChannel, Camera, mtfd\EntityReference,10,opt\MasterVol*opt\VoiceVol)
		Else
			ChannelVolume(psp\SoundCHN,opt\MasterVol*opt\VoiceVol)
		EndIf
		
	EndIf
	
End Function

Function PlayNewDialogue(id%, sequence%)
	
	mtfd\Timer = 0.0
	mtfd\CurrentProgress = 0
	mtfd\IsPlaying = True
	mtfd\CurrentDialogue = (id-1)
	mtfd\CurrentSequence = sequence
	
End Function

Function PlayPlayerSPVoiceLine(voiceLine$)
	
	If ChannelPlaying(psp\SoundCHN) Then
		StopChannel(psp\SoundCHN)
	EndIf
	psp\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Player\Voice\" + voiceLine + ".ogg"))
	
End Function

;Inlcuding the PlayerAnimations file
Include "SourceCode\PlayerAnimations.bb"

;~IDEal Editor Parameters:
;~F#2E#5A#70#18B#211#248#252#26D#294#2A4#2BE#2D1#2E4#2EF#2FB#30B#32C#333#340#346
;~F#353#394
;~C#Blitz3D