package com.oliinyk.costumes.view;

import com.oliinyk.costumes.model.Costume;
import com.oliinyk.costumes.model.User;
import com.oliinyk.costumes.repository.JdbcCostumeRepository;
import com.oliinyk.costumes.repository.JdbcUserRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminPanelController {

    @FXML private TableView<Costume> costumesTable;
    @FXML private TableView<User> usersTable;
    @FXML private TableView<?> rentalsTable;

    private final JdbcCostumeRepository costumeRepo = new JdbcCostumeRepository();
    private final JdbcUserRepository userRepo = new JdbcUserRepository();

    @FXML
    public void initialize() {
        setupCostumesTable();
        setupUsersTable();
        loadData();
    }

    private void setupCostumesTable() {
        TableColumn<Costume, String> nameCol = (TableColumn<Costume, String>) costumesTable.getColumns().get(0);
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Costume, String> descCol = (TableColumn<Costume, String>) costumesTable.getColumns().get(1);
        descCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Costume, String> priceCol = (TableColumn<Costume, String>) costumesTable.getColumns().get(2);
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPricePerDay().toString()));
    }

    private void setupUsersTable() {
        TableColumn<User, String> emailCol = (TableColumn<User, String>) usersTable.getColumns().get(0);
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> roleCol = (TableColumn<User, String>) usersTable.getColumns().get(1);
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));

        TableColumn<User, String> verifiedCol = (TableColumn<User, String>) usersTable.getColumns().get(2);
        verifiedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isVerified() ? "Так" : "Ні"));
    }

    private void loadData() {
        costumesTable.setItems(FXCollections.observableArrayList(costumeRepo.findAll()));
        usersTable.setItems(FXCollections.observableArrayList(userRepo.findAll()));
    }
}
