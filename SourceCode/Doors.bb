
Const DOOR_DEFAULT = 0
Const DOOR_CONTAINMENT = 1
Const DOOR_HCZ = 2
Const DOOR_ELEVATOR = 3
Const DOOR_914 = 4
Const DOOR_ELEVATOR_3FLOOR = 5
Const DOOR_WINDOWED = 6

Const MaxBigDoorOBJ = 2
Const MaxHeavyDoorOBJ = 2
Const FirstDoor = 0
Const SecondDoor = 1

Const MaxButtonTypes = 5
Const BUTTON_NORMAL = 0
Const BUTTON_KEYCARD = 1
Const BUTTON_KEYPAD = 2
Const BUTTON_SCANNER = 3
Const BUTTON_ELEVATOR_3FLOOR = 4

Type DoorInstance
	Field ClosestButton%
	Field ClosestDoor.Doors
	Field SelectedDoor.Doors
	Field UpdateDoorsTimer#
	Field DoorTempID%
	Field DoorOBJ%
	Field DoorframeOBJ%
	Field DoorColl%
	Field BigDoorOBJ%[MaxBigDoorOBJ]
	Field HeavyDoorOBJ%[MaxHeavyDoorOBJ]
	Field ButtonOBJ%[MaxButtonTypes]
	Field ButtonTexture%
	Field ButtonTextureLocked%
End Type

Function LoadDoors()
	d_I.DoorInstance = New DoorInstance
	
	d_I\DoorOBJ = LoadMesh_Strict("GFX\map\doors\door01.x")
	HideEntity d_I\DoorOBJ
	d_I\DoorframeOBJ = LoadMesh_Strict("GFX\map\doors\doorframe.x")
	HideEntity d_I\DoorframeOBJ
	
	d_I\HeavyDoorOBJ[FirstDoor] = LoadMesh_Strict("GFX\map\doors\heavydoor1.x")
	HideEntity d_I\HeavyDoorOBJ[FirstDoor]
	d_I\HeavyDoorOBJ[SecondDoor] = LoadMesh_Strict("GFX\map\doors\heavydoor2.x")
	HideEntity d_I\HeavyDoorOBJ[SecondDoor]
	
	d_I\DoorColl = LoadMesh_Strict("GFX\map\doors\doorcoll.x")
	HideEntity d_I\DoorColl
	
	d_I\ButtonOBJ[BUTTON_NORMAL] = LoadMesh_Strict("GFX\map\buttons\Button.b3d")
	HideEntity d_I\ButtonOBJ[BUTTON_NORMAL]
	d_I\ButtonOBJ[BUTTON_KEYCARD] = LoadMesh_Strict("GFX\map\buttons\ButtonKeycard.b3d")
	HideEntity d_I\ButtonOBJ[BUTTON_KEYCARD]
	d_I\ButtonOBJ[BUTTON_KEYPAD] = LoadMesh_Strict("GFX\map\buttons\ButtonCode.b3d")
	HideEntity d_I\ButtonOBJ[BUTTON_KEYPAD]	
	d_I\ButtonOBJ[BUTTON_SCANNER] = LoadMesh_Strict("GFX\map\buttons\ButtonScanner.b3d")
	HideEntity d_I\ButtonOBJ[BUTTON_SCANNER]
	d_I\ButtonOBJ[BUTTON_ELEVATOR_3FLOOR] = LoadMesh_Strict("GFX\map\elevatorbutton.b3d")
	HideEntity d_I\ButtonOBJ[BUTTON_ELEVATOR_3FLOOR]
	
	d_I\BigDoorOBJ[FirstDoor] = LoadMesh_Strict("GFX\map\doors\ContDoorLeft.x")
	HideEntity d_I\BigDoorOBJ[FirstDoor]
	d_I\BigDoorOBJ[SecondDoor] = LoadMesh_Strict("GFX\map\doors\ContDoorRight.x")
	HideEntity d_I\BigDoorOBJ[SecondDoor]
	
	d_I\ButtonTexture = LoadTextureCheckingIfInCache("GFX\map\textures\KeyPad.jpg", True)
	d_I\ButtonTextureLocked = LoadTextureCheckingIfInCache("GFX\map\textures\KeyPadLocked.jpg", True)
	
End Function

