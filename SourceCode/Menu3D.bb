;3D Menu from Nine Tailed Fox Mod made by "ENDSHN"

;3D Menu constants
;[Block]
Const MaxMenu3DStates = 5
Const MaxMenu3DObjects = 24
Const MaxMenu3DRenderingObjects = 4
Const MenuRoomScale# = RoomScale
;[End Block]

Type Menu3DInstance
	Field State#[MaxMenu3DStates]
	Field Progress$
	Field RoomName$
	Field Scene%
	Field Pivot%
	Field Objects%[MaxMenu3DObjects]
	Field RoomMesh%
	Field Rendering%
	Field RenderingObj%[MaxMenu3DRenderingObjects]
	Field Distortion#
	Field FreeRoam%
	Field FogColor$
End Type

Type Menu3DLights
	Field Lights%
	Field LightIntensity#
	Field LightSprites%
	Field LightSpriteHidden%
	Field LightSpritesPivot%
	Field LightSprites2%
	Field LightHidden%
	Field LightFlicker%
	Field LightR#,LightG#,LightB#
	Field LightCone%
	Field LightConeSpark%
	Field LightConeSparkTimer#
End Type

Type MenuLogo
	Field logo%
	Field gradient%
End Type

Function MainLoopMenu()
	
	While (ft\accumulator>0.0)
		ft\accumulator = ft\accumulator-GetTickDuration()
		If (ft\accumulator<=0.0) Then CaptureWorld()
		
		MousePosX = MouseX()
		MousePosY = MouseY()
		
		If Input_ResetTime>0
			Input_ResetTime = Max(Input_ResetTime-FPSfactor,0.0)
		Else
			DoubleClick = False
			If (Not co\Enabled)
				MouseHit1 = MouseHit(1)
				If MouseHit1
					If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
					LastMouseHit1 = MilliSecs()
				EndIf
				Local prevmousedown1 = MouseDown1
				MouseDown1 = MouseDown(1)
				If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
				
				MouseHit2 = MouseHit(2)
				MouseDown2 = MouseDown(2)
				
				MouseHit3 = MouseHit(3)
				
				keyhituse = KeyHit(KEY_USE)
				keydownuse = KeyDown(KEY_USE)
			Else
				;[CONTROLLER]
				MouseHit1 = JoyHit(CK_LMouse)
				If MouseHit1 Then
					If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
					LastMouseHit1 = MilliSecs()
				EndIf
				prevmousedown1 = MouseDown1
				MouseDown1 = JoyDown(CK_LMouse)
				If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
				MouseHit2 = JoyHit(CK_RMouse)
				MouseDown2 = JoyDown(CK_RMouse)
				MouseHit3 = JoyHit(CK_MMouse)
				keyhituse = JoyHit(CK_Use)
				keydownuse = JoyDown(CK_Use)
			EndIf
		EndIf
		
		If (Not keydownuse) And (Not keyhituse) Then GrabbedEntity = 0
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		
		co\PressedButton = JoyHit(CKM_Press)
		co\PressedNext = JoyDown(CKM_Next)
		co\PressedPrev = JoyDown(CKM_Prev)
		If co\PressedNext And co\PressedPrev Then
			co\PressedNext = False
			co\PressedPrev = False
		EndIf
		If ShouldPlay = 21 Then
			EndBreathSFX = LoadSound("SFX\Ending\MenuBreath.ogg")
			EndBreathCHN = PlaySound(EndBreathSFX)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66 Then
			If (Not ChannelPlaying(EndBreathCHN)) Then
				FreeSound(EndBreathSFX)
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		
		If EndingTimer >= 0.0 Then
			Update3DMenu()
			
			If KeyHit(KEY_CONSOLE) Then
				If opt\ConsoleEnabled Then
					ConsoleOpen = (Not ConsoleOpen)
					FlushKeys()
					FlushMouse()
				EndIf
			EndIf
			
			UpdateMainMenu()
		Else
			EndingTimer=EndingTimer-FPSfactor2*(opt\GraphicHeight/800.0)
			UpdateCredits()
		EndIf
		
		If MsgTimer>0 Then
			MsgTimer=MsgTimer-FPSfactor2
		EndIf
		
		UpdateAchievementMsg()
	Wend
	
	If MainMenuOpen Then
		If EndingTimer >= 0.0 Then
			CameraProjMode m_I\Cam,1
			If m3d\Rendering=0 Then
				CameraClsMode m_I\Cam,1,1
				CameraZoom m_I\Cam,1.0/(Tan((2*ATan(Tan(Float(FOV)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0))
				CameraViewport m_I\Cam,0,0,opt\GraphicWidth,opt\GraphicHeight
				CameraProjMode m_I\Cam,1
				HideEntity m_I\MenuLogo\logo
				HideEntity m_I\Sprite
				HideEntity m_I\MenuLogo\gradient
				RenderWorld(Max(0.0,1.0+(ft\accumulator/ft\tickDuration)))
				CameraZoom(m_I\Cam, 0.01)
				CameraViewport m_I\Cam,0,0,opt\GraphicWidth,opt\GraphicHeight
				CameraClsMode m_I\Cam,0,1
				CameraProjMode m_I\Cam,2
				HideEntity m3d\Scene
				ShowEntity m_I\MenuLogo\logo
				ShowEntity m_I\Sprite
				ShowEntity m_I\MenuLogo\gradient
				RenderWorld
				ShowEntity m3d\Scene
			Else
				CameraClsMode m_I\Cam,1,1
				CameraZoom m_I\Cam,1.0/(Tan((2*ATan(Tan(Float(FOV)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0))*0.8
				CameraViewport m_I\Cam,0,0,opt\GraphicWidth/5,opt\GraphicHeight/5
				CameraProjMode m_I\Cam,1
				HideEntity m_I\MenuLogo\logo
				HideEntity m_I\Sprite
				HideEntity m_I\MenuLogo\gradient
				RenderWorld(Max(0.0,1.0+(ft\accumulator/ft\tickDuration)))
				
				SetBuffer TextureBuffer(fresize_texture)
				ClsColor 0,0,0 : Cls
				CopyRect 0,0,opt\GraphicWidth/5,opt\GraphicHeight/5,1024-(opt\GraphicWidth/5)/2,1024-(opt\GraphicHeight/5)/2,BackBuffer(),TextureBuffer(m3d\RenderingObj[2])
				SetBuffer BackBuffer()
				ClsColor 0,0,0 : Cls
				Local AR# = (Float(opt\GraphicWidth/5)/Float(opt\GraphicHeight/5))/(Float(opt\GraphicWidth)/Float(opt\GraphicHeight))
				
				CameraProjMode m_I\Cam,0
				ShowEntity m3d\RenderingObj[1]
				ScaleEntity m3d\RenderingObj[1],(2050.0/Float(opt\GraphicWidth/5)*AR)+Rnd(-m3d\Distortion,m3d\Distortion),(2050.0/Float(opt\GraphicWidth/5)*AR)+Rnd(-m3d\Distortion,m3d\Distortion),1.0
				PositionEntity m3d\RenderingObj[1],Rnd(-m3d\Distortion,m3d\Distortion),Rnd(-m3d\Distortion,m3d\Distortion),1.0001
				CameraProjMode m3d\RenderingObj[0],2
				RenderWorld()
				CameraProjMode m3d\RenderingObj[0],0
				HideEntity m3d\RenderingObj[1]
				CameraProjMode m_I\Cam,1
				
				CameraZoom(m_I\Cam, 0.01)
				CameraViewport m_I\Cam,0,0,opt\GraphicWidth,opt\GraphicHeight
				CameraClsMode m_I\Cam,0,1
				CameraProjMode m_I\Cam,2
				HideEntity m3d\Scene
				ShowEntity m_I\MenuLogo\logo
				ShowEntity m_I\Sprite
				ShowEntity m_I\MenuLogo\gradient
				RenderWorld
				ShowEntity m3d\Scene
			EndIf
			CameraProjMode m_I\Cam,0
			
			RenderMainMenu()
		Else
			CameraProjMode m_I\Cam,0
			DrawCredits()
		EndIf
		
		SetFont fo\ConsoleFont
		Color 255,255,255
		If opt\ShowFPS Then Text 20, 20, "FPS: " + ft\fps
		If EndingTimer >= 0.0 Then
			Text 20,opt\GraphicHeight-30,"v"+VersionNumber
		EndIf
		UpdateConsole(2)
		
		If MsgTimer > 0 Then
			SetMsgColor(0, 0, 0)
			Text((opt\GraphicWidth / 2)+1, (opt\GraphicHeight / 2) + 201, Msg, True, False)
			SetMsgColor(255, 255, 255)
			If Left(Msg,20)="Loaded resource pack" Then
				SetMsgColor(0, 255, 0)
			ElseIf Left(Msg,14)="Blitz3D Error!" Then
				SetMsgColor(255, 0, 0)
			EndIf
			Text((opt\GraphicWidth / 2), (opt\GraphicHeight / 2) + 200, Msg, True, False)
		EndIf
	EndIf
	
	RenderAchievementMsg()
	
End Function

Function Load3DMenu(customprogress$="")
	If m3d = Null Then
		m3d.Menu3DInstance = New Menu3DInstance
	EndIf
	Local rt.RoomTemplates,StrTemp$,temp#,n%,i%
	
	If m_I = Null Then
		m_I.MenuInstance = New MenuInstance
	EndIf
	
	Color 255,255,255
	
	LoadMissingTexture()
	
	;First, we need to determine the room
	If customprogress="" Then
		m3d\Progress = GetINIString(gv\OptionFile,"options","progress","intro")
	Else
		m3d\Progress = customprogress
	EndIf
	;Then we need to assign the specific room
	Select m3d\Progress
		Case "intro","beginning"
			;[Block]
			m3d\RoomName = "gate_a_entrance"
			;[End Block]
		Case "ez"
			;[Block]
			Select Rand(2,6)
			;	Case 1
			;		m3d\RoomName = "room2_gs"
				Case 2
					m3d\RoomName = "room2_offices_2"
				Case 3
					m3d\RoomName = "room3_offices"
				Case 4
					m3d\RoomName = "room1_elevators"
				Case 5
					m3d\RoomName = "room4_ez"
				Case 6
					m3d\RoomName = "room2_servers_2"
			End Select
			;[End Block]
		Case "hcz"
			;[Block]
			Select Rand(1,6)
				Case 1
					m3d\RoomName = "cont_106"
				Case 2
					m3d\RoomName = "room2_pit"
				Case 3
					m3d\RoomName = "cont_966"
				Case 4
					m3d\RoomName = "room2_servers_1"
				Case 5
					m3d\RoomName = "checkpoint_hcz"
				Case 6
					m3d\RoomName = "room2_nuke"
			End Select
			;[End Block]
		Case "lcz"
			;[Block]
			Select Rand(1,6)
				Case 1
					m3d\RoomName = "cont_173"
				Case 2
					m3d\RoomName = "lockroom_1"
				Case 3
					m3d\RoomName = "cont_914"
				Case 4
					m3d\RoomName = "room3_window"
				Case 5
					m3d\RoomName = "surveil_room"
				Case 6
					m3d\RoomName = "room2_elevator_1"
			End Select
			;[End Block]
		Case "pd"
			;[Block]
			m3d\RoomName = "pocketdimension"
			;[End Block]
		Default
			;[Block]
			m3d\RoomName = "gate_a_entrance"
			;[End Block]
	End Select
	
	;After that, we need to load all important assets that are required for every room
	m3d\Scene = CreatePivot()
	Brightness% = GetINIFloat(gv\OptionFile, "options", "brightness", 20)
	CameraFogNear# = GetINIFloat(gv\OptionFile, "options", "camera fog near", 0.5)
	CameraFogFar# = GetINIFloat(gv\OptionFile, "options", "camera fog far", 6.0)
	StoredCameraFogFar# = CameraFogFar
	m3d\Pivot = CreatePivot()
	m_I\Cam = CreateCamera(m3d\Pivot)
	CameraRange m_I\Cam,0.01,20
	CameraFogRange m_I\Cam,CameraFogNear,CameraFogFar
	CameraFogColor m_I\Cam,0,0,0
	CameraFogMode m_I\Cam,1
	m_I\Sprite = CreateSprite(m_I\Cam)
	ScaleSprite m_I\Sprite,3,3
	EntityColor m_I\Sprite,0,0,0
	EntityFX m_I\Sprite,1
	EntityOrder m_I\Sprite,-1
	EntityAlpha m_I\Sprite,1.0
	m_I\SpriteAlpha = 1.0
	MoveEntity m_I\Sprite,0,0,1
	AmbientLightRoomTex% = CreateTextureUsingCacheSystem(2,2,1)
	TextureBlend AmbientLightRoomTex,2
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	AmbientLightRoomVal = 0
	m_I\MenuLogo = CreateMenuLogo(m_I\Cam)
	
	LightSpriteTex[0] = LoadTexture_Strict("GFX\light1.jpg",1,0)
	LightSpriteTex[2] = LoadTexture_Strict("GFX\lightsprite.jpg",1,0)
	LoadMaterials("Data\materials.ini")
	TextureLodBias TextureFloat#
	m3d\Rendering = 0
	m3d\Distortion = 0
	
	;Now we need to load the room mesh itself (by using a LoadRMesh function that only adds the necessary data)
	For rt = Each RoomTemplates
		If rt\Name = m3d\RoomName Then
			StrTemp = rt\objPath
			Exit
		EndIf
	Next
	m3d\RoomMesh = LoadRMesh(StrTemp,Null,False)
	ScaleEntity m3d\RoomMesh,RoomScale,RoomScale,RoomScale
	EntityParent m3d\RoomMesh,m3d\Scene
	EntityPickMode GetChild(m3d\RoomMesh,2),2
	
	LoadDoors()
	
	;Then, we only need to load some specific assets that are only required for some rooms
	Select m3d\RoomName
		Case "gate_a_intro"
			;[Block]
			m3d\FogColor = FogColor_Outside
			
			CameraFogRange m_I\Cam, 5,30
			CameraFogColor (m_I\Cam,200,200,200)
			CameraClsColor (m_I\Cam,200,200,200)
			CameraRange(m_I\Cam, 0.01, 100)
			
			;Sky
			m3d\Objects[0] = sky_CreateSky("GFX\map\sky\sky",m3d\Scene)
			RotateEntity m3d\Objects[0],0,-90,0
			;[End Block]
		Case "room3_offices"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			d_I\DoorOBJ = LoadMesh_Strict("GFX\map\doors\WindowedDoor.b3d")
			HideEntity d_I\DoorOBJ
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_KEYCARD],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],736.0*RoomScale,0.0,240.0*RoomScale
			;[End Block]
		Case "gate_a_entrance"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			d_I\DoorOBJ = LoadMesh_Strict("GFX\map\doors\ElevatorDoor.b3d")
			HideEntity d_I\DoorOBJ
			
			m3d\Objects[0] = LoadMesh_Strict("GFX\map\rooms\gate_a_entrance\gateaentrance_new.b3d",m3d\Scene)
			ScaleEntity m3d\Objects[0],RoomScale,RoomScale,RoomScale
			EntityPickMode m3d\Objects[0],2
			m3d\Objects[1] = LoadMesh_Strict("GFX\map\alarm_siren.b3d",m3d\Scene)
			ScaleEntity m3d\Objects[1],RoomScale,RoomScale,RoomScale
			PositionEntity m3d\Objects[1],0.0,928.0*RoomScale,592.0*RoomScale
			m3d\Objects[2] = LoadMesh_Strict("GFX\map\alarm_siren_rotor.b3d",m3d\Scene)
			ScaleEntity m3d\Objects[2],RoomScale,RoomScale,RoomScale
			PositionEntity m3d\Objects[2],0.0,928.0*RoomScale,592.0*RoomScale
			m3d\Objects[3] = CreateLight(3,m3d\Objects[2])
			MoveEntity m3d\Objects[3],0,0,0.001
			LightRange m3d\Objects[3],1.5
			LightColor m3d\Objects[3],255*5,0,0
			RotateEntity m3d\Objects[3],45,0,0
			LightConeAngles m3d\Objects[3],0,75
			
			m3d\Objects[4] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[4],RoomScale,RoomScale,RoomScale)
			RotateEntity m3d\Objects[4],0,180,0
			m3d\Objects[7] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[7],RoomScale,RoomScale,RoomScale)
			EntityParent m3d\Objects[7],m3d\Objects[4]
			m3d\Objects[5] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[5], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[6] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[6],0.03,0.03,0.03
			EntityParent m3d\Objects[4],m3d\Objects[5]
			PositionEntity m3d\Objects[5],744.0*RoomScale,0,656.0*RoomScale
			RotateEntity m3d\Objects[5],0,90,0
			PositionEntity m3d\Objects[6],688.0*RoomScale,0.7,512.0*RoomScale
			RotateEntity m3d\Objects[6],0,270,0
			MoveEntity(m3d\Objects[4], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "room2_gs"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			;First door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],0,0,8.0/2.0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			
			;Second door
			m3d\Objects[3] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[3], (204.0 * RoomScale) / MeshWidth(m3d\Objects[3]), 312.0 * RoomScale / MeshHeight(m3d\Objects[3]), 16.0 * RoomScale / MeshDepth(m3d\Objects[3]))
			RotateEntity m3d\Objects[3],0,180,0
			m3d\Objects[4] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[4], (204.0 * RoomScale) / MeshWidth(m3d\Objects[4]), 312.0 * RoomScale / MeshHeight(m3d\Objects[4]), 16.0 * RoomScale / MeshDepth(m3d\Objects[4]))
			m3d\Objects[5] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[5], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[6] = CopyEntity(d_I\ButtonOBJ[BUTTON_KEYCARD],m3d\Scene)
			ScaleEntity m3d\Objects[6],0.03,0.03,0.03
			PositionEntity m3d\Objects[6],0.6,0.7,-0.1
			EntityParent m3d\Objects[3],m3d\Objects[5]
			EntityParent m3d\Objects[4],m3d\Objects[5]
			EntityParent m3d\Objects[6],m3d\Objects[5]
			PositionEntity m3d\Objects[5],-256.0*RoomScale,0,512.0*RoomScale
			RotateEntity m3d\Objects[5],0,90,0
			EntityParent m3d\Objects[3],m3d\Scene
			EntityParent m3d\Objects[4],m3d\Scene
			MoveEntity(m3d\Objects[3], 0, 0, 8.0 * RoomScale)
			MoveEntity(m3d\Objects[4], 0, 0, 8.0 * RoomScale)
			
			;Blood Decal
			m3d\Objects[7]=CreateSprite(m3d\Scene)
			SpriteViewMode m3d\Objects[7],2
			RotateEntity m3d\Objects[7],90,0,0
			PositionEntity m3d\Objects[7],-870.0*RoomScale,0.05*RoomScale,830.0*RoomScale
			ScaleSprite m3d\Objects[7],0.75,0.75
			tex = LoadTexture_Strict("GFX\decal8.png",1+2,0)
			EntityTexture m3d\Objects[7],tex
			DeleteSingleTextureEntryFromCache tex
			HideEntity m3d\Objects[7]
			;[End Block]
		Case "room1_elevators"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			m3d\Objects[0] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[0],0.03,0.03,0.03
			PositionEntity m3d\Objects[0],96.0*RoomScale,160.0*RoomScale,64.0*RoomScale
			m3d\Objects[1] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[1],0.03,0.03,0.03
			PositionEntity m3d\Objects[1],-96.0*RoomScale,160.0*RoomScale,64.0*RoomScale
			;[End Block]
		Case "room3_window"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity(m3d\Objects[0],0,180,0)
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_KEYCARD],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],416.0*RoomScale,0.0,256.0*RoomScale
			;[End Block]	
		Case "room4_ez"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],8.0/2.0,0,0
			RotateEntity m3d\Objects[1],0,270,0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "cont_966"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			m3d\Rendering = 1
			
			m3d\Objects[0] = LoadAnimMesh_Strict("GFX\npcs\scp-966.b3d",m3d\Scene)
			PositionEntity m3d\Objects[0],0,0,512.0*RoomScale
			EntityFX m3d\Objects[0],1
			temp# = GetINIFloat("DATA\NPCs.ini", "SCP-966", "scale")/40.0
			ScaleEntity m3d\Objects[0], temp, temp, temp
			HideEntity m3d\Objects[0]
			;[End Block]
		Case "room2_servers_1"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[3] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[3],0.03,0.03,0.03
			PositionEntity m3d\Objects[3],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[3],m3d\Objects[1]
			PositionEntity m3d\Objects[1],-208.0*RoomScale,0.0,-736.0*RoomScale
			RotateEntity m3d\Objects[1],0,270,0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			
			;Black Light Sprite
			m3d\Objects[2] = CreateSprite(m_I\Cam)
			ScaleSprite m3d\Objects[2],3,3
			EntityFX m3d\Objects[2],1
			EntityColor m3d\Objects[2],0,0,0
			MoveEntity m3d\Objects[2],0,0,1
			EntityOrder m3d\Objects[2],-1
			EntityAlpha m3d\Objects[2],0.0
			EntityBlend m3d\Objects[2],1
			;[End Block]
		Case "cont_106"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			;The floating box for 106
			m3d\Objects[0] = LoadRMesh("GFX\map\rooms\cont_106\room1062_opt.rmesh",Null)
			EntityParent m3d\Objects[0],m3d\Scene
			ScaleEntity m3d\Objects[0],RoomScale,RoomScale,RoomScale
			PositionEntity m3d\Objects[0],784.0*RoomScale,-980.0*RoomScale,720.0*RoomScale
			;[End Block]
		Case "room2_nuke"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			;First door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],680.0*RoomScale,1504*RoomScale,0
			RotateEntity m3d\Objects[1],0,270,0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			
			m3d\Objects[3] = CreatePivot()
			PositionEntity m3d\Objects[3],0,6,0
			;[End Block]
		Case "checkpoint_hcz"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0],RoomScale,RoomScale,RoomScale)
			PositionEntity m3d\Objects[0],0,0,-400.0*RoomScale
			;[End Block]
		Case "room2_elevator_1"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			CheckPointDoorObj = LoadMesh_Strict("GFX\map\doors\ElevatorDoor.b3d")
			HideEntity CheckPointDoorObj
			
			;Elevator Door
			m3d\Objects[0] = CopyEntity(CheckPointDoorObj,m3d\Scene)
			ScaleEntity(m3d\Objects[0],RoomScale,RoomScale,RoomScale)
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(CheckPointDoorObj,m3d\Scene)
			ScaleEntity(m3d\Objects[1],RoomScale,RoomScale,RoomScale)
			m3d\Objects[2] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[2], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[3] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[3],0.03,0.03,0.03
			PositionEntity m3d\Objects[3],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[2]
			EntityParent m3d\Objects[1],m3d\Objects[2]
			EntityParent m3d\Objects[3],m3d\Objects[2]
			PositionEntity m3d\Objects[2],448.0 * RoomScale,0,0
			RotateEntity m3d\Objects[2],0,-90,0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			MoveEntity(m3d\Objects[1], 0, 0, 8.0 * RoomScale)
			
			;Door
			m3d\Objects[4] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[4], (204.0 * RoomScale) / MeshWidth(m3d\Objects[4]), 312.0 * RoomScale / MeshHeight(m3d\Objects[4]), 16.0 * RoomScale / MeshDepth(m3d\Objects[4]))
			RotateEntity m3d\Objects[4],0,180,0
			m3d\Objects[5] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[5], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[6] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[6],0.03,0.03,0.03
			PositionEntity m3d\Objects[6],0.6,0.7,-0.1
			EntityParent m3d\Objects[4],m3d\Objects[5]
			EntityParent m3d\Objects[6],m3d\Objects[5]
			PositionEntity m3d\Objects[5],0,0,4.0
			MoveEntity(m3d\Objects[4], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "room2_pit"
			;[Block]
			m3d\FogColor = FogColor_HCZ
			
			ParticleTextures[0] = LoadTexture_Strict("GFX\smoke.png",1+2,2)
			
			i = 0
			Local xtemp%,ztemp%
			For  xtemp% = -1 To 1 Step 2
				For ztemp% = -1 To 1
					m3d\Objects[i] = CreatePivot(m3d\Scene)
					PositionEntity m3d\Objects[i],202.0*RoomScale*xtemp,8.0*RoomScale,256.0*RoomScale*ztemp
					If i < 3
						TurnEntity m3d\Objects[i],0,-90,0
					Else
						TurnEntity m3d\Objects[i],0,90,0
					EndIf
					TurnEntity m3d\Objects[i],-45,0,0
					i=i+1
				Next
			Next
			;[End Block]
		Case "pocketdimension"
			;[Block]
			m3d\FogColor = FogColor_PD
			
			Local hallway = LoadRMesh("GFX\map\rooms\pocketdimension\pocketdimension2_opt.rmesh",Null)
			
			For i = 1 To 8
				m3d\Objects[i-1] = CopyEntity(hallway,m3d\Scene)
				ScaleEntity (m3d\Objects[i-1],RoomScale,RoomScale,RoomScale)
				Local angle# = (i-1) * (360.0/8.0)
				
				RotateEntity(m3d\Objects[i-1],0,angle-90,0)
				PositionEntity(m3d\Objects[i-1],Cos(angle)*(512.0*RoomScale),0.0,Sin(angle)*(512.0*RoomScale))
			Next
			
			hallway = FreeEntity_Strict(hallway)
			;[End Block]
		Case "cont_173"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			m3d\Rendering = 1
			m3d\Objects[0] = LoadMesh_Strict("GFX\npcs\173\173body.b3d",m3d\Scene)
			m3d\Objects[8] = LoadMesh_Strict("GFX\npcs\173\173head.b3d",m3d\Scene)
			temp# = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "scale") / MeshDepth(m3d\Objects[0]))
			ScaleEntity m3d\Objects[0],temp,temp,temp
			ScaleEntity m3d\Objects[8],temp,temp,temp
			PositionEntity m3d\Objects[0], 0.0, 0.0, -940.0* RoomScale
			PositionEntity m3d\Objects[8], 0.0, 0.0, -940.0* RoomScale
			HideEntity m3d\Objects[0]
			HideEntity m3d\Objects[8]
			;m3d\Objects[0] = CreatePivot(m3d\Scene)
			
			;Black Light Sprite
			m3d\Objects[1] = CreateSprite(m_I\Cam)
			ScaleSprite m3d\Objects[1],3,3
			EntityFX m3d\Objects[1],1
			EntityColor m3d\Objects[1],0,0,0
			MoveEntity m3d\Objects[1],0,0,1
			EntityOrder m3d\Objects[1],-1
			EntityAlpha m3d\Objects[1],0.0
			EntityBlend m3d\Objects[1],1
			
			;Door
			m3d\Objects[2] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[2], (204.0 * RoomScale) / MeshWidth(m3d\Objects[2]), 312.0 * RoomScale / MeshHeight(m3d\Objects[2]), 16.0 * RoomScale / MeshDepth(m3d\Objects[2]))
			RotateEntity m3d\Objects[2],0,180,0
			m3d\Objects[3] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[3], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[4] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[4],0.03,0.03,0.03
			PositionEntity m3d\Objects[4],0.6,0.7,-0.1
			EntityParent m3d\Objects[2],m3d\Objects[3]
			EntityParent m3d\Objects[4],m3d\Objects[3]
			PositionEntity m3d\Objects[3],0,0,1184.0* RoomScale
			MoveEntity(m3d\Objects[2], 0, 0, 8.0 * RoomScale)
			
			m3d\Objects[5] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[5], (204.0 * RoomScale) / MeshWidth(m3d\Objects[5]), 312.0 * RoomScale / MeshHeight(m3d\Objects[5]), 16.0 * RoomScale / MeshDepth(m3d\Objects[5]))
			RotateEntity m3d\Objects[5],0,180,0
			m3d\Objects[6] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[6], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[7] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[7],0.03,0.03,0.03
			PositionEntity m3d\Objects[7],0.6,0.7,-0.1
			EntityParent m3d\Objects[5],m3d\Objects[6]
			EntityParent m3d\Objects[7],m3d\Objects[6]
			PositionEntity m3d\Objects[6],0,0,-1008.0* RoomScale
			RotateEntity m3d\Objects[6],0,180,0
			MoveEntity(m3d\Objects[5], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "lockroom_1"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			m3d\Rendering = 1
			
			ParticleTextures[0] = LoadTexture_Strict("GFX\smoke.png",1+2,2)
			
			m3d\Objects[0] = CreatePivot(m3d\Scene)
			PositionEntity m3d\Objects[0],-175.0*RoomScale,370.0*RoomScale,656.0*RoomScale
			TurnEntity m3d\Objects[0],90,0,0
			m3d\Objects[1] = CreatePivot(m3d\Scene)
			PositionEntity m3d\Objects[1],-655.0*RoomScale,370.0*RoomScale,240.0*RoomScale
			TurnEntity m3d\Objects[1],90,0,0
			;[End Block]
		Case "cont_914"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			;Output Door
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			PositionEntity m3d\Objects[1],816.0*RoomScale,0.0,528.0*RoomScale
			;[End Block]
		Case "room2_offices_2"
			;[Block]
			m3d\FogColor = FogColor_EZ
			
			m3d\Objects[0] = LoadMesh_Strict("GFX\npcs\duck_low_res.b3d")
			ScaleEntity(m3d\Objects[0], 0.07, 0.07, 0.07)
			EntityParent(m3d\Objects[0], m3d\Scene)
			m3d\Objects[1] = CreatePivot(m3d\Scene)
			PositionEntity(m3d\Objects[1], -808.0 * RoomScale, -72.0 * RoomScale, -40.0 * RoomScale, True)
			m3d\Objects[2] = CreatePivot(m3d\Scene)
			PositionEntity(m3d\Objects[2], -488.0 * RoomScale, 160.0 * RoomScale, 700.0 * RoomScale, True)
			m3d\Objects[3] = CreatePivot(m3d\Scene)
			PositionEntity(m3d\Objects[3], -488.0 * RoomScale, 160.0 * RoomScale, -668.0 * RoomScale, True)
			m3d\Objects[4] = CreatePivot(m3d\Scene)
			PositionEntity(m3d\Objects[4], -572.0 * RoomScale, 350.0 * RoomScale, -4.0 * RoomScale, True)
			m3d\Objects[5] = CreatePivot(m3d\Scene)
			PositionEntity(m3d\Objects[5], -128.0 * RoomScale, 295.0 * RoomScale, -864.0 * RoomScale, True)
			
			;Black Light Sprite
			m3d\Objects[6] = CreateSprite(m_I\Cam)
			ScaleSprite m3d\Objects[6],3,3
			EntityFX m3d\Objects[6],1
			EntityColor m3d\Objects[6],0,0,0
			MoveEntity m3d\Objects[6],0,0,1
			EntityOrder m3d\Objects[6],-1
			EntityAlpha m3d\Objects[6],0.0
			EntityBlend m3d\Objects[6],1
			
			temp = Rand(1,4)
			PositionEntity(m3d\Objects[0], EntityX(m3d\Objects[temp],True),EntityY(m3d\Objects[temp],True),EntityZ(m3d\Objects[temp],True),True)
			
			;Door
			m3d\Objects[7] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[7], (204.0 * RoomScale) / MeshWidth(m3d\Objects[7]), 312.0 * RoomScale / MeshHeight(m3d\Objects[7]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[7],0,180,0
			m3d\Objects[8] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[8], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[9] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[9],0.03,0.03,0.03
			PositionEntity m3d\Objects[9],0.6,0.7,-0.1
			EntityParent m3d\Objects[7],m3d\Objects[8]
			EntityParent m3d\Objects[9],m3d\Objects[8]
			PositionEntity m3d\Objects[8],0,0,8.0/2.0
			MoveEntity(m3d\Objects[7], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "room2_2"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			m3d\Objects[3] = LoadRMesh("GFX\map\rooms\room2_2\fan_opt.rmesh",Null)
			EntityParent(m3d\Objects[3], m3d\Scene)
			ScaleEntity m3d\Objects[3], RoomScale, RoomScale, RoomScale
			PositionEntity(m3d\Objects[3], -248 * RoomScale, 528 * RoomScale, 0, 0)
			
			;Door
			m3d\Objects[0] = CopyEntity(d_I\DoorOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[0], (204.0 * RoomScale) / MeshWidth(m3d\Objects[0]), 312.0 * RoomScale / MeshHeight(m3d\Objects[0]), 16.0 * RoomScale / MeshDepth(m3d\Objects[0]))
			RotateEntity m3d\Objects[0],0,180,0
			m3d\Objects[1] = CopyEntity(d_I\DoorframeOBJ,m3d\Scene)
			ScaleEntity(m3d\Objects[1], (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
			m3d\Objects[2] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL],m3d\Scene)
			ScaleEntity m3d\Objects[2],0.03,0.03,0.03
			PositionEntity m3d\Objects[2],0.6,0.7,-0.1
			EntityParent m3d\Objects[0],m3d\Objects[1]
			EntityParent m3d\Objects[2],m3d\Objects[1]
			PositionEntity m3d\Objects[1],0,0,-4.0
			RotateEntity m3d\Objects[1],0,180,0
			MoveEntity(m3d\Objects[0], 0, 0, 8.0 * RoomScale)
			;[End Block]
		Case "surveil_room"
			;[Block]
			m3d\FogColor = FogColor_LCZ
			
			Local scale# = RoomScale * 4.5 * 0.4
			Local screen%
			
			m3d\Objects[15] = LoadAnimTexture("GFX\SL_monitors_checkpoint.jpg",1,512,512,0,4)
			m3d\Objects[16] = LoadAnimTexture("GFX\Sl_monitors.jpg",1,256,256,0,8)
			m3d\Objects[17] = LoadMesh_Strict("GFX\map\monitor.b3d")
			HideEntity m3d\Objects[17]
			
			;Monitor Objects
			For i = 0 To 14
					m3d\Objects[i] = CopyEntity(m3d\Objects[17])
					ScaleEntity(m3d\Objects[i], scale, scale, scale)
					If i <> 4 And i <> 13 Then
						screen = CreateSprite()
						EntityFX screen,17
						SpriteViewMode screen,2
						ScaleSprite(screen, MeshWidth(m3d\Objects[17]) * scale * 0.95 * 0.5, MeshHeight(m3d\Objects[17]) * scale * 0.95 * 0.5)
						Select i
							Case 0
								EntityTexture screen,m3d\Objects[16],0
							Case 2
								EntityTexture screen,m3d\Objects[16],2
							Case 3
								EntityTexture screen,m3d\Objects[16],1
							Case 8
								EntityTexture screen,m3d\Objects[16],4
							Case 9
								EntityTexture screen,m3d\Objects[16],5
							Case 10
								EntityTexture screen,m3d\Objects[16],3
							Case 11
								EntityTexture screen,m3d\Objects[16],7
							Default
								EntityTexture screen,m3d\Objects[15],3
						End Select
						EntityParent screen,m3d\Objects[i]
					ElseIf i = 4 Then
						m3d\Objects[20] = CreateSprite()
						EntityFX m3d\Objects[20],17
						SpriteViewMode m3d\Objects[20],2
						ScaleSprite(m3d\Objects[20], MeshWidth(m3d\Objects[17]) * scale * 0.95 * 0.5, MeshHeight(m3d\Objects[17]) * scale * 0.95 * 0.5)
						EntityTexture m3d\Objects[20],m3d\Objects[15],2
						EntityParent m3d\Objects[20],m3d\Objects[i]
					Else
						m3d\Objects[21] = CreateSprite()
						EntityFX m3d\Objects[21],17
						SpriteViewMode m3d\Objects[21],2
						ScaleSprite(m3d\Objects[21], MeshWidth(m3d\Objects[17]) * scale * 0.95 * 0.5, MeshHeight(m3d\Objects[17]) * scale * 0.95 * 0.5)
						EntityTexture m3d\Objects[21],m3d\Objects[16],6
						EntityParent m3d\Objects[21],m3d\Objects[i]
					EndIf
			Next
			For i = 0 To 2
				PositionEntity m3d\Objects[i],-207.94*RoomScale,(648.0+(112*i))*RoomScale,-60.0686*RoomScale
				RotateEntity m3d\Objects[i],0,105,0
				EntityParent m3d\Objects[i],m3d\Scene
				DebugLog i
			Next
			For i = 3 To 5
				PositionEntity m3d\Objects[i],-231.489*RoomScale,(648.0+(112*(i-3)))*RoomScale,95.7443*RoomScale
				RotateEntity m3d\Objects[i],0,90,0
				EntityParent m3d\Objects[i],m3d\Scene
				DebugLog i
			Next
			For i = 6 To 8; Step 2
				PositionEntity m3d\Objects[i],-231.489*RoomScale,(648.0+(112*(i-6)))*RoomScale,255.744*RoomScale
				RotateEntity m3d\Objects[i],0,90,0
				EntityParent m3d\Objects[i],m3d\Scene
				DebugLog i
			Next
			For i = 9 To 11
				PositionEntity m3d\Objects[i],-231.489*RoomScale,(648.0+(112*(i-9)))*RoomScale,415.744*RoomScale
				RotateEntity m3d\Objects[i],0,90,0
				EntityParent m3d\Objects[i],m3d\Scene
				DebugLog i
			Next
			For i = 12 To 14
				PositionEntity m3d\Objects[i],-208.138*RoomScale,(648.0+(112*(i-12)))*RoomScale,571.583*RoomScale
				RotateEntity m3d\Objects[i],0,75,0
				EntityParent m3d\Objects[i],m3d\Scene
				DebugLog i
			Next
			;[End block]
		Default
			;[Block]
			m3d\FogColor = "000000000"
			;[End Block]
	End Select
	
	If m3d\Rendering Then
		m3d\RenderingObj[0] = CreateCamera()
		CameraProjMode m3d\RenderingObj[0],2
		CameraZoom m3d\RenderingObj[0],0.1
		CameraClsMode m3d\RenderingObj[0],0,0
		CameraRange m3d\RenderingObj[0],0.1,1.5
		MoveEntity m3d\RenderingObj[0],0,0,-10000
		CameraViewport m3d\RenderingObj[0],0,0,opt\GraphicWidth,opt\GraphicHeight
		
		Local spr% = CreateMesh(m3d\RenderingObj[0])
		Local sf% = CreateSurface(spr)
		AddVertex sf, -1, 1, 0, 0, 0
		AddVertex sf, 1, 1, 0, 1, 0
		AddVertex sf, -1, -1, 0, 0, 1
		AddVertex sf, 1, -1, 0, 1, 1
		AddTriangle sf, 0, 1, 2
		AddTriangle sf, 3, 2, 1
		EntityFX spr, 17
		ScaleEntity spr, 2048.0 / Float(opt\GraphicWidth), 2048.0 / Float(opt\GraphicHeight), 1
		PositionEntity spr, 0, 0, 1.0001
		EntityOrder spr, -100001
		EntityBlend spr, 1
		m3d\RenderingObj[1] = spr
		
		m3d\RenderingObj[2] = CreateTexture(2048, 2048, 1+256)
		m3d\RenderingObj[3] = CreateTexture(2048, 2048, 1+256)
		TextureBlend m3d\RenderingObj[3],3
		SetBuffer(TextureBuffer(m3d\RenderingObj[3]))
		ClsColor 0,0,0
		Cls
		SetBuffer(BackBuffer())
		EntityTexture m3d\RenderingObj[1],m3d\RenderingObj[2],0,0
		EntityTexture m3d\RenderingObj[1],m3d\RenderingObj[3],0,1
		
		CameraProjMode m3d\RenderingObj[0],0
	EndIf
	
	m3d\FreeRoam% = False
	
	DeleteTextureEntriesFromCache(0)
	
End Function

Function Update3DMenu()
	CatchErrors("Update3DMenu ("+m3d\RoomName+")")
	
	Local i%,p.Particles
	
	AmbientLight Brightness,Brightness,Brightness
	Select m3d\RoomName
		Case "gate_a_intro"
			;[Block]
			PositionEntity m3d\Pivot,5,5.5,14
			RotateEntity m3d\Pivot,10,40,0
			PositionEntity m3d\Objects[0],EntityX(m3d\Pivot),EntityY(m3d\Pivot),EntityZ(m3d\Pivot),True
			;[End Block]
		Case "room3_offices"
			;[Block]
			PositionEntity m3d\Pivot,3.5,0.1,-0.8
			RotateEntity m3d\Pivot,-15,65,0
			;[End Block]
		Case "gate_a_entrance"
			;[Block]
			PositionEntity m3d\Pivot,-418.0*RoomScale,224.0*RoomScale,0.0
			RotateEntity m3d\Pivot,0,-22,0
			UpdateAlarmRotor(m3d\Objects[2],4)
			;[End Block]
		Case "room2_2"
			;[Block]
			PositionEntity m3d\Pivot,156.0*RoomScale,258.0*RoomScale,420.0*RoomScale
			RotateEntity m3d\Pivot,0,160,0
			
			TurnEntity (m3d\Objects[3], m3d\State[2]*FPSfactor, 0, 0)
			;If m3d\State[2] > 0.01 Then
				;m3d\Objects[4] = LoopSound2 (RoomAmbience[9], m3d\Objects[4], m_I\Cam, m3d\Objects[3], 5.0, (m3d\State[2]/8.0))
			;EndIf
			m3d\State[2] = CurveValue(m3d\State[1]*5, m3d\State[2], 150.0)		
			
			If m3d\State[0] < 0 Then
				m3d\State[0] = Rand(15,30)*70
				;temp = m3d\State[1]
				m3d\State[1] = Rand(0,1)
					;If temp = 0 And m3d\State[1] = 1.0 Then ;turn on the fan
					;	PlaySound2 (LoadTempSound("SFX\ambient\Room ambience\FanOn.ogg"), m_I\Cam, m3d\Objects[3], 4.0)
					;ElseIf temp = 1 And m3d\State[1] = 0.0 ;turn off the fan
					;	PlaySound2 (LoadTempSound("SFX\ambient\Room ambience\FanOff.ogg"), m_I\Cam, m3d\Objects[3], 4.0)
					;EndIf
			Else
				m3d\State[0] = m3d\State[0]-FPSfactor
			EndIf
			
			;[End Block]	
		Case "room2_gs"
			;[Block]
			PositionEntity m3d\Pivot,0.75,0.9,0.8
			RotateEntity m3d\Pivot,0,40,0
			
			If m3d\State[0]=0 Then
				If Rand(1000)=1 Then
					m3d\State[0]=1
					If m3d\State[1]=0 Then
						;PlaySound2(OpenDoorSFX[0 * 3 + Rand(0,2)],m3d\Cam,m3d\Objects[5]
						If Rand(10)=5 Then
							ShowEntity m3d\Objects[7]
						EndIf
					;Else
						;PlaySound2(CloseDoorSFX[0 * 3 + Rand(0,2)],m3d\Cam,m3d\Objects[5])
					EndIf
				EndIf
			Else
				If m3d\State[1]=0 Then
					If m3d\State[2]<180 Then
						m3d\State[2] = Min(180, m3d\State[2] + FPSfactor * 2)
						MoveEntity(m3d\Objects[3], Sin(m3d\State[2]) * FPSfactor / 80.0, 0, 0)
						MoveEntity(m3d\Objects[4], Sin(m3d\State[2]) * FPSfactor / 80.0, 0, 0)
					Else
						m3d\State[1]=1
						m3d\State[0]=0
					EndIf
				Else
					If m3d\State[2]>0 Then
						m3d\State[2] = Max(0, m3d\State[2] - FPSfactor * 2)
						MoveEntity(m3d\Objects[3], Sin(m3d\State[2]) * -FPSfactor / 80.0, 0, 0)
						MoveEntity(m3d\Objects[4], Sin(m3d\State[2]) * -FPSfactor / 80.0, 0, 0)
					Else
						m3d\State[1]=0
						m3d\State[0]=0
						HideEntity m3d\Objects[7]
					EndIf
				EndIf
			EndIf
			;[End Block]
		Case "room2_offices_2"
			;[Block]
			PositionEntity m3d\Pivot,-96.0*RoomScale,336.0*RoomScale,-896.0*RoomScale
			RotateEntity m3d\Pivot,15,50,0
			CameraFogRange m_I\Cam, 0.5,8
			CameraFogColor (m_I\Cam,200,200,200)
			CameraClsColor (m_I\Cam,200,200,200)
			CameraRange(m_I\Cam,0.01,8)
			If Rand(300)=1 Then
				m3d\State[2] = Rnd(1.0,5.0)
				PositionEntity m3d\Objects[0], EntityX(m3d\Objects[m3d\State[2]]), EntityY(m3d\Objects[m3d\State[2]]), EntityZ(m3d\Objects[m3d\State[2]]),True
				RotateEntity m3d\Objects[0], 0, DeltaYaw(m3d\Objects[0],m3d\Pivot), 0,True
			EndIf
			m3d\State[3] = 0
			m3d\State[2] = Max(m3d\State[2] - (FPSfactor / 35.0), 0)
			m3d\State[3] = Min(Max(m3d\State[3], m3d\State[2] * Rnd(0.3, 0.8)), 1.0)
			EntityAlpha(m3d\Objects[6], m3d\State[3])
			;[End Block]
		Case "room2_offices_3"
			;[Block]
			PositionEntity m3d\Pivot,-84.0*RoomScale,128.0*RoomScale,-574.0*RoomScale
			RotateEntity m3d\Pivot,-10,-30,0
			;[End Block]	
		Case "room1_elevators"
			;[Block]
			PositionEntity m3d\Pivot,1.5,0.9,-3.8
			RotateEntity m3d\Pivot,0,30,0
			;[End Block]
		Case "room2_elevator_1"
			;[Block]
			PositionEntity m3d\Pivot,-132.0*RoomScale,194.0*RoomScale,-508.0*RoomScale
			RotateEntity m3d\Pivot,0,-20,0
			;[End Block]
		Case "room4_ez"
			;[Block]
			PositionEntity m3d\Pivot,-600.0*RoomScale,64.0*RoomScale,-128.0*RoomScale
			RotateEntity m3d\Pivot,-15,-55,0
			;[End Block]
		Case "room2_servers_2"
			;[Block]
			PositionEntity m3d\Pivot,0.5,-1.9,-3.3
			RotateEntity m3d\Pivot,0,0,0
			;[End Block]
		Case "room3_window"
			;[Block]
			PositionEntity m3d\Pivot,0,250.0*RoomScale,-708.0*RoomScale
			RotateEntity m3d\Pivot,-5,0,0
			;[End Block]	
		Case "cont_966"
			;[Block]
			If m3d\State[0] = 0 Then
				m3d\State[1]=m3d\State[1]+0.2 * FPSfactor
				If m3d\State[1] > (45 * 1.3) Then m3d\State[0] = 1
			Else
				m3d\State[1]=m3d\State[1]-0.2 * FPSfactor
				If m3d\State[1] < (-45 * 1.3) Then m3d\State[0] = 0
			EndIf
			PositionEntity m3d\Pivot,-312.0*RoomScale,414*RoomScale,656*RoomScale
			RotateEntity m3d\Pivot,20,225+Max(Min(m3d\State[1], 45), -45),0
			
			Animate2(m3d\Objects[0],AnimTime(m3d\Objects[0]),2,456,0.22,True)
			
			If m3d\State[2]=0 Then
				If m3d\State[4]=0 Then
					If Rand(250)=1 Then
						RotateEntity m3d\Objects[0],-90,Rnd(360),0
						m3d\State[4] = 70*3
						m3d\State[2] = 70*Rand(3,7)
					EndIf
				Else
					m3d\State[4]=Max(m3d\State[4]-FPSfactor,0)
				EndIf
				EntityColor m3d\RenderingObj[1],255,255,255
				CameraFogMode m_I\Cam,1
				HideEntity m3d\Objects[0]
				m3d\Distortion = 0.0
			Else
				If m3d\State[3]=0 Then
					ShowEntity m3d\Objects[0]
					m3d\Distortion = 0.0
					If Rand(175)=1 Then
						HideEntity m3d\Objects[0]
						m3d\State[3]=10
						m3d\Distortion=0.05
					EndIf
				Else
					HideEntity m3d\Objects[0]
					m3d\State[3]=Max(m3d\State[3]-FPSfactor,0)
					m3d\Distortion=0.05
				EndIf
				EntityColor m3d\RenderingObj[1],255*(m3d\State[3]>0),155+(100*(m3d\State[3]=0)),255*(m3d\State[3]>0)
				CameraFogMode m_I\Cam,0
				m3d\State[2]=Max(m3d\State[2]-FPSfactor,0)
			EndIf
			;[End Block]
		Case "room2_servers_1"
			;[Block]
			If m3d\State[0]=0 Then
				If m3d\State[1]<5.0 Then
					m3d\State[1]=Min(m3d\State[1]+0.02*FPSfactor,5)
				Else
					m3d\State[0]=1
				EndIf
			Else
				If m3d\State[1]>-5.0 Then
					m3d\State[1]=Max(m3d\State[1]-0.02*FPSfactor,-5)
				Else
					m3d\State[0]=0
				EndIf
			EndIf
			
			If Rand(300)=1 Then
				;If m3d\State[2]=0 Then
				;	Local tempchn% = 0
				;	tempchn% = PlaySound_Strict(IntroSFX[Rand(1,3)])
				;	ChannelVolume tempchn%,Rnd(0.1,0.3)*opt\SFXVolume
				;EndIf
				m3d\State[2] = Rnd(1.0,2.0)
			EndIf
			m3d\State[3] = 0
			m3d\State[2] = Max(m3d\State[2] - (FPSfactor / 35.0), 0)
			m3d\State[3] = Min(Max(m3d\State[3], m3d\State[2] * Rnd(0.3, 0.8)), 1.0)
			EntityAlpha(m3d\Objects[2], m3d\State[3])
			
			PositionEntity m3d\Pivot,-4.25,1.4,-2.8
			RotateEntity m3d\Pivot,15,-45+m3d\State[1],0
			;[End Block]
		Case "cont_106"
			;[Block]
			PositionEntity m3d\Objects[0],EntityX(m3d\Objects[0]),CurveValue(-980.0*RoomScale + Sin(Float(MilliSecs())*0.04)*0.07,EntityY(m3d\Objects[0]),200.0),EntityZ(m3d\Objects[0])
			RotateEntity m3d\Objects[0], Sin(Float(MilliSecs())*0.03), EntityYaw(m3d\Objects[0]), -Sin(Float(MilliSecs())*0.025)
			
			PositionEntity m3d\Pivot,300.0*RoomScale,274.0*RoomScale,2164.0*RoomScale
			RotateEntity m3d\Pivot,10,200,0
			;[End Block]
		Case "room2_nuke"
			;[Block]
			If m3d\State[0]=0 Then
				If m3d\State[1]<1.2 Then
					m3d\State[1]=Min(m3d\State[1]+0.005*FPSfactor,1.2)
				ElseIf m3d\State[1]>=1.2 And m3d\State[1]<1.49 Then
					m3d\State[1]=CurveValue(1.5,m3d\State[1],100.0)
				Else
					m3d\State[0]=1
				EndIf
			Else
				If m3d\State[1]>-2.0 Then
					m3d\State[1]=Max(m3d\State[1]-0.005*FPSfactor,-2.0)
				ElseIf m3d\State[1]<=-2.0 And m3d\State[1]>-2.29 Then
					m3d\State[1]=CurveValue(-2.3,m3d\State[1],100.0)
				Else
					m3d\State[0]=0
				EndIf
			EndIf
			
			PointEntity m3d\Pivot,m3d\Objects[3]
			PositionEntity m3d\Pivot,-2.6,7.0,m3d\State[1]
			;[End Block]
		Case "checkpoint_hcz"
			;[Block]
			PositionEntity m3d\Pivot,-352.0*RoomScale,65.0*RoomScale,-928.0*RoomScale
			RotateEntity m3d\Pivot,-8,-22,0
			;[End Block]
		Case "room2_pit"
			;[Block]
			For i = 0 To 5
				p.Particles = CreateParticle(EntityX(m3d\Objects[i], True), EntityY(m3d\Objects[i], True), EntityZ(m3d\Objects[i], True), 0, 0.03, -0.2, 200)
				EntityParent(p\obj,m3d\Scene)
				p\speed = 0.0045
				RotateEntity(p\pvt, EntityPitch(m3d\Objects[i], True), EntityYaw(m3d\Objects[i], True), EntityRoll(m3d\Objects[i], True), True)
				
				TurnEntity(p\pvt, Rnd(-30, 30), Rnd(-30, 30), 0)
				
				TurnEntity p\obj, 0,0,Rnd(360)
				
				p\SizeChange = 0.007
				
				p\Achange = -0.016
			Next
			
			PositionEntity m3d\Pivot,-0.0,0.3,-2.5+m3d\State[1]
			RotateEntity m3d\Pivot,-10,0,0
			;[End Block]
		Case "pocketdimension"
			;[Block]
			PositionEntity m3d\Pivot,0.0,1.0,0.0
			TurnEntity m3d\Pivot,0,-0.05*FPSfactor,0
			RotateEntity m3d\Pivot,0,EntityYaw(m3d\Pivot),WrapAngle(Sin(MilliSecs()/150.0)*30.0)
			
			m3d\State[0] = m3d\State[0] + FPSfactor
			
			ScaleEntity(m3d\RoomMesh,RoomScale, RoomScale*(1.0 + Sin(m3d\State[0]/14.0)*0.2), RoomScale)
			For i = 0 To 7
				ScaleEntity(m3d\Objects[i],RoomScale*(1.0 + Abs(Sin(m3d\State[0]/21.0+i*45.0)*0.1)),RoomScale*(1.0 + Sin(m3d\State[0]/14.0+i*20.0)*0.1), RoomScale,True)
			Next
			;[End Block]
		Case "cont_173"
			;[Block]
			If m3d\State[0] = 0 Then
				m3d\State[1]=m3d\State[1]+0.2 * FPSfactor
				If m3d\State[1] > (45 * 1.3) Then
					m3d\State[0] = 1
					If Rand(10)=1 Then
						ShowEntity m3d\Objects[0]
						ShowEntity m3d\Objects[8]
					Else
						HideEntity m3d\Objects[0]
						HideEntity m3d\Objects[8]
					EndIf
				EndIf
			Else
				m3d\State[1]=m3d\State[1]-0.2 * FPSfactor
				If m3d\State[1] < (-45 * 1.3) Then
					m3d\State[0] = 0
					
				EndIf
			EndIf
			PositionEntity m3d\Pivot,-336.0 * RoomScale,352 * RoomScale,48.0 * RoomScale
			RotateEntity m3d\Pivot,20,270+Max(Min(m3d\State[1], 45), -45),0
			;[End Block]
		Case "lockroom_1"
			;[Block]
			If m3d\State[2]<70*4 Then
				For i = 0 To 1
					p.Particles = CreateParticle(EntityX(m3d\Objects[i], True), EntityY(m3d\Objects[i], True), EntityZ(m3d\Objects[i], True), 0, 0.03, -0.24, 200)
					EntityParent(p\obj,m3d\Scene)
					p\speed = 0.05
					RotateEntity(p\pvt, EntityPitch(m3d\Objects[i], True), EntityYaw(m3d\Objects[i], True), EntityRoll(m3d\Objects[i], True), True)
					
					TurnEntity(p\pvt, Rnd(-20, 20), Rnd(-20, 20), 0)
					
					TurnEntity p\obj, 0,0,Rnd(360)
					
					p\SizeChange = 0.007
					
					p\Achange = -0.006
				Next
				m3d\State[2]=m3d\State[2]+FPSfactor
			ElseIf m3d\State[2]>=70*4 And m3d\State[2]<70*7 Then
				m3d\State[2]=m3d\State[2]+FPSfactor
			Else
				If Rand(500)=1 Then
					m3d\State[2]=Rnd(-(70*2),0)
				EndIf
			EndIf
			
			If m3d\State[0] = 0 Then
				m3d\State[1]=m3d\State[1]+0.2 * FPSfactor
				If m3d\State[1] > (45 * 1.3) Then
					m3d\State[0] = 1
				EndIf
			Else
				m3d\State[1]=m3d\State[1]-0.2 * FPSfactor
				If m3d\State[1] < (-45 * 1.3) Then
					m3d\State[0] = 0
				EndIf
			EndIf
			PositionEntity m3d\Pivot,-112*RoomScale,384*RoomScale,112*RoomScale
			RotateEntity m3d\Pivot,40,45+Max(Min(m3d\State[1], 45), -45),0
			;[End Block]
		Case "cont_914"
			;[Block]
			PositionEntity m3d\Pivot,3.5,1.25,-1.25
			
			If m3d\State[0]=0 Then
				While m3d\State[0]=0
					m3d\State[0]=Rand(-1,1)
				Wend
				While m3d\State[1]=0
					m3d\State[1]=Rand(-1,1)
				Wend
			Else
				If m3d\State[0]=-1 Then
					If m3d\State[2]>m3d\State[0] Then
						m3d\State[2]=Max(m3d\State[2]-0.01*FPSfactor,m3d\State[0])
					Else
						m3d\State[0]=0
					EndIf
				Else
					If m3d\State[2]<m3d\State[0] Then
						m3d\State[2]=Min(m3d\State[2]+0.01*FPSfactor,m3d\State[0])
					Else
						m3d\State[0]=0
					EndIf
				EndIf
				
				If m3d\State[1]=-1 Then
					If m3d\State[3]>m3d\State[1] Then
						m3d\State[3]=Max(m3d\State[3]-0.01*FPSfactor,m3d\State[1])
					Else
						m3d\State[1]=0
					EndIf
				Else
					If m3d\State[3]<m3d\State[1] Then
						m3d\State[3]=Min(m3d\State[3]+0.01*FPSfactor,m3d\State[1])
					Else
						m3d\State[1]=0
					EndIf
				EndIf
			EndIf
			
			RotateEntity m3d\Pivot,15+m3d\State[2],30+m3d\State[3],0
			;[End Block]
		Case "surveil_room"
			;[Block]
			PositionEntity m3d\Pivot,480.0*RoomScale,572.0*RoomScale,800.0*RoomScale
			RotateEntity m3d\Pivot,-10,150,0
			;[End Block]	
		Default
			;[Block]
			PositionEntity m3d\Pivot,0.0,0.5,-1.0
			RotateEntity m3d\Pivot,0,0,0
			;[End Block]
	End Select
	
	If m3d\FreeRoam
		EntityParent m_I\Cam,0
		RotateEntity m_I\Cam,EntityPitch(m_I\Cam)+(MouseYSpeed()*0.25),EntityYaw(m_I\Cam)-(MouseXSpeed()*0.25),0
		MoveEntity m_I\Cam,0.05*((KeyDown(KEY_LEFT)*-1)+(KeyDown(KEY_RIGHT)))*FPSfactor,0,0.05*((KeyDown(KEY_UP))+(KeyDown(KEY_DOWN)*-1))*FPSfactor
		MoveMouse opt\GraphicWidth/2,opt\GraphicHeight/2
	Else
		EntityParent m_I\Cam,m3d\Pivot
		PositionEntity m_I\Cam,0,0,0
		RotateEntity m_I\Cam,0,0,0
	EndIf
	
	Local FogColorR% = Left(m3d\FogColor,3)
	Local FogColorG% = Mid(m3d\FogColor,4,3)
	Local FogColorB% = Right(m3d\FogColor,3)
	CameraFogColor m_I\Cam,FogColorR,FogColorG,FogColorB
	CameraClsColor m_I\Cam,FogColorR,FogColorG,FogColorB
	
	UpdateParticles()
	UpdateLightsMenu3D(m_I\Cam)
	
	UpdateWorld
	
	CatchErrors("Uncaught Update3DMenu ("+m3d\RoomName+")")
End Function

Function Null3DMenu()
	CatchErrors("Null3DMenu")
	
	Local i
	
	DeleteTextureEntriesFromCache(2)
	
	Delete Each DoorInstance
	
	Delete Each Materials
	Delete Each Props
	Delete Each Menu3DLights
	Delete Each Particles
	Delete Each MenuLogo
	Delete Each FluLight
	Delete Each TempFluLight
	
	ClearWorld
	ResetTimingAccumulator()
	Camera = 0
	ark_blur_cam = 0
	m_I\Cam = 0
	m3d\Scene = 0
	InitFastResize()
	
	For i=0 To 9
		If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	
	Delete Each Menu3DInstance
	
	CatchErrors("Uncaught (Null3DMenu)")
End Function

Function AddLightMenu3D.Menu3DLights(x#,y#,z#,ltype%,range#,r%,g%,b%)
	Local ml.Menu3DLights = New Menu3DLights
	Local i
	
	ml\Lights = CreateLight(ltype,m3d\Scene)
	LightRange(ml\Lights,range)
	LightColor(ml\Lights,r,g,b)
	PositionEntity(ml\Lights,x,y,z,True)
	
	ml\LightIntensity = (r+g+b)/255.0/3.0
	
	ml\LightSprites = CreateSprite(m3d\Scene)
	PositionEntity(ml\LightSprites, x, y, z)
	ScaleSprite(ml\LightSprites, 0.13 , 0.13)
	EntityTexture(ml\LightSprites, LightSpriteTex[0])
	EntityBlend (ml\LightSprites, 3)
	EntityFX ml\LightSprites,1+8
	EntityAutoFade ml\LightSprites,CameraFogNear,CameraFogFar
	
	ml\LightSpritesPivot = CreatePivot(m3d\Scene)
	EntityRadius ml\LightSpritesPivot,0.05
	PositionEntity(ml\LightSpritesPivot, x, y, z)
	
	ml\LightSprites2 = CreateSprite(m3d\Scene)
	PositionEntity(ml\LightSprites2, x, y, z)
	ScaleSprite(ml\LightSprites2, 0.6, 0.6)
	EntityTexture(ml\LightSprites2, LightSpriteTex[2])
	EntityBlend(ml\LightSprites2, 3)
	EntityOrder(ml\LightSprites2, -1)
	EntityColor(ml\LightSprites2, r%, g%, b%)
	EntityFX(ml\LightSprites2,1+8)
	RotateEntity(ml\LightSprites2,0,0,Rand(360))
	SpriteViewMode(ml\LightSprites2,1)
	ml\LightSpriteHidden% = True
	HideEntity ml\LightSprites2
	ml\LightFlicker% = Rand(1,10)
	
	ml\LightR = r
	ml\LightG = g
	ml\LightB = b
	
	HideEntity ml\Lights
	
	Local ml_before.Menu3DLights = Before(ml)
	
	Return ml
End Function

Function UpdateLightsMenu3D(cam%)
	
	Local ml.Menu3DLights, i, random#, alpha#
	
	For ml = Each Menu3DLights
		If ml\Lights%<>0
			If opt\EnableRoomLights%
				ShowEntity ml\LightSprites
				
				If EntityDistanceSquared(cam%,ml\Lights%)<PowTwo(8.5)
					If ml\LightHidden
						ShowEntity ml\Lights%
						ml\LightHidden = False
					EndIf
				Else
					If (Not ml\LightHidden)
						HideEntity ml\Lights%
						ml\LightHidden = True
					EndIf
				EndIf
				
				If ml\LightCone<>0 Then ShowEntity ml\LightCone
				If ml\LightConeSpark<>0 
					If ml\LightConeSparkTimer>0 And ml\LightConeSparkTimer<10
						ShowEntity ml\LightConeSpark
						ml\LightConeSparkTimer=ml\LightConeSparkTimer+FPSfactor
					Else
						HideEntity ml\LightConeSpark
						ml\LightConeSparkTimer=0
					EndIf
				EndIf
				
				If (EntityDistanceSquared(cam%,ml\LightSprites2)<PowTwo(8.5)) ;Lor ml\RoomTemplate\UseLightCones)
					If EntityVisible(cam%,ml\LightSpritesPivot) ;Lor ml\RoomTemplate\UseLightCones
						If ml\LightSpriteHidden%
							ShowEntity ml\LightSprites2%
							ml\LightSpriteHidden% = False
						EndIf
						If ml\LightFlicker%<5
							random# = Rnd(0.38,0.42)
						ElseIf ml\LightFlicker%>4 And ml\LightFlicker%<10
							random# = Rnd(0.35,0.45)
						Else
							random# = Rnd(0.3,0.5)
						EndIf
						ScaleSprite ml\LightSprites2,random#,random#
						;If ml\LightCone<>0
						;	ScaleEntity ml\LightCone,0.005+((-0.4+random#)*0.025),0.005+((-0.4+random#)*0.025),0.005+((-0.4+random#)*0.025)
						;	If ml\LightFlicker%>4
						;		If Rand(400)=1
						;			SetEmitter(ml\LightSpritesPivot,ParticleEffect[0])
						;			PlaySound2(IntroSFX[Rand(1,3)],cam,ml\LightSpritesPivot)
						;			ShowEntity ml\LightConeSpark
						;			ml\LightConeSparkTimer = FPSfactor
						;		EndIf
						;	EndIf
						;EndIf
						alpha# = Float(Inverse(Max(Min((EntityDistance(cam%,ml\LightSpritesPivot)+0.5)/7.5,1.0),0.0)))
						
						If alpha# > 0.0
							EntityAlpha ml\LightSprites2,Max(3*(Brightness/255)*(ml\LightIntensity/2),1)*alpha#
						Else
							;Instead of rendering the sprite invisible, just hiding it if the player is far away from it
							If (Not ml\LightSpriteHidden%)
								HideEntity ml\LightSprites2
								ml\LightSpriteHidden%=True
							EndIf
						EndIf
						
						;If ml\RoomTemplate\UseLightCones
						;	If EntityDistance(cam%,ml\LightSprites2)>=8.5 Lor (Not EntityVisible(cam%,ml\LightSpritesPivot))
						;		HideEntity ml\LightSprites2%
						;		ml\LightSpriteHidden% = True
						;	EndIf
						;EndIf
					Else
						If (Not ml\LightSpriteHidden%)
							HideEntity ml\LightSprites2%
							ml\LightSpriteHidden% = True
						EndIf
					EndIf
				Else
					If (Not ml\LightSpriteHidden%)
						HideEntity ml\LightSprites2%
						ml\LightSpriteHidden% = True
						If ml\LightCone<>0 Then HideEntity ml\LightCone
						If ml\LightConeSpark<>0 HideEntity ml\LightConeSpark
					EndIf
				EndIf
			Else
				If (Not ml\LightHidden)
					HideEntity ml\Lights%
					ml\LightHidden = True
				EndIf
				If (Not ml\LightSpriteHidden)
					HideEntity ml\LightSprites2
					ml\LightSpriteHidden=True
				EndIf
				If ml\LightCone<>0 Then HideEntity ml\LightCone
				If ml\LightConeSpark<>0 HideEntity ml\LightConeSpark
			EndIf
		EndIf
	Next
End Function

Function DrawButtonMenu3D%(x%, y%, width%, height%, txt$, bigfont% = True, waitForMouseUp%=False, usingAA%=True, currButton%=-1)
	Local clicked% = False
	
	Local mb.MenuButton
	Local buttonexists%=False
	For mb = Each MenuButton
		If mb\x=x And mb\y=y And mb\width=width And mb\height=height
			buttonexists=True
			Exit
		EndIf
	Next
	If (Not buttonexists)
		mb = New MenuButton
		mb\x = x
		mb\y = y
		mb\width = width
		mb\height = height
		mb\txt = txt
		mb\bigfont = bigfont
		mb\currButton = currButton
		mb\Menu3D = True
	EndIf
	
	If (Not ConsoleOpen)
		If (Not co\Enabled)
			If MouseOn(x, y, width, height) Then
				If (MouseHit1 And (Not waitForMouseUp)) Lor (MouseUp1 And waitForMouseUp) Then
					clicked = True
					PlaySound_Strict(ButtonSFX)
					If waitForMouseUp Then
						MouseUp1 = False
					Else
						MouseHit1 = False
					EndIf
				EndIf
			EndIf
		Else
			If co\CurrButton[0] = currButton
				If (co\PressedButton)
					PlaySound_Strict(ButtonSFX)
					clicked = True
					FlushJoy()
				EndIf
			EndIf
		EndIf
	EndIf
	
	Return clicked
End Function

Function Reload()
	
	Local name$ = m3d\RoomName
	Null3DMenu()
	Load3DMenu(name)
	
End Function

Function ReloadAll()
	Local i%,snd.Sound,ls.LoadingScreens
	
	;Delete all stuff
	CurrMusicVolume = 0.0
	StopStream_Strict(MusicCHN)
	FreeImage MenuWhite
	FreeImage MenuBlack
	FreeImage QuickLoadIcon
	DeleteMenuImages()
	For snd.Sound = Each Sound
		FreeSound snd\internalHandle
		snd\internalHandle = 0
	Next
	For i = 0 To 4
		FreeImage NavImages[i]
	Next
	FreeImage BlinkMeterIMG
	FreeImage PauseMenuIMG%
	FreeImage SprintIcon%
	FreeImage BlinkIcon%
	FreeImage CrouchIcon%
	FreeImage HandIcon%
	FreeImage HandIcon2%
	FreeImage StaminaMeterIMG%
	FreeImage KeypadHUD
	FreeImage Panel294
;	For i = 0 To MAXACHIEVEMENTS-1
;		FreeImage Achievements[i]\IMG
;	Next
;	FreeImage AchvLocked
	FreeImage LoadingBack
	For ls = Each LoadingScreens
		For i = 0 To (ls\imgamount-1)
			FreeImage ls\img[i]
		Next
		Delete ls
	Next
	Local name$ = m3d\RoomName
	Null3DMenu()
	InitFonts()
	
	;Reload all stuff
	MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
	MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")
	QuickLoadIcon% = LoadImage_Strict("GFX\menu\QuickLoading.png")
	ResizeImage(QuickLoadIcon, ImageWidth(QuickLoadIcon) * MenuScale, ImageHeight(QuickLoadIcon) * MenuScale)
	LoadMenuImages()
	If (Not opt\EnableSFXRelease)
		For snd.Sound = Each Sound
			snd\internalHandle = LoadSound(snd\name)
		Next
	EndIf
	For i = 0 To 3
		NavImages[i] = LoadImage_Strict("GFX\navigator\roomborder"+i+".png")
		MaskImage NavImages[i],255,0,255
	Next
	NavImages[4] = LoadImage_Strict("GFX\navigator\batterymeter.png")
	BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")
	PauseMenuIMG% = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	MaskImage PauseMenuIMG, 255,255,0
	ScaleImage PauseMenuIMG,MenuScale,MenuScale
	SprintIcon% = LoadImage_Strict("GFX\sprinticon.png")
	BlinkIcon% = LoadImage_Strict("GFX\blinkicon.png")
	CrouchIcon% = LoadImage_Strict("GFX\sneakicon.png")
	HandIcon% = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2% = LoadImage_Strict("GFX\handsymbol2.png")
	StaminaMeterIMG% = LoadImage_Strict("GFX\staminameter.jpg")
	KeypadHUD =  LoadImage_Strict("GFX\keypadhud.jpg")
	MaskImage(KeypadHUD, 255,0,255)
	Panel294 = LoadImage_Strict("GFX\294panel.jpg")
	MaskImage(Panel294, 255,0,255)
;	For i = 0 To MAXACHIEVEMENTS-1
;		Local loc2% = GetINISectionLocation(AchvIni, "s"+Str(i))
;		Local image$ = GetINIString2(AchvIni, loc2, "image") 
;		Achievements[i]\IMG = LoadImage_Strict("GFX\menu\achievements\"+image+".jpg")
;		Achievements[i]\IMG = ResizeImage2(Achievements[i]\IMG,ImageWidth(Achievements[i]\IMG)*opt\GraphicHeight/768.0,ImageHeight(Achievements[i]\IMG)*opt\GraphicHeight/768.0)
;	Next
;	AchvLocked = LoadImage_Strict("GFX\menu\achievements\achvlocked.jpg")
;	AchvLocked = ResizeImage2(AchvLocked,ImageWidth(AchvLocked)*opt\GraphicHeight/768.0,ImageHeight(AchvLocked)*opt\GraphicHeight/768.0)
	LoadingBack% = LoadImage_Strict("Loadingscreens\loadingback.jpg")
	InitLoadingScreens("Loadingscreens\loadingscreens.ini")
	Load3DMenu(name)
	MusicCHN% = StreamSound_Strict("SFX\Music\"+Music[NowPlaying]+".ogg",CurrMusicVolume,Mode)
	
End Function







;~IDEal Editor Parameters:
;~F#3#9#1F7#21B
;~C#Blitz3D