/*----------------------------------------------------------------------------*/
/* Source File:   DEFAULTCOUNTRYDATA.JAVA                                     */
/* Copyright (c), 2025 CSoftZ                                                 */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Apr.20/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.country.data;

import com.acme.country.domain.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Implementation of the CountryData interface using JdbcTemplate for data access.
 * <p>
 * Provides methods to perform CRUD operations (Create, Read, Update, Delete) on
 * country data, interacting with a relational database.
 * <p>
 * This class uses SQL queries to perform operations such as retrieving all
 * countries, finding a specific country by its identifier, saving or updating
 * a country, and deleting a country by its identifier.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
public class DefaultCountryData implements CountryData {
    private final JdbcTemplate jdbcTemplate;

    public DefaultCountryData(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Country> findAll() {
        return jdbcTemplate.query("SELECT * FROM country", new CountryRowMapper());
    }

    @Override
    public Optional<Country> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM country WHERE id = ?", new CountryRowMapper(), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Country save(Country country) {
        if (country.id() == null) {
            jdbcTemplate.update("INSERT INTO country (id, name, code, population) VALUES (country_seq.NEXTVAL, ?, ?, ?)", country.name(), country.code(), country.population());
            Long newId = jdbcTemplate.queryForObject("SELECT country_seq.CURRVAL FROM dual", Long.class);
            return new Country(newId, country.name(), country.code(), country.population());
        } else {
            String sql = "UPDATE country SET name = ?, code = ?, population = ? WHERE id = ?";
            jdbcTemplate.update(sql, country.name(), country.code(), country.population(), country.id());
            return country;
        }
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM country WHERE id = ?", id);
    }
}
