package productcatalog.productoverview.search;

import common.contexts.UserContext;
import common.i18n.I18nIdentifier;
import common.i18n.I18nResolver;
import common.models.SelectableData;
import io.sphere.sdk.models.Base;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SortSelectorBean extends Base {
    private String key;
    private List<SelectableData> list;

    public SortSelectorBean() {
    }

    public SortSelectorBean(final SortSelector sortCriteria, final UserContext userContext, final I18nResolver i18nResolver) {
        final SortConfig sortConfig = sortCriteria.getSortConfig();
        this.key = sortConfig.getKey();
        this.list = sortConfig.getOptions().stream()
                .map(option -> optionToSelectableData(option, sortCriteria, userContext, i18nResolver))
                .collect(toList());
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public List<SelectableData> getList() {
        return list;
    }

    public void setList(final List<SelectableData> list) {
        this.list = list;
    }

    private static SelectableData optionToSelectableData(final SortOption option, final SortSelector sortCriteria,
                                                         final UserContext userContext, final I18nResolver i18nResolver) {
        final String label = i18nResolver.get(userContext.locales(), I18nIdentifier.of(option.getLabel()))
                .orElse(option.getLabel());
        final SelectableData sortOption = new SelectableData(label, option.getValue());
        if (sortCriteria.getSelectedValues().contains(option.getValue())) {
            sortOption.setSelected(true);
        }
        return sortOption;
    }
}
