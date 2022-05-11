
;SCP-1048a multiplayer constants
;[Block]
Const MP1048a_STATE_FREEZE = -1
Const MP1048a_STATE_WANDER = 0
Const MP1048a_STATE_DETECTED = 1
Const MP1048a_STATE_ATTACK = 2
;[End Block]

Function CreateNPCtype1048aMP(n.NPCs)
	
	n\CollRadius = 0.15
	n\Collider = CreatePivot()
	EntityRadius n\Collider, n\CollRadius, 0.15
	EntityType n\Collider, HIT_NPC_MP
	
	n\obj = CopyEntity(mp_I\SCP1048aModel)
	
	Local temp# = 0.05
	ScaleEntity n\obj, temp, temp, temp	
	
	n\Speed = (2.0 / 150.0)
	n\HP = 60
	n\PathTimer = 70*5
	n\GravityMult = 0.5
	n\NVName = "SCP-1048-A"
	
	CopyHitBoxes(n)
	
	If n\Sound = 0 Then
		n\Sound = LoadSound_Strict("SFX\SCP\1048A\Squishing.ogg")
	EndIf
	
End Function

Function UpdateNPCtype1048aMP(n.NPCs)
	
	Local p.Particles, cmsg.ChatMSG
	Local dist#,prevFrame#,dist2#,distSqr#
	Local i,j
	
	prevFrame = n\Frame
	
	If (Not n\IsDead)
		Select n\State
			Case MP1048a_STATE_FREEZE
				;[Block]
				;do nothing
				;[End Block]
			Case MP1048a_STATE_WANDER ;Wandering around
				;[Block]
				If n\PathStatus=1 Then
					While n\Path[n\PathLocation]=Null
						If n\PathLocation >= 19
							n\PathLocation = 0 : n\PathStatus = 0 : Exit
						Else
							n\PathLocation = n\PathLocation + 1
						EndIf
					Wend
					If n\PathStatus=1 Then
						PointEntity n\obj, n\Path[n\PathLocation]\obj
						If mp_I\PlayState = GAME_SERVER Then RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
						
						AnimateNPC(n,250,278,n\CurrSpeed*40)
						n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
						
						dist = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
						If dist < 0.36 Then
							n\PathLocation = n\PathLocation + 1
						EndIf
					EndIf
				Else
					If n\PathTimer <= 0.0 Then
						n\EnemyX = EntityX(Players[n\ClosestPlayer]\Collider)
						n\EnemyY = EntityY(Players[n\ClosestPlayer]\Collider)
						n\EnemyZ = EntityZ(Players[n\ClosestPlayer]\Collider)
						n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY,n\EnemyZ)
						
						If n\PathStatus = 1 Then
							If n\Path[1]<>Null Then
								If n\Path[2]=Null And EntityDistanceSquared(n\Path[1]\obj,n\Collider)<0.16 Then
									n\PathLocation = 0
									n\PathStatus = 0
								EndIf
							EndIf
							If n\Path[0]<>Null And n\Path[1]=Null Then
								n\PathLocation = 0
								n\PathStatus = 0
							EndIf
						EndIf
						
						If n\PathStatus<>1 Then
							n\PathTimer = 70*5
						EndIf
					Else
						n\PathTimer = n\PathTimer - FPSfactor
						
						PointEntity n\obj, Players[n\ClosestPlayer]\Collider
						If mp_I\PlayState = GAME_SERVER Then RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
						
						AnimateNPC(n,250,278,n\CurrSpeed*40)
						n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
					EndIf
				EndIf
				
				If n\DistanceTimer <= 0.0 Then
					If EntityDistanceSquared(n\obj,Players[n\ClosestPlayer]\Collider)<56.25 Then
						If EntityVisible(n\obj,Players[n\ClosestPlayer]\Collider) Then
							n\State = MP1048a_STATE_DETECTED
						EndIf
					EndIf
					n\DistanceTimer = NPCDistanceCheckTime
				Else
					n\DistanceTimer = n\DistanceTimer - FPSfactor
				EndIf
				
				If mp_I\Gamemode\EnemyCount <= MinEnemyLeft Then
					If n\BlinkTimer <= 0.0 Then
						CreateOverHereParticle(EntityX(n\Collider),EntityY(n\Collider)+0.1,EntityZ(n\Collider))
						n\BlinkTimer = 70*5
					Else
						n\BlinkTimer = n\BlinkTimer - FPSfactor
					EndIf
				EndIf
				;[End Block]
			Case MP1048a_STATE_DETECTED ;Player detected
				;[Block]
				PointEntity n\obj, Players[n\ClosestPlayer]\Collider
				If mp_I\PlayState = GAME_SERVER Then RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
				
				AnimateNPC(n,250,278,n\CurrSpeed*40)
				n\CurrSpeed = CurveValue(n\Speed*1.1, n\CurrSpeed, 20.0)
				MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
				
				dist = EntityDistanceSquared(n\obj,Players[n\ClosestPlayer]\Collider)
				If dist<0.5625 Then
					If (Abs(DeltaYaw(n\Collider,Players[n\ClosestPlayer]\Collider))<=60.0)
						n\State = MP1048a_STATE_ATTACK
						SetNPCFrame(n, 1)
					EndIf
				EndIf
				
				If n\DistanceTimer <= 0.0 Then
					If dist>56.25 Lor (Not EntityVisible(n\obj,Players[n\ClosestPlayer]\Collider)) Then
						n\State = MP1048a_STATE_WANDER
						n\PathTimer = 0.0
						n\PathStatus = 0
					EndIf
					n\DistanceTimer = NPCDistanceCheckTime
				Else
					n\DistanceTimer = n\DistanceTimer - FPSfactor
				EndIf
				;[End Block]
			Case MP1048a_STATE_ATTACK ;Attacking
				;[Block]
				AnimateNPC(n, 1, 201, 0.5, False)
				If prevFrame < 4.0 And n\Frame >= 4.0 Then
					n\Sound = LoadSound_Strict("SFX\SCP\1048A\Shriek.ogg")
					n\SoundChn = PlaySound2(n\Sound,Camera,n\Collider,20)
				EndIf
				If n\Frame > 6.0 Then
					dist = EntityDistanceSquared(n\Collider,Players[mp_I\PlayerID]\Collider)
					If (dist<16.0) Then
						distSqr = (dist^0.5)
						CameraShake = Max(8-(2*distSqr),0)
						Local nextBlurTimer = Max(1200-(300*distSqr),0)
						If nextBlurTimer > BlurTimer Then
							BlurTimer = nextBlurTimer
						EndIf
					EndIf
					If n\LastSeen = 0.0 Then
						If mp_I\PlayState = GAME_SERVER Then
							For i = 0 To (mp_I\MaxPlayers-1)
								If Players[i]<>Null Then
									dist2 = EntityDistanceSquared(Players[i]\Collider,n\Collider)
									If dist2 < 16.0 Then
										If Players[i]\CurrHP > 0 Then
											Players[i]\CurrHP = Max(Players[i]\CurrHP - 0.5, 0)
											If Players[i]\CurrHP <= 0 Then
												cmsg = AddChatMSG("death_killedby", 0, SERVER_MSG_IS, CHATMSG_TYPE_TWOPARAM_TRANSLATE)
												cmsg\Msg[1] = Players[n\ClosestPlayer]\Name
												cmsg\Msg[2] = n\NVName
												Players[i]\Deaths = Players[i]\Deaths + 1
											EndIf
										EndIf
									EndIf
								EndIf
							Next
						EndIf
						n\LastSeen = 7
					Else
						n\LastSeen = Max(n\LastSeen-FPSfactor,0.0)
					EndIf
				EndIf
				If n\Frame => 200.5 And mp_I\PlayState = GAME_SERVER Then
					n\IsDead = True
				EndIf
				;[End Block]
		End Select
		
		UpdateSoundOrigin(n\SoundChn, Camera, n\Collider, 20)
		
		If n\HP<=0 Then
			n\IsDead=True
			EntityType n\Collider,HIT_DEAD
		EndIf
		If n\State <> MP1048a_STATE_ATTACK Then
			n\SoundChn = LoopSound2(n\Sound,n\SoundChn,Camera,n\Collider,5)
		EndIf	
	Else
		If n\SoundChn <> 0
			StopChannel n\SoundChn
			n\SoundChn = 0
			FreeSound_Strict n\Sound
			n\Sound = 0
		EndIf
		PlaySound2(LoadTempSound("SFX\SCP\1048A\Explode.ogg"),Camera,n\Collider)
		p.Particles = CreateParticle(EntityX(n\Collider),EntityY(n\Collider)+0.2,EntityZ(n\Collider),5,0.25,0.0)
		EntityColor p\obj,100,100,100
		RotateEntity p\pvt,0,0,Rnd(360)
		p\Achange = -Rnd(0.02,0.03)
		For i = 0 To 1
			p.Particles = CreateParticle(EntityX(n\Collider)+Rnd(-0.2,0.2),EntityY(n\Collider)+0.25,EntityZ(n\Collider)+Rnd(-0.2,0.2),5,0.15,0.0)
			RotateEntity p\pvt,0,0,Rnd(360)
			EntityColor p\obj,100,100,100
			p\Achange = -Rnd(0.02,0.03)
		Next
		RemoveNPC(n)
		Return
	EndIf
	
	PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-n\CollRadius, EntityZ(n\Collider))
	RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), EntityRoll(n\Collider), True
	
	EntityAutoFade(n\obj,GetCameraFogRangeFar(Camera)-0.5,GetCameraFogRangeFar(Camera)+0.5)
	
End Function







;~IDEal Editor Parameters:
;~F#2#9#22#A7
;~C#Blitz3D