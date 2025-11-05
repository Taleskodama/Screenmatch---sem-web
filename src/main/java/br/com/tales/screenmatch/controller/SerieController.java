package br.com.tales.screenmatch.controller;

import br.com.tales.screenmatch.dto.SerieDTO;
import br.com.tales.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/series")
public class SerieController {

@Autowired
private SerieService servico;

    @GetMapping
    public List<SerieDTO> obterSeries(){
    return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Series(){
        return servico.obterTop5Series();
    }
}
