import com.deque.html.axecore.args.AxeRuleOptions;
import com.deque.html.axecore.args.AxeRunOnlyOptions;
import com.deque.html.axecore.args.AxeRunOptions;
import com.deque.html.axecore.results.ResultType;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;
import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.deque.html.axecore.extensions.WebDriverExtensions.analyze;
import static com.deque.html.axecore.selenium.AxeReporter.getReadableAxeResults;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AccessibilityTestSteps {
    private WebDriver driver;
    AxeBuilder axeBuilder = new AxeBuilder();
    Results axeResults;
    private static final Logger logger = LoggerFactory.getLogger(AccessibilityTestSteps.class);

    @Before
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

    }

    @After
    public void closeBrowser() {
        driver.quit();
    }

    @Given("I open the web page {string}")
    public void i_open_the_web_page(String url) {
        driver.get(url);
    }

    @When("I check the accessibility of the page")
    public void i_check_the_accessibility_of_the_page() {
        AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
        runOnlyOptions.setType("tag");
        runOnlyOptions.setValues(Arrays.asList("wcag2a", "wcag2aa"));



        AxeRunOptions options = new AxeRunOptions();
        options.setRunOnly(runOnlyOptions);

        List<String> axeRules = Arrays.asList("html-lang", "image-alt", "color-contrast");
        List<String> disabledRules = Arrays.asList("color-contrast");

        List<String> axeTags = Arrays.asList("wcag2a");

        axeResults = axeBuilder.analyze(driver);
    }

    @Then("accessibility violations should be found")
    public void accessibility_violations_should_be_found() {
        assertFalse("Accessibility violations found", axeResults.getViolations().isEmpty());
    }

    @And("a report is generated")
    public void report_is_generated() {
        List<Rule> violations = axeResults.getViolations();
        if (!violations.isEmpty()) {
            logger.info(() -> "Violations: " + violations);
            String AxeReportPath = System.getProperty("user.dir") + File.separator + "axeReports" + File.separator;
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
            String AxeViolationReportPath = AxeReportPath + "AccessibilityViolations_" + timeStamp;

            assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), driver, violations));
            AxeReporter.writeResultsToTextFile(AxeViolationReportPath, AxeReporter.getAxeResultString());
        } else {
            logger.warn(() -> "No Accessibility violations found");
        }
    }
}