Type Doors
	Field obj%, obj2%, frameobj%, buttons%[2]
	Field locked%, lockedupdated%, open%, angle%, openstate#, fastopen%
	Field dir%
	Field timer%, timerstate#
	Field KeyCard%
	Field room.Rooms
	
	Field DisableWaypoint%
	
	Field dist#
	
	Field SoundCHN%
	
	Field Code$
	
	Field ID%
	
	Field Level%
	Field LevelDest%
	
	Field AutoClose%
	
	Field LinkedDoor.Doors
	
	Field IsElevatorDoor% = False
	
	Field MTFClose% = True
	Field NPCCalledElevator% = False
	
	Field DoorHitOBJ%
End Type

Function CreateDoor.Doors(lvl, x#, y#, z#, angle#, room.Rooms, dopen% = False,  big% = False, keycard% = False, code$="", elevator_type%=1)
	Local d.Doors, parent, i%
	If room <> Null Then parent = room\obj
	Local d2.Doors
	
	d.Doors = New Doors
	If big=DOOR_CONTAINMENT Then
		d\obj = CopyEntity(d_I\BigDoorOBJ[FirstDoor])
		ScaleEntity(d\obj, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		d\obj2 = CopyEntity(d_I\BigDoorOBJ[SecondDoor])
		ScaleEntity(d\obj2, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		
		d\frameobj = CopyEntity(d_I\DoorColl)				
		ScaleEntity(d\frameobj, RoomScale, RoomScale, RoomScale)
		EntityType d\frameobj, HIT_MAP
		EntityAlpha d\frameobj, 0.0
	ElseIf big=DOOR_HCZ Then
		d\obj = CopyEntity(d_I\HeavyDoorOBJ[FirstDoor])
		ScaleEntity(d\obj, RoomScale, RoomScale, RoomScale)
		d\obj2 = CopyEntity(d_I\HeavyDoorOBJ[SecondDoor])
		ScaleEntity(d\obj2, RoomScale, RoomScale, RoomScale)
		
		d\frameobj = CopyEntity(d_I\DoorframeOBJ)
	ElseIf big=DOOR_ELEVATOR Lor big=DOOR_ELEVATOR_3FLOOR Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = DOOR_ELEVATOR Then
				d\obj = CopyEntity(d2\obj)
				d\obj2 = CopyEntity(d2\obj2)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj=0 Then
			d\obj = LoadMesh_Strict("GFX\map\doors\ElevatorDoor.b3d")
			d\obj2 = CopyEntity(d\obj)
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
			ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(d_I\DoorframeOBJ)
	ElseIf big=DOOR_WINDOWED Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = DOOR_WINDOWED Then
				d\obj = CopyEntity(d2\obj)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj = 0 Then
			d\obj = LoadMesh_Strict("GFX\map\doors\WindowedDoor.b3d")
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(d_I\DoorframeOBJ)
	Else
		d\obj = CopyEntity(d_I\DoorOBJ)
		ScaleEntity(d\obj, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
		
		d\frameobj = CopyEntity(d_I\DoorframeOBJ)
		d\obj2 = CopyEntity(d_I\DoorOBJ)
		
		ScaleEntity(d\obj2, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
	End If
	
	PositionEntity d\frameobj, x, y, z	
	ScaleEntity(d\frameobj, RoomScale, RoomScale, RoomScale)
	EntityType d\obj, HIT_MAP
	If d\obj2 <> 0 Then EntityType d\obj2, HIT_MAP
	
	d\ID = d_I\DoorTempID
	d_I\DoorTempID=d_I\DoorTempID+1
	
	d\KeyCard = keycard
	d\Code = code
	
	d\Level = lvl
	d\LevelDest = 66
	
	If big<>DOOR_ELEVATOR_3FLOOR Then
		For i% = 0 To 1
			If code <> "" Then 
				d\buttons[i]= CopyEntity(d_I\ButtonOBJ[BUTTON_KEYPAD])
				EntityFX(d\buttons[i], 1)
			Else
				If keycard>0 Then
					d\buttons[i]= CopyEntity(d_I\ButtonOBJ[BUTTON_KEYCARD])
				ElseIf keycard<0
					d\buttons[i]= CopyEntity(d_I\ButtonOBJ[BUTTON_SCANNER])	
				Else
					d\buttons[i] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL])
				End If
			EndIf
			
			ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
		Next
	Else
		d\buttons[0] = CopyEntity(d_I\ButtonOBJ[BUTTON_ELEVATOR_3FLOOR])
		d\buttons[1] = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL])
		EntityFX d\buttons[0],1
		ScaleEntity d\buttons[0],RoomScale*0.25,RoomScale*0.25,RoomScale*0.25
		ScaleEntity d\buttons[1],0.03,0.03,0.03
	EndIf
	
	If big = DOOR_CONTAINMENT Then
		PositionEntity d\buttons[0], x - 432.0 * RoomScale, y + 0.7, z + 192.0 * RoomScale
		PositionEntity d\buttons[1], x + 432.0 * RoomScale, y + 0.7, z - 192.0 * RoomScale
		RotateEntity d\buttons[0], 0, 90, 0
		RotateEntity d\buttons[1], 0, 270, 0
	ElseIf big = DOOR_ELEVATOR_3FLOOR Then
		PositionEntity d\buttons[0], x + 0.6, y + 0.6, z - 0.13
		PositionEntity d\buttons[1], x - 0.6, y + 0.6, z + 0.1
		RotateEntity d\buttons[1], 0, 180, 0
	Else
		PositionEntity d\buttons[0], x + 0.6, y + 0.7, z - 0.1
		PositionEntity d\buttons[1], x - 0.6, y + 0.7, z + 0.1
		RotateEntity d\buttons[1], 0, 180, 0		
	End If
	EntityParent(d\buttons[0], d\frameobj)
	EntityParent(d\buttons[1], d\frameobj)
	EntityPickMode(d\buttons[0], 2)
	EntityPickMode(d\buttons[1], 2)
	
	PositionEntity d\obj, x, y, z
	
	RotateEntity d\obj, 0, angle, 0
	RotateEntity d\frameobj, 0, angle, 0
	
	If d\obj2 <> 0 Then
		PositionEntity d\obj2, x, y, z
		If big = DOOR_CONTAINMENT Then
			RotateEntity(d\obj2, 0, angle, 0)
		Else
			RotateEntity(d\obj2, 0, angle + 180, 0)
		EndIf
		EntityParent(d\obj2, parent)
	EndIf
	
	EntityParent(d\frameobj, parent)
	EntityParent(d\obj, parent)
	
	d\angle = angle
	d\open = dopen		
	
	EntityPickMode(d\obj, 3)
	MakeCollBox(d\obj)
	If d\obj2 <> 0 Then
		EntityPickMode(d\obj2, 3)
		MakeCollBox(d\obj2)
	End If
	
	EntityPickMode d\frameobj,2
	
	If d\open And big = False And Rand(8) = 1 Then d\AutoClose = True
	d\dir=big
	d\room=room
	
	d\MTFClose = True
	
	Return d
	
End Function

Function CreateButton(x#,y#,z#, pitch#,yaw#,roll#=0)
	Local obj = CopyEntity(d_I\ButtonOBJ[BUTTON_NORMAL])	
	
	ScaleEntity(obj, 0.03, 0.03, 0.03)
	
	PositionEntity obj, x,y,z
	RotateEntity obj, pitch,yaw,roll
	
	EntityPickMode(obj, 2)	
	
	Return obj
End Function

Function UpdateDoors()
	Local i%, d.Doors, x#, z#
	If d_I\UpdateDoorsTimer =< 0 Then
		For d.Doors = Each Doors
			
			d\dist = DistanceSquared(EntityX(Collider),EntityX(d\obj,True),EntityZ(Collider),EntityZ(d\obj,True))
			
			If d\dist > PowTwo(HideDistance*2) And d\IsElevatorDoor = 0 Then
				If d\obj <> 0 Then HideEntity d\obj
				If d\frameobj <> 0 Then HideEntity d\frameobj
				If d\obj2 <> 0 Then HideEntity d\obj2
				If d\buttons[0] <> 0 Then HideEntity d\buttons[0]
				If d\buttons[1] <> 0 Then HideEntity d\buttons[1]				
			Else
				If d\obj <> 0 Then ShowEntity d\obj
				If d\frameobj <> 0 Then ShowEntity d\frameobj
				If d\obj2 <> 0 Then ShowEntity d\obj2
				If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
				If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
			EndIf
		Next
		
		d_I\UpdateDoorsTimer = 30
	Else
		d_I\UpdateDoorsTimer = Max(d_I\UpdateDoorsTimer-FPSfactor,0)
	EndIf
	
	d_I\ClosestButton = 0
	d_I\ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\dist <= PowTwo(HideDistance*2) Lor d\IsElevatorDoor>0 Then ;Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel - ENDSHN
			
			If (d\openstate >= 180 Lor d\openstate <= 0) And GrabbedEntity = 0 Then
				For i% = 0 To 1
					If d\buttons[i] <> 0 Then
						If Abs(EntityX(Collider)-EntityX(d\buttons[i],True)) < 1.0 Then 
							If Abs(EntityZ(Collider)-EntityZ(d\buttons[i],True)) < 1.0 Then 
								Local dist# = DistanceSquared(EntityX(Collider, True), EntityX(d\buttons[i], True), EntityZ(Collider, True), EntityZ(d\buttons[i], True))
								If dist < PowTwo(0.7) Then
									Local temp% = CreatePivot()
									PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
									PointEntity temp,d\buttons[i]
									
									If EntityPick(temp, 0.6) = d\buttons[i] Then
										If d_I\ClosestButton = 0 Then
											d_I\ClosestButton = d\buttons[i]
											d_I\ClosestDoor = d
										Else
											If dist < EntityDistanceSquared(Collider, d_I\ClosestButton) Then d_I\ClosestButton = d\buttons[i] : d_I\ClosestDoor = d
										EndIf							
									EndIf
									
									temp = FreeEntity_Strict(temp)
								EndIf							
							EndIf
						EndIf
						
					EndIf
				Next
			EndIf
			
			If d\open Then
				If d\openstate < 180 Then
					Select d\dir
						Case DOOR_DEFAULT, DOOR_WINDOWED
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen+1) * FPSfactor / 80.0, 0, 0)		
						Case DOOR_CONTAINMENT
							d\openstate = Min(180, d\openstate + FPSfactor * 0.8)
							MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, -Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
						Case DOOR_HCZ
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen+1) * FPSfactor / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 120.0, 0, 0)
						Case DOOR_ELEVATOR,DOOR_ELEVATOR_3FLOOR
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
						Case DOOR_914 ;Used for 914 only
							d\openstate = Min(180, d\openstate + FPSfactor * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 114.0, 0, 0)
					End Select
				Else
					d\fastopen = 0
					ResetEntity(d\obj)
					If d\obj2 <> 0 Then ResetEntity(d\obj2)
					If d\timerstate > 0 Then
						d\timerstate = Max(0, d\timerstate - FPSfactor)
						If d\timerstate + FPSfactor > 110 And d\timerstate <= 110 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\obj)
						Local sound%
						If d\dir = 1 Then sound% = Rand(0, 1) Else sound% = Rand(0, 2)
						If d\timerstate = 0 Then d\open = (Not d\open) : d\SoundCHN = PlaySound2(CloseDoorSFX[d\dir * 3 + sound], Camera, d\obj)
					EndIf
					If d\AutoClose And RemoteDoorOn = True Then
						If EntityDistanceSquared(Camera, d\obj) < PowTwo(2.1) Then
							If (Not Wearing714) Then PlaySound_Strict HorrorSFX[7]
							d\open = False : d\SoundCHN = PlaySound2(CloseDoorSFX[Min(d\dir, 1) * 3 + Rand(0, 2)], Camera, d\obj) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\openstate > 0 Then
					Select d\dir
						Case DOOR_DEFAULT, DOOR_WINDOWED
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 80.0, 0, 0)	
						Case DOOR_CONTAINMENT
							d\openstate = Max(0, d\openstate - FPSfactor*0.8)
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
							If d\openstate < 15 And d\openstate+FPSfactor => 15
								If ParticleAmount=2
									For i = 0 To Rand(75,99)
										Local pvt% = CreatePivot()
										PositionEntity(pvt, EntityX(d\frameobj,True)+Rnd(-0.2,0.2), EntityY(d\frameobj,True)+Rnd(0.0,1.2), EntityZ(d\frameobj,True)+Rnd(-0.2,0.2))
										RotateEntity(pvt, 0, Rnd(360), 0)
										
										Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
										p\speed = 0.005
										RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
										
										p\SizeChange = -0.00001
										p\size = 0.01
										ScaleSprite p\obj,p\size,p\size
										
										p\Achange = -0.01
										
										EntityOrder p\obj,-1
										
										pvt = FreeEntity_Strict(pvt)
									Next
								EndIf
							EndIf
						Case DOOR_HCZ
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 120.0, 0, 0)
						Case DOOR_ELEVATOR,DOOR_ELEVATOR_3FLOOR
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 162.0, 0, 0)
						Case DOOR_914 ;Used for 914 only
							d\openstate = Min(180, d\openstate - FPSfactor * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 114.0, 0, 0)
					End Select
					
					If d\angle = 0 Lor d\angle=180 Then
						If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.15 Then
							If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.7*(d\dir*2+1) Then
								z# = CurveValue(EntityZ(d\frameobj,True)+0.15*Sgn(EntityZ(Collider)-EntityZ(d\frameobj, True)), EntityZ(Collider), 5)
								PositionEntity Collider, EntityX(Collider), EntityY(Collider), z
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.15 Then	
							If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.7*(d\dir*2+1) Then
								x# = CurveValue(EntityX(d\frameobj,True)+0.15*Sgn(EntityX(Collider)-EntityX(d\frameobj, True)), EntityX(Collider), 5)
								PositionEntity Collider, x, EntityY(Collider), EntityZ(Collider)
							EndIf
						EndIf
					EndIf
					
					If d\DoorHitOBJ <> 0 Then
						ShowEntity d\DoorHitOBJ
					EndIf
				Else
					d\fastopen = 0
					PositionEntity(d\obj, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 Then PositionEntity(d\obj2, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 And d\dir = 0 Then
						MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
						MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
					EndIf
					If d\DoorHitOBJ <> 0 Then
						HideEntity d\DoorHitOBJ
					EndIf
				EndIf
			EndIf
			
			If d\locked <> d\lockedupdated Then
				If d\locked Then
					For i% = 0 To 1
						If d\dir <> DOOR_ELEVATOR_3FLOOR Lor i = 1 Then
							If d\IsElevatorDoor > 0 Then
								If d\buttons[i] <> 0 Then EntityTexture(d\buttons[i], d_I\ButtonTexture)
							Else
								If d\buttons[i] <> 0 Then EntityTexture(d\buttons[i], d_I\ButtonTextureLocked)
							EndIf
						EndIf
					Next
				Else
					For i% = 0 To 1
						If d\dir <> DOOR_ELEVATOR_3FLOOR Lor i = 1 Then
							If d\buttons[i] <> 0 Then EntityTexture(d\buttons[i], d_I\ButtonTexture)
						EndIf
					Next
				EndIf
				d\lockedupdated = d\locked
			EndIf
			
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\frameobj)
		
		If d\DoorHitOBJ<>0 Then
			If DebugHUD Then
				EntityAlpha d\DoorHitOBJ,0.5
			Else
				EntityAlpha d\DoorHitOBJ,0.0
			EndIf
		EndIf
	Next
End Function

Function UseDoor(d.Doors, showmsg%=True, playsfx%=True)
	Local temp% = 0
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If showmsg = True Then
				If (Instr(Msg,GetLocalString("Doors", "keycard_inserted"))=0 And (Instr(Msg,GetLocalString("Doors", "keycard_nothing"))=0 And Instr(Msg,GetLocalStringR("Doors", "keycard_required2", d\KeyCard))=0)) Lor (MsgTimer<70*3) Then
					Msg = GetLocalString("Doors", "keycard_required")
					MsgTimer = 70 * 7
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\itemtemplate\tempname
				Case "key1"
					temp = 1
				Case "key2"
					temp = 2
				Case "key3"
					temp = 3
				Case "key4"
					temp = 4
				Case "key5"
					temp = 5
				Case "key6"
					temp = 6
				Case "scp005"
					temp = 7
				Default 
					temp = -1
			End Select
			
			If temp =-1 Then 
				If showmsg = True Then
					If (Instr(Msg,GetLocalString("Doors", "keycard_inserted"))=0 And (Instr(Msg,GetLocalString("Doors", "keycard_nothing"))=0 And Instr(Msg,GetLocalStringR("Doors", "keycard_required2", d\KeyCard))=0)) Lor (MsgTimer<70*3) Then
						Msg = GetLocalString("Doors", "keycard_required")
						MsgTimer = 70 * 7
					EndIf
				EndIf
				Return				
			ElseIf temp >= d\KeyCard 
				If showmsg = True Then
					If d\locked Then
						PlaySound_Strict KeyCardSFX2
						Msg = GetLocalString("Doors", "keycard_nothing")
						MsgTimer = 70 * 7
						Return
					Else
						PlaySound_Strict KeyCardSFX1
						If SelectedItem\itemtemplate\tempname <> "scp005" Then
							Msg = GetLocalString("Doors", "keycard_inserted")
						Else
							Msg = GetLocalString("Doors", "scp005_keycard_granted")
						EndIf
						MsgTimer = 70 * 7	
					EndIf
				EndIf
				SelectedItem = Null
			Else
				SelectedItem = Null
				If showmsg = True Then 
					PlaySound_Strict KeyCardSFX2					
					If d\locked Then
						Msg = GetLocalString("Doors", "keycard_nothing")
					Else
						Msg = GetLocalStringR("Doors", "keycard_required2", d\KeyCard)
					EndIf
					MsgTimer = 70 * 7					
				EndIf
				Return
			EndIf	
		EndIf	
	ElseIf d\KeyCard < 0
        ;I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable - risingstar64
        ;And now we have the capabilities to produce short circuited boolean expressions, oh how far we've come! ~Salvage
        ;If SelectedItem <> Null And ((SelectedItem\itemtemplate\tempname = "hand" And d\KeyCard=-1) Lor (SelectedItem\itemtemplate\tempname = "hand2" And d\KeyCard=-2))
        If SelectedItem <> Null And (SelectedItem\itemtemplate\tempname = "hand" Lor SelectedItem\itemtemplate\tempname = "hand2" Lor SelectedItem\itemtemplate\tempname = "scp005") ;TODO
            PlaySound_Strict ScannerSFX1
			If (Instr(Msg,GetLocalString("Doors", "scanner_denied"))=0) Lor (MsgTimer < 70*3) Then
				If SelectedItem\itemtemplate\tempname <> "scp005" Then
					Msg = GetLocalString("Doors", "scanner_granted")
				Else
					Msg = GetLocalString("Doors", "scp005_scanner_granted")
				EndIf
            EndIf
            MsgTimer = 70 * 10
        Else
            If showmsg = True Then 
                PlaySound_Strict ScannerSFX2
                Msg = GetLocalString("Doors", "scanner_denied")
                MsgTimer = 70 * 10
            EndIf
            Return
        EndIf
        SelectedItem = Null
    Else
		If d\locked Then
			If showmsg = True Then 
				If Not (d\IsElevatorDoor>0) Then
					PlaySound_Strict ButtonSFX2
					If PlayerRoom\RoomTemplate\Name <> "room2_elevator_1" Then
                        If d\open Then
                            Msg = GetLocalString("Doors", "door_nothing")
                        Else    
                            Msg = GetLocalString("Doors", "door_locked")
                        EndIf    
                    Else
                        Msg = GetLocalString("Doors", "elevator_broken")
                    EndIf
					MsgTimer = 70 * 5
				Else
					If d\IsElevatorDoor = 1 Then
						Msg = GetLocalString("Doors", "elevator_called")
						MsgTimer = 70 * 5
					ElseIf d\IsElevatorDoor = 3 Then
						Msg = GetLocalString("Doors", "elevator_on_floor")
						MsgTimer = 70 * 5
					ElseIf (Msg<>GetLocalString("Doors", "elevator_called"))
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
			Return
		EndIf	
	EndIf
	
	OpenCloseDoor(d.Doors, playsfx)
End Function

Function UseDoorNPC(d.Doors, n.NPCs, playsfx%=True)
	If d\locked Lor d\KeyCard > n\Clearance Lor d\KeyCard < 0 Then Return
	OpenCloseDoor(d.Doors, playsfx)
End Function

Function OpenCloseDoor(d.Doors, playsfx%=True)	
	d\open = (Not d\open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\open = (Not d\LinkedDoor\open)
	
	Local sound = 0
	If d\dir = DOOR_CONTAINMENT Then sound=Rand(0, 1) Else sound=Rand(0, 2)
	
	Local dir = d\dir
	If d\dir = DOOR_ELEVATOR_3FLOOR Then
		dir = DOOR_ELEVATOR
	EndIf
	If d\dir = DOOR_WINDOWED Then
		dir = DOOR_DEFAULT
	EndIf	
	
	If playsfx=True Then
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
			d\SoundCHN = PlaySound2 (OpenDoorSFX[dir * 3 + sound], Camera, d\obj)
		Else
			d\SoundCHN = PlaySound2 (CloseDoorSFX[dir * 3 + sound], Camera, d\obj)
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\obj)
	Else
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
		EndIf
	EndIf
	
End Function

Function RemoveDoor(d.Doors)
	Local i%
	
	If d\buttons[0] <> 0 Then EntityParent d\buttons[0], 0
	If d\buttons[1] <> 0 Then EntityParent d\buttons[1], 0	
	
	d\obj = FreeEntity_Strict(d\obj)
	d\obj2 = FreeEntity_Strict(d\obj2)
	d\frameobj = FreeEntity_Strict(d\frameobj)
	For i = 0 To 1
		d\buttons[i] = FreeEntity_Strict(d\buttons[i])
	Next	
	
	Delete d
End Function


;~IDEal Editor Parameters:
;~F#15#25#4A#6B#10C
;~C#Blitz3D