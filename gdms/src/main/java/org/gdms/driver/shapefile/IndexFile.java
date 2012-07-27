/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import org.gdms.data.WarningListener;
import org.gdms.driver.ReadBufferManager;

/**
 * IndexFile parser for .shx files.<br>
 * For now, the creation of index files is done in the ShapefileWriter. But this
 * can be used to access the index.<br>
 * For details on the index file, see <br>
 * <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf"><b>"ESRI(r)
 * Shapefile - A Technical Description"</b><br> * <i>'An ESRI White Paper .
 * May 1997'</i></a>
 *
 * @author Ian Schneider
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/IndexFile.java $
 */
public class IndexFile {
	private static final Logger LOGGER = Logger
			.getLogger("org.gdms.driver.shapefile");

	private FileChannel channel;

	private ReadBufferManager buf = null;

	private int lastIndex = -1;

	private int recOffset;

	private int recLen;

	private ShapefileHeader header = null;

	/**
	 * Load the index file from the given channel.
	 *
	 * @param channel
	 *            The channel to read from.
	 * @throws IOException
	 *             If an error occurs.
	 */
	public IndexFile(FileChannel channel, WarningListener warningListener)
			throws IOException {
		readHeader(channel, warningListener);
		this.channel = channel;
		LOGGER.finest("Reading from file...");
		this.buf = new ReadBufferManager(channel, 8 * 128);
	}

	/**
	 * Get the header of this index file.
	 *
	 * @return The header of the index file.
	 */
	public ShapefileHeader getHeader() {
		return header;
	}

	private void readHeader(ReadableByteChannel channel,
			WarningListener warningListener) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(100);
		while (buffer.remaining() > 0) {
			channel.read(buffer);
		}
		buffer.flip();
		header = new ShapefileHeader();
		header.read(buffer, warningListener);
	}

	private void readRecord(int index) throws IOException {
		int pos = 100 + index * 8;
		this.recOffset = buf.getInt(pos);
		this.recLen = buf.getInt(pos + 4);
		this.lastIndex = index;
	}

	public void close() throws IOException {
		if (channel != null && channel.isOpen()) {
			channel.close();
		}
		this.buf = null;
	}

	/**
	 * Get the number of records in this index.
	 *
	 * @return The number of records.
	 */
	public int getRecordCount() {
		return (header.getFileLength() * 2 - 100) / 8;
	}

	/**
	 * Get the offset of the record (in 16-bit words).
	 *
	 * @param index
	 *            The index, from 0 to getRecordCount - 1
	 * @return The offset in 16-bit words.
	 * @throws IOException
	 */
	public int getOffset(int index) throws IOException {
		int ret = -1;

		if (this.lastIndex != index) {
			this.readRecord(index);
		}

		ret = this.recOffset;

		return 2 * ret;
	}

	/**
	 * Get the offset of the record (in real bytes, not 16-bit words).
	 *
	 * @param index
	 *            The index, from 0 to getRecordCount - 1
	 * @return The offset in bytes.
	 * @throws IOException
	 */
	public int getOffsetInBytes(int index) throws IOException {
		return this.getOffset(index) * 2;
	}

	/**
	 * Get the content length of the given record in bytes, not 16 bit words.
	 *
	 * @param index
	 *            The index, from 0 to getRecordCount - 1
	 * @return The lengh in bytes of the record.
	 * @throws IOException
	 */
	public int getContentLength(int index) throws IOException {
		int ret = -1;

		if (this.lastIndex != index) {
			this.readRecord(index);
		}

		ret = this.recLen;

		return ret;
	}

}
