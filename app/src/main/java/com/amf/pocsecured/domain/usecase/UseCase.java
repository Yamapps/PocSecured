package com.amf.pocsecured.domain.usecase;

import org.jetbrains.annotations.TestOnly;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Generic class extended by all application use cases
 * @param <T> Class of the use case's result
 * @param <Params> Class definition of the Parameters (Optional) used to build/execute this use case
 */
public abstract class UseCase<T, Params>
{

	private final CompositeDisposable disposables;

	public UseCase() {
		this.disposables = new CompositeDisposable();
	}

	/**
	 * Builds an {@link Observable} which will be used when executing
	 * the current {@link UseCase}.
	 */
	abstract Observable<T> buildUseCaseObservable(Params params);

	/**
	 * Executes the current use case.
	 *
	 * @param observer {@link DisposableObserver} which will be listening to the
	 * observable build by {@link #buildUseCaseObservable(Params)} ()} method.
	 * @param params Parameters (Optional) used to build/execute this use case.
	 */
	public void execute(DisposableObserver<T> observer, Params params) {
		Objects.requireNonNull(observer);
		final Observable<T> observable = this.buildUseCaseObservable(params)
												 .subscribeOn(Schedulers.io())
												 .observeOn(AndroidSchedulers.mainThread());
		addDisposable(observable.subscribeWith(observer));
	}

	/**
	 * Executes the current use case in unit test context.
	 * This method should only by used in testing purposes.
	 *
	 * @param observer {@link Observer} which will be listening to the observable build by {@link
	 *                 #buildUseCaseObservable(Params)} ()} method.
	 * @param params   Parameters (Optional) used to build/execute this use case.
	 */
	@TestOnly
	public void testExecute(Observer<T> observer, Params params) {
		Objects.requireNonNull(observer);
		final Observable<T> observable = buildUseCaseObservable(params)
												 .subscribeOn(Schedulers.io())
												 .observeOn(Schedulers.io());
		observable.subscribe(observer);
	}

	/**
	 * Dispose from current {@link CompositeDisposable}.
	 */
	public void dispose() {
		if (!disposables.isDisposed()) {
			disposables.dispose();
		}
	}

	/**
	 * Dispose from current {@link CompositeDisposable}.
	 */
	private void addDisposable(Disposable disposable) {
		Objects.requireNonNull(disposable);
		Objects.requireNonNull(disposables);
		disposables.add(disposable);
	}


	/**
	 * Clean use case when no londer needed.
	 */
	public abstract void onDestroy();
}
