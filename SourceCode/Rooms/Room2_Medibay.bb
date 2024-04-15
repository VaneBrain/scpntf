
Function FillRoom_Room2_Medibay(r.Rooms)
	Local it.Items
	
	r\Objects[0] = LoadRMesh("GFX\map\rooms\room2_medibay\medibay_props.rmesh",Null)
	ScaleEntity (r\Objects[0],RoomScale,RoomScale,RoomScale)
	EntityType GetChild(r\Objects[0],2), HIT_MAP
	EntityPickMode GetChild(r\Objects[0],2), 2
	PositionEntity(r\Objects[0],r\x,r\y,r\z,True)
	EntityParent(r\Objects[0], r\obj)
	
	r\Objects[1] = CreatePivot(r\obj)
	PositionEntity(r\Objects[1], r\x - 762.0 * RoomScale, r\y + 0.0 * RoomScale, r\z - 346.0 * RoomScale, True)
	r\Objects[2] = CreatePivot(r\obj)
	PositionEntity(r\Objects[2], (EntityX(r\Objects[1],True)+(126.0 * RoomScale)), EntityY(r\Objects[1],True), EntityZ(r\Objects[1],True), True)
	it = CreateItem("First Aid Kit", "firstaid", r\x - 506.0 * RoomScale, r\y + 192.0 * RoomScale, r\z - 322.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	it = CreateItem("Syringe", "syringe", r\x - 333.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 97.3 * RoomScale)
	EntityParent(it\collider, r\obj)
	it = CreateItem("Syringe", "syringe", r\x - 340.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 52.3 * RoomScale)
	EntityParent(it\collider, r\obj)
	r\RoomDoors[0] = CreateDoor(r\zone, r\x - 264.0 * RoomScale, r\y - 0.0 * RoomScale, r\z + 640.0 * RoomScale, 90, r, False, False, 3)
	
	r\Objects[3] = CreatePivot(r\obj)
	PositionEntity r\Objects[3],r\x-820.0*RoomScale,r\y,r\z-318.399*RoomScale,True
	
End Function

Function UpdateEvent_Room2_Medibay(e.Events)
	
	;e\EventState: Determines if the player has entered the room or not
	;	- 0 : Not entered
	;	- 1 : Has entered
	;e\EventState2: A timer for the zombie wake up
	
	;Hiding/Showing the props in this room
	If PlayerRoom <> e\room
		HideEntity e\room\Objects[0]
	Else
		ShowEntity e\room\Objects[0]
	EndIf
		;Setup
;		If e\EventState = 0
;			e\room\NPC[0] = CreateNPC(NPCtype008,EntityX(e\room\Objects[3],True),0.5,EntityZ(e\room\Objects[3],True))
;			RotateEntity e\room\NPC[0]\Collider,0,e\room\angle-90,0
;			e\EventState = 1
;		EndIf
;		
;		If EntityDistanceSquared(e\room\NPC[0]\Collider,Collider)<PowTwo(1.2)
;			If e\EventState2 = 0
;				LightBlink = 12.0
;				PlaySound_Strict LightSFX
;				e\EventState2 = FPSfactor
;			EndIf
;		EndIf
;	EndIf
;	
;	If e\EventState2 > 0 And e\EventState2 < 70*4
;		e\EventState2 = e\EventState2 + FPSfactor
;	ElseIf e\EventState2 >= 70*4
;		If e\room\NPC[0]\State = 0
;			e\room\NPC[0]\State = 2
;		EndIf
;	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D