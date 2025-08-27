package com.alexsys.smartmarket.address.repository;

import com.alexsys.smartmarket.address.model.Address;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
