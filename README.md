# 📦 Franchise API – Despliegue con Docker y Terraform

API RESTful reactiva para la gestión de franquicias, sucursales y productos, construida con **Spring WebFlux** y desplegada en **AWS ECS Fargate** mediante **Terraform**.

---

## 📑 Tabla de Contenidos

1. [Requisitos Previos](#-requisitos-previos)
2. [Permisos IAM Requeridos](#-permisos-iam-requeridos)
3. [Arquitectura del Sistema](#-arquitectura-del-sistema)
4. [Ejecutar Localmente con Docker](#-1-ejecutar-la-imagen-docker-localmente)
5. [Subir Imagen a Amazon ECR](#-2-subir-imagen-a-amazon-ecr)
6. [Desplegar en AWS con Terraform](#-3-desplegar-en-aws-con-terraform)
7. [Swagger UI](#-swagger-ui)
8. [Notas](#-notas)
9. [Soporte](#-soporte)

---

## 🔧 Requisitos Previos

Asegúrate de tener instaladas y configuradas las siguientes herramientas antes de continuar:

| Herramienta | Versión Mínima | Enlace |
|---|---|---|
| Docker | 20.x | https://docs.docker.com/get-docker/ |
| Terraform | 1.x | https://developer.hashicorp.com/terraform/downloads |
| AWS CLI | 2.x | https://aws.amazon.com/cli/ |
| Java (JDK) | 17 | https://adoptium.net/ |

> 💡 Configura tus credenciales de AWS ejecutando `aws configure` antes de cualquier despliegue.

---

## 🔐 Permisos IAM Requeridos

El usuario o rol de AWS utilizado para desplegar esta infraestructura debe tener adjuntas las siguientes políticas administradas por AWS. En este proyecto están asignadas al grupo **`nequi`**.

| Política IAM | Servicio        | Descripción                                                               |
|---|-----------------|---------------------------------------------------------------------------|
| `AmazonAPIGatewayAdministrator` | API Gateway     | Crear, configurar y administrar todos los recursos de Amazon API Gateway. |
| `AmazonEC2ContainerRegistryFullAccess` | ECR             | Subir, descargar y administrar imágenes Docker en Amazon ECR.             |
| `AmazonECS_FullAccess` | ECS             | Crear y administrar clústeres, servicios y tareas en ECS Fargate.         |
| `AmazonRDSDataFullAccess` | RDS             | Ejecutar consultas SQL sobre bases de datos RDS mediante el Data API.     |
| `AmazonRDSFullAccess` | RDS             | Crear, modificar y eliminar instancias de base de datos RDS.              |
| `AmazonVPCFullAccess` | VPC             | Administrar VPCs, subredes, tablas de ruteo, IGW y NAT Gateways.          |
| `ElasticLoadBalancingFullAccess` | ALB             | Crear y administrar Application Load Balancers.                           |
| `IAMFullAccess` | IAM             | Crear y administrar roles y políticas IAM necesarios para ECS.            |
| `SecretsManagerReadWrite` | Secrets Manager | Leer y escribir secretos (ej. credenciales de base de datos).             |
| `CloudFrontFullAccess` | CloudFront      | Permite disponibiliar contenido front                                     |

> ⚠️ **Advertencia de seguridad:** `IAMFullAccess` otorga privilegios elevados. En entornos productivos se recomienda reemplazarlo por una política personalizada con permisos mínimos necesarios.

### Pasos para asignar los permisos

1. Inicia sesión en la consola de AWS con un usuario administrador.
2. Navega a **IAM → Grupos de usuarios** y selecciona el grupo `nequi`.
3. En la pestaña **Permisos**, haz clic en **Agregar permisos → Adjuntar políticas**.
4. Busca y selecciona cada política de la tabla anterior.
5. Haz clic en **Agregar permisos** para confirmar.

---

## 🏗️ Arquitectura del Sistema

```
Internet
   │
   ▼
┌──────────────────────┐
│     CloudFront       │  ← Punto de entrada público (HTTPS)
│  (CDN + caché UI)    │    Expone la API y Swagger UI
└──────────┬───────────┘
           │ HTTPS
           ▼
┌──────────────────────┐
│   API Gateway (HTTP) │  ← Enruta peticiones de API y Swagger
└──────────┬───────────┘
           │ HTTP
           ▼
┌──────────────────────┐
│  ALB (Application    │  ← Balanceo de carga interno
│  Load Balancer)      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐     ┌───────────────────────┐
│   ECS Fargate        │────▶│  RDS MySQL (privada)   │
│  (franchise-api)     │     │  Secrets Manager       │
└──────────────────────┘     └───────────────────────┘
```

| Componente | Descripción |
|---|---|
| **CloudFront** | CDN global que expone el sistema por HTTPS. Cachea los recursos estáticos de Swagger UI y reenvía las llamadas a la API sin caché. |
| **API Gateway** | Enruta las peticiones HTTP hacia el ALB. Incluye rutas para la API de negocio y para Swagger (`/swagger-ui/*`, `/v3/api-docs*`, `/webjars/*`). |
| **ALB** | Distribuye el tráfico entre las tareas ECS Fargate dentro de la VPC privada. |
| **ECS Fargate** | Ejecuta el contenedor `franchise-api` (Spring WebFlux) sin necesidad de gestionar servidores. |
| **RDS MySQL** | Base de datos relacional en subred privada, sin acceso directo desde internet. |
| **Secrets Manager** | Almacena las credenciales de la base de datos de forma segura. |

---

## 🐳 1. Ejecutar la Imagen Docker Localmente

### Paso 1 – Construir la imagen

```bash
docker build -t franchise-api .
```

### Paso 2 – Ejecutar el contenedor

```bash
docker run -p 8090:8090 \
  -e URL=<URL de la base de datos> \
  -e PORT=<Puerto de la base de datos> \
  -e DB_NAME=<Nombre de la base de datos> \
  -e URL_VALUES="sslMode=DISABLED&tcpKeepAlive=true" \
  -e USER_DB_USERNAME=<Usuario de base de datos> \
  -e USER_DB_PASSWORD=<Contraseña de base de datos> \
  franchise-api
```

| Variable de entorno | Descripción |
|---|---|
| `URL` | Host o URL de conexión a la base de datos |
| `PORT` | Puerto de la base de datos (ej. `3306`) |
| `DB_NAME` | Nombre del esquema/base de datos |
| `URL_VALUES` | Parámetros adicionales de la cadena de conexión |
| `USER_DB_USERNAME` | Usuario de la base de datos |
| `USER_DB_PASSWORD` | Contraseña de la base de datos |

> 💡 Reemplaza cada `<valor>` con los datos reales de tu entorno.

---

## 🚀 2. Subir Imagen a Amazon ECR

> ⚠️ **Importante:** El nombre del contenedor debe ser exactamente `franchise-api` para que Terraform lo referencie correctamente.

### Paso 1 – Autenticarse en ECR

```bash
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin \
  047719641355.dkr.ecr.us-east-1.amazonaws.com
```

### Paso 2 – Etiquetar la imagen

```bash
docker tag franchise-api:latest \
  047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest
```

### Paso 3 – Subir la imagen

```bash
docker push 047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest
```

---

## 🌐 3. Desplegar en AWS con Terraform

### Paso 1 – Configurar las variables en `terraform.tfvars`

Edita el archivo `terraform/terraform.tfvars` con los valores de tu entorno:

```hcl
aws_region   = "<Región AWS deseada>"        # Ej: "us-east-1"
project_name = "<Nombre del proyecto>"        # Ej: "franchise-api"

vpc_cidr = "10.0.0.0/16"

public_subnets = [
  "10.0.1.0/24",
  "10.0.2.0/24"
]

private_subnets = [
  "10.0.3.0/24",
  "10.0.4.0/24"
]

rds_password = "<Contraseña de RDS>"
docker_image = "<URI de la imagen en ECR>"   # Ej: "047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest"
```

### Descripción de variables

| Variable | Descripción |
|---|---|
| `aws_region` | Región de AWS donde se desplegará la infraestructura |
| `project_name` | Prefijo usado para nombrar todos los recursos creados |
| `vpc_cidr` | Bloque CIDR de la VPC principal |
| `public_subnets` | Subredes públicas para el ALB y NAT Gateway |
| `private_subnets` | Subredes privadas para ECS y RDS |
| `rds_password` | Contraseña del usuario administrador de la base de datos RDS |
| `docker_image` | URI completa de la imagen Docker almacenada en ECR |

### Paso 2 – Inicializar y aplicar Terraform

```bash
# Moverse al directorio de Terraform
cd terraform

# Inicializar los providers y módulos
terraform init

# Previsualizar los cambios
terraform plan -var-file="terraform.tfvars"

# Aplicar la infraestructura
terraform apply -var-file="terraform.tfvars"
```

> 💡 Terraform pedirá confirmación antes de crear los recursos. Escribe `yes` para continuar.

---

## 📖 Swagger UI

La documentación interactiva de la API está disponible a través de **CloudFront** una vez desplegada la infraestructura.

### URL de acceso

Tras ejecutar `terraform apply`, el output `swagger_ui_url` mostrará la URL directa:

```bash
terraform output swagger_ui_url
# Ejemplo: https://d1abc123xyz.cloudfront.net/swagger-ui/index.html
```

### Rutas expuestas

| Ruta | Descripción |
|---|---|
| `/swagger-ui/index.html` | Interfaz gráfica de Swagger UI |
| `/v3/api-docs` | Especificación OpenAPI 3.0 en formato JSON |
| `/v3/api-docs/swagger-config` | Configuración de Swagger |
| `/webjars/**` | Recursos estáticos de Swagger UI |

### Comportamiento de caché en CloudFront

| Ruta | TTL | Motivo |
|---|---|---|
| `/swagger-ui/*` | 1 hora | Recursos semi-estáticos de la UI |
| `/v3/api-docs*` | 5 minutos | Puede cambiar en cada despliegue |
| `/webjars/*` | 24 horas – 1 año | Recursos completamente estáticos |
| `/*` (API) | Sin caché | Las respuestas de la API no deben cachearse |

> 💡 Si actualizas la aplicación y los docs no se refrescan, puedes invalidar la caché de CloudFront con:
> ```bash
> aws cloudfront create-invalidation \
>   --distribution-id $(terraform output -raw cloudfront_distribution_id) \
>   --paths "/v3/api-docs*" "/swagger-ui/*"
> ```

---

## 📝 Notas

- Verifica que tu perfil de AWS esté correctamente configurado en `~/.aws/credentials`.
- Terraform gestiona el estado de la infraestructura en el archivo `terraform.tfstate`. **No lo elimines ni lo versiones en Git.**
- Los logs del servicio ECS pueden consultarse en **AWS CloudWatch → Log Groups**.
- Para destruir todos los recursos creados por Terraform, ejecuta:

```bash
terraform destroy -var-file="terraform.tfvars"
```

---

## 🛟 Soporte

¿Tienes dudas o encontraste un problema?

- 📧 Contacta al equipo de **DevOps** o **Backend**.
- 🐛 Crea un **issue** en este repositorio describiendo el problema.
