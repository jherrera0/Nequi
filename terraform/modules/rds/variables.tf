variable "name" {
  type        = string
  description = "Nombre base del RDS"
}

variable "engine" {
  type        = string
  default     = "mysql"
  description = "Tipo de motor de base de datos"
}

variable "engine_version" {
  type        = string
  default     = "8.0.35"
}

variable "instance_class" {
  type        = string
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  type        = number
  default     = 20
}

variable "max_allocated_storage" {
  type        = number
  default     = 100
}

variable "username" {
  type        = string
}

variable "password" {
  type        = string
  sensitive   = true
}

variable "db_name" {
  type        = string
}

variable "port" {
  type        = number
  default     = 3306
}

variable "vpc_id" {
  type        = string
}

variable "subnet_ids" {
  type        = list(string)
}

variable "allowed_cidrs" {
  type        = list(string)
  default     = ["10.0.0.0/16"]
}

variable "multi_az" {
  type        = bool
  default     = false
}

variable "backup_retention_period" {
  type        = number
  default     = 7
}

variable "vpc_security_group_ids" {
  type        = list(string)
  description = "Lista de IDs de grupos de seguridad asociados al RDS"
}
