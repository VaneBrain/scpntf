
;NPCs constants
;[Block]
Const NPCtype173% = 1, NPCtypeOldMan% = 2, NPCtypeGuard% = 3, NPCtypeD% = 4
Const NPCtype372% = 6, NPCtypeApache% = 7, NPCtypeMTF% = 8, NPCtype096 = 9
Const NPCtype049% = 10, NPCtypeZombie% = 11, NPCtype5131% = 12, NPCtypeTentacle% = 13
Const NPCtype860% = 14, NPCtype939% = 15, NPCtype066% = 16, NPCtype178% = 17, NPCtypePdPlane% = 18
Const NPCtype966% = 19, NPCtype1048a = 20, NPCtype1499% = 21, NPCtype008% = 22, NPCtypeClerk% = 23

Const NPCtypeD2% = 24, NPCtype076% = 25, NPCtypeSci% = 26, NPCtypeEGuard% = 27, NPCtypeKGuard% = 29
Const NPCtypeD9341 = 30

Const NPCtypeZombieMP% = 28
Const NPCtypeGuardZombieMP% = 31
Const NPCtype035% = 32
Const NPCtype682% = 33
Const NPCtype457% = 34

Const MaxHitBoxes% = 25
Const MaxWayPoints% = 21
Const STATE_SCRIPT = 99
;[End Block]

Include "SourceCode\NPCs\NPCtype035.bb"
Include "SourceCode\NPCs\NPCtype049.bb"
Include "SourceCode\NPCs\NPCtype096.bb"
Include "SourceCode\NPCs\NPCtype106.bb"
Include "SourceCode\NPCs\NPCtype173.bb"
Include "SourceCode\NPCs\NPCtype457.bb"
Include "SourceCode\NPCs\NPCtype682.bb"
Include "SourceCode\NPCs\NPCtype939.bb"
Include "SourceCode\NPCs\NPCtype966.bb"
Include "SourceCode\NPCs\NPCtype1048a.bb"
Include "SourceCode\NPCs\NPCtypeGuard.bb"
Include "SourceCode\NPCs\NPCtypeMTF.bb"
Include "SourceCode\NPCs\NPCtypeZombie.bb"
Include "SourceCode\NPCs\NPCtypeZombieMP.bb"
Include "SourceCode\NPCs\NPCtypeD2.bb"

Global Curr173.NPCs, Curr106.NPCs, Curr096.NPCs, Curr5131.NPCs

Type NPCs
	Field obj%, obj2%, obj3%, obj4%, Collider%
	Field NPCtype%, ID%
	Field DropSpeed#, Gravity%
	Field State#, State2#, State3#, PrevState%
	Field MakingNoise%
	Field NPCName$
	
	Field Frame#
	
	Field Angle#
	Field Sound%, SoundChn%, SoundTimer#
	Field Sound2%, SoundChn2%
	
	Field Speed#, CurrSpeed#
	
	Field texture$
	
	Field Idle#
	
	Field Reload#
	
	;Field TargetEnt%
	
	Field LastSeen%, LastDist#
	
	Field FearTarget%
	
	Field PrevX#, PrevY#, PrevZ#
	
	Field Target.NPCs, TargetID%
	Field EnemyX#, EnemyY#, EnemyZ#
	
	Field Path.WayPoints[MaxWayPoints], PathStatus%, PathTimer#, PathLocation%
	
	Field NVX#,NVY#,NVZ#,NVName$
	
	Field GravityMult# = 1.0
	Field MaxGravity# = 0.2
	
	Field IsDead%
	Field BlinkTimer# = 1.0
	Field IgnorePlayer%
	
	Field ManipulateBone%
	Field ManipulationType%
	Field BoneToManipulate$
	Field BonePitch#
	Field BoneYaw#
	Field BoneRoll#
	Field NPCNameInSection$
	Field InFacility% = True
	Field CanUseElevator% = False
	Field CurrElevator.ElevatorObj
	Field HP%
	Field PathX#,PathZ#
	Field Model$
	Field ModelScaleX#,ModelScaleY#,ModelScaleZ#
	Field HideFromNVG
	Field TextureID%=-1
	Field CollRadius#
	Field IdleTimer#
	Field SoundChn_IsStream%,SoundChn2_IsStream%
	Field FallingPickDistance#
	
	Field HitBox1[MaxHitBoxes]
	Field HitBox2[MaxHitBoxes]
	Field HitBox3[MaxHitBoxes]
	Field BoneName$[MaxHitBoxes]
	Field HitBoxPosX#[MaxHitBoxes]
	Field HitBoxPosY#[MaxHitBoxes]
	Field HitBoxPosZ#[MaxHitBoxes]
	Field ShootSFX%,ShootSFXCHN%
	Field CurrAnimSeq%
	
	Field Clearance%
	
	Field Gun.NPCGun
	
	Field NPCRoom.Rooms
	
	;Multiplayer Variables
	Field noDelete%
	;Field TargetPlayerID%
	Field DistanceTimer#
	Field ClosestPlayer%
	Field DeleteTimer#
	Field IsStuck%
	Field StuckTimer#
	Field GotHit%
	Field KilledBy%
End Type

Type NPCGun
	Field ID%
	Field AnimType%
	Field Name$
	Field obj%
	Field Ammo%
	Field MaxAmmo%
	Field MaxGunshotSounds%
	Field Damage%
	Field ShootFrequency#
	Field BulletsPerShot%
End Type

Type NPCAnim
	Field NPCtype%
	Field Animation.Vector3D
	Field AnimName$
End Type

Function CreateNPC.NPCs(NPCtype%, x#, y#, z#, model%=-1)
	Local n.NPCs = New NPCs, n2.NPCs, r.Rooms
	Local temp#, i%, diff1, bump1, spec1
	Local sf, b, t1
	
	n\CurrAnimSeq = 0
	
	n\NPCtype = NPCtype
	n\GravityMult = 1.0
	n\MaxGravity = 0.2
	n\CollRadius = 0.2
	n\FallingPickDistance = 10
	Select NPCtype
		Case NPCtype173
			CreateNPCtype173(n)
		Case NPCtypeOldMan
			CreateNPCtype106(n)
		Case NPCtypeGuard
			CreateNPCtypeGuard(n)
		Case NPCtypeMTF
			CreateNPCtypeMTF(n)
		Case NPCtypeD
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(ClassDObj)
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(ClassDObj), -MeshHeight(ClassDObj), -MeshDepth(ClassDObj), MeshWidth(ClassDObj)*2, MeshHeight(ClassDObj)*2, MeshDepth(ClassDObj)*2)
			
			n\CollRadius = 0.32
			;[End Block]
		Case NPCtype372
			;[Block]
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			n\obj = LoadAnimMesh_Strict("GFX\npcs\372.b3d")
			
			temp# = 0.35 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			;[End Block]
		Case NPCtype5131
			;[Block]
			n\NVName = "SCP-513-1"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			n\obj = LoadAnimMesh_Strict("GFX\npcs\bll.b3d")
			
			n\obj2 = CopyEntity (n\obj)
			EntityAlpha n\obj2, 0.6
			
			temp# = 1.8 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			ScaleEntity n\obj2, temp, temp, temp
			;[End Block]
		Case NPCtype096
			CreateNPCtype096(n)
		Case NPCtype049
			CreateNPCtype049(n)
		Case NPCtypeZombie
			CreateNPCtypeZombie(n)
		Case NPCtypeApache
			;[Block]
			n\NVName = "Human"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			n\obj = LoadAnimMesh_Strict("GFX\apache.b3d")
			
			n\obj2 = LoadAnimMesh_Strict("GFX\apacherotor.b3d",n\obj)
			EntityParent n\obj2,n\obj
			
			For i = -1 To 1 Step 2
				Local rotor2 = CopyEntity(n\obj2,n\obj2)
				RotateEntity rotor2,0,4.0*i,0
				EntityAlpha rotor2, 0.5
			Next
			
			n\obj3 = LoadAnimMesh_Strict("GFX\apacherotor2.b3d",n\obj)
			PositionEntity n\obj3, 0.0, 2.15, -5.48
			
			EntityType n\Collider, HIT_APACHE
			EntityRadius n\Collider, 3.0
			
			For i = -1 To 1 Step 2
				Local Light1 = CreateLight(2,n\obj)
				;room\LightDist[i] = range
				LightRange(Light1,2.0)
				LightColor(Light1,255,255,255)
				PositionEntity(Light1, 1.65*i, 1.17, -0.25)
				
				Local lightsprite = CreateSprite(n\obj)
				PositionEntity(lightsprite, 1.65*i, 1.17, 0, -0.25)
				ScaleSprite(lightsprite, 0.13, 0.13)
				EntityTexture(lightsprite, LightSpriteTex[0])
				EntityBlend (lightsprite, 3)
				EntityFX lightsprite, 1+8				
			Next
			
			temp# = 0.6
			ScaleEntity n\obj, temp, temp, temp
			;[End Block]
		Case NPCtypeTentacle
			If mp_I\PlayState = 0 Then
				;[Block]
				n\NVName = "Unidentified"
				
				n\Collider = CreatePivot()
				
				For n2.NPCs = Each NPCs
					If n\NPCtype = n2\NPCtype And n<>n2 Then
						n\obj = CopyEntity (n2\obj)
						Exit
					EndIf
				Next
				
				If n\obj = 0 Then 
					n\obj = LoadAnimMesh_Strict("GFX\NPCs\035tentacle.b3d")
					ScaleEntity n\obj, 0.065,0.065,0.065
				EndIf
				
				SetAnimTime n\obj, 283
				
				n\HP = 50
				
				CopyHitBoxes(n)
				;[End Block]
			Else
				CreateNPCtypeTentacleMP(n)
			EndIf
		Case NPCtype860
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.25
			EntityType n\Collider, HIT_PLAYER
			n\obj = LoadAnimMesh_Strict("GFX\npcs\forestmonster.b3d")
			
			EntityFX(n\obj, 1)
			
			tex = LoadTexture_Strict("GFX\npcs\860_eyes.png",1+2)
			
			n\obj2 = CreateSprite()
			ScaleSprite(n\obj2, 0.1, 0.1)
			EntityTexture(n\obj2, tex)
			DeleteSingleTextureEntryFromCache tex
			
			EntityFX(n\obj2, 1 + 8)
			EntityBlend(n\obj2, BLEND_ADD)
			SpriteViewMode(n\obj2, 2)
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "forestmonster", "speed") / 100.0)
			
			temp# = (GetINIFloat("DATA\NPCs.ini", "forestmonster", "scale") / 20.0)
			ScaleEntity n\obj, temp, temp, temp	
			
			MeshCullBox (n\obj, -MeshWidth(n\obj)*2, -MeshHeight(n\obj)*2, -MeshDepth(n\obj)*2, MeshWidth(n\obj)*2, MeshHeight(n\obj)*4, MeshDepth(n\obj)*4)
			
			n\CollRadius = 0.25
			;[End Block]
		Case NPCtype939
			If mp_I\PlayState = 0 Then
				CreateNPCtype939(n)
			Else
				CreateNPCtype939MP(n)
			EndIf
		Case NPCtype066
			;[Block]
			n\NVName = "SCP-066"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = LoadAnimMesh_Strict("GFX\NPCs\scp-066.b3d")
			temp# = GetINIFloat("DATA\NPCs.ini", "SCP-066", "scale")/2.5
			ScaleEntity n\obj, temp, temp, temp		
			
			;If BumpEnabled Then 
			;	diff1 = LoadTexture_Strict("GFX\npcs\scp-066_diffuse01.png")
			;	bump1 = LoadTexture_Strict("GFX\npcs\scp-066_normal.png")
			;	;TextureBlend bump1, FE_BUMP ;USE DOT3
			;	EntityTexture n\obj, bump1, 0, 1
			;	EntityTexture n\obj, diff1, 0, 2
			;	DeleteSingleTextureEntryFromCache diff1
			;	DeleteSingleTextureEntryFromCache bump1
			;EndIf
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-066", "speed") / 100.0)
			;[End Block]
		Case NPCtype966
			CreateNPCtype966(n)
		Case NPCtype1048a
			If mp_I\PlayState = 0 Then
				;[Block]
				n\NVName = "SCP-1048-A"
				n\obj =	LoadAnimMesh_Strict("GFX\npcs\scp-1048a.b3d")
				ScaleEntity n\obj, 0.05,0.05,0.05
				SetAnimTime(n\obj, 2)
				
				n\Sound = LoadSound_Strict("SFX\SCP\1048A\Shriek.ogg")
				n\Sound2 = LoadSound_Strict("SFX\SCP\1048A\Growth.ogg")
				;[End Block]
			Else
				CreateNPCtype1048aMP(n)
			EndIf
		Case NPCtype1499
			;[Block]
			n\NVName = "Unidentified"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			For n2.NPCs = Each NPCs
				If (n\NPCtype = n2\NPCtype) And (n<>n2) Then
					n\obj = CopyEntity (n2\obj)
					Exit
				EndIf
			Next
			
			If n\obj = 0 Then 
				n\obj = LoadAnimMesh_Strict("GFX\npcs\1499-1.b3d")
			EndIf
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-1499-1", "speed") / 100.0) * Rnd(0.9,1.1)
			temp# = (GetINIFloat("DATA\NPCs.ini", "SCP-1499-1", "scale") / 4.0) * Rnd(0.8,1.0)
			
			ScaleEntity n\obj, temp, temp, temp
			
			EntityFX n\obj,1
			;[End Block]
		Case NPCtype008
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = LoadAnimMesh_Strict("GFX\npcs\zombiesurgeon.b3d")
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			SetNPCFrame n,11
			
			n\Sound = LoadSound_Strict("SFX\SCP\049\0492Breath.ogg")
			
			;ApplyHitBoxes(n,"008_zombie")
			CopyHitBoxes(n)
			
			n\HP = 120
			;[End Block]
		Case NPCtypeClerk
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = CopyEntity(ClerkOBJ)
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 2.0 / 100
			
			MeshCullBox (n\obj, -MeshWidth(ClerkOBJ), -MeshHeight(ClerkOBJ), -MeshDepth(ClerkOBJ), MeshWidth(ClerkOBJ)*2, MeshHeight(ClerkOBJ)*2, MeshDepth(ClerkOBJ)*2)
			
			n\CollRadius = 0.32
			;[End Block]
		;New NPCs in the ntf mod	
		Case NPCtypeD2
			;[Block]
			CreateNPCtypeD2(n)
			;[End Block]
		Case NPCtypeSci
			;This will be added in later versions
			;CreateNPCtypeScientist(n)
		Case NPCtype076
			;[Block]
			n\NVName = "SCP-076-2"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = LoadAnimMesh_Strict("GFX\npcs\zombiesurgeon.b3d")
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			
			n\Speed = 0.06
			
			;ApplyHitBoxes(n,"SCP-076-2")
			CopyHitBoxes(n)
			
			n\HP = 750
			;[End Block]	
		Case NPCtypeEGuard
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			;EntityRadius Collider, 0.15, 0.30
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(GuardObj) ;LoadAnimMesh_Strict("GFX\npcs\mtf.b3d")
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "Guard", "speed") / 100.0)
			temp# = (GetINIFloat("DATA\NPCs.ini", "Guard", "scale") / 2.5)
			
			ScaleEntity n\obj, temp, temp, temp
			
			MeshCullBox (n\obj, -MeshWidth(GuardObj), -MeshHeight(GuardObj), -MeshDepth(GuardObj), MeshWidth(GuardObj)*2, MeshHeight(GuardObj)*2, MeshDepth(GuardObj)*2)
			
			;ApplyHitBoxes(n,"Guard")
			CopyHitBoxes(n)
			
			n\HP = 140
			;[End Block]	
		Case NPCtypeKGuard
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			;EntityRadius Collider, 0.15, 0.30
			EntityType n\Collider, HIT_PLAYER
			n\obj = CopyEntity(GuardObj) ;LoadAnimMesh_Strict("GFX\npcs\mtf.b3d")
			
			n\Speed = (GetINIFloat("DATA\NPCs.ini", "Guard", "speed") / 100.0)
			temp# = (GetINIFloat("DATA\NPCs.ini", "Guard", "scale") / 2.5)
			
			ScaleEntity n\obj, temp, temp, temp
			
			MeshCullBox (n\obj, -MeshWidth(GuardObj), -MeshHeight(GuardObj), -MeshDepth(GuardObj), MeshWidth(GuardObj)*2, MeshHeight(GuardObj)*2, MeshDepth(GuardObj)*2)
			
			;ApplyHitBoxes(n,"Guard")
			CopyHitBoxes(n)
			
			n\HP = 1
			;[End Guard]
		Case NPCtypeD9341
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.32
			EntityType n\Collider, HIT_PLAYER
			
			n\obj = LoadAnimMesh_Strict("GFX\npcs\classd.b3d")
			Local D9341Tex = LoadTexture_Strict("GFX\npcs\classd_D9341.jpg")
			EntityTexture(n\obj,D9341Tex, 0, 0)
			
			MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
			
			temp# = 0.5 / MeshWidth(n\obj)
			ScaleEntity n\obj, temp, temp, temp
			;[End Block]	
		;Multiplayer NPCs
		Case NPCtypeZombieMP
			CreateNPCtypeZombieMP(n, model)
		Case NPCtypeGuardZombieMP
			CreateNPCtypeZombieMP_Guard(n, model)
		Case NPCtype035
			CreateNPCtype035MP(n)
		Case NPCtype682
			CreateNPCtype682(n)
		Case NPCtype457
			If mp_I\PlayState = 0 Then
				CreateNPCtype457(n)
			Else
				CreateNPCtype457MP(n)
			EndIf
	End Select
	
	PositionEntity(n\Collider, x, y, z, True)
	PositionEntity(n\obj, x, y, z, True)
	
	ResetEntity(n\Collider)
	
	If mp_I\PlayState = 0 Then
		n\ID = 0
		n\ID = FindFreeNPCID()
	Else
		n\ID = mp_I\CurrNPCID
		mp_I\CurrNPCID = mp_I\CurrNPCID + 1
	EndIf
	
	DebugLog ("Created NPC "+n\NVName+" (ID: "+n\ID+")")
	
	NPCSpeedChange(n)
	
	HideNPCHitBoxes(n)
	
	Return n
End Function

Function RemoveNPC(n.NPCs)
	
	If n=Null Then Return
	
	If n\Gun <> Null Then
		n\Gun\obj = FreeEntity_Strict(n\Gun\obj)
		Delete n\Gun
	EndIf
	
	n\obj2 = FreeEntity_Strict(n\obj2)
	n\obj3 = FreeEntity_Strict(n\obj3)
	n\obj4 = FreeEntity_Strict(n\obj4)
	
	If (Not n\SoundChn_IsStream)
		If (n\SoundChn <> 0 And ChannelPlaying(n\SoundChn)) Then
			StopChannel(n\SoundChn)
		EndIf
	Else
		If (n\SoundChn <> 0)
			StopStream_Strict(n\SoundChn)
		EndIf
	EndIf
	
	If (Not n\SoundChn2_IsStream)
		If (n\SoundChn2 <> 0 And ChannelPlaying(n\SoundChn2)) Then
			StopChannel(n\SoundChn2)
		EndIf
	Else
		If (n\SoundChn2 <> 0)
			StopStream_Strict(n\SoundChn2)
		EndIf
	EndIf
	
	If n\Sound<>0 Then FreeSound_Strict n\Sound
	If n\Sound2<>0 Then FreeSound_Strict n\Sound2
	
	n\obj = FreeEntity_Strict(n\obj)
	n\Collider = FreeEntity_Strict(n\Collider)
	
	Delete n
