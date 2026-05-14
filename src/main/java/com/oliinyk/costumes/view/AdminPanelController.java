package com.oliinyk.costumes.view;

import com.oliinyk.costumes.dto.RentalDTO;
import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.JdbcCostumeRepository;
import com.oliinyk.costumes.repository.JdbcRentalItemRepository;
import com.oliinyk.costumes.repository.JdbcRentalRepository;
import com.oliinyk.costumes.repository.JdbcUserRepository;
import com.oliinyk.costumes.service.RentalFacade;
import com.oliinyk.costumes.service.RentalService;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Контролер панелі адміністратора. Реалізує CRUD операції, асинхронне завантаження та фільтрацію
 * (Вимоги 5.1, 5.2, 4.4.5).
 */
public class AdminPanelController {

    @FXML private TableView<Costume> costumesTable;
    @FXML private TableView<User> usersTable;
    @FXML private TableView<RentalDTO> rentalsTable;
    @FXML private TextField costumeSearchField;

    private final JdbcCostumeRepository costumeRepo = new JdbcCostumeRepository();
    private final JdbcUserRepository userRepo = new JdbcUserRepository();
    private RentalFacade rentalFacade;

    private ObservableList<Costume> costumesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Ініціалізація фасаду
        rentalFacade =
                new RentalFacade(
                        new RentalService(
                                new JdbcRentalRepository(), new JdbcRentalItemRepository()),
                        new JdbcRentalRepository(),
                        new JdbcCostumeRepository(),
                        new JdbcUserRepository());

