package br.furb.corpusmapping.data.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

/**
 * Created by Janaina on 16/08/2015.
 */
public class Patient implements Serializable {

    private String name;
    private String cpf;
    private Gender gender;
    private LocalDate birthDate;
    private long id;
    private LocalDateTime lastUpdate;

    public Patient() {
    }

    public Patient(String name, String cpf, Gender gender, String birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.gender = gender;
        setBirthDate(birthDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBirthDateStr() {
        return birthDate.toString("dd/MM/yyyy");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBirthDate(String birthDate) {

        if (birthDate != null) {
            LocalDate date = LocalDate.parse(birthDate, DateTimeFormat.forPattern("dd/MM/yyyy"));
            this.birthDate = date;
        }
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

    @Override
    public String toString() {
        return getName();
    }
}
