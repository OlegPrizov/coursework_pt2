import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Планировщик, использующий кэшируемый пул потоков для операций ввода-вывода.
/// Аналог Schedulers.io() из RxJava.
public class IOThreadScheduler implements Scheduler {
    private final ExecutorService executor;

    public IOThreadScheduler() {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}