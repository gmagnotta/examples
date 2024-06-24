package org.gmagnotta.service;

import java.util.HashMap;

import org.gmagnotta.model.Battalion;
import org.gmagnotta.model.Equipment;
import org.gmagnotta.model.Member;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Cache {

    public HashMap<Long, Member> memberHashMap = new HashMap<>();
    public HashMap<Long, Battalion> battalionHashMap = new HashMap<>();
    public HashMap<Long, Equipment> equipmentHashMap = new HashMap<>();
    
}
