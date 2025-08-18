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
import ru.otus.l12.homework.Denomination;
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
    public void addBanknotes(Denomination denomination, int count) {

        Cell cell = cells.get(denomination.getDenomination());
        if (cell == null) {
            throw new IllegalArgumentException("Unsupported denomination denomination");
        }
        cell.add(count);
    }

    @Override
    public void takeBanknotes(int amount) throws ATMException {

        Map<Denomination, Integer> withdrawalPlan = calculateWithdrawal(amount);
        for (Map.Entry<Denomination, Integer> entry : withdrawalPlan.entrySet()) {
            Denomination denomination = entry.getKey();
            int count = entry.getValue();
            cells.get(denomination.getDenomination()).take(count);
        }
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(Cell::getTotal).sum();
    }

    @Override
    public Map<Denomination, Integer> calculateWithdrawal(int amount) throws ATMException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > getBalance()) {
            throw new ATMException("Insufficient funds");
        }

        Map<Denomination, Integer> result = new HashMap<>();
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