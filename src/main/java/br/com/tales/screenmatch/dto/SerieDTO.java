package br.com.tales.screenmatch.dto;

import br.com.tales.screenmatch.model.Categoria;

public record SerieDTO(
    Long id,
    String titulo,
    Integer totalTemporadas,
    Double avaliacao,
    Categoria genero,
    String atores,
    String poster,
    String sinopse) {}
