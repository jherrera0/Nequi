variable "name" {
  type        = string
  description = "Nombre base para el secreto de RDS"
}

variable "username" {
  type        = string
  description = "Usuario de la base de datos"
}

variable "password" {
  type        = string
  sensitive   = true
  description = "Contraseña de la base de datos"
}
