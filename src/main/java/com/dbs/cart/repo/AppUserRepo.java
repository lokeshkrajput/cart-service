package com.dbs.cart.repo;

import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepo extends CrudRepository<AppUser, String> {

    public AppUser findAppUserByEmail(String s);
}
