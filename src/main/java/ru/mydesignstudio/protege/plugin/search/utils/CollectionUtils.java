package ru.mydesignstudio.protege.plugin.search.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 04.01.17.
 */
public class CollectionUtils {
    public static final <ITEM> boolean isNotEmpty(Collection<ITEM> source) {
        if (source == null) {
            return false;
        }
        if (source.size() == 0) {
            return false;
        }
        return some(source, new Specification<ITEM>() {
            @Override
            public boolean isSatisfied(ITEM item) {
                return (item != null);
            }
        });
    }

    public static final <SOURCE, DESTINATION> Collection<DESTINATION> map(Collection<SOURCE> source, Transformer<SOURCE, DESTINATION> transformer) {
        final Collection<DESTINATION> destination = new ArrayList<DESTINATION>();
        for (SOURCE item : source) {
            destination.add(transformer.transform(item));
        }
        return destination;
    }

    public static final <ITEM> void forEach(Collection<ITEM> source, Action<ITEM> action) {
        for (ITEM item : source) {
            action.run(item);
        }
    }

    public static final <ITEM> boolean some(Collection<ITEM> source, Specification<ITEM> specification) {
        for (ITEM item : source) {
            if (specification.isSatisfied(item)) {
                return true;
            }
        }
        return false;
    }

    public static final <ITEM> boolean every(Collection<ITEM> source, Specification<ITEM> specification) {
        boolean result = true;
        for (ITEM item : source) {
            result = result && specification.isSatisfied(item);
        }
        return result;
    }
}
