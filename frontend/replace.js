const fs = require('fs');
const path = require('path');

const directoryPath = path.join(__dirname, 'src', 'app');

const replacements = [
  { regex: /bg-gradient-surface/g, replace: 'bg-surface' },
  { regex: /shadow-\[0_0_10px_rgba\([^)]+\)\]/g, replace: 'shadow-subtle' },
  { regex: /text-shadow:\s*0\s*0\s*15px\s*rgba\([^)]+\)/g, replace: '' }
];

function processDirectory(dirPath) {
  const files = fs.readdirSync(dirPath);

  for (const file of files) {
    const fullPath = path.join(dirPath, file);
    if (fs.statSync(fullPath).isDirectory()) {
      processDirectory(fullPath);
    } else if (fullPath.endsWith('.html')) {
      let content = fs.readFileSync(fullPath, 'utf8');
      let originalContent = content;
      
      for (const rule of replacements) {
        content = content.replace(rule.regex, rule.replace);
      }
      
      if (content !== originalContent) {
        fs.writeFileSync(fullPath, content, 'utf8');
        console.log(`Updated: ${fullPath}`);
      }
    }
  }
}

processDirectory(directoryPath);
console.log('Replacement done.');
