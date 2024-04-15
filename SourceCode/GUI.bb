
Function UpdateGUI()
	CatchErrors("Uncaught (UpdateGUI)")
	
	Local temp%, x%, y%, z%, i%, yawvalue#, pitchvalue#, pvt%
	Local x2#,y2#,z2#
	Local n%, xtemp, ytemp, strtemp$, projY#, scale#
	
	Local e.Events, it.Items, np.NPCs, ne.NewElevator
	
	If d_I\ClosestButton <> 0 And d_I\SelectedDoor = Null And InvOpen = False And MenuOpen = False And OtherOpen = Null And (Not PlayerInNewElevator) Then
		temp% = CreatePivot()
		PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
		PointEntity temp, d_I\ClosestButton
		yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		temp = FreeEntity_Strict(temp)
		
		DrawImage(HandIcon, opt\GraphicWidth / 2 + Sin(yawvalue) * (opt\GraphicWidth / 3) - 32, opt\GraphicHeight / 2 - Sin(pitchvalue) * (opt\GraphicHeight / 3) - 32)
		
		If keyhituse Then
			If d_I\ClosestDoor <> Null Then 
				If d_I\ClosestDoor\Code <> "" Lor (d_I\ClosestDoor\dir=DOOR_ELEVATOR_3FLOOR And d_I\ClosestButton=d_I\ClosestDoor\buttons[0]) Then
					temp = True
					For ne = Each NewElevator
						If ne\door = d_I\ClosestDoor Then
							For np = Each NPCs
								If np\NPCtype = NPCtype173 And np\Idle = SCP173_BOXED Then
									If Abs(EntityX(np\Collider) - EntityX(ne\obj, True)) >= 280.0 * RoomScale + (0.015 * FPSfactor) Lor Abs(EntityZ(np\Collider) - EntityZ(ne\obj, True)) >= 280.0 * RoomScale + (0.015 * FPSfactor) Then
										temp = 2
									EndIf
								EndIf
								If np\NPCtype = NPCtypeMTF And np\HP > 0 Then
									If Abs(EntityX(np\Collider) - EntityX(ne\obj, True)) >= 280.0 * RoomScale + (0.015 * FPSfactor) Lor Abs(EntityZ(np\Collider) - EntityZ(ne\obj, True)) >= 280.0 * RoomScale + (0.015 * FPSfactor) Then
										temp = False
										Exit
									EndIf
								EndIf
							Next
							Exit
						EndIf
					Next
					
					If temp = 1 Then
						d_I\SelectedDoor = d_I\ClosestDoor
						co\KeyPad_CurrButton = 0
						co\WaitTimer = 0
					ElseIf temp = 2 Then
						Msg = GetLocalString("Doors", "elevator_wait_173")
						MsgTimer = 70 * 5
					Else
						Msg = GetLocalString("Doors", "elevator_wait")
						MsgTimer = 70 * 5
					EndIf
				ElseIf d_I\ClosestDoor\dir=DOOR_ELEVATOR_3FLOOR And d_I\ClosestButton=d_I\ClosestDoor\buttons[1] Then
					PlaySound2(ButtonSFX, Camera, d_I\ClosestButton)
					For ne = Each NewElevator
						If ne\door = d_I\ClosestDoor
							If ne\state = 0.0
								If EntityY(ne\door\frameobj)>ne\floory[1]*RoomScale+1
									StartNewElevator(d_I\ClosestDoor,3)
									DebugLog "Option 3"
								ElseIf EntityY(ne\door\frameobj)<ne\floory[2]*RoomScale-1 And EntityY(ne\door\frameobj)>ne\floory[0]*RoomScale
									StartNewElevator(d_I\ClosestDoor,2)
									DebugLog "Option 2"
								Else
									StartNewElevator(d_I\ClosestDoor,1)
									DebugLog "Option 1"
								EndIf
							Else
								If (Msg<>GetLocalString("Doors", "elevator_called"))
									If (Msg=GetLocalString("Doors", "elevator_called2")) Lor (MsgTimer<70*3)	
										Select Rand(10)
											Case 1
												Msg = GetLocalString("Doors", "elevator_rand_1")
											Case 2
												Msg = GetLocalString("Doors", "elevator_rand_2")
											Case 3
												Msg = GetLocalString("Doors", "elevator_rand_3")
											Default
												Msg = GetLocalString("Doors", "elevator_called2")
										End Select
										MsgTimer = 70 * 7
									EndIf
								Else
									Msg = GetLocalString("Doors", "elevator_called2")
									MsgTimer = 70 * 7
								EndIf
							EndIf
						EndIf
					Next
				ElseIf Playable Then
					PlaySound2(ButtonSFX, Camera, d_I\ClosestButton)
					UseDoor(d_I\ClosestDoor,True)
				EndIf
			EndIf
		EndIf
	EndIf
	
;	If Using294 Then Use294()
	
	If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (ConsoleOpen=False) And (Using294=False) And (SelectedScreen=Null) And EndingTimer=>0 And KillTimer >= 0
		If PlayerRoom\RoomTemplate\Name$ <> "gate_a_intro"
;			UseChatSounds()
;			UpdateRadio()
		EndIf
		
		UpdateCommunicationAndSocialWheel()
	EndIf
	
;	If NTF_GameModeFlag=1
;		Tasks()
;	EndIf
	
	UpdateSplashTexts()
	
;	If HUDenabled Then 
;		DrawGunsInHUD()
;	EndIf
	
	If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (d_I\SelectedDoor=Null) And (ConsoleOpen=False) And (Using294=False) And (SelectedScreen=Null) And EndingTimer=>0 And KillTimer >= 0
		ToggleGuns()
	EndIf
	mpl\SlotsDisplayTimer = Max(mpl\SlotsDisplayTimer-FPSfactor,0.0)
	
	If SelectedScreen <> Null Then
		If MouseUp1 Lor MouseHit2 Then
			FreeImage SelectedScreen\img : SelectedScreen\img = 0
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	
	Local g.Guns
	
	Local shouldDrawHUD%=True
	If d_I\SelectedDoor <> Null Then
		SelectedItem = Null
		
		If shouldDrawHUD Then
			HideEntity g_I\GunPivot
			If d_I\SelectedDoor\dir<>5 Then
				pvt = CreatePivot()
				PositionEntity pvt, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True),EntityZ(d_I\ClosestButton,True)
				RotateEntity pvt, 0, EntityYaw(d_I\ClosestButton,True)-180,0
				MoveEntity pvt, 0,0,0.22
				PositionEntity Camera, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
				PointEntity Camera, d_I\ClosestButton
				pvt = FreeEntity_Strict(pvt)
				
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)+MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				projY# = ProjectedY()
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)-MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				scale# = (ProjectedY()-projY)/462.0
				
				x = opt\GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
				y = opt\GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2
				
				If KeypadMSG <> "" Then 
					KeypadTimer = KeypadTimer-FPSfactor2
					
					If KeypadTimer =<0 Then
						KeypadMSG = ""
						d_I\SelectedDoor = Null
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
					EndIf
				EndIf
				
				x = x+44*scale
				y = y+249*scale
				
				For n = 0 To 3
					For i = 0 To 2
						xtemp = x+Int(58.5*scale*n)
						ytemp = y+(67*scale)*i
						
						temp = False
						If MouseOn(xtemp,ytemp, 54*scale,65*scale) And KeypadMSG = "" Then
							If MouseUp1 Then 
								PlaySound_Strict ButtonSFX
								
								Select (n+1)+(i*4)
									Case 1,2,3
										KeypadInput=KeypadInput + ((n+1)+(i*4))
									Case 4
										KeypadInput=KeypadInput + "0"
									Case 5,6,7
										KeypadInput=KeypadInput + ((n+1)+(i*4)-1)
									Case 8 ;enter
										If KeypadInput = d_I\SelectedDoor\Code Then
											PlaySound_Strict ScannerSFX1
											d_I\SelectedDoor\locked = 0
											UseDoor(d_I\SelectedDoor,True)
											d_I\SelectedDoor = Null
											MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
										Else
											PlaySound_Strict ScannerSFX2
											KeypadMSG = Upper(GetLocalString("Doors", "keypad_denied"))
											KeypadTimer = 210
											KeypadInput = ""	
										EndIf
									Case 9,10,11
										KeypadInput=KeypadInput + ((n+1)+(i*4)-2)
									Case 12
										KeypadInput = ""
								End Select 
								
								If Len(KeypadInput)> 4 Then KeypadInput = Left(KeypadInput,4)
							EndIf
							
						Else
							temp = False
						EndIf
						
					Next
				Next
			Else
				pvt = CreatePivot()
				PositionEntity pvt, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True),EntityZ(d_I\ClosestButton,True)
				RotateEntity pvt, 0, EntityYaw(d_I\ClosestButton,True)-180,0
				MoveEntity pvt, 0,0,0.3
				PositionEntity Camera, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
				PointEntity Camera, d_I\ClosestButton
				pvt = FreeEntity_Strict(pvt)
				
				CameraZoom Camera,1.0
				
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)+MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				projY# = ProjectedY()
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)-MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				scale# = (ProjectedY()-projY)/462.0
				
				MoveEntity Camera,0.001,0.1672,0
				
				x = opt\GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
				y = opt\GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2
				
				If co\Enabled
					If co\WaitTimer = 0.0
						If GetDPadButtonPress()=0
							co\KeyPad_CurrButton = co\KeyPad_CurrButton - 1
							PlaySound_Strict co\SelectSFX
							co\WaitTimer = FPSfactor2
							If co\KeyPad_CurrButton < 0
								co\KeyPad_CurrButton = 2
							EndIf
						ElseIf GetDPadButtonPress()=180
							co\KeyPad_CurrButton = co\KeyPad_CurrButton + 1
							PlaySound_Strict co\SelectSFX
							co\WaitTimer = FPSfactor2
							If co\KeyPad_CurrButton > 2
								co\KeyPad_CurrButton = 0
							EndIf
						EndIf
						
						If GetLeftAnalogStickPitch(True) > 0.0
							co\KeyPad_CurrButton = co\KeyPad_CurrButton - 1
							PlaySound_Strict co\SelectSFX
							co\WaitTimer = FPSfactor2
							If co\KeyPad_CurrButton < 0
								co\KeyPad_CurrButton = 2
							EndIf
						ElseIf GetLeftAnalogStickPitch(True) < 0.0
							co\KeyPad_CurrButton = co\KeyPad_CurrButton + 1
							PlaySound_Strict co\SelectSFX
							co\WaitTimer = FPSfactor2
							If co\KeyPad_CurrButton > 2
								co\KeyPad_CurrButton = 0
							EndIf
						EndIf
					Else
						If co\WaitTimer > 0.0 And co\WaitTimer < 15.0
							co\WaitTimer = co\WaitTimer + FPSfactor2
						ElseIf co\WaitTimer >= 15.0
							co\WaitTimer = 0.0
						EndIf
					EndIf
				EndIf
				
				x=x+120*scale
				y=y+259*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						If MouseHit1
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,3)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				Else
					If co\KeyPad_CurrButton = 0
						If JoyHit(CKM_Press)
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,3)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				EndIf
				
				y=y+131*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						If MouseHit1
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,2)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				Else
					If co\KeyPad_CurrButton = 1
						If JoyHit(CKM_Press)
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,2)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				EndIf
				
				y=y+130*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						If MouseHit1
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,1)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				Else
					If co\KeyPad_CurrButton = 2
						If JoyHit(CKM_Press)
							PlaySound_Strict ButtonSFX
							StartNewElevator(d_I\SelectedDoor,1)
							d_I\SelectedDoor = Null
							ResetInput()
