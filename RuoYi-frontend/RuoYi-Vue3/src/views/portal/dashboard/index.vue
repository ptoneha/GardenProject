<template>
  <div class="portal-dashboard">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="eyebrow">Garden Overview</span>
        <h2>{{ heroTitle }}</h2>
        <p>{{ heroDescription }}</p>

        <div class="hero-actions">
          <el-button class="hero-primary-button" @click="router.push('/portal/tasks')">开始第一份方案</el-button>
          <el-button class="hero-secondary-button" @click="router.push('/portal/results')">查看我的方案</el-button>
        </div>

        <div class="hero-stats">
          <span class="hero-stat-inline">空间 {{ summary.lands }}</span>
          <span class="hero-stat-inline">容器 {{ summary.containers }}</span>
          <span class="hero-stat-inline">使用率 {{ containerLoadText }}</span>
        </div>
      </div>

      <div class="hero-visual" aria-hidden="true">
        <div class="status-illustration">
          <div class="status-halo"></div>
          <div class="status-sprout">
            <span class="sprout-stem"></span>
            <span class="sprout-leaf sprout-leaf-left"></span>
            <span class="sprout-leaf sprout-leaf-right"></span>
          </div>
          <div class="status-pot">
            <span class="pot-rim"></span>
            <span class="pot-soil"></span>
          </div>
          <div class="status-shadow"></div>
        </div>
      </div>
    </section>

    <section class="overview-grid">
      <el-card shadow="never" class="dashboard-card recent-plan-card clickable-card" @click="handleRecentPlanCardClick">
        <div class="section-head compact">
          <div>
            <span class="section-kicker">Recent Plan</span>
            <h3>最近方案</h3>
          </div>
          <el-button text @click.stop="router.push('/portal/results')">查看全部</el-button>
        </div>

        <template v-if="recentPlan">
          <div class="recent-plan-body">
            <div class="plan-copy">
              <div class="plan-meta">
                <el-tag effect="plain">{{ getAgriModeLabel(recentPlan.mode) }}</el-tag>
                <el-tag :type="getAgriStatusMeta(recentPlan.status).tagType || 'info'">
                  {{ getAgriStatusMeta(recentPlan.status).label }}
                </el-tag>
              </div>
              <h4>{{ recentPlan.taskName }}</h4>
              <p>{{ describeRecentPlan(recentPlan) }}</p>
              <div class="plan-actions">
                <el-button class="subtle-secondary-button" @click.stop="openTaskResults(recentPlan.taskId)">查看方案</el-button>
                <el-button text @click.stop="router.push('/portal/tasks')">继续规划</el-button>
              </div>
            </div>

            <div class="plan-visual">
              <div v-if="recentPieStyle" class="mini-pie" :style="{ background: recentPieStyle }"></div>
              <div v-else class="plan-placeholder">
                <strong>{{ getAgriStatusMeta(recentPlan.status).label }}</strong>
                <span>最近方案状态已同步到这里</span>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="dashboard-empty-state">
          <strong>最近方案</strong>
          <p>完成第一次规划后，最近方案会显示在这里。</p>
          <el-button class="subtle-secondary-button" @click.stop="router.push('/portal/tasks')">去开始方案</el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="dashboard-card season-card clickable-card" @click="router.push('/portal/tasks')">
        <div class="section-head compact">
          <div>
            <span class="section-kicker">Monthly Hint</span>
            <h3>本月提示</h3>
          </div>
        </div>
        <p class="season-summary">{{ currentMonthLabel }}适合从易启动作物开始，先做小范围尝试。</p>

        <div v-if="seasonHighlights.length" class="season-chip-list">
          <article v-for="entry in seasonHighlights" :key="entry.cropId" class="season-chip">
            <div class="season-chip__visual">
              <img v-if="entry.visual.type === 'image'" :src="entry.visual.value" :alt="entry.cropName">
              <span v-else>{{ entry.visual.value }}</span>
            </div>
            <strong>{{ entry.cropName }}</strong>
          </article>
        </div>
        <p v-else class="season-fallback">作物灵感准备好后，这里会显示本月建议。</p>
      </el-card>
    </section>

    <section class="activity-grid">
      <el-card shadow="never" class="dashboard-card activity-card clickable-card" @click="router.push('/portal/results')">
        <div class="section-head compact">
          <div>
            <span class="section-kicker">Recent Activity</span>
            <h3>最近动态</h3>
          </div>
        </div>

        <div v-if="recentTasks.length" class="timeline compact">
          <article v-for="item in recentTasks.slice(0, 3)" :key="item.taskId" class="timeline-item">
            <div class="timeline-dot" :class="`status-${String(item.status)}`"></div>
            <div class="timeline-body">
              <strong>{{ item.taskName }}</strong>
              <p>{{ getAgriModeLabel(item.mode) }} · {{ getAgriSeasonLabel(item.taskSeason) }} · {{ getAgriStatusMeta(item.status).label }}</p>
            </div>
          </article>
        </div>
        <div v-else class="dashboard-empty-state inline">
          <strong>还没有最近动态</strong>
          <p>完成规划后，这里会显示最新进展。</p>
        </div>
      </el-card>

      <el-card shadow="never" class="dashboard-card activity-card notes-card clickable-card" @click="router.push('/portal/tasks')">
        <div class="section-head compact">
          <div>
            <span class="section-kicker">Next Actions</span>
            <h3>下一步建议</h3>
          </div>
        </div>

        <ul class="action-list">
          <li v-for="item in nextStepSuggestions" :key="item.title">
            <strong>{{ item.title }}</strong>
            <span>{{ item.description }}</span>
          </li>
        </ul>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  listPortalContainer,
  listPortalCrop,
  listPortalLand,
  listPortalResult,
  listPortalResultByTask,
  listPortalTask
} from '@/api/portal/agriculture'
import { getAgriModeLabel, getAgriSeasonLabel, getAgriStatusMeta, getCropVisual } from '@/utils/agriculture'

