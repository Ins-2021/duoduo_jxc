const fs = require('fs');
const path = require('path');

const files = [
  'src/views/purchase/return/add.vue',
  'src/views/purchase/booking/add.vue',
  'src/views/purchase/add.vue',
  'src/views/sales/booking/add.vue',
  'src/views/sales/order/add.vue'
];

const basePath = path.join(__dirname, '..');

files.forEach(file => {
  const filePath = path.join(basePath, file);
  if (!fs.existsSync(filePath)) {
    console.log(`Skip: ${file} not found`);
    return;
  }
  
  let content = fs.readFileSync(filePath, 'utf8');
  
  if (content.includes('handleRefresh')) {
    console.log(`Skip: ${file} already modified`);
    return;
  }

  // 1. Replace button template
  content = content.replace('<el-button>刷新</el-button>', '<el-button @click="handleRefresh">刷新</el-button>');
  
  // 2. Inject script
  const scriptMatch = content.match(/<script setup lang="ts">\n(import .+?\n)+/m);
  if (scriptMatch) {
    const hookStr = scriptMatch[0];
    const newHook = hookStr + `\nconst handleRefresh = () => {\n  window.location.reload()\n}\n`;
    content = content.replace(hookStr, newHook);
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Updated: ${file}`);
  } else {
    console.log(`Error: script setup not found in ${file}`);
  }
});
