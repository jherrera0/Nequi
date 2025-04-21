output "rds_endpoint" {
  value = module.rds_mysql.rds_address
}

output "ecs_cluster" {
  value = module.ecs_fargate.ecs_cluster_name
}

output "api_gateway_url" {
  value = module.api_gateway.api_endpoint
}
