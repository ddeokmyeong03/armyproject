#!/bin/sh
set -e

# Railway 환경: PORT와 BACKEND_URL을 템플릿에 주입
# 로컬 dev 환경에서도 기본값으로 동작
export PORT="${PORT:-80}"
export BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"

envsubst '$PORT $BACKEND_URL' < /etc/nginx/nginx.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
