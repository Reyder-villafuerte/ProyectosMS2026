# Proyecto Microservicios Cafeteria

Guia rapida para levantar, detener y limpiar el entorno completo en Windows (PowerShell).

## 1) Arranque limpio (reset total recomendado)

Ejecuta estos comandos cuando quieras empezar desde cero.

```powershell
# 1. Detener y borrar contenedores/volumenes de infra y servicios
cd C:\ms\ProyectosMS2026\infra
docker compose down -v --remove-orphans

cd C:\ms\ProyectosMS2026\services\catalogo
docker compose down -v --remove-orphans
docker compose -f docker-compose-dev.yml down -v --remove-orphans

cd C:\ms\ProyectosMS2026\services\producto
docker compose down -v --remove-orphans
docker compose -f docker-compose-dev.yml down -v --remove-orphans

cd C:\ms\ProyectosMS2026\services\pedido
docker compose down -v --remove-orphans
docker compose -f docker-compose-dev.yml down -v --remove-orphans

cd C:\ms\ProyectosMS2026\services\user
docker compose down -v --remove-orphans
docker compose -f docker-compose-dev.yml down -v --remove-orphans

# 2. (Opcional) borrar imagenes dangling/cache de build
docker image prune -f
docker builder prune -f
```

## 2) Liberar puertos en Windows (si algo quedo colgado)

Puertos usados en este proyecto:
- Infra (host): `7072`, `7082`, `7092`
- Infra (apps en local): `7071`, `7081`, `7091`
- Observabilidad (host): `3000` (Grafana), `3100` (Loki), `9090` (Prometheus)
- Servicios (local): `8081` (catalogo), `9091` (producto), `9093` (pedido)
- MySQL dev/prod host: `3307`, `3391`, `3393`, `3394`, `3395`

Para identificar y matar un proceso por puerto:

```powershell
# Ver proceso usando un puerto (ejemplo 9091)
Get-NetTCPConnection -LocalPort 9091 -State Listen | Select-Object LocalPort, OwningProcess

# Matar proceso por PID (ejemplo)
Stop-Process -Id <PID> -Force
```

## 3) Levantar DEV (todo, incluyendo observabilidad)

Abrir 7 terminales para apps Java (y 1 adicional si quieres ver logs de Docker en vivo).

### Terminal 1 - Config Server
```powershell
cd C:\ms\ProyectosMS2026\infra\config-server
mvn spring-boot:run
```

### Terminal 2 - Registry Server (Eureka)
```powershell
cd C:\ms\ProyectosMS2026\infra\registry-server
mvn spring-boot:run
```

### Terminal 3 - Gateway
```powershell
cd C:\ms\ProyectosMS2026\infra\gateway
mvn spring-boot:run
```

### Terminal 3.1 - Observabilidad (Docker: Prometheus + Grafana + Loki + Promtail)
```powershell
cd C:\ms\ProyectosMS2026\infra
docker compose up -d prometheus loki promtail grafana
```

### Terminal 4 - Producto (DB dev + app)
```powershell
cd C:\ms\ProyectosMS2026\services\producto
docker compose -f docker-compose-dev.yml up -d
mvn spring-boot:run
```

### Terminal 5 - Pedido (DB dev + app)
```powershell
cd C:\ms\ProyectosMS2026\services\pedido
docker compose -f docker-compose-dev.yml up -d
mvn spring-boot:run
```

### Terminal 6 - Catalogo (opcional, pero recomendado)
```powershell
cd C:\ms\ProyectosMS2026\services\catalogo
docker compose -f docker-compose-dev.yml up -d
mvn spring-boot:run
```

### Terminal 7 - User (DB dev + app)
```powershell
cd C:\ms\ProyectosMS2026\services\user
docker compose -f docker-compose-dev.yml up -d
mvn spring-boot:run
```

## 4) Levantar PROD (Docker completo)

### 4.1 Infra (Docker)
```powershell
cd C:\ms\ProyectosMS2026\infra
docker compose up -d config-server registry-server gateway prometheus loki promtail grafana
```

### 4.2 Servicios (Docker)
```powershell
cd C:\ms\ProyectosMS2026\services\catalogo
copy .env.example .env
docker compose up -d

cd C:\ms\ProyectosMS2026\services\producto
copy .env.example .env
docker compose up -d

cd C:\ms\ProyectosMS2026\services\pedido
copy .env.example .env
docker compose up -d

cd C:\ms\ProyectosMS2026\services\user
copy .env.example .env
docker compose up -d
```

## 5) Detener todo

### Detener DEV
Si los servicios Java estan en `mvn spring-boot:run`, detener con `Ctrl + C` en cada terminal.

Luego bajar las DB de dev:

```powershell
cd C:\ms\ProyectosMS2026\services\catalogo
docker compose -f docker-compose-dev.yml down

cd C:\ms\ProyectosMS2026\services\producto
docker compose -f docker-compose-dev.yml down

cd C:\ms\ProyectosMS2026\services\pedido
docker compose -f docker-compose-dev.yml down

cd C:\ms\ProyectosMS2026\services\user
docker compose -f docker-compose-dev.yml down
```

