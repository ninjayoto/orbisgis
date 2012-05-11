package org.orbisgis.view.table;

import org.gdms.data.DataSource;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.edition.EditableElement;

/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor
 * 
 */
public interface TableEditableElement extends EditableElement {

	/**
	 * Get the object that manages selection
	 * 
	 * @return
	 */
	Selection getSelection();

	/**
	 * Get the data to populate the table
	 * 
	 * @return
	 */
	DataSource getDataSource();

	/**
	 * Return true if the source can be edited
	 * 
	 * @return
	 */
	boolean isEditable();

	/**
	 * Return the MapContext containing the DataSource returned in
	 * {@link #getDataSource()}. Return null if it is not contained in any
	 * MapContext
	 * 
	 * @return
	 */
	MapContext getMapContext();
	
	
}
