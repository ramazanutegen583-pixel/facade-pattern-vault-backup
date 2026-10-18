@echo off
setlocal EnableExtensions EnableDelayedExpansion
pushd "%~dp0"
set "RESULT=0"

if not exist "src\main\java" (
    if exist "facade-pattern-vault-backup-main\src\main\java" (
        cd /d "facade-pattern-vault-backup-main"
    ) else (
        echo Project sources were not found. Extract the complete project first.
        set "RESULT=1"
        goto finish
    )
)

set "JDK_BIN="
if defined JAVA_HOME call :try_jdk "%JAVA_HOME%\bin"
if not defined JDK_BIN for /d %%J in ("%USERPROFILE%\.jdks\*") do call :try_jdk "%%~fJ\bin"
if not defined JDK_BIN for /d %%J in ("%ProgramFiles%\Java\*") do call :try_jdk "%%~fJ\bin"
if not defined JDK_BIN for /f "delims=" %%J in ('where javac.exe 2^>nul') do call :try_jdk "%%~dpJ"

if not defined JDK_BIN (
    echo A JDK version 17 or later is required.
    echo Install a JDK or set JAVA_HOME to an existing JDK directory.
    set "RESULT=1"
    goto finish
)

echo Using JDK: !JDK_BIN!
echo Compiling the project...
if not exist "target" mkdir "target"
if not exist "target\demo-classes" mkdir "target\demo-classes"
(
    for /r "src\main\java" %%S in (*.java) do (
        set "SOURCE_FILE=%%~fS"
        echo "!SOURCE_FILE:\=/!"
    )
) > "target\demo-sources.txt"

"!JDK_BIN!\javac.exe" --release 17 -encoding UTF-8 -d "target\demo-classes" @"target\demo-sources.txt"
if errorlevel 1 (
    echo Compilation failed. Read the compiler message above.
    set "RESULT=1"
    goto finish
)

echo Starting the Home Theater Facade demonstration...
echo.
"!JDK_BIN!\java.exe" -cp "target\demo-classes" edu.aitu.hometheater.client.HomeTheaterDemo
set "RESULT=!ERRORLEVEL!"

:finish
popd
if /i not "%~1"=="--no-pause" (
    echo.
    echo Press any key to close this window.
    pause >nul
)
exit /b %RESULT%

:try_jdk
if defined JDK_BIN exit /b
if not exist "%~1\javac.exe" exit /b
if not exist "%~1\java.exe" exit /b
set "COMPILER_VERSION="
for /f "tokens=1,2" %%V in ('""%~1\javac.exe" -version"') do if "%%V"=="javac" set "COMPILER_VERSION=%%W"
if not defined COMPILER_VERSION exit /b
for /f "tokens=1 delims=.-" %%M in ("!COMPILER_VERSION!") do if %%M GEQ 17 set "JDK_BIN=%~1"
exit /b
