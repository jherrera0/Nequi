resource "aws_lb" "this" {
  name               = "${var.name}-alb"
  load_balancer_type = "application"
  subnets            = var.public_subnet_ids
  security_groups    = var.security_group_ids
  internal           = false

  enable_deletion_protection = false
}

resource "aws_lb_target_group" "ecs" {
  name     = "${var.name}-tg"
  port     = var.ecs_target_port
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  target_type = "ip"
  health_check {
    path                = "/health"
    protocol            = "HTTP"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200"
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs.arn
  }
}
