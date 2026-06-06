#!/bin/bash

set -e

MAX_ATTEMPTS=30
SLEEP_SECONDS=10

echo "Waiting for all containers to be healthy..."

for i in $(seq 1 $MAX_ATTEMPTS); do
    containers=$(docker compose ps -q 2>/dev/null || true)

    if [ -z "$containers" ]; then
        echo "Attempt $i/$MAX_ATTEMPTS: No containers running yet..."
        sleep $SLEEP_SECONDS
        continue
    fi

    unhealthy=$(echo "$containers" | xargs -I {} docker inspect --format='{{.State.Health.Status}}' {} 2>/dev/null | grep -v "^healthy$" | wc -l)

    if [ "$unhealthy" -eq 0 ]; then
        echo "All containers are healthy!"
        exit 0
    fi

    echo "Attempt $i/$MAX_ATTEMPTS: $unhealthy containers not healthy yet..."
    docker compose ps
    sleep $SLEEP_SECONDS
done

echo "Timeout: some containers are not healthy after $MAX_ATTEMPTS attempts"
docker-compose logs
exit 1