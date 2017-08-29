package org.zalando.switchman.api;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rx.Single;

public class CompleteItemProvider {

    private final Map<ItemId, Recipe> productById;

    public CompleteItemProvider() {
        productById = new HashMap<>();
        addNewRecipe("Pancakes", "https://cdn.pixabay.com/photo/2017/01/16/17/45/pancake-1984716_1280.jpg");
        addNewRecipe("Tiramisu", "https://cdn.pixabay.com/photo/2017/01/11/11/33/cake-1971552_1280.jpg");
        addNewRecipe("Cinnamon Rolls", "https://cdn.pixabay.com/photo/2016/05/26/16/27/baking-1417494_1280.jpg");
    }

    private void addNewRecipe(final String title, final String url) {
        ItemIdImpl itemId = new ItemIdImpl();
        Recipe recipe = new Recipe(itemId, title, url);
        productById.put(itemId, recipe);
    }


    Single<Recipe> getCompleteItem(ItemId id) {
        return Single.just(productById.get(id));
    }

    Single<List<Recipe>> getAllCompleteItems() {
        return Single.just(new ArrayList<>(productById.values()));
    }

    private static class ItemIdImpl implements ItemId {
        private final int id;

        private ItemIdImpl() {
            id = new Random().nextInt();
        }

        @Override
        public String toString() {
            return "ItemIdImpl{" +
                    "id=" + id +
                    '}';
        }
    }
}
