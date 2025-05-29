package com.example.emsismartpresence;

public class Etudiant {
    private String id;
    private String nom;
    private boolean absent;
    private String remarque;

    public Etudiant() {
        // Constructeur vide nécessaire pour Firestore
    }

    public Etudiant(String id, String nom) {
        this.id = id;
        this.nom = nom;
        this.absent = false;
        this.remarque = "";
    }

    // Getters et setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
