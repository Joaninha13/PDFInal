package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorBackup;

import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiClient;
import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServiceCli extends UnicastRemoteObject implements IRmiClient {

    private static String REGISTRY_IP;
    private static String SERVICE_NAME;
    private static int RMI_PORT ;
    public static final int MAX_CHUNCK_SIZE = 10000; //bytes
    private static final String DIR = "dataBaseTP.db";
    private static String RMILOC;

    public final String DBRDirectory;

    FileOutputStream fout = null;

    public RmiServiceCli(String name, String ip, int port, String DBRDirectory) throws RemoteException {
        SERVICE_NAME = name;
        REGISTRY_IP = ip;
        RMI_PORT = port;
        this.DBRDirectory = DBRDirectory;

        RMILOC = "rmi://" + REGISTRY_IP + "/" + SERVICE_NAME;
    }

    public void runn(){
        try{
            IRmiService remoteFileService;

            try(FileOutputStream localFileOutputStream = new FileOutputStream(DIR)){
                System.out.println("Ficheiro " + DIR + " criado.");

                remoteFileService = (IRmiService) Naming.lookup(RMILOC);

                this.setFout(localFileOutputStream);

                remoteFileService.getDb(DIR,this);

            }

        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }

    public synchronized void setFout(FileOutputStream fout) {this.fout = fout;}

    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws IOException {

        if (fout == null) {
            System.out.println("Nao existe qualquer ficheiro aberto para escrita!");
            throw new IOException("<CLI> Nao existe qualquer ficheiro aberto para escrita!");
        }
        try {
            fout.write(fileChunk, 0, nbytes);
        } catch (IOException e) {
            System.out.println("Erro ao escrever no ficheiro!" + e);
            throw new IOException("<CLI> Erro ao escrever no ficheiro!", e.getCause());
        }
    }
}
