import com.rmv.oop.task10.Counter;
import com.rmv.oop.task10.ThreadPool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class ThreadPoolEfficiencyTest {

    @InjectMocks
    private Counter counter;

    @Test
    void checkSpeedUp() throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        long timeWithMaxCores = timeWithCores(cores);
        long timeWithOneCore = timeWithCores(1);
        double speedUp = (double) timeWithOneCore / timeWithMaxCores ;
        assertTrue(speedUp > cores * 0.8);
    }

    private long timeWithCores(int cores) throws Exception {
        ThreadPool threadPool = new ThreadPool(cores);

        long start = System.nanoTime();

        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            final int j = i;
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> counter.count(j),
                            threadPool
                    ));
        }

        double value = 0;
        for (Future<Double> future : futures) {
            value += future.get();
        }
        return System.nanoTime() - start;
    }
}
