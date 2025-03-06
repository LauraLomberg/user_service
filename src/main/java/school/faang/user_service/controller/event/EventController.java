package school.faang.user_service.controller.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    public EventDto create(@NonNull EventDto eventDto) {
        if (isInvalidToCreate(eventDto)) {
            log.warn("Не пройдена валидация события для создания. Проверьте: название, дату начала и владельца");
            throw new DataValidationException("У создаваемого события не хватает входящих данных!");
        }
        log.info("Событие {} добавлено!", eventDto.getTitle());
        return eventService.create(eventDto);
    }

    public EventDto getEvent(@NonNull Long eventId) {
        if (isIdInvalid(eventId)) {
            log.warn("Передан неположительный eventId, результат не может быть получен");
            throw new DataValidationException("Передан неверный ID события для получения данных");
        }
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(@NonNull Long eventId) {
        if (isIdInvalid(eventId)) {
            log.warn("Передан неположительный eventId, удаление невозможно");
            throw new DataValidationException("Передан неверный ID события для удаления");
        }
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(@NonNull EventDto eventDto) {
        if (isInvalidToCreate(eventDto)) {
            log.warn("Не пройдена валидация события для обновления. Проверьте название, дату начала и владельца");
            throw new DataValidationException("У обновляемого события не хватает входящих данных!");
        }
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(@NonNull Long userId) {
        if (isIdInvalid(userId)) {
            log.warn("Передан неположительный userId, невозможно получить все события, которые созданы пользователем");
            throw new DataValidationException("Передан неверный ID для получения событий, созданных пользователем");
        }
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(@NonNull Long userId) {
        if (isIdInvalid(userId)) {
            log.warn("Передан неположительный userId, невозможно получить события, где участвовал пользователь");
            throw new DataValidationException("Передан неверный ID для получения событий, созданных пользователем");
        }
        return eventService.getParticipatedEvents(userId);
    }

    private boolean isInvalidToCreate(EventDto eventDto) {
        return eventDto.getTitle().isBlank() || eventDto.getStartDate() == null || eventDto.getOwnerId() == null;
    }

    private boolean isIdInvalid(Long id) {
        return id <= 0;
    }
}
