; This is the Key.bb file that is used to verify a connection from one SCP - Nine Tailed Fox instance to another, mainly to prevent people to join a server that has been modified via code.
; For modders: This file is necessary for the key authentication mechanism. In here you can define your own key as well as your own encryption and decryption functions for additional security.
; 
; The easiest security measure: Simply define a key, which will then be sent from the server to the client. For basic security, the functions Key_StartAuth and Key_Encode will have the templates unmodified.
; For more advanced security, the functions Key_StartAuth and Key_Encode should be defined, the game itself does run these functions in the source code, which means manually adding them won't be necessary.
; 
; The server uses Key_StartAuth on the ENCRYPTION_KEY variable itself and sends the returned value to the client that wants to connect.
; The client receives the value and does Key_Encode on the key and sends the returned value to the server.
; The server receives the value and does Key_Encode on the key to check if the value received by the server will match the KEY variable after the decryption function.

;The key variable itself, DO NOT SHARE THIS PUBLICLY! It is important to maintain the key's value a secret!
Global ENCRYPTION_KEY$ = ""

;The authentication function
Function Key_StartAuth$()
	Local temp$ = ""
	
	;Your start of authentication code here.
	
	Return temp
End Function

;The key encoding function
Function Key_Encode$(encrypt_key$, secret$)
	Local temp$ = ""
	
	;Your encoding code here (note, the line below this comment is just a placeholder and can be removed when using a custom authentication process.
	temp = encrypt_key
	
	Return temp
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D