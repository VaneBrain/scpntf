;Const MAXACHIEVEMENTS = 43
;
;Type Achievement
;	Field Name$
;	Field Desc$
;	Field IMG%
;	Field Unlocked%
;End Type
;
;Global Achievements.Achievement[MAXACHIEVEMENTS]
;
;Const Achv008%=0, Achv012%=1, Achv035%=2, Achv049%=3, Achv055=4,  Achv079%=5, Achv096%=6, Achv106%=7, Achv148%=8, Achv178=9, Achv205=10
;Const Achv294%=11, Achv372%=12, Achv420%=13, Achv500%=14, Achv513%=15, Achv714%=16, Achv789%=17, Achv860%=18, Achv895%=19
;Const Achv914%=20, Achv939%=21, Achv966%=22, Achv970=23, Achv1025%=24, Achv1048=25, Achv1123=26
;
;Const AchvMaynard%=27, AchvHarp%=28, AchvSNAV%=29, AchvOmni%=30, AchvConsole%=31, AchvTesla%=32, AchvPD%=33
;
;Const Achv1162% = 34, Achv1499% = 35
;
;Const AchvKeter% = 36
;
;Global UsedConsole
;
;Global AchievementsMenu%
;Global AchvMSGenabled% = GetINIInt(gv\OptionFile, "options", "achievement popup enabled", 1)
;For i = 0 To MAXACHIEVEMENTS-1
;	Local loc2% = GetINISectionLocation(AchvIni, "s"+Str(i))
;	Local NEW_ACH.Achievement = New Achievement
;	NEW_ACH\Name = GetINIString2(AchvIni, loc2, "string1")
;	NEW_ACH\Desc = GetINIString2(AchvIni, loc2, "AchvDesc")
;	
;	Local image$ = GetINIString2(AchvIni, loc2, "image")
;	
;	NEW_ACH\IMG = LoadImage_Strict("GFX\menu\achievements\"+image+".jpg")
;	NEW_ACH\IMG = ResizeImage2(NEW_ACH\IMG,ImageWidth(NEW_ACH\IMG)*opt\GraphicHeight/768.0,ImageHeight(NEW_ACH\IMG)*opt\GraphicHeight/768.0)
;	BufferDirty ImageBuffer(NEW_ACH\IMG)
;	
;	Achievements[i] = NEW_ACH ; Doing it like this will probably save at least 5 milliseconds because array access is slower than local variable access or something
;Next
;
;Global AchvLocked = LoadImage_Strict("GFX\menu\achievements\achvlocked.jpg")
;AchvLocked = ResizeImage2(AchvLocked,ImageWidth(AchvLocked)*opt\GraphicHeight/768.0,ImageHeight(AchvLocked)*opt\GraphicHeight/768.0)
;BufferDirty ImageBuffer(AchvLocked)
Function GiveAchievement(ID%, showMessage%=True)
	Return
;	If NTF_GameModeFlag=1 Then Return
;	
;	If Achievements[achvname]\Unlocked<>True Then
;		Achievements[achvname]\Unlocked=True
;		If AchvMSGenabled And showMessage Then
;			Local loc2% = GetINISectionLocation(AchvIni, "s"+achvname)
;			Local AchievementName$ = GetINIString2(AchvIni, loc2, "string1")
;			CreateAchievementMsg(achvname,AchievementName)
;		EndIf
;	EndIf
End Function
;
;Function AchievementTooltip(achvno%)
;    Local scale# = opt\GraphicHeight/768.0
;    
;    SetFont fo\Font[Font_Digital_Small]
;    Local width = StringWidth(Achievements[achvno]\Name)
;    SetFont fo\Font[Font_Default]
;    If (StringWidth(Achievements[achvno]\Desc)>width) Then
;        width = StringWidth(Achievements[achvno]\Desc)
;    EndIf
;    width = width+20*MenuScale
;    
;    Local height = 38*scale
;    
;    Color 25,25,25
;    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,True)
;    Color 150,150,150
;    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,False)
;    SetFont fo\Font[Font_Digital_Small]
;    Text(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(35*MenuScale), Achievements[achvno]\Name, True, True)
;    SetFont fo\Font[Font_Default]
;    Text(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(55*MenuScale), Achievements[achvno]\Desc, True, True)
;End Function
;
;Function DrawAchvIMG(x%, y%, achvno%)
;	Local row%
;	Local scale# = opt\GraphicHeight/768.0
;	Local SeparationConst2 = 76 * scale
;;	If achvno >= 0 And achvno < 4 Then 
;;		row = achvno
;;	ElseIf achvno >= 3 And achvno <= 6 Then
;;		row = achvno-3
;;	ElseIf achvno >= 7 And achvno <= 10 Then
;;		row = achvno-7
;;	ElseIf achvno >= 11 And achvno <= 14 Then
;;		row = achvno-11
;;	ElseIf achvno >= 15 And achvno <= 18 Then
;;		row = achvno-15
;;	ElseIf achvno >= 19 And achvno <= 22 Then
;;		row = achvno-19
;;	ElseIf achvno >= 24 And achvno <= 26 Then
;;		row = achvno-24
;;	EndIf
;	row = achvno Mod 4
;	Color 0,0,0
;	Rect((x+((row)*SeparationConst2)), y, 64*scale, 64*scale, True)
;	If Achievements[achvno]\Unlocked Then
;		DrawImage(Achievements[achvno]\IMG,(x+(row*SeparationConst2)),y)
;	Else
;		DrawImage(AchvLocked,(x+(row*SeparationConst2)),y)
;	EndIf
;	Color 50,50,50
;	
;	Rect((x+(row*SeparationConst2)), y, 64*scale, 64*scale, False)
;End Function
;
;Global CurrAchvMSGID% = 0
;
;Type AchievementMsg
;	Field achvID%
;	Field txt$
;	Field msgx#
;	Field msgtime#
;	Field msgID%
;	Field customIMG%
;End Type
;
;Function CreateAchievementMsg.AchievementMsg(id%,txt$,customIMG$="")
;	Local amsg.AchievementMsg = New AchievementMsg
;	
;	amsg\achvID = id
;	amsg\txt = txt
;	amsg\msgx = 0.0
;	amsg\msgtime = FPSfactor2
;	If customIMG<>""
;		amsg\customIMG = LoadImage_Strict(customIMG)
;		;75x75 ;21.333333333 (64.0/3.0)
;		amsg\customIMG = ResizeImage2(amsg\customIMG,Int(ImageWidth(amsg\customIMG)/(64.0/3.0)),Int(ImageHeight(amsg\customIMG)/(64.0/3.0)))
;	EndIf
;	amsg\msgID = CurrAchvMSGID
;	CurrAchvMSGID = CurrAchvMSGID + 1
;	
;	Return amsg
;End Function
;
Function UpdateAchievementMsg()
	Return
