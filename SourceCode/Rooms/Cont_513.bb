
Function FillRoom_Cont_513(r.Rooms)
	Local d.Doors,sc.SecurityCams,it.Items
	
	r\RoomDoors[0] = CreateDoor(r\zone, r\x - 704.0 * RoomScale, 0, r\z + 304.0 * RoomScale, 0, r, False, 0, 2)
	r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\open = True
	PositionEntity (r\RoomDoors[0]\buttons[0], EntityX(r\RoomDoors[0]\buttons[0],True), EntityY(r\RoomDoors[0]\buttons[0],True), r\z + 288.0 * RoomScale, True)
	PositionEntity (r\RoomDoors[0]\buttons[1], EntityX(r\RoomDoors[0]\buttons[1],True), EntityY(r\RoomDoors[0]\buttons[1],True), r\z + 320.0 * RoomScale, True)
	
	r\RoomDoors[1] = CreateDoor(r\zone, r\x - 1024.0 * RoomScale, 0, r\z, 90, r)
	r\RoomDoors[1]\locked = True : r\RoomDoors[1]\DisableWaypoint = True
	
	sc.SecurityCams = CreateSecurityCam(r\x-312.0 * RoomScale, r\y + 414*RoomScale, r\z + 656*RoomScale, r)
	sc\FollowPlayer = True
	
	it = CreateItem("SCP-513", "scp513", r\x - 32.0 * RoomScale, r\y + 196.0 * RoomScale, r\z + 688.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	it = CreateItem("Blood-stained Note", "paper", r\x + 736.0 * RoomScale,1.0, r\z + 48.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	it = CreateItem("Document SCP-513", "paper", r\x - 480.0 * RoomScale, 104.0*RoomScale, r\z - 176.0 * RoomScale)
	EntityParent(it\collider, r\obj)
	
	r\Objects[0] = LoadMesh_Strict("GFX\map\props\513_jelly.b3d")
	ScaleEntity r\Objects[0],RoomScale,RoomScale,RoomScale
	PositionEntity r\Objects[0],r\x,r\y+160.72*RoomScale,r\z+666.76*RoomScale,True
	RotateEntity r\Objects[0],0,r\angle,0
	EntityAlpha r\Objects[0],0.65
	EntityParent r\Objects[0],r\obj
	
End Function

Function UpdateEvent_Cont_513(e.Events)
	Local it.Items
	
	TFormPoint -704.0, 0.0, -212.0, e\room\obj, 0
	e\room\NPC[0]=CreateNPC(NPCtypeGuard, TFormedX(), 0.5, TFormedZ())
	PointEntity e\room\NPC[0]\Collider, e\room\RoomDoors[0]\frameobj
	RotateEntity e\room\NPC[0]\Collider, 0, EntityYaw(e\room\NPC[0]\Collider)+Rnd(-20,20),0, True
	SetNPCFrame (e\room\NPC[0], 287)
	e\room\NPC[0]\State = 8
	RemoveNPCGun(e\room\NPC[0])
	
	Select Rand(4)
		Case 1
			it = CreateItem("USP Tactical", "usp", TFormedX()-0.5, 0.5, TFormedZ()+0.1)
			it\state = Rand(1,8)
			it\state2 = Rand(1,2)
		Case 2
			it = CreateItem("M9 Beretta", "beretta", TFormedX()-0.5, 0.5, TFormedZ()+0.1)
			it\state = Rand(1,8)
			it\state2 = Rand(1,2)
		Case 3
			it = CreateItem("MP5K", "mp5k", TFormedX()-0.5, 0.5, TFormedZ()+0.1)
			it\state = Rand(12,24)
			it\state2 = Rand(1,2)
		Case 4
			it = CreateItem("FN P90", "p90", TFormedX()-0.5, 0.5, TFormedZ()+0.1)
			it\state = Rand(12,24)
			it\state2 = Rand(1,2)
	End Select
	
	RotateEntity it\collider,EntityPitch(it\collider),45,EntityRoll(it\collider),True
	EntityType it\collider, HIT_ITEM
	
	RemoveEvent(e)
	
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D