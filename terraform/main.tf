provider "aws" {
  region = var.aws_region
}

module "rds_secret" {
  source   = "./modules/rds_secret"
  name     = var.project_name
  username = "admin"
  password = var.rds_password
}

module "vpc" {
  source          = "./modules/network"
  name            = var.project_name
  cidr_block      = var.vpc_cidr
  public_subnets  = var.public_subnets
  private_subnets = var.private_subnets
  aws_region      = var.aws_region
}

module "security_groups" {
  source = "./modules/security_groups"
  name   = var.project_name
  vpc_id = module.vpc.vpc_id
}

module "rds_mysql" {
  source                  = "./modules/rds"
  name                    = var.project_name
  vpc_id                  = module.vpc.vpc_id
  subnet_ids              = module.vpc.private_subnet_ids
  vpc_security_group_ids  = [module.security_groups.rds_sg_id]
  db_name                 = "franchisedb"
  username                = "admin"
  password                = var.rds_password
}

module "alb" {
  source             = "./modules/alb"
  name               = var.project_name
  vpc_id             = module.vpc.vpc_id
  public_subnet_ids  = module.vpc.public_subnet_ids
  ecs_target_port    = 8090
  security_group_ids = [module.security_groups.alb_sg_id]
}

module "ecs_fargate" {
  source            = "./modules/ecs_service"
  name              = var.project_name
  container_name    = "franchise-api"
  container_image   = var.docker_image
  container_port    = 8090
  subnet_ids        = module.vpc.private_subnet_ids
  security_group_id = module.security_groups.ecs_sg_id

  alb_target_group_arn = module.alb.target_group_arn

  environment_variables = [
    { name = "URL",        value = module.rds_mysql.rds_address },
    { name = "PORT",       value = module.rds_mysql.rds_port },
    { name = "DB_NAME",    value = "franchisedb" },
    { name = "URL_VALUES", value = "sslMode=DISABLED&tcpKeepAlive=true" }
  ]

  secrets_variables = [
     { name = "USER_DB_USERNAME", valueFrom = "${module.rds_secret.secret_arn}:username::" },
     { name = "USER_DB_PASSWORD", valueFrom = "${module.rds_secret.secret_arn}:password::" }
  ]
}

module "api_gateway" {
  source        = "./modules/api_gateway"
  name          = var.project_name
  alb_dns_name  = module.alb.alb_dns_name
}
