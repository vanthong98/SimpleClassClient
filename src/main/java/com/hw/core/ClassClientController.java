package com.hw.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;

public class ClassClientController {
    public TextField portTextField;
    public Button connectButton;
    public Button disconnectButton;
    public Canvas canvas;
    public TextArea classMessageTextArea;
    public TextArea messageTextArea;
    public Button sendMessageButton;
    public TextArea systemConsoleTextArea;
    public TextField addressTextField;
    public Socket socket;
    public ListView<String> receivedFileListView;

    public void onConnectButtonClick(ActionEvent actionEvent) {
        Logger.init(systemConsoleTextArea);

        var address = addressTextField.getText();
        var port = portTextField.getText();

        if (address.isEmpty() || port.isEmpty()) {
            return;
        }

        ObservableList<String> fileNames = FXCollections.observableArrayList();
        receivedFileListView.setItems(fileNames);

        Dispatcher.init(classMessageTextArea, canvas, fileNames);

        try {
            Logger.log("Connecting to " + address + ":" + port);
            socket = new Socket(address, Integer.parseInt(port));
            Logger.log("Connected to " + address + ":" + port);

            var clientSocketInputHandler = new ClientSocketInputHandler(socket.getInputStream());
            clientSocketInputHandler.start();
            var clientSocketOutputHandler = new ClientSocketOutputHandler(socket.getOutputStream());
            clientSocketOutputHandler.start();

            portTextField.setDisable(true);
            addressTextField.setDisable(true);
            connectButton.setDisable(true);

            sendMessageButton.setDisable(false);
            disconnectButton.setDisable(false);
            messageTextArea.setDisable(false);
            messageTextArea.setEditable(true);

        } catch (IOException e) {
            Logger.log("Could not connect to socket");
        }
    }

    public void onDisconnectButtonClick(ActionEvent actionEvent) {

        portTextField.setDisable(false);
        connectButton.setDisable(false);

        sendMessageButton.setDisable(true);
        disconnectButton.setDisable(true);
        messageTextArea.setDisable(true);
        messageTextArea.setEditable(false);

        Logger.log("Disconnected");
    }

    public void onSendMessageButtonClick(ActionEvent actionEvent) {
        var text = messageTextArea.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        messageTextArea.clear();
        Dispatcher.sendToClassMessage("You", text);
        Dispatcher.send(text);
    }

    public void onReceivedFileListViewClick(MouseEvent mouseEvent) {

        try {
            var fileName = receivedFileListView.getSelectionModel().getSelectedItem();
            var commonFile = Dispatcher.getReceivedFile(fileName);
            byte[] decodedBytes = Base64.getDecoder().decode(commonFile.base64);
            var tempFilePath = Files.createTempFile(null, null);

            try (var fos = new FileOutputStream(tempFilePath.toFile())) {
                fos.write(decodedBytes);
            }

            var fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(fileName);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(commonFile.mimeType, "*.*"));

            var file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                Files.copy(tempFilePath, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e){
            Logger.log(e.getMessage());
        }

    }
}