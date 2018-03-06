package model;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.org.apache.xpath.internal.operations.String;


import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Memory {
//    private static ObjectMapper mapper = new ObjectMapper();

    private final static String PDA_MEMORY_STORE = "src/main/resources/storage/examples.json";

    public static void save(Definition definition) {
//        try {
//                ModelFactory.definitions.add(definition);
//                File file = new File("definitions.json");
//                mapper.writeValue(file,ModelFactory.definitions.toArray(new Definition[0]));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            ArrayList<Definition> instances = load();
            Gson gson = new Gson();
            instances.add(definition);
            FileWriter writer = new FileWriter(PDA_MEMORY_STORE);
            gson.toJson(instances.toArray(new Definition[0]), writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<Definition> load() {

        ArrayList<Definition> library = new ArrayList<>();
        ModelFactory.definitions = library;
        try {
            Gson gson = new Gson();
            FileReader json = new FileReader(PDA_MEMORY_STORE);
            library.addAll(Arrays.asList(gson.fromJson(json, Definition[].class)));
            return library;
        } catch (Exception e) {
            try {
                File file = new File(PDA_MEMORY_STORE);
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return library;
        }
    }

    public static void delete(Definition definition) {
        try {
            ArrayList<Definition> library = load();
            library.remove(definition);
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(PDA_MEMORY_STORE);
            gson.toJson(library.toArray(new Definition[0]), writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveState() {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(PDA_MEMORY_STORE);
            gson.toJson(ModelFactory.definitions.toArray(new Definition[0]), writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ArrayList<ControlState> es = new ArrayList<ControlState>();
        ControlState q1 = new ControlState("q1");
        ControlState q2 = new ControlState("q2");
        es.add(q1);
        es.add(q2);
        ArrayList<Transition> transitions = new ArrayList<>();
        Transition transition1 = new Transition(new Configuration(q1, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition1);
        Transition transition2 = new Transition(new Configuration(q1, '1', 'A'), new Action(q2, 'A'));
        transitions.add(transition2);
        Transition transition3 = new Transition(new Configuration(q2, '1', 'A'), new Action(q1, 'A'));
        transitions.add(transition3);
        Transition transition4 = new Transition(new Configuration(q1, null, 'A'), new Action(q1, 'A'));
        transitions.add(transition4);
        Definition definition = new Definition("hig", es, q1, transitions, true);
        Definition definitions = new Definition("hsdig", es, q1, transitions, true);
        save(definition);
        save(definitions);
        load();
    }


}
