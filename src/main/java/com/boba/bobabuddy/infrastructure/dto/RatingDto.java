package com.boba.bobabuddy.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.hateoas.server.core.Relation;

/**
 * Data Transfer Objects matching the corresponding entities in the domain layer
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = RatingDto.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(collectionRelation = "ratings", itemRelation = "rating")
public class RatingDto extends SimpleRatingDto {

    @JsonIdentityReference
    private UserDto userDto;

    @JsonIdentityReference
    private RatableObjectDto ratableObject;

    public RatingDto(int rating, UserDto user, RatableObjectDto ratableObject) {
        this.rating = rating;
        this.userDto = user;
        this.ratableObject = ratableObject;
    }

    public RatingDto() {
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public RatableObjectDto getRatableObject() {
        return ratableObject;
    }

    public void setRatableObject(RatableObjectDto ratableObject) {
        this.ratableObject = ratableObject;
    }
}
