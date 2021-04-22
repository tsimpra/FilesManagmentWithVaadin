package com.tsimpra.filesmanagment.ui.views.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsimpra.filesmanagment.persistence.entity.Person;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUploadHelper {
    public static String createComponent(String mimeType, String fileName,
                                      InputStream stream) {
        if (mimeType.startsWith("text")) {
            return createTextComponent(stream);
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
}
