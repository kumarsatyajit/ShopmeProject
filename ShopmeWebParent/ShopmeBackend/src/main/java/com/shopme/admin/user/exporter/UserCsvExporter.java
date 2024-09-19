package com.shopme.admin.user.exporter;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.models.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserCsvExporter extends AbstractExporter {

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = { "User Id", "First Name", "Last Name", "Email", "Enabled", "Roles" };
		String[] fieldMapping = { "id", "firstName", "lastName", "email", "enabled", "roles" };

		csvWriter.writeHeader(csvHeader);
		users.forEach(user -> {
			try {
				csvWriter.write(user, fieldMapping);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		csvWriter.close();
	}
}
