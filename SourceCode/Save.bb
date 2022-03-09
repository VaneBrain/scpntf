
Function SaveGame(file$, newzone%=-1)
	CatchErrors("SaveGame(" + file + ", " + newzone + ")")
	
	If Not Playable Then Return ;don't save if the player can't move at all
	
	If DropSpeed#>0.02*FPSfactor Or DropSpeed#<-0.02*FPSfactor Then Return
	
	If KillTimer < 0 Then Return
	
	GameSaved = True
	
	Local i%, j%
	Local it.Items, itt.ItemTemplates, g.Guns, t.NewTask
	
	If FileType(file) <> 2 Then
		CreateDir(file)
	EndIf
	
	Local savename$ = Replace(Replace(file, SavePath, ""), "\", "")
	PutINIValue(gv\OptionFile, "options", "last save", savename)
	m_I\CurrentSave = savename
	
	;Save global data in here
	Local f% = WriteFile(file + "main.ntf")
	
	;WriteString f, VersionNumber
	WriteString f, CompatibleNumber
	
	WriteString f, CurrentTime()
	WriteString f, CurrentDate()
	
	WriteInt f, PlayTime
	WriteFloat f, EntityX(Collider)
	WriteFloat f, EntityY(Collider)
	WriteFloat f, EntityZ(Collider)
	
	WriteFloat f, EntityX(Head)
	WriteFloat f, EntityY(Head)
	WriteFloat f, EntityZ(Head)
	
	WriteFloat f, EntityPitch(Collider)
	WriteFloat f, EntityYaw(Collider)
	
	WriteString f, Str(AccessCode)
	
	WriteFloat f, BlinkTimer
	WriteFloat f, BlinkEffect
	WriteFloat f, BlinkEffectTimer
	
	WriteInt f, DeathTimer
	WriteInt f, BlurTimer
	WriteFloat f, HealTimer
	
	For g = Each Guns
		WriteInt f, g\CurrAmmo
		WriteInt f, g\CurrReloadAmmo
	Next
	WriteInt f, g_I\HoldingGun
	WriteFloat f, psp\Health
	WriteFloat f, psp\Kevlar
	WriteByte f, g_I\Weapon_CurrSlot
	For i = 0 To MaxGunSlots-1
		WriteString f, g_I\Weapon_InSlot[i]
	Next
	WriteByte f, psp\Checkpoint106Passed
	
	For t = Each NewTask
		If t\Status <> TASK_STATUS_END Then
			WriteByte f, 1
			WriteInt f, t\ID
		EndIf
	Next
	WriteByte f, 0
	
	WriteByte f, mtfd\Enabled
	WriteByte f, mtfd\IsPlaying
	WriteByte f, mtfd\CurrentProgress
	WriteByte f, mtfd\PrevDialogue
	WriteByte f, mtfd\CurrentDialogue
	WriteInt f, mtfd\CurrentSequence
	WriteFloat f, mtfd\Timer
	
	WriteByte f, Crouch
	
	WriteFloat f, Stamina
	WriteFloat f, StaminaEffect
	WriteFloat f, StaminaEffectTimer
	
	WriteFloat f, EyeStuck	
	WriteFloat f, EyeIrritation
	
	WriteString f, DeathMSG
	
	For i = 0 To 5
		WriteFloat f, SCP1025state[i]
	Next
	
	WriteFloat f, VomitTimer
	WriteByte f, Vomit
	WriteFloat f, CameraShakeTimer
	WriteFloat f, Infect
	
	For i = 0 To ESOTERIC
		If (SelectedDifficulty = difficulties[i]) Then
			WriteByte f, i
			
			If (i = ESOTERIC) Then
				WriteByte f,SelectedDifficulty\aggressiveNPCs
				WriteByte f,SelectedDifficulty\permaDeath
				WriteByte f,SelectedDifficulty\saveType
				WriteByte f,SelectedDifficulty\otherFactors
			EndIf
		EndIf
	Next
	
	WriteFloat f, CameraFogFar
	WriteFloat f, StoredCameraFogFar
	
	WriteFloat f, MonitorTimer
	
	WriteFloat f, Sanity
	
	WriteByte f, mpl\HasNTFGasmask
	WriteByte f, WearingGasMask
	WriteByte f, WearingVest
	WriteByte f, WearingHazmat
	WriteByte f, I_427\Using
	WriteFloat f, I_427\Timer
	WriteByte f, Wearing714
	
	WriteByte f, WearingNightVision
	WriteByte f, mpl\NightVisionEnabled
	WriteByte f, Wearing1499
	WriteFloat f,NTF_1499PrevX#
	WriteFloat f,NTF_1499PrevY#
	WriteFloat f,NTF_1499PrevZ#
	WriteFloat f,NTF_1499X#
	WriteFloat f,NTF_1499Y#
	WriteFloat f,NTF_1499Z#
	If NTF_1499PrevRoom <> Null
		WriteFloat f,NTF_1499PrevRoom\x
		WriteFloat f,NTF_1499PrevRoom\z
	Else
		WriteFloat f,0.0
		WriteFloat f,0.0
	EndIf
	
	WriteByte f, SuperMan
	WriteFloat f, SuperManTimer
	WriteByte f, LightsOn
	
	WriteString f, RandomSeed
	
	WriteFloat f, SecondaryLightOn
	WriteFloat f, PrevSecondaryLightOn
	WriteByte f, RemoteDoorOn
	WriteByte f, SoundTransmission
	WriteByte f, Contained106
	
;	For i = 0 To MAXACHIEVEMENTS-1
;		WriteByte f, Achievements[i]\Unlocked
;	Next
	WriteInt f, RefinedItems
	
	WriteFloat f, MTFtimer
	
	;Write the IDs of the items that are inside an inventory slot (either in the inventory itself or inside of the inventory of an item that is in the inventory of the player)
	For it = Each Items
		If IsItemInInventory(it) Then
			WriteByte f, 1
			WriteString f, it\itemtemplate\name
			WriteString f, it\itemtemplate\tempname
			WriteString f, it\name
			WriteFloat f, EntityX(it\collider, True)
			WriteFloat f, EntityY(it\collider, True)
			WriteFloat f, EntityZ(it\collider, True)
			WriteByte f, it\r
			WriteByte f, it\g
			WriteByte f, it\b
			WriteFloat f, it\a
			WriteFloat f, EntityPitch(it\collider)
			WriteFloat f, EntityYaw(it\collider)
			WriteFloat f, it\state
			If SelectedItem = it Then WriteByte f, 1 Else WriteByte f, 0
			If it\itemtemplate\isAnim<>0 Then
				WriteFloat f, AnimTime(it\model)
			EndIf
			WriteByte f,it\invSlots
			WriteInt f,it\ID
			If it\itemtemplate\invimg=it\invimg Then WriteByte f,0 Else WriteByte f,1
		EndIf
	Next
	WriteByte f, 0
	
	For i = 0 To MaxItemAmount-1
		If Inventory[i] <> Null Then
			WriteInt f, Inventory[i]\ID
			If Inventory[i]\invSlots > 0 Then
				For j = 0 To Inventory[i]\invSlots-1
					If Inventory[i]\SecondInv[j] <> Null Then
						WriteInt f, Inventory[i]\SecondInv[j]\ID
					Else
						WriteInt f, -1
					EndIf
				Next
			EndIf
		Else
			WriteInt f, -1
		EndIf
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		WriteByte f, itt\found
	Next
	
	WriteByte f, UsedConsole
	
	If newzone > -1 Then
		WriteInt f, newzone
	Else
		WriteInt f, NTF_CurrZone
	EndIf
	
	CloseFile f
	
	SaveZoneData(file)
	
	If Not MenuOpen Then
		If SelectedDifficulty\saveType = SAVEONSCREENS Then
			PlaySound_Strict(LoadTempSound("SFX\General\Save2.ogg"))
		Else
			PlaySound_Strict(LoadTempSound("SFX\General\Save1.ogg"))
		EndIf
		
		;Msg = "Game progress saved."
		;MsgTimer = 70 * 4
		SetSaveMSG(GetLocalString("Menu", "progress_saved"))
	EndIf
	
	CatchErrors("Uncaught (SaveGame(" + file + ", " + newzone + "))")
End Function

Function SaveZoneData(file$)
	Local n.NPCs, r.Rooms, em.Emitters, do.Doors, it.Items, fb.FuseBox, ne.NewElevator
	Local f%, temp%, i%, j%
	
	If FileType(file) <> 2 Then
		CreateDir(file)
	EndIf
	
	;Everything after that is for SZL
	f% = WriteFile(file + "\" + NTF_CurrZone + ".ntf")
	
	;[Block]
	;Check if necessary
	;WriteInt f, MapWidth
	;WriteInt f, MapHeight
;	For x = 0 To MapWidth
;		For y = 0 To MapHeight
;			WriteInt f, MapTemp[x * MapWidth + y]
;			WriteByte f, MapFound[x * MapWidth + y]
;		Next
;	Next
	;[End Block]
	
	temp = 0
	For  n.NPCs = Each NPCs
		temp = temp +1
	Next
	
	WriteInt f, temp
	For n.NPCs = Each NPCs
		DebugLog("Saving NPC " +n\NVName+ " (ID "+n\ID+")")
		
		WriteByte f, n\NPCtype
		WriteFloat f, EntityX(n\Collider,True)
		WriteFloat f, EntityY(n\Collider,True)
		WriteFloat f, EntityZ(n\Collider,True)
		
		WriteFloat f, EntityPitch(n\Collider)
		WriteFloat f, EntityYaw(n\Collider)
		WriteFloat f, EntityRoll(n\Collider)
		
		WriteFloat f, n\State
		WriteFloat f, n\State2
		WriteFloat f, n\State3
		WriteInt f, n\PrevState
		
		WriteByte f, n\Idle
		WriteFloat f, n\LastDist
		WriteInt f, n\LastSeen
		
		WriteInt f, n\CurrSpeed
		
		WriteFloat f, n\Angle
		
		WriteFloat f, n\Reload
		
		WriteInt f, n\ID
		If n\Target <> Null Then
			WriteInt f, n\Target\ID		
		Else
			WriteInt f, 0
		EndIf
		
		WriteFloat f, n\EnemyX
		WriteFloat f, n\EnemyY
		WriteFloat f, n\EnemyZ
		
		WriteString f, n\texture
		
		WriteFloat f, AnimTime(n\obj)
		
		WriteInt f, n\IsDead
		WriteFloat f, n\PathX
		WriteFloat f, n\PathZ
		WriteInt f, n\HP
		WriteString f, n\Model
		WriteFloat f, n\ModelScaleX#
		WriteFloat f, n\ModelScaleY#
		WriteFloat f, n\ModelScaleZ#
		WriteInt f, n\TextureID
		
		If n\Gun <> Null Then
			WriteInt f, n\Gun\ID
			WriteInt f, n\Gun\Ammo
		Else
			WriteInt f, GUN_UNARMED
		EndIf
	Next
	
	For i = 0 To 6
		If MTFrooms[0]<>Null Then 
			WriteString f, MTFrooms[0]\RoomTemplate\Name 
		Else 
			WriteString f,	"a"
		EndIf
		WriteInt f, MTFroomState[i]
	Next
	
	WriteInt f, room2gw_brokendoor
	WriteFloat f,room2gw_x
	WriteFloat f,room2gw_z
	
	;[Block]
	;Custom map support will be added later!
	;WriteByte f, I_Zone\Transition[0]
	;WriteByte f, I_Zone\Transition[1]
	;WriteByte f, I_Zone\HasCustomForest
	;WriteByte f, I_Zone\HasCustomMT
	
;	temp = 0
;	For r.Rooms = Each Rooms
;		temp=temp+1
;	Next	
;	WriteInt f, temp	
;	For r.Rooms = Each Rooms
;		WriteInt f, r\RoomTemplate\id
;		WriteInt f, r\angle
;		WriteFloat f, r\x
;		WriteFloat f, r\y
;		WriteFloat f, r\z
;		
;		WriteByte f, r\found
;		
;		WriteInt f, r\zone
;		
;		If PlayerRoom = r Then 
;			WriteByte f, 1
;		Else 
;			WriteByte f, 0
;		EndIf
;		
;		For i = 0 To 11
;			If r\NPC[i]=Null Then
;				WriteInt f, 0
;			Else
;				WriteInt f, r\NPC[i]\ID
;			EndIf
;		Next
;		
;		For i=0 To 10
;			If r\Levers[i]<>0 Then
;				If EntityPitch(r\Levers[i],True) > 0 Then ;p??????ll???
;					WriteByte(f,1)
;				Else
;					WriteByte(f,0)
;				EndIf	
;			EndIf
;		Next
;		WriteByte(f,2)
;		
;		If r\grid=Null Then ;this room doesn't have a grid
;			WriteByte(f,0)
;		Else ;this room has a grid
;			WriteByte(f,1)
;			For y=0 To gridsz-1
;				For x=0 To gridsz-1
;					WriteByte(f,r\grid\grid[x+(y*gridsz)])
;					WriteByte(f,r\grid\angles[x+(y*gridsz)])
;				Next
;			Next
;		EndIf
;		
;		If r\fr=Null Then ;this room doesn't have a forest
;			WriteByte(f,0)
;		Else ;this room has a forest
;			;Custom map support will be added later!
;			;If (Not I_Zone\HasCustomForest) Then
;			;	WriteByte(f,1)
;			;Else
;			;	WriteByte(f,2)
;			;EndIf
;			WriteByte(f,1)
;			For y=0 To gridsize-1
;				For x=0 To gridsize-1
;					WriteByte(f,r\fr\grid[x+(y*gridsize)])
;				Next
;			Next
;			WriteFloat f,EntityX(r\fr\Forest_Pivot,True)
;			WriteFloat f,EntityY(r\fr\Forest_Pivot,True)
;			WriteFloat f,EntityZ(r\fr\Forest_Pivot,True)
;		EndIf
;	Next
	;[End Block]
	
	WriteFloat f, PlayerRoom\x
	WriteFloat f, PlayerRoom\z
	For r = Each Rooms
		WriteByte f, r\found
		
		For i = 0 To 11
			If r\NPC[i]=Null Then
				WriteInt f, 0
			Else
				WriteInt f, r\NPC[i]\ID
			EndIf
		Next
		
		For i=0 To 10
			If r\Levers[i]=Null Then
				WriteByte(f,2)
			Else
				If EntityPitch(r\Levers[i]\obj,True) > 0 Then
					WriteByte(f,1)
				Else
					WriteByte(f,0)
				EndIf	
			EndIf
		Next
		
		temp = 0
		For em = Each Emitters
			If (Not em\map_generated) And em\Room = r Then
				temp = temp + 1
			EndIf
		Next
		WriteInt f, temp
		For em = Each Emitters
			If (Not em\map_generated) And em\Room = r Then
				WriteFloat f, EntityX(em\Obj, True)
				WriteFloat f, EntityY(em\Obj, True)
				WriteFloat f, EntityZ(em\Obj, True)
				WriteInt f, em\emittertype
				WriteFloat f, EntityPitch(em\Obj)
				WriteFloat f, EntityYaw(em\Obj)
				WriteFloat f, EntityRoll(em\Obj)
				WriteFloat f, em\Size
				WriteFloat f, em\SizeChange
				WriteFloat f, em\Speed
				WriteFloat f, em\RandAngle
			EndIf
		Next
	Next
	
	temp = 0
	For do.Doors = Each Doors
		temp = temp + 1	
	Next	
	WriteInt f, temp	
	For do.Doors = Each Doors
		WriteFloat f, EntityX(do\frameobj,True)
		WriteFloat f, EntityY(do\frameobj,True)
		WriteFloat f, EntityZ(do\frameobj,True)
		WriteByte f, do\open
		WriteFloat f, do\openstate
		WriteByte f, do\locked
		WriteByte f, do\AutoClose
		
		WriteFloat f, EntityX(do\obj, True)
		WriteFloat f, EntityZ(do\obj, True)
		
		If do\obj2 <> 0 Then
			WriteFloat f, EntityX(do\obj2, True)
			WriteFloat f, EntityZ(do\obj2, True)
		Else
			WriteFloat f, 0.0
			WriteFloat f, 0.0
		End If
		
		WriteFloat f, do\timer
		WriteFloat f, do\timerstate
		
		WriteByte f, do\IsElevatorDoor
		WriteByte f, do\MTFClose
	Next
	
	Local d.Decals
	temp = 0
	For d.Decals = Each Decals
		temp = temp+1
	Next	
	WriteInt f, temp
	For d.Decals = Each Decals
		WriteInt f, d\ID
		
		WriteFloat f, EntityX(d\obj,True)
		WriteFloat f, EntityY(d\obj,True)
		WriteFloat f, EntityZ(d\obj,True)
		
		WriteFloat f, EntityPitch(d\obj,True)
		WriteFloat f, EntityYaw(d\obj,True)
		WriteFloat f, EntityRoll(d\obj,True)
		
		WriteByte f, d\blendmode
		WriteInt f, d\fx
		
		WriteFloat f, d\Size
		WriteFloat f, d\Alpha
		WriteFloat f, d\AlphaChange
		WriteFloat f, d\Timer
		WriteFloat f, d\lifetime
	Next
	
	Local e.Events
	temp = 0
	For e.Events = Each Events
		temp=temp+1
	Next	
	WriteInt f, temp
	For e.Events = Each Events
		WriteString f, e\EventName
		WriteFloat f, e\EventState
		WriteFloat f, e\EventState2	
		WriteFloat f, e\EventState3	
		WriteFloat f, EntityX(e\room\obj)
		WriteFloat f, EntityZ(e\room\obj)
		WriteString f, e\EventStr
	Next
	
	For it = Each Items
		If (Not IsItemInInventory(it)) Then
			WriteByte f, 1
			WriteString f, it\itemtemplate\name
			WriteString f, it\itemtemplate\tempname
			WriteString f, it\name
			WriteFloat f, EntityX(it\collider, True)
			WriteFloat f, EntityY(it\collider, True)
			WriteFloat f, EntityZ(it\collider, True)
			WriteByte f, it\r
			WriteByte f, it\g
			WriteByte f, it\b
			WriteFloat f, it\a
			WriteFloat f, EntityPitch(it\collider)
			WriteFloat f, EntityYaw(it\collider)
			WriteFloat f, it\state
			If it\itemtemplate\isAnim<>0 Then
				WriteFloat f, AnimTime(it\model)
			EndIf
			WriteByte f,it\invSlots
			WriteInt f,it\ID
			If it\itemtemplate\invimg=it\invimg Then WriteByte f,0 Else WriteByte f,1
		EndIf
	Next
	WriteByte f, 0
	
	For it = Each Items
		temp = 0
		For i = 0 To MaxItemAmount-1
			If Inventory[i] <> Null Then
				If it = Inventory[i] Then
					temp = 1
					Exit
				ElseIf Inventory[i]\invSlots > 0 Then
					For j = 0 To Inventory[i]\invSlots-1
						If it = Inventory[i]\SecondInv[j] Then
							temp = 1
							Exit
						EndIf
					Next
				EndIf
			EndIf
		Next
		If temp = 0 And it\invSlots > 0 Then
			WriteByte f, 1
			For i = 0 To it\invSlots-1
				If it\SecondInv[i] <> Null Then
					WriteInt f, it\SecondInv[i]\ID
				Else
					WriteInt f, -1
				EndIf
			Next
		EndIf
	Next
	WriteByte f, 0
	
	For fb = Each FuseBox
		WriteByte f, fb\fuses
	Next
	
	For ne = Each NewElevator
		WriteByte f, ne\tofloor
		WriteByte f, ne\currfloor
		WriteFloat f, EntityY(ne\obj)
		WriteByte f, ne\currsound
		WriteFloat f, ne\state
		WriteByte f, ne\door\open
	Next
	WriteByte f, PlayerInNewElevator
	WriteByte f, PlayerNewElevator
	
	CloseFile f
	
End Function

Function LoadPlayerData(file$, f%)
	Local version$ = ""
	
	Local x#, y#, z#, i%, j%, temp%, temp2%, strtemp$
	Local g.Guns, itt.ItemTemplates, it2.Items, it.Items, t.NewTask
	
	AccessCode = Int(ReadString(f))
	
	BlinkTimer = ReadFloat(f)
	BlinkEffect = ReadFloat(f)	
	BlinkEffectTimer = ReadFloat(f)	
	
	DeathTimer = ReadInt(f)	
	BlurTimer = ReadInt(f)	
	HealTimer = ReadFloat(f)
	
	For g = Each Guns
		g\CurrAmmo = ReadInt(f)
		g\CurrReloadAmmo = ReadInt(f)
	Next
	g_I\HoldingGun = ReadInt(f)
	psp\Health = ReadFloat(f)
	psp\Kevlar = ReadFloat(f)
	g_I\Weapon_CurrSlot = ReadByte(f)
	For i = 0 To MaxGunSlots-1
		g_I\Weapon_InSlot[i] = ReadString(f)
	Next
	DeselectIronSight()
	g_I\GunChangeFLAG = False
	psp\Checkpoint106Passed = ReadByte(f)
	
	Delete Each NewTask
	temp = ReadByte(f)
	While temp
		t.NewTask = BeginTask(ReadInt(f))
		t\Timer = 0
		t\Status = TASK_STATUS_ALREADY
		temp = ReadByte(f)
	Wend
	
	mtfd\Enabled = ReadByte(f)
	mtfd\IsPlaying = ReadByte(f)
	mtfd\CurrentProgress = ReadByte(f)
	mtfd\PrevDialogue = ReadByte(f)
	mtfd\CurrentDialogue = ReadByte(f)
	mtfd\CurrentSequence = ReadInt(f)
	mtfd\Timer = ReadFloat(f)
	
	Crouch = ReadByte(f)
	
	Stamina = ReadFloat(f)
	StaminaEffect = ReadFloat(f)	
	StaminaEffectTimer = ReadFloat(f)	
	
	EyeStuck = ReadFloat(f)
	EyeIrritation = ReadFloat(f)
	
	DeathMSG = ReadString(f)
	
	For i = 0 To 5
		SCP1025state[i]=ReadFloat(f)
	Next
	
	VomitTimer = ReadFloat(f)
	Vomit = ReadByte(f)
	CameraShakeTimer = ReadFloat(f)
	Infect = ReadFloat(f)
	
	Local difficultyIndex = ReadByte(f)
	SelectedDifficulty = difficulties[difficultyIndex]
	If (difficultyIndex = ESOTERIC) Then
		SelectedDifficulty\aggressiveNPCs = ReadByte(f)
		SelectedDifficulty\permaDeath = ReadByte(f)
		SelectedDifficulty\saveType	= ReadByte(f)
		SelectedDifficulty\otherFactors = ReadByte(f)
	EndIf
	
	CameraFogFar = ReadFloat(f)
    StoredCameraFogFar = ReadFloat(f)
	If CameraFogFar = 0 Then
		CameraFogFar = 6
	EndIf
	
	MonitorTimer = ReadFloat(f)
	
	Sanity = ReadFloat(f)
	
	mpl\HasNTFGasmask = ReadByte(f)
	WearingGasMask = ReadByte(f)
	WearingVest = ReadByte(f)	
	WearingHazmat = ReadByte(f)
	I_427\Using = ReadByte(f)
	I_427\Timer = ReadFloat(f)
	Wearing714 = ReadByte(f)
	
	WearingNightVision = ReadByte(f)
	mpl\NightVisionEnabled = ReadByte(f)
	Wearing1499 = ReadByte(f)
	NTF_1499PrevX# = ReadFloat(f)
	NTF_1499PrevY# = ReadFloat(f)
	NTF_1499PrevZ# = ReadFloat(f)
	NTF_1499X# = ReadFloat(f)
	NTF_1499Y# = ReadFloat(f)
	NTF_1499Z# = ReadFloat(f)
	Local r1499_x# = ReadFloat(f)
	Local r1499_z# = ReadFloat(f)
	
	SuperMan = ReadByte(f)
	SuperManTimer = ReadFloat(f)
	LightsOn = ReadByte(f)
	
	RandomSeed = ReadString(f)
	
	SecondaryLightOn = ReadFloat(f)
	PrevSecondaryLightOn = ReadFloat(f)
	RemoteDoorOn = ReadByte(f)
	SoundTransmission = ReadByte(f)	
	Contained106 = ReadByte(f)	
	
;	For i = 0 To MAXACHIEVEMENTS-1
;		Achievements[i]\Unlocked=ReadByte(f)
;	Next
	RefinedItems = ReadInt(f)
	
	MTFtimer = ReadFloat(f)
	
	For it = Each Items
		RemoveItem(it)
	Next
	
	temp = ReadByte(f)
	While temp
		Local ittName$ = ReadString(f)
		Local tempName$ = ReadString(f)
		Local Name$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local red% = ReadByte(f)
		Local green% = ReadByte(f)
		Local blue% = ReadByte(f)		
		Local a# = ReadFloat(f)
		
		it.Items = CreateItem(ittName, tempName, x, y, z, red,green,blue,a)
		it\name = Name
		
		EntityType it\collider, HIT_ITEM
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\collider, x, y, 0)
		
		it\state = ReadFloat(f)
		it\Picked = True : HideEntity(it\collider)
		
		Local nt% = ReadByte(f)
		If nt = True Then SelectedItem = it
		
		For itt.ItemTemplates = Each ItemTemplates
			If (itt\tempname = tempName) And (itt\name = ittName) Then
				If itt\isAnim<>0 Then SetAnimTime it\model,ReadFloat(f) : Exit
			EndIf
		Next
		it\invSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID>LastItemID Then LastItemID=it\ID
		
		If ReadByte(f)=0 Then
			it\invimg=it\itemtemplate\invimg
		Else
			it\invimg=it\itemtemplate\invimg2
		EndIf	
		
		temp = ReadByte(f)
	Wend
	
	For i = 0 To MaxItemAmount-1
		temp = ReadInt(f)
		If temp > -1 Then
			For it = Each Items
				If it\ID = temp Then
					Inventory[i] = it
					ItemAmount = ItemAmount + 1
					If it\invSlots > 0 Then
						For j = 0 To it\invSlots-1
							temp2 = ReadInt(f)
							If temp2 > -1 Then
								For it2 = Each Items
									If it2\ID = temp2 Then
										it\SecondInv[j] = it2
										Exit
									EndIf
								Next
							EndIf
						Next
					EndIf
					Exit
				EndIf
			Next
		EndIf
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = ReadByte(f)
	Next
	
	UsedConsole = ReadByte(f)
	
	NTF_CurrZone = ReadInt(f)
	
End Function

Function LoadDataForZones(file$)
	Local f% = ReadFile(file + "main.ntf")
	
	ReadString(f)
	ReadString(f)
	ReadString(f)
	
	PlayTime = ReadInt(f)
	
	ReadFloat(f)
	ReadFloat(f)
	ReadFloat(f)
	
	ReadFloat(f)
	ReadFloat(f)
	ReadFloat(f)
	
	ReadFloat(f)
	ReadFloat(f)
	
	LoadPlayerData(file, f)
	
	CloseFile f
End Function

Function LoadGame(file$, zoneToLoad%=-1)
	CatchErrors("LoadGame(" + file + ")")
	DebugLog "---------------------------------------------------------------------------"
	Local version$ = ""
	
	DropSpeed=0.0
	
	DebugHUD = False
	
	GameSaved = True
	
	Local x#, y#, z#, i%, j%, temp%, temp2%, strtemp$, id%, tex%, dist#, dist2#
	Local player_x#,player_y#,player_z#, r.Rooms, n.NPCs, do.Doors, g.Guns, itt.ItemTemplates, it2.Items, n2.NPCs, it.Items, em.Emitters, fb.FuseBox, ne.NewElevator
	Local f% = ReadFile(file + "main.ntf")
	
	version = ReadString(f)
	strtemp = ReadString(f)
	strtemp = ReadString(f)
	
	PlayTime = ReadInt(f)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y+0.05, z)
	ResetEntity(Collider)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y+0.05, z)
	ResetEntity(Head)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0, 0)
	
	LoadPlayerData(file, f)
	
	If zoneToLoad > -1 Then
		NTF_CurrZone = zoneToLoad
	EndIf
	
	CloseFile f
	
	f = ReadFile(file + NTF_CurrZone + ".ntf")
	
	;[Block]
	;TODO: Check if necessary
	;MapWidth = ReadInt(f)
	;MapHeight = ReadInt(f)
