;This BB file is actually defining the weapons and all their functions. You can easily add your own weapon by first putting it as a new type variable into
;the "InitGuns" function. After this, you need to update your gun using the "UpdateGuns" function. If you still not know how to add a new gun into the game,
;then write me a PM at the UnderTowGames Forum (my username on this forum: PXLSHN). But if you really wanna add a new weapon into that game, then you should
;try it out first by yourself (just using the code inside the "InitGuns" and "UpdateGuns" functions as a template.
;BTW: You need to let the worldmodel load by creating an item that is the gun, so it can be held in the inventory. The viewmodel of the gun must be loaded inside
;the main.bb file (also changing the scale values and other things there).

;Gun constants
;[Block]
;Gun IDs ;TODO - Need to be replaced with something else in the future!
Const GUN_UNARMED = 0
Const GUN_P90 = 1
Const GUN_USP = 2
Const GUN_MP5K = 3
Const GUN_SPAS12 = 4
Const GUN_KNIFE = 5
Const GUN_BERETTA = 6
Const GUN_CROWBAR = 7
;Gun Types
Const GUNTYPE_UNKNOWN = -1
Const GUNTYPE_SEMI = 0
Const GUNTYPE_AUTO = 1
Const GUNTYPE_SHOTGUN = 2
Const GUNTYPE_MELEE = 3
;Animation Types
Const GUNANIM_SMG = 0
Const GUNANIM_PISTOL = 1
Const GUNANIM_MP5K = 2
Const GUNANIM_SHOTGUN = 3
Const GUNANIM_MELEE = 4
Const GUNANIM_RIFLE = 5
;Weapon Decal Types
Const GUNDECAL_DEFAULT = 0
Const GUNDECAL_SLASH = 1
;Other
Const MaxGunSlots = 3
Const GunSlot1 = 0
Const GunSlot2 = 1
Const GunSlot3 = 2
Const MaxShootSounds = 4
;[End Block]

Global AimCrossIMG
Global ExtraKevlarIMG
Global BulletIcon%
Global P90BulletMeter
Global Crowbar_HitPivot
Global MuzzleFlash

Global KEY_RELOAD = GetINIInt(gv\OptionFile, "binds", "Reload key", 19)
Global KEY_HOLSTERGUN = GetINIInt(gv\OptionFile, "binds", "Holstergun key", 16)

Global BulletHole1,BulletHole2,DustParticle

Global GunPickPivot

Global GunPivot_Y#
Global GunPivot_YSide% = 0
Global GunPivot_X#
Global GunPivot_XSide% = 0

Global UsingScope%
Global ScopeTexture
Global ScopeCam
Global ScopeZoom#
Global ScopeNVG%

Global GunSFX,GunSFX2,GunCHN,GunCHN2

Global CanPlayerUseGuns% = True

Global IsPlayerShooting% = False

Global IronSightPivot%,IronSightPivot2%

Global NVGOnSFX%, NVGOffSFX%

Global NTF_InfiniteAmmo% = False
Global NTF_NoReload% = False

Global GunParticle

Type GunInstance
	Field GunAnimFLAG%
	Field GunChangeFLAG%
	;Field GunUpdateFLAG%
	Field Weapon_CurrSlot%
	Field HoldingGun%
	Field Weapon_InSlot$[MaxGunSlots]
	;Field Weapon_NoSlot$
	Field KevlarSFX%
	Field ShootEmptySFX%
	Field GunPivot
	Field UI_Select_SFX%
	Field UI_Deny_SFX%
	Field GunLight%
	Field GunLightTimer#
	Field AttachSilencerSFX%
	Field IronSight%
	Field IronSightAnim%
End Type

;Type defination for the weapon + info
;TODO: Reduce the amount of Fields!
Type Guns
	;TODO: Clean this up!
	Field GunType%
	Field ID					;<--- The ID of the gun. WARNING: Overwriting existing IDs could cause to the game would be glitched or at the worst case a MAV
	Field IMG					;<--- The Quick Selection Slot Image of the gun (musn't be the item IMG!)
	Field CurrAmmo				;<--- Current ammo in a magazine
	Field MaxCurrAmmo			;<--- Max ammo in a magazine
	Field CurrReloadAmmo		;<--- Current reload ammo
	Field MaxReloadAmmo			;<--- Max reload ammo
	Field DamageOnEntity		;<--- How much damage the gun makes, when the bullet you shot hit an hurtable entity
	Field Accuracy#				;<--- How good the accuracy of the guns is (optional, but would be better to change it, 0.0 is best acuracy)
	Field CanHaveSilencer		;<--- Does the gun has the ability to have a silencer atached to it?
	Field ShootState#			;<--- Dont change this variables in the "CreateGun" or "InitGuns" functions!
	Field ReloadState#			;<--- 								"-"
	Field DeployState#			;<---								"-"
	Field GunState				;<---								"-"
	Field Deployed%				;<---								"-"
	Field Holster%				;<---								"-"
	Field ShootAnim				;<---								"-"
	Field HasSilencer%			;<---								"-"
	Field SilenState#			;<---								"-"
	Field Frame#
	Field name$
	Field DisplayName$
	Field MouseDownTimer#
	Field obj%
	Field CanUseIronSight
	Field ViewModelPath$
	Field ViewModelPath2$
	Field IMGPath$
	Field IMGPath2$
	Field IronSightCoords.Vector3D
	Field Anim_Deploy.Vector3D
	Field Anim_Shoot.Vector3D
	Field Anim_Reload.Vector3D
	Field Anim_Reload_Start.Vector3D
	Field Anim_Reload_Stop.Vector3D
	Field Anim_Sprint_Transition.Vector3D
	Field Anim_Sprint_Cycle.Vector3D
	Field Frame_Idle#
	Field Anim_NoAmmo_Deploy.Vector3D
	Field Anim_NoAmmo_Shoot.Vector3D
	Field Anim_NoAmmo_Sprint_Transition.Vector3D
	Field Anim_NoAmmo_Sprint_Cycle.Vector3D
	Field Frame_NoAmmo_Idle#
	Field Knockback#
	Field Rate_Of_Fire#
	Field Reload_Time#
	Field Reload_Start_Time#
	Field Deploy_Time#
	Field MaxShootSounds%
	Field MaxReloadSounds%
	Field MaxWallhitSounds%
	Field Slot%
	Field Amount_Of_Bullets%
	Field ShootDelay#
	Field Range#
	Field ShootSounds%[MaxShootSounds]
	Field MuzzleFlash%
	Field PlayerModel%
	Field PlayerModelAnim%
	Field ShouldCreateItem%
	Field AttachedItemTemplate.ItemTemplates
	Field DecalType%
End Type

;Type defination for the bullets. You dont need to work with those
Type Bullet
	Field Numb
	Field DamageOnEntity
	Field FlySpeed
	Field Accuracy
End Type

Type BulletHole
	Field obj%
	Field obj2%
	Field obj3%
	Field obj4%
	Field obj5%
	Field obj6%
	Field KillTimer#,KillTimer2#
End Type

;Dont put anything inside this function, unless you want to make a special weapon like a rocket launcher or a nuke
Function CreateGun.Guns(name$, id, model$, img$, canhavesilencer = False, CanUseIronSight = True)
	Local g.Guns = New Guns
	Local i%
	
	g\name$ = name$
	g\ID = id
	g\IMG = LoadImage("GFX\weapons\"+img$)
	MaskImage(g\IMG, 255, 0, 255)
	
	g\CurrAmmo = GetINIInt("Data\weapons.ini",name$,"ammo",1)
	g\MaxCurrAmmo = g\CurrAmmo
	g\CurrReloadAmmo = 0
	g\MaxReloadAmmo = GetINIInt("Data\weapons.ini",name$,"reload_ammo")
	g\DamageOnEntity = GetINIInt("Data\weapons.ini",name$,"damage",1)
	g\Accuracy = GetINIFloat("Data\weapons.ini",name$,"accuracy",1.0)
	g\CanHaveSilencer = canhavesilencer
	g\obj = LoadAnimMesh_Strict("GFX\weapons\"+model$,g_I\GunPivot)
	ScaleEntity g\obj,0.005,0.005,0.005
	MeshCullBox(g\obj,-MeshWidth(g\obj)*3,-MeshHeight(g\obj)*3,-MeshDepth(g\obj)*3,MeshWidth(g\obj)*6,MeshHeight(g\obj)*6,MeshDepth(g\obj)*6)
	HideEntity g\obj
	g\CanUseIronSight = CanUseIronSight
	
	g\ViewModelPath = model$
	g\IMGPath = img$
	
	Local StrTemp$ = GetINIString("Data\weapons.ini", g\name, "type", "semi")
	Select StrTemp
		Case "semi"
			g\GunType = GUNTYPE_SEMI
		Case "auto"
			g\GunType = GUNTYPE_AUTO
		Case "shotgun"
			g\GunType = GUNTYPE_SHOTGUN
		Case "melee"
			g\GunType = GUNTYPE_MELEE
		Default
			g\GunType = GUNTYPE_UNKNOWN
	End Select
	
	g\Knockback = GetINIFloat("Data\weapons.ini",name,"knockback")
	g\Rate_Of_Fire = GetINIFloat("Data\weapons.ini",name,"rate_of_fire",1.0)
	g\Reload_Time = GetINIFloat("Data\weapons.ini",name,"reload_time",1.0)
	g\Reload_Start_Time = GetINIFloat("Data\weapons.ini",name,"reload_start_time",1.0)
	g\Deploy_Time = GetINIFloat("Data\weapons.ini",name,"deploy_time",1.0)
	g\MaxShootSounds = GetINIInt("Data\weapons.ini",name,"sounds_shoot",1)
	g\MaxReloadSounds = GetINIInt("Data\weapons.ini",name,"sounds_reload",1)
	g\MaxWallhitSounds = GetINIInt("Data\weapons.ini",name,"sounds_wallhit",1)
	g\Amount_Of_Bullets = GetINIInt("Data\weapons.ini",name,"amount_of_bullets",1)
	
	Local AnimString$
	;Deploy
	AnimString = GetINIString("Data\weapons.ini",name,"anim_deploy","")
	g\Anim_Deploy = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	;Shoot
	AnimString = GetINIString("Data\weapons.ini",name,"anim_shoot","")
	g\Anim_Shoot = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	;Reload
	AnimString = GetINIString("Data\weapons.ini",name,"anim_reload","")
	g\Anim_Reload = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	;Sprint Transition
	AnimString = GetINIString("Data\weapons.ini",name,"anim_sprint_transition","")
	g\Anim_Sprint_Transition = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	;Sprint Cycle
	AnimString = GetINIString("Data\weapons.ini",name,"anim_sprint_cycle","")
	g\Anim_Sprint_Cycle = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	;Deploy No Ammo
	AnimString = GetINIString("Data\weapons.ini",name,"anim_noammo_deploy","")
	If AnimString<>"" Then
		g\Anim_NoAmmo_Deploy = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	;Shoot No Ammo
	AnimString = GetINIString("Data\weapons.ini",name,"anim_noammo_shoot","")
	If AnimString<>"" Then
		g\Anim_NoAmmo_Shoot = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	;Sprint Transition No Ammo
	AnimString = GetINIString("Data\weapons.ini",name,"anim_noammo_sprint_transition","")
	If AnimString<>"" Then
		g\Anim_NoAmmo_Sprint_Transition = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	;Sprint Cycle No Ammo
	AnimString = GetINIString("Data\weapons.ini",name,"anim_noammo_sprint_cycle","")
	If AnimString<>"" Then
		g\Anim_NoAmmo_Sprint_Cycle = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	;Reload Start
	AnimString = GetINIString("Data\weapons.ini",name,"anim_reload_start","")
	If AnimString<>"" Then
		g\Anim_Reload_Start = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	;Reload Stop
	AnimString = GetINIString("Data\weapons.ini",name,"anim_reload_stop","")
	If AnimString<>"" Then
		g\Anim_Reload_Stop = CreateVector3D(Piece(AnimString,1,"|"),Piece(AnimString,2,"|"),Piece(AnimString,3,"|"))
	EndIf
	g\Frame_Idle = GetINIFloat("Data\weapons.ini",name,"frame_idle")
	g\Frame_NoAmmo_Idle = GetINIFloat("Data\weapons.ini",name,"frame_noammo_idle")
	
	Local VectorString$
	VectorString = GetINIString("Data\weapons.ini",name,"offset","")
	MoveEntity g\obj,Piece(VectorString,1,"|"),Piece(VectorString,2,"|"),Piece(VectorString,3,"|")
	VectorString = GetINIString("Data\weapons.ini",name,"aimoffset","")
	If VectorString<>"" Then
		g\IronSightCoords = CreateVector3D(Piece(VectorString,1,"|"),Piece(VectorString,2,"|"),Piece(VectorString,3,"|"))
	EndIf
	
	g\DisplayName = GetINIString("Data\weapons.ini",name,"display_name",name)
	StrTemp$ = GetINIString("Data\weapons.ini",name,"slot","secondary")
	Select StrTemp
		Case "primary"
			g\Slot = SLOT_PRIMARY
		Case "secondary"
			g\Slot = SLOT_SECONDARY
		Case "melee"
			g\Slot = SLOT_MELEE
	End Select
	
	g\ShootDelay = GetINIFloat("Data\weapons.ini",name,"attack_delay")
	g\Range = GetINIFloat("Data\weapons.ini",name,"range")
	
	;Preload gun sounds
	If g\MaxShootSounds > 1 Then
		For i = 0 To g\MaxShootSounds-1
			g\ShootSounds[i] = LoadSound_Strict("SFX\Guns\"+g\name+"\shoot"+(i+1)+".ogg")
		Next
	Else
		g\ShootSounds[0] = LoadSound_Strict("SFX\Guns\"+g\name+"\shoot.ogg")
	EndIf
	
	VectorString = GetINIString("Data\weapons.ini",name,"muzzleoffset","")
	g\MuzzleFlash = CreateSprite()
	EntityFX g\MuzzleFlash,1
	SpriteViewMode g\MuzzleFlash,2
	EntityTexture g\MuzzleFlash,ParticleTextures[1]
	EntityParent g\MuzzleFlash,g\obj
	PositionEntity g\MuzzleFlash,Piece(VectorString,1,"|"),Piece(VectorString,2,"|"),Piece(VectorString,3,"|")
	HideEntity g\MuzzleFlash
	
	g\PlayerModel = LoadAnimMesh_Strict("GFX\weapons\"+g\name+"_worldmodel.b3d")
	HideEntity g\PlayerModel
	
	Local SimpleString$ = GetINIString("Data\weapons.ini",name,"player_model_anim","")
	Select Lower(SimpleString)
		Case "smg"
			g\PlayerModelAnim = GUNANIM_SMG
		Case "pistol"
			g\PlayerModelAnim = GUNANIM_PISTOL
		Case "mp5k"
			g\PlayerModelAnim = GUNANIM_MP5K
		Case "shotgun"
			g\PlayerModelAnim = GUNANIM_SHOTGUN
		Case "melee"
			g\PlayerModelAnim = GUNANIM_MELEE
		Default
			RuntimeError "ERROR: Weapon type " + SimpleString + " doesn't exist!"
	End Select
	
	g\ShouldCreateItem = GetINIInt("Data\weapons.ini",name,"generate_item",1)
	
	SimpleString = GetINIString("Data\weapons.ini",name,"decal_type","bullet")
	Select Lower(SimpleString)
		Case "bullet"
			g\DecalType = GUNDECAL_DEFAULT
		Case "slash"
			g\DecalType = GUNDECAL_SLASH
		Default
			RuntimeError "ERROR: Weapon decal type " + SimpleString + " doesn't exist!"
	End Select
	
	Return g
End Function

;Here you need to define your weapon (look for the other guns as an example)
Function InitGuns()
	Local g.Guns
	Local it.ItemTemplates
	Local f%,l$, i%
	Local gunID%
	
	g_I.GunInstance = New GunInstance
	
	g_I\GunAnimFLAG = False
	g_I\GunChangeFLAG = False
	
	UsingScope% = False
	
	ScopeNVG% = False
	
	CanPlayerUseGuns% = True
	
	NTF_InfiniteAmmo% = False
	NTF_NoReload% = False
	
	For i = 0 To MaxGunSlots-1
		g_I\Weapon_InSlot[i] = ""
	Next
	
	AimCrossIMG = LoadImage_Strict("GFX\AimCross.png")
	BulletIcon% = LoadImage_Strict("GFX\bulleticon.png")
	P90BulletMeter = LoadImage_Strict("GFX\P90_BulletMeter.jpg")
	ExtraKevlarIMG = LoadImage_Strict("GFX\ExtraKevlarMeter.jpg")
	
	g_I\KevlarSFX = LoadSound_Strict("SFX\kevlarsound.ogg") ; TODO THIS NEVER EXISTED
	
	g_I\ShootEmptySFX = LoadSound_Strict("SFX\Guns\shoot_empty.ogg")
	
	g_I\GunPivot = CreatePivot()
	
	GunPickPivot = CreatePivot()
	EntityParent GunPickPivot,g_I\GunPivot
	
	f = ReadFile("Data\weapons.ini")
	While Not Eof(f)
		l = ReadLine(f)
		If Left(l,1) = "[" Then
			l = Mid(l,2,Len(l)-2)
			DebugLog l
			gunID = gunID + 1
			g.Guns = CreateGun(l,gunID,l+"_viewmodel.b3d","INV"+l+".jpg",False,True)
			If g\ShouldCreateItem Then
				it = CreateItemTemplate(g\DisplayName,g\name,"GFX\weapons\"+g\name+"_worldmodel.b3d","GFX\weapons\INV"+g\name+".jpg","",GetINIFloat("Data\weapons.ini",g\name,"world scale",0.02))
				it\isGun% = True
				it\sound = 66
			EndIf
		EndIf
	Wend
	CloseFile f
	
	g_I\UI_Select_SFX = LoadSound_Strict("SFX\HUD\GunSelect_Accept.ogg")
	g_I\UI_Deny_SFX = LoadSound_Strict("SFX\HUD\GunSelect_Deny.ogg")
	
	IsPlayerShooting% = False
	
	IronSightPivot% = CreatePivot(g_I\GunPivot)
	IronSightPivot2% = CreatePivot(g_I\GunPivot)
	g_I\IronSight% = False
	g_I\IronSightAnim% = False
	
	NVGOnSFX% = LoadSound_Strict("SFX\Guns\NVG_Scope_On.ogg")
	NVGOffSFX% = LoadSound_Strict("SFX\Guns\NVG_Scope_Off.ogg")
	
	g_I\GunLight = CreateLight(2,g_I\GunPivot)
	LightColor g_I\GunLight,235,55,0
	LightRange g_I\GunLight,0.4
	HideEntity g_I\GunLight
	g_I\GunLightTimer = 0.0
	
	g_I\AttachSilencerSFX = LoadSound_Strict("SFX\Guns\attachsilencer.ogg")
	
End Function

Function DeleteGuns()
	Local g.Guns,i
	
	For g.Guns = Each Guns
		;If g\IMG<>0 Then FreeImage g\IMG:g\IMG=0
		Delete g
	Next
	
	FreeImage AimCrossIMG : AimCrossIMG = 0
	FreeImage BulletIcon% : BulletIcon% = 0
	FreeImage P90BulletMeter : P90BulletMeter = 0
	FreeImage ExtraKevlarIMG : ExtraKevlarIMG = 0
	
	FreeSound_Strict g_I\KevlarSFX : g_I\KevlarSFX = 0
	
	FreeSound_Strict g_I\ShootEmptySFX : g_I\ShootEmptySFX = 0
	
	If g_I\UI_Select_SFX <> 0 Then FreeSound_Strict(g_I\UI_Select_SFX) : g_I\UI_Select_SFX=0
	If g_I\UI_Deny_SFX <> 0 Then FreeSound_Strict(g_I\UI_Deny_SFX) : g_I\UI_Deny_SFX=0
	
	If NVGOnSFX <> 0 Then FreeSound_Strict(NVGOnSFX) : NVGOnSFX=0
	If NVGOffSFX <> 0 Then FreeSound_Strict(NVGOffSFX) : NVGOffSFX=0
	
End Function

Function UpdateGuns()
	Local isMultiplayer% = (gopt\GameMode = GAMEMODE_MULTIPLAYER) ;True if it's multiplayer, otherwise False
	Local g.Guns,g2.Guns,p.Particles,n.NPCs,pl.Player,i%
	Local shooting% = False
	Local currGun.Guns
	
	Local campitch#, camyaw#, playerAlive%
	If isMultiplayer Then
		campitch# = EntityPitch(mpl\CameraPivot)+180
		camyaw# = EntityYaw(mpl\CameraPivot)
		Players[mp_I\PlayerID]\PressMouse1=False
		playerAlive = Players[mp_I\PlayerID]\CurrHP > 0
	Else
		campitch# = EntityPitch(Camera)+180
		camyaw# = EntityYaw(Camera)
		playerAlive = psp\Health > 0
	EndIf
	
	If g_I\IronSight Then
		RotateEntity g_I\GunPivot, campitch-180, camyaw, 0
	Else
		Local gpivotpitch# = EntityPitch(g_I\GunPivot)+180
		Local gpivotyaw# = EntityYaw(g_I\GunPivot)
		Local pitch# = Clamp(CurveAngle(campitch, gpivotpitch, 10.0), campitch-5, campitch+5)
		Local yaw# = CurveAngle(camyaw, gpivotyaw, 10.0)
		yaw = ClampAngle(yaw, camyaw, 5)
		RotateEntity g_I\GunPivot, pitch-180, yaw, 0
	EndIf
	
	g_I\GunAnimFLAG = True
	UsingScope% = False
	IsPlayerShooting% = False
	
	ShowEntity g_I\GunPivot
	If g_I\GunLightTimer > 0.0 And g_I\GunLightTimer < 2.5 Then
		g_I\GunLightTimer = g_I\GunLightTimer + FPSfactor
		ShowEntity g_I\GunLight
	Else
		g_I\GunLightTimer = 0.0
		HideEntity g_I\GunLight
	EndIf
	
	Local prevFrame#
	Local j
	
	Local pHoldingGun%, pDeployState#, pReloadState#, pShootState#, pPressMouse1%, pPressReload%, pAmmo%, pReloadAmmo%, pIsPlayerSprinting%, pIronSight%
	If isMultiplayer Then
		pHoldingGun = Players[mp_I\PlayerID]\WeaponInSlot[Players[mp_I\PlayerID]\SelectedSlot]
		pDeployState = Players[mp_I\PlayerID]\DeployState
		pReloadState = Players[mp_I\PlayerID]\ReloadState
		pShootState = Players[mp_I\PlayerID]\ShootState
		pPressMouse1 = Players[mp_I\PlayerID]\PressMouse1
		pPressReload = Players[mp_I\PlayerID]\PressReload
		If Players[mp_I\PlayerID]\SelectedSlot < (MaxSlots - SlotsWithNoAmmo) Then
			pAmmo = Players[mp_I\PlayerID]\Ammo[Players[mp_I\PlayerID]\SelectedSlot]
			pReloadAmmo = Players[mp_I\PlayerID]\ReloadAmmo[Players[mp_I\PlayerID]\SelectedSlot]
		EndIf
		pIsPlayerSprinting = Players[mp_I\PlayerID]\IsPlayerSprinting
		pIronSight = Players[mp_I\PlayerID]\IronSight
	Else
		For g = Each Guns
			If g\ID = g_I\HoldingGun Then
				currGun = g
				Exit
			EndIf
		Next
		pHoldingGun = g_I\HoldingGun
		pDeployState = psp\DeployState
		pReloadState = psp\ReloadState
		pShootState = psp\ShootState
		pPressMouse1 = False
		pPressReload = False
		If currGun <> Null Then
			pAmmo = currGun\CurrAmmo
			pReloadAmmo = currGun\CurrReloadAmmo
		EndIf
		pIsPlayerSprinting = IsPlayerSprinting
		pIronSight = g_I\IronSight
	EndIf
	Local shootCondition%
	If playerAlive Then
		For g = Each Guns
			HideEntity g\MuzzleFlash
			
			If g_I\GunChangeFLAG = False Then
				For g2.Guns = Each Guns
					If g2\ID%<>pHoldingGun Then
						SetAnimTime g2\obj,0
						HideEntity g2\obj
					Else
						ShowEntity g2\obj
					EndIf
				Next
				DeselectIronSight()
				pDeployState = 0
				pReloadState = 0
				pShootState = 0
				pPressMouse1 = False
				pPressReload = False
				MouseHit1 = False
				MouseDown1 = False
				MouseHit(1)
				g_I\HoldingGun = pHoldingGun
				g_I\GunChangeFLAG = True
				pIronSight = False
				If isMultiplayer Then
					mp_I\LocalAmmo = pAmmo
				EndIf
			EndIf
			
			prevFrame# = AnimTime(g\obj)
			
			If g\ID = pHoldingGun Then
				shootCondition = ((Not MenuOpen) And (Not ConsoleOpen) And ((Not isMultiplayer) Lor ((Not InLobby()) And (Not mp_I\ChatOpen) And (Not mp_I\Gamemode\DisableMovement) And (Not IsInVote()))) And (Not IsPlayerListOpen()) And (Not IsModerationOpen()))
				Select g\GunType
					Case GUNTYPE_AUTO
						;[Block]
						If pAmmo=0 Then
							If MouseHit1 And shootCondition Then
								pPressMouse1=True
								pPressReload=False
							EndIf
						Else
							If MouseDown1 And shootCondition Then
								pPressMouse1=True
								pPressReload=False
							EndIf
						EndIf
						
						If (pAmmo = 0 And pShootState > 0.0) Lor pIsPlayerSprinting Then
							pPressMouse1=False
						EndIf
						If pShootState = 0.0 And pPressMouse1 And pAmmo > 0 And pDeployState >= g\Deploy_Time Then
							SetAnimTime(g\obj,g\Frame_Idle)
						EndIf
						
						If pDeployState < g\Deploy_Time Lor pAmmo = g\MaxCurrAmmo Lor pReloadState = 0.0 Then
							pPressReload=False
						EndIf
						If pReloadAmmo = 0 Then
							pPressReload=False
						EndIf
						
						If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) And ((Not isMultiplayer) Lor (Not mp_I\ChatOpen) And (Not IsInVote())) And (Not IsModerationOpen()) Then
							If pReloadState = 0.0 And pAmmo < g\MaxCurrAmmo And pReloadAmmo > 0 Then
								pPressReload=True
								DeselectIronSight()
							EndIf
						EndIf
						
						shooting = False
						If pDeployState < g\Deploy_Time Then
							ChangeGunFrames(g,g\Anim_Deploy,False)
							If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
								PlayGunSound(g\name+"\deploy",1,1,False)
							EndIf
						Else
							If pReloadState = 0.0 Then
								If pShootState = 0.0 Then
									If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
										ChangeGunFrames(g,g\Anim_Shoot,False)
									Else
										If pIsPlayerSprinting Then
											pPressMouse1 = False
											pPressReload = False
											
											If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
												ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
											Else
												ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
											EndIf
										Else
											If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
												ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
											Else
												SetAnimTime(g\obj,g\Frame_Idle)
											EndIf
										EndIf
									EndIf
									
									If pPressMouse1 And pAmmo = 0 Then
										PlaySound_Strict g_I\ShootEmptySFX
									EndIf
								Else
									ChangeGunFrames(g,g\Anim_Shoot,False)
									If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
										PlayGunSound(g\name,g\MaxShootSounds,0,True)
										CameraShake = g\Knockback/2.0
										user_camera_pitch = user_camera_pitch - g\Knockback
										g_I\GunLightTimer = FPSfactor
										shooting = True
										ShowEntity g\MuzzleFlash
										TurnEntity g\MuzzleFlash,0,0,Rnd(360)
										ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
									EndIf
									If pShootState >= g\Rate_Of_Fire-FPSfactor And pPressMouse1 Then
										SetAnimTime g\obj,g\Anim_Shoot\x
									EndIf
								EndIf
							Else
								If AnimTime(g\obj)>g\Anim_Reload\y Lor AnimTime(g\obj)<g\Anim_Reload\x Then
									SetAnimTime(g\obj,g\Anim_Reload\x)
								EndIf
								ChangeGunFrames(g,g\Anim_Reload,False)
								If prevFrame# < (g\Anim_Reload\x+1) And AnimTime(g\obj) >= (g\Anim_Reload\x+1) Then
									PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
								ElseIf prevFrame# < (g\Anim_Reload\y-0.5) And AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
									pPressReload = False
								EndIf
								pIronSight = False
							EndIf
						EndIf
						
						If (Not pIsPlayerSprinting) And (Not shooting) Then
							g_I\GunAnimFLAG = False
						Else
							g_I\GunAnimFLAG = True
						EndIf
						;[End Block]
					Case GUNTYPE_SEMI
						;[Block]
						If MouseHit1 And shootCondition Then
							pPressMouse1=True
							pPressReload=False
						EndIf
						
						If (pAmmo = 0 And pShootState > 0.0) Lor pIsPlayerSprinting Then
							pPressMouse1=False
						EndIf
						
						If pShootState = 0.0 And pPressMouse1 And pAmmo > 0 And pDeployState >= g\Deploy_Time Then
							SetAnimTime(g\obj,g\Frame_Idle)
						EndIf
						
						If pDeployState < g\Deploy_Time Lor pAmmo = g\MaxCurrAmmo Lor pReloadState = 0.0 Then
							pPressReload=False
						EndIf
						If pReloadAmmo = 0 Then
							pPressReload=False
						EndIf
						
						If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) And ((Not isMultiplayer) Lor (Not mp_I\ChatOpen) And (Not IsInVote())) And (Not IsModerationOpen()) Then
							If pReloadState = 0.0 And pAmmo < g\MaxCurrAmmo And pReloadAmmo > 0 Then
								pPressReload=True
								DeselectIronSight()
							EndIf
						EndIf
						
						shooting = False
						If pDeployState < g\Deploy_Time Then
							If pAmmo = 0 Then
								ChangeGunFrames(g,g\Anim_NoAmmo_Deploy,False)
							Else
								ChangeGunFrames(g,g\Anim_Deploy,False)
							EndIf
							If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
								PlayGunSound(g\name+"\deploy",1,1,False)
							EndIf
						Else
							If pReloadState = 0.0 Then
								If pShootState = 0.0 Then
									If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
										ChangeGunFrames(g,g\Anim_Shoot,False)
									ElseIf AnimTime(g\obj) > g\Anim_NoAmmo_Shoot\x And AnimTime(g\obj) < g\Anim_NoAmmo_Shoot\y-0.5 Then
										ChangeGunFrames(g,g\Anim_NoAmmo_Shoot,False)
									Else
										If pIsPlayerSprinting Then
											pPressMouse1 = False
											pPressReload = False
											
											If pAmmo = 0 Then
												If AnimTime(g\obj)<=(g\Anim_NoAmmo_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_NoAmmo_Sprint_Cycle\y) Then
													ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Transition,False)
												Else
													ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Cycle,True)
												EndIf
											Else
												If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
													ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
												Else
													ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
												EndIf
											EndIf
										Else
											If pAmmo = 0 Then
												If AnimTime(g\obj)>(g\Anim_NoAmmo_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_NoAmmo_Sprint_Cycle\y Then
													ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Transition,False,True)
												Else
													SetAnimTime(g\obj,g\Frame_NoAmmo_Idle)
												EndIf
											Else
												If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
													ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
												Else
													SetAnimTime(g\obj,g\Frame_Idle)
												EndIf
											EndIf
										EndIf
									EndIf
									
									If pPressMouse1 And pAmmo = 0 Then
										PlaySound_Strict g_I\ShootEmptySFX
									EndIf
								Else
									If pAmmo = 0 Then
										ChangeGunFrames(g,g\Anim_NoAmmo_Shoot,False)
										If Ceil(AnimTime(g\obj)) = g\Anim_NoAmmo_Shoot\x Then
											PlayGunSound("slideback2",1,1,False)
											If CameraShake <= (g\Knockback/2)-FPSfactor-0.05 Then
												PlayGunSound(g\name,g\MaxShootSounds,0,True)
												CameraShake = g\Knockback/2.0
												user_camera_pitch = user_camera_pitch - g\Knockback
												g_I\GunLightTimer = FPSfactor
												ShowEntity g\MuzzleFlash
												TurnEntity g\MuzzleFlash,0,0,Rnd(360)
												ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
											EndIf
											shooting = True
										EndIf
									Else
										ChangeGunFrames(g,g\Anim_Shoot,False)
										If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
											PlayGunSound(g\name,g\MaxShootSounds,0,True)
											CameraShake = g\Knockback/2.0
											user_camera_pitch = user_camera_pitch - g\Knockback
											g_I\GunLightTimer = FPSfactor
											shooting = True
											ShowEntity g\MuzzleFlash
											TurnEntity g\MuzzleFlash,0,0,Rnd(360)
											ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
										EndIf
									EndIf
									If pShootState >= g\Rate_Of_Fire-FPSfactor And pPressMouse1 Then
										SetAnimTime g\obj,g\Anim_Shoot\x
									EndIf
								EndIf
							Else
								If AnimTime(g\obj)>g\Anim_Reload\y Lor AnimTime(g\obj)<g\Anim_Reload\x Then
									SetAnimTime(g\obj,g\Anim_Reload\x)
								EndIf
								ChangeGunFrames(g,g\Anim_Reload,False)
								If prevFrame# < (g\Anim_Reload\x+1) And AnimTime(g\obj) >= (g\Anim_Reload\x+1) Then
									PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
								ElseIf prevFrame# < (g\Anim_Reload\y-0.5) And AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
									pPressReload = False
								EndIf
								pIronSight = False
							EndIf
						EndIf
						
						If (Not pIsPlayerSprinting) And (Not shooting) Then
							g_I\GunAnimFLAG = False
						Else
							g_I\GunAnimFLAG = True
						EndIf
						;[End Block]
					Case GUNTYPE_SHOTGUN
						;[Block]
						If MouseHit1 And shootCondition Then
							pPressMouse1=True
							pPressReload=False
						EndIf
						
						If (pAmmo = 0 And pShootState > 0.0) Lor pIsPlayerSprinting Then
							pPressMouse1=False
						EndIf
						
						If pShootState = 0.0 And pPressMouse1 And pAmmo > 0 And pDeployState >= g\Deploy_Time Then
							SetAnimTime(g\obj,g\Frame_Idle)
						EndIf
						
						If pDeployState < g\Deploy_Time Lor pAmmo = g\MaxCurrAmmo Lor pReloadState = 0.0 Then
							pPressReload=False
						EndIf
						If pReloadAmmo = 0 Then
							pPressReload=False
						EndIf
						
						If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) And ((Not isMultiplayer) Lor (Not mp_I\ChatOpen) And (Not IsInVote())) And (Not IsModerationOpen()) Then
							If pReloadState = 0.0 And pAmmo < g\MaxCurrAmmo And pReloadAmmo > 0 Then
								pPressReload=True
								DeselectIronSight()
							EndIf
						EndIf
						
						shooting = False
						If pDeployState < g\Deploy_Time Then
							ChangeGunFrames(g,g\Anim_Deploy,False)
							If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
								PlayGunSound(g\name+"\deploy",1,1,False)
							EndIf
						Else
							If pReloadState = 0.0 Then
								If pShootState = 0.0 Then
									If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
										ChangeGunFrames(g,g\Anim_Shoot,False)
									ElseIf AnimTime(g\obj) >= g\Anim_Reload_Stop\x And AnimTime(g\obj) < (g\Anim_Reload_Stop\y-0.5) Then
										ChangeGunFrames(g,g\Anim_Reload_Stop,False)
										If AnimTime(g\obj) >= (g\Anim_Reload_Stop\y-0.5) Then
											SetAnimTime(g\obj,g\Frame_Idle)
											pPressReload = False
										EndIf
									Else
										If pIsPlayerSprinting Then
											pPressMouse1 = False
											pPressReload = False
											
											If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
												ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
											Else
												ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
											EndIf
										Else
											If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
												ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
											Else
												SetAnimTime(g\obj,g\Frame_Idle)
											EndIf
										EndIf
									EndIf
									
									If pPressMouse1 And pAmmo = 0 Then
										PlaySound_Strict g_I\ShootEmptySFX
									EndIf
								Else
									ChangeGunFrames(g,g\Anim_Shoot,False)
									If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
										PlayGunSound(g\name,g\MaxShootSounds,0,True)
										CameraShake = g\Knockback/2.0
										user_camera_pitch = user_camera_pitch - g\Knockback
										g_I\GunLightTimer = FPSfactor
										shooting = True
										ShowEntity g\MuzzleFlash
										TurnEntity g\MuzzleFlash,0,0,Rnd(360)
										ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
									EndIf
									If pShootState >= g\Rate_Of_Fire-FPSfactor And pPressMouse1 Then
										SetAnimTime g\obj,g\Anim_Shoot\x
									EndIf
								EndIf
							Else
								If AnimTime(g\obj)>g\Anim_Reload_Stop\y Lor AnimTime(g\obj)<g\Anim_Reload_Start\x Then
									SetAnimTime(g\obj,g\Anim_Reload_Start\x)
								EndIf
								If AnimTime(g\obj) >= g\Anim_Reload_Start\x And AnimTime(g\obj) < (g\Anim_Reload_Start\y-0.5) Then
									ChangeGunFrames(g,g\Anim_Reload_Start,False)
									If AnimTime(g\obj) >= (g\Anim_Reload_Start\y-0.5) Then
										SetAnimTime(g\obj,g\Anim_Reload\x)
										PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
									EndIf
								ElseIf AnimTime(g\obj) >= g\Anim_Reload\x And AnimTime(g\obj) < (g\Anim_Reload\y-0.5) Then
									ChangeGunFrames(g,g\Anim_Reload,False)
									If AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
										If pAmmo < g\MaxCurrAmmo And pReloadAmmo > 0 Then
											SetAnimTime(g\obj,g\Anim_Reload\x)
											PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
										Else
											SetAnimTime(g\obj,g\Anim_Reload_Stop\x)
											PlayGunSound(g\name+"\reload_stop",1,1,False)
										EndIf
									EndIf
								ElseIf AnimTime(g\obj) >= g\Anim_Reload_Stop\x And AnimTime(g\obj) < (g\Anim_Reload_Stop\y-0.5) Then
									ChangeGunFrames(g,g\Anim_Reload_Stop,False)
									If AnimTime(g\obj) >= (g\Anim_Reload_Stop\y-0.5) Then
										SetAnimTime(g\obj,g\Frame_Idle)
										pPressReload = False
									EndIf
								EndIf
								pIronSight = False
							EndIf
						EndIf
						
						If (Not pIsPlayerSprinting) And (Not shooting) Then
							g_I\GunAnimFLAG = False
						Else
							g_I\GunAnimFLAG = True
						EndIf
						;[End Block]
					Case GUNTYPE_MELEE
						;[Block]
						If MouseDown1 And shootCondition Then
							pPressMouse1=True
						EndIf
						
						If pShootState > 0.0 Then; Lor pIsPlayerSprinting Then
							pPressMouse1=False
						EndIf
						
						If pShootState = 0.0 And pPressMouse1 And pDeployState >= g\Deploy_Time Then
							SetAnimTime(g\obj,g\Frame_Idle)
						EndIf
						
						shooting = False
						If pDeployState < g\Deploy_Time Then
							ChangeGunFrames(g,g\Anim_Deploy,False)
							If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
								PlayGunSound(g\name+"\deploy",1,1,False)
							EndIf
						Else
							If pShootState = 0.0 Then
								If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
									ChangeGunFrames(g,g\Anim_Shoot,False)
								Else
									SetAnimTime(g\obj,g\Frame_Idle)
								EndIf
								pReloadState = 0.0
							Else
								ChangeGunFrames(g,g\Anim_Shoot,False)
								If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
									PlayGunSound(g\name+"\miss",1,1,True)
								EndIf
								
								If pShootState >= g\ShootDelay And pReloadState = 0.0 Then
									pReloadState = 1.0
								EndIf
								If pShootState >= g\Rate_Of_Fire-FPSfactor And pPressMouse1 Then
									SetAnimTime g\obj,g\Anim_Shoot\x
									pReloadState = 0.0
								EndIf
							EndIf
						EndIf
						
						If (Not shooting) Then
							g_I\GunAnimFLAG = False
						Else
							g_I\GunAnimFLAG = True
						EndIf
						;[End Block]
				End Select
			EndIf
			
			If (Not isMultiplayer) And g\ID = pHoldingGun Then
				If pDeployState < g\Deploy_Time Then
					pDeployState = pDeployState + FPSfactor
				Else
					If g\GunType = GUNTYPE_SHOTGUN Then
						If (pPressMouse1 And pShootState = 0.0) Then
							If pAmmo > 0 Then
								For j=1 To g\Amount_Of_Bullets
									ShootGun(g)
								Next
								pAmmo = pAmmo - 1
								pShootState = FPSfactor
								pReloadState = 0.0
							EndIf
						EndIf
						If pShootState > 0.0 And pShootState < g\Rate_Of_Fire Then
							pShootState = pShootState + FPSfactor
							pReloadState = 0.0
						Else
							pShootState = 0.0
						EndIf
						If pAmmo = g\MaxCurrAmmo Lor pReloadState > 0.0 Then
							pPressReload = False
						EndIf
						If pPressReload Then
							pReloadState = FPSfactor
						EndIf
						
						If pReloadState > 0.0 And pReloadState < g\Reload_Start_Time Then
							pShootState = 0.0
							pReloadState = pReloadState + FPSfactor
							If pReloadState >= g\Reload_Start_Time Then
								pAmmo = pAmmo + 1
								pReloadAmmo = pReloadAmmo - 1
							EndIf
						ElseIf pReloadState >= g\Reload_Start_Time And pReloadState < (g\Reload_Start_Time+g\Reload_Time) Then
							pReloadState = pReloadState + FPSfactor
							If pReloadState >= (g\Reload_Start_Time+g\Reload_Time) Then
								If pAmmo < g\MaxCurrAmmo And pReloadAmmo > 0 Then
									pReloadState = g\Reload_Start_Time
									pAmmo = pAmmo + 1
									pReloadAmmo = pReloadAmmo - 1
								EndIf
							EndIf
						Else
							pReloadState = 0.0
						EndIf
					ElseIf g\GunType = GUNTYPE_MELEE Then
						pPressReload = False
						
						If (pPressMouse1 And pShootState = 0.0) Then
							pShootState = FPSfactor
						EndIf
						If pShootState > 0.0 And pShootState < g\Rate_Of_Fire Then
							pShootState = pShootState + FPSfactor
							If pShootState >= g\ShootDelay And pShootState <= g\ShootDelay+FPSfactor Then
								For i=1 To g\Amount_Of_Bullets
									ShootGun(g)
								Next
							EndIf
						Else
							pShootState = 0.0
						EndIf
					Else
						If pReloadState = 0.0 Then
							If pPressMouse1 Lor pShootState = -1.0 Then
								If pShootState = 0.0 Lor pShootState = -1.0 Then
									If pAmmo > 0 Then
										For j=1 To g\Amount_Of_Bullets
											ShootGun(g)
										Next
										pAmmo = pAmmo - 1
										pShootState = FPSfactor
										pPressReload = False
									EndIf
								EndIf
							EndIf
							If pShootState > 0.0 And pShootState < g\Rate_Of_Fire Then
								pShootState = pShootState + FPSfactor
								pPressReload = False
							Else
								pShootState = 0.0
							EndIf
							If pAmmo = g\MaxCurrAmmo Then
								pPressReload = False
							EndIf
							If pPressReload Then
								pReloadState = FPSfactor
							EndIf
						ElseIf pReloadState > 0.0 And pReloadState < g\Reload_Time Then
							pShootState = 0.0
							pAmmo = 0
							pReloadState = pReloadState + FPSfactor
						Else
							pAmmo = g\MaxCurrAmmo
							pReloadState = 0.0
							pReloadAmmo = pReloadAmmo - 1
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	EndIf
	If isMultiplayer Then
		Players[mp_I\PlayerID]\WeaponInSlot[Players[mp_I\PlayerID]\SelectedSlot] = pHoldingGun
		Players[mp_I\PlayerID]\DeployState = pDeployState
		Players[mp_I\PlayerID]\ReloadState = pReloadState
		Players[mp_I\PlayerID]\ShootState = pShootState
		Players[mp_I\PlayerID]\PressMouse1 = pPressMouse1
		Players[mp_I\PlayerID]\PressReload = pPressReload
		If Players[mp_I\PlayerID]\SelectedSlot < (MaxSlots - SlotsWithNoAmmo) Then
			Players[mp_I\PlayerID]\Ammo[Players[mp_I\PlayerID]\SelectedSlot] = pAmmo
			Players[mp_I\PlayerID]\ReloadAmmo[Players[mp_I\PlayerID]\SelectedSlot] = pReloadAmmo
		EndIf
		Players[mp_I\PlayerID]\IsPlayerSprinting = pIsPlayerSprinting
		Players[mp_I\PlayerID]\IronSight = pIronSight
	Else
		g_I\HoldingGun = pHoldingGun
		psp\DeployState = pDeployState
		psp\ReloadState = pReloadState
		psp\ShootState = pShootState
		If currGun <> Null Then
			currGun\CurrAmmo = pAmmo
			currGun\CurrReloadAmmo = pReloadAmmo
		EndIf
		IsPlayerSprinting = pIsPlayerSprinting
	EndIf
	g_I\IronSight = pIronSight
	
	;Old singleplayer weapon code (BACKUP)
	;[Block]
;	For g = Each Guns
;		Select g\GunType
;			Case GUNTYPE_AUTO
;				;[Block]
;				If g\ID=g_I\HoldingGun Then
;					If psp\Ammo[psp\SelectedSlot]=0 Then
;						If MouseHit1 And (Not MenuOpen) And (Not ConsoleOpen) And SelectedItem = Null Then
;							PressMouse1=True
;							PressReload=False
;						EndIf
;					Else
;						If MouseDown1 And (Not MenuOpen) And (Not ConsoleOpen) And SelectedItem = Null Then
;							PressMouse1=True
;							PressReload=False
;						EndIf
;					EndIf
;					
;					If (psp\Ammo[psp\SelectedSlot]=0 And psp\ShootState > 0.0) Lor IsPlayerSprinting Then ;psp\IsPlayerSprinting
;						PressMouse1=False
;					EndIf
;					
;					If psp\ShootState = 0.0 And PressMouse1 And psp\Ammo[psp\SelectedSlot] > 0 Then
;						SetAnimTime(g\obj,g\Frame_Idle)
;					EndIf
;					
;					If psp\DeployState < g\Deploy_Time Lor psp\Ammo[psp\SelectedSlot] = g\MaxCurrAmmo Lor psp\ReloadState = 0.0 Then
;						PressReload=False
;					EndIf
;					If psp\ReloadAmmo[psp\SelectedSlot]=0 Then
;						PressReload=False
;					EndIf
;					
;					If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) Then
;						If psp\ReloadState = 0.0 And psp\Ammo[psp\SelectedSlot] < g\MaxCurrAmmo And (Not g_I\IronSight) Then
;							PressReload=True
;						EndIf
;					EndIf
;					
;					shooting = False
;					If psp\DeployState < g\Deploy_Time Then
;						ChangeGunFrames(g,g\Anim_Deploy,False)
;						If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
;							PlayGunSound(g\name+"\deploy",1,1,False)
;						EndIf
;					Else
;						If psp\ReloadState = 0.0 Then
;							If psp\ShootState = 0.0 Then
;								If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
;									ChangeGunFrames(g,g\Anim_Shoot,False)
;								Else
;									If IsPlayerSprinting Then
;										PressMouse1 = False
;										PressReload = False
;										
;										If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
;											ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
;										Else
;											ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
;										EndIf
;									Else
;										If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
;											ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
;										Else
;											SetAnimTime(g\obj,g\Frame_Idle)
;										EndIf
;									EndIf
;								EndIf
;								
;								If PressMouse1 And psp\Ammo[psp\SelectedSlot]=0 Then
;									PlaySound_Strict g_I\ShootEmptySFX
;								EndIf
;							Else
;								ChangeGunFrames(g,g\Anim_Shoot,False)
;								If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
;									PlayGunSound(g\name,g\MaxShootSounds,0,True)
;									CameraShake = g\Knockback/2.0
;									user_camera_pitch = user_camera_pitch - g\Knockback
;									g_I\GunLightTimer = FPSfactor
;									shooting = True
;									ShowEntity g\MuzzleFlash
;									TurnEntity g\MuzzleFlash,0,0,Rnd(360)
;									ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
;								EndIf
;								If psp\ShootState >= g\Rate_Of_Fire-FPSfactor And PressMouse1 Then
;									SetAnimTime g\obj,g\Anim_Shoot\x
;								EndIf
;							EndIf
;						Else
;							If AnimTime(g\obj)>g\Anim_Reload\y Lor AnimTime(g\obj)<g\Anim_Reload\x Then
;								SetAnimTime(g\obj,g\Anim_Reload\x)
;							EndIf
;							ChangeGunFrames(g,g\Anim_Reload,False)
;							If prevFrame# < (g\Anim_Reload\x+1) And AnimTime(g\obj) >= (g\Anim_Reload\x+1) Then
;								PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
;							ElseIf prevFrame# < (g\Anim_Reload\y-0.5) And AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
;								PressReload = False
;							EndIf
;							g_I\IronSight = False
;						EndIf
;					EndIf
;					
;					If (Not IsPlayerSprinting) And (Not shooting) Then
;						g_I\GunAnimFLAG = False
;					Else
;						g_I\GunAnimFLAG = True
;					EndIf
;				EndIf
;				;[End Block]
;			Case GUNTYPE_SEMI
;				;[Block]
;				;And (Not InLobby())
;				If g\ID=g_I\HoldingGun Then
;					If MouseHit1 And (Not MenuOpen) And (Not ConsoleOpen) And SelectedItem = Null Then
;						PressMouse1=True
;						PressReload=False
;					EndIf
;					
;					If (psp\Ammo[psp\SelectedSlot]=0 And psp\ShootState > 0.0) Lor IsPlayerSprinting Then
;						PressMouse1=False
;					EndIf
;					
;					If psp\ShootState = 0.0 And PressMouse1 And psp\Ammo[psp\SelectedSlot] > 0 Then
;						SetAnimTime(g\obj,g\Frame_Idle)
;					EndIf
;					
;					If psp\DeployState < g\Deploy_Time Lor psp\Ammo[psp\SelectedSlot] = g\MaxCurrAmmo Lor psp\ReloadState = 0.0 Then
;						PressReload=False
;					EndIf
;					If psp\ReloadAmmo[psp\SelectedSlot]=0 Then
;						PressReload=False
;					EndIf
;					
;					If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) Then
;						If psp\ReloadState = 0.0 And psp\Ammo[psp\SelectedSlot] < g\MaxCurrAmmo And (Not g_I\IronSight) Then
;							PressReload=True
;						EndIf
;					EndIf
;					
;					shooting = False
;					If psp\DeployState < g\Deploy_Time Then
;						If psp\Ammo[psp\SelectedSlot] = 0 Then
;							ChangeGunFrames(g,g\Anim_NoAmmo_Deploy,False)
;						Else
;							ChangeGunFrames(g,g\Anim_Deploy,False)
;						EndIf
;						If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
;							PlayGunSound(g\name+"\deploy",1,1,False)
;						EndIf
;					Else
;						If psp\ReloadState = 0.0 Then
;							If psp\ShootState = 0.0 Then
;								If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
;									ChangeGunFrames(g,g\Anim_Shoot,False)
;								ElseIf AnimTime(g\obj) > g\Anim_NoAmmo_Shoot\x And AnimTime(g\obj) < g\Anim_NoAmmo_Shoot\y-0.5 Then
;									ChangeGunFrames(g,g\Anim_NoAmmo_Shoot,False)
;								Else
;									If IsPlayerSprinting Then
;										PressMouse1 = False
;										PressReload = False
;										
;										If psp\Ammo[psp\SelectedSlot] = 0 Then
;											If AnimTime(g\obj)<=(g\Anim_NoAmmo_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_NoAmmo_Sprint_Cycle\y) Then
;												ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Transition,False)
;											Else
;												ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Cycle,True)
;											EndIf
;										Else
;											If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
;												ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
;											Else
;												ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
;											EndIf
;										EndIf
;									Else
;										If psp\Ammo[psp\SelectedSlot] = 0 Then
;											If AnimTime(g\obj)>(g\Anim_NoAmmo_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_NoAmmo_Sprint_Cycle\y Then
;												ChangeGunFrames(g,g\Anim_NoAmmo_Sprint_Transition,False,True)
;											Else
;												SetAnimTime(g\obj,g\Frame_NoAmmo_Idle)
;											EndIf
;										Else
;											If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
;												ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
;											Else
;												SetAnimTime(g\obj,g\Frame_Idle)
;											EndIf
;										EndIf
;									EndIf
;								EndIf
;								
;								If PressMouse1 And psp\Ammo[psp\SelectedSlot]=0 Then
;									PlaySound_Strict g_I\ShootEmptySFX
;								EndIf
;							Else
;								If psp\Ammo[psp\SelectedSlot] = 0 Then
;									ChangeGunFrames(g,g\Anim_NoAmmo_Shoot,False)
;									If Ceil(AnimTime(g\obj)) = g\Anim_NoAmmo_Shoot\x Then
;										PlayGunSound("slideback2",1,1,False)
;										If CameraShake <= (g\Knockback/2)-FPSfactor-0.05 Then
;											PlayGunSound(g\name,g\MaxShootSounds,0,True)
;											CameraShake = g\Knockback/2.0
;											user_camera_pitch = user_camera_pitch - g\Knockback
;											g_I\GunLightTimer = FPSfactor
;											ShowEntity g\MuzzleFlash
;											TurnEntity g\MuzzleFlash,0,0,Rnd(360)
;											ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
;										EndIf
;										shooting = True
;									EndIf
;								Else
;									ChangeGunFrames(g,g\Anim_Shoot,False)
;									If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
;										PlayGunSound(g\name,g\MaxShootSounds,0,True)
;										CameraShake = g\Knockback/2.0
;										user_camera_pitch = user_camera_pitch - g\Knockback
;										g_I\GunLightTimer = FPSfactor
;										shooting = True
;										ShowEntity g\MuzzleFlash
;										TurnEntity g\MuzzleFlash,0,0,Rnd(360)
;										ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
;									EndIf
;								EndIf
;								If psp\ShootState >= g\Rate_Of_Fire-FPSfactor And PressMouse1 Then
;									SetAnimTime g\obj,g\Anim_Shoot\x
;								EndIf
;							EndIf
;						Else
;							If AnimTime(g\obj)>g\Anim_Reload\y Lor AnimTime(g\obj)<g\Anim_Reload\x Then
;								SetAnimTime(g\obj,g\Anim_Reload\x)
;							EndIf
;							ChangeGunFrames(g,g\Anim_Reload,False)
;							If prevFrame# < (g\Anim_Reload\x+1) And AnimTime(g\obj) >= (g\Anim_Reload\x+1) Then
;								PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
;							ElseIf prevFrame# < (g\Anim_Reload\y-0.5) And AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
;								PressReload = False
;							EndIf
;							g_I\IronSight = False
;						EndIf
;					EndIf
;					
;					If (Not IsPlayerSprinting) And (Not shooting) Then
;						g_I\GunAnimFLAG = False
;					Else
;						g_I\GunAnimFLAG = True
;					EndIf
;				EndIf
;				;[End Block]
;			Case GUNTYPE_SHOTGUN
;				;[Block]
;				If g\ID=g_I\HoldingGun Then
;					If MouseHit1 And (Not MenuOpen) And (Not ConsoleOpen) And SelectedItem = Null Then
;						PressMouse1=True
;						PressReload=False
;					EndIf
;					
;					If (psp\Ammo[psp\SelectedSlot]=0 And psp\ShootState > 0.0) Lor IsPlayerSprinting Then
;						PressMouse1=False
;					EndIf
;					
;					If psp\ShootState = 0.0 And PressMouse1 And psp\Ammo[psp\SelectedSlot] > 0 Then
;						SetAnimTime(g\obj,g\Frame_Idle)
;					EndIf
;					
;					If psp\DeployState < g\Deploy_Time Lor psp\Ammo[psp\SelectedSlot] = g\MaxCurrAmmo Lor psp\ReloadState = 0.0 Then
;						PressReload=False
;					EndIf
;					If psp\ReloadAmmo[psp\SelectedSlot]=0 Then
;						PressReload=False
;					EndIf
;					
;					If KeyHit(KEY_RELOAD) And (Not MenuOpen) And (Not ConsoleOpen) Then
;						If psp\ReloadState = 0.0 And psp\Ammo[psp\SelectedSlot] < g\MaxCurrAmmo And (Not g_I\IronSight) Then
;							PressReload=True
;						EndIf
;					EndIf
;					
;					shooting = False
;					If psp\DeployState < g\Deploy_Time Then
;						ChangeGunFrames(g,g\Anim_Deploy,False)
;						If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
;							PlayGunSound(g\name+"\deploy",1,1,False)
;						EndIf
;					Else
;						If psp\ReloadState = 0.0 Then
;							If psp\ShootState = 0.0 Then
;								If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
;									ChangeGunFrames(g,g\Anim_Shoot,False)
;								ElseIf AnimTime(g\obj) >= g\Anim_Reload_Stop\x And AnimTime(g\obj) < (g\Anim_Reload_Stop\y-0.5) Then
;									ChangeGunFrames(g,g\Anim_Reload_Stop,False)
;									If AnimTime(g\obj) >= (g\Anim_Reload_Stop\y-0.5) Then
;										SetAnimTime(g\obj,g\Frame_Idle)
;										PressReload = False
;									EndIf
;								Else
;									If IsPlayerSprinting Then
;										PressMouse1 = False
;										PressReload = False
;										
;										If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
;											ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
;										Else
;											ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
;										EndIf
;									Else
;										If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
;											ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
;										Else
;											SetAnimTime(g\obj,g\Frame_Idle)
;										EndIf
;									EndIf
;								EndIf
;								
;								If PressMouse1 And psp\Ammo[psp\SelectedSlot]=0 Then
;									PlaySound_Strict g_I\ShootEmptySFX
;								EndIf
;							Else
;								ChangeGunFrames(g,g\Anim_Shoot,False)
;								If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
;									PlayGunSound(g\name,g\MaxShootSounds,0,True)
;									CameraShake = g\Knockback/2.0
;									user_camera_pitch = user_camera_pitch - g\Knockback
;									g_I\GunLightTimer = FPSfactor
;									shooting = True
;									ShowEntity g\MuzzleFlash
;									TurnEntity g\MuzzleFlash,0,0,Rnd(360)
;									ScaleSprite g\MuzzleFlash,Rnd(0.025,0.03),Rnd(0.025,0.03)
;								EndIf
;								If psp\ShootState >= g\Rate_Of_Fire-FPSfactor And PressMouse1 Then
;									SetAnimTime g\obj,g\Anim_Shoot\x
;								EndIf
;							EndIf
;						Else
;							If AnimTime(g\obj)>g\Anim_Reload_Stop\y Lor AnimTime(g\obj)<g\Anim_Reload_Start\x Then
;								SetAnimTime(g\obj,g\Anim_Reload_Start\x)
;							EndIf
;							If AnimTime(g\obj) >= g\Anim_Reload_Start\x And AnimTime(g\obj) < (g\Anim_Reload_Start\y-0.5) Then
;								ChangeGunFrames(g,g\Anim_Reload_Start,False)
;								If AnimTime(g\obj) >= (g\Anim_Reload_Start\y-0.5) Then
;									SetAnimTime(g\obj,g\Anim_Reload\x)
;									PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
;								EndIf
;							ElseIf AnimTime(g\obj) >= g\Anim_Reload\x And AnimTime(g\obj) < (g\Anim_Reload\y-0.5) Then
;								ChangeGunFrames(g,g\Anim_Reload,False)
;								If AnimTime(g\obj) >= (g\Anim_Reload\y-0.5) Then
;									If psp\Ammo[psp\SelectedSlot]<g\MaxCurrAmmo And psp\ReloadAmmo[psp\SelectedSlot]>0 Then
;										SetAnimTime(g\obj,g\Anim_Reload\x)
;										PlayGunSound(g\name+"\reload",g\MaxReloadSounds,1,False)
;									Else
;										SetAnimTime(g\obj,g\Anim_Reload_Stop\x)
;										PlayGunSound(g\name+"\reload_stop",1,1,False)
;									EndIf
;								EndIf
;							ElseIf AnimTime(g\obj) >= g\Anim_Reload_Stop\x And AnimTime(g\obj) < (g\Anim_Reload_Stop\y-0.5) Then
;								ChangeGunFrames(g,g\Anim_Reload_Stop,False)
;								If AnimTime(g\obj) >= (g\Anim_Reload_Stop\y-0.5) Then
;									SetAnimTime(g\obj,g\Frame_Idle)
;									PressReload = False
;								EndIf
;							EndIf
;							g_I\IronSight = False
;						EndIf
;					EndIf
;					
;					If (Not IsPlayerSprinting) And (Not shooting) Then
;						g_I\GunAnimFLAG = False
;					Else
;						g_I\GunAnimFLAG = True
;					EndIf
;				EndIf
;				;[End Block]
;			Case GUNTYPE_MELEE
;				;[Block]
;				If g\ID=g_I\HoldingGun Then
;					If MouseDown1 And (Not MenuOpen) And (Not ConsoleOpen) And SelectedItem = Null Then
;						PressMouse1=True
;					EndIf
;					
;					If psp\ShootState > 0.0 Lor psp\IsPlayerSprinting Then
;						PressMouse1=False
;					EndIf
;					
;					If psp\ShootState = 0.0 And PressMouse1 Then
;						SetAnimTime(g\obj,g\Frame_Idle)
;					EndIf
;					
;					shooting = False
;					If psp\DeployState < g\Deploy_Time Then
;						ChangeGunFrames(g,g\Anim_Deploy,False)
;						If prevFrame# < (g\Anim_Deploy\x+1) And AnimTime(g\obj) >= (g\Anim_Deploy\x+1) Then
;							PlayGunSound(g\name+"\deploy",1,1,False)
;						EndIf
;					Else
;						If psp\ShootState = 0.0 Then
;							If AnimTime(g\obj) > g\Anim_Shoot\x And AnimTime(g\obj) < g\Anim_Shoot\y-0.5 Then
;								ChangeGunFrames(g,g\Anim_Shoot,False)
;							Else
;								If psp\IsPlayerSprinting Then
;									psp\PressMouse1 = False
;									psp\PressReload = False
;									
;									If AnimTime(g\obj)<=(g\Anim_Sprint_Transition\y-0.5) Lor AnimTime(g\obj)>(g\Anim_Sprint_Cycle\y) Then
;										ChangeGunFrames(g,g\Anim_Sprint_Transition,False)
;									Else
;										ChangeGunFrames(g,g\Anim_Sprint_Cycle,True)
;									EndIf
;								Else
;									If AnimTime(g\obj)>(g\Anim_Sprint_Transition\x+0.5) And AnimTime(g\obj)<=g\Anim_Sprint_Cycle\y Then
;										ChangeGunFrames(g,g\Anim_Sprint_Transition,False,True)
;									Else
;										SetAnimTime(g\obj,g\Frame_Idle)
;									EndIf
;								EndIf
;							EndIf
;							psp\ReloadState = 0.0
;						Else
;							ChangeGunFrames(g,g\Anim_Shoot,False)
;							If Ceil(AnimTime(g\obj)) = g\Anim_Shoot\x Then
;								PlayGunSound(g\name+"\miss",1,1,True)
;							EndIf
;							
;							If psp\ShootState >= g\ShootDelay And psp\ReloadState = 0.0 Then
;								psp\ReloadState = 1.0
;							EndIf
;							If psp\ShootState >= g\Rate_Of_Fire-FPSfactor And PressMouse1 Then
;								SetAnimTime g\obj,g\Anim_Shoot\x
;								psp\ReloadState = 0.0
;							EndIf
;						EndIf
;					EndIf
;					
;					If (Not psp\IsPlayerSprinting) And (Not shooting) Then
;						g_I\GunAnimFLAG = False
;					Else
;						g_I\GunAnimFLAG = True
;					EndIf
;				EndIf
;				;[End Block]
;		End Select
;	Next
	;[End Block]
	
End Function

Function ToggleGuns()
	Local g.Guns
	Local i%, j%
	Local case1%, case2%
	Local GunInInventory%
	Local KeyPressed%[MaxGunSlots]
	Local KeyPressedHolster% = KeyHit(KEY_HOLSTERGUN)
	
	For i = 0 To MaxGunSlots-1
		If co\Enabled Then
			KeyPressed[i] = GetDPadButtonPress()
		Else
			KeyPressed[i] = KeyHit(i + 2)
		EndIf
	Next
	
	If KillTimer >= 0 And CanPlayerUseGuns And FPSfactor > 0.0 And ChatSFXOpened = False And (Not g_I\IronSight) Then
		For i = 0 To MaxGunSlots-1
			If co\Enabled Then
				case1 = -1
				case2 = KeyPressed[i]
			Else
				case1 = KeyPressed[i]
				case2 = False
			EndIf
			
			If (case1 = -1 And case2 = Int(WrapAngle(180 + (90 * i)))) Lor (case1 And (Not case2)) Then
				If g_I\Weapon_InSlot[i] <> "" Then
					g_I\GunChangeFLAG = False
					For g = Each Guns
						If g\name = g_I\Weapon_InSlot[i] Then
							g_I\HoldingGun = g\ID
							Exit
						EndIf
					Next
					g_I\Weapon_CurrSlot = (i + 1)
					mpl\SlotsDisplayTimer = 70*3
				EndIf
			EndIf
		Next
		
		If (Not co\Enabled) Then
			case1 = KeyPressedHolster
		EndIf
		If (case1 = -1 And case2 = 90) Lor (case1 And (Not case2)) Lor FallTimer < 0 Then
			For g.Guns = Each Guns
				If g\ID = g_I\HoldingGun Then
					PlayGunSound(g\name$+"\holster",1,1,False)
				EndIf
			Next
			g_I\GunChangeFLAG = False
			g_I\HoldingGun = 0
			g_I\Weapon_CurrSlot = 0
			mpl\SlotsDisplayTimer = 0
		EndIf
		
		For i = 0 To MaxGunSlots-1
			GunInInventory = False
			For j = 0 To MaxItemAmount-1
				If Inventory[j] <> Null And Inventory[j]\itemtemplate\tempname = g_I\Weapon_InSlot[i] Then
					GunInInventory = True
					Exit
				EndIf
			Next
			If (Not GunInInventory) Then
				For g = Each Guns
					If g\name = g_I\Weapon_InSlot[i] And g\ID = g_I\HoldingGun Then
						g_I\GunChangeFLAG = False
						g_I\HoldingGun = 0
						g_I\Weapon_CurrSlot = 0
						mpl\SlotsDisplayTimer = 0
						PlayGunSound(g\name$+"\holster",1,1,False)
						Exit
					EndIf
				Next
				g_I\Weapon_InSlot[i] = ""
			EndIf
		Next
	EndIf
	
End Function

Function AnimateGuns()
	
	If (Not g_I\GunAnimFLAG) And (CurrSpeed=0.0 Lor mi_I\EndingTimer>0.0) And (Not IsPlayerSprinting%) And (Not g_I\IronSight)
		If GunPivot_YSide%=0
			If GunPivot_Y# > -0.005
				GunPivot_Y# = GunPivot_Y# - (0.00005*FPSfactor)
			Else
				GunPivot_Y# = -0.005
				GunPivot_YSide% = 1
			EndIf
		Else
			If GunPivot_Y# < 0.0
				GunPivot_Y# = GunPivot_Y# + (0.00005*FPSfactor)
			Else
				GunPivot_Y# = 0.0
				GunPivot_YSide% = 0
			EndIf
		EndIf
		
		If GunPivot_X# < 0.0
			GunPivot_X# = Min(GunPivot_X#+(0.0001*FPSfactor),0.0)
		ElseIf GunPivot_X# > 0.0
			GunPivot_X# = Max(GunPivot_X#-(0.0001*FPSfactor),0.0)
		EndIf
	ElseIf (Not g_I\GunAnimFLAG) And CurrSpeed<>0.0 And (Not IsPlayerSprinting%) And (Not g_I\IronSight) And (Not mi_I\EndingTimer>0.0)
		If GunPivot_XSide%=0
			If GunPivot_X# > -0.0025
				GunPivot_X# = GunPivot_X# - (0.000075/(1.0 + Crouch)*FPSfactor)
				If GunPivot_X# > -0.00125
					GunPivot_Y# = Min(GunPivot_Y#+(0.000125/(1.0 + Crouch)*FPSfactor),0.001)
				Else
					GunPivot_Y# = Max(GunPivot_Y#-(0.000125/(1.0 + Crouch)*FPSfactor),0.0)
				EndIf
			Else
				GunPivot_X# = -0.0025
				GunPivot_Y# = 0.0
				GunPivot_XSide% = 1
			EndIf
		Else
			If GunPivot_X# < 0.0
				GunPivot_X# = GunPivot_X# + (0.000075/(1.0 + Crouch)*FPSfactor)
				If GunPivot_X# < -0.00125
					GunPivot_Y# = Min(GunPivot_Y#+(0.000125/(1.0 + Crouch)*FPSfactor),0.001)
				Else
					GunPivot_Y# = Max(GunPivot_Y#-(0.000125/(1.0 + Crouch)*FPSfactor),0.0)
				EndIf
			Else
				GunPivot_X# = 0.0
				GunPivot_Y# = 0.0
				GunPivot_XSide% = 0
			EndIf
		EndIf
	Else
		If GunPivot_Y# < 0.0
			GunPivot_Y# = Max(GunPivot_Y#+(0.0001*FPSfactor),0.0)
		Else
			GunPivot_Y# = 0.0
		EndIf
		
		If GunPivot_X# < 0.0
			GunPivot_X# = Min(GunPivot_X#+(0.0001*FPSfactor),0.0)
		ElseIf GunPivot_X# > 0.0
			GunPivot_X# = Max(GunPivot_X#-(0.0001*FPSfactor),0.0)
		EndIf
	EndIf
	
	PositionEntity g_I\GunPivot,EntityX(Camera), EntityY(Camera)+GunPivot_Y#, EntityZ(Camera)
	MoveEntity g_I\GunPivot,GunPivot_X#,0,0
	
End Function

Function ChangeGunFrames(g.Guns,anim.Vector3D,loop%=True,reverse%=False)
	Local newTime#,temp%
	
	;x = Start frame
	;y = End frame
	;z = Speed
	
	Local speed# = anim\z
	If reverse Then speed=-anim\z
	
	If speed > 0.0 Then
		newTime = Max(Min(AnimTime(g\obj) + speed * FPSfactor,anim\y),anim\x)
		
		If loop And newTime => anim\y Then
			newTime = anim\x
		EndIf
	Else
		Local anim_start% = anim\x
		Local anim_end% = anim\y
		If anim\x < anim\y Then
			anim_start = anim\y
			anim_end = anim\x
		EndIf
		
		If loop Then
			newTime = AnimTime(g\obj) + speed * FPSfactor
			
			If newTime < anim_end Then 
				newTime = anim_start
			ElseIf newTime > anim_start Then
				newTime = anim_end
			EndIf
		Else
			newTime = Max(Min(AnimTime(g\obj) + speed * FPSfactor,anim_start),anim_end)
		EndIf
	EndIf
	SetAnimTime g\obj,newTime
	
End Function

Function ShootPlayer(x#,y#,z#,hitProb#=1.0,particles%=True,damage%=10)
	Local pvt,i
	
	;muzzle flash
	Local p.Particles = CreateParticle(x,y,z, 1, Rnd(0.08,0.1), 0.0, 5)
	TurnEntity p\obj, 0,0,Rnd(360)
	p\Achange = -0.15
	
	LightVolume = TempLightVolume*1.2
	
	If (Not GodMode) Then 
		
		If Rnd(1.0)=<hitProb Then
			TurnEntity Camera, Rnd(-3,3), Rnd(-3,3), 0
			
			DamageSPPlayer(damage)
;			If psp\Kevlar > 0 Then
;				PlaySound_Strict NTF_PainSFX[Rand(0,7)]
;			Else
;				PlaySound_Strict NTF_PainWeakSFX[Rand(0,1)]
;			EndIf
			mpl\DamageTimer = 70
			
			PlaySound_Strict BullethitSFX
		ElseIf particles
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider),(EntityY(Collider)+EntityY(Camera))/2,EntityZ(Collider)
			PointEntity pvt, p\obj
			TurnEntity pvt, 0, 180, 0
			
			EntityPick(pvt, 2.5)
			
			If PickedEntity() <> 0 Then 
				PlaySound2(Gunshot3SFX, Camera, pvt, 0.4, Rnd(0.8,1.0))
				
				If particles Then 
					;dust/smoke particles
					p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
					p\speed = 0.001
					p\SizeChange = 0.003
					p\A = 0.8
					p\Achange = -0.01
					RotateEntity p\pvt, EntityPitch(pvt)-180, EntityYaw(pvt),0
					
					For i = 0 To Rand(2,3)
						p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
						p\speed = 0.02
						p\A = 0.8
						p\Achange = -0.01
						RotateEntity p\pvt, EntityPitch(pvt)+Rnd(170,190), EntityYaw(pvt)+Rnd(-10,10),0	
					Next
					
					;bullet hole decal
					Local de.Decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BULLETHOLE), PickedX(),PickedY(),PickedZ(), 0,0,0)
					AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
					MoveEntity de\obj, 0,0,-0.001
					EntityFX de\obj, 1+8
					de\fx = 1+8
					de\lifetime = 70*20
					EntityBlend de\obj, 2
					de\blendmode = 2
					de\Size = Rnd(0.028,0.034)
					ScaleSprite de\obj, de\Size, de\Size
				EndIf				
			EndIf
			
			pvt = FreeEntity_Strict(pvt)
		EndIf
		
	EndIf
	
