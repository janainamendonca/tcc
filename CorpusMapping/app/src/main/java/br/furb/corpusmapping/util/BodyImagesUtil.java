package br.furb.corpusmapping.util;

import br.furb.corpusmapping.R;

import static br.furb.corpusmapping.ImageSliderActivity.PARAM_IMAGES;

/**
 * Created by Janaina on 27/08/2015.
 */
public class BodyImagesUtil {

    private static BoundingBox[] headFrontBBox = {//
            new BoundingBox(50, 250, 230, 370),//orelha p/ baixo
            new BoundingBox(10, 160, 275, 250),//orelhas
            new BoundingBox(20, 15, 260, 250)};// testa

    private static BoundingBox[] headBackBBox = {};

    private static BoundingBox[] headLeftBBox = {};

    private static BoundingBox[] headRightBBox = {};

    private static BoundingBox[] armRightTopBBox = {//
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
            case R.drawable.braco_direito_cima:
                return getArmRightTopBBox();
            case R.drawable.braco_direito_baixo:
                return getArmRightDownBBox();
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
}