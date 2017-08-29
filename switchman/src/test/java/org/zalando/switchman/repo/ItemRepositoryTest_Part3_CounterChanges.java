package org.zalando.switchman.repo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;

import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;
import org.zalando.switchman.api.ApiResponse;
import org.zalando.switchman.api.HttpStatus;

import rx.Single;

import rx.observers.AssertableSubscriber;

public class ItemRepositoryTest_Part3_CounterChanges {

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

    @Test
    public void Add_ResultIn01Counter() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1));
    }

    @Test
    public void AR_ResultIn010Counter() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0));
    }

    @Test
    public void ARAR_ResultIn01010Counter() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0, 1, 0));
    }

    @Test
    public void AR_ResultIn0101Counter_When_LastRemovalFails() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.just(
                ApiResponse.createFailedResponse(HttpStatus.BAD_REQUEST.value(), mock(ApiResponse.ApiError.class))));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0, 1));
    }

    @Test
    public void AR_ResultIn0101Counter_When_LastRemovalThrows() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createSuccessfulResponse()));
        when(api.removeItem(itemId)).thenReturn(Single.error(new RuntimeException()));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        itemRepository.removeItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0, 1));
    }

    @Test
    public void Add_ResultIn010Counter_When_AdditionFails() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.just(ApiResponse.createFailedResponse(
                        HttpStatus.BAD_REQUEST.value(), mock(ApiResponse.ApiError.class))));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0));
    }

    @Test
    public void Add_ResultIn010Counter_When_ExceptionIsThrown() {
        ItemId itemId = mock(ItemId.class);

        when(api.addItem(itemId)).thenReturn(Single.error(new RuntimeException()));

        AssertableSubscriber<Integer> counterTestSubscriber = itemRepository.getCounter().test();

        itemRepository.addItem(itemId).test();
        RxJavaSchedulerUtils.advanceOneSecond();

        counterTestSubscriber.assertReceivedOnNext(Arrays.asList(0, 1, 0));
    }
}
