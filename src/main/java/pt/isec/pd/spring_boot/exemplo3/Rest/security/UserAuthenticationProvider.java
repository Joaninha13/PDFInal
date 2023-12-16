package pt.isec.pd.spring_boot.exemplo3.Rest.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{

    private static final conectionBD db = conectionBD.getInstance();


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        registo reg = db.autenticaCliente(username, password);

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (reg.getEmail().equals("admin@isec.pt") && reg.getPassword().equals("admin"))
            authorities.add(new SimpleGrantedAuthority("ADMIN"));

        return new UsernamePasswordAuthenticationToken(reg.getEmail(), reg.getPassword(), authorities);
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
