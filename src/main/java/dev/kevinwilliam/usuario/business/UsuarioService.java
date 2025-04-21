package dev.kevinwilliam.usuario.business;

import dev.kevinwilliam.usuario.business.converter.UsuarioConverter;
import dev.kevinwilliam.usuario.business.dto.UsuarioDTO;
import dev.kevinwilliam.usuario.infrastructure.entity.Usuario;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ConflictExpception;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ResourceNotFounException;
import dev.kevinwilliam.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    // Método para salvar um usuário

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        // Verifica se o email já existe
        emailExiste(usuarioDTO.getEmail());
        // Criptografa a senha
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        // Converte o DTO para a entidade
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        // Salva o usuário no banco de dados e retorna o DTO
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario));
    }
    public void emailExiste(String email) {
        try{
            boolean existe = verificaEmail(email);
            if(existe){
                throw new ConflictExpception("Usuário já cadastrado com esse email");
            }
        } catch (ConflictExpception e) {
            throw new ConflictExpception("Usuário já cadastrado com esse email", e.getCause());
        }
    }
    public boolean verificaEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    public Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado "+email));
    }

    public void deletaUsuarioPorEmail(String email) {

        usuarioRepository.deleteByEmail(email);
    }

}
