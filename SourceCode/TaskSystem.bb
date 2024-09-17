
;Task Constants
Const TASK_STATUS_NEW = 0
Const TASK_STATUS_ALREADY = 1
Const TASK_STATUS_END = 2
Const TASK_STATUS_FAILED = 3
Const TASK_RENDER_TIME = 70*5

;Tasks themselves
;Intro tasks
Const TASK_OPENINV = 0
Const TASK_CLICKKEYCARD = 1
Const TASK_OPENDOOR = 2
Const TASK_ELECLOCATE = 3
Const TASK_ELECFOUND = 4
Const TASK_CHECKPOINT = 5
;Checkpoint tasks (story mode)
Const TASK_FIXELEVATOR = 6
;Sewers tasks (story mode)
Const TASK_FINDWEAPON = 7
Const TASK_FINDFUSE = 8
Const TASK_FINDFUSEBOX = 9
;Checkpoint tasks (classic mode)
Const TASK_GOTOZONE = 10
Const TASK_CONTAIN173 = 11
Const TASK_CONTAIN106 = 12
Const TASK_173TOCHAMBER = 13
Const TASK_106RECALL = 14


Type NewTask
	Field ID%
	Field txt$
	Field Status%
	Field Timer#
End Type

 Function BeginTask.NewTask(ID%)
	Local t.NewTask
	
	For t = Each NewTask
		If t\ID = ID Then
			Return t
		EndIf
	Next
	
	t = New NewTask
	t\ID = ID
	Select t\ID
		Case TASK_OPENINV
			t\txt = GetLocalStringR("Tasks", "open_inv", KeyName[KEY_INV])
		Case TASK_CLICKKEYCARD
			t\txt = GetLocalString("Tasks", "click_keycard")
		Case TASK_OPENDOOR
			t\txt = GetLocalStringR("Tasks", "click_button", KeyName[KEY_USE])
		Case TASK_CHECKPOINT
			t\txt = GetLocalString("Tasks", "to_checkpoint")
		Case TASK_FIXELEVATOR
			t\txt = GetLocalString("Tasks", "fix_elevator")
		Case TASK_FINDWEAPON
			t\txt = GetLocalString("Tasks", "find_weapon")
		Case TASK_FINDFUSE
			t\txt = GetLocalString("Tasks", "find_fuse")
		Case TASK_FINDFUSEBOX
			t\txt = GetLocalString("Tasks", "find_fusebox")	
		Case TASK_GOTOZONE
			t\txt = GetLocalString("Tasks", "to_zone")
		Case TASK_CONTAIN173
			t\txt = GetLocalString("Tasks", "contain_173")
		Case TASK_CONTAIN106
			t\txt = GetLocalString("Tasks", "contain_106")
		Case TASK_173TOCHAMBER
			t\txt = GetLocalString("Tasks", "173_tochamber")
		Case TASK_106RECALL
			t\txt = GetLocalString("Tasks", "106_recall")
		Case TASK_ELECLOCATE
			t\txt = GetLocalString("Tasks", "elec_locate")
		Case TASK_ELECFOUND
			t\txt = GetLocalString("Tasks", "elec_found")
	End Select
	t\Timer = TASK_RENDER_TIME
	t\Status = TASK_STATUS_NEW
	
	Return t
End Function

Function TaskExists(ID%)
	Local t.NewTask
	
	For t = Each NewTask
		If t\ID = ID Then
			Return True
		EndIf
	Next
	
	Return False
End Function

Function EndTask(ID%)
	Local t.NewTask
	
	For t = Each NewTask
		If t\ID = ID Then
			If t\Status <> TASK_STATUS_END And t\Status <> TASK_STATUS_FAILED Then
				t\Status = TASK_STATUS_END
				t\Timer = TASK_RENDER_TIME
			EndIf
			Exit
		EndIf
	Next
	
End Function

Function FailTask(ID%)
	Local t.NewTask
	
	For t = Each NewTask
		If t\ID = ID Then
			If t\Status <> TASK_STATUS_END And t\Status <> TASK_STATUS_FAILED Then
				t\Status = TASK_STATUS_FAILED
				t\Timer = TASK_RENDER_TIME
			EndIf
			Exit
		EndIf
	Next
	
End Function

