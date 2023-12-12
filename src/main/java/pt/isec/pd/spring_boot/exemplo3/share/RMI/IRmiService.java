package pt.isec.pd.spring_boot.exemplo3.share.RMI;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRmiService extends Remote {

    byte [] getFileChunk(String fileName, long offset) throws IOException, RemoteException;

    void getDb(String fileName, IRmiClient cliRef) throws IOException, RemoteException;

    void addBackUp(IRmiObserver serverBackup) throws RemoteException;
    void deleteBackUp(IRmiObserver serverBackup) throws RemoteException;

}
