package br.furb.corpusmapping.util;

/**
 * Created by Janaina on 24/08/2015.
 */
public class BoundingBox {

    public int id;
    public float minX;
    public float minY;
    public float maxX;
    public float maxY;

    public BoundingBox(int id, float minX, float minY, float maxX, float maxY) {
        this(minX, minY, maxX, maxY);
        this.id = id;
    }

    public BoundingBox(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean isInner(float x, float y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
