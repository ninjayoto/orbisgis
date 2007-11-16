package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class SLDFile extends FileResource {

	private final Icon icon = new ImageIcon(getClass().getResource(
			"sld_file.png"));

	public SLDFile(String name, String filePath) {
	}

	public Icon getIcon(INode node, boolean isExpanded) {
		return icon;
	}
}
