package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Rol;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        Iterable<Usuario> iterable = usuarioRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {
        // Validar que el correo no esté registrado
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser.isPresent()) {
            throw new UsuarioServiceException("El usuario con el correo " + usuario.getEmail() + " ya está registrado.");
        }

        // Si no se proporciona rol, asignar el rol por defecto
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USER);  // Valor por defecto si no se selecciona rol
        }

        // Guardar el usuario en la base de datos
        return usuarioRepository.save(usuario);
    }
}
