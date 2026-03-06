locals {
  routes = {
    # ── API de negocio ───────────────────────────────────────────────────────
    "POST /franchise/create"     = "/franchise/create"
    "POST /franchise/updateName" = "/franchise/updateName"
    "POST /branch/addBranch"     = "/branch/addBranch"
    "POST /branch/updateName"    = "/branch/updateName"
    "POST /product/create"       = "/product/create"
    "POST /product/delete"       = "/product/delete"
    "POST /product/addProductStock" = "/product/addProductStock"
    "GET /product/getTopStockProductsByBranchAssociatedToFranchise/{franchiseId}" = "/product/getTopStockProductsByBranchAssociatedToFranchise/{franchiseId}"
    "POST /product/updateName"   = "/product/updateName"

    # ── Swagger UI ────────────────────────────────────────────────────────────
    "GET /swagger-ui/index.html"  = "/swagger-ui/index.html"
    "GET /swagger-ui/{proxy+}"    = "/swagger-ui/{proxy+}"
    "GET /v3/api-docs"            = "/v3/api-docs"
    "GET /v3/api-docs/{proxy+}"   = "/v3/api-docs/{proxy+}"
    "GET /webjars/{proxy+}"       = "/webjars/{proxy+}"
  }
}

resource "aws_apigatewayv2_api" "this" {
  name          = "${var.name}-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.this.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_apigatewayv2_integration" "ecs" {
  for_each = local.routes

  api_id                 = aws_apigatewayv2_api.this.id
  integration_type       = "HTTP_PROXY"
  integration_method     = split(" ", each.key)[0]
  integration_uri        = "http://${var.alb_dns_name}${replace(each.value, "{proxy+}", "{proxy}")}"
  payload_format_version = "1.0"
  connection_type        = "INTERNET"
}

resource "aws_apigatewayv2_route" "routes" {
  for_each = local.routes

  api_id    = aws_apigatewayv2_api.this.id
  route_key = each.key
  target    = "integrations/${aws_apigatewayv2_integration.ecs[each.key].id}"
}
