#!/bin/bash
BASE_URL="${API_URL:-https://gestion-alumnos-latest.onrender.com}"
ID="${1:?Uso: $0 <id>}"

curl -s "$BASE_URL/api/alumnos/$ID" | python3 -m json.tool
