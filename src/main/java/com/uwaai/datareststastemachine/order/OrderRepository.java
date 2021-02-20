package com.uwaai.datareststastemachine.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface OrderRepository extends PagingAndSortingRepository<Order, UUID>, JpaRepository<Order, UUID> {
}
