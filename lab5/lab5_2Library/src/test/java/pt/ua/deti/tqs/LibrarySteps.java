package pt.ua.deti.tqs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LibrarySteps {
    private Library library;
    private List<Book> searchResults;

    @Given("a library with the following books:")
    public void a_library_with_the_following_books(DataTable table) {
        library = new Library();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String title = row.get("title");
            String author = row.get("author");
            LocalDateTime published = LocalDate.parse(row.get("published")).atStartOfDay();
            library.addBook(new Book(title, author, published));
        }
    }

    @When("I search for books by author {string}")
    public void i_search_for_books_by_author(String author) {
        searchResults = library.findBooksByAuthor(author);
    }

    @Then("I should find {int} book with title {string}")
    public void i_should_find_book_with_title(int expectedCount, String expectedTitle) {
        assertEquals(expectedCount, searchResults.size());
        assertEquals(expectedTitle, searchResults.get(0).getTitle());
    }

    @When("I search for books published between {string} and {string}")
    public void i_search_for_books_published_between(String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atStartOfDay();
        searchResults = library.findBooks(start, end);
    }

    @Then("I should find {int} books")
    public void i_should_find_books(int expectedCount) {
        assertEquals(expectedCount, searchResults.size());
    }
}