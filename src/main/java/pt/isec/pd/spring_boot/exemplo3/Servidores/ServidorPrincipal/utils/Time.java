package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.utils;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;

public class Time extends Thread{

    private static final int Sleep = 60000; // 60 segundos

    private final conectionBD db = conectionBD.getInstance();

    @Override
    public void run() {

    while (true)
        {
            try {
                Thread.sleep(Sleep);
                db.updateTimes();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
