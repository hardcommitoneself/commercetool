package io.sphere.sdk.facets;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.search.TermFacetResult;
import io.sphere.sdk.search.TermStats;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FacetOption extends Base {
    private final String term;
    private final long count;
    private final boolean selected;

    FacetOption(final String term, final long count, final boolean selected) {
        this.term = term;
        this.count = count;
        this.selected = selected;
    }

    public String getTerm() {
        return term;
    }

    public long getCount() {
        return count;
    }

    public boolean isSelected() {
        return selected;
    }

    public static FacetOption of(final String term, final long count, final boolean selected) {
        return new FacetOption(term, count, selected);
    }

    public static FacetOption ofTermStats(final TermStats termStats, final List<String> selectedValues) {
        return FacetOption.of(termStats.getTerm(), termStats.getCount(), selectedValues.contains(termStats.getTerm()));
    }

    public static List<FacetOption> ofFacetResult(final TermFacetResult facetResult, final List<String> selectedValues) {
        return facetResult.getTerms().stream()
                .map(termStats -> ofTermStats(termStats, selectedValues))
                .collect(toList());
    }
}
