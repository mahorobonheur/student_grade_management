import gradable.GradeManager;
import student.StudentManager;

import java.util.InputMismatchException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager(studentManager);
        studentManager.setGradeManager(gradeManager);

        do{

                System.out.println("================================================");
                System.out.println("|      STUDENT GRADE MANAGEMENT - MAIN MENU     |");
                System.out.println("=================================================");
                System.out.println(" 1. Add Student");
                System.out.println(" 2. View Students");
                System.out.println(" 3. Record Grade");
                System.out.println(" 4. View Grade Report");
                System.out.println(" 5. Exit \n");
                System.out.println("Enter choice: ");

            try {
                 choice = scanner.nextInt();
                scanner.nextLine();

                while (choice < 0 || choice > 5) {
                    System.out.println("Please enter a valid choice (1-5) : ");
                    choice = scanner.nextInt();
                }

            } catch (InputMismatchException exception){

                System.out.println("Please enter a real number");
                scanner.nextLine();
                continue;
            }


                switch (choice) {
                    case 1:

                        studentManager.addStudent(null);
                        break;
                    case 2:
                        studentManager.viewAllStudents();
                        break;
                    case 3:
                        gradeManager.addGrade(null);
                        break;
                    case 4:
                        gradeManager.viewGradeByStudent(null);
                        break;
                    case 5:
                        System.out.println("Thank you for using Student Grade Management System! \nGoodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }


        }while(true);

    }
}