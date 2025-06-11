package madstodolist.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import madstodolist.model.Rol;

import javax.servlet.http.HttpSession;

@Component
public class ManagerUserSession {

    @Autowired
    HttpSession session;

    // Añadimos el id de usuario en la sesión HTTP para hacer
    // una autorización sencilla. En los métodos de controllers
    // comprobamos si el id del usuario logeado coincide con el obtenido
    // desde la URL
    public void logearUsuario(Long idUsuario, Rol rol) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
        session.setAttribute("rolUsuarioLogeado", rol); // Guardamos el rol
    }

    public Long usuarioLogeado() {
        return (Long) session.getAttribute("idUsuarioLogeado");
    }

    public Rol obtenerRolUsuarioLogeado() {
        return (Rol) session.getAttribute("rolUsuarioLogeado"); // Método para obtener el rol
    }

    public void logout() {
        session.setAttribute("idUsuarioLogeado", null);
        session.setAttribute("rolUsuarioLogeado", null); // Limpiamos también el rol
    }
}
