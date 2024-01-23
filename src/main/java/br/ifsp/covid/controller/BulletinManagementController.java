package br.ifsp.covid.controller;

import br.ifsp.covid.model.Bulletin;
import br.ifsp.covid.model.State;
import br.ifsp.covid.persistence.BulletinDao;
import br.ifsp.covid.persistence.BulletinDaoImpl;
import br.ifsp.covid.view.BulletinApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BulletinManagementController {

    @FXML
    private TableView<Bulletin> tableView;
    @FXML
    private TableColumn<Bulletin, String> cCity;
    @FXML
    private TableColumn<Bulletin, LocalDate> cDate;
    @FXML
    private TableColumn<Bulletin, String> cState;
    @FXML
    private TableColumn<Bulletin, Integer> cId;
    @FXML
    private TableColumn<Bulletin, Integer> cInfected;
    @FXML
    private TableColumn<Bulletin, Double> cIcuRatio;
    @FXML
    private TableColumn<Bulletin, Integer> cDeaths;

    @FXML
    private ComboBox<String> cbState;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private DatePicker dpBegin;
    @FXML
    private TextField txtCity;

    @FXML
    private Label lbAverageIcu;
    @FXML
    private Label lbTotalDeaths;
    @FXML
    private Label lbTotalInfected;

    private List<Bulletin> databaseData;
    private ObservableList<Bulletin> tableData;
    private ObservableList<Bulletin> originalItems;

    @FXML
    private void initialize() {
        loadStates();
        bindTableViewToItemsList();
        bindColumnsToValueSources();
        loadDataAndUpdateTable();
        originalItems = FXCollections.observableArrayList(tableView.getItems());
    }

    private void loadStates() {
        final var stateNames = Arrays.stream(State.values())
                .map(State::toString)
                .collect(Collectors.toList());
        cbState.setItems(FXCollections.observableArrayList(stateNames));
    }

    private void bindTableViewToItemsList() {
        tableData = FXCollections.observableArrayList();
        tableView.setItems(tableData);
    }

    private void bindColumnsToValueSources() {
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        cState.setCellValueFactory(new PropertyValueFactory<>("state"));
        cInfected.setCellValueFactory(new PropertyValueFactory<>("infected"));
        cDeaths.setCellValueFactory(new PropertyValueFactory<>("deaths"));
        cIcuRatio.setCellValueFactory(new PropertyValueFactory<>("IcuRatio"));
        cDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadDataAndUpdateTable() {
        BulletinDao bulletinDao = new BulletinDaoImpl();
        databaseData = bulletinDao.findAll();
        tableData.setAll(databaseData);
        updateStatistics();
    }

    private void updateTable(List<Bulletin> data) {
        tableData.clear();
        tableData.addAll(data);
    }

    @FXML
    public void newBulletin() throws IOException {
        BulletinApp.setRoot("bulletin");
    }


    @FXML
    public void editBulletin() throws IOException {
        final Bulletin selectedBulletin = tableView.getSelectionModel().getSelectedItem();
        if (selectedBulletin == null)
            return;

        BulletinApp.setRoot("bulletin");
        BulletinController bulletinController = (BulletinController) BulletinApp.getController();
        bulletinController.setBulletinIntoView(selectedBulletin);
    }

    @FXML
    public void removeBulletin() {
        Bulletin selectedBulletin = tableView.getSelectionModel().getSelectedItem();

        if (selectedBulletin != null) {
            BulletinDao bulletinDao = new BulletinDaoImpl();
            bulletinDao.delete(selectedBulletin);
            loadDataAndUpdateTable();
        }
    }

    @FXML
    public void filter() {
        String city = txtCity.getText();
        String state = cbState.getValue();
        LocalDate beginDate = dpBegin.getValue();
        LocalDate endDate = dpEnd.getValue();

        Stream<Bulletin> stream = originalItems.stream();

        if (city != null && !city.isEmpty()) {
            stream = stream.filter(bulletin -> bulletin.getCity().equalsIgnoreCase(city));
        }
        if (state != null && !state.isEmpty()) {
            stream = stream.filter(bulletin -> bulletin.getState().toString().equals(state));
        }
        if (beginDate != null) {
            stream = stream.filter(bulletin -> !bulletin.getDate().isBefore(beginDate));
        }
        if (endDate != null) {
            stream = stream
                    .filter(bulletin -> bulletin.getDate().isBefore(endDate) || bulletin.getDate().isEqual(endDate));
        }

        ObservableList<Bulletin> filteredList = FXCollections.observableArrayList(stream.collect(Collectors.toList()));

        tableView.setItems(filteredList);
        updateStatistics();
    }

    @FXML
    private void updateStatistics() {
        int totalInfected = 0;
        int totalDeaths = 0;
        double totalIcu = 0.0;

        List<Bulletin> bulletins = tableView.getItems();

        for (Bulletin bulletin : bulletins) {
            totalInfected += bulletin.getInfected();
            totalDeaths += bulletin.getDeaths();
            totalIcu += bulletin.getIcuRatio();
        }

        double averageIcu = bulletins.isEmpty() ? 0.0 : totalIcu / bulletins.size();

        lbTotalInfected.setText(String.format(Locale.US, "%d", totalInfected));
        lbTotalDeaths.setText(String.format(Locale.US, "%d", totalDeaths));
        lbAverageIcu.setText(String.format(Locale.US, "%.2f", averageIcu));
    }
}
