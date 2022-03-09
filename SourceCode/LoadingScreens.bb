
Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
InitLoadingScreens("Loadingscreens\loadingscreens.ini")

Const LOADINGSCREEN_MAX_IMG% = 3
Const LOADINGSCREEN_MAX_TXT% = 5

Type LoadingScreens
	Field imgpath$[LOADINGSCREEN_MAX_IMG]
	Field img%[LOADINGSCREEN_MAX_IMG], imgamount%
	Field ID%
	Field title$
	Field txt$[LOADINGSCREEN_MAX_TXT], txtamount%
	Field rotImg%
	Field cam%
	Field percent%
	Field noAuto%
End Type

Function InitLoadingScreens(filename$)
	If I_Loc\Localized And FileType(I_Loc\LangPath + filename$)=1 Then
		filename = I_Loc\LangPath + filename
	EndIf
	Local TemporaryString$, i%
	Local ls.LoadingScreens
	Local file$ = filename
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			ls.LoadingScreens = New LoadingScreens
			LoadingScreenAmount=LoadingScreenAmount+1
			ls\ID = LoadingScreenAmount
			
			ls\title = TemporaryString
			For i = 0 To (LOADINGSCREEN_MAX_IMG-1)
				ls\imgpath[i] = GetINIString(file, TemporaryString, "image"+(i+1))
				If ls\imgpath[i]<> "" Then ls\imgamount=ls\imgamount+1
			Next
			
			For i = 0 To (LOADINGSCREEN_MAX_TXT-1)
				ls\txt[i] = GetINIString(file, TemporaryString, "text"+(i+1))
				If ls\txt[i]<> "" Then ls\txtamount=ls\txtamount+1
			Next
			
			ls\noAuto = GetINIInt(file, TemporaryString, "disable auto selection")
		EndIf
	Wend
	
	CloseFile f
End Function

