package org.task.taxation.rest.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TaxationRequest {
    @NotBlank(message = "TraderId must not be empty")
    private Integer traderId;
    @Min(value = 0, message = "Played Amount value is mandatory")
    private BigDecimal playedAmount;
    @Min(value = 0, message = "Odd value is mandatory")
    private BigDecimal odd;
}
