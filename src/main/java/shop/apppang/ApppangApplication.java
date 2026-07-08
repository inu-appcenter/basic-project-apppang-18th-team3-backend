package shop.apppang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ApppangApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApppangApplication.class, args);
	}

}
