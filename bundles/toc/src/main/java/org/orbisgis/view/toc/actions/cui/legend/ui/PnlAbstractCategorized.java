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
package org.orbisgis.view.toc.actions.cui.legend.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.components.ColorConfigurationPanel;
import org.orbisgis.view.toc.actions.cui.legend.components.ColorScheme;
import org.orbisgis.view.toc.actions.cui.legend.model.TableModelInterval;
import org.orbisgis.view.toc.actions.cui.legend.panels.AbsPanel;
import org.orbisgis.view.toc.actions.cui.legend.stats.Thresholds;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;

import static org.orbisgis.coremap.renderer.se.parameter.Categorize.CategorizeMethod;

/**
 * Root class for Interval Classifications.
 *
 * @author Alexis Guéganno
 */
public abstract class PnlAbstractCategorized<U extends LineParameters> extends PnlAbstractTableAnalysis<Double,U> {
    public static final Logger LOGGER = LoggerFactory.getLogger(PnlAbstractCategorized.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlAbstractCategorized.class);
    private ColorConfigurationPanel colorConfig;
    private Thresholds thresholds;
    public static final Integer[] THRESHOLDS_NUMBER =
            new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    public static final Integer[] THRESHOLDS_SQUARE =
            new Integer[]{2, 4, 8, 16};
    private WideComboBox<Integer> numberCombo;
    private JButton createCl;
    private WideComboBox methodCombo;
    private DefaultComboBoxModel comboModel;

    /**
     * Contructor
     *
     * @param lc     LegendContext
     * @param legend Legend
     */
    public PnlAbstractCategorized(LegendContext lc,
                                  AbstractCategorizedLegend<U> legend) {
        super(lc, legend);
    }

    @Override
    public int getPreviewColumn(){
        return TableModelInterval.PREVIEW_COLUMN;
    }

    @Override
    public int getKeyColumn(){
        return TableModelInterval.KEY_COLUMN;
    }

    @Override
    public Class getPreviewClass() {
        return Double.class;
    }

    @Override
    public String getTitleBorder(){
        return I18N.tr("Interval classification");
    }

    private Thresholds computeStats(String fieldName){
        DescriptiveStatistics stats = new DescriptiveStatistics();
        try(Connection connection = getDataSource().getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select "+ TableLocation.quoteIdentifier(fieldName)+ " from "+getTable()+ " where "+ TableLocation.quoteIdentifier(fieldName) + " is not null" )) {
            while(rs.next()) {
                stats.addValue(rs.getDouble(1));
            }
        } catch (SQLException e) {
            LOGGER.warn(I18N.tr("The application has ended unexpectedly"),e);
        }
        return new Thresholds(stats,fieldName);
    }

    /**
     * Retrieve the panel that gathers all the components needed to create the classification.
     * @return The panel gathering the graphic elements that can be used to create the classification.
     */
    public JPanel getCreateClassificationPanel(){
        if(numberCombo == null){
            numberCombo = new WideComboBox<>(getThresholdsNumber());
        }
        comboModel = (DefaultComboBoxModel) numberCombo.getModel();
        createCl = new JButton(I18N.tr("Create"));
        createCl.setActionCommand("click");
        createCl.addActionListener(
                EventHandler.create(ActionListener.class, this, "onComputeClassification"));
        createCl.setEnabled(false);

        JPanel inner = new JPanel(
                new MigLayout("wrap 2", "[align r][align l]"));

        inner.add(new JLabel(I18N.tr("Method")));
        inner.add(getMethodCombo(), "width ::130");
        inner.add(new JLabel(I18N.tr("Classes")));
        inner.add(numberCombo, "split 2");
        inner.add(createCl, "gapleft push");

        JPanel outside = new JPanel(new MigLayout("wrap 1", "[" + AbsPanel.FIXED_WIDTH + ", align c]"));
        outside.setBorder(BorderFactory.createTitledBorder(
                I18N.tr(CLASSIFICATION_SETTINGS)));
        if(colorConfig == null){
            ArrayList<String> names = new ArrayList<String>(ColorScheme.rangeColorSchemeNames());
            names.addAll(ColorScheme.discreteColorSchemeNames());
            colorConfig = new ColorConfigurationPanel(names);
        }
        outside.add(new JLabel(I18N.tr("Color scheme:")), "align l");
        outside.add(colorConfig, "growx");
        outside.add(inner, "growx");
        return outside;
    }

