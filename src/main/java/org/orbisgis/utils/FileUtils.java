/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	private static int BUF_SIZE = 1024 * 64;
	private static File parentDir;

	/**
	 * Copies the specified folder in the destination folder
	 * 
	 * @param sourceDir
	 * @param destDir
	 * @throws IOException
	 */
	public static void copyDirsRecursively(File sourceDir, File destDir)
			throws IOException {
		File[] sourceChildren = sourceDir.listFiles();
		for (File file : sourceChildren) {
			if (file.isDirectory()) {
				File childDir = new File(destDir, file.getName());
				if (!childDir.exists() && !childDir.mkdirs()) {
					throw new IOException("Cannot create: " + childDir);
				}
				copyDirsRecursively(file, childDir);
			} else {
				copyFileToDirectory(file, destDir);
			}
		}
	}

        /**
         * Delete all the files in the directory.
         * This method takes care to remove all the files in the child directories (recursively) before removing
         * the directories
         * @param dir
         * @return
         *      false is a problem is encountered, true otherwise
         */
	public static boolean deleteFileInDir(File dir) {
		if (parentDir == null) {
			parentDir = dir;
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFileInDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		if (dir.equals(parentDir)) {
			return false;
		}

		return dir.delete();
	}

        /**
         * Delete a deirectory and his contents
         * @param dir
         * @return
         *      tru if no problems are encontered.
         */
	public static boolean deleteDir(File dir) {

		deleteFileInDir(dir);
		return dir.delete();
	}

        /**
         * Copy file in the directory destDir. If destDir doesn't exist, it will be created.
         * @param file
         *          The file to  be copied
         * @param destDir
         *          the destination directory.
         * @throws IOException
         *          If destDir does'nt exist and can't be crated.
         */
	public static void copyFileToDirectory(File file, File destDir)
			throws IOException {
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				throw new IOException("Cannot create directories:" + destDir);
			}
		}

		File output = new File(destDir, file.getName());
		copy(file, output);
	}

        /**
         * Copy the content of input in output.
         * @param input
         * @param output
         * @return
         * @throws IOException
         *          If there is a problem while copying the file.
         */
	public static long copy(File input, File output) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(input);
			return copy(in, output);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

        /**
         * Copy input into output, using a given Buffer to transmit the datas.
         * @param input
         * @param output
         * @param copyBuffer
         * @return
         * @throws IOException
         *          If a problem is encountered during the copy.
         */
	public static long copy(File input, File output, byte[] copyBuffer)
			throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(input);
			out = new FileOutputStream(output);
			return copy(in, out, copyBuffer);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

        /**
         * Copy the content of in in the outputFile.
         * @param in
         * @param outputFile
         * @return
         * @throws IOException
         *      If a probem is encountered during the copy.
         */
	public static long copy(InputStream in, File outputFile) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
			return copy(in, out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

        /**
         * Copy the stream in to the stream out
         * @param in
         * @param out
         * @return
         * @throws IOException
         *      If a problem is encountered during the copy.
         */
	public static long copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		return copy(in, out, buf);
	}

        /**
         * Copy the stream in to the stream out, given an intermediate copy buffer
         * @param in
         * @param out
         * @param copyBuffer
         * @return
         * @throws IOException
         *      If a problem is encountered during the copy.
         */
	public static long copy(InputStream in, OutputStream out, byte[] copyBuffer)
			throws IOException {
		long bytesCopied = 0;
		int read = -1;

		while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
			out.write(copyBuffer, 0, read);
			bytesCopied += read;
		}
		return bytesCopied;
	}

        /**
         * Retrieve a file pointed by url, store it in file.
         * @param url
         * @param file
         * @throws IOException
         *          If a problem is encountered while downloading the file.
         */
	public static void download(URL url, File file) throws IOException {
		OutputStream out = null;
		InputStream in = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			URLConnection conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[BUF_SIZE];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
			}
		}

	}

	/**
	 * Zips the specified file or folder
	 * 
	 * @param toZip
	 * @param outFile
	 * @throws IOException
	 */
	public static void zip(File toZip, File outFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(outFile)));

		// writing stream
		BufferedInputStream in = null;

		byte[] data = new byte[BUF_SIZE];
		ArrayList<File> listToZip = new ArrayList<File>();
		listToZip.add(toZip);

		while (listToZip.size() > 0) {
			File file = listToZip.remove(0);
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				for (File child : children) {
					listToZip.add(child);
				}
			} else {
				in = new BufferedInputStream(new FileInputStream(file),
						BUF_SIZE);

				out.putNextEntry(new ZipEntry(getRelativePath(toZip, file)));
				int count;
				while ((count = in.read(data, 0, BUF_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry(); // close each entry
			}
		}
		out.flush();
		out.close();
		in.close();
	}

        /**
         * Unzip an archive into the directory destDir.
         * @param zipFile
         * @param destDir
         * @throws IOException
         */
	public static void unzip(File zipFile, File destDir) throws IOException {
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(zipFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte data[] = new byte[BUF_SIZE];
			// write the files to the disk
			File newFile = new File(destDir, entry.getName());
			File parentFile = newFile.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Cannot create directory:" + parentFile);
			}
			if (!entry.isDirectory()) {
				FileOutputStream fos = new FileOutputStream(newFile);
				dest = new BufferedOutputStream(fos, BUF_SIZE);
				while ((count = zis.read(data, 0, BUF_SIZE)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		}
		zis.close();
	}

        /**
         * get the relative path to file,  according to the path to base
         * @param base
         * @param file
         * @return
         */
	public static String getRelativePath(File base, File file) {
		String absolutePath = file.getAbsolutePath();
		String path = absolutePath.substring(base.getAbsolutePath().length());
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

        /**
         * Retrieve the content of the file as an array of bytes.
         * @param file
         * @return
         * @throws FileNotFoundException
         * @throws IOException
         */
	public static byte[] getContent(File file) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(file);
		return getContent(fis);
	}

        /**
         * Retrieve the content of the Inputstream as an array of bytes.
         * @param fis
         * @return
         * @throws FileNotFoundException
         * @throws IOException
         */
	public static byte[] getContent(InputStream fis) throws IOException {
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		return buffer;
	}

        /**
         * compute the MD5 sum of the file.
         * @param file
         * @return
         * @throws FileNotFoundException
         *      if the file doesn't exist.
         * @throws IOException
         *      if there is a problem while computing the file
         * @throws NoSuchAlgorithmException
         *      if the MD5 algorithm can't be found.
         */
	public static byte[] getMD5(File file) throws FileNotFoundException,
			IOException, NoSuchAlgorithmException {
		byte[] content = getContent(file);
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(content, 0, content.length);
		return m.digest();
	}

        /**
         * Transform an array of bytes to a hexadecimal String
         * @param messageDigest
         * @return
         */
	public static String toHexString(byte[] messageDigest) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}

		return hexString.toString();
	}

        /**
         * Write content into file
         * @param file
         * @param content
         * @throws IOException
         */
	public static void setContents(File file, String content)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(content.getBytes());
		bos.close();
	}

        /**
         * Get the name of a filie without its extension.
         * @param file
         * @return
         */
	public static String getFileNameWithoutExtensionU(File file) {
		String name = file.getName();
		int extensionStart = name.lastIndexOf('.');
		String ret = name;
		if (extensionStart != -1) {
			ret = name.substring(0, name
					.indexOf(name.substring(extensionStart)));
		}

		return ret;
	}

	/**
	 * Get a file according an extension
	 * 
	 * @param file
	 * @param extension
	 * @return
	 * @throws IOException
	 */
	public static File getFileWithExtension(File file, final String extension)
			throws IOException {
		if (!file.isDirectory()) {
			final String ret = FileUtils.getFileNameWithoutExtensionU(file);
			File[] files = file.getParentFile().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String name) {

					if (name.toLowerCase().endsWith(extension)) {
						int extensionStart = name.lastIndexOf('.');
						String ret2 = name.substring(0, name.indexOf(name
								.substring(extensionStart)));
						return ret2.equals(ret);
					}
					return false;

				}

			});
			if (files.length > 0) {
				return new File(files[0].getAbsolutePath());
			} else {
				return null;
			}
		}

		throw new IOException(file.getAbsolutePath() + " is a directory");
	}

}
