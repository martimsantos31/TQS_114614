# 3
## b)

The selectors used in the tests include XPath and CSS Selectors. There are instances of XPath, such as:

```java
WebElement searchBar = driver.findElement(By.xpath("//input[@data-testid='book-search-input']"));
WebElement searchButton = driver.findElement(By.xpath("//button[contains(@class, 'Navbar_searchBtn')]"));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-testid='book-search-item']")));
List<WebElement> books = driver.findElements(By.xpath("//span[contains(@class, 'SearchList_bookTitle')]"));
```

The use of XPath can be less robust if the page structure changes. Identifier-based locators, such as `id` or `data-testid`, tend to be more stable.

Alternatively, CSS Selectors are more readable and efficient, such as:

```java
WebElement searchBar = driver.findElement(By.cssSelector("input[data-testid='book-search-input']"));
WebElement searchButton = driver.findElement(By.cssSelector("button.Navbar_searchBtn"));
```

More robust strategies include:
- `By.id("element-id")` (if available, this is the best option)
- `By.name("element-name")`
- `By.cssSelector("selector")` (more readable and stable than XPath)