;							For g.Guns = Each Guns
;								If g\name$ = "p90"
;									g\MouseDownTimer# = 15.0
;								EndIf
;							Next
						EndIf
					EndIf
				EndIf
			EndIf
			
			If (Not co\Enabled)
				If MouseHit2 Then
					d_I\SelectedDoor = Null
					ResetInput()
				EndIf
			Else
				If JoyHit(CKM_Back)
					PlaySound_Strict ButtonSFX
					d_I\SelectedDoor = Null
					ResetInput()
				EndIf
			EndIf
		Else
			d_I\SelectedDoor = Null
		EndIf
	Else
		KeypadInput = ""
		KeypadTimer = 0
		KeypadMSG = ""
	EndIf
	
	If (InteractHit(1,CK_Pause) Lor ((Not InFocus()) And (Not MenuOpen)) Lor (Steam_GetOverlayUpdated() = 1 And (Not (MenuOpen Lor InvOpen)))) And EndingTimer = 0 Then
		If MenuOpen And (Not InvOpen) Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			DeleteMenuGadgets()
			ResetInput()
		Else
			PauseSounds()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMSG = 0
		
		d_I\SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
	EndIf
	
	Local spacing%
	Local PrevOtherOpen.Items
	
	Local OtherSize%,OtherAmount%
	
	Local isEmpty%
	
	Local isMouseOn%
	
	Local closedInv%
	
	If OtherOpen<>Null Then
		;[Block]
		If (PlayerRoom\RoomTemplate\Name = "gate_a_topside") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.01, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_b_topside") Then
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.01, 60)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_a_intro") Then
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.005, 100)
		EndIf
		
		PrevOtherOpen = OtherOpen
		OtherSize=OtherOpen\invSlots;Int(OtherOpen\state2)
		
		For i%=0 To OtherSize-1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount+1
			EndIf
		Next
		
		;If OtherAmount > 0 Then
		;	OtherOpen\state = 1.0
		;Else
		;	OtherOpen\state = 0.0
		;EndIf
		InvOpen = False
		d_I\SelectedDoor = Null
		Local tempX% = 0
		
		width = 70
		height = 70
		spacing% = 35
		
		x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = opt\GraphicHeight / 2 - (height * OtherSize /5 + height * (OtherSize / 5 - 1)) / 2
		
		ItemAmount = 0
		For n% = 0 To OtherSize - 1
			isMouseOn% = False
			If MouseOn(x, y, width, height) Then 
				isMouseOn = True
				MouseSlot = n
			EndIf
			
			If OtherOpen = Null Then Exit
			
			DebugLog "otheropen: "+(OtherOpen<>Null)
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If isMouseOn Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							MouseHit1 = False
							
							If DoubleClick Then
								If OtherOpen\SecondInv[n]\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[OtherOpen\SecondInv[n]\itemtemplate\sound])
								OtherOpen = Null
								closedInv=True
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
					Else
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
				
			EndIf
			
			x=x+width + spacing
			tempX=tempX + 1
			If tempX = 5 Then 
				tempX=0
				y = y + height*2 
				x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				
			Else
				If MouseSlot = 66 Then
					If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])
					
					ShowEntity(SelectedItem\collider)
					PositionEntity(SelectedItem\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\collider, EntityPitch(Camera), EntityYaw(Camera), 0)
					MoveEntity(SelectedItem\collider, 0, -0.1, 0.1)
					RotateEntity(SelectedItem\collider, 0, Rand(360), 0)
					ResetEntity (SelectedItem\collider)
					
					SelectedItem\DropSpeed = 0.0
					
					SelectedItem\Picked = False
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					
					isEmpty=True
					
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z]<>Null Then isEmpty=False : Exit
					Next
					
					If isEmpty Then
						Select OtherOpen\itemtemplate\tempname
							Case "clipboard"
								OtherOpen\invimg = OtherOpen\itemtemplate\invimg2
								SetAnimTime OtherOpen\model,17.0
						End Select
					EndIf
					
					SelectedItem = Null
					OtherOpen = Null
					closedInv=True
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z% = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then PrevOtherOpen\SecondInv[z] = Null
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Default
								Msg = GetLocalString("Items", "cannot_combine")
								MsgTimer = 70 * 5
						End Select					
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If (closedInv) And (Not InvOpen) Then
			ResumeSounds()
			ResetInput()
			OtherOpen=Null
		EndIf
		;[End Block]
	ElseIf InvOpen Then
		;Beginning Task for keycard
		If TaskExists(TASK_OPENINV) Then
			EndTask(TASK_OPENINV)
		EndIf
		
		Local keyhits[MaxGunSlots]
		For i = 0 To MaxGunSlots-1
			keyhits[i] = KeyHit(i + 2)
		Next
		
		If (PlayerRoom\RoomTemplate\Name = "gate_a_topside") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.01, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_b_topside") Then
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.01, 60)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_a_intro") Then
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.005, 100)
		EndIf
		
		d_I\SelectedDoor = Null
		
		width% = 70
		height% = 70
		spacing% = 35
		
		x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = opt\GraphicHeight / 2 - (height * MaxItemAmount /5 + height * (MaxItemAmount / 5 - 1)) / 2
		
		ItemAmount = 0
		For  n% = 0 To MaxItemAmount - 1
			isMouseOn% = False
			If MouseOn(x, y, width, height) Then 
				isMouseOn = True
				MouseSlot = n
			EndIf
			
			If Inventory[n] <> Null And SelectedItem <> Inventory[n] Then
				If isMouseOn Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory[n]
							MouseHit1 = False
							
							If DoubleClick Then
								If Inventory[n]\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[Inventory[n]\itemtemplate\sound])
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
						
						x2# = x
						y2# = y+height
						width2# = width/3
						height2# = height/3
						
						If Inventory[n]\itemtemplate\isGun% = True
							For i = 0 To MaxGunSlots-1
								If keyhits[i] Then
									If g_I\Weapon_CurrSlot <> (i + 1) Then
										g_I\Weapon_InSlot[i] = Inventory[n]\itemtemplate\tempname
										PlaySound_Strict g_I\UI_Select_SFX
									Else
										PlaySound_Strict g_I\UI_Deny_SFX
									EndIf
								EndIf
							Next
						EndIf
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To MaxItemAmount - 1
						If Inventory[z] = SelectedItem Then Inventory[z] = Null : Exit
					Next
					Inventory[n] = SelectedItem
					SelectedItem = Null
				End If
				
			EndIf					
			
			x=x+width + spacing
			If n = 4 Then 
				y = y + height*2 
				x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
			
			width2# = 70
			height2# = 70
			spacing2# = 35
			
			x2# = opt\GraphicWidth / 2
			y2# = opt\GraphicHeight / 2 + height2*2.6
			
			x2 = opt\GraphicWidth / 2 - (width2 * MaxItemAmount /2 + spacing2 * (MaxItemAmount / 2 - 1)) / 2
			y2 = opt\GraphicHeight / 2 - height2 + height2*4
			
			x2=x2+width2 + spacing2
			
			x2=x2+width2 + spacing2
			
			x2=x2+width2 + spacing2
			
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				
			Else
				If MouseSlot = 66 Then
					Local isOverQuickSelection% = 0
					width2# = 70
					height2# = 70
					spacing2# = 35
					x2 = opt\GraphicWidth / 2 - (width2 * MaxItemAmount /2 + spacing2 * (MaxItemAmount / 2 - 1)) / 2
					y2 = opt\GraphicHeight / 2 - height2 + height2*4
					x2=x2+width2 + spacing2
					For i = 0 To MaxGunSlots-1
						If MouseOn(x2+((width2+spacing2)*i), y2, width2, height2) Then
							isOverQuickSelection = (i + 1)
							Exit
						EndIf
					Next
					
					If (Not isOverQuickSelection) Then
						DropItem(SelectedItem)
						
						InvOpen = False
						
						MoveMouse viewport_center_x, viewport_center_y
					Else
						If SelectedItem\itemtemplate\isGun Then
							If isOverQuickSelection = g_I\Weapon_CurrSlot Then
								PlaySound_Strict g_I\UI_Deny_SFX
							Else
								g_I\Weapon_InSlot[isOverQuickSelection - 1] = SelectedItem\itemtemplate\tempname
								PlaySound_Strict g_I\UI_Select_SFX
							EndIf
						Else
							Msg = GetLocalString("Items", "cannot_nonweapon")
							MsgTimer = 70 * 5
						EndIf
					EndIf
					SelectedItem = Null	
				Else
					If Inventory[MouseSlot] = Null Then
						For z% = 0 To MaxItemAmount - 1
							If Inventory[z] = SelectedItem Then Inventory[z] = Null : Exit
						Next
						Inventory[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf Inventory[MouseSlot] <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Case "paper","key1","key2","key3","key4","key5","key6","misc","oldpaper","badge","ticket" ;BoH stuff
								;[Block]
								If Inventory[MouseSlot]\itemtemplate\tempname = "clipboard" Then
									;Add an item to clipboard
									Local added.Items = Null
									If SelectedItem\itemtemplate\tempname<>"misc" Lor (SelectedItem\itemtemplate\name="Playing Card" Lor SelectedItem\itemtemplate\name="Mastercard") Then
										For c% = 0 To Inventory[MouseSlot]\invSlots-1
											If (Inventory[MouseSlot]\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory[MouseSlot]\SecondInv[c] = SelectedItem
													Inventory[MouseSlot]\state = 1.0
													SetAnimTime Inventory[MouseSlot]\model,0.0
													Inventory[MouseSlot]\invimg = Inventory[MouseSlot]\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory[ri] = SelectedItem Then
															Inventory[ri] = Null
															PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])
															Exit
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = GetLocalString("Items", "clipboard_notstrong")
										Else
											If added\itemtemplate\tempname = "paper" Lor added\itemtemplate\tempname = "oldpaper" Then
												Msg = GetLocalString("Items", "clipboard_added_1")
											ElseIf added\itemtemplate\tempname = "badge"
												Msg = GetLocalStringR("Items", "clipboard_added_2",added\itemtemplate\name)
											Else
												Msg = GetLocalStringR("Items", "clipboard_added_3",added\itemtemplate\name)
											EndIf
											
										EndIf
										MsgTimer = 70 * 5
									Else
										Msg = GetLocalString("Items", "cannot_combine")
										MsgTimer = 70 * 5
									EndIf
								EndIf
								SelectedItem = Null
								
								;[End Block]
							Case "battery", "bat"
								;[Block]
								Select Inventory[MouseSlot]\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])	
										RemoveItem (SelectedItem)
										SelectedItem = Null
										Inventory[MouseSlot]\state = 100.0
										Msg = GetLocalString("Items", "battery_nav")
									Case "S-NAV Navigator Ultimate"
										Msg = GetLocalString("Items", "battery_nav_noplace")
									Case "Radio Transceiver"
										Select Inventory[MouseSlot]\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
												Msg = GetLocalString("Items", "battery_radio_noplace")
											Case "18vradio"
												Msg = GetLocalString("Items", "battery_radio_18v")
											Case "radio"
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory[MouseSlot]\state = 100.0
												Msg = GetLocalString("Items", "battery_radio")
										End Select
									Case "Night Vision Goggles"
										Local nvname$ = Inventory[MouseSlot]\itemtemplate\tempname
										If nvname$="nvgoggles" Lor nvname$="supernv" Then
											If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])	
											RemoveItem (SelectedItem)
											SelectedItem = Null
											Inventory[MouseSlot]\state = 1000.0
											Msg = GetLocalString("Items", "battery_nvg")
										Else
											Msg = GetLocalString("Items", "battery_nvg_noplace")
										EndIf
									Default
										Msg = GetLocalString("Items", "cannot_combine")
								End Select
								MsgTimer = 70 * 5
								;[End Block]
							Case "18vbat"
								;[Block]
								Select Inventory[MouseSlot]\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										Msg = GetLocalString("Items", "battery_nav_18v")
									Case "S-NAV Navigator Ultimate"
										Msg = GetLocalString("Items", "battery_nav_noplace")
									Case "Radio Transceiver"
										Select Inventory[MouseSlot]\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
												Msg = GetLocalString("Items", "battery_radio_noplace")
											Case "18vradio"
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory[MouseSlot]\state = 100.0
												Msg = GetLocalString("Items", "battery_radio")
										End Select 
									Default
										Msg = GetLocalString("Items", "cannot_combine")	
								End Select
								MsgTimer = 70 * 5
								;[End Block]
							Default
								;[Block]
								Msg = GetLocalString("Items", "cannot_combine")
								MsgTimer = 70 * 5
								;[End Block]
						End Select
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If InvOpen = False Then 
			ResumeSounds()
			ResetInput()
		EndIf
	Else ;invopen = False
		
		;WIP
		If SelectedItem <> Null Then
			Select SelectedItem\itemtemplate\tempname
				Case "nvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat=0 Then
						If WearingNightVision = 1 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision)
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "supernv"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat=0 Then
						If WearingNightVision = 2 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 2
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "finenvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingNightVision = 3 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 3
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "ring"
					;[Block]
					If Wearing714=2 Then
						Msg = GetLocalString("Items", "scp714_off")
						Wearing714 = False
					Else
						Msg = GetLocalString("Items", "scp714_on")
						Wearing714 = 2
						TakeOffStuff(1+2+8+32+64)
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null	
					
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict LoadTempSound("SFX\SCP\513\Bell1.ogg")
					
					If Curr5131 = Null
						Curr5131 = CreateNPC(NPCtype5131, 0,0,0)
					EndIf	
					SelectedItem = Null
					;[End Block]
				Case "scp500"
					;[Block]
					If CanUseItem(False, False, True)
						If psp\Health < 100 And Infect > 0 Then
							Msg = GetLocalString("Items", "scp500_1")
						ElseIf Infect > 0 Then
							Msg = GetLocalString("Items", "scp500_2")
						Else
							Msg = GetLocalString("Items", "scp500_3")
						EndIf
						MsgTimer = 70*7
						
						DeathTimer = 0
						HealSPPlayer(100)
						Infect = 0
						Stamina = 100
						For i = 0 To 5
							SCP1025state[i]=0
						Next
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If CanUseItem(False, False, True)
						Select Rand(5)
							Case 1
								;Injuries = 3.5
								DamageSPPlayer(80, True)
								Msg = GetLocalString("Items", "strangebottle_1")
								MsgTimer = 70*7
							Case 2
								;Injuries = 0
								;Bloodloss = 0
								HealSPPlayer(100)
								Msg = GetLocalString("Items", "strangebottle_2")
								MsgTimer = 70*7
							Case 3
								;Injuries = Max(0, Injuries - Rnd(0.5,3.5))
								;Bloodloss = Max(0, Bloodloss - Rnd(10,100))
								HealSPPlayer(50)
								Msg = GetLocalString("Items", "strangebottle_3")
								MsgTimer = 70*7
							Case 4
								BlurTimer = 10000
								;Bloodloss = 0
								HealSPPlayer(25)
								Msg = GetLocalString("Items", "bluefirstaid_3")
								MsgTimer = 70*7
							Case 5
								BlinkTimer = -10
								Local roomname$ = PlayerRoom\RoomTemplate\Name
								If roomname = "dimension1499" Lor roomname = "gatea" Lor (roomname="exit1" And EntityY(Collider)>1040.0*RoomScale) Then
									;Injuries = 2.5
									DamageSPPlayer(70, True)
									Msg = GetLocalString("Items", "strangebottle_1")
									MsgTimer = 70*7
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "pocketdimension" Then
											PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))		
											ResetEntity Collider									
											UpdateDoors()
											UpdateRooms()
											PlaySound_Strict(Use914SFX)
											DropSpeed = 0
											Curr106\State = -2500
											Exit
										EndIf
									Next
									Msg = GetLocalString("Items", "strangebottle_4")
									MsgTimer = 70*8
								EndIf
						End Select
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If psp\Health = 100 Then
						Msg = GetLocalString("Items", "firstaid_noneed")
						MsgTimer = 70*5
						SelectedItem = Null
					Else
						If CanUseItem(False, True, True)
							CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							Crouch = True
							
							SelectedItem\state = Min(SelectedItem\state+(FPSfactor/5.0),100)			
							
							If SelectedItem\state = 100 Then
								If SelectedItem\itemtemplate\tempname = "finefirstaid" Then
									Bloodloss = 0
									;Injuries = Max(0, Injuries - 2.0)
									HealSPPlayer(45)
									If psp\Health = 100 Then
										Msg = GetLocalString("Items", "finefirstaid_1")
									ElseIf psp\Health > 70 Then
										Msg = GetLocalString("Items", "finefirstaid_2")
									Else
										Msg = GetLocalString("Items", "finefirstaid_3")
									EndIf
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								Else
;									Bloodloss = Max(0, Bloodloss - Rand(10,20))
;									If Injuries => 2.5 Then
;										Msg = "The wounds were way too severe to staunch the bleeding completely."
;										Injuries = Max(2.5, Injuries-Rnd(0.3,0.7))
;									ElseIf Injuries > 1.0
;										Injuries = Max(0.5, Injuries-Rnd(0.5,1.0))
;										If Injuries > 1.0 Then
;											Msg = "You bandaged the wounds but were unable to staunch the bleeding completely."
;										Else
;											Msg = "You managed to stop the bleeding."
;										EndIf
;									Else
;										If Injuries > 0.5 Then
;											Injuries = 0.5
;											Msg = "You took a painkiller, easing the pain slightly."
;										Else
;											Injuries = 0.5
;											Msg = "You took a painkiller, but it still hurts to walk."
;										EndIf
;									EndIf
									HealSPPlayer(30)
									Msg = GetLocalString("Items", "firstaid_1")
									
									If SelectedItem\itemtemplate\tempname = "firstaid2" Then 
										Select Rand(6)
											Case 1
												SuperMan = True
												Msg = GetLocalString("Items", "bluefirstaid_1")
											Case 2
												InvertMouse = (Not InvertMouse)
												Msg = GetLocalString("Items", "bluefirstaid_2")
											Case 3
												BlurTimer = 5000
												Msg = GetLocalString("Items", "bluefirstaid_3")
											Case 4
												BlinkEffect = 0.6
												BlinkEffectTimer = Rand(20,30)
											Case 5
												;Bloodloss = 0
												;Injuries = 0
												HealSPPlayer(100)
												Msg = GetLocalString("Items", "bluefirstaid_4")
											Case 6
												Msg = GetLocalString("Items", "bluefirstaid_5")
												;Injuries = 3.5
												DamageSPPlayer(70, True)
										End Select
									EndIf
									
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								EndIf							
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops"
					;[Block]
					If CanUseItem(False,False,False)
						If (Not (Wearing714=1)) Then
							BlinkEffect = 0.6
							BlinkEffectTimer = Rand(20,30)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]s
					If CanUseItem(False,False,False)
						If (Not (Wearing714=1)) Then 
							BlinkEffect = 0.4
							BlinkEffectTimer = Rand(30,40)
							Bloodloss = Max(Bloodloss-1.0, 0)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem(False,False,False)
						If (Not (Wearing714 = 1)) Then
							BlinkEffect = 0.0
							BlinkEffectTimer = 60
							EyeStuck = 10000
						EndIf
						BlurTimer = 1000
						RemoveItem(SelectedItem)
					EndIf					
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						Select SelectedItem\itemtemplate\name
							Case "Movie Ticket"
								If (SelectedItem\state = 0) Then
									Msg = GetLocalString("Items", "ticket")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
									SelectedItem\state = 1
								EndIf
						End Select
					EndIf
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025) 
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\state = Rand(0,5)
					EndIf
					
					If (Not Wearing714) Then SCP1025state[SelectedItem\state]=Max(1,SCP1025state[SelectedItem\state])
					;[End Block]
				Case "cup"
					;[Block]
					If CanUseItem(False,False,True)
						SelectedItem\name = Trim(Lower(SelectedItem\name))
						If Left(SelectedItem\name, Min(6,Len(SelectedItem\name))) = "cup of" Then
							SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-7)
						ElseIf Left(SelectedItem\name, Min(8,Len(SelectedItem\name))) = "a cup of" 
							SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-9)
						EndIf
						
						;the state of refined items is more than 1.0 (fine setting increases it by 1, very fine doubles it)
						x2 = (SelectedItem\state+1.0)
						
						Local iniStr$ = Data294
						
						Local loc% = GetINISectionLocation(iniStr, SelectedItem\name)
						
						;Stop
						
						strtemp = GetINIString2(iniStr, loc, "message")
						If strtemp <> "" Then Msg = strtemp : MsgTimer = 70*6
						
						If GetINIInt2(iniStr, loc, "lethal") Lor GetINIInt2(iniStr, loc, "deathtimer") Then 
							DeathMSG = GetINIString2(iniStr, loc, "deathmessage")
							If GetINIInt2(iniStr, loc, "lethal") Then Kill()
						EndIf
						BlurTimer = GetINIInt2(iniStr, loc, "blur")*70;*temp
						If VomitTimer = 0 Then VomitTimer = GetINIInt2(iniStr, loc, "vomit")
						CameraShakeTimer = GetINIString2(iniStr, loc, "camerashake")
						;Injuries = Max(Injuries + GetINIInt2(iniStr, loc, "damage"),0);*temp
						;Bloodloss = Max(Bloodloss + GetINIInt2(iniStr, loc, "blood loss"),0);*temp
						DamageSPPlayer(GetINIInt2(iniStr, loc, "damage") * 25.0, True)
						strtemp =  GetINIString2(iniStr, loc, "sound")
						If strtemp<>"" Then
							PlaySound_Strict LoadTempSound(strtemp)
						EndIf
						If GetINIInt2(iniStr, loc, "stomachache") Then SCP1025state[3]=1
						
						DeathTimer=GetINIInt2(iniStr, loc, "deathtimer")*70
						
						BlinkEffect = Float(GetINIString2(iniStr, loc, "blink effect", 1.0))*x2
						BlinkEffectTimer = Float(GetINIString2(iniStr, loc, "blink effect timer", 1.0))*x2
						
						StaminaEffect = Float(GetINIString2(iniStr, loc, "stamina effect", 1.0))*x2
						StaminaEffectTimer = Float(GetINIString2(iniStr, loc, "stamina effect timer", 1.0))*x2
						
						strtemp = GetINIString2(iniStr, loc, "refusemessage")
						If strtemp <> "" Then
							Msg = strtemp 
							MsgTimer = 70*6		
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0,0,0)
							it\Picked = True
							For i = 0 To MaxItemAmount-1
								If Inventory[i]=SelectedItem Then Inventory[i] = it : Exit
							Next					
							EntityType (it\collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)						
						EndIf
						
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					If CanUseItem(False,True,True)
						HealTimer = 30
						StaminaEffect = 0.5
						StaminaEffectTimer = 20
						
						Msg = "You injected yourself with the syringe and feel a slight adrenaline rush."
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "finesyringe"
					;[Block]
					If CanUseItem(False,True,True)
						HealTimer = Rnd(20, 40)
						StaminaEffect = Rnd(0.5, 0.8)
						StaminaEffectTimer = Rnd(20, 30)
						
						Msg = "You injected yourself with the syringe and feel an adrenaline rush."
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					If CanUseItem(False,True,True)
						Select Rand(3)
							Case 1
								HealTimer = Rnd(40, 60)
								StaminaEffect = 0.1
								StaminaEffectTimer = 30
								Msg = "You injected yourself with the syringe and feel a huge adrenaline rush."
							Case 2
								SuperMan = True
								Msg = "You injected yourself with the syringe and feel a humongous adrenaline rush."
							Case 3
								VomitTimer = 30
								Msg = "You injected yourself with the syringe and feel a pain in your stomach."
						End Select
						
						MsgTimer = 70 * 8
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "radio","18vradio","fineradio","veryfineradio"
					;[Block]
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.004)
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					;RadioState[5] = has the "use the number keys" -message been shown yet (true/false)
					;RadioState[6] = a timer for the "code channel"
					;RadioState[7] = another timer for the "code channel"
					
					If RadioState[5] = 0 Then 
						Msg = "Use the numbered keys 1 through 5 to cycle between various channels."
						MsgTimer = 70 * 5
						RadioState[5] = 1
						RadioState[0] = -1
					EndIf
					
					strtemp$ = ""
					
					x = opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img) ;+ 120
					y = opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img) ;- 30
					
					DrawImage(SelectedItem\itemtemplate\img, x, y)
					
					If SelectedItem\state > 0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor CoffinDistance < 4.0 Then
							ResumeChannel(RadioCHN[5])
							If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)	
						Else
							Select Int(SelectedItem\state2)
								Case 0 ;randomkanava
									ResumeChannel(RadioCHN[0])
									strtemp = "        USER TRACK PLAYER - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "NOT ENABLED     "
									ElseIf UserTrackMusicAmount<1
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "NO TRACKS FOUND     "
									Else
										If (Not ChannelPlaying(RadioCHN[0]))
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName[RadioState[0]]
										Else
											strtemp = strtemp + Upper(UserTrackName[RadioState[0]]) + "          "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict RadioSquelch
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName[RadioState[0]]
										EndIf
									EndIf
								Case 1 ;h√§lytyskanava
									DebugLog RadioState[1] 
									
									ResumeChannel(RadioCHN[1])
									strtemp = "        WARNING - CONTAINMENT BREACH          "
									If ChannelPlaying(RadioCHN[1]) = False Then
										
										If RadioState[1] => 5 Then
											RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10 + 1])	
											RadioState[1] = 0
										Else
											RadioState[1]=RadioState[1]+1	
											RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10])	
										EndIf
										
									EndIf
									
								Case 2 ;scp-radio
									ResumeChannel(RadioCHN[2])
									strtemp = "        SCP Foundation On-Site Radio          "
									If ChannelPlaying(RadioCHN[2]) = False Then
										RadioState[2]=RadioState[2]+1
										If RadioState[2] = 17 Then RadioState[2] = 1
										If Floor(RadioState[2]/2)=Ceil(RadioState[2]/2) Then ;parillinen, soitetaan normiviesti
											RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10 + Int(RadioState[2]/2)])
										Else ;pariton, soitetaan musiikkia
											RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10])
										EndIf
									EndIf 
								Case 3
									ResumeChannel(RadioCHN[3])
									strtemp = "             EMERGENCY CHANNEL - RESERVED FOR COMMUNICATION IN THE EVENT OF A CONTAINMENT BREACH         "
									If ChannelPlaying(RadioCHN[3]) = False Then RadioCHN[3] = PlaySound_Strict(RadioStatic)
									
									If MTFtimer > 0 Then 
										RadioState[3]=RadioState[3]+Max(Rand(-10,1),0)
										Select RadioState[3]
											Case 40
												If Not RadioState3[0] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[0] = True	
												EndIf											
											Case 400
												If Not RadioState3[1] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[1] = True	
												EndIf	
											Case 800
												If Not RadioState3[2] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[2] = True
												EndIf													
											Case 1200
												If Not RadioState3[3] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState[3] = RadioState[3]+1	
													RadioState3[3] = True
												EndIf
											Case 1600
												If Not RadioState3[4] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[4] = True
												EndIf
											Case 2000
												If Not RadioState3[5] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[5] = True
												EndIf
											Case 2400
												If Not RadioState3[6] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[6] = True
												EndIf
										End Select
									EndIf
								Case 4
									ResumeChannel(RadioCHN[6]) ;taustalle kohinaa
									If ChannelPlaying(RadioCHN[6]) = False Then RadioCHN[6] = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN[4])
									If ChannelPlaying(RadioCHN[4]) = False Then 
										If RemoteDoorOn = False And RadioState[8] = False Then
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter3.ogg"))	
											RadioState[8] = True
										Else
											RadioState[4]=RadioState[4]+Max(Rand(-10,1),0)
											
											Select RadioState[4]
												Case 10
													If (Not Contained106)
														If Not RadioState4[0] Then
															RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\OhGod.ogg"))
															RadioState[4] = RadioState[4]+1
															RadioState4[0] = True
														EndIf
													EndIf													
												Case 100
													If Not RadioState4[1] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[1] = True
													EndIf		
												Case 158
													If MTFtimer = 0 Then 
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState[2] = True
													EndIf
												Case 200
													If Not RadioState4[3] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter4.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[3] = True
													EndIf		
												Case 260
													If Not RadioState4[4] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[4] = True
													EndIf		
												Case 300
													If Not RadioState4[5] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter1.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[5] = True
													EndIf		
												Case 350
													If Not RadioState4[6] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[6] = True
													EndIf		
												Case 400
													If Not RadioState4[7] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[7] = True
													EndIf		
												Case 450
													If Not RadioState4[8] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin3.ogg"))	
														RadioState[4] = RadioState[4]+1		
														RadioState4[8] = True
													EndIf		
												Case 600
													If Not RadioState4[2] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin4.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[2] = True
													EndIf		
											End Select
										EndIf
									EndIf
									
									
								Case 5
									ResumeChannel(RadioCHN[5])
									If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
							End Select 
							
							x=x+66
							y=y+419
							
							Color (30,30,30)
							
							If SelectedItem\state <= 100 Then
								;Text (x - 60, y - 20, "BATTERY")
								For i = 0 To 4
									Rect(x, y+8*i, 43 - i * 6, 4, Ceil(SelectedItem\state / 20.0) > 4 - i )
								Next
							EndIf	
							
							SetFont fo\Font[Font_Digital_Small]
							Text(x+60, y, "CHN")						
							
							If SelectedItem\itemtemplate\tempname = "veryfineradio" Then ;"KOODIKANAVA"
								ResumeChannel(RadioCHN[0])
								If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
								
								;RadioState[7]=kuinka mones piippaus menossa
								;RadioState[8]=kuinka mones access coden numero menossa
								RadioState[6]=RadioState[6] + FPSfactor
								temp = Mid(Str(AccessCode),RadioState[8]+1,1)
								If RadioState[6]-FPSfactor =< RadioState[7]*50 And RadioState[6]>RadioState[7]*50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState[7]=RadioState[7]+1
									If RadioState[7]=>temp Then
										RadioState[7]=0
										RadioState[6]=-100
										RadioState[8]=RadioState[8]+1
										If RadioState[8]=4 Then RadioState[8]=0 : RadioState[6]=-200
									EndIf
								EndIf
								
								strtemp = ""
								For i = 0 To Rand(5, 30)
									strtemp = strtemp + Chr(Rand(1,100))
								Next
								
								SetFont fo\Font[Font_Digital_Large]
								Text(x+97, y+16, Rand(0,9),True,True)
								
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\state2 <> i-2 Then ;pausetetaan nykyinen radiokanava
											PlaySound_Strict RadioSquelch
											If RadioCHN[Int(SelectedItem\state2)] <> 0 Then PauseChannel(RadioCHN[Int(SelectedItem\state2)])
										EndIf
										SelectedItem\state2 = i-2
										;jos nykyist√§ kanavaa ollaan soitettu, laitetaan jatketaan toistoa samasta kohdasta
										If RadioCHN[SelectedItem\state2]<>0 Then ResumeChannel(RadioCHN[SelectedItem\state2])
									EndIf
								Next
								
								SetFont fo\Font[Font_Digital_Large]
								Text(x+97, y+16, Int(SelectedItem\state2+1),True,True)
							EndIf
							
							SetFont fo\Font[Font_Digital_Small]
							If strtemp <> "" Then
								strtemp = Right(Left(strtemp, (Int(MilliSecs()/300) Mod Len(strtemp))),10)
								Text(x+32, y+33, strtemp)
							EndIf
							
							SetFont fo\Font[Font_Default]
							
						EndIf
						
					EndIf
					
					;[End Block]
				Case "cigarette"
					;[Block]
					If CanUseItem(False,False,True)
						If SelectedItem\state = 0 Then
							Select Rand(6)
								Case 1
									Msg = GetLocalString("Items", "cigarette_1")
								Case 2
									Msg = GetLocalString("Items", "cigarette_2")
								Case 3
									Msg = GetLocalString("Items", "cigarette_3")
									RemoveItem(SelectedItem)
								Case 4
									Msg = GetLocalString("Items", "cigarette_4")
								Case 5
									Msg = GetLocalString("Items", "cigarette_5")
								Case 6
									Msg = GetLocalString("Items", "cigarette_6")
									RemoveItem(SelectedItem)
							End Select
							SelectedItem\state = 1 
						Else
							Msg = GetLocalString("Items", "cigarette_2")
						EndIf
						
						MsgTimer = 70 * 5
					EndIf
					;[End Block]
				Case "420"
					;[Block]
					If CanUseItem(False,False,True)
						If Wearing714=1 Then
							Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK" + Chr(34)
						Else
							Msg = Chr(34) + "MAN DATS SUM GOOD ASS SHIT" + Chr(34)
							;Injuries = Max(Injuries-0.5, 0)
							HealSPPlayer(80)
							BlurTimer = 500
							GiveAchievement(Achv420)
							PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
						EndIf
						MsgTimer = 70 * 5
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "420s"
					;[Block]
					If CanUseItem(False,False,True)
						If Wearing714=1 Then
							Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK" + Chr(34)
						Else
							DeathMSG = Designation+" found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							DeathMSG = DeathMSG+"Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							DeathMSG = DeathMSG+"whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							Msg = Chr(34) + "UH WHERE... WHAT WAS I DOING AGAIN... MAN I NEED TO TAKE A NAP..." + Chr(34)
							KillTimer = -1						
						EndIf
						MsgTimer = 70 * 6
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If Wearing714=1 Then
						Msg = "You removed the ring."
						Wearing714 = False
					Else
						GiveAchievement(Achv714)
						Msg = "You put on the ring."
						Wearing714 = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					Msg = "You removed the hazmat suit."
					WearingHazmat = 0
					MsgTimer = 70 * 5
					DropItem(SelectedItem)
					SelectedItem = Null	
					;[End Block]
				Case "vest"
					;[Block]
					If WearingVest Then
						Msg = "You removed the vest."
						WearingVest = False
					Else
						Msg = "You put on the vest and feel slightly encumbered."
						WearingVest = True
						TakeOffStuff(2)
					EndIf
					MsgTimer = 70 * 7
					SelectedItem = Null
					;[End Block]
				Case "finevest"
					;[Block]
					If WearingVest Then
						Msg = "You removed the vest."
						WearingVest = False						
					Else
						Msg = "You put on the vest and feel heavily encumbered."
						WearingVest = 2
						TakeOffStuff(2)
					EndIf
					SelectedItem = Null	
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If (Not mpl\HasNTFGasmask) Then
						If WearingGasMask Then
							Msg = "You removed the gas mask."
						Else
							If SelectedItem\itemtemplate\tempname = "supergasmask"
								Msg = "You put on the gas mask and you can breathe easier."
							Else
								Msg = "You put on the gas mask."
							EndIf
							TakeOffStuff(2+8+32+64)
						EndIf
						MsgTimer = 70 * 5
						If SelectedItem\itemtemplate\tempname="gasmask3" Then
							If WearingGasMask = 0 Then WearingGasMask = 3 Else WearingGasMask=0
						ElseIf SelectedItem\itemtemplate\tempname="supergasmask"
							If WearingGasMask = 0 Then WearingGasMask = 2 Else WearingGasMask=0
						Else
							WearingGasMask = (Not WearingGasMask)
						EndIf
					Else
						Msg = "You're already equipped with a gasmask."
						MsgTimer = 70*5
					EndIf
					SelectedItem = Null				
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					If SelectedItem\itemtemplate\name <> "S-NAV Navigator Ultimate" Then
						If SelectedItem\state <= 100 Then 
							SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.005)
						EndIf
					EndIf
					;[End Block]
				;new Items in SCP:CB 1.3
				Case "scp1499","super1499"
					;[Block]
					If (Not Wearing1499%) Then
						GiveAchievement(Achv1499)
						TakeOffStuff(1+2+8+32)
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name = "dimension1499" Then
								BlinkTimer = -1
								NTF_1499PrevRoom = PlayerRoom
								NTF_1499PrevX# = EntityX(Collider)
								NTF_1499PrevY# = EntityY(Collider)
								NTF_1499PrevZ# = EntityZ(Collider)
								
								If NTF_1499X# = 0.0 And NTF_1499Y# = 0.0 And NTF_1499Z# = 0.0
									PositionEntity (Collider, r\x+676.0*RoomScale, r\y+314.0*RoomScale, r\z-2080.0*RoomScale)
								Else
									PositionEntity (Collider, NTF_1499X#, NTF_1499Y#+0.05, NTF_1499Z#)
								EndIf
								ResetEntity(Collider)
								UpdateDoors()
								UpdateRooms()
								For it.Items = Each Items
									it\disttimer = 0
								Next
								PlayerRoom = r
								PlaySound_Strict (LoadTempSound("SFX\SCP\1499\Enter.ogg"))
								NTF_1499X# = 0.0
								NTF_1499Y# = 0.0
								NTF_1499Z# = 0.0
								If Curr096<>Null
									If Curr096\SoundChn<>0
										SetStreamVolume_Strict(Curr096\SoundChn,0.0)
									EndIf
								EndIf
								Exit
							EndIf
						Next
					EndIf
					If SelectedItem\itemtemplate\tempname="super1499"
						If Wearing1499%=0 Then Wearing1499% = 2 Else Wearing1499%=0
					Else
						Wearing1499% = (Not Wearing1499%)
					EndIf
					SelectedItem = Null
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						Select SelectedItem\itemtemplate\name
							Case "Old Badge"
								Msg = Chr(34)+"Huh? This guy looks just like me!"+Chr(34)
								MsgTimer = 70*10
						End Select
						
						SelectedItem\state = 1
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						
						Msg = Chr(34)+"Isn't this the key to that old shack? The one where I... No, it can't be."+Chr(34)
						MsgTimer = 70*10
						SelectedItem\state = 1
					EndIf
					
					SelectedItem = Null
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\state = 0
						Select SelectedItem\itemtemplate\name
							Case "Disciplinary Hearing DH-S-4137-17092"
								BlurTimer = 1000
								
								Msg = Chr(34)+"Why does this seem so familiar?"+Chr(34)
								MsgTimer = 70*10
								PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
								SelectedItem\state = 1
						End Select
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\state = 0
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
						Msg = Chr(34)+"Hm, somehow this feels like the coin that has made me many decisions easier..."+Chr(34)
						MsgTimer = 70*10
						SelectedItem\state = 1
					EndIf
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using=1 Then
						Msg = "You closed the locket."
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						Msg = "You opened the locket."
						I_427\Using = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = GetLocalString("Items", "pill")
						MsgTimer = 70*7
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "scp500death"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = GetLocalString("Items", "pill")
						MsgTimer = 70*7
						
						If I_427\Timer < 70*360 Then
							I_427\Timer = 70*360
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Default
					;[Block]
					;check if the item is an inventory-type object
					If SelectedItem\invSlots > 0 Then
						DoubleClick = 0
						MouseHit1 = 0
						MouseDown1 = 0
						LastMouseHit1 = 0
						OtherOpen = SelectedItem
						SelectedItem = Null
					EndIf
					
;					If SelectedItem\itemtemplate\isGun%
;						For g.Guns = Each Guns
;							If g\name$ = SelectedItem\itemtemplate\tempname$
;								;If g_I\HoldingGun <> g\ID%
;								g_I\GunChangeFLAG = False
;								g_I\HoldingGun = g\ID%
;								g_I\Weapon_CurrSlot% = 0
;								g_I\Weapon_NoSlot$ = g\name$
;								SelectedItem = Null
;								Exit
;								;EndIf
;							EndIf
;						Next
;					EndIf
					;[End Block]
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\img <> 0
					Local IN$ = SelectedItem\itemtemplate\tempname
					If IN$ = "paper" Lor IN$ = "badge" Lor IN$ = "oldpaper" Lor IN$ = "ticket" Then
						Local a_it.Items
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\itemtemplate\tempname
								If IN2$ = "paper" Lor IN2$ = "badge" Lor IN2$ = "oldpaper" Lor IN2$ = "ticket" Then
									If a_it\itemtemplate\img<>0
										If a_it\itemtemplate\img <> SelectedItem\itemtemplate\img
											FreeImage(a_it\itemtemplate\img)
											a_it\itemtemplate\img = 0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf			
			EndIf
			
			If MouseHit2 Then
				IN$ = SelectedItem\itemtemplate\tempname
				;If IN$ = "paper" Lor IN$ = "scp1025" Lor IN$ = "badge" Lor IN$ = "oldpaper" Then
				If IN$ = "scp1025" Then
					If SelectedItem\itemtemplate\img<>0 Then FreeImage(SelectedItem\itemtemplate\img)
					SelectedItem\itemtemplate\img=0
				EndIf
				
				If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\itemtemplate\sound])
				SelectedItem = Null
				ResetInput()
			EndIf
		EndIf		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 6
			If RadioCHN[i] <> 0 Then 
				If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
			EndIf
		Next
	EndIf
	
	UpdateTasks()
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse viewport_center_x, viewport_center_y
	
