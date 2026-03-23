<template>
  <div class="app-container agri-result-page">
    <el-card shadow="never" class="query-card">
      <el-form inline>
        <el-form-item label="任务ID">
          <el-input v-model="taskIdInput" placeholder="输入任务ID查看结果" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">加载结果</el-button>
          <el-button icon="Refresh" @click="reloadCurrent">刷新</el-button>
          <el-button icon="Back" @click="router.push('/agriculture/task')">返回任务中心</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-loading="loading">
      <el-empty v-if="!currentTask" description="输入任务ID后，可查看这次优化的结果。" />

      <template v-else>
        <section class="admin-hero-panel">
          <div>
            <span class="eyebrow">Results Gallery</span>
            <h1>{{ currentTask.taskName }}</h1>
            <p>这里展示任务结果的图表和明细数据，便于管理员查看执行情况。</p>
          </div>
          <div class="hero-meta">
            <el-tag effect="plain" size="large">{{ getAgriModeLabel(currentTask.mode) }}</el-tag>
            <el-tag :type="getAgriStatusMeta(currentTask.status).tagType || 'info'" size="large">
              {{ getAgriStatusMeta(currentTask.status).label }}
            </el-tag>
          </div>
        </section>

        <div class="summary-grid">
          <article>
            <span>预算</span>
            <strong>{{ Number(currentTask.totalBudget || 0).toFixed(2) }} 元</strong>
          </article>
          <article>
            <span>地块数量</span>
            <strong>{{ selectedLands.length }}</strong>
          </article>
          <article>
            <span>容器数量</span>
            <strong>{{ selectedContainers.length }}</strong>
          </article>
          <article>
            <span>结果条目</span>
            <strong>{{ resultRows.length }}</strong>
          </article>
        </div>

        <el-alert
          v-if="feedbackGuide"
          :title="feedbackGuide.title"
          type="warning"
          :closable="false"
          show-icon
          class="feedback-alert"
        >
          <template #default>
            <p>{{ feedbackGuide.description }}</p>
            <p>{{ feedbackGuide.suggestion }}</p>
          </template>
        </el-alert>

        <farmer-result-charts
          v-if="Number(currentTask.mode) === 1 && resultRows.length"
          :task="currentTask"
          :results="resultRows"
          :lands="selectedLands"
          :crops="cropOptions"
          :configs="configOptions"
        />

        <garden-result-gallery
          v-if="Number(currentTask.mode) === 2"
          :results="resultRows"
          :lands="selectedLands"
          :containers="selectedContainers"
          :crops="cropOptions"
        />

        <el-card v-if="!resultRows.length" shadow="never" class="empty-result">
          <el-empty description="该任务当前还没有可展示的结果数据。" />
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header>任务详情</template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="任务ID">{{ currentTask.taskId }}</el-descriptions-item>
            <el-descriptions-item label="季节">{{ getAgriSeasonLabel(currentTask.taskSeason) }}</el-descriptions-item>
            <el-descriptions-item label="豆类比例">{{ toPercent(currentTask.minPulseRatio || 0) }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(currentTask.createTime, '{y}-{m}-{d}') }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header>结果明细</template>
          <el-table :data="displayRows">
            <el-table-column label="地块" min-width="140" prop="landName" />
            <el-table-column label="容器" min-width="150" prop="containerName" />
            <el-table-column label="作物" min-width="150" prop="cropName" />
            <el-table-column label="面积(㎡)" prop="allocatedAreaSqm" />
            <el-table-column label="株数" prop="plantCount" />
            <el-table-column label="收益(元)" prop="profitAmount" />
            <el-table-column label="体验分" prop="experienceScore" />
          </el-table>
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup name="Result">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { parseTime } from '@/utils/ruoyi'
import { listConfig } from '@/api/agriculture/config'
import { listContainer } from '@/api/agriculture/container'
import { listCrop } from '@/api/agriculture/crop'
import { listLand } from '@/api/agriculture/land'
import { listResultByTask } from '@/api/agriculture/result'
import { getTask } from '@/api/agriculture/task'
import { listTaskLand } from '@/api/agriculture/taskLand'
import FarmerResultCharts from '@/components/agriculture/FarmerResultCharts.vue'
import GardenResultGallery from '@/components/agriculture/GardenResultGallery.vue'
import { getAgriModeLabel, getAgriSeasonLabel, getAgriStatusMeta, getCropVisual, loadTaskFeedback, resolveAgriErrorGuide, toPercent } from '@/utils/agriculture'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const taskIdInput = ref(route.query.taskId ? String(route.query.taskId) : '')
const allLands = ref([])
const allContainers = ref([])
const cropOptions = ref([])
const configOptions = ref([])
const currentTask = ref(null)
const resultRows = ref([])
const selectedLandIds = ref([])
const feedback = ref(null)

