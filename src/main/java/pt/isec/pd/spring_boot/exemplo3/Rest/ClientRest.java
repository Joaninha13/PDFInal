package pt.isec.pd.spring_boot.exemplo3.Rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class ClientRest {
    private static final String loginUri = "http://localhost:8080/login";
    private static String originalInput;
    static String regUri = "http://localhost:8080/user/reg";
    private static String token;
    static String geraCodeUri = "http://localhost:8080/eventos/gerarCodigo";

    static String addEventUri = "http://localhost:8080/eventos";

    private static boolean isAdmin = false;
    private static boolean isLoggedin = false;

    public static String sendRequestAndShowResponseWithJsonBody(String uri, String verb, String authorizationValue, String jsonBody) throws IOException {
        String responseBody = null;

        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod(verb);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");

            if(authorizationValue!=null) {
                connection.setRequestProperty("Authorization", authorizationValue);
            }

            // Enable output stream and write JSON body
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }


            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode + " (" + connection.getResponseMessage() + ")");

            InputStream errorStream = connection.getErrorStream();
            InputStream responseStream = (errorStream != null) ? errorStream : connection.getInputStream();

            try (Scanner scanner = new Scanner(responseStream).useDelimiter("\\A")) {
                responseBody = scanner.hasNext() ? scanner.next() : null;
            }
        } finally {
            connection.disconnect();
        }

        System.out.println(verb + " " + uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }

    public static String sendRequestAndShowResponse(String uri, String verb, String authorizationValue) throws MalformedURLException, IOException {

        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if(authorizationValue!=null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

        Scanner s;

        if(connection.getErrorStream()!=null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e){}

        connection.disconnect();

        System.out.println(verb + " " + uri + " -> " + responseBody);
        System.out.println();

        return responseBody;
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("Menu Principal");
            System.out.println("1. Registar");
            System.out.println("2. Login");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("Introduza o seu nome:");
                    String nome = scanner.nextLine();
                    System.out.println("Introduza o seu email:");
                    String email = scanner.nextLine();
                    System.out.println("Introduza a sua password:");
                    String password  = scanner.nextLine();

                    String jsonBody = "{\"nome\":\""+nome+"\",\"email\":\""+email+"\",\"password\":\""+password+"\"}";

                    sendRequestAndShowResponseWithJsonBody(regUri, "POST", "Bear ", jsonBody);

                    break;
                case 2:
                    System.out.println("Introduza o seu email:");
                    String mail = scanner.nextLine();
                    System.out.println("Introduza a sua password:");
                    String pass = scanner.nextLine();

                    originalInput = mail + ":" + pass;

                    String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

                    token = sendRequestAndShowResponse(loginUri, "POST","basic " + encodedString);

                    if (token == null)
                        System.out.println("Login inválido");

                    else if(mail.equals("admin") && pass.equals("admin")){
                        isAdmin = true;
                        isLoggedin = true;
                    }
                    else isLoggedin = token != null;
                    break;
                case 3:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
            }

            while(isLoggedin){
                if(!isAdmin){
                    System.out.println("1. Submeter código");
                    System.out.println("2. Consultar as presenças registadas");
                    System.out.println("3. Teste privilégios");
                    System.out.println("4. Sair");
                    System.out.println("Escolha uma opção: ");
                    int op = scanner.nextInt();
                    scanner.nextLine();
                    switch(op){
                        case 1:
                            System.out.println("Introduza o sub-codigo:");
                            String codigo = scanner.nextLine();

                            String subcode = "http://localhost:8080/user/subCode?code=" + codigo;

                            sendRequestAndShowResponse(subcode, "POST", "bearer " + token);
                            break;
                        case 2:
                            System.out.println("Introduza o data de inicio:");
                            String di2 = "di=" + scanner.nextLine();
                            System.out.println("Introduza o data de fim:");
                            String df2 = "df=" + scanner.nextLine();
                            System.out.println("Introduza o local:");
                            String loc2 = "loc=" + scanner.nextLine();
                            System.out.println("Introduza a descricao:");
                            String desc2 = "desc=" + scanner.nextLine();
                            String presence;

                            if(di2.equals("di=") && df2.equals("df=") && loc2.equals("loc=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user";
                            }
                            if(di2.equals("di=") && df2.equals("df=") && loc2.equals("loc=")){
                                presence = "http://localhost:8080/user?"+desc2;
                            }
                            else if(di2.equals("di=") && df2.equals("df=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+loc2;
                            }
                            else if(di2.equals("di=") && loc2.equals("loc=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+df2;
                            }
                            else if(df2.equals("df=") && loc2.equals("loc=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+di2;
                            }
                            else if(di2.equals("di=") && df2.equals("df=")){
                                presence = "http://localhost:8080/user?"+loc2+"&"+desc2;
                            }
                            else if(di2.equals("di=") && loc2.equals("loc=")){
                                presence = "http://localhost:8080/user?"+df2+"&"+desc2;
                            }
                            else if(di2.equals("di=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+df2+"&"+loc2;
                            }
                            else if(df2.equals("df=") && loc2.equals("loc=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+desc2;
                            }
                            else if(df2.equals("df=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+loc2;
                            }
                            else if(loc2.equals("loc=") && desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+df2;
                            }
                            else if(di2.equals("di=")){
                                presence = "http://localhost:8080/user?"+df2+"&"+loc2+"&"+desc2;
                            }
                            else if(df2.equals("df=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+loc2+"&"+desc2;
                            }
                            else if(loc2.equals("loc=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+df2+"&"+desc2;
                            }
                            else if(desc2.equals("desc=")){
                                presence = "http://localhost:8080/user?"+di2+"&"+df2+"&"+loc2;
                            }
                            else{
                                presence = "http://localhost:8080/user?"+di2+"&"+df2+"&"+loc2+"&"+desc2;
                            }

                            sendRequestAndShowResponse(presence, "GET", "bearer " + token);
                            break;
                        case 3:
                            String jsonBody2 = "{\"descricao\": \"Evento1\",\"tempoValidade\": \"20\"}";

                            sendRequestAndShowResponseWithJsonBody(geraCodeUri, "POST", "bearer " + token, jsonBody2);

                            break;
                        default:
                            isLoggedin = false;
                            break;
                    }
                }
                else{
                    System.out.println("1. Adicionar evento");
                    System.out.println("2. Eliminar evento");
                    System.out.println("3. Consultar eventos criados");
                    System.out.println("4. Gerar código");
                    System.out.println("5. Consultar presenças");
                    System.out.println("6. Sair");
                    System.out.println("Escolha uma opção: ");
                    int op = scanner.nextInt();
                    scanner.nextLine();
                    switch(op){
                        case 1:
                            System.out.print("Nome do evento: ");
                            String descricao = scanner.nextLine();
                            System.out.print("Local: ");
                            String local = scanner.nextLine();
                            System.out.print("Data (AAAA-MM-DD): ");
                            String data = scanner.nextLine();
                            System.out.print("Hora de início (HH:MM): ");
                            String horaInicio = scanner.nextLine();
                            System.out.print("Hora de fim (HH:MM): ");
                            String horaFim = scanner.nextLine();

                            String jsonBody3 = "{\"descricao\":\""+descricao+"\",\"local\":\""+local+"\",\"data\":\""+data+"\",\"horaInicio\":\""+horaInicio+"\",\"horaFim\":\""+horaFim+"\"}";

                            sendRequestAndShowResponseWithJsonBody(addEventUri, "POST", "bearer " + token, jsonBody3);

                            break;
                        case 2:
                            System.out.println("Introduza o nome do evento:");
                            String nomeEventoD = scanner.nextLine();

                            System.out.println(nomeEventoD);

                            String deleteUri = "http://localhost:8080/eventos/" + nomeEventoD;

                            System.out.println(deleteUri);

                            sendRequestAndShowResponse(deleteUri, "DELETE", "bearer " + token);

                            break;
                        case 3:
                            System.out.println("Introduza a data de inicio:");
                            String di = "di=" + scanner.nextLine();
                            System.out.println("Introduza a data de fim:");
                            String df = "df=" + scanner.nextLine();
                            System.out.println("Introduza o local:");
                            String loc = "loc=" + scanner.nextLine();
                            String event;

                            if(di.equals("di=") && df.equals("df=") && loc.equals("loc=")){
                                event = "http://localhost:8080/eventos";
                            }
                            else if(di.equals("di=") && df.equals("df=")){
                                event = "http://localhost:8080/eventos?"+loc;
                            }
                            else if(di.equals("di=") && loc.equals("loc=")){
                                event = "http://localhost:8080/eventos?"+df;
                            }
                            else if(df.equals("df=") && loc.equals("loc=")){
                                event = "http://localhost:8080/eventos?"+di;
                            }
                            else if(di.equals("di=")){
                                event = "http://localhost:8080/eventos?"+df+"&"+loc;
                            }
                            else if(df.equals("df=")){
                                event = "http://localhost:8080/eventos?"+di+"&"+loc;
                            }
                            else if(loc.equals("loc=")){
                                event = "http://localhost:8080/eventos?"+di+"&"+df;
                            }
                            else{
                                event = "http://localhost:8080/eventos?"+di+"&"+df+"&"+loc;
                            }

                            sendRequestAndShowResponse(event, "GET", "bearer " + token);
                            break;
                        case 4:
                            System.out.println("Introduza o nome do evento:");
                            String nomeEvento = scanner.nextLine();
                            System.out.println("Introduza o tempo em minutos:");
                            String tempo = scanner.nextLine();

                            String jsonBody2 = "{\"descricao\": \""+nomeEvento+"\",\"tempoValidade\": \""+tempo+"\"}";

                            sendRequestAndShowResponseWithJsonBody(geraCodeUri, "POST", "bearer " + token, jsonBody2);
                            break;
                        case 5:
                            System.out.println("Introduza o descricao do evento:");
                            String desc = scanner.nextLine();

                            String eventPresence = "http://localhost:8080/eventos/presencas?desc=" + desc;

                            sendRequestAndShowResponse(eventPresence, "GET", "bearer " + token);
                            break;
                        default:
                            isLoggedin = false;
                            isAdmin = false;
                            break;
                    }
                }
            }
        }
    }
}
