package br.furb.corpusmapping.util;

import br.furb.corpusmapping.R;

/**
 * Created by Janaina on 27/08/2015.
 */
public class BodyImagesUtil {

    private static BoundingBox[] headFrontBBox = {//
            new BoundingBox(50, 250, 230, 370),//orelha p/ baixo
            new BoundingBox(10, 160, 275, 250),//orelhas
            new BoundingBox(20, 15, 260, 250)};// testa

    private static BoundingBox[] headBackBBox = {//
            new BoundingBox(50, 250, 230, 370),//orelha p/ baixo
            new BoundingBox(10, 160, 275, 250),//orelhas
            new BoundingBox(20, 15, 260, 250)};// testa

    private static final BoundingBox[] headLeftBBox = {//
        new BoundingBox(15,200,270,330),//nariz p/ baixo
        new BoundingBox(5,30,250,200)//nariz p/ baixo
    };

    private static final BoundingBox[] headRightBBox = {//
            new BoundingBox(20,210,250,320),//nariz p/ baixo
            new BoundingBox(35,25,260,210)//nariz p/ baixo
    };

    private static final BoundingBox[] armRightTopBBox = {//
            new BoundingBox(58, 290, 150, 420),//
            new BoundingBox(50, 200, 130, 290),//
            new BoundingBox(50, 140, 100, 200),//
            new BoundingBox(25, 100, 90, 140),//
            new BoundingBox(15, 20, 100, 100) // mão
    };

    private static BoundingBox[] armRightDownBBox = {//
            new BoundingBox(50, 365, 125, 420),//
            new BoundingBox(20, 200, 105, 365),//
            new BoundingBox(20, 140, 70, 200),//
            new BoundingBox(15, 15, 90, 140)//mão
    };
    private static final BoundingBox[] armLeftTopBBox = {
            new BoundingBox(20, 350, 85, 420),//
            new BoundingBox(40, 240, 100, 350),//
            new BoundingBox(60, 140, 110, 240),//
            new BoundingBox(80, 20, 140, 140)//
    };
    private static final BoundingBox[] armLeftDownBBox = {
            new BoundingBox(20, 350, 85, 410),//
            new BoundingBox(40, 240, 110, 350),//
            new BoundingBox(70, 140, 110, 240),//
            new BoundingBox(65, 20, 100, 140)//
    };


    private static final BoundingBox[] legRightFrontBBox = {
            new BoundingBox(20, 30, 100, 180),//
            new BoundingBox(40, 180, 95, 340),//
            new BoundingBox(55, 340, 100, 425)//
    };
    private static final BoundingBox[] legRightBackBBox = {
            new BoundingBox(20, 30, 105, 180),//
            new BoundingBox(25, 180, 80, 340),//
            new BoundingBox(25, 340, 55, 425)//
    };
    private static final BoundingBox[] footRightFrontBBox = {
            new BoundingBox(10, 10, 65, 90),//
            new BoundingBox(10, 90, 60, 170)//
    };
    private static final BoundingBox[] footRightBackBBox = {
            new BoundingBox(20, 20, 70, 105),//
            new BoundingBox(25, 105, 50, 165)//
    };

    private static final BoundingBox[] legLeftFrontBBox = {
            new BoundingBox(20, 30, 105, 180),//
            new BoundingBox(25, 180, 80, 340),//
            new BoundingBox(25, 340, 55, 425)//
    };
    private static final BoundingBox[] legLeftBackBBox = {
            new BoundingBox(20, 30, 100, 180),//
            new BoundingBox(40, 180, 95, 340),//
            new BoundingBox(55, 340, 100, 425)//
    };
    private static final BoundingBox[] footLeftFrontBBox = {
            new BoundingBox(10, 20, 70, 100),//
            new BoundingBox(20, 100, 70, 170)//
    };
    private static final BoundingBox[] footLeftBackBBox = {
            new BoundingBox(10, 20, 70, 105),//
            new BoundingBox(25, 105, 50, 170)//
    };

