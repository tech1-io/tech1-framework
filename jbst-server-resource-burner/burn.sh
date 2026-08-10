#!/usr/bin/env bash
# Resource Burner API client.
#
# Usage:
#   ./burn.sh <command> [base-url] [key=value ...]
#
# Commands:
#   status
#   start | stop | clean             — CPU + RAM together
#   cpu-start | cpu-stop | cpu-clean — CPU only
#   ram-start | ram-stop | ram-clean — RAM only
#   watch                            — poll status every 5 seconds (Ctrl+C to exit)
#
# Tuning (start commands only, passed as key=value query params):
#   everySeconds=N   growth step interval, seconds (1-3600, default 10)
#   threads=N        CPU burner threads added per step (1-256, default 1)
#   chunkMB=N        RAM chunk retained per step, MB (1-1024, default 50)
#
#   ./burn.sh cpu-start everySeconds=1 threads=8      # burn CPU really fast
#   ./burn.sh ram-start everySeconds=300 chunkMB=10   # burn RAM really slow
#   ./burn.sh start everySeconds=1 threads=4 chunkMB=500
#
# Base URL resolution (first match wins):
#   1. an argument containing ://   ./burn.sh status http://my-droplet:3003/api
#   2. RESOURCE_BURNER_URL env var  RESOURCE_BURNER_URL=http://my-droplet:3003/api ./burn.sh status
#   3. fallback                     http://localhost:3003/api

COMMAND=${1:-status}
shift 2> /dev/null

BASE_URL=""
QUERY=""
for arg in "$@"; do
    if [[ "$arg" == *://* ]]; then
        BASE_URL="$arg"
    elif [[ "$arg" == *=* ]]; then
        QUERY="${QUERY:+$QUERY&}$arg"
    else
        echo "Unknown argument: $arg (expected a base URL or key=value)"
        exit 1
    fi
done
BASE_URL=${BASE_URL:-${RESOURCE_BURNER_URL:-http://localhost:3003/api}}
API="$BASE_URL/resource-burner"

pretty() {
    if command -v jq > /dev/null 2>&1; then
        jq .
    else
        cat
        echo
    fi
}

request() {
    curl -sS --fail-with-body "$@" | pretty
}

case "$COMMAND" in
    status)
        request "$API/status"
        ;;
    start | stop | clean)
        request -X POST "$API/$COMMAND${QUERY:+?$QUERY}"
        ;;
    cpu-start | cpu-stop | cpu-clean | ram-start | ram-stop | ram-clean)
        request -X POST "$API/${COMMAND/-//}${QUERY:+?$QUERY}"
        ;;
    watch)
        while true; do
            date "+--- %H:%M:%S ---"
            request "$API/status"
            sleep 5
        done
        ;;
    *)
        echo "Unknown command: $COMMAND"
        echo "Commands: status | start | stop | clean | cpu-start | cpu-stop | cpu-clean | ram-start | ram-stop | ram-clean | watch"
        exit 1
        ;;
esac
