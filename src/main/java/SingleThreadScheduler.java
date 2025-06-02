import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Планировщик, использующий один поток для последовательного выполнения операций.
/// Аналог Schedulers.single() из RxJava.
public class SingleThreadScheduler implements Scheduler {
    private final ExecutorService executor;

    public SingleThreadScheduler() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}