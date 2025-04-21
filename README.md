# 📦 Franchise API – Docker + Terraform Deployment

Este proyecto contiene una API que puede ejecutarse localmente usando Docker y desplegarse en AWS mediante Terraform y ECS Fargate.

---

## 🔧 Requisitos Previos

Antes de comenzar, asegúrate de tener instalados y configurados los siguientes elementos:

- Docker: https://docs.docker.com/get-docker/
- Terraform: https://developer.hashicorp.com/terraform/downloads
- AWS CLI (Ejecuta `aws configure`)
- Permisos necesarios en AWS para trabajar con: Amazon ECR, ECS, RDS, VPC, Subnets, etc.

---

## 🐳 1. Ejecutar la Imagen Docker Localmente

### Paso 1: Construcción de la imagen

docker build -t franchise-api .

### Paso 2: Ejecutar la imagen

docker run -p 8090:8090 \
  -e URL=<URL de la base de datos> \
  -e PORT=<Puerto de la base de datos> \
  -e DB_NAME=<Nombre de la base de datos> \
  -e URL_VALUES="sslMode=DISABLED&tcpKeepAlive=true" \
  -e USER_DB_USERNAME=<Usuario de base de datos> \
  -e USER_DB_PASSWORD=<Contraseña de base de datos> \
  franchise-api

> Nota: Asegúrate de reemplazar los valores de entorno por los datos reales de tu base de datos.

---

## 🚀 2. Subir Imagen a Amazon ECR

IMPORTANTE: Es necesario que el contenedor se llame "franchise-api"

### Paso 1: Autenticarse en ECR

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 047719641355.dkr.ecr.us-east-1.amazonaws.com

### Paso 2: Etiquetar la imagen

docker tag franchise-api:latest 047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest

### Paso 3: Subir la imagen

docker push 047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest

---

## 🌐 3. Desplegar en AWS con Terraform

### Paso 1: Modificar el archivo terraform.tfvars

aws_region      = "<Región AWS deseada>"
project_name    = "<Nombre del proyecto>"

vpc_cidr        = "10.0.0.0/16"

public_subnets  = [
  "10.0.1.0/24",
  "10.0.2.0/24"
]

private_subnets = [
  "10.0.3.0/24",
  "10.0.4.0/24"
]

rds_password    = "<Contraseña de RDS>"

docker_image    = "<URI de la imagen Docker en ECR>"

### Descripción de variables

Variable         | Descripción
------------------|--------------------------------------------------------------
aws_region        | Región donde se realizará el despliegue en AWS
project_name      | Nombre del proyecto (usado como prefijo en recursos)
vpc_cidr          | Rango CIDR de la VPC
public_subnets    | Subredes públicas para ALB y NAT Gateway
private_subnets   | Subredes privadas para ECS y RDS
rds_password      | Contraseña del usuario principal de la base de datos RDS
docker_image      | URL de la imagen Docker en el repositorio de Amazon ECR

---

### Paso 2: Inicializar y aplicar Terraform

terraform init
terraform plan -var-file="terraform.tfvars"
terraform apply -var-file="terraform.tfvars"

---

## 📝 Notas

- Verifica que tu perfil de AWS esté correctamente configurado (~/.aws/credentials).
- Terraform creará los recursos con base en los valores definidos en el archivo .tfvars.
- Puedes monitorear los logs del servicio ECS desde la consola de AWS o a través de CloudWatch.

---

## 🛟 Soporte

¿Tienes dudas? Puedes:

- Contactar al equipo DevOps o Backend.
- Crear un issue en este repositorio para obtener ayuda.
