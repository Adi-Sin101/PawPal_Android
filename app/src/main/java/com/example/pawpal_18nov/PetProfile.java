package com.example.pawpal_18nov;

public class PetProfile {

    private String petName;
    private String species;
    private String gender;
    private String birth;
    private String weight;
    private String habitat;
    private String sterilize;

    // Default constructor required for Firebase
    public PetProfile() {
        // Default constructor required for Firebase
    }

    // Constructor to initialize the PetProfile with values
    public PetProfile(String petName, String species, String gender, String birth, String weight, String habitat, String sterilize) {
        this.petName = petName;
        this.species = species;
        this.gender = gender;
        this.birth = birth;
        this.weight = weight;
        this.habitat = habitat;
        this.sterilize = sterilize;
    }

    // Getters and Setters for each field
    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getSterilize() {
        return sterilize;
    }

    public void setSterilize(String sterilize) {
        this.sterilize = sterilize;
    }

    @Override
    public String toString() {
        return "PetProfile{" +
                "petName='" + petName + '\'' +
                ", species='" + species + '\'' +
                ", gender='" + gender + '\'' +
                ", birth='" + birth + '\'' +
                ", weight='" + weight + '\'' +
                ", habitat='" + habitat + '\'' +
                ", sterilize='" + sterilize + '\'' +
                '}';
    }
}

