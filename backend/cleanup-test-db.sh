#!/bin/bash

echo ""
echo "🛑 Stopping Test Environment..."
echo ""

# Stop application
if [ -f /tmp/backend-test.pid ]; then
  APP_PID=$(cat /tmp/backend-test.pid)
  if ps -p $APP_PID > /dev/null 2>&1; then
    echo "Stopping application (PID: $APP_PID)..."
    kill $APP_PID 2>/dev/null || true

    # Wait for graceful shutdown (max 10 seconds)
    for i in {1..10}; do
      if ! ps -p $APP_PID > /dev/null 2>&1; then
        break
      fi
      sleep 1
    done

    # Force kill if still running
    if ps -p $APP_PID > /dev/null 2>&1; then
      echo "Force stopping application..."
      kill -9 $APP_PID 2>/dev/null || true
    fi
  fi
  rm /tmp/backend-test.pid
else
  echo "Stopping any running bootRun processes..."
  pkill -f "bootRun.*test" || true
  pkill -f "GradleDaemon.*test" || true
fi
echo "✅ Application stopped"

# Stop database
echo "Stopping test database..."
docker compose -f src/main/resources/scripts/compose-test.yaml down -v
echo "✅ Database stopped and volumes removed"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Test environment stopped!"
echo ""
echo "   To restart: ./start-test-env.sh"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
