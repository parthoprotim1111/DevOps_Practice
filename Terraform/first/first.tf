provider "aws" {
region = "ap-south-1"
profile = "terraform-user"
}

resource "aws_instance" "ubuntu-1" {
	ami = "ami-0f5ee92e2d63afc18"
	instance_type = "t2.micro"
	tags ={
	Name = "first-ubuntu"
	} 
}
/*
resource "aws_instance" "ubuntu-2" {
	ami = "ami-0f5ee92e2d63afc18"
	instance_type = "t2.micro"
	tags ={
	Name = "second ubuntu"
	} 
}
*/
output "var1" {
	value=aws_instance.ubuntu-1  
}
