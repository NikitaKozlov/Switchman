package org.zalando.switchman.repo;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;

/**
 * Represents the expected item collection's state from the application point of view allowing it
 * to react immediately on user actions.
 * Having {@link #itemsDuringAddition} and {@link #itemsDuringRemoval} allows handling error from
 * network and {@link org.zalando.switchman.api.Api} without loosing of data's integrity
 * @param <T> type of an item id
 */
public class LocalStorage<T extends ItemId> {

    private final SerializedSubject<Integer, Integer> counter =
            BehaviorSubject.<Integer>create().toSerialized();
    private List<T> addedItems = new ArrayList<>();
    private List<T> itemsDuringAddition = new ArrayList<>();
    private List<T> itemsDuringRemoval = new ArrayList<>();

    public synchronized void refreshLocalStorage(final Set<? extends Item<T>> input) {
        addedItems = new ArrayList<>(input.size());

        for (Item<T> item : input) {
            addedItems.add(item.getId());
        }
    }

    Observable<Integer> getCounter() {
        return counter;
    }

    void publishCounter() {
        counter.onNext(getCounterValue());
    }

    public synchronized int getCounterValue() {
        int counterValue = addedItems.size();
        for (T itemDuringAddition : itemsDuringAddition) {
            if (!addedItems.contains(itemDuringAddition)) {
                counterValue++;
            }
        }

        for (T itemDuringRemoval : itemsDuringRemoval) {
            if (addedItems.contains(itemDuringRemoval)) {
                counterValue--;
            }
        }

        return counterValue;
    }

    public synchronized boolean contains(final T id) {
        return (addedItems.contains(id)
                    || itemsDuringAddition.contains(id)) //
                && !itemsDuringRemoval.contains(id);
    }

    public synchronized void moveItemIdFromAdditionToAdded(final T id) {
        removeItemIdFromAddition(id);
        if (!addedItems.contains(id)) {
            addedItems.add(id);
        }
    }

    public synchronized void removeItemIdFromRemovalAndAdded(final T id) {
        removeItemIdFromRemoving(id);
        if (addedItems.contains(id)) {
            addedItems.remove(id);
        }
    }

    public synchronized void addItemIdForAddition(final T id) {
        if (!itemsDuringAddition.contains(id)) {
            itemsDuringAddition.add(id);
        }
    }

    public synchronized void removeItemIdFromAddition(final T id) {
        if (itemsDuringAddition.contains(id)) {
            itemsDuringAddition.remove(id);
        }
    }

    public synchronized void addItemIdForRemoving(final T id) {
        if (!itemsDuringRemoval.contains(id)) {
            itemsDuringRemoval.add(id);
        }
    }

    public synchronized void removeItemIdFromRemoving(final T id) {
        if (itemsDuringRemoval.contains(id)) {
            itemsDuringRemoval.remove(id);
        }
    }

}
