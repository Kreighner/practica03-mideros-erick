package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Rol;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    /**
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    } **/

    /**
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // Redirigir al login si no hay usuario autenticado
        }

        // Obtener el usuario logueado
        UsuarioData usuarioLogueado = usuarioService.findByEmail(principal.getName());

        // Agregar log para verificar el rol del usuario
        System.out.println("Usuario logueado: " + usuarioLogueado.getEmail() + " con rol: " + usuarioLogueado.getRol());

        // Verificar si el usuario tiene rol ADMIN
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/";  // Redirigir si no es admin
        }

        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    } **/

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        // Obtener el rol del usuario logueado desde la sesión
        Rol rolUsuarioLogeado = managerUserSession.obtenerRolUsuarioLogeado();

        // Verificar si el rol del usuario logueado es ADMIN
        if (rolUsuarioLogeado != Rol.ADMIN) {
            return "redirect:/";  // Redirigir si no es admin
        }

        // Obtener la lista de usuarios solo si el rol es ADMIN
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    }

    @GetMapping("/usuarios/{id}")
    public String detalleUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return "redirect:/usuarios"; // Redirige si el usuario no existe
        }
        model.addAttribute("usuario", usuario);
        return "usuarioDescripcion";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        // Verificar si el usuario tiene el rol ADMIN
        Rol rolUsuarioLogeado = managerUserSession.obtenerRolUsuarioLogeado();
        if (rolUsuarioLogeado != Rol.ADMIN) {
            return "redirect:/";  // Redirigir si no es admin
        }

        // Obtener el usuario con el id proporcionado
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return "redirect:/usuarios";  // Redirigir si el usuario no existe
        }

        model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
        return "usuarioEditar";  // Vista para editar el usuario
    }

    @PostMapping("/usuarios/{id}/editar")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, Model model) {
        // Verificar si el usuario tiene el rol ADMIN
        Rol rolUsuarioLogeado = managerUserSession.obtenerRolUsuarioLogeado();
        if (rolUsuarioLogeado != Rol.ADMIN) {
            return "redirect:/";  // Redirigir si no es admin
        }

        // Asegurarse de que el ID del usuario no se pierda
        usuario.setId(id);

        // Actualizar el usuario con los datos recibidos
        usuarioService.actualizarUsuario(usuario);

        return "redirect:/usuarios";  // Redirigir a la lista de usuarios después de guardar los cambios
    }

    @GetMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {
        // Verificar si el usuario tiene el rol ADMIN
        Rol rolUsuarioLogeado = managerUserSession.obtenerRolUsuarioLogeado();
        if (rolUsuarioLogeado != Rol.ADMIN) {
            return "redirect:/";  // Redirigir si no es admin
        }

        // Eliminar el usuario con el id proporcionado
        usuarioService.eliminarUsuario(id);

        return "redirect:/usuarios";  // Redirigir a la lista de usuarios después de eliminar
    }
}

