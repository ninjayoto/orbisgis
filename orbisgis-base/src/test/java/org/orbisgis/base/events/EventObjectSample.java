/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.base.events;

import java.util.EventObject;

/**
 * @brief Event Data Sample for unit test
 * This is the event Data attached with an event (a call on listeners)
 */
public class EventObjectSample extends EventObject {
    String message;
    /**
     * 
     * @param message The secret message from the Event Source
     * @param o The event source
     */
    public EventObjectSample(String message, Object o) {
        super(o);
        this.message = message;
    }
    /**
     * 
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    /**
     * 
     * @param message The message to send to the target
     */
    public void setMessage(String message) {
        this.message = message;
    }    
}
