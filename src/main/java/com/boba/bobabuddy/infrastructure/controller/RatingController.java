package com.boba.bobabuddy.infrastructure.controller;

import com.boba.bobabuddy.core.entity.Item;
import com.boba.bobabuddy.core.entity.Rating;
import com.boba.bobabuddy.core.usecase.rating.port.ICreateRating;
import com.boba.bobabuddy.core.usecase.rating.port.IFindRating;
import com.boba.bobabuddy.core.usecase.rating.port.IRemoveRating;
import com.boba.bobabuddy.core.usecase.rating.port.IUpdateRating;
import com.boba.bobabuddy.core.usecase.request.CreateRatingPointRequest;
import com.boba.bobabuddy.infrastructure.assembler.RatingResourceAssembler;
import com.boba.bobabuddy.infrastructure.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Controller for RatingPoint related api calls.
 */
@RestController
public class RatingController {

    // Input Boundary
    private final ICreateRating createRatingPoint;
    private final IFindRating findRatingPoint;
    private final IRemoveRating removeRatingPoint;
    private final IUpdateRating updateRatingPoint;
    private final RatingResourceAssembler assembler;
    private final FullDtoConverter<Rating, SimpleRatingDto, RatingDto> fullDtoConverter;


    @Autowired
    public RatingController(ICreateRating createRatingPoint, IFindRating findRatingPoint,
                            IRemoveRating removeRatingPoint, IUpdateRating updateRatingPoint,
                            RatingResourceAssembler assembler, ModelMapper mapper) {
        this.createRatingPoint = createRatingPoint;
        this.findRatingPoint = findRatingPoint;
        this.removeRatingPoint = removeRatingPoint;
        this.updateRatingPoint = updateRatingPoint;
        this.assembler = assembler;

        this.fullDtoConverter = new FullDtoConverter<>(mapper, Rating.class, SimpleRatingDto.class, RatingDto.class);
    }

    /**
     * Handles POST requests to add a Rating to the database.
     *
     * @param createRatingPointRequest request class containing the data to construct a new RatingPoint entity
     * @param id the id of the rated RatableObject
     * @param email the email of the user who made the rating
     * @return the constructed RatingPoint
     */
    @PostMapping(path = "/{ratableObject}/{id}/ratings", params = "createdBy")
    public ResponseEntity<EntityModel<RatingDto>> createRatingPoint(@RequestBody SimpleRatingDto createRatingPointRequest,
                                                                 @PathVariable UUID id, @RequestParam("createdBy") String email) {
        Rating rating = createRatingPoint.create(fullDtoConverter.convertToEntityFromSimple(createRatingPointRequest), id, email);
        RatingDto ratingToPresent = fullDtoConverter.convertToDto(rating);
        return ResponseEntity.created(linkTo(methodOn(RatingController.class).findById(ratingToPresent.getId())).toUri()).body(assembler.toModel(ratingToPresent));
    }

    /**
     * Handles GET requests for all Rating entities in the database.
     *
     * @return the list of all Rating entities
     */
    @GetMapping(path = "/ratings")
    public ResponseEntity<CollectionModel<EntityModel<RatingDto>>> findAll() {
        return ResponseEntity.ok(assembler.toCollectionModel(fullDtoConverter.convertToDtoCollection(findRatingPoint.findAll())));
    }

    /**
     * Handles GET requests for all Rating entities belonging to a RatableObject.
     *
     * @param ratableObject the name of a RatableObject subclass
     * @param id the id of the RatableObject of the same subclass
     * @return the list of Rating entities belonging to the RatableObject
     */
    @GetMapping(path = "/{ratableObject}/{id}/ratings")
    public ResponseEntity<CollectionModel<EntityModel<RatingDto>>> findByRatableObject(@PathVariable String ratableObject, @PathVariable UUID id) {
        if (ratableObject.equals("items")) {
            return ResponseEntity.ok(assembler.toCollectionModel(fullDtoConverter.convertToDtoCollection(findRatingPoint.findByItem(id))));
        }
        if(ratableObject.equals("stores")) {
            return ResponseEntity.ok(assembler.toCollectionModel(fullDtoConverter.convertToDtoCollection(findRatingPoint.findByStore(id))));
        }
        Exception e = new MalformedURLException("must be /item/ or /store/");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    /**
     * Handles GET requests for all Rating entities belonging to a User.
     *
     * @param email the email of the User
     * @return the list of Rating entities belonging to the User
     */
    @GetMapping(path = "/users/{email}/ratings")
    public ResponseEntity<CollectionModel<EntityModel<RatingDto>>> findByUser(@PathVariable String email) {
        return ResponseEntity.ok(assembler.toCollectionModel(fullDtoConverter.convertToDtoCollection(findRatingPoint.findByUser(email))));
    }

    /**
     * Handles GET requests for a Rating by its UUID.
     *
     * @param id the UUID of the Rating
     * @return the Rating with the matching UUID
     */
    @GetMapping(path = "/ratings/{id}")
    public ResponseEntity<EntityModel<RatingDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(fullDtoConverter.convertToDto(findRatingPoint.findById(id))));
    }

    /**
     * Handles DELETE requests to remove Rating by its UUID.
     *
     * @param id the UUID if the Rating to be removed from the database
     * @return NO_CONTENT http status
     */
    @DeleteMapping(path = "/ratings/{id}")
    public ResponseEntity<?> removeById(@PathVariable UUID id) {
        removeRatingPoint.removeById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handles PUT requests to update an existing Rating.
     *
     * @param id the UUID of the Rating to be updated
     * @param rating the new value of the Rating
     * @return the updated Rating
     */

    @PutMapping(path = "/ratings/{id}")
    public ResponseEntity<EntityModel<RatingDto>> updateRating(@PathVariable UUID id, @RequestBody SimpleRatingDto rating) {
        return ResponseEntity.ok(assembler.toModel(fullDtoConverter.convertToDto(updateRatingPoint.updateRating(id, rating.getRating()))));
    }
}