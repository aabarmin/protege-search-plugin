package ru.mydesignstudio.protege.plugin.search.service.sparql.query.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 05.02.17.
 */
public class IntegerWherePartConverter implements WherePartConditionConverter<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegerWherePartConverter.class);

    @Override
    public String convert(WherePart wherePart, String stringValue, String variableName) {
        final StringBuilder builder = new StringBuilder();
        final LogicalOperation operation = wherePart.getLogicalOperation();
        builder.append("FILTER(").append(variableName);
        //
        if (LogicalOperation.EQUALS.equals(operation)) {
            builder.append("=");
        } else if (LogicalOperation.MORE_THAN.equals(operation)) {
            builder.append(">");
        } else if (LogicalOperation.MORE_OR_EQUALS.equals(operation)) {
            builder.append(">=");
        } else if (LogicalOperation.LESS_THAN.equals(operation)) {
            builder.append("<");
        } else if (LogicalOperation.LESS_OR_EQUALS.equals(operation)) {
            builder.append("<=");
        } else {
            LOGGER.error("Unsupported operation {}", operation);
            throw new RuntimeException(String.format(
                    "Unsupported operation %s",
                    operation
            ));
        }
        //
        builder.append(stringValue);
        builder.append(")");
        return builder.toString();
    }
}
