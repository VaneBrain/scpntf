;A file that stores the global accessors for the type instance variables

;Global accessors that are memory-persistent
Global co.Controller = New Controller
Global fo.Fonts = New Fonts
Global ft.FixedTimesteps = New FixedTimesteps
Global gv.GlobalVariables = New GlobalVariables
Global kb.KeyBinds = New KeyBinds
Global opt.Options = New Options
Global aud.AudioControl = New AudioControl
Global gopt.GameOptions = New GameOptions

;Global accessors that will only be loaded into memory when necessary
Global mp_I.MultiplayerInstance
Global mp_O.MultiplayerOptions
Global d_I.DoorInstance
Global g_I.GunInstance
Global I_427.SCP427
Global m_I.MenuInstance
Global I_MIG.MenuImages
Global mi_I.MissionInstance
Global mpl.MainPlayer
Global m3d.Menu3DInstance
Global psp.PlayerSP
Global mtfd.MTFDialogue
Global pll.PlayerList

;~IDEal Editor Parameters:
;~C#Blitz3D