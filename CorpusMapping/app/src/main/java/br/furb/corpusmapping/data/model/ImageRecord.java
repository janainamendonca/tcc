package br.furb.corpusmapping.data.model;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by Janaina on 05/09/2015.
 */
public class ImageRecord implements Serializable {

    private long id;
    private SpecificBodyPart bodyPart;
    private LocalDateTime imageDate;
    private PointF position;
    private Long patientId;
    private Patient patient;
    private String annotations;
    private Long moleGroupId;
    private MoleGroup moleGroup;
    private ImageType imageType;
    private String imagePath;
    private LocalDateTime lastUpdate;

    public ImageRecord() {
    }

    public SpecificBodyPart getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(SpecificBodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public LocalDateTime getImageDate() {
        return imageDate;
    }

    public void setImageDate(LocalDateTime
                                     imageDate) {
        this.imageDate = imageDate;
    }

    public String getImageDateAsString() {
        return imageDate != null ? imageDate.toString("dd/MM/yyyy HH:mm") : "";
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        if (patient != null) {
            setPatientId(patient.getId());
        }
        this.patient = patient;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public long getMoleGroupId() {
        return moleGroupId;
    }

    public void setMoleGroupId(long moleGroupId) {
        this.moleGroupId = moleGroupId;
    }

    public MoleGroup getMoleGroup() {
        return moleGroup;
    }

    public void setMoleGroup(MoleGroup moleGroup) {
        this.moleGroup = moleGroup;

        if (moleGroup != null) {
            setMoleGroupId(moleGroup.getId());
        }
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
