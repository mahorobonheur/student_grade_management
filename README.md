STUDENT GRADE MANAGEMENT SYSTEM
================================
This is a simple Java console application that helps a teacher to 
register students, record their grades and also display all students grades 
and also class average.
The system supports two types of students: Honors and Regular. And
also it holds two types of courses: Core and Elective.

features
===========
  Student Management
  ===================
- Add Regular or Honors students
- Auto–generate Student IDs (e.g., STU001)
- Validate name, age, email, and phone
- Display all students in a formatted table
- Enforce minimum listing requirements:
    * At least 3 Regular students
    * At least 2 Honors students

   Grade Management
   ================
- Register subjects
- Add grades for each student

  Calculate:
  ==========
- Subject-wise average
- Student overall average
-  Class average

 Honors Eligibility
 ==================
- Honors students must score 85% or above for honors recognition
- Regular students pass at 50%, Honors at 60%

  How It Works
  ==============
     Add a Student
     =============
System validates:
 - Name (letters only, min 3 chars)
 - Age (5–120)
 - Email format
 - Phone number (10–13 digits)

 Add Grades
 ===========
- Grades are saved per student per subject.
- Average is calculated automatically.

 View Student Listing
 ====================
Displays:
- ID
- Name
- Type
- Average Grade
- Total Enrolled Subjects
- Passing Status
- Honors Eligibility (for Honors students)


How to Run
===========
- Open project in IntelliJ, Eclipse, or VS Code
- Ensure JDK 8+ is installed
- Run the Main.java file
- Use the menu to navigate through the system 


Author
======
Bonheur Mahoro
Honors students must score 85% or above for honors recognition

Regular students pass at 50%, Honors at 60%