    /**
     * Change what is displayed by the combo box.
     */
    private void changeModelContent(){
        Integer selItem = (Integer)numberCombo.getSelectedItem();
        Integer[] vals = getThresholdsNumber();
        int index = Arrays.binarySearch(vals,selItem);
        int real = index < 0 ? -index-1 : index;
        comboModel.removeAllElements();
        for(int i=0;i<vals.length;i++){
            comboModel.addElement(vals[i]);
        }
        numberCombo.setSelectedIndex(real);
        numberCombo.invalidate();
    }

    /**
     * Gets the JComboBox used to select the classification method.
     * @return The JComboBox.
     */
    public JComboBox getMethodCombo(){
        if(methodCombo == null){
            ContainerItemProperties[] categorizeMethods = getCategorizeMethods();
            methodCombo = new WideComboBox(categorizeMethods);
            methodCombo.addActionListener(
                    EventHandler.create(ActionListener.class, this, "methodChanged"));
            methodCombo.setSelectedItem(CategorizeMethod.MANUAL.toString());
        }
        methodChanged();
        return methodCombo;
    }

    /**
     * Gets the supported number of thresholds for the currently selected classification.
     * @return The number of thresholds.
     */
    private Integer[] getThresholdsNumber(){
        if(methodCombo == null){
            return THRESHOLDS_SQUARE;
        } else {
            ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
            CategorizeMethod cm = CategorizeMethod.valueOf(selectedItem.getKey());
            switch(cm){
                case BOXED_MEANS: return THRESHOLDS_SQUARE;
                default : return THRESHOLDS_NUMBER;
            }
        }
    }

    /**
     * The selected classification has changed. Called by EventHandler.
     */
    public void methodChanged(){
        ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
        changeModelContent();
        boolean b = CategorizeMethod.valueOf(selectedItem.getKey()).equals(CategorizeMethod.MANUAL);
        if(createCl != null){
            createCl.setEnabled(!b);
        }
    }

    /**
     * This method is called by EventHandler when clicking on the button dedicated to classification creation.
     */
    public void onComputeClassification(){
        String name = getFieldName();
        if(thresholds == null || !thresholds.getFieldName().equals(name)){
            thresholds = computeStats(name);
        }
        ContainerItemProperties selectedItem = (ContainerItemProperties) methodCombo.getSelectedItem();
        CategorizeMethod cm = CategorizeMethod.valueOf(selectedItem.getKey());
        Integer number = (Integer) numberCombo.getSelectedItem();
        SortedSet<Double> set = thresholds.getThresholds(cm,number);
        if(!set.isEmpty()){
            ColorScheme sc = colorConfig.getColorScheme();
            MappedLegend<Double,U> cl = createColouredClassification(
                    set,
                    new NullProgressMonitor(),
                    sc);
            cl.setLookupFieldName(((MappedLegend)getLegend()).getLookupFieldName());
            cl.setName(getLegend().getName());
            setLegend(cl);
        }
    }

    /**
     * Gets the value contained in the {@code Methods} enum with their
     * internationalized representation in a {@code
     * ContainerItemProperties} array.
     * @return {@link Categorize.CategorizeMethod} in an array of containers.
     */
    public ContainerItemProperties[] getCategorizeMethods(){
        CategorizeMethod[] us = CategorizeMethod.values();
        ArrayList<ContainerItemProperties> temp = new ArrayList<ContainerItemProperties>();
        for (CategorizeMethod u : us) {
            if (isSupported(u)) {
                ContainerItemProperties cip = new ContainerItemProperties(u.name(), u.toLocalizedString());
                temp.add(cip);
            }
        }
        return temp.toArray(new ContainerItemProperties[temp.size()]);
    }

    /**
     * Return if the given method is supported
     * @param cm The tested method
     * @return true if cm is supported
     */
    private boolean isSupported(CategorizeMethod cm){
        switch(cm){
            case EQUAL_INTERVAL : return true;
            case MANUAL : return true;
            case STANDARD_DEVIATION: return true;
            case QUANTILES: return true;
            case BOXED_MEANS: return true;
            default : return false;
        }
    }
}
