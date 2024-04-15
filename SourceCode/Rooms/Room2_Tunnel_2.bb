
Function FillRoom_Room2_Tunnel_2(r.Rooms)
	
End Function

Function UpdateEvent_Room2_Tunnel_2(e.Events)
	
	Select e\EventState
		Case 0 ; Wait for player to come in
			If PlayerRoom = e\room Then
				If (Curr173 <> Null And Curr173\Idle = SCP173_ACTIVE) Then
					If DistanceSquared(EntityX(Collider), EntityX(e\room\obj), EntityZ(Collider), EntityZ(e\room\obj)) < 12.0 Then
						PlaySound_Strict(LightSFX)
						LoadEventSound(e,"SFX\SCP\173\VentRun.ogg")
						e\EventState = 1 ; Begin event
					EndIf
				EndIf
			EndIf
		Case 1 ; The lights go off
			If (Not ChannelPlaying(e\SoundCHN)) Then
				e\SoundCHN = PlaySound_Strict(e\Sound)
			EndIf
			UpdateSoundOrigin(e\SoundCHN, Camera, e\room\obj, 10, 2.0)
			e\EventState2 = Min(e\EventState2 + FPSfactor, 200)
			If e\EventState2 >= 50 Then
				BlinkTimer = -10
			Else
				LightBlink = Rnd(0.0,1.0)*(e\EventState2/50)
			EndIf
			If e\EventState2 >= 200 Then
				e\EventState = 2
			EndIf
		Case 2 ; Teleport 173 to the room
			StopChannel(e\SoundCHN)
			PlaySound_Strict(LoadTempSound("SFX\ambient\general\ambient3.ogg"))
			PositionEntity(Curr173\Collider, EntityX(e\room\obj), 0.6, EntityZ(e\room\obj))
			ResetEntity(Curr173\Collider)					
			Curr173\Idle = SCP173_STATIONARY
			e\EventState = 3
		Case 3 ; Bring back the lights
			BlinkTimer = -10
			PointEntity(Collider,Curr173\Collider)
			e\EventState2 = Max(e\EventState2 - FPSfactor*2, 0)
			If e\EventState2 <= 0 Then
				LightBlink = 1.0
				BlinkTimer = BLINKFREQ
				Curr173\Idle = SCP173_ACTIVE
				RemoveEvent(e)
			EndIf
	End Select
	
End Function

Function UpdateEvent_Room2_Tunnel_2_Smoke(e.Events)
	Local em.Emitters
	Local i%
	
	If PlayerRoom = e\room Then
		If e\room\dist < 3.5 Then
			PlaySound_Strict(BurstSFX) 
			For i = -1 To 1 Step 2
				em.Emitters = CreateEmitter(EntityX(e\room\obj,True) + ((512.0 * RoomScale) * i) * (e\room\angle Mod 180 = 90), 544.0 * RoomScale, EntityZ(e\room\obj,True) + ((512.0 * RoomScale) * i) * (e\room\angle Mod 180 = 0), 0)
				TurnEntity(em\Obj, 90, 0, 0, True)
				EntityParent(em\Obj, e\room\obj)
				em\Size = 0.05
				em\RandAngle = 10
				em\Speed = 0.06
				em\SizeChange = 0.007
			Next
			RemoveEvent(e)
		EndIf					
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1#5#35
;~C#Blitz3D