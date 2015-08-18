package br.furb.corpusmapping.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Janaina on 16/08/2015.
 */
public class Patient implements Serializable {

    private String name;
    private String cpf;
    private Gender gender;
    private Date birthDate;
    private long id;

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

    public Date getBirthDate() {
        return birthDate;
    }

    public String getBirthDateStr() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(birthDate);
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setBirthDate(String birthDate) {

        if (birthDate != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = df.parse(birthDate);
                this.birthDate = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
