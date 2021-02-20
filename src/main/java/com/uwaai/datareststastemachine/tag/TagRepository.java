package com.uwaai.datareststastemachine.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, UUID> ,JpaRepository<Tag, UUID> {
    List<Tag> findAllByItemId(UUID itemId);
}
