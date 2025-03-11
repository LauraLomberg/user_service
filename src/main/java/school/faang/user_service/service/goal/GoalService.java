package school.faang.user_service.service.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.ErrorCode;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        Goal goal = findGoal(goalDto.getId());
        validateGoal(userId, goal);
        Goal createdGoal = goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        addSkillsToGoal(goalDto.getSkillIds(), goalDto.getId());
        return goalMapper.toDto(createdGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long userId, GoalDto goalDto) {
        Goal goal = findGoal(goalDto.getId());
        validateGoal(userId, goal);
        if (GoalStatus.COMPLETED.equals(goal.getStatus()) && !goal.getSkillsToAchieve().isEmpty()) {
            assignSkillsToUsers(goal, goalDto);
            updateSkills(goalDto);
        }
        return goalMapper.toDto(goal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(@NonNull Long goalId) {
        List<Goal> goals = goalRepository.findByParent(goalId).toList();
        return goals.stream().map(goalMapper::toDto).toList();
    }

    private void assignSkillsToUsers(Goal goal, GoalDto goalDto) {
        goal.getUsers().forEach(user -> goalDto.getSkillIds().forEach(skillId ->
                skillRepository.assignSkillToUser(skillId, user.getId())));
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

    private Goal findGoal(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new ValidationException(ErrorCode.GOAL_NOT_FOUND));
    }

    private void addSkillsToGoal(List<Long> skillIds, Long goalId) {
        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));
    }

    private void updateSkills(GoalDto goalDto) {
        goalRepository.removeSkillsFromGoal(goalDto.getId());
        addSkillsToGoal(goalDto.getSkillIds(), goalDto.getId());
    }
}
