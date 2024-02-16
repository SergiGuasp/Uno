package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Card;
import model.Player;
import utils.Color;
import utils.Number;

public class DaoImpl implements Dao {
    
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/uno";
    private final String user = "root";
    private final String password = "";

    @Override
    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public int getLastIdCard(int playerId) throws SQLException {
        int lastId = 0;
        String query = "SELECT IFNULL(MAX(idCard), 0) + 1 AS lastId FROM card WHERE idPlayerfk = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lastId = resultSet.getInt("lastId");
                }
            }
        }
        return lastId;
    }

    @Override
    public Card getLastCard() throws SQLException {
        Card lastCard = null;
        String query = "SELECT * FROM card ORDER BY idCard DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int id = resultSet.getInt("idCard");
                String number = resultSet.getString("number");
                String color = resultSet.getString("color");
                int playerId = resultSet.getInt("idPlayerfk");
                lastCard = new Card(id, number, color, playerId);
            }
        }
        return lastCard;
    }

    @Override
    public Player getPlayer(String user, String pass) throws SQLException {
        Player player = null;
        String query = "SELECT * FROM player WHERE user = ? AND Password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            statement.setString(2, pass);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("idPlayer");
                    String name = resultSet.getString("name");
                    int games = resultSet.getInt("games");
                    int victories = resultSet.getInt("victories");
                    player = new Player(id, name, games, victories);
                }
            }
        }
        return player;
    }

    @Override
    public ArrayList<Card> getCards(int playerId) throws SQLException {
        ArrayList<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM card WHERE idPlayerfk = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idCard");
                    String number = resultSet.getString("number");
                    String color = resultSet.getString("color");
                    cards.add(new Card(id, number, color, playerId));
                }
            }
        }
        return cards;
    }

    @Override
    public Card getCard(int cardId) throws SQLException {
        Card card = null;
        String query = "SELECT * FROM card WHERE idCard = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String number = resultSet.getString("number");
                    String color = resultSet.getString("color");
                    int playerId = resultSet.getInt("idPlayerfk");
                    card = new Card(cardId, number, color, playerId);
                }
            }
        }
        return card;
    }

    @Override
    public void saveGame(Card card) throws SQLException {
        String query = "INSERT INTO game (number, color) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, card.getNumber());
            statement.setString(2, card.getColor());
            statement.executeUpdate();
        }
    }

    @Override
    public void saveCard(Card card) throws SQLException {
        String query = "INSERT INTO card (number, color, idPlayerfk) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, card.getNumber());
            statement.setString(2, card.getColor());
            statement.setInt(3, card.getPlayerId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteCard(Card card) throws SQLException {
        String query = "DELETE FROM game WHERE idGame = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, card.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void clearDeck(int playerId) throws SQLException {
        String query = "DELETE * FROM card WHERE idPlayerfk = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.executeUpdate();
        }
    }

    @Override
    public void addVictories(int playerId) throws SQLException {
        String query = "UPDATE player SET victories = victories + 1 WHERE idPlayer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.executeUpdate();
        }
    }

    @Override
    public void addGames(int playerId) throws SQLException {
        String query = "UPDATE player SET games = games + 1 WHERE idPlayer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.executeUpdate();
        }
    }
}
