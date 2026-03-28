const fs = require('fs');
const path = require('path');

const viewsDir = path.join(__dirname, '../frontend/src/views');

function scanVueFiles(dir, fileList = []) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    if (fs.statSync(fullPath).isDirectory()) {
      scanVueFiles(fullPath, fileList);
    } else if (fullPath.endsWith('.vue')) {
      fileList.push(fullPath);
    }
  }
  return fileList;
}

const vueFiles = scanVueFiles(viewsDir);
const buttons = {};

vueFiles.forEach(file => {
  const content = fs.readFileSync(file, 'utf-8');
  // Simple regex to extract perm and the text until the next <
  const regex = /v-perm\s*=\s*['"](?:\[['"])?([^'"\]]+)(?:['"]\])?['"][^>]*>\s*([^<]+)\s*</g;
  let match;
  while ((match = regex.exec(content)) !== null) {
    const perm = match[1];
    const name = match[2].trim();
    if (name && !name.includes('{{')) {
      buttons[perm] = name;
    }
  }
  // also check for text inside {{}} or icon only? 
  // For simplicity, just manual map if missed.
});

console.log(JSON.stringify(buttons, null, 2));