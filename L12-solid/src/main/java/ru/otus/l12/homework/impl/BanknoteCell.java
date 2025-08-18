package ru.otus.l12.homework.impl;

import ru.otus.l12.homework.Cell;
import ru.otus.l12.homework.exception.ATMException;

public class BanknoteCell implements Cell {

    private final int denomination;
    private int count;

    public BanknoteCell(int denomination) {
        this.denomination = denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void add(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        this.count += count;
    }

    @Override
    public void take(int count) throws ATMException {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        if (count > this.count) {
            throw new ATMException("Not enough banknotes in cell");
        }
        this.count -= count;
    }

    @Override
    public int getTotal() {
        return denomination * count;
    }
}
