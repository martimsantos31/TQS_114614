Feature: Library book search

  Scenario: Search for books by author
    Given a library with the following books:
    | title            | author         | published  |
    | Clean Code      | Robert Martin  | 2008-08-01 |
    | The Pragmatic Programmer | Andrew Hunt | 1999-10-30 |
    | Code Complete   | Steve McConnell | 2004-06-09 |
    When I search for books by author "Robert Martin"
    Then I should find 1 book with title "Clean Code"

  Scenario: Search for books by publication date range
    Given a library with the following books:
    | title            | author         | published  |
    | Clean Code      | Robert Martin  | 2008-08-01 |
    | The Pragmatic Programmer | Andrew Hunt | 1999-10-30 |
    | Code Complete   | Steve McConnell | 2004-06-09 |
    When I search for books published between "2000-01-01" and "2010-01-01"
    Then I should find 2 books