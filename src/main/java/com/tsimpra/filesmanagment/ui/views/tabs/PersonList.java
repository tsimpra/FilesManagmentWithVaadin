package com.tsimpra.filesmanagment.ui.views.tabs;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.service.PersonService;
import com.tsimpra.filesmanagment.persistence.service.TitleService;
import com.tsimpra.filesmanagment.ui.views.MainView;
import com.tsimpra.filesmanagment.ui.views.helpers.FileUploadHelper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Route(value = "",layout = MainView.class)
public class PersonList extends VerticalLayout {

    private PersonService personService;
    private TitleService titleService;

    private Grid<Person> grid = new Grid<>(Person.class,false);
    private Upload fileUpload = new Upload();
    private Anchor download = new Anchor();;
    private Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD_ALT));

    @Autowired
    public PersonList(PersonService personService,TitleService titleService) {
        this.personService=personService;
        this.titleService=titleService;

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
        return new ByteArrayInputStream(grid.getSelectedItems().stream()
                .map(x->x.toString())
                .reduce("",(x,y)->x+y).getBytes(StandardCharsets.UTF_8));
    }

    private void configureFileUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        fileUpload.setReceiver(buffer);//Upload upload = new Upload(buffer);
        fileUpload.setAcceptedFileTypes(".csv",".txt",".xlsx");
        fileUpload.setMaxFileSize(30000);

        fileUpload.addSucceededListener(event -> {
            //String result = createComponent(event.getMIMEType(), event.getFileName(), buffer.getInputStream());

            if(event.getMIMEType().startsWith("text")) {
                String result = FileUploadHelper.convertInputToString(buffer.getInputStream());
                //Person resultingPerson = FileUploadHelper.parseResult(result);
                List<Person> persons = FileUploadHelper.parseJSONtoList(result);
                for (Person p : persons) {
                    personService.save(p);
                }
                //personService.save(resultingPerson);
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
            return arr.length()>1?arr.substring(0,arr.length()-1)+"]":"-";//arr+"]";
        }).setHeader("Titles");

        grid.asSingleSelect().addValueChangeListener(ev->{
            if(grid.getSelectedItems().size()>0) {
                download.setHref(
                        new StreamResource("Person"+ev.getValue().getName().replaceAll(" ","")+".txt",
                        () ->createResource())
                );
                downloadButton.setEnabled(true);
            }else{
                download.removeHref();
                downloadButton.setEnabled(false);
            }
        });
    }

    private void updateList(){
        grid.setItems(this.personService.findAll());
    }
}
