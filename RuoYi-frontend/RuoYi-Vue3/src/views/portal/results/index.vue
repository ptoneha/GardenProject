<template>
  <div class="portal-result-page">
    <section class="filter-shell" :class="{ muted: !currentTask }">
      <div class="filter-chip-list">
        <button class="filter-chip active" type="button">全部方案</button>
        <button class="filter-chip" type="button">春季</button>
        <button class="filter-chip" type="button">夏季</button>
        <button class="filter-chip" type="button">农户</button>
        <button class="filter-chip" type="button">园艺</button>
      </div>
      <p v-if="!currentTask" class="filter-placeholder-tip">有方案后可以按条件筛选</p>
    </section>

    <div v-loading="loading" class="portal-result-body">
      <template v-if="currentTask">
        <section class="gallery-overview">
          <article class="overview-card">
            <span>当前方案</span>
            <strong>{{ currentTask.taskName }}</strong>
            <p>这是一份已经完成计算、可以回看和复用的种植组合。</p>
          </article>
          <article class="overview-card accent-soft">
            <span>方案类型</span>
            <strong>{{ getAgriModeLabel(currentTask.mode) }}</strong>
            <p>不同模式会影响推荐逻辑、结果展示方式和重点指标。</p>
          </article>
          <article class="overview-card accent-warm">
            <span>{{ Number(currentTask.mode) === 1 ? '总预期收益' : '体验总分' }}</span>
            <strong>{{ Number(currentTask.mode) === 1 ? `${totalProfit.toFixed(2)} 元` : `${totalExperience} 分` }}</strong>
            <p>
              {{ Number(currentTask.mode) === 1
                ? '你可以先从收益更高、投入更明确的组合开始落地。'
                : '更高的分数通常意味着更完整的种植体验。' }}
            </p>
          </article>
          <article class="overview-card">
            <span>本次涉及空间</span>
            <strong>{{ selectedLands.length }} 处</strong>
            <p>系统已经把这些地块和容器整理成可浏览的方案画廊。</p>
          </article>
        </section>

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

        <section class="gallery-story-card">
          <div class="section-heading">
            <div>
              <span class="section-kicker">Gallery Story</span>
              <h3>这份方案值得先看什么</h3>
            </div>
            <div class="story-tags">
              <span class="story-tag">{{ getAgriModeLabel(currentTask.mode) }}</span>
              <span class="story-tag">{{ selectedLands.length }} 个空间</span>
              <span class="story-tag">{{ resultRows.length }} 条推荐</span>
            </div>
          </div>
          <p>{{ narrativeText }}</p>
        </section>

        <section class="gallery-section">
          <div class="section-heading">
            <div>
              <span class="section-kicker">Insight View</span>
              <h3>先快速看懂这份方案</h3>
            </div>
          </div>

          <farmer-result-charts
            v-if="Number(currentTask.mode) === 1 && resultRows.length"
            :task="currentTask"
            :results="resultRows"
            :lands="selectedLands"
            :crops="cropOptions"
            :configs="configOptions"
          />

          <garden-result-gallery
            v-else-if="Number(currentTask.mode) === 2"
            :results="resultRows"
            :lands="selectedLands"
            :containers="selectedContainers"
            :crops="cropOptions"
          />

          <div v-else class="inline-empty-state">
            <h4>这份方案暂时还没有足够的推荐内容</h4>
            <p>你可以回到种植计划页微调季节、预算或偏好，再重新生成一份更完整的方案。</p>
            <el-button class="natural-primary-button" @click="router.push('/portal/tasks')">回到种植计划</el-button>
          </div>
        </section>

        <section class="gallery-section">
          <div class="section-heading">
            <div>
              <span class="section-kicker">Archive Cards</span>
              <h3>逐张查看你的种植安排</h3>
            </div>
          </div>

          <div v-if="resultRows.length" class="result-card-grid">
            <article
              v-for="item in displayRows"
              :key="item.resultId || `${item.landId}-${item.containerId}-${item.cropId}`"
              class="result-card"
            >
              <div class="result-card-top">
                <div class="crop-visual">
                  <img v-if="item.visual.type === 'image'" :src="item.visual.value" :alt="item.cropName">
                  <span v-else>{{ item.visual.value }}</span>
                </div>
                <div>
                  <span class="result-eyebrow">{{ item.containerName || item.landName }}</span>
                  <h4>{{ item.cropName }}</h4>
                </div>
              </div>

              <div class="result-meta">
                <div>
                  <span>推荐位置</span>
                  <strong>{{ item.landName }}</strong>
                </div>
                <div>
                  <span>空间说明</span>
                  <strong>{{ item.containerName || '直接安排在地块中' }}</strong>
                </div>
              </div>

              <div class="result-stats">
                <div v-if="item.allocatedAreaSqm != null">
                  <span>推荐面积</span>
                  <strong>{{ Number(item.allocatedAreaSqm).toFixed(2) }} ㎡</strong>
                </div>
                <div v-if="item.plantCount != null">
                  <span>推荐株数</span>
                  <strong>{{ item.plantCount }} 株</strong>
                </div>
                <div v-if="item.profitAmount != null">
                  <span>预期收益</span>
                  <strong>{{ Number(item.profitAmount).toFixed(2) }} 元</strong>
                </div>
                <div v-if="item.experienceScore != null">
                  <span>体验得分</span>
                  <strong>{{ item.experienceScore }} 分</strong>
                </div>
              </div>

              <p class="result-note">{{ buildResultNote(item) }}</p>
            </article>
          </div>

          <div v-else class="result-card-placeholder-grid">
            <article v-for="index in 3" :key="index" class="result-card placeholder-card">
              <div class="placeholder-block visual"></div>
              <div class="placeholder-line long"></div>
              <div class="placeholder-line medium"></div>
              <div class="placeholder-meta-grid">
                <div class="placeholder-cell"></div>
                <div class="placeholder-cell"></div>
              </div>
              <div class="placeholder-line short"></div>
            </article>
          </div>
        </section>
      </template>

      <template v-else>
        <section class="empty-gallery-card">
          <div class="empty-illustration">
            <div class="halo halo-a"></div>
            <div class="halo halo-b"></div>
            <div class="gallery-frame">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>

          <div class="empty-copy">
            <h3>你还没有任何种植方案</h3>
            <p class="empty-subtitle">从一次简单的规划开始，我们会帮你生成第一份可执行的种植组合。</p>
            <el-button class="natural-primary-button" @click="router.push('/portal/tasks')">开始一份种植计划</el-button>
          </div>
        </section>

        <section class="gallery-placeholder-section">
          <div class="section-heading">
            <div>
              <span class="section-kicker">My Plan Archive</span>
              <h3>我的种植方案</h3>
              <p class="preview-copy">你的种植方案会以卡片形式呈现在这里。</p>
            </div>
          </div>

          <div class="plan-card-grid">
            <plan-archive-card
              v-for="plan in mockPlanCards"
              :key="plan.id"
              :plan="plan"
            />
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup name="PortalResults">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getPortalTask,
  listPortalContainer,
  listPortalCrop,
  listPortalLand,
  listPortalResultByTask,
  listPortalTask,
  listPortalTaskLand
} from '@/api/portal/agriculture'
import FarmerResultCharts from '@/components/agriculture/FarmerResultCharts.vue'
import GardenResultGallery from '@/components/agriculture/GardenResultGallery.vue'
import PlanArchiveCard from '@/components/agriculture/PlanArchiveCard.vue'
import { getAgriModeLabel, getCropVisual, loadTaskFeedback, resolveAgriErrorGuide } from '@/utils/agriculture'
import { mockPlanCards } from './mockPlans'

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
const totalProfit = computed(() => resultRows.value.reduce((sum, item) => sum + Number(item.profitAmount || 0), 0))
const totalExperience = computed(() => resultRows.value.reduce((sum, item) => sum + Number(item.experienceScore || 0), 0))

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

