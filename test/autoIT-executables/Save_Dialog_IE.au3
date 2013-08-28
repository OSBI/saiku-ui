;-----------------------------------------------------------------------
;~ Save_Dialog_IE.au3
;~ Purpose: To handle the Download/save Dialogbox in Internet Explorer 10
;~ Usage: Save_Dialog_IE.exe
;-----------------------------------------------------------------------

; get the handle of main window
Local $windHandle=WinGetHandle("[Class:IEFrame]", "")
Local $winTitle = "[HANDLE:" & $windHandle & "]"; 
;get coordinates of default HWND 
Local $ctlText=ControlGetPos ($winTitle, "", "[Class:DirectUIHWND;INSTANCE:1]")

; Select save option
WinActivate ($winTitle, "")
Send("{F6}")
sleep(5)
Send("{TAB}")
sleep(5)
Send("{DOWN}")
sleep(5)
Send("s")

; Wait the download completes, then close "Open" dialog box
sleep(500)
Send("{F6}")
sleep(5)
Send("{TAB}")
sleep(5)
Send("{TAB}")
sleep(5)
Send("{TAB}")
sleep(5)
Send("{ENTER}")