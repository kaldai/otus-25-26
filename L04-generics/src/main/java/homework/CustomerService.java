package homework;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    TreeMap<Customer, String> originalMap = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public static Map.Entry<Customer, java.lang.String> deepCopyEntry(Map.Entry<Customer, java.lang.String> entry) {
        return entry == null
            ? null
            : new AbstractMap.SimpleEntry<>(
            new Customer(
                entry.getKey().getId(),
                entry.getKey().getName(),
                entry.getKey().getScores()),
            entry.getValue());
    }

    public Map.Entry<Customer, String> getSmallest() {
        return deepCopyEntry(originalMap.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return deepCopyEntry(originalMap.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        originalMap.put(customer, data);
    }
}