locals {
  # Extraemos solo el host del endpoint de API Gateway (sin https://)
  api_gateway_domain = replace(replace(var.api_gateway_endpoint, "https://", ""), "/", "")
}

resource "aws_cloudfront_distribution" "this" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = "${var.name} – CloudFront distribution (API + Swagger UI)"
  price_class     = "PriceClass_100" # Solo edge locations en EE.UU., Canadá y Europa

  # ─────────────────────────────────────────────
  # Origen: API Gateway HTTP (todas las rutas API)
  # ─────────────────────────────────────────────
  origin {
    domain_name = local.api_gateway_domain
    origin_id   = "ApiGatewayOrigin"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  # ─────────────────────────────────────────────
  # Comportamiento por defecto → API Gateway
  # ─────────────────────────────────────────────
  default_cache_behavior {
    target_origin_id       = "ApiGatewayOrigin"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods         = ["GET", "HEAD"]
    compress               = true

    # Sin caché para las llamadas a la API
    forwarded_values {
      query_string = true
      headers      = ["Authorization", "Content-Type", "Accept", "Origin"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 0
    max_ttl     = 0
  }

  # ─────────────────────────────────────────────
  # Comportamiento: /swagger-ui/* → API Gateway
  # TTL elevado para recursos estáticos de la UI
  # ─────────────────────────────────────────────
  ordered_cache_behavior {
    path_pattern           = "/swagger-ui/*"
    target_origin_id       = "ApiGatewayOrigin"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    compress               = true

    forwarded_values {
      query_string = true
      headers      = ["Origin"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 3600
    max_ttl     = 86400
  }

  # ─────────────────────────────────────────────
  # Comportamiento: /v3/api-docs* → API Gateway
  # ─────────────────────────────────────────────
  ordered_cache_behavior {
    path_pattern           = "/v3/api-docs*"
    target_origin_id       = "ApiGatewayOrigin"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    compress               = true

    forwarded_values {
      query_string = true
      headers      = ["Origin"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 300
    max_ttl     = 3600
  }

  # ─────────────────────────────────────────────
  # Comportamiento: /webjars/* → API Gateway
  # (recursos estáticos de Swagger UI vía webjars)
  # ─────────────────────────────────────────────
  ordered_cache_behavior {
    path_pattern           = "/webjars/*"
    target_origin_id       = "ApiGatewayOrigin"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    compress               = true

    forwarded_values {
      query_string = true
      headers      = ["Origin"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 86400
    max_ttl     = 31536000
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name    = "${var.name}-cloudfront"
    Project = var.name
  }
}

