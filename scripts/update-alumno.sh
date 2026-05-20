#!/bin/bash
BASE_URL="${API_URL:-https://gestion-alumnos-latest.onrender.com}"
ID="${1:-1}"
curl -s -X PUT "$BASE_URL/api/alumnos/$ID" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Perez","email":"juan.perez@example.com","matricula":"M001"}' | jq .
