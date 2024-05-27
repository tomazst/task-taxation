package org.task.taxation.repository;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "trader")
@Data
public class Trader implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trader_id")
    private Long traderId;

    @Column(name = "tax_value_type")
    private String taxValueType;

    @Column(name = "tax_value")
    private BigDecimal taxValue;

}
