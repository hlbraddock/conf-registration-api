package org.cru.crs.model;

import org.codehaus.jackson.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "CONFERENCE_COSTS_BLOCKS")
public class ConferenceCostsBlockEntity implements java.io.Serializable
{

    @Id
    @Column(name = "ID")
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "ENABLING_ANSWER")
    @Type(type="org.cru.crs.utils.JsonUserType")
    JsonNode answerToEnableAdjustment;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public JsonNode getAnswerToEnableAdjustment()
    {
        return answerToEnableAdjustment;
    }

    public void setAnswerToEnableAdjustment(JsonNode answerToEnableAdjustment)
    {
        this.answerToEnableAdjustment = answerToEnableAdjustment;
    }

}