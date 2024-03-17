import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implements a command runner and task completion listener for managing and executing long-running tasks asynchronously.
 * This class provides functionalities to start, cancel, and monitor the status of tasks using an ExecutorService.
 */
public class Solution implements CommandRunner, TaskCompletionListener{
    private final ExecutorService executorService;
    private final ConcurrentHashMap<Long, Future<?>> taskMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> resultMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Long>> taskDependencies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Boolean> cancelMap = new ConcurrentHashMap<>();

    /**
     * Constructs a Solution instance initializing the executor service to manage asynchronous tasks.
     */
    public Solution() {
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Interprets and executes a given command string, performing operations such as starting,
     * canceling tasks, and querying their state.
     *
     * @param command The command to be executed.
     * @return A string message indicating the result of the command execution.
     */
    @Override
    public String runCommand(String command) {
        String[] parts = command.split(" "); // Split the command into parts
        String cmd = parts[0]; // The actual command, e.g., "start"

        try {
            switch (cmd) {
                case "start":
                    if (parts.length == 2) {
                        long N = Long.parseLong(parts[1]);
                        return startN(N);
                    } else {
                        return "Invalid command";
                    }
                case "cancel":
                    if (parts.length == 2) {
                        long N = Long.parseLong(parts[1]);
                        return cancelN(N);
                    } else {
                        return "Invalid command";
                    }
                case "running":
                    if (parts.length == 1) {
                        return running();
                    } else {
                        return "Invalid command";
                    }
                case "get":
                    if (parts.length == 2) {
                        long N = Long.parseLong(parts[1]);
                        return getN(N);
                    } else {
                        return "Invalid command";
                    }
                case "after":
                    if (parts.length == 3) {
                        long N = Long.parseLong(parts[1]);
                        long M = Long.parseLong(parts[2]);
                        return afterNM(N, M);
                    } else {
                        return "Invalid command";
                    }
                case "finish":
                    if (parts.length == 1) {
                        return finish();
                    } else {
                        return "Invalid command";
                    }
                case "abort":
                    if (parts.length == 1) {
                        return abort();
                    } else {
                        return "Invalid command";
                    }
                default:
                    return "Invalid command";
            }
        } catch (NumberFormatException e) {
            return "Invalid command"; // Catch parsing errors
        }
    }

    /**
     * Called when a task is completed. Removes the task from tracking maps and triggers any dependent tasks.
     *
     * @param N The identifier of the completed task.
     * @param result The result of the completed task.
     */
    @Override
    public void onTaskCompleted(long N, int result) {
        taskMap.remove(N);
        resultMap.put(N, (long) result);

        // Start any tasks dependent on N
        triggerDependentTasks(N);
    }

    /**
     * Starts a task with the specified identifier if it's not already running.
     *
     * @param N The identifier for the task to start.
     * @return A string message indicating the start status of the task.
     */
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

    /**
     * Attempts to cancel a task with the specified identifier. This method directly attempts
     * to interrupt the task if it's running, updates the task tracking to reflect the cancellation,
     * and triggers any dependent tasks to start, as the cancellation of a task should not
     * prevent its dependents from executing.
     *
     * @param N The identifier of the task to cancel.
     * @return A string message indicating whether the task was successfully cancelled.
     */
    private String cancelN(long N) {
        Future<?> future = taskMap.get(N);
        boolean cancelled = false;
        if (future != null) {
            cancelled = future.cancel(true); // Attempts to interrupt the task
            if (cancelled) {
                // Perform the cancellation actions only if the task was successfully cancelled
                taskMap.remove(N);
                cancelMap.put(N, true);
                // Directly handle dependent tasks here to ensure they are triggered upon cancellation
                triggerDependentTasks(N);
            }
        }
        return cancelled ? "cancelled " + N : "Failed to cancel " + N;
    }


    /**
     * Provides the current status of running tasks, including their identifiers.
     *
     * @return A string message listing running tasks or indicating that no tasks are running.
     */
    private String running() {
        if (taskMap.isEmpty()) {
            return "no calculations running";
        }
        String runningTasks = taskMap.keySet().stream()
                .map(N -> N.toString())
                .collect(Collectors.joining(" "));
        return taskMap.size() + " calculations running: " + runningTasks;
    }

    /**
     * Retrieves the result of a task with the specified identifier, if available.
     *
     * @param N The identifier of the task.
     * @return A string message indicating the task's result, its calculation status, or a cancellation notice.
     */
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

    /**
     * Schedules a task to start after the completion or cancellation of another task.
     *
     * @param N The identifier of the prerequisite task.
     * @param M The identifier of the task to start afterward.
     * @return A string message indicating the scheduling status.
     */
    private String afterNM(long N, long M) {
        taskDependencies.computeIfAbsent(N, k -> new ArrayList<>()).add(M);
        return M + " will start after " + N;
    }

    /**
     * Waits for all tasks, including those scheduled with dependencies, to complete before returning.
     *
     * @return A string message indicating that all tasks have finished.
     */
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

    /**
     * Stops all running tasks and clears any scheduled tasks, effectively aborting all operations.
     *
     * @return A string message indicating that all tasks have been aborted.
     */
    private String abort() {
        for (Future<?> future : taskMap.values()) {
            future.cancel(true); // Attempt to cancel each running task
        }
        taskMap.clear(); // Clear all tasks
        taskDependencies.clear(); // Clear scheduled tasks
        return "aborted";
    }

    /**
     * Triggers any tasks that are dependent on a given task, identified by N. This method
     * is used after a task completes or is cancelled to ensure that dependent tasks are
     * started as intended.
     *
     * @param N The identifier of the task that has completed or been cancelled.
     */
    private void triggerDependentTasks(long N) {
        List<Long> dependents = taskDependencies.remove(N);
        if (dependents != null) {
            for (Long dependent : dependents) {
                startN(dependent);
            }
        }
    }
}
