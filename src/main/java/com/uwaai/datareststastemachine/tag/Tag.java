package com.uwaai.datareststastemachine.tag;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "type")
    private String type;

    public Tag(UUID itemId, String type) {
        this.itemId = itemId;
        this.type = type;
    }

    public Tag() {
        this.itemId = null;
        this.type = null;
    }
}
