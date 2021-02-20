package com.uwaai.datareststastemachine;

import com.uwaai.datareststastemachine.order.OrderEvent;
import com.uwaai.datareststastemachine.order.OrderState;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import java.util.EnumSet;


@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Configuration
    @EnableStateMachineFactory
    public class Config extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

        @Autowired
        private StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister;

        @Override
        public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config)
                throws Exception {
            config
                    .withPersistence()
                    .runtimePersister(stateMachineRuntimePersister);
        }

        @Override
        @SneakyThrows
        public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) {
            states.withStates().initial(OrderState.SUBMITTED).states(EnumSet.allOf(OrderState.class));
        }

        @Override
        @SneakyThrows
        public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) {
            transitions
                    .withExternal().source(OrderState.SUBMITTED).target(OrderState.PAID).event(OrderEvent.PAY)
                    .and()
                    .withExternal().source(OrderState.PAID).target(OrderState.SHIPPED).event(OrderEvent.SHIP);
        }
    }

    @Bean
    public StateMachineService<OrderState, OrderEvent> stateMachineService(
            StateMachineFactory<OrderState, OrderEvent> stateMachineFactory,
            StateMachineRuntimePersister<OrderState, OrderEvent, String> StateMachineRuntimePersister) {
        return new DefaultStateMachineService<OrderState, OrderEvent>(stateMachineFactory, StateMachineRuntimePersister);
    }
}
