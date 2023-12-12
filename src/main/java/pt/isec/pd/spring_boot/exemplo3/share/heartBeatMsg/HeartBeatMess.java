package pt.isec.pd.spring_boot.exemplo3.share.heartBeatMsg;

import java.io.Serializable;

public class HeartBeatMess implements Serializable{

    private static final long serialVersionUID = 1L;

    private int listeningPortRMI;
    private String serviceNameRMI;
    private int databaseVersion;

    public HeartBeatMess(int listeningPortRMI, String serviceNameRMI, int databaseVersion) {
        this.listeningPortRMI = listeningPortRMI;
        this.serviceNameRMI = serviceNameRMI;
        this.databaseVersion = databaseVersion;
    }

    public int getListeningPortRMI() {return listeningPortRMI;}

    public String getServiceNameRMI() {return serviceNameRMI;}

    public int getDatabaseVersion() {return databaseVersion;}
}
