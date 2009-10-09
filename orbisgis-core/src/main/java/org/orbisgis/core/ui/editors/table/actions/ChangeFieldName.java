package org.orbisgis.core.ui.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editors.table.FieldNameChooser;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.sif.UIFactory;

public class ChangeFieldName implements ITableColumnAction {

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {
		return (selectedColumn != -1) && element.getDataSource().isEditable();
	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		try {
			DataSource dataSource = element.getDataSource();
			FieldNameChooser av = new FieldNameChooser(dataSource
					.getFieldNames(), "New field name", "strlength(txt) > 0",
					"Empty name not allowed", dataSource.getMetadata()
							.getFieldName(selectedColumnIndex));
			if (UIFactory.showDialog(av)) {
				dataSource.setFieldName(selectedColumnIndex, av.getValue());
			}
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot change field name", e);
		}
	}

}