package pt.isec.pd.spring_boot.exemplo3.Clientes.communication;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

public class ClientCommunication {

    private static final int MAX_SIZE = 4000;
    private InetAddress serverAddr;
    private int serverPort;
    private Socket socket;

    public ClientCommunication(String serverAddress, int serverPort) throws UnknownHostException {
        this.serverAddr = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
        this.socket = new Socket();
    }

    public registo authenticateUser(String email,String password) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "login " + email + " " + password;
            oout.writeObject(message);
            oout.flush();

            return (registo) oin.readObject();
        }
    }

    public registo editUserData(registo userData) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            userData.setMsg("edit");

            oout.writeObject(userData);
            oout.flush();

            return (registo) oin.readObject();
        }
    }

    public registo registerUser(registo userRegister) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            oout.writeObject(userRegister);
            oout.flush();

            return (registo) oin.readObject();
        }
    }

    public String submitCode(String code, String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "sub " + code + " " + email;
            oout.writeObject(message);
            oout.flush();

            String response = (String) oin.readObject();
            return response;
        }
    }

    public ConsultPresence consultAttendance(String email, String filter) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "ConsultPresenca " + email;
            if (filter != null && !filter.isEmpty()) {
                message += " " + filter;
            }

            oout.writeObject(message);
            oout.flush();

            return (ConsultPresence) oin.readObject();
        }
    }

    public String sendEventDetails(String eventDetails) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            oout.writeObject(eventDetails);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public String updateEvent(events event) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            oout.writeObject(event);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public String deleteEvent(String descricaoEvento) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "delete " + descricaoEvento;
            oout.writeObject(message);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public String generatePresenceCode(String descEvento, int tempoValidade) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "gerar " + descEvento + " " + tempoValidade;
            oout.writeObject(message);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public ConsultPresence consultEvents(String filter) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "ConsultEvents";
            if (filter != null && !filter.isEmpty()) {
                message += " " + filter;
            }
            oout.writeObject(message);
            oout.flush();

            return (ConsultPresence) oin.readObject();
        }
    }

    public ConsultPresence consultPresenceInEvent(String eventName) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "ConsultPesencaEvent " + eventName;
            oout.writeObject(message);
            oout.flush();

            return (ConsultPresence) oin.readObject();
        }
    }

    public String deleteRegisteredAttendance(String email, String eventName) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "checkout " + email + " " + eventName;
            oout.writeObject(message);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public String insertAttendance(String email, String eventName) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddr, serverPort);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

            String message = "checkin " + email + " " + eventName;
            oout.writeObject(message);
            oout.flush();

            return (String) oin.readObject();
        }
    }

    public boolean isConnected() {
        try {
            if (!socket.isConnected()) {
                socket.connect(new InetSocketAddress(serverAddr, serverPort));
            }
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    public void obterArquivoCSV(String serverAddress, int serverPort, String userEmail, String filtro, String nomeArquivoCSV) {
        try (Socket socketToServer = new Socket(serverAddress, serverPort);
             ObjectOutputStream dout = new ObjectOutputStream(socketToServer.getOutputStream());
             DataInputStream din = new DataInputStream(socketToServer.getInputStream());
             FileOutputStream fileOut = new FileOutputStream("src/Clientes/utils" + File.separator + nomeArquivoCSV)) {

            String msg = "CSVU " + userEmail + " " + filtro + " " + nomeArquivoCSV;
            dout.writeObject(msg);
            dout.flush();

            byte[] fileChunk = new byte[MAX_SIZE];
            int nbytes;
            while ((nbytes = din.read(fileChunk)) > 0) {
                fileOut.write(fileChunk, 0, nbytes);
            }

            System.out.println("Arquivo " + nomeArquivoCSV + " recebido com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao enviar/receber dados: " + e.getMessage());
        }
    }

    public void obterArquivoECSV(String serverAddress, int serverPort, String designacao, String nomeArquivoCSV) {
        try (Socket socketToServer = new Socket(serverAddress, serverPort);
             ObjectOutputStream dout = new ObjectOutputStream(socketToServer.getOutputStream());
             DataInputStream din = new DataInputStream(socketToServer.getInputStream());
             FileOutputStream fileOut = new FileOutputStream("src/Clientes/utils" + File.separator + nomeArquivoCSV)) {

            String msg = "CSVE " + designacao + " " + nomeArquivoCSV;
            dout.writeObject(msg);
            dout.flush();

            byte[] fileChunk = new byte[MAX_SIZE];
            int nbytes;
            while ((nbytes = din.read(fileChunk)) > 0) {
                fileOut.write(fileChunk, 0, nbytes);
            }

            System.out.println("Arquivo " + nomeArquivoCSV + " recebido com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao enviar/receber dados: " + e.getMessage());
        }
    }

}
