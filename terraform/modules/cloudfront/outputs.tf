output "cloudfront_domain" {
  description = "Dominio público de la distribución CloudFront"
  value       = aws_cloudfront_distribution.this.domain_name
}

output "cloudfront_url" {
  description = "URL base de CloudFront (HTTPS)"
  value       = "https://${aws_cloudfront_distribution.this.domain_name}"
}

output "swagger_ui_url" {
  description = "URL pública de Swagger UI a través de CloudFront"
  value       = "https://${aws_cloudfront_distribution.this.domain_name}/swagger-ui/index.html"
}

output "cloudfront_distribution_id" {
  description = "ID de la distribución CloudFront"
  value       = aws_cloudfront_distribution.this.id
}