const narrativeText = computed(() => {
  if (!currentTask.value) {
    return ''
  }
  if (Number(currentTask.value.mode) === 1) {
    return `系统已经为你整理出 ${resultRows.value.length} 条收益导向的安排。你可以先看收益更高、投入更明确的组合，再决定优先从哪块地开始。`
  }
  return `系统已经为你整理出 ${resultRows.value.length} 条布局导向的安排。你可以从容器分布、株数和环境适配度里，挑出最想先落地的一组。`
})

async function loadCatalogs() {
  const [landRes, containerRes, cropRes] = await Promise.all([
    listPortalLand({ pageNum: 1, pageSize: 1000 }),
    listPortalContainer({ pageNum: 1, pageSize: 1000 }),
    listPortalCrop({ pageNum: 1, pageSize: 1000 })
  ])
  allLands.value = landRes.rows || []
  allContainers.value = containerRes.rows || []
  cropOptions.value = cropRes.rows || []
  configOptions.value = []
}

async function resolveTaskId() {
  if (taskIdInput.value) {
    return taskIdInput.value
  }
  const response = await listPortalTask({ pageNum: 1, pageSize: 1, status: '1' })
  const latestTask = response.rows?.[0]
  if (!latestTask) {
    return ''
  }
  taskIdInput.value = String(latestTask.taskId)
  router.replace({ path: route.path, query: { taskId: latestTask.taskId } })
  return taskIdInput.value
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
      getPortalTask(taskId),
      listPortalResultByTask(taskId),
      listPortalTaskLand({ pageNum: 1, pageSize: 1000, taskId })
    ])
    currentTask.value = taskRes.data || null
    resultRows.value = resultRes.rows || []
    selectedLandIds.value = (taskLandRes.rows || []).map((item) => item.landId)
    feedback.value = loadTaskFeedback(taskId)
  } finally {
    loading.value = false
  }
}

