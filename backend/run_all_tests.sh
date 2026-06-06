#!/bin/bash

set -e

echo "=== Running auth-service tests ==="
cd auth-service
./gradlew test --no-daemon
cd ..

echo "=== Running deck-service tests ==="
cd deck-service
./gradlew test --no-daemon
cd ..

echo "=== Running api-gateway tests ==="
cd api-gateway
./gradlew test --no-daemon
cd ..

echo "=== All tests passed ==="