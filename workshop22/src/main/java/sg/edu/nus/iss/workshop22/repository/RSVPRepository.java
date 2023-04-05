package sg.edu.nus.iss.workshop22.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.workshop22.model.RSVP;
import static sg.edu.nus.iss.workshop22.repository.DBQueries.*;

@Repository
public class RSVPRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<RSVP> getAllRSVP() {
        List<RSVP> rsvps = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_ALL_RSVP);

        while (rs.next()) {
            rsvps.add(RSVP.createFromResults(rs));
        }
        return rsvps;
    }

    public List<RSVP> getAllRSVPFromName(String name) {
        List<RSVP> rsvps = new ArrayList<>();
        String completeName = "%" + name + "%";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_RSVP_BASED_ON_NAME, completeName);
        while (rs.next()) {
            rsvps.add(RSVP.createFromResults(rs));
        }
        return rsvps;
    }

    public RSVP getRSVPByEmail(String email) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_RSVP_BY_EMAIL, email);
        RSVP rsvp = null;
        if (rs.first()) {
            rsvp = RSVP.createFromResults(rs);
        }
        return rsvp;
    }

    public RSVP createRsvp(RSVP rsvp) {
        RSVP newRsvp = getRSVPByEmail(rsvp.getEmail());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (newRsvp == null) {
            // insert record
            jdbcTemplate.update(conn -> {
                PreparedStatement statement = conn.prepareStatement(INSERT_NEW_RSVP, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, rsvp.getName());
                statement.setString(2, rsvp.getEmail());
                statement.setString(3, rsvp.getPhone());
                statement.setTimestamp(4, new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()));
                statement.setString(5, rsvp.getComments());
                return statement;

            }, keyHolder);

            BigInteger primaryKey = (BigInteger) keyHolder.getKey();
            rsvp.setId(primaryKey.intValue());
        } else {
            // update existing record
            newRsvp.setName(rsvp.getName());
            newRsvp.setPhone(rsvp.getPhone());
            newRsvp.setConfirmationDate(rsvp.getConfirmationDate());
            newRsvp.setComments(rsvp.getComments());

            jdbcTemplate.update(UPDATE_EXISTING_RSVP,
                    newRsvp.getName(),
                    newRsvp.getPhone(),
                    new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()),
                    newRsvp.getComments(),
                    newRsvp.getEmail());

            rsvp.setId(newRsvp.getId());
        }

        return rsvp;
    }

    public RSVP updateRsvp(RSVP rsvp) {
        RSVP newRsvp = getRSVPByEmail(rsvp.getEmail());
        if (newRsvp != null) {
            newRsvp.setName(rsvp.getName());
            newRsvp.setPhone(rsvp.getPhone());
            newRsvp.setConfirmationDate(rsvp.getConfirmationDate());
            newRsvp.setComments(rsvp.getComments());

            jdbcTemplate.update(UPDATE_EXISTING_RSVP,
                    newRsvp.getName(),
                    newRsvp.getPhone(),
                    new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()),
                    newRsvp.getComments(),
                    newRsvp.getEmail());
        }
        return newRsvp;
    }

    public Long getRsvpCount() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(SELECT_RSVP_COUNT);
        return (Long) rows.get(0).get("total_count");
    }

    // public Long getRsvpCount() {
    // SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_RSVP_COUNT);
    // Long count = 0L;
    // while (rs.first()) {
    // count = rs.getLong("total_count");
    // }
    // return count;
    // }

}
