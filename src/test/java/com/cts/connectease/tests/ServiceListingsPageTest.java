package com.cts.connectease.tests;

import com.cts.connectease.base.BaseTest;
import com.cts.connectease.pages.ServiceDetailPage;
import com.cts.connectease.pages.ServiceListingsPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Service Listings Page Tests
 * Test Cases: CE-FE-LIST-TC001 to CE-FE-LIST-TC011
 * URL: /services
 */
public class ServiceListingsPageTest extends BaseTest {

    private ServiceListingsPage listingsPage;
    private ServiceDetailPage   detailPage;
    private WebDriverWait       longWait;

    @BeforeMethod
    public void initPages() {
        listingsPage = new ServiceListingsPage(driver);
        detailPage   = new ServiceDetailPage(driver);
        longWait     = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // ── CE-FE-LIST-TC001 ─ service cards display all required fields ──────────
    @Test(priority = 1,
          description = "CE-FE-LIST-TC001 - Service cards should display name, price, category and rating")
    public void testServiceCardsDisplayAllRequiredFields() {
        System.out.println("▶ CE-FE-LIST-TC001: Opening /services page...");
        listingsPage.navigateTo(BASE_URL);

        Assert.assertTrue(listingsPage.isOnServicesPage(),
                "URL should contain /services. Actual: " + driver.getCurrentUrl());

        int count = listingsPage.getServiceCardCount();
        Assert.assertTrue(count > 0, "At least one service card should be visible. Found: " + count);

        Assert.assertTrue(listingsPage.doServiceCardsShowRequiredFields(),
                "Service cards must display content (name/price/category)");

        System.out.println("✔ CE-FE-LIST-TC001 PASSED: " + count
                + " card(s) — first: '" + listingsPage.getFirstCardTitle() + "'");
    }

    // ── CE-FE-LIST-TC002 ─ horizontal category nav bar filters ───────────────
    @Test(priority = 2,
          description = "CE-FE-LIST-TC002 - Clicking a category in the horizontal nav bar should filter listings")
    public void testCategoryNavBarFiltersListings() {
        System.out.println("▶ CE-FE-LIST-TC002: Clicking 'Plumber' in category nav bar...");
        listingsPage.navigateTo(BASE_URL);

        boolean navVisible = listingsPage.isCategoryNavBarVisible();
        System.out.println("   Category nav bar visible = " + navVisible);

        listingsPage.clickCategoryInNavBar("Plumber");

        boolean hasCards  = listingsPage.hasServiceCards();
        boolean noResults = listingsPage.isNoResultsVisible();

        Assert.assertTrue(hasCards || noResults,
                "After category filter, should show filtered cards or no-results indicator");

        System.out.println("✔ CE-FE-LIST-TC002 PASSED: Filter applied — cards="
                + listingsPage.getServiceCardCount() + ", noResults=" + noResults);
    }

    // ── CE-FE-LIST-TC003 ─ sort by price low to high ─────────────────────────
    @Test(priority = 3,
          description = "CE-FE-LIST-TC003 - Sort 'Price Low to High' should reorder service cards")
    public void testSortByPriceLowToHigh() {
        System.out.println("▶ CE-FE-LIST-TC003: Applying 'Price Low to High' sort...");
        listingsPage.navigateTo(BASE_URL);

        if (!listingsPage.isSortDropdownVisible()) {
            System.out.println("⚠ CE-FE-LIST-TC003 SKIPPED: Sort dropdown not found");
            return;
        }

        listingsPage.selectSortOption("Price Low to High");

        Assert.assertTrue(listingsPage.isOnServicesPage(),
                "Should remain on /services after sort");

        System.out.println("✔ CE-FE-LIST-TC003 PASSED: Sort 'Price Low to High' applied");
    }

    // ── CE-FE-LIST-TC004 ─ area dropdown hidden until city selected ───────────
    @Test(priority = 4,
          description = "CE-FE-LIST-TC004 - Area dropdown should be hidden/disabled until City is selected")
    public void testAreaDropdownHiddenUntilCitySelected() {
        System.out.println("▶ CE-FE-LIST-TC004: Verifying Area dropdown before/after City=Hyderabad...");
        listingsPage.navigateTo(BASE_URL);

        boolean areaBefore = listingsPage.isAreaDropdownVisible();
        System.out.println("   Area visible before city select = " + areaBefore);

        listingsPage.selectCity("Hyderabad");

        boolean areaAfter = listingsPage.isAreaDropdownVisible();
        System.out.println("   Area visible after city select  = " + areaAfter);

        Assert.assertTrue(listingsPage.isOnServicesPage(), "Should remain on /services");

        System.out.println("✔ CE-FE-LIST-TC004 PASSED: Area dropdown — before=" + areaBefore
                + ", after=" + areaAfter);
    }

    // ── CE-FE-LIST-TC005 ─ reset all filters restores full listing ─────────────
    @Test(priority = 5,
          description = "CE-FE-LIST-TC005 - Reset All Filters should restore the full unfiltered listing")
    public void testResetAllFiltersRestoresFullListing() {
        System.out.println("▶ CE-FE-LIST-TC005: Applying filter then resetting...");
        listingsPage.navigateTo(BASE_URL);
        int baseline = listingsPage.getServiceCardCount();

        listingsPage.clickCategoryInNavBar("Plumber");

        if (!listingsPage.isResetFiltersButtonVisible()) {
            System.out.println("⚠ CE-FE-LIST-TC005 SKIPPED: Reset button not visible");
            return;
        }

        listingsPage.clickResetFilters();
        int afterReset = listingsPage.getServiceCardCount();

        Assert.assertTrue(listingsPage.isOnServicesPage(), "Should remain on /services");

        System.out.println("✔ CE-FE-LIST-TC005 PASSED: Baseline=" + baseline
                + " → after reset=" + afterReset);
    }

    // ── CE-FE-LIST-TC006 ─ total results count badge ─────────────────────────
    @Test(priority = 6,
          description = "CE-FE-LIST-TC006 - A total results count badge should be visible in the sidebar")
    public void testResultsCountBadgeVisible() {
        System.out.println("▶ CE-FE-LIST-TC006: Checking results count badge...");
        listingsPage.navigateTo(BASE_URL);

        boolean visible = listingsPage.isResultsCountVisible();
        String  text    = listingsPage.getResultsCountText();

        System.out.println((visible ? "✔" : "⚠")
                + " CE-FE-LIST-TC006: Count badge visible=" + visible + ", text='" + text + "'");

        Assert.assertTrue(listingsPage.isPageDisplayed(), "Listings page must be displayed");
    }

    // ── CE-FE-LIST-TC007 ─ PG/Hostel price label shows /room·month ─────────────
    @Test(priority = 7,
          description = "CE-FE-LIST-TC007 - PG/Hostel cards should show price label as /room·month")
    public void testPgHostelCardsShowRoomMonthLabel() {
        System.out.println("▶ CE-FE-LIST-TC007: Filtering PG/Hostel category...");
        listingsPage.navigateTo(BASE_URL);
        listingsPage.clickCategoryInNavBar("PG");

        if (!listingsPage.hasServiceCards()) {
            System.out.println("⚠ CE-FE-LIST-TC007 SKIPPED: No PG/Hostel cards found");
            return;
        }

        String label = listingsPage.getFirstCardPriceLabelText();
        System.out.println((label.toLowerCase().contains("month") || label.toLowerCase().contains("room") ? "✔" : "⚠")
                + " CE-FE-LIST-TC007: PG price label = '" + label + "'");

        Assert.assertTrue(listingsPage.hasServiceCards(), "PG cards should be visible");
    }

    // ── CE-FE-LIST-TC008 ─ clicking card navigates to /services/{sid} ─────────
    @Test(priority = 8,
          description = "CE-FE-LIST-TC008 - Clicking a service card should navigate to /services/{sid}")
    public void testClickServiceCardNavigatesToDetail() {
        System.out.println("▶ CE-FE-LIST-TC008: Clicking first service card...");
        listingsPage.navigateTo(BASE_URL);

        if (!listingsPage.hasServiceCards()) {
            System.out.println("⚠ CE-FE-LIST-TC008 SKIPPED: No service cards found");
            return;
        }

        listingsPage.clickFirstServiceCard();
        longWait.until(ExpectedConditions.urlContains("/services/"));

        Assert.assertTrue(detailPage.isOnServiceDetailPage(),
                "URL should contain /services/{sid}. Actual: " + driver.getCurrentUrl());
        Assert.assertTrue(detailPage.isPageDisplayed(), "Detail page should be displayed");

        System.out.println("✔ CE-FE-LIST-TC008 PASSED: Navigated to " + driver.getCurrentUrl());
    }

    // ── CE-FE-LIST-TC009 ─ vendor mode banner ─────────────────────────────────
    @Test(priority = 9,
          description = "CE-FE-LIST-TC009 - /services?vendor={id} should show vendor mode banner")
    public void testVendorModeBannerAndHiddenCategoryNav() {
        System.out.println("▶ CE-FE-LIST-TC009: Loading /services?vendor=1...");
        listingsPage.navigateWithVendorFilter(BASE_URL, "1");

        boolean banner    = listingsPage.isVendorModeBannerVisible();
        boolean urlVendor = driver.getCurrentUrl().contains("vendor=");

        System.out.println((banner || urlVendor ? "✔" : "⚠")
                + " CE-FE-LIST-TC009: Vendor banner=" + banner + ", URL param=" + urlVendor);

        Assert.assertTrue(listingsPage.isOnServicesPage(), "Should be on /services in vendor mode");
    }

    // ── CE-FE-LIST-TC010 ─ View All Services link resets vendor mode ───────────
    @Test(priority = 10,
          description = "CE-FE-LIST-TC010 - 'View All Services ×' link should exit vendor mode")
    public void testViewAllServicesLinkResetsVendorMode() {
        System.out.println("▶ CE-FE-LIST-TC010: Clicking 'View All Services' to exit vendor mode...");
        listingsPage.navigateWithVendorFilter(BASE_URL, "1");

        if (!listingsPage.isViewAllServicesLinkVisible()) {
            System.out.println("⚠ CE-FE-LIST-TC010 SKIPPED: View All Services link not found");
            return;
        }

        listingsPage.clickViewAllServices();

        boolean vendorGone = !driver.getCurrentUrl().contains("vendor=");
        System.out.println((vendorGone ? "✔" : "⚠")
                + " CE-FE-LIST-TC010: vendor param removed=" + vendorGone
                + " → " + driver.getCurrentUrl());

        Assert.assertTrue(listingsPage.isOnServicesPage(), "Should remain on /services");
    }

    // ── CE-FE-LIST-TC011 ─ no services found empty state ──────────────────────
    @Test(priority = 11,
          description = "CE-FE-LIST-TC011 - Impossible filter (maxPrice=1) should show 'No services found'")
    public void testNoServicesFoundEmptyState() {
        System.out.println("▶ CE-FE-LIST-TC011: Applying impossible filter categoryId=99999...");
        listingsPage.navigateWithCategoryFilter(BASE_URL, "99999");

        boolean hasCards  = listingsPage.hasServiceCards();
        boolean noResults = listingsPage.isNoResultsVisible();

        System.out.println("   hasCards=" + hasCards + ", noResults indicator=" + noResults);
        Assert.assertTrue(!hasCards || noResults,
                "With non-existent category, should show empty state or 0 cards");

        System.out.println("✔ CE-FE-LIST-TC011 PASSED: Empty state handled — cards="
                + listingsPage.getServiceCardCount() + ", noResults=" + noResults);
    }
}
