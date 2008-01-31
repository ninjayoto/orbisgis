package org.orbisgis.geoview.renderer;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.legend.LabelLegend;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueValueLegend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class NewRenderer {

	public static void main(String[] args) throws Exception {
		ILayer root = LayerFactory.createLayerCollection("root");
		ILayer layer = LayerFactory.createVectorialLayer(new File(
				"/home/gonzales/workspace/" + "datas2tests/shp/mediumshape2D"
						+ "/landcover2000.shp"));
		// ILayer layer = LayerFactory.createVectorialLayer(new File(
		// "/home/gonzales/workspace/" + "datas2tests/shp/mediumshape2D"
		// + "/hedgerow.shp"));
		// ILayer layer = LayerFactory.createVectorialLayer(new File(
		// "/home/gonzales/workspace/" + "datas2tests/shp/smallshape2D"
		// + "/multipoint2d.shp"));
		// ILayer layer = LayerFactory.createVectorialLayer(getDataSource());

		root.addLayer(layer);
		layer.open();
		VectorLayer vl = (VectorLayer) layer;
		vl.setLegend(getLegend());

		Envelope extent = layer.getEnvelope();
		Image img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
		Renderer r = new Renderer();
		int size = 350;
		 extent = new Envelope(new Coordinate(extent.centre().x - size, extent
				.centre().y
				- size), new Coordinate(extent.centre().x + size, extent
				.centre().y
				+ size));
		r.draw(img, extent, root);

		JFrame frm = new JFrame();
		frm.getContentPane().add(new JLabel(new ImageIcon(img)));
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);

		//
		// ProportionalLegend pl = LegendFactory.createProportionalLegend();
		// pl.setClassificationField("population");
		//
		// LabelLegend ll = LegendFactory.createLabelLegend();
		// ll.setClassificationField("nompays");
		// ll.setLabelSizeField("population");
		//
		// Legend l = LegendFactory.createLegendComposite(uvl, pl, ll);

	}

	private static DataSource getDataSource() throws DriverLoadException,
			DataSourceCreationException, DriverException, ParseException {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });
		WKTReader reader = new WKTReader();
		// omd.addValues(new Value[] { ValueFactory.createValue(reader
		// .read("MULTIPOINT(0 0, 1 0, 2 0)")) });
		// omd.addValues(new Value[] { ValueFactory.createValue(reader
		// .read("LINESTRING(0 -1, 1 -1, 2 -2)")) });
		omd.addValues(new Value[] { ValueFactory.createValue(reader
				.read("MULTIPOLYGON((" + "(1 1,5 1,5 5,1 5,1 1),"
						+ "(2 2, 3 2, 3 3, 2 3,2 2)),"
						+ "((8 8,8 10,10 10,8 8)))")) });
		// omd.addValues(new Value[] { ValueFactory.createValue(reader
		// .read("GEOMETRYCOLLECTION(MULTIPOINT(5 5, 6 6, 7 7)"
		// + ",LINESTRING(8 8, 9 9, 10 10, 11 2))")) });

		return OrbisgisCore.getDSF().getDataSource(omd);
	}

	private static Legend getLegend() throws DriverException {
		Legend legend;

		// UniqueSymbolLegend l = LegendFactory.createUniqueSymbolLegend();
		// Symbol polSym = SymbolFactory.createPolygonSymbol(Color.black,
		// Color.red);
		// Symbol pointSym = SymbolFactory.createCirclePointSymbol(Color.black,
		// Color.red, 10);
		// Symbol lineSym = SymbolFactory.createLineSymbol(Color.blue,
		// new BasicStroke(4));
		// Symbol composite = SymbolFactory.createSymbolComposite(polSym,
		// pointSym, lineSym);
		// l.setSymbol(polSym);
		// l.setSymbol(pointSym);
		// l.setSymbol(lineSym);
		// l.setSymbol(composite);
		// legend = l;

		// UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
		// Symbol grass = SymbolFactory.createPolygonSymbol(Color.black,
		// Color.green);
		// Symbol cereals = SymbolFactory.createPolygonSymbol(Color.black,
		// Color.yellow);
		// Symbol lesoutres = SymbolFactory.createPolygonSymbol(Color.black);
		// uvl.addClassification(ValueFactory.createValue("grassland"), grass);
		// uvl.addClassification(ValueFactory.createValue("cereals"), cereals);
		// uvl.setClassificationField("type");
		// uvl.setDefaultSymbol(lesoutres);
		// legend = uvl;

		// IntervalLegend uvl = LegendFactory.createIntervalLegend();
		// Symbol low = SymbolFactory.createPolygonSymbol(Color.black, new
		// Color(
		// 64, 64, 64));
		// Symbol middle = SymbolFactory.createPolygonSymbol(Color.black,
		// new Color(128, 128, 128));
		// Symbol high = SymbolFactory.createPolygonSymbol(Color.black, new
		// Color(
		// 192, 192, 192));
		// Symbol lesoutres = SymbolFactory.createPolygonSymbol(Color.black);
		// uvl.addInterval(ValueFactory.createValue(0), true, ValueFactory
		// .createValue(0.1), false, low);
		// uvl.addInterval(ValueFactory.createValue(0.1), true, ValueFactory
		// .createValue(0.2), true, middle);
		// uvl.addIntervalWithMinLimit(ValueFactory.createValue(0.2), false,
		// high);
		// uvl.setClassificationField("runoff_sum");
		// uvl.setDefaultSymbol(lesoutres);
		// legend = uvl;

		UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
		Symbol grass = SymbolFactory.createPolygonSymbol(Color.black,
				Color.green);
		Symbol cereals = SymbolFactory.createPolygonSymbol(Color.black,
				Color.yellow);
		Symbol lesoutres = SymbolFactory.createPolygonSymbol(Color.black);
		uvl.addClassification(ValueFactory.createValue("grassland"), grass);
		uvl.addClassification(ValueFactory.createValue("cereals"), cereals);
		uvl.setClassificationField("type");
		uvl.setDefaultSymbol(lesoutres);
		LabelLegend ll = LegendFactory.createLabelLegend();
		ll.setClassificationField("type");
		legend = LegendFactory.createLegendComposite(uvl, ll);

		return legend;
	}
}
