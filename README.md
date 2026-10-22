# Utilización de Interceptores en SpringBoot

### En el siguiente código busca implementar y ayudar en los conceptos de Interceptores dentro de SpringBoot.

#### Dentro de la carpeta de interceptos contaremos con el código para crear y utilizar interceptores para nuestros controladores, aplicando en un caso práctico de tiempos de ejecución.

##### Encontrandose los conceptos de preHandle y postHandle.



<p align="centerr">
<img src="https://i.ytimg.com/vi/ZxadlU1F_DY/maxresdefault.jpg" alt="Programación orientada a aspectos">
</p>

---

## Objetivo

Proyecto de práctica del curso de Spring Boot 3 para aprender a **crear, registrar y aplicar interceptores HTTP** (`HandlerInterceptor`) sobre los controladores. El caso práctico mide el tiempo de ejecución de una petición (entre `preHandle` y `postHandle`) y además muestra cómo un interceptor puede **bloquear el acceso** a un handler devolviendo una respuesta JSON de error.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.2.1 |
| Spring Web (`spring-boot-starter-web`) | gestionada por Spring Boot |
| Spring Boot Actuator | gestionada por Spring Boot |
| Spring Boot DevTools | gestionada por Spring Boot |
| Jackson (`ObjectMapper`) | incluido en starter-web |
| Maven (wrapper `mvnw`) | - |

No utiliza base de datos.

## Estructura del proyecto

```
src/main/java/com/joaquin/curso/springboot/app/interceptor/springbootinterceptor
├── SpringbootInterceptorApplication.java   # Clase principal
├── MvcConfig.java                          # WebMvcConfigurer: registra el interceptor y sus rutas
├── controllers
│   └── FooController.java                  # Endpoints /app/foo, /app/bar y /app/baz
└── interceptors
    └── LoadingTimeInterceptor.java         # HandlerInterceptor con preHandle y postHandle
```

## Conceptos aplicados

- **`HandlerInterceptor`**: la clase `LoadingTimeInterceptor` lo implementa y se registra como bean con `@Component("loadingTimeInterceptor")`.
- **`preHandle()`**: se ejecuta *antes* del método del controlador.
  - Obtiene el método invocado mediante `HandlerMethod`.
  - Guarda el instante de inicio en el `request` (`request.setAttribute("start", ...)`), ya que el request es lo que se comparte entre `preHandle` y `postHandle`.
  - Simula una demora aleatoria (0 a 499 ms) con `Thread.sleep`.
  - Si retorna `false` **no se ejecuta el controlador**: en el código actual construye un JSON con `ObjectMapper` (`error`, `date`), fija `Content-Type: application/json` y status `401`, y lo escribe en el `response`. Hay comentada una alternativa con `response.sendRedirect(...)` pensada para Thymeleaf.
  - Las peticiones `POST` se dejan pasar directamente (`return true`).
- **`postHandle()`**: se ejecuta *después* del controlador; en peticiones `GET` calcula y registra en el log el tiempo transcurrido.
- **Registro del interceptor**: `MvcConfig` implementa `WebMvcConfigurer` y en `addInterceptors()` lo aplica solo a las rutas indicadas con `addPathPatterns("/app/foo", "/app/bar")` (sin `addPathPatterns` se aplicaría a todas).
- **Inyección con `@Autowired` + `@Qualifier`** para seleccionar el interceptor por nombre de bean.
- **Logging** con SLF4J (`LoggerFactory`).

## Endpoints

| Método | Ruta | Interceptada | Descripción |
|---|---|---|---|
| GET | `/app/foo` | Sí | Devuelve `{"message": "handler foo del controlador"}`. Con el código actual el interceptor devuelve `false`, por lo que responde **401** con el JSON de error. |
| GET | `/app/bar` | Sí | Devuelve `{"message": "handler bar del controlador"}`. Mismo comportamiento que `/app/foo` respecto del interceptor. |
| GET | `/app/baz` | No | Devuelve `{"message": "handler baz del controlador"}` sin pasar por el interceptor. |

> Para ver el flujo completo `preHandle` -> controlador -> `postHandle` con el tiempo transcurrido en consola, cambia el `return false;` de `preHandle()` por `return true;`.

## Configuración

`application.properties` no requiere ninguna propiedad adicional. No hay base de datos.

## Cómo ejecutarlo

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`. Ejemplos:

```bash
curl -i http://localhost:8080/app/foo
curl -i http://localhost:8080/app/baz
```
