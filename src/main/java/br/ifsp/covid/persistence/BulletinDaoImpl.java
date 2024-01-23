package br.ifsp.covid.persistence;

import br.ifsp.covid.model.Bulletin;
import br.ifsp.covid.model.State;
import br.ifsp.covid.view.BulletinApp;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BulletinDaoImpl implements BulletinDao{
    @Override
    public boolean insert(Bulletin bulletin) throws DuplicatedBulletinException {
        final String checkSql = "SELECT * FROM bulletin WHERE city = ? AND date = ?";
        try {
            PreparedStatement checkStmt = ConnectionFactory.createPreparedStatement(checkSql);
            checkStmt.setString(1, bulletin.getCity());
            checkStmt.setString(2, String.valueOf(Date.valueOf(bulletin.getDate())));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                throw new DuplicatedBulletinException("There is already a bulletin for this date and city.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final String sql = "INSERT INTO bulletin (city, state, infected, deaths, icu_ratio, date) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            final PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql);
            stmt.setString(1, bulletin.getCity());
            stmt.setString(2, bulletin.getState().toString());
            stmt.setInt(3, bulletin.getInfected());
            stmt.setInt(4, bulletin.getDeaths());
            stmt.setDouble(5, bulletin.getIcuRatio());
            stmt.setString(6, String.valueOf(Date.valueOf(bulletin.getDate())));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Bulletin bulletin) {
        final String sql = "DELETE FROM bulletin WHERE id = ?";
        try {
            final PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql);
            stmt.setInt(1, bulletin.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Bulletin bulletin) {
        final String sql = "UPDATE bulletin SET city = ?, date = ?, infected = ?, deaths = ?, icu_ratio = ?, state = ? WHERE id = ?";
        try {
            final PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql);

            stmt.setString(1, bulletin.getCity());
            stmt.setString(2, String.valueOf(Date.valueOf(bulletin.getDate())));
            stmt.setInt(3, bulletin.getInfected());
            stmt.setInt(4, bulletin.getDeaths());
            stmt.setDouble(5, bulletin.getIcuRatio());
            stmt.setString(6, bulletin.getState().toString());

            stmt.setInt(7, bulletin.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Bulletin> findAll() {
        final String sql = "SELECT * FROM bulletin";
        List<Bulletin> bulletins = new ArrayList<>();
        try (PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String city = rs.getString("city");
                int infected = rs.getInt("infected");
                State state = State.fromName(rs.getString("state"));
                int deaths = rs.getInt("deaths");
                double icuratio = rs.getDouble("icu_ratio");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                bulletins.add(new Bulletin(id, city, state, infected, deaths, icuratio, date));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bulletins.isEmpty() ? Collections.emptyList() : bulletins;
    }
}
