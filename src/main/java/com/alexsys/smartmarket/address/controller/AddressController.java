package com.alexsys.smartmarket.address.controller;

import com.alexsys.smartmarket.address.model.Address;
import com.alexsys.smartmarket.address.service.AddressService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smartmarket/addresses")
public class AddressController {
    private final AddressService addressService;
    public AddressController(AddressService addressService) { this.addressService = addressService; }

    @GetMapping
    public List<Address> getAllAddresses() { return addressService.getAllAddresses(); }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Integer id) {
        Optional<Address> address = addressService.getAddressById(id);
        return address.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Address createAddress(@RequestBody Address address) { return addressService.saveAddress(address); }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Integer id, @RequestBody Address addressDetails) {
        var updatedAddress = addressService.updateAddress(id, addressDetails);
        return updatedAddress.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
