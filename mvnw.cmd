@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM ----------------------------------------------------------------------------
@setlocal
@set WRAPPER_PROPERTIES="%~dp0\.mvn\wrapper\maven-wrapper.properties"
@set WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
@set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
@set MAVEN_PROJECTBASEDIR=%~dp0

@if not exist %WRAPPER_JAR% (
    @echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri %DOWNLOAD_URL% -OutFile %WRAPPER_JAR%"
)

@for /f "tokens=1,* delims==" %%a in (%WRAPPER_PROPERTIES%) do @(
    if "%%a"=="distributionUrl" set MAVEN_DIST_URL=%%b
)

@set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9
@if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    @echo Downloading Maven distribution...
    @set MAVEN_ZIP=%USERPROFILE%\.m2\wrapper\dists\maven.zip
    @if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"
    powershell -Command "Invoke-WebRequest -Uri '%MAVEN_DIST_URL%' -OutFile '%MAVEN_ZIP%'"
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
)

@set PATH=%MAVEN_HOME%\bin;%PATH%
@mvn.cmd %*
