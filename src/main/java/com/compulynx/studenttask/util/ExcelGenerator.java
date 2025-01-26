package com.compulynx.studenttask.util;

import com.compulynx.studenttask.model.db.Student;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static byte[] generateExcel(List<Student> students) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Fname", "Lname", "DOB", "class", "score", "status", "photo"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        // Data rows
        int rowIndex = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(student.getStudentId());
            row.createCell(1).setCellValue(student.getFirstName());
            row.createCell(2).setCellValue(student.getLastName());

            CellStyle dateCellStyle = workbook.createCellStyle();
            DataFormat dateFormat = workbook.createDataFormat();
            dateCellStyle.setDataFormat(dateFormat.getFormat("dd-mm-yyyy"));

            var dateCell = row.createCell(3);
            dateCell.setCellValue(student.getDateOfBirth());
            dateCell.setCellStyle(dateCellStyle);

            row.createCell(4).setCellValue(student.getStudentClass().name());
            row.createCell(5).setCellValue(student.getScore());
            row.createCell(6).setCellValue(student.getStatus().name());
            row.createCell(7).setCellValue(student.getPhotoPath());
        }

        // Autosize columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private static CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