End Function

Function ShootTarget(x#,y#,z#,n.NPCs,hitProb#=1.0,particles%=True,damage%=10)
	Local pvt%,i%
	
	;muzzle flash
	Local p.Particles = CreateParticle(x,y,z, 1, Rnd(0.08,0.1), 0.0, 5)
	TurnEntity p\obj, 0,0,Rnd(360)
	p\Achange = -0.15
	
	If Rnd(1.0)=<hitProb Then
		p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 5, 0.06, 0.2, 80)
		p\speed = 0.001
		p\SizeChange = 0.003
		p\A = 0.8
		p\Achange = -0.02
		
		If n\Target <> Null
			n\Target\HP% = n\Target\HP% - damage%
			;n\Target\GotShot% = True
		EndIf
		
	ElseIf particles Then
		pvt = CreatePivot()
		PositionEntity pvt, EntityX(Collider),(EntityY(Collider)+EntityY(Camera))/2,EntityZ(Collider)
		PointEntity pvt, p\obj
		TurnEntity pvt, 0, 180, 0
		
		EntityPick(pvt, 2.5)
		
		If PickedEntity() <> 0 Then 
			PlaySound2(Gunshot3SFX, Camera, pvt, 0.4, Rnd(0.8,1.0))
			
			If particles Then 
				;dust/smoke particles
				p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
				p\speed = 0.001
				p\SizeChange = 0.003
				p\A = 0.8
				p\Achange = -0.01
				RotateEntity p\pvt, EntityPitch(pvt)-180, EntityYaw(pvt),0
				
				For i = 0 To Rand(2,3)
					p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
					p\speed = 0.02
					p\A = 0.8
					p\Achange = -0.01
					RotateEntity p\pvt, EntityPitch(pvt)+Rnd(170,190), EntityYaw(pvt)+Rnd(-10,10),0	
				Next
				
				;bullet hole decal
				Local de.Decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BULLETHOLE), PickedX(),PickedY(),PickedZ(), 0,0,0)
				AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
				MoveEntity de\obj, 0,0,-0.001
				EntityFX de\obj, 1+8
				de\fx = 1+8
				de\lifetime = 70*20
				EntityBlend de\obj, 2
				de\blendmode = 2
				de\Size = Rnd(0.028,0.034)
				ScaleSprite de\obj, de\Size, de\Size
			EndIf				
		EndIf
		
		pvt = FreeEntity_Strict(pvt)
	EndIf
	
