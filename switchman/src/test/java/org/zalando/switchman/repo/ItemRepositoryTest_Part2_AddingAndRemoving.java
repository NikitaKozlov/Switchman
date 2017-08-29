package org.zalando.switchman.repo;

import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import static org.zalando.switchman.repo.RxJavaUtils.createNeverCompletedSingle;

import static junit.framework.TestCase.assertFalse;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;
import org.zalando.switchman.api.ApiResponse;
import org.zalando.switchman.api.HttpStatus;

import rx.Single;

import rx.observers.AssertableSubscriber;

public class ItemRepositoryTest_Part2_AddingAndRemoving {

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

    // Additions
    @Test
    public void addItem_DidNotAdd_When_NotSubscribed() {
        ItemId itemId = mock(ItemId.class);
        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        itemRepository.addItem(itemId);
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void hasItem_ReturnTrue_When_AddItemIsCalledButApiCallIsNotFinished() {
        ItemId itemId = mock(ItemId.class);
        when(api.addItem(itemId)).thenReturn(createNeverCompletedSingle());

        itemRepository.addItem(itemId).test().assertNotCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void addItem_CallsApiAndReturnSuccess_When_ApiReturnSuccess() {
        ItemId itemId = mock(ItemId.class);
        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Response> subscriber = itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertResult(Response.createSuccessfulResponse())
                .assertCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void addItem_CallsApiAndReturnFailed_When_ApiReturnBadRequest() {
        ItemId itemId = mock(ItemId.class);
        ApiResponse.ApiError apiError = mock(ApiResponse.ApiError.class);
        when(api.addItem(any())).thenReturn(Single.just(ApiResponse.createFailedResponse(HttpStatus.BAD_REQUEST.value(), apiError)));
        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(apiError)).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertResult(Response.createFailedResponse(cause))
                .assertCompleted();
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void addItem_CallsApiAndReturnSuccess_When_ApiReturnConflict() {
        ItemId itemId = mock(ItemId.class);
        ApiResponse.ApiError apiError = mock(ApiResponse.ApiError.class);
        when(api.addItem(any())).thenReturn(Single.just(ApiResponse.createFailedResponse(HttpStatus.CONFLICT.value(), apiError)));
        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(apiError)).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertResult(Response.createSuccessfulResponse())
                .assertCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void addItem_CallsApiAndReturnFailed_When_ApiThrowsException() {
        ItemId itemId = mock(ItemId.class);
        Throwable throwable = new RuntimeException();
        when(api.addItem(itemId)).thenReturn(Single.error(throwable));
        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(new ExceptionApiError(throwable))).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertResult(Response.createFailedResponse(cause))
                .assertCompleted();
        assertFalse(itemRepository.hasItem(itemId));
    }

    // Removals
    @Test
    public void removeItem_DidNotRemove_When_NotSubscribed() {
        ItemId itemId = addNewItemSuccessfully();
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        itemRepository.removeItem(itemId);
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void hasItem_ReturnFalse_When_RemoveItemIsCalledButApiCallIsNotFinished() {
        ItemId itemId = addNewItemSuccessfully();
        when(api.removeItem(itemId)).thenReturn(createNeverCompletedSingle());

        itemRepository.removeItem(itemId).test();
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void removeItem_CallsApiAndReturnSuccess_When_ApiReturnSuccess() {
        ItemId itemId = addNewItemSuccessfully();
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Response> subscriber = itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertReceivedOnNext(Collections.singletonList(Response.createSuccessfulResponse()))
                .assertCompleted();
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void removeItem_CallsApiAndReturnFailed_When_ApiReturnFailed() {
        ItemId itemId = addNewItemSuccessfully();

        ApiResponse.ApiError apiError = mock(ApiResponse.ApiError.class);

        when(api.removeItem(any())).thenReturn(Single.just(ApiResponse.createFailedResponse(HttpStatus.BAD_REQUEST.value(), apiError)));

        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(apiError)).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();
        subscriber.assertResult(Response.createFailedResponse(cause))
                .assertCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    @Test
    public void removeItem_CallsApiAndReturnSuccess_When_ApiReturnNotFound() {
        ItemId itemId = addNewItemSuccessfully();

        ApiResponse.ApiError apiError = mock(ApiResponse.ApiError.class);

        when(api.removeItem(any())).thenReturn(Single.just(ApiResponse.createFailedResponse(HttpStatus.NOT_FOUND.value(), apiError)));

        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(apiError)).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();
        subscriber.assertResult(Response.createSuccessfulResponse())
                .assertCompleted();
        assertFalse(itemRepository.hasItem(itemId));
    }

    @Test
    public void removeItem_CallsApiAndReturnFailed_When_ApiThrows() {
        ItemId itemId = addNewItemSuccessfully();
        Throwable throwable = new RuntimeException();
        when(api.removeItem(itemId)).thenReturn(Single.error(throwable));
        Response.Cause cause = mock(Response.Cause.class);
        when(apiErrorConverter.convertApiError(new ExceptionApiError(throwable))).thenReturn(cause);

        AssertableSubscriber<Response> subscriber = itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        subscriber.assertResult(Response.createFailedResponse(cause))
                .assertCompleted();
        assertTrue(itemRepository.hasItem(itemId));
    }

    /**
     * Helper method for adding a single item.
     *
     * @return ItemId of the added item
     */
    private ItemId addNewItemSuccessfully() {
        ItemId itemId = mock(ItemId.class);
        addItemSuccessfully(itemId);
        return itemId;
    }

    /**
     * Helper method for adding a given item.
     *
     * @param itemId id of the item to add
     */
    private void addItemSuccessfully(final ItemId itemId) {
        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();
    }

}
