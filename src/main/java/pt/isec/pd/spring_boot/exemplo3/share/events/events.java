package pt.isec.pd.spring_boot.exemplo3.share.events;

import java.io.Serializable;

public class events implements Serializable {

    private static final long serialVersionUID = 1L;

    private String descricao, local, data, horaIncio, horaFim;

    private String msg;


    public events(String descricao, String local, String data, String horaIncio, String horaFim) {
        this.descricao = descricao;
        this.local = local;
        this.data = data;
        this.horaIncio = horaIncio;
        this.horaFim = horaFim;
    }

    public String getDescricao() {return descricao;}

    public void setDescricao(String descricao) {this.descricao = descricao;}

    public String getLocal() {return local;}

    public void setLocal(String local) {this.local = local;}

    public String getData() {return data;}

    public void setData(String data) {this.data = data;}

    public String getHoraIncio() {return horaIncio;}

    public void setHoraIncio(String horaIncio) {this.horaIncio = horaIncio;}

    public String getHoraFim() {return horaFim;}

    public void setHoraFim(String horaFim) {this.horaFim = horaFim;}

    public void setMsg(String msg) {this.msg = msg;}

    public String getMsg() {return msg;}

    @Override
    public String toString() {
        return "events{" +
                "descricao='" + descricao + '\'' +
                ", local='" + local + '\'' +
                ", data='" + data + '\'' +
                ", horaIncio='" + horaIncio + '\'' +
                ", horaFim='" + horaFim + '\'' +
                '}';
    }
}
