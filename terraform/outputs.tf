output "rds_endpoint" {
  value = module.rds_mysql.rds_address
}

output "ecs_cluster" {
  value = module.ecs_fargate.ecs_cluster_name
}

output "api_gateway_url" {
  value       = module.api_gateway.api_endpoint
  description = "URL base del API Gateway"
}

output "cloudfront_url" {
  value       = module.cloudfront.cloudfront_url
  description = "URL base de CloudFront (punto de entrada público)"
}

output "swagger_ui_url" {
  value       = module.cloudfront.swagger_ui_url
  description = "URL pública de Swagger UI"
}

output "cloudfront_distribution_id" {
  value       = module.cloudfront.cloudfront_distribution_id
  description = "ID de la distribución CloudFront (útil para invalidaciones de caché)"
}
