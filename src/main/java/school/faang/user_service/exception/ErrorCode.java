package school.faang.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /** Ошибки целей */
    GOAL_EMPTY_TITLE("Пустой заголовок цели"),
    MAX_ACTIVE_GOALS("Число активных целей превышает допустимое значение"),
    GOAL_NON_EXISTING_SKILLS("В цели есть несуществующий навык"),
    GOAL_NOT_FOUND("Цель не найдена");

    private final String description;
}