;	If InvOpen
;		For g.Guns = Each Guns
;			If g\name$ = "p90"
;				g\MouseDownTimer# = 15.0
;			EndIf
;		Next
;	EndIf
	
	CatchErrors("UpdateGUI")
End Function

Function DrawGUI()
	CatchErrors("Uncaught (DrawGUI)")
	
	Local temp%, x%, y%, z%, i%, yawvalue#, pitchvalue#
	Local x2#,y2#,z2#
	Local n%, xtemp, ytemp, strtemp$, projY#, scale#
	
	Local e.Events, it.Items
	
	If MenuOpen Lor ConsoleOpen Lor d_I\SelectedDoor <> Null Lor InvOpen Lor OtherOpen <> Null Lor EndingTimer < 0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom And e\EventState > 600 Then
				If BlinkTimer < -3 And BlinkTimer > -11 Then
					If e\img = 0 Then
						If BlinkTimer > -5 And Rand(30)=1 Then
							If Rand(5)<5 Then PlaySound_Strict DripSFX[0]
							If e\img = 0 Then e\img = LoadImage_Strict("GFX\npcs\106face.jpg")
						EndIf
					Else
						DrawImage e\img, opt\GraphicWidth/2-Rand(390,310), opt\GraphicHeight/2-Rand(290,310)
					EndIf
				Else
					If e\img <> 0 Then FreeImage e\img : e\img = 0
				EndIf
				
				Exit
			EndIf
		Next
	EndIf
	
	If d_I\ClosestButton <> 0 And d_I\SelectedDoor = Null And InvOpen = False And MenuOpen = False And OtherOpen = Null And (Not PlayerInNewElevator)
		temp% = CreatePivot()
		PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
		PointEntity temp, d_I\ClosestButton
		yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		temp = FreeEntity_Strict(temp)
		
		DrawImage(HandIcon, opt\GraphicWidth / 2 + Sin(yawvalue) * (opt\GraphicWidth / 3) - 32, opt\GraphicHeight / 2 - Sin(pitchvalue) * (opt\GraphicHeight / 3) - 32)
	EndIf
	
	If ClosestItem <> Null Then
		yawvalue# = -DeltaYaw(Camera, ClosestItem\collider)
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = -DeltaPitch(Camera, ClosestItem\collider)
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		DrawImage(HandIcon2, opt\GraphicWidth / 2 + Sin(yawvalue) * (opt\GraphicWidth / 3) - 32, opt\GraphicHeight / 2 - Sin(pitchvalue) * (opt\GraphicHeight / 3) - 32)
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
		EndIf
	Next
	
	If Using294 Then Use294()
	
	If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (ConsoleOpen=False) And (Using294=False) And (SelectedScreen=Null) And EndingTimer=>0 And KillTimer >= 0
		If PlayerRoom\RoomTemplate\Name$ <> "gate_a_intro"
