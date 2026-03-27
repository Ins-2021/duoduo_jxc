const fs = require('fs');
const path = require('path');

const files = [
  'src/views/sales/booking/add.vue',
  'src/views/sales/return/add.vue',
  'src/views/purchase/add.vue',
  'src/views/purchase/booking/add.vue',
  'src/views/purchase/return/add.vue'
];

const basePath = path.join(__dirname, '..');

const templateCategoryOld = `<el-select v-model="addCustomerForm.category" placeholder="请选择客户分类" style="flex: 1;">
                      <el-option label="默认分类" value="default" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link">新增</el-link>`;

const templateCategoryNew = `<el-select v-model="addCustomerForm.category" placeholder="请选择客户分类" style="flex: 1;">
                      <el-option v-for="item in customerCategories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link" @click="handleAddCustomerCategory">新增</el-link>`;

const templateLevelOld = `<el-select v-model="addCustomerForm.level" placeholder="普通客户" style="flex: 1;">
                      <el-option label="普通客户" value="normal" />
                      <el-option label="VIP客户" value="vip" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link">设置</el-link>`;

const templateLevelNew = `<el-select v-model="addCustomerForm.level" placeholder="普通客户" style="flex: 1;">
                      <el-option v-for="item in customerLevels" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-link type="primary" :underline="false" class="inline-link" @click="handleSetCustomerLevel">设置</el-link>`;

const scriptAdditions = `const customerCategories = ref([{ label: '默认分类', value: 'default' }])
const customerLevels = ref([
  { label: '普通客户', value: 'normal' },
  { label: 'VIP客户', value: 'vip' }
])

const handleAddCustomerCategory = () => {
  import('element-plus').then(({ ElMessageBox, ElMessage }) => {
    ElMessageBox.prompt('请输入客户分类名称', '新增客户分类', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      if (value) {
        const newVal = 'cat_' + Date.now()
        customerCategories.value.push({ label: value, value: newVal })
        addCustomerForm.category = newVal
        ElMessage.success('新增成功')
      }
    }).catch(() => {})
  })
}

const handleSetCustomerLevel = () => {
  import('element-plus').then(({ ElMessageBox, ElMessage }) => {
    ElMessageBox.prompt('请输入客户等级名称', '新增客户等级', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      if (value) {
        const newVal = 'lvl_' + Date.now()
        customerLevels.value.push({ label: value, value: newVal })
        addCustomerForm.level = newVal
        ElMessage.success('新增成功')
      }
    }).catch(() => {})
  })
}
`;

files.forEach(file => {
  const filePath = path.join(basePath, file);
  if (!fs.existsSync(filePath)) {
    console.log(`Skip: ${file} not found`);
    return;
  }
  
  let content = fs.readFileSync(filePath, 'utf8');
  
  if (content.includes('handleAddCustomerCategory')) {
    console.log(`Skip: ${file} already modified`);
    return;
  }

  // Replace templates
  content = content.replace(templateCategoryOld, templateCategoryNew);
  content = content.replace(templateLevelOld, templateLevelNew);
  
  // Inject script additions
  const hookStr = 'const addCustomerForm = reactive({';
  if (content.includes(hookStr)) {
    content = content.replace(hookStr, scriptAdditions + '\n' + hookStr);
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Updated: ${file}`);
  } else {
    console.log(`Error: hook not found in ${file}`);
  }
});
