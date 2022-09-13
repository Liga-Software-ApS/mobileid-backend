package dk.liga.mobileid.backendapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BackendApiApplication {


	public static void main(String[] args) {

			
		SpringApplication.run(BackendApiApplication.class, args);
		
	}


}
