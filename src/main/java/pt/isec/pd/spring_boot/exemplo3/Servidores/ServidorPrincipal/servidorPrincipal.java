package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat.DBUpdate;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat.HeartBeat;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.ConectionClientThread.conectionClientThread;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.RMI.RmiService;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class servidorPrincipal extends Thread{

    private static final String ADDRESS = "230.44.44.44";
    private static final int PORT = 4444;

    private String [] args;

    public servidorPrincipal(String [] args){this.args = args;}


    @Override
    public void run(){

        File DBDirectory;

        if(args.length != 5){
            System.out.println("Sintaxe: java servidorPrincipal <listeningPort> <localDirectoryBD> <serviceNameRMI> <listeningPortRMI> <IP_Interface_Network_RMI_Service>");
            return;
        }

        System.setProperty("java.rmi.server.hostname", args[4]);

        DBDirectory = new File(args[1].trim());

        if(!DBDirectory.exists()){
            System.out.println("A directoria " + DBDirectory + " nao existe!");
            return;
        }

        if(!DBDirectory.isDirectory()){
            System.out.println("O caminho " + DBDirectory + " nao se refere a uma diretoria!");
            return;
        }

        if(!DBDirectory.canRead()){
            System.out.println("Sem permissoes de leitura na diretoria " + DBDirectory + "!");
            return;
        }


        try(ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]))){

            MulticastSocket ms = new MulticastSocket(PORT);
            InetAddress ipGroup = InetAddress.getByName(ADDRESS);

            new DBUpdate(args[2], Integer.parseInt(args[3]), ms, ipGroup, PORT);

            conectionBD bd = new conectionBD(args[1].trim());

            new RmiService(args[2], Integer.parseInt(args[3]), DBDirectory).start();

            new HeartBeat(args[2], Integer.parseInt(args[3]), ms, ipGroup, PORT).start();

            System.out.println("TCP Server iniciado no porto " + socket.getLocalPort() + " ...");

            while(true){

                Socket toClientSocket = socket.accept();
                new conectionClientThread(toClientSocket, bd).start();

            }

        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(IOException e){
            System.out.println("Ocorreu um erro ao nivel do socket de escuta:\n\t"+e);
        }
    }

    /*public static void main(String[] args) {
        File DBDirectory;

        if(args.length != 5){
            System.out.println("Sintaxe: java servidorPrincipal <listeningPort> <localDirectoryBD> <serviceNameRMI> <listeningPortRMI> <IP_Interface_Network_RMI_Service>");
            return;
        }

        System.setProperty("java.rmi.server.hostname", args[4]);

        DBDirectory = new File(args[1].trim());

        if(!DBDirectory.exists()){
            System.out.println("A directoria " + DBDirectory + " nao existe!");
            return;
        }

        if(!DBDirectory.isDirectory()){
            System.out.println("O caminho " + DBDirectory + " nao se refere a uma diretoria!");
            return;
        }

        if(!DBDirectory.canRead()){
            System.out.println("Sem permissoes de leitura na diretoria " + DBDirectory + "!");
            return;
        }


        try(ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]))){

            MulticastSocket ms = new MulticastSocket(PORT);
            InetAddress ipGroup = InetAddress.getByName(ADDRESS);

            new DBUpdate(args[2], Integer.parseInt(args[3]), ms, ipGroup, PORT);

            conectionBD bd = new conectionBD(args[1].trim());

            new RmiService(args[2], Integer.parseInt(args[3]), DBDirectory).start();

            new HeartBeat(args[2], Integer.parseInt(args[3]), ms, ipGroup, PORT).start();

            System.out.println("TCP Server iniciado no porto " + socket.getLocalPort() + " ...");

            while(true){

                Socket toClientSocket = socket.accept();
                new conectionClientThread(toClientSocket, bd).start();

            }

        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(IOException e){
            System.out.println("Ocorreu um erro ao nivel do socket de escuta:\n\t"+e);
        }
    }*/
}
