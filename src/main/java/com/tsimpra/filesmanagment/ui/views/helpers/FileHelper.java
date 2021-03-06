package com.tsimpra.filesmanagment.ui.views.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileHelper {
    public static String convertInputToString(InputStream inputStream) {
        String text;
        try {
            text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return text;
    }

    //Not Used. Was used to parse a single Person Object.
    public static Person parseResult(String result) {
        Person resultingPerson =null;
        try {
            resultingPerson = new ObjectMapper().readValue(result, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }

    //takes a string with the contents of a file, given the contents are serialized json Person objects
    //reads the string and creates an iterator with the objects. Then loops and creates
    //a Person and adds it to the list
    //returns the list of persons read from the file
    public static List<Person> parseJSONtoList(String result) {
        List<Person> resultingPerson =new ArrayList<>();
        ObjectMapper mapper=new ObjectMapper();
        try (var parser= mapper.createParser(result)){
            Iterator<Person> it = parser.readValuesAs(Person.class);
            while(it.hasNext()){
                Person p = it.next();
                resultingPerson.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }

    //takes a csv file to string. Each line represents an object of type Person
    //Reads each line and creates a Person from the values.First value is the name,second the job
    //and the rest are just Titles of the Person.
    public static List<Person> parseCSVtoList(String result) {
        List<Person> resultingPerson = new ArrayList<>();
        try(var in = new BufferedReader(new CharArrayReader(result.toCharArray()))) {
            String line;
            while((line=in.readLine())!=null){
                String[] values = line.split(",");//COMMA_DELIMITER);
                Person p = new Person();
                p.setName(values[0]);
                p.setJob(values[1]);
                String[] titlesArr = Arrays.copyOfRange(values,2,values.length);
                List<Title> titles = parseTitles(titlesArr,p);
                p.setTitles(titles);
                resultingPerson.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }

    //takes a xlsx file inputstream. transforms it to Workbook and getting the first sheet
    //iterates through rows and then through columns and creates a Person object
    // for each row.
    public static List<Person> parseExcelToList(InputStream inputStream) {
        List<Person> resultingPerson = new ArrayList<>();
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            rows.next();
            while(rows.hasNext()){
                Person p = new Person();
                Row row = rows.next();
                Iterator<Cell> cells = row.cellIterator();
                if(cells.hasNext()){
                    p.setName(cells.next().getStringCellValue());
                }
                if(cells.hasNext()){
                    p.setJob(cells.next().getStringCellValue());
                }
                if(cells.hasNext()){
                    String[] titles = cells.next().getStringCellValue().split(",");
                    List<Title> titleList = parseTitles(titles,p);
                    p.setTitles(titleList);
                }
                resultingPerson.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }

    //helper method to create Titles for a Person
    private static List<Title> parseTitles(String[] input,Person p){
        List<Title> titles = new ArrayList<>();
        for(String title:input){
            Title t = new Title();
            title = title.replaceAll("\"","");
            t.setName(title);
            t.setPerson(p);
            titles.add(t);
        }
        return titles;
    }

    //Creates a jackson ByteArrayInputStream from a Person data
    public static InputStream getPersonStream(Person p) {
        ObjectMapper om = new ObjectMapper();
        var baos = new ByteArrayOutputStream();
        try {
            om.writeValue(baos,p);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
}

