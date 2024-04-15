Include "SourceCode\Events\096_Spawn.bb"
Include "SourceCode\Events\106_Sinkhole.bb"
Include "SourceCode\Events\106_Victim.bb"
Include "SourceCode\Events\682_Roar.bb"
Include "SourceCode\Events\1048_A.bb"
Include "SourceCode\Events\ClassD_Spawns.bb"
Include "SourceCode\Events\Room_GW.bb"
Include "SourceCode\Events\Room2_Trick.bb"
Include "SourceCode\Events\Room2_Tunnel_106.bb"
Include "SourceCode\Events\Room3_Servers.bb"
Include "SourceCode\Events\Room4_Tunnel.bb"
Include "SourceCode\Events\Room4_1.bb"

Type Events
	Field EventName$
	Field room.Rooms
	
	;Change all of that shit
	Field EventState#, EventState2#, EventState3#
	Field SoundCHN%, SoundCHN2%
	Field Sound, Sound2
	Field SoundCHN_isStream%, SoundCHN2_isStream%
	
	Field EventStr$
	
	Field img%
End Type 

Function CreateEvent.Events(eventname$, roomname$, id%, prob# = 0.0)
	;roomname = the name of the room(s) you want the event to be assigned to
	
	;the id-variable determines which of the rooms the event is assigned to,
	;0 will assign it to the first generated room, 1 to the second, etc
	
	;the prob-variable can be used to randomly assign events into some rooms
	;0.5 means that there's a 50% chance that event is assigned to the rooms
	;1.0 means that the event is assigned to every room
	;the id-variable is ignored if prob <> 0.0
	
	Local i% = 0, temp%, e.Events, e2.Events, r.Rooms
	
	If prob = 0.0 Then
		For r.Rooms = Each Rooms
			If (roomname = "" Lor roomname = r\RoomTemplate\Name) Then
				temp = False
				For e2.Events = Each Events
					If e2\room = r Then temp = True : Exit
				Next
				
				i=i+1
				If i >= id And temp = False Then
					e.Events = New Events
					e\EventName = eventname					
					e\room = r
					LoadEventAssets(e)
					Return e
				EndIf
			EndIf
		Next
	Else
		For r.Rooms = Each Rooms
			If (roomname = "" Lor roomname = r\RoomTemplate\Name) Then
				temp = False
				For e2.Events = Each Events
					If e2\room = r Then temp = True : Exit
				Next
				
				If Rnd(0.0, 1.0) <= prob And temp = False Then
					e.Events = New Events
					e\EventName = eventname
					e\room = r
					LoadEventAssets(e)
				EndIf
			EndIf
		Next		
	EndIf
	
	Return Null
End Function

Function LoadEventAssets(e.Events)
	
	Select e\EventName
		Case "106_sinkhole"
			CreateEvent_106_Sinkhole(e)
		Case "room2_elevator_alt_2"
			CreateEvent_Room2_Elevator_1_Alt_2(e)
		Case "room2_elevator_2"
			CreateEvent_Room2_Elevator_2(e)
		Case "room2_gw_b"
			CreateEvent_Room2_GW_B(e)
	End Select
	
End Function

Function InitEvents()
	Local e.Events
	
	SeedRnd GenerateSeedNumber(RandomSeed)
	
	CreateEvent("cont_173", "cont_173", 0)
	
	CreateEvent("pocketdimension", "pocketdimension", 0)
	
	;there's a 7% chance that 106 appears in the rooms named "tunnel"
	CreateEvent("room2_tunnel_106", "room2_tunnel_1", 0, 0.07 + (0.1*SelectedDifficulty\aggressiveNPCs))
	
	;the chance for 173 appearing in the first lockroom is about 66%
	;there's a 30% chance that it appears in the later lockrooms
	If Rand(3)<3 Then CreateEvent("lockroom_1", "lockroom_1", 0)
	CreateEvent("lockroom_1", "lockroom_1", 0, 0.3 + (0.5*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2_trick", "room2_1", 0, 0.15)	
	
	CreateEvent("1048_a", "room2_1", 0, 0.3 + (0.3*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("1048_a", "room4_1", 0, 0.3 + (0.3*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("1048_a", "room2_tunnel_1", 0, 0.3 + (0.3*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("1048_a", "room4_tunnel", 0, 0.3 + (0.3*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2_storage", "room2_storage", 0)
	
	CreateEvent("endroom_1", "endroom_1", Rand(0,1))
	
	CreateEvent("room2_dr_offices_2", "room2_dr_offices_2", 0)
	
	CreateEvent("room2_2", "room2_2", 0, 1.0)
	
	CreateEvent("room2_elevator_1_alt_2", "room2_elevator_1", 0)
	CreateEvent("room2_elevator_1", "room2_elevator_1", Rand(1,2))
	
	CreateEvent("room3_storage", "room3_storage", 0)
	
	CreateEvent("room2_tunnel_2_smoke", "room2_tunnel_2", 0, 0.2)
	CreateEvent("room2_tunnel_2", "room2_tunnel_2", Rand(0,2))
	CreateEvent("room2_tunnel_2", "room2_tunnel_2", 0, (0.2*SelectedDifficulty\aggressiveNPCs))
	
	;173 appears in half of the "room2doors" -rooms
	CreateEvent("room2_doors", "room2_doors", 0, 0.5 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	;the anomalous duck in room2offices2-rooms
	CreateEvent("room2_offices_2", "room2_offices_2", 0, 0.7)
	
	CreateEvent("room2_closets", "room2_closets", 0)
	
	CreateEvent("room2_cafeteria", "room2_cafeteria", 0)
	
	CreateEvent("room3_pit", "room3_pit", 0)
	CreateEvent("room3_pit_1048", "room3_pit", 1)
	
	;the event that causes the door to open by itself in room2offices3
	CreateEvent("room2_offices_3", "room2_offices_3", 0, 1.0)	
	
	CreateEvent("room2_servers_1", "room2_servers_1", 0)
	
	CreateEvent("room3_servers", "room3_servers_1", 0)
	CreateEvent("room3_servers", "room3_servers_2", 0)
	
	;the dead guard
	CreateEvent("room3_tunnel","room3_tunnel", 0, 0.2)
	CreateEvent("cont_513","cont_513", 0)
	
	If gopt\GameMode = GAMEMODE_DEFAULT Then
		CreateEvent("room4_1","room4_1", 0)
	EndIf
	
	If Rand(5)<5 Then
		Select Rand(3)
			Case 1
				CreateEvent("682_roar", "room2_tunnel_1", Rand(0,2))	
			Case 2
				CreateEvent("682_roar", "room3_pit", Rand(0,2))		
			Case 3
				CreateEvent("682_roar", "room2_ez", 0)
		End Select 
	EndIf
	
	CreateEvent("testroom_2b", "testroom_2b", 0, 1.0)	
	
	CreateEvent("room2_tesla", "room2_tesla_ez", 0, 0.9)
	
	CreateEvent("room2_nuke", "room2_nuke", 0)
	
	If Rand(5) < 5 Then 
		CreateEvent("cont_895_106_spawn", "cont_895", 0)
	Else
		CreateEvent("cont_895", "cont_895", 0)
	EndIf	
	
	If Rand(2)=1 Then
		CreateEvent("106_victim", "room3_1", Rand(1,2))
		CreateEvent("106_sinkhole", "room3_2", Rand(2,3))
	Else
		CreateEvent("106_victim", "room3_2", Rand(1,2))
		CreateEvent("106_sinkhole", "room3_1", Rand(2,3))
	EndIf
	CreateEvent("106_sinkhole", "room4_1", Rand(1,2))
	
	CreateEvent("cont_079", "cont_079", 0)	
	CreateEvent("cont_049", "cont_049", 0)
	CreateEvent("cont_012", "cont_012", 0)
	CreateEvent("cont_035", "cont_035", 0)
	CreateEvent("cont_008", "cont_008", 0)
	CreateEvent("cont_106", "cont_106", 0)	
	CreateEvent("cont_372", "cont_372", 0)
	CreateEvent("cont_914", "cont_914", 0)
	CreateEvent("cont_970", "cont_970", 0)
	
	CreateEvent("toilet_guard", "room2_toilets", 1)
	CreateEvent("buttghost", "room2_toilets", 0, 0.8)
	
	CreateEvent("room2_pipes_1", "room2_pipes_1", Rand(0, 3)) 
	
	CreateEvent("room2_pit", "room2_pit", 0, 0.4 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("testroom_682", "testroom_682", 0)
	
	CreateEvent("electrical_center", "electrical_center", 0)
	
	CreateEvent("gate_a_topside", "gate_a_topside", 0)	
	CreateEvent("gate_b_topside", "gate_b_topside", 0)
	
	CreateEvent("cont_205", "cont_205", 0)
	
	CreateEvent("testroom_860","testroom_860", 0)
	
	CreateEvent("cont_966","cont_966", 0)
	
	CreateEvent("cont_1123", "cont_1123", 0, 0)
	CreateEvent("room2_tesla", "room2_tesla_lcz", 0, 0.9)
	CreateEvent("room2_tesla", "room2_tesla_hcz", 0, 0.9)
	
	CreateEvent("room1_elevators", "room1_elevators", 0)
	CreateEvent("room2_elevator_2", "room2_elevator_2", 0)
	
	;New Events in SCP:CB Version 1.3 - ENDSHN
	CreateEvent("room4_tunnel","room4_tunnel",0)
	CreateEvent("room_gw","room2_gw_a",0,1.0)
	CreateEvent("dimension_1499","dimension_1499",0)
	CreateEvent("cont_1162","cont_1162",0)
	CreateEvent("cont_500_1499","cont_500_1499",0)
	CreateEvent("room_gw","room3_gw",0,1.0)
	CreateEvent("surveil_room","surveil_room",0)
	CreateEvent("room2_medibay","room2_medibay",0)
	
	CreateEvent("room2_gw_b","room2_gw_b",Rand(0,1))
	
	CreateEvent("096_spawn","room4_pit",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room3_pit",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room2_pipes_1",0,0.4+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room2_pit",0,0.5+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room3_tunnel",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room4_tunnel",0,0.7+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room2_tunnel_1",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room2_tunnel_2",0,0.4+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096_spawn","room3_hcz",0,0.7+(0.2*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2_pit","room2_4",0,0.4 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2_offices_1","room2_offices_1",0)
	CreateEvent("room2_living_facility","room2_living_facility",0)
	
	CreateEvent("room2_pit_106", "room2_pit", 0, 0.07 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("room2_pit_body","room2_pit",1)
	
	CreateEvent("room1_archive", "room1_archive", 0, 1.0)
	
	;Redo the way Class-Ds spawn
	CreateEvent("classd_spawn","room2_1", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_2", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_3", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_4", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_5", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_tunnel_1", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","endroom_2", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_tunnel_2", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2_tunnel_3", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room2c_tunnel", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room3_1", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room3_2", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room3_3", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room4_1", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room4_2", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("classd_spawn","room3_tunnel", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	If gopt\GameMode = GAMEMODE_CLASSIC Then
		CreateEvent("classd_spawn","room4_ez", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
		CreateEvent("classd_spawn","endroom_1", 0, 0.2 + (0.1*SelectedDifficulty\aggressiveNPCs))
	EndIf	
	
	;New events in SCP:NTF
	CreateEvent("gate_a_entrance","gate_a_entrance",0)
	CreateEvent("gate_a_intro","gate_a_intro",0)
	CreateEvent("room2_maintenance","room2_maintenance",0)
	CreateEvent("cont_457","cont_457",0)
	If gopt\GameMode <> GAMEMODE_UNKNOWN Then ;Disable SZL when playing mission mode
		CreateEvent("checkpoint_ez_106","checkpoint_ez",0,1.0)
		CreateEvent("checkpoints","checkpoint_lcz",0,1.0)
		CreateEvent("checkpoints","checkpoint_hcz",0,1.0)
	EndIf
	CreateEvent("area_076","area_076", 0)
	CreateEvent("room2_offices_5", "room2_offices_5", 0)
	
	e = CreateEvent("room1_sewers", "room1_sewers", 0)
	If e <> Null And gopt\GameMode <> GAMEMODE_DEFAULT Then
		e\EventState = 8
		e\EventState2 = 1.0
	EndIf
	
	CreateEvent("testmap","testmap",0)
	
End Function

Function RemoveEvent(e.Events)
	If e\Sound<>0 Then FreeSound_Strict e\Sound
	If e\Sound2<>0 Then FreeSound_Strict e\Sound2
	If e\img<>0 Then FreeImage e\img
	Delete e
End Function

Function UpdateEvents()
	CatchErrors("UpdateEvents")
	Local dist#, i%, temp%, pvt%, strtemp$, j%, k%
	
	Local p.Particles, n.NPCs, r.Rooms, e.Events, e2.Events, it.Items, em.Emitters, sc.SecurityCams, sc2.SecurityCams
	Local rt.RoomTemplates, ne.NewElevator
	
	Local CurrTrigger$ = ""
	
	Local x#, y#, z#
	
	Local angle#
	
	CurrStepSFX = 0
	
	UpdateRooms()
	
	UpdateBiggerParticles = False
	NTF_DisableLight = False
	CoffinDistance = 1000.0
	
	For e.Events = Each Events
		If e<>Null Then
			CatchErrors(Chr(34)+e\EventName+Chr(34)+" event")
		Else
			CatchErrors("Deleted event")
		EndIf
		Select e\EventName
			Case "096_spawn"
				UpdateEvent_096_Spawn(e)
			Case "106_victim"
				UpdateEvent_106_Victim(e)
			Case "106_sinkhole"
				UpdateEvent_106_Sinkhole(e)
			Case "682_roar"
				UpdateEvent_682_Roar(e)
			Case "1048_a"
				UpdateEvent_1048_A(e)
			Case "area_076"
				UpdateEvent_Area_076(e)
			Case "buttghost"
				UpdateEvent_Buttghost(e)
			Case "checkpoint_ez_106"
				UpdateEvent_Checkpoint_EZ_106(e)
			Case "checkpoints"
				UpdateEvent_Checkpoints(e)
			Case "classd_spawn", "classd_spawn_group"
				UpdateEvent_ClassD_Spawn(e)
	;		Case "classd_spawn_group"
	;			UpdateEvent_ClassD_Spawn_Group(e)
			Case "cont_008"
				UpdateEvent_Cont_008(e)
			Case "cont_012"
				UpdateEvent_Cont_012(e)
			Case "cont_035"
				UpdateEvent_Cont_035(e)
			Case "cont_049"
				UpdateEvent_Cont_049(e)
			Case "cont_079"
				UpdateEvent_Cont_079(e)
			Case "cont_106"
				UpdateEvent_Cont_106(e)
			Case "cont_173"
				UpdateEvent_Cont_173(e)
			Case "cont_205"
				UpdateEvent_Cont_205(e)
			Case "cont_372"
				UpdateEvent_Cont_372(e)
			Case "cont_457"
				UpdateEvent_Cont_457(e)
			Case "cont_500_1499"
				UpdateEvent_Cont_500_1499(e)
			Case "cont_513"
				UpdateEvent_Cont_513(e)
			Case "cont_895","cont_895_106_spawn"
				UpdateEvent_Cont_895(e)
			Case "cont_914"
				UpdateEvent_Cont_914(e)
			Case "cont_966"
				UpdateEvent_Cont_966(e)
			Case "cont_970"
				UpdateEvent_Cont_970(e)
			Case "cont_1123"
				UpdateEvent_Cont_1123(e)
			Case "cont_1162"
				UpdateEvent_Cont_1162(e)
			Case "electrical_center"
				UpdateEvent_Electrical_Center(e)
			Case "endroom_1"
				UpdateEvent_Endroom_1(e)
			Case "gate_a_entrance"
				UpdateEvent_Gate_A_Entrance(e)
			Case "gate_a_intro"
				UpdateEvent_Gate_A_Intro(e)
			Case "gate_a_topside"
				UpdateEvent_Gate_A_Topside(e)
			Case "gate_b_entrance"
				UpdateEvent_Gate_B_Entrance(e)
			Case "gate_b_topside"
				UpdateEvent_Gate_B_Topside(e)
			Case "lockroom_1"
				UpdateEvent_Lockroom_1(e)
			Case "pocketdimension"
				UpdateEvent_Pocketdimension(e)
			Case "room_gw"
				UpdateEvent_Room_GW(e)
			Case "room1_archive"
				UpdateEvent_Room1_Archive(e)
			Case "room1_elevators"
				UpdateEvent_Room1_Elevators(e)
			Case "room1_sewers"
				UpdateEvent_Room1_Sewers(e)
			Case "room2_2"
				UpdateEvent_Room2_2(e)
			Case "room2_cafeteria"
				UpdateEvent_Room2_Cafeteria(e)
			Case "room2_closets"
				UpdateEvent_Room2_Closets(e)
			Case "room2_doors"
				UpdateEvent_Room2_Doors(e)
			Case "room2_dr_offices_2"
				UpdateEvent_Room2_Dr_Offices_2(e)
			Case "room2_elevator_1"
				UpdateEvent_Room2_Elevator_1(e)
			Case "room2_elevator_2"
				UpdateEvent_Room2_Elevator_2(e)
			Case "room2_gw_b"
				UpdateEvent_Room2_GW_B(e)
			Case "room2_living_facility"	
				UpdateEvent_Room2_Living_Facility(e)
			Case "room2_maintenance"
				UpdateEvent_Room2_Maintenance(e)
			Case "room2_medibay"
				UpdateEvent_Room2_Medibay(e)
			Case "room2_nuke"
				UpdateEvent_Room2_Nuke(e)
			Case "room2_offices_1"
				UpdateEvent_Room2_Offices_1(e)
			Case "room2_offices_2"
				UpdateEvent_Room2_Offices_2(e)
			Case "room2_offices_3"
				UpdateEvent_Room2_Offices_3(e)
			Case "room2_offices_5"
				UpdateEvent_Room2_Offices_5(e)
			Case "room2_pipes_1"
				UpdateEvent_Room2_Pipes_1(e)
			Case "room2_pit"
				UpdateEvent_Room2_Pit(e)
			Case "room2_pit_106"
				UpdateEvent_Room2_Pit_106(e)
			Case "room2_pit_body"
				UpdateEvent_Room2_Pit_Body(e)
			Case "room2_servers_1"
				UpdateEvent_Room2_Servers_1(e)
			Case "room2_tesla"
				UpdateEvent_Room2_Tesla(e)
			Case "room2_trick"
				UpdateEvent_Room2_Trick(e)
			Case "room2_tunnel_2"
				UpdateEvent_Room2_Tunnel_2(e)
			Case "room2_tunnel_106"
				UpdateEvent_Room2_Tunnel_106(e)
			Case "room2_tunnel_2_smoke"
				UpdateEvent_Room2_Tunnel_2_Smoke(e)
			Case "room3_pit"
				UpdateEvent_Room3_Pit(e)
			Case "room3_pit_1048"
				UpdateEvent_Room3_Pit_1048(e)
			Case "room3_servers"
				UpdateEvent_Room3_Servers(e)
			Case "room3_storage"
				UpdateEvent_Room3_Storage(e)
			Case "room3_tunnel"
				UpdateEvent_Room3_Tunnel(e)
			Case "room4_1"
				UpdateEvent_Room4_1(e)
			Case "room4_tunnel"
				UpdateEvent_Room4_Tunnel(e)
			Case "surveil_room"
				UpdateEvent_Surveil_Room(e)
			Case "testmap"
				UpdateEvent_Testmap(e)
			Case "testroom_2b"
				UpdateEvent_Testroom_2B(e)
			Case "testroom_682"
				UpdateEvent_Testroom_682(e)
			Case "testroom_860"
				UpdateEvent_Testroom_860(e)
			Case "toilet_guard"
				UpdateEvent_Toilet_Guard(e)
		End Select
	Next
	
	;This here is necessary because the 294 drinks with explosion effect didn't worked anymore - ENDSHN
	If ExplosionTimer > 0 Then
		ExplosionTimer = ExplosionTimer+FPSfactor
		
		If ExplosionTimer < 140.0 Then
			If ExplosionTimer-FPSfactor < 5.0 Then
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke1.ogg")
				PlaySound_Strict ExplosionSFX
				CameraShake = 10.0
				ExplosionTimer = 5.0
			EndIf
			
			CameraShake = CurveValue(ExplosionTimer/60.0,CameraShake, 50.0)
		Else
			CameraShake = Min((ExplosionTimer/20.0),20.0)
			If ExplosionTimer-FPSfactor < 140.0 Then
				BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke2.ogg")
				PlaySound_Strict ExplosionSFX				
				For i = 0 To (10+(10*(ParticleAmount+1)))
					p.Particles = CreateParticle(EntityX(Collider)+Rnd(-0.5,0.5),EntityY(Collider)-Rnd(0.2,1.5),EntityZ(Collider)+Rnd(-0.5,0.5),0, Rnd(0.2,0.6), 0.0, 350)	
					RotateEntity p\pvt,-90,0,0,True
					p\speed = Rnd(0.05,0.07)
				Next
			EndIf
			LightFlash = Min((ExplosionTimer-140.0)/10.0,5.0)
			
			If ExplosionTimer > 160 Then KillTimer = Min(KillTimer,-0.1)
			If ExplosionTimer > 500 Then ExplosionTimer = 0
			
			;a dirty workaround to prevent the collider from falling down into the facility once the nuke goes off,
			;causing the UpdateEvent function to be called again and crashing the game
			PositionEntity Collider, EntityX(Collider), 200, EntityZ(Collider)
		EndIf
	EndIf
	CatchErrors("UpdateEvents (Uncaught)")
End Function

;~IDEal Editor Parameters:
;~F#D#1C#50#5F#12B#132
;~C#Blitz3D