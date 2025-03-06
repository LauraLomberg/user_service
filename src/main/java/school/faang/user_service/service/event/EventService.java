package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        if (hasNoRelatedSkill(event)) {
            log.warn("Пользователь {} пытается создать событие, но у него не хватает навыков",
                    event.getOwner().getUsername());
            throw new DataValidationException("Попытка создания события пользователем без требуемых навыков");
        }
        event = eventRepository.save(event);
        log.info("Событие {} добавлено!", event.getTitle());
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Был запрошен пользователь с несуществующим ID = {}", eventId);
            return new DataValidationException("Неверно указан ID пользователя");
        });
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> filteredEvents = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filter)) {
                filteredEvents = eventFilter.apply(filteredEvents, filter);
            }
        }
        return filteredEvents.map(eventMapper::toDto).toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
        log.info("Событие с ID = {} удалено", eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        if (eventDto.getId() == null) {
            log.warn("При обновлении события, его ID не может быть null");
            throw new DataValidationException("ID не может быть null, чтобы обновить событие");
        }
        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("Передан неверный ID события"));
        if (hasNoRelatedSkill(event)) {
            log.warn("Пользователь {} пытается обновить событие, но у него не хватает навыков",
                    event.getOwner().getUsername());
            throw new DataValidationException("Попытка обновления события пользователем без требуемых навыков");
        }
        eventMapper.update(eventDto, event);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> filteredParticipatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return filteredParticipatedEvents.stream().map(eventMapper::toDto).toList();
    }

    private boolean hasNoRelatedSkill(Event event) {
        List<Skill> relatedSkills = event.getOwner()
                .getSkills()
                .stream()
                .filter(skill -> event.getRelatedSkills().contains(skill))
                .toList();
        return relatedSkills.isEmpty();
    }
}