const router = useRouter()

const summary = reactive({
  lands: 0,
  containers: 0
})

const cropLibrary = ref([])
const recentTasks = ref([])
const recentPieStyle = ref('')
const containerLoadRate = ref(0)

const currentMonthLabel = computed(() => `${new Date().getMonth() + 1}月`)
const containerLoadText = computed(() => `${(containerLoadRate.value * 100).toFixed(0)}%`)
const recentPlan = computed(() => recentTasks.value[0] || null)
const hasBaseSetup = computed(() => summary.lands > 0 || summary.containers > 0)
const seasonHighlights = computed(() =>
  cropLibrary.value.slice(0, 3).map((crop) => ({
    cropId: crop.cropId,
    cropName: crop.cropName,
    visual: getCropVisual(crop)
  }))
)

const heroTitle = computed(() => {
  if (recentPlan.value) {
    return '当前种植状态已更新，可以开始下一轮规划'
  }
  if (hasBaseSetup.value) {
    return '基础种植信息已经就绪，可以开始第一份方案'
  }
  return '先整理当前种植状态，再开始第一份方案'
})

const heroDescription = computed(() => {
  if (recentPlan.value) {
    return '最近方案、空间和容器信息已经同步到首页，你可以直接继续规划，或先回看已有方案。'
  }
  if (hasBaseSetup.value) {
    return '空间或容器已经录入完成，下一步只需要发起规划，系统就会生成更贴近实际环境的建议。'
  }
  return '先补充空间或容器，再开始规划，后续推荐会更贴近你的真实种植环境。'
})

const nextStepSuggestions = computed(() => [
  { title: '添加一个空间', description: '补齐真实种植环境' },
  { title: '添加一个容器', description: '补齐容器与摆放信息' },
  { title: '开始第一份方案', description: '进入规划页生成建议' }
])

