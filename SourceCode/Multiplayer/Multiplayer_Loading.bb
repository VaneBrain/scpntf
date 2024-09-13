
Function LoadingServer()
	
	;Load the game for the server
	CreateMPGame()
	
	;Close the connection between any clients that may still be connected to the server
;	For rp = Each ReservedPlayer
;		Steam_CloseConnection(rp\SteamIDUpper, rp\SteamIDLower)
;	Next
	
	InitMPGame()
	RespawnPlayers()
	
	DrawLoading(100, False, mp_I\Gamemode\name)
	
	For i = 1 To (mp_I\MaxPlayers-1)
		If Players[i]<>Null Then
			Players[i]\LastMsgTime = MilliSecs()
		EndIf
	Next
	
	ResetTimingAccumulator()
	
	If mp_O\LocalServer=False Then
		AddServer(VersionNumber, Steam_GetPlayerIDLower(), Steam_GetPlayerIDUpper(), Steam_GetIPCountry(), mp_I\ServerName, (mp_O\Password<>""), mp_O\Gamemode\name, mp_O\MapInList\Name, mp_I\PlayerCount, mp_O\MaxPlayers)
		mp_I\ServerListUpdateTimer = 0.0
	EndIf
	
End Function

Function LoadingClient(ingame%=False)
	
	;Create the game
	CreateMPGame()
	
	Players[mp_I\PlayerID]\WantsSlot = SLOT_SECONDARY
	
	InitMPGame()
	
	DrawLoading(100, False, mp_I\Gamemode\name)
	
	ResetTimingAccumulator()
	
	mp_I\LastPingMillisecs = MilliSecs()
	
End Function

Function CreateMPGame()
	Local i%,getconn%,j%
	Local tex%
	Local mmt.MultiplayerMapTemplate
	Local p.Player
	Local StrTemp$
	
	DrawLoading(0, False, mp_I\Gamemode\name)
	
	For p = Each Player
		p\FinishedLoading = False
	Next
	
	FreeImage mp_I\ServerIcon
	FreeImage mp_I\PasswordIcon
	
	;Will be added in a future version, currently not properly functional - ENDSHN
	;[Block]
