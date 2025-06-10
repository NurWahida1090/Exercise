package com.example.projekakhir.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Exercise {

    @SerializedName("exerciseId")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("gifUrl")
    private String gifUrl;

    @SerializedName("instructions")
    private List<String> instructions;

    @SerializedName("targetMuscles")
    private List<String> targetMuscles;

    @SerializedName("bodyParts")
    private List<String> bodyParts;

    @SerializedName("equipments")
    private List<String> equipments;

    @SerializedName("secondaryMuscles")
    private List<String> secondaryMuscles;

    // Getter dan Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getGifUrl() { return gifUrl; }
    public List<String> getInstructions() { return instructions; }
    public List<String> getTargetMuscles() { return targetMuscles; }
    public List<String> getBodyParts() { return bodyParts; }
    public List<String> getEquipments() { return equipments; }
    public List<String> getSecondaryMuscles() { return secondaryMuscles; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }
    public void setTargetMuscles(List<String> targetMuscles) { this.targetMuscles = targetMuscles; }
    public void setBodyParts(List<String> bodyParts) { this.bodyParts = bodyParts; }
    public void setEquipments(List<String> equipments) { this.equipments = equipments; }
    public void setSecondaryMuscles(List<String> secondaryMuscles) { this.secondaryMuscles = secondaryMuscles; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Exercise exercise = (Exercise) obj;
        return id != null && id.equals(exercise.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
