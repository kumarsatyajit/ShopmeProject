package com.shopme.admin.product.exporter;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.models.Product;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ProductExcelExporter extends AbstractExporter{

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public ProductExcelExporter() {
		this.workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		this.sheet = workbook.createSheet("product_data");
		XSSFRow row = sheet.createRow(0);

		XSSFCellStyle cellStyle = this.workbook.createCellStyle();
		XSSFFont font = this.workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);

		createCell(row, 0, "id", cellStyle);
		createCell(row, 1, "name", cellStyle);
		createCell(row, 2, "alias", cellStyle);
		createCell(row, 3, "shortdescription", cellStyle);
		createCell(row, 4, "fulldescription", cellStyle);
		createCell(row, 5, "createdtime", cellStyle);
		createCell(row, 6, "updatedtime", cellStyle);
		createCell(row, 7, "enabled", cellStyle);
		createCell(row, 8, "instock", cellStyle);
		createCell(row, 9, "cost", cellStyle);
		createCell(row, 10, "price", cellStyle);
		createCell(row, 11, "discountpercent", cellStyle);
		createCell(row, 12, "length", cellStyle);
		createCell(row, 13, "width", cellStyle);
		createCell(row, 14, "height", cellStyle);
		createCell(row, 15, "weight", cellStyle);
		createCell(row, 16, "mainimage", cellStyle);
		createCell(row, 17, "extraimages", cellStyle);
		createCell(row, 18, "brand", cellStyle);
		createCell(row, 19, "category", cellStyle);

	}

	private void writeDataLines(List<Product> products) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		cellStyle.setFont(font);

		int rowIndex = 1;
		for (Product product : products) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;

			createCell(row, columnIndex++, product.getId(), cellStyle);
			createCell(row, columnIndex++, product.getName(), cellStyle);
			createCell(row, columnIndex++, product.getAlias(), cellStyle);
			createCell(row, columnIndex, product.getShortDescription(), cellStyle);
			createCell(row, columnIndex, product.getFullDescription(), cellStyle);
			createCell(row, columnIndex, product.getCreatedTime(), cellStyle);
			createCell(row, columnIndex, product.getUpdatedTime(), cellStyle);
			createCell(row, columnIndex, product.isEnabled(), cellStyle);
			createCell(row, columnIndex, product.isInStock(), cellStyle);
			createCell(row, columnIndex, product.getCost(), cellStyle);
			createCell(row, columnIndex, product.getPrice(), cellStyle);
			createCell(row, columnIndex, product.getDiscountPercent(), cellStyle);
			createCell(row, columnIndex, product.getLength(), cellStyle);
			createCell(row, columnIndex, product.getWidth(), cellStyle);
			createCell(row, columnIndex, product.getHeight(), cellStyle);
			createCell(row, columnIndex, product.getWeight(), cellStyle);
			createCell(row, columnIndex, product.getProductMainImage(), cellStyle);
			createCell(row, columnIndex, product.getExtraImageUrls(), cellStyle);
			createCell(row, columnIndex, product.getBrandName(), cellStyle);
			createCell(row, columnIndex, product.getCategoryName(), cellStyle);
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

	public void export(List<Product> products, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
		writeHeaderLine();
		writeDataLines(products);

		ServletOutputStream outputStream = response.getOutputStream();
		this.workbook.write(outputStream);
		this.workbook.close();
		outputStream.close();
	}
}
