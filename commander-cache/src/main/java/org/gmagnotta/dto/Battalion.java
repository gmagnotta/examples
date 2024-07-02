package org.gmagnotta.dto;

import java.util.HashSet;
import java.util.Set;

public class Battalion {
    public static String STATIC = "static";
    public static String DEPLOYED = "deployed";
    public static String MOBILE = "mobile";
    public static String DISMOUNT = "dismount";

    private Long id;

    private String name;
    private String description;
    private String status;  // static, deployed, mobile. dismount

    private double altitude;
    private double longitude;
    private double latitude;

    private Set<Member> members;

    private Set<Equipment> equipments;

    public Battalion() {
        this.members = new HashSet<>();
        this.equipments = new HashSet<>();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Set<Member> getMembers() {
        return members;
    }
    public Set<Equipment> getEquipments() {
        return equipments;
    }
    public void setMembers(Set<Member> members) {
        this.members = members;
    }
    public void setEquipments(Set<Equipment> equipments) {
        this.equipments = equipments;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
