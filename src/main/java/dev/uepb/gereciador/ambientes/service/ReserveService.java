package dev.uepb.gereciador.ambientes.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.GetReservesPerDayRequest;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import dev.uepb.gereciador.ambientes.repository.ReserveRespository;

@Service
public class ReserveService {

    @Autowired
    private ReserveRespository reserveRespository;

    private final LocalTime openTimeUEPB = LocalTime.of(8, 0); // 08:00
    private final LocalTime closeTimeUEPB = LocalTime.of(22, 0); // 22:00

    public Reserve createReserve(String userId, CreateReserveRequest createReserveRequest)
            throws ResponseStatusException {
        if (createReserveRequest.date().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dia inválido");
        }

        createReserveRequest.slots().forEach(slot -> {
            boolean isOneHour = slot.startTime().plusHours(1).equals(slot.endTime());
            boolean startsOnHour = slot.startTime().getMinute() == 0;
            boolean isTimeBeforeNow = createReserveRequest.date().equals(LocalDate.now())
                    && slot.startTime().isBefore(LocalTime.now());

            if (!isOneHour || !startsOnHour || isTimeBeforeNow
                    || slot.startTime().isBefore(openTimeUEPB)
                    || slot.endTime().isAfter(closeTimeUEPB)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot inválido");
            }
        });

        List<Reserve> reserves = reserveRespository.findAllByEnvironmentIdAndDate(
                createReserveRequest.environmentId(), createReserveRequest.date());

        List<Slot> slots =
                reserves.stream().flatMap(reserve -> reserve.getSlots().stream()).toList();

        List<ReserveSlot> slotsRequest = createReserveRequest.slots();

        boolean hasTimeConflict = slotsRequest.stream().anyMatch(requestSlot -> {
            return slots.stream().anyMatch(slot -> {
                return requestSlot.startTime().equals(slot.getStartTime());
            });
        });

        if (hasTimeConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflito de horário");
        }

        Reserve newReserve = new Reserve();

        newReserve.setUserId(userId);
        newReserve.setEnvironmentId(createReserveRequest.environmentId());
        newReserve.setJustification(createReserveRequest.justification());
        newReserve.setNumberOfParticipants(createReserveRequest.numberOfParticipants());
        newReserve.setDate(createReserveRequest.date());
        newReserve.setStatus(ReserveStatus.PENDING);
        newReserve.setSlots(createReserveRequest.slots().stream().map(Slot::new).toList());

        reserveRespository.save(newReserve);

        return newReserve;
    }

    public List<Slot> getReservesPerDay(GetReservesPerDayRequest getReservesPerDayRequest) {
        List<Reserve> reserves = reserveRespository.findAllByEnvironmentIdAndDate(
                getReservesPerDayRequest.environmentId(), getReservesPerDayRequest.date());

        List<Slot> reservedSlots =
                reserves.stream().flatMap(reserve -> reserve.getSlots().stream()).toList();

        List<Slot> unreservedSlots = new ArrayList<>();
        LocalTime currentTime = openTimeUEPB;

        while (currentTime.isBefore(closeTimeUEPB)) {
            Slot newSlot = new Slot();
            newSlot.setStartTime(currentTime);
            newSlot.setEndTime(currentTime.plusHours(1));

            unreservedSlots.add(newSlot);
            currentTime = currentTime.plusHours(1);
        }

        return unreservedSlots.stream().filter(unreservedSlot -> reservedSlots.stream().noneMatch(
                reservedSlot -> reservedSlot.getStartTime().equals(unreservedSlot.getStartTime())))
                .toList();
    }

    public List<Reserve> getReservesByUserId(String userId) {
        return reserveRespository.findAllByUserId(userId);
    }
}
