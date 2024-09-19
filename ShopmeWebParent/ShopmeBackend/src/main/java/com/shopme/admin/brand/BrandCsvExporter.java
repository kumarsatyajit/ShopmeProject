package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.models.Brand;

import jakarta.servlet.http.HttpServletResponse;

public class BrandCsvExporter extends AbstractExporter {

	public void export(List<Brand> brands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "ID", "Name", "LogoUrl", "Categories" };
		String[] filedMapping = { "id", "name", "brandlogo", "categoryNames" };

		csvWriter.writeHeader(header);
		brands.forEach(category -> {
			try {
				csvWriter.write(category, filedMapping);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		csvWriter.close();
	}
}
