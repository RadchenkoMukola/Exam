package org.example.RMI.Server;

import org.example.Core.Repositories.DBRepository;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            DBRepository repository = new DBRepository("jdbc:postgresql://localhost:1234/postgres", "postgres", "mypass");

            Registry registry = LocateRegistry.createRegistry(4097);
            registry.rebind("repository", repository);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
