@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo =========================================================================
echo   Limpando pastas target do projeto Weather Buddy
echo =========================================================================

for %%D in (
    "target"
    "domain\target"
    "application\target"
    "infrastructure\target"
    "presentation\target"
) do (
    if exist "%%~D" (
        echo [INFO] Removendo %%~D ...
        rmdir /s /q "%%~D"
        if exist "%%~D" (
            echo [AVISO] Nao foi possivel remover %%~D ^(verifique se a aplicacao esta rodando^).
        )
    )
)

echo.
echo [OK] Limpeza das pastas target concluida!
endlocal

