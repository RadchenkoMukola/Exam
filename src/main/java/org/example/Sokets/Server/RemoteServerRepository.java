package org.example.Sokets.Server;


import org.example.Core.Models.Date;
import org.example.Core.Models.Calendar;
import org.example.Core.Repositories.Repository;
import org.example.Utils.Serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

public class RemoteServerRepository implements Repository {
    private final ServerSocket serverSocket;

    private final DataInputStream in;
    private final DataOutputStream out;

    private final Repository repository;

    public RemoteServerRepository(Repository repository, int port) {
        try {
            serverSocket = new ServerSocket(8080);

            Socket socket = serverSocket.accept();

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.repository = repository;
    }

    @Override
    public int countTeams() throws RemoteException {
        return repository.countTeams();
    }

    @Override
    public int countPlayers() throws RemoteException {
        return repository.countPlayers();
    }

    @Override
    public void updateTeam(Team team) throws RemoteException {
        repository.updateTeam(team);
    }

    @Override
    public void updatePlayer(Player player) throws RemoteException {
        repository.updatePlayer(player);
    }

    @Override
    public void movePlayertoTeam(int playerId, int teamId) throws RemoteException {
        repository.movePlayertoTeam(playerId, teamId);
    }

    @Override
    public void insertTeam(Team team) throws RemoteException {
        repository.insertTeam(team);
    }

    @Override
    public void insertPlayer(int teamId, Player player) throws RemoteException {
        repository.insertPlayer(teamId, player);
    }

    @Override
    public void deleteTeam(int id) throws RemoteException {
        repository.deleteTeam(id);
    }

    @Override
    public void deletePlayer(int id) throws RemoteException {
        repository.deletePlayer(id);
    }

    @Override
    public Team getTeam(int id) throws RemoteException {
        return repository.getTeam(id);
    }

    @Override
    public Player getPlayer(int id) throws RemoteException {
        return repository.getPlayer(id);
    }

    @Override
    public List<Team> getTeams() throws RemoteException {
        return repository.getTeams();
    }

    @Override
    public List<Player> getPlayers() throws RemoteException {
        return repository.getPlayers();
    }

    void start() {
        try {
            while (true) {
                int operation = in.readInt();
                System.out.println("Received operation: " + operation);

                boolean isOk = switch (operation) {
                    case 0 -> {
                        out.writeInt(repository.countTeams());

                        yield true;
                    }
                    case 1 -> {
                        out.writeInt(repository.countPlayers());

                        yield true;
                    }
                    case 2 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.updateTeam((Team) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 3 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.updatePlayer((Player) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 4 -> {
                        int playerId = in.readInt();
                        int teamId = in.readInt();

                        repository.movePlayertoTeam(playerId, teamId);

                        yield true;
                    }
                    case 5 -> {
                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.insertTeam((Team) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 6 -> {
                        int teamId = in.readInt();

                        int size = in.readInt();
                        byte[] bytes = new byte[size];
                        in.readFully(bytes);

                        repository.insertPlayer(teamId, (Player) Serialization.fromBytes(bytes));

                        yield true;
                    }
                    case 7 -> {
                        int id = in.readInt();

                        repository.deleteTeam(id);

                        yield true;
                    }
                    case 8 -> {
                        int id = in.readInt();

                        repository.deletePlayer(id);

                        yield true;
                    }
                    case 9 -> {
                        int id = in.readInt();

                        Team team = repository.getTeam(id);
                        String bytes = Serialization.toString(team);
                        out.writeInt(bytes.length());
                        out.writeBytes(bytes);

                        yield true;
                    }
                    case 10 -> {
                        int id = in.readInt();

                        Player player = repository.getPlayer(id);
                        String bytes = Serialization.toString(player);
                        out.writeInt(bytes.length());
                        out.writeBytes(bytes);

                        yield true;
                    }
                    case 11 -> {
                        List<Team> teams = repository.getTeams();

                        out.writeInt(teams.size());

                        for (Team team: teams) {
                            String bytes = Serialization.toString(team);
                            out.writeInt(bytes.length());
                            out.writeBytes(bytes);
                        }

                        yield true;
                    }
                    case 12 -> {
                        List<Player> players = repository.getPlayers();

                        out.writeInt(players.size());

                        for (Player player: players) {
                            String bytes = Serialization.toString(player);
                            out.writeInt(bytes.length());
                            out.writeBytes(bytes);
                        }

                        yield true;
                    }
                    default -> {
                        System.out.println("Unsupported operation");
                        yield false;
                    }
                };

                if (!isOk) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
