package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.weight.calculator;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator.RowWeightCalculatorSupport;

/**
 * Created by abarmin on 28.05.17.
 *
 * Calculate row weight during fuzzy search
 */
public class FuzzyRowWeightCalculator extends RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    public FuzzyRowWeightCalculator(SelectQuery selectQuery, FuzzyAttributiveProcessorParams processorParams) {
        super(selectQuery, processorParams);
    }

    @Override
    public boolean usePropertyWeights(SearchProcessorParams processorParams) throws ApplicationException {
        return false;
    }
}
