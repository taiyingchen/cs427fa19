package edu.illinois.cs427.mp4;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Represents a collection of books or (sub)collections.
 */
public final class Collection extends Element {
    List<Element> elements;
    private String name;
    
    /**
     * Creates a new collection with the given name.
     *
     * @param name the name of the collection
     */
    public Collection(String name) {
        this.name = name;
        this.elements = new ArrayList<Element>();
    }

    /**
     * Restores a collection from its given string representation.
     *
     * @param stringRepresentation the string representation
     */
    public static Collection restoreCollection(String stringRepresentation) {
        JSONObject jsonObject = new JSONObject(stringRepresentation);
        return new Collection(jsonObject);
    }

    public Collection(JSONObject jsonObject) {
        this.name = (String) jsonObject.get("name");
        this.elements = new ArrayList<Element>();
        
        JSONArray elements = (JSONArray) jsonObject.get("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject elementJsonObject = elements.getJSONObject(i);
            Element element;
            String type = (String) elementJsonObject.get("type");
            if (type.equals("Book")) {
                element = new Book(elementJsonObject);
                addElement(element);
            }
            if (type.equals("Collection")) {
                element = new Collection(elementJsonObject);
                addElement(element);
            }
        }
    }
    /**
     * Returns the string representation of a collection. 
     * The string representation should have the name of this collection, 
     * and all elements (books/subcollections) contained in this collection.
     *
     * @return string representation of this collection
     */
    public String getStringRepresentation() {
        return toJSONObject().toString();        
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("type", "Collection");
        jsonObject.put("name", this.name);
        for (int i = 0; i < this.elements.size(); i++) {
            if (this.elements.get(i) instanceof Book) {
                Book element = (Book) this.elements.get(i);
                jsonArray.put(element.toJSONObject());
            } 
            if (this.elements.get(i) instanceof Collection) {
                Collection element = (Collection) this.elements.get(i);
                jsonArray.put(element.toJSONObject());
            }
        }
        jsonObject.put("elements", jsonArray);
        return jsonObject;
    }

    /**
     * Returns the name of the collection.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Adds an element to the collection.
     * If parentCollection of given element is not null,
     * do not actually add, but just return false.
     * (explanation: if given element is already a part of  
     * another collection, you should have deleted the element 
     * from that collection before adding to another collection)
     *
     * @param element the element to add
     * @return true on success, false on fail
     */
    public boolean addElement(Element element) {
        if (element.getParentCollection() != null) {
            return false;
        }
        this.elements.add(element);
        element.setParentCollection(this);
        return true;
    }
    
    /**
     * Deletes an element from the collection.
     * Returns false if the collection does not have 
     * this element.
     * Hint: Do not forget to clear parentCollection
     * of given element 
     *
     * @param element the element to remove
     * @return true on success, false on fail
     */
    public boolean deleteElement(Element element) {
        if (!this.elements.contains(element)) {
            return false;
        }
        this.elements.remove(element);
        element.setParentCollection(null);
        return true;
    }
    
    /**｀
     * Returns the list of elements.
     * 
     * @return the list of elements
     */
    public List<Element> getElements() {
        return this.elements;
    }
}
