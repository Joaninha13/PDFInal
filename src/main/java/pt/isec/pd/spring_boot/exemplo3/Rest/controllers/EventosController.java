package pt.isec.pd.spring_boot.exemplo3.Rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.eventsModel;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private static final conectionBD db = conectionBD.getInstance();

    /*@PutMapping("/add")
    public ResponseEntity<eventsModel> addEvent(@PathVariable("descricao") String descricao,
                                                @PathVariable("local") String local,
                                                @PathVariable("data") String data,
                                                @PathVariable("horaInicio") String horaInicio,
                                                @PathVariable("horaFim") String horaFim) {

        if(db.criaEvento(descricao, local, data, horaInicio, horaFim).equals("Evento criado com sucesso!"))
            return ResponseEntity.ok(new eventsModel(descricao, local, data, horaInicio, horaFim));
        else
            return ResponseEntity.badRequest().build();

    }*/

    @PostMapping("/add")
    public ResponseEntity<eventsModel> addEvent(@RequestParam("descricao") String descricao,
                                                @RequestParam("local") String local,
                                                @RequestParam("data") String data,
                                                @RequestParam("horaInicio") String horaInicio,
                                                @RequestParam("horaFim") String horaFim) {

        String result = db.criaEvento(descricao, local, data, horaInicio, horaFim);

        if (result.equals("Evento criado com sucesso!")) {
            eventsModel newEvent = new eventsModel(descricao, local, data, horaInicio, horaFim);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    //Eliminar um evento
    @DeleteMapping("/delete/{descricao}")
    public ResponseEntity<String> deleteEvent(@PathVariable("descricao") String descricao) {

        String result = db.eliminaEvento(descricao);

        if (result.equals("Evento eliminado com sucesso!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }



    //Consultar os eventos criados, podendo ser aplicados diversos tipos de critérios/filtros opcionais


    //Gerar um código de registo de presenças para um evento, com indicação do tempo de validade em minutos




    //Consultar as presenças registadas num determinado evento.
}
