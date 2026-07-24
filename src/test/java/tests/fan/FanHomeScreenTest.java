package tests.fan;

import org.testng.annotations.Test;
import pages.fan.FanHomePage;

public class FanHomeScreenTest extends BaseFanTest {

    @Test(priority = 1, description = "Fan Home screen: from discover click Home icon, assert /fan/home URL and popcorn logo")
    public void fanHomeScreenValidations() {
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();
        home.assertUsernameBadgeLogo();
    }
}
