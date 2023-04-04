package sg.edu.nus.iss.workshop22.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class RSVP {
    private int id;
    private String name;
    private String email;
    private String phone;
    private DateTime confirmationDate;
    private String comments;

    public RSVP() {
    }

    public RSVP(int id, String name, String email, String phone, DateTime confirmationDate, String comments) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.confirmationDate = confirmationDate;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(DateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "RSVP [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", confirmationDate="
                + confirmationDate + ", comments=" + comments + "]";
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("email", this.getEmail())
                .add("phone", this.getPhone())
                .add("confirmationDate", this.getConfirmationDate().toString(DateTimeFormat.forPattern("dd-MM-yyyy")))
                .add("comments", this.getComments())
                .build();
    }

    public static RSVP createFromResults(SqlRowSet rs) {
        RSVP rsvp = new RSVP();
        rsvp.setId(rs.getInt("id"));
        rsvp.setName(rs.getString("name"));
        rsvp.setEmail(rs.getString("email"));
        rsvp.setPhone(rs.getString("phone"));
        rsvp.setConfirmationDate(new DateTime(
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm").parseDateTime(rs.getString("confirmation_date"))));
        rsvp.setComments(rs.getString("comments"));
        return rsvp;
    }

    public static RSVP createFromJSON(String json) throws IOException {
        RSVP rsvp = new RSVP();
        if (json != null) {
            try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
                JsonReader jr = Json.createReader(is);
                JsonObject jsObj = jr.readObject();
                rsvp.setName(jsObj.getString("name"));
                rsvp.setEmail(jsObj.getString("email"));
                rsvp.setPhone(jsObj.getString("phone"));
                String date = jsObj.getString("confirmationDate");
                rsvp.setConfirmationDate(getDateTime(date));
                rsvp.setComments(jsObj.getString("comments"));
            }
        }
        return rsvp;
    }

    public static DateTime getDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dateTime = formatter.parseDateTime(date);
        return dateTime;
    }

}
