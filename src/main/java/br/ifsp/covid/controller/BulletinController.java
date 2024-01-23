package br.ifsp.covid.controller;


import br.ifsp.covid.model.Bulletin;
import br.ifsp.covid.model.State;
import br.ifsp.covid.persistence.BulletinDao;
import br.ifsp.covid.persistence.BulletinDaoImpl;
import br.ifsp.covid.persistence.DuplicatedBulletinException;
import br.ifsp.covid.view.BulletinApp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BulletinController {
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnExec;

    @FXML
    private ComboBox<String> cbState;

    @FXML
    private TextField inpCity;

    @FXML
    private DatePicker inpDate;

    @FXML
    private TextField inpDeaths;

    @FXML
    private TextField inpInfected;

    @FXML
    private TextField inpIcuRatio;

    private Bulletin bulletin;

    @FXML
    private void initialize() {
        loadStates();
    }

    private void loadStates() {
        final var stateNames = Arrays.stream(State.values())
                .map(State::toString)
                .collect(Collectors.toList());
        cbState.setItems(FXCollections.observableArrayList(stateNames));
    }

    public void setBulletinIntoView(Bulletin bulletin) {
        this.bulletin = bulletin;
        inpCity.setText(bulletin.getCity());
        inpDate.setValue(bulletin.getDate());
        cbState.getSelectionModel().select(bulletin.getState().toString());
        inpDeaths.setText(String.valueOf(bulletin.getDeaths()));
        inpInfected.setText(String.valueOf(bulletin.getInfected()));
        inpIcuRatio.setText(String.valueOf(bulletin.getIcuRatio()));
    }

    public Bulletin getBulletinFromView() {
        if (bulletin == null)
            bulletin = new Bulletin();

        bulletin.setCity(inpCity.getText());
        bulletin.setDate(inpDate.getValue());
        bulletin.setState(State.fromName(cbState.getSelectionModel().getSelectedItem()));
        bulletin.setDeaths(Integer.parseInt(inpDeaths.getText()));
        bulletin.setInfected(Integer.parseInt(inpInfected.getText()));
        bulletin.setIcuRatio(Double.parseDouble(inpIcuRatio.getText()));

        return bulletin;
    }

    public void cancelar(ActionEvent event) throws IOException { close(); }

    public void execute(ActionEvent event) {
        BulletinDao bulletinDao = new BulletinDaoImpl();

        try {
            if (bulletin == null) {
                bulletinDao.insert(getBulletinFromView());
            } else {
                bulletinDao.update(getBulletinFromView());
            }
            close();
        } catch (DuplicatedBulletinException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            try {
                closeError();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeError() throws IOException { BulletinApp.setRoot("bulletin"); }
    private void close() throws IOException { BulletinApp.setRoot("bulletin_management"); }

}
