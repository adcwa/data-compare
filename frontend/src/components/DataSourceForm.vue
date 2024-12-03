<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="120px"
  >
    <el-form-item label="数据源名称" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>

    <el-form-item label="数据源类型" prop="type">
      <el-select v-model="form.type" placeholder="请选择数据源类型">
        <el-option label="数据库" value="DATABASE" />
        <el-option label="HTTP接口" value="HTTP" />
        <el-option label="文件" value="FILE" />
      </el-select>
    </el-form-item>

    <el-form-item label="配置信息" prop="config">
      <el-input
        v-model="form.config"
        type="textarea"
        :rows="8"
        placeholder="请输入JSON格式的配置信息"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'
import type { FormInstance } from 'element-plus'
import type { DataSource } from '@/types'

const props = defineProps<{
  initialData?: Partial<DataSource>
}>()

const emit = defineEmits<{
  (e: 'submit', data: Partial<DataSource>): void
  (e: 'cancel'): void
}>()

const formRef = ref<FormInstance>()
const form = ref({
  name: props.initialData?.name || '',
  type: props.initialData?.type || '',
  config: props.initialData?.config || ''
})

const rules = {
  name: [
    { required: true, message: '请输入数据源名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择数据源类型', trigger: 'change' }
  ],
  config: [
    { required: true, message: '请输入配置信息', trigger: 'blur' },
    {
      validator: (rule: any, value: string) => {
        try {
          if (value) {
            JSON.parse(value)
          }
          return true
        } catch (e) {
          return false
        }
      },
      message: '请输入有效的JSON格式',
      trigger: 'blur'
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
</script> 