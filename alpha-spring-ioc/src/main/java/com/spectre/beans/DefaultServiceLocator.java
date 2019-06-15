package com.spectre.beans;

import com.spectre.beans.service.AccountService;
import com.spectre.beans.service.impl.AccountServiceImpl;

public class DefaultServiceLocator {

    private static ClientService clientService = ClientService.getInstance();

    private static AccountService accountService = new AccountServiceImpl();

    public ClientService getClientService() {
        return clientService;
    }

    public AccountService getAccountService() {
        return accountService;
    }
}
