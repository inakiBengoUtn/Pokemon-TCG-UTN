## Iniciar servicios
**Backend**

Se debe tener instalado por lo menos JDK17 o superior y bien configurado el IDE(si usan intellij IDEA) para esa version.

Si no tienes instalado MAVEN ejecutar este comando `./mvnw clean install`. (esto descarga los paquetes necesarios para spring)

Se debe tener docker inicializado y lanzar el siguiente comando desde la carpeta raiz "back", `docker compose up -d`.

Y ya deberias poder ejecutar el backend.

**Frontend** 

Ejecutar `npm i` y para arrancar el servidor local `npm run start`.
El servidor local deberia estar corriendo en el puerto 4200. 

## Notas para el equipo

No usen SockJs, es solo para compatibilidad con navegadores viejos y complica bastante el sistema.

Los enpoint publicos a websocket se hacen en "/topic/..." y los privados a "user/queue/..."

## Backend

Spring Boot 🌱

-----
Dependencias

- web
- security
- websockets
- JPA
- PostgreDriver
- Lombok
- JWT
- REDIS

-----
Docker 🐳

|Image      | Alias       | Port 
|-----------|-------------|-------
|PostgreSql | postgres_db | 5433
|Redis      | redis_cache | 6379

Glosario de custom errors

- USER_TAKEN: nombre de usuario no disponible.
- BAD_CREDENTIALS: Las credenciales del usuarios no son validas.
- NOT_AUTHENTICATED: El usuario no esta autneticado

## Frontend

Angular 21 📐

---
Rutas

- '': lobby.
- '/auth': login.
- '/auth/register': registro de usuario.
- '/match': pagina para matchear usuarios.
- '/game': pagina de la partida.

----
Dependencias

- @angular/material
- @angular/material:theme-color
- @stomp/stompjs -> El back usa STOMP como standard para WebScokets.

## Caracteristicas

**Seguridad**
Cookies y JWT. Ademas tiene un sistema de refresh token para mejorar la experiencia del usuario.
WebSockets y STOMP