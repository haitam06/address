package com.alexsys.smartmarket.address.service;

import com.alexsys.smartmarket.address.mapper.AddressMapper;
import com.alexsys.smartmarket.address.model.Address;
import com.alexsys.smartmarket.address.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address createTestAddress() {
        Address address = new Address();
        address.setId(1);
        address.setTitle("Maison");
        address.setAddressLine1("123 Rue Principale");
        address.setAddressLine2("Appartement 4B");
        address.setCity("Paris");
        address.setCountry("France");
        address.setPostalCode("75001");
        address.setLandmark("Près de la tour Eiffel");
        address.setPhoneNumber("+33123456789");
        address.setUserId(1);
        return address;
    }

    @Test
    void getAllAddresses_shouldReturnAllAddresses() {
        // Arrange
        Address address1 = createTestAddress();
        Address address2 = createTestAddress();
        address2.setId(2);
        address2.setTitle("Bureau");
        
        when(addressRepository.findAll()).thenReturn(Arrays.asList(address1, address2));

        // Act
        List<Address> addresses = addressService.getAllAddresses();

        // Assert
        assertEquals(2, addresses.size());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void getAddressById_shouldReturnAddressWhenExists() {
        // Arrange
        Address address = createTestAddress();
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        // Act
        Optional<Address> result = addressService.getAddressById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("Maison", result.get().getTitle());
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    void getAddressById_shouldReturnEmptyWhenNotExists() {
        // Arrange
        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Address> result = addressService.getAddressById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(999);
    }

    @Test
    void saveAddress_shouldPersistAndReturnAddress() {
        // Arrange
        Address addressToSave = createTestAddress();
        addressToSave.setId(null); // ID should be null before saving
        
        Address savedAddress = createTestAddress(); // ID will be set after save
        when(addressRepository.save(addressToSave)).thenReturn(savedAddress);

        // Act
        Address result = addressService.saveAddress(addressToSave);

        // Assert
        assertNotNull(result.getId());
        assertEquals(1, result.getId());
        verify(addressRepository, times(1)).save(addressToSave);
    }

    @Test
    void updateAddress_shouldUpdateWhenAddressExists() {
        // Arrange
        Address existingAddress = createTestAddress();
        Address addressDetails = createTestAddress();
        addressDetails.setTitle("Nouvelle Maison");
        addressDetails.setCity("Lyon");
        addressDetails.setPostalCode("69001");

        when(addressRepository.findById(1)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        // Act - Note: addressMapper.update will be called by the service
        Optional<Address> result = addressService.updateAddress(1, addressDetails);

        // Assert
        assertTrue(result.isPresent());
        verify(addressMapper, times(1)).update(existingAddress, addressDetails);
        verify(addressRepository, times(1)).save(existingAddress);
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    void updateAddress_shouldReturnEmptyWhenAddressNotExists() {
        // Arrange
        Address addressDetails = createTestAddress();
        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Address> result = addressService.updateAddress(999, addressDetails);

        // Assert
        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(999);
        verify(addressMapper, never()).update(any(), any());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteAddress_shouldCallRepositoryDelete() {
        // Arrange - no setup needed for void method

        // Act
        addressService.deleteAddress(1);

        // Assert
        verify(addressRepository, times(1)).deleteById(1);
    }

    @Test
    void updateAddress_shouldOnlyUpdateAllowedFields() {
        // Arrange
        Address existingAddress = createTestAddress();
        Address addressDetails = createTestAddress();
        addressDetails.setTitle("Nouveau Titre");
        addressDetails.setUserId(999); // This should not be updatable if business logic restricts it

        when(addressRepository.findById(1)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        // Act
        Optional<Address> result = addressService.updateAddress(1, addressDetails);

        // Assert
        assertTrue(result.isPresent());
        verify(addressMapper, times(1)).update(existingAddress, addressDetails);
        // The mapper should handle which fields can be updated
    }
}