package com.uwaai.datareststastemachine.item;

import com.uwaai.datareststastemachine.tag.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

@Projection(
        name = "itemProjection",
        types = { Item.class })
public interface ItemProjection {

    @Id
    UUID getId();

    String getName();

    String getPrice();

    @Value("#{@itemComponent.getTags(target.id)}")
    List<Tag> getTags();
}