End Function

Function UpdateNPCs()
	CatchErrors("Uncaught (UpdateNPCs)")
	Local n.NPCs, n2.NPCs, d.Doors, de.Decals, r.Rooms, eo.ElevatorObj, eo2.ElevatorObj, w.WayPoints
	Local i%, dist#, dist2#, angle#, x#, y#, z#, prevFrame#, PlayerSeeAble%, RN$, temp%, pvt%, TempSound2%
	
	Local target
	
	Local pick%
	
	Local sfxstep%
	
	For n.NPCs = Each NPCs
		;A variable to determine if the NPC is in the facility or not
		n\InFacility = CheckForNPCInFacility(n)
		
		HideNPCHitBoxes(n)
		
		Select n\NPCtype
			Case NPCtype173
				UpdateNPCtype173(n)
			Case NPCtypeOldMan ;------------------------------------------------------------------------------------------------------------------
				UpdateNPCtype106(n)
			Case NPCtype096
				UpdateNPCtype096(n)
			Case NPCtype049
				UpdateNPCtype049(n)
			Case NPCtypeZombie
				UpdateNPCtypeZombie(n)
			Case NPCtypeGuard ;------------------------------------------------------------------------------------------------------------------
				UpdateNPCtypeGuard(n)
			Case NPCtypeMTF ;------------------------------------------------------------------------------------------------------------------
				UpdateNPCtypeMTF(n)
			Case NPCtypeD,NPCtypeClerk 	;------------------------------------------------------------------------------------------------------------------
				;[Block]
				RotateEntity(n\Collider, 0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				
				prevFrame = AnimTime(n\obj)
				
				Select n\State
					Case 0 ;idle
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						Animate2(n\obj, AnimTime(n\obj), 210, 235, 0.1)
					Case 1 ;walking
						If n\State2 = 1.0
							n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
						Else
							n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
						EndIf
						Animate2(n\obj, AnimTime(n\obj), 236, 260, n\CurrSpeed * 18)
					Case 2 ;running
						n\CurrSpeed = CurveValue(0.03, n\CurrSpeed, 5.0)
						Animate2(n\obj, AnimTime(n\obj), 301, 319, n\CurrSpeed * 18)
				End Select
				
				If n\State2 <> 2
					If n\State = 1
						If n\CurrSpeed > 0.01 Then
							If prevFrame < 244 And AnimTime(n\obj)=>244 Then
								sfxstep = GetStepSound(n\Collider,n\CollRadius)
								PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							ElseIf prevFrame < 256 And AnimTime(n\obj)=>256
								sfxstep = GetStepSound(n\Collider,n\CollRadius)
								PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					ElseIf n\State = 2
						If n\CurrSpeed > 0.01 Then
							If prevFrame < 309 And AnimTime(n\obj)=>309
								sfxstep = GetStepSound(n\Collider,n\CollRadius)
								PlaySound2(StepSFX(sfxstep,1,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							ElseIf prevFrame =< 319 And AnimTime(n\obj)=<301
								sfxstep = GetStepSound(n\Collider,n\CollRadius)
								PlaySound2(StepSFX(sfxstep,1,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					EndIf
				EndIf
				
				If n\Frame = 19 Lor n\Frame = 60
					n\IsDead = True
				EndIf
				If AnimTime(n\obj)=19 Lor AnimTime(n\obj)=60
					n\IsDead = True
				EndIf
				
				MoveEntity(n\Collider, 0, 0, n\CurrSpeed * FPSfactor)
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider)-180.0, 0
				;[End Block]
			Case NPCtype5131
				;[Block}
				;If KeyHit(48) Then n\Idle = True : n\State2 = 0
				
				If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then 
					If n\Idle Then
						HideEntity(n\obj)
						HideEntity(n\obj2)
						If Rand(200)=1 Then
							For w.WayPoints = Each WayPoints
								If w\room<>PlayerRoom Then
									x = Abs(EntityX(Collider)-EntityX(w\obj,True))
									If x>3 And x < 9 Then
										z = Abs(EntityZ(Collider)-EntityZ(w\obj,True))
										If z>3 And z < 9 Then
											PositionEntity(n\Collider, EntityX(w\obj,True), EntityY(w\obj,True), EntityZ(w\obj,True))
											PositionEntity(n\obj, EntityX(w\obj,True), EntityY(w\obj,True), EntityZ(w\obj,True))
											ResetEntity n\Collider
											ShowEntity(n\obj)
											ShowEntity(n\obj2)
											
											n\LastSeen = 0
											
											n\Path[0]=w
											
											n\Idle = False
											n\State2 = Rand(15,20)*70
											n\State = Max(Rand(-1,2),0)
											n\PrevState = Rand(0,1)
											Exit
										EndIf
									EndIf
								EndIf
							Next
						End If
					Else
						dist = EntityDistanceSquared(Collider, n\Collider)
						
						;use the prev-values to do a "twitching" effect
						n\PrevX = CurveValue(0.0, n\PrevX, 10.0)
						n\PrevZ = CurveValue(0.0, n\PrevZ, 10.0)
						
						If Rand(100)=1 Then
							If Rand(5)=1 Then
								n\PrevX = (EntityX(Collider)-EntityX(n\Collider))*0.9
								n\PrevZ = (EntityZ(Collider)-EntityZ(n\Collider))*0.9
							Else
								n\PrevX = Rnd(0.1,0.5)
								n\PrevZ = Rnd(0.1,0.5)						
							EndIf
						EndIf
						
						temp = Rnd(-1.0,1.0)
						PositionEntity n\obj2, EntityX(n\Collider)+n\PrevX*temp, EntityY(n\Collider) - 0.2 + Sin((MilliSecs()/8-45) Mod 360)*0.05, EntityZ(n\Collider)+n\PrevZ*temp
						RotateEntity n\obj2, 0, EntityYaw(n\obj), 0
						If (Floor(AnimTime(n\obj2))<>Floor(n\Frame)) Then SetAnimTime n\obj2, n\Frame
						
						If n\State = 0 Then
							If n\PrevState=0
								AnimateNPC(n,2,74,0.2)
							Else
								AnimateNPC(n,75,124,0.2)
							EndIf
							;AnimateNPC(n, 229, 299, 0.2)
							
							If n\LastSeen Then 	
								PointEntity n\obj2, Collider
								RotateEntity n\obj, 0, CurveAngle(EntityYaw(n\obj2),EntityYaw(n\obj),40), 0
								If dist < PowTwo(4) Then n\State = Rand(1,2)
							Else
								If dist < PowTwo(6) And Rand(5)=1 Then
									If EntityInView(n\Collider,Camera) Then
										If EntityVisible(Collider, n\Collider) Then
											n\LastSeen = 1
											PlaySound_Strict LoadTempSound("SFX\SCP\513\Bell"+Rand(2,3)+".ogg")
										EndIf
									EndIf
								EndIf								
							EndIf
							
						Else
							If n\Path[0]=Null Then
								
								;move towards a waypoint that is:
								;1. max 8 units away from 513-1
								;2. further away from the player than 513-1's current position 
								For w.WayPoints = Each WayPoints
									x = Abs(EntityX(n\Collider,True)-EntityX(w\obj,True))
									If x < 8.0 And x > 1.0 Then
										z = Abs(EntityZ(n\Collider,True)-EntityZ(w\obj,True))
										If z < 8.0 And z > 1.0 Then
											If EntityDistanceSquared(Collider, w\obj) > dist Then
												n\Path[0]=w
												Exit
											EndIf
										EndIf
									EndIf
								Next
								
								;no suitable path found -> 513-1 simply disappears
								If n\Path[0] = Null Then
									n\Idle = True
									n\State2 = 0
								EndIf
							Else
								
								If EntityDistanceSquared(n\Collider, n\Path[0]\obj) > PowTwo(1.0) Then
									PointEntity n\obj, n\Path[0]\obj
									RotateEntity n\Collider, CurveAngle(EntityPitch(n\obj),EntityPitch(n\Collider),15.0), CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),15.0), 0, True
									n\CurrSpeed = CurveValue(0.05*Max((7.0-Sqr(dist))/7.0,0.0),n\CurrSpeed,15.0)
									MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
									If Rand(200)=1 Then MoveEntity n\Collider, 0, 0, 0.5
									RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True
								Else
									For i = 0 To 4
										If n\Path[0]\connected[i] <> Null Then
											If EntityDistanceSquared(Collider, n\Path[0]\connected[i]\obj) > dist Then
												
												If n\LastSeen = 0 Then 
													If EntityInView(n\Collider,Camera) Then
														If EntityVisible(Collider, n\Collider) Then
															n\LastSeen = 1
															PlaySound_Strict LoadTempSound("SFX\SCP\513\Bell"+Rand(2,3)+".ogg")
														EndIf
													EndIf
												EndIf
												
												n\Path[0]=n\Path[0]\connected[i]
												Exit
											EndIf
										EndIf
									Next
									
									If n\Path[0]=Null Then n\State2 = 0
								EndIf
							EndIf
						EndIf
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2 + Sin((MilliSecs()/8) Mod 360)*0.1, EntityZ(n\Collider))
						
						Select n\State 
							Case 1
								If n\PrevState=0
									AnimateNPC(n,125,194,n\CurrSpeed*20)
								Else
									AnimateNPC(n,195,264,n\CurrSpeed*20)
								EndIf
								;AnimateNPC(n, 458, 527, n\CurrSpeed*20)
								RotateEntity n\obj, 0, EntityYaw(n\Collider), 0 
							Case 2
								If n\PrevState=0
									AnimateNPC(n,2,74,0.2)
								Else
									AnimateNPC(n,75,124,0.2)
								EndIf
								;AnimateNPC(n, 229, 299, 0.2)
								RotateEntity n\obj, 0, EntityYaw(n\Collider), 0						
						End Select
						
						If n\State2 > 0 Then
							If dist < PowTwo(4.0) Then n\State2 = n\State2-FPSfactor*4
							n\State2 = n\State2-FPSfactor
						Else
							n\Path[0]=Null
							n\Idle = True
							n\State2=0
						EndIf
						
					End If
					
				EndIf
				
				n\DropSpeed = 0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtype372 ;------------------------------------------------------------------------------------------------------------------
				;[Block]
				RN$ = PlayerRoom\RoomTemplate\Name
				If RN$ <> "pocketdimension" And RN$ <> "dimension1499" Then 
					If n\Idle Then
						HideEntity(n\obj)
						If Rand(50) = 1 And (BlinkTimer < -5 And BlinkTimer > -15) Then
							ShowEntity(n\obj)
							angle# = EntityYaw(Collider)+Rnd(-90,90)
							
							dist = Rnd(1.5, 2.0)
							PositionEntity(n\Collider, EntityX(Collider) + Sin(angle) * dist, EntityY(Collider)+0.2, EntityZ(Collider) + Cos(angle) * dist)
							n\Idle = False
							n\State = Rand(20, 60)
							
							If Rand(300)=1 Then PlaySound2(RustleSFX[Rand(0,2)],Camera, n\obj, 8, Rnd(0.0,0.2))
						End If
					Else
						PositionEntity(n\obj, EntityX(n\Collider) + Rnd(-0.005, 0.005), EntityY(n\Collider)+0.3+0.1*Sin(MilliSecs()/2), EntityZ(n\Collider) + Rnd(-0.005, 0.005))
						RotateEntity n\obj, 0, EntityYaw(n\Collider), ((MilliSecs()/5) Mod 360)
						
						AnimateNPC(n, 32, 113, 0.4)
						;Animate2(n\obj, AnimTime(n\obj), 32, 113, 0.4)
						
						If EntityInView(n\obj, Camera) Then
							GiveAchievement(Achv372)
							
							If Rand(30)=1 Then 
								If (Not ChannelPlaying(n\SoundChn)) Then
									If EntityVisible(Camera, n\obj) Then 
										n\SoundChn = PlaySound2(RustleSFX[Rand(0,2)],Camera, n\obj, 8, 0.3)
									EndIf
								EndIf
							EndIf
							
							temp = CreatePivot()
							PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
							PointEntity temp, n\Collider
							
							angle =  WrapAngle(EntityYaw(Collider)-EntityYaw(temp))
							If angle < 180 Then
								RotateEntity n\Collider, 0, EntityYaw(Collider)-80, 0		
							Else
								RotateEntity n\Collider, 0, EntityYaw(Collider)+80, 0
							EndIf
							temp = FreeEntity_Strict(temp)
							
							MoveEntity n\Collider, 0, 0, 0.03*FPSfactor
							
							n\State = n\State-FPSfactor
						EndIf
						n\State=n\State-(FPSfactor/80.0)
						If n\State <= 0 Then n\Idle = True	
					End If
					
				EndIf
				
				n\DropSpeed = 0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtypeApache ;------------------------------------------------------------------------------------------------------------------
				;[Block]
				If EntityDistanceSquared(Collider, n\Collider)<PowTwo(60.0) Then 
					If PlayerRoom\RoomTemplate\Name = "exit1" Then 
						dist2 = Max(Min(EntityDistance(n\Collider, PlayerRoom\Objects[3])/(8000.0*RoomScale),1.0),0.0)
					Else 
						dist2 = 1.0
					EndIf
					
					n\SoundChn = LoopSound2(ApacheSFX, n\SoundChn, Camera, n\Collider, 25.0, dist2)
				EndIf
				
				n\DropSpeed = 0
				
				Select n\State
					Case 0,1
						TurnEntity(n\obj2,0,20.0*FPSfactor,0)
						TurnEntity(n\obj3,20.0*FPSfactor,0,0)
						
						If n\State=1 And (Not NoTarget) Then
							If Abs(EntityX(Collider)-EntityX(n\Collider))< 30.0 Then
								If Abs(EntityZ(Collider)-EntityZ(n\Collider))<30.0 Then
									If Abs(EntityY(Collider)-EntityY(n\Collider))<20.0 Then
										If Rand(20)=1 Then 
											If EntityVisible(Collider, n\Collider) Then
												n\State = 2
												PlaySound2(AlarmSFX[1], Camera, n\Collider, 50, 1.0)
											EndIf
										EndIf									
									EndIf
								EndIf
							EndIf							
						EndIf
					Case 2,3 ;player located -> attack
						
						If n\State = 2 Then 
							target = Collider
						ElseIf n\State = 3
							target=CreatePivot()
							PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
						EndIf
						
						If NoTarget And n\State = 2 Then n\State = 1
						
						TurnEntity(n\obj2,0,20.0*FPSfactor,0)
						TurnEntity(n\obj3,20.0*FPSfactor,0,0)
						
						If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 Then
							If Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 Then
								If Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
									PointEntity n\obj, target
									RotateEntity n\Collider, CurveAngle(Min(WrapAngle(EntityPitch(n\obj)),40.0),EntityPitch(n\Collider),40.0), CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),90.0), EntityRoll(n\Collider), True
									PositionEntity(n\Collider, EntityX(n\Collider), CurveValue(EntityY(target)+8.0,EntityY(n\Collider),70.0), EntityZ(n\Collider))
									
									dist# = Distance(EntityX(target),EntityX(n\Collider),EntityZ(target),EntityZ(n\Collider))
									
									n\CurrSpeed = CurveValue(Min(dist-6.5,6.5)*0.008, n\CurrSpeed, 50.0)
									
									;If DistanceSquared(EntityX(Collider),EntityZ(Collider),EntityX(n\collider),EntityZ(n\collider)) > PowTwo(6.5) Then
									;	n\currspeed = CurveValue(0.08,n\currspeed,50.0)
									;Else
									;	n\currspeed = CurveValue(0.0,n\currspeed,30.0)
									;EndIf
									MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
									
									If n\PathTimer = 0 Then
										n\PathStatus = EntityVisible(n\Collider,target)
										n\PathTimer = Rand(100,200)
									Else
										n\PathTimer = Min(n\PathTimer-FPSfactor,0.0)
									EndIf
									
									If n\PathStatus = 1 Then ;player visible
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(0, EntityRoll(n\Collider),40), True
										
										If n\Reload =< 0 Then
											If dist<20.0 Then
												pvt = CreatePivot()
												
												PositionEntity pvt, EntityX(n\Collider),EntityY(n\Collider), EntityZ(n\Collider)
												RotateEntity pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),EntityRoll(n\Collider)
												MoveEntity pvt, 0, 8.87*(0.21/9.0), 8.87*(1.7/9.0) ;2.3
												PointEntity pvt, target
												
												If WrapAngle(EntityYaw(pvt)-EntityYaw(n\Collider))<10 Then
													PlaySound2(Gunshot2SFX, Camera, n\Collider, 20)
													
													If PlayerRoom\RoomTemplate\Name = "exit1" Then
														DeathMSG = Chr(34)+"CH-2 to control. Shot down "+Designation+" at Gate B."+Chr(34)
													Else
														DeathMSG = Chr(34)+"CH-2 to control. Shot down "+Designation+" at Gate A."+Chr(34)
													EndIf
													
													ShootPlayer( EntityX(pvt),EntityY(pvt), EntityZ(pvt),((10/dist)*(1/dist))*(n\State=2),(n\State=2))
													
													n\Reload = 5
												EndIf
												
												pvt = FreeEntity_Strict(pvt)
											EndIf
										EndIf
									Else 
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(-20, EntityRoll(n\Collider),40), True
									EndIf
									MoveEntity n\Collider, -EntityRoll(n\Collider)*0.002,0,0
									
									n\Reload=n\Reload-FPSfactor
								EndIf
							EndIf
						EndIf		
						
						If n\State = 3 Then
							target = FreeEntity_Strict(target)
						EndIf
					Case 4 ;crash
						If n\State2 < 300 Then
							
							TurnEntity(n\obj2,0,20.0*FPSfactor,0)
							TurnEntity(n\obj3,20.0*FPSfactor,0,0)
							
							TurnEntity n\Collider,0,-FPSfactor*7,0;Sin(MilliSecs()/40)*FPSfactor
							n\State2=n\State2+FPSfactor*0.3
							
							target=CreatePivot()
							PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
							
							PointEntity n\obj, target
							MoveEntity n\obj, 0,0,FPSfactor*0.001*n\State2
							PositionEntity(n\Collider, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
							
							If EntityDistanceSquared(n\obj, target) < PowTwo(0.3) Then
								If TempSound2 <> 0 Then FreeSound_Strict TempSound2 : TempSound2 = 0
								TempSound2 = LoadSound_Strict("SFX\Character\Apache\Crash"+Rand(1,2)+".ogg")
								CameraShake = Max(CameraShake, 3.0)
								PlaySound_Strict TempSound2
								n\State = 5
							EndIf
							
							target = FreeEntity_Strict(target)
						EndIf
				End Select
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider), EntityRoll(n\Collider), True
				;[End Block]
			Case NPCtypeTentacle
				;[Block]
				dist = EntityDistanceSquared(n\Collider,Collider)
				
				If n\HP <= 0 Then
					n\IsDead = True
					SetNPCFrame(n, 285)
				Else
					If dist < PowTwo(HideDistance)
					
						Select n\State 
							Case 0 ;spawn
							
								If n\Frame>283 Then
									HeartBeatVolume = Max(CurveValue(1.0, HeartBeatVolume, 50),HeartBeatVolume)
									HeartBeatRate = Max(CurveValue(130, HeartBeatRate, 100),HeartBeatRate)
								
									PointEntity n\obj, Collider
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),25.0), 0
								
									AnimateNPC(n, 283, 389, 0.3, False)
									;Animate2(n\obj, AnimTime(n\obj), 283, 389, 0.3, False)
								
									If n\Frame>388 Then n\State = 1
								Else
									If dist < PowTwo(2.5) Then 
										SetNPCFrame(n, 284)
										n\Sound2 = LoadSound_Strict("SFX\Room\035Chamber\TentacleSpawn.ogg")
										PlaySound_Strict(n\Sound2)
									EndIf
								EndIf
								;spawn 283,389
								;attack 2, 32
								;idle 33, 174
							Case 1 ;idle
								If dist < PowTwo(1.8) Then 
									If Abs(DeltaYaw(n\Collider, Collider))<20 Then 
										n\State = 2
										If n\Sound<>0 Then FreeSound_Strict n\Sound : n\Sound = 0 
										If n\Sound2<>0 Then FreeSound_Strict n\Sound2 : n\Sound2 = 0 
									
									EndIf
								EndIf
							
								PointEntity n\obj, Collider
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),25.0), 0
							
								AnimateNPC(n, 33, 174, 0.3, True)
								;Animate2(n\obj, AnimTime(n\obj), 33, 174, 0.3, True)
							Case 2
							
								;finish the idle animation before playing the attack animation
								If n\Frame>33 And n\Frame<174 Then
									AnimateNPC(n, 33, 174, 2.0, False)
									;Animate2(n\obj, AnimTime(n\obj), 33, 174, 2.0, False)
								Else
									PointEntity n\obj, Collider
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),10.0), 0							
								
									If n\Frame>33 Then 
										;SetAnimTime(n\obj,2)
										n\Frame = 2
										n\Sound = LoadSound_Strict("SFX\Room\035Chamber\TentacleAttack"+Rand(1,2)+".ogg")
										PlaySound_Strict(n\Sound)
									EndIf
									AnimateNPC(n, 2, 32, 0.3, False)
									;Animate2(n\obj, AnimTime(n\obj), 2, 32, 0.3, False)
								
									If n\Frame>=5 And n\Frame<6 Then
										If dist < PowTwo(1.8) Then
											If Abs(DeltaYaw(n\Collider, Collider))<20 Then 
												If WearingHazmat Then
													;Injuries = Injuries+Rnd(0.5)
													If (Not GodMode) Then
														DamageSPPlayer(Rnd(10.0))
													EndIf
													PlaySound_Strict(LoadTempSound("SFX\General\BodyFall.ogg"))
												Else
													BlurTimer = 100
													;Injuries = Injuries+Rnd(1.0,1.5)
													If (Not GodMode) Then
														DamageSPPlayer(Rnd(25.0,50.0))
													EndIf
													PlaySound_Strict DamageSFX[Rand(3,4)]
												
													If (Not IsSPPlayerAlive()) Then
														DeathMSG = Chr(34)+"We will need more than the regular cleaning team to care of this. "
														DeathMSG = DeathMSG + "Two large and highly active tentacle-like appendages seem "
														DeathMSG = DeathMSG + "to have formed inside the chamber. Their level of aggression is "
														DeathMSG = DeathMSG + "unlike anything we've seen before - it looks like they have "
														DeathMSG = DeathMSG + "beaten some unfortunate Class D to death at some point during the breach."+Chr(34)
														Kill()
													EndIf
												EndIf
											
											EndIf
										EndIf
									
										n\Frame = 6
										;SetAnimTime(n\obj, 6)
									ElseIf n\Frame=32
										n\State = 1
										n\Frame = 173
										;SetAnimTime(n\obj, 173)
									EndIf
								EndIf
							
						End Select
					
					EndIf
				
					PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
					RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider)-180, EntityRoll(n\Collider), True
				
					n\DropSpeed = 0
				
					ResetEntity n\Collider
				EndIf
				;[End Block]
			Case NPCtype860
				;[Block]
				If PlayerRoom\RoomTemplate\Name = "room860" Then
					Local fr.Forest=PlayerRoom\fr;Object.Forest(e\room\Objects[1])
					
					If n\State <> 0 Then
						dist = EntityDistance(Collider,n\Collider)
					EndIf
					
					If ForestNPC<>0
						If ForestNPCData[2]=1
							ShowEntity ForestNPC
							If n\State<>1
								If (BlinkTimer<-8 And BlinkTimer >-12) Lor (Not EntityInView(ForestNPC,Camera))
									ForestNPCData[2]=0
									HideEntity ForestNPC
								EndIf
							EndIf
						Else
							HideEntity ForestNPC
						EndIf
					EndIf
					
					Select n\State
						Case 0 ;idle (hidden)
							
							HideEntity n\Collider
							HideEntity n\obj
							HideEntity n\obj2
							
							n\State2 = 0
							PositionEntity(n\Collider, 0, -100, 0)
						Case 1 ;appears briefly behind the trees
							n\DropSpeed = 0
							
							If EntityY(n\Collider)<= -100 Then
								;transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
								
								;calculate the indices of the forest cell the player is in
								x = Floor((TFormedX()+6.0)/12.0)
								z = Floor((TFormedZ()+6.0)/12.0)
								
								;step through nearby cells
								For x2 = Max(x-1,1) To Min(x+1,gridsize) Step 2
									For z2 = Max(z-1,1) To Min(z+1,gridsize) Step 2
										;choose an empty cell (not on the path)
										If fr\grid[(z2*gridsize)+x2]=0 Then
											;spawn the monster between the empty cell and the cell the player is in
											TFormPoint(((x2+x)/2)*12.0,0,((z2+z)/2)*12.0,fr\Forest_Pivot,0)
											
											;in view -> nope, keep searching for a more suitable cell
											If EntityInView(n\Collider, Camera) Then
												PositionEntity n\Collider, 0, -110, 0
												DebugLog("spawned monster in view -> hide")
											Else ; not in view -> all good
												DebugLog("spawned monster successfully")
												
												PositionEntity n\Collider, TFormedX(), EntityY(fr\Forest_Pivot,True)+2.3, TFormedZ()
												
												x2 = gridsize
												Exit												
											EndIf
										EndIf
									Next
								Next
								
								If EntityY(n\Collider)> -100 Then
									PlaySound2(Step2SFX[Rand(3,5)], Camera, n\Collider, 15.0, 0.5)
									
									If ForestNPCData[2]<>1 Then ForestNPCData[2]=0
									
									Select Rand(3)
										Case 1
											PointEntity n\Collider, Collider
											n\Frame = 2
											;SetAnimTime(n\obj, 2)
										Case 2
											PointEntity n\Collider, Collider
											n\Frame = 201
											;SetAnimTime(n\obj, 201)
										Case 3
											PointEntity n\Collider, Collider
											TurnEntity n\Collider, 0, 90, 0
											n\Frame = 299
											;SetAnimTime(n\obj, 299)
									End Select
									
									n\State2 = 0
								EndIf
							Else
								
								ShowEntity n\obj
								ShowEntity n\Collider
								
								PositionEntity n\Collider, EntityX(n\Collider), EntityY(fr\Forest_Pivot,True)+2.3, EntityZ(n\Collider)
								
								If ForestNPC<>0
									If ForestNPCData[2]=0
										Local docchance% = 0
										Local docamount% = 0
										For i = 0 To MaxItemAmount-1
											If Inventory[i]<>Null
												Local docname$ = Inventory[i]\itemtemplate\name
												If docname = "Log #1" Lor docname = "Log #2" Lor docname = "Log #3"
													;860,850,830,800
													docamount% = docamount% + 1
													docchance = docchance + 10*docamount%
												EndIf
											EndIf
										Next
										
										If Rand(1,860-docchance)=1
											ShowEntity ForestNPC
											ForestNPCData[2]=1
											If Rand(2)=1
												ForestNPCData[0]=0
											Else
												ForestNPCData[0]=2
											EndIf
											ForestNPCData[1]=0
											PositionEntity ForestNPC,EntityX(n\Collider),EntityY(n\Collider)+0.5,EntityZ(n\Collider)
											RotateEntity ForestNPC,0,EntityYaw(n\Collider),0
											MoveEntity ForestNPC,0.75,0,0
											RotateEntity ForestNPC,0,0,0
											EntityTexture ForestNPC,ForestNPCTex,ForestNPCData[0]
										Else
											ForestNPCData[2]=2
										EndIf
									ElseIf ForestNPCData[2]=1
										If ForestNPCData[1]=0.0
											If Rand(200)=1
												ForestNPCData[1]=FPSfactor
												EntityTexture ForestNPC,ForestNPCTex,ForestNPCData[0]+1
											EndIf
										ElseIf ForestNPCData[1]>0.0 And ForestNPCData[1]<5.0
											ForestNPCData[1]=Min(ForestNPCData[1]+FPSfactor,5.0)
										Else
											ForestNPCData[1]=0
											EntityTexture ForestNPC,ForestNPCTex,ForestNPCData[0]
										EndIf
									EndIf
								EndIf
								
								If n\State2 = 0 Then ;don't start moving until the player is looking
									If EntityInView(n\Collider, Camera) Then 
										n\State2 = 1
										If Rand(8)=1 Then
											PlaySound2(LoadTempSound("SFX\SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider, 20.0)
										EndIf										
									EndIf
								Else
									If n\Frame<=199 Then
										AnimateNPC(n, 2, 199, 0.5,False)
										If n\Frame=199 Then n\Frame = 298 : PlaySound2(Step2SFX[Rand(3,5)], Camera, n\Collider, 15.0)
									ElseIf n\Frame <= 297
										PointEntity n\Collider, Collider
										
										AnimateNPC(n, 200, 297, 0.5, False)
										If n\Frame=297 Then n\Frame=298 : PlaySound2(Step2SFX[Rand(3,5)], Camera, n\Collider, 15.0)
									Else
										angle = CurveAngle(point_direction(EntityX(n\Collider),EntityZ(n\Collider),EntityX(Collider),EntityZ(Collider)),EntityYaw(n\Collider)+90,20.0)
										
										RotateEntity n\Collider, 0, angle-90, 0, True
										
										AnimateNPC(n, 298, 316, n\CurrSpeed*10)
										
										;Animate2(n\obj, AnimTime(n\obj), 298, 316, n\CurrSpeed*10)
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
										
										If dist>15.0 Then
											PositionEntity n\Collider, 0,-110,0
											n\State = 0
											n\State2 = 0
										EndIf
									EndIf									
								EndIf
								
							EndIf
							
							ResetEntity n\Collider
						Case 2 ;appears on the path and starts to walk towards the player
							ShowEntity n\obj
							ShowEntity n\Collider
							
							prevFrame = n\Frame
							
							If EntityY(n\Collider)<= -100 Then
								;transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
								
								;calculate the indices of the forest cell the player is in
								x = Floor((TFormedX()+6.0)/12.0)
								z = Floor((TFormedZ()+6.0)/12.0)
								
								For x2 = Max(x-1,1) To Min(x+1,gridsize)
									For z2 = Max(z-1,1) To Min(z+1,gridsize)
										;find a nearby cell that's on the path and NOT the cell the player is in
										If fr\grid[(z2*gridsize)+x2]>0 And (x2<>x Lor z2<>z) And (x2=x Lor z2=z) Then
											
											;transform the position of the cell back to world coordinates
											TFormPoint(x2*12.0, 0,z2*12.0, fr\Forest_Pivot,0)
											
											PositionEntity n\Collider, TFormedX(), EntityY(fr\Forest_Pivot,True)+1.0,TFormedZ()
											
											DebugLog(TFormedX()+", "+TFormedZ())
											
											If EntityInView(n\Collider, Camera) Then
												BlinkTimer=-10
											Else
												x2 = gridsize
												Exit
											EndIf
										EndIf
									Next
								Next
							Else
								angle = CurveAngle(Find860Angle(n, fr),EntityYaw(n\Collider)+90,80.0)
								
								RotateEntity n\Collider, 0, angle-90, 0, True
								
								n\CurrSpeed = CurveValue(n\Speed*0.3, n\CurrSpeed, 50.0)
								MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
								
								AnimateNPC(n, 494, 569, n\CurrSpeed*25)
								
								If n\State2 = 0 Then
									If dist<8.0 Then
										If EntityInView(n\Collider,Camera) Then
											PlaySound_Strict LoadTempSound("SFX\SCP\860\Chase"+Rand(1,2)+".ogg")
											
											PlaySound2(LoadTempSound("SFX\SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider)	
											n\State2 = 1
										EndIf										
									EndIf
								EndIf
								
								If CurrSpeed > 0.03 Then ;the player is running
									n\State3 = n\State3 + FPSfactor
									If Rnd(5000)<n\State3 Then
										temp = True
										If n\SoundChn <> 0 Then
											If ChannelPlaying (n\SoundChn) Then temp = False
										EndIf
										If temp Then
											n\SoundChn = PlaySound2(LoadTempSound("SFX\SCP\860\Cancer"+Rand(0,2)+".ogg"), Camera, n\Collider)
										EndIf
									EndIf
								Else
									n\State3 = Max(n\State3 - FPSfactor,0)
								EndIf
								
								If dist<4.5 Lor n\State3 > Rnd(200,250) Then
									n\SoundChn = PlaySound2(LoadTempSound("SFX\SCP\860\Cancer"+Rand(3,5)+".ogg"), Camera, n\Collider)
									n\State = 3
								EndIf
								
								If dist > 20.0 Then
									n\State = 0
									n\State2 = 0
									PositionEntity n\Collider, 0,-110,0
								EndIf
							EndIf
							
							;535, 568
							If (prevFrame < 533 And n\Frame=>533) Lor (prevFrame > 568 And n\Frame<2) Then
								PlaySound2(Step2SFX[Rand(3,5)], Camera, n\Collider, 15.0, 0.6)
							EndIf
							
						Case 3 ;runs towards the player and attacks
							ShowEntity n\obj
							ShowEntity n\Collider
							
							prevFrame = n\Frame
							
							angle = CurveAngle(Find860Angle(n, fr),EntityYaw(n\Collider)+90,40.0)
							
							RotateEntity n\Collider, 0, angle-90, 0, True
							
							If n\Sound = 0 Then n\Sound = LoadSound_Strict("SFX\General\Slash1.ogg")
							If n\Sound2 = 0 Then n\Sound2 = LoadSound_Strict("SFX\General\Slash2.ogg")
							
							;if close enough to attack OR already attacking, play the attack anim
							If (dist<1.1 Lor (n\Frame>451 And n\Frame<493) Lor KillTimer < 0) Then
								DeathMSG = ""
								
								n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
								
								AnimateNPC(n, 451,493, 0.5, False)
								
								;Animate2(n\obj, AnimTime(n\obj), 451,493, 0.5, False)
								If (prevFrame < 461 And n\Frame=>461) Then 
									If KillTimer => 0 Then Kill() : KillAnim = 0
									PlaySound_Strict(n\Sound)
								EndIf
								If (prevFrame < 476 And n\Frame=>476) Then PlaySound_Strict(n\Sound2)
								If (prevFrame < 486 And n\Frame=>486) Then PlaySound_Strict(n\Sound2)
							Else
								n\CurrSpeed = CurveValue(n\Speed*0.8, n\CurrSpeed, 10.0)
								
								AnimateNPC(n, 298, 316, n\CurrSpeed*10)
								;Animate2(n\obj, AnimTime(n\obj), 298, 316, n\CurrSpeed*10)
								
								If (prevFrame < 307 And n\Frame=>307) Then
									PlaySound2(Step2SFX[Rand(3,5)], Camera, n\Collider, 10.0)
								EndIf
							EndIf
							
							MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
					End Select
					
					If n\State <> 0 Then
						RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True	
						
						PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider)-0.1, EntityZ(n\Collider))
						RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), EntityRoll(n\Collider), True
						
						If dist > 8.0 Then
							ShowEntity n\obj2
							EntityAlpha n\obj2, Min(dist-8.0,1.0)
							
							PositionEntity(n\obj2, EntityX(n\obj), EntityY(n\obj) , EntityZ(n\obj))
							RotateEntity(n\obj2, 0, EntityYaw(n\Collider) - 180, 0)
							MoveEntity(n\obj2, 0, 30.0*0.025, -33.0*0.025)
							
							;render distance is set to 8.5 inside the forest,
							;so we need to cheat a bit to make the eyes visible if they're further than that
							pvt = CreatePivot()
							PositionEntity pvt, EntityX(Camera),EntityY(Camera),EntityZ(Camera)
							PointEntity pvt, n\obj2
							MoveEntity pvt, 0,0,8.0
							PositionEntity n\obj2, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
							pvt = FreeEntity_Strict(pvt)
						Else
							HideEntity n\obj2
						EndIf
					EndIf
				EndIf
				;[End Block] 
			Case NPCtype939
				UpdateNPCtype939(n)
			Case NPCtype066
				;[Block]
				dist = DistanceSquared(EntityX(Collider),EntityX(n\Collider),EntityZ(Collider),EntityZ(n\Collider))
				
				Select n\State
					Case 0 
						;idle: moves around randomly from waypoint to another if the player is far enough
						;starts staring at the player when the player is close enough
						
						If dist > PowTwo(20.0) Then
							AnimateNPC(n, 451, 612, 0.2, True)
							;Animate2(n\obj, AnimTime(n\obj), 451, 612, 0.2, True)
							
							If n\State2 < MilliSecs() Then
								For w.WayPoints = Each WayPoints
									If w\door = Null Then
										If Abs(EntityX(w\obj,True)-EntityX(n\Collider))<4.0 Then
											If Abs(EntityZ(w\obj,True)-EntityZ(n\Collider))<4.0 Then
												PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.3, EntityZ(w\obj,True)
												ResetEntity n\Collider
												Exit
											EndIf
										EndIf
									EndIf
								Next
								n\State2 = MilliSecs()+5000
							EndIf
						ElseIf dist < PowTwo(8.0)
							n\LastDist = Rnd(1.0, 2.5)
							n\State = 1
						EndIf
					Case 1 ;staring at the player
						
						If n\Frame<451 Then
							angle = WrapAngle(CurveAngle(DeltaYaw(n\Collider, Collider)-180, (AnimTime(n\obj)-2.0)/1.2445, 15.0))
							;0->360 = 2->450
							SetNPCFrame(n,angle*1.2445+2.0)
							
							;SetAnimTime(n\obj, angle*1.2445+2.0)							
						Else
							AnimateNPC(n, 636, 646, 0.4, False)
							If n\Frame = 646 Then SetNPCFrame(n,2)
							;Animate2(n\obj, AnimTime(n\obj), 636, 646, 0.4, False)
							;If AnimTime(n\obj)=646 Then SetAnimTime (n\obj, 2)
						EndIf
						
						If Rand(700)=1 Then PlaySound2(LoadTempSound("SFX\SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
						
						If dist < PowTwo(1.0+n\LastDist) Then n\State = Rand(2,3)
					Case 2 ;roll towards the player and make a sound, and then escape	
						If n\Frame < 647 Then 
							angle = CurveAngle(0, (AnimTime(n\obj)-2.0)/1.2445, 5.0)
							
							If angle < 5 Lor angle > 355 Then 
								SetNPCFrame(n,647)
							Else
								SetNPCFrame(n,angle*1.2445+2.0)
							EndIf
							;SetAnimTime(n\obj, angle*1.2445+2.0)
							;If angle < 5 Lor angle > 355 Then SetAnimTime(n\obj, 647)
						Else
							If n\Frame=683 Then 
								If n\State2 = 0 Then
									If Rand(2)=1 Then
										PlaySound2(LoadTempSound("SFX\SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
									Else
										PlaySound2(LoadTempSound("SFX\SCP\066\Notes"+Rand(1,6)+".ogg"), Camera, n\Collider, 8.0)
									EndIf									
									
									Select Rand(1,6)
										Case 1
											If n\Sound2=0 Then n\Sound2=LoadSound_Strict("SFX\SCP\066\Beethoven.ogg")
											n\SoundChn2 = PlaySound2(n\Sound2, Camera, n\Collider)
											DeafTimer# = 70*(45+(15*SelectedDifficulty\aggressiveNPCs))
											DeafPlayer = True
											CameraShake = 10.0
										Case 2
											n\State3 = Rand(700,1400)
										Case 3
											For d.Doors = Each Doors
												If d\locked = False And d\KeyCard = 0 And d\Code = "" Then
													If Abs(EntityX(d\frameobj)-EntityX(n\Collider))<16.0 Then
														If Abs(EntityZ(d\frameobj)-EntityZ(n\Collider))<16.0 Then
															UseDoor(d, False)
														EndIf
													EndIf
												EndIf
											Next
										Case 4
											If PlayerRoom\RoomTemplate\DisableDecals = False Then
												CameraShake = 5.0
												de.Decals = CreateDecal(DECAL_CRACKS, EntityX(n\Collider), 0.01, EntityZ(n\Collider), 90, Rand(360), 0)
												de\Size = 0.3 : UpdateDecals
												PlaySound_Strict(LoadTempSound("SFX\General\BodyFall.ogg"))
												If DistanceSquared(EntityX(Collider),EntityX(n\Collider),EntityZ(Collider),EntityZ(n\Collider))<PowTwo(0.8) Then
													;Injuries = Injuries + Rnd(0.3,0.5)
													DamageSPPlayer(Rnd(5.0,10.0), True)
												EndIf
											EndIf
									End Select
								EndIf
								
								n\State2 = n\State2+FPSfactor
								If n\State2>70 Then 
									n\State = 3
									n\State2 = 0
								EndIf
							Else
								n\CurrSpeed = CurveValue(n\Speed*1.5, n\CurrSpeed, 10.0)
								PointEntity n\obj, Collider
								;angle = CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj)-180, EntityYaw(n\Collider), 10), 0
								
								AnimateNPC(n, 647, 683, n\CurrSpeed*25, False)
								;Animate2(n\obj, AnimTime(n\obj), 647, 683, n\CurrSpeed*25, False)
								
								MoveEntity n\Collider, 0,0,-n\CurrSpeed*FPSfactor
								
							EndIf
						EndIf
					Case 3
						PointEntity n\obj, Collider
						angle = CurveAngle(EntityYaw(n\obj)+n\Angle-180, EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
						RotateEntity n\Collider, 0, angle, 0
						
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
						MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
						
						;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						
						If Rand(100)=1 Then n\Angle = Rnd(-20,20)
						
						n\State2 = n\State2 + FPSfactor
						If n\State2>250 Then 
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25, False)
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25, False)
							If n\Frame=647 Then 
								n\State = 0
								n\State2=0
							EndIf
						Else
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25)
							
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						EndIf
						
				End Select
				
				If n\State > 1 Then
					If n\Sound = 0 Then n\Sound = LoadSound_Strict("SFX\SCP\066\Rolling.ogg")
					If n\SoundChn<>0 Then
						If ChannelPlaying(n\SoundChn) Then
							n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 20)
						EndIf
					Else
						n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider, 20)
					EndIf					
				EndIf
				
				If n\State3 > 0 Then
					n\State3 = n\State3-FPSfactor
					LightVolume = TempLightVolume-TempLightVolume*Min(Max(n\State3/500,0.01),0.6)
					HeartBeatRate = Max(HeartBeatRate, 130)
					HeartBeatVolume = Max(HeartBeatVolume,Min(n\State3/1000,1.0))
				EndIf
				
				If ChannelPlaying(n\SoundChn2)
					UpdateSoundOrigin(n\SoundChn2,Camera,n\Collider,20,1.0,True)
					BlurTimer = Max((5.0-Distance(EntityX(Collider),EntityX(n\Collider),EntityZ(Collider),EntityZ(n\Collider)))*300,0)
				EndIf
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				RotateEntity n\obj, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), 0
				;[End Block]
			Case NPCtype966
				UpdateNPCtype966(n)
			Case NPCtype1048a
				;[Block]
				Select n\State	
						
					Case 1
						n\PathStatus = FindPath(n, n\EnemyX,n\EnemyY+0.1,n\EnemyZ)
						;649, 677
				End Select
				;[End block]
			Case NPCtype1499
				;[Block]
				;n\State: Current State of the NPC
				;n\State2: A second state variable (dependend on the current NPC's n\State)
				
				prevFrame# = n\Frame
				
				If (Not n\Idle) And EntityDistanceSquared(n\Collider,Collider)<PowTwo(HideDistance*3)
					If n\State = 0 Lor n\State = 2
						For n2.NPCs = Each NPCs
							If n2\NPCtype = n\NPCtype And n2 <> n
								If n2\State <> 0 And n2\State <> 2
									n\State = 1
									n\State2 = 0
									Exit
								EndIf
							EndIf
						Next
					EndIf
					
					Select n\State
						Case 0
							If n\CurrSpeed = 0.0
								If n\State2 < 500.0*Rnd(1,3)
									n\CurrSpeed = 0.0
									n\State2 = n\State2 + FPSfactor
								Else
									If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
								EndIf
							Else
								If n\State2 < 10000.0*Rnd(1,3)
									n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
									n\State2 = n\State2 + FPSfactor
								Else
									n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,50.0)
								EndIf
								
								RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
								
								If Rand(200) = 1 Then n\Angle = n\Angle + Rnd(-45,45)
								
								HideEntity n\Collider
								EntityPick(n\Collider, 1.5)
								If PickedEntity() <> 0 Then
									n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
								EndIf
								ShowEntity n\Collider
							EndIf
							
							If n\CurrSpeed = 0.0
								AnimateNPC(n,296,317,0.2)
							Else
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
							EndIf
							
							;randomly play the "screaming animation" and revert back to state 0
							If (Rand(5000)=1) Then
								n\State = 2
								n\State2 = 0
								
								If Not ChannelPlaying(n\SoundChn) Then
									If (EntityDistanceSquared(n\Collider,Collider) < PowTwo(20.0)) Then
										If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
										n\Sound = LoadSound_Strict("SFX\SCP\1499\Idle"+Rand(1,4)+".ogg")
										n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
									EndIf
								EndIf
							EndIf
							
							If (n\ID Mod 2 = 0) And (Not NoTarget) Then
								dist = EntityDistanceSquared(n\Collider,Collider)
								If dist < PowTwo(10.0) Then
									If EntityVisible(n\Collider,Collider) Then
										;play the "screaming animation"
										n\State = 2
										If dist < PowTwo(5.0) Then
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
											n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
											
											n\State2 = 1 ;if player is too close, switch to attack after screaming
											
											For n2.NPCs = Each NPCs
												;If n2\NPCtype = n\NPCtype And n2 <> n And (n\ID Mod 2 = 0) Then
												If n2\NPCtype = n\NPCtype And n2 <> n
													n2\State = 1
													n2\State2 = 0
												EndIf
											Next
										Else
											n\State2 = 0 ;otherwise keep idling
										EndIf
										
										n\Frame = 203
									EndIf
								EndIf
							EndIf
						Case 1 ;attacking the player
							If NoTarget Then n\State = 0
							
							If PlayerRoom\RoomTemplate\Name = "dimension1499"
								If Music[19]=0 Then Music[19] = LoadSound_Strict("SFX\Music\1499Danger.ogg")
								ShouldPlay = 19
							EndIf
							
							PointEntity n\obj,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
							
							If n\State2 = 0.0
								n\CurrSpeed = CurveValue(n\Speed*1.75,n\CurrSpeed,10.0)
								
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
							EndIf
							
							If EntityDistanceSquared(n\Collider,Collider) < PowTwo(0.75)
								If (n\ID Mod 2 = 0) Lor n\State3 = 1
									n\State2 = Rand(1,2)
									n\State = 3
									If n\State2 = 1
										SetNPCFrame(n,63)
									Else
										SetNPCFrame(n,168)
									EndIf
								Else
									n\State = 4
								EndIf
							EndIf
						Case 2 ;play the "screaming animation" and switch to n\state2 after it's finished
							n\CurrSpeed = 0.0
							AnimateNPC(n,203,295,0.1,False)
							
							If n\Frame > 294.0 Then
								n\State = n\State2
							EndIf
						Case 3 ;slashing at the player
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							dist = EntityDistanceSquared(n\Collider,Collider)
							If n\State2 = 1 Then
								AnimateNPC(n,63,100,0.6,False)
								If prevFrame < 89 And n\Frame=>89
									If dist > PowTwo(0.85) Lor Abs(DeltaYaw(n\Collider,Collider))>60.0 Then
										;Miss
									Else
										;Injuries = Injuries + Rnd(0.75,1.5)
										DamageSPPlayer(Rnd(15.0,25.0))
										PlaySound2(LoadTempSound("SFX\General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If (Not IsSPPlayerAlive()) Then
											Kill()
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class-B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [REDACTED]."
											Else
												DeathMSG = "An unidentified male and the deceased "+Designation+" were discovered in [REDACTED] by the Nine-Tailed Fox. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 99 Then
									n\State2 = 0.0
									n\State = 1
								EndIf
							Else
								AnimateNPC(n,168,202,0.6,False)
								If prevFrame < 189 And n\Frame=>189 Then
									If dist > PowTwo(0.85) Lor Abs(DeltaYaw(n\Collider,Collider))>60.0 Then
										;Miss
									Else
										;Injuries = Injuries + Rnd(0.75,1.5)
										DamageSPPlayer(Rnd(15.0,25.0))
										PlaySound2(LoadTempSound("SFX\General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If (Not IsSPPlayerAlive()) Then
											Kill()
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class-B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [REDACTED]."
											Else
												DeathMSG = "An unidentified male and the deceased "+Designation+" were discovered in [REDACTED] by the Nine-Tailed Fox. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 201 Then
									n\State2 = 0.0
									n\State = 1
								EndIf
							EndIf
						Case 4 ;standing in front of the player
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							AnimateNPC(n,296,317,0.2)
							
							PointEntity n\obj,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
							
							If EntityDistanceSquared(n\Collider,Collider) > PowTwo(0.85)
								n\State = 1
							EndIf
					End Select
					
					If n\SoundChn <> 0 And ChannelPlaying(n\SoundChn) Then
						UpdateSoundOrigin(n\SoundChn,Camera,n\Collider,20.0)
					EndIf
					
					MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
					
					RotateEntity n\obj,0,EntityYaw(n\Collider)-180,0
					PositionEntity n\obj,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
					
					ShowEntity n\obj
				Else
					HideEntity n\obj
				EndIf
				
				;[End Block]
			Case NPCtype008
				;[Block]
				;n\State: Main State
				;n\State2: A timer used for the player detection
				;n\State3: A timer for making the NPC idle (if the player escapes during that time)
				
				If (Not n\IsDead)
					If n\State = 0
						EntityType n\Collider,HIT_DEAD
					Else
						EntityType n\Collider,HIT_PLAYER
					EndIf
					
					prevFrame = n\Frame
					
					n\BlinkTimer = 1
					
					Select n\State
						Case 0 ;Lying next to the wall
							SetNPCFrame(n,11)
						Case 1 ;Standing up
							AnimateNPC(n,11,32,0.1,False)
							If n\Frame => 29
								n\State = 2
							EndIf
						Case 2 ;Being active
							PlayerSeeAble = MeNPCSeesPlayer(n)
							If PlayerSeeAble=1 Lor n\State2 > 0.0
								If PlayerSeeAble=1
									n\State2 = 70*2
								Else
									n\State2 = Max(n\State2-FPSfactor,0)
								EndIf
								PointEntity n\obj, Collider
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
								
								AnimateNPC(n, 64, 93, n\CurrSpeed*30)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
								
								If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.0)
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										n\State = 3
									EndIf
								EndIf
								
								n\PathTimer = 0
								n\PathStatus = 0
								n\PathLocation = 0
								n\State3 = 0
							Else
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity n\obj, n\Path[n\PathLocation]\obj
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
										
										AnimateNPC(n, 64, 93, n\CurrSpeed*30)
										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
										
										;opens doors in front of him
										dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
										If dist2<PowTwo(0.6) Then
											temp = True
											If n\Path[n\PathLocation]\door <> Null Then
												If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
													If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\KeyCard>0 Lor n\Path[n\PathLocation]\door\Code<>"" Then
														temp = False
													Else
														If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
													EndIf
												EndIf
											EndIf
											If dist2<PowTwo(0.2) And temp
												n\PathLocation = n\PathLocation + 1
											ElseIf dist2<PowTwo(0.5) And (Not temp)
												n\PathStatus = 0
												n\PathTimer# = 0.0
											EndIf
										EndIf
										
										;If Rand(100)=3
										;	n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										;EndIf
									EndIf
								Else
									AnimateNPC(n, 323, 344, 0.2, True)
									n\CurrSpeed = 0
									If n\PathTimer < 70*5
										n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*FPSfactor
									Else
										n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										n\PathTimer = 0
									EndIf
								EndIf
								
								If EntityDistanceSquared(n\Collider,Collider)>PowTwo(HideDistance)
									If n\State3 < 70*(15+(10*SelectedDifficulty\aggressiveNPCs))
										n\State3 = n\State3+FPSfactor
									Else
										DebugLog "SCP-008-1 IDLE"
										n\State3 = 70*(6*60)
										n\State = 4
									EndIf
								EndIf
							EndIf
							
							If n\CurrSpeed > 0.005 Then
								If (prevFrame < 80 And n\Frame=>80) Lor (prevFrame > 92 And n\Frame<65)
									PlaySound2(StepSFX(0,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
								EndIf
							EndIf
							
							n\SoundChn = LoopSound2(n\Sound,n\SoundChn,Camera,n\Collider)
						Case 3 ;Attacking
							AnimateNPC(n, 126, 165, 0.4, False)
							If (n\Frame => 146 And prevFrame < 146)
								If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.1)
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										PlaySound_Strict DamageSFX[Rand(5,8)]
										;Injuries = Injuries+Rnd(0.4,1.0)
										DamageSPPlayer(Rnd(10.0,20.0))
										Infect = Infect + (1+(1*SelectedDifficulty\aggressiveNPCs))
										DeathMSG = Designation+". Cause of death: multiple lacerations and severe blunt force trauma caused by [DATA EXPUNGED], who was infected with SCP-008. Said subject was located by Nine-Tailed Fox and terminated."
									EndIf
								EndIf
							ElseIf n\Frame => 164
								If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.1)
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										SetNPCFrame(n,126)
									Else
										n\State = 2
									EndIf
								Else
									n\State = 2
								EndIf
							EndIf
						Case 4 ;Idling
							HideEntity n\obj
							HideEntity n\Collider
							n\DropSpeed = 0
							PositionEntity n\Collider,0,500,0
							ResetEntity n\Collider
							If n\Idle > 0
								n\Idle = Max(n\Idle-(1+(1*SelectedDifficulty\aggressiveNPCs))*FPSfactor,0)
							Else
								If PlayerInReachableRoom() ;Player is in a room where SCP-008-1 can teleport to
									If Rand(50-(20*SelectedDifficulty\aggressiveNPCs))=1
										ShowEntity n\Collider
										ShowEntity n\obj
										For w.WayPoints = Each WayPoints
											If w\door=Null And w\room\dist < HideDistance And Rand(3)=1 Then
												If EntityDistanceSquared(w\room\obj,n\Collider)<EntityDistanceSquared(Collider,n\Collider)
													x = Abs(EntityX(n\Collider)-EntityX(w\obj,True))
													If x < 12.0 And x > 4.0 Then
														z = Abs(EntityZ(n\Collider)-EntityZ(w\obj,True))
														If z < 12 And z > 4.0 Then
															If w\room\dist > 4
																DebugLog "MOVING 008-1 TO "+w\room\RoomTemplate\Name
																PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True)
																ResetEntity n\Collider
																n\PathStatus = 0
																n\PathTimer# = 0.0
																n\PathLocation = 0
																Exit
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
										Next
										n\State = 2
										n\State3 = 0
									EndIf
								EndIf
							EndIf
					End Select
				Else
					If n\SoundChn <> 0
						StopChannel n\SoundChn
						n\SoundChn = 0
						FreeSound_Strict n\Sound
						n\Sound = 0
					EndIf
					AnimateNPC(n, 344, 363, 0.5, False)
				EndIf
				
				If n\HP <= 0 Then
					n\IsDead = True
				EndIf
				
				RotateEntity n\obj,0,EntityYaw(n\Collider)-180,0
				PositionEntity n\obj,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
				;[End Block]
			;new NPCs in the SCP:NTF mod
			Case NPCtypeD2
				;[Block]
				UpdateNPCtypeD2(n)
				;[End Block]
			Case NPCtype076
				;[Block]
				RotateEntity(n\Collider, 0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				
				If n\HP > 0 Then
					n\State2 = n\State2 + FPSfactor
					DebugLog n\State2
				EndIf
				
				If n\HP > 0 Then
					If n\State2 < 1310 Then
						If n\SoundChn = 0
							n\SoundChn = StreamSound_Strict("SFX\Music\076FightStart.ogg")
							n\SoundChn_IsStream = True
						Else
							UpdateStreamSoundOrigin(n\SoundChn,Camera,Collider,8.0,1.0)
						EndIf
					Else
						StopStream_Strict(n\SoundChn) : n\SoundChn = 0
						If n\SoundChn2 = 0
							n\SoundChn2 = StreamSound_Strict("SFX\Music\076FightContinue.ogg")
							n\SoundChn2_IsStream = True
						Else
							UpdateStreamSoundOrigin(n\SoundChn2,Camera,Collider,8.0,1.0)
						EndIf
					EndIf
				Else
					StopStream_Strict(n\SoundChn2) : n\SoundChn2 = 0
					StopStream_Strict(n\SoundChn) : n\SoundChn = 0
				EndIf
				
				prevFrame = AnimTime(n\obj)
				
				ShouldPlay = 66
				
				If n\State2 = 1300 Then
					n\SoundChn = 0
				EndIf
				
				If n\HP > 0
					Select n\State
						Case 0 ;idle
							If EntityDistanceSquared(n\Collider, Collider) < PowTwo(1.1) Then
								n\State = 1
							Else
								PointEntity n\Collider, Collider
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10), 0, True)
								PointEntity n\Collider, Collider
								AnimateNPC(n, 61, 112, n\CurrSpeed*30)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
							EndIf
						Case 1 ;attacking
							AnimateNPC(n, 113, 151, 0.2, True)
							n\CurrSpeed = 0
							If (n\Frame => 127 And prevFrame < 127) Lor (n\Frame => 135 And prevFrame < 135)
								If EntityDistanceSquared(n\Collider, Collider) < PowTwo(1.1) Then
									PlaySound_Strict DamageSFX[Rand(5,8)]
									;Injuries = Injuries+Rnd(0.4,1.0)
									DamageSPPlayer(Rnd(15.0,25.0))
									DeathMSG = Chr(34)+"SCP-076-2 breached his containment chamber during the containment breach event. We flooded the entire Containment Area 25b in order "
									DeathMSG = DeathMSG+"to neutralize SCP-076-2. It had been confirmed that "+Designation+" entered the site, but he never came out. We think that he got "
									DeathMSG = DeathMSG+"killed by SCP-076-2."+Chr(34)
								EndIf
							ElseIf n\Frame => 150
								n\State = 0
							EndIf
					End Select
					
					If n\State = 0
						If n\CurrSpeed > 0.01 Then
							If prevFrame < 244 And AnimTime(n\obj)=>244 Then
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))						
							ElseIf prevFrame < 256 And AnimTime(n\obj)=>256
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					EndIf
				Else
					n\IsDead = True
					SetNPCFrame(n,0)
				EndIf
				
				If n\Frame = 19 Lor n\Frame = 60
					n\IsDead = True
				EndIf
				If AnimTime(n\obj)=19 Lor AnimTime(n\obj)=60
					n\IsDead = True
				EndIf
				
				If n\IsDead = False Then
					MoveEntity(n\Collider, 0, 0, n\CurrSpeed * FPSfactor)
					
					PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
					
					RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider)-180.0, 0
				EndIf
				;[End Block]	
			Case NPCtypeSci	;------------------------------------------------------------------------------------------------------------------
				;This will be added in future versions
				;UpdateNPCtypeScientist(n)
			Case NPCtypeD9341 ;------------------------------------------------------------------------------------------------------------------
				;[Block]
				RotateEntity(n\Collider, 0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				
				prevFrame = AnimTime(n\obj)
				n\BlinkTimer = 1.0
					Select n\State
						Case 0 ;idle
							AnimateNPC(n, 210, 235, 0.1)
						Case 1 ;following path (Running)
							If n\PathStatus = 2 Then
								n\State = 0
								n\CurrSpeed = 0
							ElseIf n\PathStatus = 1
								If n\Path[n\PathLocation]=Null Then 
									If n\PathLocation > 19 Then 
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									PointEntity n\obj, n\Path[n\PathLocation]\obj
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
									
									n\CurrSpeed = CurveValue(0.03, n\CurrSpeed, 5.0)
									AnimateNPC(n, 301, 319, n\CurrSpeed * 18)
									MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
									
									dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
									If dist2<PowTwo(0.6) Then
										temp = True
										If n\Path[n\PathLocation]\door <> Null Then
											If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
												If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>"" Then
													temp = False
												Else
													If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
												EndIf
											EndIf
										EndIf
										If dist2<PowTwo(0.2) And temp
											n\PathLocation = n\PathLocation + 1
										ElseIf dist2<PowTwo(0.5) And (Not temp)
											n\PathStatus = 0
											n\PathTimer# = 0.0
										EndIf
									EndIf
								EndIf
							Else
								n\CurrSpeed = 0
								n\State = 0
							EndIf
						Case 2 ;following path (Walking)
							If n\PathStatus = 2 Then
								n\State = 0
								n\CurrSpeed = 0
							ElseIf n\PathStatus = 1
								If n\Path[n\PathLocation]=Null Then 
									If n\PathLocation > 19 Then 
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									PointEntity n\obj, n\Path[n\PathLocation]\obj
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
									
									n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
									AnimateNPC(n, 236, 260, n\CurrSpeed * 18)
									MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
									
									dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
									If dist2<PowTwo(0.6) Then
										temp = True
										If n\Path[n\PathLocation]\door <> Null Then
											If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
												If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>"" Then
													temp = False
												Else
													If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
												EndIf
											EndIf
										EndIf
										If dist2<PowTwo(0.2) And temp
											n\PathLocation = n\PathLocation + 1
										ElseIf dist2<PowTwo(0.5) And (Not temp)
											n\PathStatus = 0
											n\PathTimer# = 0.0
										EndIf
									EndIf
								EndIf
							Else
								n\CurrSpeed = 0
								n\State = 0
							EndIf
					End Select
					
					If n\State = 1
						If n\CurrSpeed < 0.01 Then
							If prevFrame < 309 And AnimTime(n\obj)=>309
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))						
							ElseIf prevFrame =< 319 And AnimTime(n\obj)=<301
								PlaySound2(StepSFX(GetStepSound(n\Collider),0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
							EndIf
						EndIf
					EndIf
				
				If n\Frame = 19 Lor n\Frame = 60
					n\IsDead = True
				EndIf
				If AnimTime(n\obj)=19 Lor AnimTime(n\obj)=60
					n\IsDead = True
				EndIf
				
				PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity n\obj, EntityPitch(n\Collider), EntityYaw(n\Collider)-180.0, 0
				;[End Block]	
			Case NPCtype682
				UpdateNPCtype682(n)
			Case NPCtype457
				UpdateNPCtype457(n)
		End Select
		
		If n\IsDead
			EntityType n\Collider,HIT_DEAD
		EndIf
		
		If DistanceSquared(EntityX(Collider),EntityX(n\Collider),EntityZ(Collider),EntityZ(n\Collider))<PowTwo(HideDistance*0.7) Lor n\NPCtype = NPCtype1499 Then
			If n\InFacility = InFacility
				TranslateEntity n\Collider, 0, n\DropSpeed, 0
				
				Local CollidedFloor% = False
				For i% = 1 To CountCollisions(n\Collider)
					If CollisionY(n\Collider, i) < EntityY(n\Collider) - 0.01 Then CollidedFloor = True : Exit
				Next
				
				If CollidedFloor = True Then
					n\DropSpeed# = 0
				Else
					If ShouldEntitiesFall
						Local UpdateGravity% = False
						Local MaxX#,MinX#,MaxZ#,MinZ#
						If n\InFacility=1
							For e.Events = Each Events
								If e\EventName = "cont_860" Then
									If e\EventState = 1.0
										UpdateGravity = True
										Exit
									EndIf
								EndIf
							Next
							If (Not UpdateGravity)
								For r.Rooms = Each Rooms
									If r\MaxX<>0 Lor r\MinX<>0 Lor r\MaxZ<>0 Lor r\MinZ<>0
										MaxX# = r\MaxX
										MinX# = r\MinX
										MaxZ# = r\MaxZ
										MinZ# = r\MinZ
									Else
										MaxX# = 4.0
										MinX# = 0.0
										MaxZ# = 4.0
										MinZ# = 0.0
									EndIf
									If Abs(EntityX(n\Collider)-EntityX(r\obj))<=Abs(MaxX-MinX)
										If Abs(EntityZ(n\Collider)-EntityZ(r\obj))<=Abs(MaxZ-MinZ)
											If r=PlayerRoom
												UpdateGravity = True
												Exit
											EndIf
											If IsRoomAdjacent(PlayerRoom,r)
												UpdateGravity = True
												Exit
											EndIf
											For i=0 To 3
												If (IsRoomAdjacent(PlayerRoom\Adjacent[i],r))
													UpdateGravity = True
													Exit
												EndIf
											Next
										EndIf
									EndIf
								Next
							EndIf
						Else
							UpdateGravity = True
						EndIf
						If UpdateGravity
							n\DropSpeed# = Max(n\DropSpeed - 0.005*FPSfactor*n\GravityMult,-n\MaxGravity)
						Else
							If n\FallingPickDistance>0
								n\DropSpeed = 0.0
							Else
								n\DropSpeed# = Max(n\DropSpeed - 0.005*FPSfactor*n\GravityMult,-n\MaxGravity)
							EndIf
						EndIf
					Else
						n\DropSpeed# = 0.0
					EndIf
				EndIf
			Else
				n\DropSpeed = 0
			EndIf
		Else
			n\DropSpeed = 0
		EndIf
		
		ShowNPCHitBoxes(n)
		
		CatchErrors(Chr(34)+n\NVName+Chr(34)+" NPC")
		
	Next
	
End Function

Function TeleportCloser(n.NPCs)
	Local closestDist# = 0
	Local closestWaypoint.WayPoints
	Local w.WayPoints
	
	Local xtemp#, ztemp#
	
	For w.WayPoints = Each WayPoints
		If w\door = Null Then
			xtemp = Abs(EntityX(w\obj,True)-EntityX(n\Collider,True))
			If xtemp < 10.0 And xtemp > 1.0 Then 
				ztemp = Abs(EntityZ(w\obj,True)-EntityZ(n\Collider,True))
				If ztemp < 10.0 And ztemp > 1.0 Then
					If (EntityDistanceSquared(Collider, w\obj)>PowTwo(16-(8*SelectedDifficulty\aggressiveNPCs))) Then
						;teleports to the nearby waypoint that takes it closest to the player
						Local newDist# = EntityDistanceSquared(Collider, w\obj)
						If (newDist < closestDist Lor closestWaypoint = Null) Then
							closestDist = newDist	
							closestWaypoint = w
						EndIf						
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	Local shouldTeleport% = False
	If (closestWaypoint<>Null) Then
		If n\InFacility <> 1 Lor SelectedDifficulty\aggressiveNPCs Then
			shouldTeleport = True
		ElseIf EntityY(closestWaypoint\obj,True)<=7.0 And EntityY(closestWaypoint\obj,True)>=-10.0 Then
			shouldTeleport = True
		EndIf
		
		If shouldTeleport Then
			PositionEntity n\Collider, EntityX(closestWaypoint\obj,True), EntityY(closestWaypoint\obj,True)+0.15, EntityZ(closestWaypoint\obj,True), True
			ResetEntity n\Collider
			n\PathStatus = 0
			n\PathTimer# = 0.0
			n\PathLocation = 0
		EndIf
	EndIf
	
End Function

Function OtherNPCSeesMeNPC%(me.NPCs,other.NPCs)
	If other\BlinkTimer<=0.0 Then Return False
	
	If EntityDistanceSquared(other\Collider,me\Collider)<PowTwo(6.0) Then
		If Abs(DeltaYaw(other\Collider,me\Collider))<60.0 Then
			Return True
		EndIf
		If EntityDistanceSquared(other\Collider,me\Collider)<PowTwo(1.5) Then
			Return True
		EndIf
	EndIf
	Return False
End Function

Function MeNPCSeesPlayer%(me.NPCs,disablesoundoncrouch%=False,disablesounddetect%=False)
	;Return values:
		;False (=0): Player is not detected anyhow
		;True (=1): Player is detected by vision
		;2: Player is detected by emitting a sound
		;3: Player is detected by a camera (only for MTF Units!)
		;4: Player is detected through glass
	
	If NoTarget Then Return False
	
	PlayerDetected = False
	If (Not PlayerDetected) Lor me\NPCtype <> NPCtypeMTF
		If me\BlinkTimer<=0.0 Then Return False
		If EntityDistanceSquared(Collider,me\Collider)>PowTwo(8.0-CrouchState+PlayerSoundVolume) Then Return False
		
		;spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume>1.0
			If (Abs(DeltaYaw(me\Collider,Collider))>60.0) And EntityVisible(me\Collider,Collider)
				Return 1
			ElseIf (Not EntityVisible(me\Collider,Collider))
				If (disablesoundoncrouch% And Crouch%) Lor disablesounddetect%
					Return False
				Else
					Return 2
				EndIf
			EndIf
		Else
			If (Abs(DeltaYaw(me\Collider,Collider))>60.0) Then Return False
		EndIf
		Return EntityVisible(me\Collider,Collider)
	Else
		If EntityDistanceSquared(Collider,me\Collider)>PowTwo(8.0-CrouchState+PlayerSoundVolume) Then Return 3
		If EntityVisible(me\Collider, Camera) Then Return True
		
		;spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume>1.0 Then Return 2
		Return 3
	EndIf
	
End Function

;This is useless for the current MTF. -Meow
;
;Function TeleportMTFGroup(n.NPCs)
;	Local n2.NPCs
;	
;	If n\MTFLeader <> Null Then Return
;	
;	TeleportCloser(n)
;	
;	For n2 = Each NPCs
;		If n2\NPCtype = NPCtypeMTF
;			If n2\MTFLeader <> Null
;				PositionEntity n2\Collider,EntityX(n2\MTFLeader\Collider),EntityY(n2\MTFLeader\Collider)+0.1,EntityZ(n2\MTFLeader\Collider)
;			EndIf
;		EndIf
;	Next
;	
;	DebugLog "Teleported MTF Group (dist:"+EntityDistance(n\Collider,Collider)+")"
;	
;End Function

Function UpdateMTFUnit(n.NPCs)
	;[Block]
	
	If n\NPCtype<>NPCtypeMTF Then
		Local realType$ = ""
		Select n\NPCtype
			Case NPCtype173
                realType = "173"
			Case NPCtypeOldMan
                realType = "106"
			Case NPCtypeGuard
                realType = "guard"
			Case NPCtypeD
                realType = "d"
			Case NPCtype372
                realType = "372"
			Case NPCtypeApache
                realType = "apache"
			Case NPCtype096
                realType = "096"
			Case NPCtype049
                realType = "049"
			Case NPCtypeZombie
                realType = "zombie"
			Case NPCtype5131
                realType = "513-1"
			Case NPCtypeTentacle
                realType = "tentacle"
			Case NPCtype860
                realType = "860"
			Case NPCtype939
                realType = "939"
			Case NPCtype066
                realType = "066"
			Case NPCtype178
                realType = "178"
			Case NPCtypePdPlane
                realType = "PDPlane"
			Case NPCtype966
                realType = "966"
			Case NPCtype1048a
                realType = "1048-A"
			Case NPCtype1499
				realType = "1499-1"
		End Select
		RuntimeError "Called UpdateMTFUnit on "+realType
	EndIf
	;[End Block]
	
	Return
	;[Block]
;	Local x#,y#,z#
;	Local r.Rooms,w.WayPoints
;	Local prevDist#,newDist#
;	Local n2.NPCs
;	Local temp, PlayerSeeAble, sfxstep
;	
;	Local p.Particles, target, dist#, dist2#
;	
;	If n\IsDead Then
;		n\BlinkTimer = -1.0
;		SetNPCFrame(n, 532)
;		Return
;	EndIf
;	
;	n\MaxGravity = 0.03
;	
;	n\BlinkTimer = n\BlinkTimer - FPSfactor
;	DebugLog n\BlinkTimer
;	If n\BlinkTimer<=-5.0 Then 
;		;only play the "blinking" sound clip if searching/containing 173
;		If n\State = 2
;			If OtherNPCSeesMeNPC(Curr173,n)
;				PlayMTFSound(LoadTempSound("SFX\Character\MTF\173\BLINKING.ogg"),n)
;			EndIf
;		EndIf
;		n\BlinkTimer = 70.0*Rnd(10.0,15.0)
;	EndIf	
;	n\State3 = 60*Rnd(10,25)
;	DebugLog n\State3
;	
;	
;	n\Reload = n\Reload - FPSfactor
;	If Int(n\State)<>1 Then n\State2 = n\State2 - FPSfactor
;	
;	Local prevFrame# = n\Frame
;	
;	n\BoneToManipulate = ""
;	n\ManipulateBone = False
;	n\ManipulationType = 0
;	n\NPCNameInSection = "MTF"
;	
;	Local NPCSeeAble% = False
;	
;	If Int(n\State) <> 1 Then n\PrevState = 0
;	
;	If n\Idle>0.0 Then
;		FinishWalking(n,488,522,0.015*26)
;		n\Idle=n\Idle-FPSfactor
;		If n\Idle<=0.0 Then n\Idle = 0.0
;	Else
;		Select Int(n\State) ;what is this MTF doing
;			Case 0 ;wandering around the facility + following the player
;				;[Block]
;				;PlayerSeeAble = MeNPCSeesPlayer(n,False,True)
;				PlayerSeeAble = False
;				If (Not NoTarget)
;					If EntityDistanceSquared(Collider,n\Collider)<=PowTwo(8.0-CrouchState+PlayerSoundVolume)
;						If EntityVisible(n\Collider,Collider)
;							PlayerSeeAble = True
;						EndIf
;					EndIf
;				EndIf
;				
;				If n\State3 <= 0 Then
;					PlayMTFSound(LoadTempSound("SFX\Character\MTF\Random"+Rand(1,4)+".ogg"),n)
;					n\State3 = 60*Rnd(10,25)
;				EndIf
;				
;				If SplitUp = True
;					n\State = 8
;				EndIf
;				
;				For n2.NPCs = Each NPCs
;					NPCSeeAble = OtherNPCSeesMeNPC(n,n2)
;					If n2\NPCtype = NPCtypeD2 Then
;						If n2\IsDead = False And NPCSeeAble = True 
;							If EntityVisible(n\Collider,n2\Collider) Then
;								Select Rand(1)
;									Case 0
;										PlayMTFSound(LoadTempSound("SFX\Character\MTF\Stop"+Rand(1,6)+".ogg"),n)
;									Case 1
;										PlayMTFSound(LoadTempSound("SFX\Character\MTF\ThereHeIs"+Rand(1,6)+".ogg"),n)
;								End Select
;								;n\TargetEnt% = n2\Collider
;								n\Target = n2
;								PointEntity2(n\Collider,n2\Collider,0.0,False,True)
;								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10), 0, True)
;								n\State = 4
;							EndIf
;						EndIf
;					ElseIf n2\NPCtype = NPCtype173 Then
;						If OtherNPCSeesMeNPC(Curr173,n) And Contain173State < 2 Then
;							If EntityVisible(n\Collider,n2\Collider) Then
;								PlayMTFSound(LoadTempSound("SFX\Character\MTF\173\Spotted"+Rand(1,2)+".ogg"),n)
;								;n\TargetEnt% = n2\Collider
;								n\Target = n2
;								n\State = 2
;							EndIf
;						EndIf
;					ElseIf n2\NPCtype = NPCtypeOldMan Then
;						If EntityVisible(n\Collider,n2\Collider) Then
;							PlayMTFSound(LoadTempSound("SFX\Character\MTF\106\Spotted"+Rand(1,3)+".ogg"),n)
;							;n\TargetEnt% = n2\Collider
;							n\Target = n2
;							n\State = 4
;						EndIf
;					EndIf
;				Next
;				If PlayerSeeAble
;					n\PathStatus = 0
;					n\PathLocation = 0
;					n\PathTimer = 0.0
;						;PointEntity n\obj, Collider
;					
;					PointEntity2(n\obj,Collider,0.0,False,True)
;					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
;					n\Angle = EntityYaw(n\Collider)
;					If EntityDistanceSquared(n\Collider,Collider)<PowTwo(1.0)
;						If n\CurrSpeed <= 0.01
;							n\CurrSpeed = 0.0
;							AnimateNPC(n, 962, 1259, 0.3)
;						Else
;							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,20.0)
;							AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;							MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;						EndIf
;					Else
;						n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;						AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;						MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;					EndIf
;				Else
;					If dist < PowTwo(12) Then
;						If n\PathStatus = 1
;							If n\Path[n\PathLocation]=Null
;								If n\PathLocation > 19 Then 
;									n\PathLocation = 0 : n\PathStatus = 0
;								Else
;									n\PathLocation = n\PathLocation + 1
;								EndIf
;							Else
;									;PointEntity n\obj, n\Path[n\PathLocation]\obj
;								
;								PointEntity2(n\obj,n\Path[n\PathLocation]\obj,0.0,False,True)
;								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
;								
;								n\Angle = EntityYaw(n\Collider)
;								
;								AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;								
;								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;								
;									;opens doors in front of him
;								dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
;								If dist2 < PowTwo(0.6) Then
;									temp = True
;									If n\Path[n\PathLocation]\door <> Null
;										If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
;											If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>""
;												temp = False
;											Else
;												If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
;											EndIf
;										EndIf
;										If n\Path[n\PathLocation]\door\openstate>=180
;											n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;										Else
;											n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
;										EndIf
;									Else
;										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;									EndIf
;									If dist2<PowTwo(0.2) And temp
;										n\PathLocation = n\PathLocation + 1
;									ElseIf dist2<PowTwo(0.5) And (Not temp)
;										n\PathStatus = 0
;										n\PathTimer# = 0.0
;									EndIf
;								Else
;									n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;								EndIf
;							EndIf
;						Else
;								;Tries to find a path to the player
;							n\EnemyX = EntityX(Collider)
;							n\EnemyY = EntityY(Collider)
;							n\EnemyZ = EntityZ(Collider)
;								;Only trying to find a path if possible
;							n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY,n\EnemyZ)
;							If n\PathStatus = 1
;								
;							EndIf
;							n\PathTimer = 0.0
;						EndIf
;					Else
;						TeleportCloser(n)
;					EndIf
;				EndIf
;				;[End Block]
;			Case 1 ;used for intro
;                ;[Block]
;				If n\State2 = 0 ;sitting
;					AnimateNPC(n, 1525, 1623, 0.5)
;				ElseIf n\State2 = 1 ;aiming
;					AnimateNPC(n, 346, 351, 0.2, False)
;				ElseIf n\State2 = 2 ;idle
;					If Rand(400) = 1 Then n\PrevState = Rnd(-30, 30)
;					AnimateNPC(n, 962, 1259, 0.3)
;					RotateEntity(n\Collider, 0, 90, 0, True)
;				EndIf
;				;[End Block]
;			Case 2 ;SCP-173 spotted
;				;[Block]
;				If (Not EntityVisible(n\Collider,Curr173\Collider)) And Contain173State = 1 Then
;					If n\PathStatus = 1
;						If n\Path[n\PathLocation]=Null
;							If n\PathLocation > 19 Then 
;								n\PathLocation = 0 : n\PathStatus = 0
;							Else
;								n\PathLocation = n\PathLocation + 1
;							EndIf
;						Else
;							PointEntity2(n\obj,n\Path[n\PathLocation]\obj,0.0,False,True)
;							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
;							
;							n\Angle = EntityYaw(n\Collider)
;							
;							AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;							
;							MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;							
;							dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
;							If dist2 < PowTwo(0.6) Then
;								temp = True
;								If n\Path[n\PathLocation]\door <> Null
;									If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
;										If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>""
;											temp = False
;										Else
;											If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
;										EndIf
;									EndIf
;									If n\Path[n\PathLocation]\door\openstate>=180
;										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;									Else
;										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
;									EndIf
;								Else
;									n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;								EndIf
;								If dist2<PowTwo(0.2) And temp
;									n\PathLocation = n\PathLocation + 1
;								ElseIf dist2<PowTwo(0.5) And (Not temp)
;									n\PathStatus = 0
;									n\PathTimer# = 0.0
;								EndIf
;							Else
;								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;							EndIf
;						EndIf
;					Else
;						n\PathStatus = FindPath(n,EntityX(Curr173\Collider),EntityY(Curr173\Collider),EntityZ(Curr173\Collider))
;					EndIf
;				Else
;					PointEntity2(n\obj,Curr173\obj,0.0,False,True)
;					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
;					Contain173Timer# = Contain173Timer# + FPSfactor
;					DebugLog Contain173Timer
;					If Contain173Timer# > 5000
;						Contain173State% = 3
;					EndIf
;					If Contain173State = 3 Then
;						PlayMTFSound(LoadTempSound("SFX\Character\MTF\173\Box"+Rand(1,3)+".ogg"),n)
;						Curr173\Idle = 2
;						If SplitUp = False
;							n\State = 0
;						Else
;							n\State = 8
;						EndIf
;						;Curr173\TargetEnt = Collider
;						Contain173Timer# = Contain173Timer#
;					Else
;						Curr173\Idle = True
;					EndIf
;				EndIf
;				;[End block]
;			Case 3 ;following a path
;				;[Block]
;				
;				n\Angle = CurveValue(0,n\Angle,40.0)
;				
;				If n\PathStatus = 2 Then
;					n\State = 5
;					n\CurrSpeed = 0
;				ElseIf n\PathStatus = 1
;					If n\Path[n\PathLocation]=Null Then 
;						If n\PathLocation > 19 Then 
;							n\PathLocation = 0
;							n\PathStatus = 0
;							If n\LastSeen > 0 Then n\State = 5
;						Else
;							n\PathLocation = n\PathLocation + 1
;						EndIf
;					Else
;						If n\Path[n\PathLocation]\door <> Null Then
;							If n\Path[n\PathLocation]\door\open = False Then
;								n\Path[n\PathLocation]\door\open = True
;								n\Path[n\PathLocation]\door\timerstate = 8.0*70.0
;								PlayMTFSound(MTFSFX,n)
;							EndIf
;						EndIf
;						
;						If dist < PowTwo(HideDistance*0.7) Then 
;							dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) 
;							
;							;If Rand(5)=1 Then 
;							;	For n2.NPCs = Each NPCs
;							;		If n2\NPCtype = n\NPCtype And n2<>n Then
;							;			If EntityDistanceSquared(n\Collider, n2\Collider)<PowTwo(2) Then
;							;				n\Idle = 150
;							;			EndIf
;							;		EndIf
;							;	Next
;							;EndIf
;							
;							PointEntity n\obj, n\Path[n\PathLocation]\obj
;							
;							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
;							If n\Idle = 0 Then
;								n\CurrSpeed = CurveValue(n\Speed*Max(Min(dist2,1.0),0.1), n\CurrSpeed, 20.0)
;								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;								
;								;If dist2 < (0.25+((n\Path[Min(n\PathLocation+1,19)]=Null)*0.3 * (n\ID Mod 3))) Then
;								If EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)<PowTwo(0.5)
;									n\PathLocation = n\PathLocation + 1
;								EndIf
;							EndIf
;						Else
;							If Rand(20)=1 Then 
;								PositionEntity n\Collider, EntityX(n\Path[n\PathLocation]\obj,True),EntityY(n\Path[n\PathLocation]\obj,True)+0.25,EntityZ(n\Path[n\PathLocation]\obj,True),True
;								n\PathLocation = n\PathLocation + 1
;								ResetEntity n\Collider
;							EndIf
;						EndIf
;						
;					EndIf
;				Else
;					n\CurrSpeed = 0
;					n\State = 5
;				EndIf
;				
;				
;				If n\Idle = 0 And n\PathStatus = 1 Then
;					If dist < PowTwo(HideDistance) Then
;						If n\Frame>959 Then
;							AnimateNPC(n, 1376, 1383, 0.2, False)
;							If n\Frame >1382.9 Then n\Frame = 488
;						Else
;							AnimateNPC(n, 488, 522, n\CurrSpeed*30)
;						EndIf
;					EndIf
;				Else
;					If dist < PowTwo(HideDistance) Then
;						If n\LastSeen > 0 Then 
;							AnimateNPC(n, 78, 312, 0.2, True)
;						Else
;							If n\Frame<962 Then
;								If n\Frame>487 Then n\Frame = 463
;								AnimateNPC(n, 463, 487, 0.3, False)
;								If n\Frame>486.9 Then n\Frame = 962
;							Else
;								AnimateNPC(n, 962, 1259, 0.3)
;							EndIf
;						EndIf
;					EndIf
;					
;					n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
;				EndIf
;				
;				n\Angle = EntityYaw(n\Collider)
;				;[End Block]
;			Case 4 ;spotted other NPC
;				;[Block]
;				If n\Target\NPCtype = NPCtypeD2 Then
;					Local pvt% = CreatePivot()
;					If n\Target\IsDead = False Then
;						AnimateNPC(n,1373, 1382, 0.2, False)
;						If n\Reload =< 0 And n\Frame = 1382 Then
;							SetNPCFrame(n,1382)
;							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
;							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
;							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
;							
;							p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
;							TurnEntity p\obj, 0,0,Rnd(360)
;							p\Achange = -0.15
;							PlaySound2(LoadTempSound("SFX\Guns\p90\shoot_in_0"+Rand(1,7)+".ogg"), Camera, n\Collider, 35)
;							ShootTarget(EntityX(pvt),EntityY(pvt),EntityZ(pvt),n)
;							pvt = FreeEntity_Strict(pvt)
;							n\Reload = 7
;						EndIf
;					Else
;						AnimateNPC(n,1373, 1382, -0.2, False)
;						If n\Frame = 1373 Then
;							If SplitUp = False
;								n\State = 0
;							Else
;								n\State = 8
;							EndIf
;						EndIf
;					EndIf
;				ElseIf n\Target\NPCtype = NPCtypeOldMan
;					If n\Target\State <= 0
;						PointEntity2(n\obj,n\Target\Collider,0,False)
;						RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10), 0, True)
;						n\CurrSpeed = CurveValue(n\Speed*-1.4,n\CurrSpeed,10.0)
;						AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;						MoveEntity(n\Collider,0,0,n\CurrSpeed)
;					Else
;						AnimateNPC(n,1373, 1382, -0.2, False)
;						If n\Frame = 1373 Then
;							If SplitUp = False
;								n\State = 0
;							Else
;								n\State = 8
;							EndIf
;						EndIf
;					EndIf
;				EndIf
;				;[End Block]
;			Case 5 ;looking at some other target than the player
;				;[Block]
;				target=CreatePivot()
;				PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
;				
;				If dist<PowTwo(HideDistance) Then
;					AnimateNPC(n, 346, 351, 0.2, False)
;				EndIf
;				
;				If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 And Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 And Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
;					
;					PointEntity n\obj, target
;					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),30.0), 0, True
;					
;					If n\PathTimer = 0 Then
;						n\PathStatus = EntityVisible(n\Collider,target)
;						n\PathTimer = Rand(100,200)
;					Else
;						n\PathTimer = Min(n\PathTimer-FPSfactor,0.0)
;					EndIf
;					
;					If n\PathStatus = 1 And n\Reload =< 0 Then
;						dist# = DistanceSquared(EntityX(target),EntityX(n\Collider),EntityZ(target),EntityZ(n\Collider))
;						
;						;If dist<PowTwo(20.0) Then
;						;	pvt = CreatePivot()
;						;	
;						;	PositionEntity pvt, EntityX(n\obj),EntityY(n\obj), EntityZ(n\obj)
;						;	RotateEntity pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),0
;						;	MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
;						;	
;						;	If WrapAngle(EntityYaw(pvt)-EntityYaw(n\Collider))<5 Then
;						;		PlaySound2(GunshotSFX, Camera, n\Collider, 20)
;						;		p.Particles = CreateParticle(EntityX(n\obj, True), EntityY(n\obj, True), EntityZ(n\obj, True), 1, 0.2, 0.0, 5)
;						;		PositionEntity(p\pvt, EntityX(pvt), EntityY(pvt), EntityZ(pvt))
;						;		
;						;		n\Reload = 7
;						;	EndIf
;						;	
;						;	pvt = FreeEntity_Strict(pvt)
;						;EndIf
;					EndIf
;				EndIf		
;				
;				target = FreeEntity_Strict(target)
;				
;				n\Angle = EntityYaw(n\Collider)
;				;[End Block]
;			Case 6 ;seeing the player as a 049-2 instance
;				;[Block]
;				PointEntity n\obj,Collider
;				RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
;				n\Angle = EntityYaw(n\Collider)
;				
;				AnimateNPC(n, 346, 351, 0.2, False)
;				
;				If n\Reload =< 0 And KillTimer = 0 Then
;					If EntityVisible(n\Collider, Collider) Then
;						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
;						;If angle < 5 Lor angle > 355 Then
;						If (Abs(DeltaYaw(n\Collider,Collider))<50.0)
;							;prev% = KillTimer
;							
;							PlaySound2(GunshotSFX, Camera, n\Collider, 15)
;							
;							pvt% = CreatePivot()
;							
;							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
;							PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
;							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
;							
;							ShootPlayer(EntityX(pvt),EntityY(pvt),EntityZ(pvt),0.9, False)
;							n\Reload = 7
;							
;							pvt = FreeEntity_Strict(pvt)
;							
;							;If prev => 0 And KillTimer < 0 Then
;								;DeathMSG="Subject D-9341. Terminated by Nine-Tailed Fox."
;								;If n\MTFLeader = Null Then PlayMTFSound(LoadTempSound("SFX\Character\MTF\049\Player0492_2.ogg"),n)
;							;EndIf
;						EndIf	
;					EndIf
;				EndIf
;				
;				;[End Block]
;			Case 7 ;just shooting
;				;[Block]
;				AnimateNPC(n, 346, 351, 0.2, False)
;				
;				RotateEntity n\Collider,0,CurveAngle(n\State2,EntityYaw(n\Collider),20),0
;				n\Angle = EntityYaw(n\Collider)
;				
;				If n\Reload =< 0 
;					LightVolume = TempLightVolume*1.2
;					PlaySound2(GunshotSFX, Camera, n\Collider, 20)
;					
;					pvt% = CreatePivot()
;					
;					RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
;					PositionEntity(pvt, EntityX(n\obj), EntityY(n\obj), EntityZ(n\obj))
;					MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
;					
;					p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
;					TurnEntity p\obj, 0,0,Rnd(360)
;					p\Achange = -0.15
;					
;					pvt = FreeEntity_Strict(pvt)
;					n\Reload = 7
;				End If
;				;[End Block]
;			Case 8 ;split up
;				;[Block]
;				If SplitUp = False
;					n\State = 0
;				Else
;					For n2.NPCs = Each NPCs
;						NPCSeeAble = OtherNPCSeesMeNPC(n,n2)
;						If n2\NPCtype = NPCtypeD2 Then
;							If n2\IsDead = False And NPCSeeAble = True 
;								If EntityVisible(n\Collider,n2\Collider) Then
;									Select Rand(1)
;										Case 0
;											PlayMTFSound(LoadTempSound("SFX\Character\MTF\Stop"+Rand(1,6)+".ogg"),n)
;										Case 1
;											PlayMTFSound(LoadTempSound("SFX\Character\MTF\ThereHeIs"+Rand(1,6)+".ogg"),n)
;									End Select
;									;n\TargetEnt% = n2\Collider
;									n\Target = n2
;									PointEntity2(n\Collider,n2\Collider,0.0,False,True)
;									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10), 0, True)
;									n\State = 4
;								EndIf
;							EndIf
;						ElseIf n2\NPCtype = NPCtype173 Then
;							If OtherNPCSeesMeNPC(Curr173,n) And Contain173State < 2 Then
;								If EntityVisible(n\Collider,n2\Collider) Then
;									PlayMTFSound(LoadTempSound("SFX\Character\MTF\173\Spotted"+Rand(1,2)+".ogg"),n)
;									;n\TargetEnt% = n2\Collider
;									n\Target = n2
;									n\State = 2
;								EndIf
;							EndIf
;						EndIf
;					Next
;					If n\PathStatus = 1
;						If n\Path[n\PathLocation]=Null
;							If n\PathLocation > 19 Then 
;								n\PathLocation = 0 : n\PathStatus = 0
;							Else
;								n\PathLocation = n\PathLocation + 1
;							EndIf
;						Else
;									;PointEntity n\obj, n\Path[n\PathLocation]\obj
;							
;							PointEntity2(n\obj,n\Path[n\PathLocation]\obj,0.0,False,True)
;							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 20.0), 0
;							
;							n\Angle = EntityYaw(n\Collider)
;							
;							AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;							
;							MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;							
;									;opens doors in front of him
;							dist2# = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
;							If dist2 < PowTwo(0.6) Then
;								temp = True
;								If n\Path[n\PathLocation]\door <> Null
;									If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
;										If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>""
;											temp = False
;										Else
;											If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
;										EndIf
;									EndIf
;									If n\Path[n\PathLocation]\door\openstate>=180
;										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;									Else
;										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
;									EndIf
;								Else
;									n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;								EndIf
;								If dist2#<PowTwo(0.2) And temp
;									n\PathLocation = n\PathLocation + 1
;								ElseIf dist2#<PowTwo(0.5) And (Not temp)
;									n\PathStatus = 0
;									n\PathTimer# = 0.0
;								EndIf
;							Else
;								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
;							EndIf
;						EndIf
;					Else
;						If n\CurrSpeed <= 0.01
;							n\CurrSpeed = 0.0
;							AnimateNPC(n, 962, 1259, 0.3)
;							If n\PathTimer < 70*15.0
;								n\PathTimer = n\PathTimer+Rnd(1.0,2.5)
;							Else
;								n\EnemyX = 0.0
;								n\EnemyY = 0.0
;								n\EnemyZ = 0.0
;											;Tries to find a room that is close to him
;								For r.Rooms = Each Rooms
;									If EntityDistanceSquared(r\obj,n\Collider)<PowTwo(14.0) And EntityDistanceSquared(r\obj,n\Collider)>PowTwo(6.0)
;										n\EnemyX = EntityX(r\obj)
;										n\EnemyY = EntityY(r\obj)
;										n\EnemyZ = EntityZ(r\obj)
;										Exit
;									EndIf
;								Next
;											;No room found, trying to get the coordinates from a random waypoint that is close to the npc, but not too close
;								If n\EnemyX=0.0 And n\EnemyY=0.0 And n\EnemyZ=0.0
;									For w.WayPoints = Each WayPoints
;										If EntityDistanceSquared(w\obj,n\Collider)<PowTwo(10.0) And EntityDistanceSquared(w\obj,n\Collider)>PowTwo(4.0)
;											n\EnemyX = EntityX(w\obj)
;											n\EnemyY = EntityY(w\obj)
;											n\EnemyZ = EntityZ(w\obj)
;										EndIf
;									Next
;								EndIf
;											;Only trying to find a path if possible
;								If n\EnemyX<>0.0 Lor n\EnemyY<>0.0 Lor n\EnemyZ<>0.0
;									n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY,n\EnemyZ)
;									If n\PathStatus = 1
;													;If n\Path[0] = n\Path[19]
;													;	n\PathStatus = 0
;													;EndIf
;									EndIf
;								EndIf
;								n\PathTimer = 0.0
;							EndIf
;						Else
;							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,20.0)
;							AnimateNPC(n, 1496, 1524, n\CurrSpeed*30)
;							MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
;						EndIf
;					EndIf
;				EndIf
;				;[End Block]
;		End Select
;		
;		If n\CurrSpeed > 0.01 Then
;			If prevFrame > 500 And n\Frame<495 Then
;				sfxstep = GetStepSound(n\Collider,n\CollRadius)
;				PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
;			ElseIf prevFrame < 505 And n\Frame=>505
;				sfxstep = GetStepSound(n\Collider,n\CollRadius)
;				PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
;			ElseIf prevFrame < 1509 And n\Frame=>1509
;				sfxstep = GetStepSound(n\Collider,n\CollRadius)
;				PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
;			ElseIf prevFrame < 1522 And n\Frame=>1522
;				sfxstep = GetStepSound(n\Collider,n\CollRadius)
;				PlaySound2(StepSFX(sfxstep,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
;			EndIf
;		EndIf
;		
;		If n\State <> 3 And n\State <> 5 And n\State <> 6 And n\State <> 7 And n\State <> 2 And n\State <> 4 And n\State <> 8
;			If n\State <> 1
;				If EntityDistanceSquared(n\Collider,Collider)<PowTwo(0.7) Then
;					PointEntity n\Collider,Collider
;					RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
;					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
;					;TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)-45)* 0.01 * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)-45)* 0.01 * FPSfactor, True
;					TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)-45)* 0.005 * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)-45)* 0.005 * FPSfactor, True
;				EndIf
;				If Abs(DeltaYaw(Collider,n\Collider))<80.0 Then
;					If EntityDistanceSquared(Collider,n\Collider)<PowTwo(0.7) Then							
;						;TranslateEntity n\Collider, Cos(EntityYaw(Collider,True)+90)* 0.01 * FPSfactor, 0, Sin(EntityYaw(Collider,True)+90)* 0.01 * FPSfactor, True
;						TranslateEntity n\Collider, Cos(EntityYaw(Collider,True)+90)* 0.005 * FPSfactor, 0, Sin(EntityYaw(Collider,True)+90)* 0.005 * FPSfactor, True
;					EndIf
;				EndIf
;			EndIf
;		EndIf
;		
;		If Int(n\State)<>1 Lor Int(n\State2)<>0
;			RotateEntity n\obj,0.0,n\Angle,0.0,True
;			
;			PositionEntity n\obj,EntityX(n\Collider,True),EntityY(n\Collider,True)-n\CollRadius,EntityZ(n\Collider,True),True
;		EndIf
;		
;	EndIf
	;[End Block]
