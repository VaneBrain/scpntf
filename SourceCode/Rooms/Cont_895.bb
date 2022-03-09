
Function FillRoom_Cont_895(r.Rooms)
	Local d.Doors,sc.SecurityCams,it.Items
	Local i
	
	d = CreateDoor(r\zone, r\x, 0, r\z - 448.0 * RoomScale, 0, r, False, True, 2)
	d\AutoClose = False : d\open = False
	PositionEntity(d\buttons[0], r\x - 384.0 * RoomScale, 0.7, r\z - 280.0 * RoomScale, True)
	
	sc.SecurityCams = CreateSecurityCam(r\x - 320.0 * RoomScale, r\y + 704 * RoomScale, r\z + 288.0 * RoomScale, r, True)
	sc\angle = 45 + 180
	sc\turn = 45
	sc\CoffinEffect = True
	TurnEntity(sc\CameraObj, 120, 0, 0)
	EntityParent(sc\obj, r\obj)
	
	CoffinCam = sc
	
	PositionEntity(sc\ScrObj, r\x - 800 * RoomScale, 288.0 * RoomScale, r\z - 340.0 * RoomScale)
	EntityParent(sc\ScrObj, r\obj)
	TurnEntity(sc\ScrObj, 0, 180, 0)
	
	r\Levers[0] = CreateLever(r, r\x - 800.0 * RoomScale, r\y + 180.0 * RoomScale, r\z - 336 * RoomScale,180,True)
	
	r\Objects[0] = CreatePivot()
	PositionEntity(r\Objects[0], r\x, -1320.0 * RoomScale, r\z + 2304.0 * RoomScale)
	EntityParent(r\Objects[0], r\obj)
	
	it = CreateItem("Document SCP-895", "paper", r\x - 688.0 * RoomScale, r\y + 133.0 * RoomScale, r\z - 304.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	it = CreateItem("Level 3 Key Card", "key3", r\x + 240.0 * RoomScale, r\y -1456.0 * RoomScale, r\z + 2064.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	it = CreateItem("Night Vision Goggles", "nvgoggles", r\x + 280.0 * RoomScale, r\y -1456.0 * RoomScale, r\z + 2164.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	r\Objects[1] = CreatePivot(r\obj)
	PositionEntity(r\Objects[1], r\x + 96.0*RoomScale, -1532.0 * RoomScale, r\z + 2016.0 * RoomScale,True)
	
End Function

Function UpdateEvent_Cont_895(e.Events)
	Local sc.SecurityCams,de.Decals,it.Items
	
	If e\EventState < MilliSecs() Then
		;SCP-079 starts broadcasting 895 camera feed on monitors after leaving the first zone
		If PlayerZone > 0 Then 
			If EntityPitch(e\room\Levers[0]\obj,True) > 0 Then ;camera feed on
				For sc.SecurityCams = Each SecurityCams
					If sc\CoffinEffect=0 And sc\room\RoomTemplate\Name<>"cont_106" And sc\room\RoomTemplate\Name<>"cont_205" Then sc\CoffinEffect = 2
					If sc\room = e\room Then sc\Screen = True
				Next
			Else ;camera feed off
				For sc.SecurityCams = Each SecurityCams
					If sc\CoffinEffect<>1 Then sc\CoffinEffect = 0
					If sc\room = e\room Then sc\Screen = False
				Next
			EndIf						
		EndIf
		
		e\EventState = MilliSecs()+3000
	EndIf
	
	If PlayerRoom = e\room Then
		CoffinDistance = EntityDistance(Collider, e\room\Objects[1]) ;TODO CoffinDistanceSquared? (probably not)
		If CoffinDistance < 1.5 Then 
			GiveAchievement(Achv895)
			If (Not Contained106) And e\EventName="cont_895_106_spawn" And e\EventState2 = 0 Then
				de.Decals = CreateDecal(DECAL_DECAY, EntityX(e\room\Objects[1],True), -1531.0*RoomScale, EntityZ(e\room\Objects[1],True), 90, Rand(360), 0)
				de\Size = 0.05 : de\SizeChange = 0.001 : EntityAlpha(de\obj, 0.8) : UpdateDecals()
				
				If Curr106\State > 0 Then
					PositionEntity Curr106\Collider, EntityX(e\room\Objects[1],True), -10240*RoomScale, EntityZ(e\room\Objects[1],True)
					Curr106\State = -0.1
					ShowEntity Curr106\obj
					e\EventState2 = 1
				EndIf
			EndIf
		ElseIf CoffinDistance < 3.0 Then
			If e\room\NPC[0]=Null Then
				e\room\NPC[0]=CreateNPC(NPCtypeGuard,e\room\x,e\room\y,e\room\z)
				RotateEntity e\room\NPC[0]\Collider,0,e\room\angle+90,0
				e\room\NPC[0]\State = 8
				SetNPCFrame(e\room\NPC[0],270)
				e\room\NPC[0]\GravityMult = 0.0
				e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardIdle"+Rand(1,3)+".ogg")
				e\room\NPC[0]\SoundChn = PlaySound2(e\room\NPC[0]\Sound,Camera,e\room\NPC[0]\Collider)
				e\room\NPC[0]\IsDead = True
				e\room\NPC[0]\FallingPickDistance = 0.0
				RemoveNPCGun(e\room\NPC[0])
			EndIf
		ElseIf CoffinDistance > 5.0 Then
			If e\room\NPC[0]<>Null Then
				If e\room\NPC[0]\PrevState = 0 Then
					If ChannelPlaying(e\room\NPC[0]\SoundChn) Then
						StopChannel e\room\NPC[0]\SoundChn
					EndIf
					FreeSound_Strict e\room\NPC[0]\Sound
					e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardScream"+Rand(1,3)+".ogg")
					e\room\NPC[0]\SoundChn = PlaySound2(e\room\NPC[0]\Sound,Camera,e\room\NPC[0]\Collider,100)
					e\room\NPC[0]\PrevState = 1
					e\room\NPC[0]\State2 = 0.0
					
					it = CreateItem("MP5K", "mp5k",e\room\x,e\room\y-1.0,e\room\z+0.5)
					RotateEntity it\collider,EntityPitch(it\collider),45,EntityRoll(it\collider),True
					it\state = Rand(15,30)
					it\state2 = 2
					EntityType it\collider, HIT_ITEM
					it\DropSpeed = -0.025
				EndIf
			EndIf
		EndIf
		
		If e\room\NPC[0]<>Null Then
			UpdateSoundOrigin(e\room\NPC[0]\SoundChn,Camera,e\room\NPC[0]\Collider,100)
			If e\room\NPC[0]\PrevState = 0 Then
				e\room\NPC[0]\GravityMult = 0.0
			ElseIf e\room\NPC[0]\PrevState = 1 Then
				If e\room\NPC[0]\State2 < 70*1 Then
					e\room\NPC[0]\State2 = e\room\NPC[0]\State2 + FPSfactor
					e\room\NPC[0]\GravityMult = 0.0
				Else
					e\room\NPC[0]\GravityMult = 1.0
				EndIf
				If EntityY(e\room\NPC[0]\Collider)>(-1531.0*RoomScale)+0.35 Then
					dist# = EntityDistance(Collider,e\room\NPC[0]\Collider)
					If dist<0.8 Then ;get the player out of the way
						fdir# = point_direction(EntityX(Collider,True),EntityZ(Collider,True),EntityX(e\room\NPC[0]\Collider,True),EntityZ(e\room\NPC[0]\Collider,True))
						TranslateEntity Collider,Cos(-fdir+90)*(dist-0.8)*(dist-0.8),0,Sin(-fdir+90)*(dist-0.8)*(dist-0.8)
					EndIf
					
					If EntityY(e\room\NPC[0]\Collider)>0.6 Then EntityType e\room\NPC[0]\Collider,0
				Else
					e\EventState=e\EventState+FPSfactor
					AnimateNPC(e\room\NPC[0], 270, 286, 0.4, False)
					If e\Sound=0 Then
						LoadEventSound(e,"SFX\General\BodyFall.ogg")
						e\SoundCHN = PlaySound_Strict(e\Sound)
						
						de.Decals = CreateDecal(DECAL_BLOODSPLAT2, EntityX(e\room\obj), -1531.0*RoomScale, EntityZ(e\room\obj), 90, Rand(360), 0)
						de\Size = 0.4 : ScaleSprite(de\obj,de\Size,de\Size) : UpdateDecals()
					EndIf
					If e\room\NPC[0]\Frame = 286.0 Then
						e\room\NPC[0]\PrevState = 2
					EndIf
				EndIf
				If e\room\NPC[0]\SoundChn2 = 0 Then
					e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\Room\895Chamber\GuardRadio.ogg")
					e\room\NPC[0]\SoundChn2 = LoopSound2(e\room\NPC[0]\Sound2,e\room\NPC[0]\SoundChn2,Camera,e\room\NPC[0]\Collider,5)
				EndIf
			ElseIf e\room\NPC[0]\PrevState = 2 Then
				If (Not ChannelPlaying(e\SoundCHN)) And e\Sound<>0 Then
					FreeSound_Strict e\Sound : e\Sound = 0
					e\SoundCHN = 0
				EndIf
				If (Not ChannelPlaying(e\room\NPC[0]\SoundChn)) And e\room\NPC[0]\Sound<>0 Then
					FreeSound_Strict e\room\NPC[0]\Sound : e\room\NPC[0]\Sound = 0
					e\room\NPC[0]\SoundChn = 0
				EndIf
				If e\room\NPC[0]\Sound2 = 0 Then
					e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\Room\895Chamber\GuardRadio.ogg")
				EndIf
				e\room\NPC[0]\SoundChn2 = LoopSound2(e\room\NPC[0]\Sound2,e\room\NPC[0]\SoundChn2,Camera,e\room\NPC[0]\Collider,5)
			EndIf
		EndIf
		
		If e\EventState3>0.0 Then e\EventState3=Max(e\EventState3-FPSfactor,0.0)
		
		ShouldPlay = 66
		
		If UpdateLever(e\room\Levers[0]\obj) Then
			For sc.SecurityCams = Each SecurityCams
				If sc\CoffinEffect=0 And sc\room\RoomTemplate\Name<>"cont_106" Then sc\CoffinEffect = 2
				If sc\CoffinEffect = 1 Then EntityBlend(sc\ScrOverlay, 3)
				If sc\room = e\room Then sc\Screen = True
			Next
		Else
			For sc.SecurityCams = Each SecurityCams
				If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
				If sc\CoffinEffect = 1 Then EntityBlend(sc\ScrOverlay, 0)
				If sc\room = e\room Then sc\Screen = False
			Next
		EndIf
	Else
		CoffinDistance = e\room\dist
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D