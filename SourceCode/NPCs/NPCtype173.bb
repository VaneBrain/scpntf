
;SCP-173 constants
;[Block]
Const SCP173_ACTIVE = 0
Const SCP173_STATIONARY = 1
Const SCP173_CONTAIN = 2
Const SCP173_DISABLED = 3
;Containment States
Const SCP173_BOXED = 3
Const SCP173_NOMOVE = 2
;[End Block]

Function CreateNPCtype173(n.NPCs)
	Local temp#
	Local cheat.Cheats = First Cheats
	
	n\NVName = "SCP-173"
	n\Collider = CreatePivot()
	EntityRadius n\Collider, 0.23, 0.32
	EntityType n\Collider, HIT_PLAYER
	n\Gravity = True
	
	n\obj = LoadMesh_Strict("GFX\npcs\173\173body.b3d")
	n\obj2 = LoadMesh_Strict("GFX\npcs\173\173head.b3d")
	
	temp# = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "scale") / MeshDepth(n\obj))
	ScaleEntity n\obj, temp,temp,temp
	ScaleEntity n\obj2, temp,temp,temp
	
	;On Halloween set jack-o-latern texture.
	If (Left(CurrentDate(), 7) = "31 Oct ") Then
		HalloweenTex = True
		Local texFestive = LoadTexture_Strict("GFX\npcs\173\173h.pt", 1)
		EntityTexture n\obj, texFestive
		EntityTexture n\obj2, texFestive
		DeleteSingleTextureEntryFromCache texFestive
	;ENDSHN's birthday and seed
	ElseIf (Left(CurrentDate(), 7) = "11 Nov ") And RandomSeed = "ENDSHN" Then
		Local scaleX# = EntityScaleX(n\obj)
		Local scaleY# = EntityScaleY(n\obj)
		Local scaleZ# = EntityScaleZ(n\obj)
		cheat\OwO = True
		n\obj = FreeEntity_Strict(n\obj)
		n\obj = LoadAnimMesh_Strict("GFX\npcs\173\173body_owo.b3d")
		Animate n\obj, 1, 0.5
		n\obj2 = FreeEntity_Strict(n\obj2)
		n\obj2 = LoadAnimMesh_Strict("GFX\npcs\173\173head_owo.b3d")
		Animate n\obj2, 1, 0.5
		ScaleEntity n\obj, scaleX,scaleY,scaleZ
		ScaleEntity n\obj2, scaleX,scaleY,scaleZ
	EndIf
	
	n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "speed") / 200.0)
	
	n\obj3 = LoadMesh_Strict("GFX\npcs\173\173box.b3d")
	ScaleEntity n\obj3, RoomScale, RoomScale, RoomScale
	HideEntity n\obj3
	
	n\CollRadius = 0.32
	n\HP = 100
End Function

