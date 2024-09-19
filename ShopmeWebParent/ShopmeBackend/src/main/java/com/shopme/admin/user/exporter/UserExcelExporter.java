package com.shopme.admin.user.exporter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.models.Role;
import com.shopme.common.models.User;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class UserExcelExporter extends AbstractExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public UserExcelExporter() {
		this.workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		this.sheet = workbook.createSheet("user_data");
		XSSFRow row = sheet.createRow(0);

		// Header Cell style
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);

		createCell(row, 0, "ID", cellStyle);
		createCell(row, 1, "First Name", cellStyle);
		createCell(row, 2, "Last Name", cellStyle);
		createCell(row, 3, "Email", cellStyle);
		createCell(row, 4, "Enabled", cellStyle);
		createCell(row, 5, "Rols", cellStyle);

	}

	private void writeDataLines(List<User> users) {
		// Data Cell Style
		XSSFCellStyle cellStyle = this.workbook.createCellStyle();
		XSSFFont font = this.workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);

		int rowIndex = 1;
		for (User user : users) {
			XSSFRow row = this.sheet.createRow(rowIndex++);
			int columnIndex = 0;
			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
			createCell(row, columnIndex, getUserRoles(user), cellStyle);

		}
	}

	private String getUserRoles(User user) {
		String roles = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));
		return roles;
	}

	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
		XSSFCell cell = row.createCell(columnIndex);
		this.sheet.autoSizeColumn(columnIndex);

		if (value instanceof Integer)
			cell.setCellValue((Integer) value);
		if (value instanceof String)
			cell.setCellValue((String) value);
		if (value instanceof Boolean)
			cell.setCellValue((Boolean) value);

		cell.setCellStyle(cellStyle);
	}

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx");
		writeHeaderLine();
		writeDataLines(users);

		ServletOutputStream outputStream = response.getOutputStream();
		this.workbook.write(outputStream);
		this.workbook.close();
		outputStream.close();
	}
}
