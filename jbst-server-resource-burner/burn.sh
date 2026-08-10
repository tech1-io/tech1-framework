#!/usr/bin/env bash
# Resource Burner API client.
#
# Usage:
#   ./burn.sh <command> [base-url]
#
# Commands:
#   status
#   start | stop | clean             — CPU + RAM together
#   cpu-start | cpu-stop | cpu-clean — CPU only
#   ram-start | ram-stop | ram-clean — RAM only
#   watch                            — poll status every 5 seconds (Ctrl+C to exit)
#
# Base URL resolution (first match wins):
#   1. second argument              ./burn.sh status http://my-droplet:3003/api
#   2. RESOURCE_BURNER_URL env var  RESOURCE_BURNER_URL=http://my-droplet:3003/api ./burn.sh status
#   3. fallback                     http://localhost:3003/api

COMMAND=${1:-status}
BASE_URL=${2:-${RESOURCE_BURNER_URL:-http://localhost:3003/api}}
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
        request -X POST "$API/$COMMAND"
        ;;
    cpu-start | cpu-stop | cpu-clean | ram-start | ram-stop | ram-clean)
        request -X POST "$API/${COMMAND/-//}"
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
