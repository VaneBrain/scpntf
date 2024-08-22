Global keyhituse,keydownuse

Global SwitchFollow$ = "Split Up"
Global ChatSFXCHN
;Global KEY_CHAT = GetINIInt(gv\OptionFile, "binds", "Chat key", 46)
Global ChatSFXOpened% = False
Global ChatSFXOpenedTimer# = 0.0
Global ChatSFXOpenedColor% = 255
Global ChatSFXOpenedColorFloat# = 255.0
Global ChatSFX_On, ChatSFX_Off
Global ChatSFX_CurrSound

Global BloodSpitSprite1,BloodSpitSprite2

;Global NTF_DisableConsoleOpening% = GetINIInt(gv\OptionFile, "console", "disable opening")

Global ClassDObj2,N966Obj

Global KEY_USE = GetINIInt(gv\OptionFile, "binds", "Use key", 18)

Global NTF_PainSFX[8]
Global NTF_PainWeakSFX[2]

Global NTF_MaxAmbientSFX% = 39

Global NTF_AimCross% = GetINIInt(gv\OptionFile, "options", "aim cross", 1)

;AI For the NTF

Global ReplaceIMG%,ReplaceTexture%

Global NTF_ErrorAmount% = 0

Global NTF_457Death

Global flame01,Ash

Global NTF_457Flame

Global NTF_RadioCHN
Global KEY_RADIOTOGGLE = GetINIInt(gv\OptionFile, "binds", "Radiotoggle key", 20)

Global NTF_BrokenDoorSFX

Const NTF_Achv457% = 37
Const NTF_AchvMassage% = 38
Const NTF_AchvTeam% = 39
Const NTF_AchvReal% = 40
Const NTF_AchvRage% = 41
Const NTF_AchvContain173% = 42

Global NTF_RadioSFX1[5] ; TODO never assigned
Global NTF_RadioSFX2[11] ; TODO never assigned

; Global NTF_BloodOverlay[2 * 2] UNUSUED, needs porting to array

Global NTF_AmbienceSFX
Global NTF_AmbienceCHN

;General Ambience
Const NTF_MaxAmbienceSFX = 105
Global NTF_AmbienceStrings$[NTF_MaxAmbienceSFX]
;[Block]
NTF_AmbienceStrings[0] = "brief_encounter"
NTF_AmbienceStrings[1] = "friendly_fire"
NTF_AmbienceStrings[2] = "int_bursts"
NTF_AmbienceStrings[3] = "panic"
NTF_AmbienceStrings[4] = "indoor_camera_generic_underground_jolt1"
NTF_AmbienceStrings[5] = "indoor_camera_generic_underground_tremor_high1"
NTF_AmbienceStrings[6] = "indoor_dist_generic_door_kick1"
NTF_AmbienceStrings[7] = "indoor_dist_generic_gunfire_chaotic_pistol1"
NTF_AmbienceStrings[8] = "indoor_dist_generic_gunfire_sustained_pistol1"
NTF_AmbienceStrings[9] = "indoor_dist_generic_gunfire_sustained_smg1"
NTF_AmbienceStrings[10] = "indoor_dist_generic_metal_break1"
NTF_AmbienceStrings[11] = "indoor_dist_generic_metal_scrape1"
NTF_AmbienceStrings[12] = "indoor_dist_generic_metal_scrape2"
NTF_AmbienceStrings[13] = "indoor_dist_generic_metal_stress1"
NTF_AmbienceStrings[14] = "indoor_dist_generic_metal_stress2"
NTF_AmbienceStrings[15] = "indoor_dist_generic_metaldoor_kick1"
NTF_AmbienceStrings[16] = "indoor_far_generic_groan1"
NTF_AmbienceStrings[17] = "indoor_far_generic_groan2"
NTF_AmbienceStrings[18] = "indoor_dist_generic_door_kick1"
NTF_AmbienceStrings[19] = "indoor_dist_generic_explosion_bassy1"
NTF_AmbienceStrings[20] = "indoor_dist_generic_explosion_bassy2"
NTF_AmbienceStrings[21] = "indoor_dist_generic_gunfire_chaotic_ar1"
NTF_AmbienceStrings[22] = "indoor_dist_generic_gunfire_chaotic_pistol2"
NTF_AmbienceStrings[23] = "indoor_dist_generic_gunfire_sustained_ar1"
NTF_AmbienceStrings[24] = "indoor_dist_generic_gunfire_sustained_lmg1"
NTF_AmbienceStrings[25] = "indoor_dist_generic_gunfire_sustained_pistol2"
NTF_AmbienceStrings[26] = "indoor_dist_generic_gunfire_sustained_shotgun1"
NTF_AmbienceStrings[27] = "indoor_dist_generic_gunfire_sustained_smg2"
NTF_AmbienceStrings[28] = "indoor_dist_generic_humanscream_long1"
NTF_AmbienceStrings[29] = "indoor_dist_generic_humanscream_long2"
NTF_AmbienceStrings[30] = "indoor_far_generic_explosion_small1"
NTF_AmbienceStrings[31] = "indoor_far_generic_gunfire_chaotic_pistol1"
NTF_AmbienceStrings[32] = "indoor_far_generic_gunfire_chaotic_smg1"
NTF_AmbienceStrings[33] = "indoor_far_generic_gunfire_chaotic_smg2"
NTF_AmbienceStrings[34] = "indoor_far_generic_gunfire_sustained_ar1"
NTF_AmbienceStrings[35] = "indoor_far_generic_gunfire_sustained_ar2"
NTF_AmbienceStrings[36] = "indoor_far_generic_gunfire_sustained_smg1"
NTF_AmbienceStrings[37] = "indoor_far_generic_gunfire_sustained_smg2"
NTF_AmbienceStrings[38] = "alley_lmg"
For i = 0 To 4
	NTF_AmbienceStrings[39+i] = "int_amb"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[44+i] = "scream"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[47+i] = "indoor_camera_generic_underground_tremor"+(i+1)
Next
For i = 0 To 6
	NTF_AmbienceStrings[50+i] = "indoor_dist_generic_metal_strike"+(i+1)
Next
For i = 0 To 3
	NTF_AmbienceStrings[57+i] = "indoor_dist_generic_metal_squeak"+(i+1)
Next
For i = 0 To 3
	NTF_AmbienceStrings[61+i] = "indoor_dist_generic_metal_strikeshort"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[65+i] = "indoor_far_generic_gunfire_sustained_pistol"+(i+1)
Next
For i = 0 To 4
	NTF_AmbienceStrings[68+i] = "indoor_far_generic_moan"+(i+1)
Next
For i = 0 To 4
	NTF_AmbienceStrings[73+i] = "indoor_far_generic_snarl"+(i+1)
Next
For i = 0 To 3
	NTF_AmbienceStrings[78+i] = "indoor_camera_generic_lights_flicker"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[81+i] = "indoor_dist_generic_howl"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[84+i] = "indoor_dist_generic_humanscream_short"+(i+1)
Next
For i = 0 To 3
	NTF_AmbienceStrings[87+i] = "indoor_dist_generic_roar"+(i+1)
Next
For i = 0 To 4
	NTF_AmbienceStrings[91+i] = "indoor_far_generic_explosion_med"+(i+1)
Next
For i = 0 To 2
	NTF_AmbienceStrings[96+i] = "indoor_far_generic_gunfire_sustained_pistol"+(i+1)
Next
NTF_AmbienceStrings[99] = "2scream"
NTF_AmbienceStrings[100] = "2scream2"
For i = 0 To 2
	NTF_AmbienceStrings[101+i] = "Boom"+(i+1)
Next
NTF_AmbienceStrings[104] = "LowMoan"
;[End Block]

;Entrance Zone Ambience
Const NTF_MaxEZAmbience% = 3
Global NTF_EZAmbienceStrings$[NTF_MaxEZAmbience]
;[Block]
NTF_EZAmbienceStrings[0] = "Chatter4"
NTF_EZAmbienceStrings[1] = "containmentbreachreaction1"
NTF_EZAmbienceStrings[2] = "OhGod"
;[End Block]

;Heavy Containment Zone Ambience
Const NTF_MaxHCZAmbience% = 7
Global NTF_HCZAmbienceStrings$[NTF_MaxHCZAmbience]
;[Block]
NTF_HCZAmbienceStrings[0] = "008death1"
NTF_HCZAmbienceStrings[1] = "008death2"
NTF_HCZAmbienceStrings[2] = "BigDoorClose2"
NTF_HCZAmbienceStrings[3] = "BigDoorOpen"
NTF_HCZAmbienceStrings[4] = "Cough1"
NTF_HCZAmbienceStrings[5] = "Damage2"
NTF_HCZAmbienceStrings[6] = "Damage5"
;[End Block]

;Light Containment Zone Ambience
Const NTF_MaxLCZAmbience% = 6
Global NTF_LCZAmbienceStrings$[NTF_MaxLCZAmbience]
;[Block]
NTF_LCZAmbienceStrings[0] = "ElevatorCrash"
NTF_LCZAmbienceStrings[1] = "muffle"
NTF_LCZAmbienceStrings[2] = "Spooky"
NTF_LCZAmbienceStrings[4] = "StuckInAirlock2"
NTF_LCZAmbienceStrings[5] = "troublewithdoors"
;[End Block]

;Random Sound Event Ambience TODO UNUSUED RN
Const NTF_RES_Max = 11
Global NTF_RandomEventSound$[NTF_RES_Max]
;[Block]
NTF_RandomEventSound[0] = "7_classD_finished_off.ogg"
NTF_RandomEventSound[1] = "8_guard_is_compromised_by_loud_radio.ogg"
NTF_RandomEventSound[2] = "classD_pretends_tosurrender_but_doesnt.ogg"
NTF_RandomEventSound[3] = "grenadelauncher_panicked_discharge.ogg"
NTF_RandomEventSound[4] = "guard_squads_meet_each_other.ogg"
NTF_RandomEventSound[5] = "guards_accidental_friendly_fire_scientist.ogg"
NTF_RandomEventSound[6] = "guards_squad_attacked_bySCP_one_survivor.ogg"
NTF_RandomEventSound[7] = "guards_use_a_grenade.ogg"
NTF_RandomEventSound[8] = "mtf_breach_door.ogg"
NTF_RandomEventSound[9] = "mtf_finds_bodies.ogg"
NTF_RandomEventSound[10] = "mtf_prepares_to_apprehend_SCP.ogg"
;[End Block]

;Intro Ambience
Const NTF_MaxIntroAmbienceSFX = 41
Global NTF_IntroAmbienceStrings$[NTF_MaxIntroAmbienceSFX]
;[Block]
For i = 0 To 6
	NTF_IntroAmbienceStrings[i] = "outdoor_helo_generic_pass_dist_single"+(i+1)
Next
For i = 0 To 2
	NTF_IntroAmbienceStrings[7+i] = "outdoor_helo_generic_pass_dist_squadron"+(i+1)
Next
For i = 0 To 4
	NTF_IntroAmbienceStrings[10+i] = "outdoor_helo_generic_pass_far_double"+(i+1)
Next
For i = 0 To 8
	NTF_IntroAmbienceStrings[15+i] = "outdoor_helo_generic_pass_far_single"+(i+1)
Next
For i = 0 To 4
	NTF_IntroAmbienceStrings[24+i] = "outdoor_helo_generic_pass_xfar_double"+(i+1)
Next
For i = 0 To 6
	NTF_IntroAmbienceStrings[29+i] = "outdoor_helo_generic_pass_xfar_single"+(i+1)
Next
For i = 0 To 4
	NTF_IntroAmbienceStrings[36+i] = "outdoor_helo_generic_patrol_dist_long"+(i+3)
Next
;[End Block]

