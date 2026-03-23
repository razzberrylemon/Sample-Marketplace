package ie.ucd.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // this annotation means this is the main class
public class MarketApplication {

	public static void main(String[] args) {
		// next line starts the application
		SpringApplication.run(MarketApplication.class, args); 
	}
}
