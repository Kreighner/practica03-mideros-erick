# Documentación Técnica de la Práctica 3

## Introducción

Esta documentación cubre la evolución de la aplicación de gestión de tareas, desde su implementación inicial hasta la última versión 1.1.0, que incluye nuevas funcionalidades como la página de "Acerca de", la implementación de la barra de navegación, la asignación de roles a los usuarios y la protección de ciertas vistas basadas en el rol del usuario. Todo el desarrollo se ha realizado siguiendo una metodología ágil, implementando nuevas funcionalidades paso a paso y garantizando una correcta integración con la base de datos y las vistas.

## Nuevas Clases y Métodos Implementados

### 1. `UsuarioController.java`
- **Método `listarUsuarios`**: Este método maneja la ruta `/usuarios` y asegura que solo los usuarios con rol `ADMIN` tengan acceso al listado de usuarios. Redirige a los usuarios sin privilegios a la página principal.

```java
@GetMapping("/usuarios")
public String listarUsuarios(Model model, Principal principal) {
    if (principal == null) {
        return "redirect:/login";  // Redirigir al login si no hay usuario autenticado
    }

    UsuarioData usuarioLogueado = usuarioService.findByEmail(principal.getName());
    if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
        return "redirect:/";  // Redirigir si no es admin
    }

    List<Usuario> usuarios = usuarioService.findAll();
    model.addAttribute("usuarios", usuarios);
    return "usuarios";
}
```

### 2. `AuthInterceptor.java`  
Interceptor de autenticación: Asegura que las rutas protegidas solo puedan ser accedidas por usuarios autenticados. Si el usuario no está autenticado, se le redirige al login.

```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String email = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;

        if (email == null || usuarioService.findByEmail(email) == null) {
            response.sendRedirect("/login");
            return false;  // Interrumpe la ejecución si no está autenticado
        }

        return true;  // Si está autenticado, permite el acceso
    }
}
```

## Nuevas Plantillas Thymeleaf Añadidas

### 1. `usuarios.html`  
Descripción: Muestra un listado de usuarios, permitiendo al administrador ver y gestionar a los usuarios. Contiene la tabla con los datos y un enlace a la descripción del usuario.

```html
<table class="table table-striped">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Correo</th>
            <th>Rol</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="usuario : ${usuarios}">
            <td th:text="${usuario.id}"></td>
            <td th:text="${usuario.nombre}"></td>
            <td th:text="${usuario.email}"></td>
            <td th:text="${usuario.rol}"></td>
            <td><a th:href="@{/usuarios/{id}(id=${usuario.id})}">Ver</a></td>
        </tr>
    </tbody>
</table>
```

### 2. `usuarioDescripcion.html`  
Descripción: Muestra los detalles de un usuario específico, como el ID, nombre, correo y rol.

```html
<ul class="list-group">
    <li class="list-group-item"><strong>ID:</strong> <span th:text="${usuario.id}"></span></li>
    <li class="list-group-item"><strong>Nombre:</strong> <span th:text="${usuario.nombre}"></span></li>
    <li class="list-group-item"><strong>Email:</strong> <span th:text="${usuario.email}"></span></li>
    <li class="list-group-item"><strong>Rol:</strong> <span th:text="${usuario.rol}"></span></li>
</ul>
```

## Explicación de los Tests Implementados

Durante el desarrollo de la aplicación, se implementaron diversos tests unitarios y tests de integración para asegurar el correcto funcionamiento de las funcionalidades implementadas.

- **Tests de UsuarioService**: Se crearon pruebas para verificar que los usuarios se registran correctamente, que el rol se asigna correctamente, y que los métodos de búsqueda (`findByEmail`, `findAll`) funcionan como se espera.

- **Tests de UsuarioController**: Se validaron las rutas protegidas y el comportamiento cuando el rol del usuario no es el adecuado para acceder a ciertas vistas. Los tests aseguran que solo los administradores puedan acceder al listado de usuarios.

## Explicación de Código Fuente Relevante

Un ejemplo importante del código implementado es el método que restringe el acceso al listado de usuarios solo para administradores:

```java
@GetMapping("/usuarios")
public String listarUsuarios(Model model, Principal principal) {
    if (principal == null) {
        return "redirect:/login";  // Redirigir al login si no hay usuario autenticado
    }

    UsuarioData usuarioLogueado = usuarioService.findByEmail(principal.getName());
    if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
        return "redirect:/";  // Redirigir si no es admin
    }

    List<Usuario> usuarios = usuarioService.findAll();
    model.addAttribute("usuarios", usuarios);
    return "usuarios";
}
```

Este código asegura que solo los administradores puedan acceder a la página `/usuarios`, implementando una verificación de roles dentro del controlador.

## Conclusión

Este documento cubre las funcionalidades implementadas en la versión 1.1.0 de la aplicación, incluyendo la creación de nuevas vistas Thymeleaf, la implementación de roles y restricciones de acceso, así como los tests realizados para asegurar el correcto funcionamiento de las características nuevas.

### Recomendación para los compañeros desarrolladores:

* Asegúrese de que la validación de roles se realice no solo en los controladores, sino también utilizando interceptores para asegurar la consistencia en todas las rutas protegidas.

* Mantener pruebas unitarias e integración para asegurar que las funcionalidades no se rompan con futuras modificaciones.
