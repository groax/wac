package org.groax.firstApp.domain;

import org.groax.firstApp.persistence.CountryPostgresDaoImpl;

import java.util.List;

public class WorldService {
    private CountryPostgresDaoImpl countryDao = new CountryPostgresDaoImpl();

    public WorldService() {

    }

    public List<Country> getAllCountries() {
        return countryDao.findAll();
    }

    public List<Country> get10LargestPopulations() {
        return countryDao.find10LargestPop();
    }

    public List<Country> get10LargestSurfaces() {
        return countryDao.find10LargestSurfaces();
    }

    public Country getCountryByCode(String code) {
        Country allCountries = countryDao.findByCode(code);
        return allCountries;
    }

    public Country deleteCountry(String code) {
        Country c = this.getCountryByCode(code);
        this.countryDao.delete(c);
        return c;
    }

    public int updateCountry(String code, String name, String capital, String region, int surface, int population) {
        Country c = this.getCountryByCode(code);
        int cc = 0;

        c.setName(name);
        c.setCapital(capital);
        c.setRegion(region);
        c.setSurface(surface);
        c.setPopulation(population);

        try {
            cc = this.countryDao.update(c);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("in update.");
        }

        return cc;
    }

    public Country createCountry(String code, String name, String capital, String region, int surface, int population) {
        Country c = new Country();

        c.setCode(code);
        c.setName(name);
        c.setCapital(capital);
        c.setRegion(region);
        c.setSurface(surface);
        c.setPopulation(population);

        try {
            return this.countryDao.save(c);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}