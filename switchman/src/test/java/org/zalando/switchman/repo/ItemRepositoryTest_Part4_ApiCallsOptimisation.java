package org.zalando.switchman.repo;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;
import org.zalando.switchman.api.ApiResponse;

import rx.Single;
import rx.observers.AssertableSubscriber;

public class ItemRepositoryTest_Part4_ApiCallsOptimisation {

    static {
        RxJavaSchedulerUtils.overrideSchedulersWithTestScheduler();
    }

    @Mock
    Api api;

    @Mock
    ApiErrorConverter apiErrorConverter;

    private ItemRepositoryImpl itemRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        itemRepository = new ItemRepositoryImpl(api, apiErrorConverter);
    }



    // API Calls Optimization
    @Test
    public void ARA_ResultInApiCallAdd() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        verify(api).addItem(itemId);
        verify(api, never()).removeItem(itemId);
    }

    @Test
    public void ARAR_ResultInTwoApiCallsAddAndRemove() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        verify(api).addItem(itemId);
        verify(api).removeItem(itemId);
    }

    @Test
    public void addItem_ShouldNotBeSkipped_When_AddRequestFailedOnceAlready() {
        ItemId itemId = mock(ItemId.class);
        Throwable throwable = new RuntimeException();
        when(api.addItem(itemId))
                .thenReturn(Single.error(throwable))
                .thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(new ExceptionApiError(throwable))).thenReturn(cause);

        itemRepository.addItem(itemId).test();
        AssertableSubscriber<Response> subscriber = itemRepository.addItem(itemId).test();

        RxJavaSchedulerUtils.advanceOneSecond();
        verify(api, times(2)).addItem(itemId);
        subscriber.assertResult(Response.createSuccessfulResponse())
                .assertCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void removeItem_ShouldNotBeSkipped_When_RemoveRequestFailedOnceAlready() {
        ItemId itemId = addNewItemSuccessfully();
        Throwable throwable = new RuntimeException();
        when(api.removeItem(itemId))
                .thenReturn(Single.error(throwable))
                .thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(new ExceptionApiError(throwable))).thenReturn(cause);

        itemRepository.removeItem(itemId).test();
        AssertableSubscriber<Response> subscriber = itemRepository.removeItem(itemId).test();

        RxJavaSchedulerUtils.advanceOneSecond();
        verify(api, times(2)).removeItem(itemId);
        subscriber.assertResult(Response.createSuccessfulResponse())
                .assertCompleted();
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void twoAdditions_ShouldBeExecutedInParallel_When_ItemIdsAreDifferent() {
        ItemId itemId1 = mock(ItemId.class);
        ItemId itemId2 = mock(ItemId.class);

        when(api.addItem(itemId1)).thenReturn(RxJavaUtils.createNeverCompletedSingle());
        when(api.addItem(itemId2)).thenReturn(RxJavaUtils.createNeverCompletedSingle());

        AssertableSubscriber<Response> subscriber1 = itemRepository.addItem(itemId1).test();
        AssertableSubscriber<Response> subscriber2 = itemRepository.addItem(itemId2).test();

        RxJavaSchedulerUtils.advanceOneSecond();
        subscriber1.assertNotCompleted();
        subscriber2.assertNotCompleted();

        verify(api).addItem(itemId1);
        verify(api).addItem(itemId2);
    }

    @Test
    public void ARA_ReturnsSkippedResponsesForRemoveAndSecondAdd() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Response> firstAddSubscriber = itemRepository.addItem(itemId).test();
        AssertableSubscriber<Response> removeSubscriber = itemRepository.removeItem(itemId).test();
        AssertableSubscriber<Response> secondAddSubscriber = itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        assertTrue(firstAddSubscriber.getOnNextEvents().get(0).isSuccessful());
        assertTrue(removeSubscriber.getOnNextEvents().get(0).isSkipped());
        assertTrue(secondAddSubscriber.getOnNextEvents().get(0).isSkipped());
    }

    @Test
    public void ARAR_ReturnsSkippedResponsesForFirstRemoveAndSecondAdd() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Response> firstAddSubscriber = itemRepository.addItem(itemId).test();
        AssertableSubscriber<Response> firstRemoveSubscriber = itemRepository.removeItem(itemId).test();
        AssertableSubscriber<Response> secondAddSubscriber = itemRepository.addItem(itemId).test();
        AssertableSubscriber<Response> secondRemoveSubscriber = itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        assertTrue(firstAddSubscriber.getOnNextEvents().get(0).isSuccessful());
        assertTrue(firstRemoveSubscriber.getOnNextEvents().get(0).isSkipped());
        assertTrue(secondAddSubscriber.getOnNextEvents().get(0).isSkipped());
        assertTrue(secondRemoveSubscriber.getOnNextEvents().get(0).isSuccessful());
    }

    /**
     * Helper method for adding a single item.
     *
     * @return  ItemId of the added item
     */
    private ItemId addNewItemSuccessfully() {
        ItemId itemId = mock(ItemId.class);
        addItemSuccessfully(itemId);
        return itemId;
    }

    /**
     * Helper method for adding a given item.
     *
     * @param  itemId  id of the item to add
     */
    private void addItemSuccessfully(final ItemId itemId) {
        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();
    }
}
