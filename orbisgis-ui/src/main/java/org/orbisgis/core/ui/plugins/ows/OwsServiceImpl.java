/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Proxy for the remote services.
 * @author C�dric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OwsServiceImpl implements OwsService {

    private SAXParser parser;
    private final OwsSAXHandler owsSaxHandler;
    private DocumentBuilder builder;

    public OwsServiceImpl() throws ParserConfigurationException, SAXException {
        this.parser = SAXParserFactory.newInstance().newSAXParser();
        this.owsSaxHandler = new OwsSAXHandler();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // In order to get a xerces implementation which takes namespaces
        // into account
        dbf.setNamespaceAware(true); 
        builder = dbf.newDocumentBuilder();
    }
    
    

    @Override
    public List<OwsFileBasic> getAllOwsFiles() {
        List<OwsFileBasic> owsFiles = new ArrayList<OwsFileBasic>();

        try {
            this.parser.parse(OwsContextUtils.callService(OwsContextUtils.getServiceGetAllUrl()), this.owsSaxHandler);
            owsFiles = this.owsSaxHandler.getFiles();
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return owsFiles;
    }
    
    @Override
    public Node getOwsFile(int id) {
        String url = OwsContextUtils.getServiceGetOneOwsUrl() + "/" + id;
        InputStream owsInput = OwsContextUtils.callService(url);
        Node node = null;
        
        try {
            Document doc = builder.parse(owsInput);
            doc.getDocumentElement().normalize();
            node = doc.getElementsByTagName("OWSContext").item(0);
        } catch (SAXException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return node;

    }

    @Override
    public void saveOwsFileAs(String data) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("owc", data));
        String url = OwsContextUtils.getServiceExportOwsAsUrl();
        OwsContextUtils.callServicePost(url, formparams);
    }

    @Override
    public void saveOwsFile(String data, int projectId) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("owc", data));
        formparams.add(new BasicNameValuePair("id", Integer.toString(projectId)));
        String url = OwsContextUtils.getServiceExportOwsAsUrl();
        OwsContextUtils.callServicePost(url, formparams);
    }

    private class OwsSAXHandler extends DefaultHandler {

        private final List<OwsFileBasic> files;
        private int id;
        private String owsTitle;
        private String owsAbstract;
        private String buffer;

        public OwsSAXHandler() {
            this.files = new ArrayList<OwsFileBasic>();
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            this.files.clear();
        }
        
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equals("item")) {
                OwsFileBasic file = new OwsFileBasic(id, owsTitle, owsAbstract);
                this.files.add(file);
            }
            else {
                if (qName.equals("id")) {
                    this.id = Integer.parseInt(buffer);
                }
                else if (qName.equals("title")) {
                    this.owsTitle = buffer;
                }
                else if (qName.equals("abstract")) {
                    this.owsAbstract = buffer;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.buffer = new String(ch, start, length);
        }

        
        public List<OwsFileBasic> getFiles() {
            return new ArrayList<OwsFileBasic>(this.files);
        }
    }
}