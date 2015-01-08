@echo off
rem JAVA_OPTS=

set ROOT_PATH=../

rem class path

set TOOLS_JAR_PATH=%ROOT_PATH%/lib/tools.jar
set CLASS_PATH=.;%TOOLS_JAR_PATH%

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
cd ..\lib
java %JAVA_OPTS% -classpath %CLASS_PATH%  %MAIN_CLASS%