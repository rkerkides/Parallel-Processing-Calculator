import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Solution implements CommandRunner, TaskCompletionListener{
    private final ExecutorService executorService;
    private final ConcurrentHashMap<Long, Future<?>> taskMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> resultMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Long>> taskDependencies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Boolean> cancelMap = new ConcurrentHashMap<>();

    public Solution() {
       this.executorService = Executors.newCachedThreadPool();
    }

    // The runCommand method of this class will be passed a string, corresponding to a command the user entered.
    // It should perform the relevant command, and return the specified output string.
    @Override
    public String runCommand(String command) {
        String[] parts = command.split(" "); // Split the command into parts
        String cmd = parts[0]; // The actual command, e.g., "start"

        switch (cmd) {
            case "start":
                if (parts.length > 1) {
                    long N = Long.parseLong(parts[1]);
                    return startN(N);
                }
                break;
            case "cancel":
                if (parts.length > 1) {
                    long N = Long.parseLong(parts[1]);
                    return cancelN(N);
                }
                break;
            case "running":
                return running();
            case "get":
                if (parts.length > 1) {
                    long N = Long.parseLong(parts[1]);
                    return getN(N);
                }
                break;
            case "after":
                if (parts.length > 2) {
                    long N = Long.parseLong(parts[1]);
                    long M = Long.parseLong(parts[2]);
                    return afterNM(N, M);
                }
                break;
            case "finish":
                return finish();
            case "abort":
                return abort();
        }
        return "Invalid command";
    }

    @Override
    public void onTaskCompleted(long N, int result) {
        taskMap.remove(N);
        resultMap.put(N, (long) result);

        // Start any tasks dependent on N
        List<Long> dependents = taskDependencies.getOrDefault(N, new ArrayList<>());
        for (Long dependent : dependents) {
            startN(dependent);
        }
        // Optionally, remove N from the dependency map
        taskDependencies.remove(N);
    }


    @Override
    public void onTaskCancelled(long N) {

    }

    private String startN(long N) {
        // Check if the task is already running
        if (taskMap.containsKey(N)) {
            return "started " + N;
        }

        // Cast SlowCalculator to Callable<Integer> to resolve ambiguity
        Future<?> future = executorService.submit((Runnable) new SlowCalculator(N, this));
        taskMap.put(N, future);

        return "started " + N;
    }

    private String cancelN(long N) {
        Future<?> future = taskMap.get(N);
        if (future != null) {
            future.cancel(true); // Attempts to interrupt the task
            taskMap.remove(N);
            cancelMap.put(N, true);
            return "cancelled " + N;
        }
        return "Cancel failed: " + N + " not found";
    }

    // return a message indicating the total number
    //of calculations currently running (i.e. excluding those already completed/cancelled), and
    //their inputs N (in any order), in the form
    //“3 calculations running: 83476 1000 176544”.
    //If no calculations are running, return the string “no
    //calculations running”
    private String running() {
        if (taskMap.isEmpty()) {
            return "no calculations running";
        }
        String runningTasks = taskMap.keySet().stream()
                .map(N -> N.toString())
                .collect(Collectors.joining(" "));
        return taskMap.size() + " calculations running: " + runningTasks;
    }



    // if the calculation for N is finished, return message
    //“result is M ” where M is the integer result; if
    //the calculation is not yet finished, return message
    //“calculating”. If the calculation was started but
    //already cancelled, return message “cancelled”
    private String getN(long N) {
        if (resultMap.containsKey(N)) {
            return "result is " + resultMap.get(N);
        } else if (taskMap.containsKey(N)) {
            return "calculating";
        } else if (cancelMap.containsKey(N)) {
            return "cancelled";
        }
        return "Can't get " + N + ", not found";
    }



    // schedule the calculation for M to start when that for
    //N finishes (or is cancelled). Return the message “M
    //will start after N ” immediately (without waiting for either calculation). The calculation for M
    //should not appear in running until it is actually running (i.e. N has completed)
    private String afterNM(long N, long M) {
        taskDependencies.computeIfAbsent(N, k -> new ArrayList<>()).add(M);
        return M + " will start after " + N;
    }



    // wait for all calculations previously requested by the
    //user (including those scheduled with after) to finish, and then after they are all completed, return
    //message “finished”
    private String finish() {
        // First, wait for all currently running tasks to complete
        for (Future<?> future : taskMap.values()) {
            try {
                future.get(); // Wait for each task to complete
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                // Optionally log the exception or handle it as needed
            }
        }

        // Now, check if there are any new tasks that have been started as dependencies
        // and wait for them as well. Repeat the process until no new tasks are started.
        boolean newTasksStarted;
        do {
            newTasksStarted = false;
            List<Future<?>> futures = new ArrayList<>();
            for (Long key : new HashSet<>(taskDependencies.keySet())) {
                if (!taskMap.containsKey(key) && taskDependencies.get(key) != null) {
                    for (Long dependentTaskId : new ArrayList<>(taskDependencies.get(key))) {
                        if (taskMap.containsKey(dependentTaskId)) {
                            futures.add(taskMap.get(dependentTaskId));
                            newTasksStarted = true;
                        }
                    }
                }
            }

            // Wait for any newly started tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    // Optionally log the exception or handle it as needed
                }
            }
        } while (newTasksStarted);

        return "finished";
    }



    // immediately stop all running calculations (and discard any scheduled using after), and then when
    //they are stopped (which should be within 0.1s) return message “aborted”
    private String abort() {
        for (Future<?> future : taskMap.values()) {
            future.cancel(true); // Attempt to cancel each running task
        }
        taskMap.clear(); // Clear all tasks
        taskDependencies.clear(); // Clear scheduled tasks
        return "aborted";
    }




}
