# 🚗 EliteDrive — Backend

## Equipo: Asesuisa

**EliteDrive** es el backend del sistema web para la **reserva y gestión de vehículos** desarrollado por el equipo *Asesuisa*.  
Provee una API REST segura y optimizada que permite a los usuarios alquilar vehículos, gestionar reservas y mantener el historial de mantenimiento de cada unidad.

---

## 🧩 Descripción general

Este proyecto fue desarrollado con **Spring Boot 3.5.0 (Java 21)** y se conecta a una base de datos **PostgreSQL 17.5**.  
Integra autenticación con **JWT**, control de roles, optimización de consultas y documentación técnica actualizada para facilitar la instalación y mantenimiento.

---

## 🛠️ Tecnologías utilizadas

| Capa             | Tecnología usada     |
|------------------|----------------------|
| **Backend**      | Spring Boot (Java 21)|
| **Seguridad**    | Spring Security + JWT|
| **Base de datos**| PostgreSQL 17.5      |
| **Persistencia** | Spring Data JPA      |
| **Despliegue**   | Docker + Render      |
| **Documentación**| Markdown / README técnico |

---

## 📁 Estructura del proyecto

```
EliteDriverBackendSoft/
├── src/
│   └── main/
│       ├── java/com/example/elitedriverbackend/
│       │   ├── config/         # Configuración general
│       │   ├── controller/     # Controladores REST (Auth, Vehicle, Reservation, Maintenance)
│       │   ├── domain/         # Entidades, DTOs y lógica de negocio
│       │   ├── repositories/   # Interfaces JPA
│       │   ├── security/       # Configuración JWT y roles
│       │   ├── services/       # Lógica de negocio principal
│       │   └── EliteDriverBackendApplication.java
│       └── resources/
│           └── application.properties
├── Dockerfile
└── pom.xml
```

---

## 🔐 Seguridad y Roles

El sistema utiliza **Spring Security + JWT** para el control de acceso.  

### Roles definidos
- **ROLE_ADMIN** → puede gestionar vehículos, mantenimientos y reservas.  
- **ROLE_USER** → puede buscar vehículos y realizar reservas.

El token JWT debe enviarse en todas las peticiones protegidas mediante el encabezado:

```
Authorization: Bearer <tu-token-jwt>
```

---

## 🌐 Endpoints principales

> Todos los endpoints comienzan con el prefijo:  
> **`/api`**

---

### 🔑 Autenticación (`/api/auth`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/register` | Registrar un nuevo usuario. |
| POST | `/login` | Iniciar sesión y obtener token JWT. |
| GET  | `/validate` | Validar un token JWT. |

**Ejemplo JSON de registro:**
```json
{
  "firstName": "Juan",
  "lastName": "Palacios",
  "email": "juan@example.com",
  "password": "Admin123",
  "confirmPassword": "Admin123"
}
```

**Respuesta:**
```json
{ "message": "Usuario registrado exitosamente" }
```

---

### 🚘 Vehículos (`/api/vehicles`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Listar todos los vehículos. |
| GET | `/{id}` | Obtener vehículo por ID. |
| POST | `/` | Registrar un nuevo vehículo (solo admin). |
| PUT | `/{id}` | Actualizar vehículo existente. |
| DELETE | `/{id}` | Eliminar vehículo. |
| POST | `/by-type` | Buscar por tipo de vehículo. |
| GET | `/by-capacity` | Filtrar por capacidad. |
| GET | `/available` | Listar vehículos disponibles por rango de fechas. |

**Ejemplo JSON de creación:**
```json
{
  "name": "Toyota Hilux",
  "brand": "Toyota",
  "model": "2024",
  "pricePerDay": 75.0,
  "capacity": 5,
  "vehicleType": "Pickup",
  "mainImageUrl": "https://img.toyota.com/hilux.jpg"
}
```

---

