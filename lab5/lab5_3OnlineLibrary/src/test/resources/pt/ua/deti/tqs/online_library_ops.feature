Feature: Online Library Search

  Scenario: Search for a book by title
    Given I am on the library homepage
    When I search for "Harry Potter"
    Then I should see the book "Harry Potter and the Sorcerer's Stone" in the results

  Scenario: Search for a book by author
    Given I am on the library homepage
    When I search for books by author "Rick Riordan"
    Then I should see 2 books by "Rick Riordan" in the results
