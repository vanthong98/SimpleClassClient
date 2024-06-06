package com.hw.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ClientSocketInputHandler extends Thread {

    InputStream _socketInputStream;

    public ClientSocketInputHandler(InputStream socketInputStream) {
        _socketInputStream  = socketInputStream;
    }

    public void run() {
        Logger.log("Client socket input handler started");
        var reader = new BufferedReader(new InputStreamReader(_socketInputStream)) ;

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                var response = reader.readLine();

                if (response.isEmpty()){
                    continue;
                }

                var parts = response.split(":");
                var hasContent = parts.length > 2;
                var senderPart = parts[0];
                var typePart = parts[1];
                var contentPart = hasContent ? parts[2] : "";

                if (Objects.equals(typePart, MessageType.SendMessage.toString())) {
                    Dispatcher.sendToClassMessage(senderPart, contentPart);
                }

                if (Objects.equals(typePart, MessageType.SendBoardActionStartDrawing.toString())){
                    Dispatcher.startDrawing(contentPart);
                }

                if (Objects.equals(typePart, MessageType.SendBoardActionDrawing.toString())){
                    Dispatcher.draw(contentPart);
                }

                if (Objects.equals(typePart, MessageType.SendBoardActionClear.toString())){
                    Dispatcher.clear();
                }

                if (Objects.equals(typePart, MessageType.SendFile.toString())){

                    var fileParts = contentPart.split(";");
                    var fileName = fileParts[0];
                    var mimeType = fileParts[1];
                    var base64 = fileParts[2];
                    Dispatcher.addReceivedFiles(fileName, mimeType, base64);
                }

            }
        } catch (Exception e) {
            Logger.log(e.getMessage());
        }
    }


}

