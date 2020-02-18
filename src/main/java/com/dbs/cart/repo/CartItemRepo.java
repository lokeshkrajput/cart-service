package com.dbs.cart.repo;

import com.dbs.cart.domain.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepo extends CrudRepository<CartItem, String> {

}
