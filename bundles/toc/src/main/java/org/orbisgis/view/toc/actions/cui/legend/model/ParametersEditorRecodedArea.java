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
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.recode.RecodedArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legend.ui.PnlUniqueAreaSE;

import java.awt.event.ActionEvent;

/**
 * @author alexis
 */
public class ParametersEditorRecodedArea extends ParametersEditorMappedLegend<String, AreaParameters> {

    /**
     * Editors for a LineParameters stored in a JTable. We'll open a dedicated dialog
     */
    public ParametersEditorRecodedArea(){
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            RecodedArea rl = (RecodedArea) getMappedLegend();
            String key = (String) getCellEditorValue();
            AreaParameters lp = rl.get(key);
            UniqueSymbolArea usl = new UniqueSymbolArea(lp);
            if(!rl.isStrokeEnabled()){
                usl.setPenStroke(new NullPenStrokeLegend());
            }
            PnlUniqueAreaSE pls = new PnlUniqueAreaSE(usl, rl.isStrokeEnabled());
            if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
                AreaParameters edited = usl.getAreaParameters();
                rl.put((String)getCellEditorValue(), edited);
                fireEditingStopped();
            }
            fireEditingCanceled();
        }
    }
}

