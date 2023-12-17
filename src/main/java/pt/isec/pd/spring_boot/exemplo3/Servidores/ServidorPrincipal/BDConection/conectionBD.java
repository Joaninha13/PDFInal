package pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.BDConection;

import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat.DBUpdate;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.Cluster.HeartBeat.HeartBeat;
import pt.isec.pd.spring_boot.exemplo3.Servidores.ServidorPrincipal.utils.Time;
import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.heartBeatMsg.HeartBeatMess;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.io.*;
import java.sql.*;

public class conectionBD {
    private static final String NOME_BD = "dataBaseTP.db";
    private int IDUtilizador;
    private static String dbDir;
    private static volatile conectionBD instance = null;

    // URL de conexão com o banco de dados
    private static String URL_CONEXAO;
    private static Connection conn;

    private static final DBUpdate dbUpdate = DBUpdate.getInstance();


    public static conectionBD getInstance() {
        if (instance == null) {
            synchronized(conectionBD.class) {
                if (instance == null) {
                    instance = new conectionBD(dbDir);
                }
            }
        }
        return instance;
    }

    // Start/create functions
    public conectionBD(String dbDir) {
        //connect a BD

        URL_CONEXAO = "jdbc:sqlite:" + dbDir + "/" + NOME_BD;

        criarBD();

        try {
            conn = DriverManager.getConnection(URL_CONEXAO);
            System.out.println("Connection to SQLite has been established.");
            setIDUtilizador(getLastIDUtilizador() + 1);
            instance = this;
            new Time().start();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // testar -> OK
    public static void criarBD() {
        try {
            Connection conexao = DriverManager.getConnection(URL_CONEXAO);
            Statement statement = conexao.createStatement();

            // Lê o script SQL do arquivo "bdScript.sql" e executa-o
            StringBuilder scriptSQL = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("bdScript.sql"))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    scriptSQL.append(linha).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo de script SQL: " + e.getMessage());
                return;
            }

            statement.executeUpdate(scriptSQL.toString());

            statement.close();
            conexao.close();

            System.out.println("Base de dados criada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar a base de dados: " + e.getMessage());
        }
    } // testar -> OK


    //private functions
    private int getID(String email){
        try {
            String selectQuery = "SELECT * FROM Utilizadores WHERE Email = '" + email + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            int id = rs.getInt("Numero_Indentificacao");

            // Fechar recursos
            rs.close();
            stmt.close();

            return id;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar id, utilizador inexistente: " + e.getMessage());
            return -1;
        }
    }

    private boolean existEvento(String designacaoEvent){
        try {
            String selectQuery = "SELECT * FROM Eventos WHERE Designacao = '" + designacaoEvent + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();
            stmt.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se evento existe: " + e.getMessage());
            return false;
        }
    }

    private String getDesignacaoEvent(String codigo) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Codigo_Registo WHERE Codigo = '" + codigo + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            String designacao = rs.getString("Evento_Designacao");

            // Fechar recursos
            rs.close();

