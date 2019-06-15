package com.spectre.beans;

public class ClientService {

    private static ClientService instance = new ClientService();

    private ClientService() {
    }

    public static ClientService getInstance() {
        return instance;
    }
}
