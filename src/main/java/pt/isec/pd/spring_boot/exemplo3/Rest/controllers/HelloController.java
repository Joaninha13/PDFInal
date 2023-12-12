package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    /*================= Exercício =================*/
    @GetMapping("/hello/{lang}")
    public ResponseEntity<String> helloLanguages(@PathVariable("lang") String language,
                                                 @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        String responseBody;

        switch (language.toUpperCase()){
            case "UK" -> responseBody = "Hello " + name + "!";
            case "PT" -> responseBody ="Ola' " + name + "!";
            case "ES" -> responseBody = "Ola " + name + "!";
            case "FR" -> responseBody = "Salut " + name + "!";
            default -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not supported language:" + language + ".");
            }
        }

        //pt.isec.pd.spring_boot.exemplo1.Application.log.info(responseBody);

        return ResponseEntity.ok(responseBody);
    }

/*
    @GetMapping("/hello/{lang}")
    public String helloLanguages(@PathVariable("lang") String language,
                                 @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        switch (language.toUpperCase()){
            case "UK" -> {
                return ("Hello " + name + "!");
            }
            case "PT" -> {
                return ("Ola' " + name + "!");
            }
            case "ES" -> {
                return ("Ola " + name + "!");
            }
            case "FR" -> {
                return ("Salut " + name + "!");
            }
            default -> {
                return ("Unsupported language!");
            }
        }

    }
*/

}


