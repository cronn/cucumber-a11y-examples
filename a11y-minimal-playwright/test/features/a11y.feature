Feature: Accessibility Testing

  Scenario: Verify accessibility of a web page
    Given I open the web page "https://www.cronn.de/"
    When I check the accessibility of the page
    Then accessibility violations should be found
    And report is generated