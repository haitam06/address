package com.alexsys.smartmarket.address.mapper;

import com.alexsys.smartmarket.address.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AddressMapperTest {

    @Autowired
    private AddressMapper addressMapper;

    private Address createSourceAddress() {
        Address address = new Address();
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

    private Address createTargetAddress() {
        Address address = new Address();
        address.setId(1);
        address.setTitle("Bureau");
        address.setAddressLine1("456 Avenue des Champs");
        address.setAddressLine2(null);
        address.setCity("Lyon");
        address.setCountry("France");
        address.setPostalCode("69001");
        address.setLandmark(null);
        address.setPhoneNumber("+33456789012");
        address.setUserId(1);
        return address;
    }

    @Test
    void update_shouldUpdateNonNullFields() {
        // Arrange
        Address target = createTargetAddress();
        Address source = createSourceAddress();
        
        // Change some fields to null in source to test null ignoring
        source.setAddressLine2(null);
        source.setLandmark(null);

        // Act
        addressMapper.update(target, source);

        // Assert - Fields should be updated
        assertEquals("Maison", target.getTitle());
        assertEquals("123 Rue Principale", target.getAddressLine1());
        assertEquals("Paris", target.getCity());
        assertEquals("75001", target.getPostalCode());
        assertEquals("+33123456789", target.getPhoneNumber());
        
        // Assert - Null fields in source should NOT update target (should remain unchanged)
        assertNull(target.getAddressLine2()); // Was null in source, should remain null
        assertNull(target.getLandmark()); // Was null in source, should remain null
        
        // Assert - Fields not in source should remain unchanged
        assertEquals(1, target.getId());
        assertEquals(1, target.getUserId());
    }

    @Test
    void update_shouldNotUpdateWhenSourceFieldsAreNull() {
        // Arrange
        Address target = createTargetAddress();
        Address source = new Address(); // Empty source with all null fields
        
        // Remember original values
        String originalTitle = target.getTitle();
        String originalCity = target.getCity();
        Integer originalUserId = target.getUserId();

        // Act
        addressMapper.update(target, source);

        // Assert - All fields should remain unchanged
        assertEquals(originalTitle, target.getTitle());
        assertEquals(originalCity, target.getCity());
        assertEquals(originalUserId, target.getUserId());
        assertEquals(1, target.getId());
    }

    @Test
    void update_shouldHandlePartialUpdates() {
        // Arrange
        Address target = createTargetAddress();
        Address source = new Address();
        source.setTitle("Nouvelle Maison");
        source.setCity("Marseille");
        // Only set these two fields, others remain null

        // Act
        addressMapper.update(target, source);

        // Assert - Only the set fields should be updated
        assertEquals("Nouvelle Maison", target.getTitle());
        assertEquals("Marseille", target.getCity());
        
        // Assert - Other fields should remain unchanged
        assertEquals("456 Avenue des Champs", target.getAddressLine1());
        assertEquals("69001", target.getPostalCode());
        assertEquals("+33456789012", target.getPhoneNumber());
        assertEquals(1, target.getId());
        assertEquals(1, target.getUserId());
    }

    @Test
    void update_shouldIgnoreNullValuesFromSource() {
        // Arrange
        Address target = createTargetAddress();
        Address source = createSourceAddress();
        
        // Set some fields to null in source
        source.setAddressLine2(null);
        source.setLandmark(null);
        source.setPhoneNumber(null);

        // Remember original values for null fields
        String originalPhoneNumber = target.getPhoneNumber();

        // Act
        addressMapper.update(target, source);

        // Assert - Non-null fields should be updated
        assertEquals("Maison", target.getTitle());
        assertEquals("123 Rue Principale", target.getAddressLine1());
        assertEquals("Paris", target.getCity());
        
        // Assert - Null fields should NOT update target (should remain unchanged)
        assertNull(target.getAddressLine2()); // Was explicitly set to null in source
        assertNull(target.getLandmark()); // Was explicitly set to null in source
        assertEquals(originalPhoneNumber, target.getPhoneNumber()); // Should remain unchanged because source was null
    }

    @Test
    void update_shouldWorkWithEmptyTarget() {
        // Arrange
        Address target = new Address(); // Empty target
        Address source = createSourceAddress();

        // Act
        addressMapper.update(target, source);

        // Assert - All fields should be copied from source
        assertEquals("Maison", target.getTitle());
        assertEquals("123 Rue Principale", target.getAddressLine1());
        assertEquals("Appartement 4B", target.getAddressLine2());
        assertEquals("Paris", target.getCity());
        assertEquals("France", target.getCountry());
        assertEquals("75001", target.getPostalCode());
        assertEquals("Près de la tour Eiffel", target.getLandmark());
        assertEquals("+33123456789", target.getPhoneNumber());
        assertEquals(1, target.getUserId());
        
        // Assert - ID should remain null (not in source)
        assertNull(target.getId());
    }

    @Test
    void update_shouldWorkWithEmptySource() {
        // Arrange
        Address target = createTargetAddress();
        Address source = new Address(); // Empty source

        // Remember all original values
        Integer originalId = target.getId();
        String originalTitle = target.getTitle();
        String originalAddressLine1 = target.getAddressLine1();
        String originalCity = target.getCity();
        Integer originalUserId = target.getUserId();

        // Act
        addressMapper.update(target, source);

        // Assert - All fields should remain unchanged
        assertEquals(originalId, target.getId());
        assertEquals(originalTitle, target.getTitle());
        assertEquals(originalAddressLine1, target.getAddressLine1());
        assertEquals(originalCity, target.getCity());
        assertEquals(originalUserId, target.getUserId());
    }
}