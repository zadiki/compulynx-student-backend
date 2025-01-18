package com.compulynx.studenttask.service;

import com.compulynx.studenttask.model.Status;
import com.compulynx.studenttask.model.StudentClass;
import com.compulynx.studenttask.model.db.Student;
import com.compulynx.studenttask.repository.StudentRepository;
import com.github.javafaker.Faker;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomUtils.nextInt;

@Service
public class StudentService {
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
            fakeStudent.setPhotoPath(faker.avatar().image());
            fakeStudent.setStudentClass(StudentClass.values()[ThreadLocalRandom.current().nextInt(StudentClass.values().length)]);
            fakeStudent.setStatus(Status.values()[ThreadLocalRandom.current().nextInt(Status.values().length)]);
            fakeStudent.setDateOfBirth(
                    faker.date().birthday(14, 24)
            );
            generatedStudents.add(fakeStudent);
            count--;
        }

        return generatedStudents;
    }

    public void generateStudentListExcel(List<Student> studentList,String fileName) throws Exception {
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
            dataRow.createCell(0).setCellValue(student.getStudentId()==null?0:student.getStudentId());
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
        FileOutputStream out = getFileOutputStream(fileName);
        workbook.write(out);
        workbook.close();
        out.close();

    }

    private static FileOutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
        String directoryName = "/var/log/applications/API/dataprocessing/";
        String filePath= directoryName+ fileName;
        if(!(new File("/var/log")).exists()){
            (new File("/var/log")).mkdir();
        }

        if(!(new File("/var/log/applications")).exists()){
            (new File("/var/log/applications")).mkdir();
        }

        if(!(new File("/var/log/applications/API")).exists()){
            (new File("/var/log/applications/API")).mkdir();
        }
        if(!(new File("/var/log/applications/API/dataprocessing")).exists()){
            (new File("/var/log/applications/API/dataprocessing")).mkdir();
        }


        File directory = new File(directoryName);
        if (!directory.exists()){
            directory.mkdir();
        }
        FileOutputStream out = new FileOutputStream(
                filePath);
        return out;
    }


}
