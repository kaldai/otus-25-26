package ru.otus.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.AddressRepository;
import ru.otus.repository.ClientRepository;
import ru.otus.repository.PhoneRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public ClientService(
            ClientRepository clientRepository, AddressRepository addressRepository, PhoneRepository phoneRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
    }

    public List<Client> getAllClients() {
        return (List<Client>) clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client createClient(Client client) {
        for (Address address : client.getAddresses()) {
            address.setClient(client);
        }
        for (Phone phone : client.getPhones()) {
            phone.setClient(client);
        }
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository
                .findById(id)
                .map(client -> {
                    client.setName(updatedClient.getName());

                    // Обновляем адреса
                    client.getAddresses().clear();
                    for (Address addr : updatedClient.getAddresses()) {
                        addr.setClient(client);
                        client.getAddresses().add(addr);
                    }

                    // Обновляем телефоны
                    client.getPhones().clear();
                    for (Phone ph : updatedClient.getPhones()) {
                        ph.setClient(client);
                        client.getPhones().add(ph);
                    }

                    return clientRepository.save(client);
                })
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
}
