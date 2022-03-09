Const ROUGH% = -2, COARSE% = -1, ONETOONE% = 0, FINE% = 1, VERY_FINE% = 2

Function Use914(item.Items, setting%, x#, y#, z#)
	
	RefinedItems = RefinedItems+1
	
	Local it2.Items
	Select item\itemtemplate\name
		Case "Gas Mask", "Heavy Gas Mask"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case ONETOONE
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case FINE, VERY_FINE
					it2 = CreateItem("Gas Mask", "supergasmask", x, y, z)
					RemoveItem(item)
			End Select
		Case "SCP-1499"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case ONETOONE
					it2 = CreateItem("Gas Mask", "gasmask", x, y, z)
					RemoveItem(item)
				Case FINE
					it2 = CreateItem("SCP-1499", "super1499", x, y, z)
					RemoveItem(item)
				Case VERY_FINE
					n.NPCs = CreateNPC(NPCtype1499,x,y,z)
					n\State = 1
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
					n\State3 = 1
					RemoveItem(item)
			End Select
		Case "Ballistic Vest"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case ONETOONE
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case FINE
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					RemoveItem(item)
				Case VERY_FINE
					it2 = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					RemoveItem(item)
			End Select
		Case "Clipboard"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_PAPERSTRIPS, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					For i% = 0 To 19
						If item\SecondInv[i]<>Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i]=Null
					Next
					RemoveItem(item)
				Case ONETOONE
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case FINE
					item\invSlots = Max(item\state2,15)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case VERY_FINE
					item\invSlots = Max(item\state2,20)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
			End Select
		Case "Cowbell"
			Select setting
				Case ROUGH,COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case ONETOONE,FINE,VERY_FINE
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
			End Select
		Case "Night Vision Goggles"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case ONETOONE
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case FINE
					it2 = CreateItem("Night Vision Goggles", "finenvgoggles", x, y, z)
					RemoveItem(item)
				Case VERY_FINE
					it2 = CreateItem("Night Vision Goggles", "supernv", x, y, z)
					it2\state = 1000
					RemoveItem(item)
			End Select
		Case "Metal Panel", "SCP-148 Ingot"
			Select setting
				Case ROUGH, COARSE
					it2 = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					RemoveItem(item)
				Case ONETOONE, FINE, VERY_FINE
					it2 = Null
					For it.Items = Each Items
						If it<>item And it\collider <> 0 And it\Picked = False Then
							If DistanceSquared(EntityX(it\collider,True), EntityX(item\collider, True), EntityZ(it\collider,True), EntityZ(item\collider, True)) < PowTwo(180.0 * RoomScale) Lor DistanceSquared(EntityX(it\collider,True), x, EntityZ(it\collider,True), z) < PowTwo(180.0 * RoomScale) Then
								it2 = it
								Exit
							End If
						End If
					Next
					
					If it2<>Null Then
						Select it2\itemtemplate\tempname
							Case "gasmask", "supergasmask"
								RemoveItem (it2)
								RemoveItem (item)
								
								it2 = CreateItem("Heavy Gas Mask", "gasmask3", x, y, z)
							Case "vest"
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
							Case "hazmatsuit","hazmatsuit2"
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Hazmat Suit", "hazmatsuit3", x, y, z)
						End Select
					Else 
						If item\itemtemplate\name="SCP-148 Ingot" Then
							it2 = CreateItem("Metal Panel", "scp148", x, y, z)
							RemoveItem(item)
						Else
							PositionEntity(item\collider, x, y, z)
							ResetEntity(item\collider)							
						EndIf
					EndIf					
			End Select
		Case "Severed Hand", "Black Severed Hand"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_BLOODSPLAT2, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE, FINE, VERY_FINE
					If (item\itemtemplate\name = "Severed Hand")
						it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
					Else
						it2 = CreateItem("Severed Hand", "hand", x, y, z)
					EndIf
			End Select
			RemoveItem(item)
		Case "First Aid Kit", "Blue First Aid Kit"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					If Rand(2)=1 Then
						it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					Else
						it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
					EndIf
				Case FINE
					it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
				Case VERY_FINE
					it2 = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
			End Select
			RemoveItem(item)
		Case "Level 1 Key Card", "Level 2 Key Card", "Level 3 Key Card", "Level 4 Key Card", "Level 5 Key Card", "Key Card"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("Playing Card", "misc", x, y, z)
				Case FINE
					Select item\itemtemplate\name
						Case "Level 1 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
								Case NORMAL
									If Rand(5)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
								Case HARD
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
							End Select
						Case "Level 2 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
							End Select
						Case "Level 3 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									If Rand(10)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
								Case NORMAL
									If Rand(15)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
								Case HARD
									If Rand(20)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
							End Select
						Case "Level 4 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
							End Select
						Case "Level 5 Key Card"	
