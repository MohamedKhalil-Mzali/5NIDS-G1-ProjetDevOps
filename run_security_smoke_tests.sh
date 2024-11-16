#!/bin/bash

# Define your target URL
TARGET_URL="http://localhost:8080/health"  # Adjust as needed
USERNAME="your_username"  # If basic auth is needed
PASSWORD="your_password"  # If basic auth is needed

echo "Checking if the application is up..."

# Make a simple GET request to the health endpoint (using basic auth if required)
HTTP_STATUS=$(curl -u "${USERNAME}:${PASSWORD}" -s -o /dev/null -w "%{http_code}" "$TARGET_URL")

if [ "$HTTP_STATUS" -ne 200 ]; then
    echo "ERROR: Web application is not responding as expected (HTTP Status: $HTTP_STATUS)"
    exit 1
else
    echo "Application is up and responding (HTTP Status: $HTTP_STATUS)"
fi


# Example: Check if the MySQL container is running
echo "Checking if the MySQL container is up..."
MYSQL_CONTAINER="5-nids-1-rayen-balghouthi-g1-mysqldb-1"
MYSQL_STATUS=$(docker inspect --format '{{.State.Running}}' $MYSQL_CONTAINER)

if [ "$MYSQL_STATUS" != "true" ]; then
  echo "ERROR: MySQL container is not running!"
  exit 1
else
  echo "MySQL container is running."
fi

# Add more checks as needed
echo "Smoke tests completed successfully!"
