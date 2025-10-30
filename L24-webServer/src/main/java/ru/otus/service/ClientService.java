package ru.otus.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.ClientDto;
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

    public ClientService(
            ClientRepository clientRepository, AddressRepository addressRepository, PhoneRepository phoneRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientDto> getAllClients() {
        List<Client> clients = (List<Client>) clientRepository.findAll();
        return clients.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.map(this::toDto);
    }

    @Transactional
    public ClientDto saveClient(ClientDto clientDto) {
        // Создаем клиента
        Client client = new Client(clientDto.getName());
        if (clientDto.getId() != null) {
            client.setId(clientDto.getId());
        }

        // Сохраняем клиента
        Client savedClient = clientRepository.save(client);

        // Сохраняем адрес
        if (clientDto.getAddress() != null && !clientDto.getAddress().trim().isEmpty()) {
            Address address = new Address(clientDto.getAddress().trim(), savedClient.getId());
            Address savedAddress = addressRepository.save(address);
            savedClient.setAddress(savedAddress);
        }

        // Сохраняем телефоны
        if (clientDto.getPhones() != null && !clientDto.getPhones().isEmpty()) {
            for (String phoneNumber : clientDto.getPhones()) {
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    Phone phone = new Phone(phoneNumber.trim(), savedClient.getId());
                    phoneRepository.save(phone);
                    savedClient.getPhones().add(phone);
                }
            }
        }

        return toDto(savedClient);
    }

    @Transactional
    public void deleteClient(Long id) {
        phoneRepository.deleteByClientId(id);
        addressRepository.deleteByClientId(id);
        clientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllClients();
        }

        List<Client> clients = clientRepository.findByNameContainingIgnoreCase(searchTerm);
        return clients.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ClientDto toDto(Client client) {
        ClientDto dto = new ClientDto(client.getId(), client.getName());

        // Загружаем адрес
        if (client.getAddress() != null) {
            dto.setAddress(client.getAddress().getStreet());
        } else {
            // Если адрес не загружен автоматически, загружаем вручную
            addressRepository.findByClientId(client.getId()).ifPresent(address -> dto.setAddress(address.getStreet()));
        }

        // Загружаем телефоны
        List<String> phoneNumbers;
        if (client.getPhones() != null && !client.getPhones().isEmpty()) {
            phoneNumbers = client.getPhones().stream().map(Phone::getNumber).collect(Collectors.toList());
        } else {
            // Если телефоны не загружены автоматически, загружаем вручную
            phoneNumbers = phoneRepository.findByClientId(client.getId()).stream()
                    .map(Phone::getNumber)
                    .collect(Collectors.toList());
        }
        dto.setPhones(phoneNumbers);

        return dto;
    }
}
