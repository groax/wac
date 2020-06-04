package org.groax.firstApp.persistence;


import org.groax.firstApp.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserPostgresDaoImpl extends PostgresBaseDao implements UserDao {
    private ArrayList<User> executeQuery(String query, Object... params) {
        ArrayList<User> users = new ArrayList<User>();

        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i += 1) {
                ps.setObject(i+1, params[i]);
            }

            ResultSet dbResultSet = ps.executeQuery();

            while (dbResultSet.next()) {
                User u = new User(dbResultSet.getString("username"), dbResultSet.getString("password"), dbResultSet.getString("role"));
                users.add(u);
            }

            return users;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<User> findAll() {
        return this.executeQuery("SELECT * FROM useraccount");
    }

    @Override
    public User save(User user) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO useraccount (username, password, role) VALUES (?, ?, ?)");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public int update(User user) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE useraccount SET password = ?, role = ? where username = ?");

            ps.setString(1, user.getPassword());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getUsername());
            int dbResultSet = ps.executeUpdate();
            return dbResultSet;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public User delete(User user) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM useraccount WHERE username = ?");

            ps.setString(1, user.getUsername());

            ResultSet dbResultSet = ps.executeQuery();
            return user;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String findRoleForUser(String username, String password) {
        ArrayList<User> users = this.executeQuery("SELECT * FROM useraccount WHERE username = ? AND password = ?", username, password);


        if(users.size() < 1) {
            return null;
        }

        User u = users.get(0);

        return u.getRole();
    }
}
