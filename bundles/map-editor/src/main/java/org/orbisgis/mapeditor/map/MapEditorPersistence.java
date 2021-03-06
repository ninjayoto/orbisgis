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
package org.orbisgis.mapeditor.map;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.mapeditor.map.mapsManager.MapsManagerPersistenceImpl;
import org.orbisgis.sif.docking.PropertyHost;
import org.orbisgis.sif.docking.XElement;

/**
 * The map editor stores the last open default map context
 * @author Nicolas Fortin
 */
public class MapEditorPersistence implements DockingPanelLayout, Serializable, PropertyHost {
        private static final long serialVersionUID = 3L; // One by new property
        private MapsManagerPersistenceImpl mapsManagerPersistence = new MapsManagerPersistenceImpl();
        private static final String MAP_EDITOR_NODE = "mapEditor";
        /**
         * Map Context file name to show on application start.
         * {@link MapEditorPersistence#getDefaultMapContext()}
         */
        public static final String PROP_DEFAULTMAPCONTEXT = "defaultMapContext";
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        private String defaultMapContext = "map.ows"; //Default map context on application start

        /**
         * Update the default map context
         * @param defaultMapContext 
         */
        final public void setDefaultMapContext(String defaultMapContext) {
                String oldDefaultMapContext = this.defaultMapContext;
                this.defaultMapContext = defaultMapContext;
                propertyChangeSupport.firePropertyChange(PROP_DEFAULTMAPCONTEXT,oldDefaultMapContext,defaultMapContext);
        }

        /**
         * @return Serialisation class related to the maps manager
         */
        public MapsManagerPersistenceImpl getMapsManagerPersistence() {
            return mapsManagerPersistence;
        }

        /**
         * 
         * @return The last loaded map context path, or the default one
         */
        public String getDefaultMapContext() {
                return defaultMapContext;
        }
        
        @Override
        public void writeStream(DataOutputStream out) throws IOException {
                out.writeLong(serialVersionUID);
                out.writeUTF(defaultMapContext);
                mapsManagerPersistence.writeStream(out);
        }

        @Override
        public void readStream(DataInputStream in) throws IOException {
                // Check version
                long version = in.readLong();
                if(version==serialVersionUID) {
                        setDefaultMapContext(in.readUTF());
                        mapsManagerPersistence.readStream(in);
                }
        }

        @Override
        public void writeXML(XElement element) {
                element.addLong("serialVersionUID", serialVersionUID);
                XElement mapElement = element.addElement(MAP_EDITOR_NODE);
                mapElement.addString(PROP_DEFAULTMAPCONTEXT, defaultMapContext);
                mapsManagerPersistence.writeXML(mapElement);
        }

        @Override
        public void readXML(XElement element) {
                long version = element.getLong("serialVersionUID");
                if(version==serialVersionUID) {
                        XElement mapElement =  element.getElement(MAP_EDITOR_NODE);
                        setDefaultMapContext(mapElement.getString(PROP_DEFAULTMAPCONTEXT));
                        mapsManagerPersistence.readXML(mapElement);
                }
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.addPropertyChangeListener(listener);
        }

        @Override
        public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertyChangeSupport.addPropertyChangeListener(prop,listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(prop,listener);
        }
}
