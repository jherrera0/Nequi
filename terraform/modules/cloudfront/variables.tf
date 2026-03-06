variable "name" {
  type        = string
  description = "Nombre del proyecto, usado como prefijo en los recursos"
}

variable "api_gateway_endpoint" {
  type        = string
  description = "Endpoint HTTPS del API Gateway (ej. https://abc123.execute-api.us-east-1.amazonaws.com)"
}