function describeRecentPlan(task) {
  const status = String(task?.status)
  if (status === '1') {
    return '这份方案已经完成计算，可以继续查看收益、布局和种植建议。'
  }
  if (status === '2') {
    return '这份方案暂时存在约束冲突，可以回去调整偏好后再试一次。'
  }
  if (status === '4') {
    return '系统正在为你推演这份方案，稍后就能看到结果。'
  }
  if (status === '3') {
    return '这次尝试没有顺利完成，可以稍后重新发起一次新的计算。'
  }
  return '这份方案已经创建完成，接下来可以继续调整并生成结果。'
}

function buildMiniPieStyle(results) {
  if (!results.length) {
    return ''
  }
  const total = results.reduce((sum, item) => sum + Number(item.profitAmount || item.experienceScore || 0), 0)
  if (total <= 0) {
    return ''
  }
  const palette = ['#2f5f4a', '#d59a62', '#8aa86c', '#d86f57', '#7b8eb1']
  let start = 0
  const segments = results.map((item, index) => {
    const value = Number(item.profitAmount || item.experienceScore || 0)
    const angle = (value / total) * 100
    const end = start + angle
    const color = palette[index % palette.length]
    const segment = `${color} ${start}% ${end}%`
    start = end
    return segment
  })
  return `conic-gradient(${segments.join(', ')})`
}

function openTaskResults(taskId) {
  router.push({ path: '/portal/results', query: { taskId } })
}

function handleRecentPlanCardClick() {
  if (recentPlan.value) {
    openTaskResults(recentPlan.value.taskId)
    return
  }
  router.push('/portal/tasks')
}

