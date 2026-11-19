<template>
  <el-card>
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="openAdd">新增部门</el-button>
      <el-button :icon="Refresh" @click="load">刷新</el-button>
    </div>

    <el-table :data="tree" row-key="id" border default-expand-all>
      <el-table-column prop="name" label="部门名称" min-width="240" />
      <el-table-column prop="code" label="部门编码" width="180" />
      <el-table-column prop="sort" label="排序" width="90" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="140" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑部门' : '新增部门'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="上级部门">
          <el-tree-select v-model="form.parentId" :data="parentOptions" check-strictly
                          :props="{ label: 'name', value: 'id' }" style="width: 100%" />
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="form.code" placeholder="如 HC-SALES" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import request from '../api/request'

const tree = ref([])
const dialogVisible = ref(false)

const defaultForm = { id: null, parentId: 0, name: '', code: '', sort: 0, status: 1 }
const form = reactive({ ...defaultForm })

// 上级部门下拉：树形数据 + 一个“顶级部门”选项
const parentOptions = computed(() => [{ id: 0, name: '顶级部门', children: tree.value }])

async function load() {
  tree.value = await request.get('/departments/tree')
}

function openAdd() {
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, defaultForm, row)
  dialogVisible.value = true
}

async function save() {
  if (!form.name || !form.code) return ElMessage.warning('请填写部门名称和编码')
  await request.post('/departments', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除部门「${row.name}」吗？`, '提示', { type: 'warning' })
  await request.delete(`/departments/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
</style>
