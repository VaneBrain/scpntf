
Const Controller_DInput = 0 ;DirectInput (Default), used for pretty much every controller that can be used on a PC
Const Controller_PS = 1		;Almost the same as Direct Input, but a different controller layout, as the layout of a PlayStation controller is different than what a DirectInput controller has
Const Controller_Custom = 2 ;This is currently not supported, in a future version you will be able to completely customize your controller layout
Const Controller_XAxis = 0
Const Controller_YAxis = 1
Const Controller_MaxButtons = 32

Type Controller
	Field Enabled%
	Field FullControl%
	Field Sensitivity#
	Field InvertAxis[2]
	Field Scheme%
	Field SwapDPad_And_LeftStick%
	Field WaitTimer#
	Field PressedButton%
	Field CurrButton%[Controller_MaxButtons]
	Field CurrButtonSub%[Controller_MaxButtons]
	Field SelectSFX%
	Field ScrollBarY#
	Field PressedNext%
	Field PressedPrev%
	Field KeyPad_CurrButton%
End Type

Function InitController()
	
	If JoyType()=0 Then
		co\Enabled = False
		Return
	EndIf
	
	co\Enabled = IniGetInt(gv\OptionFile, "binds", "controller enabled")
	co\Sensitivity = IniGetFloat(gv\OptionFile, "binds", "controller sensitivity")
	co\InvertAxis[Controller_XAxis] = IniGetInt(gv\OptionFile, "binds", "controller invert x")
	co\InvertAxis[Controller_YAxis] = IniGetInt(gv\OptionFile, "binds", "controller invert y")
	co\FullControl = False ;Currently only partial controller support is available, so the main menu, pause menu and console cannot be navigated by a controller
	co\Scheme = IniGetInt(gv\OptionFile, "binds", "controller scheme")
	co\SwapDPad_And_LeftStick = IniGetInt(gv\OptionFile, "binds", "controller swap dpad leftstick")
	
	co\SelectSFX = LoadSound_Strict("SFX\Interact\SelectButton.ogg")
	
End Function

Function SaveController()
	If JoyType()=0 Then Return ;Just skip this if no controller has been plugged in or detected
	
	IniWriteString(gv\OptionFile, "binds", "controller enabled", co\Enabled)
	IniWriteString(gv\OptionFile, "binds", "controller sensitivity", co\Sensitivity)
	IniWriteString(gv\OptionFile, "binds", "controller invert x", co\InvertAxis[Controller_XAxis])
	IniWriteString(gv\OptionFile, "binds", "controller invert y", co\InvertAxis[Controller_YAxis])
	IniWriteString(gv\OptionFile, "binds", "controller scheme", co\Scheme)
	IniWriteString(gv\OptionFile, "binds", "controller swap dpad leftstick", co\SwapDPad_And_LeftStick)
	
End Function










;~IDEal Editor Parameters:
;~F#8#1A#2F
;~C#Blitz3D