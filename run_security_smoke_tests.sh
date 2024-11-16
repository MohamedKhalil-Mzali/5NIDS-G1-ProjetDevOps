#!/bin/bash

echo "Starting Security Smoke Tests..."

# Verify if MY_SECRET_KEY is properly set
if [ -z "$MY_SECRET_KEY" ]; then
  echo "❌ Error: MY_SECRET_KEY is not set. Aborting tests."
  exit 1
else
  echo "✅ MY_SECRET_KEY is set."
fi

# Define test endpoints and expected responses
TEST_ENDPOINTS=(
  "http://localhost:8080/health"
  "http://localhost:8080/api/login"
  "http://localhost:8080/api/data"
)
EXPECTED_RESPONSES=(
  "healthy"
  "login_page"
  "data_fetched"
)

# Perform smoke tests on endpoints
for i in "${!TEST_ENDPOINTS[@]}"; do
  URL="${TEST_ENDPOINTS[$i]}"
  EXPECTED="${EXPECTED_RESPONSES[$i]}"
  
  echo "Testing endpoint: $URL"
  RESPONSE=$(curl -s "$URL")

  if [[ "$RESPONSE" == *"$EXPECTED"* ]]; then
    echo "✅ Test passed for $URL. Response contains expected output."
  else
    echo "❌ Test failed for $URL. Expected: '$EXPECTED', Got: '$RESPONSE'"
    exit 1
  fi
done

# Check Docker container health
echo "Checking Docker container health..."
CONTAINER_HEALTH=$(docker inspect --format='{{.State.Health.Status}}' my_container_name 2>/dev/null)

if [ "$CONTAINER_HEALTH" == "healthy" ]; then
  echo "✅ Docker container is healthy."
else
  echo "❌ Docker container is not healthy. Current status: $CONTAINER_HEALTH"
  exit 1
fi

# Validate database connection
echo "Validating database connection..."
DB_HOST="localhost"
DB_USER="my_user"
DB_PASS="my_password"
DB_NAME="my_database"

if mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null; then
  echo "✅ Database connection successful."
else
  echo "❌ Failed to connect to database."
  exit 1
fi

echo "All security smoke tests passed successfully! 🚀"
exit 0
