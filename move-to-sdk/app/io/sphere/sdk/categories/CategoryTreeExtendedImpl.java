package io.sphere.sdk.categories;

import io.sphere.sdk.models.Identifiable;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class CategoryTreeExtendedImpl implements CategoryTreeExtended {
    private final CategoryTree categoryTree;

    CategoryTreeExtendedImpl(final List<Category> allCategoriesAsFlatList) {
        categoryTree = CategoryTree.of(allCategoriesAsFlatList);
    }

    @Override
    public List<Category> getRoots() {
        return categoryTree.getRoots();
    }

    @Override
    public Optional<Category> findById(final String id) {
        return categoryTree.findById(id);
    }

    @Override
    public Optional<Category> findByExternalId(final String externalId) {
        return categoryTree.findByExternalId(externalId);
    }

    @Override
    public Optional<Category> findBySlug(final Locale locale, final String slug) {
        return categoryTree.findBySlug(locale, slug);
    }

    @Override
    public List<Category> getAllAsFlatList() {
        return categoryTree.getAllAsFlatList();
    }

    @Override
    public List<Category> findChildren(final Identifiable<Category> category) {
        return categoryTree.findChildren(category);
    }

    @Override
    public List<Category> findSiblings(final Collection<? extends Identifiable<Category>> collection) {
        return categoryTree.findSiblings(collection);
    }

    @Override
    public CategoryTree getSubtree(final Collection<? extends Identifiable<Category>> collection) {
        return CategoryTree.of(Collections.emptyList());
    }

    @Override
    public Category getRootAncestor(final Identifiable<Category> identifiable) {
        return categoryTree.getRootAncestor(identifiable);
    }

    @Override
    public List<Category> getSubtreeRoots() {
        return categoryTree.getSubtreeRoots();
    }

    @Override
    public List<Category> getSiblings(final Collection<? extends Identifiable<Category>> categoryIds) {
        return categoryIds.stream()
                .flatMap(category -> getSiblings(category).stream())
                .distinct()
                .filter(sibling -> !categoryIds.stream().anyMatch(c -> sibling.getId().equals(c.getId())))
                .collect(toList());
    }

    @Override
    public Category getRootAncestor(final Category category) {
        requireNonNull(category);
        return category.getAncestors().stream().findFirst()
                .flatMap(root -> categoryTree.findById(root.getId()))
                .orElse(category);
    }

    private List<Category> getSiblings(final Identifiable<Category> category) {
        return Optional.ofNullable(findById(category.getId()).orElseThrow(() -> new IllegalStateException("categoryTree does not contain " + category)).getParent())
                .map(categoryTree::findChildren)
                .orElseGet(Collections::emptyList);
    }

    private List<Category> getSubtreeAsFlatList(final Collection<Category> parentCategories) {
        final List<Category> categories = new ArrayList<>();
        parentCategories.stream().forEach(parent -> {
            categories.add(parent);
            final List<Category> children = categoryTree.findChildren(parent);
            categories.addAll(getSubtreeAsFlatList(children));
        });
        return categories;
    }
}
