package br.furb.corpusmapping.data.model;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by Janaina on 05/09/2015.
 */
public class MoleGroup implements Serializable {

    private long id;
    private String groupName;
    private String annotations;
    private String description;
    private PointF position;
    private long patientId;
    private MoleClassification classification;
    private LocalDateTime lastUpdate;

    public MoleGroup() {
        this(null, null);
    }

    public MoleGroup(String groupName, String annotations) {
        this(groupName, annotations, null);
    }

    public MoleGroup(String groupName, String annotations, String description) {
        this.groupName = groupName;
        this.annotations = annotations;
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getPosition() {
        return position;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setClassification(MoleClassification classification) {
        this.classification = classification;
    }

    public MoleClassification getClassification() {
        return classification == null ? MoleClassification.NONE : classification;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
