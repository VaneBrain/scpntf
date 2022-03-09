
Function FillRoom_Gate_A_Entrance(r.Rooms)
	
;	r\RoomDoors[0] = CreateDoor(0, r\x+744.0*RoomScale, 0, r\z+656.0*RoomScale, 90, r, True)
;	r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\open = True
;	PositionEntity(r\RoomDoors[0]\buttons[1],r\x+688*RoomScale, EntityY(r\RoomDoors[0]\buttons[1],True), r\z+512.0*RoomScale, True)
;	PositionEntity(r\RoomDoors[0]\buttons[0],r\x+784*RoomScale, EntityY(r\RoomDoors[0]\buttons[0],True), r\z+800.0*RoomScale, True)
	r\Objects[0] = CreatePivot()
	PositionEntity(r\Objects[0], r\x+1048.0*RoomScale, 0, r\z+656.0*RoomScale, True)
	EntityParent r\Objects[0], r\obj
	
	r\RoomDoors[1] = CreateDoor(r\zone, r\x, 0, r\z + 1008.0 * RoomScale, 0, r, False, True, 5)
	r\RoomDoors[1]\dir = 1 : r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\open = False
	PositionEntity(r\RoomDoors[1]\buttons[1], r\x+416*RoomScale, r\y + 0.7, r\z+1200.0*RoomScale, True)
	RotateEntity r\RoomDoors[1]\buttons[1],0,r\angle-90,0,True
	PositionEntity(r\RoomDoors[1]\buttons[0], r\x, 20.0, r\z, True)
	r\RoomDoors[1]\MTFClose = False
	
	r\Objects[2] = CreatePivot()
	PositionEntity(r\Objects[2],r\x+1184.0*RoomScale,r\y+64.0*RoomScale,r\z+640.0*RoomScale,True)
	EntityParent r\Objects[2],r\obj
	r\Objects[3] = CreatePivot()
	PositionEntity(r\Objects[3],r\x+1184.0*RoomScale,r\y+64.0*RoomScale,r\z+384.0*RoomScale,True)
	EntityParent r\Objects[3],r\obj
	
	r\Objects[4] = LoadMesh_Strict("GFX\map\rooms\gate_a_entrance\gateaentrance_new.b3d",r\obj)
	EntityPickMode r\Objects[4],2
	EntityType r\Objects[4],HIT_MAP
	EntityFX r\Objects[4], 2
	LightMesh r\Objects[4],-120,-120,-120
	
	r\Objects[5] = LoadMesh_Strict("GFX\map\alarm_siren.b3d")
	ScaleEntity r\Objects[5],RoomScale,RoomScale,RoomScale
	PositionEntity r\Objects[5],r\x,r\y+928.0*RoomScale,r\z-40.0*RoomScale,True
	EntityParent r\Objects[5],r\obj
	
	r\Objects[6] = LoadMesh_Strict("GFX\map\alarm_siren_rotor.b3d")
	ScaleEntity r\Objects[6],RoomScale,RoomScale,RoomScale
	PositionEntity r\Objects[6],r\x,r\y+928.0*RoomScale,r\z-40.0*RoomScale,True
	EntityParent r\Objects[6],r\obj
	
	r\AlarmRotor[0] = r\Objects[6]
	r\AlarmRotorLight[0] = CreateLight(3,r\Objects[6])
	MoveEntity r\AlarmRotorLight[0],0,0,0.001
	LightRange r\AlarmRotorLight[0],1.5
	LightColor r\AlarmRotorLight[0],255*3,0,0
	RotateEntity r\AlarmRotorLight[0],45,0,0
	LightConeAngles r\AlarmRotorLight[0],0,75
	
	r\Objects[7] = CreateButton(r\x + 48.0*RoomScale, r\y + 160.0 * RoomScale, r\z + 1785.0 * RoomScale, 0,0,0)
	EntityParent (r\Objects[7],r\obj)
	
	r\Objects[8] = CreateButton(r\x - 48.0*RoomScale, r\y + 160.0 * RoomScale, r\z + 1785.0 * RoomScale, 0,0,0)
	EntityParent (r\Objects[8],r\obj)
