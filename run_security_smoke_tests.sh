#!/bin/bash

# Simple security smoke tests

echo "Running security smoke tests..."

# Check if required environment variable (e.g., MY_SECRET_KEY) is set
if [ -z "$MY_SECRET_KEY" ]; then
  echo "ERROR: MY_SECRET_KEY is not set!"
  exit 1
else
  echo "MY_SECRET_KEY is set."
fi

# Check if any critical ports (e.g., HTTP 80, HTTPS 443) are open
open_ports=$(netstat -tuln | grep -E ':80|:443')
if [ -z "$open_ports" ]; then
  echo "ERROR: Expected ports (80 or 443) are not open."
  exit 1
else
  echo "Ports 80 or 443 are open."
fi

# Check for the presence of critical tools (e.g., curl, jq)
for tool in curl jq; do
  if ! command -v $tool &> /dev/null; then
    echo "ERROR: $tool is not installed!"
    exit 1
  else
    echo "$tool is installed."
  fi
done

# Check for any exposed secrets in the repository (you can use tools like git-secrets for this)
# Placeholder for running secrets scanning tool
# echo "Running secrets scan..."
# If you have a tool like git-secrets installed, you could add it here
# git secrets --scan

echo "Security smoke tests passed!"

# Exit successfully
exit 0
