package com.uwaai.datareststastemachine;

import com.uwaai.datareststastemachine.order.OrderEvent;
import com.uwaai.datareststastemachine.order.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ObjectUtils;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("ut")
public class JpaRepositoryTests {

    public final static String MACHINE_ID_1 = "8aa31026-027a-4923-9e01-e77c62bc3d65";
    public final static String MACHINE_ID_2 = "2683b3cc-2c45-400d-890d-5d6a640386b4";
    public final static String MACHINE_ID_3 = "23c314fa-beb5-4087-ac44-b654e19f5d8b";

    @Autowired
    private StateMachineService<OrderState, OrderEvent> stateMachineService;

    @Autowired
    private StateMachinePersist<OrderState, OrderEvent, String> stateMachinePersist;

    private StateMachine<OrderState, OrderEvent> currentStateMachine;

    private StateMachineLogListener listener = new StateMachineLogListener();

    @Test
    public void parallelInstantiation() throws Exception {

        // Start First State Machine
        StateMachine<OrderState, OrderEvent> firstStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_1);
        firstStateMachine.start();

        // Start Second State Machine
        StateMachine<OrderState, OrderEvent> secondStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_2);
        secondStateMachine.start();
        secondStateMachine.sendEvent(OrderEvent.PAY);

        // Start Third State Machine
        StateMachine<OrderState, OrderEvent> thirdStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_3);
        thirdStateMachine.sendEvent(OrderEvent.PAY);
        thirdStateMachine.sendEvent(OrderEvent.SHIP);

        assertThat(firstStateMachine.getState().getId()).isEqualTo(OrderState.SUBMITTED);
        assertThat(secondStateMachine.getState().getId()).isEqualTo(OrderState.PAID);
        assertThat(thirdStateMachine.getState().getId()).isEqualTo(OrderState.SHIPPED);
    }

    @Test
    public void persistWithJpa() throws Exception {

        // Start First State Machine
        StateMachine<OrderState, OrderEvent> firstStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_1);
        firstStateMachine.start();
        firstStateMachine.sendEvent(OrderEvent.PAY);

        StateMachineContext firstStateMachineContext = stateMachinePersist.read(MACHINE_ID_1);
        stateMachinePersist.write(firstStateMachineContext, MACHINE_ID_1);
        assertThat(firstStateMachineContext.getState()).isEqualTo(OrderState.PAID);

        // Start Second StateMachine
        StateMachine<OrderState, OrderEvent> secondStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_2);
        secondStateMachine.start();

        // Restore State Machine, restart and Send Event
        StateMachine<OrderState, OrderEvent> restoredStateMachine = stateMachineService.acquireStateMachine(MACHINE_ID_1);
        assertThat(restoredStateMachine.getState().getId()).isEqualTo(OrderState.PAID);

        restoredStateMachine.start();
        restoredStateMachine.sendEvent(OrderEvent.SHIP);

        StateMachineContext restoredStateMachineContext = stateMachinePersist.read(MACHINE_ID_1);
        stateMachinePersist.write(restoredStateMachineContext, MACHINE_ID_1);

        assertThat(restoredStateMachineContext.getState()).isEqualTo(OrderState.SHIPPED);
    }

    private synchronized StateMachine<OrderState, OrderEvent> getStateMachine(String machineId) throws Exception {
        listener.resetMessages();
        if (currentStateMachine == null) {
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.addStateListener(listener);
            currentStateMachine.start();
        } else if (!ObjectUtils.nullSafeEquals(currentStateMachine.getId(), machineId)) {
            stateMachineService.releaseStateMachine(currentStateMachine.getId());
            currentStateMachine.stop();
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.addStateListener(listener);
            currentStateMachine.start();
        }
        return currentStateMachine;
    }
}
