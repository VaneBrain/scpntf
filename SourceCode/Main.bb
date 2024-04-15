
Include "SourceCode\Math.bb"

Type FixedTimesteps
	Field tickDuration#
	Field accumulator#
	Field prevTime%
	Field currTime%
	Field fps%
	Field tempfps%
	Field fpsgoal%
	Field DeltaTime%
End Type

Function SetTickrate(tickrate%)
	ft\tickDuration = 70.0/Float(tickrate)
End Function

Function AddToTimingAccumulator(milliseconds%)
	
	If (milliseconds<1 Lor milliseconds>500) Then
		Return
	EndIf
	ft\accumulator = ft\accumulator+Max(0,Float(milliseconds)*70.0/1000.0)
	
End Function

Function ResetTimingAccumulator()
	
	ft\accumulator = 0.0
	
End Function

Function SetCurrTime(time%)
	
	ft\currTime = time%
	
End Function

Function SetPrevTime(time%)
	
	ft\prevTime = time%
	
End Function

Function GetCurrTime%()
	Return ft\currTime
End Function

Function GetPrevTime%()
	Return ft\prevTime
End Function

Function GetTickDuration#()
	Return ft\tickDuration
End Function

SetTickrate(60)

Type Loc
	Field Lang$
	Field LangPath$
	Field Localized%
End Type

Global I_Loc.Loc = New Loc

Type LocalString
	Field section$
	Field parameter$
	Field value$
End Type

Function UpdateLang(Lang$)
	If I_Loc\LangPath <> "" Then ;Only need to delete local and fonts, because this line is only ever called twice in the launcher
		DeleteINIFile(I_Loc\LangPath + "Data\local.ini")
	EndIf
	If Lang = "English" Then
		I_Loc\Lang = ""
		I_Loc\LangPath = ""
		I_Loc\Localized = False
	Else
		I_Loc\Lang = Lang
		I_Loc\LangPath = "Localization\" + Lang + "\"
		I_Loc\Localized = True
	EndIf
	Delete Each LocalString
	;These are the strings to be cached in order to allow for better framerates.
	;Order is important, first created is fastest to access.
	; TODO SetLocalString("Messages", "savecantloc")
	InitFonts()
End Function

;UpdateLang(GetINIString(gv\OptionFile, "options", "pack", "English"))
UpdateLang(Steam_GetCurrentGameLang())

Function SetLocalString(Section$, Parameter$)
	Local l.LocalString = New LocalString
	l\value = GetLocalString(Section, Parameter) ;need to set the value first, otherwise it is being set to itself
	l\section = Section
	l\parameter = Parameter
End Function

;Returns localized version of a String, if no translation exists, use English
Function GetLocalString$(Section$, Parameter$)
	
	Local l.LocalString
	For l.LocalString = Each LocalString
		If l\section = Section And l\parameter = Parameter Then
			Return l\value
		EndIf
	Next
	; TODO Find out all occassions where this is called every frame
	;CreateConsoleMsg("Called " + Section + Parameter)
	
	Local temp$
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + "Data\local.ini") = 1 Then
		temp=GetINIString(I_Loc\LangPath + "Data\local.ini", Section, Parameter)
		If temp <> "" Then
			l.LocalString = New LocalString
			l\section = Section
			l\parameter = Parameter
			l\value = temp
			Return temp
		EndIf
	EndIf
	
	temp=GetINIString("Data\local.ini", Section, Parameter)
	If temp <> "" Then
		l.LocalString = New LocalString
		l\section = Section
		l\parameter = Parameter
		l\value = temp
		Return temp
	EndIf
	
	Return Section + "." + Parameter
	
End Function

;With Formatting! %s in a String gets replaced
Function GetLocalStringR$(Section$, Parameter$, Replace$)
	
	Return Replace(GetLocalString(Section, Parameter), "%s", Replace)
	
End Function

;Include "SourceCode\FMod.bb"

;Include "SourceCode\resourcepacks.bb"
;InitResourcePacks()

Include "SourceCode\StrictLoads.bb"
;Include "SourceCode\fullscreen_window_fix.bb"
Include "SourceCode\KeyName.bb"
Include "SourceCode\KeyBinds.bb"

LoadKeyBinds()

;Font constants
;[Block]
Const MaxFontAmount = 10
Const Font_Default = 0
Const Font_Menu = 1
Const Font_Digital_Small = 2
Const Font_Digital_Large = 3
Const Font_Journal = 4
Const Font_Default_Large = 5
Const Font_Menu_Medium = 6
Const Font_Menu_Small = 7
Const Font_Digital_Medium = 8
Const Font_Default_Medium = 9
;[End Block]

Type Fonts
	Field UpdaterFont
	Field ConsoleFont
	Field Font[MaxFontAmount]
End Type

InitController()

Const VersionNumber$ = "0.2.9"
Const CompatibleNumber$ = "0.2.8"

Global MenuWhite%, MenuBlack%
Global ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
Global ButtonSFX2 = LoadSound_Strict("SFX\Interact\Button2.ogg")

;Global EnableSFXRelease% = GetINIInt(gv\OptionFile, "audio", "sfx release", 1)
;Global EnableSFXRelease_Prev% = EnableSFXRelease%

;Global ConsoleEnabled% = Instr(gv\Cmd$,"-console");GetINIInt(gv\OptionFile, "console", "enabled")

;[Block]

;Global LauncherWidth%= Min(GetINIInt(gv\OptionFile, "launcher", "launcher width", 640), 1024)
;Global LauncherHeight% = Min(GetINIInt(gv\OptionFile, "launcher", "launcher height", 480), 768)
;Global LauncherEnabled% = GetINIInt(gv\OptionFile, "launcher", "launcher enabled", 1)
Global LauncherIMG%

;Global GraphicWidth% = GetINIInt(gv\OptionFile, "options", "width", DesktopWidth())
;Global GraphicHeight% = GetINIInt(gv\OptionFile, "options", "height", DesktopHeight())
;Global Depth% = 0

;Global SelectedGFXMode%
;Global SelectedGFXDriver% = Max(GetINIInt(gv\OptionFile, "options", "gfx driver", 1), 1)

Global fresize_image%, fresize_texture%, fresize_texture2%
Global fresize_cam%

;Global ShowFPS = GetINIInt(gv\OptionFile, "options", "show FPS")

Global WireframeState
Global HalloweenTex

;Global DisplayMode% = GetINIInt(gv\OptionFile, "options", "display mode", 1)
Global RealGraphicWidth%,RealGraphicHeight%
Global AspectRatioRatio#

;Global EnableRoomLights% = GetINIInt(gv\OptionFile, "options", "room lights enabled", 1)

;Global TextureDetails% = GetINIInt(gv\OptionFile, "options", "texture details", 3)
Global TextureFloat#
Select opt\TextureDetails%
	Case 0
		TextureFloat# = 0.8
	Case 1
		TextureFloat# = 0.0
	Case 2
		TextureFloat# = -0.8
End Select
;Global ConsoleOpening% = GetINIInt(gv\OptionFile, "console", "auto opening")
;Global SFXVolume# = GetINIFloat(gv\OptionFile, "audio", "sound volume", 1.0)

;Include "SourceCode\Achievements.bb"
;Include "SourceCode\Difficulty.bb"
;Include "SourceCode\resourcepacks.bb"

Global AchvIni$ = "Data\achievementstrings.ini"

Global Data294$ = "Data\SCP-294.ini"

AspectRatioRatio = 1.0
UpdateLauncher()
Delete Each Resolution