        setupTables();
        loadDataAsync();
        setupSearch();
    }

    private void setupTables() {
        setupCostumesTable();
        setupUsersTable();
        setupRentalsTable();
    }

    private void setupCostumesTable() {
        TableColumn<Costume, String> nameCol =
                (TableColumn<Costume, String>) costumesTable.getColumns().get(0);
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Costume, String> descCol =
                (TableColumn<Costume, String>) costumesTable.getColumns().get(1);
        descCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Costume, String> priceCol =
                (TableColumn<Costume, String>) costumesTable.getColumns().get(2);
        priceCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPricePerDay().toString()));

        // Додавання кнопок дій (Вимога 5.1 CRUD)
        TableColumn<Costume, Void> actionCol =
                (TableColumn<Costume, Void>) costumesTable.getColumns().get(3);
        actionCol.setCellFactory(
                param ->
                        new javafx.scene.control.TableCell<>() {
                            private final javafx.scene.control.Button editBtn =
                                    new javafx.scene.control.Button();
                            private final javafx.scene.control.Button deleteBtn =
                                    new javafx.scene.control.Button();
                            private final javafx.scene.layout.HBox pane =
                                    new javafx.scene.layout.HBox(10, editBtn, deleteBtn);

                            {
                                editBtn.getStyleClass().addAll("button-icon", "accent");
                                editBtn.setGraphic(new org.kordamp.ikonli.javafx.FontIcon("fas-edit"));
                                editBtn.setTooltip(new javafx.scene.control.Tooltip("Редагувати"));
                                editBtn.setOnAction(
                                        event -> {
                                            Costume costume =
                                                    getTableView().getItems().get(getIndex());
                                            handleEditCostume(costume);
                                        });

                                deleteBtn.getStyleClass().addAll("button-icon", "danger");
                                deleteBtn.setGraphic(new org.kordamp.ikonli.javafx.FontIcon("fas-trash"));
                                deleteBtn.setTooltip(new javafx.scene.control.Tooltip("Видалити"));
                                deleteBtn.setOnAction(
                                        event -> {
                                            Costume costume =
                                                    getTableView().getItems().get(getIndex());
                                            handleDeleteCostume(costume);
                                        });
                                pane.setAlignment(javafx.geometry.Pos.CENTER);
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) setGraphic(null);
                                else setGraphic(pane);
                            }
                        });
    }

    @FXML
    private void onAddCostumeClicked() {
        showCostumeDialog(null);
    }

    private void handleEditCostume(Costume costume) {
        showCostumeDialog(costume);
    }

    private void showCostumeDialog(Costume costume) {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/CostumeDialog.fxml"));
            javafx.scene.layout.VBox page = loader.load();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(costume == null ? "Додати костюм" : "Редагувати костюм");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(costumesTable.getScene().getWindow());
            javafx.scene.Scene scene = new javafx.scene.Scene(page);
            dialogStage.setScene(scene);

            CostumeDialogController controller = loader.getController();
            controller.setCostume(costume);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                Costume result = controller.getCostume();
                if (costume == null) {
                    costumeRepo.save(result);
                } else {
                    costumeRepo.update(result);
                }
                loadDataAsync();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCostume(Costume costume) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження видалення");
        alert.setHeaderText("Видалити костюм: " + costume.getName() + "?");
        alert.setContentText("Цю дію неможливо скасувати.");

        alert.showAndWait()
                .ifPresent(
                        response -> {
                            if (response == javafx.scene.control.ButtonType.OK) {
                                costumeRepo.delete(costume.getId());
                                loadDataAsync(); // Перезавантаження даних
                            }
                        });
    }

    private void setupUsersTable() {
        TableColumn<User, String> emailCol =
                (TableColumn<User, String>) usersTable.getColumns().get(0);
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> roleCol =
                (TableColumn<User, String>) usersTable.getColumns().get(1);
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));

        TableColumn<User, String> verifiedCol =
                (TableColumn<User, String>) usersTable.getColumns().get(2);
        verifiedCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().isVerified() ? "Так" : "Ні"));
        verifiedCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label badge = new javafx.scene.control.Label(val);
                    badge.getStyleClass().addAll("badge", "Так".equals(val) ? "success" : "subtle");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<User, String> blockedCol = new TableColumn<>("Заблоковано");
        blockedCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().isBlocked() ? "Так" : "Ні"));
        blockedCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label badge = new javafx.scene.control.Label(val);
                    badge.getStyleClass().addAll("badge", "Так".equals(val) ? "danger" : "subtle");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("Дії");
        actionCol.setCellFactory(
                param ->
                        new javafx.scene.control.TableCell<>() {
                            private final javafx.scene.control.Button blockBtn =
                                    new javafx.scene.control.Button();

                            {
                                blockBtn.getStyleClass().add("button-icon");
                                blockBtn.setOnAction(
                                        event -> {
                                            User user = getTableView().getItems().get(getIndex());
                                            handleToggleBlock(user);
                                        });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    User user = getTableView().getItems().get(getIndex());
                                    if (user.isBlocked()) {
                                        blockBtn.setGraphic(new org.kordamp.ikonli.javafx.FontIcon("fas-lock-open"));
                                        blockBtn.getStyleClass().setAll("button-icon", "success");
                                        blockBtn.setTooltip(new javafx.scene.control.Tooltip("Розблокувати"));
                                    } else {
                                        blockBtn.setGraphic(new org.kordamp.ikonli.javafx.FontIcon("fas-lock"));
                                        blockBtn.getStyleClass().setAll("button-icon", "danger");
                                        blockBtn.setTooltip(new javafx.scene.control.Tooltip("Заблокувати"));
                                    }
                                    setGraphic(blockBtn);
                                }
                            }
                        });

        usersTable.getColumns().addAll(blockedCol, actionCol);
    }

    private void handleToggleBlock(User user) {
        user.setBlocked(!user.isBlocked());
        userRepo.update(user);
        usersTable.refresh(); // Миттєве оновлення UI (Вимога розділу 6)
    }

    private void setupRentalsTable() {
        TableColumn<RentalDTO, String> userCol =
                (TableColumn<RentalDTO, String>) rentalsTable.getColumns().get(0);
        userCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getUserEmail()));

        TableColumn<RentalDTO, String> startCol =
                (TableColumn<RentalDTO, String>) rentalsTable.getColumns().get(1);
        startCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getStartDate().toString()));

        TableColumn<RentalDTO, String> endCol =
                (TableColumn<RentalDTO, String>) rentalsTable.getColumns().get(2);
        endCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getEndDate().toString()));

        TableColumn<RentalDTO, String> statusCol =
                (TableColumn<RentalDTO, String>) rentalsTable.getColumns().get(3);
        statusCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label badge = new javafx.scene.control.Label();
                    if ("ACTIVE".equals(status)) {
                        badge.setText("Активна");
                        badge.getStyleClass().addAll("badge", "accent");
                    } else if ("COMPLETED".equals(status)) {
                        badge.setText("Завершена");
                        badge.getStyleClass().addAll("badge", "success");
                    } else {
                        badge.setText(status);
                        badge.getStyleClass().add("badge");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<RentalDTO, String> totalCol =
                (TableColumn<RentalDTO, String>) rentalsTable.getColumns().get(4);
        totalCol.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getTotalPrice().toString() + " грн"));

        // Додавання кнопок зміни статусу (Вимога 2.2 Admin Panel)
        TableColumn<RentalDTO, Void> actionCol = new TableColumn<>("Дії");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(
                param ->
                        new javafx.scene.control.TableCell<>() {
                            private final javafx.scene.control.Button completeBtn =
                                    new javafx.scene.control.Button();

                            {
                                completeBtn.getStyleClass().addAll("button-icon", "success");
                                completeBtn.setGraphic(new org.kordamp.ikonli.javafx.FontIcon("fas-check"));
                                completeBtn.setTooltip(new javafx.scene.control.Tooltip("Завершити оренду"));
                                completeBtn.setOnAction(
                                        event -> {
                                            RentalDTO rental =
                                                    getTableView().getItems().get(getIndex());
                                            handleStatusChange(rental, "COMPLETED");
                                        });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    RentalDTO rental = getTableView().getItems().get(getIndex());
                                    if ("ACTIVE".equals(rental.getStatus())) {
                                        setGraphic(completeBtn);
                                    } else {
                                        setGraphic(null);
                                    }
                                }
                            }
                        });
        rentalsTable.getColumns().add(actionCol);
    }

    private void handleStatusChange(RentalDTO rentalDto, String newStatus) {
        JdbcRentalRepository rentalRepo = new JdbcRentalRepository();
        rentalRepo
                .findById(rentalDto.getId())
                .ifPresent(
                        rental -> {
                            rental.setStatus(newStatus);
                            rentalRepo.update(rental);
                            loadDataAsync();
                        });
    }

    private void loadDataAsync() {
        // Асинхронне завантаження (Вимога розділу 4.4.5)
        Task<Void> loadTask =
                new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        List<Costume> costumes = costumeRepo.findAll();
                        List<User> users = userRepo.findAll();
                        List<RentalDTO> rentals = rentalFacade.getAllRentals();

                        // Оновлення UI в основному потоці
                        javafx.application.Platform.runLater(
                                () -> {
                                    costumesData.setAll(costumes);
                                    usersTable.setItems(FXCollections.observableArrayList(users));
                                    rentalsTable.setItems(
                                            FXCollections.observableArrayList(rentals));
                                });
                        return null;
                    }
                };
        new Thread(loadTask).start();
    }

    private void setupSearch() {
        // Реалізація пошуку (Вимога розділу 5.2)
        FilteredList<Costume> filteredData = new FilteredList<>(costumesData, p -> true);
        costumeSearchField
                .textProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            filteredData.setPredicate(
                                    costume -> {
                                        if (newValue == null || newValue.isEmpty()) return true;
                                        String lowerCaseFilter = newValue.toLowerCase();
                                        return costume.getName()
                                                .toLowerCase()
                                                .contains(lowerCaseFilter);
                                    });
                        });
        costumesTable.setItems(filteredData);
    }
}
