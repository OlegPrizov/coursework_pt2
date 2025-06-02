import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicBoolean;

/// Класс, представляющий Observable в паттерне Наблюдатель.
/// @param <T> - тип элементов, которые отправляются
public class Observable<T> {
    private final Consumer<Observer<T>> source;

    private Observable(Consumer<Observer<T>> source) {
        this.source = source;
    }

    /// Создаёт новый Observable из функции-источника.
    /// @param source Функция, которая определяет, как Observable отправляет элементы
     /// @param <T> - тип элементов, которые отправляются
     /// возвращает новый экземпляр Observable
    public static <T> Observable<T> create(Consumer<Observer<T>> source) {
        return new Observable<>(source);
    }

    /// Подписывает Observer на этот Observable и возвращает Disposable.
    /// @param observer - Observer для подписки
     /// возвращает Disposable, который можно использовать для отмены подписки
    public Disposable subscribe(Observer<T> observer) {
        AtomicBoolean disposed = new AtomicBoolean(false);
        try {
            source.accept(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposed.get()) {
                        observer.onNext(item);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposed.get()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposed.get()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Exception e) {
            if (!disposed.get()) {
                observer.onError(e);
            }
        }
        return new Disposable() {
            @Override
            public void dispose() {
                disposed.set(true);
            }

            @Override
            public boolean isDisposed() {
                return disposed.get();
            }
        };
    }

    /// Подписывается на этот Observable.
    /// @param onNext для обработки отправленных элементов
     /// @param onError для обработки ошибок
     /// @param onComplete для обработки завершения
     /// возвращает Disposable, который можно использовать для отмены подписки
    public Disposable subscribe(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        return subscribe(new Observer<T>() {
            @Override
            public void onNext(T item) {
                onNext.accept(item);
            }

            @Override
            public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override
            public void onComplete() {
                onComplete.run();
            }
        });
    }

    /// Преобразует элементы, отправляемые этим Observable, применяя функцию к каждому элементу.
    /// @param mapper Функция для преобразования каждого элемента
     /// @param <R> Тип элементов, отправляемых новым Observable
     /// возвращает новый Observable, отправляющий преобразованные элементы
    public <R> Observable<R> map(Function<T, R> mapper) {
        return new Observable<>(observer -> subscribe(
                item -> {
                    try {
                        observer.onNext(mapper.apply(item));
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                },
                observer::onError,
                observer::onComplete
        ));
    }

    /// Фильтрует элементы, отправляемые этим Observable, отправляя только те, которые удовлетворяют условию.
    /// @param predicate для проверки каждого элемента
     /// возвращает новый Observable, отправляющий только элементы, прошедшие проверку
    public Observable<T> filter(Predicate<T> predicate) {
        return new Observable<>(observer -> subscribe(
                item -> {
                    try {
                        if (predicate.test(item)) {
                            observer.onNext(item);
                        }
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                },
                observer::onError,
                observer::onComplete
        ));
    }

    /// Преобразует элементы этого Observable в другие Observable, затем объединяет их отправляемые элементы в один поток.
    /// @param mapper - функция, возвращающая Observable для каждого элемента исходного Observable
     /// @param <R> - тип элементов, отправляемых новым Observable
     /// возвращает новый Observable, отправляющий элементы из всех Observable, возвращённых функцией mapper
    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(
                    item -> {
                        if (!disposed.get()) {
                            try {
                                Observable<R> innerObservable = mapper.apply(item);
                                innerObservable.subscribe(
                                        observer::onNext,
                                        observer::onError,
                                        () -> {} // Не завершать при завершении вложенного Observable
                                );
                            } catch (Exception e) {
                                observer.onError(e);
                            }
                        }
                    },
                    observer::onError,
                    observer::onComplete
            );
        });
    }

    /// Указывает Scheduler, на котором будет выполняться этот Observable.
    /// @param scheduler - scheduler для использования
     /// возвращает новый Observable, работающий на указанном Scheduler
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new Observable<>(observer -> scheduler.execute(() -> subscribe(observer)));
    }

    /// Указывает Scheduler, на котором Observer будет наблюдать за этим Observable.
    /// @param scheduler - scheduler для использования
     /// возвращает новый Observable, который отслеживается на указанном Scheduler
    public Observable<T> observeOn(Scheduler scheduler) {
        return new Observable<>(observer -> subscribe(
                item -> scheduler.execute(() -> observer.onNext(item)),
                error -> scheduler.execute(() -> observer.onError(error)),
                () -> scheduler.execute(observer::onComplete)
        ));
    }
}