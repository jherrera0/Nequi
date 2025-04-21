aws_region      = "us-east-1"
project_name    = "franchise-api-test-9"
vpc_cidr        = "10.0.0.0/16"

public_subnets  = [
  "10.0.1.0/24",
  "10.0.2.0/24"
]

private_subnets = [
  "10.0.3.0/24",
  "10.0.4.0/24"
]

rds_password    = "rootpassword"

docker_image    = "047719641355.dkr.ecr.us-east-1.amazonaws.com/franchise-api:latest"