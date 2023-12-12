package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.LoremConfig;

@RestController
@RequestMapping("lorem")
public class LoremController {

    @GetMapping
    public ResponseEntity getTextRandomType(Authentication authentication,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value="length", required=false) Integer length)     {
        if (type == null)
            type = (Math.random()<0.5 ? "word":"paragraph");

        if (length == null)
            length = 1;

        String subject = authentication.getName();
        Jwt userDetails = (Jwt) authentication.getPrincipal();
        String scope = userDetails.getClaim("scope");

        return generateLorem(type, length, String.format("[%s/%s]",subject,scope));
    }

    @GetMapping("{type}")
    public ResponseEntity getText(@PathVariable("type") String type,
                                  @RequestParam(value="length", required=false) Integer length) {
        if (length == null)
            length = 1;

        return this.generateLorem(type, length, null);
    }

    @PostMapping
    public ResponseEntity postText(@RequestBody LoremConfig config) {
        if (config.getType() == null)
            return ResponseEntity.badRequest().body("Type is mandatory.");

        if (config.getLength() == null)
            config.setLength(1);

        return this.generateLorem(config.getType(), config.getLength(), null);
    }

    private ResponseEntity generateLorem(String type, Integer length, String prefix) {
        Lorem lorem = LoremIpsum.getInstance();

        switch(type.toLowerCase()) {
            case "word" -> {
                return ResponseEntity.ok((prefix==null ? "" : prefix+" -> ") + lorem.getWords(length));
            }
            case "paragraph" -> {
                return ResponseEntity.ok((prefix==null ? "" : prefix+" -> ") + lorem.getParagraphs(length, length));
            }
            default -> {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Invalid type: " + type +".");
            }
        }
    }

}
