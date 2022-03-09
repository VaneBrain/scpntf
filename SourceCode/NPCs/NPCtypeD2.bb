;[Block]
Const D2_RELOAD = 4
Const D2_GO_AFTER = 3
Const D2_FIND_COVER = 2
Const D2_ATTACK = 1
Const D2_IDLE = 0
;[End Block]

Function CreateNPCtypeD2(n.NPCs)
	Local temp#, tex%
	
	n\NPCName = "Class-D"
	n\NVName = "Human"
	n\Collider = CreatePivot()
	EntityRadius n\Collider, 0.25, 0.32
	EntityType n\Collider, HIT_PLAYER
	
	n\obj = CopyEntity(ClassDObj2)
	
	temp# = 0.5 / MeshWidth(n\obj)
	ScaleEntity n\obj, temp, temp, temp
	
	Local random% = Rand(0,4)
	
	Select random%
		Case 0
			n\texture = "GFX\npcs\classd1.jpg"
		Case 1
			n\texture = "GFX\npcs\classd4.jpg"
		Case 2
			n\texture = "GFX\npcs\classd5.jpg"
		Case 3
			n\texture = "GFX\npcs\classd6.jpg"
		Case 4
			n\texture = "GFX\npcs\classd2.jpg"
	End Select
	
	tex = LoadTexture_Strict(n\texture, 0, 2)
	TextureBlend(tex,5)
	EntityTexture(n\obj, tex)
	
	n\Speed = 4.0 / 100
	
	MeshCullBox (n\obj, -MeshWidth(ClassDObj2), -MeshHeight(ClassDObj2), -MeshDepth(ClassDObj2)*5, MeshWidth(ClassDObj2)*2, MeshHeight(ClassDObj2)*2, MeshDepth(ClassDObj2)*10)
	
	Local i% = Rand(1, 100)
	If i <= 33 Then
		SwitchNPCGun%(n, GUN_USP)
	ElseIf i > 33 And i <= 66 Then	
		SwitchNPCGun%(n, GUN_BERETTA)
	ElseIf i > 66 And i <= 78 Then
		SwitchNPCGun%(n, GUN_P90)
	ElseIf i > 78 And i <= 90 Then
		SwitchNPCGun%(n, GUN_MP5K)
	ElseIf i > 90 Then
		SwitchNPCGun%(n, GUN_SPAS12)
	EndIf	
	
	n\CollRadius = 0.16
	
	n\HP = 80+(20*SelectedDifficulty\otherFactors)
	
	CopyHitBoxes(n)
End Function

