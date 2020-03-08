package com.dbs.cart.repo;

import com.dbs.cart.domain.CartItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartItemRepo extends CrudRepository<CartItem, String> {

    @Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId order by c.itemCode asc")
    public List<CartItem> findCartItemByUserId(String userId);
}
