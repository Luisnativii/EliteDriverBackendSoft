## 📂 Estructura General de la Colección

| Carpeta / Endpoint      | Método | Descripción                               |
|-------------------------|--------|-------------------------------------------|
| **Reservation**         | —      | Endpoints para gestionar reservas         |
| — Create Reservation    | POST   | Crear una reserva                         |
| — Get All Reservations  | GET    | Obtener todas las reservas                |
| — Get By Vehicle Type   | GET    | Reservas por tipo de vehículo             |
| — Get By Vehicle        | GET    | Reservas por vehículo                     |
| — Get By Vehicle Id     | GET    | Reservas por ID de vehículo               |
| — Get By User Id        | GET    | Reservas de un usuario                    |
| — Delete Reservation    | DELETE | Eliminar reserva                          |
| — Get By Range          | GET    | Reservas dentro de un rango de fechas     |
| **Auth**                | —      | Autenticación de usuarios                 |
| — Register              | POST   | Registrar nuevo usuario                   |
| — Login                 | POST   | Inicio de sesión                          |
| **Vehicles**            | —      | Gestión de vehículos                      |
| — Get All               | GET    | Obtener todos los vehículos               |
| — Create Vehicle        | POST   | Crear un vehículo nuevo (Admin)           |
| — Get Vehicle by ID     | GET    | Obtener un vehículo por ID                |
| — Update Vehicle        | PUT    | Modificar datos del vehículo              |
| — Add Images            | POST   | Subir imágenes al vehículo                |

--------------------------------------------------------------------------------------------------

type: collection.insomnia.rest/5.0
name: EliteDriver
meta:
  id: wrk_06be8fea585a4c6ca908a45b596407e5
  created: 1750385449667
  modified: 1750385449667
