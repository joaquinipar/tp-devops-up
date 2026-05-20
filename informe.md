# Trabajo Práctico — Gestión de Alumnos API
## Informe de Implementación

---

## Introducción

El objetivo de este trabajo práctico fue construir una aplicación funcional y aplicarle prácticas modernas de DevOps de principio a fin. La idea no era desarrollar un sistema complejo, sino tener un artefacto real sobre el que montar un pipeline de integración y entrega continua completo. Para eso decidí implementar una API REST simple de gestión de alumnos en Java con Spring Boot, lo que me permitió enfocarme en la infraestructura y los procesos sin perderme en lógica de negocio.

El trabajo se dividió en cinco fases que fui completando de forma incremental, con un historial de Git limpio y trazable en todo momento.

---

## Fase 1 — Código Base y Cultura Git

Lo primero fue definir la tecnología: Java 17 con Spring Boot 3, Maven como sistema de build y PostgreSQL como base de datos. Utilicé Spring Data JPA para el acceso a datos, lo que me permitió tener un CRUD completo sin escribir SQL manual.

La entidad central es `Alumno`, con campos básicos como nombre, apellido, email y matrícula. Sobre eso construí un repositorio, un servicio con la lógica de negocio y un controlador REST expuesto en `/api/alumnos` que responde a las operaciones GET, POST y DELETE.

Un punto importante de esta fase fue el testing. Escribí pruebas unitarias para el servicio usando JUnit 5 y Mockito, sin levantar el contexto de Spring ni conectarse a ninguna base de datos. Esto da tests rápidos y aislados que el pipeline puede ejecutar en segundos. Cubrí los cuatro métodos del servicio con seis casos de prueba, incluyendo tanto los caminos exitosos como los de error.

Desde el primer commit utilicé la convención de Conventional Commits (`feat:`, `fix:`, `chore:`, `ci:`, `test:`), una decisión que después resultó clave para la generación automática de releases.

---

## Fase 2 — Contenedores

Con la aplicación funcionando, el siguiente paso fue dockerizarla correctamente.

El `Dockerfile` usa una estrategia multi-stage: una primera etapa con una imagen completa de Maven para compilar el código y generar el `.jar`, y una segunda etapa que parte de una imagen liviana (`eclipse-temurin:17-jre-alpine`) que solo contiene lo necesario para ejecutar la aplicación. El resultado es una imagen de producción que pesa mucho menos y tiene una superficie de ataque considerablemente menor.

Dentro del contenedor la aplicación corre con un usuario sin privilegios (`appuser`), aplicando el principio de mínimo privilegio. Si el proceso fuera comprometido, no tendría acceso de root al sistema.

Para orquestar la aplicación con su base de datos localmente definí un `docker-compose.yml` con dos servicios: `db` con PostgreSQL 16 y `api` con la aplicación. El servicio de base de datos tiene un `healthcheck` con `pg_isready`, y la API tiene configurado `depends_on` con `condition: service_healthy`, lo que garantiza que la aplicación no intente conectarse antes de que PostgreSQL esté realmente listo para aceptar conexiones. Los datos de la base persisten entre reinicios gracias a un volumen nombrado.

La configuración de la conexión a la base de datos se parametriza completamente mediante variables de entorno con valores por defecto, lo que permite usar el mismo contenedor en cualquier entorno sin modificar el código.

---

## Fase 3 — Integración Continua con GitHub Actions

El pipeline de CI vive en `.github/workflows/ci.yml` y se dispara en cada push y pull request hacia la rama `main`.

Tiene tres jobs encadenados. El primero, `build-and-test`, compila el proyecto y ejecuta todos los tests unitarios usando `mvn verify`. Si algún test falla, el pipeline se detiene y no avanza al siguiente paso. El segundo, `docker-publish`, solo corre cuando hay un push directo a `main` (no en PRs), construye la imagen Docker multi-stage y la publica en Docker Hub con dos tags: `latest` y un tag con el SHA del commit, lo que permite rastrear exactamente qué código originó cada imagen. El tercer job, `render-deploy`, se detalla en la siguiente fase.

Para optimizar los tiempos de ejecución habilité el caché de dependencias de Maven, de modo que el pipeline no descarga las librerías desde cero en cada run.

Las credenciales de Docker Hub se manejan como GitHub Secrets y nunca aparecen en el código.

---

## Fase 4 — Entrega Continua y Observabilidad

### Deploy en Render

El tercer job del pipeline, `render-deploy`, se ejecuta después de publicar la imagen en Docker Hub y hace un POST al deploy hook de Render. Esto desencadena automáticamente un nuevo deploy del servicio con la imagen más reciente. El flujo completo desde un `git push` hasta el nuevo deploy en producción es completamente automático y no requiere intervención manual.

La base de datos de producción corre en el servicio de PostgreSQL de Render. Un detalle que surgió durante la integración fue que la URL que Render proporciona no incluye el número de puerto, que el driver JDBC de PostgreSQL requiere obligatoriamente. La solución fue separar las credenciales de la URL y agregar el puerto 5432 explícitamente en las variables de entorno del servicio.

### Datadog

Para la observabilidad se configuró un **Log Stream** en Render que reenvía automáticamente todos los logs de la aplicación a Datadog. Esto se hace desde la configuración de la cuenta en Render, apuntando al endpoint de ingesta de logs de Datadog (`https://http-intake.logs.us5.datadoghq.com/api/v2/logs`) con la API Key como token de autenticación. Sin modificar el código ni la imagen Docker, todos los logs generados por la aplicación en producción quedan centralizados y consultables en **Datadog → Logs → Explorer**.

En el entorno local, `docker-compose.yml` incluye un contenedor adicional con el agente de Datadog para recolectar métricas y trazas. La API Key se lee desde un archivo `.env` local que está excluido del repositorio.

---

## Fase 5 — Automatización Avanzada

### Releases Automáticos con Release Please

Configuré la GitHub Action `release-please` de Google, que analiza el historial de commits en `main` y genera automáticamente un Pull Request de release con el número de versión calculado según Semantic Versioning y un changelog con todos los cambios. Los commits `feat:` incrementan la versión menor, los `fix:` incrementan el parche y los commits con `BREAKING CHANGE` o `!` incrementan la versión mayor. Cuando el PR se mergea, se crea el tag y el GitHub Release automáticamente.

### Protección de Rama

Configuré reglas de protección sobre la rama `main` en GitHub para impedir cualquier push directo. Todo cambio debe ingresar mediante un Pull Request que cumpla dos condiciones: tener al menos una revisión aprobada y el check `CI Pipeline / Build & Test` en verde. Esto garantiza que ningún cambio que rompa los tests pueda llegar a producción.

Dado que el repositorio es individual y no cuenta con otros colaboradores para aprobar revisiones, se configuró una excepción mediante el **Bypass list** del ruleset. Esto permite que el owner del repositorio pueda mergear Pull Requests sin requerir una aprobación externa, manteniendo igualmente activo el requisito de que el CI esté en verde. En un equipo real esta excepción no existiría, ya que siempre habría otro desarrollador disponible para revisar el código.

---

## Conclusión

El resultado final es un sistema donde con un simple `git push`, sin ninguna intervención adicional, los tests corren automáticamente, la imagen se construye y publica, el deploy en producción se actualiza y el historial de versiones se mantiene solo. Cada pieza tiene su rol y las responsabilidades están claramente separadas entre el código, el pipeline y la infraestructura.
