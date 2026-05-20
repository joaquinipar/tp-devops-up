# Gestión de Alumnos API

API REST de gestión de alumnos desarrollada con Java 17 y Spring Boot 3, dockerizada y desplegada con un pipeline CI/CD completo. Proyecto final para la materia DevOps — Universidad de Palermo.

## Stack

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Maven
- **Base de datos:** PostgreSQL 16
- **Contenedores:** Docker (multi-stage), Docker Compose
- **CI/CD:** GitHub Actions → Docker Hub → Render
- **Observabilidad:** Datadog (Log Stream)

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/alumnos` | Lista todos los alumnos |
| GET | `/api/alumnos/{id}` | Obtiene un alumno por ID |
| POST | `/api/alumnos` | Crea un nuevo alumno |
| DELETE | `/api/alumnos/{id}` | Elimina un alumno |

**Base URL producción:** `https://gestion-alumnos-latest.onrender.com`

## Correr localmente

**Requisitos:** Docker y Docker Compose instalados.

```bash
# Clonar el repositorio
git clone https://github.com/joaquinipar/tp-devops-up.git
cd tp-devops-up

# Crear el archivo de variables de entorno
echo "DD_API_KEY=tu-api-key" > .env

# Levantar la aplicación con la base de datos
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

## Correr los tests

```bash
mvn test
```

## Scripts de prueba

```bash
./scripts/get-alumnos.sh            # GET todos los alumnos
./scripts/create-alumno.sh          # POST crear alumno
./scripts/get-alumno-by-id.sh 1     # GET por ID
./scripts/delete-alumno.sh 1        # DELETE por ID
```

Para apuntar a local:
```bash
API_URL=http://localhost:8080 ./scripts/get-alumnos.sh
```

## Pipeline CI/CD

Cada push a `main` dispara el siguiente flujo:

```
build-and-test → docker-publish → render-deploy
```

1. Compila el proyecto y ejecuta los tests unitarios
2. Construye la imagen Docker y la publica en Docker Hub
3. Dispara el redeploy automático en Render

## Estructura del proyecto

```
├── src/
│   ├── main/java/com/devops/alumnos/
│   │   ├── controller/   # AlumnoController
│   │   ├── entity/       # Alumno
│   │   ├── exception/    # AlumnoNotFoundException
│   │   ├── repository/   # AlumnoRepository
│   │   └── service/      # AlumnoService
│   └── test/             # Tests unitarios (JUnit 5 + Mockito)
├── scripts/              # Scripts curl para probar la API
├── .github/workflows/    # Pipelines de GitHub Actions
├── Dockerfile            # Multi-stage build
└── docker-compose.yml    # Orquestación local con PostgreSQL y Datadog
```

## Autor

Joaquin Ipar — Universidad de Palermo, 2026
