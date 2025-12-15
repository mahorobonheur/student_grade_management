package org.example;

import org.example.grade.GradeManager;
import org.example.service.BulkImportService;
import org.example.student.StudentManager;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This the main class that will be used to call other classes in order to run the application.
 * I chose to use switch case to choose an option and that option corresponds to a function call from other class that performs that action
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager(studentManager);
        studentManager.setGradeManager(gradeManager);
        BulkImportService bulkImportService = new BulkImportService();


        do{

            System.out.println("================================================");
            System.out.println("|      STUDENT GRADE MANAGEMENT - MAIN MENU     |");
            System.out.println("=================================================");
            System.out.println(" 1. Add Student");
            System.out.println(" 2. View Students");
            System.out.println(" 3. Record Grade");
            System.out.println(" 4. View Grade Report");
            System.out.println(" 5. Export Grade Report");
            System.out.println(" 6. Calculate Student GPA");
            System.out.println(" 7. Bulk Grade Import");
            System.out.println(" 8. View Class Statistics");
            System.out.println(" 9. Search Students");
            System.out.println(" 10. Exit \n");
            System.out.println("Enter choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                while (choice < 0 || choice > 10) {
                    System.out.println("Please enter a valid choice (1-10) : ");
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
                    gradeManager.exportGradeReport();
                    break;
                case 6:
                    gradeManager.calculateStudentGPA();
                    break;
                case 7:
                    gradeManager.bulkImportGrades();
                    break;
                case 8:
                    gradeManager.viewClassStatistics();
                    break;
                case 9:
                    gradeManager.searchStudents();
                    break;
                case 10:
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