End Function

Function ShootGun(g.Guns)
	Local temp,n.NPCs,p.Particles,j,de.Decals,ent_pick%,i%
	Local hitNPC.NPCs ;unused right now, but could be very useful later, there should be no performance difference and better code
	
	IsPlayerShooting% = True
	
	If g\GunType <> GUNTYPE_MELEE Then
		LightVolume = TempLightVolume*1.2
		ShowEntity g_I\GunLight
		g_I\GunLightTimer = FPSfactor
	EndIf
	
	If (Not g_I\IronSight)
		RotateEntity GunPickPivot,Rnd(-g\Accuracy,g\Accuracy)/(1.0+(3.0*g_I\IronSight)),Rnd(-g\Accuracy,g\Accuracy)/(1.0+(3.0*g_I\IronSight)),0
	Else
		RotateEntity GunPickPivot,0,0,0
	EndIf
	
	HideEntity Head
	If g\Range<=0.0 Then
		EntityPick GunPickPivot,10000.0
	Else
		EntityPick GunPickPivot,g\Range
	EndIf
	
	ent_pick% = PickedEntity()
	If ent_pick%<>0 Then
		For n.NPCs = Each NPCs
			For j = 0 To 24
				If ent_pick% = n\HitBox1[j] Then ;Head has been shot, instant death
					n\HP = 0
					hitNPC = n
					Exit
				EndIf
				If ent_pick% = n\HitBox2[j] Then ;Body has been shot, doing damage with g\DamageOnEntity
					n\HP = n\HP - g\DamageOnEntity
					hitNPC = n
					Exit
				EndIf
				If ent_pick% = n\HitBox3[j] Then ;Arms or legs have been shot, doing damage with g\DamageOnEntity/2
					n\HP = n\HP - (g\DamageOnEntity/2)
					hitNPC = n
					Exit
				EndIf
			Next
			If hitNPC <> Null Then Exit
		Next
	EndIf
	If hitNPC <> Null Then
		p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 5, 0.06, 0.2, 80)
		p\speed = 0.001
		p\SizeChange = 0.003
		p\A = 0.8
		p\Achange = -0.02
		
		If g\GunType = GUNTYPE_MELEE Then
			PlayGunSound(g\name+"\hitbody",g\MaxShootSounds,0,True,True)
		EndIf
		
		;DebugLog "shot"
	ElseIf ent_pick <> 0 Then
		p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
		p\speed = 0.001
		p\SizeChange = 0.003
		p\A = 0.8
		p\Achange = -0.01
		RotateEntity p\pvt, EntityPitch(g_I\GunPivot)-180, EntityYaw(g_I\GunPivot),0
		
		If g\GunType <> GUNTYPE_MELEE Then
			PlaySound2(Gunshot3SFX, Camera, p\pvt, 0.4, Rnd(0.8,1.0))
		Else
			PlayGunSound(g\name+"\hitwall",g\MaxWallhitSounds,0,True,True)
		EndIf
		
		For i = 0 To Rand(2,3)
			p.Particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
			p\speed = 0.02
			p\A = 0.8
			p\Achange = -0.01
			RotateEntity p\pvt, EntityPitch(g_I\GunPivot)+Rnd(170,190), EntityYaw(g_I\GunPivot)+Rnd(-10,10),0	
		Next
		
		Local DecalID% = 0
		Select g\DecalType
			Case GUNDECAL_DEFAULT
				DecalID = DECAL_TYPE_BULLETHOLE
			Case GUNDECAL_SLASH
				DecalID = DECAL_TYPE_SLASHHOLE
		End Select
		
		de.Decals = CreateDecal(GetRandomDecalID(DecalID), PickedX(),PickedY(),PickedZ(), 0,0,0)
		AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
		MoveEntity de\obj, 0,0,-0.001
		EntityFX de\obj, 1+8
		de\fx = 1+8
		de\lifetime = 70*20
		EntityBlend de\obj, 2
		de\blendmode = 2
		de\Size = Rnd(0.028,0.034)
		ScaleSprite de\obj, de\Size, de\Size
		EntityParent de\obj,ent_pick
	EndIf
	