function buildResultNote(item) {
  if (item.profitAmount != null) {
    return `如果你优先看收益，可以先从这组 ${item.cropName} 开始，它更容易形成明确的投入产出反馈。`
  }
  if (item.plantCount != null) {
    return `这组安排更适合在 ${item.containerName || item.landName} 中落地，种植密度和空间感会更平衡。`
  }
  return '这组推荐已经综合考虑了位置、作物和环境条件。'
}

onMounted(async () => {
  await loadCatalogs()
  const taskId = await resolveTaskId()
  if (taskId) {
    await loadTaskResult(taskId)
  }
})
</script>

<style scoped lang="scss">
.portal-result-page {
  display: grid;
  gap: 22px;
  color: #4d5548;
}

.filter-shell,
.overview-card,
.gallery-story-card,
.gallery-section,
.empty-gallery-card,
.placeholder-card {
  border-radius: 28px;
}

.section-kicker,
.result-eyebrow {
  display: inline-block;
  color: #b08e58;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-size: 12px;
}

.gallery-story-card p,
.result-note,
.empty-subtitle,
.inline-empty-state p {
  margin: 0;
  color: #7a725f;
  line-height: 1.8;
}

.natural-primary-button {
  min-height: 48px;
  padding: 0 26px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  box-shadow: 0 14px 28px rgba(108, 160, 115, 0.2);
  color: #fff;
}

.natural-primary-button:deep(span) {
  font-weight: 600;
}

.filter-shell {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: space-between;
  align-items: center;
  padding: 18px 22px;
  background: rgba(255, 255, 255, 0.74);
  border: 1px solid rgba(201, 168, 106, 0.12);
}

.filter-shell.muted {
  opacity: 0.72;
}

.filter-chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.filter-chip {
  height: 38px;
  padding: 0 18px;
  border: 1px solid rgba(201, 168, 106, 0.16);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  color: #6d6959;
  cursor: default;
}

.filter-chip.active {
  border-color: rgba(201, 168, 106, 0.34);
  background: rgba(249, 243, 229, 0.96);
  color: #9c7a45;
}

.filter-placeholder-tip {
  margin: 0;
  color: #988f80;
  font-size: 13px;
}

.portal-result-body {
  display: grid;
  gap: 20px;
}

.gallery-overview,
.result-card-grid,
.result-card-placeholder-grid,
.plan-card-grid {
  display: grid;
  gap: 16px;
}

.gallery-overview {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.overview-card,
.gallery-story-card,
.gallery-section,
.empty-gallery-card {
  padding: 24px 26px;
  background: rgba(255, 255, 255, 0.84);
  border: 1px solid rgba(201, 168, 106, 0.12);
  box-shadow: 0 16px 34px rgba(157, 130, 81, 0.05);
}

.overview-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(251, 247, 239, 0.9));
}

.overview-card.accent-soft {
  background: linear-gradient(180deg, rgba(252, 247, 236, 0.96), rgba(248, 241, 226, 0.94));
}

.overview-card.accent-warm {
  background: linear-gradient(180deg, rgba(255, 248, 237, 0.98), rgba(245, 235, 216, 0.94));
}

.overview-card span,
.result-meta span,
.result-stats span {
  color: #a18152;
  font-size: 13px;
}

.overview-card strong {
  display: block;
  margin-top: 12px;
  font-size: 28px;
  color: #4c5342;
  line-height: 1.28;
}

.overview-card p {
  margin: 10px 0 0;
  color: #7b7362;
  line-height: 1.72;
}

.feedback-alert {
  border-radius: 24px;
}