;	Local amsg.AchievementMsg,amsg2.AchievementMsg
;	Local scale# = opt\GraphicHeight/768.0
;	Local width% = 264*scale
;	Local height% = 84*scale
;	Local x%,y%
;	
;	For amsg = Each AchievementMsg
;		If amsg\msgtime <> 0
;			If amsg\msgtime > 0.0 And amsg\msgtime < 70*7
;				amsg\msgtime = amsg\msgtime + FPSfactor2
;				If amsg\msgx > -width%
;					amsg\msgx = Max(amsg\msgx-4*FPSfactor2,-width%)
;				EndIf
;			ElseIf amsg\msgtime >= 70*7
;				amsg\msgtime = -1
;			ElseIf amsg\msgtime = -1
;				If amsg\msgx < 0.0
;					amsg\msgx = Min(amsg\msgx+4*FPSfactor2,0.0)
;				Else
;					amsg\msgtime = 0.0
;				EndIf
;			EndIf
;		Else
;			If amsg\customIMG<>0
;				FreeImage amsg\customIMG
;			EndIf
;			Delete amsg
;		EndIf
;	Next
;	
End Function
;
Function RenderAchievementMsg()
	Return
;	Local amsg.AchievementMsg,amsg2.AchievementMsg
;	Local scale# = opt\GraphicHeight/768.0
;	Local width% = 264*scale
;	Local height% = 84*scale
;	Local x%,y%
;	
;	For amsg = Each AchievementMsg
;		If amsg\msgtime <> 0
;			x=opt\GraphicWidth+amsg\msgx
;			;y=(opt\GraphicHeight-height)
;			y=0
;			For amsg2 = Each AchievementMsg
;				If amsg2 <> amsg
;					If amsg2\msgID > amsg\msgID
;						y=y+height ;-
;					EndIf
;				EndIf
;			Next
;			DrawFrame(x,y,width,height)
;			If amsg\customIMG=0
;				Color 0,0,0
;				Rect(x+10*scale,y+10*scale,64*scale,64*scale,True)
;				DrawImage(Achievements[amsg\achvID]\IMG,x+10*scale,y+10*scale)
;				Color 50,50,50
;				Rect(x+10*scale,y+10*scale,64*scale,64*scale,False)
;			Else
;				Color 0,0,0
;				Rect(x+10*scale,y+((height/2)-(ImageHeight(amsg\customIMG)/2)),ImageWidth(amsg\customIMG),ImageHeight(amsg\customIMG),True)
;				DrawImage(amsg\customIMG,x+10*scale,y+((height/2)-(ImageHeight(amsg\customIMG)/2)))
;				Color 50,50,50
;				Rect(x+10*scale,y+((height/2)-(ImageHeight(amsg\customIMG)/2)),ImageWidth(amsg\customIMG),ImageHeight(amsg\customIMG),False)
;			EndIf
;			Color 255,255,255
;			SetFont fo\Font[Font_Default]
;			If amsg\customIMG=0
;				RowText("Achievement Unlocked - "+amsg\txt,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
;			Else
;				RowText(amsg\txt,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
;			EndIf
;		Else
;			;Delete amsg
;		EndIf
;	Next
;	
End Function
;
;

;~IDEal Editor Parameters:
;~C#Blitz3D