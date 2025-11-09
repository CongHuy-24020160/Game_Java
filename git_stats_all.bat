@echo off
setlocal enabledelayedexpansion

echo ============================================
echo    GIT CONTRIBUTION STATS (ALL BRANCHES)
echo ============================================

set /a total_add=0
set /a total_del=0

for /f "delims=" %%a in (authors.txt) do (
    set /a add=0
    set /a del=0

    for /f "tokens=1,2" %%i in ('git log --all --author="%%a" --pretty=tformat: --numstat ^| findstr "^[0-9]"') do (
        set /a add=!add!+%%i
        set /a del=!del!+%%j
    )

    set /a total_add+=!add!
    set /a total_del+=!del!

    echo.
    echo ---- %%a ----
    echo Insertions: !add!
    echo Deletions : !del!
)

set /a total = total_add + total_del
echo ============================================
echo TOTAL INSERTIONS: %total_add%
echo TOTAL DELETIONS : %total_del%
echo TOTAL CHANGES   : %total%
echo ============================================

echo.
echo --- Percentage by author ---
for /f "delims=" %%a in (authors.txt) do (
    set /a add=0
    set /a del=0

    for /f "tokens=1,2" %%i in ('git log --all --author="%%a" --pretty=tformat: --numstat ^| findstr "^[0-9]"') do (
        set /a add=!add!+%%i
        set /a del=!del!+%%j
    )

    set /a person_total=!add!+!del!
    if !person_total! gtr 0 (
        set /a percent=100*!person_total!/%total%
        echo %%a : !percent!%%
    )
)

endlocal
pause
