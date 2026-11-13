#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

echo "========================================================================="
echo "  Limpando pastas target do projeto Weather Buddy"
echo "========================================================================="

for dir in "target" "domain/target" "application/target" "infrastructure/target" "presentation/target"; do
    if [ -d "$dir" ]; then
        echo "[INFO] Removendo $dir ..."
        rm -rf "$dir"
    fi
done

echo ""
echo "[OK] Limpeza das pastas target concluida!"

