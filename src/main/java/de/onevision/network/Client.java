package de.onevision.network;

import de.onevision.Platform.Exceptions.Error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public String send(String msg) throws Error {
        out.print(msg);
        try {
            String resp = in.readLine();
            return resp;
        } catch (IOException e) {
            throw new Error("internal error", "", "unable to read message");
        }
    }

    public String receive(String response) throws Error {
        try {
            String recv = in.readLine();
            out.print(response);
            return recv;
        } catch (IOException e) {
            out.print("");
            throw new Error("internal error", "", "unable to read message");
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    public Socket clientSocket;
    public PrintWriter out;
    public BufferedReader in;
}
