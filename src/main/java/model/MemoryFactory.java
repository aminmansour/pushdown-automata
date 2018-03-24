package model;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * MemoryFactory is a suite which is in charge of all memory operations found in the
 * application
 */
public class MemoryFactory {

    private final static String PDA_MEMORY_STORE = "library.json";
    private final static String EXAMPLE_MEMORY_STORE = "/storage/examples.json";


    /**
     * A method which saves definition instance to memory
     *
     * @param definition the definition to save
     */
    public static void saveToLibrary(Definition definition) {
        try {
            ArrayList<Definition> instances = loadLibrary();
            Gson gson = new Gson();
            instances.add(definition);
            FileWriter writer = null;
            writer = new FileWriter(PDA_MEMORY_STORE);
            gson.toJson(instances.toArray(new Definition[0]), writer);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method which loads all the definition instances stored in memory as an ArrayList
     *
     * @return the array list of definition instances stored in memory
     */
    public static ArrayList<Definition> loadLibrary() {
        ArrayList<Definition> library = new ArrayList<>();
        ModelFactory.libraryStore = library;
        return getDefinitionsFromFile(library, PDA_MEMORY_STORE, false);
    }

    public static ArrayList<Definition> loadExamples() {
        if (ModelFactory.exampleStore == null) {
            ArrayList<Definition> library = new ArrayList<>();
            ModelFactory.exampleStore = library;
            return getDefinitionsFromFile(library, EXAMPLE_MEMORY_STORE, true);
        }
        return ModelFactory.exampleStore;
    }

    //reads definitions stored on file and convert them into arraylist
    private static ArrayList<Definition> getDefinitionsFromFile(ArrayList<Definition> library, String url, boolean loadExamples) {
        try {
            Gson gson = new Gson();
            Reader reader;
            if (loadExamples) {
                reader = new BufferedReader(new InputStreamReader(MemoryFactory.class.getResourceAsStream(url)));
            } else {
                reader = new FileReader(url);
            }
            library.addAll(Arrays.asList(gson.fromJson(reader, Definition[].class)));
            return library;
        } catch (Exception e) {
            try {
                File file = new File(PDA_MEMORY_STORE);
                file.createNewFile();
                return library;
            } catch (IOException e1) {
                e1.printStackTrace();
                return library;
            }
        }
    }


    /**
     * A method which saves the most recent changes and updates the loaded PDA
     */
    public static void saveState() {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(PDA_MEMORY_STORE);
            gson.toJson(ModelFactory.libraryStore.toArray(new Definition[0]), writer);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
