import java.util.Random;

public class Student {

    private final int studentId;
    private final String studentName;
    private Random random;

    private long time; // to ensure the process time

    public Student(int studentId, String studentName){
        this.studentId = studentId;
        this.studentName = studentName;
        this.random = new Random();
        this.time = System.currentTimeMillis();
    }

    public int getStudentId(){
        return studentId;
    }

    public String getStudentName(){
        return studentName;
    }

    public boolean submitExam(String assignmentName){
        System.out.println(this.studentName + " with ID -> " + this.studentId + " starting submission process for " + assignmentName );

        try{
            Thread.sleep(random.nextInt(10) + 1);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println(Thread.currentThread().getName() + " interrupted while submitting assignments (-_-)");
        }

        long submissionProcessTime = System.currentTimeMillis() - this.time;

        int chance = random.nextInt(100); // simulate the success or failure

        if (chance < 5){
            System.out.println(this.studentName + " with ID -> " + this.studentId + " couldn't submission process for " + assignmentName );
            return false; // to simulate the failure submissions

        }else {
            System.out.println(this.studentName + " with ID -> " + this.studentId + " successfully finished submission (*_*): " + assignmentName + " in " + submissionProcessTime + " ms");
            return true; // to simulate success submissions
        }
    }
}
