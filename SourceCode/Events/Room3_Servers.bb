
Function UpdateEvent_Room3_Servers(e.Events)
	Local temp%
	
	If PlayerRoom = e\room Then
		If e\EventState3=0 And Curr173\Idle = 0 Then
			If BlinkTimer < -10 Then 
				temp = Rand(0,2)
				PositionEntity Curr173\Collider, EntityX(e\room\Objects[temp],True),EntityY(e\room\Objects[temp],True),EntityZ(e\room\Objects[temp],True)
				ResetEntity Curr173\Collider
				e\EventState3=1
			EndIf
		EndIf
		
		If e\room\Objects[3]<>0 Then 
			If BlinkTimer<-8 And BlinkTimer >-12 Then
				PointEntity e\room\Objects[3], Camera
				RotateEntity(e\room\Objects[3], 0, EntityYaw(e\room\Objects[3],True),0, True)
			EndIf
			If e\EventState2 = 0 Then 
				e\EventState = CurveValue(0, e\EventState, 15.0)
				If Rand(800)=1 Then e\EventState2 = 1
			Else
				e\EventState = e\EventState+(FPSfactor*0.5)
				If e\EventState > 360 Then e\EventState = 0	
				
				If Rand(1200)=1 Then e\EventState2 = 0
			EndIf
			
			PositionEntity e\room\Objects[3], EntityX(e\room\Objects[3],True), (-608.0*RoomScale)+0.05+Sin(e\EventState+270)*0.05, EntityZ(e\room\Objects[3],True), True
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D