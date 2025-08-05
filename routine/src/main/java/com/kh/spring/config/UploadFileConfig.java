package com.kh.spring.config;


	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
	import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

	@Configuration
	public class UploadFileConfig implements WebMvcConfigurer{
		
		@Value("${file.upload.path}")
		private String filePath;
		
		
		//uploadFile/** 요청이 들어오면 실제 파일 경로 위치로 요청 보내기 설정
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/uploadFiles/**") //해당 경로로 요청이 들어오면 
					.addResourceLocations("file:///"+filePath); //설정한 경로로 요청이 되게 설정
		}
		
	}
	
	

