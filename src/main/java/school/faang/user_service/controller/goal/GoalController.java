package school.faang.user_service.controller.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping(value = "/create/{userId}")
    public GoalDto createGoal(@NonNull Long userId, @NonNull GoalDto goal) {
        return goalService.createGoal(userId, goal);
    }

    @PutMapping(value = "/update/{goalId}")
    public GoalDto updateGoal(@NonNull Long goalId, @NonNull GoalDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping(value = "/delete/{goalId}")
    public void deleteGoal(@NonNull Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping(value = "/subtasks/{goalId}")
    public List<GoalDto> findSubtasksByGoalId(@NonNull Long goalId) {
        return goalService.findSubtasksByGoalId(goalId);
    }
}
