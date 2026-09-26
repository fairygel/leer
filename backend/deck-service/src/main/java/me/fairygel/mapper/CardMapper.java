package me.fairygel.mapper;

import java.util.List;
import me.fairygel.dto.card.CardResponseDTO;
import me.fairygel.dto.card.CreateCardRequestDTO;
import me.fairygel.dto.card.UpdateCardRequestDTO;
import me.fairygel.entity.Card;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    @Mapping(source = "deck.id", target = "deckId")
    CardResponseDTO toResponse(Card card);

    List<CardResponseDTO> toResponseList(List<Card> cards);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastViewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Card toEntity(CreateCardRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "lastViewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateCardRequestDTO dto, @MappingTarget Card card);
}