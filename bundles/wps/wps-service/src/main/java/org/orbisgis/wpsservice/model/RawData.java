/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * RawData extends the ComplexData class.
 * It represents a file or a folder.
 *
 * @author Sylvain PALOMINOS
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RawData", propOrder = {"isFile", "isDirectory", "multiSelection", "fileTypes", "excludedTypes"})
public class RawData extends ComplexDataType {

    /** True if the RawData can be a file, false otherwise. */
    @XmlAttribute(name = "isFile")
    private boolean isFile;
    /** True if the RawData can be a directory, false otherwise. */
    @XmlAttribute(name = "isDirectory")
    private boolean isDirectory;
    /** True if the user can select more than one file/directory, false otherwise. */
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection;
    /** Array of the file type allowed for the raw data. If no types are specified, accept all. */
    @XmlElement(name = "fileTypes")
    private String[] fileTypes;
    /** Array of the file type not allowed for the raw data. */
    @XmlElement(name = "excludedTypes")
    private String[] excludedTypes;

    /**
     * Constructor giving a list of format.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null default format.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public RawData(List<Format> formatList) throws MalformedScriptException {
        this.format = formatList;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected RawData(){
        super();
    }

    /**
     * Returns if the RawData can be a directory or not.
     * @return True if the RawData can be a directory, false otherwise.
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Sets if the RawData can be a directory or not.
     * @param directory True if the RawData can be a directory, false otherwise.
     */
    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    /**
     * Returns if the RawData can be a file or not.
     * @return True if the RawData can be a file, false otherwise.
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Sets if the RawData can be a file or not.
     * @param file True if the RawData can be a file, false otherwise.
     */
    public void setFile(boolean file) {
        isFile = file;
    }

    /**
     * Returns if the user can select more than just one file/directory.
     * @return True if user can select more than just one file/directory, false otherwise.
     */
    public boolean multiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than just one file/directory.
     * @param multiSelection True if user can select more than just one file/directory, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    /**
     * Returns the array of the file type allowed for the raw data. If no types are specified, accept all.
     * @return the array of the file type allowed for the raw data.
     */
    public String[] getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the array of the file type allowed for the raw data. If no types are specified, accept all.
     * @param fileTypes The array of the file type allowed for the raw data.
     */
    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * Returns the array of the file type not allowed for the raw data.
     * @return the array of the file type not allowed for the raw data.
     */
    public String[] getExcludedTypes() {
        return excludedTypes;
    }

    /**
     * Sets the array of the file type not allowed for the raw data.
     * @param excludedTypes The array of the file type not allowed for the raw data.
     */
    public void setExcludedTypes(String[] excludedTypes) {
        this.excludedTypes = excludedTypes;
    }
}
