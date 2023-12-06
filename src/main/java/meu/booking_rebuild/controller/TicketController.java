package meu.booking_rebuild.controller;

import meu.booking_rebuild.model.BusSlotModel;
import meu.booking_rebuild.model.BusTicketModel;
import meu.booking_rebuild.model.TripModel;
import meu.booking_rebuild.repository.BusSlotRepo;
import meu.booking_rebuild.repository.TicketRepo;
import meu.booking_rebuild.repository.TripRepo;
import meu.booking_rebuild.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("api/v1/ticket")
public class TicketController {
    @Autowired
    private TicketRepo repo;
    @Autowired
    private BusSlotRepo slotRepo;
    @Autowired
    private TripRepo tripRepo;
    @PostMapping
    public ResponseEntity<?> addTicket(@RequestBody BusTicketModel model,
                                       @RequestParam UUID id_trip){
        try{
            TripModel tripModel = new TripModel();
            tripModel = tripRepo.findTripModelById(id_trip);
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try{
//            TripModel tripModel = tripRepo.findTripModelById(model.getTrip().getId());
            System.out.println(id_trip);
            String code = CodeService.convertDateTimeToCode(new Date());
            model.setCode_ticket(code);
            for(BusSlotModel busSlotModel: model.getSloots()){
                UUID slootId = busSlotModel.getId();
                BusSlotModel slot = slotRepo.findBusSlotModelByIdAndTripId(slootId, model.getTrip().getId());
                if(slot == null){
                    return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                slot.set_available(false);
                slotRepo.save(slot);
            }
            repo.save(model);
//            BusSlotModel slot = slotRepo.findBusSlotModelByTrip(model.getTrip());
//            if(slot != null)
//                repo.save(model);
//            assert slot != null;
//            slot.set_available(false);
//            slotRepo.save(slot);
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }
}