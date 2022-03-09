Type Difficulty
	Field name$
	Field description$
	Field permaDeath%
	Field aggressiveNPCs
	Field saveType%
	Field otherFactors%
	
	Field r%
	Field g%
	Field b%
	
	Field customizable%
End Type

Global difficulties.Difficulty[4]

Global SelectedDifficulty.Difficulty

Const SAFE=0, EUCLID=1, KETER=2, ESOTERIC=3

Const SAVEANYWHERE = 0, SAVEONQUIT=1, SAVEONSCREENS=2

Const EASY = 0, NORMAL = 1, HARD = 2

difficulties[SAFE] = New Difficulty
difficulties[SAFE]\name = GetLocalString("Difficulty", "safe")
difficulties[SAFE]\description = GetLocalString("Difficulty", "safe_desc")
difficulties[SAFE]\permaDeath = False
difficulties[SAFE]\aggressiveNPCs = False
difficulties[SAFE]\saveType = SAVEANYWHERE
difficulties[SAFE]\otherFactors = EASY
difficulties[SAFE]\r = 120
difficulties[SAFE]\g = 150
difficulties[SAFE]\b = 50

difficulties[EUCLID] = New Difficulty
difficulties[EUCLID]\name = GetLocalString("Difficulty", "euclid")
difficulties[EUCLID]\description = GetLocalString("Difficulty", "euclid_desc")
difficulties[EUCLID]\permaDeath = False
difficulties[EUCLID]\aggressiveNPCs = False
difficulties[EUCLID]\saveType = SAVEONSCREENS
difficulties[EUCLID]\otherFactors = NORMAL
difficulties[EUCLID]\r = 200
difficulties[EUCLID]\g = 200
difficulties[EUCLID]\b = 0

difficulties[KETER] = New Difficulty
difficulties[KETER]\name = GetLocalString("Difficulty", "keter")
difficulties[KETER]\description = GetLocalString("Difficulty", "keter_desc")
difficulties[KETER]\permaDeath = True
difficulties[KETER]\aggressiveNPCs = True
difficulties[KETER]\saveType = SAVEONQUIT
difficulties[KETER]\otherFactors = HARD
difficulties[KETER]\r = 200
difficulties[KETER]\g = 0
difficulties[KETER]\b = 0

difficulties[ESOTERIC] = New Difficulty
difficulties[ESOTERIC]\name = GetLocalString("Difficulty", "esoteric")
difficulties[ESOTERIC]\permaDeath = False
difficulties[ESOTERIC]\aggressiveNPCs = True
difficulties[ESOTERIC]\saveType = SAVEANYWHERE
difficulties[ESOTERIC]\customizable = True
difficulties[ESOTERIC]\otherFactors = EASY
difficulties[ESOTERIC]\r = 255
difficulties[ESOTERIC]\g = 255
difficulties[ESOTERIC]\b = 255

SelectedDifficulty = difficulties[SAFE]
;~IDEal Editor Parameters:
;~F#0
;~C#Blitz3D