package ru.otus.l12.homework;

import ru.otus.l12.homework.exception.ATMException;

public interface Cell {

    int getCount();

    void add(int count);

    void take(int count) throws ATMException;

    int getTotal();
}