;Sewer Ambience
Const NTF_MaxSewerAmbienceSFX = 9
Global NTF_SewerAmbienceStrings$[NTF_MaxSewerAmbienceSFX]
;[Block]
NTF_SewerAmbienceStrings[0] = "BangDoorMetalSCP"
NTF_SewerAmbienceStrings[1] = "Breathe"
NTF_SewerAmbienceStrings[2] = "explosiveprojectilefallin"
NTF_SewerAmbienceStrings[3] = "ImHereSewerScp"
NTF_SewerAmbienceStrings[4] = "NearYou"
NTF_SewerAmbienceStrings[5] = "Sewers1"
NTF_SewerAmbienceStrings[6] = "Sewers2"
NTF_SewerAmbienceStrings[7] = "Tunnels1"
NTF_SewerAmbienceStrings[8] = "Tunnels2"
;[End Block]

Global NTF_GasMaskBlood

Global GunPitchShift% = GetINIInt(gv\OptionFile, "options", "gun sfx pitch", 1)
;Global NTF_LQModels% = GetINIInt(gv\OptionFile, "ingame", "lq models")

Global NTF_AchvMenuScroll# = 0.0
;Global NTF_LangMenuScroll# = 0.0

Global NTF_ChatSFX1 = 0, NTF_ChatSFX2 = 0, NTF_ChatCHN1 = 0, NTF_ChatCHN2 = 0

Const ClrR = 50, ClrG = 50, ClrB = 50

Global NTF_CurrZone% = 3

Global Contain173State% = 0
Global Contain173Timer# = 0.0
Global Contain173_SoundPlayed% = False

Const HIT_INTRO_HELI% = 6

Global Intro_SFX
Global Intro_SFX_Timer# = -10.0
Global Intro_CurrSound% = 0

Global MeleeSFX

;Global Z008Textures[3]

Global IsPlayerSprinting% = False

;Global SFXRelease% = GetINIInt(gv\OptionFile, "options", "sfx release", 1)

Global CurrD9341.NPCs

Global SelectedElevatorFloor% = 0
Global CurrElevatorButtonTex%[3]
Global NewElevatorMoveSFX1, NewElevatorMoveSFX2
Global SelectedElevatorEvent

Global UpdateAlarmLight% = False
Global NTF_DisableLight = False

Global MTF_CameraCheckTimer% = 0
Global MTF_CameraCheckDetected% = False

Global GasMaskOverlay2

Global MouseHit3

Global CheckPointDoorObj
Global CheckPointSFX

Global EntityMapLoading% = 0

Global WaterParticleTexture%[2]

Global SplashTextSFX% = LoadSound_Strict("SFX\Interact\Typing.ogg")

Global SaveTexturesInVRam = GetINIInt(gv\OptionFile,"options","enable vram",1)

Global EquipmentSFX[2 * 8] ; TODO unusued
For i = 0 To 7
	EquipmentSFX[i] = LoadSound_Strict("SFX\Player\StepSounds\Equipment_Walk"+(i+1)+".ogg")
	EquipmentSFX[8 + i] = LoadSound_Strict("SFX\Player\StepSounds\Equipment_Run"+(i+1)+".ogg")
Next

;Cheat Menu Variables
Global NTF_SmallHead% = False
Global NTF_FlipGuns% = False

;Ingame Controls
Global CK_Blink = 2		;Cross
Global CK_Use = 6		;R1
Global CK_LMouse = 8	;R2
Global CK_RMouse = 7	;L2
Global CK_MMouse = 5	;L1
Global CK_Sprint = 11	;L3
Global CK_Crouch = 12	;R3
Global CK_Inv = 14		;Touchpad
Global CK_Pause = 10	;OPTIONS
Global CK_Save = 9		;SHARE
Global CK_Reload = 1	;Square
Global CK_Chat = 3		;Circle
Global CK_Radio = 4		;Triangle
;Menu Controls
Global CKM_Press = 2	;Cross
Global CKM_Back = 3		;Circle
Global CKM_Next = 6		;R1
Global CKM_Prev = 5		;L1

Global FOV% = GetINIInt(gv\OptionFile, "options", "fov", 60)

Type CubeMap
	Field Name$
	Field Texture%
	Field Cam%
	Field CamOverlay%
	Field RenderTimer%
	Field RenderY#
	Field Position#[3]
	Field FollowsCamera%
End Type

Global MapCubeMap.CubeMap

Function LoadModStuff()
	Local temp#,i
	
	CreateMainPlayer()
	CreateDamageOverlay()
	
;	D035Obj = LoadAnimMesh_Strict("GFX\npcs\035.b3d")
;	HideEntity D035Obj
;	NaziObj = LoadAnimMesh_Strict("GFX\npcs\naziofficer.b3d")
;	HideEntity NaziObj
;	ZombieObj = LoadAnimMesh_Strict("GFX\npcs\zombie1.b3d")
;	HideEntity ZombieObj
;	ClassDObj2 = LoadAnimMesh_Strict("Unused\GFX\npcs\classd2.b3d")
;	HideEntity ClassDObj2
;	B457Obj = LoadAnimMesh_Strict("Unused\GFX\npcs\457test.b3d")
;	HideEntity B457Obj
	
	ChatSFX_On = LoadSound_Strict("SFX\Player\RadioOn.ogg")
	ChatSFX_Off = LoadSound_Strict("SFX\Player\RadioOff.ogg")
	
	ChatSFX_CurrSound = 0
	
	For i = 0 To 7
		NTF_PainSFX[i]=LoadSound_Strict("SFX\player\pain"+(i+1)+".ogg")
	Next
	For i = 0 To 1
		NTF_PainWeakSFX[i]=LoadSound_Strict("SFX\player\painweak"+(i+1)+".ogg")
	Next
;	
;	NTF_457Death = LoadSound_Strict("SFX\294\burn.ogg")
;	NTF_457Flame = LoadSound_Strict("Unused\SFX\457\Fire2.ogg")
;	
;	Ash=LoadSprite("Unused\GFX\fire_particle.bmp"):HideEntity Ash
;	flame01=LoadSprite("Unused\GFX\smk01.bmp"):HideEntity flame01
;	
	NTF_RadioCHN = 3
;	
;	NTF_BrokenDoorSFX = LoadSound_Strict("Unused\SFX\ambience\rooms\brokendoor.ogg")
;	
;	NTF_BloodOverlay(0,0) = LoadSprite("Unused\GFX\BloodOverlay1.jpg",1+2)
;	SpriteViewMode NTF_BloodOverlay(0,0),1
;	ScaleSprite NTF_BloodOverlay(0,0),3,2
;	EntityParent NTF_BloodOverlay(0,0),ark_blur_cam
;	MoveEntity NTF_BloodOverlay(0,0),0,0,1
;	EntityFX NTF_BloodOverlay(0,0),1
;	EntityOrder NTF_BloodOverlay(0,0),-2
;	HideEntity NTF_BloodOverlay(0,0)
;	
;	NTF_BloodOverlay(0,1) = CreateSprite()
;	SpriteViewMode NTF_BloodOverlay(0,1),1
;	ScaleSprite NTF_BloodOverlay(0,1),3,2
;	EntityParent NTF_BloodOverlay(0,1),ark_blur_cam
;	MoveEntity NTF_BloodOverlay(0,1),0,0,1
;	EntityFX NTF_BloodOverlay(0,1),1
;	EntityOrder NTF_BloodOverlay(0,1),-1
;	EntityColor NTF_BloodOverlay(0,1),50,0,0
;	HideEntity NTF_BloodOverlay(0,1)
;	
;	NTF_GasMaskBlood = LoadSprite("Unused\GFX\BloodOverlay.jpg",1+2,ark_blur_cam)
;	ScaleSprite(NTF_GasMaskBlood, 1.0, Float(opt\GraphicHeight)/Float(opt\GraphicWidth))
;	EntityBlend (NTF_GasMaskBlood, 2)
;	EntityFX(NTF_GasMaskBlood, 1)
;	EntityOrder NTF_GasMaskBlood, -1000
;	MoveEntity(NTF_GasMaskBlood, 0, 0, 1.0)
;	HideEntity(NTF_GasMaskBlood)
	
	Contain173State% = 0
	Contain173Timer# = 0.0
;	
;	SplashText_Timer# = 0.0
;	SplashText_Timer2# = 0.0
;	SplashText_Timer3# = 255.0
;	SplashText_CurrentLength% = 0
;	SplashText_X# = 0.0
;	SplashText_Y# = 0.0
;	SplashText_Text$ = ""
;	SplashText_ShowTime# = 0.0
;	SplashText_Length% = 0
;	
;	MeleeSFX = LoadSound_Strict("Unused\SFX\melee_whoosh1.wav")
;	
;	For i = 0 To 2
;		Z008Textures[i] = LoadTexture_Strict("Unused\GFX\npcs\008_"+(i+1)+".jpg")
;	Next
;	
;	PlayCustomMusic% = False
;	
;	;CurrD9341 = CreateNPC(NPCtypeD9341,0,-30,0)
	
	;NewElevatorMoveSFX1 = LoadSound_Strict("Unused\SFX\Elevator\sfx_plat_stm1+mod1.ogg")
	;NewElevatorMoveSFX2 = LoadSound_Strict("Unused\SFX\Elevator\sfx_plat_stm1+mod2.ogg")
	
;	;MainLight = CreateLight()
;	;MainLightHidden% = False
;	;LightColor MainLight,40,40,40
;	;LightRange MainLight,0.5
;	;;EntityParent MainLight,Camera
;	
;	NTF_SprintPitch# = 0.0
;	NTF_SprintPitchSide% = 0
;	NTF_KnockBack# = 0.0
;	NTF_KnockBackSide% = 1
	
	GasMaskOverlay2 = LoadSprite("GFX\GasmaskOverlay.jpg",1,ark_blur_cam)
	ScaleSprite GasMaskOverlay2,1.0,Float(opt\GraphicHeight)/Float(opt\GraphicWidth)
	EntityBlend (GasMaskOverlay2, 2)
	EntityFX(GasMaskOverlay2, 1)
	EntityOrder GasMaskOverlay2, -2000
	MoveEntity(GasMaskOverlay2, 0, 0, 1.0)
	ShowEntity(GasMaskOverlay2)
	
	CheckPointDoorObj = LoadMesh_Strict("GFX\checkpointdoors.b3d")
	HideEntity CheckPointDoorObj
	CheckPointSFX = LoadSound_Strict("SFX\Door\DoorCheckpoint.ogg")
	
	MTFSFX=LoadSound_Strict("SFX\Character\MTF\Beep.ogg")
	
	WaterParticleTexture[0] = LoadTexture_Strict("GFX\WaterParticle.png",1+2,2)
	WaterParticleTexture[1] = LoadTexture_Strict("GFX\WaterParticle2.png",1+2,2)
	
	;NTF_ViewPlayerModel = LoadAnimMesh_Strict("Unused\GFX\npcs\MTF_PlayerBody.b3d")
	ClassDObj2 = LoadAnimMesh_Strict("GFX\npcs\classd2.b3d")
	HideEntity ClassDObj2
	
	ApplyHitBoxes(NPCtypeGuard,"Guard")
	ApplyHitBoxes(NPCtypeZombie,"Zombie")
	ApplyHitBoxes(NPCtype008,"008")
	ApplyHitBoxes(NPCtypeD2,"Class-D")
	ApplyHitBoxes(NPCtype076,"Zombie2")
	ApplyHitBoxes(NPCtypeEGuard,"Guard")
	ApplyHitBoxes(NPCtypeKGuard,"Guard")
	ApplyHitBoxes(NPCtype939,"939")
	ApplyHitBoxes(NPCtypeTentacle,"Tentacle")
	ApplyHitBoxes(NPCtype1048a,"1048A")
	
	PreloadAllNPCAnimations()
	
	MapCubeMap = CreateCubeMap("MapCubeMap")
	
	NEI = New NewElevatorInstance
	NEI\button_number_tex = LoadAnimTexture("GFX\map\elev_123.png",1,64,64,0,3)
	
	InitGuns()
	
	SZL_LoadEntitiesForZone()
	
	CreateCommunicationAndSocialWheel()
	
End Function

