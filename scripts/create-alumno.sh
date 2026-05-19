#!/bin/bash
BASE_URL="${API_URL:-https://gestion-alumnos-latest.onrender.com}"

curl -s -X POST "$BASE_URL/api/alumnos" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Perez","email":"juan.perez@test.com","matricula":"MAT-001"}' \
  | python3 -m json.tool
