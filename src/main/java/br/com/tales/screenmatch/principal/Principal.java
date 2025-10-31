package br.com.tales.screenmatch.principal;

import br.com.tales.screenmatch.model.*;
import br.com.tales.screenmatch.repository.SerieRepository;
import br.com.tales.screenmatch.service.ConsumoApi;
import br.com.tales.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

  private Scanner leitura = new Scanner(System.in);
  private ConsumoApi consumo = new ConsumoApi();
  private ConverteDados conversor = new ConverteDados();
  private final String ENDERECO = "https://www.omdbapi.com/?t=";
  private final String API_KEY = "&apikey=dd79755f";
  private SerieRepository repositorio;

  private List<DadosSerie> dadosSeries = new ArrayList<>();

  public Principal(SerieRepository repositorio) {
    this.repositorio = repositorio;
  }

  private List<Serie> series = new ArrayList<>();

  public void exibeMenu() {
    var opcao = -1;
    while (opcao != 0) {
      var menu =
          """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por Título
                5 - Buscar séries por Ator
                6 - Top 5 séries
                7 - Buscar séries por categoria
                8 - Buscar séries por número de temporadas e avaliação mínima
                9 - Buscar episódios por Trecho

                0 - Sair
                """;

      System.out.println(menu);
      opcao = leitura.nextInt();
      leitura.nextLine();

      switch (opcao) {
        case 1:
          buscarSerieWeb();
          break;
        case 2:
          buscarEpisodioPorSerie();
          break;
        case 3:
          listarSeriesBuscadas();
          break;
        case 4:
          buscarSeriePorTitulo();
          break;
          case 5:
              buscarSeriesPorAtor();
              break;
          case 6:
              buscarTop5Series();
              break;
          case 7:
              buscarSeriesPorCategoria();
              break;
          case 8:
              buscarSeriesPorTemporadaAvaliacaoMinima();
              break;
          case 9:
              buscarEpisodioPorTrecho();
              break;
        case 0:
          System.out.println("Saindo...");
          break;
        default:
          System.out.println("Opção inválida");
      }
    }
  }

  private void buscarSerieWeb() {
    DadosSerie dados = getDadosSerie();
    Serie serie = new Serie(dados);
    // dadosSeries.add(dados);
    repositorio.save(serie);
    System.out.println(dados);
  }

  private DadosSerie getDadosSerie() {
    System.out.println("Digite o nome da série para busca");
    var nomeSerie = leitura.nextLine();
    var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
    DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
    return dados;
  }

  private void buscarEpisodioPorSerie() {
    listarSeriesBuscadas();
    System.out.println("Escolha uma série pelo nome: ");
    var nomeSerie = leitura.nextLine();

    Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

    if (serie.isPresent()) {

      var serieEncontrada = serie.get();
      List<DadosTemporada> temporadas = new ArrayList<>();

      for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
        var json =
            consumo.obterDados(
                ENDERECO
                    + serieEncontrada.getTitulo().replace(" ", "+")
                    + "&season="
                    + i
                    + API_KEY);
        DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
        temporadas.add(dadosTemporada);
      }
      temporadas.forEach(System.out::println);

      List<Episodio> episodios =
          temporadas.stream()
              .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
              .collect(Collectors.toList());
      serieEncontrada.setEpisodios(episodios);
      repositorio.save(serieEncontrada);
    } else {
      System.out.println("Série não encontrada!");
    }
  }

  private void listarSeriesBuscadas() {
    series = repositorio.findAll();
    series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
  }

  private void buscarSeriePorTitulo() {
      System.out.println("Escolha uma série pelo nome: ");
      var nomeSerie = leitura.nextLine();
      Optional<Serie>  serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

      if(serieBuscada.isPresent()){
      System.out.println("Dados da série: " + serieBuscada.get());
      }else{
      System.out.println("Série não encontrada!");
      }
  }

  private void buscarSeriesPorAtor() {
    System.out.println("Qual o nome para a busca?");
    var nomeAtor = leitura.nextLine();
    System.out.println("Avaliações a partir de qual valor?");
    var avaliacao = leitura.nextDouble();
      List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avaliacao);
    System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
    seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
  }

  private void buscarTop5Series(){
      List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
      serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
  }

  private void buscarSeriesPorCategoria(){
    System.out.println("Deseja buscar séries de que categoria/gênero? ");
    var nomeGenero = leitura.nextLine();
      Categoria categoria = Categoria.fromPortugues(nomeGenero);
      List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
    System.out.println("Séries da categoria " + nomeGenero);
    seriesPorCategoria.forEach(System.out::println);
  }

  private void buscarSeriesPorTemporadaAvaliacaoMinima(){
    System.out.println("Coloque o número máximo de Temporadas: ");
    var numeroTemporadas = leitura.nextInt();
    System.out.println("Coloque a avaliação mínima da série: ");
    var  avaliacaoMinima = leitura.nextDouble();
    List<Serie> seriesMaxTemporadaAvaliacaoMinima = repositorio.seriePorTemporadaEAvaliacao(numeroTemporadas, avaliacaoMinima);
    System.out.println("Séries com número máximo de: " + numeroTemporadas + " temporadas e avaliação mínima de: " +  avaliacaoMinima );
    seriesMaxTemporadaAvaliacaoMinima.forEach(System.out::println);
  }

  private void buscarEpisodioPorTrecho(){
    System.out.println("Digite o nome do episódio para busca: ");
    var trechoEpisodio = leitura.nextLine();
    List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
    episodiosEncontrados.forEach(e ->
            System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo()));;
  }
}
