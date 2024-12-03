<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="120px"
  >
    <el-form-item label="任务名称" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>

    <el-form-item label="源数据源" prop="sourceId">
      <el-select v-model="form.sourceId" placeholder="请选择源数据源">
        <el-option
          v-for="ds in dataSources"
          :key="ds.id"
          :label="ds.name"
          :value="ds.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="目标数据源" prop="targetId">
      <el-select v-model="form.targetId" placeholder="请选择目标数据源">
        <el-option
          v-for="ds in dataSources"
          :key="ds.id"
          :label="ds.name"
          :value="ds.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="主键字段" prop="primaryKeys">
      <el-input v-model="form.primaryKeys" placeholder="多个字段用逗号分隔" />
    </el-form-item>

    <el-form-item label="比较字段" prop="compareFields">
      <el-input v-model="form.compareFields" placeholder="多个字段用逗号分隔" />
    </el-form-item>

    <el-form-item label="触发方式" prop="triggerType">
      <el-radio-group v-model="form.triggerType">
        <el-radio label="MANUAL">手动触发</el-radio>
        <el-radio label="SCHEDULED">定时触发</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item
      v-if="form.triggerType === 'SCHEDULED'"
      label="Cron表达式"
      prop="cronExpression"
    >
      <el-input v-model="form.cronExpression" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useDataSourceStore } from '@/stores/dataSource'
import type { FormInstance } from 'element-plus'
import type { CompareTask } from '@/types'

const props = defineProps<{
  initialData?: Partial<CompareTask>
}>()

const emit = defineEmits<{
  (e: 'submit', data: Partial<CompareTask>): void
  (e: 'cancel'): void
}>()

const dataSourceStore = useDataSourceStore()
const formRef = ref<FormInstance>()
const form = ref({
  name: props.initialData?.name || '',
  sourceId: props.initialData?.source?.id || '',
  targetId: props.initialData?.target?.id || '',
  primaryKeys: props.initialData?.primaryKeys || '',
  compareFields: props.initialData?.compareFields || '',
  triggerType: props.initialData?.triggerType || 'MANUAL',
  cronExpression: props.initialData?.cronExpression || ''
})

const rules = {
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  sourceId: [
    { required: true, message: '请选择源数据源', trigger: 'change' }
  ],
  targetId: [
    { required: true, message: '请选择目标数据源', trigger: 'change' }
  ],
  primaryKeys: [
    { required: true, message: '请输入主键字段', trigger: 'blur' }
  ],
  compareFields: [
    { required: true, message: '请输入比较字段', trigger: 'blur' }
  ],
  triggerType: [
    { required: true, message: '请选择触发方式', trigger: 'change' }
  ],
  cronExpression: [
    {
      required: true,
      message: '请输入Cron表达式',
      trigger: 'blur',
      validator: (rule: any, value: string) => {
        if (form.value.triggerType === 'SCHEDULED' && !value) {
          return false
        }
        return true
      }
    }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', form.value)
    }
  })
}

onMounted(async () => {
  try {
    await dataSourceStore.fetchDataSources()
  } catch (error) {
    ElMessage.error('加载数据源列表失败')
  }
})

const dataSources = computed(() => dataSourceStore.dataSources)
</script> 