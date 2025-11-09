@echo off
setlocal enabledelayedexpansion

REM ---------------------------
REM 1️⃣ Lấy danh sách tác giả (không trùng)
git log --format="%%aN" | sort /unique > authors.txt

REM 2️⃣ Tính tổng insertions và deletions toàn repo
set /a TOTAL_ADD=0
set /a TOTAL_DEL=0

for /f "tokens=1,2" %%a in ('git log --pretty^=tformat:^ --numstat ^| findstr "^[0-9]"') do (
    set /a TOTAL_ADD+=%%a
    set /a TOTAL_DEL+=%%b
)

set /a TOTAL_CHANGE=%TOTAL_ADD%+%TOTAL_DEL%

echo ===============================================
echo 📊 TỔNG TOÀN BỘ REPO:
echo   + %TOTAL_ADD% insertions
echo   - %TOTAL_DEL% deletions
echo   = %TOTAL_CHANGE% total changes
echo ===============================================

REM 3️⃣ Lặp qua từng tác giả và tính riêng
for /f "delims=" %%A in (authors.txt) do (
    set /a ADD=0
    set /a DEL=0
    for /f "tokens=1,2" %%a in ('git log --author^="%%A" --pretty^=tformat:^ --numstat ^| findstr "^[0-9]"') do (
        set /a ADD+=%%a
        set /a DEL+=%%b
    )
    set /a CHANGE=!ADD!+!DEL!
    if !TOTAL_CHANGE! gtr 0 (
        set /a PERCENT=100*!CHANGE!/!TOTAL_CHANGE!
    ) else (
        set /a PERCENT=0
    )
    echo %%A: +!ADD! / -!DEL!  = !CHANGE! changes  (~!PERCENT!%%)
)

echo ===============================================
echo Done!
endlocal
pause