End Function

;Unused
;[Block]
;Function UpdateScope()
;	
;	If FPSfactor > 0.0
;		If MouseHit3
;			ScopeNVG = Not ScopeNVG
;			If ScopeNVG
;				PlaySound_Strict NVGOnSFX
;			Else
;				PlaySound_Strict NVGOffSFX
;			EndIf
;		EndIf
;	EndIf
;	
;End Function
;
;Function RenderScope()
;	Local n.NPCs
;	
;	If NTF_GameModeFlag<>3
;		If PlayerRoom\RoomTemplate\Name$ = "gate_a_topside" Then
;			CameraFogRange ScopeCam, 5,30
;			CameraFogColor (ScopeCam,200,200,200)
;			CameraClsColor (ScopeCam,200,200,200)
;			CameraRange(ScopeCam, 0.005, 30)
;		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_a_intro") Then
;			CameraFogRange ScopeCam, 5,30
;			CameraFogColor (ScopeCam,200,200,200)
;			CameraClsColor (ScopeCam,200,200,200)					
;			CameraRange(ScopeCam, 0.005, 100)
;		ElseIf (PlayerRoom\RoomTemplate\Name = "gate_b_topside") Then
;			CameraFogRange ScopeCam, 5,45
;			CameraFogColor (ScopeCam,200,200,200)
;			CameraClsColor (ScopeCam,200,200,200)					
;			CameraRange(ScopeCam, 0.005, 60)
;		ElseIf (PlayerRoom\RoomTemplate\Name = "room2_maintenance") And (EntityY(Collider)<-3500.0*RoomScale) Then
;			CameraFogRange ScopeCam,1,6
;			CameraFogColor ScopeCam,5,20,3
;			CameraClsColor ScopeCam,5,20,3
;			CameraRange ScopeCam,0.01,7
;		Else
;			If (Not ScopeNVG)
;				CameraFogRange(ScopeCam, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
;				CameraRange(ScopeCam, 0.005, Min(CameraFogFar*LightVolume*1.5,32))
;			Else
;				CameraFogRange(ScopeCam, CameraFogNear*LightVolume,30*LightVolume)
;				CameraRange(ScopeCam, 0.005, Min(30*LightVolume*1.5,32))
;			EndIf
;			CameraFogColor(ScopeCam, 0,0,0)
;			CameraClsColor ScopeCam,0,0,0
;			CameraFogMode ScopeCam,1
;		EndIf
;	Else
;		If (Not ScopeNVG)
;			;CameraFogRange(ScopeCam, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
;			;CameraRange(ScopeCam, 0.005, Min(CameraFogFar*LightVolume*1.5,32))
;		Else
;			;CameraFogRange(ScopeCam, CameraFogNear*LightVolume,30*LightVolume)
;			;CameraRange(ScopeCam, 0.005, Min(30*LightVolume*1.5,32))
;		EndIf
;		CameraFogColor(ScopeCam, 0,0,0)
;		CameraFogMode ScopeCam,1
;	EndIf
;	
;	HideEntity Camera
;	ShowEntity ScopeCam
;	Cls
;	SetBuffer BackBuffer()
;	If ScopeNVG
;		For n = Each NPCs
;			If n\NPCtype = NPCtype966
;				ShowEntity n\obj
;			EndIf
;		Next
;	EndIf
;	RenderWorld
;	CopyRect 0,0,128,128,0,0,BackBuffer(),TextureBuffer(ScopeTexture)
;	ShowEntity Camera
;	HideEntity ScopeCam
;	
;End Function
;[End Block]

Function PlayGunSound(name$,max_amount%=1,sfx%=0,pitchshift%=False,custom%=False)
	Local g.Guns, gun.Guns
	
	For g = Each Guns
		If name = g\name Then
			gun = g
			Exit
		EndIf
	Next
	
	If sfx%=0 Then
		If (Not custom) Then
			If max_amount% = 1 Then
				GunSFX = gun\ShootSounds[0]
			Else
				GunSFX = gun\ShootSounds[Rand(0,max_amount%-1)]
			EndIf
		Else
			If max_amount% = 1 Then
				GunSFX = LoadSound_Strict("SFX\Guns\"+name$+".ogg")
			Else
				GunSFX = LoadSound_Strict("SFX\Guns\"+name$+Rand(1,max_amount%)+".ogg")
			EndIf
		EndIf
		GunCHN = PlaySound_Strict(GunSFX)
		If GunPitchShift% = 1 Then
			If pitchshift% Then
				ChannelPitch GunCHN,Rand(38000,43000)
			EndIf
		EndIf
	Else
		If GunSFX2 <> 0 Then FreeSound_Strict GunSFX2:GunSFX2=0
		If ChannelPlaying(GunCHN2) Then StopChannel(GunCHN2) : GunCHN2 = 0
		If max_amount% = 1 Then
			GunSFX2 = LoadSound_Strict("SFX\Guns\"+name$+".ogg")
		Else
			GunSFX2 = LoadSound_Strict("SFX\Guns\"+name$+Rand(1,max_amount%)+".ogg")
		EndIf
		GunCHN2 = PlaySound_Strict(GunSFX2)
	EndIf
	
End Function

Function UpdateIronSight()
	Local pvt%,g.Guns,hasIronSight%,prevIronSight%
	Local currGun.Guns
	
	If IsPlayerSprinting% Then
		DeselectIronSight()
	EndIf
	
	For g.Guns = Each Guns
		If g\ID = g_I\HoldingGun Then
			If g\GunType<>GUNTYPE_MELEE Then
				hasIronSight% = True
				If g_I\IronSight% Lor g_I\IronSightAnim% Then
					EntityParent g\obj,IronSightPivot2%
				EndIf
				currGun = g
				Exit
			Else
				hasIronSight = False
				Exit
			EndIf
		EndIf
	Next
	
	If (Not hasIronSight) Then
		Return
	EndIf
	
	If g_I\IronSight% Then
		If g_I\IronSightAnim = 2 Then
			If currGun<>Null Then
				PositionEntity IronSightPivot%,currGun\IronSightCoords\x,currGun\IronSightCoords\y,currGun\IronSightCoords\z
			Else
				PositionEntity IronSightPivot%,0,0,0
			EndIf
			g_I\IronSightAnim = 1
		EndIf
	Else
		If g_I\IronSightAnim = 2 Then
			PositionEntity IronSightPivot%,0,0,0
			g_I\IronSightAnim = 1
		EndIf
	EndIf
	
	PositionEntity IronSightPivot2%,CurveValue(EntityX(IronSightPivot),EntityX(IronSightPivot2),5.0),CurveValue(EntityY(IronSightPivot),EntityY(IronSightPivot2),5.0),CurveValue(EntityZ(IronSightPivot),EntityZ(IronSightPivot2),5.0)
	If EntityX(IronSightPivot2%) <= 0.001 And EntityX(IronSightPivot2%) >= -0.001 Then
		g_I\IronSightAnim = 0
	EndIf
	If currGun<>Null Then
		If EntityX(IronSightPivot2%) <= currGun\IronSightCoords\x+0.001 And EntityX(IronSightPivot2%) >= currGun\IronSightCoords\x-0.001 Then
			g_I\IronSightAnim = 0
		EndIf
	Else
		g_I\IronSightAnim = 0
	EndIf
	
	If opt\HoldToAim Then
		If (Not g_I\IronSightAnim) And hasIronSight Then
			prevIronSight = g_I\IronSight
			g_I\IronSight% = MouseDown2
			If g_I\IronSight <> prevIronSight Then
				g_I\IronSightAnim = 2
			EndIf
		EndIf
	Else
		If MouseHit2 Then
			If SelectedItem = Null Then
				If (Not g_I\IronSightAnim) And hasIronSight Then
					g_I\IronSight% = (Not g_I\IronSight%)
					g_I\IronSightAnim = 2
				EndIf
			EndIf
		EndIf
	EndIf
	
End Function

Function DeselectIronSight()
	Local g.Guns
	
	g_I\IronSight% = 0
	g_I\IronSightAnim% = 0
	PositionEntity IronSightPivot%,0,0,0
	PositionEntity IronSightPivot2%,0,0,0
	For g = Each Guns
		EntityParent g\obj,g_I\GunPivot
	Next
	
End Function

Function AddSilencer(g.Guns,render%=True)
	Local prevanimtime
	
	If g <> Null
		If g\CanHaveSilencer
			prevanimtime = AnimTime(g\obj)
			
			g\obj = FreeEntity_Strict(g\obj)
			g\obj = LoadAnimMesh_Strict("GFX\items\weapons\"+g\ViewModelPath2,g_I\GunPivot) ; TODO NEVER ASSIGNED
			ScaleEntity g\obj,0.005,0.005,0.005
			MeshCullBox(g\obj,-MeshWidth(g\obj)*3,-MeshHeight(g\obj)*3,-MeshDepth(g\obj)*3,MeshWidth(g\obj)*6,MeshHeight(g\obj)*6,MeshDepth(g\obj)*6)
			If g_I\HoldingGun <> g\ID
				HideEntity g\obj
			Else
				If render
					SetAnimTime g\obj,prevanimtime
					UpdateWorld
					RenderWorld
				EndIf
			EndIf
			
			FreeImage g\IMG
			g\IMG = LoadImage("GFX\items\weapons\"+g\IMGPath2) ; TODO NEVER ASSIGNED
			MaskImage(g\IMG, 255, 0, 255)
			
			g\HasSilencer = True
		EndIf
		
		If g\name = "usp"
			MoveEntity g\obj,0.01,0.0,0.01
		EndIf
	EndIf
	
End Function

Function IsPlayerOutside()
	Local e.Events
	
	If gopt\GameMode = GAMEMODE_MULTIPLAYER Then Return False
	
	If PlayerRoom\RoomTemplate\Name = "gate_a_topside" Then Return True
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then Return True
	If PlayerRoom\RoomTemplate\Name = "gate_a_intro" Then Return True
	If PlayerRoom\RoomTemplate\Name = "gate_b_topside" Then Return True
	For e.Events = Each Events
		If e\EventName = "testroom_860" Then
			If e\EventState = 1.0
				Return True
			EndIf
			Exit
		EndIf
	Next
	Return False
	
End Function

Function GetWeaponMaxCurrAmmo(gunID%)
	Local g.Guns
	
	For g = Each Guns
		If g\ID = gunID Then
			Return g\MaxCurrAmmo
			Exit
		EndIf
	Next
	
End Function

Function GetWeaponMaxReloadAmmo(gunID%)
	Local g.Guns
	
	For g = Each Guns
		If g\ID = gunID Then
			Return g\MaxReloadAmmo
			Exit
		EndIf
	Next
	
End Function




;~IDEal Editor Parameters:
;~F#8
;~B#55#62F#647#65F#67A#68B#6A2#6B9#6D1
;~C#Blitz3D