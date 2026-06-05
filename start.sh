#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$SCRIPT_DIR/hermes-api" && ./mvnw spring-boot:run &
API_PID=$!

cd "$SCRIPT_DIR/hermes-spa" && npm run dev &
SPA_PID=$!

trap "kill $API_PID $SPA_PID" SIGINT SIGTERM

wait
