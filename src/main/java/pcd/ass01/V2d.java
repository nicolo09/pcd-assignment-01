/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package pcd.ass01;

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
public record V2d(double x,double y) {

    public V2d sum(V2d v){
        return new V2d(x+v.x,y+v.y);
    }

    public double abs(){
        return (double)Math.sqrt(x*x+y*y);
    }

    public V2d getNormalized(){
        double module=(double)Math.sqrt(x*x+y*y);
        return new V2d(x/module,y/module);
    }

    public V2d mul(double fact){
        return new V2d(x*fact,y*fact);
    }

    public String toString(){
        return "V2d("+x+","+y+")";
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof V2d){
            V2d v = (V2d)o;
            return x == v.x && y == v.y;
        }
        return false;
    }
    
}
