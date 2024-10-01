;[Block]
Const SCP682_IDLE = 0
Const SCP682_WALK = 1
;[End Block]

Function CreateNPCtype682(n.NPCs)
	Local temp#
	Local tex%
	
	n\NVName = "SCP-682"
	n\Collider = CreatePivot()
	n\GravityMult = 0.0
	n\MaxGravity = 0.0
	EntityRadius n\Collider, 0.2
	EntityType n\Collider, HIT_PLAYER
	n\obj = LoadAnimMesh_Strict("GFX\npcs\SCP-682.b3d")
	
	temp# = 1.4 / MeshWidth(n\obj)
	ScaleEntity n\obj, temp, temp, temp
	
	MeshCullBox (n\obj, -MeshWidth(n\obj), -MeshHeight(n\obj), -MeshDepth(n\obj), MeshWidth(n\obj)*2, MeshHeight(n\obj)*2, MeshDepth(n\obj)*2)
	
	n\Speed = (IniGetFloat("Data\NPCs.ini", "SCP-939", "speed") / 100.0)
	
End Function

Function UpdateNPCtype682(n.NPCs)
	Local w.WayPoints,n2.NPCs
	Local dist#,prevFrame#,yaw#
	Local i,j
	
	Select n\State
		Case SCP682_IDLE
			;[Block]
			AnimateNPC(n, 76, 295, 0.3)
			PointEntity n\obj, Collider
			RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
			;[End Block]
		Case SCP682_WALK
			;[Block]
			AnimateNPC(n, 296, 329, 14*n\CurrSpeed)
			PointEntity n\obj, Collider
			RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 30.0), 0
			n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
			MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
			If EntityDistanceSquared(n\Collider, Collider) <= 0.5 Then
				n\State = SCP682_IDLE
			EndIf
			;[End Block]
	End Select
	
	If KeyHit(200) Then
		n\State = SCP682_WALK
	EndIf
	
	PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.65, EntityZ(n\Collider))
	
	RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D