;	For x = 0 To MapWidth
;		For y = 0 To MapHeight
;			MapTemp[x * MapWidth + y] = ReadInt(f)
;			MapFound[x * MapWidth + y] = ReadByte(f)
;		Next
;	Next
	;[End Block]
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local NPCtype% = ReadByte(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				Curr173 = n
			Case NPCtypeOldMan
				Curr106 = n
			Case NPCtype096
				Curr096 = n
			Case NPCtype5131
				Curr5131 = n
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		DebugLog("Loading NPC " +n\NVName+ " (ID "+n\ID+")")
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\texture = ReadString(f)
		If n\texture <> "" Then
			tex = LoadTexture_Strict (n\texture)
			TextureBlend(tex,5)
			EntityTexture n\obj, tex
		EndIf
		
		Local frame# = ReadFloat(f)
		Select NPCtype
			Case NPCtypeOldMan, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtypeZombie, NPCtypeClerk, NPCtypeD2
				SetAnimTime(n\obj, frame)
		End Select
		
		n\Frame = frame
		
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX# = ReadFloat(f)
		n\ModelScaleY# = ReadFloat(f)
		n\ModelScaleZ# = ReadFloat(f)
		If n\Model <> ""
			n\obj = FreeEntity_Strict(n\obj)
			n\obj = LoadAnimMesh_Strict(n\Model)
			ScaleEntity n\obj,n\ModelScaleX,n\ModelScaleY,n\ModelScaleZ
			SetAnimTime n\obj,frame
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs,n\TextureID-1)
			SetAnimTime(n\obj,frame)
		EndIf
		
		Local GunID% = ReadInt(f)
		If GunID <> GUN_UNARMED Then
			If n\Gun = Null Lor n\Gun\ID <> GunID Then
				SwitchNPCGun(n, GunID)
			EndIf
			Local GunAmmo% = ReadInt(f)
			If n\Gun <> Null Then
				n\Gun\Ammo = GunAmmo
			EndIf
		ElseIf n\Gun <> Null Then
			RemoveNPCGun(n)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.NPCs = Each NPCs
				If n2<>n Then
					If n2\ID = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	For i = 0 To 6
		strtemp =  ReadString(f)
		If strtemp <> "a" Then
			For r.Rooms = Each Rooms
				If r\RoomTemplate\Name = strtemp Then
					MTFrooms[i]=r
				EndIf
			Next
		EndIf
		MTFroomState[i]=ReadInt(f)
	Next
	
	room2gw_brokendoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	;[Block]
	;Custom map support will be added later!
	;If version = CompatibleNumber Then
	;	I_Zone\Transition[0] = ReadByte(f)
	;	I_Zone\Transition[1] = ReadByte(f)
	;	I_Zone\HasCustomForest = ReadByte(f)
	;	I_Zone\HasCustomMT = ReadByte(f)
	;EndIf
	
