package com.tirana.events.service;

import com.tirana.events.model.Event;
import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {
    
    /**
     * Generate Apple Wallet pass data
     * In production, use a proper library like jpasskit
     */
    public Map<String, Object> generateAppleWalletPass(Ticket ticket) {
        Event event = ticket.getEvent();
        User user = ticket.getUser();
        
        Map<String, Object> pass = new HashMap<>();
        pass.put("formatVersion", 1);
        pass.put("passTypeIdentifier", "pass.com.tirana.events");
        pass.put("serialNumber", ticket.getId().toString());
        pass.put("teamIdentifier", "YOUR_TEAM_ID");
        pass.put("organizationName", "Tirana Events");
        pass.put("description", event.getName());
        
        // Event ticket specific fields
        Map<String, Object> eventTicket = new HashMap<>();
        eventTicket.put("primaryFields", createField("event", event.getName(), "EVENT"));
        eventTicket.put("secondaryFields", createField("date", 
            event.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")), 
            "DATE"));
        eventTicket.put("auxiliaryFields", createField("location", event.getLocation(), "LOCATION"));
        eventTicket.put("backFields", createField("attendee", user.getFullName(), "ATTENDEE"));
        
        pass.put("eventTicket", eventTicket);
        
        // Barcode
        Map<String, Object> barcode = new HashMap<>();
        barcode.put("message", ticket.getQrCode());
        barcode.put("format", "PKBarcodeFormatQR");
        barcode.put("messageEncoding", "iso-8859-1");
        pass.put("barcodes", new Object[]{barcode});
        
        // Colors
        pass.put("backgroundColor", "rgb(10, 10, 15)");
        pass.put("foregroundColor", "rgb(255, 255, 255)");
        pass.put("labelColor", "rgb(160, 160, 176)");
        
        // Relevant date
        pass.put("relevantDate", event.getStartDate().toString());
        
        // Location
        if (event.getLatitude() != null && event.getLongitude() != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("latitude", event.getLatitude());
            location.put("longitude", event.getLongitude());
            location.put("relevantText", "You're near " + event.getName());
            pass.put("locations", new Object[]{location});
        }
        
        return pass;
    }
    
    /**
     * Generate Google Wallet pass data
     */
    public Map<String, Object> generateGoogleWalletPass(Ticket ticket) {
        Event event = ticket.getEvent();
        User user = ticket.getUser();
        
        Map<String, Object> pass = new HashMap<>();
        pass.put("id", "tirana-events-" + ticket.getId());
        pass.put("classId", "tirana-events-class");
        pass.put("state", "ACTIVE");
        
        // Event details
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("eventName", createLocalizedString(event.getName()));
        eventDetails.put("venue", createLocalizedString(event.getLocation()));
        eventDetails.put("dateTime", createDateTime(event.getStartDate()));
        pass.put("eventTicketObject", eventDetails);
        
        // Barcode
        Map<String, Object> barcode = new HashMap<>();
        barcode.put("type", "QR_CODE");
        barcode.put("value", ticket.getQrCode());
        pass.put("barcode", barcode);
        
        // Ticket holder
        Map<String, Object> ticketHolder = new HashMap<>();
        ticketHolder.put("ticketHolderName", user.getFullName());
        pass.put("ticketHolderName", ticketHolder);
        
        // Hex color
        pass.put("hexBackgroundColor", "#0A0A0F");
        
        return pass;
    }
    
    /**
     * Generate NFC data for tap-to-enter
     */
    public String generateNFCData(Ticket ticket) {
        // NFC data format: TICKET:{ticketId}:{qrCode}:{timestamp}
        String data = String.format("TICKET:%d:%s:%d", 
            ticket.getId(), 
            ticket.getQrCode(),
            System.currentTimeMillis());
        
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    
    /**
     * Verify NFC data
     */
    public boolean verifyNFCData(String nfcData, Ticket ticket) {
        try {
            String decoded = new String(Base64.getDecoder().decode(nfcData));
            String[] parts = decoded.split(":");
            
            if (parts.length != 4 || !parts[0].equals("TICKET")) {
                return false;
            }
            
            Long ticketId = Long.parseLong(parts[1]);
            String qrCode = parts[2];
            
            return ticketId.equals(ticket.getId()) && qrCode.equals(ticket.getQrCode());
        } catch (Exception e) {
            return false;
        }
    }
    
    private Map<String, Object> createField(String key, String value, String label) {
        Map<String, Object> field = new HashMap<>();
        field.put("key", key);
        field.put("value", value);
        field.put("label", label);
        return field;
    }
    
    private Map<String, String> createLocalizedString(String value) {
        Map<String, String> localized = new HashMap<>();
        localized.put("language", "en");
        localized.put("value", value);
        return localized;
    }
    
    private Map<String, String> createDateTime(java.time.LocalDateTime dateTime) {
        Map<String, String> dt = new HashMap<>();
        dt.put("date", dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dt;
    }
}
