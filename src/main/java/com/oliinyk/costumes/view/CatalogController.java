package com.oliinyk.costumes.view;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.viewmodel.CatalogViewModel;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class CatalogController {

    @FXML private TilePane costumesGrid;

    private CatalogViewModel viewModel;

    public void setViewModel(CatalogViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        populateGrid();
    }

    private void bindViewModel() {
        // Тільки байндимо ті речі, що потрібні для каталогу.
        // Наприклад, відображення сітки чи списку, коли ми це додамо.
    }
    
    private void populateGrid() {
        costumesGrid.getChildren().clear();
        for (Costume costume : viewModel.getCostumes()) {
            costumesGrid.getChildren().add(createCostumeCard(costume));
        }
    }
    
    private VBox createCostumeCard(Costume costume) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card"); // З AtlantaFX
        card.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 12; -fx-border-color: -color-border-default; -fx-border-radius: 12; -fx-border-width: 1; -fx-pref-width: 280; -fx-pref-height: 250;");
        
        Label nameLabel = new Label(costume.getName());
        nameLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: -color-fg-default;");
        
        ImageView imageView = new ImageView();
        if (costume.getImagePath() != null && !costume.getImagePath().isEmpty()) {
            try {
                String path = costume.getImagePath();
                java.net.URL url = getClass().getResource(path);
                if (url != null) {
                    imageView.setImage(new Image(url.toExternalForm(), true));
                } else {
                    imageView.setImage(new Image(path, true)); // Fallback
                }
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
            } catch (Exception ex) {}
        }
        
        Label descLabel = new Label(costume.getDescription() != null ? costume.getDescription() : "Немає опису");
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setStyle("-fx-text-fill: -color-fg-muted;");
        
        Label priceLabel = new Label(costume.getPricePerDay() + " грн/день");
        priceLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: -color-accent-emphasis;");
        
        Button addToCartBtn = new Button("Додати в кошик");
        addToCartBtn.getStyleClass().addAll("accent");
        addToCartBtn.setMaxWidth(Double.MAX_VALUE);
        addToCartBtn.setOnAction(e -> {
            viewModel.addToCart(costume);
            addToCartBtn.setText("Додано!");
            addToCartBtn.getStyleClass().remove("accent");
            addToCartBtn.getStyleClass().add("success");
            addToCartBtn.setDisable(true);
        });
        
        card.getChildren().addAll(imageView, nameLabel, descLabel, priceLabel, addToCartBtn);
        return card;
    }
}
