package server;

import manager.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }
}