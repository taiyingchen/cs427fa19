package edu.illinois.cs427.mp4;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;

public class LibraryTest {
    @Test
    public void testLibraryConstructorFromFile1() throws IOException {
        testSaveLibraryToFile1();

        String fileName = "testLibrary.json";
        Library library = new Library(fileName);

        String name = "Computer Science";        
        String subName = "Operating Systems";
        String title = "Introduction to Computer Science";
        String author = "David Malan";

        List<Collection> collections = library.getCollections();
        assertEquals(name, collections.get(0).getName());
        assertEquals(subName, ((Collection) collections.get(0).getElements().get(0)).getName());
        assertEquals(title, ((Book) collections.get(0).getElements().get(1)).getTitle());
        assertEquals(author, ((Book) collections.get(0).getElements().get(1)).getAuthor());
    }

    @Test
    public void testSaveLibraryToFile1() throws IOException {
        Library library1 = new Library();

        String name = "Computer Science";
        Collection collection = new Collection(name);
        
        String subName = "Operating Systems";
        Collection subCollection = new Collection(subName);
        
        String title = "Introduction to Computer Science";
        String author = "David Malan";
        Book book = new Book(title, author);
        
        collection.addElement(subCollection);
        collection.addElement(book);
        library1.addCollection(collection);

        String fileName = "testLibrary.json";
        library1.saveLibraryToFile(fileName);

        Library library2 = new Library(fileName);
        List<Collection> collections = library2.getCollections();
        assertEquals(name, collections.get(0).getName());
        assertEquals(subName, ((Collection) collections.get(0).getElements().get(0)).getName());
        assertEquals(title, ((Book) collections.get(0).getElements().get(1)).getTitle());
        assertEquals(author, ((Book) collections.get(0).getElements().get(1)).getAuthor());
    }
}
