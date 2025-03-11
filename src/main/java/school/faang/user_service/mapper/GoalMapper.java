package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "skillIds", target = "skillsToAchieve")
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "skillsToAchieve", target = "skillIds")
    GoalDto toDto(Goal goal);
}
