package ru.mydesignstudio.protege.plugin.search.service.owl;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.empty.EmptyResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.DataTypeUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyImpl;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by abarmin on 03.01.17.
 *
 * ?????????? ?????????????? ???????????????? ???????? ??????????:
 * https://github.com/phillord/owl-api/blob/master/contract/src/test/java/org/coode/owlapi/examples/Examples.java
 */
public class OWLServiceImpl implements OWLService {
    @Inject
    private OwlClassHierarchyBuilder hierarchyBuilder;

    @Override
    public OWLOntology getOntology() throws ApplicationException {
        return OntologyConfig.getOntology();
    }

    protected OWLModelManager getModelManager() throws ApplicationException {
        return OntologyConfig.getModelManager();
    }

    @Override
    public Collection<OWLClass> getClasses() throws ApplicationException {
        return getOntology().getClassesInSignature();
    }

    @Override
    public Collection<OWLObjectProperty> getObjectProperties(OWLClass owlClass) throws ApplicationException {
        final Collection<OWLObjectProperty> properties = new ArrayList<>();
        final Set<OWLObjectPropertyDomainAxiom> axioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN);
        for (OWLObjectPropertyDomainAxiom axiom : axioms) {
            if (axiom.getDomain().equals(owlClass)) {
                properties.addAll(axiom.getObjectPropertiesInSignature());
            }
        }
        return properties;
    }

    @Override
    public Collection<OWLDataProperty> getDataProperties(OWLClass owlClass) throws ApplicationException {
        final Collection<OWLDataProperty> properties = new ArrayList<>();
        final Set<OWLDataPropertyDomainAxiom> axioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
        for (OWLDataPropertyDomainAxiom axiom : axioms) {
            if (axiom.getDomain().equals(owlClass)) {
                properties.addAll(axiom.getDataPropertiesInSignature());
            }
        }
        return properties;
    }

    @Override
    public Collection<OWLPropertyRange> getPropertyRanges(OWLProperty owlProperty) throws ApplicationException {
        final Collection<OWLPropertyRange> ranges = new ArrayList<>();
        final Set<OWLDataPropertyRangeAxiom> dataRangeAxioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_RANGE);
        for (OWLDataPropertyRangeAxiom dataRangeAxiom : dataRangeAxioms) {
            if (dataRangeAxiom.getSignature().contains(owlProperty)) {
                ranges.add(dataRangeAxiom.getRange());
            }
        }
        final Set<OWLObjectPropertyRangeAxiom> objectRangeAxioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_RANGE);
        for (OWLObjectPropertyRangeAxiom objectRangeAxiom : objectRangeAxioms) {
            if (objectRangeAxiom.getSignature().contains(owlProperty)) {
                ranges.add(objectRangeAxiom.getRange());
            }
        }
        return ranges;
    }

    @Override
    public Collection<OWLNamedIndividual> getIndividuals(OWLClass owlClass) throws ApplicationException {
        final Set<OWLClassAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.CLASS_ASSERTION);
        return CollectionUtils.map(CollectionUtils.filter(axioms, new Specification<OWLClassAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLClassAssertionAxiom axiom) {
                return axiom.getClassesInSignature().contains(owlClass);
            }
        }), new Transformer<OWLClassAssertionAxiom, OWLNamedIndividual>() {
            @Override
            public OWLNamedIndividual transform(OWLClassAssertionAxiom item) {
                return (OWLNamedIndividual) item.getIndividual();
            }
        });
    }

    @Override
    public ResultSet search(Collection<LookupParam> params) throws ApplicationException {
        /**
         * ?????????????? ???????????? ???? ???????????????? ?????????? ????????????
         */
        SelectQuery selectQuery = null;
        for (LookupParam param : params) {
            final SearchStrategy strategy = param.getStrategy();
            final SearchProcessor processor = strategy.getSearchProcessor();
            selectQuery = processor.prepareQuery(selectQuery, param.getStrategyParams());
        }
        /**
         * ?????????????????? ???????????????????????????????? ?????????? ?????????????? ??????????????????????
         * ???? ???????????? ????????????????????, ?????????????????? ?? ????????????????????
         */
        ResultSet resultSet = new EmptyResultSet();
        for (LookupParam param : params) {
            final SearchStrategy strategy = param.getStrategy();
            final SearchProcessor processor = strategy.getSearchProcessor();
            resultSet = processor.collect(resultSet, selectQuery, param.getStrategyParams());
        }
        return resultSet;
    }

    @Override
    public OWLClass getParentClass(OWLClass child) throws ApplicationException {
        final Set<OWLSubClassOfAxiom> axioms = getOntology().getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom axiom : axioms) {
            for (OWLClass childClass : axiom.getSubClass().getClassesInSignature()) {
                if (child.equals(childClass)) {
                    final Set<OWLClass> parentClasses = axiom.getSuperClass().getClassesInSignature();
                    return parentClasses.isEmpty() ?
                            null :
                            parentClasses.iterator().next();
                }
            }
        }
        return null;
    }

    @Override
    public Collection<OWLClass> getChildrenClasses(OWLClass parent) throws ApplicationException {
        final Set<OWLSubClassOfAxiom> axioms = getOntology().getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom axiom : axioms) {
            for (OWLClass owlClass : axiom.getSuperClass().getClassesInSignature()) {
                if (parent.equals(owlClass)) {
                    return axiom.getSubClass().getClassesInSignature();
                }
            }
        }
        return null;
    }

    @Override
    public OWLIndividual getIndividual(final IRI uri) throws ApplicationException {
        final Set<OWLNamedIndividual> owlNamedIndividuals = getOntology().getIndividualsInSignature();
        return CollectionUtils.findFirst(owlNamedIndividuals, new Specification<OWLNamedIndividual>() {
            @Override
            public boolean isSatisfied(OWLNamedIndividual individual) {
                return individual.getIRI().equals(uri);
            }
        });
    }

    @Override
    public OWLClass getOWLClass(final IRI iri) throws ApplicationException {
        final Set<OWLClass> classesInSignature = getOntology().getClassesInSignature();
        return CollectionUtils.findFirst(classesInSignature, new Specification<OWLClass>() {
            @Override
            public boolean isSatisfied(OWLClass owlClass) {
                return OWLUtils.equals(owlClass.getIRI(), iri);
            }
        });
    }

    @Override
    public OWLProperty getProperty(IRI iri) throws ApplicationException {
        final Set<OWLObjectProperty> objectProperties = getOntology().getObjectPropertiesInSignature();
        final OWLObjectProperty objectProperty = CollectionUtils.findFirst(objectProperties, new Specification<OWLObjectProperty>() {
            @Override
            public boolean isSatisfied(OWLObjectProperty property) {
                return OWLUtils.equals(property.getIRI(), iri);
            }
        });
        if (objectProperty != null) {
            return objectProperty;
        }
        final Set<OWLDataProperty> dataProperties = getOntology().getDataPropertiesInSignature();
        return CollectionUtils.findFirst(dataProperties, new Specification<OWLDataProperty>() {
            @Override
            public boolean isSatisfied(OWLDataProperty property) {
                return OWLUtils.equals(property.getIRI(), iri);
            }
        });
    }

    /**
     * ?????????????? ???????????????? ?????????????? ?????????????????? ????????????????
     * @param individual - ????????????????
     * @param property - ????????????????
     * @return - ?????????????? "???????????????? ??????????????????"
     * @throws ApplicationException
     */
    private OWLPropertyAssertionAxiom getPropertyAssertionAxiom(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        // @TODO ?????????????????? ?????? ??????????????????. ?????? ?? ???????? ???? ???????????????????? ???????????????????????? ??????????
        if (property instanceof OWLObjectProperty) {
            final Set<OWLObjectPropertyAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
            final Collection<OWLObjectPropertyAssertionAxiom> subjectAxioms = CollectionUtils.filter(axioms, new Specification<OWLObjectPropertyAssertionAxiom>() {
                @Override
                public boolean isSatisfied(OWLObjectPropertyAssertionAxiom axiom) {
                    return axiom.getSubject().equals(individual) && axiom.getProperty().equals(property);
                }
            });
            if (CollectionUtils.isNotEmpty(subjectAxioms)) {
                return subjectAxioms.iterator().next();
            }
        } else if (property instanceof OWLDataProperty) {
            final Set<OWLDataPropertyAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
            final Collection<OWLDataPropertyAssertionAxiom> subjectAxioms = CollectionUtils.filter(axioms, new Specification<OWLDataPropertyAssertionAxiom>() {
                @Override
                public boolean isSatisfied(OWLDataPropertyAssertionAxiom axiom) {
                    return axiom.getSubject().equals(individual) && axiom.getProperty().equals(property);
                }
            });
            if (CollectionUtils.isNotEmpty(subjectAxioms)) {
                return subjectAxioms.iterator().next();
            }
        }
        return null;
    }

    @Override
    public Object getPropertyValue(OWLIndividual individual, String propertyName) throws ApplicationException {
        final Set<OWLDataPropertyAssertionAxiom> dataAxioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
        final Collection<OWLDataPropertyAssertionAxiom> dataIndividualAxioms = CollectionUtils.filter(dataAxioms, new Specification<OWLDataPropertyAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLDataPropertyAssertionAxiom axiom) {
                return OWLUtils.equals(axiom.getSubject(), individual) &&
                        StringUtils.equalsIgnoreCase(
                                axiom.getProperty().asOWLDataProperty().getIRI().getFragment(),
                                propertyName
                        );
            }
        });
        if (CollectionUtils.isNotEmpty(dataIndividualAxioms)) {
            return dataIndividualAxioms.iterator().next().getObject();
        }
        final Set<OWLObjectPropertyAssertionAxiom> objectAxioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
        final Collection<OWLObjectPropertyAssertionAxiom> objectIndividualAxioms = CollectionUtils.filter(objectAxioms, new Specification<OWLObjectPropertyAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLObjectPropertyAssertionAxiom axiom) {
                return OWLUtils.equals(axiom.getSubject(), individual) &&
                        StringUtils.equalsIgnoreCase(
                                axiom.getProperty().asOWLObjectProperty().getIRI().getFragment(),
                                propertyName
                        );
            }
        });
        if (CollectionUtils.isNotEmpty(objectIndividualAxioms)) {
            return objectIndividualAxioms.iterator().next().getObject();
        }
        return null;
    }

    @Override
    public Object getPropertyValue(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        final OWLPropertyAssertionAxiom propertyAssertionAxiom = getPropertyAssertionAxiom(individual, property);
        if (propertyAssertionAxiom == null) {
            return null;
        }
        return propertyAssertionAxiom.getObject();
    }

    @Override
    public Collection<OWLClass> getIndividualClasses(OWLIndividual individual) throws ApplicationException {
        final Set<OWLClassAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.CLASS_ASSERTION);
        final Collection<OWLClassAssertionAxiom> classAxioms = CollectionUtils.filter(axioms, new Specification<OWLClassAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLClassAssertionAxiom axiom) {
                return OWLUtils.equals(
                        individual,
                        axiom.getIndividual()
                );
            }
        });
        return CollectionUtils.flatMap(classAxioms, new Transformer<OWLClassAssertionAxiom, Collection<OWLClass>>() {
            @Override
            public Collection<OWLClass> transform(OWLClassAssertionAxiom item) {
                return item.getClassesInSignature();
            }
        });
    }

    @Override
    public OWLClass getIndividualClass(OWLIndividual individual) throws ApplicationException {
        /**
         * ?????????????? ?????? ????????????, ?? ?????????????? ?????????????????? individual ???? ???????????????? ????????????. ?????????? ?????????????????? ?????????????????????? ?? ??????,
         * ?????????? ?????????? ?????????? ?????????? ???????????? ???? ????????????????
         */
        final Collection<OWLClass> classes = getIndividualClasses(individual);
        /**
         * ???????????? ?????????? ?????????? ?????????? - ?????????????????? ?????????? ???????????????? ???? ???????????????? ???????????? ???? ?????????????? ????????????????. ?????????? ??
         * ?????????? ?????????????? ?????????? ?? ???????????????? ???????????????? ???????????????????? ?????????????? individual-??
         */
        OWLClass suitableClass = null;
        int length = 0;
        for (OWLClass owlClass : classes) {
            final Collection<OWLClass> path = hierarchyBuilder.build(owlClass);
            if (path.size() > length) {
                length = path.size();
                suitableClass = owlClass;
            }
        }
        return suitableClass;
    }

    @Override
    public OWLLiteral getLiteral(String value) throws ApplicationException {
        return new OWLLiteralImplString(value);
    }

    @Override
    public void setPropertyValue(OWLIndividual individual, OWLProperty property, Object value) throws ApplicationException {
        final OWLModelManager manager = getModelManager();
        final OWLDataFactory factory = manager.getOWLDataFactory();
        /**
         * ?????????????? ?????????????? ???????????????? ????????????????
         */
        final OWLPropertyAssertionAxiom assertionAxiom = getPropertyAssertionAxiom(individual, property);
        if (assertionAxiom != null) {
            /**
             * ?????? ???????????????? ?????? ????????, ???????? ????????????????.
             * ?????????????? ???????????? ???????????? ????????????????
             */
            final RemoveAxiom removeAxiom = new RemoveAxiom(getOntology(), assertionAxiom);
            manager.applyChange(removeAxiom);
        }
        /**
         * ?????????????? ???????????? ?????????????? ?? ?????????????????????? ???? ???????? ??????????????
         */
        if (property instanceof OWLDataProperty) {
            /**
             * ???????????????? ???????????????? ???? ??????????????????, ???????? ?????????????? ???????? ??????????
             */
            final OWLDataProperty dataProperty = factory.getOWLDataProperty(property.getIRI());
            final OWLNamedIndividual namedIndividual = (OWLNamedIndividual) individual;
            /**
             * ???????????????? ?????????????? "???????????????? ????????????????"
             */
            final OWLDataPropertyAssertionAxiom newAssertionAxiom;
            if (DataTypeUtils.isInteger(value)) {
                newAssertionAxiom = factory.getOWLDataPropertyAssertionAxiom(dataProperty, namedIndividual, (int) value);
            } else {
                throw new ApplicationException(String.format(
                        "Unsupported data property value type %s",
                        value.getClass()
                ));
            }
            /**
             * ????????????????, ???? ?????????? ???? ???????????????? ???????????????? ??????????????
             */
            final Collection<OWLClass> classes = getIndividualClasses(individual);
            final Set<OWLDataPropertyDomainAxiom> domainAxioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
            final Collection<OWLDataPropertyDomainAxiom> propertyAxioms = CollectionUtils.filter(domainAxioms, new Specification<OWLDataPropertyDomainAxiom>() {
                @Override
                public boolean isSatisfied(OWLDataPropertyDomainAxiom axiom) {
                    return OWLUtils.equals(
                            axiom.getProperty().asOWLDataProperty(),
                            dataProperty
                    );
                }
            });
            /**
             * ???????????? ??????, ?? ???????????????? ???? ??????????????
             */
            final Collection<OWLClass> notLinkedClasses = CollectionUtils.filter(classes, new Specification<OWLClass>() {
                @Override
                public boolean isSatisfied(OWLClass owlClass) {
                    return !CollectionUtils.some(propertyAxioms, new Specification<OWLDataPropertyDomainAxiom>() {
                        @Override
                        public boolean isSatisfied(OWLDataPropertyDomainAxiom axiom) {
                            return OWLUtils.equals(
                                    axiom.getDomain().asOWLClass().getIRI(),
                                    owlClass.getIRI()
                            );
                        }
                    });
                }
            });
            /**
             * ???????????? ?? ???????? ????????
             */
            for (OWLClass targetClass : notLinkedClasses) {
                final OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(dataProperty, targetClass);
                manager.applyChange(new AddAxiom(getOntology(), domainAxiom));
            }
            /**
             * ???????????????? ?????? ???????????????????? ??????????????
             */
            final AddAxiom addAxiom = new AddAxiom(getOntology(), newAssertionAxiom);
            manager.applyChange(addAxiom);
        } else {
            throw new ApplicationException("Setting values of object properties is not implemented yet");
        }
    }

    @Override
    public OWLProperty createDataProperty(Collection<OWLClass> targetClasses, String propertyName, Type propertyType) throws ApplicationException {
        final OWLModelManager manager = getModelManager();
        final OWLDataFactory factory = manager.getOWLDataFactory();
        /**
         * ?????????????? ????????????????. ?????? ???????? ???? ?? ???????? ???? ??????????????????, ???????? ???? ????????
         */
        final OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(
                getOntologyIRI() + "#" + propertyName
        ));
        /**
         * ?????????????????????? ???????????????? ?? ??????????????, ???????? ?????? ?????????? ?????????? ???????? ??????????
         */
        for (OWLClass targetClass : targetClasses) {
            final OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(dataProperty, targetClass);
            manager.applyChange(new AddAxiom(getOntology(), domainAxiom));
        }
        /**
         * ???????????? ?????????????????? ?????? ????????????
         */
        final OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(dataProperty, getPropertyRange(propertyType));
        manager.applyChange(new AddAxiom(getOntology(), rangeAxiom));
        /**
         * ????????????
         */
        return dataProperty;
    }

    /**
     * DataRange ?????? ?????????? ???????????? Java
     *
     * @param propertyType - ?????? ???????????? Java
     * @return - DataRange ???? OWL
     * @throws ApplicationException - ???????? ?????? ?????????????????? ???????????????????? ???????? ????????????
     */
    private OWLDataRange getPropertyRange(Type propertyType) throws ApplicationException {
        final OWLModelManager manager = getModelManager();
        final OWLDataFactory factory = manager.getOWLDataFactory();
        if (propertyType == Integer.class) {
            return factory.getIntegerOWLDatatype();
        }
        throw new ApplicationException(String.format(
                "Support for type %s is not implemented",
                propertyType
        ));
    }

    /**
     * ???????????????? ?????????????????? ?????????????????????????? IRI ??????????????????
     * ?????????????????????? ?????????? Reflection, ?????? ?????? ?????????? ?????? ???? ????????????????????
     * ???????????? ??????????, ???????? ?????????? ???????????????? ???????????? ?????? ?????? ??????????????
     * @return - ?????????????????? IRI ??????????????????.
     * @throws ApplicationException
     */
    private String getOntologyIRI() throws ApplicationException {
        // TODO: 04.06.17 ?????? ??????????-???? ???????????????????? ????????????. ????????????????????
        final ConcurrentOWLOntologyImpl concurrentOWLOntology = (ConcurrentOWLOntologyImpl) getOntology();
        final OWLOntologyID ontologyID = concurrentOWLOntology.getOntologyID();
        for (Field field : ontologyID.getClass().getDeclaredFields()) {
            if (StringUtils.equalsIgnoreCase(field.getName(), "ontologyIRI")) {
                try {
                    field.setAccessible(true);
                    final Optional<IRI> optional = (Optional<IRI>) field.get(ontologyID);
                    return optional.get().toString();
                } catch (Exception e) {
                    throw new ApplicationException(e);
                }
            }
        }
        return null;
    }

    @Override
    public Collection<OWLClass> getEqualClasses(OWLClass owlClass) throws ApplicationException {
        final Set<OWLEquivalentClassesAxiom> axioms = getOntology().getAxioms(AxiomType.EQUIVALENT_CLASSES);
        final Collection<OWLEquivalentClassesAxiom> filteredAxioms = CollectionUtils.filter(axioms, new Specification<OWLEquivalentClassesAxiom>() {
            @Override
            public boolean isSatisfied(OWLEquivalentClassesAxiom axiom) {
                return CollectionUtils.some(axiom.getClassExpressions(), new Specification<OWLClassExpression>() {
                    @Override
                    public boolean isSatisfied(OWLClassExpression expression) {
                        if (!(expression instanceof OWLClass)) {
                            return false;
                        }
                        final OWLClass expressionClass = (OWLClass) expression;
                        return OWLUtils.equals(
                                expressionClass,
                                owlClass
                        );
                    }
                });
            }
        });
        final Collection<OWLClass> equalClasses = new ArrayList<>();
        for (OWLEquivalentClassesAxiom axiom : filteredAxioms) {
            /**
             * ?????????????? ?????? Person and (hasParent some Person)
             */
            final Collection<OWLObjectIntersectionOf> intersections = CollectionUtils.map(
                    CollectionUtils.filter(axiom.getClassExpressions(), new Specification<OWLClassExpression>() {
                        @Override
                        public boolean isSatisfied(OWLClassExpression expression) {
                            return expression instanceof OWLObjectIntersectionOf;
                        }
                    }),
                    new Transformer<OWLClassExpression, OWLObjectIntersectionOf>() {
                        @Override
                        public OWLObjectIntersectionOf transform(OWLClassExpression item) {
                            return (OWLObjectIntersectionOf) item;
                        }
                    }
            );
            final Collection<OWLClassExpression> operands = CollectionUtils.flatMap(intersections, new Transformer<OWLObjectIntersectionOf, Collection<OWLClassExpression>>() {
                @Override
                public Collection<OWLClassExpression> transform(OWLObjectIntersectionOf item) {
                    return item.getOperands();
                }
            });
            equalClasses.addAll(CollectionUtils.map(
                    CollectionUtils.filter(operands, new Specification<OWLClassExpression>() {
                        @Override
                        public boolean isSatisfied(OWLClassExpression expression) {
                            return expression instanceof OWLClass;
                        }
                    }),
                    new Transformer<OWLClassExpression, OWLClass>() {
                        @Override
                        public OWLClass transform(OWLClassExpression item) {
                            return (OWLClass) item;
                        }
                    }
            ));
            /**
             * ?????????????? ???????? WOW or StarCraft or Diablo
             */
            final Collection<OWLObjectUnionOf> unions = CollectionUtils.map(
                    CollectionUtils.filter(axiom.getClassExpressions(), new Specification<OWLClassExpression>() {
                        @Override
                        public boolean isSatisfied(OWLClassExpression expression) {
                            return expression instanceof OWLObjectUnionOf;
                        }
                    }),
                    new Transformer<OWLClassExpression, OWLObjectUnionOf>() {
                        @Override
                        public OWLObjectUnionOf transform(OWLClassExpression item) {
                            return (OWLObjectUnionOf) item;
                        }
                    }
            );
            final Collection<OWLClass> foundClasses = CollectionUtils.map(
                    CollectionUtils.flatMap(unions, new Transformer<OWLObjectUnionOf, Collection<OWLClassExpression>>() {
                        @Override
                        public Collection<OWLClassExpression> transform(OWLObjectUnionOf item) {
                            return item.getOperands();
                        }
                    }),
                    new Transformer<OWLClassExpression, OWLClass>() {
                        @Override
                        public OWLClass transform(OWLClassExpression item) {
                            return (OWLClass) item;
                        }
                    }
            );
            equalClasses.addAll(foundClasses);
        }
        return equalClasses;
    }

    @Override
    public void saveOntology() throws ApplicationException {
        final OWLModelManager manager = getModelManager();
        try {
            manager.save();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
