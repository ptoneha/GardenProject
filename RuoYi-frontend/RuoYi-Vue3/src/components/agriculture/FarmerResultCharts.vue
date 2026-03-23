<template>
  <div class="farmer-panel">
    <div class="metric-grid">
      <article>
        <span>土地占用</span>
        <strong>{{ landUsageText }}</strong>
        <p>已规划 {{ landUsedArea.toFixed(2) }} / {{ totalLandArea.toFixed(2) }} sqm</p>
      </article>
      <article>
        <span>预算消耗</span>
        <strong>{{ budgetUsageText }}</strong>
        <p>估算成本 {{ budgetUsed.toFixed(2) }} 元</p>
      </article>
      <article>
        <span>预计收益</span>
        <strong>{{ totalProfit.toFixed(2) }} 元</strong>
        <p>来自 {{ summaryRows.length }} 种作物组合</p>
      </article>
    </div>

    <div class="chart-grid">
      <el-card shadow="never" class="chart-card">
        <template #header>收益组成饼图</template>
        <div ref="pieRef" class="chart-box"></div>
      </el-card>
      <el-card shadow="never" class="chart-card">
        <template #header>资源占用仪表盘</template>
        <div ref="gaugeRef" class="chart-box"></div>
      </el-card>
      <el-card shadow="never" class="chart-card wide">
        <template #header>作物收益对比</template>
        <div ref="barRef" class="chart-box"></div>
      </el-card>
    </div>

    <el-table :data="summaryRows" stripe class="summary-table">
      <el-table-column label="作物" min-width="160">
        <template #default="{ row }">
          <div class="crop-name">
            <span>{{ row.cropName }}</span>
            <el-tag v-if="row.isPulse" size="small" effect="plain" type="success">豆类</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分配面积(sqm)" prop="area" />
      <el-table-column label="预计成本(元)" prop="cost" />
      <el-table-column label="预计收益(元)" prop="profit" />
    </el-table>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { resolveLandAreaSqm } from '@/utils/agriculture'

const props = defineProps({
  task: {
    type: Object,
    default: () => ({})
  },
  results: {
    type: Array,
    default: () => []
  },
  lands: {
    type: Array,
    default: () => []
  },
  crops: {
    type: Array,
    default: () => []
  },
  configs: {
    type: Array,
    default: () => []
  }
})

const pieRef = ref(null)
const barRef = ref(null)
const gaugeRef = ref(null)
let pieChart
let barChart
let gaugeChart

const cropMap = computed(() => new Map(props.crops.map((item) => [item.cropId, item])))
const landMap = computed(() => new Map(props.lands.map((item) => [item.landId, item])))
const configMap = computed(() => {
  const map = new Map()
  props.configs.forEach((item) => {
    map.set(`${item.cropId}_${item.landType}`, item)
  })
  return map
})

const summaryRows = computed(() => {
  const summary = new Map()
  props.results.forEach((item) => {
    const crop = cropMap.value.get(item.cropId) || {}
    const land = landMap.value.get(item.landId) || {}
    const key = item.cropId
    const config = configMap.value.get(`${item.cropId}_${land.landType}`) || {}
    const area = Number(item.allocatedAreaSqm || 0)
    const profit = Number(item.profitAmount || 0)
    const cost = area * Number(config.costVal || 0)

    if (!summary.has(key)) {
      summary.set(key, {
        cropId: item.cropId,
        cropName: crop.cropName || `作物 ${item.cropId}`,
        isPulse: String(crop.isPulse).toUpperCase() === 'Y',
        area: 0,
        cost: 0,
        profit: 0
      })
    }
    const current = summary.get(key)
    current.area += area
    current.cost += cost
    current.profit += profit
  })

  return Array.from(summary.values()).map((item) => ({
    ...item,
    area: item.area.toFixed(2),
    cost: item.cost.toFixed(2),
    profit: item.profit.toFixed(2)
  }))
})

