/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author eroot
 */
public class KeyValueTrio<Q,V,P> {
   private Q q;
   private V v;
   private P p;

    public KeyValueTrio(Q q, V v, P p) {
        this.q = q;
        this.v = v;
        this.p = p;
    }

   

    public KeyValueTrio() {
    }

    public Q getKey() {
        return q;
    }

    public void setKey(Q q) {
        this.q = q;
    }

    public V getValue() {
        return v;
    }

    public void setValue(V v) {
        this.v = v;
    }

    public P getParam() {
        return p;
    }

    public void setParam(P p) {
        this.p = p;
    }
   
}
