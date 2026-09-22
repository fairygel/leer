package me.fairygel.mapper;

import java.util.List;
import me.fairygel.dto.deck.CreateDeckRequestDTO;
import me.fairygel.dto.deck.DeckResponseDTO;
import me.fairygel.dto.deck.UpdateDeckRequestDTO;
import me.fairygel.entity.Deck;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeckMapper {

    DeckResponseDTO toResponse(Deck deck);

    List<DeckResponseDTO> toResponseList(List<Deck> decks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "lastViewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Deck toEntity(CreateDeckRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "lastViewedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateDeckRequestDTO dto, @MappingTarget Deck deck);
}