@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo =========================================================================
echo   Iniciando Weather Buddy (Time Hidro-Alerta)
echo =========================================================================

:: =========================================================================
:: CONFIGURACAO DO JAVA 25 (ALTERE PARA O PATH DO SEU COMPUTADOR SE PRECISAR)
:: Exemplo: C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot
:: =========================================================================
set "JDK_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"

set "JAVA_EXEC=java"
if exist "%JDK_HOME%\bin\java.exe" (
    set "JAVA_HOME=%JDK_HOME%"
    set "PATH=%JDK_HOME%\bin;%PATH%"
    set "JAVA_EXEC=%JDK_HOME%\bin\java.exe"
) else (
    echo [AVISO] JDK 25 nao encontrado em "%JDK_HOME%". Usando o Java padrao do PATH.
)

echo [INFO] Versao do Java em uso:
"%JAVA_EXEC%" -version

echo [INFO] Empacotando os modulos Maven do Weather Buddy...
call .\mvnw.cmd package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERRO] Falha na compilacao dos modulos Maven.
    pause
    exit /b %ERRORLEVEL%
)

echo [INFO] Iniciando servidor Spring Boot na porta 8080...
"%JAVA_EXEC%" -jar presentation\target\presentation-1.0.0-SNAPSHOT.jar

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERRO] Ocorreu uma falha ao executar a aplicacao.
    pause
)

endlocal
