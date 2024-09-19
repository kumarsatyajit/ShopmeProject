package com.shopme.admin.brand;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {
	public static void setResponseHeader(HttpServletResponse response, String contentType, String extention) {
		DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH:ss:mm");
		String timestamp = dateFormater.format(new Date());
		String fileName = "categories_" + timestamp + extention;

		response.setContentType(contentType);

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; fileName=" + fileName;

		response.setHeader(headerKey, headerValue);
	}
}
