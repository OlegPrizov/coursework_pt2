public class Main {
    public static void main(String[] args) {
        IOThreadScheduler ioScheduler = new IOThreadScheduler();
        ComputationScheduler computationScheduler = new ComputationScheduler();
        SingleThreadScheduler singleThreadScheduler = new SingleThreadScheduler();

        System.out.println("\nExample 1: Using flatMap");
        Observable<Integer> numbers = Observable.create(observer -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    observer.onNext(i);
                }
                observer.onComplete();
            } catch (Exception e) {
                observer.onError(e);
            }
        });

        Disposable disposable = numbers
                .flatMap(number -> Observable.create(observer -> {
                    try {
                        Thread.sleep(100);
                        observer.onNext(number * 10);
                        observer.onNext(number * 20);
                        observer.onComplete();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }))
                .subscribe(
                        item -> System.out.println("FlatMap result: " + item),
                        error -> System.out.println("Error: " + error.getMessage()),
                        () -> System.out.println("FlatMap completed!")
                );

        System.out.println("\nExample 2: Error handling");
        Observable.create(observer -> {
                    try {
                        observer.onNext(1);
                        observer.onNext(2);
                        throw new RuntimeException("Simulated error");
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                })
                .subscribe(
                        item -> System.out.println("Received: " + item),
                        error -> System.out.println("Error handled: " + error.getMessage()),
                        () -> System.out.println("This won't be called due to error")
                );

        System.out.println("\nExample 3: Disposable usage");
        Observable<Integer> infinite = Observable.create(observer -> {
            int i = 0;
            while (true) {
                observer.onNext(i++);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Disposable infiniteDisposable = infinite
                .subscribeOn(ioScheduler)
                .observeOn(computationScheduler)
                .subscribe(
                        item -> System.out.println("Infinite: " + item),
                        error -> System.out.println("Error: " + error.getMessage()),
                        () -> System.out.println("This won't be called")
                );

        try {
            Thread.sleep(500);
            infiniteDisposable.dispose();
            System.out.println("Infinite stream disposed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}