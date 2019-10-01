package edu.illinois.cs427.mp4;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

public class CollectionTest {
    @Test
    public void testRestoreCollection1() {
        // Construct string representation
        String name = "Computer Science";
        String elementString1 = "{type: Collection, name: Operating Systems, elements: []}";
        String elementString2 = "{type: Book, title: Introduction to Computer Science, author: David Malan}";
        String jsonString = String.format("{type: Collection, name: %s, elements: [%s, %s]}", name, elementString1, elementString2);

        // Restore a collection from this string representation
        Collection collection = Collection.restoreCollection(jsonString);

        // Test if the restoring collection is corrects
        assertEquals(name, collection.getName());
        assertEquals(2, collection.getElements().size());
        // First element is a collection
        Collection element1 = (Collection) collection.getElements().get(0);
        assertEquals("Operating Systems", element1.getName());
        // Second element is a book
        Book element2 = (Book) collection.getElements().get(1);
        assertEquals("Introduction to Computer Science", element2.getTitle());
        assertEquals("David Malan", element2.getAuthor());
    }

    @Test
    public void testGetStringRepresentation1() {
        String name = "Computer Science";
        Collection collection = new Collection(name);
        
        String subName = "Operating Systems";
        Collection subCollection = new Collection(subName);
        
        String title = "Introduction to Computer Science";
        String author = "David Malan";
        Book book = new Book(title, author);
        
        collection.addElement(subCollection);
        collection.addElement(book);

        // Get string representation of this collection
        String jsonString = collection.getStringRepresentation();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray elements = (JSONArray) jsonObject.get("elements");

        // Check collection type and name
        assertEquals("Collection", jsonObject.get("type"));
        assertEquals(name, jsonObject.get("name"));

        // Check elements
        assertEquals(2, elements.length());
        assertEquals(subName, elements.getJSONObject(0).get("name"));
        assertEquals(title, elements.getJSONObject(1).get("title"));
        assertEquals(author, elements.getJSONObject(1).get("author"));
    }

    @Test
    public void testAddElement1() {
        Collection collection = new Collection("Computer Science");
        Collection subCollection1 = new Collection("Operating Systems");
        Collection subCollection2 = new Collection("Programming Langauge");
        
        assertTrue(collection.addElement(subCollection1));
        assertTrue(collection.addElement(subCollection2));
        List<Element> elements = collection.getElements();

        // Check the size and if it contains the elements
        assertEquals(2, elements.size());
        assertTrue(elements.contains(subCollection1));
        assertTrue(elements.contains(subCollection2));

        // Check if given element is already a part of another collection
        assertFalse(collection.addElement(subCollection1));
    }

    @Test
    public void testDeleteElement1() {
        Collection collection = new Collection("Computer Science");
        Collection subCollection1 = new Collection("Operating Systems");
        Collection subCollection2 = new Collection("Programming Langauge");
        
        collection.addElement(subCollection1);
        collection.addElement(subCollection2);
        List<Element> elements = collection.getElements();

        // Check the size before deleting the elements
        assertEquals(2, elements.size());
        
        // Check the size after deleting the elements
        assertTrue(collection.deleteElement(subCollection1));
        assertEquals(1, elements.size());

        // Check if the collection does not have the element
        assertFalse(collection.deleteElement(subCollection1));
    }
}
