package madstodolist.controller;

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

    /**
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    } **/

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
}

