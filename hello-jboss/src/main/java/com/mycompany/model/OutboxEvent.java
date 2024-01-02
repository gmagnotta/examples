/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mycompany.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.mycompany.event.ExportedEvent;

/**
 * The outbox event entity.
 *
 * The contents of the {@link ExportedEvent} will be replicated to this entity
 * definition and persisted to
 * the database in order for Debezium to capture the event.
 *
 * @author Chris Cranford
 * 
 *         This class was adapted from
 *         https://github.com/Naros/debezium-quarkus/blob/master/extensions/outbox/runtime/src/main/java/io/debezium/quarkus/outbox/OutboxEvent.java
 */
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue
    private UUID id;

    private String aggregateType;

    private String aggregateId;

    @Column(name = "type_value")
    private String type;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(columnDefinition = "BLOB")
    private byte[] payload;

    public OutboxEvent(String aggregateType, String aggregateId, String type, byte[] payload, Date timestamp) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.type = type;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}