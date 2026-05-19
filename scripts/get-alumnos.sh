#!/bin/bash
BASE_URL="${API_URL:-https://gestion-alumnos-latest.onrender.com}"

curl -s "$BASE_URL/api/alumnos" | python3 -m json.tool
