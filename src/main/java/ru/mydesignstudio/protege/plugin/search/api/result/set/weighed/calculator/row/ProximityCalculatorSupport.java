package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

/**
 * Created by abarmin on 28.05.17.
 *
 * Служебные функции для работы калькулятора близости
 */
public abstract class ProximityCalculatorSupport implements ProximityCalculator {
    private final OWLService owlService;
    private final FuzzyOWLService fuzzyOWLService;

    @Inject
    public ProximityCalculatorSupport(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		this.owlService = owlService;
		this.fuzzyOWLService = fuzzyOWLService;
	}

	/**
     * Получить вес указанного свойства. Если вес не должен учитываться, возвращает 1
     * @param property - свойство, для которого необходимо узать вес
     * @param usePropertyWeight - использовать ли вес при расчете
     * @return - вес
     * @throws ApplicationException
     */
    public double getPropertyWeight(@SuppressWarnings("rawtypes") OWLProperty property, 
    		boolean usePropertyWeight) throws ApplicationException {
        if (!usePropertyWeight) {
            return 1;
        }
        return fuzzyOWLService.getPropertyWeigth(property);
    }

    /**
     * Значение указанного свойства у переданного экземпляра
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - значение свойства
     * @throws ApplicationException
     */
    public Object getPropertyValue(OWLIndividual individual, 
    		@SuppressWarnings("rawtypes") OWLProperty property) throws ApplicationException {
        return owlService.getPropertyValue(individual, property);
    }

    /**
     * Является ли переданное свойство указанного экземпляра строковым значением
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - является или нет
     * @throws ApplicationException
     */
	public boolean isStringProperty(OWLIndividual individual, 
			@SuppressWarnings("rawtypes") OWLProperty property) throws ApplicationException {
        if (!(property instanceof OWLDataProperty)) {
            return false;
        }
        final Object propertyValue = getPropertyValue(individual, property);
        if (!(propertyValue instanceof OWLLiteralImplString)) {
            return false;
        }
        return true;
    }

    /**
     * Получить свойство указанного экземпляра
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - значение свойства
     * @throws ApplicationException - если свойство не является строковым
     */
    public String getPropertyAsString(OWLIndividual individual, 
    		@SuppressWarnings("rawtypes") OWLProperty property) throws ApplicationException {
        if (!isStringProperty(individual, property)) {
            throw new ApplicationException(String.format(
                    "Property %s is not string property",
                    property.getIRI().getFragment()
            ));
        }
        final Object propertyValue = getPropertyValue(individual, property);
        return ((OWLLiteralImplString) propertyValue).getLiteral();
    }

    /**
     * Количество общих букв в двух строках, которые идут по порядку
     * @param first - между этой строкой
     * @param second - и вот этой
     * @return - число общих букв
     * @throws ApplicationException
     */
    public int getCommonSymbols(String first, String second) throws ApplicationException {
    		final String firstPrepared = first.toLowerCase();
    		final String secondPrepared = second.toLowerCase();
        for (int lastIndex = second.length(); lastIndex > 0; lastIndex--) {
        		final String substring = secondPrepared.substring(0, lastIndex);
        		int count = 0;
        		int startFrom = 0;
        		while (firstPrepared.indexOf(substring, startFrom) > -1) {
        			count = count + substring.length();
        			if (substring.length() == 1) {
        				return count;
        			}
        			startFrom = firstPrepared.indexOf(substring, startFrom) + substring.length() + 1;
        		}
        		if (count > 0) {
        			return count;
        		}
        }
        return 0;
    }
}
