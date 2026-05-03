@echo off
REM ─────────────────────────────────────────────────────────────────────────
REM SLCAS Build & Run Script (Windows)
REM Smart Library Circulation & Automation System — COS 202
REM MIVA Open University
REM ─────────────────────────────────────────────────────────────────────────

echo ========================================================
echo   SLCAS — Smart Library Circulation ^& Automation System
echo   COS 202 ^| MIVA Open University
echo ========================================================

where javac >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: javac not found. Please install JDK 11+ and add to PATH.
    pause
    exit /b 1
)

echo.
java -version
echo.

IF EXIST bin rmdir /s /q bin
mkdir bin

echo Compiling source files...

javac -d bin ^
    src\model\Borrowable.java ^
    src\model\LibraryItem.java ^
    src\model\Book.java ^
    src\model\Magazine.java ^
    src\model\Journal.java ^
    src\model\BorrowRecord.java ^
    src\model\UserAccount.java ^
    src\utils\IDGenerator.java ^
    src\utils\FileHandler.java ^
    src\controller\SearchEngine.java ^
    src\controller\SortEngine.java ^
    src\controller\LibraryManager.java ^
    src\gui\UITheme.java ^
    src\gui\GradientPanel.java ^
    src\gui\StyledButton.java ^
    src\gui\StatCard.java ^
    src\gui\LibraryTable.java ^
    src\gui\ViewItemsPanel.java ^
    src\gui\BorrowPanel.java ^
    src\gui\SearchSortPanel.java ^
    src\gui\AdminPanel.java ^
    src\gui\ReportsPanel.java ^
    src\gui\MainWindow.java ^
    src\Main.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    echo Launching SLCAS...
    echo.
    java -cp bin Main
) else (
    echo.
    echo Compilation FAILED. Please check the errors above.
    pause
    exit /b 1
)

pause
