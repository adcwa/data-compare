<template>
  <div class="compare-result-chart">
    <el-card>
      <template #header>
        <div class="chart-header">
          <span>差异分布</span>
          <el-radio-group v-model="chartType" size="small">
            <el-radio-button label="pie">饼图</el-radio-button>
            <el-radio-button label="bar">柱状图</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

const props = defineProps<{
  data: {
    missingInSource: number
    missingInTarget: number
    valueDiff: number
  }
}>()

const chartRef = ref<HTMLElement>()
const chartType = ref<'pie' | 'bar'>('pie')
let chart: echarts.ECharts | null = null

const chartData = [
  { name: '源数据缺失', value: props.data.missingInSource },
  { name: '目标数据缺失', value: props.data.missingInTarget },
  { name: '数据不一致', value: props.data.valueDiff }
]

function getPieOption(): EChartsOption {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '70%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
}

function getBarOption(): EChartsOption {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: chartData.map(item => item.name)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        data: chartData.map(item => item.value)
      }
    ]
  }
}

function initChart() {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  updateChart()
  
  window.addEventListener('resize', handleResize)
}

function updateChart() {
  if (!chart) return
  
  const option = chartType.value === 'pie' ? getPieOption() : getBarOption()
  chart.setOption(option)
}

function handleResize() {
  chart?.resize()
}

watch(() => chartType.value, updateChart)

watch(() => props.data, () => {
  chartData.forEach((item, index) => {
    switch (index) {
      case 0:
        item.value = props.data.missingInSource
        break
      case 1:
        item.value = props.data.missingInTarget
        break
      case 2:
        item.value = props.data.valueDiff
        break
    }
  })
  updateChart()
}, { deep: true })

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.compare-result-chart {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 400px;
}
</style> 