;							Local CurrAchvAmount%=0
;							For i = 0 To MAXACHIEVEMENTS-1
;								If Achievements[i]\Unlocked Then
;									CurrAchvAmount=CurrAchvAmount+1
;								EndIf
;							Next
;							
;							DebugLog CurrAchvAmount
;							
;							Select SelectedDifficulty\otherFactors
;								Case EASY
;									If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
;										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;									Else
;										it2 = CreateItem("Mastercard", "misc", x, y, z)
;									EndIf
;								Case NORMAL
;									If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
;										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;									Else
;										it2 = CreateItem("Mastercard", "misc", x, y, z)
;									EndIf
;								Case HARD
;									If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
;										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;									Else
;										it2 = CreateItem("Mastercard", "misc", x, y, z)
;									EndIf
;							End Select
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Key Card Omni", "key6", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									EndIf
							End Select
					End Select
				Case VERY_FINE
;					CurrAchvAmount%=0
;					For i = 0 To MAXACHIEVEMENTS-1
;						If Achievements[i]\Unlocked Then
;							CurrAchvAmount=CurrAchvAmount+1
;						EndIf
;					Next
;					
;					DebugLog CurrAchvAmount
;					
;					Select SelectedDifficulty\otherFactors
;						Case EASY
;							If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
;								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;							Else
;								it2 = CreateItem("Mastercard", "misc", x, y, z)
;							EndIf
;						Case NORMAL
;							If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
;								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;							Else
;								it2 = CreateItem("Mastercard", "misc", x, y, z)
;							EndIf
;						Case HARD
;							If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
;								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
;							Else
;								it2 = CreateItem("Mastercard", "misc", x, y, z)
;							EndIf
;					End Select
					Select SelectedDifficulty\otherFactors
						Case EASY
							it2 = CreateItem("Key Card Omni", "key6", x, y, z)
						Case NORMAL
							If Rand(4)=1 Then
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							Else
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							EndIf
						Case HARD
							If Rand(3)=1 Then
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							Else
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							EndIf
					End Select
			End Select
			
			RemoveItem(item)
		Case "Key Card Omni"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					If Rand(2)=1 Then
						it2 = CreateItem("Mastercard", "misc", x, y, z)
					Else
						it2 = CreateItem("Playing Card", "misc", x, y, z)			
					EndIf	
				Case FINE, VERY_FINE
					it2 = CreateItem("Key Card Omni", "key6", x, y, z)
			End Select			
			
			RemoveItem(item)
		Case "Playing Card", "Coin", "Quarter"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)	
			    Case FINE, VERY_FINE
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
			End Select
			RemoveItem(item)
		Case "Mastercard"
			Select setting
				Case ROUGH
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case COARSE
					it2 = CreateItem("Quarter", "25ct", x, y, z)
					Local it3.Items,it4.Items,it5.Items
					it3 = CreateItem("Quarter", "25ct", x, y, z)
					it4 = CreateItem("Quarter", "25ct", x, y, z)
					it5 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType (it3\collider, HIT_ITEM)
					EntityType (it4\collider, HIT_ITEM)
					EntityType (it5\collider, HIT_ITEM)
				Case ONETOONE
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)	
			    Case FINE, VERY_FINE
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
			End Select
			RemoveItem(item)
		Case "S-NAV 300 Navigator", "S-NAV 310 Navigator", "S-NAV Navigator", "S-NAV Navigator Ultimate"
			Select setting
				Case ROUGH, COARSE
					it2 = CreateItem("Electronical components", "misc", x, y, z)
				Case ONETOONE
					it2 = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\state = 100
				Case FINE
					it2 = CreateItem("S-NAV 310 Navigator", "nav", x, y, z)
					it2\state = 100
				Case VERY_FINE
					it2 = CreateItem("S-NAV Navigator Ultimate", "nav", x, y, z)
					it2\state = 101
			End Select
			
			RemoveItem(item)
		Case "Radio Transceiver"
			Select setting
				Case ROUGH, COARSE
					it2 = CreateItem("Electronical components", "misc", x, y, z)
				Case ONETOONE
					it2 = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\state = 100
				Case FINE
					it2 = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					it2\state = 101
				Case VERY_FINE
					it2 = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
					it2\state = 101
			End Select
			
			RemoveItem(item)
		Case "SCP-513"
			Select setting
				Case ROUGH, COARSE
					PlaySound_Strict LoadTempSound("SFX\SCP\513\914Refine.ogg")
					For n.npcs = Each NPCs
						If n\npctype = NPCtype5131 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(DECAL_DECAY, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE, FINE, VERY_FINE
					it2 = CreateItem("SCP-513", "scp513", x, y, z)
					
			End Select
			
			RemoveItem(item)
		Case "Some SCP-420-J", "Cigarette"
			Select setting
				Case ROUGH, COARSE			
					d.Decals = CreateDecal(DECAL_DECAY, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
				Case FINE
					it2 = CreateItem("Joint", "420s", x + 1.5, y + 0.5, z + 1.0)
				Case VERY_FINE
					it2 = CreateItem("Smelly Joint", "420s", x + 1.5, y + 0.5, z + 1.0)
			End Select
			
			RemoveItem(item)
		Case "9V Battery", "18V Battery", "Strange Battery"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("18V Battery", "18vbat", x, y, z)
				Case FINE
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
				Case VERY_FINE
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
			End Select
			
			RemoveItem(item)
		Case "ReVision Eyedrops", "RedVision Eyedrops", "Eyedrops"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("RedVision Eyedrops", "eyedrops", x,y,z)
				Case FINE
					it2 = CreateItem("Eyedrops", "fineeyedrops", x,y,z)
				Case VERY_FINE
					it2 = CreateItem("Eyedrops", "supereyedrops", x,y,z)
			End Select
			
			RemoveItem(item)		
		Case "Hazmat Suit"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("Hazmat Suit", "hazmatsuit", x,y,z)
				Case FINE
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
				Case VERY_FINE
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
			End Select
			
			RemoveItem(item)
			
		Case "Syringe"
			Select item\itemtemplate\tempname
				Case "syringe"
					Select setting
						Case ROUGH, COARSE
							d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case ONETOONE
							it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)	
						Case FINE
							it2 = CreateItem("Syringe", "finesyringe", x, y, z)
						Case VERY_FINE
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					End Select
					
				Case "finesyringe"
					Select setting
						Case ROUGH
							d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case COARSE
							it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
						Case ONETOONE
							it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)	
						Case FINE, VERY_FINE
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					End Select
					
				Case "veryfinesyringe"
					Select setting
						Case ROUGH, COARSE, ONETOONE, FINE
							it2 = CreateItem("Electronical components", "misc", x, y, z)	
						Case VERY_FINE
							n.NPCs = CreateNPC(NPCtype008,x,y,z)
							n\State = 2
					End Select
			End Select
			
			RemoveItem(item)
			
		Case "SCP-500-01", "Upgraded pill", "Pill"
			Select setting
				Case ROUGH, COARSE
					d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case ONETOONE
					it2 = CreateItem("Pill", "pill", x, y, z)
					RemoveItem(item)
				Case FINE
					Local no427Spawn% = False
					For it3.Items = Each Items
						If it3\itemtemplate\tempname = "scp427" Then
							no427Spawn = True
							Exit
						EndIf
					Next
					If (Not no427Spawn) Then
						it2 = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					EndIf
					RemoveItem(item)
				Case VERY_FINE
					it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					RemoveItem(item)
			End Select
			
		Default
			
			Select item\itemtemplate\tempname
				Case "cup"
					Select setting
						Case ROUGH, COARSE
							d.Decals = CreateDecal(DECAL_DECAY, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
							d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
						Case ONETOONE
							it2 = CreateItem("cup", "cup", x,y,z, 255-item\r,255-item\g,255-item\b,item\a)
							it2\name = item\name
							it2\state = item\state
						Case FINE
							it2 = CreateItem("cup", "cup", x,y,z, Min(item\r*Rnd(0.9,1.1),255),Min(item\g*Rnd(0.9,1.1),255),Min(item\b*Rnd(0.9,1.1),255),item\a)
							it2\name = item\name
							it2\state = item\state+1.0
						Case VERY_FINE
							it2 = CreateItem("cup", "cup", x,y,z, Min(item\r*Rnd(0.5,1.5),255),Min(item\g*Rnd(0.5,1.5),255),Min(item\b*Rnd(0.5,1.5),255),item\a)
							it2\name = item\name
							it2\state = item\state*2
							If Rand(5)=1 Then
								ExplosionTimer = 135
							EndIf
					End Select	
					
					RemoveItem(item)
				Case "paper"
					Select setting
						Case ROUGH, COARSE
							d.Decals = CreateDecal(DECAL_PAPERSTRIPS, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
						Case ONETOONE
							Select Rand(6)
								Case 1
									it2 = CreateItem("Document SCP-106", "paper", x, y, z)
								Case 2
									it2 = CreateItem("Document SCP-079", "paper", x, y, z)
								Case 3
									it2 = CreateItem("Document SCP-173", "paper", x, y, z)
								Case 4
									it2 = CreateItem("Document SCP-895", "paper", x, y, z)
								Case 5
									it2 = CreateItem("Document SCP-682", "paper", x, y, z)
								Case 6
									it2 = CreateItem("Document SCP-860", "paper", x, y, z)
							End Select
						Case FINE, VERY_FINE
							it2 = CreateItem("Origami", "misc", x, y, z)
					End Select
					
					RemoveItem(item)
				Default
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)	
			End Select
			
	End Select
	
	If it2 <> Null Then EntityType (it2\collider, HIT_ITEM)
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D