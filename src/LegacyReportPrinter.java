import java.util.List;

public class LegacyReportPrinter {

    public void printStudents(List<Student> students) {
        System.out.println("---- STUDENTS ----");
        for (Student s : students) {
            System.out.println(s.getId() + " | " + s.getName() + " | " + s.getDepartment() + " | " + s.getStatus() + " | " + s.getGpa());
        }
    }

    public void printPayments(List<PaymentRecord> payments) {
        System.out.println("---- PAYMENTS ----");
        for (PaymentRecord p : payments) {
            System.out.println(p.studentId + " | " + p.amount + " | " + p.method + " | " + p.status);
        }
    }

    public void printCourses(List<Course> courses) {
        System.out.println("---- COURSES ----");
        for (Course c : courses) {
            System.out.println(c.getCode() + " | " + c.getTitle() + " | " + c.getInstructorName() + " | " + c.getCreditHours());
        }
    }
}
