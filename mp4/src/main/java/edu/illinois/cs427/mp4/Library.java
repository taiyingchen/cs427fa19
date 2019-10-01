package edu.illinois.cs427.mp4;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;

/**
 * Container class for all the collections (that eventually contain books).
 * Its main responsibility is to save the collections to a file and restore them from a file.
 */
public final class Library {
    private List<Collection> collections;

    /**
     * Builds a new, empty library.
     */
    public Library() {
        this.collections = new ArrayList<Collection>();
    }

    /**
     * Builds a new library and restores its contents from the given file.
     *
     * @param fileName the file from where to restore the library.
     */
    public Library(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String stringRepresentation = bufferedReader.readLine();
        bufferedReader.close();

        this.collections = new ArrayList<Collection>();

        JSONObject jsonObject = new JSONObject(stringRepresentation);
        JSONArray jsonArray = (JSONArray) jsonObject.get("collections");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject collectionJsonObject = jsonArray.getJSONObject(i);
            Collection collection = new Collection(collectionJsonObject);
            addCollection(collection);
        }
    }

    /**
     * Saved the contents of the library to the given file.
         *
     * @param fileName the file where to save the library
     */
    public void saveLibraryToFile(String fileName) throws IOException {
        // Write the content in file 
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.collections.size(); i++) {
            jsonArray.put(this.collections.get(i).toJSONObject());
        }
        jsonObject.put("type", "Library");
        jsonObject.put("collections", jsonArray);
        
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        bufferedWriter.write(jsonObject.toString());
        bufferedWriter.close();
    }

    /**
     * Returns the collections contained in the library.
     *
     * @return library contained elements
     */
    public List<Collection> getCollections() {
        return this.collections;
    }

    /**
     * Add collections to the library.
     *
     * @param collection to be added to the library
     */
    public void addCollection(Collection collection) {
        this.collections.add(collection);
    }
   
}
