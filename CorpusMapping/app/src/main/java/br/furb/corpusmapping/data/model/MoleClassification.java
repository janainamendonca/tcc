package br.furb.corpusmapping.data.model;

import br.furb.corpusmapping.R;

/**
 * Created by Janaina on 11/09/2015.
 */
public enum MoleClassification {

    NORMAL(R.drawable.ic_class_green, "Baixo"),

    ATTENTION(R.drawable.ic_class_yellow, "Médio"),

    DANGER(R.drawable.ic_class_red, "Alto"),

    NONE(R.drawable.ic_ic_class_gray, "Não classificado");

    private int resource;
    private String description;

    private MoleClassification(int resource, String description){
        this.resource = resource;
        this.description = description;
    }

    public int getResource() {
        return resource;
    }

    public String getDescription() {
        return description;
    }
}
