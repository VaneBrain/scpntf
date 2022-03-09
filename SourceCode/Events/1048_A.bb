
Function UpdateEvent_1048_A(e.Events)
	
	If e\room\Objects[0]=0 Then
		If PlayerRoom<>e\room And BlinkTimer<-10 Then
			If (DistanceSquared(EntityX(Collider),EntityX(e\room\obj),EntityZ(Collider),EntityZ(e\room\obj))<PowTwo(16.0)) Then
				e\room\Objects[0] =	LoadAnimMesh_Strict("GFX\npcs\scp-1048a.b3d")
				ScaleEntity e\room\Objects[0], 0.05,0.05,0.05
				SetAnimTime(e\room\Objects[0], 2)
				PositionEntity(e\room\Objects[0], EntityX(e\room\obj), 0.0, EntityZ(e\room\obj))
				
				RotateEntity(e\room\Objects[0], -90.0, Rnd(0.0, 360.0), 0.0)
				
				e\Sound = LoadSound_Strict("SFX\SCP\1048A\Shriek.ogg")
				e\Sound2 = LoadSound_Strict("SFX\SCP\1048A\Growth.ogg")
				
				e\EventState = 1
			EndIf
		EndIf
	Else
		e\EventState3 = 1
		Select e\EventState
			Case 1
				Animate2(e\room\Objects[0], AnimTime(e\room\Objects[0]), 2.0, 395.0, 1.0)
				
				If (EntityDistanceSquared(Collider, e\room\Objects[0])<PowTwo(2.5)) Then e\EventState = 2
			Case 2
				Local prevFrame# = AnimTime(e\room\Objects[0]) 
				Animate2(e\room\Objects[0], prevFrame, 2.0, 647.0, 1.0, False)
				
				If (prevFrame <= 400.0 And AnimTime(e\room\Objects[0])>400.0) Then
					e\SoundCHN = PlaySound_Strict(e\Sound)
				EndIf
				
				Local volume# = Max(1.0 - Abs(prevFrame - 600.0)/100.0, 0.0)
				
				BlurTimer = volume*1000.0
				CameraShake = volume*10.0
				
				PointEntity(e\room\Objects[0], Collider)
				RotateEntity(e\room\Objects[0], -90.0, EntityYaw(e\room\Objects[0]), 0.0)
				
				If (prevFrame>646.0) Then
					If (PlayerRoom = e\room) Then
						e\EventState = 3	
						PlaySound_Strict e\Sound2
						
						Msg = "Something is growing all around your body."
						MsgTimer = 70.0 * 3.0
					Else
						e\EventState3 = 70*30
					EndIf
				EndIf
			Case 3
				e\EventState2 = e\EventState2 + FPSfactor
				
				BlurTimer = e\EventState2*2.0
				
				If (e\EventState2>250.0 And e\EventState2-FPSfactor <= 250.0) Then
					Select Rand(3)
						Case 1
							Msg = "Ears are growing all over your body."
						Case 2
							Msg = "Ear-like organs are growing all over your body."
						Case 3
							Msg = "Ears are growing all over your body. They are crawling on your skin."
					End Select
					
					MsgTimer = 70.0 * 3.0
				Else If (e\EventState2>600.0 And e\EventState2-FPSfactor <= 600.0)
					Select Rand(4)
						Case 1
							Msg = "It is becoming difficult to breathe."
						Case 2
							Msg = "You have excellent hearing now. Also, you are dying."
						Case 3
							Msg = "The ears are growing inside your body."
						Case 4
							Msg = Chr(34)+"Can't... Breathe..."+Chr(34)
					End Select
					
					MsgTimer = 70.0 * 5.0
				EndIf
				
				If (e\EventState2>70*15) Then
					Kill()
					e\EventState = 4
					RemoveEvent(e)
				EndIf
		End Select 
		
		If (e <> Null) Then
			If PlayerRoom <> e\room Then
				If e\EventState3>0 Then
					e\EventState3 = e\EventState3+FPSfactor
					
					If e\EventState3>70*25 Then
						e\room\Objects[0] = FreeEntity_Strict(e\room\Objects[0])
						RemoveEvent(e)
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D