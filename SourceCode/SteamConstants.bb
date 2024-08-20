;EFriendRelationship
;Declares the set of relationships that Steam users may have.
Const k_EFriendRelationshipNone% = 0
Const k_EFriendRelationshipBlocked% = 1
Const k_EFriendRelationshipRequestRecipient% = 2
Const k_EFriendRelationshipFriend% = 3
Const k_EFriendRelationshipRequestInitiator% = 4
Const k_EFriendRelationshipIgnored% = 5
Const k_EFriendRelationshipIgnoredFriend% = 6
Const k_EFriendRelationshipSuggested_DEPRECATED% = 7
Const k_EFriendRelationshipMax% = 8

;EP2PSend
;Specifies the send type of SendP2PPacket.
;Typically k_EP2PSendUnreliable is what you want For UDP-like packets, k_EP2PSendReliable For TCP-like packets
Const k_EP2PSendUnreliable% = 0
Const k_EP2PSendUnreliableNoDelay% = 1
Const k_EP2PSendReliable% = 2
Const k_EP2PSendReliableWithBuffering% = 3

;Flags used for message sending
;These values are used in bitmask parameters to functions such as SendMessageToUser
Const k_nSteamNetworkingSend_Unreliable% = 0
Const k_nSteamNetworkingSend_NoNagle% = 1
Const k_nSteamNetworkingSend_NoDelay% = 4
Const k_nSteamNetworkingSend_Reliable% = 8

;Steam Achievement Constants
Const ACHV_939_5MIN$ = "ACHV_939_5MIN"
Const ACHV_BURNING_MAN$ = "ACHV_BURNING_MAN"
Const ACHV_MASK$ = "ACHV_MASK"
Const ACHV_TIS_BUT_A_SCRATCH$ = "ACHV_TIS_BUT_A_SCRATCH"
Const ACHV_106_CONTAINED$ = "ACHV_106_CONTAINED"
Const ACHV_173_CONTAINED$ = "ACHV_173_CONTAINED"
Const STAT_939$ = "STAT_939"
Const STAT_1048$ = "STAT_1048"
Const STAT_FUSE$ = "STAT_FUSE"
Const STAT_LOBOTOMY$ = "STAT_LOBOTOMY"
Const STAT_ZOMBIES$ = "STAT_ZOMBIES"
Const STAT_ONE_SHOT$ = "STAT_ONE_SHOT"

Function Steam_LoadPacket%(channel% = 0)
	Return Steam_ReceiveMessagesOnChannel(channel)
End Function

Function Steam_SendPacketToUser%(upperID%, lowerID%, flag% = k_nSteamNetworkingSend_Unreliable, cacheData% = 0, channel% = 0)
	Return Steam_SendMessageToUser(upperID, lowerID, flag, cacheData, channel)
End Function

Function Steam_CloseConnection%(upperID%, lowerID%)
	Return Steam_CloseSessionWithUser(upperID, lowerID)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D