### 📅 Reservas (`/api/reservations`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/` | Crear nueva reserva. |
| GET | `/` | Listar todas las reservas. |
| GET | `/{id}` | Obtener reserva por ID. |
| GET | `/date?startDate=dd-MM-yyyy&endDate=dd-MM-yyyy` | Buscar reservas dentro de un rango de fechas. |
| GET | `/user?userId={uuid}` | Obtener reservas de un usuario. |
| GET | `/vehicle?vehicleId={uuid}` | Obtener reservas de un vehículo específico. |
| GET | `/vehicleType?vehicleType={tipo}` | Obtener reservas filtradas por tipo de vehículo. |
| DELETE | `/{id}` | Eliminar / cancelar una reserva. |

**Ejemplo JSON de reserva:**
```json
{
  "userId": "c6b7e3fa-2f42-4e0b-bc81-3b2b2f1a6c84",
  "vehicleId": "8e21ef70-70b9-44e0-b6c0-324e1b39f29b",
  "startDate": "28-11-2025",
  "endDate": "30-11-2025"
}
```

**Respuesta:**
```json
{
  "status": "confirmado",
  "totalPrice": 150.0,
  "vehicle": { "name": "Toyota Hilux", "pricePerDay": 75.0 },
  "user": { "firstName": "Juan", "email": "juan@example.com" }
}
```

---

### 🛠️ Mantenimientos (`/api/maintenances`) *(ÉPICA 2)*

> Esta sección se activará con el nuevo `MaintenanceController`.

**Objetivo:** permitir registrar mantenimientos como una entidad separada, vinculada a cada vehículo.

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/` | Registrar nuevo mantenimiento asociado a un vehículo. |
| GET | `/vehicle/{id}` | Listar historial de mantenimientos de un vehículo. |
| GET | `/alerts` | Mostrar vehículos que requieren mantenimiento. |

**Ejemplo JSON de mantenimiento:**
```json
{
  "vehicleId": "b12e3f70-70b9-44e0-b6c0-324e1b39f29b",
  "date": "2025-11-01",
  "serviceType": "Cambio de aceite",
  "description": "Mantenimiento preventivo de motor",
  "cost": 45.00,
  "mileage": 10500
}
```

---

## ⚙️ Optimización de flujos de datos (ÉPICA 6)

El backend fue optimizado para mejorar el rendimiento general:

1. **Reducción de peticiones redundantes** entre frontend y backend.  
2. Implementación de **paginación** en endpoints con grandes volúmenes de datos.  
3. Ajuste de **relaciones JPA Lazy/Eager** para optimizar memoria.  
4. Preparación para **caché** en consultas frecuentes.  
5. Validación y manejo de errores centralizado con `ResponseStatusException` y logs controlados.

---

## 📘 Guía de instalación local

### 1️⃣ Requisitos previos
- **Java 21**
- **Maven**
- **PostgreSQL 17.5**
- **Docker** (opcional)
- **Render CLI** (para despliegue)

### 2️⃣ Variables de entorno

Archivo `.env` o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/elitedriver
spring.datasource.username=postgres
spring.datasource.password=admin

jwt.secret=supersecreto
jwt.expiration=86400000
```

### 3️⃣ Ejecución local
```bash
mvn spring-boot:run
```
API disponible en:  
👉 **http://localhost:8080/api**

### 4️⃣ Ejecución con Docker
```bash
docker build -t elitedriver-backend .
docker run -p 8080:8080 elitedriver-backend
```

---

## 🧩 Despliegue en Render

1. Conectar el repositorio GitHub.  
2. Configurar variables de entorno en Render.  
3. Deploy automático tras cada push en la rama principal.

---

## 🧾 Usuarios de prueba

| Rol | Email | Contraseña |
|------|--------|------------|
| Admin | admin@example.com | adminadmin |
| Usuario | (registrarse desde el frontend) | — |


---

## 📄 Licencia

Proyecto académico del curso **Programación en N Capas — Ciclo 01-25**.  
Uso estrictamente educativo.