;	temp = ReadInt(f)
;	For i = 1 To temp
;		Local roomtemplateID% = ReadInt(f)
;		Local angle% = ReadInt(f)
;		x = ReadFloat(f)
;		y = ReadFloat(f)
;		z = ReadFloat(f)
;		
;		found = ReadByte(f)
;		
;		level = ReadInt(f)
;		
;		temp2 = ReadByte(f)		
;		
;;		If angle >= 360
;;            angle = angle-360
;;        EndIf
;		angle=WrapAngle(angle)
;		
;		For rt.roomtemplates = Each RoomTemplates
;			If rt\id = roomtemplateID Then
;				r.Rooms = CreateRoom(level, rt\shape, x, y, z, rt\name)
;				TurnEntity(r\obj, 0, angle, 0)
;				r\angle = angle
;				r\found = found
;				Exit
;			End If
;		Next
;		
;		If temp2 = 1 Then PlayerRoom = r.Rooms
;		
;		For x = 0 To 11
;			id = ReadInt(f)
;			If id > 0 Then
;				For n.NPCs = Each NPCs
;					If n\ID = id Then r\NPC[x]=n : Exit
;				Next
;			EndIf
;		Next
;		
;		For x=0 To 11
;			id = ReadByte(f)
;			If id=2 Then
;				Exit
;			Else If id=1 Then
;				RotateEntity(r\Levers[x], 78, EntityYaw(r\Levers[x]), 0)
;			Else
;				RotateEntity(r\Levers[x], -78, EntityYaw(r\Levers[x]), 0)
;			EndIf
;		Next
;		
;		If ReadByte(f)=1 Then ;this room has a grid
;			If r\grid<>Null Then ;remove the old grid content
;				For x=0 To gridsz-1
;					For y=0 To gridsz-1
;						If r\grid\Entities[x+(y*gridsz)]<>0 Then
;							r\grid\Entities[x+(y*gridsz)] = FreeEntity_Strict(r\grid\Entities[x+(y*gridsz)])
;						EndIf
;						If r\grid\waypoints[x+(y*gridsz)]<>Null Then
;							RemoveWaypoint(r\grid\waypoints[x+(y*gridsz)])
;							r\grid\waypoints[x+(y*gridsz)]=Null
;						EndIf
;					Next
;				Next
;				For x=0 To 5
;					If r\grid\Meshes[x]<>0 Then
;						r\grid\Meshes[x] = FreeEntity_Strict(r\grid\Meshes[x])
;					EndIf
;				Next
;				Delete r\grid
;			EndIf
;			r\grid=New Grids
;			For y=0 To gridsz-1
;				For x=0 To gridsz-1
;					r\grid\grid[x+(y*gridsz)]=ReadByte(f)
;					r\grid\angles[x+(y*gridsz)]=ReadByte(f)
;					;get only the necessary data, make the event handle the meshes and waypoints separately
;				Next
;			Next
;		EndIf
;		
;		Local hasForest = ReadByte(f)
;		If hasForest>0 Then ;this room has a forest
;			If r\fr<>Null Then ;remove the old forest
;				DestroyForest(r\fr)
;			Else
;				r\fr=New Forest
;			EndIf
;			For y=0 To gridsize-1
;				Local sssss$ = ""
;				For x=0 To gridsize-1
;					r\fr\grid[x+(y*gridsize)]=ReadByte(f)
;					sssss=sssss+Str(r\fr\grid[x+(y*gridsize)])
;				Next
;				DebugLog sssss
;			Next
;			lx# = ReadFloat(f)
;			ly# = ReadFloat(f)
;			lz# = ReadFloat(f)
;			If hasForest=1 Then
;				PlaceForest(r\fr,lx,ly,lz,r)
;			Else
;				;Custom map support will be added later!
;				;PlaceForest_MapCreator(r\fr,lx,ly,lz,r)
;			EndIf
;		ElseIf r\fr<>Null Then ;remove the old forest
;			DestroyForest(r\fr)
;			Delete r\fr
;		EndIf
;		
;	Next
	;[End Block]
	
	;TODO: Add custom map support
	CreateMap()
	
	Local rx = ReadFloat(f)
	Local rz = ReadFloat(f)
	For r = Each Rooms
		r\found = ReadByte(f)
		
		If r\x = rx And r\z = rz Then
			PlayerRoom = r
		EndIf	
		
		If r\x = r1499_x# And r\z = r1499_z#
			NTF_1499PrevRoom = r
		EndIf
		
		For x = 0 To 11
			id = ReadInt(f)
			If id > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = id Then r\NPC[x]=n : Exit
				Next
			EndIf
		Next
		
		For x=0 To 10
			id = ReadByte(f)
			If id=1 Then
				RotateEntity(r\Levers[x]\obj, 78, EntityYaw(r\Levers[x]\obj), 0)
			ElseIf id=0 Then
				RotateEntity(r\Levers[x]\obj, -78, EntityYaw(r\Levers[x]\obj), 0)
			EndIf
		Next
		
		temp = ReadInt(f)
		For j = 1 To temp
			x = ReadFloat(f)
			y = ReadFloat(f)
			z = ReadFloat(f)
			id = ReadInt(f)
			em.Emitters = CreateEmitter(x, y, z, id)
			x = ReadFloat(f)
			y = ReadFloat(f)
			z = ReadFloat(f)
			RotateEntity em\Obj, x, y, z
			EntityParent(em\Obj, r\obj)
			em\Size = ReadFloat(f)
			em\SizeChange = ReadFloat(f)
			em\Speed = ReadFloat(f)
			em\RandAngle = ReadFloat(f)
		Next
	Next
	
	;[Block]
