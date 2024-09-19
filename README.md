
# Shopme E-commerce Web Application



![App Screenshot](https://github.com/JuanmaFranco/Shopme-Ecommerce/raw/main/images/shopme%20logo/ShopmeAdminBig.png)

Welcome to my personal project – a fully-fledged e-commerce web application that harnesses cutting-edge technologies to deliver an exceptional online shopping experience. Built using Java, Spring Boot, Hibernate, Thymeleaf, Bootstrap, jQuery, HTML, and RESTful Web Services, this project showcases the power of modern web development tools.

# Project Overview
This project serves as a comprehensive e-commerce platform that comprises both an Admin application.The Admin application encompasses various modules including
Users, Categories, Brands, Products.

# Functional Requirements






**Key Actors**

- Backend
    - Admin
    - Editor
    - Salesperson
    - Shipper

**User Cases - Backend**
- Admin User
    - Manage Everything
        - Manage Users

- Editor Use Cases
    - Manage brands
    - Manage products
    - Manage categories

- Salesperson Use Cases
    - View products
    - Update product price

**Technical Requirements**
- Accesibility:
    - Apps can be accessible from any devices connected to the internet: PC, Laptop, Tablet, Smartphone.

- Availability:
    - Users can access apps anytime, 24/7.

- Security
    - Authentication is required for all users.
    - Authorization is required in the admin control panel (editor, salesperson, shipper, etc.).

- Performance
    - Fast response time.
    - No request takes longer than 4 seconds.

- Scalability
    - Apps can be scaled on demand and run well under loads.

# System Architecture
**Local Development**
![App Screenshot](https://github.com/JuanmaFranco/Shopme-Ecommerce/raw/main/images/system%20architecture/local_development.png)

On local development environment, I have everything running on a single computer: the Admin Application is running as a standalone JAR file. It is a Spring Boot application with embedded Tomcat server And it is connecting to a local instance of MySQL database server.

And for the static resources like the images of the products - are stored in the local file system.

![App Screenshot](https://github.com/JuanmaFranco/Shopme-Ecommerce/raw/main/images/application%20architecture/logical_layers.png)

Typically an application is structured into some logical layers.

On top is the view layer that renders the user interface to the client. In the view layer, we can use Thymeleaf mixed with HTML code to render the pages to the client. And the actions of the users in the view layer will invoke the controller layer.

In controller layer, we can use Spring MVC Controller, or REST controller or RESTful webservices.

And the controller layer depends on the service layer that contains business classes. And the service layer depends on the repository layer that contains entity classes and repository interfaces.

And below is the Spring Data JPA layer. Spring Data JPA will use Hibernate framework as the implementation of JPA. Hibernate will use JDBC driver to communicate with the underlying database.

The request from the client will come to the controller layer, the controller layer is responsible for handling the request from the clients.

It will call the service layer to perform the business logic of the application, and then it will render the view that is returned to the client.



