
import java.util.concurrent.*;

class TransactionProcessor {
    ExecutorService pool = Executors.newFixedThreadPool(4);

    void submit(Runnable task) {
        pool.execute(task);
    }

    void stop() {
        pool.shutdown();

        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
