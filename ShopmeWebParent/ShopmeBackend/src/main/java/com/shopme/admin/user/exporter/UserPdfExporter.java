package com.shopme.admin.user.exporter;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
import com.shopme.common.models.Role;
import com.shopme.common.models.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserPdfExporter extends AbstractExporter {
	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setSize(11);
		font.setColor(Color.BLACK);

		cell.setPhrase(new Phrase("User ID", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("First Name", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Last Name", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("E-mail", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Roles", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Enabled", font));
		table.addCell(cell);
	}

	private void writeTableData(PdfPTable table, List<User> users) {
		for (User user : users) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(user.getFirstName());
			table.addCell(user.getLastName());
			table.addCell(user.getEmail());
			table.addCell(getUserRoles(user));
			table.addCell(String.valueOf(user.isEnabled()));

		}
	}

	private String getUserRoles(User user) {
		return user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "));
	}

	public void export(List<User> users, HttpServletResponse response) throws DocumentException, IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf");

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(14);
		font.setColor(Color.BLUE);

		Paragraph paragraph = new Paragraph("List Of Users", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(paragraph);

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10f);
		table.setWidths(new float[] { 1.5f, 2.0f, 2.0f, 4.0f, 3.0f, 1.5f });

		writeTableHeader(table);
		writeTableData(table, users);
		document.add(table);

		document.close();

	}
}
