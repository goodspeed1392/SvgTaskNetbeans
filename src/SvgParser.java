
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author goodspeed
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

// SAX
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SvgParser {

    public SvgParser(File file)
    {
        this.file = file;
        
        SAXParserFactory factory = SAXParserFactory.newInstance();

        factory.setValidating(true);
        factory.setNamespaceAware(false);
        SAXParser parser;

        InputStream xmlData = null;
        try
        {
            //xmlData = new FileInputStream("test.svg");

            parser = factory.newSAXParser();
            MyParser myparser = new MyParser();
            parser.parse(file, myparser);
            map = myparser.getPaths();
            listOtr = myparser.getList();
            
            
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            // обработки ошибки, файл не найден
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
            // обработка ошибки Parser
        } catch (SAXException e)
        {
            e.printStackTrace();
            // обработка ошибки SAX
        } catch (IOException e)
        {
            e.printStackTrace();
            // обработка ошибок ввода
        } 
        
    }
    public Map<String,Double> getPaths(){
        return map;
    }
    
    public List<Otrezok> getList(){
        return listOtr;
    }
    
    private Map<String,Double> map;
    private File file;
    private List<Otrezok> listOtr;
}

class MyParser extends DefaultHandler {
    
    public MyParser(){
        map = new HashMap();
        listOtr = new LinkedList();
    }
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if(qName.equals("path"))
            //System.out.println("id отрезка "+attributes.getValue("id"));
            if (attributes.getValue("d")!=null){
                //System.out.println("Длина отрезка "+segmLength(attributes.getValue("d")));
                map.put(attributes.getValue("id"), segmLength(attributes.getValue("d")));
            }
            //
        super.startElement(uri, localName, qName, attributes);
    }
    
    public double segmLength(String str){
        //удаляем ненужные символы
        String str2;
        str2 = str.replace(',', ' ');
        String str3 = str2.replace('M', ' ');
        
        double[] coord = new double[4]; //массив координат
        
        int index = 0; //индекс символа в строке
        int indexCoord = 0; //индекс в массиве координат
        String token = "";
        
        //парсим строку
        while (index<str3.length())
        {
            if (Character.isWhitespace(str3.charAt(index))) {
                index++;
                if(token!=""){
                    coord[indexCoord] = Double.parseDouble(token);
                    indexCoord++;
                    token = "";
                }
            }
            else
            {
                token+=str3.charAt(index);
                index++;
            }
        }
        if (!Character.isWhitespace(str3.charAt(index-1)))  
            coord[indexCoord] = Double.parseDouble(token);  //записываем последний токен
        
        //Вычислим длину
        double x1 = coord[0];
        double y1 = coord[1];
        double x2 = coord[2];
        double y2 = coord[3];
        
        Otrezok otr = new Otrezok(x1,y1,x2,y2);         //добавляем отрезки в список для дальнейшей отрисовки
        listOtr.add(otr);                           
        
        double dlina = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1), 2));
        return dlina;
    }
    
    public Map<String,Double> getPaths(){
        return map;
    }
    
    public List<Otrezok> getList(){
        return listOtr;
    }
    
    private Map<String,Double> map;
    private List<Otrezok> listOtr;

}
  

