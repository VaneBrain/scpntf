
Function UpdateEvent_682_Roar(e.Events)
	
	If e\EventState = 0 Then
		If PlayerRoom = e\room Then e\EventState = 70 * Rand(300,1000)
	ElseIf PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "testroom_860" And PlayerRoom\RoomTemplate\Name <> "cont_1123" Then
		e\EventState = e\EventState-FPSfactor
		
		If e\EventState < 17*70 Then
			If	e\EventState+FPSfactor => 17*70 Then LoadEventSound(e,"SFX\SCP\682\Roar.ogg") : e\SoundCHN = PlaySound_Strict(e\Sound)
			If e\EventState > 17*70 - 3*70 Then CameraShake = 0.5
			If e\EventState < 17*70 - 7.5*70 And e\EventState > 17*70 - 11*70 Then CameraShake = 2.0				
			If e\EventState < 70 Then 
				If e\Sound<>0 Then FreeSound_Strict (e\Sound) 
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1
;~C#Blitz3D