Function DrawLoading(percent%, shortloading=False, customloadingscreen$="")
	CatchErrors("Function DrawLoading (" + percent + ", " + shortloading + ", " + customloadingscreen + ")")
	Local ls.LoadingScreens
	Local x%, y%, width%, height%
	Local strtemp$, i%
	
	Local temp%, firstloop%, isSelected%
	
	If percent = 0 Then
		LoadingScreenText = 0
		If shortloading = False Then
			If customloadingscreen = "" Then
				isSelected = False
				While (Not isSelected)
					temp = Rand(1, LoadingScreenAmount)
					For ls = Each LoadingScreens
						If ls\ID = temp Then
							If (Not ls\noAuto) Then
								For i = 0 To (ls\imgamount-1)
									If ls\img[i] = 0 Then ls\img[i] = LoadImage_Strict("Loadingscreens\"+ls\imgpath[i])
									ls\img[i] = ResizeImage2(ls\img[i], ImageWidth(ls\img[i]) * MenuScale, ImageHeight(ls\img[i]) * MenuScale)
								Next
								SelectedLoadingScreen = ls
								isSelected = True
							EndIf
							Exit
						EndIf
					Next
				Wend
			Else
				For ls = Each LoadingScreens
					If ls\title = customloadingscreen Then
						For i = 0 To (ls\imgamount-1)
							If ls\img[i] = 0 Then ls\img[i] = LoadImage_Strict("Loadingscreens\"+ls\imgpath[i])
							ls\img[i] = ResizeImage2(ls\img[i], ImageWidth(ls\img[i]) * MenuScale, ImageHeight(ls\img[i]) * MenuScale)
						Next
						SelectedLoadingScreen = ls
						Exit
					EndIf
				Next
			EndIf
		Else ;short loading - don't load the image and just use first loading screen.
			SelectedLoadingScreen = First LoadingScreens
		EndIf
		If SelectedLoadingScreen = Null Then
			Return
		EndIf
		SelectedLoadingScreen\cam = CreateCamera()
		CameraRange SelectedLoadingScreen\cam,0.99,1.01
		SelectedLoadingScreen\rotImg = LoadSprite("GFX\menu\LoadingIcon.png",1+2,SelectedLoadingScreen\cam)
		ScaleSprite(SelectedLoadingScreen\rotImg, 0.05, 0.05)
		EntityOrder(SelectedLoadingScreen\rotImg,-100)
		PositionEntity SelectedLoadingScreen\rotImg,0.9,-0.45-((0.5/(Float(opt\GraphicWidth)/Float(opt\GraphicHeight)/(16.0/9.0)))-0.5),1.0
		SpriteViewMode(SelectedLoadingScreen\rotImg,2)
	EndIf
	
	If SelectedLoadingScreen = Null Then
		Return
	EndIf
	
	If percent < SelectedLoadingScreen\percent Then
		percent = SelectedLoadingScreen\percent
	Else
		SelectedLoadingScreen\percent = percent
	EndIf
	
	firstloop = True
	Repeat
		
		TurnEntity(SelectedLoadingScreen\rotImg, 0, 0, -5)
		
		ClsColor 0,0,0
		Cls
		
		Rect 0,0,opt\GraphicWidth,opt\GraphicHeight
		
		CameraProjMode(SelectedLoadingScreen\cam,1)
		If Camera<>0 Then
			HideEntity Camera
		EndIf
		If m3d<>Null And m_I<>Null Then
			If m_I\Cam<>0 Then
				HideEntity m_I\Cam
			EndIf
			If m3d\Scene<>0 Then
				HideEntity m3d\Scene
			EndIf
		EndIf
		CameraViewport(SelectedLoadingScreen\cam,0,0,opt\GraphicWidth,opt\GraphicHeight)
		RenderWorld()
		CameraProjMode SelectedLoadingScreen\cam,0
		
		If (Not shortloading) Then
			
			If percent > (100.0 / SelectedLoadingScreen\txtamount)*(LoadingScreenText+1) Then
				LoadingScreenText=LoadingScreenText+1
			EndIf
			
			For i = 0 To (SelectedLoadingScreen\imgamount-1)
				width = ImageWidth(SelectedLoadingScreen\img[i])
				x = (opt\GraphicWidth / 2) - (width * 0.5) - (width * 0.625 * (SelectedLoadingScreen\imgamount-1-i)) + (width * 0.625 * i)
				DrawImage SelectedLoadingScreen\img[i], x, (opt\GraphicHeight / 2) - (ImageHeight(SelectedLoadingScreen\img[i]) / 2)
			Next
			
			width% = 300
			height% = 20
			x% = opt\GraphicWidth / 2 - width / 2
			y% = opt\GraphicHeight / 2 + 30 - 100
			
			Local width2% = Int((width - 2) * (100.0 / 330.0))*(opt\GraphicWidth/1280.0)
			Local height2% = (height/2-4)*(opt\GraphicHeight/720.0)
			Rect(opt\GraphicWidth-(width2)-(20.0*(opt\GraphicWidth/1280.0)), opt\GraphicHeight-(height2)-(20*(opt\GraphicHeight/720.0)), (Int((width - 2) * (percent / 330.0)))*(opt\GraphicWidth/1280.0), height2)
			
			If SelectedLoadingScreen\title = "CWM" Then
				
				If firstloop Then 
					If percent = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm1.cwm")
					ElseIf percent = 100
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm2.cwm")
					EndIf
				EndIf
				
				SetFont fo\Font[Font_Menu]
				strtemp$ = ""
				temp = Rand(2,9)
				For i = 0 To temp
					strtemp$ = strtemp + Chr(Rand(48,122))
				Next
				Text(opt\GraphicWidth / 2, opt\GraphicHeight / 6, strtemp, True, True)
				
				If percent = 0 Then 
					If Rand(5)=1 Then
						Select Rand(2)
							Case 1
								SelectedLoadingScreen\txt[0] = GetLocalStringR("Loading", "990_1",CurrentDate())
							Case 2
								SelectedLoadingScreen\txt[0] = CurrentTime()
						End Select
					Else
						i = Rand(13)
						Select i
							Case 1,2,3,4,5,6,7,8,9
								SelectedLoadingScreen\txt[0] = GetLocalString("Loading", "990_"+(i+1))
							Case 10
								SelectedLoadingScreen\txt[0] = "???????????"
							Case 11
								SelectedLoadingScreen\txt[0] = "???____??_???__????n?"
							Case 12
								SelectedLoadingScreen\txt[0] = "eof9nsd3jue4iwe1fgj"	
						End Select
					EndIf
				EndIf
				
				strtemp$ = SelectedLoadingScreen\txt[0]
				temp = Int(Len(SelectedLoadingScreen\txt[0])-Rand(5))
				For i = 0 To Rand(10,15)
					strtemp$ = Replace(SelectedLoadingScreen\txt[0],Mid(SelectedLoadingScreen\txt[0],Rand(1,Len(strtemp)-1),1),Chr(Rand(130,250)))
				Next		
				SetFont fo\Font[Font_Default]
				RowText(strtemp, opt\GraphicWidth / 2-300, opt\GraphicHeight / 1.25,600,300,True)
			Else
				
				SetFont fo\Font[Font_Menu]
				Color 255,255,255
				Text(opt\GraphicWidth / 2, opt\GraphicHeight / 6, SelectedLoadingScreen\title, True, True)
				
				SetFont fo\Font[Font_Default]
				If SelectedLoadingScreen\imgamount > 1 Then
					For i = 0 To Min(SelectedLoadingScreen\imgamount, SelectedLoadingScreen\txtamount)
						For i = 0 To (SelectedLoadingScreen\imgamount-1)
							width = ImageWidth(SelectedLoadingScreen\img[i])
							x = (opt\GraphicWidth / 2) - (width * 0.5) - (width * 0.625 * (SelectedLoadingScreen\imgamount-1-i)) + (width * 0.625 * i)
							y = (opt\GraphicHeight / 2) + (ImageHeight(SelectedLoadingScreen\img[i]) / 2) + 20
							RowText(SelectedLoadingScreen\txt[i], x, y, width, 300, True)
						Next
					Next
				Else
					RowText(SelectedLoadingScreen\txt[LoadingScreenText], opt\GraphicWidth / 2-(opt\GraphicWidth/6), opt\GraphicHeight / 1.25,opt\GraphicWidth/3,300,True)
				EndIf
			EndIf
		EndIf
		FlushKeys()
		FlushMouse()
		FlushJoy()
		
		GammaUpdate()
		
		Flip True
		
		Local close% = False
		firstloop = False
		If percent <> 100 Then
			Exit
		Else
			FlushKeys()
			FlushMouse()
			FlushJoy()
			ResetTimingAccumulator()
			SetFont fo\Font[Font_Default]
			close = True
			If NTF_GameModeFlag=3
				Players[mp_I\PlayerID]\FinishedLoading = True
			EndIf
		EndIf
	Until close
	
	DeleteMenuGadgets()
	
	If close Then
		SelectedLoadingScreen\rotImg = FreeEntity_Strict(SelectedLoadingScreen\rotImg)
		SelectedLoadingScreen\cam = FreeEntity_Strict(SelectedLoadingScreen\cam)
		
		If Camera<>0 Then
			ShowEntity Camera
		EndIf
		If m3d<>Null And m_I<>Null Then
			If m_I\Cam<>0 Then
				ShowEntity m_I\Cam
			EndIf
			If m3d\Scene<>0 Then
				ShowEntity m3d\Scene
			EndIf
		EndIf
		SelectedLoadingScreen\percent=0
	EndIf
	
	CatchErrors("Uncaught: Function DrawLoading")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D