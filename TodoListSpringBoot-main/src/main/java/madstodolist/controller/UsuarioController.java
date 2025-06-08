package madstodolist.controller;

import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
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

