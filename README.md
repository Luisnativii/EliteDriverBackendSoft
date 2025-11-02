
# EliteDrive — Backend

## Equipo: Asesuisa

Este es el repositorio del backend de **EliteDrive**, una aplicación web para la reserva y gestión de vehículos. El sistema permite a los usuarios alquilar vehículos disponibles y a los administradores gestionar inventario, mantenimientos y reservas desde un panel centralizado.

Este backend está desarrollado con **Spring Boot** y expone una API REST consumida por el frontend implementado en React.js.

---

## 🛠️ Tecnologías utilizadas

| Capa             | Tecnología usada     |
|------------------|----------------------|
| **Backend**      | Spring Boot (Java 21)|
| **Seguridad**    | Spring Security + JWT |
| **Base de datos**| PostgreSQL 17.5      |
| **Persistencia** | Spring Data JPA      |
| **Otros**        | Docker, Render       |

---

## 📁 Estructura del proyecto

```
pnc-proyecto-final-grupo-04-s01/
├── src/
│   └── main/
│       ├── java/com/example/elitedriverbackend/
│       │   ├── config/         # Configuración general del proyecto
│       │   ├── controller/     # Controladores REST
│       │   ├── domain/         # Entidades y lógica de dominio
│       │   ├── handlers/       # Manejadores de excepciones
│       │   ├── repositories/   # Interfaces JPA
│       │   ├── security/       # Seguridad y JWT
│       │   ├── services/       # Lógica de negocio
│       │   └── EliteDriverBackendApplication.java # Clase principal
│       └── resources/
│           └── application.properties
├── Dockerfile
└── pom.xml
```

---

## 🔐 Seguridad

El proyecto implementa autenticación y autorización con **JWT (JSON Web Tokens)**.

### Roles definidos:
- `ROLE_ADMIN`: puede gestionar vehículos, reservas, mantenimientos y visualizar alertas.
- `ROLE_USER`: puede buscar vehículos, reservar y gestionar sus reservas.

---

## 🌐 Endpoints principales

> Prefijo común: `/api`

| Método | Endpoint                     | Descripción                          |
|--------|------------------------------|--------------------------------------|
| POST   | `/auth/register`             | Registro de usuario                  |
| POST   | `/auth/login`                | Login y generación de token JWT      |
| GET    | `/vehicles`                  | Obtener todos los vehículos          |
| POST   | `/vehicles`                  | Crear vehículo (admin)              |
| PUT    | `/vehicles/{id}`             | Editar vehículo (admin)             |
| DELETE | `/vehicles/{id}`             | Eliminar vehículo (admin)           |
| GET    | `/reservations`              | Obtener reservas (por rol)          |
| POST   | `/reservations`              | Crear nueva reserva                  |
| DELETE | `/reservations/{id}`         | Cancelar reserva                     |
| PUT    | `/vehicles/{id}/maintenance` | Marcar vehículo en mantenimiento     |
| GET    | `/vehicles/alerts`           | Alertas de mantenimiento (admin)     |

---

## 🧪 Pruebas

Puedes probar la API con herramientas como **Insomnia** o **Postman**. Los tokens JWT deben enviarse en el header `Authorization`:

```
Authorization: Bearer <tu-token-jwt>
```

---

## 🚀 Despliegue

### Render (recomendado)
El backend puede desplegarse fácilmente en Render.com utilizando el archivo `Dockerfile` incluido.

### Localmente con Docker

```bash
# Construcción de imagen
docker build -t elitedrive-backend .

# Ejecución de contenedor
docker run -p 8080:8080 elitedrive-backend
```

---

## 🧾 Variables de entorno (ejemplo)

Para producción o desarrollo, debes configurar:

```properties
spring.datasource.url=jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
spring.datasource.username=postgres
spring.datasource.password=admin

jwt.secret=supersecreto
jwt.expiration=86400000
```

---

## 🧑‍💼 Usuarios de prueba

### Administrador

- **Email:** `admin@example.com`  
- **Contraseña:** `adminadmin`

### Cliente (Usuario)

- Puedes registrarte desde el frontend (`/register`)

---

## 🔍 Funcionalidades clave

- Autenticación con JWT
- Registro/Login
- Gestión de vehículos (CRUD)
- Gestión de reservas y disponibilidad
- Control y alertas de mantenimiento
- Historial de mantenimiento por vehículo
- Roles diferenciados y seguridad

---

## Diagrama de la Bace de datos

![WhatsApp Image 2025-06-29 at 22 42 21_522a0a7e](https://github.com/user-attachments/assets/f016631c-9c9b-4900-8f55-af3ca904c4c9)


---

## 📝 Licencia

Este proyecto fue desarrollado como entrega final del curso **Programación N Capas - Ciclo 01-25**. Uso estrictamente académico.

---

## 🔗 Repositorios relacionados

- [Frontend - EliteDrive](https://github.com/PNC-012025/pnc-proyecto-final-frontend-grupo-04-s01)
