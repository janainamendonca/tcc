package br.furb.corpusmapping;

/**
 * Created by Janaina on 26/08/2015.
 */
public enum SpecificBodyPart {

    /**/
    HEAD_FRONT("Cabeça (frente)", R.drawable.cabeca_frente),
    HEAD_BACK("Cabeça (atrás)", R.drawable.cabeca_atras),
    HEAD_LEFT("Cabeça (face direita)", R.drawable.cabeca_direita),
    HEAD_RIGHT("Cabeça (face esquerda)", R.drawable.cabeca_esquerda),

    /**/
    BODY_FRONT("Corpo (frente)", R.drawable.tronco_frente),
    BODY_BACK("Corpo (costas)", R.drawable.tronco_costas),

    /**/
    LEFT_ARM_TOP("Braço esquerdo", R.drawable.braco_esquerdo_cima),
    LEFT_ARM_DOWN("Braço esquerdo", R.drawable.braco_esquerdo_baixo),

    /**/
    RIGHT_ARM_TOP("Braço direito", R.drawable.braco_direito_cima),
    RIGHT_ARM_DOWN("Braço direito", R.drawable.braco_direito_baixo),

    /**/
    LEFT_LEG_FRONT("Perna esquerda (frente)", R.drawable.perna_esquerda_frente),
    LEFT_LEG_BACK("Perna esquerda (atrás)", R.drawable.perna_esquerda_atras),

    /**/
    RIGHT_LEG_FRONT("Perna direita (frente)", R.drawable.perna_direita_frente),
    RIGHT_LEG_BACK("Perna direita (atrás)", R.drawable.perna_direita_atras),

    /**/
    LEFT_FOOT_TOP("Pé esquerdo", R.drawable.pe_esquerdo_cima),
    LEFT_FOOT_DOWN("Pé esquerdo", R.drawable.pe_esquerdo_baixo),

    /**/
    RIGHT_FOOT_TOP("Pé direito", R.drawable.pe_direito_cima),
    RIGHT_FOOT_DOWN("Pé direito", R.drawable.pe_direito_baixo);

    private String bodyPartName;
    private int resource;

    private SpecificBodyPart(String bodyPartName, int resource) {
        this.bodyPartName = bodyPartName;
        this.resource = resource;
    }

    public String getBodyPartName() {
        return bodyPartName;
    }

    public int getResource() {
        return resource;
    }
}
