#!/bin/bash

# Test script for Legacy Service APIs
# This script tests the APIs with mock JWT tokens

echo "🧪 Testing Legacy Service APIs"
echo "================================"

# Test 1: Health Check (Public)
echo "1. Testing Health Endpoint (Public)..."
curl -s http://localhost:8085/api/v1/actuator/health
echo -e "\n"

# Test 2: Categories without JWT (should fail)
echo "2. Testing Categories without JWT (should return 403)..."
curl -s -w "HTTP Status: %{http_code}\n" http://localhost:8085/api/v1/categories
echo ""

# Test 3: Create a test JWT token (mock)
echo "3. Creating mock JWT token for testing..."
# For testing purposes, we'll use a simple base64 encoded token
# In real scenario, this would come from Auth Service
MOCK_JWT="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxMjMsInJvbGUiOiJVU0VSIiwiaWF0IjoxNjM5MDc2MDAwLCJleHAiOjE2MzkwODU2MDB9.test-signature"

echo "Mock JWT Token: $MOCK_JWT"
echo ""

# Test 4: Categories with JWT (should work if token is valid)
echo "4. Testing Categories with JWT token..."
curl -s -w "HTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $MOCK_JWT" \
  http://localhost:8085/api/v1/categories
echo ""

# Test 5: Create a category
echo "5. Testing Create Category..."
curl -s -w "HTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $MOCK_JWT" \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "name": "Family Stories",
    "description": "Stories passed down through generations",
    "categoryType": "USER_DEFINED",
    "parentCategoryId": null,
    "sortOrder": 1,
    "isActive": true
  }' \
  http://localhost:8085/api/v1/categories
echo ""

# Test 6: Get all categories
echo "6. Testing Get All Categories..."
curl -s -w "HTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $MOCK_JWT" \
  http://localhost:8085/api/v1/categories
echo ""

echo "✅ API Testing Complete!"
echo "Note: Some tests may fail due to JWT validation, which is expected without proper Auth Service integration."
