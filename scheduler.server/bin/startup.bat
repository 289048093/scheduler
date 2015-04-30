@echo off
rem JAVA_OPTS=

rem cd ../%CURRENT_PATH
set CURRENT_PATH=%~dp0
cd  %CURRENT_PATH%
cd ..
set ROOT_PATH=%cd%

rem class path

set TOOLS_JAR_PATH=%ROOT_PATH%/lib/tools.jar
set CLASS_PATH=.;%TOOLS_JAR_PATH%
cd bin
echo %cd%
SETLOCAL ENABLEDELAYEDEXPANSION
for /f %%i in ('dir /b /s "../lib/*.jar"') do (
	set str=%%i
	set str=!str:\=/!
	set CLASS_PATH=!CLASS_PATH!;!str!
)

set MAIN_CLASS=com.mokylin.gm.scheduler.Main

rem startup
echo  classpath: %CLASS_PATH%  
echo  startup 
cd %ROOT_PATH%/lib
echo %cd%
echo java %JAVA_OPTS% -classpath %CLASS_PATH% -Dscheduler.root %ROOT_PATH%  %MAIN_CLASS%
java %JAVA_OPTS% -classpath %CLASS_PATH% -Dscheduler.root=%ROOT_PATH%  %MAIN_CLASS%