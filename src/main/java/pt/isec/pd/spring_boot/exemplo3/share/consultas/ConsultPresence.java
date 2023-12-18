package pt.isec.pd.spring_boot.exemplo3.share.consultas;

import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConsultPresence implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<registo> reg;
    private List<events> event;

    public ConsultPresence() {
        reg = new ArrayList<>();
        event = new ArrayList<>();
    }

    public List<registo> getReg() {return reg;}

    public void setReg(List<registo> reg) {this.reg = reg;}

    public registo getRegisto(){return reg.get(0);}

    public List<events> getEvent() {return event;}

    public events getEvents(){return event.get(0);}

    public void setEvent(List<events> event) {this.event = event;}
}
