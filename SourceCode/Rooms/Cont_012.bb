
Function FillRoom_Cont_012(r.Rooms)
	Local d.Doors,it.Items,de.Decals
	Local tex%
	Local i
	
	d.Doors = CreateDoor(r\zone, r\x + 264.0 * RoomScale, 0.0, r\z + 672.0 * RoomScale, 270, r, False, False, 3)
	PositionEntity(d\buttons[0], r\x + 224.0 * RoomScale, EntityY(d\buttons[0],True), r\z + 540.0 * RoomScale, True)
	PositionEntity(d\buttons[1], r\x + 304.0 * RoomScale, EntityY(d\buttons[1],True), r\z + 840.0 * RoomScale, True)	
	TurnEntity d\buttons[1],0,0,0,True
	
	r\RoomDoors[0] = CreateDoor(r\zone, r\x -512.0 * RoomScale, -768.0*RoomScale, r\z -336.0 * RoomScale, 0, r, False, False)
	r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\open = False : r\RoomDoors[0]\locked = True
	PositionEntity(r\RoomDoors[0]\buttons[0], r\x + 176.0 * RoomScale, -512.0*RoomScale, r\z - 364.0 * RoomScale, True)
	r\RoomDoors[0]\buttons[1] = FreeEntity_Strict(r\RoomDoors[0]\buttons[1])
	
	r\Levers[0] = CreateLever(r, r\x + 240.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 364 * RoomScale)
	
	r\Objects[2] = LoadRMesh("GFX\map\rooms\cont_012\room012_2_opt.rmesh",Null)
	ScaleEntity r\Objects[2], RoomScale, RoomScale, RoomScale
	PositionEntity(r\Objects[2], r\x - 360 * RoomScale, - 130 * RoomScale, r\z + 456.0 * RoomScale, 0)
	EntityParent(r\Objects[2], r\obj)
	
	r\Objects[3] = CreateSprite()
	PositionEntity(r\Objects[3], r\x - 43.5 * RoomScale, - 574 * RoomScale, r\z - 362.0 * RoomScale)
	ScaleSprite(r\Objects[3], 0.015, 0.015)
	EntityTexture(r\Objects[3], LightSpriteTex[1])
	EntityBlend (r\Objects[3], 3)
	EntityParent(r\Objects[3], r\obj)
	HideEntity r\Objects[3]
	
	r\Objects[4] = LoadMesh_Strict("GFX\map\rooms\cont_012\room012_3.b3d")
	tex=LoadTexture_Strict("GFX\map\rooms\cont_012\scp-012_0.jpg")
	EntityTexture r\Objects[4],tex, 0,1
	ScaleEntity r\Objects[4], RoomScale, RoomScale, RoomScale
	PositionEntity(r\Objects[4], r\x - 360 * RoomScale, - 130 * RoomScale, r\z + 456.0 * RoomScale, 0)
	EntityParent(r\Objects[4], r\Objects[2])
	
	it = CreateItem("Document SCP-012", "paper", r\x - 56.0 * RoomScale, r\y - 576.0 * RoomScale, r\z - 408.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	it.Items = CreateItem("Severed Hand", "hand", r\x - 784*RoomScale, -576*RoomScale+0.3, r\z+640*RoomScale)
	EntityParent(it\collider, r\obj)
	
	de.Decals = CreateDecal(DECAL_BLOODSPLAT2,  r\x - 784*RoomScale, -768*RoomScale+0.01, r\z+640*RoomScale,90,Rnd(360),0)
	de\Size = 0.5
	ScaleSprite(de\obj, de\Size,de\Size)
	EntityParent de\obj, r\obj
	
End Function

Function UpdateEvent_Cont_012(e.Events)
	Local de.Decals
	Local pvt%,dist#,tex%,angle#
	
	If PlayerRoom = e\room Then
		
		If e\EventState=0 Then
			If EntityDistanceSquared(Collider, e\room\RoomDoors[0]\obj)<PowTwo(2.5) And RemoteDoorOn Then
				GiveAchievement(Achv012)
				PlaySound_Strict HorrorSFX[7]
				PlaySound2 (LeverSFX,Camera,e\room\RoomDoors[0]\obj) 
				e\EventState=1
				e\room\RoomDoors[0]\locked = False
				UseDoor(e\room\RoomDoors[0],False)
				e\room\RoomDoors[0]\locked = True
			EndIf
		Else
			
			If e\Sound=0 Then LoadEventSound(e,"SFX\Music\012Golgotha.ogg")
			e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\Objects[3], 5.0)
			
			If e\Sound2=0 Then LoadEventSound(e,"SFX\Music\012.ogg",1)
			
			If e\EventState<90 Then e\EventState=CurveValue(90,e\EventState,500)
			PositionEntity e\room\Objects[2], EntityX(e\room\Objects[2],True),(-130-448*Sin(e\EventState))*RoomScale,EntityZ(e\room\Objects[2],True),True
			
			If e\EventState2 > 0 And e\EventState2 < 200 Then
				e\EventState2 = e\EventState2 + FPSfactor
				RotateEntity(e\room\Levers[0]\obj, CurveValue(85, EntityPitch(e\room\Levers[0]\obj), 5), EntityYaw(e\room\Levers[0]\obj), 0)
			Else
				e\EventState2 = e\EventState2 + FPSfactor
				If e\EventState2<250 Then
					ShowEntity e\room\Objects[3] 
				Else
					HideEntity e\room\Objects[3] 
					If e\EventState2>300 Then e\EventState2=200
				EndIf
			EndIf
			
			If Wearing714=False And WearingGasMask<3 And WearingHazmat<3 Then
				If EntityVisible(e\room\Objects[2],Camera) Then 							
					;012 not visible, walk to the door
					
					e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\Objects[3], 10, e\EventState3/(86.0*70.0))
					
					pvt% = CreatePivot()
					PositionEntity pvt, EntityX(Camera), EntityY(e\room\Objects[2],True)-0.05, EntityZ(Camera)
					PointEntity(pvt, e\room\Objects[2])
					RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(pvt), EntityYaw(Collider), 80-(e\EventState3/200.0)), 0)
					
					TurnEntity(pvt, 90, 0, 0)
					user_camera_pitch = CurveAngle(EntityPitch(pvt)+25, user_camera_pitch + 90.0, 80-(e\EventState3/200.0))
					user_camera_pitch=user_camera_pitch-90
					
					dist = Distance(EntityX(Collider),EntityX(e\room\Objects[2],True),EntityZ(Collider),EntityZ(e\room\Objects[2],True))
					
					HeartBeatRate = 150
					HeartBeatVolume = Max(3.0-dist,0.0)/3.0
					BlurVolume = Max((2.0-dist)*(e\EventState3/800.0)*(Sin(Float(MilliSecs()) / 20.0 + 1.0)),BlurVolume)
					CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs()) / 20.0)+1.0)*8.0*Max((3.0-dist),0.0))
					
					If BreathCHN <> 0 Then
						If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
					EndIf
					
					If dist < 0.6 Then
						e\EventState3=Min(e\EventState3+FPSfactor,86*70)
						If e\EventState3>70 And e\EventState3-FPSfactor=<70 Then
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech1.ogg")
						ElseIf e\EventState3>13*70 And e\EventState3-FPSfactor=<13*70
							Msg=GetLocalString("Singleplayer", "cont_012_1")
							MsgTimer = 7*70
							;Injuries=Injuries+0.5
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech2.ogg")
						ElseIf e\EventState3>31*70 And e\EventState3-FPSfactor=<31*70
							tex = LoadTexture_Strict("GFX\map\rooms\cont_012\scp-012_1.jpg",1,0)
							EntityTexture (e\room\Objects[4], tex,0,1)
							DeleteSingleTextureEntryFromCache tex
							
							Msg=GetLocalString("Singleplayer", "cont_012_2")
							MsgTimer = 7*70
							;Injuries=Max(Injuries,1.5)
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech"+Rand(3,4)+".ogg")
						ElseIf e\EventState3>49*70 And e\EventState3-FPSfactor=<49*70
							Msg=GetLocalString("Singleplayer", "cont_012_3")
							MsgTimer = 8*70
							;Injuries=Injuries+0.3
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech5.ogg")
						ElseIf e\EventState3>63*70 And e\EventState3-FPSfactor=<63*70
							tex = LoadTexture_Strict("GFX\map\rooms\cont_012\scp-012_2.jpg",1,0)
							EntityTexture (e\room\Objects[4], tex,0,1)	
							DeleteSingleTextureEntryFromCache tex
							
							;Injuries=Injuries+0.5
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech6.ogg")
						ElseIf e\EventState3>74*70 And e\EventState3-FPSfactor=<74*70
							tex = LoadTexture_Strict("GFX\map\rooms\cont_012\scp-012_3.jpg",1,0)
							EntityTexture (e\room\Objects[4], tex,0,1)
							DeleteSingleTextureEntryFromCache tex
							
							Msg=GetLocalString("Singleplayer", "cont_012_4")
							MsgTimer = 7*70
							;Injuries=Injuries+0.8
							PlaySound_Strict LoadTempSound("SFX\SCP\012\Speech7.ogg")
							Crouch = True
							
							de.Decals = CreateDecal(DECAL_BLOODPOOL,  EntityX(Collider), -768*RoomScale+0.01, EntityZ(Collider),90,Rnd(360),0)
							de\Size = 0.1 : de\MaxSize = 0.45 : de\SizeChange = 0.0002 : UpdateDecals()
						ElseIf e\EventState3>85*70 And e\EventState3-FPSfactor=<85*70
							DeathMSG = GetLocalStringR("Singleplayer", "cont_012_death", Designation)
							Kill()
						EndIf
						
						RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Collider)+Sin(e\EventState3*(e\EventState3/2000))*(e\EventState3/300), EntityYaw(Collider), 80), 0)
						
					Else
						angle = WrapAngle(EntityYaw(pvt)-EntityYaw(Collider))
						If angle<40.0 Then
							ForceMove = (40.0-angle)*0.02
						ElseIf angle > 310.0
							ForceMove = (40.0-Abs(360.0-angle))*0.02
						EndIf
					EndIf								
					
					pvt = FreeEntity_Strict(pvt)								
				Else
					
					If (DistanceSquared(EntityX(Collider), EntityX(e\room\RoomDoors[0]\frameobj), EntityZ(Collider), EntityZ(e\room\RoomDoors[0]\frameobj))<PowTwo(4.5)) And  EntityY(Collider)<-2.5 Then
						pvt% = CreatePivot()
						PositionEntity pvt, EntityX(Camera), EntityY(Collider), EntityZ(Camera)
						PointEntity(pvt, e\room\RoomDoors[0]\frameobj)
						user_camera_pitch = CurveAngle(90, user_camera_pitch+90, 100)
						user_camera_pitch=user_camera_pitch-90
						RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(pvt), EntityYaw(Collider), 150), 0)
						
						angle = WrapAngle(EntityYaw(pvt)-EntityYaw(Collider))
						If angle<40.0 Then
							ForceMove = (40.0-angle)*0.008
						ElseIf angle > 310.0
							ForceMove = (40.0-Abs(360.0-angle))*0.008
						EndIf
						pvt = FreeEntity_Strict(pvt)
						
					EndIf
					DebugLog Distance(EntityX(Collider), EntityX(e\room\RoomDoors[0]\frameobj), EntityZ(Collider), EntityZ(e\room\RoomDoors[0]\frameobj))
					DebugLog EntityY(Collider)
				EndIf
				
			EndIf
			
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D