public class Main {
    public static void main(String[] args) {
        // Test
        CommandRunner commandRunner = new Solution();
        System.out.println(commandRunner.runCommand("start 1386"));
        System.out.println(commandRunner.runCommand("start 30"));
        System.out.println(commandRunner.runCommand("start 20"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("get 1386"));
        System.out.println(commandRunner.runCommand("cancel 20"));
        System.out.println(commandRunner.runCommand("get 30"));
        System.out.println(commandRunner.runCommand("get 20"));
        System.out.println(commandRunner.runCommand("running"));
        System.out.println(commandRunner.runCommand("finish"));
        System.out.println(commandRunner.runCommand("get 1386"));
       /* System.out.println(commandRunner.runCommand("cancel 10"));
        System.out.println(commandRunner.runCommand("get 10"));*/
    }
}