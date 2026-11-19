<template>
  <div class="category-page">
    <el-card shadow="never">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" :icon="Plus" @click="handleAdd(null)">新增分类</el-button>
          <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        </div>
        <div class="toolbar-right">
          <el-input
            v-model="filterText"
            placeholder="搜索名称 / 编码"
            :prefix-icon="Search"
            clearable
            style="width: 220px"
          />
        </div>
      </div>

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="filteredTree"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="code" label="分类编码" min-width="150" show-overflow-tooltip />
        <el-table-column label="属性模板" min-width="160" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.attributeTemplate && row.attributeTemplate.length" type="success" size="small">
              {{ row.attributeTemplate.length }} 个属性
            </el-tag>
            <el-tag v-else type="info" size="small">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" min-width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="op-btns">
              <el-button link type="primary" :icon="Plus" @click="handleAdd(row)">下级</el-button>
              <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            default-expand-all
            placeholder="不选则为顶级分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类编码" prop="code">
              <el-input v-model="form.code" placeholder="唯一编码，如 PHONE-MATE" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="分类说明">
          <el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" show-word-limit />
        </el-form-item>

        <!-- 属性模板编辑 -->
        <el-form-item label="属性模板">
          <div class="attr-template">
            <div class="attr-toolbar">
              <span class="attr-tip">定义该分类产品的差异属性，产品录入时按此模板填写</span>
              <el-button size="small" type="primary" :icon="Plus" @click="addAttr">添加属性</el-button>
            </div>

            <div v-if="form.attributeTemplate.length === 0" class="attr-empty">
              暂无属性，可点击「添加属性」定义
            </div>

            <template v-for="(attr, idx) in form.attributeTemplate" :key="idx">
              <div class="attr-row">
                <el-input v-model="attr.code" placeholder="编码(英文)" class="attr-code" />
                <el-input v-model="attr.name" placeholder="名称" class="attr-name" />
                <el-select v-model="attr.type" placeholder="类型" class="attr-type">
                  <el-option label="数字" value="number" />
                  <el-option label="整数" value="integer" />
                  <el-option label="文本" value="string" />
                  <el-option label="布尔" value="boolean" />
                  <el-option label="枚举" value="enum" />
                </el-select>
                <el-input v-model="attr.unit" placeholder="单位" class="attr-unit" />
                <el-checkbox v-model="attr.required" class="attr-required">必填</el-checkbox>
                <el-button link type="danger" :icon="Delete" @click="removeAttr(idx)" />
              </div>
              <!-- 枚举选项编辑 -->
              <div v-if="attr.type === 'enum'" class="attr-options">
                <span class="attr-options-label">选项（逗号分隔）：</span>
                <el-input
                  :model-value="(attr.options || []).join(',')"
                  placeholder="如 256GB,512GB,1TB"
                  class="attr-options-input"
                  @update:model-value="(val) => (attr.options = val.split(',').map((s) => s.trim()).filter(Boolean))"
                />
              </div>
            </template>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, Edit, Delete } from '@element-plus/icons-vue'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '../../api/product'

const loading = ref(false)
const submitting = ref(false)
const filterText = ref('')
const treeData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const editingId = ref(null)

const form = reactive({
  parentId: null,
  name: '',
  code: '',
  sortOrder: 0,
  status: 1,
  description: '',
  attributeTemplate: []
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
}

const parentOptions = computed(() => [
  { id: null, name: '顶级分类', children: treeData.value }
])

// 按关键字过滤树：自身命中或子级命中的节点保留
const filteredTree = computed(() => {
  if (!filterText.value) return treeData.value
  const kw = filterText.value.trim().toLowerCase()
  const filterNode = (nodes) =>
    nodes
      .map((node) => {
        const children = node.children ? filterNode(node.children) : []
        const selfMatch =
          node.name?.toLowerCase().includes(kw) || node.code?.toLowerCase().includes(kw)
        if (selfMatch || children.length) return { ...node, children }
        return null
      })
      .filter(Boolean)
  return filterNode(treeData.value)
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getCategoryTree()
    treeData.value = Array.isArray(data) ? data : data?.list || []
  } catch (e) {
    treeData.value = []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.parentId = null
  form.name = ''
  form.code = ''
  form.sortOrder = 0
  form.status = 1
  form.description = ''
  form.attributeTemplate = []
  editingId.value = null
  formRef.value?.clearValidate()
}

const addAttr = () => {
  form.attributeTemplate.push({
    code: '',
    name: '',
    type: 'string',
    unit: '',
    required: false,
    options: []
  })
}

const removeAttr = (idx) => {
  form.attributeTemplate.splice(idx, 1)
}

const handleAdd = (parent) => {
  dialogTitle.value = parent ? `新增下级分类（上级：${parent.name}）` : '新增分类'
  resetForm()
  form.parentId = parent ? parent.id : null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  resetForm()
  editingId.value = row.id
  form.parentId = row.parentId ?? null
  form.name = row.name
  form.code = row.code
  form.sortOrder = row.sortOrder ?? 0
  form.status = row.status
  form.description = row.description ?? ''
  form.attributeTemplate = (row.attributeTemplate || []).map((a) => ({ ...a }))
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    // 校验属性模板：编码和名称必填
    for (const attr of form.attributeTemplate) {
      if (!attr.code || !attr.name) {
        ElMessage.warning('属性模板的「编码」和「名称」不能为空')
        return
      }
    }
    submitting.value = true
    try {
      const payload = {
        parentId: form.parentId,
        name: form.name,
        code: form.code,
        sortOrder: form.sortOrder,
        status: form.status,
        description: form.description,
        attributeTemplate: form.attributeTemplate
      }
      if (editingId.value) {
        await updateCategory(editingId.value, payload)
        ElMessage.success('修改成功')
      } else {
        await createCategory(payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 拦截器已提示
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = (row) => {
  const hasChildren = row.children?.length
  const msg = hasChildren
    ? `「${row.name}」下存在子分类，需先删除或移动其子分类后才能删除该分类。`
    : `确定删除分类「${row.name}」吗？删除后不可恢复。`
  ElMessageBox.confirm(
    msg,
    '删除确认',
    { confirmButtonText: hasChildren ? '知道了' : '删除', cancelButtonText: '取消', type: 'warning', showCancelButton: !hasChildren }
  )
    .then(async () => {
      if (hasChildren) return
      await deleteCategory(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.op-btns {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}
.op-btns .el-button + .el-button {
  margin-left: 4px;
}
.attr-template {
  width: 100%;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 12px;
}
.attr-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.attr-tip {
  font-size: 12px;
  color: #909399;
}
.attr-empty {
  color: #c0c4cc;
  font-size: 13px;
  padding: 8px 0;
}
.attr-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}
.attr-code {
  width: 130px;
}
.attr-name {
  width: 120px;
}
.attr-type {
  width: 90px;
}
.attr-unit {
  width: 80px;
}
.attr-required {
  margin: 0 4px;
  white-space: nowrap;
}
.attr-options {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: -2px 0 10px 0;
}
.attr-options-label {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}
.attr-options-input {
  width: 240px;
}
</style>
