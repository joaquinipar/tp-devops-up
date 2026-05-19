#!/bin/bash
BASE_URL="${API_URL:-https://gestion-alumnos-latest.onrender.com}"
ID="${1:?Uso: $0 <id>}"

curl -s -o /dev/null -w "Status: %{http_code}\n" -X DELETE "$BASE_URL/api/alumnos/$ID"
