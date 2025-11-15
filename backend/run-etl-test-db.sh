#!/bin/bash
set -e

echo "🚀 Starting Test Environment..."

echo "📦 Starting test PostgreSQL container..."
docker compose -f src/main/resources/scripts/compose-test.yaml up -d

echo "⏳ Waiting for database to be ready..."
until docker exec gamesdb-test pg_isready -U gameuser -d gamesdb_test > /dev/null 2>&1; do
  sleep 1
done
echo "✅ Test database is ready!"

echo "🏃 Starting application with test profile..."
echo "   Flyway migrations will run automatically..."
echo "   Logs will appear below..."
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Run application in foreground so logs are visible
# Use --console=plain for readable output
./gradlew bootRun --args='--spring.profiles.active=test' --console=plain &
APP_PID=$!

# Save PID for cleanup script
echo $APP_PID > /tmp/backend-test.pid

# Wait for application to start
echo ""
echo "⏳ Waiting for application to start (this may take 30-60 seconds)..."
for i in {1..120}; do
  if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "✅ Application started successfully!"
    break
  fi
  if [ $i -eq 120 ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "❌ Application failed to start after 2 minutes"
    echo "   Check the logs above for errors"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    exit 1
  fi
  printf "."
  sleep 1
done

# Display instructions
echo ""
echo "📍 Base URL: http://localhost:8080"
echo ""
echo "🔧 Available Endpoints:"
echo "   • Run ETL:"
echo "     curl -X POST http://localhost:8080/api/etl/run | jq"
echo ""
echo "   • Compute Similarities:"
echo "     curl -X POST http://localhost:8080/api/etl/compute-similarities | jq"
echo ""
echo "   • Get Similar Games:"
echo "     curl http://localhost:8080/api/games/{id}/similar?limit=10 | jq"
echo ""
echo "   • Health Check:"
echo "     curl http://localhost:8080/actuator/health | jq"
echo ""
echo "📊 Database Access:"
echo "   psql -h localhost -p 5433 -U gameuser -d gamesdb_test"
echo ""
echo "🔍 Quick Diagnostic Query:"
echo "   psql -h localhost -p 5433 -U gameuser -d gamesdb_test -c \\"
echo "     \"SELECT 'Total Games' as metric, COUNT(*)::text FROM games"
echo "      UNION ALL SELECT 'With Descriptions', COUNT(*)::text FROM games WHERE description IS NOT NULL"
echo "      UNION ALL SELECT 'Similarities', COUNT(*)::text FROM game_similarities;\""
echo ""
echo "🛑 To stop: Press Ctrl+C, then run ./stop-test-env.sh"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📝 Application is running in foreground. Logs will appear below:"
echo ""

# Wait for application process (keeps script running and shows logs)
wait $APP_PID
