package student;

import gradable.Grade;
import gradable.GradeManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class StudentManager {
    private Student[] student = new Student[200];
    private int studentCount;

     Scanner scanner = new Scanner(System.in);
    GradeManager gradeManager = new GradeManager();


    public void addStudent(Student students){
        System.out.println("ADD STUDENT");
        System.out.println("_________________________");
        System.out.println("Enter student name: ");
        String studentName = scanner.nextLine();

        while(true) {
            if (studentName.length() < 3) {
                System.out.println("Please enter a name with more than 3 characters: ");
                studentName = scanner.nextLine();
            } else {
                boolean validateName = true;
                for(char c : studentName.toCharArray()){
                    if(!Character.isLetter(c) && c != ' '){
                        validateName = false;
                        break;
                    }

                }
                if(validateName) break;
                System.out.println("Name must contain only letters: ");
                studentName = scanner.nextLine();
            }

        }

        System.out.print("Enter age: ");
        int age = 0;

        while (true) {
            String input = scanner.nextLine().trim();
            try {
                age = Integer.parseInt(input);

                if (age >= 5 && age <= 120) break;

                System.out.print("Enter a valid age (5–120): ");
            }
            catch (NumberFormatException e) {
                System.out.print("Age must be a number: ");
            }
        }


        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        while(true){
            if(!email.contains("@") || !email.contains(".")){
                System.out.print("Email must contain '@' and '.': ");
            } else{
                int at = email.indexOf('@');
                int dot = email.lastIndexOf('.');

                   //to validate the email the @ symbol must come before dot(.)
                if (at > 0 && dot > at + 1 && dot < email.length() - 1) {
                    break;
                }

                System.out.print("Invalid email format: ");
            }
            email = scanner.nextLine().trim();
        }

        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();

        while (true) {
            String p = phone;

            if (p.startsWith("+")) {
                p = p.substring(1);
            }

            boolean validatePhone = true;

            if (p.length() < 10 || p.length() > 13) {
                validatePhone = false;
            }

            for (char c : p.toCharArray()) {
                if (!Character.isDigit(c)) {
                    validatePhone = false;
                    break;
                }
            }

            if (validatePhone) break;

            System.out.print("Invalid phone number (10–13 digits): ");
            phone = scanner.nextLine().trim();
        }

        System.out.println("Student Type: ");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
        int studentTypeChoice = 0;

        while (true) {
            try {
                studentTypeChoice = Integer.parseInt(scanner.nextLine().trim());

                if (studentTypeChoice == 1 || studentTypeChoice == 2) {
                    break;
                }

                System.out.print("Invalid choice! Enter 1 or 2: ");
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid input! Enter a NUMBER (1 or 2): ");
            }
        }

        if(studentTypeChoice == 1){
            RegularStudent newStudent = new RegularStudent(studentName, age, email, phone);
            newStudent.setStudentId(String.format("STU%03d", studentCount +1));
            newStudent.setGradeManager(this.gradeManager);
            newStudent.setStudentManager(this);
            if (studentCount >= student.length) {
                System.out.println("Cannot add more grades. Storage is full.");
                return;
            }
            student[studentCount] = newStudent;
            studentCount++;

            System.out.println("✅ Student added successfully!");
            newStudent.displayStudentDetails();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } else {

            HonorsStudent newStudent = new HonorsStudent(studentName, age, email, phone);
            newStudent.setStudentId(String.format("STU%03d", studentCount +1));
            student[studentCount] = newStudent;
            newStudent.setStudentManager(this);
            newStudent.setGradeManager(this.gradeManager);
            if (studentCount >= student.length) {
                System.out.println("Cannot add more grades. Storage is full.");
                return;
            }
            studentCount++;


            System.out.println("✅ Student added successfully!");
            newStudent.displayStudentDetails();
            System.out.println("Press Enter to continue...");
            scanner.nextLine();


        }

    }

    public Student findStudent(String studentId){
        for(int i = 0; i < studentCount; i++){
            if(student[i].getStudentId().equals(studentId)){
                return student[i];
            }

        }
        return null;

    }

    public void viewAllStudents(){

        int regularCount = 0;
        int honorsCount = 0;

        for (Student s : student) {
            if (s != null) {
                if (s.getStudentType().equals("Regular")) regularCount++;
                else if (s.getStudentType().equals("Honors")) honorsCount++;
            }
        }


        if (regularCount < 3 || honorsCount < 2) {
            System.out.println("Cannot display listing.");
            System.out.println("Minimum requirements:");
            System.out.println("- At least 3 Regular Students (current: " + regularCount + ")");
            System.out.println("- At least 2 Honors Students (current: " + honorsCount + ")");
            return;
        }


        System.out.println("STUDENT LISTING");
        System.out.println("___________________________________________________________________________________________________");
        System.out.printf("%-15s | %-25s | %-20s | %-12s | %-10s %n", "STU ID", " NAME", "TYPE", "AVG GRADE", "STATUS");
        System.out.println("---------------------------------------------------------------------------------------------------");

        int studentCount = 0;


        for(int i = 0; i < student.length; i++){
            Student stud = student[i];
           if(stud != null ){
               double averageGrades = gradeManager.calculateOverallAverage(stud.getStudentId());

               System.out.printf("%-15s | %-25s | %-20s | %-12s | %-10s%n",
                       stud.getStudentId(),
                       stud.getName(),
                       stud.getStudentType(),
                       String.format("%.1f%%", averageGrades),
                       stud.isPassing(stud.getStudentId()));


               int totalSubjects = gradeManager.getRegisteredSubjects(stud.getStudentId());
               if(stud.getStudentType().equals("Regular")) {
                   System.out.printf("%-15s | %-28s | %-35s %n", " ", "Enrolled Subjects: " +totalSubjects, "Passing Grade: " + stud.getPassingGrade() +"%");
                   System.out.println("---------------------------------------------------------------------------------------------------");
               } else{
                   if(gradeManager.calculateOverallAverage(stud.getStudentId()) >= 85){
                       HonorsStudent honorsStudent = new HonorsStudent();
                       honorsStudent.setHonorsEligible(true);
                       if(honorsStudent.isHonorsEligible()){
                           System.out.printf("%-15s | %-28s | %-28s  | %-25s %n", " ", "Enrolled Subjects: "  +totalSubjects, "Passing Grade: " + stud.getPassingGrade() + "%", "Honors Eligible");
                           System.out.println("---------------------------------------------------------------------------------------------------");

                       }
                   }
                   else {
                       System.out.printf("%-15s | %-28s | %-28s  | %-25s %n", " ", "Enrolled Subjects: "  +totalSubjects, "Passing Grade: " + stud.getPassingGrade() +"%", "Not Honors Eligible");
                       System.out.println("---------------------------------------------------------------------------------------------------");

                   }
                       }
               studentCount++;
           }

        }

        System.out.println("\nTotal Students: " +studentCount);
        System.out.printf("Average Class Grade: %.1f %% ",getAverageClassGrade());
        System.out.println();
        System.out.println("\nPress enter to continue");
        scanner.nextLine();

    }


    public double getAverageClassGrade() {
        double total = 0;
        double countedStudents = 0;

        for (Student s : student) {
            if (s != null) {
                double avg = s.calculateAverageGrade();
                if (avg > 0) {
                    total += avg;
                    countedStudents++;
                }
            }
        }

        if (countedStudents == 0) return 0;

        return total / countedStudents;
    }


    public int getStudentCount() {
        return studentCount;
    }

    public void setGradeManager(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
    }
}
