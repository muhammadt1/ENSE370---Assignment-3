import java.util.ArrayList;
import java.util.List;

public class UniversitySystem {

    public List<Student> students = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();
    public List<Enrollment> enrollments = new ArrayList<>();
    public List<Instructor> instructors = new ArrayList<>();
    public List<PaymentRecord> payments = new ArrayList<>();
    public List<String> logs = new ArrayList<>();

    public String universityName = "Metro University";
    public double localRate = 300;
    public double internationalRate = 550;
    public double scholarshipRate = 100;

    private GradeService gradeService;
    public UniversitySystem() {
        this.gradeService = new GradeService(students, courses, enrollments);
    }


    public void enrollStudent(String studentId, String courseCode, String semester, String paymentType) {
        Student student = findStudent(studentId);
        Course course = findCourse(courseCode);


        if (student == null) {
            System.out.println("Student not found");
            logs.add("Student not found: " + studentId);
            return;
        }

        if (course == null) {
            System.out.println("Course not found");
            logs.add("Course not found: " + courseCode);
            return;
        }

        if (student.isBlocked()) {
            System.out.println("Student is blocked");
            logs.add("Blocked student tried enrollment");
            return;
        }

        if (student.getStatus().equals("PROBATION")) {
            int count = 0;
            for (Enrollment e : enrollments) {
                if (e.getStudentId().equals(studentId) && e.getSemester().equals(semester)) {
                    count++;
                }
            }
            if (count >= 2) {
                System.out.println("Probation student cannot register more than 2 courses");
                logs.add("Probation limit reached");
                return;
            }
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            System.out.println("Course is full");
            logs.add("Course full: " + courseCode);
            return;
        }

        if (student.getOutstandingBalance() > 1000) {
            System.out.println("Student has unpaid balance");
            logs.add("Balance issue for " + student.getId());
            return;
        }

        for (Enrollment e : enrollments) {
            if (e.getStudentId().equals(studentId) && e.getSemester().equals(semester)) {
                if (e.getDay().equals(course.getDay()) && e.getTimeSlot().equals(course.getTimeSlot())) {
                    System.out.println("Schedule conflict");
                    logs.add("Conflict for " + studentId);
                    return;
                }
            }
        }

        if (!checkPrerequisite(studentId, course)) {
            System.out.println("Missing prerequisite");
            logs.add("Missing prerequisite for " + studentId);
            return;

        }
        //new call for calcualteFee to seperate method
        double fee = calculateFee(student, course, semester, paymentType, courseCode);

        student.setOutstandingBalance(student.getOutstandingBalance() + fee);
        Enrollment newEnrollment = new Enrollment(studentId, courseCode, semester, course.getDay(), course.getTimeSlot());
        course.incrementEnrolled();


        System.out.println("Enrollment completed for " + student.getName() + " in " + course.getTitle() + " | Fee: " + fee);

        if (student.getEmail() != null && student.getEmail().contains("@")) {
            System.out.println("Email sent to " + student.getEmail() + ": enrolled in " + course.getTitle());
            logs.add("Enrollment email sent");
        } else {
            System.out.println("Invalid email");
            logs.add("Invalid email for " + student.getId());
        }
    }

    private double calculateFee(Student student, Course course, String semester, String paymentType, String courseCode) {
        double fee = 0;

        if (student.getType() == StudentType.LOCAL) {
            fee = course.getCreditHours() * 300;
        } else if (student.getType() == StudentType.INTERNATIONAL) {
            fee = course.getCreditHours() * 550;
        } else if (student.getType() == StudentType.SCHOLARSHIP) {
            fee = course.getCreditHours() * 100;
        } else {
            fee = course.getCreditHours() * 300;
        }

        if (paymentType.equals("INSTALLMENT")) {
            fee = fee + 50;
        } else if (paymentType.equals("CARD")) {
            fee = fee + 10;
        } else if (paymentType.equals("CASH")) {
            // no surcharge for cash
        } else {
            fee = fee + 100;
        }

        if (semester.equals("SUMMER")) {
            fee = fee + 200;
        }

        if (courseCode.startsWith("SE")) {
            fee = fee + 75;
        }

        return fee;
    }
    private boolean checkPrerequisite(String studentId, Course course) {
        if (course.getPrerequisite() == null || course.getPrerequisite().equals("")) {
            return true;
        }

    for (Enrollment enrollment : enrollments) {
        if (enrollment.getStudentId().equals(studentId) &&
                enrollment.getCourseCode().equals(course.getPrerequisite())) {
            if (enrollment.getGrade() != null && (
                    enrollment.getGrade().equals("A") ||
                            enrollment.getGrade().equals("B") ||
                            enrollment.getGrade().equals("C"))) {
                return true;
            }
        }
    }
    return false;
}

