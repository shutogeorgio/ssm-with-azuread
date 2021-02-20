package com.uwaai.datareststastemachine.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = ItemProjection.class)
public interface ItemRepository extends PagingAndSortingRepository<Item, UUID> , JpaRepository<Item, UUID> {
}