;	Local spacing# = 8.0
;	Local zone%,shouldSpawnDoor%
;	Local test% = 0
;	For y = MapHeight To 0 Step -1
;		
;		;TODO: Work a bit on this!
;		;If y<I_Zone\Transition[1]-(SelectedMap="") Then
;		;	zone=3
;		;ElseIf y>=I_Zone\Transition[1]-(SelectedMap="") And y<I_Zone\Transition[0]-(SelectedMap="") Then
;		;	zone=2
;		;Else
;		;	zone=1
;		;EndIf
;		zone = NTF_CurrZone
;		
;		For x = MapWidth To 0 Step -1
;			If MapTemp[x * MapWidth + y] > 0 Then
;				test = test + 1
;				If zone = 2 Then temp=2 Else temp=0
;                
;                For r.Rooms = Each Rooms
;					r\angle = WrapAngle(r\angle)
;					If Int(r\x/8.0)=x And Int(r\z/8.0)=y Then
;						shouldSpawnDoor = False
;						Select r\RoomTemplate\Shape
;							Case ROOM1
;								If r\angle=90
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM2
;								If r\angle=90 Or r\angle=270
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM2C
;								If r\angle=0 Or r\angle=90
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM3
;								If r\angle=0 Or r\angle=180 Or r\angle=90
;									shouldSpawnDoor = True
;								EndIf
;							Default
;								shouldSpawnDoor = True
;						End Select
;						If shouldSpawnDoor Then
;							If (x+1)<(MapWidth+1) Then
;								If MapTemp[(x + 1) * MapWidth + y] > 0 Then
;									do.Doors = CreateDoor(r\zone, Float(x) * spacing + spacing / 2.0, 0, Float(y) * spacing, 90, r, Max(Rand(-3, 1), 0), temp)
;									r\AdjDoor[0] = do
;								EndIf
;							EndIf
;						EndIf
;						
;						shouldSpawnDoor = False
;						Select r\RoomTemplate\Shape
;							Case ROOM1
;								If r\angle=180
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM2
;								If r\angle=0 Or r\angle=180
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM2C
;								If r\angle=180 Or r\angle=90
;									shouldSpawnDoor = True
;								EndIf
;							Case ROOM3
;								If r\angle=180 Or r\angle=90 Or r\angle=270
;									shouldSpawnDoor = True
;								EndIf
;							Default
;								shouldSpawnDoor = True
;						End Select
;						If shouldSpawnDoor Then
;							If (y+1)<(MapHeight+1) Then
;								If MapTemp[x * MapWidth + (y + 1)] > 0 Then
;									do.Doors = CreateDoor(r\zone, Float(x) * spacing, 0, Float(y) * spacing + spacing / 2.0, 0, r, Max(Rand(-3, 1), 0), temp)
;									r\AdjDoor[3] = do
;								EndIf
;							EndIf
;						EndIf
;						
;						Exit
;					EndIf
;                Next
;			EndIf
;		Next
;	Next
;	RuntimeError test
	;[End Block]
	
	temp = ReadInt(f)
	
	For i = 1 To temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local open% = ReadByte(f)
		Local openstate# = ReadFloat(f)
		Local locked% = ReadByte(f)
		Local autoclose% = ReadByte(f)
		
		Local objX# = ReadFloat(f)
		Local objZ# = ReadFloat(f)
		
		Local obj2X# = ReadFloat(f)
		Local obj2Z# = ReadFloat(f)
		
		Local timer% = ReadFloat(f)
		Local timerstate# = ReadFloat(f)
		
		Local IsElevDoor = ReadByte(f)
		Local MTFClose = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\frameobj,True) = x And EntityY(do\frameobj,True) = y And EntityZ(do\frameobj,True) = z Then
				do\open = open
				do\openstate = openstate
				do\locked = locked
				do\AutoClose = autoclose
				do\timer = timer
				do\timerstate = timerstate
				do\IsElevatorDoor = IsElevDoor
				do\MTFClose = MTFClose
				
				PositionEntity(do\obj, objX, y, objZ, True)
				If do\obj2 <> 0 Then PositionEntity(do\obj2, obj2X, y, obj2Z, True)
				Exit
			EndIf
		Next
	Next
	
	InitWayPoints()
	
	Local d.Decals
	For d.Decals = Each Decals
		d\obj = FreeEntity_Strict(d\obj)
		Delete d
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		id% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		Local pitch# = ReadFloat(f)
		Local yaw# = ReadFloat(f)
		Local roll# = ReadFloat(f)
		d.Decals = CreateDecal(id, x, y, z, pitch, yaw, roll)
		d\blendmode = ReadByte (f)
		d\fx = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\lifetime = ReadFloat(f)
		
		ScaleSprite(d\obj, d\Size, d\Size)
		EntityBlend d\obj, d\blendmode
		EntityFX d\obj, d\fx
		
		DebugLog "Created Decal @"+x+","+y+","+z
	Next
	UpdateDecals()
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local e.Events = New Events
		e\EventName = ReadString(f)
		
		e\EventState =ReadFloat(f)
		e\EventState2 =ReadFloat(f)		
		e\EventState3 =ReadFloat(f)
		x = ReadFloat(f)
		z = ReadFloat(f)
		For r.Rooms = Each Rooms
			If EntityX(r\obj) = x And EntityZ(r\obj) = z Then
				e\room = r
				Exit
			EndIf
		Next
		e\EventStr = ReadString(f)
	Next
	
	For e.Events = Each Events
		;Reset for the monitor loading and stuff for room2sl
		If e\EventName = "room2sl"
			e\EventState = 0.0
			e\EventStr = ""
			DebugLog "Reset Eventstate in "+e\EventName
		;Reset dimension1499
		ElseIf e\EventName = "dimension1499"
			If e\EventState > 0.0
				e\EventState = 0.0
				e\EventStr = ""
				HideChunks()
				DeleteChunks()
				For n.NPCs = Each NPCs
					If n\NPCtype = NPCtype1499
						If n\InFacility = 0
							RemoveNPC(n)
						EndIf
					EndIf
				Next
				DebugLog "Reset Eventstate in "+e\EventName
			EndIf
		;Reset the forest event to make it loading properly
		ElseIf e\EventName = "room860"
			e\EventStr = ""
		ElseIf e\EventName = "room205"
			e\EventStr = ""
		ElseIf e\EventName = "room106"
			If e\EventState2 = False Then
				PositionEntity (e\room\Objects[6],EntityX(e\room\Objects[6],True),-1280.0*RoomScale,EntityZ(e\room\Objects[6],True),True)
			EndIf
		EndIf
	Next
	
	For it.Items = Each Items
		If (Not IsItemInInventory(it)) Then
			RemoveItem(it)
		EndIf
	Next
	
	temp = ReadByte(f)
	While temp
		ittName$ = ReadString(f)
		tempName$ = ReadString(f)
		Name$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		red% = ReadByte(f)
		green% = ReadByte(f)
		blue% = ReadByte(f)
		a# = ReadFloat(f)
		
		it.Items = CreateItem(ittName, tempName, x, y, z, red,green,blue,a)
		it\name = Name
		
		EntityType it\collider, HIT_ITEM
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\collider, x, y, 0)
		
		it\state = ReadFloat(f)
		
		For itt.ItemTemplates = Each ItemTemplates
			If (itt\tempname = tempName) And (itt\name = ittName) Then
				If itt\isAnim<>0 Then SetAnimTime it\model,ReadFloat(f) : Exit
			EndIf
		Next
		it\invSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID>LastItemID Then LastItemID=it\ID
		
		If ReadByte(f)=0 Then
			it\invimg=it\itemtemplate\invimg
		Else
			it\invimg=it\itemtemplate\invimg2
		EndIf	
		
		temp = ReadByte(f)
	Wend
	
	temp = ReadByte(f)
	While temp
		For it = Each Items
			If (Not IsItemInInventory(it)) And it\invSlots > 0 Then
				For i = 0 To it\invSlots-1
					temp2 = ReadInt(f)
					If temp2 > -1 Then
						For it2 = Each Items
							If it2\ID = temp2 Then
								it\SecondInv[j] = it2
								Exit
							EndIf
						Next
					EndIf
				Next
			EndIf
		Next
		
		temp = ReadByte(f)
	Wend
	
	For fb = Each FuseBox
		fb\fuses = ReadByte(f)
	Next
	
	For ne = Each NewElevator
		ne\tofloor = ReadByte(f)
		ne\currfloor = ReadByte(f)
		y = ReadFloat(f)
		PositionEntity ne\obj, EntityX(ne\obj), y, EntityZ(ne\obj)
		ne\currsound = ReadByte(f)
		ne\state = ReadFloat(f)
		ne\door\open = ReadByte(f)
	Next
	PlayerInNewElevator = ReadByte(f)
	PlayerNewElevator = ReadByte(f)
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			dist# = 20.0
			Local closestroom.Rooms
			For r.Rooms = Each Rooms
				dist2# = EntityDistance(r\obj, do\obj)
				If dist2 < dist Then
					dist = dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	;[Block]
	;Custom map support will be added later!
	;If version = "1.3.10" Then
	;	I_Zone\Transition[0] = ReadByte(f)
	;	I_Zone\Transition[1] = ReadByte(f)
	;	I_Zone\HasCustomForest = ReadByte(f)
	;	I_Zone\HasCustomMT = ReadByte(f)
	;EndIf
	;[End Block]
	
	CloseFile f
	
	For r.Rooms = Each Rooms
		r\Adjacent[0]=Null
		r\Adjacent[1]=Null
		r\Adjacent[2]=Null
		r\Adjacent[3]=Null
		For r2.Rooms = Each Rooms
			If r<>r2 Then
				If r2\z=r\z Then
					If (r2\x)=(r\x+8.0) Then
						r\Adjacent[0]=r2
						;If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf (r2\x)=(r\x-8.0)
						r\Adjacent[2]=r2
						;If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x=r\x Then
					If (r2\z)=(r\z-8.0) Then
						r\Adjacent[1]=r2
						;If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf (r2\z)=(r\z+8.0)
						r\Adjacent[3]=r2
						;If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0]<>Null) And (r\Adjacent[1]<>Null) And (r\Adjacent[2]<>Null) And (r\Adjacent[3]<>Null) Then Exit
		Next
		
		For do.Doors = Each Doors
			If (do\KeyCard = 0) And (do\Code="") Then
				If EntityZ(do\frameobj,True)=r\z Then
					If EntityX(do\frameobj,True)=r\x+4.0 Then
						r\AdjDoor[0] = do
					ElseIf EntityX(do\frameobj,True)=r\x-4.0 Then
						r\AdjDoor[2] = do
					EndIf
				ElseIf EntityX(do\frameobj,True)=r\x Then
					If EntityZ(do\frameobj,True)=r\z+4.0 Then
						r\AdjDoor[3] = do
					ElseIf EntityZ(do\frameobj,True)=r\z-4.0 Then
						r\AdjDoor[1] = do
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	For r.Rooms = Each Rooms
		r\Adjacent[0]=Null
		r\Adjacent[1]=Null
		r\Adjacent[2]=Null
		r\Adjacent[3]=Null
		For r2.Rooms = Each Rooms
			If r<>r2 Then
				If r2\z=r\z Then
					If (r2\x)=(r\x+8.0) Then
						r\Adjacent[0]=r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf (r2\x)=(r\x-8.0)
						r\Adjacent[2]=r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x=r\x Then
					If (r2\z)=(r\z-8.0) Then
						r\Adjacent[1]=r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf (r2\z)=(r\z+8.0)
						r\Adjacent[3]=r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0]<>Null) And (r\Adjacent[1]<>Null) And (r\Adjacent[2]<>Null) And (r\Adjacent[3]<>Null) Then Exit
		Next
		
		For do.Doors = Each Doors
			If (do\KeyCard = 0) And (do\Code="") Then
				If EntityZ(do\frameobj,True)=r\z Then
					If EntityX(do\frameobj,True)=r\x+4.0 Then
						r\AdjDoor[0] = do
					ElseIf EntityX(do\frameobj,True)=r\x-4.0 Then
						r\AdjDoor[2] = do
					EndIf
				ElseIf EntityX(do\frameobj,True)=r\x Then
					If EntityZ(do\frameobj,True)=r\z+4.0 Then
						r\AdjDoor[3] = do
					ElseIf EntityZ(do\frameobj,True)=r\z-4.0 Then
						r\AdjDoor[1] = do
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
		BlinkTimer = -1
		ShouldEntitiesFall = False
		PlayerRoom = NTF_1499PrevRoom
		UpdateDoors()
		UpdateRooms()
		For it.Items = Each Items
			it\disttimer = 0
		Next
	EndIf
	
	If Collider <> 0 Then
		If PlayerRoom<>Null Then
			ShowEntity PlayerRoom\obj
		EndIf
		ShowEntity Collider
		TeleportEntity(Collider,EntityX(Collider),EntityY(Collider)+0.5,EntityZ(Collider),0.3,True)
		If PlayerRoom<>Null Then
			HideEntity PlayerRoom\obj
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0
	
	CatchErrors("Uncaught (LoadGame(" + file + "))")