### Detener PROD
```powershell
cd C:\ms\ProyectosMS2026\services\catalogo
docker compose down

cd C:\ms\ProyectosMS2026\services\producto
docker compose down

cd C:\ms\ProyectosMS2026\services\pedido
docker compose down

cd C:\ms\ProyectosMS2026\services\user
docker compose down

cd C:\ms\ProyectosMS2026\infra
docker compose down
```

## 6) URLs utiles

### Infra
- Config Server (host): `http://localhost:7072`
- Eureka (host): `http://localhost:7082`
- Gateway (host): `http://localhost:7092`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (user/pass inicial: `admin` / `admin`)
- Loki API: `http://localhost:3100`

### Servicios directos (DEV local)
- Catalogo: `http://localhost:8081`
- Producto: `http://localhost:9091`
- Pedido: `http://localhost:9093`
- User: `http://localhost:9095`

### Swagger directos (DEV local) - verificados
- Catalogo Swagger: `http://localhost:8081/swagger-ui/index.html`
- Producto Swagger: `http://localhost:9091/swagger-ui/index.html`
- Pedido Swagger: `http://localhost:9093/swagger-ui/index.html`
- User Swagger: `http://localhost:9095/swagger-ui/index.html`

### Swagger via Gateway (DEV/PROD)
- Catalogo: `http://localhost:7092/catalogo/swagger-ui/index.html`
- Producto: `http://localhost:7092/producto/swagger-ui/index.html`
- Pedido: `http://localhost:7092/pedido/swagger-ui/index.html`
- User: `http://localhost:7092/user/swagger-ui/index.html`

### Endpoints principales via Gateway
- Catalogo API: `http://localhost:7092/api/v1/categorias`
- Producto API: `http://localhost:7092/api/v1/productos`
- Pedido API: `http://localhost:7092/api/v1/pedidos`
- User API: `http://localhost:7092/api/v1/users`

## 7) Verificacion rapida

```powershell
# Estado de contenedores
docker ps

# Probar salud basica por gateway
curl http://localhost:7092/api/v1/productos
curl http://localhost:7092/api/v1/pedidos
```

Pruebas utiles adicionales:

```powershell
# Swagger directos (como en tus pruebas)
start http://localhost:8081/swagger-ui/index.html
start http://localhost:9091/swagger-ui/index.html

# Endpoints de metricas (prometheus format)
curl http://localhost:8081/actuator/prometheus
curl http://localhost:9091/actuator/prometheus
curl http://localhost:9093/actuator/prometheus
curl http://localhost:9095/actuator/prometheus
curl http://localhost:7091/actuator/prometheus
curl http://localhost:7081/actuator/prometheus
curl http://localhost:7071/actuator/prometheus
```

## 8) Resiliencia (Circuit Breaker en pedido -> producto)

El microservicio `pedido` tiene Circuit Breaker `productoService` para llamadas Feign a `producto`:
- consulta de producto al crear pedido
- descuento de stock al confirmar pedido

Prueba rapida:

```powershell
# 1) Levanta infra + producto + pedido
# 2) Deten producto (simular caida)
cd C:\ms\ProyectosMS2026\services\producto
# si corre en mvn, Ctrl + C; si corre en docker:
docker compose down

# 3) Intenta crear pedido por gateway
curl -X POST http://localhost:7092/api/v1/pedidos -H "Content-Type: application/json" -d "{\"idUsuario\":1,\"detalles\":[{\"idProducto\":1,\"cantidad\":1}]}"
```

Resultado esperado: respuesta `503 Service Unavailable` controlada por fallback.

## 9) Observabilidad y trazabilidad (Prometheus + Grafana + Loki)

La infraestructura `infra/docker-compose.yml` ya incluye:
- `prometheus` (scrapea `/actuator/prometheus` de infra y microservicios)
- `grafana` (con datasource preconfigurado a Prometheus y Loki)
- `loki` (almacen de logs)
- `promtail` (envia logs de contenedores Docker a Loki)

### 9.1 Levantar stack de observabilidad

```powershell
cd C:\ms\ProyectosMS2026\infra
docker compose up -d prometheus loki promtail grafana
```

### 9.2 Endpoints de metricas por servicio

Todos los servicios tienen habilitado:

```text
/actuator/prometheus
```

Ejemplos:

```powershell
curl http://localhost:9091/actuator/prometheus   # producto (dev local)
curl http://localhost:8081/actuator/prometheus   # catalogo (dev local)
curl http://localhost:9093/actuator/prometheus   # pedido (dev local)
curl http://localhost:9095/actuator/prometheus   # user (dev local)
curl http://localhost:7091/actuator/prometheus   # gateway (dev local)
curl http://localhost:7081/actuator/prometheus   # registry-server (dev local)
curl http://localhost:7071/actuator/prometheus   # config-server (dev local)
```

### 9.3 Verificacion rapida en Grafana

1. Abre `http://localhost:3000`
2. Login: `admin` / `admin`
3. Ve a Explore:
   - Fuente `Prometheus`: consulta `up`
   - Fuente `Loki`: consulta `{container=~".+"}`

### 9.4 Notas importantes

- En DEV (apps con `mvn spring-boot:run`), Prometheus usa `host.docker.internal` para llegar a puertos locales.
- En PROD (apps en Docker), Prometheus usa nombres de servicio (`gateway`, `producto`, `pedido`, etc.).
- Si no ves logs en Loki, verifica que Docker Desktop permita acceso al socket Docker en Linux containers.
"# ProyectosMS2026" 
