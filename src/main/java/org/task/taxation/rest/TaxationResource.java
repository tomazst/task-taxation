package org.task.taxation.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.task.taxation.rest.pojo.TaxationRequest;
import org.task.taxation.rest.pojo.TaxationResponse;
import org.task.taxation.rest.service.TaxationService;

@RestController
@RequestMapping("/api/tax")
public class TaxationResource {

    private final TaxationService taxationService;

    public TaxationResource(TaxationService taxationService) {
        this.taxationService = taxationService;
    }

    @PostMapping("/general")
    public ResponseEntity<TaxationResponse> generalTaxation(@RequestBody TaxationRequest taxationRequest) {
        return ResponseEntity.ok(taxationService.calcTradersGeneralTax(taxationRequest));
    }

    @PostMapping("/winings")
    public ResponseEntity<TaxationResponse> winingsTaxation(@RequestBody TaxationRequest taxationRequest) {
        return ResponseEntity.ok(taxationService.calcTradersWiningTax(taxationRequest));
    }

}
