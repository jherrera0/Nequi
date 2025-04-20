variable "name" {
  type = string
}

variable "cidr_block" {
  type = string
}

variable "public_subnets" {
  type = list(string)
}

variable "private_subnets" {
  type = list(string)
}

variable "aws_region" {
  type = string
}

variable "az_suffixes" {
  type = map(string)
  default = {
    0 = "a"
    1 = "b"
    2 = "c"
  }
}
