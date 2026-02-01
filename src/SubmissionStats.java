import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SubmissionStats {

    // atomic counters to track the success and failed submissions safely
    private final AtomicInteger successfulSubmissions;
    private final AtomicInteger failedSubmissions;

    // atomic timestamps to track process start and end time safely
    private final AtomicLong startProcessTime;
    private final AtomicLong endProcessTime;

    public SubmissionStats() {
        this.successfulSubmissions = new AtomicInteger(0);
        this.failedSubmissions = new AtomicInteger(0);
        this.startProcessTime = new AtomicLong(0);
        this.endProcessTime = new AtomicLong(0);
    }

    public void successfulSubmissions() {
        successfulSubmissions.incrementAndGet(); // increment the successful submissions
    }

    public void failedSubmission() {
        failedSubmissions.incrementAndGet(); // increment the failed submissions
    }

    public int getSuccessfulSubmissions() {
        return successfulSubmissions.get();
    }

    public int getFailedSubmissions() {
        return failedSubmissions.get();
    }

    public int getTotalSubmission() {
        return successfulSubmissions.get() + failedSubmissions.get();
    }

    public double getSuccessfulSubmissionRate() {
        int total = successfulSubmissions.get() + failedSubmissions.get();

        if(total == 0){
            return 0.0;
        }else {
            return (successfulSubmissions.get() * 100.0 / getTotalSubmission());
        }
    }

    public double getFailedSubmissionRate() {
        int total = successfulSubmissions.get() + failedSubmissions.get();

        if (total == 0){
            return 0.0;
        }else {
            return (failedSubmissions.get() * 100.0 / getTotalSubmission());
        }
    }

    public void setStartProcessTime() {
        this.startProcessTime.set(System.currentTimeMillis());
    }

    public void setEndProcessTime() {
        this.endProcessTime.set(System.currentTimeMillis());
    }

    public double getTotalProcessTime() {
        return (this.endProcessTime.get() - this.startProcessTime.get());
    }

    public double getSubmissionsPerSecond() {
        double totalProcessTime = (endProcessTime.get() - startProcessTime.get()) / 1000.0;
        int totalSubmissions = successfulSubmissions.get() + failedSubmissions.get();

        if(totalProcessTime == 0){
            return 0.0;
        }else {
            return (totalSubmissions / totalProcessTime);
        }
    }

    public void printResultSummary() {

        int totalSubmissions = successfulSubmissions.get() + failedSubmissions.get();
        double totalProcessTime = (endProcessTime.get() - startProcessTime.get()) / 1000.0;

        System.out.println();
        System.out.println("+---------------------------------------+");
        System.out.println("|       Submission Statistics Summary    |");
        System.out.println("+---------------------------------------+");
        System.out.println("| Total Submissions:       " + totalSubmissions);
        System.out.println("| Successful Submissions:  " + getSuccessfulSubmissions());
        System.out.println("| Failed Submissions:      " + getFailedSubmissions());
        System.out.println("| Success Submission Rate: " + getSuccessfulSubmissionRate() + " %");
        System.out.println("| Failed Submission Rate:  " + getFailedSubmissionRate() + " %");
        System.out.println("| Total Time Taken:        " + totalProcessTime + " seconds");
        System.out.println("| Submission Rate per sec: " + getSubmissionsPerSecond() + " submissions/second");
        System.out.println("+---------------------------------------+");

    }
}
