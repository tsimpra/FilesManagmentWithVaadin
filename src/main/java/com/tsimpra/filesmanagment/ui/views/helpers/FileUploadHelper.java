package com.tsimpra.filesmanagment.ui.views.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.vaadin.flow.internal.MessageDigestUtil;
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
    public static String createComponent(String mimeType, String fileName,
                                      InputStream stream) {
        if (mimeType.startsWith("text")) {
            return createTextComponent(stream);
        }else{
            try {
                return IOUtils.toString(stream, StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                mimeType, MessageDigestUtil.sha256(stream.toString()));
        return text;

    }

    public static String createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
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

    public static String convertInputToString(InputStream inputStream) {
        String text;
        try {
            text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return text;
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
                List<Title> titles = new ArrayList<>();
                for (int i = 2; i <values.length ; i++) {
                    Title t = new Title();
                    values[i]= values[i].replaceAll("\"","");
                    t.setName(values[i]);
                    t.setPerson(p);
                    titles.add(t);
                }
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
                    List<Title> titleList = new ArrayList<>();
                    for(String title:titles){
                        Title t = new Title();
                        t.setName(title);
                        t.setPerson(p);
                        titleList.add(t);
                    }
                    p.setTitles(titleList);
                }
                resultingPerson.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultingPerson;
    }
}
