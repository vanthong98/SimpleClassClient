package com.hw.core;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Objects;

public class ClientSocketOutputHandler extends Thread {

    OutputStream _socketOutputStream;

    public ClientSocketOutputHandler(OutputStream socketOutputStream) {
        _socketOutputStream  = socketOutputStream;
    }

    public void run() {
        PrintWriter writer = new PrintWriter(_socketOutputStream, true);

        String message;
        do {
            message = Dispatcher.APPLICATION_QUEUE.poll();

            if (message == null) {
                continue;
            }

            writer.println(message + "\n");

            Logger.log("Sent: " + message);


        }while (!Objects.equals(Common.ExitMessage, message));
    }
}