End Function

;[Block]
;Function Shoot(x#, y#, z#, hitProb# = 1.0, particles% = True, instaKill% = False)
;	
;	;muzzle flash
;	Local p.Particles = CreateParticle(x,y,z, 1, Rnd(0.08,0.1), 0.0, 5)
;	TurnEntity p\obj, 0,0,Rnd(360)
;	p\Achange = -0.15
;	
;	LightVolume = TempLightVolume*1.2
;	
;	If (Not GodMode) Then 
;		
;		If instaKill Then Kill() : PlaySound_Strict BullethitSFX : Return
;		
;		If Rnd(1.0) =< hitProb Then
;			TurnEntity Camera, Rnd(-3,3), Rnd(-3,3), 0
;			
;			Local ShotMessageUpdate$
;			If WearingVest>0 Then
;				If WearingVest = 1 Then
;					Select Rand(8)
;						Case 1,2,3,4,5
;							BlurTimer = 500
;							Stamina = 0
;							ShotMessageUpdate = "A bullet penetrated your vest, making you gasp."
;							Injuries = Injuries + Rnd(0.1,0.5)
;						Case 6
;							BlurTimer = 500
;							ShotMessageUpdate = "A bullet hit your left leg."
;							Injuries = Injuries + Rnd(0.8,1.2)
;						Case 7
;							BlurTimer = 500
;							ShotMessageUpdate = "A bullet hit your right leg."
;							Injuries = Injuries + Rnd(0.8,1.2)
;						Case 8
;							BlurTimer = 500
;							Stamina = 0
;							ShotMessageUpdate = "A bullet struck your neck, making you gasp."
;							Injuries = Injuries + Rnd(1.2,1.6)
;					End Select	
;				Else
;					If Rand(10)=1 Then
;						BlurTimer = 500
;						Stamina = Stamina - 1
;						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed some of the damage."
;						Injuries = Injuries + Rnd(0.8,1.1)
;					Else
;						ShotMessageUpdate = "A bullet hit your chest. The vest absorbed most of the damage."
;						Injuries = Injuries + Rnd(0.1,0.5)
;					EndIf
;				EndIf
;				
;				If Injuries >= 5
;					If Rand(3) = 1 Then Kill()
;				EndIf
;			Else
;				Select Rand(6)
;					Case 1
;						Kill()
;					Case 2
;						BlurTimer = 500
;						ShotMessageUpdate = "A bullet hit your left leg."
;						Injuries = Injuries + Rnd(0.8,1.2)
;					Case 3
;						BlurTimer = 500
;						ShotMessageUpdate = "A bullet hit your right leg."
;						Injuries = Injuries + Rnd(0.8,1.2)
;					Case 4
;						BlurTimer = 500
;						ShotMessageUpdate = "A bullet hit your right shoulder."
;						Injuries = Injuries + Rnd(0.8,1.2)	
;					Case 5
;						BlurTimer = 500
;						ShotMessageUpdate = "A bullet hit your left shoulder."
;						Injuries = Injuries + Rnd(0.8,1.2)	
;					Case 6
;						BlurTimer = 500
;						ShotMessageUpdate = "A bullet hit your right shoulder."
;						Injuries = Injuries + Rnd(2.5,4.0)
;				End Select
;			EndIf
;			
;			;Only updates the message if it's been more than two seconds.
;			If (MsgTimer < 64*4) Then
;				Msg = ShotMessageUpdate
;				MsgTimer = 70*6
;			EndIf
;
;			Injuries = Min(Injuries, 4.0)
;			
;			;Kill()
;			PlaySound_Strict BullethitSFX
;		ElseIf particles And ParticleAmount>0
;			pvt = CreatePivot()
;			PositionEntity pvt, EntityX(Collider),(EntityY(Collider)+EntityY(Camera))/2,EntityZ(Collider)
;			PointEntity pvt, p\obj
;			TurnEntity pvt, 0, 180, 0
;			
;			EntityPick(pvt, 2.5)
;			
;			If PickedEntity() <> 0 Then 
;				PlaySound2(Gunshot3SFX, Camera, pvt, 0.4, Rnd(0.8,1.0))
;				
;				If particles Then 
;					;dust/smoke particles
;					p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
;					p\speed = 0.001
;					p\SizeChange = 0.003
;					p\A = 0.8
;					p\Achange = -0.01
;					RotateEntity p\pvt, EntityPitch(pvt)-180, EntityYaw(pvt),0
;					
;					For i = 0 To Rand(2,3)
;						p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
;						p\speed = 0.02
;						p\A = 0.8
;						p\Achange = -0.01
;						RotateEntity p\pvt, EntityPitch(pvt)+Rnd(170,190), EntityYaw(pvt)+Rnd(-10,10),0	
;					Next
;					
;					;bullet hole decal
;					Local de.Decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BULLETHOLE), PickedX(),PickedY(),PickedZ(), 0,0,0)
;					AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
;					MoveEntity de\obj, 0,0,-0.001
;					EntityFX de\obj, 1
;					de\lifetime = 70*20
;					EntityBlend de\obj, 2
;					de\Size = Rnd(0.028,0.034)
;					ScaleSprite de\obj, de\Size, de\Size
;				EndIf				
;			EndIf
;			pvt = FreeEntity_Strict(pvt)
;		EndIf
;		
;	EndIf
;	
;End Function
;[End Block]

