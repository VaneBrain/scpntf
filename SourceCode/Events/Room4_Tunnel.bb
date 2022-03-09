Function FillRoom_Room4_Tunnel(r.Rooms)
	r\Objects[0] = CreatePivot()
	PositionEntity(r\Objects[0], r\x + 1.0, 0.5, r\z + 1.0)
	EntityParent(r\Objects[0], r\obj)
End Function	

Function UpdateEvent_Room4_Tunnel(e.Events)
	
	If e\room\dist < 10.0 And e\room\dist > 0 Then
		e\room\NPC[0]=CreateNPC(NPCtypeD, EntityX(e\room\Objects[0],True), 0.5, EntityZ(e\room\Objects[0],True))
		ChangeNPCTextureID(e\room\NPC[0], 8)
		RotateEntity e\room\NPC[0]\Collider, 0, e\room\angle+65.0, 0
		SetNPCFrame e\room\NPC[0], 558
		e\room\NPC[0]\State=3
		e\room\NPC[0]\IsDead=True
		
		RemoveEvent(e)
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D