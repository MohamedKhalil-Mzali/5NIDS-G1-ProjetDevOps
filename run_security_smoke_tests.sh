#!/bin/bash

# Example Smoke Test Script for checking web application and database

# URL of your deployed app (adjust with your actual URL)
APP_URL="http://localhost:8080"

# Check if the web app is running (status code 200)
echo "Checking if the application is up..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL)

if [ "$HTTP_STATUS" -ne 200 ]; then
  echo "ERROR: Web application is not responding as expected (HTTP Status: $HTTP_STATUS)"
  exit 1
else
  echo "Web application is up and responding (HTTP Status: $HTTP_STATUS)"
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
