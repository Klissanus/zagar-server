package ticker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by apomosov on 14.05.16.
 *
 * Synchronizes services
 */
public class Ticker {
    private final static Logger log = LogManager.getLogger(Ticker.class);

    private final long sleepTimeNanos;
    private final AtomicLong tickNumber;
    private final Tickable tickable;

    public Ticker(Tickable tickable, int maxTicksPerSecond) {
        this.tickable = tickable;
        this.tickNumber = new AtomicLong(0);
        this.sleepTimeNanos = TimeUnit.SECONDS.toNanos(1) / maxTicksPerSecond;
    }

    public void loop() {
        long elapsed = sleepTimeNanos;
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.nanoTime();
            tickable.tick(elapsed);
            elapsed = System.nanoTime() - started;
            if (elapsed < sleepTimeNanos) {
                log.trace("All tickers finish at {} ms", TimeUnit.NANOSECONDS.toMillis(elapsed));
                LockSupport.parkNanos(sleepTimeNanos - elapsed);
            } else {
                log.warn("tick lag {} ms", TimeUnit.NANOSECONDS.toMillis(elapsed - sleepTimeNanos));
            }
            log.trace("{} <tick> {}", tickable, tickNumber.incrementAndGet());
        }
    }
}
