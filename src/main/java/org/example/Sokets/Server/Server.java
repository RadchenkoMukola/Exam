package org.example.Sokets.Server;

import org.example.Core.Repositories.DBRepository;
import org.example.Core.Repositories.Repository;

import java.rmi.RemoteException;

public class Server {
    public static void main(String[] args) throws RemoteException {
        Repository innerRepository = new DBRepository("jdbc:postgresql://localhost:1234/postgres", "postgres", "mypass");
        RemoteServerRepository repository = new RemoteServerRepository(innerRepository, 8080);
        repository.start();
    }
}