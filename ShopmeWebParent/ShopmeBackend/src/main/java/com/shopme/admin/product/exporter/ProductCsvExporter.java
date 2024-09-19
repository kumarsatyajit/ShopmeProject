package com.shopme.admin.product.exporter;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.models.Product;

import jakarta.servlet.http.HttpServletResponse;

public class ProductCsvExporter extends AbstractExporter {
	public void export(List<Product> products, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "id", "name", "alias", "shortDescription", "fullDescription", "createdTime", "updatedTime",
				"enabled", "inStock", "cost", "price", "discountPercent", "length", "width", "height", "weight",
				"mainImage", "extraImages", "brand", "category" };
		String[] filedMapping = { "id", "name", "alias", "shortDescription", "fullDescription", "createdTime", "updatedTime",
				"enabled", "inStock", "cost", "price", "discountPercent", "length", "width", "height", "weight",
				"productMainImage", "extraImageUrls", "brandName", "categoryName" };

		csvWriter.writeHeader(header);
		products.forEach(category -> {
			try {
				csvWriter.write(category, filedMapping);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		csvWriter.close();
	}
}