Function DeleteModStuff()
	Local vf.VolumeFog,i
	
	DestroyMainPlayer()
	
	For i = 0 To 7
		FreeSound_Strict NTF_PainSFX[i] : NTF_PainSFX[i] = 0
	Next
	For i = 0 To 1
		FreeSound_Strict NTF_PainWeakSFX[i] : NTF_PainWeakSFX[i] = 0
	Next
	FreeSound_Strict NTF_457Death : NTF_457Death = 0
	FreeSound_Strict NTF_457Flame : NTF_457Flame = 0
	
	If ChannelPlaying(ChatSFXCHN) Then StopChannel(ChatSFXCHN)
	
	FreeSound_Strict NTF_BrokenDoorSFX : NTF_BrokenDoorSFX = 0
	
	If ChannelPlaying(NTF_ChatCHN1) Then StopChannel(NTF_ChatCHN1)
	If ChannelPlaying(NTF_ChatCHN2) Then StopChannel(NTF_ChatCHN2)
	
	If NTF_ChatSFX1 <> 0 Then FreeSound_Strict NTF_ChatSFX1 : NTF_ChatSFX1 = 0
	If NTF_ChatSFX2 <> 0 Then FreeSound_Strict NTF_ChatSFX2 : NTF_ChatSFX2 = 0
	
	Contain173State% = 0
	Contain173Timer# = 0.0
	
	If Intro_SFX <> 0 Then FreeSound_Strict Intro_SFX : Intro_SFX = 0
	Intro_SFX_Timer# = -10.0
	Intro_CurrSound% = 0
	
	If MeleeSFX <> 0 Then FreeSound_Strict MeleeSFX : MeleeSFX = 0
	
	PlayCustomMusic% = False
	If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
	
	If NewElevatorMoveSFX1 <> 0 Then FreeSound_Strict NewElevatorMoveSFX1 : NewElevatorMoveSFX1 = 0
	If NewElevatorMoveSFX2 <> 0 Then FreeSound_Strict NewElevatorMoveSFX2 : NewElevatorMoveSFX2 = 0
	
	Delete Each NewElevator
	Delete Each HitBox
	Delete Each CubeMap
	MapCubeMap = Null
	
	Delete NEI
	
	DeleteGuns()
	
	Delete CurrGrid
	
End Function

Type BloodSpit
	Field obj%
	Field x#,y#,z#
	Field KillTimer#
	Field DirectionRAND
End Type

Function CreateBloodSpit.BloodSpit(x#,y#,z#)
	Local BS.BloodSpit = New BloodSpit
	
	;BS\obj = CopyEntity(BloodSpitSprite(Rand(0,1)))
	Local random% = Rand(0,1)
	If random%=0
		BS\obj = CopyEntity(BloodSpitSprite1)
	Else
		BS\obj = CopyEntity(BloodSpitSprite2)
	EndIf
	ScaleSprite BS\obj,Rand(0.1,0.25),Rand(0.1,0.25)
	BS\x# = x#
	BS\y# = y#
	BS\z# = z#
	PositionEntity BS\obj,x#,y#,z#
	BS\DirectionRAND = Rand(0,3)
	
	Return BS
End Function

Function UpdateBloodSpit()
	Local BS.BloodSpit
	
	For BS = Each BloodSpit
		If BS\KillTimer# < 2000.0
			BS\KillTimer# = BS\KillTimer# + FPSfactor#
			Select BS\DirectionRAND
				Case 0
					MoveEntity BS\obj,0,0,0.1
				Case 1
					MoveEntity BS\obj,0.1,0,0
				Case 2
					MoveEntity BS\obj,0,0,-0.1
				Case 3
					MoveEntity BS\obj,-0.1,0,0
			End Select
			TranslateEntity BS\obj,0,-0.1*FPSfactor,0
		Else
			BS\obj = FreeEntity_Strict(BS\obj)
			Delete BS
		EndIf
	Next
	
End Function

;Each Flame of the fire
Type flame
	Field ent
	Field ang#
	Field size#
	Field alph#
	Field dis#
	Field dx#, dy#, dz#
End Type

;The fire itself
Type fire
	Field piv
	; Direction
	Field dx#, dy#, dz#
	Field flag$
End Type

;Hot ashes
Type ash_particle
	Field ent
	Field alpha#
	Field dx#,dy#,dz#
	Field pop
End Type

;Add a flame to the fire
Function Add_flame(x#,y#,z#,size#=1,dis#=.016,dx#=0,dy#=0.3,dz#=0)
	a.flame=New flame
	a\ent=CopyEntity(flame01)
	PositionEntity a\ent,x,y,z
	a\alph=1
	a\size=size
	a\dis=dis
	a\ang=Rnd(360)
	ScaleSprite a\ent,a\size,a\size
	EntityColor a\ent,Rnd(150,255),Rnd(0,100),0
	a\dx=dx
	a\dy=dy
	a\dz=dz
End Function

;Update flames
Function Update_flames()
	For a.flame=Each flame
		If a\alph>0.01 Then
			a\alph=a\alph-a\dis
			EntityAlpha a\ent,a\alph
			RotateSprite a\ent,a\ang
			a\ang=a\ang+.2
			MoveEntity a\ent,a\dx,a\dy,a\dz
		Else
			a\ent = FreeEntity_Strict(a\ent)
			Delete a
		EndIf
	Next
End Function

;Erase all flames
Function Erase_flames()
	For a.flame=Each flame
		a\ent = FreeEntity_Strict(a\ent)
	Next
	Delete Each flame
End Function

;Update all fires
Function Update_Fires()
	For a.fire=Each fire
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
		Add_flame(EntityX(a\piv)+Rnd(-0.1,0.1),EntityY(a\piv),EntityZ(a\piv)+Rnd(-0.1,0.1),Rnd(0.2,0.6),.04,a\dx,a\dy,a\dz)
	Next
	Update_flames()
End Function

;Erase all fires
Function Erase_Fires()
	For a.fire=Each fire
		a\piv = FreeEntity_Strict(a\piv)
	Next
	Delete Each fire
End Function

;Add a fire to the scene
Function Add_Fire.fire(x#,y#,z#,dx#=0,dy#=.05,dz#=0,flag$="")
	a.fire=New fire
	a\piv=CreatePivot()
	PositionEntity a\piv,x,y,z
	a\dx=dx:a\dy=dy:a\dz=dz
	a\flag$ = flag$
	Return a
End Function

;Add a particle to the scene
Function Add_AshParticle(x#,y#,z#,r=255,g=255,b=255)
	a.ash_particle=New ash_particle
	a\ent=CopyEntity(Ash)
	PositionEntity a\ent,x,y,z
	a\dx=Rnd(-.01,.01)
	a\dy=Rnd(0.01,.07)
	a\dz=Rnd(-.01,.01)
	ScaleSprite a\ent,Rnd(.01,.02),Rnd(.01,.02)
	a\alpha=1
	a\pop=False
	EntityColor a\ent,r,g,b
End Function

;Update all particles
Function Update_AshParticles()
	For a.ash_particle = Each ash_particle
		MoveEntity a\ent,a\dx,a\dy,a\dz
		If EntityY(a\ent)<.3 Then 
			a\dy=-a\dy
			a\dy=a\dy*.62
			a\pop=True
		End If
		a\dy=a\dy-.02
		
		If a\pop Then
			a\alpha=a\alpha-.02
			EntityAlpha a\ent,a\alpha
			If a\alpha<0.05 Then
				a\ent = FreeEntity_Strict(a\ent)
				Delete a
			EndIf
		EndIf
	Next
End Function

;Erase all particles
Function Erase_Particles()
	For a.ash_particle = Each ash_particle
		a\ent = FreeEntity_Strict(a\ent)
	Next
	Delete Each ash_particle
End Function

;this works very similar to the radio, but it has the difference of that you need to toggle between the channels using a key
Function UpdateRadio()
	
	Local CoffinExists = False
	For r.rooms = Each Rooms
		If r\RoomTemplate\Name$ = "coffin"
			CoffinExists = True
			Exit
		EndIf
	Next
	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" 
		
	ElseIf CoffinDistance < 4.0 And CoffinExists
		;Unused\SFX\radiomal.ogg TODO
	Else
		Select NTF_RadioCHN
			Case 0 ;randomkanava
				
			Case 1 ;hÃ?Â¤lytyskanava
				DebugLog RadioState[1] 
				
				ResumeChannel(RadioCHN[1])
				If ChannelPlaying(RadioCHN[1]) = False Then
					
					If RadioState[1] => 5 Then
						RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10 + 1])
						RadioState[1] = 0
					Else
						RadioState[1]=RadioState[1]+1	
						RadioCHN[1] = PlaySound_Strict(RadioSFX[0 * 10])	
					EndIf
						
				EndIf
				
			Case 2 ;scp-radio
				ResumeChannel(RadioCHN[2])
				If ChannelPlaying(RadioCHN[2]) = False Then
					RadioState[2]=RadioState[2]+1
					If RadioState[2] = 17 Then RadioState[2] = 1
						If Floor(RadioState[2]/2)=Ceil(RadioState[2]/2) Then ;parillinen, soitetaan normiviesti
							RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10 + Int(RadioState[2]/2)])	
						Else ;pariton, soitetaan musiikkia
							RadioCHN[2] = PlaySound_Strict(RadioSFX[1 * 10])
						EndIf
					EndIf
				;EndIf
			Case 3
				ResumeChannel(RadioCHN[3])
				
				If MTFtimer > 0 Then 
					RadioState[3]=RadioState[3]+Max(Rand(-10,1),0)
					Select RadioState[3]
						Case 100
							RadioCHN[3] = PlaySound_Strict(NTF_RadioSFX1[0])
							RadioState[3]=RadioState[3]+1
						Case 800
							RadioCHN[3] = PlaySound_Strict(NTF_RadioSFX1[1])
							RadioState[3]=RadioState[3]+1													
						Case 1200
							RadioCHN[3] = PlaySound_Strict(NTF_RadioSFX1[2])
							RadioState[3]=RadioState[3]+1	
						Case 1600
							RadioCHN[3] = PlaySound_Strict(NTF_RadioSFX1[3])
							RadioState[3]=RadioState[3]+1															
						Case 2000
							RadioCHN[3] = PlaySound_Strict(NTF_RadioSFX1[4])	
							RadioState[3]=RadioState[3]+1		
					End Select
				EndIf
			Case 4									
				ResumeChannel(RadioCHN[4])
				If ChannelPlaying(RadioCHN[4]) = False Then 
					If RemoteDoorOn = False And RadioState[8] = False Then
						RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[0])	
						RadioState[8] = True
					Else
						RadioState[4]=RadioState[4]+Max(Rand(-10,1),0)
						
						Select RadioState[4]
							Case 10
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[1])
								RadioState[4]=RadioState[4]+1													
							Case 100
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[2])
								RadioState[4]=RadioState[4]+1	
							Case 158
								If MTFtimer = 0 Then 
									RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[3])
									RadioState[4]=RadioState[4]+1
								EndIf
							Case 200
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[4])
								RadioState[4]=RadioState[4]+1
							Case 260
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[5])
								RadioState[4]=RadioState[4]+1
							Case 300
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[6])	
								RadioState[4]=RadioState[4]+1	
							Case 350
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[7])
								RadioState[4]=RadioState[4]+1
							Case 400
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[8])
								RadioState[4]=RadioState[4]+1
							Case 450
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[9])	
								RadioState[4]=RadioState[4]+1		
							Case 600
								RadioCHN[4] = PlaySound_Strict(NTF_RadioSFX2[10])	
								RadioState[4]=RadioState[4]+1	
						End Select
					EndIf
				EndIf
		End Select
		
		If InteractHit(KEY_RADIOTOGGLE,CK_Radio)
			If RadioCHN[NTF_RadioCHN] <> 0 Then PauseChannel(RadioCHN[NTF_RadioCHN])
			NTF_RadioCHN = NTF_RadioCHN + 1
			PlaySound_Strict RadioSquelch
			If NTF_RadioCHN > 4
				NTF_RadioCHN = 0
			EndIf
		EndIf
		
		Color 255,255,255
		If HUDenabled
			
			;x% = opt\GraphicWidth - 150
			x% = opt\GraphicWidth - 300
			;y% = (opt\GraphicHeight/2) + 250
			y% = opt\GraphicHeight - 135
			
			SetFont fo\Font[Font_Digital_Small]
			Text x,y,"CHN: "+(NTF_RadioCHN+1)
		EndIf
		SetFont fo\Font[Font_Default]
		
	EndIf
	