async function loadDashboard() {
  const [taskRes, landRes, containerRes, cropRes, resultRes] = await Promise.all([
    listPortalTask({ pageNum: 1, pageSize: 6 }),
    listPortalLand({ pageNum: 1, pageSize: 1000 }),
    listPortalContainer({ pageNum: 1, pageSize: 1000 }),
    listPortalCrop({ pageNum: 1, pageSize: 12 }),
    listPortalResult({ pageNum: 1, pageSize: 1000 })
  ])

  recentTasks.value = taskRes.rows || []
  cropLibrary.value = cropRes.rows || []
  summary.lands = Number(landRes.total || 0)
  summary.containers = Number(containerRes.total || 0)

  const totalSites = (containerRes.rows || []).reduce((sum, item) => sum + Number(item.plantingSites || 0), 0)
  const usedSites = (resultRes.rows || []).reduce((sum, item) => sum + Number(item.plantCount || 0), 0)
  containerLoadRate.value = totalSites > 0 ? Math.min(usedSites / totalSites, 1) : 0

  const latestSuccessTask = recentTasks.value.find((item) => String(item.status) === '1')
  if (latestSuccessTask) {
    const resultByTaskRes = await listPortalResultByTask(latestSuccessTask.taskId)
    recentPieStyle.value = buildMiniPieStyle(resultByTaskRes.rows || [])
  } else {
    recentPieStyle.value = ''
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped lang="scss">
.portal-dashboard {
  display: grid;
  gap: 24px;
}

.hero-panel,
.dashboard-card {
  border-radius: 24px;
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 28px;
  padding: 34px 36px;
  background:
    radial-gradient(circle at top right, rgba(90, 126, 103, 0.16), transparent 26%),
    linear-gradient(135deg, rgba(255, 250, 243, 0.96), rgba(236, 224, 206, 0.94));
  border: 1px solid rgba(120, 93, 63, 0.08);
  box-shadow: 0 24px 44px rgba(102, 78, 52, 0.08);
}

.hero-copy {
  flex: 1;
}

.eyebrow,
.section-kicker {
  display: inline-block;
  color: #956d4f;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 12px;
}

.hero-panel h2 {
  margin: 12px 0 10px;
  color: #2f4334;
  font-size: 42px;
  line-height: 1.12;
}

.hero-panel p,
.plan-copy p,
.dashboard-empty-state p,
.timeline-body p,
.season-summary,
.season-fallback {
  margin: 0;
  color: #685c4f;
  line-height: 1.75;
}

.hero-actions,
.plan-actions {
  display: flex;
  gap: 14px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.hero-primary-button,
.hero-secondary-button,
.subtle-secondary-button {
  height: 42px;
  padding: 0 20px;
  border: none;
  border-radius: 999px;
  font-size: 15px;
}

.hero-primary-button {
  background: #4c9e68;
  color: #fff;
  box-shadow: 0 10px 22px rgba(76, 158, 104, 0.22);
}

.hero-primary-button:hover,
.hero-primary-button:focus {
  background: #438f5d;
}

.hero-secondary-button,
.subtle-secondary-button {
  background: #eef1ee;
  color: #4d5f56;
}

.hero-secondary-button:hover,
.hero-secondary-button:focus,
.subtle-secondary-button:hover,
.subtle-secondary-button:focus {
  background: #e5ebe7;
  color: #32443b;
}

.hero-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  margin-top: 14px;
}

.hero-stat-inline {
  color: #8d6f57;
  font-size: 12px;
  line-height: 1.5;
}

.hero-visual {
  width: min(28vw, 280px);
  min-width: 220px;
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0.8;
}

.status-illustration {
  position: relative;
  width: 100%;
  max-width: 248px;
  aspect-ratio: 1 / 1.02;
  max-height: 220px;
  display: grid;
  place-items: center;
  padding: 18px 18px 28px;
}

.status-halo {
  position: absolute;
  inset: 8% 10% 16%;
  border-radius: 36px;
  background:
    radial-gradient(circle at 50% 36%, rgba(243, 250, 245, 0.95), rgba(243, 250, 245, 0.18) 58%, rgba(243, 250, 245, 0) 76%),
    linear-gradient(180deg, rgba(236, 246, 238, 0.58), rgba(245, 232, 214, 0.16));
}

.status-shadow {
  position: absolute;
  left: 50%;
  bottom: 18px;
  width: 42%;
  height: 12%;
  border-radius: 50%;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(92, 79, 63, 0.14), rgba(92, 79, 63, 0));
}

.status-pot {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
  width: 112px;
  height: 92px;
  margin-top: 68px;
  border-radius: 22px 22px 28px 28px;
  background: linear-gradient(180deg, #cf9c6d, #b17b53);
  box-shadow: 0 16px 28px rgba(157, 110, 74, 0.14);
}

.pot-rim,
.pot-soil,
.sprout-stem,
.sprout-leaf {
  display: block;
}

.pot-rim {
  position: absolute;
  left: 50%;
  top: -9px;
  width: 76%;
  height: 14px;
  border-radius: 999px;
  transform: translateX(-50%);
  background: rgba(160, 111, 74, 0.24);
}

.pot-soil {
  position: absolute;
  left: 50%;
  top: 10px;
  width: 70%;
  height: 12px;
  transform: translateX(-50%);
  border-radius: 999px;
  background: linear-gradient(180deg, #7f624b, #5d4332);
}

.status-sprout {
  position: absolute;
  left: 50%;
  bottom: 92px;
  z-index: 3;
  width: 70px;
  height: 86px;
  transform: translateX(-50%);
}

.sprout-stem {
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 8px;
  height: 58px;
  border-radius: 999px;
  transform: translateX(-50%);
  background: linear-gradient(180deg, #7aac83, #4f7f5d);
}

.sprout-leaf {
  position: absolute;
  bottom: 32px;
  width: 26px;
  height: 36px;
  border-radius: 100% 0;
  background: linear-gradient(180deg, #76a782, #4f7f5d);
}

.sprout-leaf-left {
  left: 10px;
  transform: rotate(-42deg);
}

.sprout-leaf-right {
  right: 10px;
  transform: scaleX(-1) rotate(-42deg);
}

.overview-grid,
.activity-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
  align-items: stretch;
}

.dashboard-card {
  height: 100%;
  border: 1px solid rgba(110, 91, 68, 0.1);
  background: rgba(255, 255, 255, 0.8);
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.clickable-card {
  cursor: pointer;
}

.clickable-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 32px rgba(93, 73, 52, 0.08);
  border-color: rgba(110, 91, 68, 0.14);
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: end;
  margin-bottom: 12px;
}

.section-head h3 {
  margin: 8px 0 0;
  color: #304435;
  font-size: 24px;
}

.recent-plan-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 148px;
  gap: 18px;
  align-items: center;
  min-height: 100%;
}

.plan-copy h4 {
  margin: 16px 0 8px;
  color: #304435;
  font-size: 28px;
}

.plan-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.plan-visual {
  display: grid;
  place-items: center;
  min-height: 152px;
  padding: 8px;
}

.mini-pie {
  width: 126px;
  height: 126px;
  border-radius: 50%;
  box-shadow: inset 0 0 0 20px rgba(255, 255, 255, 0.9);
}

.plan-placeholder {
  display: grid;
  gap: 8px;
  text-align: center;
}

.plan-placeholder strong {
  color: #304435;
  font-size: 22px;
}

.dashboard-empty-state {
  display: grid;
  gap: 10px;
  justify-items: start;
  padding: 4px 0;
}

.dashboard-empty-state.inline {
  min-height: 64px;
  gap: 6px;
}

.dashboard-empty-state strong {
  color: #304435;
  font-size: 18px;
}

.season-card {
  background: rgba(255, 255, 255, 0.66);
  border-color: rgba(110, 91, 68, 0.08);
}

.season-summary {
  margin-bottom: 10px;
  font-size: 13px;
  line-height: 1.65;
}

.season-chip-list {
  display: grid;
  gap: 8px;
  margin-top: auto;
}

.season-chip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(248, 244, 236, 0.7), rgba(239, 247, 234, 0.66));
}

.season-chip__visual {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.86);
  overflow: hidden;
  flex-shrink: 0;
}

