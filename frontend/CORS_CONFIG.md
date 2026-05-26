# Configuration CORS - Backend

## Problème CORS
L'accès au backend depuis `http://localhost:4201` est bloqué par la politique CORS.

## Solution - Ajouter Configuration CORS dans le Backend

### Option 1: Spring Boot Configuration Class (Recommandé)

Créer un fichier `src/main/java/com/yourpackage/config/CorsConfig.java`:

```java
package com.okane.transfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4201", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### Option 2: Annotation @CrossOrigin (Alternative)

Sur chaque contrôleur ou méthode:

```java
@CrossOrigin(origins = "http://localhost:4201")
@RestController
@RequestMapping("/api/transferts")
public class TransfertController {
    // ...
}
```

### Option 3: Propriétés application.properties

```properties
# CORS Configuration
server.servlet.cors.allowed-origins=http://localhost:4201,http://localhost:3000
server.servlet.cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
server.servlet.cors.allowed-headers=*
server.servlet.cors.allow-credentials=true
server.servlet.cors.max-age=3600
```

## Verification

Après configuration, vérifier les headers CORS dans DevTools:
```
Access-Control-Allow-Origin: http://localhost:4201
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Credentials: true
```

## Production

Pour la production, remplacer `localhost:4201` par le domaine réel:
```java
.allowedOrigins("https://monsite.com", "https://www.monsite.com")
```
