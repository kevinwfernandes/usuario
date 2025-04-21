package dev.kevinwilliam.usuario.business;

import dev.kevinwilliam.usuario.business.converter.UsuarioConverter;
import dev.kevinwilliam.usuario.business.dto.UsuarioDTO;
import dev.kevinwilliam.usuario.infrastructure.entity.Usuario;
import dev.kevinwilliam.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    // Método para salvar um usuário

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        // Converte o DTO para a entidade
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        // Salva o usuário no banco de dados e retorna o DTO
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario));
    }

}
