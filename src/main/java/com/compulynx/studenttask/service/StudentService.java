package com.compulynx.studenttask.service;

import com.compulynx.studenttask.exception.DataDoesNotExistException;
import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.model.db.StudentLog;
import com.compulynx.studenttask.repository.StudentRepository;
import com.compulynx.studenttask.util.Constants;
import com.github.javafaker.Faker;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

import java.util.concurrent.ThreadLocalRandom;



@Slf4j
@Service
public class StudentService {
    String directoryName = FileUtils.getUserDirectory() + "/log/applications/API/dataprocessing/";
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Student createStudent(Student student) {
        studentRepository.save(student);
        return student;
    }

    public List<Student> saveStudentsList(List<Student> students) {
        return studentRepository.saveAll(students);
    }

    public List<Student> generateStudentList(int count) {
        List<Student> generatedStudents = new ArrayList<>();
        while (count > 0) {
            var faker = new Faker();
            var fakeStudent = new Student();
            fakeStudent.setFirstName(faker.name().firstName());
            fakeStudent.setLastName(faker.name().lastName());
            fakeStudent.setScore(faker.number().numberBetween(55, 85));
//            fakeStudent.setPhotoPath(faker.avatar().image());
            fakeStudent.setPhotoPath(
                    "https://i.pravatar.cc?img="+( new Random()).nextInt(50,60));
            fakeStudent.setStudentClass(StudentClass.values()[ThreadLocalRandom.current().nextInt(StudentClass.values().length)]);
            fakeStudent.setStatus(Status.values()[ThreadLocalRandom.current().nextInt(Status.values().length)]);
            fakeStudent.setDateOfBirth(faker.date().birthday(14, 24));
            generatedStudents.add(fakeStudent);
            count--;
        }

        return generatedStudents;
    }

    public List<Student> saveGeneratedStudents(String fileName) throws Exception {
        var listFromExcel = readStudentListExcel(fileName);
        var students = new ArrayList<Student>();
        for (Student student : listFromExcel) {
            student.setStudentId(null);
            student.setScore(student.getScore() + 5);
            students.add(student);

        }

        return studentRepository.saveAll(students);
    }