const totalLandArea = computed(() => props.lands.reduce((sum, item) => sum + resolveLandAreaSqm(item), 0))
const landUsedArea = computed(() => props.results.reduce((sum, item) => sum + Number(item.allocatedAreaSqm || 0), 0))
const budgetUsed = computed(() => summaryRows.value.reduce((sum, item) => sum + Number(item.cost || 0), 0))
const totalProfit = computed(() => summaryRows.value.reduce((sum, item) => sum + Number(item.profit || 0), 0))
const landUsageRate = computed(() => totalLandArea.value > 0 ? landUsedArea.value / totalLandArea.value : 0)
const budgetUsageRate = computed(() => Number(props.task?.totalBudget || 0) > 0 ? budgetUsed.value / Number(props.task.totalBudget) : 0)
const landUsageText = computed(() => `${(landUsageRate.value * 100).toFixed(1)}%`)
const budgetUsageText = computed(() => `${(budgetUsageRate.value * 100).toFixed(1)}%`)

function renderCharts() {
  if (!pieRef.value || !barRef.value || !gaugeRef.value) {
    return
  }

  pieChart = pieChart || echarts.init(pieRef.value)
  barChart = barChart || echarts.init(barRef.value)
  gaugeChart = gaugeChart || echarts.init(gaugeRef.value)

  const pieData = summaryRows.value.map((item) => ({
    name: item.cropName,
    value: Number(item.profit)
  }))

  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      name: '收益组成',
      type: 'pie',
      radius: ['42%', '72%'],
      center: ['50%', '50%'],
      label: { formatter: '{b}\n{d}%' },
      data: pieData.length ? pieData : [{ name: '暂无数据', value: 1 }]
    }]
  })

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: summaryRows.value.map((item) => item.cropName),
      axisLabel: { interval: 0, rotate: 18 }
    },
    yAxis: { type: 'value', name: '元' },
    grid: { left: 42, right: 20, top: 30, bottom: 60 },
    series: [{
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#4d8c68' },
          { offset: 1, color: '#d59f63' }
        ])
      },
      data: summaryRows.value.map((item) => Number(item.profit))
    }]
  })

  gaugeChart.setOption({
    series: [
      {
        type: 'gauge',
        center: ['28%', '55%'],
        radius: '78%',
        min: 0,
        max: 100,
        progress: { show: true, width: 12 },
        axisLine: { lineStyle: { width: 12 } },
        detail: { valueAnimation: true, formatter: '{value}%\n土地', offsetCenter: [0, '72%'] },
        data: [{ value: Number((landUsageRate.value * 100).toFixed(1)) }]
      },
      {
        type: 'gauge',
        center: ['72%', '55%'],
        radius: '78%',
        min: 0,
        max: 100,
        progress: { show: true, width: 12 },
        axisLine: { lineStyle: { width: 12 } },
        detail: { valueAnimation: true, formatter: '{value}%\n预算', offsetCenter: [0, '72%'] },
        data: [{ value: Number((budgetUsageRate.value * 100).toFixed(1)) }]
      }
    ]
  })
}

function resizeCharts() {
  pieChart?.resize()
  barChart?.resize()
  gaugeChart?.resize()
}

watch(() => [props.task, props.results, props.lands, props.crops, props.configs], async () => {
  await nextTick()
  renderCharts()
}, { deep: true })

onMounted(() => {
  renderCharts()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  pieChart?.dispose()
  barChart?.dispose()
  gaugeChart?.dispose()
})
</script>

<style scoped lang="scss">
.farmer-panel {
  display: grid;
  gap: 18px;
}

.metric-grid,
.chart-grid {
  display: grid;
  gap: 16px;
}

.metric-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.metric-grid article {
  padding: 18px 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(255, 250, 242, 0.95), rgba(239, 233, 220, 0.95));
  border: 1px solid rgba(111, 92, 70, 0.12);
}

.metric-grid span {
  color: #8c6e56;
}

.metric-grid strong {
  display: block;
  margin: 8px 0;
  font-size: 28px;
  color: #334734;
}

.metric-grid p {
  margin: 0;
  color: #62564b;
}

.chart-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.chart-card.wide {
  grid-column: 1 / -1;
}

.chart-card {
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.86);
}

.chart-box {
  height: 320px;
}

.summary-table {
  border-radius: 20px;
  overflow: hidden;
}

.crop-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 992px) {
  .metric-grid,
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
