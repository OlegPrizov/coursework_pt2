import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Планировщик, использующий фиксированный пул потоков для вычислительных операций.
/// Аналог Schedulers.computation() из RxJava.
public class ComputationScheduler implements Scheduler {
    private final ExecutorService executor;

    public ComputationScheduler() {
        int processors = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(processors);
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}