<template>
  <div class="datasource-list">
    <div class="header">
      <h2>数据源管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        创建数据源
      </el-button>
    </div>

    <el-table
      v-loading="dataSourceStore.loading"
      :data="dataSourceStore.dataSources"
      style="width: 100%"
    >
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型">
        <template #default="{ row }">
          {{ getTypeLabel(row.type) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button @click="testConnection(row)" :loading="testingId === row.id">
            测试连接
          </el-button>
          <el-button type="primary" @click="editDataSource(row)">
            编辑
          </el-button>
          <el-button type="danger" @click="confirmDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑数据源' : '创建数据源'"
      width="600px"
    >
      <DataSourceForm
        v-if="dialogVisible"
        :initial-data="currentDataSource"
        @submit="handleSubmit"
        @cancel="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDataSourceStore } from '@/stores/dataSource'
import DataSourceForm from '@/components/DataSourceForm.vue'
import type { DataSource } from '@/types'

const dataSourceStore = useDataSourceStore()
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentDataSource = ref<Partial<DataSource> | null>(null)
const testingId = ref<number | null>(null)

const typeLabels = {
  DATABASE: '数据库',
  HTTP: 'HTTP接口',
  FILE: '文件'
}

function getTypeLabel(type: string) {
  return typeLabels[type as keyof typeof typeLabels] || type
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

function showCreateDialog() {
  isEdit.value = false
  currentDataSource.value = null
  dialogVisible.value = true
}

function editDataSource(dataSource: DataSource) {
  isEdit.value = true
  currentDataSource.value = { ...dataSource }
  dialogVisible.value = true
}

async function handleSubmit(data: Partial<DataSource>) {
  try {
    if (isEdit.value && currentDataSource.value?.id) {
      await dataSourceStore.updateDataSource(currentDataSource.value.id, data)
      ElMessage.success('数据源更新成功')
    } else {
      await dataSourceStore.createDataSource(data)
      ElMessage.success('数据源创建成功')
    }
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(dataSourceStore.error || '操作失败')
  }
}

async function confirmDelete(dataSource: DataSource) {
  try {
    await ElMessageBox.confirm(
      `确定要删除数据源"${dataSource.name}"吗？`,
      '确认删除',
      {
        type: 'warning'
      }
    )
    await dataSourceStore.deleteDataSource(dataSource.id)
    ElMessage.success('数据源删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(dataSourceStore.error || '删除失败')
    }
  }
}

async function testConnection(dataSource: DataSource) {
  testingId.value = dataSource.id
  try {
    await dataSourceStore.testConnection(dataSource.id)
    ElMessage.success('连接测试成功')
  } catch (error) {
    ElMessage.error(dataSourceStore.error || '连接测试失败')
  } finally {
    testingId.value = null
  }
}

onMounted(async () => {
  try {
    await dataSourceStore.fetchDataSources()
  } catch (error) {
    ElMessage.error(dataSourceStore.error || '加载数据源失败')
  }
})
</script>

<style scoped>
.datasource-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}
</style> 