package com.webservice.utils;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Parse Objects
 * @author leonardo.pereira
 *
 */

@Component
@Scope("singleton")
public class WebServiceUtils {
	private JAXBContext jaxbContext;
	private Unmarshaller jaxbUnmarshaller;
	
	/**
	 * Transform String to Object
	 * @param xml
	 * @param jaxbClass
	 * @return Object<T>
	 * @throws JAXBException
	 */
	public <T> T stringToObject(String xml, Class<T>  jaxbClass) throws JAXBException { 
		jaxbContext = JAXBContext.newInstance(jaxbClass);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Object objClass = jaxbUnmarshaller.unmarshal(new StringReader(xml));
		return jaxbClass.cast(objClass);
	}
	
	/**
	 * Parser xml find by tag
	 * @param xml
	 * @param tag
	 * @return String [value]
	 */
	public String findTagValue(String xml, String tag) {
	    Document document = Jsoup.parse(xml, "", Parser.xmlParser());
		return document.select(tag).text();
	}
}
