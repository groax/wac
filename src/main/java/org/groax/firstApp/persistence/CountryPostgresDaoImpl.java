package org.groax.firstApp.persistence;

import org.groax.firstApp.domain.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CountryPostgresDaoImpl extends PostgresBaseDao implements CountryDao {

    private ArrayList<Country> executeQuery(String query, Object... params) {
        ArrayList<Country> countries = new ArrayList<Country>();

        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i += 1) {
                ps.setObject(i+1, params[i]);
            }

            ResultSet dbResultSet = ps.executeQuery();

            while (dbResultSet.next()) {
                Country c = new Country(dbResultSet.getString("code"), dbResultSet.getString("iso3"), dbResultSet.getString("name"), dbResultSet.getString("capital"), dbResultSet.getString("continent"), dbResultSet.getString("region"), dbResultSet.getInt("surfacearea"), dbResultSet.getInt("population"), dbResultSet.getString("governmentform"), dbResultSet.getDouble("latitude"), dbResultSet.getDouble("longitude"));;
                countries.add(c);
            }

            return countries;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);

        }
    }

    public ArrayList<Country> find10LargestPop() {
        return this.executeQuery("SELECT * FROM country ORDER BY population DESC LIMIT 10");
    }

    public ArrayList<Country> find10LargestSurfaces() {
        return this.executeQuery("SELECT * FROM country ORDER BY surfacearea DESC LIMIT 10");
    }

    public Country findByCode(String code) {
        ArrayList<Country> countries = this.executeQuery("SELECT * FROM country WHERE code = ? order by name", code);

        return countries.get(0);
    }

    @Override
    public ArrayList<Country> findAll() {
        return executeQuery("SELECT * FROM public.country order by name");
    }

    @Override
    public Country save(Country country) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO country (code, name, capital, region, surfacearea, population) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, country.getCode());
            ps.setString(2, country.getName());
            ps.setString(3, country.getCapital());
            ps.setString(4, country.getRegion());
            ps.setDouble(5, country.getSurface());
            ps.setInt(6, country.getPopulation());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public int update(Country country) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE public.country SET name = ?, capital = ?, region = ?, surfacearea = ?, population = ? WHERE code = ?");

            ps.setString(1, country.getName());
            ps.setString(2, country.getCapital());
            ps.setString(3, country.getRegion());
            ps.setDouble(4, country.getSurface());
            ps.setInt(5, country.getPopulation());
            ps.setString(6, country.getCode());
            int dbResultSet = ps.executeUpdate();
            return dbResultSet;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Country delete(Country country) {
        try(Connection conn = super.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.country WHERE code = ?");

            ps.setString(1, country.getCode());

            ResultSet dbResultSet = ps.executeQuery();
            return country;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}