End Function

Function UpdateEvent_Gate_A_Entrance(e.Events)
	Local i%, tex%
	Local n.NPCs
	
	If PlayerRoom = e\room Then
		If e\EventState = 0 Then
			e\EventState = 1
			
			For i = 0 To 1
				n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\obj),0.25,EntityZ(e\room\obj)-1650.0*RoomScale)
				RotateEntity n\Collider,0,e\room\angle+180,0
				MoveEntity n\Collider,-i*1.5,0,0
				
				e\room\NPC[i] = n
				n\PrevX = i
				n\PrevState = (i+1)
				n\State = MTF_TOTARGET
				n\EnemyX = EntityX(n\Collider)
				n\EnemyY = 0.25
				n\EnemyZ = EntityZ(n\Collider)
				PointEntity(n\Collider,e\room\RoomDoors[1]\frameobj)
				SetNPCFrame(n,Rand(962, 1259))
			Next
			
			e\room\NPC[1]\texture = "GFX\npcs\MTF_newdiffuse_medic.jpg"
			tex = LoadTexture_Strict(e\room\NPC[1]\texture, 1, 2)
			TextureBlend(tex,5)
			EntityTexture(e\room\NPC[1]\obj, tex)
			DeleteSingleTextureEntryFromCache tex
			
			MTFtimer = 1.0
			
			Curr106\Idle = True
			Curr173\Idle = SCP173_DISABLED
			
			PlayNewDialogue(0,%01)
			
			BeginTask(TASK_OPENINV)
			BeginTask(TASK_CLICKKEYCARD)
			BeginTask(TASK_OPENDOOR)
			;Other tasks for this event are located in UpdateGUI!
		EndIf
		
		If e\EventState = 1 Then
			If e\room\RoomDoors[1]\open Then
				e\SoundCHN = PlaySound2(LoadTempSound("SFX\Door\BigDoorStartsOpenning.ogg"),Camera,e\room\RoomDoors[1]\frameobj,10,2.5)
				StopChannel(e\room\RoomDoors[1]\SoundCHN)
				e\EventState = 2
				EndTask(TASK_OPENDOOR)
			ElseIf SelectedItem <> Null And Left(SelectedItem\itemtemplate\tempname, 3) = "key" Then
				EndTask(TASK_CLICKKEYCARD)
			EndIf
		ElseIf e\EventState = 2 Then
			UpdateSoundOrigin(e\SoundCHN,Camera,e\room\RoomDoors[1]\frameobj,10,2.5)
			e\room\RoomDoors[1]\openstate = 0.01
			e\room\RoomDoors[1]\open = False
		EndIf
		If e\EventState = 2 And (Not ChannelPlaying(e\SoundCHN)) Then
			OpenCloseDoor(e\room\RoomDoors[1])
			e\EventState = 3
			PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
			e\room\NPC[0]\IdleTimer = 70*15
			e\room\NPC[0]\State = MTF_SEARCH
			e\room\NPC[1]\IdleTimer = 70*17
			e\room\NPC[1]\State = MTF_SEARCH
			mtfd\Enabled = True
		EndIf
		
		LightVolume = Min(LightVolume+0.05,TempLightVolume*1.25)
	EndIf
	
	If e\EventState = 3 And (Not IsStreamPlaying_Strict(IntercomStreamCHN)) Then
		BeginTask(TASK_CHECKPOINT)
		e\EventState = 4
	EndIf
	
	If e\room\RoomDoors[1]\openstate > 0.01 Then
		If PlayerRoom = e\room Lor IsRoomAdjacent(e\room,PlayerRoom) Then
			UpdateAlarmRotor(e\room\AlarmRotor[0],4)
			ShowEntity e\room\AlarmRotor[0]
		Else
			HideEntity e\room\AlarmRotor[0]
		EndIf
	Else
		HideEntity e\room\AlarmRotor[0]
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D