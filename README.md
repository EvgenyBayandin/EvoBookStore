## Приложение для управления заказами в книжном магазине с использованием микросервисной архитектуры

### Начало работы
Чтобы начать работу с проектом, выполните следующие шаги:
Клонируйте репозиторий: 
### `git clone`
Перейдите в директорию проекта: 
### `cd EvoBookStore`

Для работы приложения необходим "Docker Desktop", при необходимости скачайте, установите и запустите.

Запустите сервисы Postgres и Pgadmin командой: 
### `docker-compose up`

Запустите сервисы в следующей последовательности:
### `EurekaApplication`
### `GatewayApplication`
### `PersonApplication`
### `ProductApplication`

Для работы с сервисами Person и Product доступен Swagger UI
###  http://localhost:8081/swagger-ui/index.html#
###  http://localhost:8082/swagger-ui/index.html#

Создайте пользователя и товары, затем можно создавать заказы, вносить изменения в товары, пользователя, заказы, удалять пользователй, товары, заказы и просматривать заказы и заказы конкретного пользователя. Ддя удобства воспользуйтесь Swagger UI сервисов

## Использованные технологии

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Web
- Spring Cloud
- Springdoc openApi
- PostgreSQL
- Docker
- Docker Compose
- Maven
- Git

## Дополнительная информация
