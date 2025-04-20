variable "alb_target_group_arn" {
  description = "ARN del target group del ALB"
  type        = string
}

variable "environment_variables" {
  type        = list(object({ name = string, value = string }))
  description = "List of non-sensitive environment variables"
  default     = []
}

variable "secrets_variables" {
  type = list(object({
    name       = string
    valueFrom  = string
  }))
  description = "List of sensitive environment variables from Secrets Manager"
  default     = []
}


variable "name" {
  type = string
}

variable "container_name" {
  type = string
}

variable "container_image" {
  type = string
}

variable "container_port" {
  type    = number
  default = 8080
}

variable "cpu" {
  type    = string
  default = "256"
}

variable "memory" {
  type    = string
  default = "512"
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "subnet_ids" {
  type = list(string)
}

variable "security_group_id" {
  type = string
}