;			DrawChatSoundsGUI()
;			UpdateRadio()
		EndIf
	EndIf
	
;	If NTF_GameModeFlag=1
;		Tasks()
;	EndIf
	
	DrawSplashTexts()
	
	If HUDenabled Then 
		
		Local width% = 204, height% = 20
		x% = 80
		y% = opt\GraphicHeight - 95
		
		;Blinking Bar
		If BlinkTimer <= BLINKFREQ / 5 Then
			Color 255, 0, 0
		Else
			If BlinkEffect < 1.0 Then
				Color 0, 255, 0
			Else
				Color 255, 255, 255
			EndIf
		EndIf
		Rect (x, y, width, height, False)
		For i = 1 To Int(((width - 2) * (BlinkTimer / (BLINKFREQ))) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		If BlinkTimer <= 0.0 Lor BlurTimer > 0.0 Lor LightFlash > 0.0 Lor LightBlink > 0.0 Lor EyeIrritation > 0 Then
			Color 255, 0, 0
		Else
			If BlinkEffect < 1.0 Then
				Color 0, 255, 0
			Else
				Color 255, 255, 255
			EndIf
		EndIf
		
		Rect(x - 50 - 1, y, 30 + 2, 30 + 2, False)
		DrawImage BlinkIcon, x - 50, y + 1
		
		SetFont fo\Font%[Font_Digital_Medium]
		y = opt\GraphicHeight - 55
		
		;Health
		Color 0,0,0
		Rect(x - 50, y, 30, 30)
		
		If psp\Health > 100 Then
			Color 0, 255, 0
		ElseIf psp\Health > 20 Then
			Color 255, 255, 255
		Else
			Color 255, 0, 0
		EndIf
		
		Rect(x - 50 - 1, y, 30 + 2, 30 + 2, False)
		DrawImage mpl\HealthIcon, x - 50, y + 1
		
		If psp\Health > 20
			Color 0, 255, 0
		Else
			Color 255, 0, 0
		EndIf
		TextWithAlign x + 30, y + 5, Int(psp\Health), 2
		
		;Kevlar
		Color 0,0,0
		Rect(x + 100, y, 30, 30)
		
		If psp\Kevlar > 100 Then
			Color 0, 255, 0
		ElseIf psp\Kevlar > 20 Then
			Color 255, 255, 255
		Else
			Color 255, 0, 0
		EndIf
		
		Rect(x + 100 - 1, y, 30 + 2, 30 + 2, False)
		DrawImage mpl\KevlarIcon, x + 100, y + 1
		
		If psp\Kevlar > 20
			Color 0, 255, 0
		Else
			Color 255, 0, 0
		EndIf
		TextWithAlign x + 180, y + 5, Int(psp\Kevlar), 2
		
		;Stamina Bar
		If Stamina < 100.0 And PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then
			y = opt\GraphicHeight - 55
			x = (opt\GraphicWidth / 2) - (width / 2) + 20
			If InfiniteStamina Lor (StaminaEffect < 1.0) Then
				Color 0, 255, 0
			Else
				If Stamina <= 20.0 Then
					Color 255, 0, 0
				Else
					Color 255, 255, 255
				EndIf
			EndIf
			Rect (x, y, width, height, False)
			For i = 1 To Int(((width - 2) * (Stamina / 100.0)) / 10)
				DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
			Next
			
			Color 0, 0, 0
			Rect(x - 50, y, 30, 30)
			
			If SuperMan Lor InfiniteStamina Lor (StaminaEffect < 1.0) Then
				Color 0, 255, 0
			Else
				If Stamina <= 0.0 Then
					Color 255, 0, 0
				Else
					Color 255, 255, 255
				EndIf
			EndIf
			Rect(x - 50 - 1, y, 30 + 2, 30 + 2, False)
			If Crouch Then
				DrawImage CrouchIcon, x - 50, y + 1
			Else
				DrawImage SprintIcon, x - 50, y + 1
			EndIf
		EndIf
		
		;Weapons
		DrawGunsInHUD()
		
		If DebugHUD Then
			x% = 80
			
			Color 255, 255, 255
			SetFont fo\ConsoleFont
			
			;Text x + 250, 50, "Zone: " + (EntityZ(Collider)/8.0)
			Text x + 50, 20, "Delta time: "+ft\DeltaTime
			Text x - 50, 50, "Player Position: (" + f2s(EntityX(Collider), 3) + ", " + f2s(EntityY(Collider), 3) + ", " + f2s(EntityZ(Collider), 3) + ")"
			Text x - 50, 70, "Camera Position: (" + f2s(EntityX(Camera), 3)+ ", " + f2s(EntityY(Camera), 3) +", " + f2s(EntityZ(Camera), 3) + ")"
			Text x - 50, 100, "Player Rotation: (" + f2s(EntityPitch(Collider), 3) + ", " + f2s(EntityYaw(Collider), 3) + ", " + f2s(EntityRoll(Collider), 3) + ")"
			Text x - 50, 120, "Camera Rotation: (" + f2s(EntityPitch(Camera), 3)+ ", " + f2s(EntityYaw(Camera), 3) +", " + f2s(EntityRoll(Camera), 3) + ")"
			Text x - 50, 150, "Map seed: "+RandomSeed
			Text x - 50, 170, "Room: " + PlayerRoom\RoomTemplate\Name
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					Text x - 50, 190, "Room event: " + ev\EventName   
					Text x - 50, 205, "state: " + ev\EventState
					Text x - 50, 220, "state2: " + ev\EventState2   
					Text x - 50, 235, "state3: " + ev\EventState3
					Text x - 50, 250, "str: " + ev\EventStr
					Exit
				EndIf
			Next
			Text x - 50, 270, "Room coordinates: (" + Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5) + ", angle: "+PlayerRoom\angle + ")"
			Text x - 50, 290, "Current Trigger: " + CheckTriggers()
			Text x - 50, 320, "Stamina: " + f2s(Stamina, 3)
			Text x - 50, 340, "Death timer: " + f2s(KillTimer, 3)               
			Text x - 50, 360, "Blink timer: " + f2s(BlinkTimer, 3)
			Text x - 50, 380, "Health: " + psp\Health
			Text x - 50, 400, "Kevlar: " + psp\Kevlar
			If Curr173 <> Null
				Text x - 50, 430, "SCP - 173 Position (collider): (" + f2s(EntityX(Curr173\Collider), 3) + ", " + f2s(EntityY(Curr173\Collider), 3) + ", " + f2s(EntityZ(Curr173\Collider), 3) + ")"
				Text x - 50, 450, "SCP - 173 Position (obj): (" + f2s(EntityX(Curr173\obj), 3) + ", " + f2s(EntityY(Curr173\obj), 3) + ", " + f2s(EntityZ(Curr173\obj), 3) + ")"
				;Text x - 50, 410, "SCP - 173 Idle: " + Curr173\Idle
				Text x - 50, 470, "SCP - 173 State: " + Curr173\State
			EndIf
			If Curr106 <> Null
				Text x - 50, 490, "SCP - 106 Position: (" + f2s(EntityX(Curr106\obj), 3) + ", " + f2s(EntityY(Curr106\obj), 3) + ", " + f2s(EntityZ(Curr106\obj), 3) + ")"
				Text x - 50, 510, "SCP - 106 Idle: " + Curr106\Idle
				Text x - 50, 530, "SCP - 106 State: " + Curr106\State
			EndIf
			offset% = 0
			For npc.NPCs = Each NPCs
				If npc\NPCtype = NPCtype096 Then
					Text x - 50, 550, "SCP - 096 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					Text x - 50, 570, "SCP - 096 Idle: " + npc\Idle
					Text x - 50, 590, "SCP - 096 State: " + npc\State
					Text x - 50, 610, "SCP - 096 Speed: " + f2s(npc\currspeed, 5)
				EndIf
				;If npc\NPCtype = NPCtypeMTF Then
				;	Text x - 50, 600 + 60 * offset, "MTF " + offset + " Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
				;	Text x - 50, 640 + 60 * offset, "MTF " + offset + " State: " + npc\State
				;	Text x - 50, 620 + 60 * offset, "MTF " + offset + " LastSeen: " + npc\lastseen					
				;	offset = offset + 1
				;EndIf
			Next
			If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
				Text x + 350, 50, "Current Chunk X/Z: ("+(Int((EntityX(Collider)+20)/40))+", "+(Int((EntityZ(Collider)+20)/40))+")"
				Local CH_Amount% = 0
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				Text x + 350, 70, "Current Chunk Amount: "+CH_Amount
			Else
				Text x + 350, 50, "Current Room Position: ("+PlayerRoom\x+", "+PlayerRoom\y+", "+PlayerRoom\z+")"
			EndIf
			
			Text x + 350, 90, SystemProperty("os")+" "+gv\OSBit+" bit, CPU: "+SystemProperty("cpuname")+" (Arch: "+SystemProperty("cpuarch")+", "+GetEnv("NUMBER_OF_PROCESSORS")+" Threads)"
			Text x + 350, 110, "Phys. Memory: "+(AvailPhys()/1024)+" MB/"+(TotalPhys()/1024)+" MB ("+(AvailPhys())+" KB/"+(TotalPhys())+" KB). CPU Usage: "+MemoryLoad()+"%"
			Text x + 350, 130, "Virtual Memory: "+(AvailVirtual()/1024)+" MB/"+(TotalVirtual()/1024)+" MB ("+(AvailVirtual())+" KB/"+(TotalVirtual())+" KB)"
			Text x + 350, 150, "Video Memory: "+(AvailVidMem()/1024)+" MB/"+(TotalVidMem()/1024)+" MB ("+(AvailVidMem())+" KB/"+(TotalVidMem())+" KB)"
			Text x + 350, 170, "Triangles rendered: "+CurrTrisAmount
			Text x + 350, 190, "Active textures: "+ActiveTextures()
			Text x + 350, 210, "MTF Camera Scan Timer: "+MTF_CameraCheckTimer
			
			SetFont fo\Font[Font_Default]
		EndIf
	EndIf
	
