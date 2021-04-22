package com.tsimpra.filesmanagment.ui.views.tabs;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.service.PersonService;
import com.tsimpra.filesmanagment.persistence.service.TitleService;
import com.tsimpra.filesmanagment.ui.views.MainView;
import com.tsimpra.filesmanagment.ui.views.helpers.FileUploadHelper;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.tsimpra.filesmanagment.ui.views.helpers.FileUploadHelper.createComponent;

@Route(value = "",layout = MainView.class)
public class PersonList extends VerticalLayout {

    private PersonService personService;
    private TitleService titleService;

    private Grid<Person> grid = new Grid<>(Person.class,false);
    private Upload fileUpload = new Upload();

    @Autowired
    public PersonList(PersonService personService,TitleService titleService) {
        this.personService=personService;
        this.titleService=titleService;

        configureGrid();
        configureFileUpload();
        updateList();

        this.add(grid,fileUpload);
    }

    private void configureFileUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        fileUpload.setReceiver(buffer);//Upload upload = new Upload(buffer);

        fileUpload.addSucceededListener(event -> {
            String result = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            Person resultingPerson = FileUploadHelper.parseResult(result);
            personService.save(resultingPerson);
            updateList();
        });

        fileUpload.addFileRejectedListener(event -> {
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
            return arr.substring(0,arr.length()-1)+"]";
        }).setHeader("Titles");
    }
    private void updateList(){
        grid.setItems(this.personService.findAll());
    }
}
