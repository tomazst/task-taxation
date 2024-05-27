package org.task.taxation.rest.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TaxationResponse {
    private BigDecimal possibleReturnAmount;
    private BigDecimal possibleReturnAmountBefTax;
    private BigDecimal possibleReturnAmountAfterTax;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
}
