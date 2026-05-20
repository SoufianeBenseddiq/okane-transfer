package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devises")
public class Devise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code;           // ex: "MAD", "EUR", "XOF"

    @Column(nullable = false)
    private String nom;            // ex: "Dirham Marocain"

    @Column(nullable = false, length = 10)
    private String symbole;        // ex: "د.م."

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal tauxVersEuro;

    private LocalDateTime derniereMaj;

    private String sourceTaux;     // "MANUEL" ou "API"

    // getters / setters
}