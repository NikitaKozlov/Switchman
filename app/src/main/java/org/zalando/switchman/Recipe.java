package org.zalando.switchman;

public class Recipe implements Item<ItemId> {
    private final ItemId id;
    private final String title;
    private final String url;

    public Recipe(ItemId id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    @Override
    public ItemId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
