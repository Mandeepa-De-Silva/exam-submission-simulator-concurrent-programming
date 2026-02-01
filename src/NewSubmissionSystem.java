import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NewSubmissionSystem {

    private final ExecutorService executor;
    private int poolSize;
    public SubmissionStats submissionStats;

    public NewSubmissionSystem(int poolSize){
        this.poolSize = poolSize;
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.submissionStats = new SubmissionStats();
    }


    public void processSubmissionsWithFutureObject(int numberOfSubmissions){
        submissionStats.setStartProcessTime();
        System.out.println("Starting process all submissions concurrently with thread pool size: " + poolSize);

        List<Future<Boolean>> futureList = new ArrayList<>();
        for(int i = 0; i < numberOfSubmissions; i++){
            final int index = i + 1;
            Future<Boolean> future = executor.submit(() -> {
                Student student = new Student(index, "Student - " + index);
                boolean status = false; // initial submission status
                int retryCount = 0;
                final int maxRetryCount = 3; // maximum retry attempts
                while(!status && retryCount < maxRetryCount) { // retry if the submission failed
                    try {
                        status = student.submitExam("Assignment -" + index);
                        if (!status) { // if submission failed, increment retry count and wait before retrying
                            retryCount++;
                            System.out.println("Student - " + index + " with ID -> " + index + " will start " + retryCount + " retry after 5 ms");
                            Thread.sleep(5); // wait before retrying
                        }
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                        System.err.println("Interrupted while processing (-_-)");
                    }
                }
                return status;
            });
            futureList.add(future);
        }

        for(Future<Boolean> future : futureList){
            try{
                boolean status = future.get();
                if(status){
                    submissionStats.successfulSubmissions();
                }else {
                    submissionStats.failedSubmission();
                }
            }catch (InterruptedException | ExecutionException e){
                Thread.currentThread().interrupt();
                System.err.println("Interrupted");
            }
        }
        submissionStats.setEndProcessTime();
        System.out.println("Finished all submissions concurrently (*_*)");
        submissionStats.printResultSummary();

    }

    public void processSubmissions(int numberOfSubmissions) {
        submissionStats.setStartProcessTime();
        System.out.println("Starting process all submissions concurrently with thread pool size: " + poolSize);

        // used to block the main thread until all submissions tasks are complete
        CountDownLatch latch = new CountDownLatch(numberOfSubmissions);
        for(int i = 0; i < numberOfSubmissions; i++) {
            final int index = i + 1;
            executor.submit(() -> {
                Student student = new Student(index, "Student - " + index);
                boolean status = false; // initial submission status
                int retryCount = 0;
                final int maxRetryCount = 3; // maximum retry attempts
                while(!status && retryCount < maxRetryCount) { // retry if the submission failed
                    try {
                        status = student.submitExam("Assignment -" + index);
                        if (!status) { // if submission failed, increment retry count and wait before retrying
                            retryCount++;
                            System.out.println("Student - " + index + " with ID -> " + index + " will start " + retryCount + " retry after 5 ms");
                            Thread.sleep(5); // wait before retrying
                        }
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                        System.err.println("Interrupted while processing (-_-)");
                    }
                }

                if (status) { // update the submission statistics
                    submissionStats.successfulSubmissions();
                } else {
                    submissionStats.failedSubmission();
                }
                latch.countDown(); // decrement by 1
            });
        }

        try {
            latch.await(); // this is the synchronization barrier, and wait for all threads to complete
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while processing (-_-)");
        }

        submissionStats.setEndProcessTime();
        System.out.println("Finished all submissions concurrently (*_*)");
        submissionStats.printResultSummary();
    }

    public void processSubmissionSequentially(int numberOfSubmissions) {
        submissionStats = new SubmissionStats(); // reset the statistics for sequential processing
        submissionStats.setStartProcessTime();
        System.out.println("Processing all submission sequentially with pool size: " + poolSize);

        for(int i = 0; i < numberOfSubmissions; i ++) {
            final int index = i + 1;
            Student student = new Student(index, " Student - " + index);
            boolean status = student.submitExam("Assignment - " + index);
            if(status) {
                submissionStats.successfulSubmissions();
            }else {
                submissionStats.failedSubmission();
            }
        }

        submissionStats.setEndProcessTime();
        System.out.println("Finished all submissions sequentially (*_*)");
        submissionStats.printResultSummary();
    }

    public void shutdownProcess() {
        executor.shutdown(); // stop accepting new tasks
        try{
            if(!executor.awaitTermination(60, TimeUnit.SECONDS)) { // wait for existing tasks to terminate
                executor.shutdownNow();
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while shutting down (-_-)");
        }

        System.out.println("Successfully shutting down system (*_*)");
    }
}