If opt\DisplayMode=1 Then
	Graphics3DExt(DesktopWidth(), DesktopHeight(), 0, 4)
	
	RealGraphicWidth = DesktopWidth()
	RealGraphicHeight = DesktopHeight()
	
	AspectRatioRatio = (Float(opt\GraphicWidth)/Float(opt\GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
Else
	Graphics3DExt(opt\GraphicWidth, opt\GraphicHeight, 0, 2)
	
	RealGraphicWidth = opt\GraphicWidth
	RealGraphicHeight = opt\GraphicHeight
EndIf

If FileType(I_Loc\LangPath + Data294) = 1 Then
	Data294 = I_Loc\LangPath + Data294
EndIf

If FileType(I_Loc\LangPath + AchvIni) = 1 Then
	AchvIni = I_Loc\LangPath + AchvIni
EndIf

Global MenuScale# = (opt\GraphicHeight / 1024.0)

LoadMenuImages()

SetBuffer(BackBuffer())

Global CurTime%, PrevTime%, LoopDelay%, FPSfactor#, FPSfactor2#

Global Framelimit% = GetINIInt(gv\OptionFile, "options", "framelimit", 120)
Global Vsync% = GetINIInt(gv\OptionFile, "options", "vsync")

Global CurrFrameLimit# = (Framelimit%-29)/100.0

TextureAnisotropic 2^(opt\TextureFiltering+1)
TextureLodBias TextureFloat#

Global ScreenGamma# = GetINIFloat(gv\OptionFile, "options", "screengamma", 1.0)

SeedRnd MilliSecs()

;[End block]

Global GameSaved%

Global CanSave% = True

AppTitle "SCP: Nine-Tailed Fox v"+VersionNumber
Delay 100
;---------------------------------------------------------------------------------------------------------------------

;[Block]

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")

Include "SourceCode\LoadingScreens.bb"

;For some reason, Blitz3D doesn't load fonts that have filenames that
;don't match their "internal name" (i.e. their display name in applications
;like Word and such). As a workaround, I moved the files and renamed them so they
;can load without FastText.
InitFonts()

Global CreditsFont%,CreditsFont2%

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")

DrawLoading(0, True)

; - -Viewport.
Global viewport_center_x% = opt\GraphicWidth / 2, viewport_center_y% = opt\GraphicHeight / 2

; -- Mouselook.
Global mouselook_x_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_left_limit% = 250 * MenuScale, mouse_right_limit% = opt\GraphicWidth - mouse_left_limit
Global mouse_top_limit% = 150 * MenuScale, mouse_bottom_limit% = opt\GraphicHeight - mouse_top_limit ; As above.
Global mouse_x_speed_1#, mouse_y_speed_1#

Global KEY_RIGHT = GetINIInt(gv\OptionFile, "binds", "Right key", 32)
Global KEY_LEFT = GetINIInt(gv\OptionFile, "binds", "Left key", 30)
Global KEY_UP = GetINIInt(gv\OptionFile, "binds", "Up key", 17)
Global KEY_DOWN = GetINIInt(gv\OptionFile, "binds", "Down key", 31)

Global KEY_BLINK = GetINIInt(gv\OptionFile, "binds", "Blink key", 57)
Global KEY_SPRINT = GetINIInt(gv\OptionFile, "binds", "Sprint key", 42)
Global KEY_INV = GetINIInt(gv\OptionFile, "binds", "Inventory key", 15)
Global KEY_CROUCH = GetINIInt(gv\OptionFile, "binds", "Crouch key", 29)
Global KEY_SAVE = GetINIInt(gv\OptionFile, "binds", "Save key", 63)
Global KEY_CONSOLE = GetINIInt(gv\OptionFile, "binds", "Console key", 61)

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

;player stats -------------------------------------------------------------------------------------------------------
Global KillTimer#, KillAnim%, FallTimer#, DeathTimer#
Global Sanity#, ForceMove#, ForceAngle#
Global RestoreSanity%

Global Playable% = True

Global BLINKFREQ#
Global BlinkTimer#, EyeIrritation#, EyeStuck#, BlinkEffect# = 1.0, BlinkEffectTimer#

Global Stamina#, StaminaEffect#=1.0, StaminaEffectTimer#

Global CameraShakeTimer#, Vomit%, VomitTimer#, Regurgitate%

Global SCP1025state#[6]

Global HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#

Global WearingGasMask%, WearingHazmat%, WearingVest%, Wearing714%, WearingNightVision%
Global NVTimer#

Global SuperMan%, SuperManTimer#

;Global Injuries#, Bloodloss#, Infect#, HealTimer#
Global Infect#, HealTimer#

Global RefinedItems%

;player coordinates, angle, speed, movement etc ---------------------------------------------------------------------
Global DropSpeed#, HeadDropSpeed#, CurrSpeed#
Global user_camera_pitch#, side#
Global Crouch%, CrouchState#

Global PlayerZone%, PlayerRoom.Rooms

Global GrabbedEntity%

Global InvertMouse% = GetINIInt(gv\OptionFile, "options", "invert mouse y")
Global MouseHit1%, MouseDown1%, MouseHit2%, MouseDown2%, DoubleClick%, LastMouseHit1%, MouseUp1%

Global GodMode%, NoClip%, NoClipSpeed# = 2.0

Global CoffinDistance#

Global PlayerSoundVolume#

;camera/lighting effects (blur, camera shake, etc)-------------------------------------------------------------------
Global Shake#

Global ExplosionTimer#, ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

;menus, GUI ---------------------------------------------------------------------------------------------------------
Global MainMenuOpen%, MenuOpen%, StopHidingTimer#, InvOpen%
Global OtherOpen.Items = Null

Global SelectedEnding$, EndingScreen%, EndingTimer#

Global MsgTimer#, Msg$, DeathMSG$

Global AccessCode%, KeypadInput$, KeypadTimer#, KeypadMSG$

Global DrawHandIcon%
Global DrawArrowIcon%[4]

;misc ---------------------------------------------------------------------------------------------------------------

Global MTFtimer#, MTFrooms.Rooms[10], MTFroomState%[10]

Global RadioState#[9]
Global RadioState3%[7] ; TODO this shit is either partly unused or never worked
Global RadioState4%[9]
Global RadioCHN%[8] ; TODO check if 8 channels are necessary

Global OldAiPics%[2]

Global PlayTime%
Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global InfiniteStamina% = False
Global NVBlink%
Global IsNVGBlinking% = False
;[End block]

Include "SourceCode\Achievements.bb"
Include "SourceCode\Difficulty.bb"

Include "SourceCode\Use914.bb"

;----------------------------------------------  Console -----------------------------------------------------

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#,ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR% = 255,ConsoleG% = 255,ConsoleB% = 255

Type Cheats
	Field CDScream%
	Field Mini173%
	Field OwO%
End Type

Function InitCheats()
	Local Cheat.Cheats = New Cheats
End Function

InitCheats()

;---------------------------------------------------------------------------------------------------

Global DebugHUD%

Global BlurVolume#, BlurTimer#

Global LightBlink#, LightFlash#

Global BumpEnabled% = GetINIInt(gv\OptionFile, "options", "bump mapping enabled", 1)
Global HUDenabled% = GetINIInt(gv\OptionFile, "options", "HUD enabled", 1)

Global Camera%, CameraShake#, CurrCameraZoom#

Global Brightness% = GetINIFloat(gv\OptionFile, "options", "brightness", 20)
Global CameraFogNear# = GetINIFloat(gv\OptionFile, "options", "camera fog near", 0.5)
Global CameraFogFar# = GetINIFloat(gv\OptionFile, "options", "camera fog far", 6.0)

Global StoredCameraFogFar# = CameraFogFar

Global MouseSens# = GetINIFloat(gv\OptionFile, "options", "mouse sensitivity")

Include "SourceCode\dreamfilter.bb"

;----------------------------------------------  Sounds -----------------------------------------------------

;[Block]

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

;The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
; TODO this can be improved upon (shrinking the array)
Global Music$[67]
Music[0] = "LCZ"
Music[1] = "HCZ"
Music[2] = "EZ"
Music[3] = "PD"
Music[4] = "079"
Music[5] = "GateB1"
Music[6] = "GateB2"
Music[7] = "Room3Storage"
Music[8] = "Room049"
Music[9] = "8601"
Music[10] = "106"
Music[11] = "Menu"
Music[12] = "8601Cancer"
Music[13] = "Intro"
Music[14] = "178"
Music[15] = "PDTrench"
Music[16] = "205"
Music[17] = "GateA"
Music[18] = "1499"
Music[19] = "1499Danger"
Music[20] = "049Chase"
Music[21] = "..\Ending\MenuBreath"
Music[22] = "914"
Music[23] = "Ending"
Music[24] = "Credits"
Music[25] = "Intro1"
Music[26] = "Intro2"
Music[27] = "538"
Music[28] = "SingingPipes"
Music[29] = "939"
Music[30] = "457"
Music[31] = "076"
Music[32] = "076FightStart"
Music[33] = "076FightContinue"
Music[34] = "GateBEnding"
Music[35] = "Multiplayer\ConflictArises"
Music[36] = "Multiplayer\It_Follows"
Music[37] = "Multiplayer\It_Watches"
Music[38] = "Suspense"
Music[39] = "SaveMeFrom"

;Global MusicVolume# = GetINIFloat(gv\OptionFile, "audio", "music volume", 0.5)
;Global MusicCHN% = PlaySound_Strict(Music[2])

Global MusicCHN
;MusicCHN = StreamSound_Strict("SFX\Music\"+Music[2]+".ogg",MusicVolume,Mode)

Global CurrMusicVolume# = 1.0, NowPlaying%=66, ShouldPlay%=11
Global CurrMusic% = 1

DrawLoading(10, True)

Global OpenDoorSFX%[4 * 3], CloseDoorSFX%[4 * 3]

Global KeyCardSFX1
Global KeyCardSFX2
Global ScannerSFX1
Global ScannerSFX2

Global OpenDoorFastSFX
Global CautionSFX%

Global NuclearSirenSFX%

Global CameraSFX 

Global StoneDragSFX%

Global GunshotSFX%
Global Gunshot2SFX%
Global Gunshot3SFX%
Global BullethitSFX%

Global TeslaIdleSFX
Global TeslaActivateSFX
Global TeslaPowerUpSFX

Global MagnetUpSFX%, MagnetDownSFX
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Global DecaySFX%[4]

Global BurstSFX

DrawLoading(20, True)

Global RustleSFX%[3]

Global Death914SFX%

Global DripSFX%[4]

Global LeverSFX%, LightSFX%

Global ButtGhostSFX%

Global RadioSFX[2 * 10]

Global RadioSquelch
Global RadioStatic
Global RadioBuzz

Global ElevatorBeepSFX, ElevatorMoveSFX

Global PickSFX%[4]

Global AmbientSFXCHN%, CurrAmbientSFX%
Global AmbientSFXAmount[6] ; TODO this system could be done better! Avoid empty spaces in array
;0 = light containment, 1 = heavy containment, 2 = entrance
AmbientSFXAmount[0]=8 : AmbientSFXAmount[1]=11 : AmbientSFXAmount[2]=12
;3 = general, 4 = pre-breach
AmbientSFXAmount[3]=15 : AmbientSFXAmount[4]=5
;5 = forest
AmbientSFXAmount[5]=10

Global AmbientSFX%[6 * 15]

Global OldManSFX%[9]

Global Scp173SFX%[3]

Global HorrorSFX%[16]

DrawLoading(25, True)

Global IntroSFX%[5]

;IntroSFX[13] = LoadSound_Strict("SFX\intro\shoot1.ogg")
;IntroSFX[14] = LoadSound_Strict("SFX\intro\shoot2.ogg")

Global AlarmSFX%[4]

Global HeartBeatSFX

Global VomitSFX%

Global BreathSFX[2 * 5]
Global BreathCHN%


Global NeckSnapSFX%[3]

Global DamageSFX%[9]

Global MTFSFX%

Global CoughSFX%[3]
Global CoughCHN%, VomitCHN%

Global MachineSFX%

Global ApacheSFX

; TODO look at this again
Global CurrStepSFX
Const STEP_FLOORS = 5, STEP_STATES = 2, STEP_IDS = 8
Global StepSFXSource%[STEP_FLOORS * STEP_STATES * STEP_IDS]
;(normal/metal, walk/run, id)
Function StepSFX(floor%, state%, id%)
	Return StepSFXSource[state + STEP_STATES * (id + Floor * STEP_IDS)]
End Function

Function SetStepSFX(floor%, state%, id%, value%)
	StepSFXSource[state + STEP_STATES * (id + Floor * STEP_IDS)] = value
End Function

Global Step2SFX%[6]

DrawLoading(30, True)

;[End block]

;New Sounds and Meshes/Other things in SCP:CB 1.3 - ENDSHN
;[Block]
;Global NTF_1499EnterSFX% = LoadSound_Strict("SFX\SCP\1499\Enter.ogg")
;Global NTF_1499LeaveSFX% = LoadSound_Strict("SFX\SCP\1499\Exit.ogg")

Global PlayCustomMusic% = False, CustomMusic% = 0

Global Monitor2, Monitor3, MonitorTexture2, MonitorTexture3, MonitorTexture4, MonitorTextureOff
Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

;This variable is for when a camera detected the player
	;False: Player is not seen (will be set after every call of the Main Loop)
	;True: The Player got detected by a camera
Global PlayerDetected%
;Global PrevInjuries#,PrevBloodloss#
Global NoTarget% = False

Global NVGImages = LoadAnimImage("GFX\battery.png",64,64,0,2)
MaskImage NVGImages,255,0,255

Global Wearing1499% = False
Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global EnableUserTracks% = GetINIInt(gv\OptionFile, "audio", "enable user tracks")
Global UserTrackMode% = GetINIInt(gv\OptionFile, "audio", "user track setting")
Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Global UserTrackName$[64]

Global NTF_1499PrevX#
Global NTF_1499PrevY#
Global NTF_1499PrevZ#
Global NTF_1499PrevRoom.Rooms
Global NTF_1499X#
Global NTF_1499Y#
Global NTF_1499Z#
Global NTF_1499Sky%

Global OptionsMenu% = 0
Global QuitMSG% = 0

Global InFacility% = True

Global PrevMusicVolume# = opt\MusicVol
Global PrevSFXVolume# = opt\SFXVolume#
Global DeafPlayer% = False
Global DeafTimer# = 0.0

Global IsZombie% = False

Global room2gw_brokendoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Global ParticleAmount% = GetINIInt(gv\OptionFile,"options","particle amount", 2)

Global NavImages%[5]
For i = 0 To 3
	NavImages[i] = LoadImage_Strict("GFX\navigator\roomborder"+i+".png")
	MaskImage NavImages[i],255,0,255
Next
NavImages[4] = LoadImage_Strict("GFX\navigator\batterymeter.png")

Global NavBG = CreateImage(opt\GraphicWidth,opt\GraphicHeight)

Global LightConeModel

Global ParticleEffect[10]

Const MaxDTextures=10
Global DTextures[MaxDTextures]

Global NPC049OBJ, NPC0492OBJ
Global ClerkOBJ

Global IntercomStreamCHN%

Global ForestNPC,ForestNPCTex,ForestNPCData#[3]
;[End Block]

;-----------------------------------------  Images ----------------------------------------------------------

Global PauseMenuIMG%

Global SprintIcon%
Global BlinkIcon%
Global CrouchIcon%
Global HandIcon%
Global HandIcon2%

Global StaminaMeterIMG%

Global KeypadHUD

Global Panel294, Using294%, Input294$

DrawLoading(35, True)

;----------------------------------------------  Items  -----------------------------------------------------

Include "SourceCode\Items.bb"
DrawLoading(40, True)
;--------------------------------------- Particles ------------------------------------------------------------

Include "SourceCode\Particles.bb"
DrawLoading(45, True)
;-------------------------------------  Doors --------------------------------------------------------------

Include "SourceCode\Doors.bb"

DrawLoading(50,True)

Include "SourceCode\Materials.bb"
Global GorePics%[6]
;Include "SourceCode\Cutscenes.bb"
Include "SourceCode\MapSystem.bb"
DrawLoading(55, True)
LoadRoomTemplates("Data\rooms.ini")

;-----------------------------------------NTF STUFF------------------------------------------------------

Include "SourceCode\MTF.bb"
DrawLoading(60, True)
Include "SourceCode\Guns.bb"
DrawLoading(65, True)
Include "SourceCode\TextureCache.bb"
DrawLoading(70, True)
Include "SourceCode\Mission.bb"

DrawLoading(75,True)

Include "SourceCode\NPCs.bb"
DrawLoading(80, True)

;-------------------------------------  Events --------------------------------------------------------------
Include "SourceCode\Events.bb"

;Collision constants
;[Block]
Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_178% = 5
Const HIT_DEAD% = 6
;Multiplayer
Const HIT_PLAYER_MP% = 7
Const HIT_NPC_MP% = 8
;[End Block]

Collisions HIT_PLAYER, HIT_MAP, 2, 2
Collisions HIT_PLAYER, HIT_PLAYER, 1, 2 ;3
Collisions HIT_ITEM, HIT_MAP, 2, 2
Collisions HIT_APACHE, HIT_APACHE, 1, 2
;Collisions HIT_178, HIT_MAP, 2, 2
;Collisions HIT_178, HIT_178, 1, 3
Collisions HIT_DEAD, HIT_MAP, 2, 2
;For Multiplayer
Collisions HIT_PLAYER_MP, HIT_MAP, 2, 2
Collisions HIT_PLAYER_MP, HIT_NPC_MP, 1, 2
Collisions HIT_NPC_MP, HIT_MAP, 2, 2
;Collisions HIT_NPC_MP, HIT_NPC_MP, 1, 2

;Function MilliSecs2()
;	Local retVal% = MilliSecs()
;	If retVal < 0 Then retVal = retVal + 2147483648
;	Return retVal
;End Function

Type AudioControl
	Field MasterVol#
	Field MusicVol#
	Field VoiceVol#
	Field EnviromentVol#
End Type

DrawLoading(90, True)

;----------------------------------- meshes and textures ----------------------------------------------------------------

Global FogTexture%, Fog%
Global GasMaskTexture%, GasMaskOverlay%
Global InfectTexture%, InfectOverlay%
Global DarkTexture%, Dark%
Global Collider%, Head%

Global FogNVTexture%
Global NVTexture%, NVOverlay%

Global TeslaTexture%

Global LightTexture%, Light%
Global LightSpriteTex%[3]

Global LeverOBJ%, LeverBaseOBJ%

Global Monitor%, MonitorTexture%
Global CamBaseOBJ%, CamOBJ%

Global LiquidObj%,MTFObj%,GuardObj%,ClassDObj%
Global ApacheObj%,ApacheRotorObj%

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global Save_MSG$ = ""
Global Save_MSG_Timer# = 0.0
Global Save_MSG_Y# = 0.0

;Global MTF_CameraCheckTimer# = 0.0
;Global MTF_CameraCheckDetected% = False

;---------------------------------------------------------------------------------------------------

Include "SourceCode\Multiplayer\Multiplayer_Base.bb"

Include "SourceCode\Player.bb"

Include "SourceCode\Menu.bb"
Include "SourceCode\Menu3D.bb"
Include "SourceCode\Console.bb"
DrawLoading(95, True)
MainMenuOpen = True
InitConsole(2)
Load3DMenu()

;---------------------------------------------------------------------------------------------------

FlushKeys()
FlushMouse()
FlushJoy()

DrawLoading(100, True)
PlayStartupVideos()
CurrMusicVolume# = 0.01
LoopDelay = MilliSecs()

Global CurrTrisAmount%

Global Input_ResetTime# = 0.0
Global MousePosX#, MousePosY#

Type SCP427
	Field Using%
	Field Timer#
	Field Sound[2]
	Field SoundCHN[2]
End Type

InitErrorMsgs(9)
SetErrorMsg(0, "An error occured in SCP: Nine Tailed Fox Mod v"+VersionNumber+Chr(10)+"Save compatible version: "+CompatibleNumber+". Engine version: "+SystemProperty("blitzversion"))
SetErrorMsg(1, "OS: "+SystemProperty("os")+" "+gv\OSBit+" bit (Build: "+SystemProperty("osbuild")+")")
SetErrorMsg(2, "CPU: "+Trim(SystemProperty("cpuname"))+" (Arch: "+SystemProperty("cpuarch")+", "+GetEnv("NUMBER_OF_PROCESSORS")+" Threads)")

SetErrorMsg(8, Chr(10)+"Please take a screenshot of this error and send it to us!")

;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------       		MAIN LOOP                 ---------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

GlobalGameLoop()
Steam_Shutdown()

Function GlobalGameLoop()
	
	Repeat
		Local CurrDelta% = MilliSecs()
		
		SetErrorMsg(3, "GPU: "+GfxDriverName(CountGfxDrivers())+" ("+((TotalVidMem()/1024)-(AvailVidMem()/1024))+" MB/"+(TotalVidMem()/1024)+" MB)")
		SetErrorMsg(4, "Triangles rendered: "+CurrTrisAmount+", Active textures: "+ActiveTextures()+Chr(10))
		If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
			If PlayerRoom <> Null Then
				SetErrorMsg(5, "Map seed: "+RandomSeed + ", Room: " + PlayerRoom\RoomTemplate\Name+" (" + Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5) + ", angle: "+PlayerRoom\angle + ")")
				
				For ev.Events = Each Events
					If ev\room = PlayerRoom Then
						SetErrorMsg(6, "Room event: "+ev\EventName+" (" +ev\EventState+", "+ev\EventState2+", "+ev\EventState3+")"+Chr(10))
						Exit
					EndIf
				Next
			EndIf
		ElseIf gopt\GameMode = GAMEMODE_MULTIPLAYER Then
			SetErrorMsg(5, "Map: "+mp_I\MapInList\Name)
			SetErrorMsg(6, "Gamemode: "+mp_I\Gamemode\name+Chr(10))
		EndIf
		
		CatchErrors("Global main loop")
		Cls
		
		Local elapsedMilliseconds%
		SetCurrTime(MilliSecs())
		elapsedMilliseconds = GetCurrTime()-GetPrevTime()
		AddToTimingAccumulator(elapsedMilliseconds)
		SetPrevTime(GetCurrTime())
		
		If Framelimit > 0 Then
			;Framelimit
			Local WaitingTime% = (1000.0 / Framelimit) - (MilliSecs() - LoopDelay)
			Delay WaitingTime%
			
			LoopDelay = MilliSecs()
		EndIf
		
		FPSfactor = GetTickDuration()
		FPSfactor2 = FPSfactor
		
		If MainMenuOpen Then
			MainLoopMenu()
		Else
			If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
				MainLoop()
			Else
				If mp_I\PlayState=GAME_SERVER
					MPMainLoop()
				Else
					MPMainLoopClient()
				EndIf
			EndIf
		EndIf
		
		GammaUpdate()
		
		If opt\ShowFPS Then
			If ft\fpsgoal < MilliSecs() Then
				ft\fps = ft\tempfps
				ft\tempfps = 0
				ft\fpsgoal = MilliSecs() + 1000
			Else
				ft\tempfps = ft\tempfps + 1
			EndIf
		EndIf
		
		;Text 700, 90, SystemProperty("os")+" (Build: "+SystemProperty("osbuild")+"), CPU: "+GetEnv("PROCESSOR_IDENTIFIER")+" (Arch: "+GetEnv("PROCESSOR_ARCHITECTURE")+", "+GetEnv("NUMBER_OF_PROCESSORS")+" Threads)"
		;Text 700, 110, "Phys. Memory: "+((TotalPhys()/1024)-(AvailPhys()/1024))+" MB/"+(TotalPhys()/1024)+" MB ("+(TotalPhys()-AvailPhys())+" KB/"+TotalPhys()+" KB). CPU Usage: "+MemoryLoad()+"%"
		;Text 700, 130, "Virtual Memory: "+((TotalVirtual()/1024)-(AvailVirtual()/1024))+" MB/"+(TotalVirtual()/1024)+" MB ("+(TotalVirtual()-AvailVirtual())+" KB/"+TotalVirtual()+" KB)"
		;Text 700, 150, "Video Memory: "+((TotalVidMem()/1024)-(AvailVidMem()/1024))+" MB/"+(TotalVidMem()/1024)+" MB ("+(TotalVidMem()-AvailVidMem())+" KB/"+TotalVidMem()+" KB)"
		
		Steam_Update()
		UpdateRichPresence()
		
		Flip Vsync
		
		ft\DeltaTime = MilliSecs() - CurrDelta
		
		CatchErrors("Global main loop / uncaught")
	Forever
	
End Function

Function MainLoop()
	Local r.Rooms,e.Events
	Local i%
	
	While (ft\accumulator>0.0)
		ft\accumulator = ft\accumulator-GetTickDuration()
		If (ft\accumulator<=0.0) Then CaptureWorld()
		
		If MenuOpen Lor InvOpen Lor OtherOpen<>Null Lor ConsoleOpen Lor SelectedScreen <> Null Lor Using294 Then FPSfactor = 0
		If d_I <> Null Then 
			If d_I\SelectedDoor <> Null Then
				FPSfactor = 0
			EndIf
		EndIf	
		If mi_I\IsEnding Then FPSfactor = 0
		If ConsoleOpen Lor InvOpen
			FPSfactor=0.0
		EndIf
		
		MousePosX = MouseX()
		MousePosY = MouseY()
		
		If Input_ResetTime > 0 And FPSfactor > 0.0 Then
			Input_ResetTime = Max(Input_ResetTime - FPSfactor, 0.0)
		Else
			DoubleClick = False
			If (Not co\Enabled)
				MouseHit1 = MouseHit(1)
				If MouseHit1
					If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
					LastMouseHit1 = MilliSecs()
				EndIf
				Local prevmousedown1 = MouseDown1
				MouseDown1 = MouseDown(1)
				If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
				
				MouseHit2 = MouseHit(2)
				MouseDown2 = MouseDown(2)
				
				MouseHit3 = MouseHit(3)
				
				keyhituse = KeyHit(KEY_USE)
				keydownuse = KeyDown(KEY_USE)
			Else
				;[CONTROLLER]
				MouseHit1 = JoyHit(CK_LMouse)
				If MouseHit1 Then
					If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
					LastMouseHit1 = MilliSecs()
				EndIf
				prevmousedown1 = MouseDown1
				MouseDown1 = JoyDown(CK_LMouse)
				If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
				MouseHit2 = JoyHit(CK_RMouse)
				MouseDown2 = JoyDown(CK_RMouse)
				MouseHit3 = JoyHit(CK_MMouse)
				keyhituse = JoyHit(CK_Use)
				keydownuse = JoyDown(CK_Use)
			EndIf
		EndIf
		
		;If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
		If (Not keydownuse) And (Not keyhituse) Then GrabbedEntity = 0
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		If mi_I\EndSFX<>0
			If mi_I\EndingTimer=0.0
				If mi_I\EndSFX_Vol>0.0
					mi_I\EndSFX_Vol = Max(mi_I\EndSFX_Vol-0.01*FPSfactor2,0)
					SetStreamVolume_Strict(mi_I\EndSFX,(opt\MusicVol)*mi_I\EndSFX_Vol)
				Else
					StopStream_Strict(mi_I\EndSFX)
					mi_I\EndSFX = 0
				EndIf
			EndIf
		EndIf
		
		UpdateStreamSounds()
		
		DrawHandIcon = False
		For i = 0 To 3
			DrawArrowIcon[i] = False
		Next
		
		RestoreSanity = True
		ShouldEntitiesFall = True
		
		ShouldUpdateWater = ""
		WaterRender_IgnoreObject = 0
		
		If FPSfactor > 0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then UpdateSecurityCams()
		
		If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (d_I\SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And EndingTimer=>0 Then
			;ShouldPlay = Min(PlayerZone,2)
			If NTF_CurrZone = 0 Then
				ShouldPlay = 25
			Else
				If (Not PlayerInNewElevator) Then
					ShouldPlay = NTF_CurrZone-1
				EndIf
			EndIf
		EndIf
		
		If FPSfactor>0 Then
			;If (Not InvOpen) Then CurrOverhaul=0
		Else
			co\PressedButton = JoyHit(CKM_Press)
			co\PressedNext = JoyDown(CKM_Next)
			co\PressedPrev = JoyDown(CKM_Prev)
			If co\PressedNext And co\PressedPrev
				co\PressedNext = False
				co\PressedPrev = False
			EndIf
		EndIf
		
		If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gate_a_topside" And PlayerRoom\RoomTemplate\Name <> "gate_b_topside" And (Not MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then 
			
			If mi_I\EndingTimer = 0.0
				If PlayerRoom\RoomTemplate\Name <> "gate_a_intro"
					If Rand(1500) = 1 Then
						If NTF_AmbienceSFX=0
							For i = 0 To 5
								If AmbientSFX[i * 15 + CurrAmbientSFX]<>0 Then
									If ChannelPlaying(AmbientSFXCHN)=0 Then FreeSound_Strict AmbientSFX[i * 15 + CurrAmbientSFX] : AmbientSFX[i * 15 + CurrAmbientSFX] = 0
								EndIf			
							Next
						EndIf
						If ChannelPlaying(NTF_AmbienceCHN)=0 Then FreeSound_Strict NTF_AmbienceSFX : NTF_AmbienceSFX = 0
						
						;PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
						PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), EntityY(Camera) + Rnd(-1.0, 1.0), EntityZ(Camera) + Rnd(-1.0, 1.0))
						
						;If Rand(3)=1 Then PlayerZone = 3
						;If Rand(3)=1 Then PlayerZone = 6
						
						PlayerZone = NTF_CurrZone-1
						
						Select Rand(0,3)
							Case 0
								PlayerZone = 3
							Case 1
								PlayerZone = 6
							Case 2
								PlayerZone = 7
							Default
								PlayerZone = NTF_CurrZone-1
						End Select
						
						If PlayerRoom\RoomTemplate\Name = "gate_a_intro" Then 
							PlayerZone = 4
						ElseIf PlayerRoom\RoomTemplate\Name = "cont_860"
							For e.Events = Each Events
								If e\EventName = "cont_860"
									If e\EventState = 1.0
										PlayerZone = 5
										PositionEntity (SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
									EndIf
									
									Exit
								EndIf
							Next
						ElseIf PlayerRoom\RoomTemplate\Name = "room2_maintenance"
							If EntityY(Collider)<-3500.0*RoomScale
								PlayerZone = 8
							EndIf
						EndIf
						
						If PlayerZone < 6
							CurrAmbientSFX = Rand(0,AmbientSFXAmount[PlayerZone]-1)
						ElseIf PlayerZone=6
							CurrAmbientSFX = Rand(0,NTF_MaxAmbienceSFX-1)
						ElseIf PlayerZone = 7
							Select NTF_CurrZone
								Case 1
									CurrAmbientSFX = Rand(0,NTF_MaxLCZAmbience-1)
								Case 2
									CurrAmbientSFX = Rand(0,NTF_MaxHCZAmbience-1)
								Case 3
									CurrAmbientSFX = Rand(0,NTF_MaxEZAmbience-1)
							End Select
						Else
							CurrAmbientSFX = Rand(0,NTF_MaxSewerAmbienceSFX-1)
						EndIf
						
						Select PlayerZone
							Case 0,1,2
								If AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=0 Then AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=LoadSound_Strict("SFX\Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg")
							Case 3
								If AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=0 Then AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=LoadSound_Strict("SFX\Ambient\General\ambient"+(CurrAmbientSFX+1)+".ogg")
							Case 4
								If AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=0 Then AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=LoadSound_Strict("SFX\Ambient\Pre-breach\ambient"+(CurrAmbientSFX+1)+".ogg")
							Case 5
								If AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=0 Then AmbientSFX[PlayerZone * 15 + CurrAmbientSFX]=LoadSound_Strict("SFX\Ambient\Forest\ambient"+(CurrAmbientSFX+1)+".ogg")
							Case 6
								If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\"+NTF_AmbienceStrings[CurrAmbientSFX]+".ogg")
							Case 7
								Select NTF_CurrZone
									Case 1
										If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\LCZ\"+NTF_LCZAmbienceStrings[CurrAmbientSFX]+".ogg")
									Case 2
										If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\HCZ\"+NTF_HCZAmbienceStrings[CurrAmbientSFX]+".ogg")
									Case 3
										If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\EZ\"+NTF_EZAmbienceStrings[CurrAmbientSFX]+".ogg")
								End Select
							Case 8
								If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\rooms\sewers\"+NTF_SewerAmbienceStrings[CurrAmbientSFX]+".ogg")
						End Select
						
						If PlayerZone < 6
							AmbientSFXCHN = PlaySound2(AmbientSFX[PlayerZone * 15 + CurrAmbientSFX], Camera, SoundEmitter)
						Else
							NTF_AmbienceCHN = PlaySound2(NTF_AmbienceSFX, Camera, SoundEmitter)
						EndIf
						UpdateSoundOrigin(AmbientSFXCHN,Camera, SoundEmitter)
						UpdateSoundOrigin(NTF_AmbienceCHN,Camera, SoundEmitter)
					EndIf
					
					If Rand(50000) = 3 Then
						Local RN$ = PlayerRoom\RoomTemplate\Name$
						If RN$ <> "cont_860" And RN$ <> "cont_1123" And RN$ <> "gate_a_intro" Then
							If FPSfactor > 0 Then LightBlink = Rnd(1.0,2.0)
							PlaySound_Strict  LoadTempSound("SFX\SCP\079\Broadcast"+Rand(1,7)+".ogg")
						EndIf 
					EndIf
				Else
					If Rand(1500) = 1 Then
						If ChannelPlaying(NTF_AmbienceCHN)=0 Then FreeSound_Strict NTF_AmbienceSFX : NTF_AmbienceSFX = 0
						
						PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
						
						CurrAmbientSFX = Rand(0,NTF_MaxIntroAmbienceSFX-1)
						
						If NTF_AmbienceSFX=0 Then NTF_AmbienceSFX=LoadSound_Strict("SFX\Ambience\Intro\"+NTF_IntroAmbienceStrings[CurrAmbientSFX]+".ogg")
						
						NTF_AmbienceCHN = PlaySound2(NTF_AmbienceSFX, Camera, SoundEmitter)
					EndIf
					UpdateSoundOrigin(NTF_AmbienceCHN,Camera, SoundEmitter)
				EndIf
			EndIf
		EndIf
		
		UpdateCheckpoint1 = False
		UpdateCheckpoint2 = False
		
		If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (d_I\SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And EndingTimer=>0 And (Not mi_I\IsEnding) Then
			LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
			CameraFogRange(Camera, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
			;CameraFogColor(Camera, 0,0,0)
			CameraFogMode Camera,1
			CameraRange(Camera, 0.01, Min(CameraFogFar*LightVolume*1.5,28))
			For r.Rooms = Each Rooms
				For i = 0 To r\MaxLights%
					If r\Lights%[i]<>0
						EntityAutoFade r\LightSprites%[i],CameraFogNear*LightVolume,CameraFogFar*LightVolume
					EndIf
				Next
			Next
			
			AmbientLight Brightness, Brightness, Brightness	
			PlayerSoundVolume = CurveValue(0.0, PlayerSoundVolume, 5.0)
			
			CanSave% = True
			If psp\Health = 0 And KillTimer >= 0 Then
				Kill()
			EndIf
			UpdateDeafPlayer()
			UpdateEmitters()
			UpdateNewElevators()
			If mpl\HasNTFGasmask Then
				ShowEntity GasMaskOverlay2
			Else
				HideEntity GasMaskOverlay2
			EndIf
			MouseLook()
			MovePlayer()
			UpdateNightVision()
			InFacility = CheckForPlayerInFacility()
			UpdateDoors()
			UpdateScreens()
			Update294()
			If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
				UpdateEvents()
			EndIf
			UpdateRoomLights(Camera)
			UpdateFluLights()
			UpdateFuseBoxes()
			If ShouldUpdateWater<>"" Then
				UpdateWater(ShouldUpdateWater)
			EndIf
			If KillTimer >= 0 Then
				If CanPlayerUseGuns% Then
					UpdateGuns()
					AnimateGuns()
					UpdateIronSight()
				EndIf
				UpdateSPPlayer()
			Else
				If ChannelPlaying(ChatSFXCHN) Then StopChannel(ChatSFXCHN)
				HideEntity g_I\GunPivot
			EndIf
			UpdateDecals()
			UpdateMTFDialogue()
			UpdateMTF()
			UpdateNPCs()
			UpdateItems()
			UpdateParticles()
			UpdateDamageOverlay()
			If I_427\Using Then
				Use427()
			Else
				If I_427\Timer >= 70*360 Then
					If Rnd(200)<2.0 Then
						pvt = CreatePivot()
						PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
						TurnEntity pvt, 90, 0, 0
						EntityPick(pvt,0.3)
						de.Decals = CreateDecal(DECAL_FOAM, PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
						de\Size = Rnd(0.03,0.08)*2.0 : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
						tempchn% = PlaySound_Strict (DripSFX[Rand(0,3)])
						ChannelVolume tempchn, Rnd(0.0,0.8)*(opt\SFXVolume*opt\MasterVol)
						ChannelPitch tempchn, Rand(20000,30000)
						pvt = FreeEntity_Strict(pvt)
						BlurTimer = 800
					EndIf
				EndIf
				If I_427\Timer >= 70*390 Then
					Crouch = True
				EndIf
			EndIf
		Else
			keyhituse = False
			keydownuse = False
		EndIf
		
		Local CurrFogColor$ = ""
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "room2_maintenance" And EntityY(Collider)<-3500.0*RoomScale Then
				CurrFogColor = FogColor_Sewers
			ElseIf PlayerRoom\RoomTemplate\Name = "gate_a_topside" Lor PlayerRoom\RoomTemplate\Name = "gate_a_intro" Lor PlayerRoom\RoomTemplate\Name = "gate_b_topside" Then
				CurrFogColor = FogColor_Outside
			ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
				;PD sets the fog customly in the UpdateEvent code
				CurrFogColor = ""
			Else
				Select NTF_CurrZone
					Case LCZ
						CurrFogColor = FogColor_LCZ
					Case HCZ
						CurrFogColor = FogColor_HCZ
					Case EZ
						CurrFogColor = FogColor_EZ
				End Select
			EndIf
		EndIf
		If CurrFogColor <> "" Then
			Local FogColorR% = Left(CurrFogColor,3)
			Local FogColorG% = Mid(CurrFogColor,4,3)
			Local FogColorB% = Right(CurrFogColor,3)
			CameraFogColor Camera,FogColorR,FogColorG,FogColorB
			CameraClsColor Camera,FogColorR,FogColorG,FogColorB
		EndIf
		
		If InfiniteStamina% Then Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
		
		If gopt\GameMode = GAMEMODE_UNKNOWN Then
			UpdateMissionEvents()
			UpdateMissionEnding()
		EndIf
		If FPSfactor = 0 Then
			UpdateWorld(0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
			If NTF_SmallHead Then UpdateSmallHeadMode()
		EndIf
		
		If MTF_CameraCheckTimer>0.0 And MTF_CameraCheckTimer<70*90
			MTF_CameraCheckTimer=MTF_CameraCheckTimer+FPSfactor
		ElseIf MTF_CameraCheckTimer>=70*90
			MTF_CameraCheckTimer=0.0
		EndIf
		
		;[Block]
		If (Not MenuOpen)  Then
			BlurVolume = Min(CurveValue(0.0, BlurVolume, 20.0),0.95)
			If BlurTimer > 0.0 Then
				BlurVolume = Max(Min(0.95, BlurTimer / 1000.0), BlurVolume)
				BlurTimer = Max(BlurTimer - FPSfactor, 0.0)
			End If
			
			Local darkA# = 0.0
			
			If Sanity < 0 Then
				If RestoreSanity Then Sanity = Min(Sanity + FPSfactor, 0.0)
				If Sanity < (-200) Then 
					darkA = Max(Min((-Sanity - 200) / 700.0, 0.6), darkA)
					If KillTimer => 0 Then 
						HeartBeatVolume = Min(Abs(Sanity+200)/500.0,1.0)
						HeartBeatRate = Max(70 + Abs(Sanity+200)/6.0,HeartBeatRate)
					EndIf
				EndIf
			End If
			
			If EyeStuck > 0 Then 
				BlinkTimer = BLINKFREQ
				EyeStuck = Max(EyeStuck-FPSfactor,0)
				
				If EyeStuck < 9000 Then BlurTimer = Max(BlurTimer, (9000-EyeStuck)*0.5)
				If EyeStuck < 6000 Then darkA = Min(Max(darkA, (6000-EyeStuck)/5000.0),1.0)
				If EyeStuck < 9000 And EyeStuck+FPSfactor =>9000 Then 
					Msg = "The eyedrops are causing your eyes to tear up."
					MsgTimer = 70*6
				EndIf
			EndIf
			
			If BlinkTimer < 0 Then
				If BlinkTimer > - 5 Then
					darkA = Max(darkA, Sin(Abs(BlinkTimer * 18.0)))
				ElseIf BlinkTimer > - 15
					darkA = 1.0
				Else
					darkA = Max(darkA, Abs(Sin(BlinkTimer * 18.0)))
				EndIf
				
				If BlinkTimer <= - 20 Then
					;Randomizes the frequency of blinking. Scales with difficulty.
					Select SelectedDifficulty\otherFactors
						Case EASY
							BLINKFREQ = Rnd(490,700)
						Case NORMAL
							BLINKFREQ = Rnd(455,665)
						Case HARD
							BLINKFREQ = Rnd(420,630)
					End Select 
					BlinkTimer = BLINKFREQ
				EndIf
				
				BlinkTimer = BlinkTimer - FPSfactor
			Else
				BlinkTimer = BlinkTimer - FPSfactor * 0.6 * BlinkEffect
				If EyeIrritation > 0 Then BlinkTimer=BlinkTimer-Min(EyeIrritation / 100.0 + 1.0, 5.0) * FPSfactor
				
				darkA = Max(darkA, 0.0)
			EndIf
			
			If BlinkEffectTimer > 0 Then
				BlinkEffectTimer = BlinkEffectTimer - (FPSfactor/70)
			Else
				If BlinkEffect <> 1.0 Then BlinkEffect = 1.0
			EndIf
			
			LightBlink = Max(LightBlink - (FPSfactor / 35.0), 0)
			If LightBlink > 0 Then darkA = Min(Max(darkA, LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If Using294 Then darkA=1.0
			
			If (Not mpl\NightVisionEnabled) And (Not WearingNightVision) Then
				darkA = Max((1.0-SecondaryLightOn)*0.9, darkA)
			EndIf
			
			If KillTimer < 0 Then
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(KillTimer*5)
				KillTimer=KillTimer-(FPSfactor*0.8)
				If KillTimer < - 360 And (gopt\GameMode <> GAMEMODE_UNKNOWN) Then 
					MenuOpen = True
					If SelectedEnding <> "" Then EndingTimer = Min(KillTimer,-0.1)
				EndIf
				darkA = Max(darkA, Min(Abs(KillTimer / 400.0), 1.0))
			EndIf
			
			If FallTimer < 0 Then
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(FallTimer*10)
				FallTimer = FallTimer-FPSfactor
				darkA = Max(darkA, Min(Abs(FallTimer / 400.0), 1.0))				
			EndIf
			
			If LightFlash > 0 Then
				ShowEntity Light
				EntityAlpha(Light, Max(Min(LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
				LightFlash = Max(LightFlash - (FPSfactor / 70.0), 0)
			Else
				HideEntity Light
				;EntityAlpha(Light, LightFlash)
			EndIf
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\tempname = "navigator" Lor SelectedItem\itemtemplate\tempname = "nav" Then darkA = Max(darkA, 0.5)
			End If
			If SelectedScreen <> Null Then darkA = Max(darkA, 0.5)
			
			If darkA <> 0.0 Then
				ShowEntity(Dark)
				EntityAlpha(Dark, darkA)
			Else
				HideEntity(Dark)
			EndIf	
		EndIf
		;[End block]
		
		;[CONTROLLER]
		If InteractHit(KEY_INV,CK_Inv) And VomitTimer >= 0 And (Not MenuOpen) Then 
			If InvOpen Then
				ResumeSounds()
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
			Else
				PauseSounds()
			EndIf
			InvOpen = Not InvOpen
			If OtherOpen<>Null Then OtherOpen=Null
			SelectedItem = Null 
		EndIf
		
		UpdateGUI()
		
		If gopt\GameMode <> GAMEMODE_UNKNOWN Then
			If InteractHit(KEY_SAVE,CK_Save) Then
				If SelectedDifficulty\saveType = SAVEANYWHERE Then
					RN$ = PlayerRoom\RoomTemplate\Name$
					If RN$ = "173" Lor (RN$ = "exit1" And EntityY(Collider)>1040.0*RoomScale) Lor RN$ = "gatea"
						SetSaveMSG("You cannot save in this location.")
					ElseIf (Not CanSave) Lor QuickLoadPercent > -1
						SetSaveMSG("You cannot save at this moment.")
						If QuickLoadPercent > -1
							Save_MSG = Save_MSG + " (game is loading)"
						EndIf
					Else
						SaveGame(SavePath + CurrSave\Name + "\")
					EndIf
				ElseIf SelectedDifficulty\saveType = SAVEONSCREENS
					If SelectedScreen=Null And SelectedMonitor=Null Then
						SetSaveMSG("You cannot save in this location.")
					Else
						RN$ = PlayerRoom\RoomTemplate\Name$
						If RN$ = "173" Lor (RN$ = "exit1" And EntityY(Collider)>1040.0*RoomScale) Lor RN$ = "gatea"
							SetSaveMSG("You cannot save in this location.")
						ElseIf (Not CanSave) Lor QuickLoadPercent > -1
							SetSaveMSG("You cannot save at this moment.")
							If QuickLoadPercent > -1
								Save_MSG = Save_MSG + " (game is loading)"
							EndIf
						Else
							If SelectedScreen<>Null
								GameSaved = False
								Playable = True
								DropSpeed = 0
							EndIf
							SaveGame(SavePath + CurrSave\Name + "\")
						EndIf
					EndIf
				Else
					SetSaveMSG("Quick saving is disabled.")
				EndIf
			Else If SelectedDifficulty\saveType = SAVEONSCREENS And (SelectedScreen<>Null Lor SelectedMonitor<>Null)
				If (Save_MSG<>"Game progress saved." And Save_MSG<>"You cannot save in this location." And Save_MSG<>"You cannot save at this moment.") Lor Save_MSG_Timer<=0
					SetSaveMSG("Press "+KeyName[KEY_SAVE]+" to save.")
				EndIf
				
				If MouseHit2 Then SelectedMonitor = Null
			EndIf
			
			If KeyHit(KEY_CONSOLE) Then
				If opt\ConsoleEnabled
					If ConsoleOpen Then
						UsedConsole = True
						ResumeSounds()
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
					Else
						PauseSounds()
					EndIf
					ConsoleOpen = (Not ConsoleOpen)
					FlushKeys()
				EndIf
			EndIf
		EndIf
		
		If EndingTimer < 0
			If SelectedEnding <> "" Then UpdateEnding()
		Else
			If mi_I\EndingTimer=0.0 Then UpdateMenu()
		EndIf
		If mi_I\EndingTimer > 0.0
			InvOpen = False
			SelectedItem = Null
			SelectedScreen = Null
			SelectedMonitor = Null
			MenuOpen = False
			ShouldPlay = 66
		EndIf
		
		If MsgTimer>0
			MsgTimer=MsgTimer-FPSfactor2
		EndIf
		
		UpdateAchievementMsg()
		UpdateSaveMSG()
	Wend
	
	;Go out of function immediately if the game has been quit
	If MainMenuOpen Then Return
	
	If FPSfactor > 0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then RenderSecurityCams()
	
	If ShouldUpdateWater<>"" Then
		RenderWater(ShouldUpdateWater)
	EndIf
	
	RenderWorld2(Max(0.0,1.0+(ft\accumulator/ft\tickDuration)))
	
	UpdateBlur(BlurVolume)
	
	DrawGUI()
	
	If EndingTimer < 0 Then
		If SelectedEnding <> "" Then DrawEnding()
	Else
		If mi_I\EndingTimer=0.0 Then DrawMenu()
	EndIf
	DrawMissionEnding()
	
	UpdateConsole(1)
	
	If MsgTimer > 0 Then
		Local temp% = False
		If (Not InvOpen%)
			If SelectedItem <> Null
				If SelectedItem\itemtemplate\tempname = "paper" Lor SelectedItem\itemtemplate\tempname = "oldpaper"
					temp% = True
				EndIf
			EndIf
		EndIf
		
		If (Not temp%)
			SetMsgColor(0, 0, 0)
			Text((opt\GraphicWidth / 2)+1, (opt\GraphicHeight / 2) + 201, Msg, True, False)
			SetMsgColor(255, 255, 255)
			Text((opt\GraphicWidth / 2), (opt\GraphicHeight / 2) + 200, Msg, True, False)
		Else
			SetMsgColor(0, 0, 0)
			Text((opt\GraphicWidth / 2)+1, (opt\GraphicHeight * 0.94) + 1, Msg, True, False)
			SetMsgColor(255, 255, 255)
			Text((opt\GraphicWidth / 2), (opt\GraphicHeight * 0.94), Msg, True, False)
		EndIf
	EndIf
	
	Color 255, 255, 255
	SetFont fo\ConsoleFont
	If opt\ShowFPS Then
		Text 20, 20, "FPS: " + ft\fps : SetFont fo\Font[Font_Default]
	EndIf
	
	DrawQuickLoading()
	
	RenderAchievementMsg()
	RenderSaveMSG()
	
End Function

;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

Function QuickLoadEvents()
	CatchErrors("Uncaught (QuickLoadEvents)")
	
	If QuickLoad_CurrEvent = Null Then
		QuickLoadPercent = -1
		Return
	EndIf
	
	Local e.Events = QuickLoad_CurrEvent
	
	Local r.Rooms,sc.SecurityCams,sc2.SecurityCams,scale#,pvt%,n.NPCs,tex%,i%,x#,z#
	
	;might be a good idea to use QuickLoadPercent to determine the "steps" of the loading process 
	;instead of magic values in e\eventState and e\eventStr
	
	Select e\EventName
		Case "surveil_room"
			;[Block]
			If e\EventState = 0 And e\EventStr <> ""
				If e\EventStr <> "" And Left(e\EventStr,4) <> "load"
					QuickLoadPercent = QuickLoadPercent + 5
					If Int(e\EventStr) > 9
						e\EventStr = "load2"
					Else
						e\EventStr = Int(e\EventStr) + 1
					EndIf
				ElseIf e\EventStr = "load2"
					Local skip = False
					If e\room\NPC[0]=Null Then
						For n.NPCs = Each NPCs
							If n\NPCtype = NPCtype049
								skip = True
								Exit
							EndIf
						Next
						
						If (Not skip)
							e\room\NPC[0] = CreateNPC(NPCtype049,EntityX(e\room\Objects[7],True),EntityY(e\room\Objects[7],True)+5,EntityZ(e\room\Objects[7],True))
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity e\room\NPC[0]\Collider,EntityX(e\room\Objects[7],True),EntityY(e\room\Objects[7],True)+5,EntityZ(e\room\Objects[7],True)
							ResetEntity e\room\NPC[0]\Collider
							RotateEntity e\room\NPC[0]\Collider,0,e\room\angle+180,0
							e\room\NPC[0]\State = 0
							e\room\NPC[0]\PrevState = 2
							
							DebugLog(EntityX(e\room\Objects[7],True)+", "+EntityY(e\room\Objects[7],True)+", "+EntityZ(e\room\Objects[7],True))
						Else
							DebugLog "Skipped 049 spawning in room2sl"
						EndIf
					EndIf
					QuickLoadPercent = 80
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					e\EventState = 1
					If e\EventState2 = 0 Then e\EventState2 = -(70*5)
					
					QuickLoadPercent = 100
				EndIf
			EndIf
			;[End Block]
		Case "room2_closets"
			;[Block]
			If e\EventState = 0
				If e\EventStr = "load0"
					QuickLoadPercent = 10
					If e\room\NPC[0]=Null Then
						e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[0],True),EntityY(e\room\Objects[0],True),EntityZ(e\room\Objects[0],True))
					EndIf
					
					ChangeNPCTextureID(e\room\NPC[0],4)
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					QuickLoadPercent = 20
					e\room\NPC[0]\Sound=LoadSound_Strict("SFX\Room\Storeroom\Escape1.ogg")
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					QuickLoadPercent = 35
					e\room\NPC[0]\SoundChn = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 12)
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					QuickLoadPercent = 55
					If e\room\NPC[1]=Null Then
						e\room\NPC[1] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[1],True),EntityY(e\room\Objects[1],True),EntityZ(e\room\Objects[1],True))
					EndIf
					
					ChangeNPCTextureID(e\room\NPC[1],2)
					e\EventStr = "load4"
				ElseIf e\EventStr = "load4"
					QuickLoadPercent = 80
					e\room\NPC[1]\Sound=LoadSound_Strict("SFX\Room\Storeroom\Escape2.ogg")
					e\EventStr = "load5"
				ElseIf e\EventStr = "load5"
					QuickLoadPercent = 100
					PointEntity e\room\NPC[0]\Collider, e\room\NPC[1]\Collider
					PointEntity e\room\NPC[1]\Collider, e\room\NPC[0]\Collider
					
					e\EventState=1
				EndIf
			EndIf
			;[End Block]
		Case "room3_storage"
			;[Block]
			If e\room\NPC[0]=Null Then
				e\room\NPC[0]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 20
			ElseIf e\room\NPC[1]=Null Then
				e\room\NPC[1]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 50
			ElseIf e\room\NPC[2]=Null Then
				e\room\NPC[2]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 100
			Else
				If QuickLoadPercent > -1 Then QuickLoadPercent = 100
			EndIf
			;[End Block]
		Case "cont_049"
			;[Block]
			If e\EventState = 0 Then
				If e\EventStr = "load0"
					n.NPCs = CreateNPC(NPCtypeZombie, EntityX(e\room\Objects[4],True),EntityY(e\room\Objects[4],True),EntityZ(e\room\Objects[4],True))
					PointEntity n\Collider, e\room\obj
					TurnEntity n\Collider, 0, 190, 0
					QuickLoadPercent = 20
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					n.NPCs = CreateNPC(NPCtypeZombie, EntityX(e\room\Objects[5],True),EntityY(e\room\Objects[5],True),EntityZ(e\room\Objects[5],True))
					PointEntity n\Collider, e\room\obj
					TurnEntity n\Collider, 0, 20, 0
					QuickLoadPercent = 60
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype049
							e\room\NPC[0]=n
							e\room\NPC[0]\State = 2
							e\room\NPC[0]\Idle = 1
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity e\room\NPC[0]\Collider,EntityX(e\room\Objects[4],True),EntityY(e\room\Objects[4],True)+3,EntityZ(e\room\Objects[4],True)
							ResetEntity e\room\NPC[0]\Collider
							Exit
						EndIf
					Next
					If e\room\NPC[0]=Null
						n.NPCs = CreateNPC(NPCtype049, EntityX(e\room\Objects[4],True), EntityY(e\room\Objects[4],True)+3, EntityZ(e\room\Objects[4],True))
						PointEntity n\Collider, e\room\obj
						n\State = 2
						n\Idle = 1
						n\HideFromNVG = True
						e\room\NPC[0]=n
					EndIf
					QuickLoadPercent = 100
					e\EventState=1
				EndIf
			EndIf
			;[End Block]
		Case "cont_205"
			;[Block]
			If e\EventState=0 Lor e\room\Objects[0]=0 Then
				If e\EventStr = "load0"
					e\room\Objects[3] = LoadAnimMesh_Strict("GFX\npcs\205_demon1.b3d")
					QuickLoadPercent = 10
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					e\room\Objects[4] = LoadAnimMesh_Strict("GFX\npcs\205_demon2.b3d")
					QuickLoadPercent = 20
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					e\room\Objects[5] = LoadAnimMesh_Strict("GFX\npcs\205_demon3.b3d")
					QuickLoadPercent = 30
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					e\room\Objects[6] = LoadAnimMesh_Strict("GFX\npcs\205_woman.b3d")
					QuickLoadPercent = 40
					e\EventStr = "load4"
				ElseIf e\EventStr = "load4"
					QuickLoadPercent = 50
					e\EventStr = "load5"
				ElseIf e\EventStr = "load5"
					For i = 3 To 6
						PositionEntity e\room\Objects[i], EntityX(e\room\Objects[0],True), EntityY(e\room\Objects[0],True), EntityZ(e\room\Objects[0],True), True
						RotateEntity e\room\Objects[i], -90, EntityYaw(e\room\Objects[0],True), 0, True
						ScaleEntity(e\room\Objects[i], 0.05, 0.05, 0.05, True)
					Next
					QuickLoadPercent = 70
					e\EventStr = "load6"
				ElseIf e\EventStr = "load6"
						;GiveAchievement(Achv205)
					
					HideEntity(e\room\Objects[3])
					HideEntity(e\room\Objects[4])
					HideEntity(e\room\Objects[5])
					QuickLoadPercent = 100
					e\EventStr = "loaddone"
						;e\EventState = 1
				EndIf
			EndIf
			;[End Block]
		Case "testroom_860"
			;[Block]
			If e\EventStr = "load0"
				QuickLoadPercent = 15
				ForestNPC = CreateSprite()
					;0.75 = 0.75*(410.0/410.0) - 0.75*(width/height)
				ScaleSprite ForestNPC,0.75*(140.0/410.0),0.75
				SpriteViewMode ForestNPC,4
				EntityFX ForestNPC,1+8
				ForestNPCTex = LoadAnimTexture("GFX\npcs\AgentIJ.AIJ",1+2,140,410,0,4)
				ForestNPCData[0] = 0
				EntityTexture ForestNPC,ForestNPCTex,ForestNPCData[0]
				ForestNPCData[1]=0
				ForestNPCData[2]=0
				HideEntity ForestNPC
				e\EventStr = "load1"
			ElseIf e\EventStr = "load1"
				QuickLoadPercent = 40
				e\EventStr = "load2"
			ElseIf e\EventStr = "load2"
				QuickLoadPercent = 100
				If e\room\NPC[0]=Null Then e\room\NPC[0]=CreateNPC(NPCtype860, 0,0,0)
				e\EventStr = "loaddone"
			EndIf
			;[End Block]
		Case "cont_966"
			;[Block]
			If e\EventState = 1
				e\EventState2 = e\EventState2+FPSfactor
				If e\EventState2>30 Then
					If e\EventStr = ""
						CreateNPC(NPCtype966, EntityX(e\room\Objects[0],True), EntityY(e\room\Objects[0],True), EntityZ(e\room\Objects[0],True))
						QuickLoadPercent = 50
						e\EventStr = "load0"
					ElseIf e\EventStr = "load0"
						CreateNPC(NPCtype966, EntityX(e\room\Objects[2],True), EntityY(e\room\Objects[2],True), EntityZ(e\room\Objects[2],True))
						QuickLoadPercent = 100
						e\EventState=2
					EndIf
				Else
					QuickLoadPercent = Int(e\EventState2)
				EndIf
			EndIf
			;[End Block]
		Case "dimension_1499"
			;[Block]
			If e\EventState = 0.0
				If e\EventStr = "load0"
					QuickLoadPercent = 10
					e\room\Objects[0] = LoadMesh_Strict("GFX\map\dimension1499\1499plane.b3d")
						;Local planetex% = LoadTexture_Strict("GFX\map\dimension1499\grit3.jpg")
						;ScaleTexture planetex%,0.5,0.5
						;EntityTexture e\room\Objects[0],planetex%
						;DeleteSingleTextureEntryFromCache planetex%
					HideEntity e\room\Objects[0]
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					QuickLoadPercent = 30
					NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
					e\EventStr = 1
				Else
					If Int(e\EventStr)<16
						QuickLoadPercent = QuickLoadPercent + 2
						e\room\Objects[Int(e\EventStr)] = LoadMesh_Strict("GFX\map\dimension1499\1499object"+(Int(e\EventStr))+".b3d")
						HideEntity e\room\Objects[Int(e\EventStr)]
						e\EventStr = Int(e\EventStr)+1
					ElseIf Int(e\EventStr)=16
						QuickLoadPercent = 90
						CreateChunkParts(e\room)
						e\EventStr = 17
					ElseIf Int(e\EventStr) = 17
						QuickLoadPercent = 100
						x# = EntityX(e\room\obj)
						z# = EntityZ(e\room\obj)
						Local ch.Chunk
						For i = -2 To 0 Step 2
							ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#,True)
						Next
						For i = -2 To 0 Step 2
							ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#-40,True)
						Next
						e\EventState = 2.0
						e\EventStr = 18
					EndIf
				EndIf
			EndIf
			;[End Block]
	End Select
	
	CatchErrors("QuickLoadEvents "+e\EventName)
	
End Function

Function Kill()
	If GodMode Then Return
	
	psp\Health = 0
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If KillTimer >= 0 Then
		KillAnim = Rand(0,1)
		PlaySound_Strict(DamageSFX[0])
		If SelectedDifficulty\permaDeath Then
			DeleteGame(CurrSave)
			LoadSaveGames()
		EndIf
		
		KillTimer = Min(-1, KillTimer)
		ShowEntity Head
		PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity (Head)
		RotateEntity(Head, 0, EntityYaw(Camera), 0)		
	EndIf
End Function

Function DrawEnding()
	
	ShowPointer()
	
	Local x,y,width,height, temp
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(SelectedEnding)
		Case "b2", "a1"
			ClsColor Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0)
		Default
			ClsColor 0,0,0
	End Select
	
	Cls
	
	If EndingTimer<-200 Then
		If EndingTimer > -700 Then 
			
			;-200 -> -700
			;Max(50 - (Abs(KillTimer)-200),0)    =    0->50
			If Rand(1,150)<Min((Abs(EndingTimer)-200),155) Then
				DrawImage EndingScreen, opt\GraphicWidth/2-400, opt\GraphicHeight/2-400
			Else
				Color 0,0,0
				Rect 100,100,opt\GraphicWidth-200,opt\GraphicHeight-200
				Color 255,255,255
			EndIf
			
		Else
			If EndingTimer > -2000 Then
				DrawImage EndingScreen, opt\GraphicWidth/2-400, opt\GraphicHeight/2-400
			EndIf
			
			If EndingTimer < -1000 And EndingTimer > -2000 Then
				width = ImageWidth(PauseMenuIMG)
				height = ImageHeight(PauseMenuIMG)
				x = opt\GraphicWidth / 2 - width / 2
				y = opt\GraphicHeight / 2 - height / 2
				
				DrawImage PauseMenuIMG, x, y
				
				Color(255, 255, 255)
				SetFont fo\Font[Font_Menu]
				Text(x + width / 2 + 40*MenuScale, y + 20*MenuScale, "THE END", True)
				SetFont fo\Font[Font_Default]
				
				If AchievementsMenu=0 Then 
					x = x+132*MenuScale
					y = y+122*MenuScale
					
					Local roomamount = 0, roomsfound = 0
					For r.Rooms = Each Rooms
						roomamount = roomamount + 1
						roomsfound = roomsfound + r\found
					Next
					
					Local docamount=0, docsfound=0
					For itt.ItemTemplates = Each ItemTemplates
						If itt\tempname = "paper" Then
							docamount=docamount+1
							docsfound=docsfound+itt\found
						EndIf
					Next
					
;					Local scpsEncountered=1
;					For i = 0 To 24
;						scpsEncountered = scpsEncountered+Achievements[i]\Unlocked
;					Next
;					
;					Local achievementsUnlocked =0
;					For i = 0 To MAXACHIEVEMENTS-1
;						achievementsUnlocked = achievementsUnlocked + Achievements[i]\Unlocked
;					Next
					
					Text x, y, "SCPs encountered: " +scpsEncountered
					Text x, y+20*MenuScale, "Achievements unlocked: " + achievementsUnlocked+"/"+MAXACHIEVEMENTS
					Text x, y+40*MenuScale, "Rooms found: " + roomsfound+"/"+roomamount
					Text x, y+60*MenuScale, "Documents discovered: " +docsfound+"/"+docamount
					Text x, y+80*MenuScale, "Items refined in SCP-914: " +RefinedItems			
					
					x = opt\GraphicWidth / 2 - width / 2
					y = opt\GraphicHeight / 2 - height / 2
					x = x+width/2
					y = y+height-100*MenuScale
					
					
				Else
					DrawMenu()
				EndIf
			;Credits
			ElseIf EndingTimer<=-2000 Then
				DrawCredits()
			EndIf
			
		EndIf
		
	EndIf
	
	SetFont fo\Font[Font_Default]
	
	DrawAllMenuButtons()
	
End Function

Function UpdateEnding()
	
	FPSfactor = 0
	;EndingTimer=EndingTimer-FPSfactor2
	If EndingTimer>-2000
		EndingTimer=Max(EndingTimer-FPSfactor2,-1111)
	Else
		EndingTimer=EndingTimer-FPSfactor2*(opt\GraphicHeight/800.0)
	EndIf
	
	GiveAchievement(Achv055)
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\name = "Keter" Then GiveAchievement(AchvKeter)
	Local x,y,width,height, temp
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(SelectedEnding)
		Case "b2", "a1"
			;ClsColor Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0)
		Default
			;ClsColor 0,0,0
	End Select
	
	ShouldPlay = 66
	
	If EndingTimer<-200 Then
		
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel BreathCHN : Stamina = 100
		EndIf
		
		;If EndingTimer <-400 Then 
		;	ShouldPlay = 13
		;EndIf
		
		If EndingScreen = 0 Then
			EndingScreen = LoadImage_Strict("GFX\endingscreen.pt")
			
			ShouldPlay = 23
			CurrMusicVolume = opt\MusicVol
			
			CurrMusicVolume = opt\MusicVol
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\"+Music[23]+".ogg",CurrMusicVolume,0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict LightSFX
		EndIf
		
		If EndingTimer > -700 Then 
			
			;-200 -> -700
			;Max(50 - (Abs(KillTimer)-200),0)    =    0->50
			If Rand(1,150)<Min((Abs(EndingTimer)-200),155) Then
				
			Else
				
			EndIf
			
			If EndingTimer+FPSfactor2 > -450 And EndingTimer <= -450 Then
				Select Lower(SelectedEnding)
					Case "a1", "a2"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateA\Ending"+SelectedEnding+".ogg")
					Case "b1", "b2", "b3"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateB\Ending"+SelectedEnding+".ogg")
				End Select
			EndIf			
			
		Else
			
			If EndingTimer < -1000 And EndingTimer > -2000
				
				width = ImageWidth(PauseMenuIMG)
				height = ImageHeight(PauseMenuIMG)
				x = opt\GraphicWidth / 2 - width / 2
				y = opt\GraphicHeight / 2 - height / 2
				
				If AchievementsMenu=0 Then 
					x = x+132*MenuScale
					y = y+122*MenuScale
					
					Local roomamount = 0, roomsfound = 0
					For r.Rooms = Each Rooms
						roomamount = roomamount + 1
						roomsfound = roomsfound + r\found
					Next
					
					Local docamount=0, docsfound=0
					For itt.ItemTemplates = Each ItemTemplates
						If itt\tempname = "paper" Then
							docamount=docamount+1
							docsfound=docsfound+itt\found
						EndIf
					Next
					
;					Local scpsEncountered=1
;					For i = 0 To 24
;						scpsEncountered = scpsEncountered+Achievements[i]\Unlocked
;					Next
;					
;					Local achievementsUnlocked =0
;					For i = 0 To MAXACHIEVEMENTS-1
;						achievementsUnlocked = achievementsUnlocked + Achievements[i]\Unlocked
;					Next
					
					x = opt\GraphicWidth / 2 - width / 2
					y = opt\GraphicHeight / 2 - height / 2
					x = x+width/2
					y = y+height-100*MenuScale
					
					If DrawButton(x-145*MenuScale,y-200*MenuScale,390*MenuScale,60*MenuScale,"ACHIEVEMENTS", True) Then
						AchievementsMenu = 1
						DeleteMenuGadgets()
					EndIf
					
					If DrawButton(x-145*MenuScale,y-100*MenuScale,390*MenuScale,60*MenuScale,"MAIN MENU", True)
						LoadCredits()
					EndIf
				Else
					ShouldPlay = 23
					;DrawMenu()
					UpdateMenu()
				EndIf
			;Credits
			ElseIf EndingTimer<=-2000
				UpdateCredits()
			EndIf
			
		EndIf
		
	EndIf
	
End Function

Type CreditsLine
	Field txt$
	Field id%
	Field stay%
End Type

Const MaxCreditScreens% = 3
Global CreditsTimer# = 0.0
Global CreditsScreen[MaxCreditScreens]
Global CreditsScreenCurrent% = 0
Global CreditsScreenNext% = 0

Function LoadCredits()
	Local i%
	
	ShouldPlay = 24
	NowPlaying = ShouldPlay
	For i = 0 To 9
		If TempSounds[i] <> 0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	StopStream_Strict(MusicCHN)
	MusicCHN = StreamSound_Strict("SFX\Music\"+Music[NowPlaying]+".ogg",0.0,Mode)
	SetStreamVolume_Strict(MusicCHN,1.0*aud\MusicVol)
	FlushKeys()
	EndingTimer=-2000
	DeleteMenuGadgets()
	InitCredits()
	
End Function

Function InitCredits()
	Local cl.CreditsLine, d.Doors
	Local file% = OpenFile("Credits.txt")
	Local l$, i%
	
	CreditsFont% = LoadFont_Strict("GFX\font\Courier New.ttf", Int(21 * (opt\GraphicHeight / 1024.0)))
	CreditsFont2% = LoadFont_Strict("GFX\font\Courier New.ttf", Int(35 * (opt\GraphicHeight / 1024.0))) ;TODO make this use a bold font!
	
	CreditsScreen[0] = LoadRMesh("GFX\map\rooms\room2_tunnel_1\tunnel_opt.rmesh", Null)
	CreditsScreen[1] = LoadRMesh("GFX\map\rooms\room2_tunnel_2\tunnel2_opt.rmesh", Null)
	CreditsScreen[2] = LoadRMesh("GFX\map\rooms\room2_tunnel_3\tunnel3.rmesh", Null)
	For i = 0 To (MaxCreditScreens-1)
		ScaleEntity CreditsScreen[i], RoomScale, RoomScale, RoomScale
		PositionEntity CreditsScreen[i], 0, 100 - 0.75, 2048.0 * RoomScale
		HideEntity CreditsScreen[i]
	Next
	CreditsScreenCurrent = Rand(0, (MaxCreditScreens-1))
	CreditsScreenNext = CreditsScreenCurrent
	While CreditsScreenCurrent = CreditsScreenNext
		CreditsScreenNext = Rand(0, (MaxCreditScreens-1))
	Wend
	ShowEntity CreditsScreen[CreditsScreenCurrent]
	ShowEntity CreditsScreen[CreditsScreenNext]
	PositionEntity CreditsScreen[CreditsScreenNext], 0, 100 - 0.75, 4096.0 * RoomScale
	Camera = FreeEntity_Strict(Camera)
	Camera = CreateCamera()
	CameraFogMode Camera, 1
	CameraRange Camera, 0.01, 5
	CameraFogRange Camera, 0.5, 2.5
	Local FogColorR% = Left(FogColor_HCZ,3)
	Local FogColorG% = Mid(FogColor_HCZ,4,3)
	Local FogColorB% = Right(FogColor_HCZ,3)
	CameraFogColor Camera,FogColorR,FogColorG,FogColorB
	CameraClsColor Camera,FogColorR,FogColorG,FogColorB
	PositionEntity Camera, 0, 100, 1024.0 * RoomScale
	
	d = CreateDoor(0, 0.0, 100 - 0.75, 3072.0 * RoomScale, 0, Null, True, 2)
	MoveEntity d\obj, 0, -10.0, 0
	MoveEntity d\obj2, 0, -10.0, 0
	
	Repeat
		l = ReadLine(file)
		cl = New CreditsLine
		cl\txt = l
	Until Eof(file)
	
	Delete First CreditsLine
	CreditsTimer = 0
	
End Function

Function DrawCredits()
    Local credits_Y# = (EndingTimer+2000)/2+(opt\GraphicHeight+10)
    Local cl.CreditsLine
    Local id%
    Local endlinesamount%
	Local LastCreditLine.CreditsLine
	
	CameraProjMode Camera, 1
	RenderWorld(Max(0.0,1.0+(ft\accumulator/ft\tickDuration)))
	
	id = 0
	endlinesamount = 0
	LastCreditLine = Null
	Color 255,255,255
	For cl = Each CreditsLine
		cl\id = id
		If Left(cl\txt,1) = "*" Then
			SetFont CreditsFont2
			If (Not cl\stay) Then
				Text opt\GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
			EndIf
		ElseIf Left(cl\txt,1) = "/" Then
			LastCreditLine = Before(cl)
		Else
			SetFont CreditsFont
			If cl\stay = False Then
				Text opt\GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),cl\txt,True
			EndIf
		EndIf
		If LastCreditLine<>Null Then
			If cl\id>LastCreditLine\id Then
				cl\stay = True
			EndIf
		EndIf
		If cl\stay Then
			endlinesamount = endlinesamount + 1
		EndIf
		id = id + 1
	Next
	If (credits_Y+(24*LastCreditLine\id*MenuScale)) < -StringHeight(LastCreditLine\txt) Then
		If CreditsTimer >= 0.0 And CreditsTimer < 255.0 Then
			Color Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0)
		ElseIf CreditsTimer >= 255.0 Then
			Color 255,255,255
		Else
			Color Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0)
		EndIf
	Else
		Color 0,0,0
	EndIf
	If CreditsTimer <> 0.0 Then
		For cl = Each CreditsLine
			If cl\stay Then
				SetFont CreditsFont
				If Left(cl\txt,1) = "/" Then
					Text opt\GraphicWidth/2,(opt\GraphicHeight/2)+(endlinesamount/2)+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
				Else
					Text opt\GraphicWidth/2,(opt\GraphicHeight/2)+(24*(cl\id-LastCreditLine\id)*MenuScale)-((endlinesamount/2)*24*MenuScale),cl\txt,True
				EndIf
			EndIf
		Next
	EndIf
    
End Function

Function UpdateCredits()
	Local credits_Y# = (EndingTimer+2000)/2+(opt\GraphicHeight+10)
    Local cl.CreditsLine
    Local id%, i%
    Local endlinesamount%
	Local LastCreditLine.CreditsLine
	
	ShouldPlay = 24
	
	MoveEntity Camera, 0, 0, 0.008*FPSfactor2
	If EntityZ(Camera) > 3072.0 * RoomScale Then
		CreditsScreenCurrent = CreditsScreenNext
		While CreditsScreenCurrent = CreditsScreenNext
			CreditsScreenNext = Rand(0, (MaxCreditScreens-1))
		Wend
		For i = 0 To (MaxCreditScreens-1)
			HideEntity CreditsScreen[i]
		Next
		ShowEntity CreditsScreen[CreditsScreenCurrent]
		ShowEntity CreditsScreen[CreditsScreenNext]
		PositionEntity CreditsScreen[CreditsScreenCurrent], 0, 100 - 0.75, 2048.0 * RoomScale
		PositionEntity CreditsScreen[CreditsScreenNext], 0, 100 - 0.75, 4096.0 * RoomScale
		PositionEntity Camera, EntityX(Camera), EntityY(Camera), EntityZ(Camera) - 2048.0 * RoomScale
		CaptureWorld()
	EndIf
	
	id = 0
	endlinesamount = 0
	LastCreditLine = Null
	
	For cl = Each CreditsLine
		cl\id = id
		If Left(cl\txt,1) = "/" Then
			LastCreditLine = Before(cl)
		EndIf
		If LastCreditLine <> Null Then
			If cl\id>LastCreditLine\id Then
				cl\stay = True
			EndIf
		EndIf
		If cl\stay Then
			endlinesamount = endlinesamount + 1
		EndIf
		id = id + 1
	Next
	If (credits_Y+(24*LastCreditLine\id*MenuScale)) < -StringHeight(LastCreditLine\txt) Then
		CreditsTimer=CreditsTimer+(0.5*FPSfactor2)
		If CreditsTimer>=255.0 Then
			If CreditsTimer > 500.0 Then
				CreditsTimer = -255.0
			EndIf
		ElseIf CreditsTimer < 0.0 Then
			If CreditsTimer >= -1.0 Then
				CreditsTimer = -1.0
			EndIf
		EndIf
	EndIf
	
	If GetKey() Then CreditsTimer = -1
	
	If CreditsTimer = -1 Then
		FreeFont CreditsFont
		FreeFont CreditsFont2
		For i = 0 To (MaxCreditScreens-1)
			CreditsScreen[i] = 0
		Next
		CreditsScreenCurrent = 0
		CreditsScreenNext = 0
		FreeImage EndingScreen
		EndingScreen = 0
		Delete Each CreditsLine
		Local prevMainMenuOpen = MainMenuOpen
		If (Not prevMainMenuOpen) Then
			MainMenuOpen = True
			NullGame(False, False)
		Else
			EndingTimer = 0
			Reload()
		EndIf
		If (Not prevMainMenuOpen) Then
			StopStream_Strict(MusicCHN)
			ShouldPlay = 21
		EndIf
		MenuOpen = False
        MainMenuTab = 0
        CurrSave = Null
        FlushKeys()
	EndIf
	
End Function

Function SetSaveMSG(txt$)
	
	Save_MSG = txt
	Save_MSG_Timer = 0.0
	Save_MSG_Y = 0.0
	
End Function

Function UpdateSaveMSG()
	Local scale# = opt\GraphicHeight/768.0
	;Local width% = 200*scale
	Local width = StringWidth(Save_MSG)+20*scale
	Local height% = 30*scale
	Local x% = (opt\GraphicWidth/2)-(width/2)
	Local y% = (-height)+Save_MSG_Y
	
	If Save_MSG <> ""
		If Save_MSG_Timer < 70*5
			If Save_MSG_Y < height
				Save_MSG_Y = Min(Save_MSG_Y+2*FPSfactor2,height)
			Else
				Save_MSG_Y = height
			EndIf
			Save_MSG_Timer = Save_MSG_Timer + FPSfactor2
		Else
			If Save_MSG_Y > 0
				Save_MSG_Y = Max(Save_MSG_Y-2*FPSfactor2,0)
			Else
				SetSaveMSG("")
			EndIf
		EndIf
	EndIf
	
End Function

Function RenderSaveMSG()
	Local scale# = opt\GraphicHeight/768.0
	;Local width% = 200*scale
	Local width = StringWidth(Save_MSG)+20*scale
	Local height% = 30*scale
	Local x% = (opt\GraphicWidth/2)-(width/2)
	Local y% = (-height)+Save_MSG_Y
	
	If Save_MSG <> ""
		DrawFrame(x,y,width,height)
		Color 255,255,255
		SetFont fo\Font[Font_Default]
		Text(opt\GraphicWidth/2,y+(height/2),Save_MSG,True,True)
	EndIf
	
End Function

;--------------------------------------- player controls -------------------------------------------

Function MovePlayer()
	CatchErrors("Uncaught (MovePlayer)")
	Local Sprint# = 1.0, Speed# = 0.018, i%, angle#
	
	;IsPlayerSprinting% = False
	
	If SuperMan Then
		Speed = Speed * 3
		
		SuperManTimer=SuperManTimer+FPSfactor
		
		CameraShake = Sin(SuperManTimer / 5.0) * (SuperManTimer / 1500.0)
		
		If SuperManTimer > 70 * 50 Then
			DeathMSG = "A complete set of armor for MTF units was found in [DATA REDACTED]. Upon further examination, the armor was found to be filled with 12.5 kilograms of blue ash-like substance. "
			DeathMSG = DeathMSG + "Chemical analysis of the substance remains non-conclusive. Most likely related to SCP-914."
			Kill()
			ShowEntity Fog
		Else
			BlurTimer = 500		
			HideEntity Fog
		EndIf
	End If
	
	If DeathTimer > 0 Then
		DeathTimer=DeathTimer-FPSfactor
		If DeathTimer < 1 Then DeathTimer = -1.0
	ElseIf DeathTimer < 0 
		Kill()
	EndIf
	
	If CurrSpeed > 0 Then
        Stamina = Min(Stamina + 0.15 * FPSfactor/1.25, 100.0)
    Else
        Stamina = Min(Stamina + 0.15 * FPSfactor*1.25, 100.0)
    EndIf
	
	If StaminaEffectTimer > 0 Then
		StaminaEffectTimer = StaminaEffectTimer - (FPSfactor/70)
	Else
		If StaminaEffect <> 1.0 Then StaminaEffect = 1.0
	EndIf
	
	Local temp#
	
	If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then
		If KeyDown(KEY_SPRINT) And (Not KeyDown(KEY_CROUCH)) Then
			If Stamina < 5 Then
				temp = 0
				If WearingGasMask>0 Lor Wearing1499>0 Then temp=1
				If ChannelPlaying(BreathCHN)=False Then BreathCHN = PlaySound_Strict(BreathSFX[temp * 5])
			ElseIf Stamina < 50
				If BreathCHN=0 Then
					temp = 0
					If WearingGasMask>0 Lor Wearing1499>0 Then temp=1
					BreathCHN = PlaySound_Strict(BreathSFX[temp * 5 + Rand(1,3)])
					ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*opt\SFXVolume*opt\MasterVol
				Else
					If ChannelPlaying(BreathCHN)=False Then
						temp = 0
						If WearingGasMask>0 Lor Wearing1499>0 Then temp=1
						BreathCHN = PlaySound_Strict(BreathSFX[temp * 5 + Rand(1,3)])
						ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*opt\SFXVolume*opt\MasterVol		
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount-1
		If Inventory[i]<>Null Then
			If Inventory[i]\itemtemplate\tempname = "finevest" Then Stamina = Min(Stamina, 60)
		EndIf
	Next
	
	If Wearing714 Then 
		Stamina = Min(Stamina, 10)
		Sanity = Max(-850, Sanity)
	EndIf
	
	If IsZombie Then Crouch = False
	
	If (Not psp\NoMove) Then
		If Abs(CrouchState-Crouch)<0.001 Then 
			CrouchState = Crouch
		Else
			CrouchState = CurveValue(Crouch, CrouchState, 10.0)
		EndIf
		
		If (Not NoClip) Then
			;[CONTROLLER]
			If co\Enabled
				Local case1% = 0
				Local case2% = ((GetLeftAnalogStickPitch()<>0 Lor GetLeftAnalogStickYaw()<>0) And Playable)
			Else
				case1% = ((KeyDown(KEY_DOWN) Xor KeyDown(KEY_UP)) Lor (KeyDown(KEY_RIGHT) Xor KeyDown(KEY_LEFT)) And Playable)
				case2% = 0
			EndIf
			If case1% Lor case2% Lor ForceMove>0
				
				;[CONTROLLER]
				Local SprintKeyAssigned = False
				If (Not co\Enabled)
					If KeyDown(KEY_SPRINT) Then SprintKeyAssigned = True
				Else
					If IsPlayerSprinting
						If JoyHit(CK_Sprint)
							SprintKeyAssigned = 0
						Else
							SprintKeyAssigned = 1
						EndIf
					Else
						If JoyHit(CK_Sprint)
							SprintKeyAssigned = 1
						Else
							SprintKeyAssigned = 0
						EndIf
					EndIf
				EndIf
				
				IsPlayerSprinting% = False
				
				If Crouch = 0 And (SprintKeyAssigned) And Stamina > 0.0 And (Not IsZombie) And g_I\IronSight = 0 Then
					Sprint = 2.5
					IsPlayerSprinting% = True
					;Stamina = Stamina - FPSfactor * 0.4 * StaminaEffect
					Stamina = Stamina - FPSfactor * 0.25 * StaminaEffect
					If Stamina <= 0 Then Stamina = -20.0
				End If
				
				If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then 
					If EntityY(Collider)<2000*RoomScale Lor EntityY(Collider)>2608*RoomScale Then
						Stamina = 0
						IsPlayerSprinting% = False
						Speed = 0.015
						Sprint = 1.0					
					EndIf
				EndIf	
				
				If ForceMove>0 Then Speed=Speed*ForceMove
				
				If SelectedItem<>Null Then
					If SelectedItem\itemtemplate\tempname = "firstaid" Lor SelectedItem\itemtemplate\tempname = "finefirstaid" Lor SelectedItem\itemtemplate\tempname = "firstaid2" Then 
						Sprint = 0
						IsPlayerSprinting% = False
					EndIf
				EndIf
				
				Sprint = (Sprint / (1.0+Crouch))
				
				temp# = (Shake Mod 360)
				Local tempchn%
				If (Not UnableToMove%) Then Shake# = (Shake + FPSfactor * Min(Sprint, 1.7) * 10) Mod 720
				If temp < 180 And (Shake Mod 360) >= 180 And KillTimer>=0 Then
					If CurrStepSFX=0 Then
						temp = GetStepSound(Collider)
						If Sprint = 1.0 Then
							PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
							tempchn% = PlaySound_Strict(mpl\StepSoundWalk[Rand(0,MaxStepSounds-1)+(temp*MaxStepSounds)])
							ChannelVolume tempchn, (1.0-(Crouch*0.6))*(opt\SFXVolume*opt\MasterVol)
						Else
							PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
							tempchn% = PlaySound_Strict(mpl\StepSoundRun[Rand(0,MaxStepSounds-1)+(temp*MaxStepSounds)])
							ChannelVolume tempchn, (1.0-(Crouch*0.6))*(opt\SFXVolume*opt\MasterVol)
						EndIf
					ElseIf CurrStepSFX=1 Then
						tempchn% = PlaySound_Strict(Step2SFX[Rand(0, 2)])
						ChannelVolume tempchn, (1.0-(Crouch*0.4))*(opt\SFXVolume*opt\MasterVol)
					ElseIf CurrStepSFX=2 Then
						tempchn% = PlaySound_Strict(Step2SFX[Rand(3,5)])
						ChannelVolume tempchn, (1.0-(Crouch*0.4))*(opt\SFXVolume*opt\MasterVol)
					ElseIf CurrStepSFX=3 Then
						If Sprint = 1.0 Then
							PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
							tempchn% = PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
							ChannelVolume tempchn, (1.0-(Crouch*0.6))*(opt\SFXVolume*opt\MasterVol)
						Else
							PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
							tempchn% = PlaySound_Strict(StepSFX(0, 1, Rand(0, 7)))
							ChannelVolume tempchn, (1.0-(Crouch*0.6))*(opt\SFXVolume*opt\MasterVol)
						EndIf
					EndIf
	;				If ItemAmount>0
	;					If Sprint = 1.0
	;						tempchn% = PlaySound_Strict(EquipmentSFX[Rand(0,7)])
	;						ChannelVolume tempchn,((1.0-(Crouch*0.6)*(ItemAmount/10.0))*opt\SFXVolume)
	;					Else
	;						tempchn% = PlaySound_Strict(EquipmentSFX[8 + Rand(0,7)])
	;						ChannelVolume tempchn,((1.0-(Crouch*0.6)*(ItemAmount/10.0))*opt\SFXVolume)
	;					EndIf
	;				EndIf
				EndIf
				
				Sprint = (Sprint * (1.0+Crouch))
			Else
				IsPlayerSprinting% = False
			EndIf
		Else ;noclip on
			;[CONTROLLER]
			If (Not co\Enabled)
				If (KeyDown(KEY_SPRINT)) Then 
					Sprint = 2.5
				ElseIf KeyDown(KEY_CROUCH)
					Sprint = 0.5
				EndIf
			Else
				If IsPlayerSprinting
					If JoyDown(CK_Sprint)
						Sprint = 2.5
					ElseIf JoyDown(CK_Crouch)
						Sprint = 0.5
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	;[CONTROLLER]
	If opt\HoldToCrouch Then
		If Playable Then
			If (Not co\Enabled) Then
				Crouch = KeyDown(KEY_CROUCH)
			Else
				Crouch = JoyDown(CK_Crouch)
			EndIf
		EndIf
	Else
		If (Not co\Enabled) Then
			If KeyHit(KEY_CROUCH) And Playable Then Crouch = (Not Crouch)
		Else
			If JoyHit(CK_Crouch) And Playable Then Crouch = (Not Crouch)
		EndIf
	EndIf
	
	Local temp2# = (Speed * Sprint) / (1.0+CrouchState)
	
	If NoClip Then 
		Shake = 0
		CurrSpeed = 0
		CrouchState = 0
		Crouch = 0
		
		RotateEntity Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0
		
		temp2 = temp2 * NoClipSpeed
		
		;[CONTROLLER]
		If (Not co\Enabled)
			If KeyDown(KEY_DOWN) Then MoveEntity Collider, 0, 0, -temp2*FPSfactor
			If KeyDown(KEY_UP) Then MoveEntity Collider, 0, 0, temp2*FPSfactor
			
			If KeyDown(KEY_LEFT) Then MoveEntity Collider, -temp2*FPSfactor, 0, 0
			If KeyDown(KEY_RIGHT) Then MoveEntity Collider, temp2*FPSfactor, 0, 0
		Else
			If GetLeftAnalogStickPitch()<0.0
				MoveEntity Collider, 0, 0, -temp2*FPSfactor
			EndIf
			If GetLeftAnalogStickPitch()>0.0
				MoveEntity Collider, 0, 0, temp2*FPSfactor
			EndIf
			If GetLeftAnalogStickYaw(True)<0.0
				MoveEntity Collider, -temp2*FPSfactor, 0, 0
			EndIf
			If GetLeftAnalogStickYaw(True)>0.0
				MoveEntity Collider, temp2*FPSfactor, 0, 0
			EndIf
		EndIf
		
		ResetEntity Collider
	Else
		;temp2# = temp2 / Max((Injuries+3.0)/3.0,1.0)
		;If Injuries > 0.5 Then 
		;	temp2 = temp2*Min((Sin(Shake/2)+1.2),1.0)
		;EndIf
		
		If (Not psp\NoMove) Then
			temp = False
			If (Not IsZombie%)
				If (Not co\Enabled)
					If KeyDown(KEY_DOWN) And (Not KeyDown(KEY_UP)) And Playable Then
						temp = True 
						angle = 180
						If KeyDown(KEY_LEFT) And (Not KeyDown(KEY_RIGHT)) Then angle = 135 
						If KeyDown(KEY_RIGHT) And (Not KeyDown(KEY_LEFT)) Then angle = -135
					ElseIf KeyDown(KEY_UP) And (Not KeyDown(KEY_DOWN)) And Playable Then
						temp = True
						angle = 0
						If KeyDown(KEY_LEFT) And (Not KeyDown(KEY_RIGHT)) Then angle = 45
						If KeyDown(KEY_RIGHT) And (Not KeyDown(KEY_LEFT)) Then angle = -45
					ElseIf ForceMove>0 Then
						temp=True
						angle = ForceAngle
					Else If Playable Then
						If KeyDown(KEY_LEFT) And (Not KeyDown(KEY_RIGHT)) Then angle = 90 : temp = True
						If KeyDown(KEY_RIGHT) And (Not KeyDown(KEY_LEFT)) Then angle = -90 : temp = True 
					EndIf
				Else
					;[CONTROLLER]
					If GetLeftAnalogStickPitch()<0.0 And Playable
						temp = True
						angle = 180
						If GetLeftAnalogStickYaw(True)<>0.0
							angle = GetLeftAnalogStickYaw(True,True)*(180.0-(45.0*Abs(GetLeftAnalogStickYaw())))
						EndIf
					ElseIf GetLeftAnalogStickPitch()>0.0 And Playable
						temp = True
						angle = 0
						If GetLeftAnalogStickYaw(True)<>0.0
							angle = GetLeftAnalogStickYaw(True,True)*(45.0*Abs(GetLeftAnalogStickYaw()))
						EndIf
					ElseIf ForceMove>0
						temp = True
						angle = ForceAngle
					ElseIf Playable
						If GetLeftAnalogStickYaw(True)<>0.0
							;angle = GetLeftAnalogStickYaw(True,True)*(90*Abs(GetLeftAnalogStickYaw()))
							angle = GetLeftAnalogStickYaw(True,True)*90.0
							temp = True
						EndIf
					EndIf
				EndIf
			Else
				temp=True
				angle = ForceAngle
			EndIf
			
			angle = WrapAngle(EntityYaw(Collider,True)+angle+90.0)
			
			If temp Then 
				CurrSpeed = CurveValue(temp2, CurrSpeed, 20.0)
			Else
				CurrSpeed = Max(CurveValue(0.0, CurrSpeed-0.1, 1.0),0.0)
			EndIf
			
			If (Not UnableToMove%) Then TranslateEntity Collider, Cos(angle)*CurrSpeed * FPSfactor, 0, Sin(angle)*CurrSpeed * FPSfactor, True
		EndIf
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Collider)
			If CollisionY(Collider, i) < EntityY(Collider) - 0.25 Then 
				CollidedFloor = True
			EndIf
		Next
		
		If CollidedFloor = True Then
			If DropSpeed# < - 0.07 Then
				If CurrStepSFX=0 Then
					PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX=1
					PlaySound_Strict(Step2SFX[Rand(0, 2)])
				ElseIf CurrStepSFX=2
					PlaySound_Strict(Step2SFX[Rand(3, 5)])
				ElseIf CurrStepSFX=3
					PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
				EndIf
				PlayerSoundVolume = Max(3.0,PlayerSoundVolume)
			EndIf
			DropSpeed# = 0
		Else
			DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
		EndIf
		
		If (Not UnableToMove%) And ShouldEntitiesFall Then 
			TranslateEntity Collider, 0, DropSpeed * FPSfactor, 0
		EndIf
	EndIf
	
	ForceMove = False
	
;	If Injuries > 1.0 Then
;		temp2 = Bloodloss
;		BlurTimer = Max(Max(Sin(MilliSecs()/100.0)*Bloodloss*30.0,Bloodloss*2*(2.0-CrouchState)),BlurTimer)
;		Bloodloss = Min(Bloodloss + (Min(Injuries,3.5)/300.0)*FPSfactor,100)
;		
;		If temp2 <= 60 And Bloodloss > 60 Then
;			Msg = "You are feeling faint from the amount of blood you loss."
;			MsgTimer = 70*4
;		EndIf
;	EndIf
	
	UpdateInfect()
	
;	If Bloodloss > 0 Then
;		If Rnd(200)<Min(Injuries,4.0) Then
;			pvt = CreatePivot()
;			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
;			TurnEntity pvt, 90, 0, 0
;			EntityPick(pvt,0.3)
;			de.decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BLOODDROP), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
;			de\size = Rnd(0.03,0.08)*Min(Injuries,3.0) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
;			tempchn% = PlaySound_Strict (DripSFX[Rand(0,3)])
;			ChannelVolume tempchn, Rnd(0.0,0.8)*(opt\SFXVolume*opt\MasterVol)
;			ChannelPitch tempchn, Rand(20000,30000)
;			
;			pvt = FreeEntity_Strict(pvt)
;		EndIf
;		
;		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs())/20.0)+1.0)*Bloodloss*0.2)
;		
;		If Bloodloss > 60 Then Crouch = True
;		If Bloodloss => 100 Then 
;			Kill()
;			HeartBeatVolume = 0.0
;		ElseIf Bloodloss > 80.0
;			HeartBeatRate = Max(150-(Bloodloss-80)*5,HeartBeatRate)
;			HeartBeatVolume = Max(HeartBeatVolume, 0.75+(Bloodloss-80.0)*0.0125)	
;		ElseIf Bloodloss > 35.0
;			HeartBeatRate = Max(70+Bloodloss,HeartBeatRate)
;			HeartBeatVolume = Max(HeartBeatVolume, (Bloodloss-35.0)/60.0)			
;		EndIf
;	EndIf
	
	If HealTimer > 0 Then
		DebugLog HealTimer
		HealTimer = HealTimer - (FPSfactor / 70)
		;Bloodloss = Min(Bloodloss + (2 / 400.0) * FPSfactor, 100)
		;Injuries = Max(Injuries - (FPSfactor / 70) / 30, 0.0)
		HealSPPlayer(0.01 * FPSfactor)
	EndIf
	
	If Playable Then
		If (Not co\Enabled)
			If KeyHit(KEY_BLINK) Then BlinkTimer = 0
			If KeyDown(KEY_BLINK) And BlinkTimer < - 10 Then BlinkTimer = -10
		Else
			;[CONTROLLER]
			If JoyHit(CK_Blink) Then BlinkTimer = 0
			If JoyDown(CK_Blink) And BlinkTimer < - 10 Then BlinkTimer = -10
		EndIf
	EndIf
	
	
	If HeartBeatVolume > 0 Then
		If HeartBeatTimer <= 0 Then
			tempchn = PlaySound_Strict (HeartBeatSFX)
			ChannelVolume tempchn, HeartBeatVolume*(opt\SFXVolume*opt\MasterVol)
			
			HeartBeatTimer = 70.0*(60.0/Max(HeartBeatRate,1.0))
		Else
			HeartBeatTimer = HeartBeatTimer - FPSfactor
		EndIf
		
		HeartBeatVolume = Max(HeartBeatVolume - FPSfactor*0.001, 0)
	EndIf
	
	CatchErrors("MovePlayer")
