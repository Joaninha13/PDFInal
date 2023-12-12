package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.ConectionClientThread;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection.conectionBD;
import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.io.*;
import java.net.Socket;

public class conectionClientThread extends Thread{

    private static final int MAX_SIZE = 4000;

    private Socket toClientSocket, auxSocket;
    private conectionBD bd;

    public conectionClientThread(Socket toClientSocket, conectionBD bd){
        this.bd = bd;
        this.toClientSocket = toClientSocket;
        auxSocket = toClientSocket;
    }

    @Override
    public void run(){

        registo reg = null;
        events event = null;
        String msg, resp;

        try (ObjectOutputStream oout = new ObjectOutputStream(toClientSocket.getOutputStream());
             ObjectInputStream oin = new ObjectInputStream(toClientSocket.getInputStream())){

            Object obj = oin.readObject();

            if (obj instanceof registo) {

                reg = (registo) obj;

                if (reg.getMsg() != null && reg.getMsg().equalsIgnoreCase("edit")){
                    //editar dados do utilizador
                    oout.writeObject(bd.editCliente(reg));
                    oout.flush();
                }

                oout.writeObject(bd.registaCliente(reg));
                oout.flush();

            }
            else if (obj instanceof events) {

                event = (events) obj;

                oout.writeObject(bd.editEvento(event));
                oout.flush();

            }
            else if (obj instanceof String) {
                msg = (String) obj;

                //divisao da msg por espaços

                String[] parts = msg.split(" ");

                //verificar se o primeiro elemento é um comando

                if (parts[0].equals("login")){

                    oout.writeObject(bd.autenticaCliente(parts[1], parts[2]));
                    oout.flush();
                }
               else if (parts[0].equals("sub")){
                    //adicionar o utilizador a lista de presenças do evento

                    oout.writeObject(bd.registaPresenca(parts[1], parts[2]));
                    oout.flush();

                }
                else if (parts[0].equals("create")) {
                    //criar evento

                    oout.writeObject(bd.criaEvento(parts[1], parts[2], parts[3], parts[4], parts[5]));
                    oout.flush();

                }
                else if (parts[0].equals("delete")) {
                    //eliminar evento

                    oout.writeObject(bd.eliminaEvento(parts[1]));
                    oout.flush();

                }
                else if (parts[0].equals("checkin")) {
                    //adicionar o utilizador a lista de presenças do evento

                    oout.writeObject(bd.inserePresenca(parts[1], parts[2]));
                    oout.flush();

                }
                else if (parts[0].equals("checkout")) {
                    //remover o utilizador da lista de presenças do evento

                    oout.writeObject(bd.eliminaPresenca(parts[1], parts[2]));
                    oout.flush();

                }
                else if (parts[0].equals("gerar")) {
                    //gerar codigo

                    oout.writeObject(bd.geraCodigo(parts[1], parts[2]));
                    oout.flush();
                }
                else if (parts[0].equals("ConsultPresenca")) {
                    //consultar presenças de um utilizador

                    if (parts.length == 2)
                        oout.writeObject(bd.consultaPresencasUtilizador(parts[1], ""));
                    else
                        oout.writeObject(bd.consultaPresencasUtilizador(parts[1], parts[2]));

                    oout.flush();
                }

                else if (parts[0].equals("ConsultPesencaEvent")) {
                    //consultar presenças num determinado evento

                    oout.writeObject(bd.consultaPresencasEvento(parts[1]));
                    oout.flush();
                }

                else if (parts[0].equals("ConsultEvents")) {
                    //consultar eventos criados com filtro

                    if (parts.length == 1)
                        oout.writeObject(bd.consultaEventos(""));
                    else
                        oout.writeObject(bd.consultaEventos(parts[1]));

                    oout.flush();
                }

                else if (parts[0].equals("CSVE")){

                    escreverResultadosCSVEventos(bd.consultaPresencasEvento(parts[1]), parts[2]);

                    mandaFicheiro(parts[2]);

                }

                else if (parts[0].equals("CSVU")){

                    if (parts.length == 3) {
                        escreverResultadosCSVUtilizador(bd.consultaPresencasUtilizador(parts[1], ""), parts[2]);
                        mandaFicheiro(parts[2]);
                    }
                    else {
                        escreverResultadosCSVUtilizador(bd.consultaPresencasUtilizador(parts[1], parts[2]), parts[3]);
                        mandaFicheiro(parts[3]);
                    }

                }

            }

        } catch (ClassNotFoundException e) {
            System.out.println();
            System.out.println("Mensagem recebida de tipo inesperado!");
        } catch (IOException e) {
            System.out.println();
            System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
        } catch(Exception e){
            assert auxSocket != null;
            System.out.println("Problema na comunicacao com o cliente " +
                    auxSocket.getInetAddress().getHostAddress() + ":" +
                    auxSocket.getPort()+"\n\t" + e);
        }
    }