.season-chip__visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.season-chip__visual span {
  font-size: 20px;
}

.season-chip strong {
  color: #304435;
  font-size: 14px;
}

.activity-card {
  min-height: 0;
}

.timeline.compact {
  display: grid;
  gap: 8px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 18px minmax(0, 1fr);
  gap: 12px;
}

.timeline-dot {
  width: 18px;
  height: 18px;
  margin-top: 6px;
  border-radius: 50%;
  background: #6e89c8;
}

.status-1 {
  background: #4f9368;
}

.status-2,
.status-3 {
  background: #cc6e59;
}

.timeline-body strong {
  display: block;
  color: #304435;
  font-size: 14px;
}

.timeline-body p {
  font-size: 13px;
  line-height: 1.55;
}

.action-list {
  display: grid;
  gap: 0;
  margin: 0;
  padding: 0;
  list-style: none;
}

.action-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 44px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(227, 234, 230, 0.9);
}

.action-list li:last-child {
  border-bottom: none;
}

.action-list strong {
  color: #304435;
  font-size: 14px;
  white-space: nowrap;
}

.action-list span {
  color: #685c4f;
  font-size: 13px;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.dashboard-card .el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 24px;
}

.season-card :deep(.el-card__body),
.activity-card :deep(.el-card__body) {
  padding: 22px 24px;
}

@media (max-width: 1100px) {
  .overview-grid,
  .activity-grid,
  .recent-plan-body {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-panel,
  .section-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-panel {
    padding: 26px 22px;
  }

  .hero-panel h2 {
    font-size: 34px;
  }

  .hero-stats,
  .action-list li {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-list span {
    white-space: normal;
  }

  .hero-visual {
    width: 100%;
    min-width: 0;
  }

  .status-illustration {
    max-width: 220px;
  }
}
</style>
