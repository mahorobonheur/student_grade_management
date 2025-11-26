package student;

import java.util.Scanner;

public class StudentManager {
    private Student[] student = new Student[200];
    private int studentCount;

     Scanner scanner = new Scanner(System.in);

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
                boolean validate = true;
                for(char c : studentName.toCharArray()){
                    if(!Character.isLetter(c) && c != ' '){
                        validate = false;
                        break;
                    }

                }
                if(validate) break;
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

            boolean valid = true;

            if (p.length() < 10 || p.length() > 13) {
                valid = false;
            }

            for (char c : p.toCharArray()) {
                if (!Character.isDigit(c)) {
                    valid = false;
                    break;
                }
            }

            if (valid) break;

            System.out.print("Invalid phone number (10–13 digits): ");
            phone = scanner.nextLine().trim();
        }

        System.out.println("Student Type: ");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition");
        int studentTypeChoice = scanner.nextInt();
        while(studentTypeChoice < 1 || studentTypeChoice > 2){
            System.out.println("Invalid choice! Please enter choice between 1 or 2.");
            studentTypeChoice = scanner.nextInt();
        }
        scanner.nextLine();

        if(studentTypeChoice == 1){
            RegularStudent newStudent = new RegularStudent(studentName, age, email, phone);
            newStudent.setStudentId(String.format("STU%03d", studentCount +1));
            if (studentCount >= student.length) {
                System.out.println("Cannot add more grades. Storage is full.");
                return;
            }
            student[studentCount] = newStudent;
            studentCount++;


            System.out.println("✅ Student added successfully!");
            newStudent.displayStudentDetails();
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        } else {

            HonorsStudent newStudent = new HonorsStudent(studentName, age, email, phone);
            newStudent.setStudentId(String.format("STU%03d", studentCount +1));
            student[studentCount] = newStudent;
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

    }

    public void getAverageClassGrade(){

    }

    public int getStudentCount() {
        return studentCount;
    }
}