;	;Custom spray loading
;	;For i = 0 To (mp_I\MaxPlayers-1)
;	;	If FileType("spray"+i+".cache")=1 Then
;	;		DeleteFile "spray"+i+".cache"
;	;	EndIf
;	;Next
;	Local row% = 0
;	Local currpart% = 0, shouldpart% = 0
;	Local BytesSaved% = False
;	Local file%
;	Local TempRead%
;	Local count% = 0
;	Local allFinished% = 0
;	Local byte%, bytepos%
;	If mp_I\EnableSprays And mp_I\PlayerCount>1 Then
;		Repeat
;			If Players[row]<>Null Then
;				If mp_I\PlayerID=row Then
;					If FileType(mp_I\SprayIMGPath)=1 Then
;						If (Not BytesSaved) Then
;							If Players[row]\DataBank=0 Then
;								Players[row]\DataBank = CreateBank(UDP_MAX_BYTE_AMOUNT)
;							EndIf
;							file = ReadFile(mp_I\SprayIMGPath)
;							count = 0
;							SeekFile file,bytepos
;							For i = 0 To UDP_MAX_BYTE_AMOUNT-1
;								byte = ReadByte(file)
;								PokeByte Players[row]\DataBank,i,byte
;								count = count + 1
;								If Eof(file) Then
;									Exit
;								EndIf
;							Next
;							bytepos = bytepos + count
;							BytesSaved = True
;							CloseFile file
;						EndIf
;						getconn = RecvUDPMsg(mp_I\Server)
;						While getconn
;							For i = 0 To (mp_I\MaxPlayers-1)
;								If Players[i]<>Null And i<>mp_I\PlayerID Then
;									If Players[i]\IP = getconn Then
;										If ReadLine(mp_I\Server)="wantspray" Then
;											TempRead = ReadInt(mp_I\Server)
;											Players[i]\CurrDataPackage = TempRead
;											;DebugLog "Receive response from ID "+i+"|||"+TempRead
;										EndIf
;									EndIf
;								EndIf
;							Next
;							getconn = RecvUDPMsg(mp_I\Server)
;						Wend
;						allFinished = 0
;						For i = 0 To (mp_I\MaxPlayers-1)
;							If Players[i]<>Null And i<>mp_I\PlayerID Then
;								If Players[i]\CurrDataPackage = currpart Then
;									WriteInt mp_I\Server,currpart
;									WriteInt mp_I\Server,count
;									WriteBytes(Players[row]\DataBank,mp_I\Server,0,count)
;									SendUDPMsg(mp_I\Server,Players[i]\IP,Players[i]\Port)
;									;DebugLog "Send package "+currpart
;								Else
;									allFinished = allFinished + 1
;								EndIf
;							EndIf
;						Next
;						For i = 0 To (mp_I\MaxPlayers-1)
;							If Players[i]<>Null And i<>mp_I\PlayerID Then
;								DebugLog i+"|"+Players[i]\CurrDataPackage
;							EndIf
;						Next
;						If allFinished=(mp_I\PlayerCount-1) Then
;							currpart = currpart + 1
;							BytesSaved = False
;							If count < UDP_MAX_BYTE_AMOUNT-1 Then
;								FreeBank Players[row]\DataBank
;								row = row + 1
;								currpart = 0
;								bytepos = 0
;								For i = 0 To (mp_I\MaxPlayers-1)
;									If Players[i]<>Null Then
;										Players[i]\CurrDataPackage = 0
;									EndIf
;								Next
;							EndIf
;						EndIf
;					Else
;						For i = 0 To (mp_I\MaxPlayers-1)
;							If Players[i]<>Null And i<>mp_I\PlayerID Then
;								WriteInt mp_I\Server,-1
;								SendUDPMsg(mp_I\Server,Players[i]\IP,Players[i]\Port)
;							EndIf
;						Next
;						row = row + 1
;					EndIf
;				Else
;					shouldpart = -1
;					getconn = RecvUDPMsg(mp_I\Server)
;					While getconn
;						If Players[row]\IP = getconn Then
;							If FileType("spray"+row+"_client"+mp_I\PlayerID+".cache")<>1 Then
;								file = WriteFile("spray"+row+"_client"+mp_I\PlayerID+".cache")
;							Else
;								file = OpenFile("spray"+row+"_client"+mp_I\PlayerID+".cache")
;							EndIf
;							If Players[row]\DataBank=0 Then
;								Players[row]\DataBank = CreateBank(UDP_MAX_BYTE_AMOUNT)
;							EndIf
;							DebugLog "Receive data from client ID "+row
;							shouldpart = ReadInt(mp_I\Server)
;							If shouldpart => 0 Then
;								SeekFile file,bytepos
;								count = ReadInt(mp_I\Server)
;								ReadBytes(Players[row]\DataBank,mp_I\Server,0,count)
;								If shouldpart=currpart Then
;									WriteBytes(Players[row]\DataBank,file,0,count)
;								EndIf
;								If shouldpart=currpart Then
;									currpart = currpart + 1
;									bytepos = bytepos + count
;								EndIf
;								CloseFile file
;							Else
;								FreeBank Players[row]\DataBank
;								CloseFile file
;								DeleteFile("spray"+row+"_client"+mp_I\PlayerID+".cache")
;								row = row + 1
;							EndIf
;						EndIf
;						getconn = RecvUDPMsg(mp_I\Server)
;					Wend
;					WriteLine mp_I\Server,"wantspray"
;					WriteInt mp_I\Server,currpart
;					SendUDPMsg mp_I\Server,Players[row]\IP,Players[row]\Port
;					If count < UDP_MAX_BYTE_AMOUNT-1 And shouldpart=>0 Then
;						FreeBank Players[row]\DataBank
;						row = row + 1
;						currpart = 0
;						bytepos = 0
;					EndIf
;				EndIf
;			Else
;				row = row + 1
;			EndIf
;			
;			If row > (mp_I\MaxPlayers-1) Then
;				Exit
;			EndIf
;			
;			Text(opt\GraphicWidth / 2, opt\GraphicHeight - 50, "DOWNLOADING CUSTOM SPRAYS", True, True)
;			Flip 0
;		;JEFF
;		Forever
;	EndIf
;	
;	DrawLoading(5)
;	Text(opt\GraphicWidth / 2, opt\GraphicHeight - 50, "LOADING CUSTOM SPRAYS", True, True)
;	Flip 1
;	
;	If mp_I\EnableSprays Then
;		Local scale# = 0.5
;		Local tex%
;		For i = 0 To (mp_I\MaxPlayers-1)
;			If Players[i]<>Null And i<>mp_I\PlayerID Then
;				If FileSize("spray"+i+"_client"+mp_I\PlayerID+".cache")>0 Then
;					tex = LoadTexture("spray"+i+"_client"+mp_I\PlayerID+".cache",1+8+16+32+256)
;					Players[i]\SpraySprite = CreateSprite()
;					EntityTexture Players[i]\SpraySprite,tex
;					DeleteSingleTextureEntryFromCache tex
;					ScaleSprite Players[i]\SpraySprite,scale,scale
;					SpriteViewMode Players[i]\SpraySprite,2
;					PositionEntity Players[i]\SpraySprite,0,-5000,0
;					EntityFX Players[i]\SpraySprite,1
;					DeleteFile "spray"+i+"_client"+mp_I\PlayerID+".cache"
;				EndIf
;			EndIf
;		Next
;		If FileSize(mp_I\SprayIMGPath)>0 Then
;			tex = LoadTexture(mp_I\SprayIMGPath,1+8+16+32+256)
;			Players[mp_I\PlayerID]\SpraySprite = CreateSprite()
;			EntityTexture Players[mp_I\PlayerID]\SpraySprite,tex
;			DeleteSingleTextureEntryFromCache tex
;			ScaleSprite Players[mp_I\PlayerID]\SpraySprite,scale,scale
;			SpriteViewMode Players[mp_I\PlayerID]\SpraySprite,2
;			PositionEntity Players[mp_I\PlayerID]\SpraySprite,0,-5000,0
;			EntityFX Players[mp_I\PlayerID]\SpraySprite,1
;		EndIf
;	EndIf
	;[End Block]
	
	LoadPlayerList()
	
	MainMenuTab = MenuTab_Default
	
	DrawLoading(6, False, mp_I\Gamemode\name)
	
	gopt\GameMode = GAMEMODE_MULTIPLAYER
	
	HideDistance# = 15.0
	
	CreateMainPlayer()
	
	SprintIcon% = LoadImage_Strict("GFX\sprinticon.png")
	CrouchIcon% = LoadImage_Strict("GFX\sneakicon.png")
	HandIcon% = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2% = LoadImage_Strict("GFX\handsymbol2.png")
	StaminaMeterIMG% = LoadImage_Strict("GFX\staminameter.jpg")
	
	;PauseMenuIMG% = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	;MaskImage PauseMenuIMG, 255,255,0
	;ScaleImage PauseMenuIMG,MenuScale,MenuScale
	
	mp_I\Map = New MultiplayerMap
	
	LoadMissingTexture()
	
	Brightness% = GetINIFloat(gv\OptionFile, "options", "brightness", 20)
	CameraFogNear# = GetINIFloat(gv\OptionFile, "options", "camera fog near", 0.5)
	CameraFogFar# = GetINIFloat(gv\OptionFile, "options", "camera fog far", 6.0)
	StoredCameraFogFar# = CameraFogFar
	
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
	If mp_O\Gamemode\ID = Gamemode_Deathmatch Then
		CameraFogRange(Camera,CameraFogNear,CameraFogFar*5)
	ElseIf mp_O\Gamemode\ID = Gamemode_Waves Then
		CameraFogRange(Camera,CameraFogNear,CameraFogFar*3)
	Else
		CameraFogRange(Camera,CameraFogNear,CameraFogFar)
	EndIf
	CameraRange(Camera,0.01,GetCameraFogRangeFar(Camera)*2.0)
	CameraFogMode (Camera, 1)
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
	
	;Currently is like that, will change in the future
	EntityParent Camera,mpl\CameraPivot
	
	DrawLoading(7, False, mp_I\Gamemode\name)
	
	CreateBlurImage()
	CameraProjMode ark_blur_cam,0
	
	FogTexture = LoadTexture_Strict("GFX\fog.jpg",1,2)
	
	Fog = CreateSprite(ark_blur_cam)
	ScaleSprite(Fog, Max(opt\GraphicWidth / 1240.0, 1.0), Max(opt\GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(Fog, FogTexture)
	EntityBlend (Fog, 2)
	EntityOrder Fog, -1000
	MoveEntity(Fog, 0, 0, 1.0)
	
	GasMaskOverlay2 = LoadSprite("GFX\GasmaskOverlay.jpg",1,ark_blur_cam)
	ScaleSprite GasMaskOverlay2,1.0,Float(opt\GraphicHeight)/Float(opt\GraphicWidth)
	EntityBlend (GasMaskOverlay2, 2)
	EntityFX(GasMaskOverlay2, 1)
	EntityOrder GasMaskOverlay2, -2000
	MoveEntity(GasMaskOverlay2, 0, 0, 1.0)
	ShowEntity(GasMaskOverlay2)
	
	LightTexture = CreateTextureUsingCacheSystem(1024, 1024, 1 + 2)
	SetBuffer TextureBuffer(LightTexture)
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	Light = CreateSprite(ark_blur_cam)
	ScaleSprite(Light, 1.0, Float(opt\GraphicHeight)/Float(opt\GraphicWidth))
	EntityTexture(Light, LightTexture)
	EntityBlend (Light, 1)
	EntityOrder Light, -1002
	MoveEntity(Light, 0, 0, 1.0)
	HideEntity Light
	
	DrawLoading(8, False, mp_I\Gamemode\name)
	
	mp_I\PlayerModel_Lower[Team_MTF-1] = LoadAnimMesh_Strict("GFX\npcs\MTF_Player_Lower.b3d")
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 486, 574
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 1, 151
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 152, 302
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 303, 363
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 364, 424
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 425, 485
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 575, 625
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 626, 686
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 687, 717
	ExtractAnimSeq mp_I\PlayerModel_Lower[Team_MTF-1], 718, 748
	
	mp_I\PlayerModel_Lower[Team_CI-1] = CopyEntity(mp_I\PlayerModel_Lower[Team_MTF-1])
	tex = LoadTexture_Strict("GFX\npcs\CI_newdiffuse02.jpg")
	TextureBlend(tex,5)
	EntityTexture mp_I\PlayerModel_Lower[Team_CI-1],tex
	DeleteSingleTextureEntryFromCache tex
	mp_I\PlayerModel_Upper[Team_MTF-1] = LoadAnimMesh_Strict("GFX\npcs\MTF_Player_Upper.b3d")
	mp_I\PlayerModel_Upper[Team_CI-1] = LoadAnimMesh_Strict("GFX\npcs\CI_Player_Upper.b3d")
	For i = 0 To (MaxPlayerTeams-1)
		;[Block]
		;Pistol
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1, 89
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 388, 538
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 90, 240
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 292, 298
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 241, 291
		;Rifle
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 299, 387
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 774, 924
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 590, 773
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 925, 931
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 539, 589
		;Shotgun
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 932, 1020
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1070, 1220
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1221, 1241
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1342, 1350
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1291, 1341
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1021, 1069
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1242, 1290
		;SMG
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1351, 1439
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1440, 1590
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1591, 1811
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1812, 1816
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1817, 1867
		;Melee
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1868, 1956
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 1957, 2107
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2108, 2148
		;MP5K
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2149, 2237
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2238, 2388
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2389, 2529
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2530, 2535
		ExtractAnimSeq mp_I\PlayerModel_Upper[i], 2536, 2586
		;[End Block]
		HideEntity mp_I\PlayerModel_Lower[i]
		HideEntity mp_I\PlayerModel_Upper[i]
	Next
	
	If mp_I\Gamemode\ID = Gamemode_Waves Then
		;Zombie models
		mp_I\ZombieModel[0] = LoadAnimMesh_Strict("GFX\npcs\zombies\zombie1.b3d")
		mp_I\ZombieModel[1] = LoadAnimMesh_Strict("GFX\npcs\zombies\zombie2.b3d")
		mp_I\ZombieModel[2] = LoadAnimMesh_Strict("GFX\npcs\zombies\Zombieclassd.b3d")
		mp_I\ZombieModel[3] = LoadAnimMesh_Strict("GFX\npcs\zombies\zombieclerk.b3d")
		mp_I\ZombieModel[4] = LoadAnimMesh_Strict("GFX\npcs\zombies\zombiesurgeon.b3d")
		mp_I\ZombieModel[5] = CopyEntity(mp_I\ZombieModel[2])
		tex = LoadTexture_Strict("GFX\npcs\zombies\dclass_zombie_2.jpg")
		TextureBlend(tex,5)
		EntityTexture mp_I\ZombieModel[5],tex
		DeleteSingleTextureEntryFromCache tex
		mp_I\ZombieModel[6] = CopyEntity(mp_I\ZombieModel[2])
		tex = LoadTexture_Strict("GFX\npcs\zombies\scientist_zombie_1.jpg")
		TextureBlend(tex,5)
		EntityTexture mp_I\ZombieModel[6],tex
		DeleteSingleTextureEntryFromCache tex
		mp_I\ZombieModel[7] = CopyEntity(mp_I\ZombieModel[2])
		tex = LoadTexture_Strict("GFX\npcs\zombies\janitor_zombie.jpg")
		TextureBlend(tex,5)
		EntityTexture mp_I\ZombieModel[7],tex
		DeleteSingleTextureEntryFromCache tex
		mp_I\ZombieModel[8] = LoadAnimMesh_Strict("GFX\npcs\zombies\zombieworker.b3d")
		For i = 0 To MaxZombieTypes-1
			HideEntity mp_I\ZombieModel[i]
		Next
		;Guard zombie models
		mp_I\GuardZombieModel[0] = LoadAnimMesh_Strict("GFX\npcs\zombies\guardzombie.b3d")
		mp_I\GuardZombieModel[1] = LoadAnimMesh_Strict("GFX\npcs\zombies\MTFzombie.b3d")
		For i = 0 To MaxGuardZombieTypes-1
			HideEntity mp_I\GuardZombieModel[i]
		Next
		;SCP-939 model
		mp_I\SCP939Model = LoadAnimMesh_Strict("GFX\npcs\scp-939_mp.b3d")
		HideEntity mp_I\SCP939Model
		;SCP-1048-a model
		mp_I\SCP1048aModel = LoadAnimMesh_Strict("GFX\npcs\scp-1048a_mp.b3d")
		HideEntity mp_I\SCP1048aModel
		;Tentacle model
		mp_I\TentacleModel = LoadAnimMesh_Strict("GFX\npcs\035tentacle.b3d")
		HideEntity mp_I\TentacleModel
		;Boss model, depending on the map
		Select mp_I\MapInList\BossNPC
			Case "SCP-035"
				mp_I\BossModel = LoadAnimMesh_Strict("GFX\npcs\035.b3d")
			Case "SCP-457"
				mp_I\BossModel = LoadAnimMesh_Strict("GFX\npcs\457.b3d")
			Default
				RuntimeError "Error: Boss NPC " + mp_I\MapInList\BossNPC + " is not a boss or doesn't exist."
		End Select
		HideEntity mp_I\BossModel
	EndIf
	
	DrawLoading(9, False, mp_I\Gamemode\name)
	
	LightSpriteTex[0] = LoadTexture_Strict("GFX\light1.jpg",1,2)
	LightSpriteTex[1] = LoadTexture_Strict("GFX\light2.jpg",1,2)
	LightSpriteTex[2] = LoadTexture_Strict("GFX\lightsprite.jpg",1,2)
	
	LastItemID% = 0
	InitMPItemTemplates()
	
	DrawLoading(10, False, mp_I\Gamemode\name)
	
	For i = 0 To 7
		NTF_PainSFX[i]=LoadSound_Strict("SFX\Player\pain"+(i+1)+".ogg")
	Next
	For i = 0 To 1
		NTF_PainWeakSFX[i]=LoadSound_Strict("SFX\Player\painweak"+(i+1)+".ogg")
	Next
	
	LoadDecals(True)
	
	ParticleTextures[0] = LoadTexture_Strict("GFX\smoke.png",1+2,2)
	ParticleTextures[1] = LoadTexture_Strict("GFX\flash.jpg",1+2,2)
	ParticleTextures[2] = LoadTexture_Strict("GFX\dust.jpg",1+2,2)
	;ParticleTextures[3] = LoadTexture_Strict("GFX\npcs\hg.pt",1+2,2)
	ParticleTextures[4] = LoadTexture_Strict("GFX\map\sun.jpg",1+2,2)
	ParticleTextures[5] = LoadTexture_Strict("GFX\bloodsprite.png",1+2,2)
	ParticleTextures[6] = LoadTexture_Strict("GFX\smoke2.png",1+2,2)
	ParticleTextures[7] = LoadTexture_Strict("GFX\spark.jpg",1+2,2)
	ParticleTextures[8] = LoadTexture_Strict("GFX\particle.png",1+2,2)
	;ParticleTextures[9] = LoadAnimTexture("GFX\fog_textures.png",1+2,256,256,0,4) ;Maybe this can be useful at some point?
	ParticleTextures[12] = LoadTexture_Strict("GFX\WaterParticle3.png",1+2,2)
	ParticleTextures[13] = LoadTexture_Strict("GFX\fire_particle.png",1+2,2)
	;TODO: Change that in the future!
	WaterParticleTexture[0] = LoadTexture_Strict("GFX\WaterParticle.png",1+2,2)
	;WaterParticleTexture[1] = LoadTexture_Strict("GFX\WaterParticle2.png",1+2,2)
	
	DrawLoading(11, False, mp_I\Gamemode\name)
	
	i = 1
	Repeat
		StrTemp = GetINIString("Data\rooms.ini", "room ambience", "ambience"+i)
		If StrTemp = "" Then Exit
		
		RoomAmbience[i]=LoadSound_Strict(StrTemp)
		i=i+1
	Forever
	
	DrawLoading(12, False, mp_I\Gamemode\name)
	
	InitGuns()
	
	mp_I\SpectatePlayer = -1
	mp_I\DeathChunk = -1
	
	mp_I\MuzzleFlash = CreateSprite()
	EntityTexture mp_I\MuzzleFlash,ParticleTextures[1]
	HideEntity mp_I\MuzzleFlash
	
	DrawLoading(15, False, mp_I\Gamemode\name)
	
	LoadAllSounds()
	
	mmt = New MultiplayerMapTemplate
	mmt\obj = LoadRMesh(mp_I\MapInList\MeshPath+".rmesh",Null,False)
	mp_I\Map\obj = CopyEntity(mmt\obj)
	ScaleEntity mp_I\Map\obj,RoomScale,RoomScale,RoomScale
	EntityType mp_I\Map\obj,HIT_MAP
	EntityPickMode mp_I\Map\obj,2
	EntityPickMode GetChild(mp_I\Map\obj,RMESH_INVISBLE),2
	If mp_I\MapInList\ChunkEnd > 0 Then
		For i = mp_I\MapInList\ChunkStart To mp_I\MapInList\ChunkEnd
			mmt = New MultiplayerMapTemplate
			mmt\obj = LoadRMesh(mp_I\MapInList\MeshPath+"_chunk"+i+".rmesh",Null,False)
			mp_I\Map\Chunks[i-mp_I\MapInList\ChunkStart] = CopyEntity(mmt\obj)
			ScaleEntity mp_I\Map\Chunks[i-mp_I\MapInList\ChunkStart],RoomScale,RoomScale,RoomScale
			EntityType mp_I\Map\Chunks[i-mp_I\MapInList\ChunkStart],HIT_MAP
			EntityPickMode mp_I\Map\Chunks[i-mp_I\MapInList\ChunkStart],2
			EntityPickMode GetChild(mp_I\Map\Chunks[i-mp_I\MapInList\ChunkStart],RMESH_INVISBLE),2
		Next
	EndIf
	For i = 0 To mp_I\MapInList\TriggerAmount-1
		If i = 0 Then
			mp_I\Map\Triggers[i] = LoadMesh_Strict(mp_I\MapInList\TriggerMeshPath)
		Else
			mp_I\Map\Triggers[i] = CopyEntity(mp_I\Map\Triggers[0])
		EndIf
		ScaleEntity mp_I\Map\Triggers[i],RoomScale,RoomScale,RoomScale
		PositionEntity mp_I\Map\Triggers[i],mp_I\MapInList\TriggerCoords[i]\x*RoomScale,mp_I\MapInList\TriggerCoords[i]\y*RoomScale,mp_I\MapInList\TriggerCoords[i]\z*RoomScale
		RotateEntity mp_I\Map\Triggers[i],0,mp_I\MapInList\TriggerYaw[i],0
		EntityAlpha mp_I\Map\Triggers[i],0.0
		mp_I\Map\TriggerPoint1[i] = CreatePivot(mp_I\Map\Triggers[i])
		PositionEntity mp_I\Map\TriggerPoint1[i],0,0,-100
		mp_I\Map\TriggerPoint2[i] = CreatePivot(mp_I\Map\Triggers[i])
		PositionEntity mp_I\Map\TriggerPoint2[i],0,0,100
	Next
	
	Local tfll.TempFluLight,fll.FluLight
	For tfll.TempFluLight = Each TempFluLight
		fll = CreateFluLight(tfll\id)
		PositionEntity fll\obj,tfll\position\x,tfll\position\y,tfll\position\z
		RotateEntity fll\obj,tfll\rotation\x,tfll\rotation\y,tfll\rotation\z
		EntityPickMode fll\obj,2
		;EntityParent fll\obj,r\obj
		PositionEntity fll\lightobj,tfll\position\x,tfll\position\y,tfll\position\z
		EntityParent fll\lightobj,fll\obj
		PositionEntity fll\flashsprite,tfll\position\x,tfll\position\y-0.07,tfll\position\z
		EntityParent fll\flashsprite,fll\obj
	Next
	InitFluLight(0,FLU_STATE_OFF,Null)
	InitFluLight(1,FLU_STATE_ON,Null)
	InitFluLight(2,FLU_STATE_FLICKER,Null)
	
	DrawLoading(30, False, mp_I\Gamemode\name)
	If mp_I\Gamemode\ID = Gamemode_Waves Then
		InitMPWayPoints(30)
	EndIf	
	DrawLoading(70, False, mp_I\Gamemode\name)
	
	CreateDamageOverlay()
	CreateCommunicationAndSocialWheel()
	
	Players[mp_I\PlayerID]\Team = Team_MTF
	Players[mp_I\PlayerID]\WantsTeam = Team_MTF
	
	For i = 0 To (mp_I\MaxPlayers-1)
		If Players[i]<>Null Then
			CreatePlayer(i)
			If (Not IsSpectator(i)) Then ;CHECK FOR IMPLEMENTATION
				SwitchPlayerGun(i)
			EndIf
			Players[i]\CurrHP = 100
		EndIf
	Next
	
	If mp_I\Gamemode\ID = Gamemode_Deathmatch Then
		Players[mp_I\PlayerID]\Team = Team_Unknown
		Players[mp_I\PlayerID]\WantsTeam = Team_Unknown
	EndIf
	
	;CHECK FOR IMPLEMENTATION
	EntityAlpha Players[mp_I\PlayerID]\obj_lower,0.0
	EntityAlpha Players[mp_I\PlayerID]\obj_upper,0.0
	
	;Hitboxes should be client sided too, so proper hit detection will be applied for it
	If mp_I\Gamemode\ID = Gamemode_Deathmatch Then
		ApplyPlayerHitBoxes()
	ElseIf mp_I\Gamemode\ID = Gamemode_Waves Then
		ApplyHitBoxes(NPCtypeZombieMP,"Zombie2")
		ApplyHitBoxes(NPCtypeGuardZombieMP,"Zombie")
		ApplyHitBoxes(NPCtype1048a,"1048A")
		ApplyHitBoxes(NPCtype939,"939")
		ApplyHitBoxes(NPCtype035,"Class-D")
		ApplyHitBoxes(NPCtypeTentacle,"Tentacle")
	EndIf
	
	LoadMultiplayerAchievements()
	
	mp_I\NoMapImage = LoadImage_Strict("GFX\menu\map_preview_unknown.png")
	mp_I\NoMapImage = ResizeImage2(mp_I\NoMapImage, 384 * MenuScale, 192 * MenuScale)
	
End Function

Function InitMPGame()
	Local n2.NPCs
	Local i%
	
	Players[mp_I\PlayerID]\GunPivot = FreeEntity_Strict(Players[mp_I\PlayerID]\GunPivot)
	Players[mp_I\PlayerID]\GunPivot = g_I\GunPivot
	
	For n2.NPCs = Each NPCs
		HideNPCHitBoxes(n2)
	Next
	
	Local mmt.MultiplayerMapTemplate
	For mmt = Each MultiplayerMapTemplate
		mmt\obj = FreeEntity_Strict(mmt\obj)
		Delete mmt
	Next
	
	DrawLoading(79)
	
	MoveMouse viewport_center_x,viewport_center_y
	
	SetFont fo\Font[Font_Default]
	
	HidePointer()
	
	FlushKeys()
	
	DeleteTextureEntriesFromCache(0)
	
	DrawLoading(90)
	
	NoClipSpeed = 2.0
	
	InitConsole(3)
	
	FlushMouse()
	
	Players[mp_I\PlayerID]\DropSpeed = 0
	
	PrevTime = MilliSecs()
	
	mp_I\ReadyTimer = 70*3
	mp_I\IsReady = False
	
	For i = 0 To MaxPlayerTeams-1
		mp_I\Gamemode\RoundWins[i] = 0
	Next
	
End Function

Function NullMPGame(nomenuload%=False,playbuttonsfx%=True)
	CatchErrors("NullMPGame")
	Local i%, x%, y%, lvl
	Local Host_IDUpper% = Players[0]\SteamIDUpper
	Local Host_IDLower% = Players[0]\SteamIDLower
	
	;mp_I\ServerMSG = SERVER_MSG_NONE
	If mp_O\LocalServer=False And mp_I\PlayState = GAME_SERVER Then
		RemoveServer(Steam_GetPlayerIDLower(), Steam_GetPlayerIDUpper())
	EndIf
	
	If (Not nomenuload) Lor mp_I\PlayState = GAME_CLIENT Then
		Disconnect()
	EndIf
	
	DeleteINIFile("Data\weapons.ini")
	
	If (Not nomenuload) Lor mp_I\PlayState = GAME_CLIENT Then
		Local SelGamemode$ = mp_O\Gamemode\name
		Local mgm.MultiplayerGameMode
		For mgm = Each MultiplayerGameMode
			For i = 0 To MaxGamemodeImages-1
				If mgm\img[i] <> 0 Then
					FreeImage mgm\img[i]
				EndIf
			Next
		Next
		Delete Each MultiplayerGameMode
		
		CreateMPGameModes()
		If SelGamemode$<>"" Then
			For mgm = Each MultiplayerGameMode
				If mgm\name = SelGamemode Then
					mp_O\Gamemode = mgm
					Exit
				EndIf
			Next
		EndIf
		
		If mp_O\Gamemode = Null Then
			mp_O\Gamemode = First MultiplayerGameMode
		EndIf
		
		If mp_O\MapInList\Name <> mp_I\MapInList\Name Then
			mp_I\MapInList = mp_O\MapInList
		EndIf
		
		LoadMultiplayerMenuResources()
	EndIf
	
	FreeImage mp_I\NoMapImage
	If nomenuload And mp_I\PlayState = GAME_SERVER Then
		Local mfl.MapForList
		For mfl = Each MapForList
			If mfl\Name = mp_I\MapsToVote[mp_I\VotedMap - 1] Then
				mp_I\MapInList = mfl
				Exit
			EndIf
		Next
	EndIf
	mp_I\VotedMap = 0
	For i = 0 To (MAX_VOTED_MAPS - 1)
		mp_I\MapsToVote[i] = ""
	Next
	
	UnLoadPlayerList()
	
	KillSounds()
	If playbuttonsfx Then PlaySound_Strict ButtonSFX
	
	DeleteNewElevators()
	
	;ClearTextureCache
	DeleteTextureEntriesFromCache(2)
	
	UnableToMove% = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer# = 0
	QuickLoad_CurrEvent = Null
	
	HideDistance# = 15.0
	
	DropSpeed = 0
	Shake = 0
	CurrSpeed = 0
	
	DeathTimer=0
	
	HeartBeatVolume = 0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	
	WireframeState = 0
	WireFrame 0
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
;	For i = 0 To MAXACHIEVEMENTS-1
;		Achievements[i]\Unlocked=0
;	Next
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	ShouldPlay = 66
	
	Stamina = 100
	BlurTimer = 0
	
	Msg = ""
	MsgTimer = 0
	
	For i = 0 To MaxItemAmount - 1
		Inventory[i] = Null
	Next
	SelectedItem = Null
	
	;Some of that stuff should be added in the initializing stage of the multiplayer!
	Delete Each Doors
	Delete Each LightTemplates
	Delete Each Materials
	Delete Each WayPoints
	Delete Each TempWayPoints
	Delete Each ItemTemplates
	Delete Each Items
	Delete Each Props
	Delete Each Decals
	Delete Each NPCs
	Delete Each NPCGun
	Delete Each NPCAnim
	Delete Each Emitters
	Delete Each Particles
	Delete Each ConsoleMsg
	Delete Each ItemSpawner
	Delete Each EnemySpawner
	Delete Each PlayerSpawner
	Delete Each MultiplayerMap
	Delete Each ChatMSG
	Delete Each TempFluLight
	Delete Each FluLight
	Delete Each MenuLogo
	Delete Each FuseBox
	Delete Each Generator
	Delete Each SoundEmittor
	Delete Each ButtonGen
	Delete Each LeverGen
	Delete Each ParticleGen
	Delete Each DamageBossRadius
	
	If (Not nomenuload) Lor mp_I\PlayState = GAME_CLIENT Then
		Delete Each Player
	Else
		;Delete all players, except the host
		For i = 1 To (mp_I\MaxPlayers-1)
			Delete Players[i]
		Next
	EndIf
	
	OptionsMenu% = -1
	QuitMSG% = -1
	AchievementsMenu% = -1
	
	MusicVolume# = PrevMusicVolume ;Does this need to be removed or fixed?
	opt\SFXVolume# = PrevSFXVolume
	DeafPlayer% = False
	DeafTimer# = 0.0
	
	DeleteModStuff()
	
	gopt\GameMode = gopt\SingleplayerGameMode
	
;	Delete Each AchievementMsg
;	CurrAchvMSGID = 0
	
	LastItemID% = 0
	
	Delete Each DoorInstance
	Delete Each GunInstance
	DeleteVectors2D()
	DeleteVectors3D()
	
	ClearWorld
	ResetTimingAccumulator()
	Camera = 0
	ark_blur_cam = 0
	m_I\Cam = 0
	InitFastResize()
	
	If (Not nomenuload) Lor mp_I\PlayState = GAME_CLIENT Then
		Delete Each Menu3DInstance
		
		MainMenuOpen = True
		InitConsole(2)
		Load3DMenu()
	EndIf
	
	If nomenuload And mp_I\PlayState = GAME_CLIENT Then
		MainMenuTab = MenuTab_Serverlist
		Connect(Host_IDUpper, Host_IDLower)
		SaveMPOptions()
		If mp_I\PlayState = GAME_CLIENT Then
			Return
		EndIf
	EndIf
	
	DeleteMenuGadgets()
	
	CatchErrors("Uncaught (NullMPGame)")
End Function

Function LeaveMPGame(playbuttonsfx%=False)
	
	NullMPGame(False,playbuttonsfx)
	MenuOpen = False
	MainMenuOpen = True
	MainMenuTab = MenuTab_Serverlist
	mp_I\ServerListPage = 0
	mp_I\SelectedListServer = 0
	mp_I\ServerListSort = 0
	mp_I\PlayState = 0
	ResetInput()
	
End Function

;~IDEal Editor Parameters:
;~F#1F#30#41
;~C#Blitz3D