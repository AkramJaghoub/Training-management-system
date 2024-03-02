# Tero - Training Management System

## Introduction

Welcome to Tero, a Training Management System designed to bridge the gap between students seeking real-world training opportunities and companies eager to discover fresh talent. Tero is more than just a platform; it's a journey led by our mascot, Tero the bird, guiding users through the vast landscape of digital education towards meaningful connections and opportunities.

## Software Requirements

### Web Development Frameworks

- **Backend Development**: Java Spring
- **Frontend Development**: HTML, CSS, Bootstrap, JavaScript
- **Template Engine**: Thymeleaf for server-client communication

### Database Management System

- **DBMS**: MySQL

### Version Control System

- **VCS**: Git and GitHub for source code management and tracking changes

### Containerization and Deployment

- **Tools**: Docker for containerizing the application, ensuring consistent behavior across different environments

### Image Storage

- **Cloud Platform**: Google Cloud Platform (GCP) is used for uploading and storing images securely and efficiently.

## Getting Started

To get started with Tero, please follow the instructions below:

1. **Clone the Repository**

```
git clone https://github.com/AkramJaghoub/Training-management-system
```

2. **Use the below docker commands in order to access the database locally**

- **get mysql image**
```
docker pull mysql
```

- **run the container**
```
docker run --name container_name -e MYSQL_ROOT_PASSWORD=db_password -p 3306:3306 -d mysql
```

- access mysql bash
```
docker exec -it container_name mysql -u root -p
```

- create a database
```
create database database_name;
```

3. **update the application.proprties file**

```
spring.datasource.url=jdbc:mysql://localhost:3306/database_name
spring.datasource.username=root
spring.datasource.password=db_password from docker run
```

4. **run the program**

5. **Access the website**

```
localhost:8080/
```
