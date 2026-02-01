import java.util.Scanner;

public class MenuController {

    private final Scanner scanner = new Scanner(System.in);

    private int poolSize;
    private int previousNumberOfMainSubmissions = 0;
    private int previousNumberOfComparisonSubmissions = 0;
    private double previousSequentialTime = 0;
    private double previousConcurrentTime = 0;
    private double previousSpeed = 0;

    private SubmissionStats previousMainStatistics = null;
    private SubmissionStats previousSequentialStatistics = null;
    private SubmissionStats previousConcurrentStatistics = null;

    public MenuController(int poolSize) {
        this.poolSize = poolSize;
    }

    public void runMethods(){
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getInput();

            switch (choice){
                case 1 -> runMainConcurrentSimulation();
                case 2 -> runPerformanceComparison();
                case 3 -> printPreviousStatistics();
//                case 4 -> changeSettings();
                case 4 -> showCurrentSettings();
                case 5 -> {
                    System.out.println("Thank you for using the simulator (*_*) have a good day!");
                    running = false;
                }
                default -> System.out.println("Invalid choice (-_-) please select 1 - 5");
            }
            if(running){
                System.out.println(); //
            }
        }
        scanner.close();
    }

    private void printMenu(){
        System.out.println("""
                --------------------------------------------------
                                     MAIN MENU
                --------------------------------------------------
                1. Run Main Concurrent Simulation
                2. Run Performance Comparison (Sequential vs Concurrent)
                3. View Last Recorded Statistics
                4. Show Current Settings
                5. Exit
                --------------------------------------------------
                """);
    }

    private void runMainConcurrentSimulation(){ // main concurrent simulation
        int numberOfSubmissions = getPositiveInput("Enter number of submission for main simulation (*_*): ");
        NewSubmissionSystem mainSystem = new NewSubmissionSystem(poolSize);

        System.out.println("Running main concurrent simulation with: " + numberOfSubmissions + " submissions");

        mainSystem.processSubmissions(numberOfSubmissions);
        previousNumberOfMainSubmissions = numberOfSubmissions;
        previousMainStatistics = mainSystem.submissionStats;

        mainSystem.shutdownProcess();
    }

    private void runPerformanceComparison(){ // sequential vs concurrent
        int numberOfComparisonSubmissions = getPositiveInput("Enter the number of submissions for the comparison (*_*): ");
        NewSubmissionSystem comparisonSystem = new NewSubmissionSystem(poolSize);

        // sequential comparison
        System.out.println("Running " + numberOfComparisonSubmissions + " submissions sequentially");

        long startSequentialProcessTime = System.currentTimeMillis();
        comparisonSystem.processSubmissionSequentially(numberOfComparisonSubmissions);
        long endSequentialProcessTime = System.currentTimeMillis();

        previousSequentialTime = (endSequentialProcessTime - startSequentialProcessTime) / 1000.0;
        previousSequentialStatistics = comparisonSystem.submissionStats;

        // concurrent comparison
        System.out.println("Running " + numberOfComparisonSubmissions + " submissions concurrently");

        long startConcurrentProcessTime  = System.currentTimeMillis();
        comparisonSystem.processSubmissions(numberOfComparisonSubmissions);
        long endConcurrentProcessTime = System.currentTimeMillis();

        previousConcurrentTime = (endConcurrentProcessTime - startConcurrentProcessTime) / 1000.0;
        previousConcurrentStatistics = comparisonSystem.submissionStats;

        if(previousSequentialTime > 0){
            previousSpeed =  previousSequentialTime / previousConcurrentTime;
        }
        else {
            previousSpeed = 0;
        }
        previousNumberOfComparisonSubmissions = numberOfComparisonSubmissions;

        // comparison summary
        System.out.println("\n" + "-".repeat(60));
        System.out.println("             Performance Comparison Summary (*_*)");
        System.out.println("-".repeat(60));
        System.out.println("Sequential Time : " + previousSequentialTime +  " seconds");
        System.out.println("Concurrent Time : " + previousConcurrentTime + " seconds");
        System.out.println("Speedup         : " + previousSpeed + " x");
        System.out.println("-".repeat(60));

        comparisonSystem.shutdownProcess();
    }

    private void printPreviousStatistics(){
        System.out.println("\n" + "-".repeat(60));
        System.out.println("             Previous Recorded Statistics (*_*)");
        System.out.println("-".repeat(60) + "\n");

        if(previousMainStatistics != null && previousNumberOfMainSubmissions > 0){
            System.out.println("Main concurrent simulations for: " + previousNumberOfMainSubmissions + " submissions");
            previousMainStatistics.printResultSummary();
            System.out.println();
        }

        if(previousNumberOfComparisonSubmissions > 0){
            System.out.println("------ Performance Summary ------");
            System.out.println("   Sequential Time : " + previousSequentialTime + " seconds");
            System.out.println("   Concurrent Time : " + previousConcurrentTime + " seconds");
            System.out.println("   Speedup         : " + previousSpeed + " x");
        }

        if(previousMainStatistics == null && previousSequentialStatistics == null && previousConcurrentStatistics == null){
            System.out.println("No statistics available yet (-_-), run a simulation first");
        }
    }

    private void changeSettings() {
        System.out.println("\nChange Setting");
        poolSize = getPositiveInteger("Enter new thread pool size (current: " + poolSize + "): ", poolSize);
        System.out.println("Thread pool size updated to: " + poolSize + "\n");
    }

    private void showCurrentSettings() {
        System.out.println("\n=== Current Settings (*_*) ===");
        System.out.println("Thread Pool Size                        : " + poolSize);
        System.out.println("Last number of main submissions         : " + (previousNumberOfMainSubmissions > 0 ? previousNumberOfMainSubmissions : "Not run yet"));
        System.out.println("Last number of comparison submissions   : " + (previousNumberOfComparisonSubmissions > 0 ? previousNumberOfComparisonSubmissions : "Not run yet"));
        if (previousNumberOfComparisonSubmissions > 0) {
            System.out.println("Last comparison speed               : " +  previousSpeed);
        }
        System.out.println();
    }

    private int getInput(){
        while (true) {
            System.out.print("Enter your choice between 1 - 5 (*_*): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 6) {
                    return choice;
                }
                System.out.println("Please select a number between 1 and 5 (-_-).");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (-_-).");
            }
        }
    }

    private int getPositiveInput(String prompt){
        return getPositiveInteger(prompt, -1);
    }

    private int getPositiveInteger(String prompt, int defaultIfEmpty){
        while (true){
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if(defaultIfEmpty > 0 && input.isEmpty()){
                return defaultIfEmpty;
            }

            try{
                int value = Integer.parseInt(input);
                if(value > 0){
                    return value;
                }
                System.out.println("Invalid input detected (-_-), please enter a positive valid number");
            }catch (NumberFormatException e){
                System.out.println("Invalid input (-_-), please enter a valid integer");
            }
        }
    }

}
