package org.gmagnotta.utils;

import java.util.HashMap;

import org.gmagnotta.model.Equipment;
import org.gmagnotta.model.Member;

public class ConversionUtils {

    public static org.gmagnotta.dto.Battalion toDTO(org.gmagnotta.model.Battalion battalion) {

        org.gmagnotta.dto.Battalion retVal = new org.gmagnotta.dto.Battalion();
        
        retVal.setId(battalion.getId());
        retVal.setName(battalion.getName());
        retVal.setDescription(battalion.getDescription());
        retVal.setStatus(battalion.getStatus());
        retVal.setAltitude(battalion.getAltitude());
        retVal.setLongitude(battalion.getLongitude());
        retVal.setLatitude(battalion.getLatitude());

        return retVal;

    }

    public static org.gmagnotta.dto.Equipment toDTO(org.gmagnotta.model.Equipment equipment) {
        
        org.gmagnotta.dto.Equipment retVal = new org.gmagnotta.dto.Equipment();

        retVal.setId(equipment.getId());
        retVal.setName(equipment.getName());
        retVal.setType(equipment.getType());
        retVal.setCode(equipment.getCode());
        retVal.setStatus(equipment.getStatus());

        return retVal;
    }

    public static org.gmagnotta.dto.Member toDTO(org.gmagnotta.model.Member member) {
        
        org.gmagnotta.dto.Member retVal = new org.gmagnotta.dto.Member();

        retVal.setId(member.getId());
        retVal.setName(member.getName());
        retVal.setEmail(member.getEmail());
        retVal.setRank(member.getRank());

        return retVal;
    }

    public static org.gmagnotta.dto.Battalion updateBattalionFromCache(org.gmagnotta.dto.Battalion dtoBattalion, HashMap<Long, Equipment> equipmentHashMap, HashMap<Long, Member> memberHashMap) {

        for (org.gmagnotta.model.Member member : memberHashMap.values()) {

            if (member.getBattalion().equals(dtoBattalion.getId())) {

                org.gmagnotta.dto.Member dtoMember = ConversionUtils.toDTO(member);

                dtoMember.setBattalion(dtoBattalion);

                dtoBattalion.getMembers().add(dtoMember);

            }

        }

        for (org.gmagnotta.model.Equipment equipment : equipmentHashMap.values()) {

            if (equipment.getBattalion().equals(dtoBattalion.getId())) {

                org.gmagnotta.dto.Equipment dtoEquipment = ConversionUtils.toDTO(equipment);

                dtoEquipment.setBattalion(dtoBattalion);

                dtoBattalion.getEquipments().add(dtoEquipment);
            }
        }

        return dtoBattalion;

    }
}
