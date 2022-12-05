package de.onevision.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.onevision.Platform.Exceptions.Error;

public class Connection {
    public static Connection connect(Host host) throws Error {
        Connection connection = new Connection();
        try {
            connection.clientSocket = new Socket(host.ipAsString(), host.port);
            connection.out = new PrintWriter(connection.clientSocket.getOutputStream(), true);
            connection.in = new BufferedReader(new InputStreamReader(connection.clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new Error("internal error", "",
                    "cannot connect to host " + host.ipAsString() + ":" + Integer.toString(host.port));
        }

        return connection;
    }

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

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
}