;	If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (SelectedDoor=Null) And (ConsoleOpen=False) And (Using294=False) And (SelectedScreen=Null) And EndingTimer=>0 And KillTimer >= 0
;		ToggleGuns()
;	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage SelectedScreen\img, opt\GraphicWidth/2-ImageWidth(SelectedScreen\img)/2,opt\GraphicHeight/2-ImageHeight(SelectedScreen\img)/2
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	
	Local g.Guns
	
	Local shouldDrawHUD%=True
	If d_I\SelectedDoor <> Null Then
		SelectedItem = Null
		
		If shouldDrawHUD Then
			If d_I\SelectedDoor\dir<>5 Then
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)+MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				projY# = ProjectedY()
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)-MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				scale# = (ProjectedY()-projY)/462.0
				
				x = opt\GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
				y = opt\GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2
				
				SetFont fo\Font[Font_Digital_Small]
				If KeypadMSG <> "" Then 
					If (KeypadTimer Mod 70) < 35 Then Text opt\GraphicWidth/2, y+124*scale, KeypadMSG, True,True
				Else
					Text opt\GraphicWidth/2, y+70*scale, "ACCESS CODE: ",True,True	
					SetFont fo\Font[Font_Digital_Large]
					Text opt\GraphicWidth/2, y+124*scale, KeypadInput,True,True	
				EndIf
				
				x = x+44*scale
				y = y+249*scale
			Else
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)+MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				projY# = ProjectedY()
				CameraProject(Camera, EntityX(d_I\ClosestButton,True),EntityY(d_I\ClosestButton,True)-MeshHeight(d_I\ButtonOBJ[BUTTON_NORMAL])*0.015,EntityZ(d_I\ClosestButton,True))
				scale# = (ProjectedY()-projY)/462.0
				
				x = opt\GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
				y = opt\GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2
				
				Color 255,0,0
				x=x+120*scale
				y=y+259*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						Rect x,y,82*scale,82*scale,False
					EndIf
				Else
					If co\KeyPad_CurrButton = 0
						Rect x,y,82*scale,82*scale,False
					EndIf
				EndIf
				
				y=y+131*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						Rect x,y,82*scale,82*scale,False
					EndIf
				Else
					If co\KeyPad_CurrButton = 1
						Rect x,y,82*scale,82*scale,False
					EndIf
				EndIf
				
				y=y+130*scale
				If (Not co\Enabled)
					If RectsOverlap(x,y,82*scale,82*scale,MouseX(),MouseY(),0,0)
						Rect x,y,82*scale,82*scale,False
					EndIf
				Else
					If co\KeyPad_CurrButton = 2
						Rect x,y,82*scale,82*scale,False
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	Local spacing%
	Local PrevOtherOpen.Items
	
	Local OtherSize%,OtherAmount%
	
	Local isEmpty%
	
	Local isMouseOn%
	
	Local closedInv%
	
	Local InvImgSize% = (64 * MenuScale) / 2
	
	If OtherOpen<>Null Then
		;[Block]
		PrevOtherOpen = OtherOpen
		OtherSize=OtherOpen\invSlots;Int(OtherOpen\state2)
		
		For i%=0 To OtherSize-1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount+1
			EndIf
		Next
		
		Local tempX% = 0
		
		width = 70
		height = 70
		spacing% = 35
		
		x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = opt\GraphicHeight / 2 - (height * OtherSize /5 + height * (OtherSize / 5 - 1)) / 2
		
		;ItemAmount = 0
		For n% = 0 To OtherSize - 1
			isMouseOn% = False
			If MouseOn(x, y, width, height) Then 
				isMouseOn = True
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (SelectedItem <> OtherOpen\SecondInv[n] Lor isMouseOn) Then DrawImage(OtherOpen\SecondInv[n]\invimg, x + width / 2 - 32, y + height / 2 - 32)
			EndIf
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If isMouseOn Then
					Color 255, 255, 255
					SetFont(fo\Font[Font_Default])
					Text(x + width / 2, y + height + spacing - 15, OtherOpen\SecondInv[n]\itemtemplate\name, True)
				EndIf
				
				;ItemAmount=ItemAmount+1
			EndIf					
			
			x=x+width + spacing
			tempX=tempX + 1
			If tempX = 5 Then 
				tempX=0
				y = y + height*2 
				x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - InvImgSize, ScaledMouseY() - InvImgSize)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\invimg, ScaledMouseX() - InvImgSize, ScaledMouseY() - InvImgSize)
				EndIf
			EndIf
		EndIf
		;[End Block]
	ElseIf InvOpen Then
		
		d_I\SelectedDoor = Null
		
		width% = 70
		height% = 70
		spacing% = 35
		
		x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = opt\GraphicHeight / 2 - (height * MaxItemAmount /5 + height * (MaxItemAmount / 5 - 1)) / 2
		
		;ItemAmount = 0
		For  n% = 0 To MaxItemAmount - 1
			isMouseOn% = False
			
			If Inventory[n] <> Null Then
				Color 200, 200, 200
				Select Inventory[n]\itemtemplate\tempname 
					Case "gasmask"
						If WearingGasMask=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "supergasmask"
						If WearingGasMask=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "gasmask3"
						If WearingGasMask=3 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit"
						If WearingHazmat=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit2"
						If WearingHazmat=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit3"
						If WearingHazmat=3 Then Rect(x - 3, y - 3, width + 6, height + 6)	
					Case "vest"
						If WearingVest=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "finevest"
						If WearingVest=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp714"
						If Wearing714=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "nvgoggles"
						If WearingNightVision=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "supernv"
						If WearingNightVision=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp1499"
						If Wearing1499=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "super1499"
						If Wearing1499=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "finenvgoggles"
						If WearingNightVision=3 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp427"
						If I_427\Using=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
				End Select
			EndIf
			
			If MouseOn(x, y, width, height) Then 
				isMouseOn = True
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			Color 255, 255, 255
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))
			
			If Inventory[n] <> Null Then
				If (SelectedItem <> Inventory[n] Lor isMouseOn) Then 
					DrawImage(Inventory[n]\invimg, x + width / 2 - 32, y + height / 2 - 32)
				EndIf
			EndIf
			
			If Inventory[n] <> Null And SelectedItem <> Inventory[n] Then
				If isMouseOn Then
					If SelectedItem = Null
						spacing2# = 0
						x2# = x
						y2# = y+height
						width2# = width/3
						height2# = height/3
						
						If Inventory[n]\itemtemplate\isGun% = True
							SetFont fo\Font[Font_Default]
							For i = 0 To MaxGunSlots-1
								Color 255,255,255
								DrawFrame(x2+(width2*i), y2, width2, height2, (x Mod 64), (x Mod 64))
								If g_I\Weapon_CurrSlot = (i + 1) Then
									Color 255,0,0
								Else
									Color 255,255,255
								EndIf
								Text (x2+(width2*i))+(width2*0.5), y2+(height2*0.5), (i + 1), 1, 1
								spacing2# = 10
							Next
						EndIf
						
						SetFont fo\Font[Font_Default]
						Color 0,0,0
						Text(x + width / 2 + 1, y + height + spacing + spacing2 - 15 + 1, Inventory[n]\name, True)							
						Color 255, 255, 255	
						Text(x + width / 2, y + height + spacing + spacing2 - 15, Inventory[n]\name, True)	
					EndIf
				EndIf
				
				;ItemAmount=ItemAmount+1
			Else
				
			EndIf					
			
			x=x+width + spacing
			If n = 4 Then 
				y = y + height*2 
				x = opt\GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		width2# = 70
		height2# = 70
		spacing2# = 35
		
		x2# = opt\GraphicWidth / 2
		y2# = opt\GraphicHeight / 2 + height2*2.6
		
		SetFont fo\Font[Font_Default]
		Color 0,0,0
		Text x2+1,y2+1,"Weapon Quick-Selection Slots:",1,1
		Color 255,255,255
		Text x2,y2,"Weapon Quick-Selection Slots:",1,1
		
		x2 = opt\GraphicWidth / 2 - (width2 * MaxItemAmount /2 + spacing2 * (MaxItemAmount / 2 - 1)) / 2
		y2 = opt\GraphicHeight / 2 - height2 + height2*4
		
		x2=x2+width2 + spacing2
		
		Color 255,255,255
		For i = 0 To MaxGunSlots-1
			Local hasGunInSlot% = False
			If g_I\Weapon_InSlot[i] <> "" Then
				For g = Each Guns
					If g\name = g_I\Weapon_InSlot[i] Then
						If g_I\Weapon_CurrSlot = (i + 1) Then
							Color 200,200,200
							Rect x2+((width2+spacing2)*i) - 3, y2 - 3, width2 + 6, height2 + 6
						EndIf
						Color 255,255,255
						DrawFrame(x2+((width2+spacing2)*i), y2, width2, height2, (x2 Mod 64), (x2 Mod 64))
						DrawImage g\IMG, (x2+((width2+spacing2)*i)) + width2 / 2 - 32, y2 + height2 / 2 - 32
						hasGunInSlot = True
						Exit
					EndIf
				Next
			EndIf
			If (Not hasGunInSlot) Then
				DrawFrame(x2+((width2+spacing2)*i), y2, width2, height2, (x2 Mod 64), (x2 Mod 64))
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Lor SelectedItem <> Inventory[MouseSlot] Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - InvImgSize, ScaledMouseY() - InvImgSize)
				EndIf
			EndIf
		EndIf
		
	Else ;invopen = False
		
		;WIP
		If SelectedItem <> Null Then
			Select SelectedItem\itemtemplate\tempname
				Case "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "25ct", "coin", "fuse"
					;[Block]
					DrawImage(SelectedItem\itemtemplate\invimg, opt\GraphicWidth / 2 - InvImgSize, opt\GraphicHeight / 2 - InvImgSize)
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If psp\Health < 100 Then
						If CanUseItem(False, True, True)
							DrawImage(SelectedItem\itemtemplate\invimg, opt\GraphicWidth / 2 - InvImgSize, opt\GraphicHeight / 2 - InvImgSize)
							
							width% = 300
							height% = 20
							x% = opt\GraphicWidth / 2 - width / 2
							y% = opt\GraphicHeight / 2 + 80
							Rect(x, y, width+4, height, False)
							For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						Select SelectedItem\itemtemplate\name
							Case "Burnt Note" 
								SelectedItem\itemtemplate\img = LoadImage_Strict("GFX\items\bn.it")
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 0,0,0
								SetFont fo\Font[Font_Default_Medium]
								Text 277, 469, AccessCode, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Document SCP-372"
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 37,45,137
								SetFont fo\Font[Font_Journal]
								temp = ((Int(AccessCode)*3) Mod 10000)
								If temp < 1000 Then temp = temp+1000
								Text 383*MenuScale, 734*MenuScale, temp, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Movie Ticket"
								;don't resize because it messes up the masking
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
							Default 
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						End Select
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "scp1025"
					;[Block]
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict("GFX\items\1025\1025_"+Int(SelectedItem\state)+".jpg")	
						SelectedItem\itemtemplate\img=ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					;[End Block]
				Case "radio","18vradio","fineradio","veryfineradio"
					;[Block]
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.004)
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					;RadioState[5] = has the "use the number keys" -message been shown yet (true/false)
					;RadioState[6] = a timer for the "code channel"
					;RadioState[7] = another timer for the "code channel"
					
					If RadioState[5] = 0 Then 
						Msg = "Use the numbered keys 1 through 5 to cycle between various channels."
						MsgTimer = 70 * 5
						RadioState[5] = 1
						RadioState[0] = -1
					EndIf
					
					strtemp$ = ""
					
					x = opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img) ;+ 120
					y = opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img) ;- 30
					
					DrawImage(SelectedItem\itemtemplate\img, x, y)
					
					If SelectedItem\state > 0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor CoffinDistance < 4.0 Then
							ResumeChannel(RadioCHN[5])
							If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)	
						Else
							Select Int(SelectedItem\state2)
								Case 0 ;randomkanava
									ResumeChannel(RadioCHN[0])
									strtemp = "        USER TRACK PLAYER - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "NOT ENABLED     "
									ElseIf UserTrackMusicAmount<1
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "NO TRACKS FOUND     "
									Else
										If (Not ChannelPlaying(RadioCHN[0]))
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName[RadioState[0]]
										Else
											strtemp = strtemp + Upper(UserTrackName[RadioState[0]]) + "          "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict RadioSquelch
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName[RadioState[0]]
										EndIf
									EndIf
								Case 1 ;h√§lytyskanava
									DebugLog RadioState[1] 
									
									ResumeChannel(RadioCHN[1])
									strtemp = "        WARNING - CONTAINMENT BREACH          "
									If ChannelPlaying(RadioCHN[1]) = False Then
										
										If RadioState[1] => 5 Then
											RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10 + 1])	
											RadioState[1] = 0
										Else
											RadioState[1]=RadioState[1]+1	
											RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10])	
										EndIf
										
									EndIf
									
								Case 2 ;scp-radio
									ResumeChannel(RadioCHN[2])
									strtemp = "        SCP Foundation On-Site Radio          "
									If ChannelPlaying(RadioCHN[2]) = False Then
										RadioState[2]=RadioState[2]+1
										If RadioState[2] = 17 Then RadioState[2] = 1
										If Floor(RadioState[2]/2)=Ceil(RadioState[2]/2) Then ;parillinen, soitetaan normiviesti
											RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10 + Int(RadioState[2]/2)])	
										Else ;pariton, soitetaan musiikkia
											RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10 + 1])
										EndIf
									EndIf 
								Case 3
									ResumeChannel(RadioCHN[3])
									strtemp = "             EMERGENCY CHANNEL - RESERVED FOR COMMUNICATION IN THE EVENT OF A CONTAINMENT BREACH         "
									If ChannelPlaying(RadioCHN[3]) = False Then RadioCHN[3] = PlaySound_Strict(RadioStatic)
									
									If MTFtimer > 0 Then 
										RadioState[3]=RadioState[3]+Max(Rand(-10,1),0)
										Select RadioState[3]
											Case 40
												If Not RadioState3[0] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[0] = True	
												EndIf											
											Case 400
												If Not RadioState3[1] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[1] = True	
												EndIf	
											Case 800
												If Not RadioState3[2] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[2] = True
												EndIf													
											Case 1200
												If Not RadioState3[3] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState[3] = RadioState[3]+1	
													RadioState3[3] = True
												EndIf		
										End Select
									EndIf
								Case 4
									ResumeChannel(RadioCHN[6]) ;taustalle kohinaa
									If ChannelPlaying(RadioCHN[6]) = False Then RadioCHN[6] = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN[4])
									If ChannelPlaying(RadioCHN[4]) = False Then 
										If RemoteDoorOn = False And RadioState[8] = False Then
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter3.ogg"))	
											RadioState[8] = True
										Else
											RadioState[4]=RadioState[4]+Max(Rand(-10,1),0)
											
											Select RadioState[4]
												Case 10
													If Not RadioState4[0] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\OhGod.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[0] = True
													EndIf													
												Case 100
													If Not RadioState4[1] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[1] = True
													EndIf		
												Case 158
													If MTFtimer = 0 Then 
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState[2] = True
													EndIf
												Case 200
													If Not RadioState4[3] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter4.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[3] = True
													EndIf		
												Case 260
													If Not RadioState4[4] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[4] = True
													EndIf		
												Case 300
													If Not RadioState4[5] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter1.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[5] = True
													EndIf		
												Case 350
													If Not RadioState4[6] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[6] = True
													EndIf		
												Case 400
													If Not RadioState4[7] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[7] = True
													EndIf		
												Case 450
													If Not RadioState4[8] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin3.ogg"))	
														RadioState[4] = RadioState[4]+1		
														RadioState4[8] = True
													EndIf		
												Case 600
													If Not RadioState4[2] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin4.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[2] = True
													EndIf		
											End Select
										EndIf
									EndIf
									
									
								Case 5
									ResumeChannel(RadioCHN[5])
									If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
							End Select 
							
							x=x+66
							y=y+419
							
							Color (30,30,30)
							
							If SelectedItem\state <= 100 Then
								;Text (x - 60, y - 20, "BATTERY")
								For i = 0 To 4
									Rect(x, y+8*i, 43 - i * 6, 4, Ceil(SelectedItem\state / 20.0) > 4 - i )
								Next
							EndIf	
							
							SetFont fo\Font[Font_Digital_Small]
							Text(x+60, y, "CHN")						
							
							If SelectedItem\itemtemplate\tempname = "veryfineradio" Then ;"KOODIKANAVA"
								ResumeChannel(RadioCHN[0])
								If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
								
								;RadioState[7]=kuinka mones piippaus menossa
								;RadioState[8]=kuinka mones access coden numero menossa
								RadioState[6]=RadioState[6] + FPSfactor
								temp = Mid(Str(AccessCode),RadioState[8]+1,1)
								If RadioState[6]-FPSfactor =< RadioState[7]*50 And RadioState[6]>RadioState[7]*50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState[7]=RadioState[7]+1
									If RadioState[7]=>temp Then
										RadioState[7]=0
										RadioState[6]=-100
										RadioState[8]=RadioState[8]+1
										If RadioState[8]=4 Then RadioState[8]=0 : RadioState[6]=-200
									EndIf
								EndIf
								
								strtemp = ""
								For i = 0 To Rand(5, 30)
									strtemp = strtemp + Chr(Rand(1,100))
								Next
								
								SetFont fo\Font[Font_Digital_Large]
								Text(x+97, y+16, Rand(0,9),True,True)
								
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\state2 <> i-2 Then ;pausetetaan nykyinen radiokanava
											PlaySound_Strict RadioSquelch
											If RadioCHN[Int(SelectedItem\state2)] <> 0 Then PauseChannel(RadioCHN[Int(SelectedItem\state2)])
										EndIf
										SelectedItem\state2 = i-2
										;jos nykyist√§ kanavaa ollaan soitettu, laitetaan jatketaan toistoa samasta kohdasta
										If RadioCHN[SelectedItem\state2]<>0 Then ResumeChannel(RadioCHN[SelectedItem\state2])
									EndIf
								Next
								
								SetFont fo\Font[Font_Digital_Large]
								Text(x+97, y+16, Int(SelectedItem\state2+1),True,True)
							EndIf
							
							SetFont fo\Font[Font_Digital_Small]
							If strtemp <> "" Then
								strtemp = Right(Left(strtemp, (Int(MilliSecs()/300) Mod Len(strtemp))),10)
								Text(x+32, y+33, strtemp)
							EndIf
							
							SetFont fo\Font[Font_Default]
							
						EndIf
						
					EndIf
					
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					Local navType%
					Select SelectedItem\itemtemplate\name
						Case "S-NAV 300 Navigator"
							navType = 300
						Case "S-NAV 310 Navigator"
							navType = 310
						Case "S-NAV Navigator Ultimate"
							navType = 999
						Default
							navType = 300
					End Select
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						SelectedItem\itemtemplate\imgwidth = ImageWidth(SelectedItem\itemtemplate\img) / 2
						SelectedItem\itemtemplate\imgheight = ImageHeight(SelectedItem\itemtemplate\img) / 2
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					x = opt\GraphicWidth - SelectedItem\itemtemplate\imgwidth+20
					y = opt\GraphicHeight - SelectedItem\itemtemplate\imgheight-85
					width = 287
					height = 256
					
					Local PlayerX,PlayerZ
					
					DrawImage(SelectedItem\itemtemplate\img, x - SelectedItem\itemtemplate\imgwidth, y - SelectedItem\itemtemplate\imgheight + 85)
					
					SetFont fo\Font[Font_Digital_Small]
					
					If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
						If (MilliSecs() Mod 1000) > 300 Then
							Color 30,30,30
							Text(x, y + height / 2 - 80, "ERROR 06", True)
							Text(x, y + height / 2 - 60, "LOCATION UNKNOWN", True)
						EndIf
					Else						
						If (SelectedItem\state > 0 Lor navType = 999) And (Rnd(CoffinDistance + 15.0) > 1.0 Lor PlayerRoom\RoomTemplate\Name <> "cont_895") Then
							PlayerX% = Floor(EntityX(Collider) / 8.0 + 0.5) ;PlayerRoom\obj
							PlayerZ% = Floor(EntityZ(Collider) / 8.0 + 0.5) ;PlayerRoom\obj
							
							Local xx = x-SelectedItem\itemtemplate\imgwidth
							Local yy = y-SelectedItem\itemtemplate\imgheight+85
							
							If SelectedItem\state2 = 0.0 Then
								Local grid_size%
								If NTF_CurrZone = 3 Then
									grid_size% = MapGridSizeEZ
								Else
									grid_size% = MapGridSize
								EndIf
								
								Local posX# = EntityX(Collider) - 4.0
								Local posZ# = EntityZ(Collider) - 4.0
								Local stepsX% = 0
								Local stepsZ% = 0
								Local tempPos# = posX
								While tempPos < 0.0
									stepsX = stepsX + 1
									tempPos = tempPos + 8.0
								Wend
								tempPos# = posZ
								While tempPos < 0.0
									stepsZ = stepsZ + 1
									tempPos = tempPos + 8.0
								Wend
								x = x - 12 + ((posX + (8.0 * stepsX)) Mod 8.0) * 3
								y = y + 12 - ((posZ + (8.0 * stepsZ)) Mod 8.0) * 3
								
								SetBuffer ImageBuffer(NavBG)
								DrawImage(SelectedItem\itemtemplate\img, xx, yy)
								Local val% = 6 / (1 + 0.5*(navType = 300))
								For z2 = Max(0, PlayerZ - val) To Min(grid_size - 1, PlayerZ + val)
									For x2 = Max(0, PlayerX - val) To Min(grid_size - 1, PlayerX + val)
										;Make distinguishing between S-NAV 300 Navigator, S-NAV 310 Navigator and S-NAV Ultimate
										If navType = 300 And Rand(0,1) Then Exit
										If CoffinDistance > 16.0 Lor Rnd(16.0) < CoffinDistance Then
											If CurrGrid\Grid[x2 + (z2 * grid_size)] Then
												Local drawx% = x + (PlayerX - x2) * 24 - 12, drawy% = y - (PlayerZ - z2) * 24 - 12
												
												If (x2 + 1) > (grid_size - 1) Lor (Not CurrGrid\Grid[(x2 + 1) + (z2 * grid_size)]) Then
													DrawImage NavImages[3], drawx, drawy
												EndIf
												If (x2 - 1) < 0 Lor (Not CurrGrid\Grid[(x2 - 1) + (z2 * grid_size)]) Then
													DrawImage NavImages[1], drawx, drawy
												EndIf
												If (z2 - 1) < 0 Lor (Not CurrGrid\Grid[x2 + ((z2 - 1) * grid_size)]) Then
													DrawImage NavImages[0], drawx, drawy
												EndIf
												If (z2 + 1) > (grid_size - 1) Lor (Not CurrGrid\Grid[x2 + ((z2 + 1) * grid_size)]) Then
													DrawImage NavImages[2], drawx, drawy
												EndIf
											EndIf
										EndIf
									Next
								Next								
								SetBuffer BackBuffer()
								SelectedItem\state2 = 2.0
							Else
								SelectedItem\state2 = Max(0.0, SelectedItem\state2 - FPSfactor)
							EndIf
							DrawImageRect NavBG,xx+80,yy+70,xx+80,yy+70,270,230
							Color 30,30,30
							Rect xx+80,yy+70,270,230,False
							
							x = opt\GraphicWidth - SelectedItem\itemtemplate\imgwidth+20
							y = opt\GraphicHeight - SelectedItem\itemtemplate\imgheight-85
							If (MilliSecs() Mod 1000) > 300 Then
								If navType < 310 Then
									Text(x - width/2 + 10, y - height/2 + 10, "WARNING - LOW SIGNAL")
								EndIf
								
								yawvalue = EntityYaw(Collider)-90
								x1 = x+Cos(yawvalue)*6 : y1 = y-Sin(yawvalue)*6
								x2 = x+Cos(yawvalue-140)*5 : y2 = y-Sin(yawvalue-140)*5				
								x3 = x+Cos(yawvalue+140)*5 : y3 = y-Sin(yawvalue+140)*5
								
								Line x1,y1,x2,y2
								Line x1,y1,x3,y3
								Line x2,y2,x3,y3
							EndIf
							
							Local SCPs_found% = 0
							
							If navType = 999 And (MilliSecs() Mod 600) < 400 Then
								For np.NPCs = Each NPCs
									If np\NPCtype = NPCtype049 Lor np\NPCtype = NPCtype096 Lor np\NPCtype = NPCtypeOldMan Lor np\NPCtype = NPCtype173 Then
										Local dist# = EntityDistanceSquared(mpl\CameraPivot, np\obj)
										If dist < PowTwo(8.0 * 4) Then
											dist = Sqr(dist)
											If (Not np\HideFromNVG) Then
												Color 100, 0, 0
												Oval(x - dist * 1.5, y - dist * 1.5, dist * 3, dist * 3, False)
												Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), np\NVName)
												SCPs_found% = SCPs_found% + 1
											EndIf
										EndIf
									EndIf
								Next
								If PlayerRoom\RoomTemplate\Name = "cont_895" Then
									If CoffinDistance < 8.0 Then
										dist = Rnd(4.0, 8.0)
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), "SCP-895")
									EndIf
								EndIf
							EndIf
							
							Color (30,30,30)
							If navType < 999 Then
								If SelectedItem\state <= 100 Then
									xtemp = x - width/2 + 196
									ytemp = y - height/2 + 10
									Rect xtemp,ytemp,80,20,False
									
									For i = 1 To Ceil(SelectedItem\state / 10.0)
										DrawImage NavImages[4],xtemp+i*8-6,ytemp+4
									Next
									
									SetFont fo\Font[Font_Digital_Small]
								EndIf
							EndIf
						EndIf
					EndIf
					;[End Block]
				;new Items in SCP:CB 1.3
				Case "badge"
					;[Block]
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						;SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
			End Select
			
		EndIf		
	EndIf
	
	DrawTasks()
	
	RenderCommunicationAndSocialWheel()
	
	CatchErrors("DrawGUI")
