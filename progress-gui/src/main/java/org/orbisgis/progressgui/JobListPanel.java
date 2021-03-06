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
package org.orbisgis.progressgui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A panel that manage a list of subpanel.
 * 
 * This panel use the same model and renderer as a JList but it
 * store the item components. This class will respond to component repaint
 * but it is heavier than a JList
 */
public class JobListPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private ListModel listModel;
        private ListCellRenderer listRenderer;
        private BoxLayout cellsStack;
        private ModelListener modelListener = new ModelListener();
        private static final Logger LOGGER = LoggerFactory.getLogger(JobListPanel.class);
        
        public JobListPanel() {
                cellsStack = new BoxLayout(this, BoxLayout.Y_AXIS);
                setLayout(cellsStack);
        }

        /**
         * 
         * @return The JobListModel
         */
        public ListModel getModel() {
                return listModel;
        }

        @Override
        public void removeNotify() {
                super.removeNotify();
                setModel(null);               
        }

        
        
        /**
         * Set the model, contents change events will be ignored,
         * The component attach with the item must be updated
         * @param listModel 
         */
        public void setModel(ListModel listModel) {
                if(this.listModel!=null) {
                        this.listModel.removeListDataListener(modelListener);
                }
                this.listModel = listModel;
                if(listModel!=null) {
                        listModel.addListDataListener(modelListener);
                        //Read the content
                        for(int rowi=0;rowi<listModel.getSize();rowi++) {
                                onAddRow(rowi);
                        }
                        repaint();
                }
        }

        /**
         * The renderer
         * @return 
         */
        public ListCellRenderer getListRenderer() {
                return listRenderer;
        }

        /**
         * Sets the renderer, the component is required once for each component
         * @param listRenderer 
         */
        public void setRenderer(ListCellRenderer listRenderer) {
                this.listRenderer = listRenderer;
        }
        
        private void onAddRow(int index) {
                add(listRenderer.getListCellRendererComponent(null, listModel.getElementAt(index), index, true, true));
        }
        /**
         * Remove the Swing component
         * @param index 
         */
        private void onRemoveRow(int index) {
                try {
                        if(index<getComponentCount()) {
                                remove(index);
                        }
                } catch (ArrayIndexOutOfBoundsException ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                }
        }
        
        private class ModelListener implements ListDataListener {

                @Override
                public void intervalAdded(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onAddRow(index);
                        }
                        repaint();
                }

                @Override
                public void intervalRemoved(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onRemoveRow(index);
                        }
                        repaint();
                }

                @Override
                public void contentsChanged(ListDataEvent lde) {
                        //JPanel already listen for child updates
                }
        }
}
