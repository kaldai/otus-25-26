package ru.otus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.Client;
import ru.otus.service.ClientService;

@Controller
public class ClientWebController {

    private final ClientService clientService;

    public ClientWebController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/clients")
    public String showClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients";
    }

    @GetMapping("/client/new")
    public String createForm(Model model) {
        model.addAttribute("client", new Client());
        return "client-form";
    }

    @PostMapping("/client/save")
    public String saveClient(@ModelAttribute Client client) {
        clientService.createClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/client/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id).orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "client-form";
    }
}
