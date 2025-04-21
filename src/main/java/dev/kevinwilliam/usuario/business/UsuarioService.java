package dev.kevinwilliam.usuario.business;

import dev.kevinwilliam.usuario.business.converter.UsuarioConverter;
import dev.kevinwilliam.usuario.business.dto.UsuarioDTO;
import dev.kevinwilliam.usuario.infrastructure.entity.Usuario;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ConflictExpception;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ResourceNotFounException;
import dev.kevinwilliam.usuario.infrastructure.repository.UsuarioRepository;
import dev.kevinwilliam.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
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
    // Método para verificar se o email já existe
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

    // Método para buscar um usuário por email
    public Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado "+email));
    }

    // Método para deletar um usuário por email
    public void deletaUsuarioPorEmail(String email) {

        usuarioRepository.deleteByEmail(email);
    }

    // Método para atualizar dados de um usuário
    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO usuarioDTO) {

        // Extrai o email do token JWT
        String email = jwtUtil.extractEmail(token.substring(7));

        //Busca o usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado "+email));
        //Caso a senha não seja nula, criptografa a senha
        usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null);


        //Mescla os dados do Banco de dados com os do DTO
        Usuario usuarioEntity = usuarioConverter.atualizaUsuario(usuario, usuarioDTO);



        //Salva dados convertidos
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuarioEntity));



    }



}
