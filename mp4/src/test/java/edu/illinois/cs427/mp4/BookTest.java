package edu.illinois.cs427.mp4;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.*;
import java.util.List;

public class BookTest {
    @Test
    public void testBookConstructor1() {
        String title = "Harry Potter";
        String author = "J. K. Rowling";
        Book book = new Book(title, author);
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
    }

    @Test
    public void testBookConstructor2() {
        String title = "Harry Potter";
        String author = "J. K. Rowling";
        String jsonString = String.format("{type: Book, title: \"%s\", author: \"%s\"}", title, author);
        Book book = new Book(jsonString);
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
    }

    @Test
    public void testBookConstructor3() {
        JSONObject jsonObject = new JSONObject();
        String title = "Harry Potter";
        String author = "J. K. Rowling";
        jsonObject.put("title", title);
        jsonObject.put("author", author);

        Book book = new Book(jsonObject);
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
    }


    @Test
    public void testGetStringRepresentation1() {
        String title = "Harry Potter";
        String author = "J. K. Rowling";
        Book book = new Book(title, author);

        String jsonString = book.getStringRepresentation();
        JSONObject jsonObject = new JSONObject(jsonString);

        assertEquals("Book", jsonObject.get("type"));
        assertEquals(title, jsonObject.get("title"));
        assertEquals(author, jsonObject.get("author"));
    }

    @Test
    public void testGetContainingCollections1() {
        Collection topCollection = new Collection("Computer Science");
        Collection subCollection = new Collection("Operating Systems");
        Book book = new Book("The Linux Kernel", "Linus Benedict Torvalds");
        book.setParentCollection(subCollection);
        subCollection.setParentCollection(topCollection);

        List<Collection> collections = book.getContainingCollections();
        assertEquals(2, collections.size());
        assertTrue(collections.contains(topCollection));
        assertTrue(collections.contains(subCollection));
    }
}
