
;Zombie constants
;[Block]
Const Z_STATE_LYING = 0
Const Z_STATE_STANDUP = 1
Const Z_STATE_WANDER = 2
Const Z_STATE_ATTACK = 3
;[End Block]

Function CreateNPCtypeZombie(n.NPCs)
	Local temp#
	
	n\NVName = "Human"
	n\Collider = CreatePivot()
	EntityRadius n\Collider, 0.2
	EntityType n\Collider, HIT_PLAYER
	
	If n\obj = 0 Then 
		n\obj = CopyEntity(NPC0492OBJ)
		
		temp# = (GetINIFloat("DATA\NPCs.ini", "SCP-049-2", "scale") / 2.5)
		ScaleEntity n\obj, temp, temp, temp
		
		MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
	EndIf
	
	n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-049-2", "speed") / 100.0)
	
	SetAnimTime(n\obj, 107)
	
	n\Sound = LoadSound_Strict("SFX\SCP\049\0492Breath.ogg")
	
	n\HP = 100
	
	CopyHitBoxes(n)
	
End Function

Function UpdateNPCtypeZombie(n.NPCs)
	Local prevFrame#
	
	If Abs(EntityY(Collider)-EntityY(n\Collider))<4.0 Then
		
		prevFrame# = n\Frame
		
		If (Not n\IsDead)
			Select n\State
				Case Z_STATE_LYING
					;[Block]
					AnimateNPC(n, 719, 777, 0.2, False)
					
					If n\Frame=777 Then
						If Rand(700)=1 Then
							If EntityDistanceSquared(Collider, n\Collider)<PowTwo(5.0) Then
								n\Frame = 719
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case Z_STATE_STANDUP ;stands up
					;[Block]
					If n\Frame=>682 Then 
						AnimateNPC(n, 926, 935, 0.3, False)
						If n\Frame = 935 Then n\State = Z_STATE_WANDER
					Else
						AnimateNPC(n, 155, 682, 1.5, False)
					EndIf
					;[End Block]
				Case Z_STATE_WANDER ;following the player
					;[Block]
					If n\State3 < 0 Then ;check if the player is visible every three seconds
						If EntityDistanceSquared(Collider, n\Collider) < PowTwo(5.0) Then 
							If EntityVisible(Collider, n\Collider) Then n\State2 = 70*5
						EndIf
						n\State3=70*3
					Else
						n\State3=n\State3-FPSfactor
					EndIf
					
					If n\State2 > 0 And (Not NoTarget) Then ;player is visible -> attack
						n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 6.0, 0.6)
						
						n\PathStatus = 0
						
						PointEntity n\obj, Collider
						RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
						
						If EntityDistanceSquared(Collider, n\Collider) < PowTwo(0.7) Then 
							n\State = Z_STATE_ATTACK
							If Rand(2)=1 Then
								n\Frame = 2
							Else
								n\Frame = 66
							EndIf
						Else
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
							MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
							
							AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
						EndIf
						
						n\State2=n\State2-FPSfactor
					Else
						If n\PathStatus = 1 Then ;path found
							If n\Path[n\PathLocation]=Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity n\obj, n\Path[n\PathLocation]\obj
								
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
								
								AnimateNPC(n, 936, 1017, n\CurrSpeed*60)
								
								If EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj) < PowTwo(0.2) Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						Else ;no path to the player, stands still
							n\CurrSpeed = 0
							AnimateNPC(n, 778, 926, 0.1)
							
							n\PathTimer = n\PathTimer-FPSfactor
							If n\PathTimer =< 0 Then
								n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider)+0.1,EntityZ(Collider))
								n\PathTimer = n\PathTimer+70*5
							EndIf
						EndIf
					EndIf
					
					;65, 80, 93, 109, 123
					If n\CurrSpeed > 0.005 Then
						If (prevFrame < 977 And n\Frame=>977) Lor (prevFrame > 1010 And n\Frame<940) Then
							PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
						EndIf
					EndIf
					;[End Block]
				Case Z_STATE_ATTACK
					;[Block]
					If NoTarget Then n\State = Z_STATE_WANDER
					If n\Frame < 66 Then
						AnimateNPC(n, 2, 65, 0.7, False)
						If prevFrame < 23 And n\Frame=>23 Then
							If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.1)
								If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
									PlaySound_Strict DamageSFX[Rand(5,8)]
									;Injuries = Injuries+Rnd(0.4,1.0)
									DamageSPPlayer(Rnd(10.0,20.0))
									DeathMSG = Designation+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
								EndIf
							EndIf
						ElseIf n\Frame=65 Then
							n\State = Z_STATE_WANDER
						EndIf							
					Else
						AnimateNPC(n, 66, 132, 0.7, False)
						If prevFrame < 90 And n\Frame=>90 Then
							If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.1)
								If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
									PlaySound_Strict DamageSFX[Rand(5,8)]
									;Injuries = Injuries+Rnd(0.4,1.0)
									DamageSPPlayer(Rnd(10.0,20.0))
									DeathMSG = Designation+". Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
								EndIf
							EndIf
						ElseIf n\Frame=132 Then
							n\State = Z_STATE_WANDER
						EndIf		
					EndIf
					;[End Block]
			End Select
		Else
			If n\SoundChn <> 0
				StopChannel n\SoundChn
				n\SoundChn = 0
				FreeSound_Strict n\Sound
				n\Sound = 0
			EndIf
			AnimateNPC(n, 133, 157, 0.5, False)
		EndIf
		
		If n\HP <= 0 Then
			n\IsDead = True
			If n\Frame > 157 Then
				SetNPCFrame(n, 133)
			EndIf
		EndIf
		
		PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
		
		RotateEntity n\obj, -90, EntityYaw(n\Collider), 0
	EndIf
	
End Function






;~IDEal Editor Parameters:
;~F#2#9#30#3C#45
;~C#Blitz3D