package dev.kevinwilliam.usuario.business;

import dev.kevinwilliam.usuario.business.converter.UsuarioConverter;
import dev.kevinwilliam.usuario.business.dto.EnderecoDTO;
import dev.kevinwilliam.usuario.business.dto.TelefoneDTO;
import dev.kevinwilliam.usuario.business.dto.UsuarioDTO;
import dev.kevinwilliam.usuario.infrastructure.entity.Endereco;
import dev.kevinwilliam.usuario.infrastructure.entity.Telefone;
import dev.kevinwilliam.usuario.infrastructure.entity.Usuario;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ConflictExpception;
import dev.kevinwilliam.usuario.infrastructure.exceptions.ResourceNotFounException;
import dev.kevinwilliam.usuario.infrastructure.repository.EnderecoRepository;
import dev.kevinwilliam.usuario.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;
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
    public UsuarioDTO buscaUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado " + email)));
        } catch (ResourceNotFounException e) {
            throw new ResourceNotFounException("Usuário não encontrado " + email, e.getCause());
        }
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

    // Método para atualizar endereços de um usuário
    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        // Busca endereço pelo id
        Endereco entity = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new ResourceNotFounException("Id do endereço não encontrado " + idEndereco));
        Endereco endereco = usuarioConverter.atualizaEndereco(entity, enderecoDTO);

        // Salva o endereço atualizado
        return usuarioConverter.paraEnderecoDTO( enderecoRepository.save(endereco));


    }

    // Método para atualizar telefones de um usuário
    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO){
        // Busca telefone pelo id
        Telefone entity = telefoneRepository.findById(idTelefone)
                .orElseThrow(() -> new ResourceNotFounException("Id do telefone não encontrado " + idTelefone));
        Telefone telefone = usuarioConverter.atualizaTelefone(entity, telefoneDTO);

        // Salva o telefone atualizado
        return usuarioConverter.paraTelefoneDTO( telefoneRepository.save(telefone));


    }

    //Método para cadastrar um telefone
    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO telefoneDTO){
        // Extrai o email do token JWT
        String email = jwtUtil.extractEmail(token.substring(7));
        // Busca o usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado "+email));

        Telefone telefoneEntity = usuarioConverter.cadastraTelefone(telefoneDTO, usuario.getId());
        // Salva o telefone
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefoneEntity));
    }

    //Método para cadastrar um endereço
    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO enderecoDTO){
        // Extrai o email do token JWT
        String email = jwtUtil.extractEmail(token.substring(7));
        // Busca o usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFounException("Usuário não encontrado "+email));

        Endereco enderecoEntity = usuarioConverter.cadastraEndereco(enderecoDTO, usuario.getId());
        // Salva o endereço
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(enderecoEntity));
    }



}
