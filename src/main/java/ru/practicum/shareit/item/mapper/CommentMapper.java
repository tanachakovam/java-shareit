package ru.practicum.shareit.item.mapper;


import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;


@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CommentDto commentDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> toCommentDtoCollection(Collection<Comment> comment);

}

