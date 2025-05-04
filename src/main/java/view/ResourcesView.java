package view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A view that lets the user search Google Places for local therapists/clinics,
 * displays expandable tiles, and opens links in the default browser.
 */
public class ResourcesView {
    /** DTO for each place. */
    public record PlaceResult(
        String placeId,
        String name,
        String address,
        String phone,
        String website,
        String rating
    ) {}

    private final VBox                   root;
    private final TextField              searchField, zipField;
    private final Button                 searchButton;
    private final ListView<PlaceResult>  resultsList;
    private final HttpClient             http        = HttpClient.newHttpClient();
    private final String                 apiKey      = System.getenv("GOOGLE_PLACES_API_KEY");
    private String                        expandedPlaceId = null;
    private final Map<String, PlaceResult> detailsCache  = new HashMap<>();

    public ResourcesView() {
        // layout
        root = new VBox(15);
        root.getStyleClass().add("content-pane");
        root.setPadding(new Insets(20));

        Label heading = new Label("Resources & Support");
        heading.getStyleClass().add("title-label");

        searchField = new TextField();
        searchField.setPromptText("e.g. therapists");
        zipField = new TextField();
        zipField.setPromptText("zipcode (optional)");

        searchButton = new Button("search");
        searchButton.getStyleClass().addAll("action-button","primary-button");
        searchButton.setOnAction(e -> doSearch());

        HBox searchBar = new HBox(10, searchField, zipField, searchButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        // list
        resultsList = new ListView<>();
        VBox.setVgrow(resultsList, Priority.ALWAYS);
        resultsList.setSelectionModel(new NoSelectionModel<>());

        resultsList.setCellFactory(lv -> new ListCell<>() {
            private final VBox      box       = new VBox(6);
            private final Label     nameLbl   = new Label();
            private final Label     addrLbl   = new Label();
            private final Label     phoneLbl  = new Label();
            private final Hyperlink webLink   = new Hyperlink();
            private final Label     ratingLbl = new Label();

            {
                // no background on cell or container
                setStyle("-fx-background-color: transparent;");
                box.setBackground(Background.EMPTY);
                box.getStyleClass().add("result-tile");
                nameLbl.getStyleClass().add("result-name");
                addrLbl.getStyleClass().add("result-detail");
                phoneLbl.getStyleClass().add("result-detail");
                webLink.getStyleClass().add("result-detail");
                ratingLbl.getStyleClass().add("result-detail");

                webLink.setOnAction(evt -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(URI.create(webLink.getText()));
                        } catch (IOException | RuntimeException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(PlaceResult pr, boolean empty) {
                super.updateItem(pr, empty);
                if (empty || pr == null) {
                    setGraphic(null);
                } else {
                    box.getChildren().clear();
                    nameLbl.setText(pr.name());
                    box.getChildren().add(nameLbl);

                    if (pr.placeId().equals(expandedPlaceId)) {
                        PlaceResult full = detailsCache.getOrDefault(pr.placeId(), pr);
                        addrLbl .setText("📍 " + full.address());
                        phoneLbl.setText("📞 " + full.phone());
                        webLink.setText(full.website());
                        ratingLbl.setText("⭐ " + full.rating());
                        box.getChildren().addAll(addrLbl, phoneLbl, webLink, ratingLbl);
                    }

                    setGraphic(box);
                    setOnMouseClicked(evt -> {
                        if (evt.getClickCount() == 1) {
                            if (pr.placeId().equals(expandedPlaceId)) {
                                expandedPlaceId = null;
                            } else {
                                expandedPlaceId = pr.placeId();
                                if (!detailsCache.containsKey(pr.placeId())) {
                                    fetchDetails(pr);
                                }
                            }
                            resultsList.refresh();
                        }
                    });
                }
            }
        });

        // assemble without any grey card
        root.getChildren().setAll(
            heading,
            searchBar,
            resultsList
        );
    }

    private void doSearch() {
        String term = searchField.getText().trim();
        String zip  = zipField.getText().trim();
        if (term.isEmpty()) {
            alert("enter a search term");
            return;
        }
        if (apiKey == null) {
            alert("missing GOOGLE_PLACES_API_KEY");
            return;
        }
        resultsList.setItems(FXCollections.observableArrayList(
            new PlaceResult("","… searching …","","","","")
        ));
        expandedPlaceId = null;
        detailsCache.clear();

        String q   = zip.isEmpty() ? term : term + " near " + zip;
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
                   + "?query=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
                   + "&key=" + apiKey;

        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(this::parseAndShow)
            .exceptionally(ex -> {
                Platform.runLater(() -> alert("api error: " + ex.getMessage()));
                return null;
            });
    }

    private void parseAndShow(String json) {
        JsonArray arr = JsonParser.parseString(json)
                                  .getAsJsonObject()
                                  .getAsJsonArray("results");
        List<PlaceResult> list = new ArrayList<>();
        for (JsonElement e : arr) {
            JsonObject p = e.getAsJsonObject();
            list.add(new PlaceResult(
                p.get("place_id").getAsString(),
                p.get("name").getAsString(),
                p.has("formatted_address") ? p.get("formatted_address").getAsString() : "n/a",
                "…","…",
                p.has("rating") ? p.get("rating").getAsString() : "n/a"
            ));
        }
        if (list.isEmpty()) {
            list.add(new PlaceResult("","no results","","","",""));
        }
        Platform.runLater(() ->
            resultsList.setItems(FXCollections.observableArrayList(list))
        );
    }

    private void fetchDetails(PlaceResult base) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json"
                   + "?place_id=" + base.placeId()
                   + "&fields=name,formatted_address,formatted_phone_number,website,rating"
                   + "&key=" + apiKey;
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(body -> {
                JsonObject r = JsonParser.parseString(body)
                                   .getAsJsonObject()
                                   .getAsJsonObject("result");
                String phone   = r.has("formatted_phone_number")
                               ? r.get("formatted_phone_number").getAsString()
                               : "n/a";
                String website = r.has("website")
                               ? r.get("website").getAsString()
                               : "n/a";
                String rating  = r.has("rating")
                               ? r.get("rating").getAsString()
                               : base.rating();

                PlaceResult full = new PlaceResult(
                    base.placeId(), base.name(), base.address(),
                    phone, website, rating
                );
                detailsCache.put(base.placeId(), full);
                Platform.runLater(resultsList::refresh);
            })
            .exceptionally(ex -> null);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public Node getView() {
        return root;
    }

    /** disables built-in selection/highlight */
    private static class NoSelectionModel<T> extends MultipleSelectionModel<T> {
        @Override public ObservableList<Integer> getSelectedIndices() { return FXCollections.emptyObservableList(); }
        @Override public ObservableList<T>       getSelectedItems()   { return FXCollections.emptyObservableList(); }
        @Override public void selectIndices(int index, int... indices) {}
        @Override public void selectAll() {}
        @Override public void clearAndSelect(int index) {}
        @Override public void select(int index) {}
        @Override public void select(T obj) {}
        @Override public void clearSelection(int index) {}
        @Override public void clearSelection() {}
        @Override public boolean isSelected(int index) { return false; }
        @Override public boolean isEmpty()             { return true; }
        @Override public void selectPrevious()        {}
        @Override public void selectNext()            {}
        @Override public void selectFirst()           {}
        @Override public void selectLast()            {}
    }
}
