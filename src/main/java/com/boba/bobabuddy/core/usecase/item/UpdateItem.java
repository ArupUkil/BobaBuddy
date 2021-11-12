package com.boba.bobabuddy.core.usecase.item;

import com.boba.bobabuddy.core.entity.Item;
import com.boba.bobabuddy.core.usecase.exceptions.DifferentResourceException;
import com.boba.bobabuddy.core.usecase.item.port.IUpdateItem;
import com.boba.bobabuddy.infrastructure.database.ItemJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * This class handle the usecase of updating items in the system.
 */

@Service
@Transactional
public class UpdateItem implements IUpdateItem {
    private final ItemJpaRepository repo;

    /***
     * Construct the usecase class
     * @param repo the repository that hosts the Item entity.
     */
    public UpdateItem(final ItemJpaRepository repo) {
        this.repo = repo;
    }

    /***
     * Update an Item by overwriting it.
     * The api request is responsible for sending in an Item representation that was modified.
     * If there exists an Item entity within the database that has the same uuid, data will be overwritten and thus
     * updated.
     * However, if no Item with the same uuid exist an exception will be thrown.
     * @param itemToUpdate Item to update.
     * @param itemPatch the overwriting item.
     * @return the updated item.
     * @throws DifferentResourceException thrown when itemPatch have a different id than the itemToUpdate
     */
    @Override
    public Item updateItem(Item itemToUpdate, Item itemPatch) throws DifferentResourceException {
        if (Objects.equals(itemToUpdate, itemPatch)) {
            return repo.save(itemPatch);
        }
        throw new DifferentResourceException("Not the same item", new Exception());
    }


}
