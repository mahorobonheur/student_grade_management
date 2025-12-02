package org.example.grade;

import org.example.newImprementations.FileExporter;
import org.example.student.RegularStudent;
import org.example.student.Student;
import org.example.student.StudentManager;
import org.example.subject.CoreSubject;
import org.example.subject.ElectiveSubject;
import org.example.subject.Subject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GradeManager {
    private Grade[] grades = new Grade[200];
    private int gradeCount;
    private StudentManager studentManager;
    Scanner scanner = new Scanner(System.in);

    private Subject[] subject ={
            new CoreSubject("Mathematics", "MAT101"),
            new CoreSubject("English", "EN101"),
            new CoreSubject("Science", "SC101"),
            new ElectiveSubject("Music", "MU101"),
            new ElectiveSubject("Art", "AR101"),
            new ElectiveSubject("Physical Education", "PE101")
    };

    public GradeManager(){
    }

    public GradeManager(StudentManager studentManager){
        this.studentManager = studentManager;
    }

    public void addGrade(Grade theGrade) {
        System.out.println("RECORD GRADE");
        System.out.println("__________________________");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        Student student = studentManager.findStudent(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " Not found!");
            return;
        }

        System.out.println("Student Details:");
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.printf("Current Average: %.1f%% " , calculateOverallAverage(id));
        System.out.println();

        System.out.println("Subject Type:");
        System.out.println("1. Core Subject (Mathematics, English, Science)");
        System.out.println("2. Elective Subject (Music, Arts, Physical Education)");
        System.out.print("Select Type (1–2): ");

        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1 || choice == 2) break;
            } catch (Exception ignored) { }
            System.out.print("Invalid! Enter 1 or 2: ");
        }

        Subject[] filtered = new Subject[3];
        int fCount = 0;

        for (Subject s : subject) {
            if (choice == 1 && s.getSubjectType().equals("Core")) {
                filtered[fCount++] = s;
            }
            else if (choice == 2 && s.getSubjectType().equals("Elective")) {
                filtered[fCount++] = s;
            }
        }

        System.out.println(choice == 1 ? "Available Core Subjects:" : "Available Elective Subjects:");
        for (int i = 0; i < fCount; i++) {
            System.out.println((i + 1) + ". " + filtered[i].getSubjectName());
        }

        System.out.print("Select Subject (1–" + fCount + "): ");
        int subjectChoice;

        while (true) {
            try {
                subjectChoice = Integer.parseInt(scanner.nextLine());
                if (subjectChoice >= 1 && subjectChoice <= fCount) break;
            } catch (Exception ignored) { }
            System.out.print("Invalid! Choose between 1–" + fCount + ": ");
        }

        Subject selectedSubject = filtered[subjectChoice - 1];

        System.out.print("Enter grade (0–100): ");
        double gradeValue;

        while (true) {
            try {
                gradeValue = Double.parseDouble(scanner.nextLine());
                if (gradeValue >= 0 && gradeValue <= 100) break;
            } catch (Exception ignored) { }
            System.out.print("Invalid! Enter grade between 0–100: ");
        }

        Grade existing = findExistingGrade(student.getStudentId(), selectedSubject.getSubjectCode());

        if (existing != null) {
            System.out.println("\nA grade for this subject already exists!");
            System.out.println("Current Marks: " + existing.getGrade() + "%");

            System.out.print("Do you want to UPDATE the marks? (Y/N): ");
            String update = scanner.nextLine().trim();

            while (!update.equalsIgnoreCase("Y") && !update.equalsIgnoreCase("N")) {
                System.out.print("Invalid! Enter Y or N: ");
                update = scanner.nextLine().trim();
            }

            if (update.equalsIgnoreCase("Y")) {
                existing.setGrade(gradeValue);
                System.out.println("Grade updated successfully!");

                // Update student file after grade update
                double newAverage = calculateOverallAverage(id);
                int totalSubjects = getRegisteredSubjects(id);
                FileExporter.updateStudentGradeFile(student, existing, newAverage, totalSubjects);

            } else {
                System.out.println("Update cancelled.");
            }

            System.out.println("Press enter to continue.");
            scanner.nextLine();
            return;
        }

        theGrade = new Grade(student.getStudentId(), selectedSubject, gradeValue);
        theGrade.setGradeId(String.format("GRD%03d", gradeCount + 1));

        System.out.println("\nGRADE CONFIRMATION");
        System.out.println("_________________________________");
        System.out.println("Grade ID: " + theGrade.getGradeId());
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Subject: " + selectedSubject.getSubjectName());
        System.out.println("Grade: " + gradeValue + "%");
        System.out.println("Date: " + theGrade.getDate());
        System.out.print("Confirm grade? (Y/N): ");

        String confirm = scanner.nextLine().trim();

        while (!confirm.equalsIgnoreCase("Y") && !confirm.equalsIgnoreCase("N")) {
            System.out.print("Invalid! Please enter Y or N: ");
            confirm = scanner.nextLine().trim();
        }

        if (confirm.equalsIgnoreCase("Y")) {
            if (gradeCount >= grades.length) {
                System.out.println("Cannot add more grades. Storage is full.");
                return;
            }
            grades[gradeCount++] = theGrade;
            System.out.println("Grade saved successfully!");

            // Update student file after adding grade
            double newAverage = calculateOverallAverage(id);
            int totalSubjects = getRegisteredSubjects(id);
            FileExporter.updateStudentGradeFile(student, theGrade, newAverage, totalSubjects);

            System.out.println("Press enter to continue.");
            scanner.nextLine();
        } else {
            System.out.println("Grade recording cancelled.");
            System.out.println("Press enter to continue.");
            scanner.nextLine();
        }
    }

    public void viewGradeByStudent(String studentId){
        System.out.println("VIEW GRADE");
        System.out.println("__________________________");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        Student student = studentManager.findStudent(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " Not found!");
            return;
        }

        int foundCount = 0;
        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g != null && g.getStudentId().equals(id)) {
                foundCount++;
            }
        }

        if (foundCount == 0) {
            System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
            System.out.println("Type: " + student.getStudentType());

            double passingRequirement = (student instanceof RegularStudent) ? 50 : 60;
            System.out.println("Passing: " + (int) passingRequirement + "%");
            System.out.println();
            System.out.println("----------------------------------------------------------");
            System.out.println("No grades recorded for this student.");
            System.out.println("----------------------------------------------------------");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");

        double passingRequirement = (student instanceof RegularStudent) ? 50 : 60;
        System.out.printf("Current Average: %.1f%%", calculateOverallAverage(id));
        if(calculateOverallAverage(id) < passingRequirement){
            System.out.println("\nStatus: "  + "Failing ❌" );
        } else {
            System.out.println("\nStatus: " + "Passing ✅");
        }

        System.out.println("\nGRADE HISTORY");
        System.out.println("_________________________________________________________________________________________________");
        System.out.printf("%-15s | %-20s | %-12s | %-12s | %-10s %n", "GRD ID","DATE", "SUBJECT", "TYPE", "GRADE");
        System.out.println("-------------------------------------------------------------------------------------------------");

        int count = 0;
        for (int i = gradeCount - 1; i >= 0; i--) {
            Grade g = grades[i];
            if (g != null && g.getStudentId().equals(id)) {
                g.displayGradeDetails();
                count++;
            }
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("Total Grades: " + count);
        double coreAvg = calculateCoreAverage(id);
        double electiveAvg = calculateElectiveAverage(id);
        double overallAvg = calculateOverallAverage(id);

        System.out.printf("Core subjects Average: %.1f%%\n", coreAvg);
        System.out.printf("Elective Subjects Average: %.1f%%\n", electiveAvg);
        System.out.printf("Overall Average: %.1f%%\n", overallAvg);

        System.out.println();
        System.out.println("Performance Summary: ");

        if (coreAvg >= passingRequirement) {
            System.out.println("✅ Passing all core subjects (Required: " + (int) passingRequirement + "%)");
        } else {
            System.out.println("❌ Not passing core subjects (Required: " + (int) passingRequirement + "%)");
        }

        if (overallAvg >= passingRequirement) {
            System.out.println("✅ Meeting overall passing requirement (" + (int) passingRequirement + "%)");
        } else {
            System.out.println("❌ Not meeting overall passing requirement (" + (int) passingRequirement + "%)");
        }

        System.out.println("----------------------------------------------------------");
        System.out.print("Export this report to file? (Y/N): ");
        String exportChoice = scanner.nextLine().trim().toUpperCase();

        if (exportChoice.equals("Y")) {
            exportStudentGradeReport(id);
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    public double calculateCoreAverage(String studentId){
        int countCore = 0;
        int coreTotal = 0;
        for(int i = 0; i < grades.length; i++){
            if(grades[i] != null && grades[i].getStudentId().equals(studentId) && grades[i].getSubject().getSubjectType().equals("Core")){
                coreTotal += grades[i].getGrade();
                countCore++;
            }
        }

        double averageCore = (countCore > 0) ? (double) coreTotal / countCore : 0;
        return averageCore;
    }

    public double calculateElectiveAverage(String studentId){
        int electiveCount = 0;
        int electiveTotal = 0;

        for(int i = 0; i < grades.length; i++){
            if(grades[i] != null && grades[i].getStudentId().equals(studentId) && grades[i].getSubject().getSubjectType().equals("Elective")){
                electiveTotal += grades[i].getGrade();
                electiveCount++;
            }
        }

        double averageElective = (electiveCount > 0) ? (double) electiveTotal / electiveCount : 0;
        return averageElective;
    }

    public double calculateOverallAverage(String studentId){
        int totalGrades = 0;
        int countGrades = 0;
        for(int i = 0; i < grades.length; i++){
            if(grades[i] != null && grades[i].getStudentId().equals(studentId)){
                totalGrades += grades[i].getGrade();
                countGrades ++;
            }
        }

        double overallAverage = (countGrades > 0) ? (double) totalGrades/ countGrades : 0;
        return overallAverage;
    }

    public int getGradeCount(){
        return gradeCount;
    }

    public int getRegisteredSubjects(String studentId) {
        String[] subjectCodes = new String[50];
        int count = 0;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g != null && g.getStudentId().equals(studentId)) {
                String code = g.getSubject().getSubjectCode();

                boolean exists = false;
                for (int j = 0; j < count; j++) {
                    if (subjectCodes[j].equals(code)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    subjectCodes[count++] = code;
                }
            }
        }

        return count;
    }

    private Grade findExistingGrade(String studentId, String subjectCode) {
        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g != null &&
                    g.getStudentId().equals(studentId) &&
                    g.getSubject().getSubjectCode().equals(subjectCode)) {
                return g;
            }
        }
        return null;
    }

    public void exportGradeReport() {
        System.out.println("EXPORT GRADE REPORT");
        System.out.println("__________________________");

        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        Student student = studentManager.findStudent(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " not found!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nStudent Details:");
        System.out.println("ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType());

        // Collect grades for this student
        List<Grade> studentGrades = new ArrayList<>();
        int totalGrades = 0;
        double totalScore = 0;

        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g != null && g.getStudentId().equals(id)) {
                studentGrades.add(g);
                totalGrades++;
                totalScore += g.getGrade();
            }
        }

        if (studentGrades.isEmpty()) {
            System.out.println("No grades found for this student.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        double average = totalScore / totalGrades;

        System.out.println("Export options:");
        System.out.println("1. Summary Report (Overview Only)");
        System.out.println("2. Detailed Report (All grades)");
        System.out.println("3. Both");

        System.out.print("\nEnter choice (1-3): ");
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 3) break;
                else System.out.print("Invalid! Enter 1, 2, or 3: ");
            } catch (Exception ignored) {
                System.out.print("Invalid! Enter a number (1-3): ");
            }
        }

        System.out.print("\nEnter file name without extension: ");
        String baseFileName = scanner.nextLine().trim();

        // Generate student-specific filename
        String studentFileName = student.getName().toLowerCase()
                .replace(" ", "_")
                .replaceAll("[^a-z_]", "") + "_report";

        // Export based on choice
        List<File> exportedFiles = new ArrayList<>();

        if (choice == 1 || choice == 3) {
            File summaryFile = FileExporter.exportStudentToFile(
                    student,
                    average,
                    studentGrades.size(),
                    baseFileName + "_summary",
                    false
            );
            if (summaryFile != null) {
                exportedFiles.add(summaryFile);
            }
        }

        if (choice == 2 || choice == 3) {
            File detailedFile = FileExporter.exportStudentToFile(
                    student,
                    average,
                    studentGrades.size(),
                    baseFileName + "_detailed",
                    true
            );
            if (detailedFile != null) {
                exportedFiles.add(detailedFile);
            }
        }

        // Display file information
        if (!exportedFiles.isEmpty()) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("EXPORT COMPLETED SUCCESSFULLY!");
            System.out.println("=".repeat(60));

            for (File file : exportedFiles) {
                System.out.println("\n📁 File Information:");
                System.out.println("   File: " + file.getName());
                System.out.println("   Size: " + file.length() + " bytes");
                System.out.println("   Location: " + file.getAbsolutePath());

                // Read and display what the file contains
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    System.out.println("   Contains:");
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < 8) {
                        if (line.trim().isEmpty()) continue;
                        System.out.println("     • " + line.trim());
                        lineCount++;
                    }
                    if (lineCount == 8) {
                        System.out.println("     • ... (more content truncated)");
                    }
                } catch (IOException e) {
                    System.out.println("   Could not read file contents for preview.");
                }
            }

            // Also save to the student-specific file format
            File studentSpecificFile = FileExporter.exportStudentToFile(
                    student,
                    average,
                    studentGrades.size(),
                    studentFileName,
                    true
            );

            if (studentSpecificFile != null) {
                System.out.println("\n📝 Also saved to student-specific file:");
                System.out.println("   File: " + studentSpecificFile.getName());
                System.out.println("   This file will be updated with future grades.");
            }
        } else {
            System.out.println("No files were exported.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void exportAllGradesToCSV() {
        System.out.println("EXPORT ALL GRADES TO CSV");
        System.out.println("__________________________");

        if (gradeCount == 0) {
            System.out.println("No grades available to export!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Get student data from studentManager
        Student[] students = new Student[0];
        int studentCount = 0;

        try {
            java.lang.reflect.Field studentField = StudentManager.class.getDeclaredField("student");
            studentField.setAccessible(true);
            students = (Student[]) studentField.get(studentManager);

            java.lang.reflect.Field studentCountField = StudentManager.class.getDeclaredField("studentCount");
            studentCountField.setAccessible(true);
            studentCount = (int) studentCountField.get(studentManager);
        } catch (Exception e) {
            System.out.println("Error accessing student data: " + e.getMessage());
        }

        FileExporter.exportAllGradesToCSV(grades, gradeCount, students, studentCount);

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    public void exportStudentGradeReport(String studentId) {
        Student student = studentManager.findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        double averageGrade = calculateOverallAverage(studentId);
        int subjectCount = getRegisteredSubjects(studentId);

        String studentFileName = student.getName().toLowerCase()
                .replace(" ", "_")
                .replaceAll("[^a-z_]", "") + "_report";

        File exported = FileExporter.exportStudentToFile(
                student,
                averageGrade,
                subjectCount,
                studentFileName,
                true
        );

        if (exported != null) {
            System.out.println("Student report exported: " + exported.getAbsolutePath());
        } else {
            System.out.println("Failed to export student report.");
        }
    }
}