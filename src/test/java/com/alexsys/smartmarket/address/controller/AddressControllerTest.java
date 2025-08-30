package com.alexsys.smartmarket.address.controller;

import com.alexsys.smartmarket.address.model.Address;
import com.alexsys.smartmarket.address.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
    }

    @Test
    void getAllAddresses_shouldReturnListOfAddresses() {
        // Arrange
        Address address1 = createTestAddress();
        Address address2 = createTestAddress();
        address2.setId(2);
        address2.setTitle("Bureau");
        
        List<Address> addresses = Arrays.asList(address1, address2);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        // Act
        List<Address> result = addressController.getAllAddresses();

        // Assert
        assertEquals(2, result.size());
        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    void getAddressById_shouldReturnAddressWhenExists() {
        // Arrange
        Address address = createTestAddress();
        when(addressService.getAddressById(1)).thenReturn(Optional.of(address));

        // Act
        ResponseEntity<Address> response = addressController.getAddressById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(1, response.getBody().getId());
        verify(addressService, times(1)).getAddressById(1);
    }

    @Test
    void getAddressById_shouldReturnNotFoundWhenNotExists() {
        // Arrange
        when(addressService.getAddressById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Address> response = addressController.getAddressById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
        verify(addressService, times(1)).getAddressById(999);
    }

    @Test
    void createAddress_shouldReturnCreatedAddress() {
        // Arrange
        Address addressToCreate = createTestAddress();
        addressToCreate.setId(null);
        
        Address createdAddress = createTestAddress(); // With ID set
        when(addressService.saveAddress(any(Address.class))).thenReturn(createdAddress);

        // Act
        Address result = addressController.createAddress(addressToCreate);

        // Assert
        assertNotNull(result.getId());
        assertEquals(1, result.getId());
        verify(addressService, times(1)).saveAddress(addressToCreate);
    }

    @Test
    void updateAddress_shouldReturnUpdatedAddressWhenExists() {
        // Arrange
        Address addressDetails = createTestAddress();
        addressDetails.setTitle("Nouvelle Maison");
        
        Address updatedAddress = createTestAddress();
        updatedAddress.setTitle("Nouvelle Maison");
        
        when(addressService.updateAddress(1, addressDetails)).thenReturn(Optional.of(updatedAddress));

        // Act
        ResponseEntity<Address> response = addressController.updateAddress(1, addressDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals("Nouvelle Maison", response.getBody().getTitle());
        verify(addressService, times(1)).updateAddress(1, addressDetails);
    }

    @Test
    void updateAddress_shouldReturnNotFoundWhenNotExists() {
        // Arrange
        Address addressDetails = createTestAddress();
        when(addressService.updateAddress(999, addressDetails)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Address> response = addressController.updateAddress(999, addressDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
        verify(addressService, times(1)).updateAddress(999, addressDetails);
    }

    @Test
    void deleteAddress_shouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = addressController.deleteAddress(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(response.hasBody());
        verify(addressService, times(1)).deleteAddress(1);
    }

    // Tests avec MockMvc pour les endpoints REST
    @Test
    void getAllAddresses_endpoint_shouldReturnOk() throws Exception {
        // Arrange
        setupMockMvc();
        Address address = createTestAddress();
        when(addressService.getAllAddresses()).thenReturn(List.of(address));

        // Act & Assert
        mockMvc.perform(get("/smartmarket/addresses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Maison"));
    }

    @Test
    void getAddressById_endpoint_shouldReturnOk() throws Exception {
        // Arrange
        setupMockMvc();
        Address address = createTestAddress();
        when(addressService.getAddressById(1)).thenReturn(Optional.of(address));

        // Act & Assert
        mockMvc.perform(get("/smartmarket/addresses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Maison"));
    }

    @Test
    void getAddressById_endpoint_shouldReturnNotFound() throws Exception {
        // Arrange
        setupMockMvc();
        when(addressService.getAddressById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/smartmarket/addresses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAddress_endpoint_shouldReturnCreated() throws Exception {
        // Arrange
        setupMockMvc();
        Address addressToCreate = createTestAddress();
        addressToCreate.setId(null);
        
        Address createdAddress = createTestAddress();
        when(addressService.saveAddress(any(Address.class))).thenReturn(createdAddress);

        // Act & Assert
        mockMvc.perform(post("/smartmarket/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Maison"));
    }

    @Test
    void updateAddress_endpoint_shouldReturnOk() throws Exception {
        // Arrange
        setupMockMvc();
        Address addressDetails = createTestAddress();
        addressDetails.setTitle("Updated Title");
        
        Address updatedAddress = createTestAddress();
        updatedAddress.setTitle("Updated Title");
        
        when(addressService.updateAddress(eq(1), any(Address.class))).thenReturn(Optional.of(updatedAddress));

        // Act & Assert
        mockMvc.perform(put("/smartmarket/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteAddress_endpoint_shouldReturnNoContent() throws Exception {
        // Arrange
        setupMockMvc();

        // Act & Assert
        mockMvc.perform(delete("/smartmarket/addresses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(addressService, times(1)).deleteAddress(1);
    }
}