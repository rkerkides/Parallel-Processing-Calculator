import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Test
        CommandRunner commandRunner = new Solution();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a command or 'exit' to quit:");
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("exit")) {
                break;
            }
            System.out.println(commandRunner.runCommand(command));
        }
    }
}