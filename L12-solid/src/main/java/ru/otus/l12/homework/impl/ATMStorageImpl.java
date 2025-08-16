package ru.otus.l12.homework.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.NonNull;
import ru.otus.l12.homework.ATMStorage;
import ru.otus.l12.homework.Banknote;
import ru.otus.l12.homework.Cell;
import ru.otus.l12.homework.exception.ATMException;

@Getter
public class ATMStorageImpl implements ATMStorage {

    private final Map<Integer, Cell> cells = new TreeMap<>(Comparator.reverseOrder());

    public ATMStorageImpl(@NonNull Collection<Integer> denominations) {

        for (int denomination : denominations) {
            cells.put(denomination, new BanknoteCell(denomination));
        }
    }

    @Override
    public void addBanknotes(Banknote banknote, int count) {

        Cell cell = cells.get(banknote.getDenomination());
        if (cell == null) {
            throw new IllegalArgumentException("Unsupported banknote denomination");
        }
        cell.add(count);
    }

    @Override
    public void takeBanknotes(int amount) throws ATMException {

        Map<Banknote, Integer> withdrawalPlan = calculateWithdrawal(amount);
        for (Map.Entry<Banknote, Integer> entry : withdrawalPlan.entrySet()) {
            Banknote banknote = entry.getKey();
            int count = entry.getValue();
            cells.get(banknote.getDenomination()).take(count);
        }
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(Cell::getTotal).sum();
    }

    @Override
    public Map<Banknote, Integer> calculateWithdrawal(int amount) throws ATMException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > getBalance()) {
            throw new ATMException("Insufficient funds");
        }

        Map<Banknote, Integer> result = new HashMap<>();
        int remaining = amount;

        Iterator<Map.Entry<Integer, Cell>> iterator = cells.entrySet().iterator();
        while (iterator.hasNext() && remaining > 0) {
            Map.Entry<Integer, Cell> entry = iterator.next();
            int denomination = entry.getKey();
            Cell cell = entry.getValue();

            if (denomination <= remaining) {
                int count = Math.min(cell.getCount(), remaining / denomination);
                if (count > 0) {
                    result.put(() -> denomination, count);
                    remaining -= denomination * count;
                }
            }
        }

        if (remaining > 0) {
            throw new ATMException("Cannot dispense requested amount with available banknotes");
        }

        return result;
    }
}