End Function

Function DrawGunsInHUD()
	Local isMultiplayer% = (gopt\GameMode = GAMEMODE_MULTIPLAYER)
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
	
	Local pAmmo%, pReloadAmmo%
	If isMultiplayer Then
		pAmmo = Players[mp_I\PlayerID]\Ammo[Players[mp_I\PlayerID]\SelectedSlot]
		pReloadAmmo = Players[mp_I\PlayerID]\ReloadAmmo[Players[mp_I\PlayerID]\SelectedSlot]
	Else
		For g = Each Guns
			If g\ID = g_I\HoldingGun Then
				pAmmo = g\CurrAmmo
				pReloadAmmo = g\CurrReloadAmmo
			EndIf
		Next
	EndIf
	
	For g = Each Guns
		If g\ID = g_I\HoldingGun Then
			If (g\GunType <> GUNTYPE_MELEE) Then
				If pAmmo > 0 Then
					Color 255,255,255
				Else
					Color 255,0,0
				EndIf
				Rect(x - 50 - 1 - 30, y - 1, 30 + 2, 30 + 2, False)
				DrawImage BulletIcon, x - 50 - 30, y
				
				SetFont fo\Font%[Font_Digital_Medium]
				If pAmmo > g\MaxCurrAmmo/5
					Color 0,255,0
				Else
					Color 255,0,0
				EndIf
				TextWithAlign x, y + 5, pAmmo, 2
				Color 0,255,0
				Text x, y + 5, "/"
				width2% = StringWidth("/")
				If pReloadAmmo > 0
					Color 0,255,0
				Else
					Color 255,0,0
				EndIf
				Text x + width2, y + 5, pReloadAmmo
			EndIf
			Exit
		EndIf
	Next
	
	Color 255,255,255
	
	x = 55
	y = 55
	width = 64
	height = 64
	If mpl\SlotsDisplayTimer > 0.0 Then
		For i = 0 To MaxGunSlots-1
			DrawFrame((x-3)+(128*i),y-3,width+6,height+6)
			If g_I\Weapon_CurrSlot = (i + 1) Then
				Color 0,255,0
				Rect (x-3)+(128*i),y-3,width+6,height+6,True
			EndIf
			If g_I\Weapon_InSlot[i] <> "" Then
				For g = Each Guns
					If g\name = g_I\Weapon_InSlot[i] Then
						DrawImage g\IMG,x+(128*i),y
						Color 255,255,255
						If g_I\Weapon_CurrSlot = (i + 1) Then
							SetFont fo\Font[Font_Default]
							Text(x+(width/2)+(128*i),y+height+10,g\DisplayName,True,False)
						EndIf
						Exit
					EndIf
				Next
			EndIf
		Next
	EndIf
	
	Color 255,255,255
	
