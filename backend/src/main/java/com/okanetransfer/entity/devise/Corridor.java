package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "corridors")
public class Corridor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "devise_source_id", nullable = false)
    private Devise deviseSource;

    @ManyToOne
    @JoinColumn(name = "devise_destination_id", nullable = false)
    private Devise deviseDestination;

    @Column(nullable = false)
    private Boolean actif = true;

    private LocalDate dateActivation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Devise getDeviseSource() {
        return deviseSource;
    }

    public void setDeviseSource(Devise deviseSource) {
        this.deviseSource = deviseSource;
    }

    public Devise getDeviseDestination() {
        return deviseDestination;
    }

    public void setDeviseDestination(Devise deviseDestination) {
        this.deviseDestination = deviseDestination;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDate getDateActivation() {
        return dateActivation;
    }

    public void setDateActivation(LocalDate dateActivation) {
        this.dateActivation = dateActivation;
    }
}