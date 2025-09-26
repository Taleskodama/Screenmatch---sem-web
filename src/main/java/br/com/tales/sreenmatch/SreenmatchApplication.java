package br.com.tales.sreenmatch;

import br.com.tales.sreenmatch.model.DadosSerie;
import br.com.tales.sreenmatch.service.ConsumoApi;
import br.com.tales.sreenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SreenmatchApplication implements CommandLineRunner {
  @Override
  public void run(String... args) throws Exception {
    var consumoApi = new ConsumoApi();
    var json =
        consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=dd79755f");
    System.out.println(json);
    //        json = consumoApi.obterDados("https://coffee.alexflipnote.dev/random.json");
    //        System.out.println(json);
      ConverteDados conversor = new ConverteDados();
      DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
    System.out.println(dados);
  }

  public static void main(String[] args) {
    SpringApplication.run(SreenmatchApplication.class, args);
  }
}
