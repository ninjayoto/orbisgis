/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.legend.thematic.categorize;

import org.junit.Test;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.graphic.MarkGraphic;
import org.orbisgis.coremap.renderer.se.graphic.ViewBox;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.coremap.renderer.se.parameter.Literal;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * @author Alexis Guéganno
 */
public class CategorizedPointTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        CategorizedPoint cp = new CategorizedPoint(ps);
        assertTrue(cp.getSymbolizer().equals(ps));
    }

    @Test
    public void testBadInstanciation() throws Exception {
        Style s = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        try{
            CategorizedPoint cp = new CategorizedPoint(ps);
            fail();
        }catch (IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGetFallback() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters ap = new PointParameters(Color.decode("#111111"),.2,2.0,"1 1",Color.decode("#111111"),.5,5.0,5.0,"CIRCLE");
        assertTrue(ca.getFallbackParameters().equals(ap));
    }

    @Test
    public void testSetFallback() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters ap1 = new PointParameters(Color.decode("#211111"),.4,22.0,"21 1",Color.decode("#211111"),.1254,25.0,25.0,"CIRCLE");
        PointParameters ap2 = new PointParameters(Color.decode("#211111"),.4,22.0,"21 1",Color.decode("#211111"),.1254,25.0,25.0,"CIRCLE");
        ca.setFallbackParameters(ap1);
        assertTrue(ca.getFallbackParameters().equals(ap2));
    }

    @Test
    public void testGet() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        tester = new PointParameters(Color.decode("#ffaa00"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(110000.0).equals(tester));
    }

    @Test
    public void testRemove() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        PointParameters rm = ca.remove(70000.0);
        assertTrue(tester.equals(rm));
        tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        assertFalse(ca.containsKey(75000.0));
    }

    @Test
    public void testRemoveInf() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters rm = ca.remove(Double.NEGATIVE_INFINITY);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(tester.equals(rm));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        assertFalse(ca.containsKey(70000.0));
        assertTrue(ca.containsKey(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testPutExisting() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        PointParameters testRm = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        PointParameters rm = ca.put(70000.0, new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X"));
        assertTrue(rm.equals(testRm));
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutNotExisting() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        ca.put(76000.0, new PointParameters(Color.decode("#ababab"), 1.2, 12.0, "11 1", Color.decode("#bcbcbc"), 1.5, 17.0, 15.0, "X"));
        assertTrue(ca.get(76000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutInf() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        ca.put(Double.NEGATIVE_INFINITY, new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X"));
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
    }

    @Test
    public void testInstanciationNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer as = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        CategorizedPoint ca = new CategorizedPoint(as);
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testGetNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testPutNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        ca.put(12.0,new PointParameters(Color.BLACK, 1.0 ,1.0, "1", Color.decode("#252525"),2.0, 17.0,15.0,"X"));
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#252525"),2.0, 17.0,15.0,"X");
        assertTrue(ca.get(12.0).equals(tester));
    }

    @Test
    public void testRemoveNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        PointParameters rm = ca.remove(100000.0);
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"", Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(rm.equals(tester));
    }

    @Test
    public void testDisableStroke() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        ca.setStrokeEnabled(false);
        PointSymbolizer as = (PointSymbolizer) ca.getSymbolizer();
        MarkGraphic pg = (MarkGraphic) as.getGraphicCollection().getGraphic(0);
        assertNull(pg.getStroke());
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testEnableStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        ca.setStrokeEnabled(true);
        assertTrue(ca.isStrokeEnabled());
        PenStroke ps = new PenStroke();
        PointParameters ap = new PointParameters(
                ((SolidFill)ps.getFill()).getColor().getColor(null),
                ((SolidFill)ps.getFill()).getOpacity().getValue(null),
                ps.getWidth().getValue(null),
                ps.getDashArray().getValue(null),
                Color.decode("#113355"),
                .5,
                6.0,
                5.0,
                "SQUARE");
        assertTrue(ap.equals(ca.get(Double.NEGATIVE_INFINITY)));
    }

    @Test
    public void testNullViewBox() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.setViewBox(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,
                MarkGraphic.DEFAULT_SIZE,MarkGraphic.DEFAULT_SIZE,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testViewBoxNullWidth() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.getViewBox().setWidth(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,5.0,5.0,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testViewBoxNullHeight() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.getViewBox().setHeight(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,6.0,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testParamsToCat() throws Exception {
        PointSymbolizer ps = new PointSymbolizer();
        CategorizedPoint cp = new CategorizedPoint(ps);
        cp.put(25.0, new PointParameters(Color.decode("#223344"), .2, 22.0, "1 1", Color.decode("#113355"), .5, 5.0, 5.0, "SQUARE"));
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        PenStroke str = (PenStroke) mg.getStroke();
        assertTrue(str.getWidth() instanceof Categorize);
        assertTrue(str.getDashArray() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Categorize);
        assertTrue(((SolidFill)mg.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)mg.getFill()).getOpacity() instanceof Categorize);
        assertTrue(mg.getViewBox().getWidth() instanceof Categorize);
        assertTrue(mg.getViewBox().getHeight() instanceof Categorize);
        assertTrue(mg.getWkn() instanceof Categorize);
    }

    @Test
    public void testParamsToCatAfterStrokeEnabling() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer ps = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        CategorizedPoint cp = new CategorizedPoint(ps);
        assertFalse(cp.isStrokeEnabled());
        cp.setStrokeEnabled(true);
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        PenStroke str = (PenStroke) mg.getStroke();
        assertTrue(str.getWidth() instanceof Literal);
        assertTrue(str.getDashArray() instanceof Literal);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Literal);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Literal);
        cp.put(25.0, new PointParameters(Color.decode("#223344"), .2, 22.0, "1 1", Color.decode("#113355"), .5, 5.0, 5.0, "SQUARE"));
        assertTrue(str.getWidth() instanceof Categorize);
        assertTrue(str.getDashArray() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Categorize);
    }

    private PointSymbolizer getPointSymbolizer() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT);
        return (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private CategorizedPoint getCategorizedPoint() throws Exception {
        return new CategorizedPoint(getPointSymbolizer());
    }

    private CategorizedPoint getNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer as = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return new CategorizedPoint(as);
    }
}
