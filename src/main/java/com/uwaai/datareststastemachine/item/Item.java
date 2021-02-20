package com.uwaai.datareststastemachine.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private String price;

    public Item(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Item() {
        this.name = null;
        this.price = null;
    }
}
