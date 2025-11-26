package gradable;

import subject.Subject;

import java.time.LocalDate;

public class Grade implements Gradable {
    static int gradeCounter;
    private String gradeId;
    private String studentId;
    private Subject subject;
    private double grade;
    private String date = String.valueOf(LocalDate.now());

    public Grade(String studentId, Subject subject, double grade) {
        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;
    }

    public void displayGradeDetails(){

    }

    public void getLetterGrade(){

    }


    @Override
    public boolean recordGrade(double grade) {
        return false;
    }

    @Override
    public boolean validateGrade(double grade) {
        return false;
    }

    public static int getGradeCounter() {
        return gradeCounter;
    }

    public static void setGradeCounter(int gradeCounter) {
        Grade.gradeCounter = gradeCounter;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
