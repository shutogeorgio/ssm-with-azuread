package com.uwaai.datareststastemachine.item;

import com.uwaai.datareststastemachine.tag.Tag;
import com.uwaai.datareststastemachine.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemComponent {

    final TagRepository tagRepository;

    public List<Tag> getTags(UUID itemId) {
        return tagRepository.findAllByItemId(itemId);
    }
}
