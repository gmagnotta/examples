package org.gmagnotta.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="Battalion")
@NamedQuery(name = "Battalion.findAll", query = "SELECT t FROM Battalion t")
@NamedQuery(name = "Battalion.findById", query = "SELECT t FROM Battalion t where id = :id")
@NamedQuery(name = "Battalion.findByStatus", query = "SELECT b FROM Battalion b where status=:status")
@NamedQuery(name = "Battalion.findSystemStatusByIds", query = "SELECT b FROM Battalion b where id in(:ids)")
public class Battalion {
    public static String STATIC = "static";
    public static String DEPLOYED = "deployed";
    public static String MOBILE = "mobile";
    public static String DISMOUNT = "dismount";

    @Id
    @GeneratedValue(generator = "battalionSequence")
    @SequenceGenerator(name = "battalionSequence", allocationSize = 50, initialValue = 100)
    public Long id;

    private String name;
    private String description;
    private String status;  // static, deployed, mobile. dismount

    private double altitude;
    private double longitude;
    private double latitude;

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

    @OneToMany(mappedBy = "battalion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Member> members;

    @OneToMany(mappedBy = "battalion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Equipment> equipments;


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
    // public Map<String,Integer> getEquipmentMap(){
    //     Map<String,Integer> map = new HashMap<String,Integer>();
    //     for (Equipment equipment : equipments) {
    //         Integer counter = map.get(equipment.getType())!=null?map.get(equipment.getType()):0;
    //         map.put(equipment.getType(),++counter);
    //     }
    //     return map;
    // }
    // public Map<String,String> getEquipmenStatusMap(){
    //     Map<String,String> map = new HashMap<String,String>();
    //     for (Equipment equipment : equipments) {
    //         map.put(equipment.getType(),equipment.getStatus());
    //     }
    //     return map;
    // }

}
