/// Интерфейс для планирования выполнения задач в разных потоках.
public interface Scheduler {
    /// @param task Задача, которая должна быть выполнена
    void execute(Runnable task);
}