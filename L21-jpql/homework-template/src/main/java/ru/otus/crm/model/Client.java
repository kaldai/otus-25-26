package ru.otus.crm.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        setAddress(address);
        setPhones(phones);
    }

    public void setAddress(Address address) {
        if (address != null) {
            address.setClient(this);
        }
        this.address = address;
    }

    public void setPhones(List<Phone> phones) {
        this.phones.clear();
        if (phones != null) {
            phones.forEach(phone -> phone.setClient(this));
            this.phones.addAll(phones);
        }
    }

    public void addPhone(Phone phone) {
        if (phone != null) {
            phone.setClient(this);
            this.phones.add(phone);
        }
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address clonedAddress = null;
        if (this.address != null) {
            clonedAddress = new Address(this.address.getId(), this.address.getStreet());
        }

        List<Phone> clonedPhones = null;
        if (this.phones != null) {
            clonedPhones = this.phones.stream()
                    .map(phone -> new Phone(phone.getId(), phone.getNumber()))
                    .toList();
        }

        return new Client(this.id, this.name, clonedAddress, clonedPhones);
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", address=" + address + ", phones=" + phones + '}';
    }
}
