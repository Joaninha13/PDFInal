package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorBackup.Cluster;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorBackup.RmiServiceCli;
import pt.isec.pd.spring_boot.exemplo3.share.heartBeatMsg.HeartBeatMess;

import java.io.*;
import java.net.*;

public class MulticastChat extends Thread {
    private static final int MAX_SIZE = 1000;
    private static final int MAX_TIME = 3000;

    protected String username;
    private final MulticastSocket s;
    protected boolean running;

    private final String DBRDirectory;
    private boolean justOneTime = false;

    public MulticastChat(String username, MulticastSocket s, String DBRDirectory) {
        this.username = username;
        this.s = s;
        this.DBRDirectory = DBRDirectory;
        running = true;
    }

    public void terminate() {
        running = false;
    }

    public boolean isRunning(){return running;}

    @Override
    public void run() {

        DatagramPacket pkt;
        HeartBeatMess hbm;
        Object obj;
        RmiServiceCli rmi = null;

        if (s == null || !running) {
            return;
        }

        try {

            while (running) {

                s.setSoTimeout(30000);

                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);

                System.out.println("recebi packet");

                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()))) {

                    // "Deserializa" o objecto transportado no datagrama acabado de ser recebido

                    obj = in.readObject();

                    System.out.println();
                    System.out.print("(" + pkt.getAddress().getHostAddress() + ":" + pkt.getPort() + ") ");

                    hbm = (HeartBeatMess) obj;

                    if(!justOneTime){
                        rmi = new RmiServiceCli(hbm.getServiceNameRMI(), pkt.getAddress().getHostAddress(), pkt.getPort(), DBRDirectory);
                        rmi.runn();
                        justOneTime = true;
                    }

                    rmi.runn();
                    System.out.println();
                    System.out.print("> ");

                } catch (ClassNotFoundException e) {
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado!");
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
                }

            }

        }catch (SocketTimeoutException e){
            System.out.println("Timeout");
            terminate();

        } catch (IOException e) {
            if (running) {
                System.out.println(e);
            }

            if (!s.isClosed()) {
                s.close();
            }
        }

    }
}