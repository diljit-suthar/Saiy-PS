@echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

setlocal

set DIR=%~dp0
set JAVA_EXE=java

if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

%JAVA_EXE% ^
  -classpath "%DIR%\gradle\wrapper\gradle-wrapper.jar" ^
  org.gradle.wrapper.GradleWrapperMain %*

endlocal
