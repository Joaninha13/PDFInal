package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.RMI;

import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiClient;
import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiObserver;
import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RmiService extends UnicastRemoteObject implements IRmiService {


    private static final String REGISTRY_IP = "127.0.0.1";

    private static String SERVICE_NAME;
    private static int RMI_PORT ;
    public static final int MAX_CHUNCK_SIZE = 10000; //bytes

    protected File DBDirectory;

    List<IRmiObserver> backUpServersList; // lista de observadores

    //protected functions
    protected FileInputStream getRequestedFileInputStream(String fileName) throws IOException {
        String requestedCanonicalFilePath;

        fileName = fileName.trim();

        /*
         * Verifica se o ficheiro solicitado existe e encontra-se por baixo da localDirectory.
         */

        requestedCanonicalFilePath = new File(DBDirectory + File.separator + fileName).getCanonicalPath();

        if (!requestedCanonicalFilePath.startsWith(DBDirectory.getCanonicalPath() + File.separator)) {
            System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
            System.out.println("A directoria de base nao corresponde a " + DBDirectory.getCanonicalPath() + "!");
            throw new AccessDeniedException(fileName);
        }

        /*
         * Abre o ficheiro solicitado para leitura.
         */
        return new FileInputStream(requestedCanonicalFilePath);

    }
    protected /*synchronized*/ void notifyBackUpServers(String msg){

        List<IRmiObserver> backUpsServersToRemove = new ArrayList<>();

        synchronized (backUpServersList) {
            for (IRmiObserver observer : backUpServersList) {
                try {
                    observer.notifyNewOperationConcluded(msg);
                } catch (RemoteException ex) {
                    System.out.println("Erro ao notificar observador: " + ex.getMessage());
                    backUpsServersToRemove.add(observer);
                }
            }

            backUpServersList.removeAll(backUpsServersToRemove);
        }
    }

    public RmiService(String serviceNameRMI, int listeningPortRMI, File DBDirectory) throws RemoteException {
        /*
         * Cria o servico.
         */
        SERVICE_NAME = serviceNameRMI;
        RMI_PORT = listeningPortRMI;
        this.DBDirectory = DBDirectory;
        backUpServersList = new ArrayList<>();
    }

    public void start() {
        try{

            try{

                System.out.println("Tentativa de lancamento do registry no porto " +
                        RMI_PORT + "...");

                LocateRegistry.createRegistry(RMI_PORT);

                System.out.println("Registry lancado!");

            }catch(RemoteException e){
                System.out.println("Registry provavelmente ja em execucao!");
            }

            System.out.println("Servico RmiService criado e em execucao ("+this.getRef().remoteToString()+"...");

            /*
             * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
             * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
             */

            Naming.bind("rmi://" + REGISTRY_IP + "/" + SERVICE_NAME, this);

            System.out.println("Servico " + SERVICE_NAME + " registado no registry...");

            /*
             * Para terminar um servico RMI do tipo UnicastRemoteObject:
             *
             *  UnicastRemoteObject.unexportObject(fileService, true).
             */

        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }

    } // feito

    @Override
    public byte[] getFileChunk(String fileName, long offset) throws IOException, RemoteException {
        byte[] fileChunk = new byte[MAX_CHUNCK_SIZE];
        int nbytes;

        fileName = fileName.trim();
        //System.out.println("Recebido pedido para: " + fileName);

        try (FileInputStream requestedFileInputStream = getRequestedFileInputStream(fileName)) {

            /*
             * Obtem um bloco de bytes do ficheiro, omitindo os primeiros offset bytes.
             */
            requestedFileInputStream.skip(offset);
            nbytes = requestedFileInputStream.read(fileChunk);

            if (nbytes == -1) {//EOF
                return null;
            }

            /*
             * Se fileChunk nao esta' totalmente preenchido (MAX_CHUNCK_SIZE), recorre-se
             * a um array auxiliar com tamanho correspondente ao numero de bytes efectivamente lidos.
             */
            if (nbytes < fileChunk.length) {
                return Arrays.copyOf(fileChunk, nbytes);
            }

            return fileChunk;

        } catch (IOException e) {
            System.out.println("Ocorreu a excecao de E/S: \n\t" + e);
            throw new IOException(fileName, e.getCause());
        }
    }

    @Override
    public void getDb(String fileName, IRmiClient cliRef) throws IOException, RemoteException {

        byte[] fileChunk = new byte[MAX_CHUNCK_SIZE];
        int nbytes;

        fileName = fileName.trim();
        System.out.println("Recebido pedido para: " + fileName);

        try (FileInputStream requestedFileInputStream = getRequestedFileInputStream(fileName)) {

            /*
             * Obtem os bytes do ficheiro por blocos de bytes ("file chunks").
             */
            while ((nbytes = requestedFileInputStream.read(fileChunk)) != -1) {

                /*
                 * Escreve o bloco actual no cliente, invocando o metodo writeFileChunk da
                 * sua interface remota.
                 */

                cliRef.writeFileChunk(fileChunk, nbytes);

            }

            System.out.println("Ficheiro " + new File(DBDirectory + File.separator + fileName).getCanonicalPath() +
                    " transferido para o cliente com sucesso.");
            notifyBackUpServers("Ficheiro " + new File(DBDirectory + File.separator + fileName).getCanonicalPath() +
                    " transferido para o cliente com sucesso.");
            System.out.println();

        } catch (FileNotFoundException e) {   //Subclasse de IOException
            System.out.println("Ocorreu a excecao {" + e + "} ao tentar abrir o ficheiro!");
            notifyBackUpServers("Execeçao ao tentar abrir o ficheiro " + fileName);
            throw new FileNotFoundException("Exceçao eo abrir o ficheiro " + fileName);
        } catch (IOException e) {
            System.out.println("Ocorreu a excecao de E/S: \n\t" + e);
            notifyBackUpServers("Exceçao ao aceder para a leitura ao ficheiro " + fileName);
            throw new IOException("Exceçao eo abrir o ficheiro " + fileName, e.getCause());
        }
    }

    @Override
    public void addBackUp(IRmiObserver serverBackup) throws RemoteException {
        synchronized (backUpServersList) {
            backUpServersList.add(serverBackup);
            System.out.println("+ um observador.");
        }
    }

    @Override
    public void deleteBackUp(IRmiObserver serverBackup) throws RemoteException {
        synchronized (backUpServersList) {
            backUpServersList.remove(serverBackup);
            System.out.println("- um observador.");
        }
    }

}