End Function

Function LoadGameQuick(file$)
	CatchErrors("LoadGameQuick(" + file + ")")
	DebugLog "---------------------------------------------------------------------------"
	Local version$ = ""
	
	DebugHUD = False
	GameSaved = True
	NoTarget = False
	InfiniteStamina = False
	IsZombie% = False
	DeafPlayer% = False
	DeafTimer# = 0.0
	UnableToMove% = False
	Msg = ""
	SelectedEnding = ""
	
	PositionEntity Collider,0,1000.0,0,True
	ResetEntity Collider
	
	Local x#, y#, z#, i%, j%, temp%, temp2%, strtemp$, id%, tex%, dist#, dist2#
	Local player_x#,player_y#,player_z#, r.Rooms, n.NPCs, do.Doors, g.Guns, itt.ItemTemplates, it2.Items, n2.NPCs, it.Items, em.Emitters, fb.FuseBox, ne.NewElevator
	Local f% = ReadFile(file + "main.ntf")
	
	version = ReadString(f)
	strtemp = ReadString(f)
	strtemp = ReadString(f)
	
	DropSpeed = -0.1
	HeadDropSpeed = 0.0
	Shake = 0
	CurrSpeed = 0
	
	HeartBeatVolume = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	BlurTimer = 0
	
	KillTimer = 0
	FallTimer = 0
	MenuOpen = False
	
	GodMode = 0
	NoClip = 0
	
	PlayTime = ReadInt(f)
	
	;HideEntity Head
	HideEntity Collider
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y+0.05, z)
	;ResetEntity(Collider)
	
	ShowEntity Collider
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y+0.05, z)
	ResetEntity(Head)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0, 0)
	
	LoadPlayerData(file, f)
	
	CloseFile f
	
	f = ReadFile(file + NTF_CurrZone + ".ntf")
	
	;[Block]
	;TODO: Check if these are needed!
	;MapWidth = ReadInt(f)
	;MapHeight = ReadInt(f)
