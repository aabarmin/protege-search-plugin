package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 24.06.17.
 *
 * Компонент создания запросов методом эквивалентных классов
 */
@Component
public class EqualClassesRelatedQueriesCreator extends NearestNeighboursRelatedQueriesCreator implements RelatedQueriesCreator{
	@Inject
	public EqualClassesRelatedQueriesCreator(OWLService owlService, OwlClassHierarchyBuilder hierarchyBuilder) {
		super(owlService, hierarchyBuilder);
	}

	@Override
    public Collection<SelectQuery> create(SelectQuery initialQuery,
                                          SearchProcessorParams searchProcessorParams) throws ApplicationException {

        Validation.assertNotNull("Initial query not provided", initialQuery);
        Validation.assertNotNull("Search processor params not provided", searchProcessorParams);

        final Collection<SelectQuery> relatedQueries = new ArrayList<>();
        /**
         * получим класс, по которому ищем
         */
        final OWLClass defaultFromClass = initialQuery.getFrom().getOwlClass();
        /**
         * найдем все эквивалентные классы
         */
        final Collection<OWLClass> equalClasses = getOwlService().getEqualClasses(defaultFromClass);
        for (OWLClass equalClass : equalClasses) {
            /**
             * создадим запрос
             */
            final OWLDomainClass domainClass = new OWLDomainClass(equalClass);
            relatedQueries.add(createRelatedQuery(domainClass, initialQuery));
        }
        /**
         * все, на выход
         */
        return relatedQueries;
    }
}
