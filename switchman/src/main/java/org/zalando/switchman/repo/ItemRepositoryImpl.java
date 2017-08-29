package org.zalando.switchman.repo;

import org.zalando.switchman.Item;
import org.zalando.switchman.ItemId;
import org.zalando.switchman.api.Api;

import java.util.Set;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

public class ItemRepositoryImpl implements ItemRepository {

    private final Api api;
    private final LocalStorage<ItemId> localStorage;
    private final ApiErrorConverter apiErrorConverter;

    private final SerializedSubject<Command, Command> inputCommandStream;
    private final SerializedSubject<ResponseForCommand, ResponseForCommand> resultStream;

    public ItemRepositoryImpl(final Api api, final ApiErrorConverter apiErrorConverter) {
        this.api = api;
        this.localStorage = new LocalStorage<>();
        this.apiErrorConverter = apiErrorConverter;

        //All Subjects but Serialized one are not Thread safe.
        this.inputCommandStream = PublishSubject.<Command>create().toSerialized();
        this.resultStream = PublishSubject.<ResponseForCommand>create().toSerialized();

        init();
        localStorage.publishCounter();
    }

    private void init() {
        inputCommandStream.groupBy(Command::getKey)
                .subscribe(groupedObservable -> groupedObservable.observeOn(Schedulers.io())
                        .onBackpressureLatest()
                        .subscribe(new Subscriber<Command>() {
                            private volatile Command lastCommand;

                            @Override
                            public void onStart() {
                                super.onStart();
                                requestNextCommand();
                            }

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Command command) {
                                if (shouldNotSkipCommand(command)) {
                                    lastCommand = command;
                                    command.execute()
                                            .subscribeOn(Schedulers.io())
                                            .doOnSuccess(apiResponse -> {
                                                if (!apiResponse.isSuccessful()) {
                                                    localStorage.publishCounter();
                                                    lastCommand = null;
                                                }
                                            })
                                            .map(response -> new ResponseForCommand(command, response))
                                            .doOnSuccess(resultStream::onNext)
                                            .subscribe(ignored -> requestNextCommand());
                                } else {
                                    resultStream.onNext(new ResponseForCommand(command, Response.createSkippedResponse()));
                                    requestNextCommand();
                                }
                            }

                            private boolean shouldNotSkipCommand(Command command) {
                                return lastCommand == null || !lastCommand.isSame(command);
                            }

                            private void requestNextCommand() {
                                request(1);
                            }
                        }));
    }

    @Override
    public Single<Set<? extends Item<ItemId>>> getItemList() {
        return api.getItemList()
                .doOnSuccess(localStorage::refreshLocalStorage)
                .doOnSuccess(ignored -> localStorage.publishCounter());
    }

    @Override
    public Observable<Integer> getCounter() {
        return localStorage.getCounter();
    }

    @Override
    public boolean hasItem(final ItemId id) {
        return localStorage.contains(id);
    }

    @Override
    public Single<Response> addItem(final ItemId id) {
        return launchCommand(new Add(id, System.nanoTime(), localStorage,  api, apiErrorConverter));
    }

    @Override
    public Single<Response> removeItem(final ItemId id) {
        return launchCommand(new Remove(id, System.nanoTime(), localStorage, api, apiErrorConverter));
    }

    private Single<Response> launchCommand(Command command) {
        return Single.just(command)
                .doOnSubscribe(command::preExecute)
                .doOnSuccess(inputCommandStream::onNext)
                .flatMap(it -> prepareCommandResultSingles(resultStream, it));
    }

    /**
     * @return Single that emits single response for the proper command,
     * also checked, if response came for Command that was created after provided one,
     * then replaced result with skipped {@link Response}
     */
    private Single<Response> prepareCommandResultSingles(
            final Observable<ResponseForCommand> input, final Command command) {
        return input.filter(responseForCommand -> command.hasSameItemId(responseForCommand.command))
                .filter(responseForCommand -> responseForCommand.command.isNotBefore(command))
                .limit(1)
                .toSingle()
                .map(responseForCommand -> {
                    if (responseForCommand.command.getTimestamp() != command.getTimestamp()) {
                        return Response.createSkippedResponse();
                    }
                    return responseForCommand.response;
                });
    }

    private class ResponseForCommand {
        final Command command;
        final Response response;

        private ResponseForCommand(Command command, Response response) {
            this.command = command;
            this.response = response;
        }
    }
}
