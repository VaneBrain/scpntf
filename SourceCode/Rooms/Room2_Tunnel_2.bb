
Function FillRoom_Room2_Tunnel_2(r.Rooms)
	
	r\Objects[0] = CreatePivot(r\obj)
	PositionEntity(r\Objects[0], r\x, 544.0 * RoomScale, r\z + 512.0 * RoomScale, True)
	
	r\Objects[1] = CreatePivot(r\obj)
	PositionEntity(r\Objects[1], r\x, 544.0 * RoomScale, r\z - 512.0 * RoomScale, True)
	
End Function

Function UpdateEvent_Room2_Tunnel_2(e.Events)
	
	If PlayerRoom = e\room Then
		If Curr173\Idle <> SCP173_ACTIVE Then
			RemoveEvent(e)
			Return
		Else		
			If e\EventState = 0 Then
				If DistanceSquared(EntityX(Collider), EntityX(e\room\obj), EntityZ(Collider), EntityZ(e\room\obj)) < PowTwo(3.5) Then
					PlaySound_Strict(LightSFX)
					
					LightBlink = Rnd(0.0,1.0)*(e\EventState/200)
					e\EventState = 1
				End If
			End If	
		EndIf
	EndIf
	
	If e\EventState > 0 And e\EventState < 200 Then
		BlinkTimer = -10
		If e\EventState > 30 Then 
			LightBlink = 1.0 
			If e\EventState-FPSfactor =< 30 Then 
				PlaySound_Strict LoadTempSound("SFX\ambient\general\ambient3.ogg")
			EndIf
		EndIf
		If e\EventState-FPSfactor =< 100 And e\EventState > 100 Then
			PlaySound_Strict LoadTempSound("SFX\ambient\general\ambient6.ogg")
			PositionEntity(Curr173\Collider, EntityX(e\room\obj), 0.6, EntityZ(e\room\obj))
			ResetEntity(Curr173\Collider)					
			Curr173\Idle = SCP173_STATIONARY
		EndIf
		LightBlink = 1.0
		e\EventState = e\EventState + FPSfactor
	ElseIf e\EventState <> 0 Then
		BlinkTimer = BLINKFREQ
		
		Curr173\Idle = SCP173_ACTIVE
		RemoveEvent(e)
	EndIf
	
End Function

Function UpdateEvent_Room2_Tunnel_2_Smoke(e.Events)
	Local em.Emitters,p.Particles
	Local i,z
	
	If PlayerRoom = e\room Then
		If e\room\dist < 3.5 Then
			PlaySound2(BurstSFX, Camera, e\room\obj) 
			For i = 0 To 1
				em.Emitters = CreateEmitter(EntityX(e\room\Objects[i],True),EntityY(e\room\Objects[i],True), EntityZ(e\room\Objects[i],True),0)
				TurnEntity(em\Obj, 90, 0, 0, True)
				EntityParent(em\Obj, e\room\obj)
				em\Size = 0.05
				em\RandAngle = 10
				em\Speed = 0.06
				em\SizeChange = 0.007
				
				For z = 0 To Ceil(3.3333*(ParticleAmount+1))
					p.Particles = CreateParticle(EntityX(em\Obj, True), 448*RoomScale, EntityZ(em\Obj, True), Rand(em\MinImage, em\MaxImage), em\Size, em\Gravity, em\LifeTime)
					p\speed = em\Speed
					RotateEntity(p\pvt, Rnd(360), Rnd(360), 0, True)
					p\size = 0.05
					p\SizeChange = 0.008
				Next
				
			Next
			RemoveEvent(e)
		EndIf					
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1#36
;~C#Blitz3D