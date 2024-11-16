#!/bin/bash

# Define the base URL of the application
BASE_URL="http://192.168.56.10:8080"  # Change this to your web app's URL

# Make a basic GET request to the root URL and get the HTTP status code
echo "Checking if the application is up by hitting $BASE_URL..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL")

# Check if the HTTP status code is 200 (OK)
if [ "$HTTP_STATUS" -ne 200 ]; then
    echo "ERROR: Web application is not responding as expected."
    echo "HTTP Status: $HTTP_STATUS"
    exit 1
else
    echo "Application is up and responding (HTTP Status: $HTTP_STATUS)"
    exit 0
fi
