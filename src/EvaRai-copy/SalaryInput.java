import java.util.Scanner;

public class SalaryInput {
    public static void main(String[] args) {
        // Create a Scanner object to read input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter their salary
        System.out.println("Enter your salary:");

        // Read the salary input as a double
        double salary = scanner.nextDouble();

        // Display the entered salary
        System.out.println("Your salary is: " + salary);

        // Close the scanner to free resources
        scanner.close();
    }
}
