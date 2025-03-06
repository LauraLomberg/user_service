package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.ErrorCode;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public Goal createGoal(Long userId, Goal goal) {
        validateGoal(userId, goal);
        return goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
    }

    private void validateGoal(Long userId, Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isBlank()) {
            throw new ValidationException(ErrorCode.GOAL_EMPTY_TITLE);
        }
        if (goalRepository.countActiveGoalsPerUser(userId) > MAX_ACTIVE_GOALS) {
            throw new ValidationException(ErrorCode.MAX_ACTIVE_GOALS);
        }
        goal.getSkillsToAchieve().forEach(skill -> {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                throw new ValidationException(ErrorCode.GOAL_NON_EXISTING_SKILLS);
            }
        });
    }
}
