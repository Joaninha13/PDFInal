package pt.isec.pd.spring_boot.exemplo3.share.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRmiObserver extends Remote {

    void notifyNewOperationConcluded(String description) throws RemoteException;

}
