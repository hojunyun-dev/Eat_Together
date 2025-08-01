package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatGroupUpdateRequestDto;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NullIgnoreMapper {
    ChatGroup updateChatGroup(ChatGroupUpdateRequestDto chatGroupUpdateRequestDto, @MappingTarget ChatGroup.ChatGroupBuilder chatGroupBuilder);
}
