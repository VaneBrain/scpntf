;SCP - Nine Tailed Fox Mod
;
;    This is a modification of the game "SCP - Containment Breach"
;
;	 Version 0.2.0 and onwards are stand-alone versions of the game, which means that all the assets from the original game are
;	 included within this mod, unless these assets have been removed as they are unused.
;
;	 This mod is developed by the "Third Subdivision Studios"
;	 http://www.thirdsubdivision.com
;
;	 The mod uses the same license as SCP: Containment Breach (as this is a modification of the game).
;
;This is the main file of the NTF mod, you need to compile this file in order to be able to play the game.

CheckForDlls()

Include "SourceCode/Key.bb"

Include "SourceCode/SteamConstants.bb"

Type GlobalVariables
	Field OptionFile$
	Field OSBit%
	Field RichPresenceTimer#
End Type

Type Options
	Field GraphicWidth%
	Field GraphicHeight%
	Field LauncherEnabled%
	Field ShowFPS%
	Field TotalGFXModes%
	Field DisplayMode%
	Field EnableRoomLights%
	Field TextureDetails%
	Field TextureFiltering%
	Field ConsoleOpening%
	Field SFXVolume#
	Field MusicVol#
	Field MasterVol#
	Field VoiceVol#
	Field EnableSFXRelease%
	Field EnableSFXRelease_Prev%
	Field ConsoleEnabled%
	Field RenderCubeMapMode%
	Field MouseSmooth#
	Field HoldToAim%
	Field HoldToCrouch%
End Type

Const GAMEMODE_UNKNOWN% = -1
Const GAMEMODE_DEFAULT% = 0
Const GAMEMODE_CLASSIC% = 1
Const GAMEMODE_MULTIPLAYER% = 2

Type GameOptions
	Field SingleplayerGameMode%
	Field GameMode%
End Type

;Include the controls file
Include "SourceCode\Controls.bb"

;Include the global type instance accessors
Include "SourceCode\TypeInstances.bb"

InitGlobalVariables()

;Create the folder in the AppData if it doesn't exist
If (FileType(GetEnv$("AppData")+"\scpntf\")<>2) Then
	CreateDir(GetEnv$("AppData")+"\scpntf")
EndIf
;Create the options.ini file in the AppData folder if it doesn't exist
If (FileType(GetEnv$("AppData")+"\scpntf\options.ini")<>1) Then
	WriteFile(GetEnv$("AppData")+"\scpntf\options.ini")
EndIf

InitOptions()
BlitzcordCreateCore("905859463645376522")
;Include the Main.bb file which is the core of the game
Include "SourceCode\Main.bb"

Function CheckForDlls()
	Local InitErrorStr$ = ""
	
	If FileType("Blitzcord.dll")<>1 Then InitErrorStr=InitErrorStr+ "Blitzcord.dll"+Chr(13)+Chr(10)
	If FileType("BlitzHash.dll")<>1 Then InitErrorStr=InitErrorStr+ "BlitzHash.dll"+Chr(13)+Chr(10)
	If FileType("BlitzMovie.dll")<>1 Then InitErrorStr=InitErrorStr+ "BlitzMovie.dll"+Chr(13)+Chr(10)
	If FileType("BlitzSteamworks.dll")<>1 Then InitErrorStr=InitErrorStr+ "BlitzSteamworks.dll"+Chr(13)+Chr(10)
	If FileType("d3dim700.dll")<>1 Then InitErrorStr=InitErrorStr+ "d3dim700.dll"+Chr(13)+Chr(10)
	If FileType("discord_game_sdk.dll")<>1 Then InitErrorStr=InitErrorStr+ "discord_game_sdk.dll"+Chr(13)+Chr(10)
	If FileType("fmod.dll")<>1 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
	If FileType("steam_api.dll")<>1 Then InitErrorStr=InitErrorStr+ "steam_api.dll"+Chr(13)+Chr(10)
	If FileType("IniControler.dll")<>1 Then InitErrorStr=InitErrorStr+ "IniControler.dll"+Chr(13)+Chr(10)
	
	If Len(InitErrorStr)>0 Then
		RuntimeError "The following DLLs were not found in the game directory:"+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr
	EndIf
	
	Local SteamResultCode% = Steam_Init()
	If SteamResultCode <> 0 Then RuntimeError("Steam API failed to initialize! Error Code: " + SteamResultCode)
	
End Function

Function InitGlobalVariables()
	
	gv\OptionFile$ = GetEnv("AppData")+"\scpntf\options.ini"
	If GetEnv("ProgramFiles(X86)")>0 Then
		gv\OSBit = 64
	Else
		gv\OSBit = 32
	EndIf
	
End Function

Function InitOptions()
	
	opt\EnableSFXRelease% = GetINIInt(gv\OptionFile, "audio", "sfx release", 1)
	opt\EnableSFXRelease_Prev% = opt\EnableSFXRelease%
	opt\ConsoleEnabled% = GetINIInt(gv\OptionFile, "console", "enabled", 0)
	opt\ConsoleOpening% = GetINIInt(gv\OptionFile, "console", "auto opening", 0)
	opt\GraphicWidth% = GetINIInt(gv\OptionFile, "options", "width", DesktopWidth())
	opt\GraphicHeight% = GetINIInt(gv\OptionFile, "options", "height", DesktopHeight())
	opt\ShowFPS = GetINIInt(gv\OptionFile, "options", "show FPS", 0)
	opt\DisplayMode% = GetINIInt(gv\OptionFile, "options", "display mode", 1)
	opt\RenderCubeMapMode% = GetINIInt(gv\OptionFile, "options", "cubemaps", 2)
	opt\EnableRoomLights% = GetINIInt(gv\OptionFile, "options", "room lights enabled", 1)
	opt\TextureDetails% = GetINIInt(gv\OptionFile, "options", "texture details", 2)
	opt\TextureFiltering% = GetINIInt(gv\OptionFile, "options", "texture filtering", 2)
	opt\LauncherEnabled% = GetINIInt(gv\OptionFile, "options", "launcher enabled", 1)
	opt\TotalGFXModes% = CountGfxModes3D()
	opt\SFXVolume# = GetINIFloat(gv\OptionFile, "audio", "sound volume", 1.0)
	opt\VoiceVol# = GetINIFloat(gv\OptionFile, "audio", "voice volume", 1.0)
	opt\MasterVol# = GetINIFloat(gv\OptionFile, "audio", "master volume", 1.0)
	opt\MusicVol# = GetINIFloat(gv\OptionFile, "audio", "music volume", 1.0)
	opt\MouseSmooth# = GetINIFloat(gv\OptionFile, "options", "mouse smoothing", 1.0)
	opt\HoldToAim% = GetINIInt(gv\OptionFile, "options", "hold to aim", 1)
	opt\HoldToCrouch% = GetINIInt(gv\OptionFile, "options", "hold to crouch", 1)
	
	LoadResolutions()
	
	gopt\SingleplayerGameMode = GetINIInt(gv\OptionFile, "game options", "game mode", GAMEMODE_DEFAULT)
	gopt\GameMode = gopt\SingleplayerGameMode
	
End Function





;~IDEal Editor Parameters:
;~C#Blitz3D