package org.orbisgis.core.ui.editors.table.actions;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;
import org.orbisgis.core.ui.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;

public class SelectAll implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		try {
			return element.getDataSource().getRowCount() != element
					.getSelection().getSelectedRows().length;
		} catch (DriverException e) {
			return false;
		}
	}

	@Override
	public void execute(TableEditor editor, TableEditableElement element,
			int rowIndex, int columnIndex) {
		try {
			element.getSelection().selectInterval(0,
					(int) element.getDataSource().getRowCount() - 1);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get the number of rows", e);
		}
	}

}