package student;

public class HonorsStudent extends Student{
    private double passingGrade = 60;
    private boolean honorsEligible = true;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public void displayStudentDetails() {
        System.out.println("Student ID: " +getStudentId());
        System.out.println("Name: " +getName());
        System.out.println("Type: " +getStudentType());
        System.out.println("Age: " +getAge());
        System.out.println("Email: " +getEmail());
        System.out.println("Passing Grade: " +getPassingGrade() + "%");
        System.out.println("Honors Eligible: " +checkHonorsEligibility());
        System.out.println("Status: " +getStatus());

    }

    @Override
    public String getStudentType() {
        return "Honors";
    }

    @Override
    public double getPassingGrade() {
        return passingGrade;
    }

    public String checkHonorsEligibility(){
      if (honorsEligible == true){
          return "Yes";
      } else{
          return "No";
      }
    }
}
