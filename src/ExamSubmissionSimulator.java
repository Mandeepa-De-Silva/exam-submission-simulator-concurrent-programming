public class ExamSubmissionSimulator {
    public static void main(String[] args) {

        // get the number of available processors to utilize
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;

        MenuController controller = new MenuController(poolSize);
        controller.runMethods();
    }
}
