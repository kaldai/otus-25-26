package ru.otus.l12.homework;

import ru.otus.l12.homework.exception.ATMException;

public interface ATM {

    void deposit(Banknote banknote, int count);

    void withdraw(int amount) throws ATMException;

    int getBalance();
}
