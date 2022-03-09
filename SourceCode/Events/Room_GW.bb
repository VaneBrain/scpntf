
Function UpdateEvent_Room_GW(e.Events)
	Local p.Particles
	Local pvt%
	Local i
	
	;e\EventState: Determines if the airlock is in operation or not
	;e\EventState2: The timer for the airlocks
	;e\EventState3: Checks if the player had left the airlock or not
	
	e\room\RoomDoors[0]\locked = True
	e\room\RoomDoors[1]\locked = True
	
	Local brokendoor% = False
	If e\room\Objects[1]<>0 Then brokendoor% = True
	
	If PlayerRoom = e\room
		If e\EventState = 0.0
			If EntityDistanceSquared(e\room\Objects[0],Collider)<PowTwo(1.4) And e\EventState3 = 0.0
				e\EventState = 1.0
				If brokendoor
					If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
					e\Sound2 = LoadSound_Strict("SFX\Door\DoorSparks.ogg")
					e\SoundCHN2 = PlaySound2(e\Sound2,Camera,e\room\Objects[1],5)
				EndIf
				StopChannel e\SoundCHN
				e\SoundCHN = 0
				If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
				e\Sound = LoadSound_Strict("SFX\Door\Airlock.ogg")
				e\room\RoomDoors[0]\locked = False
				e\room\RoomDoors[1]\locked = False
				UseDoor(e\room\RoomDoors[0])
				UseDoor(e\room\RoomDoors[1])
				PlaySound_Strict(AlarmSFX[3])
			ElseIf EntityDistanceSquared(e\room\Objects[0],Collider)>PowTwo(2.4)
				e\EventState3 = 0.0
			EndIf
		Else
			If e\EventState2 < 70*7
				e\EventState2 = e\EventState2 + FPSfactor
				e\room\RoomDoors[0]\open = False
				e\room\RoomDoors[1]\open = False
				If e\EventState2 < 70*1
					
					If brokendoor
						pvt% = CreatePivot()
						Local d_ent% = e\room\Objects[1]
						PositionEntity(pvt, EntityX(d_ent%,True), EntityY(d_ent%,True)+Rnd(0.0,0.05), EntityZ(d_ent%,True))
						RotateEntity(pvt, 0, EntityYaw(d_ent%,True)+90, 0)
						MoveEntity pvt,0,0,0.2
						
						If ParticleAmount > 0
							For i = 0 To (1+(2*(ParticleAmount-1)))
								p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 7, 0.002, 0, 25)
								p\speed = Rnd(0.01,0.05)
								RotateEntity(p\pvt, Rnd(-45,0), EntityYaw(pvt)+Rnd(-10.0,10.0), 0)
								
								p\size = 0.0075
								ScaleSprite p\obj,p\size,p\size
								
								p\Achange = -0.05
							Next
						EndIf
						
						pvt = FreeEntity_Strict(pvt)
					EndIf
					
				ElseIf e\EventState2 > 70*3 And e\EventState < 70*5.5
					pvt% = CreatePivot(e\room\obj)								
					For i = 0 To 1
						If e\room\RoomTemplate\Name$ = "room3_gw"
							If i = 0
								PositionEntity pvt%,-288.0,416.0,320.0,False
							Else
								PositionEntity pvt%,192.0,416.0,320.0,False
							EndIf
						Else
							If i = 0
								PositionEntity pvt%,312.0,416.0,-128.0,False
							Else
								PositionEntity pvt%,312.0,416.0,224.0,False
							EndIf
						EndIf
						
						p.Particles = CreateParticle(EntityX(pvt,True), EntityY(pvt,True), EntityZ(pvt,True),  6, 0.8, 0, 50)
						p\speed = 0.025
						RotateEntity(p\pvt, 90, 0, 0)
						
						p\Achange = -0.02
					Next
					
					pvt = FreeEntity_Strict(pvt)
					If e\SoundCHN = 0 Then e\SoundCHN = PlaySound2(e\Sound,Camera,e\room\Objects[0],5)
				EndIf
			Else
				e\EventState = 0.0
				e\EventState2 = 0.0
				e\EventState3 = 1.0
				If e\room\RoomDoors[0]\open = False
					e\room\RoomDoors[0]\locked = False
					e\room\RoomDoors[1]\locked = False
					UseDoor(e\room\RoomDoors[0])
					UseDoor(e\room\RoomDoors[1])
				EndIf
			EndIf
		EndIf
		
		If brokendoor
			If ChannelPlaying(e\SoundCHN2)
				UpdateSoundOrigin(e\SoundCHN2,Camera,e\room\Objects[1],5)
			EndIf
		EndIf
		If ChannelPlaying(e\SoundCHN)
			UpdateSoundOrigin(e\SoundCHN,Camera,e\room\Objects[0],5)
		EndIf
	Else
		e\EventState3 = 0.0
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D