package org.example.grade;

import org.example.newImprementations.FileExporter;
import org.example.newImprementations.GPACalculator;
import org.example.service.BulkImportService;
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
import java.util.*;

public class GradeManager {
    private Grade[] grades = new Grade[200];
    private int gradeCount;
    private StudentManager studentManager;
    private BulkImportService bulkImportService = new BulkImportService();

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

            double newAverage = calculateOverallAverage(id);
            int totalSubjects = getRegisteredSubjects(id);
            FileExporter.updateStudentGradeFile(student, theGrade, newAverage, totalSubjects);
            bulkImportService.saveOrUpdateGradeCSV(student, theGrade);

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
        for(int i = 0; i < gradeCount; i++){
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

        for(int i = 0; i < gradeCount; i++){
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
        for(int i = 0; i < gradeCount; i++){
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

        String studentFileName = student.getName().toLowerCase()
                .replace(" ", "_")
                .replaceAll("[^a-z_]", "") + "_report";

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

        if (!exportedFiles.isEmpty()) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("EXPORT COMPLETED SUCCESSFULLY!");
            System.out.println("=".repeat(60));

            for (File file : exportedFiles) {
                System.out.println("\n📁 File Information:");
                System.out.println("   File: " + file.getName());
                System.out.println("   Size: " + file.length() + " bytes");
                System.out.println("   Location: " + file.getAbsolutePath());

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

    public void calculateStudentGPA() {
        System.out.println("\nCALCULATE STUDENT GPA");
        System.out.println("__________________________");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        Student student = studentManager.findStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        List<Grade> grades = getGradesByStudentId(id);
        if (grades.isEmpty()) {
            System.out.println("This student has no recorded grades.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }


        System.out.println("\n=============================================");
        System.out.println("   GPA REPORT FOR " + student.getStudentId());
        System.out.println("=============================================");
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("Overall Average: " + String.format("%.2f", calculateOverallAverage(id)) + "%");
        System.out.println();

        System.out.println("GPA Calculation (4.0 Scale)");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-25s %-12s %-10s %-10s\n", "COURSE", "PERCENTAGE", "GPA", "LETTER");
        System.out.println("------------------------------------------------------------");

        double totalGPA = 0;
        for (Grade g : grades) {
            double percentage = g.getGrade();
            double courseGPA = GPACalculator.percentageToGPA(percentage);
            String letter = GPACalculator.gpaToLetter(courseGPA);
            totalGPA += courseGPA;

            System.out.printf("%-25s %-12.2f %-10.2f %-10s\n",
                    g.getSubject().getSubjectName(),
                    percentage,
                    courseGPA,
                    letter);
        }

        System.out.println("----------------------------------------------------------------");

        double cumulativeGPA = totalGPA / grades.size();
        String cumulativeLetter = GPACalculator.gpaToLetter(cumulativeGPA);

        int rank = calculateClassRank(id);

        double classAverage = studentManager.getAverageClassGrade();
        double classGPA = GPACalculator.percentageToGPA(classAverage);

        System.out.println("\nCumulative GPA: " + String.format("%.2f", cumulativeGPA) + " / 4.0");
        System.out.println("Letter Grade: " + cumulativeLetter);
        if (rank > 0) {
            System.out.println("Class Rank: " + rank + " of " + studentManager.getStudentCount());
        }
        System.out.println();

        System.out.println("Performance Analysis:");

        if (student.getStudentType().equals("Honors")) {
            if (cumulativeGPA >= 3.5) {
                System.out.println("✓ Honors Eligibility: MAINTAINED");
            } else {
                System.out.println("⚠ Honors Eligibility: AT RISK");
                System.out.println("  Current GPA: " + String.format("%.2f", cumulativeGPA) + " | Required: 3.50");
            }
        }

        if (cumulativeGPA >= 3.7) {
            System.out.println("✓ Excellent Performance" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else if (cumulativeGPA >= 3.3) {
            System.out.println("✓ Very Good Performance" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else if (cumulativeGPA >= 3.0) {
            System.out.println("✓ Good Performance" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else if (cumulativeGPA >= 2.0) {
            System.out.println("✓ Satisfactory Performance" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else {
            System.out.println("⚠ Needs Improvement" + String.format("%.2f", cumulativeGPA) + " GPA");
        }

        if (cumulativeGPA > classGPA + 0.5) {
            System.out.println("✓ Significantly Above Class Average" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else if (cumulativeGPA > classGPA + 0.2) {
            System.out.println("✓ Above Class Average" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else if (cumulativeGPA >= classGPA - 0.2) {
            System.out.println("✓ At Class Average Level" + String.format("%.2f", cumulativeGPA) + " GPA");
        } else {
            System.out.println("⚠ Below Class Average" + String.format("%.2f", cumulativeGPA) + " GPA");
        }
        System.out.println("  Your GPA: " + String.format("%.2f", cumulativeGPA) +
                " | Class Avg GPA: " + String.format("%.2f", classGPA));

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }


    private List<Grade> getGradesByStudentId(String studentId) {
        List<Grade> studentGrades = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            Grade g = grades[i];
            if (g != null && g.getStudentId().equals(studentId)) {
                studentGrades.add(g);
            }
        }
        return studentGrades;
    }


    private int calculateClassRank(String studentId) {
        try {

            java.lang.reflect.Field studentField = StudentManager.class.getDeclaredField("student");
            studentField.setAccessible(true);
            Student[] students = (Student[]) studentField.get(studentManager);

            java.lang.reflect.Field studentCountField = StudentManager.class.getDeclaredField("studentCount");
            studentCountField.setAccessible(true);
            int totalStudents = (int) studentCountField.get(studentManager);

            List<StudentAverage> studentAverages = new ArrayList<>();
            for (int i = 0; i < totalStudents; i++) {
                Student s = students[i];
                if (s != null) {
                    double avg = calculateOverallAverage(s.getStudentId());
                    studentAverages.add(new StudentAverage(s.getStudentId(), avg));
                }
            }


            studentAverages.sort((a, b) -> Double.compare(b.average, a.average));

            for (int i = 0; i < studentAverages.size(); i++) {
                if (studentAverages.get(i).studentId.equals(studentId)) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to calculate rank");
        }
        return -1;
    }


    private static class StudentAverage {
        String studentId;
        double average;

        StudentAverage(String studentId, double average) {
            this.studentId = studentId;
            this.average = average;
        }
    }

    public void bulkImportGrades() {
        System.out.println("BULK IMPORT GRADES");
        System.out.println("---------------------------------------");

        String importDir = "./imports/";
        System.out.println("Place your CSV file in: " + importDir);
        System.out.println();
        System.out.println("CSV Format Required:");
        System.out.println("StudentID,SubjectName,SubjectType,Grade");
        System.out.println("Example: STU001,Mathematics,Core,85");
        System.out.println();

        System.out.print("Enter filename (without extension): ");
        String filename = scanner.nextLine().trim();

        File file = new File(importDir + filename + ".csv");

        if (!file.exists()) {
            System.out.println("❌ File not found: " + file.getAbsolutePath());
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nValidating file... ✓");
        System.out.println("Processing grades...\n");

        int totalRows = 0;
        int successCount = 0;
        int failCount = 0;

        List<String> failedRows = new ArrayList<>();
        List<String> logLines = new ArrayList<>();

        String logFileName = "import_log_" +
                java.time.LocalDate.now().toString().replace("-", "") + ".txt";

        File logFile = new File(logFileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                totalRows++;

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    failCount++;
                    failedRows.add("Row " + totalRows + ": Invalid format");
                    logLines.add("Row " + totalRows + " FAILED - Invalid format");
                    continue;
                }

                String studentId = parts[0].trim();
                String subjectName = parts[1].trim();
                String subjectType = parts[2].trim();
                String gradeStr = parts[3].trim();

                Student student = studentManager.findStudent(studentId);
                if (student == null) {
                    failCount++;
                    failedRows.add("Row " + totalRows + ": Invalid student ID (" + studentId + ")");
                    logLines.add("Row " + totalRows + " FAILED - Student not found: " + studentId);
                    continue;
                }

                Subject selectedSubject = null;
                for (Subject s : subject) {
                    if (s.getSubjectName().equalsIgnoreCase(subjectName)
                            && s.getSubjectType().equalsIgnoreCase(subjectType)) {
                        selectedSubject = s;
                        break;
                    }
                }

                if (selectedSubject == null) {
                    failCount++;
                    failedRows.add("Row " + totalRows + ": Invalid subject or type (" + subjectName + ")");
                    logLines.add("Row " + totalRows + " FAILED - Subject invalid: " + subjectName);
                    continue;
                }

                double gradeValue;
                try {
                    gradeValue = Double.parseDouble(gradeStr);
                    if (gradeValue < 0 || gradeValue > 100) {
                        failCount++;
                        failedRows.add("Row " + totalRows + ": Grade out of range (" + gradeStr + ")");
                        logLines.add("Row " + totalRows + " FAILED - Grade out of range: " + gradeStr);
                        continue;
                    }
                } catch (Exception e) {
                    failCount++;
                    failedRows.add("Row " + totalRows + ": Invalid grade (" + gradeStr + ")");
                    logLines.add("Row " + totalRows + " FAILED - Invalid grade: " + gradeStr);
                    continue;
                }

                Grade existing = findExistingGrade(studentId, selectedSubject.getSubjectCode());

                if (existing != null) {
                    existing.setGrade(gradeValue);
                    bulkImportService.saveOrUpdateGradeCSV(student, existing);
                    logLines.add("Row " + totalRows + " UPDATED - " + studentId + " " + subjectName);
                } else {
                    Grade g = new Grade(studentId, selectedSubject, gradeValue);
                    g.setGradeId(String.format("GRD%03d", gradeCount + 1));
                    grades[gradeCount++] = g;
                    bulkImportService.saveOrUpdateGradeCSV(student, g);
                    logLines.add("Row " + totalRows + " SUCCESS - " + studentId + " " + subjectName);
                }


                successCount++;
            }


            java.nio.file.Files.write(logFile.toPath(), logLines);

        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
            return;
        }

        System.out.println("IMPORT SUMMARY");
        System.out.println("---------------------------------------");
        System.out.println("Total Rows: " + totalRows);
        System.out.println("Successfully Imported: " + successCount);
        System.out.println("Failed: " + failCount);

        if (!failedRows.isEmpty()) {
            System.out.println("\nFailed Records:");
            for (String err : failedRows) {
                System.out.println(err);
            }
        }

        System.out.println("\n✓ Import completed!");
        System.out.println("   " + successCount + " grades added to system");
        System.out.println("   See " + logFileName + " for details");

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

}