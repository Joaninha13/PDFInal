package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.Rest.Utils;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.consultPresenModel;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.eventsModel;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.geraCodigoModel;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private static final conectionBD db = conectionBD.getInstance();

    @PostMapping
    public ResponseEntity<String> addEvent(@RequestBody eventsModel events) {

        if (!Utils.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não tem permissões de Admin para criar eventos");


        String result = db.criaEvento(events.getDescricao(), events.getLocal(), events.getData(), events.getHoraInicio(), events.getHoraFim());

        if (!result.equals("Evento criado com sucesso"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    //Eliminar um evento
    @DeleteMapping("/{descricao}")
    public ResponseEntity<String> deleteEvent(@PathVariable("descricao") String descricao) {

        if (!Utils.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não tem permissões para gerar códigos");


        String result = db.eliminaEvento(descricao);

        if (!result.equals("Evento eliminado com sucesso"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    //Consultar os eventos criados, podendo ser aplicados diversos tipos de critérios/filtros opcionais
    @GetMapping
    public ResponseEntity<ArrayList<eventsModel>> getEvents(@RequestParam("di") Optional<String> data_inicio, @RequestParam("df") Optional<String> data_fim, @RequestParam("loc") Optional<String> local) {

        if (!Utils.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());


        Date dataInicio = Utils.StrToDate(data_inicio.orElse(""));
        Date dataFim = Utils.StrToDate(data_fim.orElse(""));
        ArrayList<eventsModel> eventReturn = new ArrayList<>();
        for (events eventos : db.consultaEventos().getEvent()) {
            boolean add = true;
            if (dataInicio != null && eventos.getDatas().compareTo(dataInicio) < 0)
                add = false;

            if (dataFim != null && eventos.getDatas().compareTo(dataFim) > 0)
                add = false;

            if (local.isPresent() && !eventos.getLocal().contains(local.get()))
                add = false;

            if (add)
                eventReturn.add(new eventsModel(
                        eventos.getDescricao(),
                        eventos.getLocal(),
                        eventos.getData(),
                        eventos.getHoraIncio(),
                        eventos.getHoraFim()
                ));
        }
        return ResponseEntity.ok(eventReturn);
    }


    //Gerar um código de registo de presenças para um evento, com indicação do tempo de validade em minutos
    @PostMapping("/gerarCodigo")
    public ResponseEntity<String> geraCodigo(@RequestBody geraCodigoModel gerar) {

        if (!Utils.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não tem permissões de admin");

        String result = db.geraCodigo(gerar.getDescricao(), gerar.getTempoValidade());

        if (!result.equals("Evento nao existe") || !result.equals("Erro ao gerar codigo"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    //Consultar as presenças registadas num determinado evento.
    @GetMapping("/presencas")
    public ResponseEntity<consultPresenModel> getPresenceEvents(@RequestParam("desc") String descricao) {
        consultPresenModel eventReturn = null;

        if (!Utils.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(eventReturn);

        ConsultPresence consultPresence = db.consultaPresencasEvento(descricao);

        if(consultPresence != null) {
            eventReturn = new consultPresenModel(consultPresence.getEvent(), consultPresence.getReg());
            return ResponseEntity.status(HttpStatus.OK).body(eventReturn);
        }

        return ResponseEntity.ok(eventReturn);
    }



}
