-- Path: bdScript.sql

CREATE TABLE IF NOT EXISTS Versao (numero_versao INTEGER NOT NULL);

CREATE TABLE IF NOT EXISTS Eventos (Designacao Varchar(100) PRIMARY KEY, Localidade Varchar(100), Data Date, Hora_Inicio Time, Hora_Fim Time);

CREATE TABLE IF NOT EXISTS Codigo_Registo (codigo Varchar(100),Tempo INTEGER,Evento_Designacao Varchar(100),FOREIGN KEY (Evento_Designacao) REFERENCES Evento (Designacao));

CREATE TABLE IF NOT EXISTS Utilizadores (Numero_Indentificacao INTEGER PRIMARY KEY, Nome Varchar(100), Email Varchar(100), Password Varchar(100));

CREATE TABLE IF NOT EXISTS Presencas (Evento_Designacao Varchar(100),Utilizador_ID INTEGER,FOREIGN KEY (Evento_Designacao) REFERENCES Evento (Designacao), FOREIGN KEY (Utilizador_ID) REFERENCES Utilizadores (Numero_Indentificacao));

-- Inserir um utilizador na tabela Utilizadores e inicializar a versão da base de dados
INSERT INTO Utilizadores (Numero_Indentificacao, Nome, Email, Password) VALUES ('1', 'admin', 'admin@isec.pt', 'admin');

INSERT INTO Versao (numero_versao) VALUES (0);

-- para teste

-- Inserir dados em múltiplas tabelas para teste

INSERT INTO Eventos (Designacao, Localidade, Data, Hora_Inicio, Hora_Fim) VALUES ('Evento1', 'LocalA', '2023-01-01', '10:00:00', '12:00:00'), ('Evento2', 'LocalB', '2023-02-01', '15:30:00', '17:30:00'), ('Evento3', 'LocalC', '2023-03-01', '18:00:00', '20:00:00'), ('Evento4', 'LocalD', '2023-04-01', '14:00:00', '16:00:00'), ('Evento5', 'LocalE', '2023-05-01', '17:00:00', '19:00:00');

INSERT INTO Utilizadores (Numero_Indentificacao, Nome, Email, Password) VALUES('2', 'User1', 'user1', 'user1'), ('3', 'User2', 'user2', 'user2'), ('4', 'User3', 'user3', 'user3'), ('5', 'User4', 'user4', 'user4');

INSERT INTO Presencas (Evento_Designacao, Utilizador_ID) VALUES('Evento1', '1'), ('Evento2', '1'), ('Evento2', '2'),('Evento3', '2'), ('Evento1', '3'), ('Evento4', '3'), ('Evento5', '4');

INSERT INTO Codigo_Registo (codigo, Tempo, Evento_Designacao) VALUES('0000', '60', 'Evento1'),('1111', '10', 'Evento2'),('3333', '3', 'Evento3'),('4444', '45', 'Evento4');