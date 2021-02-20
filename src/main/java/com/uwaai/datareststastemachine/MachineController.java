package com.uwaai.datareststastemachine;

import com.uwaai.datareststastemachine.order.Order;
import com.uwaai.datareststastemachine.order.OrderEvent;
import com.uwaai.datareststastemachine.order.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.data.RepositoryTransition;
import org.springframework.statemachine.data.TransitionRepository;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;

@RepositoryRestController
@RequiredArgsConstructor
public class MachineController {

    final StateMachineLogListener listener = new StateMachineLogListener();

    final StateMachineService<OrderState, OrderEvent> stateMachineService;

    final StateMachinePersist<OrderState, OrderEvent, String> stateMachinePersist;

    final TransitionRepository<? extends RepositoryTransition> transitionRepository;

    private StateMachine<OrderState, OrderEvent> currentStateMachine;

    @RequestMapping(path = "/orders/{id}/receive/{machineId}/{event}", method = RequestMethod.POST)
    @SneakyThrows
    @Transactional
    public HttpEntity<String> receiveEvent(@PathVariable("id") Order order, @PathVariable("machineId") String machinedId, @PathVariable("event") OrderEvent event) {
        StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(machinedId);
        StateMachineLogListener listener = new StateMachineLogListener();
        stateMachine.addStateListener(listener);
        if(stateMachine.sendEvent(event)) {
            StateMachineContext stateMachineContext = stateMachinePersist.read(machinedId);
            stateMachinePersist.write(stateMachineContext, machinedId);
        }
        return ResponseEntity.accepted().build();
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
