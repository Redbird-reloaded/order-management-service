package org.vivek.orderprocessing.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vivek.orderprocessing.scheduler.service.UpdatePendingOrdersService;

@Component
public class PendingOrderScheduler {

    private final UpdatePendingOrdersService service;

    public PendingOrderScheduler(UpdatePendingOrdersService updatePendingOrdersService){
        this.service = updatePendingOrdersService;
    }
    @Scheduled(
            fixedDelayString =
                    "${order-processing.scheduler.pending-to-processing-interval}"
    )
    public void processPendingOrders() {

        service.processPendingOrders();
    }


}
