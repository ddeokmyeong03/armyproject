#!/bin/sh
set -e

# BACKEND_URL이 설정된 경우에만 템플릿 처리 (Railway 환경)
# 없으면 볼륨 마운트된 설정 파일 사용 (로컬 dev 환경)
if [ -n "$BACKEND_URL" ]; then
    envsubst '$BACKEND_URL' < /etc/nginx/nginx.conf.template > /etc/nginx/conf.d/default.conf
fi

exec nginx -g 'daemon off;'
