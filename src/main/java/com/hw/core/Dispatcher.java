package com.hw.core;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher {
    public static final PriorityBlockingQueue<String> APPLICATION_QUEUE = new PriorityBlockingQueue<>();
    private static TextArea _classMessageTextArea;
    private static Canvas _canvas;
    private static ObservableList<String> _fileNames;
    private static final HashMap<String, CommonFile> _files = new HashMap<>();
    private static Color penColor = Color.PURPLE;
    public static void init(TextArea classMessageTextArea, Canvas canvas, ObservableList<String> fileNames) {
        _classMessageTextArea = classMessageTextArea;
        _canvas = canvas;
        _fileNames = fileNames;
    }

    public static void sendToClassMessage(String sender, String message) {
        Platform.runLater(()-> _classMessageTextArea.appendText(sender + " (" +  Common.getCurrentTime() + ")" + ":" + Common.NewLine + message + Common.NewLine));
    }

    public static void send(String message){
        synchronized (APPLICATION_QUEUE){
            APPLICATION_QUEUE.add(message);
        }
    }

    public static void addReceivedFiles(String fileName, String mimeType, String base64) {
        synchronized (_files){
            if (_fileNames != null) {
                _files.put(fileName, new CommonFile (fileName, mimeType, base64));
                Platform.runLater(()-> _fileNames.add(fileName));
            }
        }
    }

    public static CommonFile getReceivedFile(String fileName) {
        synchronized (_files){
           return _files.get(fileName);
        }
    }

    public static void startDrawing(String contentPart) {
        var coordinate = contentPart.split(";");
        var x = coordinate[0];
        var y = coordinate[1];

        Platform.runLater(() -> {

            var context = _canvas.getGraphicsContext2D();
            context.beginPath();
            context.moveTo(Double.parseDouble(x), Double.parseDouble(y));
            context.stroke();
        });

        Logger.log("Start drawing line...");
    }

    public static void draw(String contentPart) {
        var coordinate = contentPart.split(";");
        var x = coordinate[0];
        var y = coordinate[1];

        Platform.runLater(() -> {
            var context = _canvas.getGraphicsContext2D();
            context.lineTo(Double.parseDouble(x), Double.parseDouble(y));
            context.stroke();
        });
    }

    public static void clear() {
        Platform.runLater(() -> {
            var context = _canvas.getGraphicsContext2D();
            context.setFill(Color.WHITESMOKE); // Set the fill color to white (or any color you desire)
            context.fillRect(1, 1, context.getCanvas().getWidth() -2, context.getCanvas().getHeight()-2);
        });
    }

    public static void changePenColor(String contentPart) {
        Platform.runLater(() -> {
            var context = _canvas.getGraphicsContext2D();
            context.setStroke(Color.web(contentPart)); // Set the fill color to white (or any color you desire)
        });
    }
}

