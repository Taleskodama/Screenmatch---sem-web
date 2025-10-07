package br.com.tales.screenmatch;

import br.com.tales.screenmatch.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class SreenmatchApplication implements CommandLineRunner {
  @Override
  public void run(String... args) throws Exception {
      Principal principal = new Principal();
      principal.exibeMenu();

  }

  public static void main(String[] args) {
    SpringApplication.run(SreenmatchApplication.class, args);
  }
}
