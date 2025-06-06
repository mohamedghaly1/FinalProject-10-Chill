package com.chill.order.pricing;

import com.chill.order.model.MysteryBag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingEngine {
    private final Map<String, PriceStrategy> strategies;

    public PricingEngine(List<PriceStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PriceStrategy::code, s -> s));
    }
    public double calculatePrice(MysteryBag bag) {
        String strategyCode = bag.getStrategyCode();
        PriceStrategy strategy = strategies.get(strategyCode);
        return strategy.price(bag);
    }

    public boolean knows(String strategy){
        return strategies.get(strategy)!=null;
    }
}
