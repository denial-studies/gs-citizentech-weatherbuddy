package br.com.fiap.hidroalerta.infrastructure.security;

import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.domain.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ResponsavelRepository responsavelRepository;

    @Override
    public UserDetails loadUserByUsername(String emailLogin) throws UsernameNotFoundException {
        Responsavel responsavel = responsavelRepository.buscarPorEmailLogin(emailLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Responsável não encontrado para o e-mail: " + emailLogin));

        if (!responsavel.isAtivo()) {
            throw new UsernameNotFoundException("O cadastro do responsável encontra-se inativo: " + emailLogin);
        }

        return new User(
                responsavel.getEmailLogin(),
                responsavel.getSenhaHash(),
                true,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))
        );
    }
}

