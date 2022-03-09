
Function UpdateEvent_ClassD_Spawn(e.Events)
	Local n.NPCs
	
	If e\room\dist < HideDistance*1.5 And e\room\dist > 2 Then
		If e\EventState = 0.0
			If Rand(0,1)=0
				n.NPCs = CreateNPC(NPCtypeD2,e\room\x,e\room\y+0.5,e\room\z)
			Else
				n.NPCs = CreateNPC(NPCtypeD2,e\room\x,e\room\y+0.5,e\room\z)
				n.NPCs = CreateNPC(NPCtypeD2,e\room\x+Rnd(1.0,1.5),e\room\y+0.5,e\room\z)
			EndIf
			e\EventState = 1.0
		Else
			RemoveEvent(e)
		EndIf
	EndIf
	
End Function

Function UpdateEvent_ClassD_Spawn_Group(e.Events)
	Local i
	
	If e\room\dist < HideDistance*1.5
		If e\EventState = 0
			If e\room\dist > 2
				e\EventState2 = Rand(2,3+(1*SelectedDifficulty\otherFactors))
				For i = 0 To e\EventState2
					e\room\NPC[i] = CreateNPC(NPCtypeD2,e\room\x+Rnd(-1.0,1.0),e\room\y+0.5,e\room\z+Rnd(-1.0,1.0))
				Next
				e\EventState = 1
			EndIf
		ElseIf e\EventState = 1
			Local suspense% = False
			For i = 0 To e\EventState2
				If e\room\NPC[i]\State = 1
					suspense = True
					Exit
				EndIf
			Next
			If suspense
				CurrOverhaul = 2
			EndIf
			Local isDead% = True
			For i = 0 To e\EventState2
				If (Not e\room\NPC[i]\IsDead)
					isDead = False
					Exit
				EndIf
			Next
			If isDead Then e\EventState = 2
		Else
			RemoveEvent(e)
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~F#1#14
;~C#Blitz3D