Function PlayMTFSound(sound%, n.NPCs, allowOverlap=False)
	Local n2.NPCs
	Local snd%
	
	For n2 = Each NPCs
		If n <> Null Then
			If n2 <> Null And allowOverlap = False Then
				If n2\NPCtype = n\NPCtype Then
					If n\ID > n2\ID Then
						snd = PlaySound_Strict(sound)
						Exit
					EndIf
				EndIf
			Else
				snd = PlaySound_Strict(sound)
				Exit
			EndIf
		EndIf
	Next
	
	ChannelVolume(snd, opt\VoiceVol*opt\MasterVol)
End Function

Function MoveToPocketDimension()
	Local r.Rooms
	
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "pocketdimension" Then
			FallTimer = 0
			UpdateDoors()
			UpdateRooms()
			ShowEntity Collider
			PlaySound_Strict(Use914SFX)
			PlaySound_Strict(OldManSFX[5])
			PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))
			DropSpeed = 0
			ResetEntity Collider
			
			BlinkTimer = -10
			
			;Injuries = Injuries+0.5
			DamageSPPlayer(10.0, True)
			
			PlayerRoom = r
			
			Return
		EndIf
	Next		
End Function

Function FindFreeNPCID%()
	Local id% = 1
	While (True)
		Local taken% = False
		For n2.NPCs = Each NPCs
			If n2\ID = id Then
				taken = True
				Exit
			EndIf
		Next
		If (Not taken) Then
			Return id
		EndIf
		id = id + 1
	Wend
