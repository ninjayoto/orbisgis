/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.view;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.h2spatialapi.EmptyProgressVisitor;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ui.ToolBoxPanel;
import org.orbisgis.orbistoolbox.view.ui.dataui.DataUIManager;
import org.orbisgis.orbistoolbox.view.utils.*;
import org.orbisgis.orbistoolbox.view.utils.dataProcessing.DataProcessingManager;
import org.orbisgis.orbistoolboxapi.annotations.model.FieldType;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.*;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * @author Sylvain PALOMINOS
 **/

@Component
public class ToolBox implements DockingPanel, EditorFactory {

    public static final String FACTORY_ID = "WPSProcessEditorFactory";
    private static final String GROOVY_EXTENSION = "groovy";

    /** Docking parameters used by DockingFrames */
    private DockingPanelParameters parameters;
    /** Process manager */
    private ProcessManager processManager;
    /** Displayed JPanel */
    private ToolBoxPanel toolBoxPanel;
    /** Object creating the UI corresponding to the data */
    private DataUIManager dataUIManager;
    /** DataManager */
    private static DataManager dataManager;
    private static DriverFunctionContainer driverFunctionContainer;

    private Map<String, Object> properties;
    private EditorManager editorManager;
    private DataProcessingManager dataProcessingManager;
    private CoreWorkspace coreWorkspace;

    private Map<String, ProcessEditableElement> mapIdPee;

    private static final String TOOLBOX_REFERENCE = "orbistoolbox";
    private static final String WPS_SCRIPT_FOLDER = "scripts";
    private static boolean areScriptsCopied = false;