    private void mandaFicheiro(String nomeArquivo) {
        try (DataOutputStream dos = new DataOutputStream(toClientSocket.getOutputStream());
             FileInputStream fis = new FileInputStream(nomeArquivo)) {

            // Enviar o nome do arquivo ao cliente
            dos.writeUTF(nomeArquivo);

            // Enviar o conteúdo do arquivo ao cliente
            byte[] buffer = new byte[MAX_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println("Arquivo enviado com sucesso para o cliente.");

        } catch (IOException e) {
            System.err.println("Erro ao enviar arquivo para o cliente: " + e.getMessage());
        }
    }

    private void escreverResultadosCSVEventos(ConsultPresence resultados, String nomeArquivo) {
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            // Escrever cabeçalhos no arquivo CSV
            writer.append("Designacao").append(":").append(String.valueOf(resultados.getEvent().get(resultados.getEvent().size() - 1).getDescricao())).append("\n");
            writer.append("Local").append(":").append(String.valueOf(resultados.getEvent().get(resultados.getEvent().size() - 1).getLocal())).append("\n");
            writer.append("Data").append(":").append(String.valueOf(resultados.getEvent().get(resultados.getEvent().size() - 1).getData())).append("\n");
            writer.append("Hora Inicio").append(":").append(String.valueOf(resultados.getEvent().get(resultados.getEvent().size() - 1).getHoraIncio())).append("\n");
            writer.append("Hora Fim").append(":").append(String.valueOf(resultados.getEvent().get(resultados.getEvent().size() - 1).getHoraFim())).append("\n\n");

            writer.append("Nome; Numero de Identificacao; Email\n");
            for (registo rg : resultados.getReg())
                writer.append(String.valueOf(rg.getName())).append(";").append(String.valueOf(rg.getIdentificationNumber())).append(";").append(String.valueOf(rg.getEmail())).append("\n");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }

    private void escreverResultadosCSVUtilizador(ConsultPresence resultados, String nomeArquivo) {
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            // Escrever cabeçalhos no arquivo CSV
            writer.append("Nome; Numero Identificação; Email\n");
            writer.append(String.valueOf(resultados.getReg().get(resultados.getReg().size() - 1).getName())).append(";").append(String.valueOf(resultados.getReg().get(resultados.getReg().size() - 1).getIdentificationNumber())).append(";").append(String.valueOf(resultados.getReg().get(resultados.getReg().size() - 1).getEmail())).append("\n\n");

            writer.append("Designação; Local; Data; Hora Inicio\n");
            for (events ev : resultados.getEvent())
                writer.append(String.valueOf(ev.getDescricao())).append(";").append(String.valueOf(ev.getLocal())).append(";").append(String.valueOf(ev.getData())).append(";").append(String.valueOf(ev.getHoraIncio())).append("\n");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }catch (IndexOutOfBoundsException e){
            System.err.println("Erro: a lista está vazia!");
        }
    }
}
