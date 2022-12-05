package de.onevision.network;

import de.onevision.Platform.Exceptions.Error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class Server {
    public static Server start() throws Error {
        return start(0);
    }

    public static Server start(int port) throws Error {
        Server server = new Server();
        try {
            server.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new Error("internal error", "", "unable to start server with port " + Integer.toString(port));
        }
        server.port = port;
        return server;
    }

    public Client accept() throws Error {
        Client client = new Client();
        try {
            client.clientSocket = this.serverSocket.accept();
            client.out = new PrintWriter(client.clientSocket.getOutputStream(), true);
            client.in = new BufferedReader(new InputStreamReader(client.clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new Error("internal error", "", "unable to accept connection on port " + Integer.toString(port));
        }

        return client;
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    private Server() {
    }

    private int port = 0;
    private ServerSocket serverSocket;
}