;	DebugLog MapWidth
;	DebugLog MapHeight
;	For x = 0 To MapWidth
;		For y = 0 To MapHeight
;			MapTemp[x * MapWidth + y] = ReadInt(f)
;			MapFound[x * MapWidth + y] = ReadByte(f)
;		Next
;	Next
	;[End Block]
	
	For n.NPCs = Each NPCs
		RemoveNPC(n)
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local NPCtype% = ReadByte(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				Curr173 = n
			Case NPCtypeOldMan
				Curr106 = n
			Case NPCtype096
				Curr096 = n
			Case NPCtype5131
				Curr5131 = n
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\texture = ReadString(f)
		If n\texture <> "" Then
			tex = LoadTexture_Strict (n\texture)
			TextureBlend(tex,5)
			EntityTexture n\obj, tex
		EndIf
		
		Local frame# = ReadFloat(f)
		Select NPCtype
			Case NPCtypeOldMan, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtypeZombie, NPCtypeClerk, NPCtypeD2
				SetAnimTime(n\obj, frame)
		End Select		
		
		n\Frame = frame
		
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX# = ReadFloat(f)
		n\ModelScaleY# = ReadFloat(f)
		n\ModelScaleZ# = ReadFloat(f)
		If n\Model <> ""
			n\obj = FreeEntity_Strict(n\obj)
			n\obj = LoadAnimMesh_Strict(n\Model)
			ScaleEntity n\obj,n\ModelScaleX,n\ModelScaleY,n\ModelScaleZ
			SetAnimTime n\obj,frame
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs,n\TextureID-1)
			SetAnimTime(n\obj,frame)
		EndIf
		
		Local GunID% = ReadInt(f)
		If GunID <> GUN_UNARMED Then
			If n\Gun = Null Lor n\Gun\ID <> GunID Then
				SwitchNPCGun(n, GunID)
			EndIf
			Local GunAmmo% = ReadInt(f)
			If n\Gun <> Null Then
				n\Gun\Ammo = GunAmmo
			EndIf
		ElseIf n\Gun <> Null Then
			RemoveNPCGun(n)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.NPCs = Each NPCs
				If n2<>n Then
					If n2\ID = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	For i = 0 To 6
		strtemp =  ReadString(f)
		If strtemp <> "a" Then
			For r.Rooms = Each Rooms
				If r\RoomTemplate\Name = strtemp Then
					MTFrooms[i]=r
				EndIf
			Next
		EndIf
		MTFroomState[i]=ReadInt(f)
	Next
	
	room2gw_brokendoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	;[Block]
	;Custom map support will be added later!
	;If version = CompatibleNumber Then
	;	I_Zone\Transition[0] = ReadByte(f)
	;	I_Zone\Transition[1] = ReadByte(f)
	;	I_Zone\HasCustomForest = ReadByte(f)
	;	I_Zone\HasCustomMT = ReadByte(f)
	;EndIf
	
