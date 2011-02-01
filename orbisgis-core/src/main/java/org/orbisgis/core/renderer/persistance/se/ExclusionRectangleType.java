//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.03 at 12:56:18 PM CET 
//


package org.orbisgis.core.renderer.persistance.se;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExclusionRectangleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExclusionRectangleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}ExclusionZoneType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}UnitOfMeasure" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}X"/>
 *         &lt;element ref="{http://www.opengis.net/se}Y"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExclusionRectangleType", propOrder = {
    "unitOfMeasure",
    "x",
    "y"
})
public class ExclusionRectangleType
    extends ExclusionZoneType
{

    @XmlElement(name = "UnitOfMeasure")
    @XmlSchemaType(name = "anyURI")
    protected String unitOfMeasure;
    @XmlElement(name = "X", required = true)
    protected ParameterValueType x;
    @XmlElement(name = "Y", required = true)
    protected ParameterValueType y;

    /**
     * Gets the value of the unitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the value of the unitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitOfMeasure(String value) {
        this.unitOfMeasure = value;
    }

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setX(ParameterValueType value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setY(ParameterValueType value) {
        this.y = value;
    }

}