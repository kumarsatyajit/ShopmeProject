package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.models.Category;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCsvExporter extends AbstractExporter {

	public void export(List<Category> categories, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "ID", "Name", "Alias", "ImageUrl", "Enabled", "Parent" };
		String[] filedMapping = { "id", "name", "alias", "categoryImage", "enabled", "parentName" };

		csvWriter.writeHeader(header);
		categories.forEach(category -> {
			try {
				csvWriter.write(category, filedMapping);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		csvWriter.close();
	}
}
