package io.sphere.sdk.facets;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.products.search.ProductProjectionSearchModel;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.search.TermFacetResult;
import io.sphere.sdk.search.TermStats;
import io.sphere.sdk.search.model.TermFacetedSearchSearchModel;
import org.junit.Test;

import java.util.List;

import static io.sphere.sdk.facets.DefaultFacetType.CATEGORY_TREE;
import static io.sphere.sdk.facets.DefaultFacetType.SELECT;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class SelectFacetBuilderTest {
    private static final String KEY = "single-select-facet";
    private static final String LABEL = "Select one option";
    private static final TermFacetedSearchSearchModel<ProductProjection> SEARCH_MODEL = ProductProjectionSearchModel.of().facetedSearch().categories().id();
    private static final TermFacetResult FACET_RESULT_WITH_THREE_TERMS = TermFacetResult.of(5L, 60L, 0L, asList(
            TermStats.of("one", 60L, 30L),
            TermStats.of("two", 40L, 20L),
            TermStats.of("three", 20L, 10L)));
    private static final List<String> SELECTED_VALUE_TWO = singletonList("two");
    private static final List<FacetOption> OPTIONS = asList(
            FacetOption.of("one", 30, false),
            FacetOption.of("two", 20, true),
            FacetOption.of("three", 10, false));
    public static final FacetOptionMapper IDENTITY_MAPPER = v -> v;

    @Test
    public void createsInstance() throws Exception {
        final SelectFacet<ProductProjection> facet = SelectFacetBuilder.of(KEY, SEARCH_MODEL)
                .label(LABEL)
                .mapper(IDENTITY_MAPPER)
                .type(CATEGORY_TREE)
                .facetResult(FACET_RESULT_WITH_THREE_TERMS)
                .selectedValues(SELECTED_VALUE_TWO)
                .matchingAll(true)
                .multiSelect(false)
                .threshold(3L)
                .limit(10L)
                .build();
        assertThat(facet.getKey()).isEqualTo(KEY);
        assertThat(facet.getType()).isEqualTo(CATEGORY_TREE);
        assertThat(facet.getLabel()).contains(LABEL);
        assertThat(facet.getMapper()).isEqualTo(IDENTITY_MAPPER);
        assertThat(facet.getFacetedSearchSearchModel()).isEqualTo(SEARCH_MODEL);
        assertThat(facet.getFacetResult()).isEqualTo(FACET_RESULT_WITH_THREE_TERMS);
        assertThat(facet.getSelectedValues()).containsExactlyElementsOf(SELECTED_VALUE_TWO);
        assertThat(facet.isMatchingAll()).isTrue();
        assertThat(facet.isMultiSelect()).isFalse();
        assertThat(facet.getThreshold()).isEqualTo(3L);
        assertThat(facet.getLimit()).isEqualTo(10L);
        assertThat(facet.isAvailable()).isTrue();
        assertThat(facet.getAllOptions()).containsExactlyElementsOf(OPTIONS);
        assertThat(facet.getLimitedOptions()).containsExactlyElementsOf(OPTIONS);
    }

    @Test
    public void createsInstanceWithOptionalValues() throws Exception {
        final SelectFacet<ProductProjection> facet = SelectFacetBuilder.of(KEY, SEARCH_MODEL).build();
        assertThat(facet.getKey()).isEqualTo(KEY);
        assertThat(facet.getType()).isEqualTo(SELECT);
        assertThat(facet.getLabel()).isNull();
        assertThat(facet.getFacetedSearchSearchModel()).isEqualTo(SEARCH_MODEL);
        assertThat(facet.getMapper()).isNull();
        assertThat(facet.getFacetResult()).isNull();
        assertThat(facet.getSelectedValues()).isEmpty();
        assertThat(facet.isMatchingAll()).isFalse();
        assertThat(facet.isMultiSelect()).isTrue();
        assertThat(facet.getThreshold()).isEqualTo(1L);
        assertThat(facet.getLimit()).isNull();
        assertThat(facet.isAvailable()).isFalse();
        assertThat(facet.getAllOptions()).isEmpty();
        assertThat(facet.getLimitedOptions()).isEmpty();
    }

    @Test
    public void mapsOptions() throws Exception {
        final CustomSortedFacetOptionMapper mapper = CustomSortedFacetOptionMapper.of(asList("two", "three", "one"));
        final SelectFacet<ProductProjection> facet = SelectFacetBuilder.of(KEY, SEARCH_MODEL)
                .mapper(mapper)
                .facetResult(FACET_RESULT_WITH_THREE_TERMS)
                .selectedValues(SELECTED_VALUE_TWO)
                .build();
        assertThat(facet.getMapper()).isEqualTo(mapper);
        assertThat(facet.getAllOptions()).containsExactly(OPTIONS.get(1), OPTIONS.get(2), OPTIONS.get(0));
        assertThat(facet.getLimitedOptions()).containsExactly(OPTIONS.get(1), OPTIONS.get(2), OPTIONS.get(0));
    }

    @Test
    public void createsInstanceWithDifferentFacetResult() throws Exception {
        final SelectFacet<ProductProjection> facet = SelectFacetBuilder.of(KEY, SEARCH_MODEL).build();
        assertThat(facet.getFacetResult()).isNull();
        assertThat(facet.withSearchResult(searchResult()).getFacetResult()).isEqualTo(FACET_RESULT_WITH_THREE_TERMS);
    }

    private static PagedSearchResult<ProductProjection> searchResult() {
        return readObjectFromResource("pagedSearchResult.json", ProductProjectionSearch.resultTypeReference());
    }
}
