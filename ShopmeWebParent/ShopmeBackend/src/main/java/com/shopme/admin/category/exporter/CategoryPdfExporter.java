package com.shopme.admin.category.exporter;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.models.Category;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryPdfExporter extends AbstractExporter {

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(11);
		font.setColor(Color.BLACK);

		cell.setPhrase(new Phrase("ID", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Name", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Alias", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Eanbled", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Parent", font));
		table.addCell(cell);

	}

	private void writeTableData(PdfPTable table, List<Category> categories) {
		for (Category category : categories) {
			table.addCell(String.valueOf(category.getId()));
			table.addCell(category.getName());
			table.addCell(category.getAlias());
			table.addCell(String.valueOf(category.isEnabled()));
			table.addCell(category.getParentName());
		}
	}

	public void export(List<Category> categories, HttpServletResponse response) throws DocumentException, IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf");

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(14);
		font.setColor(Color.BLUE);

		Paragraph paragraph = new Paragraph("List Of Categories", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(paragraph);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10f);
		table.setWidths(new float[] { 1.5f, 5.0f, 4.0f, 1.0f, 5.0f });

		writeTableHeader(table);
		writeTableData(table, categories);
		document.add(table);

		document.close();

	}
}
