package org.contrib.ui.editorViews.toc.actions.geometry.qa;

import org.contrib.algorithm.qa.InternalGapFinder;
import org.contrib.model.jump.adapter.FeatureCollectionAdapter;
import org.contrib.model.jump.adapter.FeatureCollectionDatasourceAdapter;
import org.contrib.model.jump.adapter.TaskMonitorAdapter;
import org.contrib.model.jump.model.FeatureCollection;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.progress.NullProgressMonitor;

public class IntervalGapsFinderAction implements ILayerAction {

	public boolean accepts(ILayer layer) {

		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);

		final DataSourceFactory dsf = dataManager.getDSF();

		InternalGapFinder internalGapFinder = new InternalGapFinder(

				new SpatialDataSourceDecorator(layer.getDataSource())
				, new NullProgressMonitor());

		try {
			ObjectMemoryDriver gapDriver =
				internalGapFinder.getObjectMemoryDriver();
			
				String gaplayer = dsf.getSourceManager().nameAndRegister(
					gapDriver);			
			
			final ILayer gapLayer = dataManager.createLayer(gaplayer);
			
			mapContext.getLayerModel().insertLayer(gapLayer, 0);

		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (LayerException e) {
			e.printStackTrace();
		}

	}
}