    public void assignGrade(String studentId, String courseCode, String semester, String grade) {
        gradeService.assignGrade(studentId, courseCode, semester, grade);
    }
    public void processPayment(String studentId, double amount, String method) {
        Student student = null;
        for (Student st : students) {
            if (st.getId().equals(studentId)) {
                student = st;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid payment");
            return;
        }

        if (method.equals("CARD")) {
            amount = amount - 5;
        } else if (method.equals("BANK")) {
            amount = amount - 2;
        } else if (method.equals("CASH")) {
            amount = amount;
        } else {
            amount = amount - 10;
        }

        student.setOutstandingBalance(student.getOutstandingBalance() - amount);
        if (student.getOutstandingBalance() < 0) {
            student.setOutstandingBalance(0);
        }
        System.out.println("Payment processed for " + student.getName());
        System.out.println("Remaining balance: " + student.getOutstandingBalance());
        student.getEmail();
        System.out.println("Amount accepted: " + amount);
        System.out.println("Remaining balance: " + student.getOutstandingBalance());

        if (student.getEmail() != null && student.getEmail() .contains("@")) {
            System.out.println("Email sent to " + student.getEmail()  + ": payment received");
        }
    }

    public void printTranscript(String studentId) {
        Student student = null;
        for (Student st : students) {
            if (st.getId().equals(studentId)) {
                student = st;
            }
        }

        if (student == null) {
            System.out.println("Student not found");
            return;
        }

        System.out.println("----- TRANSCRIPT -----");
        System.out.println("University: " + universityName);
        System.out.println("Name: " + student.getName());
        System.out.println("ID: " + student.getId());
        System.out.println("Department: " + student.getDepartment());
        System.out.println("Status: " + student.getStatus());
        System.out.println("GPA: " + student.getGpa());

        for (Enrollment e : enrollments) {
            if (e.getStudentId().equals(studentId)) {
                String title = "";
                int credits = 0;
                for (Course course : courses) {
                    if (course.getCode().equals(e.getCourseCode())) {
                        title = course.getTitle();
                        credits = course.getCreditHours();
                    }
                }
                System.out.println(e.getCourseCode() + " - " + title + " - " + credits + " credits - Grade: " + e.getGrade());
            }
        }

        System.out.println("Outstanding Balance: " + student.getOutstandingBalance());
        if (student.getOutstandingBalance() > 0) {
            System.out.println("WARNING: unpaid dues");
        }
    }

    public void printCourseRoster(String courseCode) {
        System.out.println("----- COURSE ROSTER -----");
        for (Course course : courses) {
            if (course.getCode().equals(courseCode)) {
                System.out.println("Course: " + course.getTitle());
                System.out.println("Instructor: " + course.getInstructorName());
                System.out.println("Capacity: " + course.getCapacity());
                System.out.println("Enrolled: " + course.getEnrolled());
            }
        }

        for (Enrollment e : enrollments) {
            if (e.getCourseCode().equals(courseCode)) {
                for (Student student : students) {
                    if (student.getId().equals(e.getStudentId())) {
                        System.out.println(student.getId() + " - " + student.getName() + " - " + student.getStatus());
                    }
                }
            }
        }
    }

    public void printDepartmentSummary(String department) {
        System.out.println("----- DEPARTMENT SUMMARY -----");
        System.out.println("Department: " + department);

        int studentCount = 0;
        int instructorCount = 0;
        int courseCount = 0;
        double avgGpa = 0;
        int gpaCount = 0;

        for (Student student : students) {
            if (student.getDepartment().equals(department)) {
                studentCount++;
                avgGpa += student.getGpa();
                gpaCount++;
            }
        }

        for (Instructor i : instructors) {
            if (i.department.equals(department)) {
                instructorCount++;
            }
        }

        for (Course course : courses) {
            if (course.getCode().startsWith(department)) {
                courseCount++;
            }
        }

        if (gpaCount > 0) {
            avgGpa = avgGpa / gpaCount;
        }

        System.out.println("Students: " + studentCount);
        System.out.println("Instructors: " + instructorCount);
        System.out.println("Courses: " + courseCount);
        System.out.println("Average GPA: " + avgGpa);
    }

    public void sendWarningLetters() {
        for (Student student : students) {
            if (student.getOutstandingBalance() > 500 || student.getStatus().equals("PROBATION")) {
                if (student.getEmail() != null && student.getEmail().contains("@")) {
                    System.out.println("Sending warning email to " + student.getEmail());
                    if (student.getOutstandingBalance() > 500) {
                        System.out.println("Reason: unpaid balance");
                    }
                    if (student.getStatus().equals("PROBATION")) {
                        System.out.println("Reason: academic probation");
                    }
                    logs.add("Warning sent to " + student.getId());
                } else {
                    System.out.println("Could not send warning to " + student.getName());
                    logs.add("Warning failed for " + student.getId());
                }
            }
        }
    }

    public Student findStudent(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    public Course findCourse(String code) {
        for (Course course : courses) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }
}
