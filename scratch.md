### ENSE 370 - Assignment 3 Scratchpad
- were given bad java uni reg system code
- we need to analyze code smells
- refactor step by step usint idea
- document with ss

requirments
- 12 code smells
- 8 refactoring techs
- at least 3 must be structural
- refactoring report ???

### functions test
- add student
- add course
- add instructor
- enroll student
- assign grade

### smells
- public fields - UniversitySystem. - students.add()
- UniversitySystem.java: enrollStudent() method - these values have no straight definition, if the fee changes then the change has to be made at each time
- duplicate code: UniversitySystem.java: enrollStudent() method - this code is duplicated
``` 
for (Student st : students) {
            if (st.id.equals(studentId)) {
                s = st;
            }
```

- findStudent - defined method but never called.
- findCourse- defined method but never called.