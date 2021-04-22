package com.tsimpra.filesmanagment.ui.views;

import com.tsimpra.filesmanagment.ui.views.tabs.PersonList;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;


public class MainView extends AppLayout {

    public MainView() {

        this.addToNavbar(new RouterLink("Persons List", PersonList.class));

    }
}
