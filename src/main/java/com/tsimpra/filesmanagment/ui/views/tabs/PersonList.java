package com.tsimpra.filesmanagment.ui.views.tabs;

import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.tsimpra.filesmanagment.persistence.entity.Title;
import com.tsimpra.filesmanagment.persistence.service.PersonService;
import com.tsimpra.filesmanagment.persistence.service.TitleService;
import com.tsimpra.filesmanagment.ui.views.MainView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "persons",layout = MainView.class)
public class PersonList extends VerticalLayout {

    private PersonService personService;
    private TitleService titleService;

    private Grid<Person> grid = new Grid<Person>(Person.class,false);

    @Autowired
    public PersonList(PersonService personService,TitleService titleService) {
        this.personService=personService;
        this.titleService=titleService;

        grid.setColumns("name","job");
        grid.addColumn(person->{
            List<Title> titles = person.getTitles();
            String arr = "[";
            for (Title title: titles) {
                arr+=title.getName()+",";
            }
            return arr.substring(0,arr.length()-1)+"]";
        }).setHeader("Titles");

        grid.setItems(personService.findAll());

        this.add(grid);
    }
}
