package org.vivek.orderprocessing.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vivek.orderprocessing.scheduler.service.UpdatePendingOrdersService;

@Component
@RequiredArgsConstructor
public class PendingOrderScheduler {

    private final UpdatePendingOrdersService service;

    @Scheduled(
            fixedDelayString =
                    "${order-processing.scheduler.pending-to-processing-interval}"
    )
    public void processPendingOrders() {

        service.processPendingOrders();
    }


}
