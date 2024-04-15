
Function UpdateEvent_Room4_1(e.Events)
	Local n.NPCs
	
	If e\EventState = 0 Then
		If PlayerRoom <> e\room Then
			If DistanceSquared(EntityX(Collider),EntityX(e\room\obj),EntityZ(Collider),EntityZ(e\room\obj)) < 64.0 Then
				For n.NPCs = Each NPCs
					If n\NPCtype = NPCtype049 Then
						If n\State = SCP049_ACTIVE And EntityDistanceSquared(Collider,n\Collider) > 256.0 Then
							TFormPoint(368, 528, 176, e\room\obj, 0)
							TeleportEntity(n\Collider, TFormedX(), TFormedY(), TFormedZ(), n\CollRadius, True)
							n\State = SCP049_NULL
							n\State2 = 0
							n\State3 = 0
							n\Idle = 0
							n\PathStatus = 0
							e\room\NPC[0] = n
							e\EventState = 1
						EndIf
						Exit
					EndIf
				Next
			EndIf
		EndIf
	Else
		AnimateNPC(e\room\NPC[0], 18, 19, 0.005)
		PointEntity e\room\NPC[0]\obj, Collider	
		RotateEntity e\room\NPC[0]\Collider, 0, CurveAngle(EntityYaw(e\room\NPC[0]\obj), EntityYaw(e\room\NPC[0]\Collider), 45.0), 0
		If DistanceSquared(EntityX(Collider),EntityX(e\room\obj),EntityZ(Collider),EntityZ(e\room\obj)) > 256.0 Then
			TeleportEntity(e\room\NPC[0]\Collider, EntityX(e\room\obj), 0.5, EntityZ(e\room\obj), e\room\NPC[0]\CollRadius, True)
			e\room\NPC[0]\State = SCP049_ACTIVE
			RemoveEvent(e)
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D