Function UpdateNPCtype173(n.NPCs)
	Local Cheat.Cheats = First Cheats
	Local w.WayPoints,d.Doors,n2.NPCs,e.Events
	Local dist#,x#,z#,pvt%,dist2#
	Local i
	Local snd.Sound
	
	If Curr173\Idle <> 3 Then
		
		dist# = EntityDistance(n\Collider, Collider)		
		
		n\State3 = 1
		
		PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
		RotateEntity (n\obj, 0, EntityYaw(n\Collider)-180, 0)
		
		PositionEntity(n\obj2, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
		RotateEntity (n\obj2, 0, (EntityYaw(n\Collider)-180)+n\Angle, 0)
		
		If n\Idle < SCP173_CONTAIN Then
			If n\Idle = SCP173_ACTIVE Then
				Local temp% = False
				Local move% = True
				If dist < 15 Then
					If dist < 10.0 Then 
						If EntityVisible(n\Collider, Collider) Then
							temp = True
							n\EnemyX = EntityX(Collider, True)
							n\EnemyY = EntityY(Collider, True)
							n\EnemyZ = EntityZ(Collider, True)
						EndIf
					EndIf
					
					Local SoundVol# = Max(Min((Distance(EntityX(n\Collider), n\PrevX, EntityZ(n\Collider), n\PrevZ) * 2.5), 1.0), 0.0)
					n\SoundChn = LoopSound2(StoneDragSFX, n\SoundChn, Camera, n\Collider, 10.0, n\State)
					
					n\PrevX = EntityX(n\Collider)
					n\PrevZ = EntityZ(n\Collider)				
					
					If (BlinkTimer < - 16 Lor BlinkTimer > - 6) Then
						If EntityInView(n\obj, Camera) Lor EntityInView(n\obj2, Camera) Then move = False
					EndIf
				EndIf
				
				If NoTarget Then move = True
				
				;player is looking at it -> doesn't move
				If move=False Then
					BlurVolume = Max(Max(Min((4.0 - dist) / 6.0, 0.9), 0.1), BlurVolume)
					CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs())/20.0)+1.0)*15.0*Max((3.5-dist)/3.5,0.0))								
					
					If dist < 3.5 And MilliSecs() - n\LastSeen > 60000 And temp Then
						i = Rand(3,4)
						If Cheat\Mini173 Then
							snd = Object.Sound(HorrorSFX[i])
							snd\internalHandle = LoadSound(snd\name)
							SoundPitch(snd\internalHandle,48000)
						EndIf
						PlaySound_Strict(HorrorSFX[i])
						
						n\LastSeen = MilliSecs()
					EndIf
					
					If dist < 1.5 And Rand(700) = 1 Then
						i = Rand(0,2)
						If Cheat\Mini173 Then
							snd = Object.Sound(Scp173SFX[i])
							snd\internalHandle = LoadSound(snd\name)
							SoundPitch(snd\internalHandle,48000)
						EndIf
						PlaySound2(Scp173SFX[i], Camera, n\obj)
					EndIf
					
					If dist < 1.5 And n\LastDist > 2.0 And temp Then
						CurrCameraZoom = 40.0
						HeartBeatRate = Max(HeartBeatRate, 140)
						HeartBeatVolume = 0.75
						
						Select Rand(5)
							Case 1
								i = 1
							Case 2
								i = 2
							Case 3
								i = 9
							Case 4
								i = 10
							Case 5
								i = 14
						End Select
						If Cheat\Mini173 Then
							snd = Object.Sound(HorrorSFX[i])
							snd\internalHandle = LoadSound(snd\name)
							SoundPitch(snd\internalHandle,48000)
						EndIf
						PlaySound_Strict(HorrorSFX[i])
					EndIf									
					
					n\LastDist = dist
					
					n\State = Max(0, n\State - FPSfactor / 20)
				Else 
					;more than 6 room lengths away from the player -> teleport to a room closer to the player
					If dist > 50 Then
						If Rand(70)=1 Then
							If PlayerRoom\RoomTemplate\Name <> "gate_b_topside" And PlayerRoom\RoomTemplate\Name <> "gate_a_topside" And PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then
								For w.WayPoints = Each WayPoints
									If w\door=Null And Rand(5)=1 Then
										x = Abs(EntityX(Collider)-EntityX(w\obj,True))
										If x < 25.0 And x > 15.0 Then
											z = Abs(EntityZ(Collider)-EntityZ(w\obj,True))
											If z < 25 And z > 15.0 Then
												DebugLog "MOVING 173 TO "+w\room\RoomTemplate\Name
												TeleportEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True),n\CollRadius
												Exit
											EndIf
										EndIf
									EndIf
								Next
							EndIf
						EndIf
					ElseIf dist > HideDistance*0.8 ;3-6 rooms away from the player -> move randomly from waypoint to another
						If Rand(70)=1 Then TeleportCloser(n)
					Else ;less than 3 rooms away -> actively move towards the player
						n\State = CurveValue(SoundVol, n\State, 3)
						
						;try to open doors
						If Rand(20) = 1 Then
							For d.Doors = Each Doors
								If (Not d\locked) And d\open = False And d\Code = "" And d\KeyCard=0 Then
									For i% = 0 To 1
										If d\buttons[i] <> 0 Then
											If Abs(EntityX(n\Collider) - EntityX(d\buttons[i])) < 0.5 Then
												If Abs(EntityZ(n\Collider) - EntityZ(d\buttons[i])) < 0.5 Then
													If (d\openstate >= 180 Lor d\openstate <= 0) Then
														pvt = CreatePivot()
														PositionEntity pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.5, EntityZ(n\Collider)
														PointEntity pvt, d\buttons[i]
														MoveEntity pvt, 0, 0, n\Speed * 0.6
														
														If EntityPick(pvt, 0.5) = d\buttons[i] Then 
															PlaySound_Strict (LoadTempSound("SFX\Door\DoorOpen173.ogg"))
															UseDoor(d,False)
														EndIf
														
														pvt = FreeEntity_strict(pvt)
													EndIf
												EndIf
											EndIf
										EndIf
									Next
								EndIf
							Next
						EndIf
						
						If NoTarget Then
							temp = False
							n\EnemyX = 0
							n\EnemyY = 0
							n\EnemyZ = 0
						EndIf
						
						;player is not looking and is visible from 173's position -> attack
						If temp Then
							n\Angle = DeltaYaw(n\Collider,Camera)
							For n2.NPCs = Each NPCs
								dist2# = EntityDistance(n\Collider, n2\Collider)
								If dist < 0.65 Then
									If KillTimer >= 0 And (Not GodMode) Then
										
										Select PlayerRoom\RoomTemplate\Name
											Case "lockroom_1", "room2_closets", "cont_895"
												DeathMSG = Designation+". Cause of death: Fatal cervical fracture. The surveillance tapes confirm that the subject was killed by SCP-173."
											Case "room2_doors"
												DeathMSG = Chr(34)+"If I'm not mistaken, one of the main purposes of these rooms was to stop SCP-173 from moving further in the event of a containment breach. "
												DeathMSG = DeathMSG + "So, who's brilliant idea was it to put A GODDAMN MAN-SIZED VENTILATION DUCT in there?"+Chr(34)
											Default 
												DeathMSG = Designation+". Cause of death: Fatal cervical fracture. Assumed to be attacked by SCP-173."
										End Select
										
										If (Not GodMode) Then n\Idle = SCP173_STATIONARY
										PlaySound_Strict(NeckSnapSFX[Rand(0,2)])
										If Rand(2) = 1 Then 
											TurnEntity(Camera, 0, Rand(80,100), 0)
										Else
											TurnEntity(Camera, 0, Rand(-100,-80), 0)
										EndIf
										Kill()
									EndIf
								ElseIf dist2 < dist And n2\HP > 0 Then
									If n2\NPCtype = NPCtypeMTF Then
										If dist2 < 0.65 Then
											n2\HP = 0
											PlaySound2(NeckSnapSFX[Rand(0,2)], Camera, n\Collider)
											Exit
										EndIf
									EndIf
									PointEntity(n\Collider, n2\Collider)
									RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0
									TranslateEntity n\Collider,Cos(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor,0.0,Sin(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor
								Else
									PointEntity(n\Collider, Collider)
									RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0
									TranslateEntity n\Collider,Cos(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor,0.0,Sin(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor
								EndIf
							Next
						Else ;player is not visible -> move to the location where he was last seen
							If n\EnemyX <> 0 Then
								n\Angle = DeltaYaw(n\Collider,Camera)
								If DistanceSquared(EntityX(n\Collider), n\EnemyX, EntityZ(n\Collider), n\EnemyZ) > PowTwo(0.5) Then
									AlignToVector(n\Collider, n\EnemyX-EntityX(n\Collider), 0, n\EnemyZ-EntityZ(n\Collider), 3)
									MoveEntity(n\Collider, 0, 0, n\Speed * FPSfactor)
									If Rand(500) = 1 Then n\EnemyX = 0 : n\EnemyY = 0 : n\EnemyZ = 0
								Else
									n\EnemyX = 0 : n\EnemyY = 0 : n\EnemyZ = 0
								EndIf
							Else
								If Rand(400)=1 Then RotateEntity (n\Collider, 0, Rnd(360), 0)
								TranslateEntity n\Collider,Cos(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor,0.0,Sin(EntityYaw(n\Collider)+90.0)*n\Speed*FPSfactor
								n\Angle = Rnd(-120,120)
							EndIf
						EndIf
					EndIf ; less than 2 rooms away from the player
					
				EndIf
				
			EndIf ;idle = false
			
			PositionEntity(n\Collider, EntityX(n\Collider), Min(EntityY(n\Collider),0.35), EntityZ(n\Collider))
			
		ElseIf n\Idle = 2 Lor n\ContainmentState = SCP173_BOXED ;idle = 2
			
			PointEntity n\obj, Collider
			RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0), 0, True								
			If EntityDistanceSquared(n\Collider, Collider) < PowTwo(1) Then
				MoveEntity n\Collider, 0, 0, -0.016*FPSfactor*Max(Min((EntityDistanceSquared(n\Collider, Collider)*PowTwo(2)-1.0)*0.5,1.0),-0.5)
			Else
				MoveEntity n\Collider, 0, 0, 0.016*FPSfactor*Max(Min((EntityDistanceSquared(n\Collider, Collider)*PowTwo(2)-1.0)*0.5,1.0),-0.5)
			EndIf
			n\GravityMult = 1.0
			
			PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) + 0.05 + Sin(MilliSecs()*0.08)*0.02, EntityZ(n\Collider))
			RotateEntity (n\obj, 0, EntityYaw(n\Collider)-180, 0)
			
			PositionEntity(n\obj2, EntityX(n\Collider), EntityY(n\Collider) + 0.05 + Sin(MilliSecs()*0.08)*0.02, EntityZ(n\Collider))
			RotateEntity (n\obj2, 0, (EntityYaw(n\Collider)-180)+n\Angle, 0)
			
			ShowEntity n\obj3
			
			PositionEntity(n\obj3, EntityX(n\Collider), EntityY(n\Collider) - 0.05 + Sin(MilliSecs()*0.08)*0.02, EntityZ(n\Collider))
			RotateEntity (n\obj3, 0, EntityYaw(n\Collider)-180, 0)
		EndIf
	EndIf
	
End Function









;~IDEal Editor Parameters:
;~C#Blitz3D