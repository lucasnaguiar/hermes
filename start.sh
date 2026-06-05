#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SPA_PORT="${VITE_PORT:-5180}"

cd "$SCRIPT_DIR/hermes-api" && ./mvnw spring-boot:run &
API_PID=$!

cd "$SCRIPT_DIR/hermes-spa" && [ ! -d node_modules ] && npm install; npm run dev &
SPA_PID=$!

cleanup() {
  pkill -P $API_PID 2>/dev/null
  kill $API_PID 2>/dev/null
  pkill -P $SPA_PID 2>/dev/null
  kill $SPA_PID 2>/dev/null
}

trap cleanup SIGINT SIGTERM

echo ""
echo "SPA disponível em http://localhost:$SPA_PORT"
echo ""

wait
