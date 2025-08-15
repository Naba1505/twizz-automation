package listeners;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class AllureAttachments {
    private AllureAttachments() {}

    public static void attachScreenshot(Page page, String name) {
        try {
            byte[] bytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(name != null ? name : "screenshot", new ByteArrayInputStream(bytes));
        } catch (Throwable ignored) {}
    }

    public static void attachHtml(Page page, String name) {
        try {
            String content = page.content();
            Allure.addAttachment(name != null ? name : "page.html",
                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Throwable ignored) {}
    }

    public static void attachText(String name, String text) {
        try {
            Allure.addAttachment(name != null ? name : "details",
                    new ByteArrayInputStream((text == null ? "" : text).getBytes(StandardCharsets.UTF_8)));
        } catch (Throwable ignored) {}
    }
}
