package com.alexsys.smartmarket.address.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Setter
@Getter
public class Address {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  private String title;
  private String addressLine1;
  private String addressLine2;
  private String country;
  private String city;
  private String postalCode;
  private String landmark;
  private String phoneNumber;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

}