            return designacao;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar designacao do evento: " + e.getMessage());
            return null;
        }
    }

    private boolean existPresencasUtilizador(String email, String codigo) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Presencas WHERE Evento_Designacao = '" + getDesignacaoEvent(codigo) + "' AND Utilizador_ID = '" + getID(email) + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se presenca existe: " + e.getMessage());
            return false;
        }
    }

    private boolean existPresencas(String email, String designacaoEvent) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Presencas WHERE Evento_Designacao = '" + designacaoEvent + "' OR Utilizador_ID = '" + getID(email) + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se presenca existe: " + e.getMessage());
            return false;
        }
    }

    private boolean existPresencasEvent(String designacaoEvent) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Presencas WHERE Evento_Designacao = '" + designacaoEvent + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se presenca existe: " + e.getMessage());
            return false;
        }
    }

    private boolean verificaCodigo(String codigo) {

        //verificar se existe codigo e se o tempo de limite ainda esta dentro do tempo limite

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Codigo_Registo WHERE Codigo = '" + codigo + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            if(existe && rs.getInt("Tempo") == 0)
                return false;

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se codigo existe: " + e.getMessage());
            return false;
        }

    }

    private boolean verificaCodigoEvent(String designacaoEvent){
        //verificar se existe um codigo ja associado a um evento

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Codigo_Registo WHERE Evento_Designacao = '" + designacaoEvent + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se codigo existe: " + e.getMessage());
            return false;
        }
    }

    private synchronized String updateCodigo(String designacaoEvent, String tempoLimite, int codigo){

        try(Statement stmt = conn.createStatement()) {
            String updateQuery = "UPDATE Codigo_Registo SET codigo = '" + codigo + "', Tempo = '" + tempoLimite + "' WHERE Evento_Designacao = '" + designacaoEvent + "'";
            stmt.executeUpdate(updateQuery);

            return "Codigo editado com sucesso -> " + codigo;

        } catch (SQLException e) {
            System.err.println("Erro ao editar codigo: " + e.getMessage());
            return "Erro ao editar codigo";
        }

    } // testar ->

    private boolean verificaEmail(String email) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Utilizadores WHERE Email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            boolean existe = rs.next();

            // Fechar recursos
            rs.close();

            return existe;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se email existe: " + e.getMessage());
            return false;
        }
    }

    private events getEvento(String designacaoEvent) {

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Eventos WHERE Designacao = '" + designacaoEvent + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            events event = new events(rs.getString("Designacao"), rs.getString("Localidade"), rs.getString("Data"), rs.getString("Hora_Inicio"), rs.getString("Hora_Fim"));

            // Fechar recursos
            rs.close();

            return event;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se evento existe: " + e.getMessage());
            return null;
        }
    }


    //Variavel statica
    private int getIDUtilizador() {return IDUtilizador;}
    private void incrementIDUtilizador() {IDUtilizador++;}
    private void setIDUtilizador(int ID) {IDUtilizador = ID;}
    private int getLastIDUtilizador(){

        try(Statement stmt = conn.createStatement()){

            String selectQuery = "SELECT Numero_Indentificacao FROM Utilizadores ORDER BY Numero_Indentificacao DESC LIMIT 1";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            int id = rs.getInt("Numero_Indentificacao");

            // Fechar recursos
            rs.close();

            return id;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar id, utilizador inexistente: " + e.getMessage());
            return -1;

        }

    }   // vai a base de dados buscar o ultimo utilizador registado e retorna o seu id


    //Utilizadores

    public registo autenticaCliente(String email, String password){
        registo reg = new registo(null, email, password);

        try(Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Utilizadores WHERE Email = '" + email + "' AND Password = '" + password + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            reg.setName(rs.getString("Nome"));
            reg.setIdentificationNumber(rs.getInt("Numero_Indentificacao"));

            if(reg.getEmail().equals("admin@isec.pt")){
                reg.setAdmin(true);
            }

            reg.setValid(true);
            reg.setMsg("Bem vindo");

            return reg;
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar utilizador: " + e.getMessage());
            return null;
        }
    } // testar - > FEITO

    public synchronized registo registaCliente(registo reg){

        if (reg.getEmail().isEmpty() || reg.getPassword().isEmpty()) {
            reg.setRegistered(false);
            reg.setMsg("Email ou Password nao podem ser Null");
            return null;
        }

        if (verificaEmail(reg.getEmail())) {
            reg.setRegistered(false);
            reg.setMsg("Email ja se encontra registrado");
            return null;
        }

        try(Statement stmt = conn.createStatement()) {
            String insertQuery = "INSERT INTO Utilizadores (Numero_Indentificacao,Nome, Email, Password) VALUES ('" + getIDUtilizador() + "','" + reg.getName() + "', '" + reg.getEmail() + "', '" + reg.getPassword() + "')";
            stmt.executeUpdate(insertQuery);

            reg.setIdentificationNumber(getIDUtilizador());
            reg.setRegistered(true);
            reg.setMsg("Registo efetuado com sucesso");
            incrementIDUtilizador();

            updateVersion();
            return reg;

        } catch (SQLException e) {
            System.err.println("Erro ao registar utilizador: " + e.getMessage());
            reg.setRegistered(false);
            return null;
        }
    } //testar ->

    public synchronized registo editCliente(registo editReg){

        // editar dados do utilizador

        try(Statement stmt = conn.createStatement()){
            String updateQuery = "UPDATE Utilizadores SET Nome = '" + editReg.getName() + "', Email = '" + editReg.getEmail() + "', Password = '" + editReg.getPassword() + "' WHERE Numero_Indentificacao = '" + editReg.getIdentificationNumber() + "'";
            stmt.executeUpdate(updateQuery);

            updateVersion();
            return editReg;

        } catch (SQLException e) {
            System.err.println("Erro ao editar utilizador: " + e.getMessage());
            return null;
        }
    } // testar ->

    private registo getClient(String email){
        try(Statement stmt = conn.createStatement()){
            String selectQuery = "SELECT * FROM Utilizadores WHERE Email = '" + email + "'";
            ResultSet rs = stmt.executeQuery(selectQuery);

            registo reg = new registo(rs.getString("Nome"), rs.getString("Email"), rs.getString("Password"));
            reg.setIdentificationNumber(rs.getInt("Numero_Indentificacao"));

            return reg;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar utilizador: " + e.getMessage());
            return null;
        }
    }

    //Eventos

    public synchronized String criaEvento(String designacaoEvent, String local, String data, String horaInicio, String horaFim) {

        //testar primeiro mas em principio mudar a data e as horas para o seu tipo de dados (data, Time)

        try(Statement stmt = conn.createStatement()) {
            if (existEvento(designacaoEvent))
                return "Evento ja existe";

            String insertQuery = "INSERT INTO Eventos (Designacao,Localidade, Data, Hora_Inicio, Hora_Fim) VALUES ('" + designacaoEvent + "', '" + local + "', '" + data + "', '" + horaInicio + "', '" + horaFim + "')";
            stmt.executeUpdate(insertQuery);

            updateVersion();
            return "Evento criado com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao registar evento: " + e.getMessage());
            return "Erro ao criar um evento";
        }

    } // testar -> FEITO

    public synchronized String editEvento(events editEvent){
        // editar dados do evento se nao tiver presenças registadas

        if (editEvent.getMsg() == null || editEvent.getMsg().equals(""))
            return "Nome do Evento a alterar nao pode ser Null";

        if (!existEvento(editEvent.getMsg()))
            return "Evento não existe";

        if (existPresencasEvent(editEvent.getMsg()))
            return "Evento ja tem presencas registadas";

        events auxEvent = getEvento(editEvent.getMsg());

        if (editEvent.getDescricao().isEmpty())
            editEvent.setDescricao(auxEvent.getDescricao());
        if (editEvent.getLocal().isEmpty())
            editEvent.setLocal(auxEvent.getLocal());
        if (editEvent.getData().isEmpty())
            editEvent.setData(auxEvent.getData());
        if (editEvent.getHoraIncio().isEmpty())
            editEvent.setHoraIncio(auxEvent.getHoraIncio());
        if (editEvent.getHoraFim().isEmpty())
            editEvent.setHoraFim(auxEvent.getHoraFim());

        try(Statement stmt = conn.createStatement()) {
            String updateQuery = "UPDATE Eventos SET Designacao = '" + editEvent.getDescricao() + "', Localidade = '" + editEvent.getLocal() + "', Data = '" + editEvent.getData() + "', Hora_Inicio = '" + editEvent.getHoraIncio() + "', Hora_Fim = '" + editEvent.getHoraFim() + "' WHERE Designacao = '" + editEvent.getMsg() + "'";
            stmt.executeUpdate(updateQuery);

            updateVersion();
            return "Evento editado com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao editar evento: " + e.getMessage());
            return "Erro ao editar evento";
        }
    } // testar ->

    public synchronized String eliminaEvento(String designacaoEvent){
        // eliminar um evento desde que nao tenha presenças associadas

        if (!existEvento(designacaoEvent))
            return "Evento não existe";
        else if (existPresencasEvent(designacaoEvent))
            return "Event ja tem presencas associadas";

        try(Statement stmt = conn.createStatement()) {

            String deleteQuery = "DELETE FROM Eventos WHERE Designacao = '" + designacaoEvent + "'";
            stmt.executeUpdate(deleteQuery);

            updateVersion();
            return "Evento eliminado com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao eliminar evento: " + e.getMessage());
            return "Erro ao eliminar evento";
        }

    } // testar -> FEITO

    public synchronized String eliminaPresenca(String email, String designacaoEvent){

        if (!existEvento(designacaoEvent))
            return "Evento nao existe";

        if (!existPresencas(email, designacaoEvent))
            return "Utilizador nao tem presenca registada neste evento";

        try(Statement stmt = conn.createStatement()){
            String deleteQuery = "DELETE FROM Presencas WHERE Evento_Designacao = '" + designacaoEvent + "' AND Utilizador_ID = '" + getID(email) + "'";
            stmt.executeUpdate(deleteQuery);

            updateVersion();
            return "Presenca eliminada com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao eliminar presenca: " + e.getMessage());
            return "Erro ao eliminar presenca";
        }

    } // testar ->

    public synchronized String inserePresenca(String email, String designacaoEvent){

            if (!existEvento(designacaoEvent))
                return "Evento nao existe";

            if (existPresencas(email, designacaoEvent))
                return "Presenca ja registada";

            try(Statement stmt = conn.createStatement()) {

            String insertQuery = "INSERT INTO Presencas (Evento_Designacao,Utilizador_ID) VALUES ('" + designacaoEvent + "','" + getID(email) + "')";
            stmt.executeUpdate(insertQuery);

            updateVersion();
            return "Presenca registada com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao registar presenca: " + e.getMessage());
            return "Erro ao registar presenca";
        }
    } // testar ->

    public synchronized String registaPresenca(String codigo, String email) {
        //verificar se codigo existe e se ainda esta dentro do tempo limite

        if (!verificaCodigo(codigo))
            return "Codigo nao existe ou expirou";

        if (existPresencasUtilizador(email, codigo))
            return "Voce ja tem presença registada neste evento";

        try(Statement stmt = conn.createStatement()) {

            String insertQuery = "INSERT INTO Presencas (Evento_Designacao,Utilizador_ID) VALUES ('" + getDesignacaoEvent(codigo) + "','" + getID(email) + "')";
            stmt.executeUpdate(insertQuery);

            updateVersion();
            return "Presenca registada com sucesso";

        } catch (SQLException e) {
            System.err.println("Erro ao registar presenca: " + e.getMessage());
            return "Erro ao registar presenca";
        }

    } // testar ->


    //Consultas
    public ConsultPresence consultaPresencasUtilizador(String email, String filtro){
        ConsultPresence consulta = new ConsultPresence();

        consulta.getReg().add(getClient(email));

        try (Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT P.Evento_Designacao, E.Localidade, E.Data, E.Hora_Inicio, E.Hora_Fim " +
                    "FROM Presencas P " +
                    "JOIN Eventos E ON P.Evento_Designacao = E.Designacao " +
                    "JOIN Utilizadores U ON P.Utilizador_ID = U.Numero_Indentificacao " +
                    "WHERE U.Email = '" + email + "' AND (E.Designacao LIKE '%" + filtro + "%' OR E.Data LIKE '%" + filtro + "%' OR E.Localidade LIKE '%" + filtro + "%' OR E.Hora_Inicio LIKE '%" + filtro + "%' OR E.Hora_Fim LIKE '%" + filtro + "%')";
            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                while (rs.next()) {
                    // Aqui você pode processar os resultados, por exemplo, imprimindo no console
                    System.out.println("Evento: " + rs.getString("Evento_Designacao"));
                    System.out.println("Localidade: " + rs.getString("Localidade"));
                    System.out.println("Data: " + rs.getString("Data"));
                    System.out.println("Hora Início: " + rs.getString("Hora_Inicio"));
                    System.out.println("Hora Fim: " + rs.getString("Hora_Fim"));
                    System.out.println("-----------------------");

                    consulta.getEvent().add(new events(rs.getString("Evento_Designacao"), rs.getString("Localidade"), rs.getString("Data"), rs.getString("Hora_Inicio"), rs.getString("Hora_Fim")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar presenças do utilizador: " + e.getMessage());
            return null;
        }
        return consulta;
    } // testar ->

    public ConsultPresence consultPresencesUtilizador(String email){

        ConsultPresence consulta = new ConsultPresence();

        try (Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT P.Evento_Designacao, E.Localidade, E.Data, E.Hora_Inicio, E.Hora_Fim " +
                    "FROM Presencas P " +
                    "JOIN Eventos E ON P.Evento_Designacao = E.Designacao " +
                    "JOIN Utilizadores U ON P.Utilizador_ID = U.Numero_Indentificacao " +
                    "WHERE U.Email = '" + email + "'";

            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                while (rs.next()) {
                    // Aqui você pode processar os resultados, por exemplo, imprimindo no console
                    System.out.println("Evento: " + rs.getString("Evento_Designacao"));
                    System.out.println("Localidade: " + rs.getString("Localidade"));
                    System.out.println("Data: " + rs.getString("Data"));
                    System.out.println("Hora Início: " + rs.getString("Hora_Inicio"));
                    System.out.println("Hora Fim: " + rs.getString("Hora_Fim"));
                    System.out.println("-----------------------");

                    consulta.getEvent().add(new events(rs.getString("Evento_Designacao"), rs.getString("Localidade"), rs.getString("Data"), rs.getString("Hora_Inicio"), rs.getString("Hora_Fim")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar presenças do utilizador: " + e.getMessage());
            return null;
        }
        return consulta;
    } // testar ->

    //Consulta de presencas de um evento
    public ConsultPresence consultaPresencasEvento(String designacaoEvent){
        ConsultPresence consulta = new ConsultPresence();

        consulta.getEvent().add(getEvento(designacaoEvent));

        try (Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT U.Nome, U.Email, U.Numero_Indentificacao " +
                    "FROM Presencas P " +
                    "JOIN Eventos E ON P.Evento_Designacao = E.Designacao " +
                    "JOIN Utilizadores U ON P.Utilizador_ID = U.Numero_Indentificacao " +
                    "WHERE E.Designacao = '" + designacaoEvent + "'";

            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                while (rs.next()) {
                    // Aqui você pode processar os resultados, por exemplo, imprimindo no console
                     System.out.println("Nome: " + rs.getString("Nome"));
                     System.out.println("Email: " + rs.getString("Email"));
                     System.out.println("Numero de Identificacao: " + rs.getInt("Numero_Indentificacao"));

                    consulta.getReg().add(new registo(rs.getString("Nome"), rs.getString("Email"), null));
                    consulta.getReg().get(consulta.getReg().size() - 1).setIdentificationNumber(rs.getInt("Numero_Indentificacao"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar presenças do evento: " + e.getMessage());
            return null;
        }
        return consulta;
    } // testar ->

    //consulta de eventos criados
    public ConsultPresence consultaEventos(String filtro){

        ConsultPresence consulta = new ConsultPresence();

        try (Statement stmt = conn.createStatement()) {
            String selectQuery = "SELECT * FROM Eventos WHERE Designacao LIKE '%" + filtro + "%' OR Localidade LIKE '%" + filtro + "%' OR Data LIKE '%" + filtro + "%' OR Hora_Inicio LIKE '%" + filtro + "%' OR Hora_Fim LIKE '%" + filtro + "%'";

            try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                while (rs.next()) {
                    // Aqui você pode processar os resultados, por exemplo, imprimindo no console
                    System.out.println("Evento: " + rs.getString("Designacao"));
                    System.out.println("Localidade: " + rs.getString("Localidade"));
                    System.out.println("Data: " + rs.getString("Data"));
                    System.out.println("Hora Início: " + rs.getString("Hora_Inicio"));
                    System.out.println("Hora Fim: " + rs.getString("Hora_Fim"));
                    System.out.println("-----------------------");

                    consulta.getEvent().add(new events(rs.getString("Designacao"), rs.getString("Localidade"), rs.getString("Data"), rs.getString("Hora_Inicio"), rs.getString("Hora_Fim")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar eventos: " + e.getMessage());
            return null;
        }

        return consulta;
    } // testar ->

    public ConsultPresence consultaEventos() {

            ConsultPresence consulta = new ConsultPresence();

            try (Statement stmt = conn.createStatement()) {
                String selectQuery = "SELECT * FROM Eventos";

                try (ResultSet rs = stmt.executeQuery(selectQuery)) {
                    while (rs.next()) {
                        // Aqui você pode processar os resultados, por exemplo, imprimindo no console
                        System.out.println("Evento: " + rs.getString("Designacao"));
                        System.out.println("Localidade: " + rs.getString("Localidade"));
                        System.out.println("Data: " + rs.getString("Data"));
                        System.out.println("Hora Início: " + rs.getString("Hora_Inicio"));
                        System.out.println("Hora Fim: " + rs.getString("Hora_Fim"));
                        System.out.println("-----------------------");

                        consulta.getEvent().add(new events(rs.getString("Designacao"), rs.getString("Localidade"), rs.getString("Data"), rs.getString("Hora_Inicio"), rs.getString("Hora_Fim")));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao consultar eventos: " + e.getMessage());
                return null;
            }

        return consulta;
    }



    //Codigos
    public synchronized String geraCodigo(String designacaoEvent, String tempoLimite){

        if (!existEvento(designacaoEvent))
            return "Evento nao existe";

        int codigo = (int)(Math.random() * 100000);

        if (verificaCodigoEvent(designacaoEvent))
            return updateCodigo(designacaoEvent, tempoLimite, codigo);

        try(Statement stmt = conn.createStatement()) {
            String insertQuery = "INSERT INTO Codigo_Registo (codigo,Tempo,Evento_Designacao) VALUES ('" + codigo + "','" + tempoLimite + "', '" + designacaoEvent + "')";
            stmt.executeUpdate(insertQuery);

            return "Codigo gerado com sucesso -> " + codigo;

        } catch (SQLException e) {
            System.err.println("Erro ao gerar codigo: " + e.getMessage());
            return "Erro ao gerar codigo";
        }

    } // testar ->

    public synchronized void updateTimes() {

        try (Statement stmt = conn.createStatement()) {
            String updateQuery = "UPDATE Codigo_Registo SET Tempo = Tempo - 1";
            stmt.executeUpdate(updateQuery);

            String deleteQuery = "DELETE FROM Codigo_Registo WHERE Tempo = -1";
            stmt.executeUpdate(deleteQuery);

        } catch (SQLException e) {
            System.err.println("Erro ao decrementar o tempo na tabela Codigo_Registo: " + e.getMessage());
        }
    } // testar ->

    //Versao

    public static int getVersion() {

        try(Statement stmt = conn.createStatement()){

            String selectQuery = "SELECT * FROM Versao";
            ResultSet rs = stmt.executeQuery(selectQuery);

            // Verificar se há algum resultado
            int versao = rs.getInt("numero_versao");

            // Fechar recursos
            rs.close();

            return versao;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar Numero da versao : " + e.getMessage());
            return -1;

        }


    } // testar ->

    public void updateVersion() {

        int newversion = getVersion() + 1;

        try (Statement stmt = conn.createStatement()) {

            String updateQuery = "UPDATE Versao SET numero_versao = '" + newversion + "'";
            stmt.executeUpdate(updateQuery);

            dbUpdate.send(newversion);

        } catch (SQLException e) {
            System.err.println("Erro ao dar Update do numero da versao : " + e.getMessage());
        }
    } // testar ->

}
