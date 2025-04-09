package dao;

import models.DatabaseConnection;
import models.FavoriteResource;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for main program to manage favorite resources the user selects from searches via Google Places API.
 * @author Isabella Castillo
 */
public class FavoriteResourcesDAO {

    /**
     * Gets a list of all the favorite resources within the database.
     * Useful for returning to the user their favorites when needed.
     * 
     * @return List<FavoriteResource> Returns a ArrayList of all of the favorite resources in the database.
     * @throws SQLException           If an error occurs.
     */
    public List<FavoriteResource> getAllFavorites() throws SQLException {

        List<FavoriteResource> favorites = new ArrayList<>();
        String query = "SELECT * FROM favorite_resources ORDER BY name";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                favorites.add(new FavoriteResource(rs.getInt("resource_id"), rs.getString("name"), rs.getString("address"), rs.getString("phone_number"), rs.getString("website")));
            }
        }
        return favorites;
    }

    /**
     * Finds a resource by it's assigned ID.
     * Useful for fetching entries for updating/deletion.
     * 
     * @param  resourceID       Unique ID assigned to that specific resource entry.
     * @return FavoriteResource Returns the FavoriteResource object by the given ID.
     * @throws SQLException     If an error occurs.
     */
    public FavoriteResource getFavoriteByID(int resourceID) throws SQLException {

        String query = "SELECT * FROM favorite_resources WHERE resource_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, resourceID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new FavoriteResource(rs.getInt("resource_id"), rs.getString("name"), rs.getString("address"), rs.getString("phone_number"), rs.getString("website"));
            }
        }
        return null;
    }

    /**
     * Searches the database of entries (resource name and address since those are required) by keyword.
     * Useful for user searches.
     * 
     * @param  keyword                Word that represents our search value for query.
     * @return List<FavoriteResource> Returns the valid search results in a list.
     * @throws SQLException           If an error occurs.
     */
    public List<FavoriteResource> searchFavorites(String keyword) throws SQLException {

        List<FavoriteResource> results = new ArrayList<>();
        String query = "SELECT * FROM favorite_resources WHERE name LIKE ? OR address LIKE ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(new FavoriteResource(rs.getInt("resource_id"), rs.getString("name"), rs.getString("address"), rs.getString("phone_number"), rs.getString("website")));
            }
        }
        return results;
    }

    /**
     * Checks if the resource is already favorited.
     * Useful for checks before adding to database.
     * 
     * @param name          Name of associated resource.
     * @param address       Address of associated resource.
     * @return boolean      Returns T/F based on if the resource is already a favorite or not.
     * @throws SQLException If an error occurs.
     */
    public boolean isResourceAlreadyFavorited(String name, String address) throws SQLException {

        String query = "SELECT COUNT(*) AS count FROM favorite_resources WHERE name = ? AND address = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt("count") > 0; // Will return true if already favorited or false if not.
        }
    }

    /**
     * Inserts favorite resource objects into the database.
     * Useful for inserting new FavoriteResource objects into the database.
     * 
     * @param  resource     A FavoriteResource type variable of the entry using FavoriteResource.java to add to database.
     * @return boolean      Returns T/F based on if the insertion is successful or not.
     * @throws SQLException If an error occurs.
     */
    public boolean insertFavorite(FavoriteResource resource) throws SQLException {

        String query = "INSERT INTO favorite_resources (name, address, phone_number, website) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, resource.getName());
            pstmt.setString(2, resource.getAddress());
            pstmt.setString(3, resource.getPhoneNumber());
            pstmt.setString(4, resource.getWebsite());

            return pstmt.executeUpdate() > 0; // Will return true upon successful insertion or false if could not insert.
        }
    }

    /**
     * Updates a known favorite resource entry in the database.
     * Useful for using together with search functions to find and update entries by user request.
     * 
     * @param  resource     A FavoriteResource type variable of the entry using FavoriteResource.java to update to the database.
     * @return boolean      Will return true if update was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean updateFavorite(FavoriteResource resource) throws SQLException {

        String query = "UPDATE favorite_resources SET name = ?, address = ?, phone_number = ?, website = ? WHERE resource_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, resource.getName());
            pstmt.setString(2, resource.getAddress());
            pstmt.setString(3, resource.getPhoneNumber());
            pstmt.setString(4, resource.getWebsite());
            pstmt.setInt(5, resource.getResourceID());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a favorite resource entry in the database.
     * Useful for using together with search functions to find and delete entries by user request.
     * 
     * @param  resourceID   The unique favorite resource ID we are updating.
     * @return boolean      Will return true if deletion was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean deleteFavorite(int resourceID) throws SQLException {

        String query = "DELETE FROM favorite_resources WHERE resource_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, resourceID);
            return pstmt.executeUpdate() > 0; // Will return true upon successful deletion or false if could not delete.
        }
    }
}