# Microservicio de restaurantes — Plazoleta

API para crear restaurantes, administrar sus platos y asociar empleados. Valida
los JWT emitidos por el microservicio de usuarios mediante el secreto compartido.

## Stack

- Java 17
- Spring Boot 3.3.13 y Spring Cloud OpenFeign
- Spring Web, Spring Data JPA, Bean Validation y Spring Security
- MySQL 8
- JJWT 0.12.6
- MapStruct, Lombok y OpenAPI / Swagger UI

## Requisitos y ejecución en Windows

Requiere JDK 17, MySQL 8, la base `plazoleta_restaurante` y el microservicio
usuarios en `http://localhost:8080`. Los dos servicios deben tener idéntico
`APP_JWT_SECRET`.

```powershell
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS plazoleta_restaurante;"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "<contraseña-local-de-MySQL>"
$env:APP_JWT_SECRET = "<secreto-local-compartido-de-32-caracteres-o-más>"
$env:USUARIO_API_URL = "http://localhost:8080"
Set-Location <ruta>\restaurante
.\mvnw.cmd spring-boot:run
```

`DB_URL` es opcional y por defecto apunta a
`jdbc:mysql://localhost:3306/plazoleta_restaurante`. Los tests usan H2 y no
necesitan MySQL:

```powershell
.\mvnw.cmd clean verify
```

Swagger UI: <http://localhost:8081/swagger-ui/index.html>

## Pedidos y notificaciones SMS

Los pedidos recorren los estados `PENDIENTE`, `EN_PREPARACION`, `LISTO`,
`ENTREGADO` o `CANCELADO`. Para HU14, configura `APP_INTERNAL_TOKEN` con el
mismo valor de al menos 32 caracteres en ambos microservicios, y configura
`TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN` y `TWILIO_PHONE_NUMBER` con las
credenciales del proveedor. No guardes estos valores en Git. Las pruebas usan
adaptadores simulados y no requieren una cuenta de Twilio.

## Endpoints implementados

Los endpoints paginados de restaurantes, platos y pedidos devuelven el mismo
`PageResponse<T>`: `contenido`, `pagina` (base cero), `tamano`,
`totalElementos` y `totalPaginas`. La consulta aplica paginación con Criteria
API y no utiliza Spring `Pageable`.

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | `/restaurantes?page=0&size=10` | Lista alfabética paginada | Público |
| POST | `/restaurantes` | Crea restaurante y valida propietario con usuarios | ADMINISTRADOR |
| GET | `/platos?idRestaurante=1&page=0&size=10` | Lista platos activos | Público |
| GET | `/platos?idRestaurante=1&todos=true` | Incluye inactivos del propietario autenticado | PROPIETARIO |
| POST | `/platos` | Crea un plato | PROPIETARIO del restaurante |
| PUT | `/platos/{id}` | Modifica precio y descripción | PROPIETARIO del restaurante |
| PATCH | `/platos/{id}/estado` | Habilita o deshabilita un plato | PROPIETARIO del restaurante |
| POST | `/empleados` | Crea y asocia empleado | PROPIETARIO |
| POST | `/pedidos` | Crea un pedido | CLIENTE |
| GET | `/pedidos?estado=PENDIENTE&page=0&size=10` | Lista pedidos del restaurante del empleado | EMPLEADO |
| PATCH | `/pedidos/{id}/asignar` | Asigna el pedido e inicia preparación | EMPLEADO |
| PATCH | `/pedidos/{id}/listo` | Marca listo y notifica el PIN por SMS | Empleado asignado |
| PATCH | `/pedidos/{id}/entregar` | Entrega con el PIN de seis dígitos | EMPLEADO |
| PATCH | `/pedidos/{id}/cancelar` | Cancela un pedido pendiente propio | CLIENTE |
| GET | `/pedidos/{id}/trazabilidad` | Consulta el historial de estados propio | CLIENTE |
| GET | `/pedidos/eficiencia?idRestaurante=1` | Consulta tiempos y ranking del restaurante | PROPIETARIO |

Las operaciones autenticadas esperan `Authorization: Bearer <JWT>`. La integración Feign hacia usuarios conserva el token de la petición.

## Datos de demostración

El informe de eficiencia incluye pedidos entregados; sus duraciones y promedios
se expresan en segundos, con el ranking ordenado de menor a mayor tiempo.

Cuando `restaurante` y `plato` están vacías, el inicializador crea los
restaurantes Sabor Paisa y Sushi Go y ocho platos (seis activos y dos inactivos).
Los propietarios configurables `APP_SEED_JUAN_OWNER_ID` y
`APP_SEED_MARIA_OWNER_ID` usan por defecto los IDs 2 y 3 del inicializador de
usuarios. Ejecuta primero usuarios en una base nueva para que esos IDs
correspondan a Juan y Maria. El inicializador no cambia tablas que ya tengan
datos.

## Docker

```powershell
docker build -t plazoleta-restaurante .
docker run --rm -p 8081:8081 `
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/plazoleta_restaurante" `
  -e DB_USERNAME="root" `
  -e DB_PASSWORD="<contraseña>" `
  -e APP_JWT_SECRET="<mismo-secreto-que-usuarios>" `
  -e USUARIO_API_URL="http://host.docker.internal:8080" `
  plazoleta-restaurante
```

## Alcance

Este microservicio persiste restaurantes, platos, empleados y pedidos. El
historial de estados y los datos de eficiencia se consultan desde las APIs de
pedidos; el acceso se limita al cliente propietario o al propietario del
restaurante correspondiente.
