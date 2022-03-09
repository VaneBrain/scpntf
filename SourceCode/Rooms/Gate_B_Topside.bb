
Function FillRoom_Gate_B_Topside(r.Rooms)
	Local d.Doors
	
	r\Objects[0] = CreatePivot(r\obj)
	PositionEntity(r\Objects[0], r\x+4356.0*RoomScale, 9767.0*RoomScale, r\z+2588.0*RoomScale, True)
	
	r\RoomDoors[4] = CreateDoor(r\zone, r\x, 0, r\z - 320.0 * RoomScale, 0, r, False, True, 5)
	r\RoomDoors[4]\dir = 1 : r\RoomDoors[4]\AutoClose = False : r\RoomDoors[4]\open = False
	PositionEntity(r\RoomDoors[4]\buttons[1], r\x+352*RoomScale, 0.7, r\z-528*RoomScale, True)
	RotateEntity r\RoomDoors[4]\buttons[1],0,r\angle-90,0,True
	PositionEntity(r\RoomDoors[4]\buttons[0], r\x, 7.0, r\z, True)		
	
	r\Objects[3] = CreatePivot()
	PositionEntity(r\Objects[3], r\x-7680.0*RoomScale, 10992.0*RoomScale, r\z-27048.0*RoomScale, True)
	EntityParent r\Objects[3], r\obj
	
	
	r\Objects[4] = CreatePivot()
	PositionEntity(r\Objects[4], r\x+5203.36*RoomScale, 12128.0*RoomScale, r\z-1739.19*RoomScale, True)
	EntityParent r\Objects[4], r\obj
	
	r\Objects[5] = CreatePivot()
	PositionEntity(r\Objects[5], r\x+4363.02*RoomScale, 10536.0*RoomScale, r\z+2766.16*RoomScale, True)
	EntityParent r\Objects[5], r\obj	
	
	r\Objects[6] = CreatePivot()
	PositionEntity(r\Objects[6], r\x+5192.0*RoomScale, 12192.0*RoomScale, r\z-1760.0*RoomScale, True)
	EntityParent r\Objects[6], r\obj
	
	r\Objects[7] = CreatePivot()
	PositionEntity(r\Objects[7], r\x+5192.0*RoomScale, 12192.0*RoomScale, r\z-4352.0*RoomScale, True)
	EntityParent r\Objects[7], r\obj
	
	r\RoomDoors[0] = CreateDoor(0, r\x+720.0*RoomScale, 0, r\z+1432.0*RoomScale, 0, r, True)
	r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\open = True
	MoveEntity r\RoomDoors[0]\buttons[0],0,0,22.0*RoomScale
	MoveEntity r\RoomDoors[0]\buttons[1],0,0,22.0*RoomScale	
	r\Objects[8] = CreatePivot()
	PositionEntity(r\Objects[8], r\x+720.0*RoomScale, 0, r\z+1744.0*RoomScale, True)
	EntityParent r\Objects[8], r\obj
	
	r\RoomDoors[1] = CreateDoor(0, r\x-5424.0*RoomScale, 10784.0*RoomScale, r\z-1380.0*RoomScale, 0, r, False)
	r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\open = False
	MoveEntity r\RoomDoors[1]\buttons[0],0,0,22.0*RoomScale
	MoveEntity r\RoomDoors[1]\buttons[1],0,0,22.0*RoomScale			
	r\Objects[9] = CreatePivot()
	PositionEntity(r\Objects[9], r\x-5424.0*RoomScale, 10784.0*RoomScale, r\z-1068.0*RoomScale, True)
	EntityParent r\Objects[9], r\obj		
	
	r\RoomDoors[2] = CreateDoor(0, r\x+4352.0*RoomScale, 10784.0*RoomScale, r\z-492.0*RoomScale, 0, r, False)
	r\RoomDoors[2]\AutoClose = False : r\RoomDoors[2]\open = False	
	
	r\RoomDoors[3] = CreateDoor(0, r\x+4352.0*RoomScale, 10784.0*RoomScale, r\z+500.0*RoomScale, 0, r, False)
	r\RoomDoors[3]\AutoClose = False : r\RoomDoors[3]\open = False	
	
	;walkway
	r\Objects[10] = CreatePivot()
	PositionEntity(r\Objects[10], r\x+4352.0*RoomScale, 10778.0*RoomScale, r\z+1344.0*RoomScale, True)
	EntityParent r\Objects[10], r\obj	
	
	;"682"
	r\Objects[11] = CreatePivot()
	PositionEntity(r\Objects[11], r\x+2816.0*RoomScale, 11024.0*RoomScale, r\z-2816.0*RoomScale, True)
	EntityParent r\Objects[11], r\obj
	
	r\RoomDoors[5] = CreateDoor(0, r\x+3248.0*RoomScale, 9856.0*RoomScale, r\z+6400.0*RoomScale, 0, r, False, False, 0, "ABCD")
	r\RoomDoors[5]\AutoClose = False : r\RoomDoors[5]\open = False		
	
	d.Doors = CreateDoor(0, r\x+3072.0*RoomScale, 9856.0*RoomScale, r\z+5800.0*RoomScale, 90, r, False, False, 3)
	d\AutoClose = False : d\open = False
	
	r\Objects[14] = CreatePivot()
	PositionEntity(r\Objects[14], r\x+3536.0*RoomScale, 10256.0*RoomScale, r\z+5512.0*RoomScale, True)
	EntityParent r\Objects[14], r\obj
	r\Objects[15] = CreatePivot()
	PositionEntity(r\Objects[15], r\x+3536.0*RoomScale, 10256.0*RoomScale, r\z+5824.0*RoomScale, True)
	EntityParent r\Objects[15], r\obj			
	r\Objects[16] = CreatePivot()
	PositionEntity(r\Objects[16], r\x+3856.0*RoomScale, 10256.0*RoomScale, r\z+5512.0*RoomScale, True)
	EntityParent r\Objects[16], r\obj
	r\Objects[17] = CreatePivot()
	PositionEntity(r\Objects[17], r\x+3856.0*RoomScale, 10256.0*RoomScale, r\z+5824.0*RoomScale, True)
	EntityParent r\Objects[17], r\obj
	
	;MTF:n spawnpoint
	r\Objects[18] = CreatePivot()
	PositionEntity(r\Objects[18], r\x+3250.0*RoomScale, 9896.0*RoomScale, r\z+6623.0*RoomScale, True)
	EntityParent r\Objects[18], r\obj
	
	r\Objects[19] = CreatePivot()
	PositionEntity(r\Objects[19], r\x+3808.0*RoomScale, 12320.0*RoomScale, r\z-13568.0*RoomScale, True)
	EntityParent r\Objects[19], r\obj
	
