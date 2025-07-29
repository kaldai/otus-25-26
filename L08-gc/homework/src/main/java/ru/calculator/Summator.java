package ru.calculator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Summator {
    private int sum = 0;
    private int thisVal = 0;
    private int someValue = 0;
    // !!! эта коллекция должна остаться. Заменять ее на счетчик нельзя.
    private final List<Data> listValues = new ArrayList<>();
    private final SecureRandom random = new SecureRandom();

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() % 100_000 == 0) {
            listValues.clear();
        }
        thisVal = data.getValue();
        sum += thisVal + random.nextInt();

        int sumLastThreeValues = thisVal * 3 - 3;

        for (var idx = 0; idx < 3; idx++) {
            someValue += (sumLastThreeValues * sumLastThreeValues / (thisVal + 1) - sum);
            someValue = Math.abs(someValue) + listValues.size();
        }
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return thisVal - 1;
    }

    public int getPrevPrevValue() {
        return thisVal - 2;
    }

    public int getSumLastThreeValues() {
        return thisVal * 3 - 3;
    }

    public int getSomeValue() {
        return someValue;
    }
}