End Function

Function DrawMenu()
	CatchErrors("Uncaught (DrawMenu)")
	
	Local x%, y%, width%, height%
	If MenuOpen Then
		If KillTimer >= 0 Then
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
			SetFont fo\Font[Font_Default]
			Text 20, opt\GraphicHeight-90, GetLocalString("Menu", "difficulty")+": "+SelectedDifficulty\name
			Text 20, opt\GraphicHeight-70, GetLocalString("Menu", "save_name")+": "+CurrSave\Name
			Text 20, opt\GraphicHeight-50, GetLocalString("Menu", "map_seed")+": "+RandomSeed
			Text 20, opt\GraphicHeight-30, "v"+VersionNumber
			
			RenderMainMenu()
		Else
			;[Block]
			width = ImageWidth(PauseMenuIMG)
			height = ImageHeight(PauseMenuIMG)
			x = viewport_center_x - width / 2
			y = viewport_center_y - height / 2
			
			DrawImage PauseMenuIMG, x, y
			
			Color(255, 255, 255)
			
			x = x+132*MenuScale
			y = y+122*MenuScale
			
			SetFont fo\Font[Font_Menu]
			Text(x, y-(122-45)*MenuScale, Upper(GetLocalString("Menu", "you_died")),False,True)
			SetFont fo\Font[Font_Default]
			
			DrawAllMenuButtons()
			
			y = y+104*MenuScale
			If (Not GameSaved) Lor SelectedDifficulty\permaDeath Then
				Color 50,50,50
				Text(x + 185*MenuScale, y + 30*MenuScale, GetLocalString("Menu", "loadgame"), True, True)
			EndIf
			y= y + 80*MenuScale
			
			SetFont fo\Font[Font_Default]
			Color(255, 255, 255)
			RowText(DeathMSG$, x, y + 80*MenuScale, 390*MenuScale, 600*MenuScale)
			;[End Block]
		EndIf
		
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
;			Text(x, y-(122-45)*MenuScale, GetLocalString("Menu", "achievements",False,True)
;			SetFont fo\Font[Font_Default]
;		ElseIf OptionsMenu > 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "OPTIONS",False,True)
;			SetFont fo\Font[Font_Default]
;		ElseIf QuitMSG > 0 Then
;			SetFont fo\Font[Font_Menu]
;			Text(x, y-(122-45)*MenuScale, "QUIT?",False,True)
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
;			Text x, y, "Difficulty: "+SelectedDifficulty\name
;			Text x, y+20*MenuScale, "Save: "+CurrSave\Name
;			Text x, y+40*MenuScale, "Map seed: "+RandomSeed
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
;						DrawOptionsTooltip("gamma",ScreenGamma)
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
;						DrawOptionsTooltip("musicvol",MusicVolume)
;					EndIf
;					
;					y = y + 30*MenuScale
;					
;					Color 255,255,255
;					Text(x, y, "Sound volume:")
;					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
;						DrawOptionsTooltip("soundvol",PrevSFXVolume)
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
;						DrawOptionsTooltip("mousesensitivity",MouseSens)
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
;						DrawOptionsTooltip("showFPS")
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
;			Local QuitButton% = 60 
;			If SelectedDifficulty\saveType = SAVEONQUIT Lor SelectedDifficulty\saveType = SAVEANYWHERE Then
;				Local RN$ = PlayerRoom\RoomTemplate\Name$
;				Local AbleToSave% = True
;				If RN$ = "173" Lor RN$ = "exit1" Lor RN$ = "gatea" Then AbleToSave = False
;				If (Not CanSave) Then AbleToSave = False
;				If AbleToSave
;					QuitButton = 140
;				EndIf
;			EndIf
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
;			If KillTimer < 0 Then Color(255, 255, 255) : RowText(DeathMSG$, x, y + 80*MenuScale, 390*MenuScale, 600*MenuScale)
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

