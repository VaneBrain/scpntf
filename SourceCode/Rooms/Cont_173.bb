
Function FillRoom_Cont_173(r.Rooms)
	Local d.Doors,de.Decals,sc.SecurityCams
	Local i
	
	;the containment doors
	r\RoomDoors[1] = CreateDoor(r\zone, r\x + 4000.0 * RoomScale, 384.0*RoomScale, r\z + 1696.0 * RoomScale, 90, r, True, True)
	r\RoomDoors[1]\locked = False : r\RoomDoors[1]\AutoClose = False
	r\RoomDoors[1]\dir = 1 : r\RoomDoors[1]\open = True 
	r\RoomDoors[1]\buttons[0] = FreeEntity_Strict(r\RoomDoors[1]\buttons[0])
	r\RoomDoors[1]\buttons[1] = FreeEntity_Strict(r\RoomDoors[1]\buttons[1])
	r\RoomDoors[1]\MTFClose = False
	
	r\RoomDoors[2] = CreateDoor(r\zone, r\x + 2704.0 * RoomScale, 384.0*RoomScale, r\z + 624.0 * RoomScale, 90, r, True)
	r\RoomDoors[2]\AutoClose = False : r\RoomDoors[2]\open = False
	r\RoomDoors[2]\buttons[0] = FreeEntity_Strict(r\RoomDoors[2]\buttons[0])
	r\RoomDoors[2]\buttons[1] = FreeEntity_Strict(r\RoomDoors[2]\buttons[1])
	r\RoomDoors[2]\MTFClose = False
	
	d.Doors = CreateDoor(r\zone, r\x + 1392.0 * RoomScale, 384.0*RoomScale, r\z + 64.0 * RoomScale, 90, r, True)
	d\AutoClose = False
	d\MTFClose = False
	d\locked = True
	
	d.Doors = CreateDoor(r\zone, r\x - 640.0 * RoomScale, 384.0*RoomScale, r\z + 64.0 * RoomScale, 90, r, False)
	d\locked = True : d\AutoClose = False
	
	d.Doors = CreateDoor(r\zone, r\x + 1280.0 * RoomScale, 384.0*RoomScale, r\z + 312.0 * RoomScale, 180, r, True)
	d\locked = True : d\AutoClose = False
	PositionEntity(d\buttons[0], r\x + 1120.0 * RoomScale, EntityY(d\buttons[0],True), r\z + 328.0 * RoomScale, True)
	PositionEntity(d\buttons[1], r\x + 1120.0 * RoomScale, EntityY(d\buttons[1],True), r\z + 296.0 * RoomScale, True)
	d\obj2 = FreeEntity_Strict(d\obj2)
	d\MTFClose = False
	
	d.Doors = CreateDoor(r\zone, r\x, 0, r\z + 1184.0 * RoomScale, 0, r, False)
	d\locked = True
	
;	r\Objects[0] = LoadMesh_Strict("GFX\map\IntroDesk.b3d")
;	ScaleEntity r\Objects[0], RoomScale, RoomScale ,RoomScale
;	PositionEntity r\Objects[0], r\x + 272.0 * RoomScale, 0, r\z + 400.0 * RoomScale
;	EntityParent r\Objects[0], r\obj
	
	de.Decals = CreateDecal(DECAL_DECAY, r\x + 272.0 * RoomScale, 0.005, r\z + 262.0 * RoomScale, 90, Rand(360), 0)
	EntityParent(de\obj, r\obj)
	
;	r\Objects[1] = LoadMesh_Strict("GFX\map\IntroDrawer.b3d")
;	ScaleEntity r\Objects[1], RoomScale, RoomScale ,RoomScale
;	PositionEntity r\Objects[1], r\x + 448.0 * RoomScale, 0, r\z + 192.0 * RoomScale
;	EntityParent r\Objects[1], r\obj
	
	de.Decals = CreateDecal(DECAL_DECAY, r\x + 456.0 * RoomScale, 0.005, r\z + 135.0 * RoomScale, 90, Rand(360), 0)
	EntityParent(de\obj, r\obj)
	
	sc.SecurityCams = CreateSecurityCam(r\x - 336.0 * RoomScale, r\y + 352 * RoomScale, r\z + 48.0 * RoomScale, r, True)
	sc\angle = 270
	sc\turn = 45
	sc\room = r
	TurnEntity(sc\CameraObj, 20, 0, 0)
	EntityParent(sc\obj, r\obj)
	
	PositionEntity(sc\ScrObj, r\x + 1456 * RoomScale, 608 * RoomScale, r\z +352.0 * RoomScale)
	TurnEntity(sc\ScrObj, 0, 90, 0)
	EntityParent(sc\ScrObj, r\obj)
	
	r\Objects[2] = CreatePivot()
	PositionEntity (r\Objects[2], EntityX(r\obj) + 40.0 * RoomScale, 460.0 * RoomScale, EntityZ(r\obj) + 1072.0 * RoomScale)
	r\Objects[3] = CreatePivot()
	PositionEntity (r\Objects[3], EntityX(r\obj) - 80.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\obj) + 526.0 * RoomScale)
	r\Objects[4] = CreatePivot()
	PositionEntity (r\Objects[4], EntityX(r\obj) - 128.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\obj) + 320.0 * RoomScale)
	
	r\Objects[5] = CreatePivot()
	PositionEntity (r\Objects[5], EntityX(r\obj) + 660.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\obj) + 526.0 * RoomScale)
	r\Objects[6] = CreatePivot()
	PositionEntity (r\Objects[6], EntityX(r\obj) + 700 * RoomScale, 100.0 * RoomScale, EntityZ(r\obj) + 320.0 * RoomScale)
	
	r\Objects[7] = CreatePivot()
	PositionEntity (r\Objects[7], EntityX(r\obj) + 1472.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\obj) + 912.0 * RoomScale)
	
	For i = 2 To 7
		EntityParent(r\Objects[i], r\obj)
	Next
	
End Function

Function UpdateEvent_Cont_173(e.Events)
	
	If e\EventState = 1 Then
		If EntityDistanceSquared(Curr173\Collider, Collider)>PowTwo(8.0) Then
			e\room\RoomDoors[1]\open = False
			PlayAnnouncement("SFX\Character\MTF\Announc173Contain.ogg")
			e\EventState = 2
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D