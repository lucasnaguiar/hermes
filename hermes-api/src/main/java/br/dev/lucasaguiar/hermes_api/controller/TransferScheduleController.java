package br.dev.lucasaguiar.hermes_api.controller;

import br.dev.lucasaguiar.hermes_api.dto.request.TransferRequest;
import br.dev.lucasaguiar.hermes_api.service.TransferSchedulingService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
public class TransferScheduleController {

    @Autowired
    protected TransferSchedulingService transferSchedulingService;

    @PostMapping("")
    public ResponseEntity<String> store(@Valid @RequestBody TransferRequest request) {
        transferSchedulingService.schedule(request);
        return ResponseEntity.ok("Hello");
    }
}