Function UpdateNPCtypeD2(n.NPCs)
	Local n2.NPCs, w.WayPoints, g.Guns, it.Items, r.Rooms, v3d.Vector3D
	Local prevFrame#, temp2%, deathFrame#, bone%, dist#
	
	prevFrame = n\Frame
	
	If n\IsDead = False Then
		If n\State = D2_IDLE Lor n\State = D2_GO_AFTER Then
			For n2.NPCs = Each NPCs
				If n2\HP > 0 Then
					If n2\NPCtype = NPCtypeMTF Then
						If NPCSeesEntity(n, n2\Collider) Then
							n\Target = n2
							If n\State = D2_IDLE Then 
								PlayNPCSound(n, LoadTempSound("SFX\Character\D-Class\MTF_Spotted"+Rand(1, 5)+".ogg"))
							EndIf	
							n\State = D2_ATTACK
							Exit
						EndIf
					EndIf
				EndIf
			Next
			If (Not NoTarget) And psp\Health > 0 Then
				If NPCSeesEntity(n, Camera) Then
					If n\State = D2_IDLE Then 
						PlayNPCSound(n, LoadTempSound("SFX\Character\D-Class\MTF_Spotted"+Rand(1, 5)+".ogg"))
					EndIf
					n\State = D2_ATTACK
				EndIf
			EndIf
		EndIf
		
		Select n\State
			Case D2_IDLE
				;[Block]
				Local roomfound% = False
				While roomfound = False
					If n\NPCRoom=Null Then
						GetNPCRoom(n)
					EndIf
					For r.Rooms = Each Rooms
						If Rand(5)=1 Then
							roomfound = True
							Exit
						EndIf
					Next
				Wend
				
				NPC_GoTo(n, FindNPCAnimation(n\NPCtype, "idle"), FindNPCAnimation(n\NPCtype, "walk"), r\obj, 0.3)
				;AnimateNPC(n, 212, 235, 0.1)
				;[End Block]
			Case D2_ATTACK
				;[Block]
				dist = EntityDistanceSquared(n\Collider, Collider)
				
				If n\Target <> Null Then
					If n\Target\HP > 0 Then
						n\CurrSpeed = 0.0
						n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\obj,n\Target\obj)
						RotateEntity n\Collider, 0, CurveAngle(n\Angle, EntityYaw(n\Collider), 20.0), 0
						If n\Reload = 0 Then
							If Abs(DeltaYaw(n\Collider,n\Target\Collider))<45.0 Then
								If NPCSeesEntity(n, n\Target\Collider)
									n\Gun\Ammo = n\Gun\Ammo - 1
									;SetNPCFrame(n,575)
									ShootTarget(0, 0, 0, n, Clamp(2 / dist, 0.0, 0.65), True, n\Gun\Damage * Rand(1, n\Gun\BulletsPerShot))
									If n\Gun\MaxGunshotSounds = 1 Then
										PlaySound_Strict(LoadTempSound("SFX\Guns\" + n\Gun\Name + "\shoot.ogg"))
									Else
										PlaySound_Strict(LoadTempSound("SFX\Guns\" + n\Gun\Name + "\shoot" + Rand(1, n\Gun\MaxGunshotSounds) + ".ogg"))
									EndIf
									If GetClassDWeaponAnim(n\Gun\AnimType) = "pistol" Then
										n\Reload = Rand(25,50)
									Else
										n\Reload = n\Gun\ShootFrequency
									EndIf
								EndIf
							EndIf
						EndIf
					Else
						n\Target = Null
						n\State = D2_IDLE
					EndIf
				Else
					If psp\Health <= 0 Then
						PlayNPCSound(n, LoadTempSound("SFX\Character\D-Class\Player_Kill"+Rand(1, 3)+".ogg"))
						DeathMSG = Designation+" was found with several bullet wounds. Cause of death is presumably killed by an armed D-Class." 
						n\State = D2_IDLE
					Else
						n\CurrSpeed = 0.0
						n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\obj,Collider)
						RotateEntity n\Collider, 0, CurveAngle(n\Angle, EntityYaw(n\Collider), 20.0), 0
						If n\Reload = 0 Then
							If Abs(DeltaYaw(n\Collider,Collider))<45.0 Then
								If NPCSeesEntity(n, Camera)
									;SetNPCFrame(n,575)
									If n\Gun\MaxGunshotSounds = 1 Then
										PlaySound_Strict(LoadTempSound("SFX\Guns\" + n\Gun\Name + "\shoot.ogg"))
									Else
										PlaySound_Strict(LoadTempSound("SFX\Guns\" + n\Gun\Name + "\shoot" + Rand(1, n\Gun\MaxGunshotSounds) + ".ogg"))
									EndIf
									ShootPlayer(0, 0, 0, Clamp(2 / dist, 0.0, 0.65), True, n\Gun\Damage * Rand(1, n\Gun\BulletsPerShot))
									n\Gun\Ammo = n\Gun\Ammo - 1
									If GetClassDWeaponAnim(n\Gun\AnimType) = "pistol" Then
										n\Reload = Rand(25,50)
									Else
										n\Reload = n\Gun\ShootFrequency
									EndIf
								Else
									n\LastSeen = Collider
									n\IdleTimer = 70*8
									n\State = D2_GO_AFTER
								EndIf
							EndIf
						EndIf
					EndIf
					
					If n\Gun\Ammo <= 0 Then
					;	Until we fix the AnimateNPC Function breaking the reloading. -Meow
					;	n\State = D2_FIND_COVER
						PlayNPCSound(n, LoadTempSound("SFX\Character\D-Class\Reload"+Rand(1, 4)+".ogg"))
						n\State = D2_FIND_COVER
					EndIf
				EndIf
				
				v3d = FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_idle")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z)
				;[End Block]
			Case D2_FIND_COVER
				;[Block]
				If n\Gun\Ammo <= 0 Then
					If n\Target<>Null Then
						temp2 = NPC_GoToCover(n, FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_walk"), n\Target\Collider, 0.8)
					Else
						temp2 = NPC_GoToCover(n, FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_walk"), Collider, 0.8)
					EndIf
				EndIf
				If temp2 Then
					n\State = D2_RELOAD
				EndIf
				;[End Block]
			Case D2_GO_AFTER
				;[Block]
				NPC_GoTo(n, FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_idle"), FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_walk"), Collider, 0.8)
				If n\IdleTimer = 0.0 Then
					n\State = D2_IDLE
				EndIf
				;[End Block]
			Case D2_RELOAD
				;[Block]
				v3d = FindNPCAnimation(n\NPCtype, GetClassDWeaponAnim(n\Gun\AnimType) + "_reload")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z, False)
				If n\Frame >= v3d\y Then
					n\Gun\Ammo = n\Gun\MaxAmmo
					n\State = D2_GO_AFTER
				EndIf
				;[End Block]
			Case STATE_SCRIPT
				;[Block]
				
				;[End Block]
		End Select
		n\IdleTimer = Max(0.0, n\IdleTimer - FPSfactor)
		n\Reload = Max(0, n\Reload - FPSfactor)
		
		;TODO: Commented out, need to find a good solution for this
;		If n\State = D2_IDLE Then
;			If n\CurrSpeed > 0.01 Then
;				If prevFrame < 244 And AnimTime(n\obj)=>244 Then
;					sfxstep = GetStepSound(n\Collider,n\CollRadius)
;					PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
;				ElseIf prevFrame < 256 And AnimTime(n\obj)=>256
;					sfxstep = GetStepSound(n\Collider,n\CollRadius)
;					PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
;				EndIf
;			EndIf
;		ElseIf n\State = D2_FIND_COVER Lor D2_GO_AFTER Then
;			If n\CurrSpeed > 0.01 Then
;				If prevFrame < 603 And AnimTime(n\obj)=>603
;					sfxstep = GetStepSound(n\Collider,n\CollRadius)
;					PlaySound2(StepSFX(sfxstep,1,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
;				ElseIf prevFrame =< 612 And AnimTime(n\obj)=<612
;					sfxstep = GetStepSound(n\Collider,n\CollRadius)
;					PlaySound2(StepSFX(sfxstep,1,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
;				EndIf
;			EndIf
;		EndIf
	Else
		Select n\State2
			Case 0.0
				v3d = FindNPCAnimation(n\NPCtype, "death_front")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z, False) ;from front
			Case 1.0
				v3d = FindNPCAnimation(n\NPCtype, "death_left")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z, False) ;from left
			Case 2.0
				v3d = FindNPCAnimation(n\NPCtype, "death_back")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z, False) ;from back
			Case 3.0
				v3d = FindNPCAnimation(n\NPCtype, "death_right")
				AnimateNPC(n, v3d\x, v3d\y, v3d\z, False) ;from right
		End Select
		n\LastSeen = 0.0
		n\Reload = 0.0
		If n\Gun <> Null And n\Frame >= v3d\y-0.5 Then
			bone% = FindChild(n\obj, GetINIString("Data\NPCBones.ini", n\NPCName, "weapon_hand_bonename"))
			For g = Each Guns
				If g\ID = n\Gun\ID Then
					it = CreateItem(g\DisplayName, g\name, EntityX(bone%, True), EntityY(bone%, True) + 0.025, EntityZ(bone%, True))
					EntityType it\collider, HIT_ITEM
					it\state = n\Gun\Ammo
					it\state2 = Rand(0,2)
					it\Dropped = 1
					Exit
				EndIf
			Next
			RemoveNPCGun(n)
		EndIf
	EndIf
	
	;Is the NPC dead?
	If n\HP <= 0 And (Not n\IsDead) Then
		n\IsDead = True
		;This needs to be rewritten!
		Local temp% = (EntityYaw(Camera) - EntityYaw(n\obj) + 45 + 180) Mod 360
		n\State2 = 0.0
		If temp > 90 Then
			n\State2 = 1.0
			If temp > 180 Then
				n\State2 = 2.0
				If temp > 270 Then
					n\State2 = 3.0
				EndIf
			EndIf
		EndIf
		SetNPCFrame(n, 0)
	EndIf
	
	PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
	
	RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider), 0
End Function

Function GetClassDWeaponAnim$(GunType%)
	
	Select GunType
		Case GUNANIM_MP5K, GUNANIM_RIFLE, GUNANIM_SMG
			Return "rifle"
		Case GUNANIM_PISTOL
			Return "pistol"
		Case GUNANIM_SHOTGUN
			Return "shotgun"
	End Select
	Return "pistol"
	
End Function

;~IDEal Editor Parameters:
;~F#0
;~C#Blitz3D