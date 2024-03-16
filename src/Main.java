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
        System.out.println(commandRunner.runCommand("finish"));


        // my test data
        /*System.out.println(commandRunner.runCommand("start 10456060"));
        System.out.println(commandRunner.runCommand("start 30"));
        System.out.println(commandRunner.runCommand("start 20"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 1386"));
        System.out.println(commandRunner.runCommand("cancel 20"));
        System.out.println(commandRunner.runCommand("get 30"));
        System.out.println(commandRunner.runCommand("get 20"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 1386"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("start 5392513"));
        System.out.println(commandRunner.runCommand("after 539259 30"));
        System.out.println(commandRunner.runCommand("after 1386 20"));
        System.out.println(commandRunner.runCommand("after 1386 1386"));
        System.out.println(commandRunner.runCommand("get 10456060"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 5392513"));
        System.out.println(commandRunner.runCommand("get 10456060"));
        System.out.println(commandRunner.runCommand("start 10456060123"));
        System.out.println(commandRunner.runCommand("cancel 10456060123"));
        System.out.println(commandRunner.runCommand("finish"));*/
    }
}