End Function

Function ForceSetNPCID(n.NPCs, newID%)
	n\ID = newID
	
	For n2.NPCs = Each NPCs
		If n2 <> n And n2\ID = newID Then
			n2\id = FindFreeNPCID()
		EndIf
	Next
End Function

Function Find860Angle(n.NPCs, fr.Forest)
	TFormPoint(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,fr\Forest_Pivot)
	Local playerx = Floor((TFormedX()+6.0)/12.0)
	Local playerz = Floor((TFormedZ()+6.0)/12.0)
	
	TFormPoint(EntityX(n\Collider),EntityY(n\Collider),EntityZ(n\Collider),0,fr\Forest_Pivot)
	Local x# = (TFormedX()+6.0)/12.0
	Local z# = (TFormedZ()+6.0)/12.0
	
	Local xt = Floor(x), zt = Floor(z)
	
	Local x2,z2
	If xt<>playerx Lor zt<>playerz Then ;the monster is not on the same tile as the player
		For x2 = Max(xt-1,0) To Min(xt+1,gridsize-1)
			For z2 = Max(zt-1,0) To Min(zt+1,gridsize-1)
				If fr\grid[(z2*gridsize)+x2]>0 And (x2<>xt Lor z2<>zt) And (x2=xt Lor z2=zt) Then
					
					;tile (x2,z2) is closer to the player than the monsters current tile
					If (Abs(playerx-x2)+Abs(playerz-z2))<(Abs(playerx-xt)+Abs(playerz-zt)) Then
						;calculate the position of the tile in world coordinates
						TFormPoint(x2*12.0,0,z2*12.0,fr\Forest_Pivot,0)
						
						Return point_direction(EntityX(n\Collider),EntityZ(n\Collider),TFormedX(),TFormedZ())+180
					EndIf
					
				EndIf
			Next
		Next
	Else
		Return point_direction(EntityX(n\Collider),EntityZ(n\Collider),EntityX(Collider),EntityZ(Collider))+180
	EndIf		
