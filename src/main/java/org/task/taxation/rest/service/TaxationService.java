package org.task.taxation.rest.service;

import org.springframework.stereotype.Service;
import org.task.taxation.repository.Trader;
import org.task.taxation.repository.TraderRepository;
import org.task.taxation.rest.pojo.TaxationRequest;
import org.task.taxation.rest.pojo.TaxationResponse;

import java.math.BigDecimal;

@Service
public class TaxationService {
    private TraderRepository traderRepository;

    public TaxationService(TraderRepository traderRepository) {
        this.traderRepository = traderRepository;
    }

    public TaxationResponse calcTradersGeneralTax(TaxationRequest taxationRequest) {
        return calcTradersTax(TaxationTypeEnum.GENERAL, taxationRequest);
    }

    public TaxationResponse calcTradersWiningTax(TaxationRequest taxationRequest) {
        return calcTradersTax(TaxationTypeEnum.WININGS, taxationRequest);
    }

    private TaxationResponse calcTradersTax(TaxationTypeEnum taxType, TaxationRequest taxationRequest) {

        Trader trader = traderRepository.findByTraderId(taxationRequest.getTraderId());

        if (trader == null) {
            throw new RuntimeException("Trader with id " + taxationRequest.getTraderId() + " does not exist");
        }

        // Calculate taxation amounts
        TaxationResponse taxationResponse = new TaxationResponse();
        taxationResponse.setPossibleReturnAmountBefTax(taxationRequest.getPlayedAmount().multiply(taxationRequest.getOdd()));
        switch (taxType) {
            case WININGS:
                BigDecimal winings = taxationResponse.getPossibleReturnAmountBefTax().subtract(taxationRequest.getPlayedAmount());
                if (trader.getTaxValueType().equals(TaxationValueEnum.RATE.name())) {
                    taxationResponse.setTaxRate(trader.getTaxValue());
                    taxationResponse.setTaxAmount(taxPerRate(
                            winings,
                            trader.getTaxValue()
                    ));
                } else {
                    taxationResponse.setTaxAmount(trader.getTaxValue());
                    taxationResponse.setTaxRate(trader.getTaxValue().divide(winings, 6, BigDecimal.ROUND_HALF_UP));
                }
                taxationResponse.setPossibleReturnAmountAfterTax(taxationResponse.getPossibleReturnAmountBefTax().subtract(taxationResponse.getTaxAmount()));
                taxationResponse.setPossibleReturnAmount(taxationResponse.getPossibleReturnAmountBefTax().subtract(taxationResponse.getTaxAmount()));
                break;
            default:
                if (trader.getTaxValueType().equals(TaxationValueEnum.RATE.name())) {
                    taxationResponse.setTaxRate(trader.getTaxValue());
                    taxationResponse.setTaxAmount(taxPerRate(
                            taxationResponse.getPossibleReturnAmountBefTax(),
                            trader.getTaxValue()
                    ));
                } else {
                    taxationResponse.setTaxAmount(trader.getTaxValue());
                    taxationResponse.setTaxRate(trader.getTaxValue().divide(taxationResponse.getPossibleReturnAmountBefTax(), 6, BigDecimal.ROUND_HALF_UP));
                }
                taxationResponse.setPossibleReturnAmountAfterTax(taxationResponse.getPossibleReturnAmountBefTax().subtract(taxationResponse.getTaxAmount()));
                taxationResponse.setPossibleReturnAmount(taxationResponse.getPossibleReturnAmountBefTax().subtract(taxationResponse.getTaxAmount()));
                break;

        }
        return taxationResponse;
    }

    private BigDecimal taxPerRate(BigDecimal value, BigDecimal rate) {
        return value.multiply(rate.divide(new BigDecimal(100l)));
    }

}
