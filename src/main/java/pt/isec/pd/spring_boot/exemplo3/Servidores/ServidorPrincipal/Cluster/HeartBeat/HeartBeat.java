package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.heartBeatMsg.HeartBeatMess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartBeat  extends Thread implements HearBeatInterface {

    private static final int Sleep = 10000; // 10 segundos
    private final InetAddress ipGroup;
    private final MulticastSocket ms;
    private final int port;
    private int listeningPortRMI;
    private String serviceNameRMI;
    private static final conectionBD db = conectionBD.getInstance();
    private boolean terminate;


    public HeartBeat(String serviceNameRMI, int listeningPortRMI, MulticastSocket ms, InetAddress ipGroup, int port){
        this.listeningPortRMI = listeningPortRMI;
        this.serviceNameRMI = serviceNameRMI;
        this.ms = ms;
        this.ipGroup = ipGroup;
        this.port = port;
        terminate = false;
    }


    @Override
    public void send() {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)){

            HeartBeatMess HeartBeatMessage = new HeartBeatMess(listeningPortRMI, serviceNameRMI, db.getVersion());
            oos.writeObject(HeartBeatMessage);

            byte[] msgBuffer = baos.toByteArray();
            DatagramPacket dp = new DatagramPacket(msgBuffer, msgBuffer.length, ipGroup, port);
            ms.send(dp);
            System.out.println("Sent heartbeat");
        } catch (IOException e) {
            System.out.println("Error sending heartbeat - " + e.getMessage());
        }

    }

    @Override
    public void run() {
        this.terminate = false;
        while (!terminate)
        {
            try {
                send();
                Thread.sleep(Sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        this.terminate = true;
    }

}
