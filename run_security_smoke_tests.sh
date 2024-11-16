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

# Jenkins Credentials
export API_USERNAME="admin_rayen"
export API_PASSWORD="admin"

# Perform smoke tests on endpoints
for i in "${!TEST_ENDPOINTS[@]}"; do
  URL="${TEST_ENDPOINTS[$i]}"
  EXPECTED="${EXPECTED_RESPONSES[$i]}"
  
  echo "Testing endpoint: $URL"
  RESPONSE=$(curl -s -u "$API_USERNAME:$API_PASSWORD" "$URL")

  if [[ "$RESPONSE" == *"$EXPECTED"* ]]; then
    echo "✅ Test passed for $URL. Response contains expected output."
  else
    echo "❌ Test failed for $URL. Expected: '$EXPECTED', Got: '$RESPONSE'"
    exit 1
  fi
done

# Additional checks for Docker container and database connection...

echo "All security smoke tests passed successfully! 🚀"
exit 0
