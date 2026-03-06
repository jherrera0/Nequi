resource "aws_secretsmanager_secret" "this" {
  name_prefix             = "${var.name}-rds-credentials-"
  description             = "RDS credentials for ${var.name}"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "this" {
  secret_id     = aws_secretsmanager_secret.this.id
  secret_string = jsonencode({
    username = var.username
    password = var.password
  })
}
