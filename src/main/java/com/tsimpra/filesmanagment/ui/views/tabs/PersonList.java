package com.tsimpra.filesmanagment.ui.views.tabs;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.PersonalDocument;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.service.PersonService;
import com.tsimpra.filesmanagment.persistence.service.PersonalDocumentService;
import com.tsimpra.filesmanagment.ui.views.MainView;
import com.tsimpra.filesmanagment.ui.views.helpers.FileHelper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
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

import java.util.List;

@Route(value = "",layout = MainView.class)
public class PersonList extends VerticalLayout {

    private PersonService personService;
    private PersonalDocumentService documentService;

    private Grid<Person> grid = new Grid<>(Person.class,false);
    private Upload fileUpload = new Upload();
    private Anchor download = new Anchor();
    private Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD_ALT));

    @Autowired
    public PersonList(PersonService personService,
                      PersonalDocumentService documentService) {
        this.personService=personService;
        this.documentService = documentService;
        this.setClassName("persons-list");

        download.getElement().setAttribute("download", true);
        download.add(downloadButton);
        downloadButton.setEnabled(false);

        configureGrid();
        configureFileUpload();
        updateList();

        HorizontalLayout fileButtons = new HorizontalLayout(fileUpload,download);
        fileButtons.addClassName("footer-buttons");
        fileButtons.setWidthFull();
        fileButtons.expand(fileUpload);

        this.add(grid,fileButtons);
    }

    //grid configuration. Adds columns and calls rest configuration methods
    private void configureGrid(){
        grid.addClassName("persons-grid");
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

        grid.setItemDetailsRenderer(new ComponentRenderer<>(person->configureItemsDetails(person)));
    }

    //configuration for single select on grid item
    private void configureSingleSelect(Person person){
        if(grid.getSelectedItems().size()>0) {
            download.setHref(
                    new StreamResource(
                            "Person"+person.getName().replaceAll(" ","")+".txt",
                            () ->FileHelper.getPersonStream(person))
            );
            downloadButton.setEnabled(true);
        }else{
            download.removeHref();
            downloadButton.setEnabled(false);
        }
    }

    //configuration for grid item details
    private Div configureItemsDetails(Person person){
        Div div = new Div();
        div.setClassName("details-upload");
        div.setSizeFull();
        if(person.getPersonalDocument()!=null){
            String filename = person.getPersonalDocument().getName();
            Anchor downloadPersonDoc = new Anchor();
            downloadPersonDoc.setHref(new StreamResource( filename,
                    ()->documentService.getDocumentStream(person.getPersonalDocument())
            ));
            Button dlButton = new Button("Download",new Icon(VaadinIcon.DOWNLOAD_ALT));
            downloadPersonDoc.getElement().setAttribute("download",true);
            downloadPersonDoc.add(dlButton);
            HorizontalLayout details = new HorizontalLayout(new Label(filename), downloadPersonDoc);
            details.addClassName("details-document-layout");
            details.setSpacing(true);
            details.setAlignItems(Alignment.BASELINE);
            div.add(details);
        }else{
            MemoryBuffer buffer = new MemoryBuffer();
            Upload personalDocUpload = new Upload(buffer);
            personalDocUpload.setMaxFileSize(3000);
            personalDocUpload.setMaxFiles(1);
            personalDocUpload.setDropAllowed(false);
            personalDocUpload.setSizeUndefined();

            personalDocUpload.addSucceededListener(event->{
                //save file to personal docs
                PersonalDocument doc = new PersonalDocument();
                doc.setPerson(person);
                doc.setName(event.getFileName());
                doc.setContentLength(event.getContentLength());
                person.setPersonalDocument(doc);
                //doc service to save
                documentService.savePersonalDocument(doc,buffer.getInputStream());
                //updateList();
                grid.getDataProvider().refreshAll();
                grid.setDetailsVisible(person,true);
            });
            personalDocUpload.addFileRejectedListener(event -> {
                Notification.show(event.getErrorMessage());
            });
            personalDocUpload.getElement().addEventListener("file-remove", event -> {
            });
            div.add(personalDocUpload);
        }
        return div;
    }

    //configuration for file upload button. Accepts only txt/csv/xlsx files
    //Depending on the given type calls the appropriate method to parse the objects stored in file
    private void configureFileUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        fileUpload.setReceiver(buffer);
        fileUpload.setAcceptedFileTypes(".csv",".txt",".xlsx");
        fileUpload.setMaxFileSize(3000);

        fileUpload.addSucceededListener(event -> {
            if(event.getMIMEType().startsWith("text")) {
                String result = FileHelper.convertInputToString(buffer.getInputStream());
                List<Person> persons = FileHelper.parseJSONtoList(result);
                //List<Person> persons = FileUploadHelper.parseJSONtoList(buffer.getInputStream());
                for (Person p : persons) {
                    personService.save(p);
                }
            }else if(event.getMIMEType().contains("ms-excel")){
                String result = FileHelper.convertInputToString(buffer.getInputStream());
                List<Person> persons = FileHelper.parseCSVtoList(result);
                for (Person p : persons) {
                    personService.save(p);
                }
            }else if(event.getMIMEType().contains("spreadsheet")){
                List<Person> persons = FileHelper.parseExcelToList(buffer.getInputStream());
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

    //Refreshes the grid
    private void updateList(){
        grid.setItems(this.personService.findAll());
    }
}
