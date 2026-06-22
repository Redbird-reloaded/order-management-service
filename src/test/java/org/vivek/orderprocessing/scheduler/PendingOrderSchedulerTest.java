package org.vivek.orderprocessing.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vivek.orderprocessing.scheduler.service.UpdatePendingOrdersService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PendingOrderSchedulerTest {

    @Mock
    private UpdatePendingOrdersService service;

    private PendingOrderScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new PendingOrderScheduler(service);
    }

    @Test
    void processPendingOrdersDelegatesToService() {

        scheduler.processPendingOrders();

        verify(service).processPendingOrders();
    }
}