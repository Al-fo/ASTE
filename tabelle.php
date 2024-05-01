CREATE TABLE Utente(
ID_Utente int NOT NULL AUTO_INCREMENT,
Nome char(50),
Cognome char(50),
Email char(128),
Password char(128),
Telefono char(13),
PRIMARY KEY (ID_Utente)
);

CREATE TABLE Articoli(
ID_Articoli int NOT NULL AUTO_INCREMENT,
Nome char(50),
Descrizione char(255),
RIF_Lotto int NOT NULL,
RIF_Utente int NOT NULL,
PRIMARY KEY (ID_Articoli),
FOREIGN KEY (RIF_Lotto) REFERENCES Lotto(ID_Lotto),
FOREIGN KEY (RIF_Utente) REFERENCES Utente(ID_Utente)
);

CREATE TABLE ArticoliCategoria(
RIF_Articoli int NOT NULL,
RIF_Categoria int NOT NULL,
CONSTRAINT PK_ArticoliCategoria PRIMARY KEY (RIF_Articoli,RIF_Categoria),
FOREIGN KEY (RIF_Lotto) REFERENCES Lotto(ID_Lotto),
FOREIGN KEY (RIF_Utente) REFERENCES Utente(ID_Utente)
);

CREATE TABLE Categoria(
ID_Categoria int NOT NULL AUTO_INCREMENT,
Nome char(50),
PRIMARY KEY (ID_Categoria)
);

CREATE TABLE Lotto(
ID_Lotto int NOT NULL AUTO_INCREMENT,
Nome char(50),
Ip_multicast char(15),
Valore_Iniziale int,
PRIMARY KEY (ID_Lotto)
)

CREATE TABLE Asta(
ID_Asta int NOT NULL AUTO_INCREMENT, 
Data_ora_inizio date,
Data_ora_fine date,
Durata int,
Terminata boolean,
RIF_Lotto int NOT NULL,
PRIMARY KEY (ID_Asta),
FOREIGN KEY (RIF_Lotto) REFERENCES Lotto(ID_Lotto)
);

CREATE TABLE Puntata(
ID_Puntata int NOT NULL AUTO_INCREMENT, 
Valore float,
Data_ora_effettuazione date,
RIF_Utente int NOT NULL,
RIF_Lotto int NOT NULL,
PRIMARY KEY (ID_Puntata),
FOREIGN KEY (RIF_Lotto) REFERENCES Lotto(ID_Lotto),
FOREIGN KEY (RIF_Utente) REFERENCES Utente(ID_Utente)
);