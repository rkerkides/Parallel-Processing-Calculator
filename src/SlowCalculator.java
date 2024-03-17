import java.util.concurrent.Callable;

public class SlowCalculator implements Runnable, Callable<Integer> {

    private final long N;
    private final TaskCompletionListener listener;

    public SlowCalculator(final long N, final TaskCompletionListener listener) {
        this.N = N;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            final int result = call(); // Perform the calculation
            try {
                Thread.sleep(1000); // Simulate a task taking time to complete
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle interruption correctly
            }
            // Notify listener of completion
            if (listener != null) {
                listener.onTaskCompleted(N, result);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public Integer call() throws Exception {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Task was interrupted");
        }

        final int result = calculateNumFactors(N);
        return result;
    }

    private static int calculateNumFactors(final long N) {
        // This (very inefficiently) finds and returns the number of unique prime factors of |N|
        // You don't need to think about the mathematical details; what's important is that it does some slow calculation taking N as input
        // You should NOT modify the calculation performed by this class, but you may want to add support for interruption
        int count = 0;
        for (long candidate = 2; candidate < Math.abs(N); ++candidate) {
            if (Thread.currentThread().isInterrupted()) {
                // Check for interruption
                return count; // or throw InterruptedException
            }
            if (isPrime(candidate)) {
                if (Math.abs(N) % candidate == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean isPrime(final long n) {
        // This (very inefficiently) checks whether n is prime
        // You should NOT modify this method
        for (long candidate = 2; candidate < Math.sqrt(n) + 1; ++candidate) {
            if (n % candidate == 0) {
                return false;
            }
        }
        return true;
    }
}