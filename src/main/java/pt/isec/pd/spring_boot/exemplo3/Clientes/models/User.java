package pt.isec.pd.spring_boot.exemplo3.Clientes.models;

public class User {
    private String nome;
    private String idNumber;
    private String email;
    private String password;

    public User(String nome, String idNumber, String email, String password) {
        this.nome = nome;
        this.idNumber = idNumber;
        this.email = email;
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
