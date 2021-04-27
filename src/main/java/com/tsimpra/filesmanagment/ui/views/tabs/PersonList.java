package com.tsimpra.filesmanagment.ui.views.tabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.PersonalDocument;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.service.PersonService;
import com.tsimpra.filesmanagment.persistence.service.PersonalDocumentService;
import com.tsimpra.filesmanagment.persistence.service.TitleService;
import com.tsimpra.filesmanagment.ui.views.MainView;
import com.tsimpra.filesmanagment.ui.views.helpers.FileUploadHelper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;

@Route(value = "",layout = MainView.class)
public class PersonList extends VerticalLayout {

    private PersonService personService;
    private TitleService titleService;
    private PersonalDocumentService documentService;

    private Grid<Person> grid = new Grid<>(Person.class,false);
    private Upload fileUpload = new Upload();
    private Anchor download = new Anchor();
    private Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD_ALT));

    @Autowired
    public PersonList(PersonService personService,
                      TitleService titleService,PersonalDocumentService documentService) {
        this.personService=personService;
        this.titleService=titleService;
        this.documentService = documentService;

        download.getElement().setAttribute("download", true);
        download.add(downloadButton);
        downloadButton.setEnabled(false);

        configureGrid();
        configureFileUpload();
        updateList();

        HorizontalLayout fileButtons = new HorizontalLayout(fileUpload,download);
        fileButtons.setWidthFull();
        fileButtons.expand(fileUpload);

        this.add(grid,fileButtons);
    }

    private InputStream createResource() {
        Person p = grid.getSelectedItems().stream().reduce(new Person(),(x,y)->y);
        ObjectMapper om = new ObjectMapper();
        var baos = new ByteArrayOutputStream();
        try {
            om.writeValue(baos,p);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void configureFileUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        fileUpload.setReceiver(buffer);//Upload upload = new Upload(buffer);
        fileUpload.setAcceptedFileTypes(".csv",".txt",".xlsx");
        fileUpload.setMaxFileSize(3000);

        fileUpload.addSucceededListener(event -> {
            //String result = createComponent(event.getMIMEType(), event.getFileName(), buffer.getInputStream());
            if(event.getMIMEType().startsWith("text")) {
                String result = FileUploadHelper.convertInputToString(buffer.getInputStream());
                List<Person> persons = FileUploadHelper.parseJSONtoList(result);
                //List<Person> persons = FileUploadHelper.parseJSONtoList(buffer.getInputStream());
                for (Person p : persons) {
                    personService.save(p);
                }
            }else if(event.getMIMEType().contains("ms-excel")){
                String result = FileUploadHelper.convertInputToString(buffer.getInputStream());
                List<Person> persons = FileUploadHelper.parseCSVtoList(result);
                for (Person p : persons) {
                    personService.save(p);
                }
            }else if(event.getMIMEType().contains("spreadsheet")){
                List<Person> persons = FileUploadHelper.parseExcelToList(buffer.getInputStream());
                for (Person p : persons) {
                    personService.save(p);
                }
            }
            updateList();
        });

        fileUpload.addFileRejectedListener(event -> {
            Notification.show(event.getErrorMessage());
        });
        fileUpload.getElement().addEventListener("file-remove", event -> {
        });
    }

    private void configureGrid(){
        grid.addColumn(Person::getName).setHeader("Name");
        grid.addColumn(Person::getJob).setHeader("Job");
        grid.addColumn(person->{
            List<Title> titles = person.getTitles();
            String arr = "[";
            for (Title title: titles) {
                arr+=title.getName()+",";
            }
            return arr.length()>1?arr.substring(0,arr.length()-1)+"]":"-";
        }).setHeader("Titles");

        grid.asSingleSelect().addValueChangeListener(ev-> configureSingleSelect(ev.getValue()));

        grid.setItemDetailsRenderer(new ComponentRenderer<>(person-> configureItemsDetails(person)));
    }

    private void configureSingleSelect(Person person){
        if(grid.getSelectedItems().size()>0) {
            download.setHref(
                    new StreamResource("Person"+person.getName().replaceAll(" ","")+".txt",
                            () ->createResource())
            );
            downloadButton.setEnabled(true);
        }else{
            download.removeHref();
            downloadButton.setEnabled(false);
        }
    }

    private HorizontalLayout configureItemsDetails(Person person){
        HorizontalLayout hl = new HorizontalLayout();
        if(person.getPersonalDocument()!=null){
            hl.add(new Label(person.getPersonalDocument().getName()));
        }else{
            MemoryBuffer buffer = new MemoryBuffer();
            Upload personalDocUpload = new Upload(buffer);
            personalDocUpload.setMaxFileSize(3000);
            personalDocUpload.setMaxFiles(1);
            personalDocUpload.addSucceededListener(event->{
               //save file to personal docs
                PersonalDocument doc = new PersonalDocument();
                doc.setPerson(person);
                doc.setName(event.getFileName());
                doc.setContentLength(event.getContentLength());
                //doc service to save
                documentService.savePersonalDocument(doc,buffer.getInputStream());
                updateList();
            });
            personalDocUpload.addFileRejectedListener(event -> {
                Notification.show(event.getErrorMessage());
            });
            personalDocUpload.getElement().addEventListener("file-remove", event -> {
            });
            hl.add(personalDocUpload);
        }
        return hl;
    }

    private void updateList(){
        grid.setItems(this.personService.findAll());
    }
}