const selectedLands = computed(() => allLands.value.filter((item) => selectedLandIds.value.includes(item.landId)))
const selectedContainers = computed(() => allContainers.value.filter((item) => selectedLandIds.value.includes(item.landId)))

const displayRows = computed(() => {
  const landMap = new Map(allLands.value.map((item) => [item.landId, item.landCode]))
  const containerMap = new Map(allContainers.value.map((item) => [item.containerId, item.containerName]))
  const cropMap = new Map(cropOptions.value.map((item) => [item.cropId, item]))
  return resultRows.value.map((item) => {
    const crop = cropMap.get(item.cropId)
    return {
      ...item,
      landName: landMap.get(item.landId) || '未命名空间',
      containerName: item.containerId ? containerMap.get(item.containerId) || '未命名容器' : '',
      cropName: crop?.cropName || '未命名作物',
      visual: getCropVisual(crop)
    }
  })
})

const feedbackGuide = computed(() => {
  if (!feedback.value?.infeasibleReason) {
    return null
  }
  return resolveAgriErrorGuide(feedback.value.infeasibleReason, feedback.value.taskSeason || currentTask.value?.taskSeason)
})

async function loadCatalogs() {
  const [landRes, containerRes, cropRes, configRes] = await Promise.all([
    listLand({ pageNum: 1, pageSize: 1000 }),
    listContainer({ pageNum: 1, pageSize: 1000 }),
    listCrop({ pageNum: 1, pageSize: 1000 }),
    listConfig({ pageNum: 1, pageSize: 1000 })
  ])
  allLands.value = landRes.rows || []
  allContainers.value = containerRes.rows || []
  cropOptions.value = cropRes.rows || []
  configOptions.value = configRes.rows || []
}

async function loadTaskResult(taskId) {
  if (!taskId) {
    currentTask.value = null
    resultRows.value = []
    selectedLandIds.value = []
    return
  }
  loading.value = true
  try {
    const [taskRes, resultRes, taskLandRes] = await Promise.all([
      getTask(taskId),
      listResultByTask(taskId),
      listTaskLand({ pageNum: 1, pageSize: 1000, taskId })
    ])
    currentTask.value = taskRes.data || null
    resultRows.value = resultRes.rows || []
    selectedLandIds.value = (taskLandRes.rows || []).map((item) => item.landId)
    feedback.value = loadTaskFeedback(taskId)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  if (!taskIdInput.value) {
    return
  }
  router.replace({ path: route.path, query: { taskId: taskIdInput.value } })
  loadTaskResult(taskIdInput.value)
}

function reloadCurrent() {
  if (!taskIdInput.value) {
    return
  }
  loadTaskResult(taskIdInput.value)
}

onMounted(async () => {
  await loadCatalogs()
  if (taskIdInput.value) {
    await loadTaskResult(taskIdInput.value)
  }
})
</script>

<style scoped lang="scss">
.agri-result-page {
  display: grid;
  gap: 20px;
}

.query-card,
.admin-hero-panel,
.detail-card,
.empty-result {
  border-radius: 24px;
}

.admin-hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 30px 32px;
  background:
    radial-gradient(circle at top right, rgba(86, 128, 104, 0.16), transparent 32%),
    linear-gradient(135deg, #fff9f1, #e9dfcc 82%);
}

.eyebrow {
  display: inline-block;
  color: #8a6d54;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  font-size: 12px;
}

.admin-hero-panel h1 {
  margin: 10px 0 8px;
  font-size: 38px;
  color: #334234;
}

.admin-hero-panel p {
  margin: 0;
  color: #6f6559;
  line-height: 1.85;
}

.hero-meta {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.summary-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.summary-grid article {
  padding: 20px 22px;
  border-radius: 24px;
  border: 1px solid rgba(117, 94, 68, 0.1);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(244, 235, 220, 0.92));
}

.summary-grid span {
  color: #92735a;
  font-size: 13px;
}

.summary-grid strong {
  display: block;
  margin-top: 10px;
  font-size: 30px;
  color: #344434;
}

.feedback-alert :deep(p) {
  margin: 8px 0 0;
  line-height: 1.7;
}

.detail-card,
.empty-result {
  background: rgba(255, 255, 255, 0.88);
}

@media (max-width: 1100px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-hero-panel {
    flex-direction: column;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
