package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.models.Brand;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class BrandExcelExporter extends AbstractExporter {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public BrandExcelExporter() {
		this.workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		this.sheet = workbook.createSheet("brand_data");
		XSSFRow row = sheet.createRow(0);

		XSSFCellStyle cellStyle = this.workbook.createCellStyle();
		XSSFFont font = this.workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);

		createCell(row, 0, "ID", cellStyle);
		createCell(row, 1, "Name", cellStyle);
		createCell(row, 2, "LogoUrl", cellStyle);
		createCell(row, 3, "Categories", cellStyle);
	}

	private void writeDataLines(List<Brand> brands) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);

		int rowIndex = 1;
		for (Brand brand : brands) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;

			createCell(row, columnIndex++, brand.getId(), cellStyle);
			createCell(row, columnIndex++, brand.getName(), cellStyle);
			createCell(row, columnIndex++, brand.getBrandlogo(), cellStyle);
			createCell(row, columnIndex, brand.getCategoryNames(), cellStyle);
		}
	}

	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);

		if (value instanceof Integer)
			cell.setCellValue((Integer) value);
		else if (value instanceof String)
			cell.setCellValue((String) value);
		else if (value instanceof Boolean)
			cell.setCellValue((Boolean) value);

		cell.setCellStyle(cellStyle);
	}

	public void export(List<Brand> brands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
		writeHeaderLine();
		writeDataLines(brands);

		ServletOutputStream outputStream = response.getOutputStream();
		this.workbook.write(outputStream);
		this.workbook.close();
		outputStream.close();
	}
}