collection:
  - name: Reservation
    meta:
      id: fld_9b76764be72d41b2a088dcbe9666b973
      created: 1750385672289
      modified: 1750393480995
      sortKey: -1750385672289
    children:
      - url: http://localhost:8080/api/reservations
        name: Create Reservation
        meta:
          id: req_4d4387ff608b4946a33cfd7523ab7e36
          created: 1750385676187
          modified: 1750387411882
          isPrivate: false
          sortKey: -1750385676187
        method: POST
        body:
          mimeType: application/json
          text: |-
            {
              "userId": "d2985821-720c-41e1-b06b-c82943d6e5fc",
              "vehicleId": "3f2829a2-8e94-49e7-a563-bf9dc412e9d4",
              "startDate": "2025-06-20",
              "endDate": "2025-06-22"
            }
        headers:
          - name: Content-Type
            value: application/json
            id: pair_365cfba5ea0349bbbfb139f77bb35327
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_6e3b5a8884054a59a73f203083d52da2
          - id: pair_e3224c955b72447da2476aa6e5e30041
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations
        name: Get All Reservation
        meta:
          id: req_dc7d6f0d3dc14af4a0551c75994388ad
          created: 1750388407392
          modified: 1750388616947
          isPrivate: false
          sortKey: -1750388407392
        method: GET
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations/vehicleType?vehicleType=PickUp
        name: Get By Vehicle Type
        meta:
          id: req_c0534d42474d4363905e843b0e61fec8
          created: 1750388873785
          modified: 1750392737474
          isPrivate: false
          sortKey: -1750387041789.5
        method: GET
        body:
          mimeType: application/json
        headers:
          - name: Content-Type
            value: application/json
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations
        name: Get By Vehicle
        meta:
          id: req_ad1fcdbe5132472f84712d2801f64c6d
          created: 1750391526443
          modified: 1750391526443
          isPrivate: false
          sortKey: -1750387724590.75
        method: GET
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations/vehicle?vehicleId=3f2829a2-8e94-49e7-a563-bf9dc412e9d4
        name: Get By Vehicle Id
        meta:
          id: req_52a15984c4d8459fba37a0beb095e019
          created: 1750392987741
          modified: 1750393372714
          isPrivate: false
          sortKey: -1750387383190.125
        method: GET
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations/user?userId=fa514a07-7e34-4452-be81-b8ffb0d8b7bb
        name: Get By User Id
        meta:
          id: req_c71ed5d5911f43c3a9e8e7ac11dd34e6
          created: 1750393493125
          modified: 1750393605836
          isPrivate: false
          sortKey: -1750387212489.8125
        method: GET
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations/b149a38b-c3e0-4bb9-8fb0-6eec2ab481df
        name: Get By Id
        meta:
          id: req_00cbaef52e314add8473d0c9094a36e1
          created: 1750393936178
          modified: 1750395000309
          isPrivate: false
          sortKey: -1750387297839.9688
        method: DELETE
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
      - url: http://localhost:8080/api/reservations/date?startDate=2025-06-20&endDate=2025-06-22
        name: Get By Range
        meta:
          id: req_7e2a83ecc3b54d449120c4f446519a3e
          created: 1750394752211
          modified: 1750395014549
          isPrivate: false
          sortKey: -1750387553890.4375
        method: GET
        headers:
          - name: User-Agent
            value: insomnia/11.0.2
            id: pair_c10ab7c90abf473abb98f0649d5cc97c
          - id: pair_78542c83281a4ba0b48a73f7f2d5dab0
            name: Authorization
            value: Bearer
              eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDM4NjIxNCwiZXhwIjoxNzUwNDcyNjE0fQ.pwrzOSqoJGigXf_4EJ7DxKtLGqiHRNKvwDiqXqOt7AE
            disabled: false
        settings:
          renderRequestBody: true
          encodeUrl: true
          followRedirects: global
          cookies:
            send: true
            store: true
          rebuildPath: true
  - url: http://localhost:8080/api/auth/register
    name: register
    meta:
      id: req_f0a94e5b278745debb90e5ed9696530b
      created: 1750133859988
      modified: 1750385557134
      isPrivate: false
      sortKey: -1750133859988
    method: POST
    body:
      mimeType: application/json
      text: |
        {
                  "firstName": "Kelvin",
                  "lastName": "Morales",
                  "birthDate": "2003-02-11",
                  "dui": "06466646-0",
                  "phoneNumber": "7880-4203",
                  "email": "isiraheta@hotmail.com",
                  "password": "admin123",
                  "confirmPassword": "admin123"
        }
    headers:
      - name: Content-Type
        value: application/json
      - name: User-Agent
        value: insomnia/11.2.0
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/vehicles
    name: get all vehicles
    meta:
      id: req_f4e5ec9bdac84f6bb49da4aec8bdcf65
      created: 1750133876956
      modified: 1763492941848
      isPrivate: false
      sortKey: -1750133876956
    method: GET
    parameters:
      - disabled: false
        id: pair_ca8bddf19e794d079330a346daf150f6
    headers:
      - name: Authorization
        value: Bearer
          eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc2MzQ5MjMwOSwiZXhwIjoxNzY0Nzg4MzA5fQ.G_ioy0cr-tqCxnwLc9BNyidlM5_YSZiWEgEnFBgiVac
        id: pair_ef833b1afb8b4f64ae0ec14abc59ef9a
      - name: User-Agent
        value: insomnia/11.2.0
        id: pair_234a886febf24a268adba2047a8a16c6
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/vehicles
    name: admin create a vehicle
    meta:
      id: req_c51457eb90354ed8a641ab43f666a0c7
      created: 1750280999429
      modified: 1763521729118
      isPrivate: false
      sortKey: -1750133868472
    method: POST
    body:
      mimeType: application/json
      text: |
        {
          "name": "Toyota MESSI",
          "brand": "Toyota",
          "model": "2024",
          "capacity": 5,
          "pricePerDay": 40,
          "kilometers": 20000,
          "kmForMaintenance": 10000,
          "features": ["A/C", "Bluetooth"],
        	"vehicleType": { "type": "SUV" }
        }
    parameters:
      - id: pair_af73b705a24041ee913ddfb40753c904
        disabled: false
    headers:
      - name: Content-Type
        value: application/json
      - name: Authorization
        value: Bearer
          eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc2MzQ5MjMwOSwiZXhwIjoxNzY0Nzg4MzA5fQ.G_ioy0cr-tqCxnwLc9BNyidlM5_YSZiWEgEnFBgiVac
        id: pair_e5f3da25e8db443080efd2c9324bf906
      - name: User-Agent
        value: insomnia/11.2.0
        id: pair_a50b66357fe64f92a8e4688e09ca0007
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/vehicles/7bf4a31c-2ba1-4946-a94c-df4324dbdbe5
    name: get vehicle by id
    meta:
      id: req_e48abff924904122b578f3f1c1d4f790
      created: 1750282099257
      modified: 1750288980006
      isPrivate: false
      sortKey: -1750282099257
    method: GET
    headers:
      - name: Content-Type
        value: application/json
      - name: Authorization
        value: Bearer
          eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDI3NzUxMywiZXhwIjoxNzUwMzYzOTEzfQ.uTCcOdiuyl90dm4iMZXf8ml8HrvgU14QrzD_I5MG6LU
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/vehicles/9dd85a51-d442-46fa-98ac-79e9e529b4bd
    name: admin update a vehicle
    meta:
      id: req_ba75171e3d3a4326b486e493b9c0ff8e
      created: 1750311596715
      modified: 1750314361230
      isPrivate: false
      sortKey: -1750133864230
    method: PUT
    body:
      mimeType: application/json
      text: >-
        {
          "pricePerDay": 41.00,
          "kilometers": 15000,
          "features": ["Bluetooth", "Polarizado", "Transmisión automática", "GPS"],
          "vehicleType": {
            "type": "d54f60f1-fffd-4f0a-be91-46a5db56e498"
          }
        }
    parameters:
      - disabled: false
        id: pair_ca8bddf19e794d079330a346daf150f6
    headers:
      - name: Authorization
        value: Bearer
          eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc1MDI3NzUxMywiZXhwIjoxNzUwMzYzOTEzfQ.uTCcOdiuyl90dm4iMZXf8ml8HrvgU14QrzD_I5MG6LU
      - name: Content-Type
        value: application/json
      - name: User-Agent
        value: insomnia/11.2.0
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/auth/login
    name: loginAdmin
    meta:
      id: req_d6e975c5e475408fa64b70d67c689c8c
      created: 1750385583391
      modified: 1763451921208
      isPrivate: false
      sortKey: -1749527773876.5
    method: POST
    body:
      mimeType: application/json
      text: |-
        {
                  "email": "admin@example.com",
                  "password": "adminadmin"
        }
    headers:
      - name: Content-Type
        value: application/json
      - name: User-Agent
        value: insomnia/11.2.0
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/auth/login
    name: login
    meta:
      id: req_ac3c507932a64c15bfdf4b7570d70b36
      created: 1750388675244
      modified: 1750388692958
      isPrivate: false
      sortKey: -1749224735985.75
    method: POST
    body:
      mimeType: application/json
      text: |-
        {
                  "email": "isiraheta@hotmail.com",
                  "password": "admin123"
        }
    headers:
      - name: Content-Type
        value: application/json
      - name: User-Agent
        value: insomnia/11.2.0
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
  - url: http://localhost:8080/api/vehicles/4bbaf85a-2806-4e55-adf8-a54db4304cb8/images
    name: Add Images
    meta:
      id: req_a31fb434d61241eb960cf6c86d27eeb6
      created: 1763493056913
      modified: 1763521769139
      isPrivate: false
      sortKey: -1750133872714
    method: POST
    body:
      mimeType: multipart/form-data
      params:
        - id: pair_b2b5d5efe5b14ff29fed2e051bfed2f8
          name: mainImage
          disabled: false
          type: file
          fileName: C:\Users\isira\Desktop\Carpeta xd\Imagenes\Kyojuro.jpg
        - id: pair_0f1ea6bb1b444818bc07a9505bc333b5
          name: listImages
          disabled: false
          type: file
          fileName: C:\Users\isira\Desktop\Carpeta xd\Imagenes\Tanjiro.jpg
        - id: pair_6aa0b94548954894b74d86b8fa8bd101
          name: listImages
          disabled: false
          type: file
          fileName: C:\Users\isira\Desktop\Carpeta xd\Imagenes\Razor.jpg
        - id: pair_ad140e5e55444e41882039dbbe291531
          name: listImages
          disabled: false
          type: file
          fileName: C:\Users\isira\Desktop\Carpeta xd\Imagenes\Jett RED.jpg
    parameters:
      - disabled: false
        id: pair_ca8bddf19e794d079330a346daf150f6
    headers:
      - name: Content-Type
        value: multipart/form-data
      - name: Authorization
        value: Bearer
          eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTc2MzQ5MjMwOSwiZXhwIjoxNzY0Nzg4MzA5fQ.G_ioy0cr-tqCxnwLc9BNyidlM5_YSZiWEgEnFBgiVac
        id: pair_ef833b1afb8b4f64ae0ec14abc59ef9a
      - name: User-Agent
        value: insomnia/11.2.0
        id: pair_234a886febf24a268adba2047a8a16c6
    settings:
      renderRequestBody: true
      encodeUrl: true
      followRedirects: global
      cookies:
        send: true
        store: true
      rebuildPath: true
cookieJar:
  name: Default Jar
  meta:
    id: jar_f99dfee650e742478ee9b012fa8aeeeb
    created: 1750133828126
    modified: 1750133828126
environments:
  name: Base Environment
  meta:
    id: env_2067b9bfec364bb8a3b0bad4239a08cf
    created: 1750133828123
    modified: 1750133828123
    isPrivate: false
