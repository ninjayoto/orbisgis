/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.beanshellConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.beanshellConsole.actions.ActionsListener;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsoleAction;
import org.orbisgis.geoview.views.sqlConsole.ui.History;
import org.orbisgis.geoview.views.sqlConsole.ui.SQLConsoleButton;
import org.syntax.jedit.JEditTextArea;

import bsh.EvalError;
import bsh.Interpreter;

public class BSHConsolePanel extends JPanel {

	private JButton btExecute = null;
	private JButton btClear = null;
	private JButton btPrevious = null;
	private JButton btNext = null;
	private JButton btOpen = null;
	private JButton btSave = null;
	

	private ActionsListener actionAndKeyListener;

	private GeoView2D geoview;

	private JPanel centerPanel;

	private ScrollPane scrollPanelWest;
	private History history;
	

	/**
	 * This is the default constructor
	 * 
	 * @param geoview
	 */
	public BSHConsolePanel(GeoView2D geoview) {
		this.geoview = geoview;

		setLayout(new BorderLayout());
		add(getNorthPanel(), BorderLayout.NORTH);
		add(getCenterPanel(), BorderLayout.CENTER);
		setButtonsStatus();
	}

	// getters
	private JPanel getNorthPanel() {
		final JPanel northPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		northPanel.setLayout(flowLayout);
		northPanel.add(getBtExecute());
		northPanel.add(getBtClear());
		northPanel.add(getBtPrevious());
		northPanel.add(getBtNext());

		northPanel.add(getBtOpen());
		northPanel.add(getBtSave());
		
		
		setBtClear();
		setBtSave();
		

		return northPanel;
	}
	
		private JButton getBtClear() {
			if (null == btClear) {
				btClear = new BSHConsoleButton(ConsoleAction.CLEAR,
						getActionAndKeyListener());
			}
			return btClear;
		}
	
		private JButton getBtPrevious() {
			if (null == btPrevious) {
				btPrevious = new BSHConsoleButton(ConsoleAction.PREVIOUS,
						getActionAndKeyListener());
			}
			return btPrevious;
		}

		private JButton getBtNext() {
			if (null == btNext) {
				btNext = new BSHConsoleButton(ConsoleAction.NEXT,
						getActionAndKeyListener());
			}
			return btNext;
		}

		private JButton getBtOpen() {
			if (null == btOpen) {
				btOpen = new BSHConsoleButton(ConsoleAction.OPEN,
						getActionAndKeyListener());
			}
			return btOpen;
		}

		private JButton getBtSave() {
			if (null == btSave) {
				btSave = new BSHConsoleButton(ConsoleAction.SAVE,
						getActionAndKeyListener());
			}
			return btSave;
		}

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(getScrollPanelWest(), BorderLayout.CENTER);
		}
		return centerPanel;
	}

	private ScrollPane getScrollPanelWest() {
		if (scrollPanelWest == null) {
			scrollPanelWest = new ScrollPane(geoview);
		}
		return scrollPanelWest;
	}

	public JEditTextArea getJEditTextArea() {
		return getScrollPanelWest().getJEditTextArea();
	}

	public String getText() {
		return getJEditTextArea().getText();
	}

	public GeoView2D getGeoview() {
		return geoview;
	}

	public void setText(String text) {
		getScrollPanelWest().setText(text);
	}

	private JButton getBtExecute() {
		if (null == btExecute) {
			btExecute = new BSHConsoleButton(ConsoleAction.EXECUTE,
					getActionAndKeyListener());
		}
		return btExecute;
	}

	private ActionsListener getActionAndKeyListener() {
		if (null == actionAndKeyListener) {
			actionAndKeyListener = new ActionsListener(this);
		}
		return actionAndKeyListener;
	}

	public void execute() {
		getActionAndKeyListener().execute();
	}

	public Interpreter getInterpreter() {

		return getScrollPanelWest().getInterpreter();
	}

	public FileOutputStream getFileOutputStream() {

		return getScrollPanelWest().getFileOutputStream();

	}

	public void eval(String queryPanelContent) {
		try {
			getInterpreter().eval(queryPanelContent);
			getJEditTextArea().setText(getScrollPanelWest().getOut());
			getJEditTextArea().setForeground(Color.BLUE);
		} catch (EvalError e) {

			getJEditTextArea().setText(e.getErrorText());

		}

	}
	
	private void setBtExecute() {
		if (0 == getText().length()) {
			getBtExecute().setEnabled(false);
		} else {
			getBtExecute().setEnabled(true);
		}
	}

	private void setBtClear() {
		if (0 == getText().length()) {
			getBtClear().setEnabled(false);
		} else {
			getBtClear().setEnabled(true);
		}
	}

	private void setBtPrevious() {
		if (getHistory().isPreviousAvailable()) {
			getBtPrevious().setEnabled(true);
		} else {
			getBtPrevious().setEnabled(false);
		}
	}

	private void setBtNext() {
		if (getHistory().isNextAvailable()) {
			getBtNext().setEnabled(true);
		} else {
			getBtNext().setEnabled(false);
		}
	}
	
	public History getHistory() {
		if (null == history) {
			history = new History();
		}
		return history;
	}

	private void setBtOpen() {
		// btOpen.setEnabled(true);
	}

	private void setBtSave() {
		if (0 == getText().length()) {
			getBtSave().setEnabled(false);
		} else {
			getBtSave().setEnabled(true);
		}
	}
	
	public void setButtonsStatus() {
		//setBtExecute();
		setBtClear();
		setBtPrevious();
		setBtNext();
		setBtOpen();
		setBtSave();
	}

}