;---------------------------------------------------------
;~ Save_Dialog_FF.au3
;~ Purpose: To handle the Dowload/save Dialogbox in Firefox
;~ Usage: Save_Dialog_FF.exe "Dialog Title" "Opetaion" "Path"
;----------------------------------------------------------

; set the select mode to select using substring
AutoItSetOption("WinTitleMatchMode","2")

if $CmdLine[0] < 2 then
; Arguments are not enough
msgbox(0,"Error","Supply all the Arguments, Dialog title,Save/Cancel and Path to save(optional)")
Exit
EndIf

; wait until dialog box appears
WinWait($CmdLine[1]) ; match the window with substring
$title = WinGetTitle($CmdLine[1]) ; retrives whole window title
WinActive($title);

; if user choose to save file
If (StringCompare($CmdLine[2],"OK",0) = 0) Then

WinActivate($title)
WinWaitActive($title)
Sleep(1)
EndIf

; Select Save radil button and then click "OK"
; It will be save after this point.
Send("!s")
Sleep(1)
Send("{ENTER}")
