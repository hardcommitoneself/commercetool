package io.sphere.sdk.facets;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.search.TermStats;

import java.util.List;

public class TermUI<T> extends Base {
    private final T term;
    private final long count;
    private final boolean selected;

    TermUI(final T term, final long count, final boolean selected) {
        this.term = term;
        this.count = count;
        this.selected = selected;
    }

    public T getTerm() {
        return term;
    }

    public long getCount() {
        return count;
    }

    public boolean isSelected() {
        return selected;
    }

    public static <T> TermUI<T> of(final TermStats<T> termStats, final List<T> selectedValues) {
        return new TermUI<>(termStats.getTerm(), termStats.getCount(), selectedValues.contains(termStats.getTerm()));
    }
}
