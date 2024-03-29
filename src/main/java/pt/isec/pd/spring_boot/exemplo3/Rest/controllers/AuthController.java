package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.spring_boot.exemplo3.Rest.security.TokenService;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

@RestController
public class AuthController
{
    private final TokenService tokenService;

    private static final conectionBD db = conectionBD.getInstance();

    public AuthController(TokenService tokenService)
    {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public String login(Authentication authentication){
            return tokenService.generateToken(authentication);
    }
}
