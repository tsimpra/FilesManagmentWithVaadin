package com.tsimpra.filesmanagment.ui.views.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

public class FileUploadHelper {
    public static String convertInputToString(InputStream inputStream) {
        String text;
        try {
            text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return text;
    }

    public static Person parseResult(String result) {
        Person resultingPerson =null;
        try {
            resultingPerson = new ObjectMapper().readValue(result, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }

    //takes a string with the contents of a file, given the contents are Person objects
    //given each line is an object it reads the line and creates the Person and adds it to the list
    //returns the list of persons read from the file
    public static List<Person> parseJSONtoList(String result) {
        List<Person> resultingPerson =new ArrayList<>();
        Scanner scanner = new Scanner(result);
        while(scanner.hasNext()){
            try {
                Person p  =  new ObjectMapper().readValue(scanner.next(),Person.class);
                resultingPerson.add(p);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return resultingPerson;
    }

    //takes a csv file to string. Each line respresents an object of type Person
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
}

