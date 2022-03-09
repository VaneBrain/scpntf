;SCP-096 Constant
;[Block]
Const SCP096_SIT = 0
Const SCP096_GETUP = 1
Const SCP096_LOOP = 2
Const SCP096_STARTRAGE = 3
Const SCP096_CHASE = 4
Const SCP096_WALKING = 5
;[End Block]

Function CreateNPCtype096(n.NPCs)
	Local temp#, headBone
	
	n\NVName = "SCP-096"
	n\Collider = CreatePivot()
	EntityRadius n\Collider, 0.3
	EntityType n\Collider, HIT_PLAYER
	n\obj = LoadAnimMesh_Strict("GFX\npcs\scp096.b3d")
	
	n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-096", "speed") / 100.0)
	
	temp = (GetINIFloat("DATA\NPCs.ini", "SCP-096", "scale") / 3.0)
	ScaleEntity n\obj, temp, temp, temp	
	
	MeshCullBox (n\obj, -MeshWidth(n\obj)*2, -MeshHeight(n\obj)*2, -MeshDepth(n\obj)*2, MeshWidth(n\obj)*2, MeshHeight(n\obj)*4, MeshDepth(n\obj)*4)
	
	n\CollRadius = 0.3
End Function

Function UpdateNPCtype096(n.NPCs)
	Local i%, dist#, dist2#, angle%, pvt
	Local de.Decals
	
	dist = EntityDistanceSquared(Collider, n\Collider)
	
	Select n\State
		Case SCP096_SIT
			;[Block]
			If dist<PowTwo(8.0) Then
				GiveAchievement(Achv096)
				If n\SoundChn = 0
					n\SoundChn = StreamSound_Strict("SFX\Music\096.ogg",0)
					n\SoundChn_IsStream = True
				EndIf
				
				If n\State3 = -1
					AnimateNPC(n,936,1263,0.1,False)
					If n\Frame=>1262.9
						n\State = 5
						n\State3 = 0
						n\Frame = 312
					EndIf
				Else
					AnimateNPC(n,936,1263,0.1)
					If n\State3 < 70*6
						n\State3=n\State3+FPSfactor
					Else
						If Rand(1,5)=1
							n\State3 = -1
						Else
							n\State3=70*(Rand(0,3))
						EndIf
					EndIf
				EndIf
				
				angle = WrapAngle(DeltaYaw(n\Collider, Collider))
				
				If (Not NoTarget)
					If angle<90 Lor angle>270 Then
						CameraProject Camera,EntityX(n\Collider), EntityY(n\Collider)+0.25, EntityZ(n\Collider)
						
						If ProjectedX()>0 And ProjectedX()<opt\GraphicWidth Then
							If ProjectedY()>0 And ProjectedY()<opt\GraphicHeight Then
								If EntityVisible(Collider, n\Collider) Then
									If (BlinkTimer < - 16 Lor BlinkTimer > - 6)
										PlaySound_Strict LoadTempSound("SFX\SCP\096\Triggered.ogg")
										
										CurrCameraZoom = 10
										
										n\Frame = 194
										StopStream_Strict(n\SoundChn) : n\SoundChn=0
										n\Sound = 0
										n\State = 1
										n\State3 = 0
									EndIf
								EndIf									
							EndIf
						EndIf								
						
					EndIf
				EndIf
			EndIf
			If n\SoundChn <> 0
				UpdateStreamSoundOrigin(n\SoundChn,Camera,n\Collider,8.0,1.0)
			EndIf
			;[End Block]
		Case SCP096_CHASE
			;[Block]
			CurrCameraZoom = CurveValue(Max(CurrCameraZoom, (Sin(Float(MilliSecs())/20.0)+1.0) * 10.0),CurrCameraZoom,8.0)
			
			If n\Target = Null Then 
				If n\SoundChn = 0
					n\SoundChn = StreamSound_Strict("SFX\SCP\096\Scream.ogg",0)
					n\SoundChn_IsStream = True
				Else
					UpdateStreamSoundOrigin(n\SoundChn,Camera,n\Collider,7.5,1.0)
				EndIf
				
				If n\SoundChn2 = 0
					n\SoundChn2 = StreamSound_Strict("SFX\Music\096Chase.ogg",0)
					n\SoundChn2_IsStream = 2
				Else
					SetStreamVolume_Strict(n\SoundChn2,Min(Max(8.0-Sqr(dist),0.6),1.0)*(opt\VoiceVol*opt\MasterVol))
				EndIf
			EndIf
			
			If NoTarget And n\Target = Null Then n\State = 5
			
			If KillTimer =>0 Then
				
				If MilliSecs() > n\State3 Then
					n\LastSeen=0
					If n\Target=Null Then
						If EntityVisible(Collider, n\Collider) Then n\LastSeen=1
					Else
						If EntityVisible(n\Target\Collider, n\Collider) Then n\LastSeen=1
					EndIf
					n\State3=MilliSecs()+3000
				EndIf
				
				If n\LastSeen=1 Then
					n\PathTimer=Max(70*3, n\PathTimer)
					n\PathStatus=0
					
					If n\Target<> Null Then dist = EntityDistanceSquared(n\Target\Collider, n\Collider)
					
					If dist < PowTwo(2.8) Lor n\Frame<150 Then 
						If n\Frame>193 Then n\Frame = 2.0 ;go to the start of the jump animation
						
						AnimateNPC(n, 2, 193, 0.7)
						
						If dist > PowTwo(1.0) Then 
							n\CurrSpeed = CurveValue(n\Speed*2.0,n\CurrSpeed,15.0)
						Else
							n\CurrSpeed = 0
							
							If n\Target=Null Then
								If (Not GodMode) Then 
									PlaySound_Strict DamageSFX[4]
									
									pvt = CreatePivot()
									CameraShake = 30
									BlurTimer = 2000
									DeathMSG = "A large amount of blood found in [DATA REDACTED]. DNA indentified as "+Designation+". Most likely [Data REDACTED] by SCP-096."
									Kill()
									KillAnim = 1
									For i = 0 To 6
										PositionEntity pvt, EntityX(Collider)+Rnd(-0.1,0.1),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.1,0.1)
										TurnEntity pvt, 90, 0, 0
										EntityPick(pvt,0.3)
										
										de.Decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BLOODDROP), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
										de\Size = Rnd(0.2,0.6) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
									Next
									pvt = FreeEntity_Strict(pvt)
								EndIf
							EndIf				
						EndIf
						
						If n\Target=Null Then
							PointEntity n\Collider, Collider
						Else
							PointEntity n\Collider, n\Target\Collider
						EndIf
						
					Else
						If n\Target=Null Then 
							PointEntity n\obj, Collider
						Else
							PointEntity n\obj, n\Target\Collider
						EndIf
						
						RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 5.0), 0
						
						If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						
						If n\Frame<906 Then ;1058
							AnimateNPC(n,737,906,n\Speed*8,False)
						Else
							AnimateNPC(n,907,935,n\CurrSpeed*8)
						EndIf
					EndIf
					
					RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True
					MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
					
				Else
					If n\PathStatus = 1 Then
						
						If n\Path[n\PathLocation]=Null Then 
							If n\PathLocation > 19 Then 
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							PointEntity n\obj, n\Path[n\PathLocation]\obj
							
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 5.0), 0
							
							If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed*1.5,n\CurrSpeed,15.0)
							MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
							
							If n\Frame<906 Then ;1058
								AnimateNPC(n,737,906,n\Speed*8,False)
							Else
								AnimateNPC(n,907,935,n\CurrSpeed*8)
							EndIf
							
							dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
							If dist2 < PowTwo(0.4) Then
								If n\Path[n\PathLocation]\door <> Null Then
									If n\Path[n\PathLocation]\door\open = False Then
										n\Path[n\PathLocation]\door\open = True
										n\Path[n\PathLocation]\door\fastopen = 1
										PlaySound2(OpenDoorFastSFX, Camera, n\Path[n\PathLocation]\door\obj)
									EndIf
								EndIf							
								If dist2 < PowTwo(0.2) Then n\PathLocation = n\PathLocation + 1
							EndIf 
						EndIf
						
					Else
						AnimateNPC(n,737,822,0.2)
						
						n\PathTimer = Max(0, n\PathTimer-FPSfactor)
						If n\PathTimer=<0 Then
							If n\Target<>Null Then
								n\PathStatus = FindPath(n, EntityX(n\Target\Collider),EntityY(n\Target\Collider)+0.2,EntityZ(n\Target\Collider))	
							Else
								n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider))	
							EndIf
							n\PathTimer = 70*5
						EndIf
					EndIf
				EndIf
				
				If dist > PowTwo(32.0) Lor EntityY(n\Collider)<-50 Then
					If Rand(50)=1 Then TeleportCloser(n)
				EndIf
			Else
				AnimateNPC(n, Min(27,AnimTime(n\obj)), 193, 0.5)
			EndIf
			
			
			;[End Block]
		Case SCP096_GETUP, SCP096_LOOP, SCP096_STARTRAGE
			;[Block]
			If n\SoundChn = 0
				n\SoundChn = StreamSound_Strict("SFX\Music\096Angered.ogg",0)
				n\SoundChn_IsStream = True
			Else
				UpdateStreamSoundOrigin(n\SoundChn,Camera,n\Collider,10.0,opt\VoiceVol*opt\MasterVol)
			EndIf
			
			If n\State=1 Then ; get up
				If n\Frame<312
					AnimateNPC(n,193,311,0.3,False)
					If n\Frame > 310.9 Then n\State = 2 : n\Frame = 737
				ElseIf n\Frame>=312 And n\Frame<=422
					AnimateNPC(n,312,422,0.3,False)
					If n\Frame > 421.9 Then n\Frame = 677
				Else
					AnimateNPC(n,677,736,0.3,False)
					If n\Frame > 735.9 Then n\State = 2 : n\Frame = 737
				EndIf
			ElseIf n\State=2
				AnimateNPC(n,737,822,0.3,False)
				If n\Frame=>822 Then n\State=3 : n\State2=0
			ElseIf n\State=3
				n\State2 = n\State2+FPSfactor
				If n\State2 > 70*18 Then
					AnimateNPC(n,823,847,n\Speed*8,False)
					If n\Frame>846.9 Then
						n\State = 4
						StopStream_Strict(n\SoundChn) : n\SoundChn=0
					EndIf
				Else
					AnimateNPC(n,737,822,0.3)
				EndIf
			EndIf
			;[End Block]
		Case SCP096_WALKING
			;[Block]
			If dist < PowTwo(16.0) Then 
				
				If dist < PowTwo(4.0) Then
					GiveAchievement(Achv096)
				EndIf
				
				If n\SoundChn = 0
					n\SoundChn = StreamSound_Strict("SFX\Music\096.ogg",0)
					n\SoundChn_IsStream = True
				Else
					UpdateStreamSoundOrigin(n\SoundChn,Camera,n\Collider,14.0,opt\VoiceVol*opt\MasterVol)
				EndIf
				
				If n\Frame>=422
					n\State2=n\State2+FPSfactor
					If n\State2>1000 Then
						If n\State2>1600 Then n\State2=Rand(0,500)
						
						If n\Frame<1382 Then
							n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
							AnimateNPC(n,1369,1382,n\CurrSpeed*45,False)
						Else
							n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
							AnimateNPC(n,1383,1456,n\CurrSpeed*45)
						EndIf
						
						If MilliSecs() > n\State3 Then
							n\LastSeen=0
							If EntityVisible(Collider, n\Collider) Then 
								n\LastSeen=1
							Else
								HideEntity n\Collider
								EntityPick(n\Collider, 1.5)
								If PickedEntity() <> 0 Then
									n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
								EndIf
								ShowEntity n\Collider
							EndIf
							n\State3=MilliSecs()+3000
						EndIf
						
						If n\LastSeen Then 
							PointEntity n\obj, Collider
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),130.0),0
							If dist < PowTwo(1.5) Then n\State2=0
						Else
							RotateEntity n\Collider, 0, CurveAngle(n\Angle,EntityYaw(n\Collider),50.0),0
						EndIf
					Else
						If n\Frame>472 Then
							n\CurrSpeed = CurveValue(n\Speed*0.05,n\CurrSpeed,8.0)
							AnimateNPC(n,1383,1469,n\CurrSpeed*45,False)
							If n\Frame=>1468.9 Then n\Frame=423
						Else
							n\CurrSpeed = CurveValue(0,n\CurrSpeed,4.0)	
							AnimateNPC(n,423,471,0.2)
						EndIf
					EndIf
					
					MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
				Else
					AnimateNPC(n,312,422,0.3,False)
				EndIf
				
				angle = WrapAngle(DeltaYaw(n\Collider, Camera))
				If (Not NoTarget) And n\ContainmentState<=0 Then
					If angle<55 Lor angle>360-55 Then
						CameraProject Camera,EntityX(n\Collider), EntityY(Collider)+5.8*0.2-0.25, EntityZ(n\Collider)
						
						If ProjectedX()>0 And ProjectedX()<opt\GraphicWidth Then
							If ProjectedY()>0 And ProjectedY()<opt\GraphicHeight Then
								If EntityVisible(Collider, n\Collider) Then
									If (BlinkTimer < - 16 Lor BlinkTimer > - 6)
										PlaySound_Strict LoadTempSound("SFX\SCP\096\Triggered.ogg")
										
										CurrCameraZoom = 10
										
										If n\Frame >= 422
											n\Frame = 677
										EndIf
										StopStream_Strict(n\SoundChn) : n\SoundChn=0
										n\Sound = 0
										n\State = 2
									EndIf
								EndIf									
							EndIf
						EndIf
						
					EndIf
				EndIf
			EndIf
			;[End Block]
	End Select
	
	PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.07, EntityZ(n\Collider))
	
	RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider), 0
End Function
;~IDEal Editor Parameters:
;~F#1#25#101#125
;~C#Blitz3D