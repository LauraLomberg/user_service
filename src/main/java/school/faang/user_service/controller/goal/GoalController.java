package school.faang.user_service.controller.goal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping(value = "/create")
    public GoalDto createGoal(@NonNull Long userId, @NonNull GoalDto goal) {
        return goalService.createGoal(userId, goal);
    }

    @PutMapping(value = "/update")
    public GoalDto updateGoal(@NonNull Long userId, @NonNull GoalDto goal) {
        return goalService.updateGoal(userId, goal);
    }

    @DeleteMapping(value = "/delete")
    public void deleteGoal(@NonNull Long goalId) {
        goalService.deleteGoal(goalId);
    }
}
