package ru.otus.l12.homework.impl;

import ru.otus.l12.homework.Denomination;

public enum RubleDenomination implements Denomination {
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000);

    private final int denomination;

    RubleDenomination(int denomination) {
        this.denomination = denomination;
    }

    @Override
    public int getDenomination() {
        return denomination;
    }
}
