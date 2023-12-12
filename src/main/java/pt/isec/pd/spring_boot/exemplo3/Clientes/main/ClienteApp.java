package pt.isec.pd.spring_boot.exemplo3.Clientes.main;

import java.io.IOException;
import java.util.Scanner;
import pt.isec.pd.spring_boot.exemplo3.Clientes.communication.ClientCommunication;
import pt.isec.pd.spring_boot.exemplo3.share.consultas.ConsultPresence;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;

public class ClienteApp {

    private static boolean isLoggedIn = false;
    private static boolean isAdmin = false;
    private static registo currentUser;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ClientCommunication communication;

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            communication = new ClientCommunication(serverAddress, serverPort);
            if(communication.isConnected()){
                System.out.println("Conexão bem sucedida");
            }
            else{
                System.out.println("Conexão falhada");
            }

            int escolha;
            do {
                System.out.println("\nBem-vindo! Escolha uma opção:");
                System.out.println("1. Registrar");
                System.out.println("2. Fazer Login");
                System.out.println("3. Sair");

                escolha = sc.nextInt();
                sc.nextLine();

                switch (escolha) {
                    case 1:
                        System.out.println("Introduza o seu nome:");
                        String nome = sc.nextLine();
                        System.out.println("Introduza o seu email:");
                        String email = sc.nextLine();
                        System.out.println("Introduza a sua password:");
                        String password = sc.nextLine();

                        registo userRegister = new registo(nome,email,password);
                        registo responseRegister = communication.registerUser(userRegister);

                        if (responseRegister.isRegistered()) {
                            System.out.println(responseRegister.getMsg());
                            isLoggedIn = true;
                            currentUser = responseRegister;
                        } else {
                            System.out.println("Erro no registo: " + responseRegister.getMsg());
                        }
                        break;

                    case 2:
                        System.out.println("Introduza o seu email:");
                        email = sc.nextLine();
                        System.out.println("Introduza a sua password:");
                        password = sc.nextLine();

                        registo responseLogin = communication.authenticateUser(email, password);

                        if (responseLogin.isValid()) {
                            System.out.println("Autenticação bem-sucedida!");
                            isLoggedIn = true;
                            isAdmin = responseLogin.isAdmin();
                            currentUser = responseLogin;
                        } else {
                            System.out.println("Erro de autenticação: " + responseLogin.getMsg());
                        }
                        break;

                    case 3:
                        System.out.println("A sair do programa...");
                        isLoggedIn = false;
                        break;

                    default:
                        System.out.println("Opção inválida. Escolha novamente.");
                }

                while (isLoggedIn) {
                    if (isAdmin) {
                        System.out.println("\nMenu de Administrador:");
                        System.out.println("1. Criar Evento");
                        System.out.println("2. Editar Evento");
                        System.out.println("3. Eliminar Evento");
                        System.out.println("4. Consultar Eventos");
                        System.out.println("5. Gerar código de registo da presença");
                        System.out.println("6. Consultar presenças registadas");
                        System.out.println("7. Obter ficheiro csv(.6)");
                        System.out.println("8. Consultar Eventos de um Utilizador");
                        System.out.println("9. Obter ficheiro csv(.8)");
                        System.out.println("10. Eliminar Presenças Registadas");
                        System.out.println("11. Inserção de Presenças");
                        System.out.println("12. Logout");
                    }
                    else{
                        System.out.println("\nMenu de Utilizador:");
                        System.out.println("1. Editar dados de Registo");
                        System.out.println("2. Submeter código de registo da presença");
                        System.out.println("3. Consultar presenças registadas");
                        System.out.println("4. Obter ficheiro csv");
                        System.out.println("9. Logout");
                    }

                    int opcao = sc.nextInt();
                    sc.nextLine();

                    if(isAdmin){
                        switch (opcao) {
                            case 1:
                                System.out.print("Nome do evento: ");
                                String nomeEvento = sc.nextLine();
                                System.out.print("Local: ");
                                String localEvento = sc.nextLine();
                                System.out.print("Data (AAAA-MM-DD): ");
                                String dataEvento = sc.nextLine();
                                System.out.print("Hora de início (HH:MM): ");
                                String horaInicio = sc.nextLine();
                                System.out.print("Hora de fim (HH:MM): ");
                                String horaFim = sc.nextLine();

                                String message = "create " + nomeEvento + " " + localEvento + " " +
                                        dataEvento + " " + horaInicio + " " + horaFim;

                                try {
                                    String response = communication.sendEventDetails(message);
                                    System.out.println("Resposta do servidor: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao enviar dados do evento: " + e.getMessage());
                                }
                                break;
                            case 2:
                                System.out.print("Nome do evento a alterar: ");
                                String alteraEvento = sc.nextLine();
                                System.out.print("Novo nome do evento (deixe em branco para não alterar): ");
                                String novoNomeEvento = sc.nextLine();
                                System.out.print("Novo local (deixe em branco para não alterar): ");
                                String novoLocalEvento = sc.nextLine();
                                System.out.print("Nova data (AAAA-MM-DD, deixe em branco para não alterar): ");
                                String novaDataEvento = sc.nextLine();
                                System.out.print("Nova hora de início (HH:MM, deixe em branco para não alterar): ");
                                String novaHoraInicio = sc.nextLine();
                                System.out.print("Nova hora de fim (HH:MM, deixe em branco para não alterar): ");
                                String novaHoraFim = sc.nextLine();

                                events updatedEvent = new events(novoNomeEvento, novoLocalEvento, novaDataEvento, novaHoraInicio, novaHoraFim);
                                updatedEvent.setMsg(alteraEvento);

                                try {
                                    String response = communication.updateEvent(updatedEvent);
                                    System.out.println("Resposta do servidor: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao editar dados do evento: " + e.getMessage());
                                }
                                break;
                            case 3:
                                System.out.print("Descrição do evento a eliminar: ");
                                String descricaoEvento = sc.nextLine();

                                try {
                                    String response = communication.deleteEvent(descricaoEvento);
                                    System.out.println("Resposta do servidor: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao eliminar evento: " + e.getMessage());
                                }
                                break;
                            case 4:
                                System.out.println("Consultar Eventos. Insira filtros se necessário:");
                                String filtro = sc.nextLine();

                                try {
                                    ConsultPresence consulta = communication.consultEvents(filtro);
                                    for (events e : consulta.getEvent()) {
                                        System.out.println(e.toString());
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao consultar eventos: " + e.getMessage());
                                }
                                break;
                            case 5:
                                System.out.print("Descrição do evento para o qual gerar o código: ");
                                String descEvento = sc.nextLine();
                                System.out.print("Tempo de validade do código (em minutos): ");
                                int tempoValidade = sc.nextInt();
                                sc.nextLine();

                                try {
                                    String response = communication.generatePresenceCode(descEvento, tempoValidade);
                                    System.out.println("Código de presença gerado: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao gerar código de presença: " + e.getMessage());
                                }
                                break;
                            case 6:
                                System.out.print("Insira o nome do evento para consultar as presenças: ");
                                String eventName = sc.nextLine();
                                try {
                                    ConsultPresence presence = communication.consultPresenceInEvent(eventName);
                                    for (registo reg : presence.getReg()) {
                                        System.out.println(reg.toString());
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao consultar presenças: " + e.getMessage());
                                }
                                break;
                            case 7:
                                System.out.print("Email do utilizador: ");
                                String userEmail = sc.nextLine();

                                System.out.print("Digite um filtro (ou deixe em branco): ");
                                filtro = sc.nextLine();

                                System.out.print("Digite o nome do arquivo CSV: ");
                                String nomeArquivoCSV = sc.nextLine();
                                communication.obterArquivoCSV(serverAddress, serverPort, userEmail, filtro, nomeArquivoCSV + ".csv");
                                break;
                            case 8:
                                System.out.print("Insira o email do utilizador para consultar os seus eventos: ");
                                userEmail = sc.nextLine();
                                try {
                                    ConsultPresence userEvents = communication.consultAttendance(userEmail, " ");
                                    for (events e : userEvents.getEvent()) {
                                        System.out.println(e.toString());
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao consultar eventos do utilizador: " + e.getMessage());
                                }
                                break;
                            case 9:
                                System.out.print("Designacao do evento: ");
                                String designacao = sc.nextLine();

                                System.out.print("Digite o nome do arquivo CSV: ");
                                nomeArquivoCSV = sc.nextLine();
                                communication.obterArquivoECSV(serverAddress, serverPort, designacao, nomeArquivoCSV + ".csv");
                                break;
                            case 10:
                                System.out.print("Insira o email do utilizador: ");
                                userEmail = sc.nextLine();
                                System.out.print("Insira a designação do evento: ");
                                eventName = sc.nextLine();
                                try {
                                    String response = communication.deleteRegisteredAttendance(userEmail, eventName);
                                    System.out.println("Resposta do servidor: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao eliminar presenças registadas: " + e.getMessage());
                                }
                                break;
                            case 11:
                                System.out.print("Insira o email do utilizador: ");
                                userEmail = sc.nextLine();
                                System.out.print("Insira a designação do evento: ");
                                eventName = sc.nextLine();
                                try {
                                    String response = communication.insertAttendance(userEmail, eventName);
                                    System.out.println("Resposta do servidor: " + response);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao inserir presença: " + e.getMessage());
                                }
                                break;
                            case 12:
                                isLoggedIn = false;
                                isAdmin = false;
                                currentUser = null;
                                System.out.println("Logout...");
                                break;
                            default:
                                System.out.println("Opção inválida. Escolha novamente.");
                        }
                    }
                    else{
                        switch (opcao) {
                            case 1:
                                System.out.println("Editar dados de registo. Deixe em branco se não deseja mudar o valor atual:");

                                System.out.println("Nome atual: " + currentUser.getName());
                                System.out.print("Novo nome: ");
                                String newName = sc.nextLine();
                                if (!newName.isEmpty()) {
                                    currentUser.setName(newName);

                                }

                                System.out.println("Email atual: " + currentUser.getEmail());
                                System.out.print("Novo email: ");
                                String newEmail = sc.nextLine();
                                if (!newEmail.isEmpty()) {
                                    currentUser.setEmail(newEmail);
                                }

                                System.out.println("Password atual: ********");
                                System.out.print("Nova password: ");
                                String newPassword = sc.nextLine();
                                if (!newPassword.isEmpty()) {
                                    currentUser.setPassword(newPassword);
                                }

                                try {
                                    registo responseEdit = communication.editUserData(currentUser);
                                    if (responseEdit.isRegistered()) {
                                        System.out.println("Dados atualizados com sucesso.");
                                        currentUser = responseEdit;
                                    } else {
                                        System.out.println("Erro na atualização dos dados: " + responseEdit.getMsg());
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao atualizar dados: " + e.getMessage());
                                }
                                break;

                            case 2:
                                System.out.println("Por favor, insira o código que deseja submeter:");
                                String codigo = sc.nextLine();
                                try {
                                    String responseSubmit = communication.submitCode(codigo, currentUser.getEmail());
                                    System.out.println("Resposta do servidor: " + responseSubmit);
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao submeter código: " + e.getMessage());
                                }
                                break;

                            case 3:
                                System.out.println("Consulta de presenças. Se quiser aplicar um filtro, introduza-o agora, caso contrário, deixe em branco:");
                                String filter = sc.nextLine();

                                try {
                                    ConsultPresence attendanceResponse = communication.consultAttendance(currentUser.getEmail(), filter);

                                    attendanceResponse.getReg().forEach(reg -> System.out.println(reg.toString()));
                                    attendanceResponse.getEvent().forEach(event -> System.out.println(event.toString()));
                                } catch (IOException | ClassNotFoundException e) {
                                    System.out.println("Erro ao consultar presenças: " + e.getMessage());
                                }
                                break;

                            case 4:
                                System.out.print("Digite um filtro (ou deixe em branco): ");
                                String filtro = sc.nextLine();

                                System.out.print("Digite o nome do arquivo CSV: ");
                                String nomeArquivoCSV = sc.nextLine();
                                communication.obterArquivoCSV(serverAddress, serverPort, currentUser.getEmail(), filtro, nomeArquivoCSV + ".csv");
                                break;
                            case 9:
                                isLoggedIn = false;
                                currentUser = null;
                                System.out.println("Logout...");
                                break;

                            default:
                                System.out.println("Opção inválida. Escolha novamente.");
                        }
                    }
                }
            } while (escolha != 3);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}