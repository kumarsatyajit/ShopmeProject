package com.shopme.admin.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);

		// User-Image
		String userDirName = "users-photos";
		Path userImageDir = Paths.get(userDirName);
		String userImageDirAbsolutePath = userImageDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + userDirName + "/**")
				.addResourceLocations("file:/" + userImageDirAbsolutePath + "/");

		// Category-Image
		String categoryDirName = "categories-images";
		Path categoryImageDir = Paths.get(categoryDirName);
		String categoryImageDirAbsolutePath = categoryImageDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + categoryDirName + "/**")
				.addResourceLocations("file:/" + categoryImageDirAbsolutePath + "/");

		// Brand-logos
		String brandLogoDirName = "brand-logos";
		Path brandlogoDir = Paths.get(brandLogoDirName);
		String brandlogoDirAbsolutePath = brandlogoDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + brandLogoDirName + "/**")
				.addResourceLocations("file:/" + brandlogoDirAbsolutePath + "/");

		// Product-images
		String productImageDirName = "product-images";
		Path productImageDir = Paths.get(productImageDirName);
		String productImageDirAbsolutePath = productImageDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + productImageDirName + "/**")
				.addResourceLocations("file:/" + productImageDirAbsolutePath + "/");

	}

}
