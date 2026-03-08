package PageObjects;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelfHealingLogic {
    static {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.SEVERE);
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
    }

    public static WebElement findElementByHealing(WebDriver driver, By byPath){
        try{
            System.out.println("Searching original element : " + byPath);
            return driver.findElement(byPath);
        }
        catch (NoSuchElementException e){
            System.out.println("Element was not found using " + byPath + " ... Initiating self Healing");

            // 1. Check if we already healed this before
            String cachedPath = HealingRegistry.getHealedPath(byPath.toString());
            if (cachedPath != null) {
                System.out.println("⚡ Using cached healing: " + cachedPath);
                return driver.findElement(By.xpath(cachedPath));
            }

            String pageContent = driver.getPageSource();
            String elementPathNew = findElementByHealingViaLLM(byPath.toString(), pageContent);

            if (!elementPathNew.isEmpty()) {
                HealingRegistry.saveHealing(byPath.toString(), elementPathNew);
                return driver.findElement(By.xpath(elementPathNew));
            }

            System.out.println("New path found : " + elementPathNew);
            return driver.findElement(By.xpath(elementPathNew));
        }
    }

    public static String getFilteredHtml(String html) {
        StringBuilder candidates = new StringBuilder();
        Elements elements = Jsoup.parse(html).select("button, span, a, input, div");

        for (Element el : elements) {
            // Only include elements that have text or important attributes
            if (!el.text().isEmpty() || el.hasAttr("aria-label") || el.hasAttr("id")) {
                candidates.append(el.outerHtml()).append("\n");
            }
        }
        return candidates.toString();
    }
    public static String findElementByHealingViaLLM(String originalSelectorPath, String pageContentFull){
        String allInteractiveElements = getFilteredHtml(pageContentFull);

        OllamaChatModel myHealer = OllamaChatModel.builder()
                .temperature(0.0)
                .baseUrl("http://localhost:11434")
                .modelName("qwen2.5:7b")
                .numCtx(4096)
                .build();

        // Use a strict instruction format
        String prompt = String.format(
                "### CONTEXT\n" +
                        "A Selenium test failed to find an element using: %s\n\n" +
                        "### HTML SNIPPET\n" +
                        "%s\n\n" +
                        "### TASK\n" +
                        "Find the element in the HTML that matches the INTENT of the broken selector (e.g., look for similar text like 'Dismiss' if the original was 'DDiismiss').\n" +
                        "Return ONLY the most specific xpath selector for that element.\n" +
                        "Example Output: //button[@aria-label='Close Welcome Banner'] or //span[contains(text(), 'Dismiss')] etc.\n\n" +
                        "Be very sure about understanding the context of the element. If a login button failed originally, you should look" +
                        "for alternatives for login button and not for other fields on the page like password or username. " +
                        "xpath Selector: \n\n" +
                        "System: You are a Selenium Automation Engineer.\n" +
                        "User: The locator //butttton[@type='submit'] failed. Based on this HTML: [SNIPPET], identify the correct XPath.\n" +
                        "Constraint: Return ONLY the XPath string. No explanation. No backticks.",
                originalSelectorPath, allInteractiveElements
        );

        // If using LangChain4j, use the chat method
        String healedElement = myHealer.generate(prompt);

        System.out.println("The healed element is : " + healedElement);

        // Safety trim: Remove any unintended whitespace or mark down backticks
        return cleanResponse(healedElement);
    }

    private static String cleanResponse(String raw) {
        return raw.replace("xpath Selector:", "")
                .replace("`", "")
                .split("\n")[0] // Take only the first line
                .trim();
    }


}
