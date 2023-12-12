package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.heartBeatMsg.HeartBeatMess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DBUpdate {

    private final InetAddress ipGroup;
    private final MulticastSocket ms;
    private final int port;
    private int listeningPortRMI;
    private String serviceNameRMI;

    private static volatile DBUpdate instance = null;


    public static DBUpdate getInstance() {return instance;}

    public DBUpdate(String serviceNameRMI, int listeningPortRMI, MulticastSocket ms, InetAddress ipGroup, int port){
        instance = this;
        this.listeningPortRMI = listeningPortRMI;
        this.serviceNameRMI = serviceNameRMI;
        this.ms = ms;
        this.ipGroup = ipGroup;
        this.port = port;
    }


    public void send(int newVersion) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)){

            HeartBeatMess HeartBeatMessage = new HeartBeatMess(listeningPortRMI, serviceNameRMI, newVersion);
            oos.writeObject(HeartBeatMessage);

            byte[] msgBuffer = baos.toByteArray();
            DatagramPacket dp = new DatagramPacket(msgBuffer, msgBuffer.length, ipGroup, port);
            ms.send(dp);
            System.out.println("Sent updateHeartBeat");

        } catch (IOException e) {
            System.out.println("Error sending heartbeat - " + e.getMessage());
        }

    }
}
