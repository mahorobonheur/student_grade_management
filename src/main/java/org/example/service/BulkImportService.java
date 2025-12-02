package org.example.service;

import org.example.grade.Grade;
import org.example.student.Student;
import org.example.student.StudentManager;
import org.example.grade.GradeManager;
import org.example.subject.CoreSubject;
import org.example.subject.ElectiveSubject;
import org.example.subject.Subject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BulkImportService {

    public static void importGradesFromCSV(StudentManager studentManager, GradeManager gradeManager) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("BULK IMPORT GRADES FROM CSV");
        System.out.println("__________________________");
        System.out.println("Place your CSV file in the ./imports/ directory");
        System.out.print("Enter CSV filename (e.g., grades.csv): ");
        String fileName = scanner.nextLine().trim();

        String filePath = "./imports/" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("❌ File not found: " + filePath);
            System.out.println("Please make sure the file exists in the ./imports/ directory");
            return;
        }

        System.out.println("Found file: " + filePath);
        System.out.println("Starting import...");
        System.out.println();

        int importedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = parseCSVLine(line);

                    if (parts.length < 9) {
                        System.out.println("⚠️  Skipping line - insufficient columns: " + line);
                        skippedCount++;
                        continue;
                    }

                    String studentId = parts[1].trim();
                    String studentName = parts[2].trim();
                    String subjectCode = parts[4].trim();
                    String subjectName = parts[5].trim();
                    String subjectType = parts[6].trim();
                    String gradeValueStr = parts[7].trim();
                    String date = parts[8].trim();

                    // Validate student exists
                    Student student = studentManager.findStudent(studentId);
                    if (student == null) {
                        System.out.println("❌ Student not found: " + studentId + " - " + studentName);
                        skippedCount++;
                        continue;
                    }

                    // Validate grade value
                    double gradeValue;
                    try {
                        gradeValue = Double.parseDouble(gradeValueStr);
                        if (gradeValue < 0 || gradeValue > 100) {
                            System.out.println("⚠️  Invalid grade value (must be 0-100): " + gradeValue);
                            skippedCount++;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️  Invalid grade format: " + gradeValueStr);
                        skippedCount++;
                        continue;
                    }

                    // Create or find subject
                    Subject subject;
                    if (subjectType.equalsIgnoreCase("Core")) {
                        subject = new CoreSubject(subjectName, subjectCode);
                    } else if (subjectType.equalsIgnoreCase("Elective")) {
                        subject = new ElectiveSubject(subjectName, subjectCode);
                    } else {
                        System.out.println("⚠️  Unknown subject type: " + subjectType);
                        skippedCount++;
                        continue;
                    }

                    // Check if grade already exists for this student and subject
                    Grade existingGrade = findExistingGrade(gradeManager, studentId, subjectCode);

                    if (existingGrade != null) {
                        System.out.println("📝 Updating existing grade for: " +
                                studentId + " - " + subjectName);
                        existingGrade.setGrade(gradeValue);
                        existingGrade.setDate(date);
                    } else {
                        // Create new grade
                        Grade newGrade = new Grade(studentId, subject, gradeValue);
                        newGrade.setGradeId("IMP" + String.format("%03d", importedCount + 1));
                        newGrade.setDate(date);

                        // Add to grade manager (we need to access the grades array)
                        addGradeToManager(gradeManager, newGrade);

                        System.out.println("✅ Imported grade for: " +
                                studentId + " - " + subjectName +
                                " (" + gradeValue + "%)");
                    }

                    importedCount++;

                } catch (Exception e) {
                    System.out.println("❌ Error processing line: " + line);
                    System.out.println("   Error: " + e.getMessage());
                    errorCount++;
                }
            }

            System.out.println();
            System.out.println("=".repeat(50));
            System.out.println("IMPORT SUMMARY");
            System.out.println("=".repeat(50));
            System.out.println("Successfully imported: " + importedCount + " grades");
            System.out.println("Skipped: " + skippedCount + " records");
            System.out.println("Errors: " + errorCount + " records");
            System.out.println("=".repeat(50));

        } catch (IOException e) {
            System.out.println("❌ Error reading CSV file: " + e.getMessage());
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private static String[] parseCSVLine(String line) {
        // Simple CSV parser that handles quoted fields
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handle double quotes (escape quotes)
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private static Grade findExistingGrade(GradeManager gradeManager, String studentId, String subjectCode) {
        try {
            // Using reflection to access the private grades array
            java.lang.reflect.Field gradesField = GradeManager.class.getDeclaredField("grades");
            gradesField.setAccessible(true);
            Grade[] grades = (Grade[]) gradesField.get(gradeManager);

            java.lang.reflect.Field gradeCountField = GradeManager.class.getDeclaredField("gradeCount");
            gradeCountField.setAccessible(true);
            int gradeCount = (int) gradeCountField.get(gradeManager);

            for (int i = 0; i < gradeCount; i++) {
                if (grades[i] != null &&
                        grades[i].getStudentId().equals(studentId) &&
                        grades[i].getSubject().getSubjectCode().equals(subjectCode)) {
                    return grades[i];
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding existing grade: " + e.getMessage());
        }
        return null;
    }

    private static void addGradeToManager(GradeManager gradeManager, Grade grade) {
        try {
            java.lang.reflect.Field gradesField = GradeManager.class.getDeclaredField("grades");
            gradesField.setAccessible(true);
            Grade[] grades = (Grade[]) gradesField.get(gradeManager);

            java.lang.reflect.Field gradeCountField = GradeManager.class.getDeclaredField("gradeCount");
            gradeCountField.setAccessible(true);
            int gradeCount = (int) gradeCountField.get(gradeManager);

            if (gradeCount < grades.length) {
                grades[gradeCount] = grade;
                gradeCountField.set(gradeManager, gradeCount + 1);
            } else {
                System.out.println("❌ Cannot import more grades. Storage is full.");
            }
        } catch (Exception e) {
            System.out.println("Error adding grade to manager: " + e.getMessage());
        }
    }
}