package com.boba.bobabuddy.infrastructure.assembler;

import com.boba.bobabuddy.core.entity.Item;
import com.boba.bobabuddy.infrastructure.controller.ItemController;
import com.boba.bobabuddy.infrastructure.controller.RatingController;
import com.boba.bobabuddy.infrastructure.controller.StoreController;
import com.boba.bobabuddy.infrastructure.dto.ItemDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ItemResourceAssembler extends SimpleIdentifiableRepresentationModelAssembler<ItemDto> {
    ItemResourceAssembler() {
        super(ItemController.class);
    }

    @Override
    public void addLinks(EntityModel<ItemDto> resource) {
        /**
         * Retain default links.
         */
        super.addLinks(resource);

        ItemDto item = Objects.requireNonNull(resource.getContent());
        // Add custom link to find associated store
        resource.add(linkTo(methodOn(StoreController.class).findById(item.getStore().getId())).withRel("stores"));
        // Add custom link to find all ratings
        resource.add(linkTo(methodOn(RatingController.class).findByRatableObject("items", item.getId())).withRel("ratings"));
    }
}