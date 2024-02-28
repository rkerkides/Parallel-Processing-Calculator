public interface TaskCompletionListener {
    void onTaskCompleted(long N, int result);
    void onTaskCancelled(long N);
}