End Function

Type MapProps
	Field obj%
End Type

Function UpdateMapProps()
	
	For a.MapProps = Each MapProps
		If a\obj<>0
			If EntityDistanceSquared(a\obj,Camera)<PowTwo(10.0)
				If EntityVisible(a\obj,Camera)
				;If EntityInView(a\obj,Camera)
					ShowEntity a\obj
				Else
					HideEntity a\obj
				EndIf
			Else
				HideEntity a\obj
			EndIf
		EndIf
	Next
	
End Function

;Unused
;[Block]
;Function PlayMTFSFX(sfx$,n.NPCs,min_amount%=0,max_amount%=0)
;	Local random% = Rand(min_amount%,max_amount%)
;	
;	;If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN)
;	If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound=0
;	
;	If n <> Null
;		If n\NPCID% = 1
;			If min_amount% = 0 And max_amount% = 0
;				n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\1\"+sfx$+".ogg")
;			Else
;				If FileType("Unused\SFX\MTF\Other_GasMask\1\"+(sfx$+random%)+".ogg")=1
;					n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\1\"+(sfx$+random%)+".ogg")
;				Else
;					n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\1\"+(sfx$+min_amount%)+".ogg")
;				EndIf
;			EndIf
;			n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider, 8.0)
;		ElseIf n\NPCID% = 2
;			If min_amount% = 0 And max_amount% = 0
;				n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\2\"+sfx$+".ogg")
;			Else
;				If FileType("Unused\SFX\MTF\Other_GasMask\2\"+(sfx$+random%)+".ogg")=1
;					n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\2\"+(sfx$+random%)+".ogg")
;				Else
;					n\Sound = LoadSound_Strict("Unused\SFX\MTF\Other_GasMask\2\"+(sfx$+min_amount%)+".ogg")
;				EndIf
;			EndIf
;			n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider, 8.0)
;		Else
;			If min_amount% = 0 And max_amount% = 0
;				n\Sound = LoadSound_Strict("SFX\MTF\"+sfx$+".ogg")
;			Else
;				If FileType("SFX\MTF\"+(sfx$+random%)+".ogg")=1
;					n\Sound = LoadSound_Strict("SFX\MTF\"+(sfx$+random%)+".ogg")
;				Else
;					n\Sound = LoadSound_Strict("SFX\MTF\"+(sfx$+min_amount%)+".ogg")
;				EndIf
;			EndIf
;			n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider, 8.0)
;		EndIf
;	EndIf
;	
;End Function
;[End Block]

Type SplashText
	Field Timer#
	Field ShowTime#
	Field CurrentLength%
	Field DisplayAmount#
	Field X#, Y#
	Field Txt$
End Type

Function CreateSplashText.SplashText(txt$,x#,y#,displayamount#)
	Local st.SplashText = New SplashText
	
	st\Txt = txt
	st\X = x
	st\Y = y
	st\DisplayAmount = displayamount
	
	Return st
End Function

Function UpdateSplashTexts()
	Local st.SplashText
	
	For st = Each SplashText
		If st\CurrentLength < Len(st\Txt) Then
			If st\Timer < 10.0
				st\Timer = st\Timer + FPSfactor
			Else
				st\CurrentLength = st\CurrentLength + 1
				PlaySound_Strict SplashTextSFX
				st\Timer = 0.0
			EndIf
		Else
			If st\Timer < st\DisplayAmount+255.0 Then
				st\Timer = Min(st\Timer+FPSfactor,st\DisplayAmount+255.0)
			Else
				Delete st
			EndIf
		EndIf
	Next
	
End Function

Function DrawSplashTexts()
	Local st.SplashText
	Local amount% = 0
	Local c%
	
	SetFont fo\Font[Font_Digital_Medium]
	For st = Each SplashText
		Color 0,0,0
		Rect st\X,st\Y+(32*amount),StringWidth(Left(st\Txt,st\CurrentLength)),StringHeight(Left(st\Txt,st\CurrentLength)),True
		If st\CurrentLength < Len(st\Txt) Then
			c = 255
		Else
			c = Min((st\DisplayAmount+255.0)-st\Timer,255.0)
		EndIf
		Color c,c,c
		Text st\X,st\Y+(32*amount),Left(st\Txt,st\CurrentLength)
		amount = amount + 1
	Next
	
End Function

Type CoordPoints
	Field name$
	Field obj%
End Type

Function CreateCoordPoint.CoordPoints(name$,x#,y#,z#,parent%=0,pos%=False)
	Local cdp.CoordPoints = New CoordPoints
	
	cdp\name$ = name$
	cdp\obj% = CreatePivot(parent%)
	PositionEntity cdp\obj%,x#,y#,z#,pos%
	
	Return cdp
End Function

Function DeleteCoordPoint(cdp.CoordPoints)
	
	cdp\obj = FreeEntity_Strict(cdp\obj)
	Delete cdp
	
End Function

Function Between(a#,value#,b#)
	
	If a# > value#
		Return a#
	ElseIf b# < value#
		Return b#
	Else
		Return value#
	EndIf
	
End Function

Function PlayIntroSFX(CHN%)
	
	If Intro_SFX_Timer# > 0.0
		Intro_SFX_Timer# = Intro_SFX_Timer# - FPSfactor
	ElseIf Intro_SFX_Timer# < 0.01 And Intro_SFX_Timer# > -10.0
		Intro_CurrSound% = Intro_CurrSound% + 1
		Intro_SFX_Timer# = 0.0
	EndIf
	
	If Intro_SFX_Timer# = 0.0
		If ChannelPlaying(CHN%) Then StopChannel(CHN%)
		If Intro_SFX <> 0 Then FreeSound_Strict Intro_SFX : Intro_SFX = 0
		Intro_SFX = LoadSound_Strict("Unused\SFX\Intro\heli_line"+Intro_CurrSound%+".ogg") ; TODO don't exist
		CHN% = PlaySound_Strict(Intro_SFX)
		Intro_SFX_Timer# = -10.0
	EndIf
	
End Function

Global NTF_SprintPitch# = 0.0
Global NTF_SprintPitchSide% = 0

Function UpdateSprint()
	
	If IsPlayerSprinting%
		If NTF_SprintPitchSide% = 0
			If NTF_SprintPitch# < 0.5
				NTF_SprintPitch# = NTF_SprintPitch + (0.1*FPSfactor)
			Else
				NTF_SprintPitchSide% = 1
			EndIf
		Else
			If NTF_SprintPitch# > -0.5
				NTF_SprintPitch# = NTF_SprintPitch - (0.1*FPSfactor)
			Else
				NTF_SprintPitchSide% = 0
			EndIf
		EndIf
		;NTF_SprintPitch# = Max(Min(NTF_SprintPitch#,0.5),-0.5)
	Else
		If NTF_SprintPitchSide% = 0
			NTF_SprintPitch# = Min(0,NTF_SprintPitch#+(0.1*FPSfactor))
		Else
			NTF_SprintPitch# = Max(0,NTF_SprintPitch#-(0.1*FPSfactor))
		EndIf
	EndIf
	
End Function

Global Credits_LineAmount% = 0
Type Credits_Lines
	Field txt$
End Type

Type VolumeFog
	Field obj%
	Field deadTime#
End Type

Function UpdateVolumeFog()
	Local vf.VolumeFog, alpha#
	
	PositionEntity VolumeFogPivot%,EntityX(Camera),EntityY(Camera),EntityZ(Camera)
	
	If VolumeFogAmount# > 0.0
		If VolumeFogUpdate# < 50.0
			VolumeFogUpdate# = VolumeFogUpdate# + (FPSfactor*VolumeFogAmount#)
		Else
			VolumeFogUpdate# = 0.0
		EndIf
		If VolumeFogUpdate# = 0.0
			vf.VolumeFog = New VolumeFog
			vf\obj% = CopyEntity(flame01,VolumeFogPivot%)
			;PositionEntity vf\obj%,EntityX(Collider)+Rnd(-4,4),EntityY(Collider)+Rnd(-1,1),EntityZ(Collider)+Rnd(-4,4)
			MoveEntity vf\obj%,Rnd(-2,2),Rnd(-1,1),Rnd(-2,2)
			SpriteViewMode vf\obj%,1
			ScaleSprite vf\obj%,Rnd(1.0,1.25),Rnd(1.0,1.25)
			EntityFX vf\obj%,1+8
			EntityColor vf\obj%,100,100,100
			RotateEntity vf\obj%,0,0,Rand(360)
			EntityOrder vf\obj%,-1
			;EntityParent vf\obj%,VolumeFogPivot%
		EndIf
	EndIf
	
	For vf.VolumeFog = Each VolumeFog
		MoveEntity vf\obj%,0,0.0025*FPSfactor,0
		If vf\deadTime# > -100.0
			vf\deadTime# = vf\deadTime# - FPSfactor
		ElseIf vf\deadTime# < -99.9 And vf\deadTime# > -300.0
			alpha# = (vf\deadTime#+300)/200
			;DebugLog alpha#
			EntityAlpha vf\obj%,alpha#
			vf\deadTime# = vf\deadTime# - FPSfactor
		Else
			vf\obj = FreeEntity_Strict(vf\obj)
			Delete vf
		EndIf
	Next
	
	If VolumeFogAmount# <= 0.0
		For vf.VolumeFog = Each VolumeFog
			vf\obj = FreeEntity_Strict(vf\obj)
			Delete vf
		Next
	EndIf
	
End Function

Function UpdateAlarmRotor(OBJ,rotation#)
	
	TurnEntity OBJ,0,-rotation*FPSfactor,0
	
End Function

Function RelightRoom(r.Rooms,tex%,oldtex%) ;replace a lightmap texture
	Local mesh=GetChild(r\obj,2)
	Local surf%,brush%,tex2%,texname$,temp%,temp2%
	Local comparison$ = StripPath(TextureName(oldtex))
	;temp=0
	;If r\RoomTemplate\isRmesh Then
	temp=(BumpEnabled+1)
	For i=1 To CountSurfaces(mesh)
		temp2=temp
		surf=GetSurface(mesh,i)
		brush=GetSurfaceBrush(surf)
		If brush<>0 Then
			tex2=GetBrushTexture(brush,temp2)
			If tex2=0 And temp2>1 Then tex2=GetBrushTexture(brush,0) : temp2=1
			If tex2<>0 Then
				texname=TextureName(tex2)
				If StripPath(texname)=comparison Then ;Instr(texname,".bmp")<>0 Then
					BrushTexture brush,tex,0,temp
					PaintSurface surf,brush
				EndIf
				DeleteSingleTextureEntryFromCache tex2
			EndIf
			FreeBrush brush
		EndIf
	Next
	;Else
	;	EntityTexture mesh,tex,0,(BumpEnabled Shl 1)
	;EndIf
End Function

Function CreateCubeMap.CubeMap(Name$,CubeMapMode%=1,FollowsCamera%=True,PosX#=0.0,PosY#=0.0,PosZ#=0.0,RenderY#=0.0,TexSize%=256)
	Local cm.CubeMap = New CubeMap
	
	cm\Name = Name$
	cm\RenderY = RenderY#
	
	cm\Texture=CreateTextureUsingCacheSystem(TexSize,TexSize,128+256)
	TextureBlend cm\Texture,3
	cm\Cam=CreateCamera()
	CameraFogMode cm\Cam,1
	CameraFogRange cm\Cam,5,10
	CameraRange cm\Cam,0.01,20
	CameraClsMode cm\Cam,False,True
	CameraProjMode cm\Cam,0
	CameraViewport cm\Cam,0,0,TexSize,TexSize
	cm\CamOverlay=CreateSprite(cm\Cam)
	EntityFX cm\CamOverlay,1
	EntityColor cm\CamOverlay,0,0,0
	EntityOrder cm\CamOverlay,-1000
	ScaleSprite cm\CamOverlay,5,5
	MoveEntity cm\CamOverlay,0,0,0.1
	EntityAlpha cm\CamOverlay,0.0
	HideEntity cm\CamOverlay
	
	SetCubeMode cm\Texture,CubeMapMode
	
	cm\FollowsCamera = FollowsCamera
	If cm\FollowsCamera = False Then
		cm\Position[Vector_X] = PosX#
		cm\Position[Vector_Y] = PosY#
		cm\Position[Vector_Z] = PosZ#
	EndIf
	
	Return cm
End Function

Function RenderCubeMap(entity%,name$)
	Local cm.CubeMap
	Local tex_sz%
	
	If opt\RenderCubeMapMode=0 Then Return
	
	For cm = Each CubeMap
		If cm\Name = name$ Then
			tex_sz=TextureWidth(cm\Texture)
			CameraProjMode Camera,0
			CameraProjMode cm\Cam,1
			If entity<>0 Then
				HideEntity entity
			EndIf
			HideEntity g_I\GunPivot
			ShowEntity cm\CamOverlay
			EntityAlpha cm\CamOverlay,0.7
			CameraFogRange cm\Cam,0.1,3
			CameraFogColor cm\Cam,5,20,3
			If opt\RenderCubeMapMode = 2 Then
				;do left view
				SetCubeFace cm\Texture,0
				RotateEntity cm\Cam,0,90,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do forward view
				SetCubeFace cm\Texture,1
				RotateEntity cm\Cam,0,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do right view	
				SetCubeFace cm\Texture,2
				RotateEntity cm\Cam,0,-90,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do backward view
				SetCubeFace cm\Texture,3
				RotateEntity cm\Cam,0,180,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do up view
				SetCubeFace cm\Texture,4
				RotateEntity cm\Cam,-90,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do down view
				SetCubeFace cm\Texture,5
				RotateEntity cm\Cam,90,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
			ElseIf opt\RenderCubeMapMode = 1 Then
				If cm\RenderTimer <= 0 Then
					;do left view	
					SetCubeFace cm\Texture,0
					RotateEntity cm\Cam,0,90,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do forward view
					SetCubeFace cm\Texture,1
					RotateEntity cm\Cam,0,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 1
				ElseIf cm\RenderTimer = 1 Then
					;do right view	
					SetCubeFace cm\Texture,2
					RotateEntity cm\Cam,0,-90,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do backward view
					SetCubeFace cm\Texture,3
					RotateEntity cm\Cam,0,180,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 2
				ElseIf cm\RenderTimer = 2 Then
					;do up view
					SetCubeFace cm\Texture,4
					RotateEntity cm\Cam,-90,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do down view
					SetCubeFace cm\Texture,5
					RotateEntity cm\Cam,90,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 0
				EndIf
			EndIf	
			If entity<>0 Then
				ShowEntity entity
			EndIf
			ShowEntity g_I\GunPivot
			EntityAlpha cm\CamOverlay,0.0
			CameraFogRange cm\Cam,5,10
			CameraFogColor cm\Cam,0,0,0
			HideEntity cm\CamOverlay
			CameraProjMode Camera,1
			CameraProjMode cm\Cam,0
			Exit
		EndIf
	Next
	
End Function

Function UpdateCubeMap(entity%,name$)
	Local cm.CubeMap
	Local camoffsety#
	
	If opt\RenderCubeMapMode=0 Then Return
	
	For cm = Each CubeMap
		If cm\Name = name$
			If cm\FollowsCamera Then
				If entity<>0 Then
					camoffsety#=(EntityY(Camera,True)-(EntityY(entity,True)+cm\RenderY))
					PositionEntity cm\Cam,EntityX(Camera,True),(EntityY#(entity,True)+cm\RenderY)-camoffsety,EntityZ(Camera,True)
				Else
					camoffsety#=(EntityY(Camera,True)+cm\RenderY)
					PositionEntity cm\Cam,EntityX(Camera,True),cm\RenderY-camoffsety,EntityZ(Camera,True)
				EndIf
			Else
				PositionEntity cm\Cam,cm\Position[Vector_X],cm\Position[Vector_Y],cm\Position[Vector_Z]
			EndIf
			If entity<>0 Then
				ShowEntity entity
			EndIf
			Exit
		EndIf
	Next
	
End Function

Global PlayerInNewElevator% = False
Global PlayerNewElevator% = 0

Type NewElevatorInstance
	Field button_number_tex
End Type
Global NEI.NewElevatorInstance

Type NewElevator
	Field obj%
	Field state#
	Field currfloor%
	Field tofloor%
	Field door.Doors
	Field floory#[3]
	Field ID#
	Field speed#=6
	Field CurrSpeed#
	Field sound%
	Field soundchn%
	Field currsound%
	Field room.Rooms
	Field button_arrow[2]
	Field button_numbers
	Field floorlocked[3]
End Type

Function CreateNewElevator.NewElevator(obj%,currfloor%,door.Doors,id#,r.Rooms,floor1y#,floor2y#,floor3y#=0.0,speed#=7.0)
	Local tex%
	Local ne.NewElevator = New NewElevator
	Local i
	
	ne\room = r
	ne\obj = obj
	ne\currfloor = currfloor
	ne\tofloor = currfloor
	ne\state = 0.0
	ne\door = door
	ne\floory[0] = floor1y
	ne\floory[1] = floor2y
	ne\floory[2] = floor3y
	ne\speed = speed
	ne\ID = id
	tex = LoadTextureCheckingIfInCache("GFX\map\elev_arrow.png")
	ne\button_arrow[0] = CreateSprite(ne\door\buttons[0])
	ne\button_arrow[1] = CopyEntity(ne\button_arrow[0],ne\door\buttons[0])
	For i = 0 To 1
		ScaleSprite ne\button_arrow[i],10,10
		SpriteViewMode ne\button_arrow[i],2
		EntityTexture ne\button_arrow[i],tex
		EntityFX ne\button_arrow[i],1
	Next
	PositionEntity ne\button_arrow[0],-10,285,0.8
	PositionEntity ne\button_arrow[1],14,285,0
	TurnEntity ne\button_arrow[1],0,0,180
	DeleteSingleTextureEntryFromCache tex
	ne\button_numbers = CreateSprite(ne\door\buttons[0])
	ScaleSprite ne\button_numbers,15,15
	SpriteViewMode ne\button_numbers,2
	PositionEntity ne\button_numbers,2,250,0
	EntityFX ne\button_numbers,1
	
	Return ne
End Function

Function StartNewElevator(door.Doors,newfloor%)
	Local ne.NewElevator, ne_found.NewElevator
	Local playerinside% = False
	
	For ne = Each NewElevator
		If ne\door = door
			ne_found = ne
			Exit
		EndIf
	Next
	
	If ne\door\locked Then
		Msg = GetLocalString("Doors", "door_locked")
		MsgTimer = 70 * 5
	ElseIf ne\floorlocked[newfloor - 1] Then
		Msg = GetLocalString("Doors", "door_nothing")
		MsgTimer = 70 * 5
	Else
		If newfloor <> ne\currfloor
			ne\tofloor = newfloor
			If Abs(EntityX(Collider)-EntityX(ne\obj,True))<=280.0*RoomScale+(0.015*FPSfactor)
				If Abs(EntityZ(Collider)-EntityZ(ne\obj,True))<=280.0*RoomScale+(0.015*FPSfactor)
					DebugLog "In Elevator"
					PlayerInNewElevator = True
					PlayerNewElevator = ne\ID
					playerinside = True
				EndIf
			EndIf
			If (Not playerinside)
				Msg = "You called the elevator."
				MsgTimer = 70 * 5
			Else
				UseDoor(ne\door)
			EndIf
		Else
			Msg = "The elevator is already on this floor."
			MsgTimer = 70 * 7
		EndIf
	EndIf
	
End Function

Function UpdateNewElevators()
	Local ne.NewElevator
	Local n.NPCs,it.Items
	Local i%
	
	For ne = Each NewElevator
		If ne\tofloor <> ne\currfloor Then
			If ne\state < 200.0 Then
				ne\state = ne\state + FPSfactor
			Else
				If ne\currsound=0 Then
					If ne\soundchn <> 0 Then StopStream_Strict(ne\soundchn)
					If (Not PlayerInNewElevator) Then
						ne\soundchn = StreamSound_Strict("SFX\General\Elevator\StartAndLoop.ogg",0,0)
						UpdateStreamSoundOrigin(ne\soundchn,Camera,ne\obj,10,opt\SFXVolume*0.5)
					Else
						ne\soundchn = StreamSound_Strict("SFX\General\Elevator\StartAndLoop.ogg",opt\SFXVolume*0.5,0)
					EndIf
					ne\currsound = 1
				Else
					If ne\currsound = 1 Lor (ne\currsound = 2 And (Not IsStreamPlaying_Strict(ne\soundchn))) Then
						;Just in case that the "StartAndLoop" sound has finished playing upon arrival
						If (Not IsStreamPlaying_Strict(ne\soundchn))
							StopStream_Strict(ne\soundchn)
							ne\soundchn = StreamSound_Strict("SFX\General\Elevator\Loop.ogg",opt\SFXVolume*0.5,Mode)
							ne\currsound = 2
						EndIf
					EndIf
				EndIf
				If ne\tofloor < ne\currfloor Then
					ShowEntity ne\button_arrow[1]
					HideEntity ne\button_arrow[0]
					If ne\currfloor = 3 And ne\tofloor = 1 Then
						If EntityY(ne\obj) < ne\floory[1] Then
							EntityTexture ne\button_numbers,NEI\button_number_tex,1
						Else
							EntityTexture ne\button_numbers,NEI\button_number_tex,2
						EndIf
					Else
						If ne\currfloor = 3 Then
							EntityTexture ne\button_numbers,NEI\button_number_tex,2
						Else
							EntityTexture ne\button_numbers,NEI\button_number_tex,1
						EndIf
					EndIf
					If EntityY(ne\obj) > ne\floory[ne\tofloor-1] Then
						If EntityY(ne\obj) > ne\floory[ne\tofloor-1] + (0.6/RoomScale) Then
							ne\CurrSpeed = CurveValue(ne\speed,ne\CurrSpeed,75.0)
						Else
							ne\CurrSpeed = CurveValue(0.05,ne\CurrSpeed,25.0)
							If ne\currsound = 1 Lor ne\currsound = 2 Then
								StopStream_Strict(ne\soundchn)
								ne\soundchn = StreamSound_Strict("SFX\General\Elevator\Stop.ogg",opt\SFXVolume*0.5,0)
								ne\currsound = 3
							EndIf
						EndIf
						MoveEntity ne\obj,0,-ne\CurrSpeed*FPSfactor,0
						If ne\room = PlayerRoom And PlayerInNewElevator And PlayerNewElevator = ne\ID Then
							TeleportEntity Collider,EntityX(Collider),EntityY(ne\obj,True)+0.3,EntityZ(Collider)
						EndIf
					Else
						PositionEntity ne\obj,EntityX(ne\obj),ne\floory[ne\tofloor-1],EntityZ(ne\obj)
						ne\currfloor = ne\tofloor
						ne\state = FPSfactor
					EndIf
				Else
					ShowEntity ne\button_arrow[0]
					HideEntity ne\button_arrow[1]
					If ne\currfloor = 1 And ne\tofloor = 3 Then
						If EntityY(ne\obj)>ne\floory[1]
							EntityTexture ne\button_numbers,NEI\button_number_tex,1
						Else
							EntityTexture ne\button_numbers,NEI\button_number_tex,0
						EndIf
					Else
						If ne\currfloor = 1 Then
							EntityTexture ne\button_numbers,NEI\button_number_tex,0
						Else
							EntityTexture ne\button_numbers,NEI\button_number_tex,1
						EndIf
					EndIf
					If EntityY(ne\obj) < ne\floory[ne\tofloor-1] Then
						If EntityY(ne\obj) < ne\floory[ne\tofloor-1] - (0.6/RoomScale) Then
							ne\CurrSpeed = CurveValue(ne\speed,ne\CurrSpeed,75.0)
						Else
							ne\CurrSpeed = CurveValue(0.05,ne\CurrSpeed,25.0)
							If ne\currsound = 1 Lor ne\currsound = 2 Then
								StopStream_Strict(ne\soundchn)
								ne\soundchn = StreamSound_Strict("SFX\General\Elevator\Stop.ogg",opt\SFXVolume*0.5,0)
								ne\currsound = 3
							EndIf
						EndIf
						MoveEntity ne\obj,0,ne\CurrSpeed*FPSfactor,0
						If ne\room = PlayerRoom And PlayerInNewElevator And PlayerNewElevator = ne\ID Then
							TeleportEntity Collider,EntityX(Collider),EntityY(ne\obj,True)+0.3,EntityZ(Collider)
						EndIf
					Else
						PositionEntity ne\obj,EntityX(ne\obj),ne\floory[ne\tofloor-1],EntityZ(ne\obj)
						ne\currfloor = ne\tofloor
						ne\state = FPSfactor
					EndIf
				EndIf
				If ne\room = PlayerRoom And PlayerInNewElevator And PlayerNewElevator = ne\ID Then
					PositionEntity ne\door\frameobj,EntityX(ne\door\frameobj),EntityY(ne\obj,True),EntityZ(ne\door\frameobj)
					PositionEntity ne\door\obj,EntityX(ne\door\obj),EntityY(ne\obj,True),EntityZ(ne\door\obj)
					PositionEntity ne\door\obj2,EntityX(ne\door\obj2),EntityY(ne\obj,True),EntityZ(ne\door\obj2)
					PositionEntity ne\door\buttons[0],EntityX(ne\door\buttons[0]),EntityY(ne\obj,True)+0.6,EntityZ(ne\door\buttons[0])
					PositionEntity ne\door\buttons[1],EntityX(ne\door\buttons[1]),EntityY(ne\obj,True)+0.7,EntityZ(ne\door\buttons[1])
					PositionEntity Collider,EntityX(Collider),EntityY(ne\obj,True)+0.3,EntityZ(Collider)
					DropSpeed = 0
					CameraShake = Sin(Abs(ne\CurrSpeed*15)/3.0)*0.5
				EndIf
				For n.NPCs = Each NPCs
					If Abs(EntityX(n\Collider)-EntityX(ne\obj,True)) < 280.0*RoomScale+(0.015*FPSfactor) Then
						If Abs(EntityZ(n\Collider)-EntityZ(ne\obj,True)) < 280.0*RoomScale+(0.015*FPSfactor) Then
							PositionEntity n\Collider,EntityX(n\Collider),EntityY(ne\obj,True)+n\CollRadius,EntityZ(n\Collider)
							n\DropSpeed = 0
						EndIf
					EndIf
				Next
				For it.Items = Each Items
					If Abs(EntityX(it\collider)-EntityX(ne\obj,True)) < 280.0*RoomScale+(0.015*FPSfactor) Then
						If Abs(EntityZ(it\collider)-EntityZ(ne\obj,True)) < 280.0*RoomScale+(0.015*FPSfactor) Then
							PositionEntity it\collider,EntityX(it\collider),EntityY(ne\obj,True)+0.01,EntityZ(it\collider)
							it\DropSpeed = 0
						EndIf
					EndIf
				Next
			EndIf
		Else
			ne\CurrSpeed = 0.0
			ne\currsound = 0
			For i = 0 To 1
				HideEntity ne\button_arrow[i]
			Next
			Select ne\currfloor
				Case 1
					EntityTexture ne\button_numbers,NEI\button_number_tex,0
				Case 2
					EntityTexture ne\button_numbers,NEI\button_number_tex,1
				Case 3
					EntityTexture ne\button_numbers,NEI\button_number_tex,2
			End Select
			If ne\state > 0.0 And ne\state < 100.0 Then
				ne\state = ne\state + FPSfactor
			ElseIf ne\state >= 100.0 Then
				UseDoor(ne\door)
				PlayerInNewElevator = False
				ne\state = 0.0
			EndIf
		EndIf
		If PlayerInNewElevator Then
			SetStreamVolume_Strict(ne\soundchn,opt\SFXVolume*0.5)
		Else
			UpdateStreamSoundOrigin(ne\soundchn,Camera,ne\obj)
		EndIf
	Next
	
End Function

Function ResetNewElevator(ne.NewElevator, default_floor% = 1)
	If ne <> Null Then
		If PlayerNewElevator = ne\ID Then
			PlayerNewElevator = 0
			PlayerInNewElevator = False
		EndIf
		ne\state = 0.0
		ne\currfloor = default_floor
		ne\tofloor = default_floor
		ne\door\open = True
		PositionEntity ne\obj,EntityX(ne\obj),ne\floory[default_floor-1],EntityZ(ne\obj)
		PositionEntity ne\door\frameobj,EntityX(ne\door\frameobj),EntityY(ne\obj,True),EntityZ(ne\door\frameobj)
		PositionEntity ne\door\obj,EntityX(ne\door\obj),EntityY(ne\obj,True),EntityZ(ne\door\obj)
		PositionEntity ne\door\obj2,EntityX(ne\door\obj2),EntityY(ne\obj,True),EntityZ(ne\door\obj2)
		PositionEntity ne\door\buttons[0],EntityX(ne\door\buttons[0]),EntityY(ne\obj,True)+0.6,EntityZ(ne\door\buttons[0])
		PositionEntity ne\door\buttons[1],EntityX(ne\door\buttons[1]),EntityY(ne\obj,True)+0.7,EntityZ(ne\door\buttons[1])
		StopStream_Strict(ne\soundchn)
	EndIf
End Function

Function DeleteNewElevators()
	Local ne.NewElevator
	
	For ne = Each NewElevator
		ne\obj = FreeEntity_Strict(ne\obj)
		StopStream_Strict(ne\soundchn)
		ne\soundchn = 0
		ne\sound = 0
		Delete ne
	Next
	
End Function

Function UpdateSmallHeadMode()
	Local n.NPCs, bonename$, bone%
	
	For n = Each NPCs
		bonename$ = GetNPCManipulationValue(n\NPCNameInSection,"head","bonename",0)
		If bonename$<>""
			bone% = FindChild(n\obj,bonename$)
			ScaleEntity bone%,0.5,0.5,0.5
		EndIf
		;That's currently an alternative, I will probably add those NPCs in the "NPCBones.ini" config file so it will be easier to mod - ENDSHN
		If n\NPCtype = NPCtype049 Then
			bone% = FindChild(n\obj, "Bone_019")
			ScaleEntity bone%,0.5,0.5,0.5
		ElseIf n\NPCtype = NPCtypeOldMan Then
			bone% = FindChild(n\obj, "Bone_022")
			ScaleEntity bone%,0.5,0.5,0.5
		ElseIf n\NPCtype = NPCtypeClerk Then
			bone% = FindChild(n\obj, "Bip01_Head")
			ScaleEntity bone%,0.5,0.5,0.5
		EndIf
	Next
	
End Function

Function ApplyBumpMap(texture%)
	
	TextureBlend texture%,6
	TextureBumpEnvMat texture%,0,0,-0.012
	TextureBumpEnvMat texture%,0,1,-0.012
	TextureBumpEnvMat texture%,1,0,0.012
	TextureBumpEnvMat texture%,1,1,0.012
	TextureBumpEnvOffset texture%,0.5
	TextureBumpEnvScale texture%,1.0
	
End Function

Function PointEntity2(source_ent%,dest_ent%,roll#=0.0,usepitch%=True,useyaw%=True)
	Local pitch#,yaw#
	
	pitch# = EntityPitch(source_ent%)
	If usepitch%
		pitch# = pitch# + DeltaPitch(source_ent%,dest_ent%)
	EndIf
	yaw# = EntityYaw(source_ent%)
	If useyaw%
		yaw# = yaw# + DeltaYaw(source_ent%,dest_ent%)
	EndIf
	RotateEntity source_ent%,pitch#,yaw#,roll#
	
End Function

Type HitBox
	Field HitBox1[25]
	Field HitBox2[25]
	Field HitBox3[25]
	Field BoneName$[25]
	Field HitBoxPosX#[25]
	Field HitBoxPosY#[25]
	Field HitBoxPosZ#[25]
	Field NPCtype%
	Field ID%
End Type

Function ApplyHitBoxes.HitBox(npctype,npcname$)
	Local hb.HitBox = New HitBox
	
	hb\NPCtype = npctype
	
	Local i%,htype%,bonename$;,bone%
	Local scaleX#,scaleY#,scaleZ#,posX#,posY#,posZ#
	Local file$ = "Data\NPCBones.ini"
	
	;If NTF_GameModeFlag=3 And mp_I\PlayState=GAME_CLIENT Then Return
	
	For i = 0 To GetINIInt(file$,npcname$,"hitbox_amount")-1
		htype% = GetINIInt(file$,npcname$,"hitbox"+(i+1)+"_type")
		bonename$ = GetINIString(file$,npcname$,"hitbox"+(i+1)+"_parent")
		hb\BoneName[i] = bonename
		;bone% = FindChild(n\obj,bonename$)
		;If bone% = 0 Then RuntimeError "Error applying hitbox: Bone "+bonename$+" not found for npc "+npcname$
		If htype = 0
			hb\HitBox1[i] = CreateCube()
			scaleX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleX",1.0)
			scaleY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleY",1.0)
			scaleZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleZ",1.0)
			posX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posX",0.0)
			posY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posY",0.0)
			posZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posZ",0.0)
			ScaleEntity hb\HitBox1[i],scaleX,scaleY,scaleZ
			PositionEntity hb\HitBox1[i],posX,posY,posZ
			EntityPickMode hb\HitBox1[i],2
			EntityAlpha hb\HitBox1[i],0.0
			HideEntity hb\HitBox1[i]
		ElseIf htype = 1
			hb\HitBox2[i] = CreateCube()
			scaleX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleX",1.0)
			scaleY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleY",1.0)
			scaleZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleZ",1.0)
			posX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posX",0.0)
			posY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posY",0.0)
			posZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posZ",0.0)
			ScaleEntity hb\HitBox2[i],scaleX,scaleY,scaleZ
			PositionEntity hb\HitBox2[i],posX,posY,posZ
			EntityPickMode hb\HitBox2[i],2
			EntityAlpha hb\HitBox2[i],0.0
			HideEntity hb\HitBox2[i]
		Else
			hb\HitBox3[i] = CreateCube()
			scaleX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleX",1.0)
			scaleY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleY",1.0)
			scaleZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_scaleZ",1.0)
			posX# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posX",0.0)
			posY# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posY",0.0)
			posZ# = GetINIFloat(file$,npcname$,"hitbox"+(i+1)+"_posZ",0.0)
			ScaleEntity hb\HitBox3[i],scaleX,scaleY,scaleZ
			PositionEntity hb\HitBox3[i],posX,posY,posZ
			EntityPickMode hb\HitBox3[i],2
			EntityAlpha hb\HitBox3[i],0.0
			HideEntity hb\HitBox3[i]
		EndIf
		hb\HitBoxPosX[i]=posX
		hb\HitBoxPosY[i]=posY
		hb\HitBoxPosZ[i]=posZ
	Next
	
	Return hb
End Function

Function CopyHitBoxes(n.NPCs)
	Local hb.HitBox,bone%,i%
	
	For hb = Each HitBox
		If hb\NPCtype = n\NPCtype Then
			For i = 0 To 24
				If hb\BoneName[i]<>"" Then
					n\BoneName[i]=hb\BoneName[i]
					bone = FindChild(n\obj,n\BoneName[i])
				EndIf
				If hb\HitBox1[i]<>0 Then
					n\HitBox1[i] = CopyEntity(hb\HitBox1[i],bone)
					PositionEntity n\HitBox1[i],hb\HitBoxPosX[i],hb\HitBoxPosY[i],hb\HitBoxPosZ[i]
				EndIf
				If hb\HitBox2[i]<>0 Then
					n\HitBox2[i] = CopyEntity(hb\HitBox2[i],bone)
					PositionEntity n\HitBox2[i],hb\HitBoxPosX[i],hb\HitBoxPosY[i],hb\HitBoxPosZ[i]
				EndIf
				If hb\HitBox3[i]<>0 Then
					n\HitBox3[i] = CopyEntity(hb\HitBox3[i],bone)
					PositionEntity n\HitBox3[i],hb\HitBoxPosX[i],hb\HitBoxPosY[i],hb\HitBoxPosZ[i]
				EndIf
			Next
			Exit
		EndIf
	Next
	
End Function

Function HideNPCHitBoxes(n.NPCs)
	Local i%
	
	For i = 0 To 24
		If n\HitBox1[i]<>0 Then HideEntity n\HitBox1[i]
		If n\HitBox2[i]<>0 Then HideEntity n\HitBox2[i]
		If n\HitBox3[i]<>0 Then HideEntity n\HitBox3[i]
	Next
	
End Function

Function ShowNPCHitBoxes(n.NPCs)
	Local i%
	
	For i = 0 To 24
		If n\HitBox1[i]<>0 Then ShowEntity n\HitBox1[i]
		If n\HitBox2[i]<>0 Then ShowEntity n\HitBox2[i]
		If n\HitBox3[i]<>0 Then ShowEntity n\HitBox3[i]
	Next
	
End Function

Function FreeNPCHitBoxes(n.NPCs)
	Local i%
	
	For i = 0 To 24
		n\HitBox1[i] = FreeEntity_Strict(n\HitBox1[i])
		n\HitBox2[i] = FreeEntity_Strict(n\HitBox2[i])
		n\HitBox3[i] = FreeEntity_Strict(n\HitBox3[i])
	Next
	
End Function

Function GetMeshExtents2(mesh) ; A better variant than the GetMeshExtents, as this also works for models that are rotated
	Local xmax#=-1000000
	Local xmin#= 1000000
	Local ymax#=-1000000
	Local ymin#= 1000000
	Local zmax#=-1000000
	Local zmin#= 1000000
	Local su,s,i,x#,y#,z#
	For su=1 To CountSurfaces(mesh)
		s=GetSurface(mesh,su)
		For i=0 To CountVertices(s)-1
			x#=VertexX(s,i)
			y#=VertexY(s,i)
			z#=VertexZ(s,i)
			TFormPoint x,y,z,mesh,0
			x=TFormedX()
			y=TFormedY()
			z=TFormedZ()
			If x>xmax Then xmax=x
			If x<xmin Then xmin=x
			If y>ymax Then ymax=y
			If y<ymin Then ymin=y
			If z>zmax Then zmax=z
			If z<zmin Then zmin=z
		Next
	Next
	
	Mesh_MinX = xmin
	Mesh_MinY = ymin
	Mesh_MinZ = zmin
	Mesh_MaxX = xmax
	Mesh_MaxY = ymax
	Mesh_MaxZ = zmax
	Mesh_MagX = xmax-xmin
	Mesh_MagY = ymax-ymin
	Mesh_MagZ = zmax-zmin
	
End Function

Function TurnIntoSeconds(number#)
	
	Return Ceil(Int(number)/70.0)
	
End Function

;Currently not ready!
Function GetAnimationSequences(n.NPCs,npcname$)
	Local i%
	Local animstart%,animstop%
	Local file$ = "Data\NPCBones.ini"
	
	For i = 1 To GetINIInt(file$,npcname$,"anim_amount")
		animstart = GetINIInt(file$,npcname$,"anim"+i+"_start")
		animstop = GetINIInt(file$,npcname$,"anim"+i+"_stop")
		ExtractAnimSeq(n\obj,animstart,animstop,0)
	Next
	
End Function

Function GetAnimationSpeed(n.NPCs,npcname$,currsequence%)
	Local file$ = "Data\NPCBones.ini"
	
	Return GetINIFloat(file$,npcname$,"anim"+currsequence%+"_speed",0.5)
End Function

Function ApplyAnimation(n.NPCs,sequence%,speed#,animmode%=1) ;Only works for animations with consistent speed - ENDSHN
	
	If n\CurrAnimSeq<>sequence%
		Animate(n\obj,animmode%,speed#,sequence%,5)
		n\CurrAnimSeq = sequence
	EndIf
	n\Frame = AnimTime(n\obj)
	DebugLog n\Frame
	
End Function

Global ShouldUpdateWater$ = ""
Global WaterRender_IgnoreObject%

Type Water
	Field obj%
	Field VexX#[256]
	Field VexY#[256]
	Field VexZ#[256]
	Field PrevVexY#[256]
	Field name$
	Field timer#
	Field isrendering%
	Field customY#
End Type

Function CreateWater.Water(filepath$,name$,x#,y#,z#,parent%=0,customY#=0.0)
	Local wa.Water = New Water
	
	wa\obj = LoadMesh_Strict(filepath,parent)
	PositionEntity wa\obj,x,y,z
	wa\name = name$
	Local surf=GetSurface(wa\obj,1)
	Local i%
	For i=0 To CountVertices(surf)-1
		wa\VexX#[i]=VertexX#(surf,i)
		wa\VexY#[i]=VertexY#(surf,i)
		wa\VexZ#[i]=VertexZ#(surf,i)
		wa\PrevVexY#[i]=wa\VexY#[i]
	Next
	EntityTexture wa\obj,MapCubeMap\Texture,0,1
	wa\customY=customY
	
	Return wa
End Function

Function UpdateWater(name$)
	Local wa.Water,it.Items
	
	For wa = Each Water
		wa\isrendering=False
		If wa\name = name
			wa\isrendering=True
			wa\timer=wa\timer+2*FPSfactor
			UpdateCubeMap(wa\obj,"MapCubeMap")
		EndIf
	Next
	
End Function

Function RenderWater(name$)
	Local wa.Water,it.Items
	Local i,s
	
	For wa = Each Water
		If wa\name = name Then
			For it = Each Items
				If EntityY(it\collider)<wa\customY Then
					HideEntity(it\model)
				EndIf
			Next
			If WaterRender_IgnoreObject<>0 Then
				HideEntity WaterRender_IgnoreObject
			EndIf
			MapCubeMap\RenderY = wa\customY
			s=GetSurface(wa\obj,1)
			For i=0 To CountVertices(s)-1
				wa\VexY[i]=Sin(wa\timer+wa\VexX[i]*500+wa\VexZ[i]*300)*5.0 ;2.5
				VertexCoords s,i,wa\VexX[i],wa\PrevVexY[i]-wa\VexY[i],wa\VexZ[i]
			Next
			UpdateNormals wa\obj
			RenderCubeMap(wa\obj,"MapCubeMap")
			If WaterRender_IgnoreObject<>0
				ShowEntity WaterRender_IgnoreObject
			EndIf
			For it = Each Items
				If EntityY(it\collider)<wa\customY Then
					ShowEntity(it\model)
				EndIf
			Next
		EndIf
	Next
	
End Function

Function SZL_LoadEntitiesForZone()
	Local i%
	
	ApacheObj = FreeEntity_Strict(ApacheObj)
	ApacheRotorObj = FreeEntity_Strict(ApacheRotorObj)
	
	Room2slCam = FreeEntity_Strict(Room2slCam)
	
	N966Obj = FreeEntity_Strict(N966Obj)
	
	MonitorTexture = LoadTexture_Strict("GFX\MonitorOverlay.jpg",1,1)
	SelectedElevatorFloor = 0
	Select NTF_CurrZone
		Case 0
			ApacheObj = LoadAnimMesh_Strict("GFX\apache.b3d")
			ApacheRotorObj = LoadAnimMesh_Strict("GFX\apacherotor.b3d")
		Case 1
			Room2slCam = CreateCamera()
			CameraViewport(Room2slCam, 0, 0, 128, 128)
			CameraRange Room2slCam, 0.05, 6.0
			CameraZoom(Room2slCam, 0.8)
			HideEntity(Room2slCam)
		Case 2
			;ElevatorDoorObj = LoadMesh_Strict("Unused\GFX\liftdoors.b3d")
			;HideEntity ElevatorDoorObj
			N966Obj = LoadAnimMesh_Strict("GFX\npcs\scp-966.b3d")
			HideEntity N966Obj
	End Select
	
	SelectedElevatorEvent = 0
	
	If ApacheObj<>0
		HideEntity ApacheObj
		HideEntity ApacheRotorObj
	EndIf
	
End Function

Function DeInitZoneEntities()
	
	Room2slCam = 0
	ApacheObj = 0
	N966Obj = 0
	
End Function

;Controller Functions (mostly programmed for the PS4-Controller)
Function GetLeftAnalogStickPitch#(onlydir%=False,invert%=True)
	
	If (Not co\Enabled) Then Return
	If Abs(JoyY())<0.15 And (Not onlydir) Then Return
	If onlydir
		If invert
			Return -JoyYDir()
		EndIf
		Return JoyYDir()
	EndIf
	If invert Then Return -JoyY()
	Return JoyY()
	
End Function

Function GetLeftAnalogStickYaw#(onlydir%=False,invert%=False)
	
	If (Not co\Enabled) Then Return
	If Abs(JoyX())<0.15 And (Not onlydir) Then Return
	If onlydir
		If invert
			Return -JoyXDir()
		EndIf
		Return JoyXDir()
	EndIf
	If invert Then Return -JoyX()
	Return JoyX()
	
End Function

Function GetRightAnalogStickPitch#(onlydir%=False,invert%=True)
	
	If (Not co\Enabled) Then Return
	If Abs(JoyRoll()/180.0)<0.15 Then Return
	If co\InvertAxis[Controller_YAxis] Then invert = Not invert
	If onlydir
		If invert
			Return -Sgn(JoyRoll()/180.0)
		EndIf
		Return Sgn(JoyRoll()/180.0)
	EndIf
	If invert Then Return -(JoyRoll()/180.0)
	Return JoyRoll()/180.0
	
End Function

Function GetRightAnalogStickYaw#(onlydir%=False,invert%=False)
	
	If (Not co\Enabled) Then Return
	If Abs(JoyZ())<0.15 And (Not onlydir) Then Return
	If onlydir
		If invert
			Return -JoyZDir()
		EndIf
		Return JoyZDir()
	EndIf
	If invert Then Return -JoyZ()
	Return JoyZ()
	
End Function

Function InteractHit(key%,controllerkey%)
	
	If (Not co\Enabled)
		If KeyHit(key%) Then Return True
	Else
		If JoyHit(controllerkey%) Then Return True
	EndIf
	Return False
	
End Function

Function GetDPadButtonPress()
	
	If (Not co\Enabled) Then Return
	Return JoyHat()
	
End Function

Function UpdateMenuControllerSelection(maxbuttons%,currTab%,system%=0,maxcurrbuttons%=1)
	
	If (Not co\Enabled) Then Return
	Select system
		Case 0,2,3
			If co\WaitTimer = 0.0
				If system <> 3
					If GetDPadButtonPress()=0
						co\CurrButton[currTab] = co\CurrButton[currTab] - 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButton[currTab] < 0
							co\CurrButton[currTab] = maxbuttons-1
						EndIf
					ElseIf GetDPadButtonPress()=180
						co\CurrButton[currTab] = co\CurrButton[currTab] + 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButton[currTab] > maxbuttons-1
							co\CurrButton[currTab] = 0
						EndIf
					EndIf
					
					If GetLeftAnalogStickPitch(True) > 0.0
						co\CurrButton[currTab] = co\CurrButton[currTab] - 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButton[currTab] < 0
							co\CurrButton[currTab] = maxbuttons-1
						EndIf
					ElseIf GetLeftAnalogStickPitch(True) < 0.0
						co\CurrButton[currTab] = co\CurrButton[currTab] + 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButton[currTab] > maxbuttons-1
							co\CurrButton[currTab] = 0
						EndIf
					EndIf
				EndIf
				
				If system = 2 Lor system = 3
					If GetDPadButtonPress()=270
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] - 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] < 0
							co\CurrButtonSub[currTab] = maxcurrbuttons-1
						EndIf
					ElseIf GetDPadButtonPress()=90
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] + 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] > maxcurrbuttons-1
							co\CurrButtonSub[currTab] = 0
						EndIf
					EndIf
					
					If GetLeftAnalogStickYaw(True) > 0.0
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] + 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] > maxcurrbuttons-1
							co\CurrButtonSub[currTab] = 0
						EndIf
					ElseIf GetLeftAnalogStickYaw(True) < 0.0
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] - 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] < 0
							co\CurrButtonSub[currTab] = maxcurrbuttons-1
						EndIf
					EndIf
					
					If co\PressedPrev
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] - 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] < 0
							co\CurrButtonSub[currTab] = maxcurrbuttons-1
						EndIf
					EndIf
					If co\PressedNext
						co\CurrButtonSub[currTab] = co\CurrButtonSub[currTab] + 1
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
						If co\CurrButtonSub[currTab] > maxcurrbuttons-1
							co\CurrButtonSub[currTab] = 0
						EndIf
					EndIf
				;Else
				;	co\CurrButtonSub[currTab] = 0
				EndIf
			EndIf
		Case 1
			If co\WaitTimer = 0.0
				If GetDPadButtonPress()=0
					If co\ScrollBarY > 0
						co\ScrollBarY = Max(co\ScrollBarY-0.05,0)
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
					Else
						co\ScrollBarY = 0
					EndIf
				ElseIf GetDPadButtonPress()=180
					If co\ScrollBarY < 1
						co\ScrollBarY = Min(co\ScrollBarY+0.05,1.0)
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
					Else
						co\ScrollBarY = 1
					EndIf
				EndIf
				
				If GetLeftAnalogStickPitch(True) > 0.0
					If co\ScrollBarY > 0
						co\ScrollBarY = Max(co\ScrollBarY-0.05,0)
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
					Else
						co\ScrollBarY = 0
					EndIf
				ElseIf GetLeftAnalogStickPitch(True) < 0.0
					If co\ScrollBarY < 1
						co\ScrollBarY = Min(co\ScrollBarY+0.05,1.0)
						PlaySound_Strict co\SelectSFX
						co\WaitTimer = FPSfactor2
					Else
						co\ScrollBarY = 1
					EndIf
				EndIf
			EndIf
			
			co\CurrButtonSub[currTab] = 0
			ScrollBarY = CurveValue(co\ScrollBarY,ScrollBarY,20.0)
	End Select
	
	If co\WaitTimer > 0.0 And co\WaitTimer < 15.0
		co\WaitTimer = co\WaitTimer + FPSfactor2
	ElseIf co\WaitTimer >= 15.0
		co\WaitTimer = 0.0
	EndIf
	
End Function

Function MouseAndControllerSelectBox(x%,y%,width%,height%,currButton%=-1,currButtonTab%=0,currButtonSub%=0)
	
	If (Not co\Enabled)
		If MouseOn(x,y,width,height)
			Return True
		EndIf
	Else
		If co\CurrButton[currButtonTab]=currButton
			If co\CurrButtonSub[currButtonTab]=currButtonSub
				Return True
			EndIf
		EndIf
	EndIf
	Return False
	
End Function

Function UpdateControllerSideSelection#(value#,minvalue#,maxvalue#,valuestep#=2.0)
	
	If (Not co\Enabled) Then Return
	If co\WaitTimer=0
		If GetDPadButtonPress()=270
			If value# > minvalue#
				value# = Max(value#-valuestep#,minvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = minvalue#
			EndIf
		ElseIf GetDPadButtonPress()=90
			If value# < maxvalue#
				value# = Min(value#+valuestep#,maxvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = maxvalue#
			EndIf
		EndIf
		
		If GetLeftAnalogStickYaw(True) > 0.0
			If value# < maxvalue#
				value# = Min(value#+valuestep#,maxvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = maxvalue#
			EndIf
		ElseIf GetLeftAnalogStickYaw(True) < 0.0
			If value# > minvalue#
				value# = Max(value#-valuestep#,minvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = minvalue#
			EndIf
		EndIf
		
		If co\PressedNext
			If value# < maxvalue#
				value# = Min(value#+valuestep#,maxvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = maxvalue#
			EndIf
		EndIf
		If co\PressedPrev
			If value# > minvalue#
				value# = Max(value#-valuestep#,minvalue#)
				PlaySound_Strict co\SelectSFX
				co\WaitTimer = FPSfactor2
			Else
				value# = minvalue#
			EndIf
		EndIf
	EndIf
	
	Return value#
	
End Function

Function ResetControllerSelections()
	Local i%
	
	co\WaitTimer# = 0.0
	co\PressedButton% = False
	For i = 0 To Controller_MaxButtons-1
		co\CurrButton[i] = 0
		co\CurrButtonSub[i] = 0
	Next
	co\ScrollBarY# = 0.0
	co\PressedNext = 0
	co\PressedPrev = 0
	co\KeyPad_CurrButton% = 0
	
End Function

;Flu-Light constants, type and functions
Const MaxFluTextures=3
Const FluState_Off=0
Const FluState_Between=1
Const FluState_On=2
Const MaxFluSounds=7
Const FLU_STATE_OFF=0
Const FLU_STATE_ON=1
Const FLU_STATE_FLICKER=2

Type TempFluLight
	Field position.Vector3D
	Field rotation.Vector3D
	Field roomtemplate.RoomTemplates
	Field id%
End Type

Type FluLight
	Field id%
	Field obj%
	Field tex%[MaxFluTextures]
	Field time#
	Field sfx%[MaxFluSounds]
	Field flashsprite%
	Field lightobj%
	Field room.Rooms
	Field state%
End Type

Function CreateFluLight.FluLight(id%)
	Local fll.FluLight = New FluLight
	Local fll2.FluLight
	Local i
	
	fll\id = id
	For fll2 = Each FluLight
		If fll2 <> fll Then
			EntityParent fll2\flashsprite,0
			EntityParent fll2\lightobj,0
			fll\obj = CopyEntity(fll2\obj)
			EntityParent fll2\flashsprite,fll2\obj
			EntityParent fll2\lightobj,fll2\obj
			For i = 0 To MaxFluTextures-1
				fll\tex[i] = fll2\tex[i]
			Next
			For i = 0 To MaxFluSounds-1
				fll\sfx[i] = fll2\sfx[i]
			Next
			Exit
		EndIf
	Next
	
	If fll\obj=0 Then
		fll\obj = LoadMesh_Strict("GFX\map\Props\light_flu.x")
	EndIf
	ScaleEntity fll\obj,RoomScale,RoomScale,RoomScale
	
	If fll\tex[FluState_Off]=0 Then
		For i = 0 To MaxFluTextures-1
			;TODO: Make it use the texture cache system
			fll\tex[i] = LoadTexture("GFX\map\textures\light_flu"+(i+1)+".jpg",1)
		Next
	EndIf
	EntityTexture fll\obj,fll\tex[FluState_Off]
	HideEntity fll\obj
	
	If fll\sfx[0]=0 Then
		For i = 0 To MaxFluSounds-1
			fll\sfx[i] = LoadSound_Strict("SFX\Room\FluLight"+(i+1)+".ogg")
		Next
	EndIf
	
	fll\flashsprite = CreateSprite()
	Local tex = LoadTexture_Strict("GFX\particle2.png",1+2)
	SpriteViewMode fll\flashsprite,2
	ScaleSprite fll\flashsprite,1.0,1.0
	EntityFX fll\flashsprite,1
	EntityBlend fll\flashsprite,3
	RotateEntity fll\flashsprite,-90,0,0
	;PositionEntity fll\flashsprite,0,-0.07,0,True
	;EntityParent fll\flashsprite,fll\obj
	EntityTexture fll\flashsprite,tex
	DeleteSingleTextureEntryFromCache tex
	HideEntity fll\flashsprite
	
	fll\lightobj = CreateLight(2) ;fll\flashsprite
	LightColor fll\lightobj,1275,1275,1275
	LightRange fll\lightobj,0.025
	
	Return fll
End Function

Function UpdateFluLights()
	Local fll.FluLight
	
	For fll = Each FluLight
		If fll\room = PlayerRoom Lor IsRoomAdjacent(fll\room,PlayerRoom) Then
			ShowEntity fll\obj
			Select fll\state
				Case FLU_STATE_OFF
					EntityFX fll\obj,0
					HideEntity fll\flashsprite
					HideEntity fll\lightobj
					EntityTexture fll\obj,fll\tex[FluState_Off]
				Case FLU_STATE_ON
					EntityFX fll\obj,1
					ShowEntity fll\flashsprite
					ShowEntity fll\lightobj
					EntityTexture fll\obj,fll\tex[FluState_On]
				Case FLU_STATE_FLICKER
					If fll\time = 0.0 Then
						EntityFX fll\obj,0
						HideEntity fll\flashsprite
						HideEntity fll\lightobj
						EntityTexture fll\obj,fll\tex[FluState_Off]
						If Rand(100)=1 Then
							fll\time = FPSfactor
							PlaySound2(fll\sfx[Rand(0,MaxFluSounds-4)],Camera,fll\obj)
						EndIf
					ElseIf fll\time > 0.0 Then
						EntityFX fll\obj,0
						HideEntity fll\flashsprite
						HideEntity fll\lightobj
						EntityTexture fll\obj,fll\tex[FluState_Between]
						fll\time = fll\time + FPSfactor
						If fll\time > 70*Rnd(1.0,3.0) Then
							fll\time = -70*0.2
							PlaySound2(fll\sfx[Rand(4,MaxFluSounds-1)],Camera,fll\obj)
						EndIf
					Else
						EntityFX fll\obj,1
						ShowEntity fll\flashsprite
						ShowEntity fll\lightobj
						EntityTexture fll\obj,fll\tex[FluState_On]
						fll\time = Min(fll\time + FPSfactor,0.0)
					EndIf
			End Select
		Else
			HideEntity fll\obj
		EndIf
	Next
	
End Function

Function InitFluLight(ID%,state%,room.Rooms)
	Local fll.FluLight
	
	For fll = Each FluLight
		If fll\room = room Then
			If fll\id = ID Then
				fll\state = state
			EndIf
		EndIf
	Next
	
End Function

Function TextWithAlign%(x%, y%, txt$, xAlign% = 0, yAlign% = 0)
	;0 = Left/Top
	;1 = Center
	;2 = Right/Bottom
	
	Text x-(StringWidth(txt)*(xAlign=2)), y-(StringHeight(txt)*(yAlign=2)), txt, (xAlign=1), (yAlign=1)
	
End Function

Function MaskTexture(texture%, red%, green%, blue%) 
	Local x%,y%,pixel%
	Local maskColor% = (red Shl 16) Or (green Shl 8) Or blue 
	Local maskSizeX% = TextureWidth(texture) 
	Local maskSizeY% = TextureHeight(texture) 
	Local maskBuffer% = TextureBuffer(texture) 
	LockBuffer(maskBuffer) 
	For x = 0 To maskSizeX-1 
		For y = 0 To maskSizeY-1 
			pixel = ReadPixel(x, y, maskBuffer) And $00FFFFFF 
			If (pixel = maskColor) Then 
				WritePixel(x, y, pixel, maskBuffer) 
			Else 
				WritePixel(x, y, pixel Or $FF000000, maskBuffer) 
			EndIf 
		Next 
	Next 
	UnlockBuffer(maskBuffer) 
End Function






;~IDEal Editor Parameters:
;~F#3D7#937
;~C#Blitz3D