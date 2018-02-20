package model;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xpath.internal.operations.String;


import java.util.ArrayList;

public class ModelFactory {

    public static ArrayList<Definition> definitions;

    public static boolean checkForOccurence(Definition definition) {
        for (Definition d : Memory.load()) {
            if (definition.getIdentifier().equals(d.getIdentifier())) {
                return true;
            }
        }
        return false;
    }
}