.feedback-alert :deep(p) {
  margin: 8px 0 0;
  line-height: 1.72;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 14px;
  margin-bottom: 18px;
}

.section-heading h3 {
  margin: 10px 0 0;
  color: #4c5342;
  font-size: 28px;
}

.story-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.story-tag {
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(247, 241, 230, 0.92);
  color: #9b7a47;
  font-size: 13px;
}

.inline-empty-state {
  display: grid;
  justify-items: start;
  gap: 14px;
  padding: 10px 0 4px;
}

.inline-empty-state h4,
.empty-copy h3 {
  margin: 0;
  color: #4c5342;
}

.result-card-grid,
.result-card-placeholder-grid,
.plan-card-grid {
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.result-card {
  padding: 22px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(247, 242, 232, 0.92));
  border: 1px solid rgba(201, 168, 106, 0.12);
  box-shadow: 0 14px 30px rgba(157, 130, 81, 0.06);
}

.result-card-top {
  display: flex;
  gap: 16px;
  align-items: center;
}

.crop-visual {
  width: 64px;
  height: 64px;
  flex: 0 0 64px;
  border-radius: 20px;
  background: rgba(251, 247, 239, 0.92);
  display: grid;
  place-items: center;
}

.crop-visual img {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  object-fit: cover;
}

.crop-visual span {
  font-size: 30px;
}

.result-card h4 {
  margin: 8px 0 0;
  color: #4c5342;
  font-size: 26px;
}

.result-meta,
.result-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.result-meta div,
.result-stats div {
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(250, 246, 237, 0.92);
}

.result-meta strong,
.result-stats strong {
  display: block;
  margin-top: 8px;
  color: #4b5341;
  font-size: 18px;
}

.result-note {
  margin-top: 18px;
}

.empty-gallery-card {
  display: grid;
  justify-items: center;
  gap: 18px;
  text-align: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(248, 243, 233, 0.92));
}

.empty-illustration {
  position: relative;
  min-height: 220px;
  width: 100%;
  display: grid;
  place-items: center;
}

.halo {
  position: absolute;
  border-radius: 999px;
  filter: blur(2px);
}

.halo-a {
  width: 160px;
  height: 160px;
  background: rgba(201, 168, 106, 0.12);
}

.halo-b {
  width: 220px;
  height: 220px;
  background: rgba(111, 179, 123, 0.1);
}

.gallery-frame {
  position: relative;
  z-index: 1;
  width: 160px;
  height: 180px;
  padding: 16px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(201, 168, 106, 0.18);
  display: grid;
  gap: 12px;
}

.gallery-frame span {
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(201, 168, 106, 0.16), rgba(111, 179, 123, 0.1));
}

.gallery-frame span:nth-child(1) {
  height: 60px;
}

.gallery-frame span:nth-child(2),
.gallery-frame span:nth-child(3) {
  height: 34px;
}

.empty-copy {
  display: grid;
  gap: 14px;
  justify-items: center;
  max-width: 620px;
}

.empty-copy h3 {
  font-size: 34px;
  line-height: 1.2;
}

.gallery-placeholder-section {
  display: grid;
  gap: 18px;
}

.preview-copy {
  margin: 12px 0 0;
  color: #7b7362;
  line-height: 1.8;
}

.placeholder-card {
  pointer-events: none;
}

.placeholder-block.visual,
.placeholder-line,
.placeholder-cell {
  background: linear-gradient(90deg, rgba(242, 236, 226, 0.9), rgba(250, 247, 240, 0.95), rgba(242, 236, 226, 0.9));
  background-size: 220% 100%;
  animation: skeleton-flow 1.8s ease-in-out infinite;
}

.placeholder-block.visual {
  width: 72px;
  height: 72px;
  border-radius: 22px;
}

.placeholder-line {
  margin-top: 16px;
  border-radius: 999px;
  height: 14px;
}

.placeholder-line.long {
  width: 72%;
}

.placeholder-line.medium {
  width: 56%;
}

.placeholder-line.short {
  width: 48%;
}

.placeholder-meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.placeholder-cell {
  height: 68px;
  border-radius: 18px;
}

@keyframes skeleton-flow {
  0% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

@media (max-width: 1200px) {
  .gallery-overview {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .filter-shell,
  .section-heading {
    flex-direction: column;
    align-items: flex-start;
  }

  .empty-copy h3 {
    font-size: 30px;
  }

  .gallery-overview,
  .result-meta,
  .result-stats,
  .placeholder-meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
