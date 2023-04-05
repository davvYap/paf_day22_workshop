package sg.edu.nus.iss.workshop22.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.workshop22.model.RSVP;
import sg.edu.nus.iss.workshop22.repository.RSVPRepository;

@Service
public class RSVPService {
    @Autowired
    RSVPRepository rsvpRepository;

    public List<RSVP> getAllRSVP() {
        return rsvpRepository.getAllRSVP();
    }

    // to retrieve all records
    public List<RSVP> getAllRSVPFromName(String name) {
        List<RSVP> rsvps = rsvpRepository.getAllRSVPFromName(name);
        return rsvps;
    }

    public RSVP getRSVPByEmail(String email) {
        return rsvpRepository.getRSVPByEmail(email);
    }

    public RSVP createRsvp(RSVP rsvp) {
        return rsvpRepository.createRsvp(rsvp);
    }

    public RSVP updateRsvp(RSVP updateRsvp) {
        RSVP rsvp = rsvpRepository.updateRsvp(updateRsvp);
        return rsvp;
    }

    public Long getRsvpCount() {
        return rsvpRepository.getRsvpCount();
    }
}
