package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class RealLiteral extends Literal implements RealParameter{

    public RealLiteral(){
        v = 1.0;
    }

    public RealLiteral(double literal){
        v = literal;
    }

    public RealLiteral(String d){
        this.v = new Double(d);
    }

    public RealLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public double getValue(DataSource ds, long fid){
        return v;
    }

    public void setValue(double value){
        v = value;
    }


    @Override
    public boolean dependsOnFeature(){
        return false;
    }


    @Override
    public String toString(){
        Double v2 = v;
        return v2.toString();
    }


    public static final RealLiteral ZERO = new RealLiteral(0.0);

    private double v;
}