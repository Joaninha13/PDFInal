package pt.isec.pd.spring_boot.exemplo3.Observers;

import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiObserver;
import pt.isec.pd.spring_boot.exemplo3.share.RMI.IRmiService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Observers extends UnicastRemoteObject implements IRmiObserver {

    public Observers() throws RemoteException {}

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException {
        System.out.println("Notificacao: " + description);
    }

    public static void main(String[] args) {

        try {
            if (args.length < 2) {

                System.out.println("Deve passar 3 argumentos na linha de comando:");
                System.out.println("1 - Endereço do RMI Registry onde esta registado o servico remoto de download ");
                System.out.println("2 - Nome do serviço do RMI Registry onde esta registado o servico remoto de download");
                System.exit(1);
            }

            System.setProperty("java.rmi.server.hostname", args[0]);

            //localiza o servico remoto nomeado "GetRemoteFileService"
            String objectUrl = "rmi://" + args[0] + "/" + args[1];

            System.out.println("Vou procurar o servico remoto em " + objectUrl);

            IRmiService remoteFileService = (IRmiService) Naming.lookup(objectUrl);

            // ver isto ainda depois!!
            IRmiService getRemote = (IRmiService) Naming.lookup(objectUrl);

            //Cria e lanca o servico

            Observers observer = new Observers();
            System.out.println("Serviço GetRemoteFileObserver criado e em execução");

            //adiciona o observador no serviço remoto
            remoteFileService.addBackUp(observer);


            System.out.println("<ENTER> para terminar...");
            System.in.read();

            remoteFileService.deleteBackUp(observer);
            UnicastRemoteObject.unexportObject(observer, true);


        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
