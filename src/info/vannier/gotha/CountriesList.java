/*
 * CountriesList.java
 *
 */

package info.vannier.gotha;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * contains a static method designed to import country list from a xml file
 * See : http://www.iso.org/iso/country_codes/iso_3166_code_lists.htm
 * @author Luc Vannier
 */
public class CountriesList {
    public static ArrayList<Country> importCountriesFromXMLFile(File sourceFile){
        Document doc = ExternalDocument.getDocumentFromXMLFile(sourceFile);
        if (doc == null){
            return null;
        }
        ArrayList<Country> alCountries = new ArrayList<Country>();

        NodeList nl = doc.getElementsByTagName("ISO_3166-1_Entry");
        for (int i = 0; i < nl.getLength(); i++){
            Country country = new Country();
            Node n = nl.item(i);
            NodeList cnl = n.getChildNodes();
            for (int j = 0; j < cnl.getLength(); j++){
                Node cn = cnl.item(j);
                if (cn.getNodeName().equals("ISO_3166-1_Country_name")){
                    country.name = cn.getTextContent();
                }
                if (cn.getNodeName().equals("ISO_3166-1_Alpha-2_Code_element")){
                    country.setAlpha2Code(cn.getTextContent());
                }
            }
            
            alCountries.add(country);
        }
        return alCountries;
    }
}