End Function

Function MouseLook()
	Local i%, g.Guns, currGun.Guns
	
	CameraShake = Max(CameraShake - (FPSfactor / 10), 0)
	
	For g.Guns = Each Guns
		If g\ID = g_I\HoldingGun Then
			If g\GunType<>GUNTYPE_MELEE Then
				currGun = g
			EndIf
			Exit
		EndIf
	Next
	
	Local IronSight_AddFOV# = 0.0
	If currGun <> Null Then
		IronSight_AddFOV = Abs(EntityX(IronSightPivot2%)/currGun\IronSightCoords\x)*0.5
	EndIf
	
	CameraZoom(Camera, (Min(1.0+(CurrCameraZoom/400.0),1.1) + IronSight_AddFOV) / (Tan((2*ATan(Tan(Float(FOV)/2)*(Float(RealGraphicWidth)/Float(RealGraphicHeight))))/2.0)))
	CurrCameraZoom = Max(CurrCameraZoom - FPSfactor, 0)
	
	If KillTimer >= 0 And FallTimer >=0 Then
		
		HeadDropSpeed = 0
		
		Local up# = (Sin(Shake) / (20.0+CrouchState*20.0))*0.6;, side# = Cos(Shake / 2.0) / 35.0		
		;Local roll# = Max(Min(Sin(Shake/2)*2.5*Min(Injuries+0.25,3.0),8.0),-8.0)
		Local roll# = Max(Min(Sin(Shake/2)*0.625,8.0),-8.0)
		
		PositionEntity Camera, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
		
		If (Not psp\NoRotation) Then
			RotateEntity Camera, 0, EntityYaw(Collider), (roll*0.5)*0.5
			MoveEntity Camera, side, (up*0.5) + 0.6 + CrouchState * -0.3, 0
			; -- Update the smoothing que To smooth the movement of the mouse.
			If mpl\WheelOpened=WHEEL_CLOSED Then
				If (Not co\Enabled)
					mouse_x_speed_1# = CurveValue(MouseXSpeed() * (MouseSens + 0.6) , mouse_x_speed_1, (6.0 / (MouseSens + 1.0))*opt\MouseSmooth)
				Else
					;[CONTROLLER]
					If GetRightAnalogStickYaw(True)<>0.0
						mouse_x_speed_1# = CurveValue(GetRightAnalogStickYaw() * ((co\Sensitivity+0.6)*10*FPSfactor), mouse_x_speed_1, 6.0 / ((co\Sensitivity+1.0)*10*FPSfactor))
					Else
						mouse_x_speed_1# = CurveValue(0.0, mouse_x_speed_1, 6.0 / ((co\Sensitivity+1.0)*10*FPSfactor))
					EndIf
				EndIf
				If IsNaN(mouse_x_speed_1) Then mouse_x_speed_1 = 0
				
				If (Not co\Enabled)
					If InvertMouse Then
						mouse_y_speed_1# = CurveValue(-MouseYSpeed() * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*opt\MouseSmooth)
					Else
						mouse_y_speed_1# = CurveValue(MouseYSpeed () * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*opt\MouseSmooth) 
					EndIf
				Else
					;[CONTROLLER]
					If Int(GetRightAnalogStickPitch(True))<>0
						mouse_y_speed_1# = CurveValue(GetRightAnalogStickPitch(False,InvertMouse) * ((co\Sensitivity+0.6)*10*FPSfactor), mouse_y_speed_1, 6.0/((co\Sensitivity+1.0)*10*FPSfactor))
					Else
						mouse_y_speed_1# = CurveValue(0.0, mouse_y_speed_1, 6.0/((co\Sensitivity+1.0)*10*FPSfactor))
					EndIf
				EndIf
				If IsNaN(mouse_y_speed_1) Then mouse_y_speed_1 = 0
				
				Local the_yaw# = ((mouse_x_speed_1#)) * mouselook_x_inc# / (1.0+WearingVest)
				Local the_pitch# = ((mouse_y_speed_1#)) * mouselook_y_inc# / (1.0+WearingVest)
				
				TurnEntity Collider, 0.0, -the_yaw#, 0.0 ; Turn the user on the Y (yaw) axis.
				If UnableToMove = 2 ;Used for intro head-rotation limitation in helicopter
					RotateEntity Collider,0.0,Max(Min(EntityYaw(Collider),70),-70),0.0
				EndIf
				user_camera_pitch# = user_camera_pitch# + the_pitch#
				; -- Limit the user;s camera To within 180 degrees of pitch rotation. ;EntityPitch(); returns useless values so we need To use a variable To keep track of the camera pitch.
				If user_camera_pitch# > 70.0 Then user_camera_pitch# = 70.0
				If user_camera_pitch# < - 70.0 Then user_camera_pitch# = -70.0
			EndIf
		Else
			RotateEntity Camera, 0, EntityYaw(Collider), 0
			Shake = 0
			MoveEntity Camera, 0, 0.6 + CrouchState * -0.3, 0
		EndIf
		
		If (Not NoClip)
			;UpdateSprint()
		Else
			;NTF_SprintPitch# = 0.0
			;NTF_SprintPitchSide% = 0
		EndIf
		
		RotateEntity Camera, WrapAngle(user_camera_pitch + Rnd(-CameraShake, CameraShake)), WrapAngle(EntityYaw(Collider) + Rnd(-CameraShake, CameraShake)), roll ; Pitch the user;s camera up And down.
		
		If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
				If EntityY(Collider)<2000*RoomScale Lor EntityY(Collider)>2608*RoomScale Then
					RotateEntity Camera, WrapAngle(EntityPitch(Camera)),WrapAngle(EntityYaw(Camera)), roll+WrapAngle(Sin(MilliSecs()/150.0)*30.0) ; Pitch the user;s camera up And down.
				EndIf
			EndIf
		EndIf
	Else
		HideEntity Collider
		PositionEntity Camera, EntityX(Head), EntityY(Head), EntityZ(Head)
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Head)
			If CollisionY(Head, i) < EntityY(Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			HeadDropSpeed# = 0
		Else
			
			If KillAnim = 0 Then 
				MoveEntity Head, 0, 0, HeadDropSpeed
				RotateEntity(Head, CurveAngle(-90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity Head, 0, 0, -HeadDropSpeed
				RotateEntity(Head, CurveAngle(90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			HeadDropSpeed# = HeadDropSpeed - 0.002 * FPSfactor
		EndIf
		
		If (Not co\Enabled)
			If InvertMouse Then
				TurnEntity (Camera, -MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
			Else
				TurnEntity (Camera, MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
			EndIf
		Else
			;[CONTROLLER]
			TurnEntity (Camera, GetRightAnalogStickPitch(False,InvertMouse) * 0.05 * FPSfactor, GetRightAnalogStickYaw(False,True) * 0.15 * FPSfactor, 0)
		EndIf
		
	EndIf
	
	If ParticleAmount=2
		If Rand(35) = 1 Then
			Local pvt% = CreatePivot()
			PositionEntity(pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(pvt, 0, Rnd(360), 0)
			If Rand(2) = 1 Then
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			EndIf
			
			Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
			p\speed = 0.001
			RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
			
			p\SizeChange = -0.00001
			
			pvt = FreeEntity_Strict(pvt)
		EndIf
	EndIf
	
	; -- Limit the mouse;s movement. Using this method produces smoother mouselook movement than centering the mouse Each loop.
	If mpl\WheelOpened=WHEEL_CLOSED Then
		If (ScaledMouseX() > mouse_right_limit) Lor (ScaledMouseX() < mouse_left_limit) Lor (ScaledMouseY() > mouse_bottom_limit) Lor (ScaledMouseY() < mouse_top_limit)
			MoveMouse viewport_center_x, viewport_center_y
		EndIf
	EndIf
	
	If WearingGasMask Or WearingHazmat Or Wearing1499 Then
		If Wearing714 = False Then
			If WearingGasMask = 2 Or Wearing1499 = 2 Or WearingHazmat = 2 Then
				Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
			EndIf
		EndIf
		If WearingHazmat = 1 Then
			Stamina = Min(60, Stamina)
		EndIf
		
		ShowEntity(GasMaskOverlay)
	Else
		HideEntity(GasMaskOverlay)
	EndIf
	
	If (Not WearingNightVision=0) Then
		ShowEntity(NVOverlay)
		If WearingNightVision=2 Then
			EntityColor(NVOverlay, 0,100,255)
			AmbientLightRooms(15)
		ElseIf WearingNightVision=3 Then
			EntityColor(NVOverlay, 255,0,0)
			AmbientLightRooms(15)
		Else
			EntityColor(NVOverlay, 0,255,0)
			AmbientLightRooms(15)
		EndIf
		EntityTexture(Fog, FogNVTexture)
	Else
		AmbientLightRooms(0)
		HideEntity(NVOverlay)
		EntityTexture(Fog, FogTexture)
	EndIf
	
	If gopt\GameMode <> GAMEMODE_MULTIPLAYER Then
		For i = 0 To 5
			If SCP1025state[i]>0 Then
				Select i
					Case 0 ;common cold
						If FPSfactor>0 Then 
							If Rand(1000)=1 Then
								If CoughCHN = 0 Then
									CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								Else
									If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								End If
							EndIf
						EndIf
						Stamina = Stamina - FPSfactor * 0.3
					Case 1 ;chicken pox
						If Rand(9000)=1 And Msg="" Then
							Msg="Your skin is feeling itchy."
							MsgTimer =70*4
						EndIf
					Case 2 ;cancer of the lungs
						If FPSfactor>0 Then 
							If Rand(800)=1 Then
								If CoughCHN = 0 Then
									CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								Else
									If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								End If
							EndIf
						EndIf
						Stamina = Stamina - FPSfactor * 0.1
					Case 3 ;appendicitis
					;0.035/sec = 2.1/min
						SCP1025state[i]=SCP1025state[i]+FPSfactor*0.0005
						If SCP1025state[i]>20.0 Then
							If SCP1025state[i]-FPSfactor<=20.0 Then Msg="The pain in your stomach is becoming unbearable." : MsgTimer = 70*4
							Stamina = Stamina - FPSfactor * 0.3
						ElseIf SCP1025state[i]>10.0
							If SCP1025state[i]-FPSfactor<=10.0 Then Msg="Your stomach is aching." : MsgTimer = 70*4
						EndIf
					Case 4 ;asthma
						If Stamina < 35 Then
							If Rand(Int(140+Stamina*8))=1 Then
								If CoughCHN = 0 Then
									CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								Else
									If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
								End If
							EndIf
							CurrSpeed = CurveValue(0, CurrSpeed, 10+Stamina*15)
						EndIf
					Case 5;cardiac arrest
						SCP1025state[i]=SCP1025state[i]+FPSfactor*0.35
					;35/sec
						If SCP1025state[i]>110 Then
							HeartBeatRate=0
							BlurTimer = Max(BlurTimer, 500)
							If SCP1025state[i]>140 Then 
								DeathMSG = Chr(34)+"He died of a cardiac arrest after reading SCP-1025, that's for sure. Is there such a thing as psychosomatic cardiac arrest, or does SCP-1025 have some "
								DeathMSG = DeathMSG + "anomalous properties we are not yet aware of?"+Chr(34)
								Kill()
							EndIf
						Else
							HeartBeatRate=Max(HeartBeatRate, 70+SCP1025state[i])
							HeartBeatVolume = 1.0
						EndIf
				End Select 
			EndIf
		Next
	EndIf
	
	
End Function

;--------------------------------------- GUI, menu etc ------------------------------------------------

Include "SourceCode\TaskSystem.bb"
Include "SourceCode\GUI.bb"

;----------------------------------------------------------------------------------------------

Include "SourceCode\LoadAllSounds.bb"
Function LoadEntities()
	CatchErrors("LoadEntities")
	DrawLoading(0)
	
	CreateSPPlayer()
	
	LoadMissingTexture()
	
	InitConsole(1)
	
	Local i%
	
	For i=0 To 9
		TempSounds[i]=0
	Next
	
	MainMenuTab = MenuTab_Default
	
	PauseMenuIMG% = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	MaskImage PauseMenuIMG, 255,0,255
	ScaleImage PauseMenuIMG,MenuScale,MenuScale
	
	SprintIcon% = LoadImage_Strict("GFX\sprinticon.png")
	BlinkIcon% = LoadImage_Strict("GFX\blinkicon.png")
	CrouchIcon% = LoadImage_Strict("GFX\sneakicon.png")
	HandIcon% = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2% = LoadImage_Strict("GFX\handsymbol2.png")
	DrawLoading(3)
	StaminaMeterIMG% = LoadImage_Strict("GFX\staminameter.jpg")
	
	KeypadHUD =  LoadImage_Strict("GFX\keypadhud.jpg")
	MaskImage(KeypadHUD, 255,0,255)
	
	Panel294 = LoadImage_Strict("GFX\294panel.jpg")
	MaskImage(Panel294, 255,0,255)
	Brightness% = GetINIFloat(gv\OptionFile, "options", "brightness", 20)
	CameraFogNear# = GetINIFloat(gv\OptionFile, "options", "camera fog near", 0.5)
	CameraFogFar# = GetINIFloat(gv\OptionFile, "options", "camera fog far", 6.0)
	StoredCameraFogFar# = CameraFogFar
	
	;TextureLodBias
	
	LoadMaterials("Data\materials.ini")
	
	AmbientLightRoomTex% = CreateTextureUsingCacheSystem(2,2,1)
	TextureBlend AmbientLightRoomTex,2
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport Camera,0,0,opt\GraphicWidth,opt\GraphicHeight
	CameraRange(Camera, 0.01, CameraFogFar)
	CameraFogMode (Camera, 1)
	CameraFogRange (Camera, CameraFogNear, CameraFogFar)
	CameraFogColor (Camera, GetINIInt(gv\OptionFile, "options", "fog r"), GetINIInt(gv\OptionFile, "options", "fog g"), GetINIInt(gv\OptionFile, "options", "fog b"))
	AmbientLight Brightness, Brightness, Brightness
	
	m_I\Cam = CreateCamera(Camera)
	CameraRange m_I\Cam,0.01,20
	CameraFogRange m_I\Cam,CameraFogNear,CameraFogFar
	CameraFogColor m_I\Cam,0,0,0
	CameraFogMode m_I\Cam,1
	CameraClsMode m_I\Cam,0,1
	CameraProjMode m_I\Cam,0
	m_I\MenuLogo = CreateMenuLogo(m_I\Cam)
	m_I\Sprite = CreateSprite(m_I\Cam)
	ScaleSprite m_I\Sprite,3,3
	EntityColor m_I\Sprite,0,0,0
	EntityFX m_I\Sprite,1
	EntityOrder m_I\Sprite,-1
	EntityAlpha m_I\Sprite,0.0
	m_I\SpriteAlpha = 0.0
	MoveEntity m_I\Sprite,0,0,1
	
	;ScreenTexs[0] = CreateTextureUsingCacheSystem(512, 512, 1+256)
	;ScreenTexs[1] = CreateTextureUsingCacheSystem(512, 512, 1+256)
	ScreenTexs[0] = CreateTextureUsingCacheSystem(512, 512, 1)
	ScreenTexs[1] = CreateTextureUsingCacheSystem(512, 512, 1)
	
	CreateBlurImage()
	CameraProjMode ark_blur_cam,0
	;Listener = CreateListener(Camera)
	
	FogTexture = LoadTexture_Strict("GFX\fog.jpg",1,2)
	
	Fog = CreateSprite(ark_blur_cam)
	ScaleSprite(Fog, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(Fog, FogTexture)
	EntityBlend (Fog, 2)
	EntityOrder Fog, -1000
	MoveEntity(Fog, 0, 0, 1.0)
	
	GasMaskTexture = LoadTexture_Strict("GFX\GasmaskOverlay2.jpg", 1)
	GasMaskOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(GasMaskOverlay, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(GasMaskOverlay, GasMaskTexture)
	EntityBlend (GasMaskOverlay, 2)
	EntityFX(GasMaskOverlay, 1)
	EntityOrder GasMaskOverlay, -1003
	MoveEntity(GasMaskOverlay, 0, 0, 1.0)
	HideEntity(GasMaskOverlay)
	
	InfectTexture = LoadTexture_Strict("GFX\InfectOverlay.jpg",1,2)
	InfectOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(InfectOverlay, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(InfectOverlay, InfectTexture)
	EntityBlend (InfectOverlay, 3)
	EntityFX(InfectOverlay, 1)
	EntityOrder InfectOverlay, -1003
	MoveEntity(InfectOverlay, 0, 0, 1.0)
	HideEntity(InfectOverlay)
	
	NVTexture = LoadTexture_Strict("GFX\NightVisionOverlay.jpg", 1)
	NVOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(NVOverlay, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(NVOverlay, NVTexture)
	EntityBlend (NVOverlay, 2)
	EntityFX(NVOverlay, 1)
	EntityOrder NVOverlay, -1003
	MoveEntity(NVOverlay, 0, 0, 1.0)
	HideEntity(NVOverlay)
	NVBlink = CreateSprite(ark_blur_cam)
	ScaleSprite(NVBlink, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityColor(NVBlink,0,0,0)
	EntityFX(NVBlink, 1)
	EntityOrder NVBlink, -1005
	MoveEntity(NVBlink, 0, 0, 1.0)
	HideEntity(NVBlink)
	
	FogNVTexture = LoadTexture_Strict("GFX\fogNV.jpg", 1, 2)
	
	DrawLoading(5)
	
	TeslaTexture = LoadTexture_Strict("GFX\map\tesla.jpg",1+2,2)
	
	DarkTexture = CreateTextureUsingCacheSystem(1024, 1024, 1 + 2)
	SetBuffer TextureBuffer(DarkTexture)
	Cls
	SetBuffer BackBuffer()
	Dark = CreateSprite(ark_blur_cam)
	ScaleSprite(Dark, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(Dark, DarkTexture)
	EntityBlend (Dark, 1)
	EntityOrder Dark, -1002
	MoveEntity(Dark, 0, 0, 1.0)
	EntityAlpha Dark, 0.0
	HideEntity Dark
	
	LightTexture = CreateTextureUsingCacheSystem(1024, 1024, 1 + 2)
	SetBuffer TextureBuffer(LightTexture)
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	Light = CreateSprite(ark_blur_cam)
	ScaleSprite(Light, 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(Light, LightTexture)
	EntityBlend (Light, 1)
	EntityOrder Light, -1002
	MoveEntity(Light, 0, 0, 1.0)
	HideEntity Light
	
	Collider = CreatePivot()
	EntityRadius Collider, 0.15, 0.30
	;EntityPickMode(Collider, 1)
	EntityType Collider, HIT_PLAYER
	
	Head = CreatePivot()
	EntityRadius Head, 0.15
	EntityType Head, HIT_PLAYER
	
	
	LiquidObj = LoadMesh_Strict("GFX\items\cupliquid.x") ;optimized the cups dispensed by 294
	HideEntity LiquidObj
	
	DrawLoading(9)
	
	MTFObj = LoadAnimMesh_Strict("GFX\npcs\MTF3.b3d") ;optimized MTFs
	HideEntity MTFObj
	DrawLoading(10)
	GuardObj = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ;optimized Guards
	HideEntity GuardObj
	DrawLoading(11)
	;GuardTex = LoadTexture_Strict("GFX\npcs\body.jpg") ;optimized the guards even more
	
	;If BumpEnabled Then
	;	bump1 = LoadTexture_Strict("GFX\npcs\mtf_newnormal01.png")
	;	;TextureBlend bump1, FE_BUMP ;USE DOT3
	;		
	;	For i = 2 To CountSurfaces(MTFObj)
	;		sf = GetSurface(MTFObj,i)
	;		b = GetSurfaceBrush( sf )
	;		t1 = GetBrushTexture(b,0)
	;		
	;		Select Lower(StripPath(TextureName(t1)))
	;			Case "MTF_newdiffuse02.png"
	;				
	;				BrushTexture b, bump1, 0, 0
	;				BrushTexture b, t1, 0, 1
	;				PaintSurface sf,b
	;		End Select
	;		FreeBrush b
	;		DeleteSingleTextureEntryFromCache t1
	;	Next
	;	DeleteSingleTextureEntryFromCache bump1	
	;EndIf
	
	
	ClassDObj = LoadAnimMesh_Strict("GFX\npcs\classd.b3d") ;optimized Class-D's and scientists/researchers
	HideEntity ClassDObj
	DrawLoading(12)
	;ApacheObj = LoadAnimMesh_Strict("GFX\apache.b3d") ;optimized Apaches (helicopters)
	;ApacheRotorObj = LoadAnimMesh_Strict("GFX\apacherotor.b3d") ;optimized the Apaches even more
	
	;HideEntity ApacheObj
	;HideEntity ApacheRotorObj
	
	;Other NPCs pre-loaded
	;[Block]
	NPC049OBJ = LoadAnimMesh_Strict("GFX\npcs\scp-049.b3d")
	HideEntity NPC049OBJ
	DrawLoading(13)
	NPC0492OBJ = LoadAnimMesh_Strict("GFX\npcs\zombie1.b3d")
	HideEntity NPC0492OBJ
	DrawLoading(14)
	ClerkOBJ = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d")
	HideEntity ClerkOBJ	
	DrawLoading(15)
	;[End Block]
	
;	For i=0 To 4
;		Select True
;			Case i=2
;				tempStr="2c"
;			Case i>2
;				tempStr=Str(i)
;			Default
;				tempStr=Str(i+1)
;		End Select
;		OBJTunnel(i)=LoadRMesh("GFX\map\mt"+tempStr+".rmesh",Null)
;		HideEntity OBJTunnel(i)
;	Next
	
;	OBJTunnel(0)=LoadRMesh("GFX\map\mt1.rmesh",Null)	
;	HideEntity OBJTunnel(0)				
;	OBJTunnel(1)=LoadRMesh("GFX\map\mt2.rmesh",Null)	
;	HideEntity OBJTunnel(1)
;	OBJTunnel(2)=LoadRMesh("GFX\map\mt2c.rmesh",Null)	
;	HideEntity OBJTunnel(2)				
;	OBJTunnel(3)=LoadRMesh("GFX\map\mt3.rmesh",Null)	
;	HideEntity OBJTunnel(3)	
;	OBJTunnel(4)=LoadRMesh("GFX\map\mt4.rmesh",Null)	
;	HideEntity OBJTunnel(4)				
;	OBJTunnel(5)=LoadRMesh("GFX\map\mt_elevator.rmesh",Null)
;	HideEntity OBJTunnel(5)
;	OBJTunnel(6)=LoadRMesh("GFX\map\mt_generator.rmesh",Null)
;	HideEntity OBJTunnel(6)
	
	LightSpriteTex[0] = LoadTexture_Strict("GFX\light1.jpg",1,2)
	LightSpriteTex[1] = LoadTexture_Strict("GFX\light2.jpg",1,2)
	LightSpriteTex[2] = LoadTexture_Strict("GFX\lightsprite.jpg",1,2)
	;LightSpriteTex[3] = LoadTexture_Strict("Unused\GFX\LensFlare.png",1,2)
	
	LoadDoors()
	
	LeverBaseOBJ = LoadMesh_Strict("GFX\map\leverbase.x")
	HideEntity LeverBaseOBJ
	LeverOBJ = LoadMesh_Strict("GFX\map\leverhandle.x")
	HideEntity LeverOBJ
	
	;For i = 0 To 1
	;	HideEntity BigDoorOBJ(i)
	;	;If BumpEnabled And 0 Then
	;	If BumpEnabled
	;		
	;		Local bumptex = LoadTexture_Strict("GFX\map\containmentdoorsbump.jpg")
	;		;TextureBlend bumptex, FE_BUMP
	;		Local tex = LoadTexture_Strict("GFX\map\containment_doors.jpg")	
	;		EntityTexture BigDoorOBJ(i), bumptex, 0, 0
	;		EntityTexture BigDoorOBJ(i), tex, 0, 1
	;		
	;		DeleteSingleTextureEntryFromCache tex
	;		DeleteSingleTextureEntryFromCache bumptex
	;	EndIf
	;Next
	
	DrawLoading(18)
	
	For i = 0 To 5
		GorePics[i] = LoadTexture_Strict("GFX\895pics\pic"+(i+1)+".jpg",1,2)
	Next
	
	OldAiPics[0] = LoadTexture_Strict("GFX\AIface.jpg",1,2)
	OldAiPics[1] = LoadTexture_Strict("GFX\AIface2.jpg",1,2)
	
	DrawLoading(20)
	
	LoadDecals()
	
	DrawLoading(25)
	
	Monitor = LoadMesh_Strict("GFX\map\monitor.b3d")
	HideEntity Monitor
	;MonitorTexture = LoadTexture_Strict("Unused\GFX\monitoroverlay.jpg",1,1)
	
	CamBaseOBJ = LoadMesh_Strict("GFX\map\cambase.x")
	HideEntity(CamBaseOBJ)
	CamOBJ = LoadMesh_Strict("GFX\map\CamHead.b3d")
	HideEntity(CamOBJ)
	
	;Monitor2 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	;HideEntity Monitor2
	;Monitor3 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	;HideEntity Monitor3
	;MonitorTexture2 = LoadTexture_Strict("LockdownScreen2.jpg",1,0,"",2)
	;MonitorTexture3 = LoadTexture_Strict("LockdownScreen.jpg",1,0,"",2)
	;MonitorTexture4 = LoadTexture_Strict("LockdownScreen3.jpg",1,0,"",2)
	;MonitorTextureOff = CreateTextureUsingCacheSystem(1,1)
	;SetBuffer TextureBuffer(MonitorTextureOff)
	;ClsColor 0,0,0
	;Cls
	;SetBuffer BackBuffer()
	LightConeModel = LoadMesh_Strict("GFX\lightcone.b3d")
	HideEntity LightConeModel
	DrawLoading(26)
;	For i = 2 To CountSurfaces(Monitor2)
;		sf = GetSurface(Monitor2,i)
;		b = GetSurfaceBrush(sf)
;		If b<>0 Then
;			t1 = GetBrushTexture(b,0)
;			If t1<>0 Then
;				name$ = StripPath(TextureName(t1))
;				If Lower(name) <> "monitortexture.jpg"
;					BrushTexture b, MonitorTextureOff, 0, 0
;					PaintSurface sf,b
;				EndIf
;				If name<>"" Then DeleteSingleTextureEntryFromCache t1
;			EndIf
;			FreeBrush b
;		EndIf
;	Next
;	For i = 2 To CountSurfaces(Monitor3)
;		sf = GetSurface(Monitor3,i)
;		b = GetSurfaceBrush(sf)
;		If b<>0 Then
;			t1 = GetBrushTexture(b,0)
;			If t1<>0 Then
;				name$ = StripPath(TextureName(t1))
;				If Lower(name) <> "monitortexture.jpg"
;					BrushTexture b, MonitorTextureOff, 0, 0
;					PaintSurface sf,b
;				EndIf
;				If name<>"" Then DeleteSingleTextureEntryFromCache t1
;			EndIf
;			FreeBrush b
;		EndIf
;	Next
	
	InitItemTemplates()
	DrawLoading(27)
	ParticleTextures[0] = LoadTexture_Strict("GFX\smoke.png",1+2,2)
	ParticleTextures[1] = LoadTexture_Strict("GFX\flash.jpg",1+2,2)
	ParticleTextures[2] = LoadTexture_Strict("GFX\dust.jpg",1+2,2)
	ParticleTextures[3] = LoadTexture_Strict("GFX\npcs\hg.pt",1+2,2)
	ParticleTextures[4] = LoadTexture_Strict("GFX\map\sun.jpg",1+2,2)
	ParticleTextures[5] = LoadTexture_Strict("GFX\bloodsprite.png",1+2,2)
	ParticleTextures[6] = LoadTexture_Strict("GFX\smoke2.png",1+2,2)
	ParticleTextures[7] = LoadTexture_Strict("GFX\spark.jpg",1+2,2)
	ParticleTextures[8] = LoadTexture_Strict("GFX\particle.png",1+2,2)
	ParticleTextures[9] = LoadAnimTexture("GFX\fog_textures.png",1+2,256,256,0,4)
	ParticleTextures[12] = LoadTexture_Strict("GFX\WaterParticle3.png",1+2,2)
	ParticleTextures[13] = LoadTexture_Strict("GFX\fire_particle.png",1+2,2)
	
	SetChunkDataValues()
	
	;[Block]
	;Gonzales
	DTextures[0] = LoadTexture_Strict("GFX\npcs\gonzales.jpg", 0, 2)
	;SCP-970 corpse
	DTextures[1] = LoadTexture_Strict("GFX\npcs\corpse.jpg", 0, 2)
	;scientist 1
	DTextures[2] = LoadTexture_Strict("GFX\npcs\scientist.jpg", 0, 2)
	;scientist 2
	DTextures[3] = LoadTexture_Strict("GFX\npcs\scientist2.jpg", 0, 2)
	;janitor
	DTextures[4] = LoadTexture_Strict("GFX\npcs\janitor.jpg", 0, 2)
	;106 Victim
	DTextures[5] = LoadTexture_Strict("GFX\npcs\106victim.jpg", 0, 2)
	;2nd ClassD
	DTextures[6] = LoadTexture_Strict("GFX\npcs\classd2.jpg", 0, 2)
	;035 victim
	DTextures[7] = LoadTexture_Strict("GFX\npcs\035victim.jpg", 0, 2)
	;body 1
	DTextures[8] = LoadTexture_Strict("GFX\npcs\body1.jpg", 0, 2)
	;body 2
	DTextures[9] = LoadTexture_Strict("GFX\npcs\body2.jpg", 0, 2)
	
	For i = 0 To MaxDTextures-1
		TextureBlend(DTextures[i],5)
	Next	
	;[End Block]
	DrawLoading(28)
	
	TextureLodBias TextureFloat#
	
	LoadModStuff()
	DrawLoading(30)
	;LoadRoomMeshes()
	
	I_427 = New SCP427
	
	CatchErrors("Uncaught LoadEntities")
End Function

Function InitNewGame()
	CatchErrors("InitNewGame")
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events, g.Guns
	
	DrawLoading(45)
	
	HideDistance# = 15.0
	
	HeartBeatRate = 70
	
	Local strtemp$ = ""
	For i = 1 To Len(RandomSeed)
		strtemp = strtemp+Asc(Mid(RandomSeed,i,1))
	Next
	SeedRnd Abs(Int(strtemp))
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1,9)*(10^i)
	Next
	
	If SelectedMap = "" Then
		CreateMap()
	Else
		LoadMap("Map Creator\Maps\"+SelectedMap)
	EndIf
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "room2_maintenance" Then
			ShowEntity r\Objects[0]
			ShowEntity r\Objects[5]
		EndIf
	Next
	InitWayPoints()
	ShowEntity Collider
	For r.Rooms = Each Rooms
		If r <> PlayerRoom
			If r\RoomTemplate\Name = "room2_maintenance" Then
				HideEntity r\Objects[0]
				HideEntity r\Objects[5]
			EndIf
		EndIf
	Next
	
	DrawLoading(79)
	
	Curr173 = CreateNPC(NPCtype173, 0, -30.0, 0)
	Curr106 = CreateNPC(NPCtypeOldMan, 0, -30.0, 0)
	Curr106\State = 70 * 60 * Rand(12,17)
	DrawLoading(80)
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
		If d\obj2 <> 0 And d\dir = 0 Then
			MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
			MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
		EndIf	
	Next
	
	For it.Items = Each Items
		EntityType (it\collider, HIT_ITEM)
		EntityParent(it\collider, 0)
	Next
	
	DrawLoading(85)
	For sc.SecurityCams= Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights-1
			If r\Lights[i]<>0 Then EntityParent(r\Lights[i],0)
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(GetRandomDecalID(DECAL_TYPE_BLOODSPLAT), EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.1, 0.4) : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.85, 0.95))
			EndIf
			
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(DECAL_DECAY, EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.5, 0.7) : EntityAlpha(de\obj, 0.7) : de\ID = 1 : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.7, 0.85))
			EndIf
		EndIf
		DrawLoading(-1)
		Select NTF_CurrZone
			Case 0
				If r\RoomTemplate\Name = "gate_a_intro"
					PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
					PlayerRoom = r
				EndIf
			Case 1
				If r\RoomTemplate\Name = "checkpoint_lcz"
					PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
					RotateEntity (Collider,0,r\angle,0)
					PlayerRoom = r
				EndIf
			Case 2
				If r\RoomTemplate\Name = "checkpoint_hcz"
					PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
					RotateEntity (Collider,0,r\angle,0)
					PlayerRoom = r
				EndIf
			Case 3
				If r\RoomTemplate\Name = "gate_a_entrance"
					PositionEntity (Collider, EntityX(r\obj), 0.5, EntityZ(r\obj)-1450.0*RoomScale)
					RotateEntity (Collider,0,r\angle+180,0)
					PlayerRoom = r
				EndIf
			Case 4
				If r\RoomTemplate\Name = "gate_a_topside"
					PositionEntity (Collider, EntityX(r\obj), 0.5, EntityZ(r\obj))
					PlayerRoom = r
				EndIf
			Case 5
				If r\RoomTemplate\Name = "testmap"
					PositionEntity (Collider, EntityX(r\obj), 0.5, EntityZ(r\obj))
					PlayerRoom = r
				EndIf
		End Select
	Next
	
	DrawLoading(90)
	
	Local rt.RoomTemplates
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = FreeEntity_Strict(rt\obj)
	Next
	
	Delete Each TempWayPoints
	Delete Each TempScreens
	
	Local tfll.TempFluLight
	For tfll = Each TempFluLight
		Delete tfll\position
		Delete tfll\rotation
		Delete tfll
	Next
	
	;TurnEntity(Collider, 0, Rand(160, 200), 0)
	
	ResetEntity Collider
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2_nuke"
			e\EventState = 1
			DebugLog "room2_nuke"
		EndIf
		If e\EventName = "cont_106"
			e\EventState2 = 1
			DebugLog "cont_106"
		EndIf	
		If e\EventName = "surveil_room"
			e\EventState3 = 1
			DebugLog "surveil_room"
		EndIf
	Next
	
	MoveMouse viewport_center_x,viewport_center_y;320, 240
	
	SetFont fo\Font[Font_Default]
	
	HidePointer()
	
	BlinkTimer = -10
	BlurTimer = 100
	Stamina = 100
	
	FPSfactor = 1.0
	DrawLoading(95)
	MovePlayer()
	DrawLoading(96)
	UpdateDoors()
	DrawLoading(97)
	UpdateNPCs()
	DrawLoading(98)
	UpdateWorld()
	DrawLoading(99)
	
	;FreeTextureCache
	DeleteTextureEntriesFromCache(0)
	DrawLoading(100)
	
	ResetInput()
	
	DropSpeed = 0
	
	CatchErrors("Uncaught InitNewGame")
End Function

Function InitLoadGame()
	CatchErrors("InitLoadGame")
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	
	DrawLoading(80)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next
	
	ResetEntity Collider
	
	;InitEvents()
	
	DrawLoading(90)
	
	MoveMouse viewport_center_x,viewport_center_y
	
	SetFont fo\Font[Font_Default]
	
	HidePointer ()
	
	BlinkTimer = BLINKFREQ
	Stamina = 100
	
	Delete Each TempWayPoints
	Delete Each TempScreens
	
	Local tfll.TempFluLight
	For tfll = Each TempFluLight
		Delete tfll\position
		Delete tfll\rotation
		Delete tfll
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = FreeEntity_Strict(rt\obj)
	Next
	
	DropSpeed = 0.0
	
	;FreeTextureCache
	DeleteTextureEntriesFromCache(0)
	
	CatchErrors("Uncaught InitLoadGame")
	DrawLoading(100)
	
	PrevTime = MilliSecs()
	FPSfactor = 0
	ResetInput()
	
End Function

Function NullGame(nomenuload%=False,playbuttonsfx%=True)
	CatchErrors("Uncaught (NullGame)")
	Local i%, x%, y%, lvl
	Local itt.ItemTemplates, s.Screens
	Local rt.RoomTemplates
	
	Local PlayerRoomName$ = PlayerRoom\RoomTemplate\Name
	Local PlayerRoomZone = NTF_CurrZone
	
	KillSounds()
	If playbuttonsfx Then PlaySound_Strict ButtonSFX
	
	DeleteNewElevators()
	
	DeleteTextureEntriesFromCache(2)
	
	DestroySPPlayer()
	
	UnableToMove% = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer# = 0
	QuickLoad_CurrEvent = Null
	
	DeathMSG$=""
	
	SelectedMap = ""
	
	UsedConsole = False
	
	RoomTempID = 0
	
	GameSaved = 0
	
	HideDistance# = 15.0
	
	For lvl = 0 To 0
		For x = 0 To MapWidth - 1
			For y = 0 To MapHeight - 1
				MapTemp[x * MapWidth + y] = 0
				MapFound[x * MapWidth + y] = 0
			Next
		Next
	Next
	
	;Probably useless
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = False
	Next
	
	DropSpeed = 0
	Shake = 0
	CurrSpeed = 0
	
	DeathTimer=0
	
	HeartBeatVolume = 0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0
	BlinkEffect = 1.0
	BlinkEffectTimer = 0
	
	;Bloodloss = 0
	;Injuries = 0
	Infect = 0
	
	For i = 0 To 5
		SCP1025state[i] = 0
	Next
	
	SelectedEnding = ""
	EndingTimer = 0
	ExplosionTimer = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	
	GodMode = 0
	NoClip = 0
	WireframeState = 0
	WireFrame 0
	WearingGasMask = 0
	WearingHazmat = 0
	WearingVest = 0
	Wearing714 = 0
	If WearingNightVision Then
		CameraFogFar = StoredCameraFogFar
		WearingNightVision = 0
	EndIf
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
	Contained106 = False
	Contained173 = False
	
	;Probably useless
	If Curr173 <> Null Then Curr173\Idle = SCP173_ACTIVE
	
	MTFtimer = 0
	For i = 0 To 9
		MTFrooms[i]=Null
		MTFroomState[i]=0
	Next
	
	For s.Screens = Each Screens
		If s\img <> 0 Then FreeImage s\img
		Delete s
	Next
	
;	For i = 0 To MAXACHIEVEMENTS-1
;		Achievements[i]\Unlocked=0
;	Next
	RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	EyeIrritation = 0
	EyeStuck = 0
	
	ShouldPlay = 0
	
	KillTimer = 0
	FallTimer = 0
	Stamina = 100
	BlurTimer = 0
	SuperMan = False
	SuperManTimer = 0
	
	InfiniteStamina% = False
	
	Msg = ""
	MsgTimer = 0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory[i] = Null
	Next
	SelectedItem = Null
	
	Delete Each Doors
	
	Delete Each LightTemplates
	Delete Each Materials
	Delete Each WayPoints
	Delete Each TempWayPoints	
	Delete Each Rooms
	Delete Each ItemTemplates
	Delete Each Items
	Delete Each Props
	Delete Each Decals
	Delete Each NPCs
	Delete Each NPCGun
	Delete Each NPCAnim
	Delete Each TempFluLight
	Delete Each Lever
	Delete Each MenuLogo
	Delete Each TempScreens
	Delete Each Water
	
	Delete Each FuseBox
	Delete Each Generator
	Delete Each SoundEmittor
	Delete Each ButtonGen
	Delete Each LeverGen
	Delete Each ParticleGen
	Delete Each DamageBossRadius
	
	Delete Each TextureInCache
	
	Delete I_427
	
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	For i = 0 To 6
		MTFrooms[i]=Null
	Next
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound<>0 Then FreeSound_Strict e\Sound
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2
		Delete e
	Next
	
	Delete Each SecurityCams
	Delete Each Emitters
	Delete Each Particles
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = 0
	Next
	
	For i = 0 To 5
		If ChannelPlaying(RadioCHN[i]) Then StopChannel(RadioCHN[i])
	Next
	
	Delete Each ConsoleMsg
	
	Delete Each NewTask
	
	NTF_1499PrevX# = 0.0
	NTF_1499PrevY# = 0.0
	NTF_1499PrevZ# = 0.0
	NTF_1499PrevRoom = Null
	NTF_1499X# = 0.0
	NTF_1499Y# = 0.0
	NTF_1499Z# = 0.0
	Wearing1499% = False
	DeleteChunks()
	
	DeleteElevatorObjects()
	
	NoTarget% = False
	
	OptionsMenu% = -1
	QuitMSG% = -1
	AchievementsMenu% = -1
	
	DeafPlayer% = False
	DeafTimer# = 0.0
	
	IsZombie% = False
	
	DeInitZoneEntities()
	
	DeleteModStuff()
	
	If gopt\GameMode = GAMEMODE_UNKNOWN
		DeleteMission()
	EndIf
	gopt\GameMode = gopt\SingleplayerGameMode
	
;	Delete Each AchievementMsg
;	CurrAchvMSGID = 0
	
	Delete Each DoorInstance
	Delete Each GunInstance
	DeleteVectors2D()
	DeleteVectors3D()
	;DestroyCutsceneStuff()
	
	Delete Each FluLight
	
	ClearWorld
	ResetTimingAccumulator()
	Camera = 0
	ark_blur_cam = 0
	m_I\Cam = 0
	InitFastResize()
	
	If (Not nomenuload)
		Local entry$
		If PlayerRoomZone = LCZ
			entry = "lcz"
		ElseIf PlayerRoomZone = HCZ
			entry = "hcz"
		ElseIf PlayerRoomZone = EZ
			entry = "ez"
		EndIf
		If entry = ""
			If PlayerRoomName = "gate_a_entrance"
				entry = "beginning"
			ElseIf PlayerRoomName = "gate_a_intro"
				entry = "intro"
			Else
				entry = PlayerRoomName
			EndIf
		EndIf
		PutINIValue(gv\OptionFile,"options","progress",entry)
		InitConsole(2)
		Delete Each Menu3DInstance
		Load3DMenu(entry)
	EndIf
	
	DeleteMenuGadgets()
	
	CatchErrors("NullGame")
End Function

Include "SourceCode\Save.bb"

;--------------------------------------- music & sounds ----------------------------------------------

Function PlaySound2%(SoundHandle%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range, 1.0)
	Local soundchn% = 0
	
	If volume > 0 Then 
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			soundchn% = PlaySound_Strict (SoundHandle)
			
			ChannelVolume(soundchn, volume# * (1 - dist#)*(opt\SFXVolume#*opt\MasterVol))
			ChannelPan(soundchn, panvalue)			
		EndIf
	EndIf
	
	Return soundchn
End Function

Function LoopSound2%(SoundHandle%, Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			If Chn = 0 Then
				Chn% = PlaySound_Strict (SoundHandle)
			Else
				If (Not ChannelPlaying(Chn)) Then Chn% = PlaySound_Strict (SoundHandle)
			EndIf
			
			ChannelVolume(Chn, volume# * (1 - dist#)*(opt\SFXVolume#*opt\MasterVol))
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
	
	Return Chn
End Function

Function LoadTempSound(file$)
	If TempSounds[TempSoundIndex]<>0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(file)
	TempSounds[TempSoundIndex] = TempSound
	
	TempSoundIndex=(TempSoundIndex+1) Mod 10
	
	Return TempSound
End Function

Function LoadEventSound(e.Events,file$,num%=0)
	
	If num=0 Then
		If e\Sound<>0 Then FreeSound_Strict e\Sound : e\Sound=0
		e\Sound=LoadSound_Strict(file)
		Return e\Sound
	Else If num=1 Then
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2 : e\Sound2=0
		e\Sound2=LoadSound_Strict(file)
		Return e\Sound2
	EndIf
End Function

Function UpdateMusic()
	
	If ConsoleFlush Then
		If Not ChannelPlaying(ConsoleMusPlay) Then ConsoleMusPlay = PlaySound(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay ; playing the wrong clip, fade out
			CurrMusicVolume# = Max(CurrMusicVolume - (FPSfactor / 250.0), 0)
			If CurrMusicVolume = 0
				If NowPlaying<66
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic=0
			EndIf
		Else ; playing the right clip
			CurrMusicVolume = CurrMusicVolume + (opt\MusicVol - CurrMusicVolume) * (0.1*FPSfactor)
		EndIf
		
		If NowPlaying < 66
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict("SFX\Music\"+Music[NowPlaying]+".ogg",0.0,Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN,((CurrMusicVolume*opt\MasterVol)*InFocus()))
		EndIf
	Else
		If FPSfactor > 0 Lor OptionsMenu = 2 Then
			;CurrMusicVolume = 1.0
			If (Not ChannelPlaying(MusicCHN)) Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume MusicCHN,1.0*aud\MusicVol
		EndIf
	EndIf
	
End Function 

Function PauseSounds()
	Local e.Events, n.NPCs, d.Doors, ne.NewElevator
	
	For e = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_isStream) And ChannelPlaying(e\SoundCHN) Then
				PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN,True)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_isStream) And ChannelPlaying(e\SoundCHN2) Then
				PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2,True)
			EndIf
		EndIf		
	Next
	
	For n = Each NPCs
		If n\SoundChn <> 0 Then
			If (Not n\SoundChn_IsStream) And ChannelPlaying(n\SoundChn) Then
				PauseChannel(n\SoundChn)
			Else
				SetStreamPaused_Strict(n\SoundChn,True)
			EndIf
		EndIf
		If n\SoundChn2 <> 0 Then
			If (Not n\SoundChn2_IsStream) And ChannelPlaying(n\SoundChn2) Then
				PauseChannel(n\SoundChn2)
			Else
				SetStreamPaused_Strict(n\SoundChn2,True)
			EndIf
		EndIf
	Next	
	
	For d = Each Doors
		If d\SoundCHN <> 0 And ChannelPlaying(d\SoundCHN) Then
			PauseChannel(d\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 And ChannelPlaying(AmbientSFXCHN) Then
		PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 And ChannelPlaying(BreathCHN) Then
		PauseChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN,True)
	EndIf
	
	If GunCHN <> 0 And ChannelPlaying(GunCHN) Then
		PauseChannel(GunCHN)
	EndIf
	If GunCHN2 <> 0 And ChannelPlaying(GunCHN2) Then
		PauseChannel(GunCHN2)
	EndIf
	If ChatSFXCHN <> 0 And ChannelPlaying(ChatSFXCHN) Then
		PauseChannel(ChatSFXCHN)
	EndIf
	If NTF_ChatCHN1 <> 0 And ChannelPlaying(NTF_ChatCHN1) Then
		PauseChannel(NTF_ChatCHN1)
	EndIf
	If NTF_ChatCHN2 <> 0 And ChannelPlaying(NTF_ChatCHN2) Then
		PauseChannel(NTF_ChatCHN2)
	EndIf
	
	For ne = Each NewElevator
		If ne\soundchn <> 0 Then
			SetStreamPaused_Strict(ne\soundchn,True)
		EndIf
	Next
	
	If psp <> Null Then
		If psp\SoundCHN <> 0 And ChannelPlaying(psp\SoundCHN) Then
			PauseChannel(psp\SoundCHN)
		EndIf
	EndIf	
	
End Function

Function ResumeSounds()
	Local e.Events, n.NPCs, d.Doors, ne.NewElevator
	
	For e = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_isStream) And ChannelPlaying(e\SoundCHN) Then
				ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN,False)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_isStream) And ChannelPlaying(e\SoundCHN2) Then
				ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2,False)
			EndIf
		EndIf	
	Next
	
	For n = Each NPCs
		If n\SoundChn <> 0 Then
			If (Not n\SoundChn_IsStream) And ChannelPlaying(n\SoundChn) Then
				ResumeChannel(n\SoundChn)
			Else
				SetStreamPaused_Strict(n\SoundChn,False)
			EndIf
		EndIf
		If n\SoundChn2 <> 0 Then
			If (Not n\SoundChn2_IsStream) And ChannelPlaying(n\SoundChn2) Then
				ResumeChannel(n\SoundChn2)
			Else
				SetStreamPaused_Strict(n\SoundChn2,False)
			EndIf
		EndIf
	Next
	
	For d = Each Doors
		If d\SoundCHN <> 0 And ChannelPlaying(d\SoundCHN) Then
			ResumeChannel(d\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 And ChannelPlaying(AmbientSFXCHN) Then
		ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 And ChannelPlaying(BreathCHN) Then
		ResumeChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN,False)
	EndIf
	
	If GunCHN <> 0 And ChannelPlaying(GunCHN) Then
		ResumeChannel(GunCHN)
	EndIf
	If GunCHN2 <> 0 And ChannelPlaying(GunCHN2) Then
		ResumeChannel(GunCHN2)
	EndIf
	If ChatSFXCHN <> 0 And ChannelPlaying(ChatSFXCHN) Then
		ResumeChannel(ChatSFXCHN)
	EndIf
	If NTF_ChatCHN1 <> 0 And ChannelPlaying(NTF_ChatCHN1) Then
		ResumeChannel(NTF_ChatCHN1)
	EndIf
	If NTF_ChatCHN2 <> 0 And ChannelPlaying(NTF_ChatCHN2) Then
		ResumeChannel(NTF_ChatCHN2)
	EndIf
	
	For ne = Each NewElevator
		If ne\soundchn <> 0 Then
			SetStreamPaused_Strict(ne\soundchn,False)
		EndIf
	Next

	If psp <> Null Then
		If psp\SoundCHN <> 0 And ChannelPlaying(psp\SoundCHN) Then
			ResumeChannel(psp\SoundCHN)
		EndIf
	EndIf	

End Function

Function KillSounds()
	Local i%,e.Events,n.NPCs,d.Doors,snd.Sound
	
	For i=0 To 9
		If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_isStream)
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_isStream)
				If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf		
	Next
	For n.NPCs = Each NPCs
		If n\SoundChn <> 0 Then
			If (Not n\SoundChn_IsStream)
				If ChannelPlaying(n\SoundChn) Then StopChannel(n\SoundChn)
			Else
				StopStream_Strict(n\SoundChn)
			EndIf
		EndIf
		If n\SoundChn2 <> 0 Then
			If (Not n\SoundChn2_IsStream)
				If ChannelPlaying(n\SoundChn2) Then StopChannel(n\SoundChn2)
			Else
				StopStream_Strict(n\SoundChn2)
			EndIf
		EndIf
		If n\ShootSFXCHN <> 0
			If ChannelPlaying(n\ShootSFXCHN) Then StopChannel(n\ShootSFXCHN)
		EndIf
	Next	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then StopChannel(d\SoundCHN)
		EndIf
	Next
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then StopChannel(AmbientSFXCHN)
	EndIf
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
	EndIf
	For snd.Sound = Each Sound
		If snd <> Object.Sound(CurrOverhaulSFX)
			For i = 0 To 31
				If snd\channels[i]<>0
					StopChannel snd\channels[i]
				EndIf
			Next
		EndIf
	Next
	If opt\EnableSFXRelease
		For snd.Sound = Each Sound
			If snd\internalHandle <> 0
				If snd <> Object.Sound(CurrOverhaulSFX)
					FreeSound snd\internalHandle
					snd\internalHandle = 0
					snd\releaseTime = 0
				EndIf
			EndIf
		Next
	EndIf
	;If CurrOverhaulChn<>0
	;	StopChannel(CurrOverhaulChn)
	;	FreeSound_Strict(CurrOverhaulSFX)
	;	CurrOverhaulChn=0
	;	CurrOverhaul=0 : OverhaulPlaying=0
	;EndIf
	
	DebugLog "Terminated all sounds"
	
End Function

Function GetStepSound(entity%,collradius#=0.3)
    Local picker%,brush%,texture%,name$,i%
    Local mat.Materials
	Local ne.NewElevator
	Local wa.Water
    
	For wa = Each Water
		If wa\isrendering Then
			If EntityY(entity)<(wa\customY+collradius#) Then
				Local p.Particles = CreateParticle(EntityX(entity),wa\customY,EntityZ(entity),10,0.05,0.0,500)
				p\SizeChange = 0.01
				p\Achange = -0.01
				SpriteViewMode p\obj,2
				RotateEntity p\obj,90,0,0
				Return 2
			EndIf
		EndIf
	Next
	
	;Unused
	;[Block]
;	If NTF_GameModeFlag<>3
;		If PlayerRoom\RoomTemplate\Name = "room2mt2"
;			If EntityY(entity)<(EntityY(PlayerRoom\Objects[3])+collradius#)
;				Local p.Particles = CreateParticle(EntityX(entity),EntityY(PlayerRoom\Objects[3]),EntityZ(entity),8,0.05,0.0,500)
;				p\SizeChange = 0.01
;				p\Achange = -0.01
;				SpriteViewMode p\obj,2
;				RotateEntity p\obj,90,0,0
;				;For i = 0 To 3
;				;	p.Particles = CreateParticle(EntityX(entity),EntityY(PlayerRoom\Objects[3]),EntityZ(entity),9,0.05,0.0,500)
;				;	p\SizeChange = 0.005
;				;	p\Achange = -0.01
;				;	p\speed = 0.005
;				;	SpriteViewMode p\obj,2
;				;	RotateEntity p\pvt,0,(90*i)+45,0
;				;	RotateEntity p\obj,90,EntityYaw(p\pvt),0
;				;Next
;				Return 4
;			EndIf
;		EndIf
;	EndIf
	;[End Block]
	
	For ne = Each NewElevator
		If Abs(EntityX(Collider)-EntityX(ne\obj,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
			If Abs(EntityZ(Collider)-EntityZ(ne\obj,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
				If Abs(EntityY(Collider)-EntityY(ne\obj,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
					Return 1
				EndIf
			EndIf
		EndIf
	Next
	
    picker = LinePick(EntityX(entity),EntityY(entity),EntityZ(entity),0,-1,0)
    If picker <> 0 Then
        If GetEntityType(picker) <> HIT_MAP Then Return 0
        brush = GetSurfaceBrush(GetSurface(picker,CountSurfaces(picker)))
        If brush <> 0 Then
			For i = 3 To 1 Step -1
				texture = GetBrushTexture(brush,i)
				If texture <> 0 Then
					name = StripPath(TextureName(texture))
					If (name <> "") Then
						FreeTexture(texture)
						For mat.Materials = Each Materials
							If mat\name = name Then
								If mat\StepSound > 0 Then
									FreeBrush(brush)
									Return mat\StepSound-1
								EndIf
								Exit
							EndIf
						Next
					EndIf
				EndIf
			Next
        EndIf
    EndIf
    
    Return 0
End Function

Function UpdateSoundOrigin(Chn%, cam%, entity%, range# = 10, volume# = 1.0, Eric = False)
	range# = Max(range,1.0)
	Local n.NPCs
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			If Eric Then
				ChannelVolume(Chn, volume# * (1 - dist#))
			Else
				ChannelVolume(Chn, volume# * (1 - dist#)*(opt\SFXVolume#*opt\MasterVol))
			EndIf
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
End Function

;--------------------------------------- random -------------------------------------------------------

Function f2s$(n#, count%)
	Return Left(n, Len(Int(Str(n)))+count+1)
End Function

Function AnimateNPC(n.NPCs, start#, quit#, speed#, loop=True)
	Local newTime#
	
	If speed > 0.0 Then 
		If n\Frame > quit Then n\Frame = start
		newTime = Max(Min(n\Frame + speed * FPSfactor,quit),start)
		
		If loop And newTime => quit Then
			newTime = start
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = n\Frame + speed * FPSfactor
			
			If newTime < quit Then 
				newTime = start
			Else If newTime > start 
				newTime = quit
			EndIf
		Else
			If n\Frame > start Then n\Frame = quit
			newTime = Max(Min(n\Frame + speed * FPSfactor,start),quit)
		EndIf
	EndIf
	SetNPCFrame(n, newTime)
	
End Function

Function SetNPCFrame(n.NPCs, frame#)
	If (Abs(n\Frame-frame)<0.001) Then Return
	
	SetAnimTime n\obj, frame
	
	n\Frame = frame
End Function

Function Animate2#(entity%, curr#, start%, quit%, speed#, loop=True)
	
	Local newTime#
	
	If speed > 0.0 Then 
		newTime = Max(Min(curr + speed * FPSfactor,quit),start)
		
		If loop Then
			If newTime => quit Then 
				;SetAnimTime entity, start
				newTime = start
			Else
				;SetAnimTime entity, newTime
			EndIf
		Else
			;SetAnimTime entity, newTime
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = curr + speed * FPSfactor
			
			If newTime < quit Then newTime = start
			If newTime > start Then newTime = quit
			
			;SetAnimTime entity, newTime
		Else
			;SetAnimTime (entity, Max(Min(curr + speed * FPSfactor,start),quit))
			newTime = Max(Min(curr + speed * FPSfactor,start),quit)
		EndIf
	EndIf
	
	SetAnimTime entity, newTime
	Return newTime
	
End Function

Function Use294()
	Local x#,y#, xtemp%,ytemp%, strtemp$, temp%
	
	ShowPointer()
	
	x = opt\GraphicWidth/2 - (ImageWidth(Panel294)/2)
	y = opt\GraphicHeight/2 - (ImageHeight(Panel294)/2)
	DrawImage Panel294, x, y
	
	temp = True
	If PlayerRoom\SoundCHN<>0 Then temp = False
	
	Text x+907, y+185, Input294, True,True
	
	If temp Then
		If MouseHit1 Then
			MouseHit1 = False
			xtemp = Floor((ScaledMouseX()-x-228) / 35.5)
			ytemp = Floor((ScaledMouseY()-y-342) / 36.5)
			
			If ytemp => 0 And ytemp < 5 Then
				If xtemp => 0 And xtemp < 10 Then PlaySound_Strict ButtonSFX
			EndIf
			
			strtemp = ""
			
			temp = False
			
			Select ytemp
				Case 0
					strtemp = (xtemp + 1) Mod 10
				Case 1
					Select xtemp
						Case 0
							strtemp = "Q"
						Case 1
							strtemp = "W"
						Case 2
							strtemp = "E"
						Case 3
							strtemp = "R"
						Case 4
							strtemp = "T"
						Case 5
							strtemp = "Y"
						Case 6
							strtemp = "U"
						Case 7
							strtemp = "I"
						Case 8
							strtemp = "O"
						Case 9
							strtemp = "P"
					End Select
				Case 2
					Select xtemp
						Case 0
							strtemp = "A"
						Case 1
							strtemp = "S"
						Case 2
							strtemp = "D"
						Case 3
							strtemp = "F"
						Case 4
							strtemp = "G"
						Case 5
							strtemp = "H"
						Case 6
							strtemp = "J"
						Case 7
							strtemp = "K"
						Case 8
							strtemp = "L"
						Case 9 ;dispense
							temp = True
					End Select
				Case 3
					Select xtemp
						Case 0
							strtemp = "Z"
						Case 1
							strtemp = "X"
						Case 2
							strtemp = "C"
						Case 3
							strtemp = "V"
						Case 4
							strtemp = "B"
						Case 5
							strtemp = "N"
						Case 6
							strtemp = "M"
						Case 7
							strtemp = "-"
						Case 8
							strtemp = " "
						Case 9
							Input294 = Left(Input294, Max(Len(Input294)-1,0))
					End Select
				Case 4
					strtemp = " "
			End Select
			
			Input294 = Input294 + strtemp
			
			Input294 = Left(Input294, Min(Len(Input294),15))
			
			If temp And Input294<>"" Then ;dispense
				Input294 = Trim(Lower(Input294))
				If Left(Input294, Min(7,Len(Input294))) = "cup of " Then
					Input294 = Right(Input294, Len(Input294)-7)
				ElseIf Left(Input294, Min(9,Len(Input294))) = "a cup of " 
					Input294 = Right(Input294, Len(Input294)-9)
				EndIf
				
				If Input294<>""
					Local loc% = GetINISectionLocation(Data294, Input294)
				EndIf
				
				If loc > 0 Then
					strtemp$ = GetINIString2(Data294, loc, "dispensesound")
					If strtemp="" Then
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(strtemp))
					EndIf
					
					If GetINIInt2(Data294, loc, "explosion")=True Then 
						ExplosionTimer = 135
						DeathMSG = GetINIString2(Data294, loc, "deathmessage")
					EndIf
					
					strtemp$ = GetINIString2(Data294, loc, "color")
					
					sep1 = Instr(strtemp, ",", 1)
					sep2 = Instr(strtemp, ",", sep1+1)
					r% = Trim(Left(strtemp, sep1-1))
					g% = Trim(Mid(strtemp, sep1+1, sep2-sep1-1))
					b% = Trim(Right(strtemp, Len(strtemp)-sep2))
					
					alpha# = Float(GetINIString2(Data294, loc, "alpha",1.0))
					glow = GetINIInt2(Data294, loc, "glow")
					;If alpha = 0 Then alpha = 1.0
					If glow Then alpha = -alpha
					
					it.items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1],True),EntityY(PlayerRoom\Objects[1],True),EntityZ(PlayerRoom\Objects[1],True), r,g,b,alpha)
					it\name = "Cup of "+Input294
					EntityType (it\collider, HIT_ITEM)
					
				Else
					;out of range
					Input294 = "OUT OF RANGE"
					PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\outofrange.ogg"))
				EndIf
				
			EndIf
			
		EndIf ;if mousehit1
		
		If MouseHit2 Lor (Not Using294) Lor InvOpen Then 
			HidePointer()
			Using294 = False
			Input294 = ""
		EndIf
		
	Else ;playing a dispensing sound
		If Input294 <> "OUT OF RANGE" Then Input294 = "DISPENSING..."
		
		If Not ChannelPlaying(PlayerRoom\SoundCHN) Then
			If Input294 <> "OUT OF RANGE" Then
				HidePointer()
				Using294 = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				Local e.Events
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0
						Exit
					EndIf
				Next
			EndIf
			Input294=""
			PlayerRoom\SoundCHN=0
		EndIf
	EndIf
	
End Function

Function Use427()
	Local i%,pvt%,de.Decals,tempchn%
	Local prevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70*360
		If I_427\Using=True Then
			I_427\Timer = I_427\Timer + FPSfactor
;			If Injuries > 0.0 Then
;				Injuries = Max(Injuries - 0.0005 * FPSfactor,0.0)
;			EndIf
;			If Bloodloss > 0.0 And Injuries <= 1.0 Then
;				Bloodloss = Max(Bloodloss - 0.001 * FPSfactor,0.0)
;			EndIf
			HealSPPlayer(0.05 * FPSfactor)
			If Infect > 0.0 Then
				Infect = Max(Infect - 0.001 * FPSfactor,0.0)
			EndIf
			For i = 0 To 5
				If SCP1025state[i]>0.0 Then
					SCP1025state[i] = Max(SCP1025state[i] - 0.001 * FPSfactor,0.0)
				EndIf
			Next
			If I_427\Sound[0]=0 Then
				I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			EndIf
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer => 70*180 Then
				If I_427\Sound[1]=0 Then
					I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				EndIf
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then
					I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
				EndIf
			EndIf
			If prevI427Timer < 70*60 And I_427\Timer => 70*60 Then
				Msg = "You feel refreshed and energetic."
				MsgTimer = 70*5
			ElseIf prevI427Timer < 70*180 And I_427\Timer => 70*180 Then
				Msg = "You feel gentle muscle spasms all over your body."
				MsgTimer = 70*5
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i]<>0 Then
					If ChannelPlaying(I_427\SoundCHN[i]) Then
						StopChannel(I_427\SoundCHN[i])
					EndIf
				EndIf
			Next
		EndIf
	Else
		If prevI427Timer-FPSfactor < 70*360 And I_427\Timer => 70*360 Then
			Msg = "Your muscles are swelling. You feel more powerful than ever."
			MsgTimer = 70*5
		ElseIf prevI427Timer-FPSfactor < 70*390 And I_427\Timer => 70*390 Then
			Msg = "You can't feel your legs. But you don't need legs anymore."
			MsgTimer = 70*5
		EndIf
		I_427\Timer = I_427\Timer + FPSfactor
		If I_427\Sound[0]=0 Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If I_427\Sound[1]=0 Then
			I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		EndIf
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then
				I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
			EndIf
		Next
		If Rnd(200)<2.0 Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.Decals = CreateDecal(DECAL_FOAM, PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\Size = Rnd(0.03,0.08)*2.0 : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
			tempchn% = PlaySound_Strict (DripSFX[Rand(0,3)])
			ChannelVolume tempchn, Rnd(0.0,0.8)*(opt\SFXVolume*opt\MasterVol)
			ChannelPitch tempchn, Rand(20000,30000)
			pvt = FreeEntity_Strict(pvt)
			BlurTimer = 800
		EndIf
		If I_427\Timer >= 70*420 Then
			Kill()
			DeathMSG = Chr(34)+"Requesting support from MTF Nu-7. We need more firepower to take this thing down."+Chr(34)
		ElseIf I_427\Timer >= 70*390 Then
			Crouch = True
		EndIf
	EndIf
	
End Function

Function UpdateMTF%()
	
	If MTFtimer > 0 Then
		If MTFtimer <= 70*120 ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 70*120 And MTFtimer < 10000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			EndIf
			MTFtimer = 10000
		ElseIf MTFtimer >= 10000 And MTFtimer <= 10000+(70*120) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 10000+(70*120) And MTFtimer < 20000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			EndIf
			MTFtimer = 20000
		ElseIf MTFtimer >= 20000 And MTFtimer <= 20000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 20000+(70*60) And MTFtimer < 25000
			If PlayerInReachableRoom()
				;Here it will just be left up to chance because in this mod you don't play D-9341.
				If Rand(1,7)=1
					PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncPossession.ogg")
				Else
					PlayAnnouncement("SFX\Character\MTF\ThreatAnnounc"+Rand(1,3)+".ogg")
				EndIf
			EndIf
			MTFtimer = 25000
			
		ElseIf MTFtimer >= 25000 And MTFtimer <= 25000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 25000+(70*60) And MTFtimer < 30000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			EndIf
			MTFtimer = 30000
			
		EndIf
	EndIf
	
End Function

Function UpdateInfect()
	Local temp#, i%, r.Rooms
	
	Local teleportForInfect% = True
	
	If PlayerRoom\RoomTemplate\Name = "testroom_860"
		For e.Events = Each Events
			If e\EventName = "testroom_860"
				If e\EventState = 1.0
					teleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Lor PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor NTF_CurrZone <> HCZ Then
		teleportForInfect = False
	EndIf
	
	If Infect>0 Then
		ShowEntity InfectOverlay
		
		If Infect < 93.0 Then
			temp=Infect
			Infect = Min(Infect+FPSfactor*0.002,100)
			
			BlurTimer = Max(Infect*3*(2.0-CrouchState),BlurTimer)
			
			HeartBeatRate = Max(HeartBeatRate, 100)
			HeartBeatVolume = Max(HeartBeatVolume, Infect/120.0)
			
			EntityAlpha InfectOverlay, Min(((Infect*0.2)^2)/1000.0,0.5) * (Sin(MilliSecs()/8.0)+2.0)
			
			For i = 0 To 6
				If Infect>i*15+10 And temp =< i*15+10 Then
					PlaySound_Strict LoadTempSound("SFX\SCP\008\Voices"+i+".ogg")
				EndIf
			Next
			
			If Infect > 20 And temp =< 20.0 Then
				Msg = "You feel kinda feverish."
				MsgTimer = 70*6
			ElseIf Infect > 40 And temp =< 40.0
				Msg = "You feel nauseated."
				MsgTimer = 70*6
			ElseIf Infect > 60 And temp =< 60.0
				Msg = "The nausea's getting worse."
				MsgTimer = 70*6
			ElseIf Infect > 80 And temp =< 80.0
				Msg = "You feel very faint."
				MsgTimer = 70*6
			ElseIf Infect =>91.5
				BlinkTimer = Max(Min(-10*(Infect-91.5),BlinkTimer),-10)
				IsZombie = True
				UnableToMove = True
				If Infect >= 92.7 And temp < 92.7 Then
					If teleportForInfect
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name="cont_008" Then
								PositionEntity Collider, EntityX(r\Objects[7],True),EntityY(r\Objects[7],True),EntityZ(r\Objects[7],True),True
								ResetEntity Collider
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6],True),EntityY(r\Objects[6],True)+0.2,EntityZ(r\Objects[6],True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundChn = PlaySound_Strict(r\NPC[0]\Sound)
								ChangeNPCTextureID(r\NPC[0],3)
								r\NPC[0]\State=6
								PlayerRoom = r
								UnableToMove = False
								IsZombie = True
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			
			temp=Infect
			Infect = Min(Infect+FPSfactor*0.004,100)
			
			If teleportForInfect
				If Infect < 94.7 Then
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/8.0)+2.0)
					BlurTimer = 900
					
					If Infect > 94.5 Then BlinkTimer = Max(Min(-50*(Infect-94.5),BlinkTimer),-10)
					PointEntity Collider, PlayerRoom\NPC[0]\Collider
					PointEntity PlayerRoom\NPC[0]\Collider, Collider
					PointEntity Camera, PlayerRoom\NPC[0]\Collider,EntityRoll(Camera)
					ForceMove = 0.3
					;Injuries = 2.5
					;Bloodloss = 0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 357, 381, 0.3)
				ElseIf Infect < 98.5
					
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/5.0)+2.0)
					BlurTimer = 950
					
					ForceMove = 0.0
					UnableToMove = True
					PointEntity Camera, PlayerRoom\NPC[0]\Collider
					
					If temp < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundChn = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						DeathMSG = Designation+" found ingesting Dr. [REDACTED] at Sector [REDACTED]. Subject was immediately terminated by Nine-Tailed Fox and sent for autopsy. "
						DeathMSG = DeathMSG + "SCP-008 infection was confirmed, after which the body was incinerated."
						
						Kill()
						de.Decals = CreateDecal(DECAL_BLOODSPLAT2, EntityX(PlayerRoom\NPC[0]\Collider), 544*RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider),90,Rnd(360),0)
						de\Size = 0.8
						ScaleSprite(de\obj, de\Size,de\Size)
					ElseIf Infect > 96
						BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
					Else
						KillTimer = Max(-350, KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2=0 Then
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 13, 19, 0.3,False)
						If AnimTime(PlayerRoom\NPC[0]\obj) => 19 Then PlayerRoom\NPC[0]\State2=1
					Else
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 19, 13, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\obj) =< 13 Then PlayerRoom\NPC[0]\State2=0
					EndIf
					
					If ParticleAmount>0
						If Rand(50)=1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider),EntityY(PlayerRoom\NPC[0]\Collider),EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05,0.1), 0.15, 200)
							p\speed = 0.01
							p\SizeChange = 0.01
							p\A = 0.5
							p\Achange = -0.01
							RotateEntity p\pvt, Rnd(360),Rnd(360),0
						EndIf
					EndIf
					
					PositionEntity Head, EntityX(PlayerRoom\NPC[0]\Collider,True), EntityY(PlayerRoom\NPC[0]\Collider,True)+0.65,EntityZ(PlayerRoom\NPC[0]\Collider,True),True
					RotateEntity Head, (1.0+Sin(MilliSecs()/5.0))*15, PlayerRoom\angle-180, 0, True
					MoveEntity Head, 0,0,-0.4
					TurnEntity Head, 80+(Sin(MilliSecs()/5.0))*30,(Sin(MilliSecs()/5.0))*40,0
				EndIf
			Else
				Kill()
				BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
				If PlayerRoom\RoomTemplate\Name = "dimension1499"
					DeathMSG = "The whereabouts of SCP-1499 are still unknown, but a recon team has been dispatched to investigate repots of a violent attack to a church in the Russian town of [REDACTED]."
				Else
					DeathMSG = ""
				EndIf
			EndIf
		EndIf
		
		
	Else
		HideEntity InfectOverlay
	EndIf
End Function

Function TakeOffStuff(flag%=0)
	;FLAG variables:
		;1: GasMask
		;2: Hazmat Suit
		;4: SCP-714
		;8: SCP-178
		;16: Kevlar Vest
		;32: Night Vision Goggles
		;64: SCP-1499
	
	Local numb_flag% = Bin(flag%)
	
	If Right(numb_flag%,1) = 1
		WearingGasMask = False
		DebugLog "GasMask Off"
	EndIf
	If Len(numb_flag%)>1
		If Mid(numb_flag%,Len(numb_flag%)-1,1) = 1
			WearingHazmat = False
			For i = 0 To MaxItemAmount-1
				If Inventory[i] <> Null Then
					If Inventory[i]\itemtemplate\name = "Hazmat Suit" Lor Inventory[i]\itemtemplate\tempname = "hazmatsuit3"
						DropItem(Inventory[i])
						Exit
					EndIf
				EndIf
			Next
			DebugLog "Hazmat Off"
		EndIf
	EndIf
	If Len(numb_flag%)>2
		If Mid(numb_flag%,Len(numb_flag%)-2,1) = 1
			Wearing714 = False
			DebugLog "SCP-714 Off"
		EndIf
	EndIf
	If Len(numb_flag%)>3
		If Mid(numb_flag%,Len(numb_flag%)-3,1) = 1
			
			DebugLog "SCP-178 Off"
		EndIf
	EndIf
	If Len(numb_flag%)>4
		If Mid(numb_flag%,Len(numb_flag%)-4,1) = 1
			WearingVest = False
			DebugLog "Kevlar Off"
		EndIf
	EndIf
	If Len(numb_flag%)>5
		If Mid(numb_flag%,Len(numb_flag%)-5,1) = 1
			
			CameraFogFar = StoredCameraFogFar
			DebugLog "NVG Off"
		EndIf
	EndIf
	If Len(numb_flag%)>6
		If Mid(numb_flag%,Len(numb_flag%)-6,1) = 1
			Wearing1499 = False
			DebugLog "SCP-1499 Off"
		EndIf
	EndIf
	
End Function

;--------------------------------------- decals -------------------------------------------------------

Include "SourceCode/Decals.bb"

;--------------------------------------- INI-functions -------------------------------------------------------

Type INIFile
	Field name$
	Field bank%
	Field bankOffset% = 0
	Field size%
End Type

Function ReadINILine$(file.INIFile)
	Local rdbyte%
	Local firstbyte% = True
	Local offset% = file\bankOffset
	Local bank% = file\bank
	Local retStr$ = ""
	rdbyte = PeekByte(bank,offset)
	While ((firstbyte) Lor ((rdbyte<>13) And (rdbyte<>10))) And (offset<file\size)
		rdbyte = PeekByte(bank,offset)
		If ((rdbyte<>13) And (rdbyte<>10)) Then
			firstbyte = False
			retStr=retStr+Chr(rdbyte)
		EndIf
		offset=offset+1
	Wend
	file\bankOffset = offset
	Return retStr
End Function

Function UpdateINIFile$(filename$)
	Local file.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(filename) Then
			file = k
			Exit
		EndIf
	Next
	
	If file=Null Then Return
	
	If file\bank<>0 Then FreeBank file\bank
	Local f% = ReadFile(file\name)
	Local fleSize% = 1
	While fleSize<FileSize(file\name)
		fleSize=fleSize*2
	Wend
	file\bank = CreateBank(fleSize)
	file\size = 0
	While Not Eof(f)
		PokeByte(file\bank,file\size,ReadByte(f))
		file\size=file\size+1
	Wend
	CloseFile(f)
End Function

Function DeleteINIFile(filename$)
	If FileType(filename) <> 0 Then
		Local file.INIFile = Null
		For k.INIFile = Each INIFile
			If k\name = Lower(filename) Then
				file = k
				Exit
			EndIf
		Next
		If file <> Null Then
			FreeBank file\bank
			DebugLog "FREED BANK FOR "+filename
			Delete file
			Return
		EndIf
	EndIf
	DebugLog "COULD NOT FREE BANK FOR "+filename+": INI FILE IS NOT LOADED"
End Function

Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	
	Local lfile.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
			Exit
		EndIf
	Next
	
	If lfile = Null Then
		DebugLog "CREATE BANK FOR "+file
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	;While Not Eof(f)
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						;CloseFile f
						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
					EndIf
				Until (Left(TemporaryString, 1) = "[") Lor (lfile\bankOffset>=lfile\size)
				
				;CloseFile f
				Return defaultvalue
			EndIf
		EndIf
	Wend
	
	Return defaultvalue
End Function

Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
End Function


Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
			CloseFile f
			Return defaultvalue
		EndIf
	Wend
	
	CloseFile f	
	
	Return defaultvalue
End Function

Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function


Function GetINISectionLocation%(file$, section$)
	Local Temp%
	Local f% = ReadFile(file)
	
	section = Lower(section)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			Temp = Instr(strtemp, section)
			If Temp>0 Then
				If (Mid(strtemp, Temp-1, 1)="[" Lor Mid(strtemp, Temp-1, 1)="|") And (Mid(strtemp, Temp+Len(section), 1)="]" Lor Mid(strtemp, Temp+Len(section), 1)="|") Then
					CloseFile f
					Return n
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile f
End Function



Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
	
	; Returns: True (Success) Lor False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	Local INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	Local INI_sFilename$ = file$
	
	; Retrieve the INI Data (If it exists)
	
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	
		; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
	
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
					; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine INI_lFileHandle, INI_sTemp
				Else
						; KEY=VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine INI_lFileHandle, INI_sTemp
						End If
					End If
				EndIf
				
			End If
			
		End If
		
			; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
		; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	End If
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function

Function INI_FileToString$(INI_sFilename$)
	
	Local INI_sString$ = ""
	Local INI_lFileHandle%= ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	End If
	Return INI_sString
	
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
	
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	
	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
	Return True
	
End Function

;Save options to .ini.
Function SaveOptionsINI()
	
	PutINIValue(gv\OptionFile, "options", "mouse sensitivity", MouseSens)
	PutINIValue(gv\OptionFile, "options", "mouse smoothing", opt\MouseSmooth)
	PutINIValue(gv\OptionFile, "options", "invert mouse y", InvertMouse)
	PutINIValue(gv\OptionFile, "options", "hold to aim", opt\HoldToAim)
	PutINIValue(gv\OptionFile, "options", "hold to crouch", opt\HoldToCrouch)
	PutINIValue(gv\OptionFile, "options", "bump mapping enabled", BumpEnabled)			
	PutINIValue(gv\OptionFile, "options", "HUD enabled", HUDenabled)
	PutINIValue(gv\OptionFile, "options", "screengamma", ScreenGamma)
	PutINIValue(gv\OptionFile, "options", "vsync", Vsync)
	PutINIValue(gv\OptionFile, "options", "show FPS", opt\ShowFPS)
	PutINIValue(gv\OptionFile, "options", "framelimit", Framelimit%)
	PutINIValue(gv\OptionFile, "options", "achievement popup enabled", AchvMSGenabled%)
	PutINIValue(gv\OptionFile, "options", "room lights enabled", opt\EnableRoomLights%)
	PutINIValue(gv\OptionFile, "options", "texture details", opt\TextureDetails%)
	PutINIValue(gv\OptionFile, "options", "texture filtering", opt\TextureFiltering%)
	PutINIValue(gv\OptionFile, "console", "enabled", opt\ConsoleEnabled%)
	PutINIValue(gv\OptionFile, "console", "auto opening", opt\ConsoleOpening%)
	PutINIValue(gv\OptionFile, "options", "particle amount", ParticleAmount)
	PutINIValue(gv\OptionFile, "options", "enable vram", SaveTexturesInVRam)
	PutINIValue(gv\OptionFile, "options", "cubemaps", opt\RenderCubeMapMode)
	
	PutINIValue(gv\OptionFile, "audio", "master volume", opt\MasterVol)
	PutINIValue(gv\OptionFile, "audio", "music volume", opt\MusicVol)
	PutINIValue(gv\OptionFile, "audio", "sound volume", opt\SFXVolume)
	PutINIValue(gv\OptionFile, "audio", "voice volume", opt\VoiceVol)
	PutINIValue(gv\OptionFile, "audio", "sfx release", opt\EnableSFXRelease)
	PutINIValue(gv\OptionFile, "audio", "enable user tracks", EnableUserTracks%)
	PutINIValue(gv\OptionFile, "audio", "user track setting", UserTrackMode%)
	
	PutINIValue(gv\OptionFile, "binds", "Right key", KEY_RIGHT)
	PutINIValue(gv\OptionFile, "binds", "Left key", KEY_LEFT)
	PutINIValue(gv\OptionFile, "binds", "Up key", KEY_UP)
	PutINIValue(gv\OptionFile, "binds", "Down key", KEY_DOWN)
	PutINIValue(gv\OptionFile, "binds", "Blink key", KEY_BLINK)
	PutINIValue(gv\OptionFile, "binds", "Sprint key", KEY_SPRINT)
	PutINIValue(gv\OptionFile, "binds", "Inventory key", KEY_INV)
	PutINIValue(gv\OptionFile, "binds", "Crouch key", KEY_CROUCH)
	PutINIValue(gv\OptionFile, "binds", "Save key", KEY_SAVE)
	PutINIValue(gv\OptionFile, "binds", "Console key", KEY_CONSOLE)
	
	;NTF mod options
	PutINIValue(gv\OptionFile, "binds", "Reload key", KEY_RELOAD)
	;PutINIValue(gv\OptionFile, "binds", "Chat key", KEY_CHAT)
	PutINIValue(gv\OptionFile, "binds", "Holstergun key", KEY_HOLSTERGUN)
	PutINIValue(gv\OptionFile, "binds", "Radiotoggle key", KEY_RADIOTOGGLE)
	PutINIValue(gv\OptionFile, "binds", "Use key", KEY_USE)
	PutINIValue(gv\OptionFile, "options", "fov", FOV)
	;PutINIValue(gv\OptionFile, "options", "pack", "English") TODO
	SaveController()
	SaveKeyBinds()
	
	PutINIValue(gv\OptionFile, "game options", "game mode", gopt\SingleplayerGameMode)
	
End Function

Function Graphics3DExt%(width%,height%,depth%=32,mode%=2)
	
	SetGfxDriver(opt\GraphicDriver+1)
	Graphics3D width,height,depth,mode
	TextureFilter "", 8192 ;This turns on Anisotropic filtering for textures. Use TextureAnisotropic to change anisotropic level.
	InitFastResize()
	
End Function

Function ResizeImage2(image%,width%,height%)
	Local img% = CopyImage(image)
	FreeImage image
	ResizeImage(img,width,height)
	Return img
	
	;TODO Broken with dgVoodoo implementation
	
;	Local img%, oldWidth%, oldHeight%
;	
;    img% = CreateImage(width,height)
;	
;	oldWidth% = ImageWidth(image)
;	oldHeight% = ImageHeight(image)
;	CopyRect 0,0,oldWidth,oldHeight,2048-oldWidth/2,2048-oldHeight/2,ImageBuffer(image),TextureBuffer(fresize_texture)
;	SetBuffer BackBuffer()
;	ScaleRender(0,0,4096.0 / Float(RealGraphicWidth) * Float(width) / Float(oldWidth), 4096.0 / Float(RealGraphicWidth) * Float(height) / Float(oldHeight))
;	;might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth,opt\GraphicHeight) if portrait sizes cause issues
;	;everyone uses landscape so it's probably a non-issue
;	CopyRect RealGraphicWidth/2-width/2,RealGraphicHeight/2-height/2,width,height,0,0,BackBuffer(),ImageBuffer(img)
;	
;    FreeImage image
;    Return img
End Function


Function RenderWorld2(tween#)
	Local temp%, temp2%, dist#, pitchvalue#, yawvalue#, xvalue#, yvalue#
	Local np.NPCs
	Local i%, k%, l%
	
	CameraProjMode ark_blur_cam,0
	CameraProjMode Camera,1
	
	If WearingNightVision>0 And WearingNightVision<3 Then
		AmbientLight Min(Brightness*2,255), Min(Brightness*2,255), Min(Brightness*2,255)
	ElseIf WearingNightVision=3
		AmbientLight 255,255,255
	ElseIf PlayerRoom<>Null
		If (PlayerRoom\RoomTemplate\Name<>"173") And (PlayerRoom\RoomTemplate\Name<>"exit1") And (PlayerRoom\RoomTemplate\Name<>"gatea") Then
			AmbientLight Brightness, Brightness, Brightness
		EndIf
	EndIf
	
	IsNVGBlinking% = False
	HideEntity NVBlink
	
	CameraViewport Camera,0,0,opt\GraphicWidth,opt\GraphicHeight
	
	Local hasBattery% = 2
	Local power% = 0
	If (WearingNightVision=1) Lor (WearingNightVision=2)
		For i% = 0 To MaxItemAmount - 1
			If (Inventory[i]<>Null) Then
				If (WearingNightVision = 1 And Inventory[i]\itemtemplate\tempname = "nvgoggles") Lor (WearingNightVision = 2 And Inventory[i]\itemtemplate\tempname = "supernv") Then
					Inventory[i]\state = Inventory[i]\state - (FPSfactor * (0.02 * WearingNightVision))
					power%=Int(Inventory[i]\state)
					If Inventory[i]\state<=0.0 Then ;this nvg can't be used
						hasBattery = 0
						Msg = "The batteries in these night vision goggles died."
						BlinkTimer = -1.0
						MsgTimer = 350
						Exit
					ElseIf Inventory[i]\state<=100.0 Then
						hasBattery = 1
					EndIf
				EndIf
			EndIf
		Next
		
		If (hasBattery) Then
			RenderWorld(tween)
		EndIf
	Else
		RenderWorld(tween)
	EndIf
	
	CurrTrisAmount = TrisRendered()
	
	If hasBattery=0 And WearingNightVision<>3
		IsNVGBlinking% = True
		ShowEntity NVBlink%
	EndIf
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6
		If WearingNightVision=2 And hasBattery<>0 Then ;show a HUD
			NVTimer=NVTimer-FPSfactor
			
			If NVTimer<=0.0 Then
				For np.NPCs = Each NPCs
					np\NVX = EntityX(np\Collider,True)
					np\NVY = EntityY(np\Collider,True)
					np\NVZ = EntityZ(np\Collider,True)
				Next
				IsNVGBlinking% = True
				ShowEntity NVBlink%
				If NVTimer<=-10
					NVTimer = 600.0
				EndIf
			EndIf
			
			Color 255,255,255
			
			SetFont fo\Font[Font_Digital_Large]
			
			Local plusY% = 0
			If hasBattery=1 Then plusY% = 40
			
			Text opt\GraphicWidth/2,(20+plusY)*MenuScale,"REFRESHING DATA IN",True,False
			
			Text opt\GraphicWidth/2,(60+plusY)*MenuScale,Max(f2s(NVTimer/60.0,1),0.0),True,False
			Text opt\GraphicWidth/2,(100+plusY)*MenuScale,"SECONDS",True,False
			
			temp% = CreatePivot() : temp2% = CreatePivot()
			PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
			
			Color 255,255,255;*(NVTimer/600.0)
			
			For np.NPCs = Each NPCs
				If np\NVName<>"" And (Not np\HideFromNVG) Then ;don't waste your time if the string is empty
					PositionEntity temp2,np\NVX,np\NVY,np\NVZ
					dist# = EntityDistance(temp2,Collider)
					If dist<23.5 Then ;don't draw text if the NPC is too far away
						PointEntity temp, temp2
						yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
						xvalue# = 0.0
						If yawvalue > 90 And yawvalue <= 180 Then
							xvalue# = Sin(90)/90*yawvalue
						Else If yawvalue > 180 And yawvalue < 270 Then
							xvalue# = Sin(270)/yawvalue*270
						Else
							xvalue = Sin(yawvalue)
						EndIf
						pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
						yvalue# = 0.0
						If pitchvalue > 90 And pitchvalue <= 180 Then
							yvalue# = Sin(90)/90*pitchvalue
						Else If pitchvalue > 180 And pitchvalue < 270 Then
							yvalue# = Sin(270)/pitchvalue*270
						Else
							yvalue# = Sin(pitchvalue)
						EndIf
						
						If (Not IsNVGBlinking%)
							Text opt\GraphicWidth / 2 + xvalue * (opt\GraphicWidth / 2),opt\GraphicHeight / 2 - yvalue * (opt\GraphicHeight / 2),np\NVName,True,True
							Text opt\GraphicWidth / 2 + xvalue * (opt\GraphicWidth / 2),opt\GraphicHeight / 2 - yvalue * (opt\GraphicHeight / 2) + 30.0 * MenuScale,f2s(dist,1)+" m",True,True
						EndIf
					EndIf
				EndIf
			Next
			
			temp = FreeEntity_Strict(temp)
			temp2 = FreeEntity_Strict(temp2)
			
			Color 0,0,55
			For k=0 To 10
				Rect 45,opt\GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,0,255
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,opt\GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,opt\GraphicHeight*0.5+30,1
			
			Color 255,255,255
		ElseIf WearingNightVision=1 And hasBattery<>0
			Color 0,55,0
			For k=0 To 10
				Rect 45,opt\GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,255,0
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,opt\GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,opt\GraphicHeight*0.5+30,0
		EndIf
	EndIf
	
	;render sprites
	CameraProjMode ark_blur_cam,2
	CameraProjMode Camera,0
	RenderWorld()
	CameraProjMode ark_blur_cam,0
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6 Then
		If (WearingNightVision=1 Or WearingNightVision=2) And (hasBattery=1) And ((MilliSecs() Mod 800) < 400) Then
			Color 255,0,0
			SetFont fo\Font[Font_Digital_Large]
			
			Text opt\GraphicWidth/2,20*MenuScale,"WARNING: LOW BATTERY",True,False
			Color 255,255,255
		EndIf
	EndIf
	
End Function


Function ScaleRender(x#,y#,hscale#=1.0,vscale#=1.0)
	If Camera<>0 Then HideEntity Camera
	WireFrame 0
	ShowEntity fresize_image
	ScaleEntity fresize_image,hscale,vscale,1.0
	PositionEntity fresize_image, x, y, 1.0001
	ShowEntity fresize_cam
	RenderWorld()
	HideEntity fresize_cam
	HideEntity fresize_image
	WireFrame WireframeState
	If Camera<>0 Then ShowEntity Camera
End Function

Function InitFastResize()
    ;Create Camera
	Local cam% = CreateCamera()
	CameraProjMode cam, 2
	CameraZoom cam, 0.1
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, -10000
	
	fresize_cam = cam
	
    ;ark_sw = GraphicsWidth()
    ;ark_sh = GraphicsHeight()
	
    ;Create sprite
	Local spr% = CreateMesh(cam)
	Local sf% = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100001
	EntityBlend spr, 1
	fresize_image = spr
	
    ;Create texture
	fresize_texture = CreateTexture(4096, 4096, 1+256)
	fresize_texture2 = CreateTexture(4096, 4096, 1+256)
	TextureBlend fresize_texture2,3
	If fresize_texture2<>0 Then
		SetBuffer(TextureBuffer(fresize_texture2))
	EndIf
	ClsColor 0,0,0
	Cls
	SetBuffer(BackBuffer())
	;TextureAnisotropy(fresize_texture)
	EntityTexture spr, fresize_texture,0,0
	EntityTexture spr, fresize_texture2,0,1
	
	HideEntity fresize_cam
End Function

;--------------------------------------- Some new 1.3 -functions -------------------------------------------------------

Function UpdateLeave1499()
	Local r.Rooms, it.Items,r2.Rooms,i%
	Local r1499.Rooms
	
	If (Not Wearing1499) And PlayerRoom\RoomTemplate\Name$ = "dimension1499"
		For r.Rooms = Each Rooms
			If r = NTF_1499PrevRoom
				BlinkTimer = -1
				NTF_1499X# = EntityX(Collider)
				NTF_1499Y# = EntityY(Collider)
				NTF_1499Z# = EntityZ(Collider)
				PositionEntity (Collider, NTF_1499PrevX#, NTF_1499PrevY#+0.05, NTF_1499PrevZ#)
				ResetEntity(Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(Collider)<-4600*RoomScale
						For i = 0 To 2
							PlayerRoom\NPC[i]\State = 2
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True),EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True)+0.2,EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True))
							ResetEntity PlayerRoom\NPC[i]\Collider
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState-3)
						Next
					EndIf
				ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
					CameraFogColor Camera, 0,0,0
					CameraClsColor Camera, 0,0,0
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499"
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\disttimer = 0
					If it\itemtemplate\tempname = "scp1499" Lor it\itemtemplate\tempname = "super1499"
						If EntityY(it\collider) >= EntityY(r1499\obj)-5
							PositionEntity it\collider,NTF_1499PrevX#,NTF_1499PrevY#+(EntityY(it\collider)-EntityY(r1499\obj)),NTF_1499PrevZ#
							ResetEntity it\collider
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict NTF_1499LeaveSFX%
				NTF_1499PrevX# = 0.0
				NTF_1499PrevY# = 0.0
				NTF_1499PrevZ# = 0.0
				NTF_1499PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
	
End Function

Function CheckForPlayerInFacility()
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(Collider)>100.0
		Return False
	EndIf
	If EntityY(Collider)< -10.0
		Return 2
	EndIf
	If EntityY(Collider)> 7.0 And EntityY(Collider)<=100.0
		Return 2
	EndIf
	
	Return True
End Function

Function IsItemGoodFor1162(itt.ItemTemplates)
	
	Select itt\tempname
		Case "key1", "key2", "key3"
			Return True
		Case "misc", "420", "cigarette"
			Return True
		Case "vest", "finevest","gasmask"
			Return True
		Case "radio","18vradio"
			Return True
		Case "clipboard","eyedrops","nvgoggles"
			Return True
		Case "drawing"
			If itt\img<>0 Then FreeImage itt\img	
			itt\img = LoadImage_Strict("GFX\items\1048\1048_"+Rand(1,20)+".jpg") ;Gives a random drawing.
			Return True
		Default
			If itt\tempname <> "paper" Then
				Return False
			Else If Instr(itt\name, "Leaflet")
				Return False
			Else
				;if the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				;(because those are items created recently, which D-9341 has most likely never seen)
				Return ((Not Instr(itt\name, "Note")) And (Not Instr(itt\name, "Log")))
			EndIf
	End Select
End Function

Function ControlSoundVolume()
	Local snd.Sound,i
	
	For snd.Sound = Each Sound
		For i=0 To 31
			;If snd\channels[i]<>0 Then
			;	ChannelVolume snd\channels[i],opt\SFXVolume#
			;Else
			ChannelVolume snd\channels[i],opt\SFXVolume#
			;EndIf
		Next
	Next
	
End Function

Function UpdateDeafPlayer()
	
	If DeafTimer > 0
		DeafTimer = DeafTimer-FPSfactor
		opt\SFXVolume# = 0.0
		If opt\SFXVolume# > 0.0
			ControlSoundVolume()
		EndIf
		DebugLog DeafTimer
	Else
		DeafTimer = 0
		;If opt\SFXVolume# < PrevSFXVolume#
		;	opt\SFXVolume# = Min(opt\SFXVolume# + (0.001*PrevSFXVolume)*FPSfactor,PrevSFXVolume#)
		;	ControlSoundVolume()
		;Else
		;opt\SFXVolume# = PrevSFXVolume#
		If DeafPlayer Then ControlSoundVolume()
		DeafPlayer = False
		;EndIf
	EndIf
	
End Function

Function ScaledMouseX%()
	Return Float(MousePosX-(RealGraphicWidth*0.5*(1.0-AspectRatioRatio)))*Float(opt\GraphicWidth)/Float(RealGraphicWidth*AspectRatioRatio)
End Function

Function ScaledMouseY%()
	Return MousePosY*Float(opt\GraphicHeight)/Float(RealGraphicHeight)
End Function

Function CatchErrors(location$)
	;errtxt = errtxt+"Video memory: "+((TotalVidMem()/1024)-(AvailVidMem()/1024))+" MB/"+(TotalVidMem()/1024)+" MB"+Chr(10)
	;errtxt = errtxt+"Global memory status: "+((TotalPhys()/1024)-(AvailPhys()/1024))+" MB/"+(TotalPhys()/1024)+" MB"+Chr(10)
	SetErrorMsg(7, "Error located in: "+location)
	;[Block]
;	Local errF%
;	If Len(errStr)>0 Then
;		If FileType(gv\ErrorFile)=0 Then
;			errF = WriteFile(gv\ErrorFile)
;			WriteLine errF,"An error occured in SCP - Containment Breach!"
;			WriteLine errF,"Version: "+VersionNumber
;			WriteLine errF,"Save compatible version: "+CompatibleNumber
;			WriteLine errF,"Date and time: "+CurrentDate()+" at "+CurrentTime()
;			WriteLine errF,"Total video memory (MB): "+TotalVidMem()/1024/1024
;			WriteLine errF,"Available video memory (MB): "+AvailVidMem()/1024/1024
;			WriteLine errF,"Global memrory status: "+(AvailPhys()/1024)+" MB/"+(TotalPhys()/1024)+" MB ("+AvailPhys()+" KB/"+TotalPhys()+" KB)"
;			WriteLine errF,"Triangles rendered: "+CurrTrisAmount
;			WriteLine errF,"Active textures: "+ActiveTextures()
;			WriteLine errF,""
;			WriteLine errF,"Error(s):"
;		Else
;			Local canwriteError% = True
;			errF = OpenFile(gv\ErrorFile)
;			While (Not Eof(errF))
;				Local l$ = ReadLine(errF)
;				If Left(l,Len(location))=location
;					canwriteError = False
;					Exit
;				EndIf
;			Wend
;			If canwriteError
;				SeekFile errF,FileSize(gv\ErrorFile)
;			EndIf
;		EndIf
;		If canwriteError
;			WriteLine errF,location+" ***************"
;			While Len(errStr)>0
;				WriteLine errF,errStr
;				DebugLog errStr
;				;errStr = ErrorLog()
;			Wend
;		EndIf
;		Msg = "Blitz3D Error! Details in "+Chr(34)+gv\ErrorFile+Chr(34)
;		MsgTimer = 20*70
;		CloseFile errF
;	EndIf
	;[End Block]
End Function

Function PlayAnnouncement(file$) ;This function streams the announcement currently playing
	
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(file$,opt\SFXVolume*opt\MasterVol,0)
	
End Function

Function UpdateStreamSounds()
	Local e.Events
	
	If FPSfactor > 0
		If IntercomStreamCHN <> 0
			SetStreamVolume_Strict(IntercomStreamCHN,opt\SFXVolume*opt\MasterVol)
		EndIf
		For e = Each Events
			If e\SoundCHN<>0
				If e\SoundCHN_isStream
					SetStreamVolume_Strict(e\SoundCHN,opt\SFXVolume*opt\MasterVol)
				EndIf
			EndIf
			If e\SoundCHN2<>0
				If e\SoundCHN2_isStream
					SetStreamVolume_Strict(e\SoundCHN2,opt\SFXVolume*opt\MasterVol)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom())
		If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea"
			If IntercomStreamCHN <> 0
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
			For e = Each Events
				If e\SoundCHN<>0 And e\SoundCHN_isStream
					StopStream_Strict(e\SoundCHN)
					e\SoundCHN = 0
					e\SoundCHN_isStream = 0
				EndIf
				If e\SoundCHN2<>0 And e\SoundCHN2_isStream
					StopStream_Strict(e\SoundCHN2)
					e\SoundCHN2 = 0
					e\SoundCHN2_isStream = 0
				EndIf
			Next
		EndIf
	EndIf
	
End Function

Function InitFonts()
	Local txt$
	
	fo\Font[Font_Default] = LoadFont("GFX\font\Courier New.ttf", Int(16 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Default_Medium] = LoadFont("GFX\font\Courier New.ttf", Int(28 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Default_Large] = LoadFont("GFX\font\Courier New.ttf", Int(46 * (opt\GraphicHeight / 1024.0))) ;TODO make this use a bold font
	fo\Font[Font_Menu_Small] = LoadFont("GFX\font\Capture It.ttf",Int(23 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Menu_Medium] = LoadFont("GFX\font\Capture It.ttf",Int(42 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Menu] = LoadFont("GFX\font\Capture It.ttf", Int(56 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Digital_Small] = LoadFont("GFX\font\DS-Digital.ttf", Int(20 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Digital_Medium] = LoadFont("GFX\font\DS-Digital.ttf", Int(28 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Digital_Large] = LoadFont("GFX\font\DS-Digital.ttf", Int(58 * (opt\GraphicHeight / 1024.0)))
	fo\Font[Font_Journal] = LoadFont("GFX\font\Journal.ttf", Int(56 * (opt\GraphicHeight / 1024.0)))
	
	fo\ConsoleFont% = LoadFont("GFX\font\Minimal5x7.ttf", Int(28 * (opt\GraphicHeight / 1024.0)))
	
	SetFont fo\Font[Font_Menu]
	
End Function

Function SetMsgColor(r%, g%, b%)
	a# = Min(MsgTimer / 2, 255)/255.0
	Color a * r, a * g, a * b
End Function

Function TeleportEntity(entity%,x#,y#,z#,customradius#=0.3,isglobal%=False,pickrange#=2.0,dir%=0)
	Local pvt,pick
	;dir = 0 - towards the floor (default)
	;dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	pvt = CreatePivot()
	PositionEntity(pvt, x,y+0.05,z,isglobal)
	If dir%=0
		RotateEntity pvt,90,0,0
	Else
		RotateEntity pvt,-90,0,0
	EndIf
	pick = EntityPick(pvt,pickrange)
	If pick<>0
		If dir%=0
			PositionEntity(entity, x,PickedY()+customradius#+0.02,z,isglobal)
		Else
			PositionEntity(entity, x,PickedY()+customradius#-0.02,z,isglobal)
		EndIf
		DebugLog "Entity teleported successfully"
	Else
		PositionEntity(entity,x,y,z,isglobal)
		DebugLog "Warning: no ground found when teleporting an entity"
	EndIf
	pvt = FreeEntity_Strict(pvt)
	ResetEntity entity
	DebugLog "Teleported entity to: "+EntityX(entity)+"/"+EntityY(entity)+"/"+EntityZ(entity)
	
End Function

Function PlayStartupVideos()
	
	If GetINIInt(gv\OptionFile,"options","play startup video",1)=0 Then Return
	
	HidePointer()
	
	Local ScaledGraphicHeight%,SplashScreenVideo
	Local Ratio# = Float(RealGraphicWidth)/Float(RealGraphicHeight)
	If Ratio>1.76 And Ratio<1.78
		ScaledGraphicHeight = RealGraphicHeight
		DebugLog "Not Scaled"
	Else
		ScaledGraphicHeight% = Float(RealGraphicWidth)/(16.0/9.0)
		DebugLog "Scaled: "+ScaledGraphicHeight
	EndIf
	
	Local i, moviefile$
	For i = 0 To 2
		Select i
			Case 0
				moviefile$ = "GFX\menu\startup_Undertow"
			Case 1
				moviefile$ = "GFX\menu\startup_TSS"
			Case 2
				moviefile$ = "GFX\menu\startup_NTF"
		End Select
		
		SplashScreenVideo = BlitzMovie_OpenD3D(moviefile$+".wmv", SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"))
			
		If SplashScreenVideo = 0 Then
			PutINIValue(gv\OptionFile, "options", "play startup video", "false")
			Return
		EndIf
		
		SplashScreenVideo = BlitzMovie_Play()
		Local SplashScreenAudio = StreamSound_Strict(moviefile$+".ogg",opt\SFXVolume,0)
		
		Repeat
			Cls
			BlitzMovie_DrawD3D(0, (RealGraphicHeight/2-ScaledGraphicHeight/2), RealGraphicWidth, ScaledGraphicHeight)
			Flip 1
			Delay 10
		Until (GetKey() Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		
		StopStream_Strict(SplashScreenAudio)
		BlitzMovie_Stop()
		BlitzMovie_Close()
		Cls
		Flip 1
	Next
	
	ShowPointer()
	
End Function

Function ProjectImage(img, w#, h#, Quad%, Texture%)
    Local img_w# = ImageWidth(img)
    Local img_h# = ImageHeight(img)
    If img_w > 4096 Then img_w = 4096
    If img_h > 4096 Then img_h = 4096
    If img_w < 1 Then img_w = 1
    If img_h < 1 Then img_h = 1
    
    If w > 4096 Then w = 4096
    If h > 4096 Then h = 4096
    If w < 1 Then w = 1
    If h < 1 Then h = 1
    
    Local w_rel# = w# / img_w#
    Local h_rel# = h# / img_h#
    Local g_rel# = 2048.0 / Float(RealGraphicWidth)
    Local dst_x = 1024 - (img_w / 2.0)
    Local dst_y = 1024 - (img_h / 2.0)
    CopyRect 0, 0, img_w, img_h, dst_x, dst_y, ImageBuffer(img), TextureBuffer(Texture)
    ScaleEntity Quad, w_rel * g_rel, h_rel * g_rel, 0.0001
    RenderWorld()
	
End Function

Function CreateQuad()
	Local mesh%,surf%,v0%,v1%,v2%,v3%
	
	mesh = CreateMesh()
	surf = CreateSurface(mesh)
	v0 = AddVertex(surf,-1.0, 1.0, 0, 0, 0)
	v1 = AddVertex(surf, 1.0, 1.0, 0, 1, 0)
	v2 = AddVertex(surf, 1.0,-1.0, 0, 1, 1)
	v3 = AddVertex(surf,-1.0,-1.0, 0, 0, 1)
	AddTriangle(surf, v0, v1, v2)
	AddTriangle(surf, v0, v2, v3)
	UpdateNormals mesh
	Return mesh
	
End Function

Function CanUseItem(canUseWithHazmat%, canUseWithGasMask%, canUseWithEyewear%)
	If (canUseWithHazmat = False And WearingHazmat) Then
		Msg = "You can't use that item while wearing a hazmat suit."
		MsgTimer = 70*5
		Return False
	Else If (canUseWithGasMask = False And (WearingGasMask Or Wearing1499))
		Msg = "You can't use that item while wearing a gas mask."
		MsgTimer = 70*5
		Return False
	Else If (canUseWithEyewear = False And (WearingNightVision))
		Msg = "You can't use that item while wearing headgear."
	EndIf
	
	Return True
End Function

Function ResetInput()
	
	MouseXSpeed()
	MouseYSpeed()
	MouseZSpeed()
	mouse_x_speed_1#=0.0
	mouse_y_speed_1#=0.0
	
	FlushKeys()
	FlushMouse()
	FlushJoy()
	MouseHit1 = 0
	MouseHit2 = 0
	MouseDown1 = 0
	MouseUp1 = 0
	MouseDown2 = 0
	MouseHit(1)
	MouseHit(2)
	MouseDown(1)
	MouseDown(2)
	GrabbedEntity = 0
	Input_ResetTime# = 10.0
	
	MouseHit3 = 0
	keyhituse = 0
	keydownuse = 0
	MouseHit(3)
	MouseDown(3)
	
End Function

Function GammaUpdate()
	
	If opt\DisplayMode=1 Then
		If (RealGraphicWidth<>opt\GraphicWidth) Lor (RealGraphicHeight<>opt\GraphicHeight) Then
			SetBuffer TextureBuffer(fresize_texture)
			ClsColor 0,0,0 : Cls
			CopyRect 0,0,opt\GraphicWidth,opt\GraphicHeight,2048-opt\GraphicWidth/2,2048-opt\GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			SetBuffer BackBuffer()
			ClsColor 0,0,0 : Cls
			ScaleRender(0,0,4096.0 / Float(opt\GraphicWidth) * AspectRatioRatio, 4096.0 / Float(opt\GraphicWidth) * AspectRatioRatio)
			;might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth,opt\GraphicHeight) if portrait sizes cause issues
			;everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	;TODO Broken with dgVoodoo implementation
	
	;not by any means a perfect solution
	;Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
;	If ScreenGamma>=1.0 Then
;		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,2048-RealGraphicWidth/2,2048-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
;		EntityBlend fresize_image,1
;		ClsColor 0,0,0 : Cls
;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth))
;		EntityFX fresize_image,1+32
;		EntityBlend fresize_image,3
;		EntityAlpha fresize_image,ScreenGamma-1.0
;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth))
;	ElseIf ScreenGamma<1.0 Then ;todo: maybe optimize this if it's too slow, alternatively give players the option to disable gamma
;		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,2048-RealGraphicWidth/2,2048-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
;		EntityBlend fresize_image,1
;		ClsColor 0,0,0 : Cls
;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth))
;		EntityFX fresize_image,1+32
;		EntityBlend fresize_image,2
;		EntityAlpha fresize_image,1.0
;		SetBuffer TextureBuffer(fresize_texture2)
;		ClsColor 255*ScreenGamma,255*ScreenGamma,255*ScreenGamma
;		Cls
;		SetBuffer BackBuffer()
;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth),4096.0 / Float(RealGraphicWidth))
;		SetBuffer(TextureBuffer(fresize_texture2))
;		ClsColor 0,0,0
;		Cls
;		SetBuffer(BackBuffer())
;	EndIf
;	EntityFX fresize_image,1
;	EntityBlend fresize_image,1
;	EntityAlpha fresize_image,1.0
	
End Function

Function UpdateRichPresence()
	If gv\RichPresenceTimer <= 0.0 Then
		If MainMenuOpen Then
			Steam_SetRichPresence("steam_display", "#Status_InMainMenu")
			BlitzcordSetLargeImage("logo")
			BlitzcordSetSmallImage("")
			BlitzcordSetActivityDetails("In Main Menu")
			BlitzcordSetActivityState("")
		ElseIf gopt\GameMode = GAMEMODE_MULTIPLAYER Then
			Select mp_I\Gamemode\ID
				Case Gamemode_Waves
					Steam_SetRichPresence("map", mp_I\MapInList\Name)
					Steam_SetRichPresence("difficulty", mp_I\Gamemode\Difficulty)
					Steam_SetRichPresence("currWave", mp_I\Gamemode\Phase/2)
					Steam_SetRichPresence("maxWaves", mp_I\Gamemode\MaxPhase)
					Steam_SetRichPresence("steam_display", "#Status_Waves")
					BlitzcordSetLargeImage("waves")
				Case Gamemode_Deathmatch
					Steam_SetRichPresence("map", mp_I\MapInList\Name)
					Steam_SetRichPresence("steam_display", "#Status_TeamDeathmatch")
					BlitzcordSetLargeImage("tdm")
			End Select
			BlitzcordSetSmallImage("logo")
			BlitzcordSetActivityDetails(mp_I\Gamemode\name+" ("+mp_I\PlayerCount+" of "+mp_I\MaxPlayers+" players)")
			BlitzcordSetActivityState(mp_I\MapInList\Name)
		Else
			Steam_SetRichPresence("difficulty", SelectedDifficulty\name)
			Steam_SetRichPresence("seed", RandomSeed)
			Steam_SetRichPresence("steam_display", "#Status_Singleplayer")
			BlitzcordSetLargeImage("singleplayer")
			BlitzcordSetSmallImage("logo")
			BlitzcordSetActivityDetails("Singleplayer")
			BlitzcordSetActivityState("Difficulty: "+SelectedDifficulty\name+" | Seed: "+RandomSeed)
		EndIf
		BlitzcordUpdateActivity()
		BlitzcordRunCallbacks()
		gv\RichPresenceTimer = 5*70
	Else
		gv\RichPresenceTimer = gv\RichPresenceTimer - FPSfactor
	EndIf
End Function	
;~IDEal Editor Parameters:
;~C#Blitz3D