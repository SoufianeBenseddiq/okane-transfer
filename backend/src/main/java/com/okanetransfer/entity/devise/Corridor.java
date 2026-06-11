package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "corridors")
public class Corridor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pays_source_id", nullable = false)
    private Pays paysSource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pays_destination_id", nullable = false)
    private Pays paysDestination;

    @Column(nullable = false)
    private Boolean actif = true;

    private LocalDate dateActivation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pays getPaysSource() { return paysSource; }
    public void setPaysSource(Pays paysSource) { this.paysSource = paysSource; }

    public Pays getPaysDestination() { return paysDestination; }
    public void setPaysDestination(Pays paysDestination) { this.paysDestination = paysDestination; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDate getDateActivation() { return dateActivation; }
    public void setDateActivation(LocalDate dateActivation) { this.dateActivation = dateActivation; }
}
