package org.example.Sokets.Client;


import org.example.Core.Models.Player;
import org.example.Core.Models.Team;
import org.example.Core.Repositories.Repository;
import org.example.Utils.Serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RemoteClientRepository implements Repository {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public RemoteClientRepository(String host, int port) throws IOException {
        socket = new Socket(host, port);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public int countTeams() {
        try {
            out.writeInt(0);
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countPlayers() {
        try {
            out.writeInt(1);
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTeam(Team team) {
        try {
            out.writeInt(2);
            String bytes = Serialization.toString(team);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePlayer(Player player) {
        try {
            out.writeInt(3);
            String bytes = Serialization.toString(player);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void movePlayertoTeam(int playerId, int teamId) {
        try {
            out.writeInt(4);
            out.writeInt(playerId);
            out.writeInt(teamId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertTeam(Team team) {
        try {
            out.writeInt(5);
            String bytes = Serialization.toString(team);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertPlayer(int teamId, Player player) {
        try {
            out.writeInt(6);
            out.writeInt(teamId);
            String bytes = Serialization.toString(player);
            out.writeInt(bytes.length());
            out.writeBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTeam(int id) {
        try {
            out.writeInt(7);
            out.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePlayer(int id) {
        try {
            out.writeInt(8);
            out.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Team getTeam(int id) {
        try {
            out.writeInt(9);
            out.writeInt(id);

            int size = in.readInt();
            byte[] bytes = new byte[size];
            in.readFully(bytes);

            return (Team) Serialization.fromBytes(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player getPlayer(int id) {
        try {
            out.writeInt(10);
            out.writeInt(id);

            int size = in.readInt();
            byte[] bytes = new byte[size];
            in.readFully(bytes);

            return (Player) Serialization.fromBytes(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Team> getTeams() {
        try {
            out.writeInt(11);

            int count = in.readInt();
            List<Team> teams = new ArrayList<>(count);

            for (int i = 0; i < count; ++i) {
                int size = in.readInt();
                byte[] bytes = new byte[size];
                in.readFully(bytes);
                teams.add((Team) Serialization.fromBytes(bytes));
            }

            return teams;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> getPlayers() {
        try {
            out.writeInt(12);

            int count = in.readInt();
            List<Player> players = new ArrayList<>(count);

            for (int i = 0; i < count; ++i) {
                int size = in.readInt();
                byte[] bytes = new byte[size];
                in.readFully(bytes);
                players.add((Player) Serialization.fromBytes(bytes));
            }

            return players;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}