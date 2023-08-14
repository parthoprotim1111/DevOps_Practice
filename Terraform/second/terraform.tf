provider "aws" {
	region = "ap-south-1" 
	profile = "terraform-user"
}
/*
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_security_group" "tf-sg" {
  name        = "tf-sg"
  description = "Allow TLS inbound traffic"
  vpc_id      = aws_vpc.default.id

  ingress {
    description      = "HTTPS"
    from_port        = 443
    to_port          = 443
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  ingress {
    description      = "HTTP"
    from_port        = 80
    to_port          = 80
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  ingress {
    description      = "SSH"
    from_port        = 22
    to_port          = 22
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name = "terraform-sg"
  }
}
*/


resource "aws_instance" "linux1"{
	ami = "ami-021f7978361c18b01"
	instance_type = "t2.micro"
	security_groups = [ "tf-sg" ]
	key_name = "K8S"
	tags = {
	Name = "web server"
	}
}

/*
resource "aws_network_interface_sg_attachment" "sg_attachment"{
  security_group_ids   = [aws_security_group.tf-sg.id]
  network_interface_id = aws_instance.linux1.network_interface_id
}

*/

resource "null_resource" "task1" {
  # ...

  # Establishes connection to be used by all
  # generic remote provisioners (i.e. file/remote-exec)
  connection {
    type     = "ssh"
    user     = "ec2-user"
    private_key = file("D:/Courses/Ashok IT DevOps/K8S.pem")
    host     = aws_instance.linux1.public_ip
  }

  provisioner "remote-exec" {
    inline = [
      "sudo yum update -y",
      "sudo yum install git -y",
      "sudo yum install httpd -y",
      "sudo yum install php -y",
      "sudo systemctl start httpd",
      "sudo systemctl enable httpd",
      "sudo yum upgrade -y"

    ]
  }
}
