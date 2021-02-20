package com.uwaai.datareststastemachine.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.statemachine.StateMachineContext;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends AbstractPersistable<UUID>  {

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Transient
    @JsonIgnore
    StateMachineContext<OrderState, OrderEvent> stateMachineContext;

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }
}
