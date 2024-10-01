
Type KeyBinds
	Field NVToggleKey%
	Field CommandWheelKey%
	Field SocialWheelKey%
	Field ChatKey%
End Type

Function LoadKeyBinds()
	
	kb\NVToggleKey = IniGetInt(gv\OptionFile, "binds", "Nightvision key", 33)
	kb\CommandWheelKey = IniGetInt(gv\OptionFile, "binds", "Command wheel key", 46)
	kb\SocialWheelKey = IniGetInt(gv\OptionFile, "binds", "Social wheel key", 47)
	kb\ChatKey = IniGetInt(gv\OptionFile, "binds", "Chat key", 28)
	
End Function

Function SaveKeyBinds()
	
	IniWriteString(gv\OptionFile, "binds", "Nightvision key", kb\NVToggleKey)
	IniWriteString(gv\OptionFile, "binds", "Command wheel key", kb\CommandWheelKey)
	IniWriteString(gv\OptionFile, "binds", "Social wheel key", kb\SocialWheelKey)
	IniWriteString(gv\OptionFile, "binds", "Chat key", kb\ChatKey)
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D