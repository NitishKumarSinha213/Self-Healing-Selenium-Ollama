AI Self-Healing Selenium Framework : 
This framework eliminates "Flaky Tests" caused by UI changes (ID changes, renamed classes, or modified XPaths) by using a local Large Language Model to "heal" broken selectors on the fly.

Demo link : https://drive.google.com/file/d/1kXS2NNwKNo6cRJP3RTl3Ow3YZMQXk8YC/view?usp=drive_link

Key Features : 
1. AI-Powered Recovery: Uses LangChain4j to interface with Ollama (Qwen2.5) for high-accuracy element matching.
2. Smart HTML Filtering: Uses Jsoup to strip unnecessary noise from the DOM, sending only interactive elements to the LLM to save tokens and improve speed.
3. Persistent Healing Cache: Healed locators are stored in a healing_registry.json file. Subsequent runs use the cached locator, ensuring native Selenium speeds (0ms overhead) after the first fix.
4. Zero-Cloud Dependency: Runs entirely on your local machine via Ollama, ensuring your DOM structure and data stay private.

Tech Stack : 
Language: Java 21

Automation: Selenium WebDriver

AI Orchestration: LangChain4j

Local LLM: Ollama (Model: qwen2.5:7b)

Logging: SLF4J + SimpleLogger

How It Works ? 
1. Detection: A standard findElement call fails with a NoSuchElementException.
2. Context Gathering: The framework scrapes the current page for interactive elements (buttons, inputs, links).
3. LLM Reasoning: The broken locator and the page snippet are sent to the local LLM with a strict prompt to identify the new XPath.
4. Healing: The test continues using the AI-generated path.
5. Memorization: The fix is saved to a JSON registry for future use.

Prerequisites : 
1. Ollama installed and running.
2. The model downloaded: ollama run qwen2.5:7b
3. Maven installed.

Setup : 
1. Clone the repository.
2. Ensure your baseUrl in SelfHealingLogic.java points to your Ollama instance (default: http://localhost:11434).
3. Run tests using mvn test.

Performance Metrics :
The framework is designed to minimize latency. By using a local JSON cache, we ensure that the AI overhead is only paid once per broken locator.

Execution Mode,Logic Path,Avg. Latency Overhead
Standard Run,Direct Selenium Find,0ms (Baseline)
Healed Run (Cold),LLM Analysis + XPath Generation,1.5s - 3.5s
Healed Run (Warm),JSON Registry Lookup,~2ms