End Function

Function UpdateEvent_Gate_B_Topside(e.Events)
	Local n.NPCs,p.Particles
	Local pvt%,angle#
	Local i
	
	If PlayerRoom = e\room Then
		
		If EntityY(Collider)>1040.0*RoomScale Then
			
			ShowEntity e\room\obj
			
			If e\EventState = 0 Then
				DrawLoading(0,True)
				
				For i = 0 To MaxRoomLights-1
					If e\room\LightSprites[i]<>0 Then 
						EntityFX e\room\LightSprites[i], 1+8
					EndIf
				Next
				
				For n.NPCs = Each NPCs
					RemoveNPC(n)
				Next
				
				CameraFogMode(Camera, 0)
				SecondaryLightOn = True
				
				DrawLoading(60,True)
				DrawLoading(90,True)
				
				
				e\room\NPC[1] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[4],True),EntityY(e\room\Objects[4],True)+0.2,EntityZ(e\room\Objects[4],True))
				e\room\NPC[1]\State = 0
				e\room\NPC[1]\State2 = 10
				
				e\room\NPC[2] = CreateNPC(NPCtypeD9341, EntityX(e\room\Objects[20],True),EntityY(e\room\Objects[20],True)+0.2,EntityZ(e\room\Objects[20],True))
				e\room\NPC[2]\State = 0
				
				pvt = CreatePivot()
				PositionEntity pvt, EntityX(e\room\Objects[0],True), EntityY(e\room\Objects[0],True), EntityZ(e\room\Objects[0],True)
				
				e\room\Objects[0] = LoadMesh_Strict("GFX\map\rooms\gate_b_topside\exit1terrain.b3d", e\room\obj)
				ScaleEntity e\room\Objects[0],RoomScale,RoomScale,RoomScale,True
				RotateEntity e\room\Objects[0],0,e\room\angle,0,True
				PositionEntity(e\room\Objects[0], EntityX(pvt), EntityY(pvt), EntityZ(pvt), True)
				
				pvt = FreeEntity_Strict(pvt)
				
				Delay 100
				
				Sky = sky_CreateSky("GFX\map\sky\sky")
				RotateEntity Sky,0,e\room\angle-90,0
				
				e\EventState = 1.0
				
				For n.NPCs = Each NPCs
					If n\NPCtype = NPCtypeMTF
						RemoveNPC(n)
					EndIf
				Next
				
				DrawLoading(100,True)
			Else
				PositionEntity(Collider, EntityX(e\room\Objects[18],True),EntityY(e\room\Objects[18],True),EntityZ(e\room\Objects[18],True))
				
				UpdateSky(Sky)
				
				If e\EventState = 0 Then
					If SelectedEnding = "" Then
						PlayAnnouncement("SFX\Ending\GateB\AlphaWarheadsFail.ogg")
						
						DebugLog "MTF Units spawned!"
						
						e\EventState = 85.0*70
						
						SelectedEnding = "B3"
						
					Else
						If SelectedEnding = "B3" Then
							If e\room\NPC[2]\IsDead = False Then
								PointEntity2(e\room\NPC[2]\Collider,Collider,0,False)
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			
			HideEntity Fog
			CameraFogRange Camera, 5,45
			
			angle = Max(Sin(EntityYaw(Collider)),0.0)
			CameraFogColor (Camera,200+(angle*50),200+(angle*30),200)
			CameraClsColor (Camera,200+(angle*50),200+(angle*30),200)					
			CameraRange(Camera, 0.01, 60)
			
			AmbientLight (140, 140, 140)
			
			If ParticleAmount > 0
				If Rand(3) = 1 Then
					p.Particles = CreateParticle(EntityX(Camera)+Rnd(-2.0,2.0), EntityY(Camera)+Rnd(0.9,2.0), EntityZ(Camera)+Rnd(-2.0,2.0), 2, 0.006, 0, 300)
					p\speed = Rnd(0.002,0.003)
					RotateEntity(p\pvt, Rnd(-20, 20), e\room\angle-90+Rnd(-15,15),0, 0)
					
					p\SizeChange = -0.00001
				EndIf
			EndIf
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D