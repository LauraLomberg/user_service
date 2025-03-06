package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SkillRepository skillRepository;

    @Mapping(target = "owner", expression = "java(mapOwnerById(eventDto.getOwnerId()))")
    @Mapping(target = "relatedSkills", expression = "java(mapRelatedSkillsByIds(eventDto.getRelatedSkills()))")
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", expression = "java(mapRelatedSkillsToIds(event.getRelatedSkills()))")
    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    public abstract EventDto toDto(Event event);

    @Mapping(target = "owner", expression = "java(mapOwnerById(eventDto.getOwnerId()))")
    @Mapping(target = "relatedSkills", expression = "java(mapRelatedSkillsByIds(eventDto.getRelatedSkills()))")
    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void update(EventDto eventDto, @MappingTarget Event event);

    protected User mapOwnerById(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("Пользователь с указанным ID не найден: " + ownerId));
    }

    protected List<Skill> mapRelatedSkillsByIds(List<Long> skillIds) {
        if (skillIds == null) {
            return Collections.emptyList();
        }
        return skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId).orElseThrow(() ->
                        new DataValidationException("Во входящих данных неверно указаны ID навыков")))
                .toList();
    }

    protected List<Long> mapRelatedSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
