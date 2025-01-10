# 🚀 Microservices Architecture with Spring WebFlux

Este repositorio contiene una arquitectura basada en **microservicios** desarrollada con **Java 21**, sin ninguna vulnerabilidad media de dependencia a la fecha 📅 **10/01/2025**. 

## 🧩 Microservicios Incluidos

1. **product-service-microservice**
2. **api-producer-service**
3. **order-consumer-microservice**
4. **client-service-microservice**

Cada microservicio cuenta con su respectivo **Swagger** para facilitar la consulta de la documentación de sus endpoints. Puedes acceder a la interfaz de Swagger en: http://localhost:8082/api/swagger-ui/webjars/swagger-ui

⚠️ **Nota:** Cambia el **host** y el **puerto** según el microservicio que deseas consultar.

---

## 🛠️ Tecnologías Clave

El proyecto está desarrollado usando **Spring WebFlux**, lo que permite manejar procesos **no bloqueantes** para una mayor escalabilidad y rendimiento. 

Además, se lleva un registro detallado de métricas en algunos microservicios, como el **Consumer** y **Client**, accesibles en: http://localhost:8081/api/actuator/metrics


⚠️ **Nota:** Cambia el **host** y el **puerto** según el microservicio correspondiente.

---

## 📦 Dependencias Relevantes

Las principales dependencias utilizadas en el proyecto son:

- **Spring Boot Starter Actuator**
- **Spring Boot Starter WebFlux**
- **Spring Kafka**
- **Micrometer Registry Prometheus**
- **Lombok**
- **MapStruct**
- **SpringDoc OpenAPI Starter WebFlux UI**
- **Spring Boot Starter Data MongoDB Reactive**
- **Swagger Annotations**
- **Redisson**
- **Spring Boot Starter Test** (Testing)
- **Reactor Test** (Testing)

---

## 🌟 Características Principales

- **Documentación API**: Todos los microservicios tienen Swagger integrado.
- **Procesos Reactivos**: Uso de Spring WebFlux para manejar procesos no bloqueantes.
- **Registro de Métricas**: Métricas disponibles mediante Actuator en algunos microservicios.
- **Dependencias Seguras**: Sin vulnerabilidades de dependencia nivel medio al 10/01/2025.

---




