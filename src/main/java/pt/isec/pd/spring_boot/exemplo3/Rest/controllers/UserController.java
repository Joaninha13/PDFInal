package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.Rest.Utils;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.consultPresenModel;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.regModel;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {


    private static final conectionBD db = conectionBD.getInstance();

    //Registar um utilizador
    @PostMapping("/reg")
    public ResponseEntity<String> regUser(@RequestBody regModel reg) {

        registo auxreg = db.registaCliente(new registo(reg.getNome(), reg.getEmail(), reg.getPassword()));

        if(!auxreg.isRegistered())
            return ResponseEntity.badRequest().body(auxreg.getMsg());

        return ResponseEntity.ok(auxreg.getMsg());
    }

    //submeter um codigo para registo de presença num evento
    @PostMapping("/subCode")
    public ResponseEntity<String> subCode(@RequestParam("code") String code, Authentication authentication) {

        String response = db.registaPresenca(code, authentication.getName());

        if(!response.equals("Presenca registada com sucesso"))
            return ResponseEntity.badRequest().body(response);

        return ResponseEntity.ok(response);
    }

    //consultar todas as presenças de um utilizador
    @GetMapping
    public ResponseEntity<consultPresenModel> getPresences(@RequestParam("di") Optional<String> data_inicio, @RequestParam("df") Optional<String> data_fim, @RequestParam("loc") Optional<String> local, @RequestParam("desc") Optional<String> descricao, Authentication authentication) {

        consultPresenModel presenReturn = new consultPresenModel(new ArrayList<>(), new ArrayList<>());

        Date dataInicio = Utils.StrToDate(data_inicio.orElse(""));
        Date dataFim = Utils.StrToDate(data_fim.orElse(""));

        ConsultPresence consultPresence = db.consultPresencesUtilizador(authentication.getName());

        for (events aux : consultPresence.getEvent()) {
            boolean add = true;
            if (dataInicio != null && aux.getDatas().compareTo(dataInicio) < 0)
                add = false;

            if (dataFim != null && aux.getDatas().compareTo(dataFim) > 0)
                add = false;

            if (local.isPresent() && !aux.getLocal().contains(local.get()))
                add = false;

            if (descricao.isPresent() && !aux.getDescricao().contains(descricao.get()))
                add = false;

            if (add) {
                presenReturn.getEvent().add(new events(
                        aux.getDescricao(),
                        aux.getLocal(),
                        aux.getData(),
                        aux.getHoraIncio(),
                        aux.getHoraFim()
                ));
            }

        }

        return ResponseEntity.status(HttpStatus.OK).body(presenReturn);
    }
}