    private static final BoundingBox[] bodyFrontBBox = {//
            new BoundingBox(80, 30, 150,75 ),// pescoço
            new BoundingBox(25, 75, 225, 160),// ombros
            new BoundingBox(40, 160, 200, 370),// meio
            new BoundingBox(70, 370, 175, 400),// virilia
    };
    private static final BoundingBox[] bodyBackBBox = {//
            new BoundingBox(80, 30, 150,75 ),// pescoço
            new BoundingBox(25, 75, 225, 160),// ombros
            new BoundingBox(40, 160, 200, 370),// meio
            new BoundingBox(70, 370, 175, 400),// virilia
    };


    public static BoundingBox[] getBoundingBoxForImage(int resourceId) {

        switch (resourceId) {
            case R.drawable.cabeca_frente:
                return getHeadFrontBBox();
            case R.drawable.cabeca_atras:
                return getHeadBackBBox();
            case R.drawable.cabeca_esquerda:
                return getHeadLeftBBox();
            case R.drawable.cabeca_direita:
                return getHeadRightBBox();
            //braços
            case R.drawable.braco_direito_cima:
                return getArmRightTopBBox();
            case R.drawable.braco_direito_baixo:
                return getArmRightDownBBox();
            case R.drawable.braco_esquerdo_cima:
                return getArmLeftTopBBox();
            case R.drawable.braco_esquerdo_baixo:
                return getArmLeftDownBBox();
            //tronco
            case R.drawable.tronco_frente:
                return getBodyFrontBBox();
            case R.drawable.tronco_costas:
                return getBodyBackBBox();
            // pernas
            case R.drawable.perna_direita_frente:
                return getLegRightFrontBBox();
            case R.drawable.perna_direita_atras:
                return getLegRightBackBBox();
            case R.drawable.perna_esquerda_atras:
                return getLegLeftBackBBox();
            case R.drawable.perna_esquerda_frente:
                return getLegLeftFrontBBox();
            // pés
            case R.drawable.pe_direito_cima:
                return getFootRightFrontBBox();
            case R.drawable.pe_direito_baixo:
                return getFootRightBackBBox();
            case R.drawable.pe_esquerdo_cima:
                return getFootLeftFrontBBox();
            case R.drawable.pe_esquerdo_baixo:
                return getFootLeftBackBBox();
        }

        return new BoundingBox[0];
    }

    public static BoundingBox[] getHeadFrontBBox() {
        return headFrontBBox;
    }

    public static BoundingBox[] getHeadBackBBox() {
        return headBackBBox;
    }

    public static BoundingBox[] getHeadLeftBBox() {
        return headLeftBBox;
    }

    public static BoundingBox[] getHeadRightBBox() {
        return headRightBBox;
    }

    public static BoundingBox[] getArmRightTopBBox() {
        return armRightTopBBox;
    }

    public static BoundingBox[] getArmRightDownBBox() {
        return armRightDownBBox;
    }

    public static BoundingBox[] getArmLeftTopBBox() {
        return armLeftTopBBox;
    }

    public static BoundingBox[] getArmLeftDownBBox() {
        return armLeftDownBBox;
    }


    public static BoundingBox[] getLegRightFrontBBox() {
        return legRightFrontBBox;
    }

    public static BoundingBox[] getLegRightBackBBox() {
        return legRightBackBBox;
    }

    public static BoundingBox[] getFootRightFrontBBox() {
        return footRightFrontBBox;
    }

    public static BoundingBox[] getFootRightBackBBox() {
        return footRightBackBBox;
    }

    public static BoundingBox[] getLegLeftFrontBBox() {
        return legLeftFrontBBox;
    }

    public static BoundingBox[] getLegLeftBackBBox() {
        return legLeftBackBBox;
    }

    public static BoundingBox[] getFootLeftFrontBBox() {
        return footLeftFrontBBox;
    }

    public static BoundingBox[] getFootLeftBackBBox() {
        return footLeftBackBBox;
    }

    public static BoundingBox[] getBodyFrontBBox() {
        return bodyFrontBBox;
    }

    public static BoundingBox[] getBodyBackBBox() {
        return bodyBackBBox;
    }
}
