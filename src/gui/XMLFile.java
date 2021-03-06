package gui;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class XMLFile {

	private Document document;

	public XMLFile(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		document = builder.build(xmlsource);
	}

	public Enumeration<String> getLanguages() {
		Vector<String> langs = new Vector<String>();
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
        while (i.hasNext()) {
        	Element elem = i.next();
        	langs.add(elem.getAttributeValue("trigger"));
        }
        return langs.elements();
	}
	
	public String getMenuEntry(String entry,String menuitem) {
		return document.getRootElement().getChild(entry).getChild(menuitem).getAttribute("name").getValue();
	}
	
	public Enumeration<String> getMenuEntries(String entry) {
		Vector<String> langs = new Vector<String>();
		Iterator<Element> i=document.getRootElement().getChild(entry).getChildren().iterator();
        while (i.hasNext()) {
        	Element elem = i.next();
        	langs.add(elem.getAttributeValue("trigger"));
        }
        return langs.elements();
	}

}