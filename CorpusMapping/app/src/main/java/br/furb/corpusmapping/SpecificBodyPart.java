package br.furb.corpusmapping;

/**
 * Created by Janaina on 26/08/2015.
 */
public enum SpecificBodyPart {

    /**/
    HEAD_FRONT("Cabeça (frente)"),
    HEAD_BACK("Cabeça (atrás)"),
    HEAD_LEFT("Cabeça (face direita)"),
    HEAD_RIGHT("Cabeça (face esquerda)"),

    /**/
    BODY_FRONT("Corpo (frente)"),
    BODY_BACK("Corpo (costas)"),

    /**/
    LEFT_ARM_TOP("Braço esquerdo"),
    LEFT_ARM_DOWN("Braço esquerdo"),

    /**/
    RIGHT_ARM_TOP("Braço direito"),
    RIGHT_ARM_DOWN("Braço direito"),

    /**/
    LEFT_LEG_FRONT("Perna esquerda (frente)"),
    LEFT_LEG_BACK("Perna esquerda (atrás)"),

    /**/
    RIGHT_LEG_FRONT("Perna direita (frente)"),
    RIGHT_LEG_BACK("Perna direita (atrá)"),

    /**/
    LEFT_FOOT_TOP("Pé esquerdo"),
    LEFT_FOOT_DOWN("Pé esquerdo"),

    /**/
    RIGHT_FOOT_TOP("Pé direito"),
    RIGHT_FOOT_DOWN("Pé direito");

    private String bodyPartName;

    private SpecificBodyPart(String bodyPartName){
        this.bodyPartName = bodyPartName;
    }

    public String getBodyPartName() {
        return bodyPartName;
    }
}
