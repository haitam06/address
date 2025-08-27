package com.alexsys.smartmarket.address.service;

import com.alexsys.smartmarket.address.mapper.AddressMapper;
import com.alexsys.smartmarket.address.model.Address;
import com.alexsys.smartmarket.address.repository.AddressRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    public List<Address> getAllAddresses() { return addressRepository.findAll(); }
    public Optional<Address> getAddressById(Integer id) { return addressRepository.findById(id); }
    public Address saveAddress(Address address) { return addressRepository.save(address); }
    public Optional<Address> updateAddress(Integer id, Address addressDetails) {
        var existingAddressOptional = getAddressById(id);
        if (existingAddressOptional.isEmpty()) {
            return Optional.empty();
        }
        var existingAddress = existingAddressOptional.get();
        addressMapper.update(existingAddress, addressDetails);
        return Optional.ofNullable(addressRepository.save(existingAddress));
    }
    public void deleteAddress(Integer id) { addressRepository.deleteById(id); }
}
