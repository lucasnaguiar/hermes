package br.dev.lucasaguiar.hermes_api.controller;

import br.dev.lucasaguiar.hermes_api.dto.request.TransferRequest;
import br.dev.lucasaguiar.hermes_api.dto.response.TransferScheduleResponse;
import br.dev.lucasaguiar.hermes_api.service.TransferSchedulingService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
public class TransferScheduleController {

    @Autowired
    protected TransferSchedulingService transferSchedulingService;

    @GetMapping("")
    public ResponseEntity<Page<TransferScheduleResponse>> index(
        @RequestParam(required = false) String sourceAccount,
        @RequestParam(required = false) String targetAccount,
        @PageableDefault(size = 10, sort = "schedulingDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
            transferSchedulingService.list(sourceAccount, targetAccount, pageable)
        );
    }

    @PostMapping("")
    public ResponseEntity<String> store(@Valid @RequestBody TransferRequest request) {
        transferSchedulingService.schedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transfer scheduled successfully");
    }
}