    public void generateStudentListExcel(List<Student> studentList, String fileName) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Students");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Student Id");
        row.createCell(1).setCellValue("First Name");
        row.createCell(2).setCellValue("Last Name");
        row.createCell(3).setCellValue("DOB");
        row.createCell(4).setCellValue("Class");
        row.createCell(5).setCellValue("Score");
        row.createCell(6).setCellValue("Status");
        row.createCell(7).setCellValue("Image Url");

        HSSFCellStyle dateCellStyle = workbook.createCellStyle();
        HSSFDataFormat dateFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd-mm-yyyy"));

        int dataRowIndex = 1;


        for (Student student : studentList) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(student.getStudentId() == null ? 0 : student.getStudentId());
            dataRow.createCell(1).setCellValue(student.getFirstName());
            dataRow.createCell(2).setCellValue(student.getLastName());
            HSSFCell dateCell = dataRow.createCell(3);
            dateCell.setCellValue(student.getDateOfBirth());
            dateCell.setCellStyle(dateCellStyle);
            dataRow.createCell(4).setCellValue(student.getStudentClass().name());
            dataRow.createCell(5).setCellValue(student.getScore());
            dataRow.createCell(6).setCellValue(student.getStatus().name());
            dataRow.createCell(7).setCellValue(student.getPhotoPath());

            dataRowIndex++;
        }
        for (int i = 0; i < dataRowIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(createFilePath(directoryName, fileName));
        workbook.write(out);
        workbook.close();
        out.close();
        generateStudentListCsv(studentList, "1.csv");

    }

    public void generateStudentListCsv(List<Student> studentList, String fileName) throws Exception {
        File file = new File(createFilePath(directoryName, fileName));
        FileWriter outputfile = new FileWriter(file);

        CSVWriter writer = new CSVWriter(outputfile);

        String[] header = {"Student Id", "First Name", "Last Name", "DOB", "Class", "Score", "Status", "Image Url"};
        writer.writeNext(header);
        for (Student student : studentList) {
            String[] data1 = {String.valueOf(student.getStudentId()), String.valueOf(student.getFirstName()), String.valueOf(student.getLastName()), String.valueOf(student.getDateOfBirth()), String.valueOf(student.getStudentClass().name()), String.valueOf(student.getScore()), String.valueOf(student.getStatus().name()), String.valueOf(student.getPhotoPath()),};
            writer.writeNext(data1);
        }

        writer.close();
    }


    public List<Student> readStudentListExcel(String fileName) throws Exception {

        var filePath = directoryName + fileName;
        var students = new ArrayList<Student>();

        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            var student = new Student();
            for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
                Cell cell = row.getCell(columnIndex);
                String columnLetter = indexToColumnLetter(columnIndex + 1);
                String cellReference = columnLetter + (rowIndex + 1);

                switch (columnLetter) {
                    case "A" ->
                            student.setStudentId(getCellValue(cell).chars().allMatch(Character::isDigit) ? Long.parseLong(getCellValue(cell)) : 0L);
                    case "B" -> student.setFirstName(getCellValue(cell));
                    case "C" -> student.setLastName(getCellValue(cell));
                    case "D" -> {
                        student.setDateOfBirth(cell.getDateCellValue());
                    }

                    case "E" -> student.setStudentClass(StudentClass.valueOf(getCellValue(cell)));
                    case "F" -> student.setScore(new java.math.BigDecimal(getCellValue(cell)).intValue());
                    case "G" -> student.setStatus(Status.valueOf(getCellValue(cell)));
                    case "H" -> student.setPhotoPath(getCellValue(cell));
                }

            }
            students.add(student);
        }

        workbook.close();

        return students;
    }


    private static String indexToColumnLetter(int index) {
        StringBuilder column = new StringBuilder();
        while (index > 0) {
            int remainder = (index - 1) % 26;
            column.insert(0, (char) ('A' + remainder));
            index = (index - 1) / 26;
        }
        return column.toString();
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "N/A";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "Unknown Value";
        }
    }

    public String createFilePath(String directoryName, String fileName) throws Exception {

        File directory = new File(directoryName);
        if (!directory.exists()) {
            var directoryCreated = directory.mkdirs();
        }
        String filePath = directoryName + fileName;

        return filePath;
    }

    public List<Student> generateStudentsCSVfromExcel(String fileToRead, String fileToGenerate) throws Exception {
        var students = readStudentListExcel(fileToRead);
        var newStudentsList = new ArrayList<Student>();
        for (Student student : students) {
            student.setScore(student.getScore() + 10);
            newStudentsList.add(student);
        }
        generateStudentListCsv(newStudentsList, fileToGenerate);
        return newStudentsList;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Page<Student> filterStudents(String firstName, String lastName, Date dob, StudentClass studentClass, Integer score, Status status, Pageable pageable,int pendingTask) {
        return studentRepository.findAll(StudentSpecification.filterByFields(firstName, lastName, dob, studentClass, score, status, 0,pendingTask), pageable);
    }

    public void deleteStudent(Long id) {
        var optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            var student = optionalStudent.get();
            student.setDeleteStatus(1);
            studentRepository.save(student);

        }
    }

    public Student updateStudent(Student student){
        studentRepository.save(student);
        return student;
    }

    public Student updateStudent(Long studentId, Student updatedStudent) throws Exception {
        Optional<Student> existingStudent = studentRepository.findById(studentId);

        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();

            // Update the student's fields
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setStatus(updatedStudent.getStatus());
            student.setScore(updatedStudent.getScore());
            student.setStudentClass(updatedStudent.getStudentClass());
            student.setDateOfBirth(updatedStudent.getDateOfBirth());

            return studentRepository.save(student);
        } else {
            throw new DataDoesNotExistException("student not found");
        }


    }

    public Optional<Student> findStudentById(Long id) {
        return studentRepository.findById(id);
    }


    public static class StudentSpecification {
        public static Specification<Student> filterByFields(String firstName, String lastName, Date dob, StudentClass studentClass, Integer score, Status status, int deleteStatus, int pendingTask) {
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (firstName != null && !firstName.isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
                }

                if (lastName != null && !lastName.isEmpty()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
                }

                if (dob != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dateOfBirth"), dob));
                }

                if (studentClass != null) {
                    predicates.add(criteriaBuilder.equal(root.get("studentClass"), studentClass));
                }

                if (score != null ) {
                    predicates.add(criteriaBuilder.equal(root.get("score"), score));
                }

                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }

                if (pendingTask== Student.STUDENT_REQUIRED_ACTION_TYPE_NONE ||pendingTask==Student.STUDENT_REQUIRED_ACTION_TYPE_EXIST) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("actionsPendingApproval"), pendingTask));
                }

                predicates.add(criteriaBuilder.equal(root.get("deleteStatus"), deleteStatus));

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }
}
