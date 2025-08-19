package ru.otus.l12.homework.impl;

import ru.otus.l12.homework.ATM;
import ru.otus.l12.homework.ATMStorage;
import ru.otus.l12.homework.Denomination;
import ru.otus.l12.homework.exception.ATMException;

public class ATMImpl implements ATM {

    private final ATMStorage storage;

    public ATMImpl(ATMStorage storage) {
        this.storage = storage;
    }

    @Override
    public void deposit(Denomination denomination, int count) {
        if (denomination == null) {
            throw new IllegalArgumentException("Denomination cannot be null");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        storage.addBanknotes(denomination, count);
    }

    @Override
    public void withdraw(int amount) throws ATMException {
        storage.takeBanknotes(amount);
    }

    @Override
    public int getBalance() {
        return storage.getBalance();
    }
}