    @Activate
    public void init(){
        toolBoxPanel = new ToolBoxPanel(this);
        processManager = new ProcessManager();
        dataUIManager = new DataUIManager(this);

        ActionCommands dockingActions = new ActionCommands();

        parameters = new DockingPanelParameters();
        parameters.setTitle("OrbisToolBox");
        parameters.setTitleIcon(ToolBoxIcon.getIcon("orbistoolbox"));
        parameters.setCloseable(true);
        parameters.setName(TOOLBOX_REFERENCE);

        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));

        dataProcessingManager = new DataProcessingManager(this);
        mapIdPee = new HashMap<>();

        if(!areScriptsCopied) {
            setScriptFolder();
        }
    }

    /**
     * Sets all the default OrbisGIS WPS script into the script folder of the .OrbisGIS folder.
     */
    private void setScriptFolder(){
        //Sets the WPS script folder
        File wpsScriptFolder = new File(coreWorkspace.getApplicationFolder(), WPS_SCRIPT_FOLDER);
        if(!wpsScriptFolder.exists()){
            if(!wpsScriptFolder.mkdir()){
                LoggerFactory.getLogger(ToolBox.class).warn("Unable to find or create a script folder.\n" +
                        "No basic script will be available.");
            }
        }
        if(wpsScriptFolder.exists() && wpsScriptFolder.isDirectory()){
            try {
                //Retrieve all the scripts url
                String folderPath = ToolBox.class.getResource("scripts").getFile();
                Enumeration<URL> enumUrl = FrameworkUtil.getBundle(ToolBox.class).findEntries(folderPath, "*", false);
                //For each url
                while(enumUrl.hasMoreElements()){
                    URL scriptUrl = enumUrl.nextElement();
                    String scriptPath = scriptUrl.getFile();
                    //Test if it's a groovy file
                    if(scriptPath.endsWith("."+GROOVY_EXTENSION)){
                        //If the script is already in the .OrbisGIS folder, remove it.
                        for(File existingFile : wpsScriptFolder.listFiles()){
                            if(existingFile.getName().endsWith(scriptPath) && existingFile.delete()){
                                LoggerFactory.getLogger(ToolBox.class).
                                        warn("Replacing script "+existingFile.getName()+" by the default one");
                            }
                        }
                        //Copy the script into the .OrbisGIS folder.
                        OutputStream out = new FileOutputStream(
                                new File(wpsScriptFolder.getAbsolutePath(),
                                        new File(scriptPath).getName()));
                        InputStream in = scriptUrl.openStream();
                        IOUtils.copy(in, out);
                        out.close();
                        in.close();
                    }
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(ToolBox.class).warn("Unable to copy the scripts. \n" +
                        "No basic script will be available. \n" +
                        "Error : "+e.getMessage());
            }
        }
        areScriptsCopied = true;
        addLocalSource(wpsScriptFolder);
    }

    @Deactivate
    public void dispose(){
        editorManager.removeEditorFactory(this);
        toolBoxPanel.dispose();
        areScriptsCopied = false;
    }

    public String getReference(){
        return TOOLBOX_REFERENCE;
    }

    /**
     * Returns the process manager.
     * @return The process manager.
     */
    public ProcessManager getProcessManager(){
        return processManager;
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return parameters;
    }

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
    }

    /**
     * Adds a local folder as a script source.
     */
    public void addNewLocalSource(){
        OpenFolderPanel openFolderPanel = new OpenFolderPanel("ToolBox.AddSource", "Add a source");
        //Wait the window answer and if the user validate set and run the export thread.
        if(UIFactory.showDialog(openFolderPanel)){
            addLocalSource(openFolderPanel.getSelectedFile());
        }
    }

    public void addLocalSource(File file){
        processManager.addLocalSource(file.getAbsolutePath());
        toolBoxPanel.addLocalSource(file, processManager);
    }

    /**
     * Open the process window for the selected process.
     * @return The process instance ID.
     */
    public ProcessEditableElement openProcess(File filePath, int index){
        Process process = processManager.getProcess(filePath);
        ProcessEditableElement pee = new ProcessEditableElement(
                process, process.getTitle().replace(" ", "_") + "_" + index);
        editorManager.openEditable(pee);
        mapIdPee.put(pee.getId(), pee);
        return pee;
    }

    /**
     * Open the process instance.
     */
    public void openInstance(String instanceID){
        editorManager.openEditable(mapIdPee.get(instanceID));
    }

    public void closeInstance(String instanceID){
        for(Editor editor : editorManager.getEditors()){
            if(editor instanceof ProcessEditor && editor.getEditableElement().getId().equals(instanceID)){
                ((ProcessEditor) editor).removeAll();
            }
        }
        mapIdPee.remove(instanceID);
    }

    /**
     * Verify if the given file is a well formed script.
     * @param f File to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkProcess(File f){
        Process process = processManager.getProcess(f);
        if(process != null){
            processManager.removeProcess(process);
        }
        return (processManager.addLocalScript(f) != null);
    }

    /**
     * Verify if the given file is a well formed script.
     * @param f File to check.
     * @return True if the file is well formed, false otherwise.
     */
    public boolean checkFolder(File f){
        if(f.exists() && f.isDirectory()){
            for(File file : f.listFiles()){
                if(file.getAbsolutePath().endsWith("."+GROOVY_EXTENSION)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove the selected process in the tree.
     */
    public void removeProcess(File file){
        processManager.removeProcess(processManager.getProcess(file));
    }

    /**
     * Returns the DataUIManager.
     * @return The DataUIManager.
     */
    public DataUIManager getDataUIManager(){
        return dataUIManager;
    }

    public Map<String, Object> getProperties(){
        return properties;
    }

    public ToolBox(){
        properties = new HashMap<>();
    }

    @Reference
    public void setDataSource(javax.sql.DataSource ds) {
        properties.put("ds", ds);
    }

    public void unsetDataSource(javax.sql.DataSource ds) {
        properties.remove("ds");
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }

    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        ToolBox.dataManager = dataManager;
    }

    public void unsetDataManager(DataManager dataManager) {
        ToolBox.dataManager = null;
    }

    public DataManager getDataManager(){
        return dataManager;
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }

    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

    public DriverFunctionContainer getDriverFunctionContainer(){
        return driverFunctionContainer;
    }

    /**
     * @param editorManager Editor windows manager
     */
    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }
    /**
     * @param editorManager Editor windows manager
     */
    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    public DataProcessingManager getDataProcessingManager() {
        return dataProcessingManager;
    }

    public ToolBoxPanel getToolBoxPanel(){
        return toolBoxPanel;
    }

    /**
     * Returns a map of the importable format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the importable  format.
     */
    public static Map<String, String> getImportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        for(DriverFunction df : driverFunctionContainer.getDriverFunctionList()){
            for(String ext : df.getImportFormats()){
                if(df.isSpatialFormat(ext) || !onlySpatial) {
                    formatMap.put(ext, df.getFormatDescription(ext));
                }
            }
        }
        return formatMap;
    }

    /**
     * Returns a map of the exportable spatial format.
     * The map key is the format extension and the value is the format description.
     * @param onlySpatial If true, returns only the spatial table.
     * @return a map of the exportable spatial format.
     */
    public static Map<String, String> getExportableFormat(boolean onlySpatial){
        Map<String, String> formatMap = new HashMap<>();
        for(DriverFunction df : driverFunctionContainer.getDriverFunctionList()){
            for(String ext : df.getExportFormats()){
                if(df.isSpatialFormat(ext) || !onlySpatial) {
                    formatMap.put(ext, df.getFormatDescription(ext));
                }
            }
        }
        return formatMap;
    }

    /**
     * Returns the list of sql table from OrbisGIS.
     * @param onlySpatial If true, returns only the spatial table.
     * @return The list of geo sql table from OrbisGIS.
     */
    public static List<String> getGeocatalogTableList(boolean onlySpatial) {
        List<String> list = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            String defaultSchema = "PUBLIC";
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
            if(!onlySpatial) {
                DatabaseMetaData md = connection.getMetaData();
                ResultSet rs = md.getTables(null, defaultSchema, "%", null);
                while (rs.next()) {
                    String tableName = rs.getString(3);
                    if (!tableName.equalsIgnoreCase("SPATIAL_REF_SYS") && !tableName.equalsIgnoreCase("GEOMETRY_COLUMNS")) {
                        list.add(tableName);
                    }
                }
            }
            else{
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM "+defaultSchema+".geometry_columns");
                while(rs.next()) {
                    list.add(rs.getString("F_TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return list;
    }

    /**
     * Return the list of the field of a table.
     * @param tableName Name of the table.
     * @param fieldTypes Type of the field accepted. If empty, accepts all the field.
     * @return The list of the field name.
     */
    public static List<String> getTableFieldList(String tableName, List<FieldType> fieldTypes){
        List<String> fieldList = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet result = dmd.getColumns(connection.getCatalog(), null, tableName, "%");
            //TODO : replace the value 3, 4 ... with constants taking into account the database used (H2, postgres ...).
            while(result.next()){
                if (!fieldTypes.isEmpty()) {
                    for (FieldType fieldType : fieldTypes) {
                        if (fieldType.name().equalsIgnoreCase(result.getObject(6).toString())) {
                            fieldList.add(result.getObject(4).toString());
                        }
                    }
                } else{
                    fieldList.add(result.getObject(4).toString());
                }
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return fieldList;
    }

    public String loadFile(File f) {
        try {
            String tableName = dataManager.findUniqueTableName(TableLocation.capsIdentifier(FilenameUtils.getBaseName(f.getName()), true)).replaceAll("\"", "");
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.importFile(dataManager.getDataSource().getConnection(), tableName, f, new EmptyProgressVisitor());
            return tableName;
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return null;
    }

    public void saveFile(File f, String tableName){
        try {
            String extension = FilenameUtils.getExtension(f.getAbsolutePath());
            DriverFunction driver = driverFunctionContainer.getImportDriverFromExt(extension, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
            driver.exportTable(dataManager.getDataSource().getConnection(), tableName, f, new EmptyProgressVisitor());
        } catch (SQLException|IOException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
    }

    /**
     * Returns the list of distinct values contained by a field from a table from the database
     * @param tableName Name of the table containing the field.
     * @param fieldName Name of the field containing the values.
     * @return The list of distinct values of the field.
     */
    public static List<String> getFieldValueList(String tableName, String fieldName) {
        List<String> fieldValues = new ArrayList<>();
        try {
            Connection connection = dataManager.getDataSource().getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT DISTINCT "+fieldName+" FROM "+tableName);
            while(result.next()){
                fieldValues.add(result.getString(1));
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(ToolBox.class).error(e.getMessage());
        }
        return fieldValues;
    }

    @Override
    public String getId() {
        return FACTORY_ID;
    }

    private boolean isEditableAlreadyOpened(EditableElement editable) {
        for(Editor editor : editorManager.getEditors()) {
            if(editor instanceof ProcessEditor && editable.equals(editor.getEditableElement())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DockingPanelLayout makeEditableLayout(EditableElement editableElement) {
        if(editableElement instanceof ProcessEditableElement) {
            ProcessEditableElement editableProcess= (ProcessEditableElement)editableElement;
            if(isEditableAlreadyOpened(editableProcess)) { //Panel already created
                LoggerFactory.getLogger(ToolBox.class)
                        .info("This process ("+editableProcess.getProcessReference()+") is already shown in an editor.");
                return null;
            }
            return new ProcessPanelLayout(editableProcess);
        } else {
            return null;
        }
    }

    @Override
    public DockingPanelLayout makeEmptyLayout() {
        return new ProcessPanelLayout();
    }

    @Override
    public boolean match(DockingPanelLayout dockingPanelLayout) {
        return dockingPanelLayout instanceof ProcessPanelLayout;
    }

    @Override
    public EditorDockable create(DockingPanelLayout layout) {
        ProcessEditableElement editableProcess = ((ProcessPanelLayout)layout).getProcessEditableElement();
        //Check the DataSource state
        ProcessEditor pe = new ProcessEditor(this, editableProcess);
        return pe;
    }
}
