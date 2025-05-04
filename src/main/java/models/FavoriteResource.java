package models;

/**
 * Object class for a favorite resource.
 * @author Isabella Castillo
 */
public class FavoriteResource {

    private int resourceID;
    private String name;
    private String address;
    private String phoneNumber;
    private String website;

    /**
     * FavoriteResource object.
     * 
     * @param resourceID  The autoincremented resource ID.
     * @param name        The name of the resource.
     * @param address     The resources physical address scraped from Google Places API return.
     * @param phoneNumber The resources phone number scraped from Google Places API return (optional aka can be null). 
     * @param website     The resources website (optional aka can be null).
     */
    public FavoriteResource(int resourceID, String name, String address, String phoneNumber, String website) {
        this.resourceID = resourceID;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
    }

    /**
     * Set of getters for parameters within the FavoriteResource object.
     */
    public int getResourceID() { return resourceID; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsite() { return website; }
}