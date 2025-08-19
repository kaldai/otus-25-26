package ru.otus.l12.homework;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.l12.homework.exception.ATMException;
import ru.otus.l12.homework.impl.ATMImpl;
import ru.otus.l12.homework.impl.ATMStorageImpl;
import ru.otus.l12.homework.impl.RubleDenomination;

public class ATMDemo {

    private static final Logger logger = LoggerFactory.getLogger(ATMDemo.class);

    public static void main(String[] args) {
        // Инициализация банкомата с поддержкой номиналов
        List<Integer> denominations = Arrays.stream(RubleDenomination.values())
                .map(RubleDenomination::getDenomination)
                .toList();
        ATMStorage storage = new ATMStorageImpl(denominations);
        ATM atm = new ATMImpl(storage);

        // Внесение денег
        atm.deposit(RubleDenomination.ONE_HUNDRED, 10);
        atm.deposit(RubleDenomination.TWO_HUNDRED, 10);
        atm.deposit(RubleDenomination.FIVE_HUNDRED, 10);
        atm.deposit(RubleDenomination.ONE_THOUSAND, 10);
        atm.deposit(RubleDenomination.TWO_THOUSAND, 10);
        atm.deposit(RubleDenomination.FIVE_THOUSAND, 10);

        logger.info("Balance after deposit: {} RUB", atm.getBalance());

        // Снятие денег
        doWithdraw(atm, 3500);
        doWithdraw(atm, 75000);
        doWithdraw(atm, 7500);
        doWithdraw(atm, 3500);

        logger.info("Final balance: {} RUB", atm.getBalance());
    }

    private static void doWithdraw(ATM atm, int amount) {
        try {
            atm.withdraw(amount);
            logger.info("Withdrawal {} successful", amount);
        } catch (ATMException e) {
            logger.error("Withdrawal {} failed: {}", amount, e.getMessage());
        }
        logger.info("Current balance: {} RUB", atm.getBalance());
    }
}
