package dev.kevinwilliam.usuario.business.dto;

import dev.kevinwilliam.usuario.infrastructure.entity.Endereco;
import dev.kevinwilliam.usuario.infrastructure.entity.Telefone;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String nome;
    private String email;
    private String senha;
    private List<EnderecoDTO> enderecos;
    private List <TelefoneDTO> telefones;
}
