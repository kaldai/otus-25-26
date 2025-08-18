package ru.otus.l12.homework;

import java.util.Map;
import ru.otus.l12.homework.exception.ATMException;

public interface ATMStorage {

    void addBanknotes(Denomination denomination, int count);

    void takeBanknotes(int amount) throws ATMException;

    int getBalance();

    Map<Denomination, Integer> calculateWithdrawal(int amount) throws ATMException;
}