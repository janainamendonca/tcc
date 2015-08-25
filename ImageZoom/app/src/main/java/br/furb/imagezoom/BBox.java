package br.furb.imagezoom;

/**
 * Created by Janaina on 24/08/2015.
 */
public class BBox {

    public float minX;
    public float minY;
    public float maxX;
    public float maxY;

    public BBox(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean isInner(float x, float y){
       return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
