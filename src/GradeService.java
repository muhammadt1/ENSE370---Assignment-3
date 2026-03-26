import java.util.List;

public class GradeService {

    private List<Student> students;
    private List<Course> courses;
    private List<Enrollment> enrollments;

    public GradeService(List<Student> students, List<Course> courses,
                        List<Enrollment> enrollments) {
        this.students = students;
        this.courses = courses;
        this.enrollments = enrollments;
    }

    public void assignGrade(String studentId, String courseCode,
                            String semester, String grade) {
        for (Enrollment e : enrollments) {
            if (e.getStudentId().equals(studentId) &&
                    e.getCourseCode().equals(courseCode) &&
                    e.getSemester().equals(semester)) {

                e.setGrade(grade);

                double points = 0;
                if (grade.equals("A")) points = 4.0;
                else if (grade.equals("B")) points = 3.0;
                else if (grade.equals("C")) points = 2.0;
                else if (grade.equals("D")) points = 1.0;
                else if (grade.equals("F")) points = 0.0;

                Student student = findStudent(studentId);
                Course course = findCourse(courseCode);

                if (student != null && course != null) {
                    student.setTotalCompletedCredits(
                            student.getTotalCompletedCredits() + course.getCreditHours());
                    student.setTotalGradePoints(
                            student.getTotalGradePoints() + points * course.getCreditHours());
                    student.setGpa(
                            student.getTotalGradePoints() / student.getTotalCompletedCredits());

                    if (student.getGpa() < 2.0) {
                        student.setStatus("PROBATION");
                    } else if (student.getGpa() >= 2.0 && student.getGpa() < 3.5) {
                        student.setStatus("GOOD");
                    } else {
                        student.setStatus("HONOR");
                    }

                    System.out.println("Grade " + grade + " assigned to "
                            + student.getName() + " | GPA: " + student.getGpa()
                            + " | Status: " + student.getStatus());

                    if (AdminHelper.validEmail(student.getEmail())) {
                        System.out.println("Email sent to " + student.getEmail() + ": grade posted");
                    } else {
                        System.out.println("Could not send grade email");
                    }
                }
            }
        }
    }

    private Student findStudent(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) return student;
        }
        return null;
    }

    private Course findCourse(String code) {
        for (Course course : courses) {
            if (course.getCode().equals(code)) return course;
        }
        return null;
    }
}