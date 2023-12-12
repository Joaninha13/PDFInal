package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorBackup;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorBackup.Cluster.MulticastChat;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class servidorBackup {

    private static final String ADDRESS = "230.44.44.44";
    private static final int PORT = 4444;
    static String FILENAME = "dataBaseTP.db";

    public static void main(String[] args) throws IOException {
        File DBRDirectory;
        String filepath;

        boolean isEmptyDirectory;

        if(args.length != 2){
            System.out.println("Sintaxe: java servidorBackup <dir_replica_bd> <IP_Interface_Network_RMI_Service>");
            return;
        }

        System.setProperty("java.rmi.server.hostname", args[1]);

        //trocar para args[0]...
        Path dirRepBd = Paths.get(args[0].trim());
        DBRDirectory = new File(dirRepBd.toAbsolutePath().toString());

        if(!DBRDirectory.exists()){
            System.out.println("A directoria " + DBRDirectory + " nao existe!");
            return;
        }
        if(!DBRDirectory.isDirectory()){
            System.out.println("O caminho " + DBRDirectory + " nao se refere a uma diretoria!");
            return;
        }
        if(!DBRDirectory.canWrite()){
            System.out.println("Sem permissoes de escrita na diretoria " + DBRDirectory);
            return;
        }
        if((Files.list(dirRepBd).findAny().isPresent())) {
            System.out.println("Diretoria não está vazia!\n");
            return;
        }

        NetworkInterface nif;
        MulticastSocket mskt = null;
        MulticastChat mchat = null;

        try {

            try{
                filepath = new File(DBRDirectory.getPath()+File.separator+FILENAME).getCanonicalPath();
            } catch (IOException ex) {
                System.out.println("Erro E/S - " + ex);
                return;
            }

            try {
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[0]));
            } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
                nif = NetworkInterface.getByName(args[0]);
            }

            mskt = new MulticastSocket(PORT);
            mskt.joinGroup(new InetSocketAddress(ADDRESS, PORT),nif);
            mchat = new MulticastChat("S", mskt, filepath);

            mchat.start();
            mchat.join();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (mchat != null) {
                mchat.terminate();
            }
            if (mskt != null){
                mskt.close();
            }

        }
    }
}