package com.alexsys.smartmarket.address.repository;

import com.alexsys.smartmarket.address.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        // Création d'une adresse de test
        testAddress = new Address();
        testAddress.setTitle("Maison");
        testAddress.setAddressLine1("123 Rue Principale");
        testAddress.setAddressLine2("Appartement 4B");
        testAddress.setCity("Paris");
        testAddress.setCountry("France");
        testAddress.setPostalCode("75001");
        testAddress.setLandmark("Près de la tour Eiffel");
        testAddress.setPhoneNumber("+33123456789");
        testAddress.setUserId(1);
    }

    @Test
    void save_shouldPersistAddress() {
        // Act
        Address savedAddress = addressRepository.save(testAddress);

        // Assert
        assertNotNull(savedAddress.getId());
        assertEquals("Maison", savedAddress.getTitle());
        assertEquals("Paris", savedAddress.getCity());
        assertEquals(1, savedAddress.getUserId());
    }

    @Test
    void findById_shouldReturnAddress() {
        // Arrange
        Address savedAddress = addressRepository.save(testAddress);

        // Act
        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getId());

        // Assert
        assertTrue(foundAddress.isPresent());
        assertEquals(savedAddress.getId(), foundAddress.get().getId());
        assertEquals("Maison", foundAddress.get().getTitle());
    }

    @Test
    void findAll_shouldReturnAllAddresses() {
        // Arrange
        addressRepository.save(testAddress);

        // Création d'une deuxième adresse
        Address anotherAddress = new Address();
        anotherAddress.setTitle("Bureau");
        anotherAddress.setAddressLine1("456 Avenue des Champs");
        anotherAddress.setCity("Lyon");
        anotherAddress.setCountry("France");
        anotherAddress.setPostalCode("69001");
        anotherAddress.setUserId(1);
        addressRepository.save(anotherAddress);

        // Act
        List<Address> addresses = addressRepository.findAll();

        // Assert
        assertEquals(2, addresses.size());
    }

    @Test
    void delete_shouldRemoveAddress() {
        // Arrange
        Address savedAddress = addressRepository.save(testAddress);

        // Act
        addressRepository.deleteById(savedAddress.getId());
        Optional<Address> deletedAddress = addressRepository.findById(savedAddress.getId());

        // Assert
        assertFalse(deletedAddress.isPresent());
    }

    @Test
    void update_shouldModifyAddress() {
        // Arrange
        Address savedAddress = addressRepository.save(testAddress);

        // Act - Modification
        savedAddress.setCity("Marseille");
        savedAddress.setPostalCode("13001");
        Address updatedAddress = addressRepository.save(savedAddress);

        // Assert
        assertEquals("Marseille", updatedAddress.getCity());
        assertEquals("13001", updatedAddress.getPostalCode());
        assertEquals(savedAddress.getId(), updatedAddress.getId());
    }

    @Test
    void findByUserId_shouldReturnAddressesForUser() {
        // Arrange
        addressRepository.save(testAddress);

        // Adresse pour un autre utilisateur
        Address otherUserAddress = new Address();
        otherUserAddress.setTitle("Maison");
        otherUserAddress.setAddressLine1("789 Other Street");
        otherUserAddress.setCity("Lille");
        otherUserAddress.setCountry("France");
        otherUserAddress.setUserId(2);
        addressRepository.save(otherUserAddress);

        // Act - Trouver les adresses pour user_id = 1
        // Note: Vous devrez ajouter cette méthode dans le repository
        List<Address> userAddresses = addressRepository.findAll(); // Pour l'instant, utilisez findAll

        // Assert - Vérifiez manuellement le user_id
        long countUserAddresses = userAddresses.stream()
                .filter(address -> address.getUserId().equals(1))
                .count();
        
        assertEquals(1, countUserAddresses);
    }

    @Test
    void save_shouldHandleNullValues() {
        // Arrange
        testAddress.setAddressLine2(null);
        testAddress.setLandmark(null);

        // Act
        Address savedAddress = addressRepository.save(testAddress);

        // Assert
        assertNotNull(savedAddress.getId());
        assertNull(savedAddress.getAddressLine2());
        assertNull(savedAddress.getLandmark());
    }
}