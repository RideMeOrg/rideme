package sn.esmt.tasks.taskmanager.dto.converters;

public class RouteRequest {
    private String origin;
    private String destination;
    private String travelMode;
    private String[] allowedTravelModes; // e.g. ["BUS", "TRAIN"]

    // Getters and setters

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String[] getAllowedTravelModes() {
        return allowedTravelModes;
    }

    public void setAllowedTravelModes(String[] allowedTravelModes) {
        this.allowedTravelModes = allowedTravelModes;
    }
}