Function UpdateTasks()
	Local t.NewTask
	Local hasEndTask% = False
	Local hasFailedTask% = False
	
	For t = Each NewTask
		If t\Status = TASK_STATUS_FAILED Then
			hasFailedTask = True
			hasEndTask = False
		ElseIf t\Status = TASK_STATUS_END And (Not hasFailedTask) Then
			hasEndTask = True
			Exit
		EndIf
	Next
	
	For t = Each NewTask
		If (hasEndTask And t\Status = TASK_STATUS_END) Lor (hasFailedTask And t\Status = TASK_STATUS_FAILED) Lor ((Not hasEndTask) And (Not hasFailedTask) And t\Status = TASK_STATUS_NEW) Then
			If t\Timer > 0.0 Then
				t\Timer = t\Timer - FPSfactor
				If t\Timer <= 0.0 Then
					t\Timer = 0.0
					If t\Status = TASK_STATUS_END Lor t\Status = TASK_STATUS_FAILED Then
						Delete t
					Else
						t\Status = TASK_STATUS_ALREADY
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
End Function

Function DrawTasks()
	Local t.NewTask
	Local a#, x#, y#, hasNewTask%, hasEndTask%, hasFailedTask%, globalTime#, txt$, atLeastOneTask%
	
	If HUDenabled Lor InvOpen Then
		hasNewTask = False
		globalTime = 0.0
		x = opt\GraphicWidth / 2
		y = opt\GraphicHeight / 4
		
		For t = Each NewTask
			If t\Status = TASK_STATUS_FAILED Then
				hasFailedTask = True
				hasEndTask = False
				hasNewTask = False
			ElseIf t\Status = TASK_STATUS_END And (Not hasFailedTask) Then
				hasEndTask = True
				hasNewTask = False
			ElseIf t\Status = TASK_STATUS_NEW And (Not hasEndTask) And (Not hasFailedTask) Then
				hasNewTask = True
			EndIf
		Next
		For t = Each NewTask
			If (t\Status = TASK_STATUS_FAILED And hasFailedTask) Lor (t\Status = TASK_STATUS_END And hasEndTask) Lor (t\Status = TASK_STATUS_NEW And hasNewTask) Then
				If t\Timer > globalTime Then
					globalTime = t\Timer
				EndIf
			EndIf
		Next
		
		If hasEndTask Lor hasFailedTask Lor hasNewTask Lor InvOpen Then
			If InvOpen Then
				txt = GetLocalString("Tasks", "inv_task")
			ElseIf hasFailedTask Then
				txt = GetLocalString("Tasks", "failed_task")
			ElseIf hasEndTask Then
				txt = GetLocalString("Tasks", "end_task")
			Else
				txt = GetLocalString("Tasks", "new_task")
			EndIf
			SetFont fo\Font[Font_Default_Medium]
			Color 0, 0, 0
			Text x + 1, y + 1, Upper(txt), True, False
			
			If InvOpen Then
				a = 255
			Else
				a = Min(globalTime / 2, 255)
			EndIf
			Color a, a * ((Not hasFailedTask) Lor (InvOpen)), a * ((Not hasFailedTask) Lor (InvOpen))
			Text x, y, Upper(txt), True, False
			
			y = y + 40.0
			SetFont fo\Font[Font_Default]
			
			atLeastOneTask = False
			For t = Each NewTask
				If (InvOpen And t\Status <> TASK_STATUS_END) Lor ((Not InvOpen) And ((hasEndTask And t\Status = TASK_STATUS_END) Lor (hasFailedTask And t\Status = TASK_STATUS_FAILED) Lor (hasNewTask And t\Status = TASK_STATUS_NEW))) Then
					If t\Timer > 0.0 Lor InvOpen Then
						Color 0, 0, 0
						Text x + 1, y + 1, t\txt, True, False
						
						If InvOpen Then
							a = 255
						Else
							a = Min(t\Timer / 2, 255)
						EndIf
						Color a, a, a
						Text x, y, t\txt, True, False
						
						y = y + 20.0
						atLeastOneTask = True
					EndIf
				EndIf
			Next
			
			If InvOpen And (Not atLeastOneTask) Then
				txt = GetLocalString("Tasks", "none")
				
				Color 0, 0, 0
				Text x + 1, y + 1, txt, True, False
				
				Color 255, 255, 255
				Text x, y, txt, True, False
			EndIf
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D
