package com.boba.bobabuddy.core.service.store.impl;

import com.boba.bobabuddy.core.data.dao.StoreJpaRepository;
import com.boba.bobabuddy.core.data.dto.StoreDto;
import com.boba.bobabuddy.core.domain.Item;
import com.boba.bobabuddy.core.domain.Store;
import com.boba.bobabuddy.core.exceptions.DifferentResourceException;
import com.boba.bobabuddy.core.exceptions.DuplicateResourceException;
import com.boba.bobabuddy.core.exceptions.ResourceNotFoundException;
import com.boba.bobabuddy.core.service.store.UpdateStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * This class handle the usecase of updating stores in the system.
 */

@Service
@Transactional
public class UpdateStoreServiceImpl implements UpdateStoreService {
    private final StoreJpaRepository repo;

    /***
     Construct the usecase class
     @param repo DAO for Store entity.
     */
    @Autowired
    public UpdateStoreServiceImpl(final StoreJpaRepository repo) {
        this.repo = repo;
    }


    @Override
    public Store updateStore(Store storeToUpdate, StoreDto storePatch) throws DifferentResourceException {
        if (Objects.equals(storeToUpdate.getId(), storePatch.getId())) {
            storeToUpdate.setLocation(storePatch.getLocation());
            storeToUpdate.setName(storePatch.getName());
            storeToUpdate.setOwner(storePatch.getOwner());
            return repo.save(storeToUpdate);
        }
        throw new DifferentResourceException("Not the same store");
    }


    @Override
    public Store addItem(Store store, Item item) throws DuplicateResourceException {
        if (store.addItem(item)) return repo.save(store);
        // At the moment this will never be thrown since addItem will never fail.
        throw new DuplicateResourceException("Item already in store");
    }


    @Override
    public Store removeItem(Store store, Item item) throws ResourceNotFoundException {
        if (store.removeItem(item)) return repo.save(store);
        throw new ResourceNotFoundException("No such Item");
        //throw new ResourceNotFoundException("No such Item", new Exception()); TODO
    }
}