;	temp = ReadInt(f)
;	For i = 1 To temp
;		Local roomtemplateID% = ReadInt(f)
;		Local angle% = ReadInt(f)
;		x = ReadFloat(f)
;		y = ReadFloat(f)
;		z = ReadFloat(f)
;		
;		found = ReadByte(f)
;		
;		level = ReadInt(f)
;		
;		temp2 = ReadByte(f)	
;		
;		If angle >= 360
;            angle = angle-360
;        EndIf
;		
;		For r.Rooms = Each Rooms
;			If r\x = x And r\z = z Then
;				Exit
;			EndIf
;		Next
;		
;		For x = 0 To 11
;			id = ReadInt(f)
;			If id > 0 Then
;				For n.NPCs = Each NPCs
;					If n\ID = id Then r\NPC[x]=n : Exit
;				Next
;			EndIf
;		Next
;		
;		For x=0 To 11
;			id = ReadByte(f)
;			If id=2 Then
;				Exit
;			Else If id=1 Then
;				RotateEntity(r\Levers[x], 78, EntityYaw(r\Levers[x]), 0)
;			Else
;				RotateEntity(r\Levers[x], -78, EntityYaw(r\Levers[x]), 0)
;			EndIf
;		Next
;		
;		If ReadByte(f)=1 Then ;this room has a grid
;			For y=0 To gridsz-1
;				For x=0 To gridsz-1
;					ReadByte(f) : ReadByte(f)
;				Next
;			Next
;		Else ;this grid doesn't exist in the save
;			If r\grid<>Null Then
;				For x=0 To gridsz-1
;					For y=0 To gridsz-1
;						If r\grid\Entities[x+(y*gridsz)]<>0 Then
;							r\grid\Entities[x+(y*gridsz)] = FreeEntity_Strict(r\grid\Entities[x+(y*gridsz)])
;						EndIf
;						If r\grid\waypoints[x+(y*gridsz)]<>Null Then
;							RemoveWaypoint(r\grid\waypoints[x+(y*gridsz)])
;							r\grid\waypoints[x+(y*gridsz)]=Null
;						EndIf
;					Next
;				Next
;				For x=0 To 5
;					If r\grid\Meshes[x]<>0 Then
;						r\grid\Meshes[x] = FreeEntity_Strict(r\grid\Meshes[x])
;					EndIf
;				Next
;				Delete r\grid
;				r\grid=Null
;			EndIf
;		EndIf
;		
;		If ReadByte(f)>0 Then ;this room has a forest
;			For y=0 To gridsize-1
;				For x=0 To gridsize-1
;					ReadByte(f)
;				Next
;			Next
;			lx# = ReadFloat(f)
;			ly# = ReadFloat(f)
;			lz# = ReadFloat(f)
;		ElseIf r\fr<>Null Then ;remove the old forest
;			DestroyForest(r\fr)
;			Delete r\fr
;		EndIf
;		
;		If temp2 = 1 Then PlayerRoom = r.Rooms
;	Next
	;[End Block]
	
	;Delete all non-map generated emitters
	For em = Each Emitters
		If (Not em\map_generated) Then
			RemoveEmitter(em)
		EndIf
	Next
	
	Local rx = ReadFloat(f)
	Local rz = ReadFloat(f)
	For r = Each Rooms
		r\found = ReadByte(f)
		
		If r\x = rx And r\z = rz Then
			PlayerRoom = r
		EndIf	
		
		If r\x = r1499_x# And r\z = r1499_z#
			NTF_1499PrevRoom = r
		EndIf
		
		For x = 0 To 11
			id = ReadInt(f)
			If id > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = id Then r\NPC[x]=n : Exit
				Next
			EndIf
		Next
		
		For x=0 To 10
			id = ReadByte(f)
			If id=1 Then
				RotateEntity(r\Levers[x]\obj, 78, EntityYaw(r\Levers[x]\obj), 0)
			ElseIf id=0 Then
				RotateEntity(r\Levers[x]\obj, -78, EntityYaw(r\Levers[x]\obj), 0)
			EndIf
		Next
		
		temp = ReadInt(f)
		For j = 1 To temp
			x = ReadFloat(f)
			y = ReadFloat(f)
			z = ReadFloat(f)
			id = ReadInt(f)
			em.Emitters = CreateEmitter(x, y, z, id)
			x = ReadFloat(f)
			y = ReadFloat(f)
			z = ReadFloat(f)
			RotateEntity em\Obj, x, y, z
			EntityParent(em\Obj, r\obj)
			em\Size = ReadFloat(f)
			em\SizeChange = ReadFloat(f)
			em\Speed = ReadFloat(f)
			em\RandAngle = ReadFloat(f)
		Next
	Next
	
	;InitWayPoints()
	
	temp = ReadInt(f)
	
	For i = 1 To temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local open% = ReadByte(f)
		Local openstate# = ReadFloat(f)
		Local locked% = ReadByte(f)
		Local autoclose% = ReadByte(f)
		
		Local objX# = ReadFloat(f)
		Local objZ# = ReadFloat(f)
		
		Local obj2X# = ReadFloat(f)
		Local obj2Z# = ReadFloat(f)
		
		Local timer% = ReadFloat(f)
		Local timerstate# = ReadFloat(f)
		
		Local IsElevDoor = ReadByte(f)
		Local MTFClose = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\frameobj,True) = x Then 
				If EntityZ(do\frameobj,True) = z Then	
					If EntityY(do\frameobj,True) = y 
						do\open = open
						do\openstate = openstate
						do\locked = locked
						do\AutoClose = autoclose
						do\timer = timer
						do\timerstate = timerstate
						do\IsElevatorDoor = IsElevDoor
						do\MTFClose = MTFClose
						
						PositionEntity(do\obj, objX, EntityY(do\obj), objZ, True)
						If do\obj2 <> 0 Then PositionEntity(do\obj2, obj2X, EntityY(do\obj2), obj2Z, True)
						
						Exit
					EndIf
				EndIf
			End If
		Next		
	Next
	
	Local d.Decals
	For d.Decals = Each Decals
		d\obj = FreeEntity_Strict(d\obj)
		Delete d
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		id% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		Local pitch# = ReadFloat(f)
		Local yaw# = ReadFloat(f)
		Local roll# = ReadFloat(f)
		d.Decals = CreateDecal(id, x, y, z, pitch, yaw, roll)
		d\blendmode = ReadByte (f)
		d\fx = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\lifetime = ReadFloat(f)
		
		ScaleSprite(d\obj, d\Size, d\Size)
		EntityBlend d\obj, d\blendmode
		EntityFX d\obj, d\fx
		
		DebugLog "Created Decal @"+x+","+y+","+z
	Next
	UpdateDecals()
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound <> 0 Then FreeSound_Strict e\Sound
		Delete e
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		e.Events = New Events
		e\EventName = ReadString(f)
		
		e\EventState = ReadFloat(f)
		e\EventState2 = ReadFloat(f)
		e\EventState3 = ReadFloat(f)		
		x = ReadFloat(f)
		z = ReadFloat(f)
		For r.Rooms = Each Rooms
			If EntityX(r\obj) = x And EntityZ(r\obj) = z Then
				;If e\EventName = "room2servers" Then Stop
				e\room = r
				Exit
			EndIf
		Next	
		e\EventStr = ReadString(f)
		If e\EventName = "alarm"
			;A hacky fix for the case that the intro objects aren't loaded when they should
			;Altough I'm too lazy to add those objects there because at the time where you can save, those objects are already in the ground anyway - ENDSHN
			If e\room\Objects[0]=0
				e\room\Objects[0]=CreatePivot()
				e\room\Objects[1]=CreatePivot()
			EndIf
		ElseIf e\EventName = "room860" Then
			If e\EventState = 1.0 Then
				ShowEntity e\room\fr\Forest_Pivot
			EndIf
		EndIf
	Next
	
	temp = ReadByte(f)
	While temp
		ittName$ = ReadString(f)
		tempName$ = ReadString(f)
		Name$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		red% = ReadByte(f)
		green% = ReadByte(f)
		blue% = ReadByte(f)
		a# = ReadFloat(f)
		
		it.Items = CreateItem(ittName, tempName, x, y, z, red,green,blue,a)
		it\name = Name
		
		EntityType it\collider, HIT_ITEM
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\collider, x, y, 0)
		
		it\state = ReadFloat(f)
		
		For itt.ItemTemplates = Each ItemTemplates
			If (itt\tempname = tempName) And (itt\name = ittName) Then
				If itt\isAnim<>0 Then SetAnimTime it\model,ReadFloat(f) : Exit
			EndIf
		Next
		it\invSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID>LastItemID Then LastItemID=it\ID
		
		If ReadByte(f)=0 Then
			it\invimg=it\itemtemplate\invimg
		Else
			it\invimg=it\itemtemplate\invimg2
		EndIf	
		
		temp = ReadByte(f)
	Wend
	
	temp = ReadByte(f)
	While temp
		For it = Each Items
			If (Not IsItemInInventory(it)) And it\invSlots > 0 Then
				For i = 0 To it\invSlots-1
					temp2 = ReadInt(f)
					If temp2 > -1 Then
						For it2 = Each Items
							If it2\ID = temp2 Then
								it\SecondInv[j] = it2
								Exit
							EndIf
						Next
					EndIf
				Next
			EndIf
		Next
		
		temp = ReadByte(f)
	Wend
	
	For fb = Each FuseBox
		fb\fuses = ReadByte(f)
	Next
	
	For ne = Each NewElevator
		ne\tofloor = ReadByte(f)
		ne\currfloor = ReadByte(f)
		y = ReadFloat(f)
		PositionEntity ne\obj, EntityX(ne\obj), y, EntityZ(ne\obj)
		ne\currsound = ReadByte(f)
		ne\state = ReadFloat(f)
		ne\door\open = ReadByte(f)
	Next
	PlayerInNewElevator = ReadByte(f)
	PlayerNewElevator = ReadByte(f)
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			dist# = 20.0
			Local closestroom.Rooms
			For r.Rooms = Each Rooms
				dist2# = EntityDistance(r\obj, do\obj)
				If dist2 < dist Then
					dist = dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	;This will hopefully fix the 895 crash bug after the player died by it's sanity effect and then quickloaded the game - ENDSHN
	For sc.SecurityCams = Each SecurityCams
		sc\PlayerState = 0
	Next
	;EntityTexture NVOverlay,NVTexture
	RestoreSanity = True
	
	;Custom map support will be added later!
	;If version = "1.3.10" Then
	;	I_Zone\Transition[0] = ReadByte(f)
	;	I_Zone\Transition[1] = ReadByte(f)
	;	I_Zone\HasCustomForest = ReadByte(f)
	;	I_Zone\HasCustomMT = ReadByte(f)
	;EndIf
	
	CloseFile f
	
	If Collider <> 0 Then
		If PlayerRoom<>Null Then
			ShowEntity PlayerRoom\obj
		EndIf
		ShowEntity Collider
		TeleportEntity(Collider,EntityX(Collider),EntityY(Collider)+0.5,EntityZ(Collider),0.3,True)
		If PlayerRoom<>Null Then
			HideEntity PlayerRoom\obj
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0
	
	;Free some entities that could potentially cause memory leaks (for the endings)
	;This is only required for the LoadGameQuick function, as the other one is from the menu where everything is already deleted anyways
	Local xtemp#,ztemp#
	If Sky <> 0 Then
		Sky = FreeEntity_Strict(Sky)
	EndIf
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "gatea" Then
			If r\Objects[0]<>0 Then
				r\Objects[0] = FreeEntity_Strict(r\Objects[0])
				xtemp#=EntityX(r\Objects[9],True)
				ztemp#=EntityZ(r\Objects[9],True)
				r\Objects[9] = FreeEntity_Strict(r\Objects[9])
				r\Objects[10] = 0 ;r\Objects[10] is already deleted because it is a parent object to r\Objects[9] which is already deleted a line before
				;Readding this object, as it is originally inside the "FillRoom" function but gets deleted when it loads GateA
				r\Objects[9]=CreatePivot()
				PositionEntity(r\Objects[9], xtemp#, r\y+992.0*RoomScale, ztemp#, True)
				EntityParent r\Objects[9], r\obj
				;The GateA wall pieces
				xtemp# = EntityX(r\Objects[13],True)
				ztemp# = EntityZ(r\Objects[13],True)
				r\Objects[13] = FreeEntity_Strict(r\Objects[13])
				r\Objects[13]=LoadMesh_Strict("GFX\map\gateawall1.b3d",r\obj)
				PositionEntity(r\Objects[13], xtemp#, r\y-1045.0*RoomScale, ztemp#, True)
				EntityColor r\Objects[13], 25,25,25
				EntityType r\Objects[13],HIT_MAP
				xtemp# = EntityX(r\Objects[14],True)
				ztemp# = EntityZ(r\Objects[14],True)
				r\Objects[14] = FreeEntity_Strict(r\Objects[14])
				r\Objects[14]=LoadMesh_Strict("GFX\map\gateawall2.b3d",r\obj)
				PositionEntity(r\Objects[14], xtemp#, r\y-1045.0*RoomScale, ztemp#, True)	
				EntityColor r\Objects[14], 25,25,25
				EntityType r\Objects[14],HIT_MAP
			EndIf
			r\Objects[12] = FreeEntity_Strict(r\Objects[12])
			r\Objects[17] = FreeEntity_Strict(r\Objects[17])
		ElseIf r\RoomTemplate\Name = "exit1" Then
			If r\Objects[0]<>0 Then
				xtemp# = EntityX(r\Objects[0],True)
				ztemp# = EntityZ(r\Objects[0],True)
				r\Objects[0] = FreeEntity_Strict(r\Objects[0])
				r\Objects[0] = CreatePivot(r\obj)
				PositionEntity(r\Objects[0], xtemp#, 9767.0*RoomScale, ztemp#, True)
			EndIf
		EndIf
	Next
	;Resetting some stuff (those get changed when going to the endings)
	CameraFogMode(Camera, 1)
	HideDistance# = 15.0
	
	CatchErrors("Uncaught (LoadGameQuick(" + file + "))")
End Function

Function IsItemInInventory(it.Items)
	Local i%, j%
	
	For i = 0 To MaxItemAmount-1
		If Inventory[i] <> Null Then
			If it = Inventory[i] Then
				Return True
			ElseIf Inventory[i]\invSlots > 0 Then
				For j = 0 To Inventory[i]\invSlots-1
					If it = Inventory[i]\SecondInv[j] Then
						Return True
					EndIf
				Next
			EndIf
		EndIf
	Next
	Return False
	
End Function

Type Save
	Field Name$
	Field Date$
	Field Time$
	Field Version$
End Type

Const SavePath$ = "Saves\"

Global CurrSave.Save
Global DelSave.Save

Global SaveGameAmount%

Function LoadSaveGames()
	CatchErrors("Uncaught (LoadSaveGames)")
	
	For I_SAV.Save = Each Save
		Delete I_SAV
	Next
	SaveGameAmount = 0
	
	If m_I <> Null And m_I\CurrentSave = "" Then
		m_I\CurrentSave = GetINIString(gv\OptionFile, "options", "last save")
	EndIf
	
	If FileType(SavePath)=1 Then RuntimeError "Can't create dir "+Chr(34)+SavePath+Chr(34)
	If FileType(SavePath)=0 Then CreateDir(SavePath)
	myDir=ReadDir(SavePath) 
	found% = False
	Repeat
		file$=NextFile$(myDir)
		If file$="" Then Exit 
		If FileType(SavePath+"\"+file$) = 2 Then 
			If file <> "." And file <> ".." Then 
				If (FileType(SavePath + file + "\main.ntf")=1) Then
					SaveGameAmount=SaveGameAmount+1
					Local NEW_SAV.Save = New Save
					NEW_SAV\Name = file
					Local f% = ReadFile(SavePath + file + "\main.ntf")
					NEW_SAV\Version = ReadString(f)
					NEW_SAV\Time = ReadString(f)
					NEW_SAV\Date = ReadString(f)
					CloseFile f
					If m_I <> Null And m_I\CurrentSave = file Then
						found% = True
					EndIf
				EndIf
			EndIf
		EndIf 
	Forever 
	CloseDir myDir
	
	If m_I <> Null And (Not found) Then
		m_I\CurrentSave = ""
	EndIf
	
	CatchErrors("LoadSaveGames")
End Function

Function DeleteGame(I_SAV.Save)
	I_SAV\Name = SavePath + I_SAV\Name + "\"
	Local delDir% = ReadDir(I_SAV\Name)
	If delDir <> 0 Then
		NextFile(delDir) : NextFile(delDir) ;Skipping "." and ".."
		Local file$=NextFile(delDir)
		While file<>""
			DeleteFile(I_SAV\Name + file)
			file=NextFile$(delDir)
		Wend
		CloseDir(delDir)
		DeleteDir(I_SAV\Name)
	EndIf
	Delete I_SAV
End Function

Function LoadSavedMaps()
	CatchErrors("Uncaught (LoadSavedMaps)")
	Local i, Dir, file$
	
	For i = 0 To MAXSAVEDMAPS-1
		SavedMaps[i]=""
	Next
	
	Dir=ReadDir("Map Creator\Maps") 
	i = 0
	Repeat 
		file$=NextFile$(Dir)
		
		DebugLog file
		
		If file$="" Then Exit 
		DebugLog (CurrentDir()+"Map Creator\Maps\"+file$)
		If FileType(CurrentDir()+"Map Creator\Maps\"+file$) = 1 Then 
			If file <> "." And file <> ".." Then 
				SavedMaps[i] = Left(file,Max(Len(file)-6,1))
				DebugLog i+": "+file
				i=i+1
			EndIf
			
			If i > MAXSAVEDMAPS Then Exit
		End If 
	Forever 
	CloseDir Dir 
	CatchErrors("LoadSavedMaps")
End Function

Function LoadMap(file$)
	CatchErrors("Uncaught (LoadMap)")
	Local f%, x%, y%, name$, angle%, prob#
	Local r.Rooms, rt.RoomTemplates, e.Events
	
	f% = ReadFile(file+".cbmap")
	DebugLog file+".cbmap"
	
	While Not Eof(f)
		x = ReadByte(f)
		y = ReadByte(f)
		name$ = Lower(ReadString(f))
		
		angle = ReadByte(f)*90.0
		
		DebugLog x+", "+y+": "+name
		DebugLog "angle: "+angle
		
		For rt.RoomTemplates=Each RoomTemplates
			If Lower(rt\Name) = name Then
                
                r.Rooms = CreateRoom(0, rt\Shape, (MapWidth-x) * 8.0, 0, y * 8.0, name)
				DebugLog "createroom"
				
                r\angle = angle
                If r\angle<>90 And r\angle<>270
					r\angle = r\angle + 180
				EndIf
				r\angle = WrapAngle(r\angle)
				SetupTriggerBoxes(r)
                
                TurnEntity(r\obj, 0, r\angle, 0)
                
                MapTemp[(MapWidth-x) * MapWidth + y]=1
                
                Exit
			EndIf
		Next
		
		name = ReadString(f)
		prob# = ReadFloat(f)
		
		If r<>Null Then
			If prob>0.0
				If Rnd(0.0,1.0)<=prob
					e.Events = New Events
					e\EventName = name
					e\room = r   
				EndIf
			EndIf
		EndIf
		
	Wend
	
	CloseFile f
	
	Local temp = 0, zone
	Local spacing# = 8.0
	Local shouldSpawnDoor% = False
	;For y = MapHeight - 1 To 1 Step - 1
	For y = MapHeight To 0 Step -1
		
		If y < MapHeight/3+1 Then
			zone=3
		ElseIf y < MapHeight*(2.0/3.0)-1
			zone=2
		Else
			zone=1
		EndIf
		
		;For x = 1 To MapWidth - 2
		For x = MapWidth To 0 Step -1
			If MapTemp[x * MapWidth + y] > 0 Then
				If zone = 2 Then temp=2 Else temp=0
                
                For r.Rooms = Each Rooms
					If Int(r\x/8.0)=x And Int(r\z/8.0)=y Then
						shouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								If r\angle=90
									shouldSpawnDoor = True
								EndIf
							Case ROOM2
								If r\angle=90 Lor r\angle=270
									shouldSpawnDoor = True
								EndIf
							Case ROOM2C
								If r\angle=0 Lor r\angle=90
									shouldSpawnDoor = True
								EndIf
							Case ROOM3
								If r\angle=0 Lor r\angle=180 Lor r\angle=90
									shouldSpawnDoor = True
								EndIf
							Default
								shouldSpawnDoor = True
						End Select
						If shouldSpawnDoor
							If (x+1)<(MapWidth+1)
								If MapTemp[(x + 1) * MapWidth + y] > 0 Then
									d.Doors = CreateDoor(r\zone, Float(x) * spacing + spacing / 2.0, 0, Float(y) * spacing, 90, r, Max(Rand(-3, 1), 0), temp)
									r\AdjDoor[0] = d
								EndIf
							EndIf
						EndIf
						
						shouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								If r\angle=180
									shouldSpawnDoor = True
								EndIf
							Case ROOM2
								If r\angle=0 Lor r\angle=180
									shouldSpawnDoor = True
								EndIf
							Case ROOM2C
								If r\angle=180 Lor r\angle=90
									shouldSpawnDoor = True
								EndIf
							Case ROOM3
								If r\angle=180 Lor r\angle=90 Lor r\angle=270
									shouldSpawnDoor = True
								EndIf
							Default
								shouldSpawnDoor = True
						End Select
						If shouldSpawnDoor
							If (y+1)<(MapHeight+1)
								If MapTemp[x * MapWidth + y + 1] > 0 Then
									d.Doors = CreateDoor(r\zone, Float(x) * spacing, 0, Float(y) * spacing + spacing / 2.0, 0, r, Max(Rand(-3, 1), 0), temp)
									r\AdjDoor[3] = d
								EndIf
							EndIf
						EndIf
						
						Exit
					EndIf
                Next
                
			End If
			
		Next
	Next
	
	;r = CreateRoom(0, ROOM1, 8, 0, (MapHeight-1) * 8, "173")
	;r = CreateRoom(0, ROOM1, (MapWidth-1) * 8, 0, (MapHeight-1) * 8, "pocketdimension")
	;r = CreateRoom(0, ROOM1, 0, 0, 8, "gatea")
	If IntroEnabled Then r = CreateRoom(0, ROOM1, 8, 0, (MapHeight+2) * 8, "173")
	r = CreateRoom(0, ROOM1, (MapWidth+2) * 8, 0, (MapHeight+2) * 8, "pocketdimension")
	r = CreateRoom(0, ROOM1, 0, 0, -16, "gatea")
	r = CreateRoom(0, ROOM1, -16, 800, 0, "dimension1499")
	
	CreateEvent("173", "173", 0)
	CreateEvent("pocketdimension", "pocketdimension", 0)   
	CreateEvent("gatea", "gatea", 0)
	CreateEvent("dimension1499", "dimension1499", 0)
	
	For r.Rooms = Each Rooms
		r\Adjacent[0]=Null
		r\Adjacent[1]=Null
		r\Adjacent[2]=Null
		r\Adjacent[3]=Null
		For r2.Rooms = Each Rooms
			If r<>r2 Then
				If r2\z=r\z Then
					If (r2\x)=(r\x+8.0) Then
						r\Adjacent[0]=r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf (r2\x)=(r\x-8.0)
						r\Adjacent[2]=r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x=r\x Then
					If (r2\z)=(r\z-8.0) Then
						r\Adjacent[1]=r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf (r2\z)=(r\z+8.0)
						r\Adjacent[3]=r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0]<>Null) And (r\Adjacent[1]<>Null) And (r\Adjacent[2]<>Null) And (r\Adjacent[3]<>Null) Then Exit
		Next
	Next
	
	CatchErrors("LoadMap")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D