End Function

Function Console_SpawnNPC(c_input$, c_state$ = "")
	Local n.NPCs
	Local consoleMSG$
	
	If mp_I\PlayState>0 Then
		Collider = Players[mp_I\PlayerID]\Collider
	EndIf
	
	Select c_input$ 
		Case "008", "008zombie"
			If mp_I\Gamemode <> Null Then
				If mp_I\Gamemode\ID = Gamemode_Deathmatch Then
					CreateConsoleMsg("SCP-008 infected human cannot be spawned in Deathmatch. Sorry!", 255, 0, 0)
				ElseIf mp_I\Gamemode\ID = Gamemode_Waves Then
					n.NPCs = CreateNPC(NPCtypeZombieMP, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				EndIf
			Else
				n.NPCs = CreateNPC(NPCtype008, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			EndIf	
			If n.NPCs <> Null Then
				n\State = 1
				consoleMSG = "SCP-008 infected human spawned."
			EndIf
			
		Case "049", "scp049", "scp-049"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-049 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype049, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				n\State = 1
				consoleMSG = "SCP-049 spawned."
			EndIf	
			
		Case "049-2", "0492", "scp-049-2", "scp049-2", "049zombie"
			If mp_I\Gamemode <> Null And mp_I\Gamemode\ID = Gamemode_Deathmatch Then
				CreateConsoleMsg("SCP-049-2 cannot be spawned in Deathmatch. Sorry!", 255, 0, 0)
			Else	
				n.NPCs = CreateNPC(NPCtypeZombie, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				n\State = 1
				consoleMSG = "SCP-049-2 spawned."
			EndIf	
			
		Case "066", "scp066", "scp-066"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-066 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype066, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-066 spawned."
			EndIf	
			
		Case "096", "scp096", "scp-096"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-096 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype096, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				n\State = 5
				If (Curr096 = Null) Then Curr096 = n
				consoleMSG = "SCP-096 spawned."
			EndIf	
			
		Case "106", "scp106", "scp-106", "larry"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-106 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeOldMan, EntityX(Collider), EntityY(Collider) - 0.5, EntityZ(Collider))
				n\State = -1
				consoleMSG = "SCP-106 spawned."
			EndIf	
			
		Case "173", "scp173", "scp-173", "statue"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-173 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype173, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				Curr173 = n
				If (Curr173\Idle = SCP173_DISABLED) Then Curr173\Idle = SCP173_ACTIVE
				consoleMSG = "SCP-173 spawned."
			EndIf
			
		Case "372", "scp372", "scp-372"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-372 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype372, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-372 spawned."
			EndIf	
			
		Case "513-1", "5131", "scp513-1", "scp-513-1"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-513-1 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype5131, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-513-1 spawned."
			EndIf	
			
		Case "860-2", "8602", "scp860-2", "scp-860-2"
			CreateConsoleMsg("SCP-860-2 cannot be spawned with the console. Sorry!", 255, 0, 0)
			
		Case "939", "scp939", "scp-939"
			If mp_I\Gamemode <> Null And mp_I\Gamemode\ID = Gamemode_Deathmatch Then
				CreateConsoleMsg("SCP-939 instances cannot be spawned in Deathmatch. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype939, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-939 instance spawned."
			EndIf
			
		Case "966", "scp966", "scp-966"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-966 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype966, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-966 instance spawned."
			EndIf	
			
		Case "1048-a", "scp1048-a", "scp-1048-a", "scp1048a", "scp-1048a"
			If mp_I\Gamemode <> Null And mp_I\Gamemode\ID = Gamemode_Waves Then
				n.NPCs = CreateNPC(NPCtype1048a, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-1048-a instance spawned."
			Else
				CreateConsoleMsg("SCP-1048-A cannot be spawned with the console. Sorry!", 255, 0, 0)
			EndIf
			
		Case "1499-1", "14991", "scp-1499-1", "scp1499-1"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-1499-1 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype1499, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-1499-1 instance spawned."
			EndIf	
			
		Case "class-d", "classd", "d"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("D-Class cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeD, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "D-Class spawned."
			EndIf
			
		Case "guard"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Guard cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeGuard, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Guard spawned."
			EndIf	
			
		Case "mtf"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("MTF unit cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeMTF, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "MTF unit spawned."
			EndIf	
			
		Case "apache", "helicopter"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Apache cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeApache, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Apache spawned."
			EndIf	
			
		Case "tentacle"
			n.NPCs = CreateNPC(NPCtypeTentacle, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
			consoleMSG = "SCP-035 tentacle spawned."
			
		Case "clerk"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Clerk cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeClerk, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Clerk spawned."
			EndIf	
			
		Case "d2","aggressive_d","classd2","class-d2"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Aggressive Class-D cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeD2, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Aggressive Class-D spawned."
			EndIf	
			
		Case "076","scp-076","scp-076-2","scp076","scp076-2"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-076-2 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype076, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-076-2 spawned."
			EndIf	
			
		Case "guard2","eguard","elite guard","guard elite"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Elite Guard cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeEGuard, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Elite Guard spawned."
				n\State = 0
			EndIf	
		Case "killableguard","kg","killguard","killg","kguard"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("Killable Guard cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtypeKGuard, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "Killable Guard spawned."
				n\State = 0
			EndIf	
		Case "035", "scp-035", "scp035"
			If mp_I\Gamemode <> Null And mp_I\Gamemode\ID = Gamemode_Waves Then
				n.NPCs = CreateNPC(NPCtype035, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-035 spawned."
			Else
				CreateConsoleMsg("SCP-035 cannot be spawned in Singleplayer. Sorry!", 255, 0, 0)
			EndIf	
		Case "682", "scp-682", "scp682"
			If gopt\GameMode = GAMEMODE_MULTIPLAYER Then
				CreateConsoleMsg("SCP-682 cannot be spawned in Multiplayer. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype682, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-682 spawned."
			EndIf	
		Case "457", "scp-457", "scp457"
			If mp_I\Gamemode <> Null And mp_I\Gamemode\ID = Gamemode_Deathmatch Then
				CreateConsoleMsg("SCP-457 cannot be spawned in Deathmatch. Sorry!", 255, 0, 0)
			Else
				n.NPCs = CreateNPC(NPCtype457, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
				consoleMSG = "SCP-457 spawned."
			EndIf	
		Default 
			CreateConsoleMsg("NPC type not found.", 255, 0, 0) : Return
	End Select
	
	If n <> Null Then
		If c_state <> "" Then n\State = Float(c_state) : consoleMSG = consoleMSG + " (State = " + n\State + ")"
	EndIf
	
	If mp_I\PlayState>0 Then
		Collider = 0
		If n <> Null Then
			PositionEntity n\Collider,0,3,0
			ResetEntity n\Collider
		EndIf
	EndIf
	
	If consoleMSG <> "" Then
		CreateConsoleMsg(consoleMSG)
	EndIf	
	
End Function

Function ManipulateNPCBones()
	Local n.NPCs,bone%,pvt%,bonename$
	Local maxvalue#,minvalue#,offset#,smooth#
	Local i%
	Local tovalue#
	
	For n = Each NPCs
		If n\ManipulateBone
			bonename$ = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"bonename",0)
			If bonename$<>""
				pvt% = CreatePivot()
				bone% = FindChild(n\obj,bonename$)
				If bone% = 0 Then RuntimeError "ERROR: NPC bone "+Chr(34)+bonename$+Chr(34)+" does not exist."
				PositionEntity pvt%,EntityX(bone%,True),EntityY(bone%,True),EntityZ(bone%,True)
				Select n\ManipulationType
					Case 0 ;<--- looking at player
						For i = 1 To GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controller_max",1)
							If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i,0) = "pitch"
								maxvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_max",2)
								minvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_min",2)
								offset# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_offset",2)
								If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_inverse",3)
									tovalue = -DeltaPitch(bone,Camera)+offset
								Else
									tovalue = DeltaPitch(bone,Camera)+offset
								EndIf
								;n\BonePitch = CurveAngle(tovalue,n\BonePitch,20.0)
								smooth# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_smoothing",2)
								If smooth>0.0
									n\BonePitch = CurveAngle(tovalue,n\BonePitch,smooth)
								Else
									n\BonePitch = tovalue
								EndIf
								n\BonePitch = ChangeAngleValueForCorrectBoneAssigning(n\BonePitch)
								n\BonePitch = Max(Min(n\BonePitch,maxvalue),minvalue)
							ElseIf GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis1",0) = "yaw"
								maxvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_max",2)
								minvalue# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_min",2)
								offset# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_offset",2)
								If GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_inverse",3)
									tovalue = -DeltaYaw(bone,Camera)+offset
								Else
									tovalue = DeltaYaw(bone,Camera)+offset
								EndIf
								;n\BoneYaw = CurveAngle(tovalue,n\BoneYaw,20.0)
								smooth# = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate,"controlleraxis"+i+"_smoothing",2)
								If smooth>0.0
									n\BoneYaw = CurveAngle(tovalue,n\BoneYaw,smooth)
								Else
									n\BoneYaw = tovalue
								EndIf
								n\BoneYaw = ChangeAngleValueForCorrectBoneAssigning(n\BoneYaw)
								n\BoneYaw = Max(Min(n\BoneYaw,maxvalue),minvalue)
							;ElseIf --> (Roll Value)
							;	
							EndIf
						Next
						
						RotateEntity bone%,EntityPitch(bone)+n\BonePitch,EntityYaw(bone)+n\BoneYaw,EntityRoll(bone)+n\BoneRoll
				End Select
				pvt = FreeEntity_Strict(pvt)
			EndIf
		EndIf
	Next
	
End Function

Function GetNPCManipulationValue$(NPC$,bone$,section$,valuetype%=0)
	;valuetype determines what type of variable should the Output be returned
	;0 - String
	;1 - Int
	;2 - Float
	;3 - Boolean
	
	Local value$ = GetINIString("Data\NPCBones.ini",NPC$,bone$+"_"+section$)
	Select valuetype%
		Case 0
			Return value$
		Case 1
			Return Int(value$)
		Case 2
			Return Float(value$)
		Case 3
			If value$ = "true" Lor value$ = "1"
				Return True
			Else
				Return False
			EndIf
	End Select
	
End Function

Function ChangeAngleValueForCorrectBoneAssigning(value#)
	Local numb#
	
	If value# <= 180.0
		numb# = value#
	Else
		numb# = -360+value#
	EndIf
	
	Return numb#
End Function

Function NPCSpeedChange(n.NPCs)
	
	Select n\NPCtype
		Case NPCtype173,NPCtypeOldMan,NPCtype096,NPCtype049,NPCtype939,NPCtype076
			Select SelectedDifficulty\otherFactors
				Case EASY
					n\Speed = n\Speed * 0.8
				Case HARD
					n\Speed = n\Speed * 1.2
			End Select
	End Select
	
End Function

Function PlayerInReachableRoom(canSpawnIn049Chamber=False)
	Local RN$ = ""
	Local e.Events, temp
	
	If PlayerRoom<>Null Then
		RN = PlayerRoom\RoomTemplate\Name
	EndIf
	
	;Player is in these rooms, returning false
	If RN = "pocketdimension" Lor RN = "gate_a_topside" Lor RN = "dimension_1499" Lor RN = "gate_b_topside" Lor RN = "gate_a_intro" Then
		Return False
	EndIf
	;Player is in 860's test room and inside the forest, returning false
	temp = False
	For e = Each Events
		If e\EventName$ = "testroom_860" And e\EventState = 1.0 Then
			temp = True
			Exit
		EndIf
	Next
	If RN = "testroom_860" And temp Then
		Return False
	EndIf
	If (Not canSpawnIn049Chamber) Then
		If SelectedDifficulty\aggressiveNPCs = False Then
			If RN = "cont_049" And EntityY(Collider)<=-2848*RoomScale Then
				Return False
			EndIf
		EndIf
	EndIf
	;Return true, this means player is in reachable room
	Return True
	
End Function

Function CheckForNPCInFacility(n.NPCs)
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(n\Collider)>100.0
		Return False
	EndIf
	If EntityY(n\Collider)< -10.0
		Return 2
	EndIf
	If EntityY(n\Collider)> 7.0 And EntityY(n\Collider)<=100.0
		Return 2
	EndIf
	
	Return True
End Function

Function FindNextElevator(n.NPCs)
	Local eo.ElevatorObj, eo2.ElevatorObj
	
	For eo = Each ElevatorObj
		If eo\InFacility = n\InFacility
			If Abs(EntityY(eo\obj,True)-EntityY(n\Collider))<10.0
				For eo2 = Each ElevatorObj
					If eo2 <> eo
						If eo2\InFacility = n\InFacility
							If Abs(EntityY(eo2\obj,True)-EntityY(n\Collider))<10.0
								If EntityDistanceSquared(eo2\obj,n\Collider)<EntityDistanceSquared(eo\obj,n\Collider)
									n\PathStatus = FindPath(n, EntityX(eo2\obj,True),EntityY(eo2\obj,True),EntityZ(eo2\obj,True))
									n\CurrElevator = eo2
									DebugLog "eo2 found for "+n\NPCtype
									Exit
								EndIf
							EndIf
						EndIf
					EndIf
				Next
				If n\CurrElevator = Null
					n\PathStatus = FindPath(n, EntityX(eo\obj,True),EntityY(eo\obj,True),EntityZ(eo\obj,True))
					n\CurrElevator = eo
					DebugLog "eo found for "+n\NPCtype
				EndIf
				If n\PathStatus <> 1
					n\CurrElevator = Null
					DebugLog "Unable to find elevator path: Resetting CurrElevator"
				EndIf
				Exit
			EndIf
		EndIf
	Next
	
End Function

Function GoToElevator(n.NPCs)
	Local distsquared#,inside%
	
	If n\PathStatus <> 1
		PointEntity n\obj,n\CurrElevator\obj
		RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\obj),EntityYaw(n\Collider),20.0),0
		
		inside% = False
		If Abs(EntityX(n\Collider)-EntityX(n\CurrElevator\obj,True))<280.0*RoomScale
			If Abs(EntityZ(n\Collider)-EntityZ(n\CurrElevator\obj,True))<280.0*RoomScale Then
				If Abs(EntityY(n\Collider)-EntityY(n\CurrElevator\obj,True))<280.0*RoomScale Then
					inside% = True
				EndIf
			EndIf
		EndIf
		
		distsquared = EntityDistanceSquared(n\Collider,n\CurrElevator\door\frameobj)
		If n\CurrElevator\door\open
			If (distsquared > PowTwo(0.4) And distsquared < PowTwo(0.7)) And inside%
				UseDoor(n\CurrElevator\door,False)
				DebugLog n\NPCtype+" used elevator"
			EndIf
		Else
			If distsquared < PowTwo(0.7)
				n\CurrSpeed = 0.0
				If n\CurrElevator\door\NPCCalledElevator=False
					n\CurrElevator\door\NPCCalledElevator = True
					DebugLog n\NPCtype+" called elevator"
				EndIf
			EndIf
		EndIf
	EndIf
	
End Function

Function FinishWalking(n.NPCs,startframe#,endframe#,speed#)
	Local centerframe#
	
	If n<>Null
		centerframe# = (endframe#-startframe#)/2
		If n\Frame >= centerframe#
			AnimateNPC(n,startframe#,endframe#,speed#,False)
		Else
			AnimateNPC(n,endframe#,startframe#,-speed#,False)
		EndIf
	EndIf
	
End Function

Function ChangeNPCTextureID(n.NPCs,textureid%)
	If (n=Null) Then
		CreateConsoleMsg("Tried to change the texture of an invalid NPC")
		If opt\ConsoleOpening Then
			ConsoleOpen = True
		EndIf
		Return
	EndIf
	
	n\TextureID = textureid+1
	EntityTexture(n\obj, DTextures[textureid])
	
End Function

Function IsNPCStuck(n.NPCs,time#)
	Local x#,y#,z#
	Local timer#
	
	timer=Max(timer-FPSfactor,0.0)
	DebugLog("Stuck Timer: "+timer)
	If timer<=0.0 Then
		If EntityX(n\Collider)=x Then
			If EntityZ(n\Collider)=z Then
				Return True
			EndIf
		EndIf
		timer=time
	EndIf
	If timer=time/2 Then
		x = EntityX(n\Collider)
		y = EntityY(n\Collider)
		z = EntityZ(n\Collider)
	EndIf
	
	Return False
End Function

Function NPC_GoTo(n.NPCs, IdleAnim.Vector3D, MoveAnim.Vector3D, path_target%, n_speed#=0.0)
	Local WaypointDist# = 0.0
	Local temp% = False
	
	Local StuckTimer# = 5.0
	
	Local w.WayPoints
	
	While n\Path[n\PathLocation]=Null
		If n\PathLocation > 19 Then
			n\PathLocation = 0 : n\PathStatus = 0 : Exit
		Else
			n\PathLocation = n\PathLocation + 1
		EndIf
	Wend
	If n\PathStatus = 1 Then
		n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\obj,n\Path[n\PathLocation]\obj)
		RotateEntity n\Collider, 0, CurveAngle(n\Angle, EntityYaw(n\Collider), 20.0), 0
		
		AnimateNPC(n, MoveAnim\x, MoveAnim\y, n\CurrSpeed * 100 * MoveAnim\z)
		
		n\CurrSpeed = CurveValue(n\Speed * n_speed, n\CurrSpeed, 20.0)
		
		MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
		
		WaypointDist = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
		If WaypointDist < 0.77 Then
			temp = True
			If n\Path[n\PathLocation]\door<>Null Then
				If (Not n\Path[n\PathLocation]\door\IsElevatorDoor) Then
					If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>"" Then
						temp = False
					Else
						If n\Path[n\PathLocation]\door\open = False Then UseDoorNPC(n\Path[n\PathLocation]\door, n, True)
						;Check if the beeping will be necessary
						If n\NPCtype = NPCtypeMTF And n\Path[n\PathLocation]\door\open = False Then PlaySound2(LoadTempSound("SFX\Character\MTF\Beep.ogg"), Camera, n\Collider)
					EndIf
				EndIf
			EndIf
			If WaypointDist < 0.3 And temp Then
				n\PathLocation = n\PathLocation + 1
			ElseIf WaypointDist<0.5 And (Not temp) Then
				n\PathStatus = 0
				n\PathTimer# = 0.0
			EndIf
		EndIf
		
		If IsNPCStuck(n, 70*StuckTimer) Then
			CreateConsoleMsg("NPCtype "+n\NPCName+": "+n\ID+" . Restarting pathfinding...", 255)
			n\PathStatus = 0
		EndIf
	Else
		n\PathTimer = Max(0, n\PathTimer-FPSfactor)
		AnimateNPC(n, IdleAnim\x, IdleAnim\y, IdleAnim\z)
		n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
		If n\PathTimer = 0.0 Then
			n\PathStatus = FindPath(n,EntityX(path_target),EntityY(path_target)+0.2,EntityZ(path_target))
			n\PathTimer = 70*2
		EndIf
	EndIf
End Function

Function NPC_GoToRoom(n.NPCs, IdleAnim.Vector3D, MoveAnim.Vector3D, n_speed#=0.0)
	Local WaypointDist# = 0.0
	Local temp% = False
	
	Local w.WayPoints, r.Rooms
	
	While n\Path[n\PathLocation]=Null
		If n\PathLocation > 19 Then
			n\PathLocation = 0 : n\PathStatus = 0 : Exit
		Else
			n\PathLocation = n\PathLocation + 1
		EndIf
	Wend
	If n\PathStatus = 1 Then
		n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\obj,n\Path[n\PathLocation]\obj)
		RotateEntity n\Collider, 0, CurveAngle(n\Angle, EntityYaw(n\Collider), 20.0), 0
		
		AnimateNPC(n, MoveAnim\x, MoveAnim\y, n\CurrSpeed * 100 * MoveAnim\z)
		
		If n\Speed<>0 Then
			n\CurrSpeed = CurveValue(n\Speed * n_speed, n\CurrSpeed, 20.0)
		Else
			n\CurrSpeed = CurveValue(n\Speed * n_speed, n\CurrSpeed, 20.0)
		EndIf
		
		MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
		
		WaypointDist = EntityDistanceSquared(n\Collider,n\Path[n\PathLocation]\obj)
		If WaypointDist < 0.77 Then
			temp = True
			If n\Path[n\PathLocation]\door<>Null Then
				If (Not n\Path[n\PathLocation]\door\IsElevatorDoor) Then
					If n\Path[n\PathLocation]\door\locked Lor n\Path[n\PathLocation]\door\Code<>"" Then
						temp = False
					Else
						If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
					EndIf
				EndIf
			EndIf
			If WaypointDist<0.3 And temp Then
				n\PathLocation = n\PathLocation + 1
			ElseIf WaypointDist<0.5 And (Not temp) Then
				n\PathStatus = 0
				n\PathTimer# = 0.0
			EndIf
		EndIf
	Else
		AnimateNPC(n, IdleAnim\x, IdleAnim\y, IdleAnim\z)
		n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
		MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
		If n\PathTimer = 0.0 Then
			Local roomfound% = False
			While roomfound = False
				If n\NPCRoom=Null Then
					GetNPCRoom(n)
				EndIf
				For r.Rooms = Each Rooms
					If IsRoomAdjacent(r,n\NPCRoom) Then
						If Rand(5)=1 Then
							n\EnemyX = EntityX(r\obj)
							n\EnemyY = EntityY(r\obj)
							n\EnemyZ = EntityZ(r\obj)
							n\NPCRoom = r
							roomfound = True
							Exit
						EndIf
					EndIf
				Next
			Wend
			If n\Idle > 0.1 Then
				n\CurrSpeed = 0.0
			EndIf
			n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY,n\EnemyZ)
			n\PathTimer = 70*5
			If n\Path[2]=Null Then
				For w.WayPoints = Each WayPoints
					If EntityDistanceSquared(w\obj,Collider)<=2.8284 Then
						n\PathLocation = 2
						n\Path[2] = w
						Exit
					EndIf
				Next
			EndIf
		EndIf
	EndIf
End Function

Function NPC_GoToCover%(n.NPCs, MoveAnim.Vector3D, target%, n_speed#=0.0)
	Local waypoint.WayPoints
	Local i%, visible%
	
	If n\Path[0] = Null Then
		n\Path[0] = GetClosestWaypoint(n\Collider, True, target)
		If n\Path[0] = Null Lor ((n\Path[0]\door <> Null) And (Not n\Path[0]\door\open)) Then
			n\Path[0] = Null
			n\CurrSpeed = 0
			Return True
		EndIf
	EndIf
	
	If EntityDistanceSquared(n\Collider, n\Path[0]\obj) > PowTwo(0.65) Then
		n\CurrSpeed = CurveValue(n\Speed * n_speed, n\CurrSpeed, 20.0)
		PointEntity n\obj, n\Path[0]\obj
		RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
		MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
		AnimateNPC(n, MoveAnim\x, MoveAnim\y, n\CurrSpeed * 100 * MoveAnim\z)
	Else
		n\CurrSpeed = 0
		n\Path[0] = Null
		Return True
	EndIf
	
	Return False
End Function

Function GetClosestWaypoint.WayPoints(entity%, mustBeVisible% = False, target% = 0)
	Local dist# = 10000.0
	Local dist2#, smallestway.WayPoints
	Local w.WayPoints
	
	For w = Each WayPoints
		dist2# = EntityDistanceSquared(entity, w\obj)
		If dist2 < dist And ((Not mustBeVisible) Lor EntityVisible(entity, w\obj)) Then
			If target = 0 Lor (Not EntityVisible(target, w\obj)) Then
				dist = dist2
				smallestway = w
			EndIf
		EndIf
	Next
	
	Return smallestway
End Function

Function NPCSeesEntity(n.NPCs, target)
	If EntityDistanceSquared(n\Collider, target)<PowTwo(7) Then
		If EntityVisible(n\Collider, target) Then
			Return True
		EndIf
	EndIf
	
	Return False
End Function

Function PlayNPCSound(n.NPCs, sound$, loop%=False, range#=10, volume#=1.0)
	range# = Max(range, 1.0)
    If loop Then
        n\SoundChn = LoopSound2(sound, n\SoundChn, Camera, n\Collider, range, volume)
    Else
        n\SoundChn = PlaySound2(sound, Camera, n\Collider, range, volume)
    EndIf
	
	Local dist# = EntityDistance(Camera, n\Collider) / range#
	If 1 - dist# > 0 And 1 - dist# < 1 Then
		ChannelVolume(n\SoundChn, volume# * (1 - dist#)*opt\VoiceVol#*opt\MasterVol)
    EndIf
	
    If (Not ChannelPlaying(n\SoundChn)) Then
        FreeSound_Strict(sound)
        n\SoundChn = 0
    EndIf
End Function

Function SwitchNPCGun%(n.NPCs, WeaponID%)
	CatchErrors("SwitchNPCGun(" + n\NPCtype + ", " + WeaponID + ")")
	Local g.Guns
	Local bone%, gunname$
	Local Scale#, VectorString$, WeaponType%
	
	RemoveNPCGun(n)
	
	Local prevYaw# = EntityYaw(n\obj)
	Local prevX# = EntityX(n\obj)
	Local prevY# = EntityY(n\obj)
	Local prevZ# = EntityZ(n\obj)
	
	RotateEntity n\obj,0,0,0
	PositionEntity n\obj,0,0,0
	
	bone = FindChild(n\obj,GetINIString("Data\NPCBones.ini", n\NPCName, "weapon_hand_bonename"))
	
	gunname$ = ""
	For g = Each Guns
		If g\ID = WeaponID Then
			n\Gun = New NPCGun
			n\Gun\obj = CopyEntity(g\PlayerModel, bone)
			n\Gun\ID = g\ID
			n\Gun\AnimType = g\PlayerModelAnim
			n\Gun\MaxAmmo = g\MaxCurrAmmo
			n\Gun\Ammo = g\CurrAmmo
			n\Gun\Name = g\name
			n\Gun\MaxGunshotSounds = g\MaxShootSounds
			n\Gun\Damage = g\DamageOnEntity
			n\Gun\ShootFrequency = g\Rate_Of_Fire
			n\Gun\BulletsPerShot = g\Amount_Of_Bullets
			gunname = g\name
			Exit
		EndIf
	Next
	
	If gunname <> "" Then
		Scale# = GetINIFloat("Data\weapons.ini", gunname, "world scale", 0.02) / EntityScaleX(n\obj)
		ScaleEntity n\Gun\obj,Scale,Scale,Scale
		
		VectorString = GetINIString("Data\weapons.ini", gunname, "player_model_offset", "")
		If VectorString <> "" Then
			PositionEntity n\Gun\obj, Piece(VectorString,1,"|"), Piece(VectorString,2,"|"), Piece(VectorString,3,"|")
		EndIf
		VectorString = GetINIString("Data\weapons.ini", gunname, "player_model_rotation", "")
		If VectorString <> "" Then
			RotateEntity n\Gun\obj, Piece(VectorString,1,"|"), Piece(VectorString,2,"|"), Piece(VectorString,3,"|")
		EndIf
	EndIf
	
	RotateEntity n\obj,0,prevYaw,0
	PositionEntity n\obj,prevX,prevY,prevZ
	
	CatchErrors("Uncaught (SwitchNPCGun(" + n\NPCtype + ", " + WeaponID + "))")
End Function

Function RemoveNPCGun(n.NPCs)
	
	If n\Gun <> Null Then
		EntityParent n\Gun\obj, 0
		n\Gun\obj = FreeEntity_Strict(n\Gun\obj)
		Delete n\Gun
	EndIf
	
End Function

Function IsTarget(n.NPCs, target.NPCs)
	
	If n <> Null And target <> Null Then
		Select n\NPCtype
			Case NPCtypeMTF
				Select target\NPCtype
					Case NPCtypeD2
						Return True
					Case NPCtypeZombie
						If target\State = Z_STATE_WANDER Then
							Return True
						EndIf
					Case NPCtype008
						If target\State = 2 Then
							Return True
						EndIf
					Case NPCtype939
						Return True
					Case NPCtypeOldMan
						Return True
					Case NPCtype173
						Return True
					Case NPCtype049
						Return True
					Case NPCtype096
						Return True
				End Select
		End Select
	EndIf
	
	Return False
End Function

Function GetNPCRoom(n.NPCs)
	Local r.Rooms
	Local x#, z#
	
	For r.Rooms = Each Rooms
		x = Abs(r\x-EntityX(n\Collider,True))
		z = Abs(r\z-EntityZ(n\Collider,True))
		
		If x<16 And z < 16 Then
			If x < 4.0 Then
				If z < 4.0 Then
					If Abs(EntityY(n\Collider) - EntityY(r\obj)) < 1.5 Then
						n\NPCRoom = r
						Exit
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PreloadAllNPCAnimations()
	
	;Class-D
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "idle")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "walk")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "death_front")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "death_back")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "death_left")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "death_right")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "pistol_idle")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "pistol_walk")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "pistol_reload")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "rifle_idle")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "rifle_walk")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "rifle_reload")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "shotgun_idle")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "shotgun_walk")
	PreloadNPCAnimation(NPCtypeD2, "Class-D", "shotgun_reload")
	
End Function

Function PreloadNPCAnimation(NPCtype%, NPCName$, AnimName$)
	Local file$ = "Data\NPCAnims.ini"
	Local na.NPCAnim
	
	Local AnimString$ = GetINIString(file, NPCName, AnimName)
	If AnimString<>"" Then
		na = New NPCAnim
		na\NPCtype = NPCtype
		na\AnimName = AnimName
		na\Animation = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	
End Function

Function FindNPCAnimation.Vector3D(NPCtype%, AnimName$)
	Local na.NPCAnim
	
	For na = Each NPCAnim
		If na\NPCtype = NPCtype And na\AnimName = AnimName Then
			Return na\Animation
		EndIf
	Next
	Return Null
	
End Function

;~IDEal Editor Parameters:
;~F#2#29#B0#C2#CB#DF#10A#125#14A#164#171#189#1A2#1B5#1BC#1D1#1E6#1FB#27C#2B7
;~F#433#5F8#6A9#6B2#793#85C#860#8C4#8C9
;~C#Blitz3D