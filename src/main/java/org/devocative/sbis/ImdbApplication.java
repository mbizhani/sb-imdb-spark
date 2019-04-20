package org.devocative.sbis;

import org.apache.spark.sql.SparkSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImdbApplication.class, args);
	}

	@Bean
	public SparkSession sparkSession() {
		return SparkSession
			.builder()
			.appName("SBIS")
			.master("local[6]")
			.config("spark.ui.enabled", false) // https://stackoverflow.com/questions/33774350/how-to-disable-sparkui-programmatically
			//.config("spark.driver.host", "127.0.0.1")
			.getOrCreate();
	}
}