; IniControler - A part of BlitzToolBox
; Write & Read ini file.
; v1.05 2022.10.28
; https://github.com/ZiYueCommentary/BlitzToolbox

.lib "IniControler.dll"

IniClearBuffer(path$):"_IniClearBuffer@4"
IniClearAllBuffer():"_IniClearAllBuffer@0"
IniSetBufferValue(path$, section$, key$, value$):"_IniSetBufferValue@16"
IniBufferSectionExist%(path$, section$):"_IniBufferSectionExist@8"
IniGetBuffer%(path$):"_IniGetBuffer@4"
IniGetAllBuffer%():"_IniGetAllBuffer@0"
IniSetBuffer(path$, buffer%):"_IniSetBuffer@8"
IniSetAllBuffer(buffer%):"_IniSetAllBuffer@4"
IniBufferKeyExist%(path$, section$, key$):"_IniBufferKeyExist@12"
IniCreateSection(path$, section$):"_IniCreateSection@8"
IniRemoveBufferKey(path$, section$, key$):"_IniRemoveBufferKey@12"
IniRemoveBufferSection(path$, section$):"_IniRemoveBufferSection@8"
IniSetExportBufferValue(buffer%, section$, key$, value$):"_IniSetExportBufferValue@16"

; they have default parameters so you need include "IniControler.bb"
IniWriteBuffer_(path$, clearPrevious%):"_IniWriteBuffer@8"
IniGetString_$(path$, section$, key$, defaultValue$, allowBuffer%):"_IniGetString@20"
IniGetInt_%(path$, section$, key$, defaultValue%, allowBuffer%):"_IniGetInt@20"
IniGetFloat_#(path$, section$, key$, defaultValue#, allowBuffer%):"_IniGetFloat@20"
IniGetBufferString_$(path$, section$, key$, defaultValue$):"_IniGetBufferString@16"
IniGetBufferInt_%(path$, section$, key$, defaultValue%):"_IniGetBufferInt@16"
IniGetBufferFloat_#(path$, section$, key$, defaultValue#):"_IniGetBufferFloat@16"
IniWriteString_(path$, section$, key$, value$, updateBuffer%):"_IniWriteString@20"
IniWriteInt_(path$, section$, key$, value%, updateBuffer%):"_IniWriteInt@20"
IniWriteFloat_(path$, section$, key$, value#, updateBuffer%):"_IniWriteFloat@20"
IniSectionExist_%(path$, section$, allowBuffer%):"_IniSectionExist@12"
IniKeyExist_%(path$, section$, key$, allowBuffer%):"_IniKeyExist@16"
IniExportJson_(path$, json$, isMin%, stringOnly%, allowBuffer%):"_IniExportJson@20"
IniBufferExportJson_(path$, json$, isMin%, stringOnly%):"_IniBufferExportJson@16"
IniExportHtml_(path$, html$, isMin%, isList%, allowBuffer%):"_IniExportHtml@20"
IniBufferExportHtml_(path$, html$, isMin%, isList%):"_IniBufferExportHtml@16"
IniExportXml_(path$, xml$, isMin%, allowBuffer%):"_IniExportXml@16"
IniBufferExportXml_(path$, xml$, isMin%):"_IniBufferExportXml@12"
IniRemoveKey_(path$, section$, key$, updateBuffer%):"_IniRemoveKey@16"
IniRemoveSection_(path$, section$, updateBuffer%):"_IniRemoveSection@12"
IniExportIni_(path$, ini$, isMin%, allowBuffer%):"_IniExportIni@16"
IniBufferExportIni_(path$, ini$, isMin%):"_IniBufferExportIni@12"

; Header File :DDD
.lib " "

IniWriteBuffer(path$, clearPrevious%)
IniGetString$(path$, section$, key$, defaultValue$, allowBuffer%)
IniGetInt%(path$, section$, key$, defaultValue%, allowBuffer%)
IniGetFloat#(path$, section$, key$, defaultValue#, allowBuffer%)
IniGetBufferString$(path$, section$, key$, defaultValue$)
IniGetBufferInt%(path$, section$, key$, defaultValue%)
IniGetBufferFloat#(path$, section$, key$, defaultValue#)
IniWriteString(path$, section$, key$, value$, updateBuffer%)
IniWriteInt(path$, section$, key$, value%, updateBuffer%)
IniWriteFloat(path$, section$, key$, value#, updateBuffer%)
IniSectionExist%(path$, section$, allowBuffer%)
IniKeyExist%(path$, section$, key$, allowBuffer%)
IniExportJson(path$, json$, isMin%, stringOnly%, allowBuffer%)
IniBufferExportJson(path$, json$, isMin%, stringOnly%)
IniExportHtml(path$, html$, isMin%, isList%, allowBuffer%)
IniBufferExportHtml(path$, html$, isMin%, isList%)
IniExportXml(path$, xml$, isMin%, allowBuffer%)
IniBufferExportXml(path$, xml$, isMin%)
IniRemoveKey(path$, section$, key$, updateBuffer%)
IniRemoveSection(path$, section$, updateBuffer%)
IniExportIni(path$, ini$, isMin%, allowBuffer%)
IniBufferExportIni(path$, ini$, isMin%)