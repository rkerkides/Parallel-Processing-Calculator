public class Main {
    public static void main(String[] args) {
        // Test
        CommandRunner commandRunner = new Solution();
        System.out.println(commandRunner.runCommand("start 10456060"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 10456060"));
        System.out.println(commandRunner.runCommand("start 72345680"));
        System.out.println(commandRunner.runCommand("start 534912560"));
        System.out.println(commandRunner.runCommand("get 534912560"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("cancel 72345680"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("after 534912560 30"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 30"));
        System.out.println(commandRunner.runCommand("get 72345680"));
        System.out.println(commandRunner.runCommand("get 10456060"));
        System.out.println(commandRunner.runCommand("after 10456060 5032390"));
        System.out.println(commandRunner.runCommand("running"));

        System.out.println("\nWaiting to finish tasks...");
        if (commandRunner.runCommand("finish").equals("finished")) {
            System.out.println("All tasks finished");
            System.out.println(commandRunner.runCommand("get 534912560"));
            System.out.println(commandRunner.runCommand("get 72345680"));
            System.out.println(commandRunner.runCommand("get 10456060"));
            System.out.println(commandRunner.runCommand("running"));
            System.out.println(commandRunner.runCommand("get 5032390"));
            System.out.println(commandRunner.runCommand("start 1045606210"));
            System.out.println(commandRunner.runCommand("running"));
            System.out.println(commandRunner.runCommand("get 1045606210"));
            System.out.println(commandRunner.runCommand("abort"));
            System.out.println(commandRunner.runCommand("running"));
        }

        System.out.println("\nTest 2");
        System.out.println(commandRunner.runCommand("start 12312312312"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 12312312312"));
        System.out.println(commandRunner.runCommand("after 12312312312 52349"));
        System.out.println(commandRunner.runCommand("cancel 12312312312"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 52349"));
        System.out.println(commandRunner.runCommand("running"));

        System.out.println("\nWaiting to finish tasks...");
        if (commandRunner.runCommand("finish").equals("finished")) {
            System.out.println("All tasks finished");
            System.out.println(commandRunner.runCommand("get 12312312312"));
            System.out.println(commandRunner.runCommand("get 52349"));
            System.out.println(commandRunner.runCommand("running"));
            System.out.println(commandRunner.runCommand("start 10456061210"));
            System.out.println(commandRunner.runCommand("start 104315606210"));
            System.out.println(commandRunner.runCommand("start 146210"));
            System.out.println(commandRunner.runCommand("start 423410"));
            System.out.println(commandRunner.runCommand("start 45606210"));
            System.out.println(commandRunner.runCommand("running"));
            System.out.println(commandRunner.runCommand("abort"));
            System.out.println(commandRunner.runCommand("running"));
            System.out.println(commandRunner.runCommand("finish"));
            System.out.println(commandRunner.runCommand("get 45606210"));
        }
    }
}