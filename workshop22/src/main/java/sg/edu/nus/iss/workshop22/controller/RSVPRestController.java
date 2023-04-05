package sg.edu.nus.iss.workshop22.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class RSVPRestController {

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

        // passing json to manipulate database data
        @PostMapping(path = "/rsvp", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> insertOrUpdateRsvpJSON(@RequestBody String json) throws IOException {
                RSVP rsvp = RSVP.createFromJSON(json);

                // to check whether is existing record
                Boolean isExisting = rsvpService.getRSVPByEmail(rsvp.getEmail()) == null ? false : true;
                System.out.println("isExisting >>>>> " + isExisting);

                RSVP newRsvp = rsvpService.createRsvp(rsvp);

                String message = isExisting ? "%s >>> %s is updated".formatted(newRsvp.getId(), newRsvp.getName())
                                : "%s >>> %s is inserted".formatted(newRsvp.getId(), newRsvp.getName());
                System.out.println(message);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Json.createObjectBuilder()
                                                .add("Message", message)
                                                .build()
                                                .toString());
        }

        @PutMapping(path = "/rsvp/{email}")
        public ResponseEntity<String> updateRsvpJSON(@PathVariable String email, @RequestBody String json)
                        throws IOException {
                RSVP rsvp = RSVP.createFromJSON(json);
                RSVP existingRsvp = rsvpService.getRSVPByEmail(email);

                if (existingRsvp == null) {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(Json.createObjectBuilder()
                                                        .add("Error message", "Rsvp with %s not found".formatted(email))
                                                        .build().toString());
                }

                // update record
                RSVP updatedRsvp = rsvpService.updateRsvp(rsvp);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Json.createObjectBuilder()
                                                .add("Message", "Rsvp with %s was updated"
                                                                .formatted(updatedRsvp.getEmail()))
                                                .build().toString());
        }

        // passing data using a form
        @PostMapping(path = "/rsvp/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        public ResponseEntity<String> insertOrUpdateRsvpForm(@ModelAttribute RSVP rsvp, @RequestParam String date) {
                // to check whether is existing record
                Boolean isExisting = rsvpService.getRSVPByEmail(rsvp.getEmail()) == null ? false : true;

                System.out.println("Date from form >>>>>>>>>>>>>>>>>>>>>>>>>>> " + date);
                rsvp.setConfirmationDate(RSVP.getDateTimeFromHtmlForm(date));
                RSVP newRsvp = rsvpService.createRsvp(rsvp);

                String message = isExisting ? "%s >>> %s is updated".formatted(newRsvp.getId(), newRsvp.getName())
                                : "%s >>> %s is inserted".formatted(newRsvp.getId(), newRsvp.getName());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Json.createObjectBuilder()
                                                .add("Message", message)
                                                .build()
                                                .toString());
        }

}
