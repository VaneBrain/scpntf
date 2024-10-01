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

Const NameOfTheGame$ = "SCP: Nine-Tailed Fox"
Const VersionNumber$ = "0.2.11"
Const CompatibleNumber$ = "0.2.10"
Const BuildMessage$ = ""
Const AppTitleMain$ = NameOfTheGame+" v"+VersionNumber
Const AppTitleLauncher$ = NameOfTheGame+" Launcher"
Const UserAgent$ = "SCPNTF"

Include "SourceCode/Key.bb"

Include "SourceCode/SteamConstants.bb"

Type GlobalVariables
	Field OptionFile$
	Field OSBit%
	Field RichPresenceTimer#
	Field SteamIDUpper%
	Field SteamIDLower%
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
	Field ShowDisclaimers%
	Field GraphicDriver%
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

InitErrorMsgs(11, True)
SetErrorMsg(0, "An error occured in "+NameOfTheGame+" v"+VersionNumber+Chr(10)+"Save compatible version: "+CompatibleNumber+". Engine version: "+SystemProperty("blitzversion"))
SetErrorMsg(1, "OS: "+SystemProperty("os")+" "+gv\OSBit+" bit (Build: "+SystemProperty("osbuild")+")")
SetErrorMsg(3, "CPU: "+Trim(SystemProperty("cpuname"))+" (Arch: "+SystemProperty("cpuarch")+", "+GetEnv("NUMBER_OF_PROCESSORS")+" Threads)")
SetErrorMsg(8, "Error: _CaughtError_")
SetErrorMsg(10, Chr(10)+"Please take a screenshot of this error and send it to us!")

CheckForDlls()

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
	
	If FileSize("IniController.dll")=0 Then InitErrorStr=InitErrorStr+ "IniController.dll"+Chr(13)+Chr(10)
	If FileSize("Blitzcord.dll")=0 Then InitErrorStr=InitErrorStr+ "Blitzcord.dll"+Chr(13)+Chr(10)
	If FileSize("BlitzHash.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzHash.dll"+Chr(13)+Chr(10)
	If FileSize("BlitzMovie.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzMovie.dll"+Chr(13)+Chr(10)
	If FileSize("BlitzSteamworks.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzSteamworks.dll"+Chr(13)+Chr(10)
	;If FileSize("D3DImm.dll")=0 Then InitErrorStr=InitErrorStr+ "D3DImm.dll"+Chr(13)+Chr(10)
	;If FileSize("DDraw.dll")=0 Then InitErrorStr=InitErrorStr+ "DDraw.dll"+Chr(13)+Chr(10)
	If FileSize("discord_game_sdk.dll")=0 Then InitErrorStr=InitErrorStr+ "discord_game_sdk.dll"+Chr(13)+Chr(10)
	If FileSize("fmod.dll")=0 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
	If FileSize("steam_api.dll")=0 Then InitErrorStr=InitErrorStr+ "steam_api.dll"+Chr(13)+Chr(10)
	
	If Len(InitErrorStr)>0 Then
		RuntimeError "The following DLLs were not found in the game directory:"+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr
	EndIf
	
	Local SteamResultCode% = Steam_Init()
	If SteamResultCode <> 0 Then RuntimeError("Steam API failed to initialize! Error Code: " + SteamResultCode)
	
	gv\SteamIDUpper = Steam_GetPlayerIDUpper()
	gv\SteamIDLower = Steam_GetPlayerIDLower()
	
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
	
	opt\EnableSFXRelease% = IniGetInt(gv\OptionFile, "audio", "sfx release", 1)
	opt\EnableSFXRelease_Prev% = opt\EnableSFXRelease%
	opt\ConsoleEnabled% = IniGetInt(gv\OptionFile, "console", "enabled", 0)
	opt\ConsoleOpening% = IniGetInt(gv\OptionFile, "console", "auto opening", 0)
	opt\GraphicWidth% = IniGetInt(gv\OptionFile, "options", "width", DesktopWidth())
	opt\GraphicHeight% = IniGetInt(gv\OptionFile, "options", "height", DesktopHeight())
	opt\ShowFPS = IniGetInt(gv\OptionFile, "options", "show FPS", 0)
	opt\DisplayMode% = IniGetInt(gv\OptionFile, "options", "display mode", 1)
	opt\GraphicDriver% = IniGetInt(gv\OptionFile, "options", "graphic driver", 0)
	opt\RenderCubeMapMode% = IniGetInt(gv\OptionFile, "options", "cubemaps", 2)
	opt\EnableRoomLights% = IniGetInt(gv\OptionFile, "options", "room lights enabled", 1)
	opt\TextureDetails% = IniGetInt(gv\OptionFile, "options", "texture details", 2)
	opt\TextureFiltering% = IniGetInt(gv\OptionFile, "options", "texture filtering", 2)
	opt\LauncherEnabled% = IniGetInt(gv\OptionFile, "options", "launcher enabled", 1)
	opt\TotalGFXModes% = CountGfxModes3D()
	opt\SFXVolume# = IniGetFloat(gv\OptionFile, "audio", "sound volume", 1.0)
	opt\VoiceVol# = IniGetFloat(gv\OptionFile, "audio", "voice volume", 1.0)
	opt\MasterVol# = IniGetFloat(gv\OptionFile, "audio", "master volume", 1.0)
	opt\MusicVol# = IniGetFloat(gv\OptionFile, "audio", "music volume", 1.0)
	opt\MouseSmooth# = IniGetFloat(gv\OptionFile, "options", "mouse smoothing", 1.0)
	opt\HoldToAim% = IniGetInt(gv\OptionFile, "options", "hold to aim", 1)
	opt\HoldToCrouch% = IniGetInt(gv\OptionFile, "options", "hold to crouch", 1)
	opt\ShowDisclaimers% = IniGetInt(gv\OptionFile, "options", "show disclaimers", 1)
	
	LoadResolutions()
	
	gopt\SingleplayerGameMode = IniGetInt(gv\OptionFile, "game options", "game mode", GAMEMODE_DEFAULT)
	gopt\GameMode = gopt\SingleplayerGameMode
	
End Function





;~IDEal Editor Parameters:
;~C#Blitz3D