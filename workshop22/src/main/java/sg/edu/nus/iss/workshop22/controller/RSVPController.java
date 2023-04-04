package sg.edu.nus.iss.workshop22.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import sg.edu.nus.iss.workshop22.model.RSVP;
import sg.edu.nus.iss.workshop22.service.RSVPService;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RSVPController {

    @Autowired
    RSVPService rsvpService;

    @GetMapping(path = "/rsvps")
    public ResponseEntity<String> getAllRSVPs() {
        List<RSVP> rsvps = rsvpService.getAllRSVP();
        JsonArrayBuilder rsvpArrBuilder = Json.createArrayBuilder();
        for (RSVP rsvp : rsvps) {
            rsvpArrBuilder.add(rsvp.toJSON());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rsvpArrBuilder.build().toString());
    }

    @GetMapping(path = "/rsvp")
    public ResponseEntity<String> getRSVPByName(@RequestParam(required = true) String q) {

        List<RSVP> rsvps = rsvpService.getAllRSVPFromName(q);

        JsonArrayBuilder jsArrBuilder = Json.createArrayBuilder();
        for (RSVP rsvp : rsvps) {
            System.out.println(rsvp.toString());
            jsArrBuilder.add(rsvp.toJSON());
        }

        JsonArray jsArr = jsArrBuilder.build();

        if (rsvps.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder()
                            .add("Error Message", "Name not found in database.")
                            .build().toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsArr.toString());
    }

    @PostMapping(path = "/rsvp")
    public ResponseEntity<String> insertOrUpdateRSVP(@RequestBody String json) throws IOException {
        RSVP rsvp = RSVP.createFromJSON(json);
        RSVP newRsvp = rsvpService.createRsvp(rsvp);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Json.createObjectBuilder()
                        .add("Message", "%s >>> %s is updated/inserted"
                                .formatted(newRsvp.getId(), newRsvp.getName()))
                        .build()
                        .toString());
    }
}