Function UpdateMenu()
	CatchErrors("Uncaught (UpdateMenu)")
	Local x%, y%, width%, height%
	
	If MenuOpen
		If KillTimer >= 0 Then
			ShowEntity m_I\Sprite
			UpdateMainMenu()
		Else
			;[Block]
			width = ImageWidth(PauseMenuIMG)
			height = ImageHeight(PauseMenuIMG)
			x = viewport_center_x - width / 2
			y = viewport_center_y - height / 2
			
			x = x+132*MenuScale
			y = y+122*MenuScale	
			
			y = y+104*MenuScale
			If GameSaved And (Not SelectedDifficulty\permaDeath) Then
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "loadgame")) Then
					;DrawLoading(0)
					
					DeleteMenuGadgets()
					MenuOpen = False
					LoadGameQuick(SavePath + CurrSave\Name + "\")
					
					MoveMouse viewport_center_x,viewport_center_y
					HidePointer()
					
					KillSounds()
					
					Playable=True
					
					UpdateRooms()
					UpdateDoors()
					
					For r.Rooms = Each Rooms
						x = Abs(EntityX(Collider) - EntityX(r\obj))
						z = Abs(EntityZ(Collider) - EntityZ(r\obj))
						
						If x < 12.0 And z < 12.0 Then
							MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = Max(MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)], 1)
							If x < 4.0 And z < 4.0 Then
								If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
								MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = 1
							EndIf
						End If
					Next
					
					;DrawLoading(100,True)
					PlaySound_Strict LoadTempSound(("SFX\Horror\Horror8.ogg"))
					
					DropSpeed=0
					
					UpdateWorld 0.0
					
					PrevTime = MilliSecs()
					FPSfactor = 0
					
					ResetInput()
					ResumeSounds()
					DeleteTextureEntriesFromCache(0)
					Return
				EndIf
			Else
				DrawButton(x, y, 390*MenuScale, 60*MenuScale, "")
			EndIf
			If DrawButton(x, y + 80*MenuScale, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "quit_to_menu")) Then
				MainMenuOpen = True
				NullGame()
				MenuOpen = False
				MainMenuTab = 0
				CurrSave = Null
				FlushKeys()
				Return
			EndIf
			;[End Block]
		EndIf
		
		;[Block]
;		If ShouldDeleteGadgets
;			DeleteMenuGadgets()
;		EndIf
;		ShouldDeleteGadgets = False
;		
;		If PlayerRoom\RoomTemplate\Name$ <> "exit1" And PlayerRoom\RoomTemplate\Name$ <> "gatea"
;			If StopHidingTimer = 0 Then
;				If Curr173 <> Null And Curr106 <> Null
;					If EntityDistanceSquared(Curr173\Collider, Collider)<PowTwo(4.0) Lor EntityDistanceSquared(Curr106\Collider, Collider)<PowTwo(4.0) Then 
;						StopHidingTimer = 1
;					EndIf
;				EndIf
;			ElseIf StopHidingTimer < 40
;				If KillTimer >= 0 Then 
;					StopHidingTimer = StopHidingTimer+FPSfactor
;					
;					If StopHidingTimer => 40 Then
;						PlaySound_Strict(HorrorSFX[15])
;						Msg = "STOP HIDING"
;						MsgTimer = 6*70
;						MenuOpen = False
;						DeleteMenuGadgets()
;						Return
;					EndIf
;				EndIf
;			EndIf
;		EndIf
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
;					Local SlideBarFOV# = FOV#-40
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
;			If SelectedDifficulty\saveType = SAVEONQUIT Lor SelectedDifficulty\saveType = SAVEANYWHERE Then
;				Local RN$ = PlayerRoom\RoomTemplate\Name$
;				Local AbleToSave% = True
;				If RN$ = "173" Lor RN$ = "exit1" Lor RN$ = "gatea" Then AbleToSave = False
;				If (Not CanSave) Then AbleToSave = False
;				If AbleToSave
;					QuitButton = 140
;					If DrawButton(x, y + 60*MenuScale, 390*MenuScale, 60*MenuScale, "Save & Quit") Then
;						DropSpeed = 0
;						MainMenuOpen = True
;						SaveGame(SavePath + CurrSave\Name + "\")
;						NullGame()
;						MenuOpen = False
;						MainMenuTab = 0
;						CurrSave = Null
;						FlushKeys()
;						Return
;					EndIf
;				EndIf
;			EndIf
;			
;			If DrawButton(x, y + QuitButton*MenuScale, 390*MenuScale, 60*MenuScale, "Quit") Then
;				MainMenuOpen = True
;				NullGame()
;				MenuOpen = False
;				MainMenuTab = 0
;				CurrSave = Null
;				FlushKeys()
;				Return
;			EndIf
;			
;			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, "Back") Then
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
;					If DrawButton(x+341*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "‚?∂", 2) Then
;						AchievementsMenu = AchievementsMenu+1
;						ShouldDeleteGadgets=True
;					EndIf
;				EndIf
;				If AchievementsMenu > 1 Then
;					If DrawButton(x+41*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "‚??", 2) Then
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
;			If KillTimer >= 0 Then	
;				
;				y = y+ 72*MenuScale
;				
;				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Resume", True, True) Then
;					MenuOpen = False
;					ResumeSounds()
;					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
;					DeleteMenuGadgets()
;					Return
;				EndIf
;				
;				y = y + 75*MenuScale
;				If (Not SelectedDifficulty\permaDeath) Then
;					If GameSaved Then
;						If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Load Game") Then
;							DrawLoading(0)
;							
;							MenuOpen = False
;							LoadGameQuick(SavePath + CurrSave\Name + "\")
;							
;							MoveMouse viewport_center_x,viewport_center_y
;							HidePointer ()
;							
;							FlushKeys()
;							FlushMouse()
;							Playable=True
;							
;							UpdateRooms()
;							
;							For r.Rooms = Each Rooms
;								x = Abs(EntityX(Collider) - EntityX(r\obj))
;								z = Abs(EntityZ(Collider) - EntityZ(r\obj))
;								
;								If x < 12.0 And z < 12.0 Then
;									MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = Max(MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)], 1)
;									If x < 4.0 And z < 4.0 Then
;										If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
;										MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = 1
;									EndIf
;								End If
;							Next
;							
;							DrawLoading(100,True)
;							
;							DropSpeed=0
;							
;							UpdateWorld 0.0
;							
;							PrevTime = MilliSecs()
;							FPSfactor = 0
;							
;							ResetInput()
;							Return
;						EndIf
;					Else
;						
;					EndIf
;					y = y + 75*MenuScale
;				EndIf
;				
;				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Achievements")
;					AchievementsMenu = 1
;					ShouldDeleteGadgets = True
;				EndIf
;				y = y + 75*MenuScale
;				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Options")
;					OptionsMenu = 1
;					ShouldDeleteGadgets = True
;				EndIf
;				y = y + 75*MenuScale
;			Else
;				y = y+104*MenuScale
;				If GameSaved And (Not SelectedDifficulty\permaDeath) Then
;					If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Load Game") Then
;						DrawLoading(0)
;						
;						MenuOpen = False
;						LoadGameQuick(SavePath + CurrSave\Name + "\")
;						
;						MoveMouse viewport_center_x,viewport_center_y
;						HidePointer ()
;						
;						FlushKeys()
;						FlushMouse()
;						Playable=True
;						
;						UpdateRooms()
;						
;						For r.Rooms = Each Rooms
;							x = Abs(EntityX(Collider) - EntityX(r\obj))
;							z = Abs(EntityZ(Collider) - EntityZ(r\obj))
;							
;							If x < 12.0 And z < 12.0 Then
;								MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = Max(MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)], 1)
;								If x < 4.0 And z < 4.0 Then
;									If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
;									MapFound[Floor(EntityX(r\obj) / 8.0) * MapWidth + Floor(EntityZ(r\obj) / 8.0)] = 1
;								EndIf
;							End If
;						Next
;						
;						DrawLoading(100,True)
;						
;						DropSpeed=0
;						
;						UpdateWorld 0.0
;						
;						PrevTime = MilliSecs()
;						FPSfactor = 0
;						
;						ResetInput()
;						Return
;					EndIf
;				Else
;					DrawButton(x, y, 390*MenuScale, 60*MenuScale, "")
;				EndIf
;				If DrawButton(x, y + 80*MenuScale, 390*MenuScale, 60*MenuScale, "Quit to Menu") Then
;					MainMenuOpen = True
;					NullGame()
;					MenuOpen = False
;					MainMenuTab = 0
;					CurrSave = Null
;					FlushKeys()
;					Return
;				EndIf
;				y= y + 80*MenuScale
;			EndIf
;			
;			If KillTimer >= 0 And (Not MainMenuOpen)
;				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "Quit") Then
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

Function MouseOn%(x%, y%, width%, height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + height Then
			Return True
		End If
	End If
	Return False
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D