import { Given, When, Then } from "@cucumber/cucumber";
import { Browser, Page, chromium, expect } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";
import { createHtmlReport } from "axe-html-reporter";
import { AxeResults } from "axe-core";

let accessibilityScanResults: AxeResults;
let page: Page;
let browser: Browser;

Given("I open the web page {string}", async function (url) {
  browser = await chromium.launch({ headless: false });
  const context = await browser.newContext();
  page = await context.newPage();
  await page.goto(url);
});

When("I check the accessibility of the page", async function () {
  accessibilityScanResults = await new AxeBuilder({ page }).analyze();
});

Then("accessibility violations should be found", async function () {
  expect(accessibilityScanResults.violations.length).toBeGreaterThan(0);
});

Then("report is generated", async function () {
  const now = new Date().toISOString().replace(/[-T:]/g, "_").slice(0, -5);
  const filename = `accessibility-scan-results-${now}.html`;
  const axeReportPath = "axeReports";
  console.log("axeReportPath", axeReportPath);

  createHtmlReport({
    results: accessibilityScanResults,
    options: {
      outputDirPath: axeReportPath,
      reportFileName: filename,
    },
  });

  expect(
    accessibilityScanResults.violations.map((v) => v.help),
    `A11y report in ${filename}`
  ).not.toEqual([]);
});
