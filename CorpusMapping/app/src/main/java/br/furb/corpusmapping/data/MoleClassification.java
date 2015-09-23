package br.furb.corpusmapping.data;

import br.furb.corpusmapping.R;

/**
 * Created by Janaina on 11/09/2015.
 */
public enum MoleClassification {

    NORMAL(R.drawable.ic_class_green, "Normal"),

    ATTENTION(R.drawable.ic_class_yellow, "Atenção"),

    DANGER(R.drawable.ic_class_red, "Perigo");

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
