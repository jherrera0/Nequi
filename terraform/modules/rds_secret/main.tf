resource "aws_secretsmanager_secret" "this" {
  name        = "${var.name}-rds-credentials"
  description = "RDS credentials for ${var.name}"
}

resource "aws_secretsmanager_secret_version" "this" {
  secret_id     = aws_secretsmanager_secret.this.id
  secret_string = jsonencode({
    username = var.username
    password = var.password
  })
}
