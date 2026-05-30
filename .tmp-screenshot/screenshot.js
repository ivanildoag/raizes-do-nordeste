const puppeteer = require('puppeteer');
const fs = require('fs');

(async () => {
  try {
    const browser = await puppeteer.launch({
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    const page = await browser.newPage();
    
    // Set viewport to a nice desktop size
    await page.setViewport({ width: 1280, height: 800 });

    // Navigate to Swagger UI
    await page.goto('http://localhost:8080/swagger-ui/index.html', {
      waitUntil: 'networkidle0',
      timeout: 30000
    });

    // Wait a bit extra for swagger rendering just in case
    await new Promise(r => setTimeout(r, 2000));

    // Save screenshot
    await page.screenshot({ path: '../docs/SwaggerUI.png' });

    await browser.close();
    console.log("Screenshot saved!");
  } catch (err) {
    console.error("Error taking screenshot:", err);
    process.exit(1);
  }
})();
