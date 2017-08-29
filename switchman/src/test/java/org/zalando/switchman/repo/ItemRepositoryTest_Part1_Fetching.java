package org.zalando.switchman.repo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;

import rx.Single;

public class ItemRepositoryTest_Part1_Fetching {

    static {
        RxJavaSchedulerUtils.overrideSchedulersWithTestScheduler();
    }

    @Mock
    private Api api;

    @Mock
    private ApiErrorConverter apiErrorConverter;

    private ItemRepositoryImpl itemRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        itemRepository = new ItemRepositoryImpl(api, apiErrorConverter);
    }

    @Test
    public void hasItem_ReturnTrue_When_ItemWasFetched() {
        ItemId itemId1 = mock(ItemId.class);
        Item<ItemId> item1 = () -> itemId1;

        ItemId itemId2 = mock(ItemId.class);
        Item<ItemId> item2 = () -> itemId2;

        Set<Item<ItemId>> itemSet = new HashSet<>();
        itemSet.add(item1);
        itemSet.add(item2);

        when(api.getItemList()).thenReturn(Single.just(itemSet));

        itemRepository.getItemList().test();
        RxJavaSchedulerUtils.advanceOneSecond();

        Assert.assertTrue(itemRepository.hasItem(itemId1));
        Assert.assertTrue(itemRepository.hasItem(itemId2));
    }

    @Test
    public void hasItem_ReturnFalse_When_ItemWasNotFetched() {
        ItemId itemId1 = mock(ItemId.class);
        Item<ItemId> item1 = () -> itemId1;

        ItemId itemId2 = mock(ItemId.class);

        Set<Item<ItemId>> itemSet = Collections.singleton(item1);

        when(api.getItemList()).thenReturn(Single.just(itemSet));

        itemRepository.getItemList().test();
        RxJavaSchedulerUtils.advanceOneSecond();

        Assert.assertTrue(itemRepository.hasItem(itemId1));
        Assert.assertFalse(itemRepository.hasItem(itemId2));
    }

    @Test
    public void hasItem_ReturnFalse_When_ItemNotInListAfterRefetching() {
        ItemId itemId1 = mock(ItemId.class);
        Item<ItemId> item1 = () -> itemId1;

        ItemId itemId2 = mock(ItemId.class);
        Item<ItemId> item2 = () -> itemId2;

        Set<Item<ItemId>> itemSet = new HashSet<>();
        itemSet.add(item1);
        itemSet.add(item2);
        Set<Item<ItemId>> newItemSet = Collections.singleton(item1);

        when(api.getItemList())
                .thenReturn(Single.just(itemSet))
                .thenReturn(Single.just(newItemSet));

        itemRepository.getItemList().test();
        RxJavaSchedulerUtils.advanceOneSecond();

        Assert.assertTrue(itemRepository.hasItem(itemId1));
        Assert.assertTrue(itemRepository.hasItem(itemId2));

        // Refetching
        itemRepository.getItemList().test();
        RxJavaSchedulerUtils.advanceOneSecond();

        Assert.assertTrue(itemRepository.hasItem(itemId1));
        Assert.assertFalse(itemRepository.hasItem(itemId2));
    }
}
