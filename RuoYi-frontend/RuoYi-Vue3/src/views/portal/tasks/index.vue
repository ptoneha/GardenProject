<template>
  <div class="portal-task-page">
    <section class="portal-task-hero">
      <div class="hero-copy">
        <span class="eyebrow">Planning Hub</span>
        <h1>从这里开始一份新的种植计划</h1>
        <p>从一次简单输入开始，让系统帮你找到更适合当前季节和空间的种植组合。</p>
      </div>
      <div class="hero-action">
        <span class="action-note">几步设置之后，你就能直接看到推荐方案、空间分配和可执行的种植组合。</span>
        <button type="button" class="launch-button" @click="openAdd">
          <span class="launch-glow"></span>
          <span>开始新的种植计划</span>
        </button>
      </div>
    </section>

    <section v-if="archiveList.length" class="archive-intro">
      <div>
        <span class="section-kicker">Your Archives</span>
        <h2>这些是你已经生成过的种植计划</h2>
        <p>每一份都可以被回看、继续调整，或者作为下一次规划的起点。</p>
      </div>
    </section>

    <section v-if="archiveList.length" class="portal-search-line">
      <el-input
        v-model="queryParams.taskName"
        class="archive-search"
        placeholder="搜索你想回看的方案名称"
        clearable
        @keyup.enter="handleQuery"
      />
      <button type="button" class="icon-trigger" aria-label="搜索方案" @click="handleQuery">
        <el-icon><Search /></el-icon>
      </button>
      <button type="button" class="icon-trigger" aria-label="重置搜索" @click="resetQuery">
        <el-icon><Refresh /></el-icon>
      </button>
    </section>

    <section class="archive-shell" v-loading="loading">
      <el-empty
        v-if="!archiveList.length"
        description="你还没有任何种植方案"
      >
        <template #image>
          <div class="empty-illustration">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </template>
        <div class="empty-copy">
          <p class="empty-subtitle">从第一份计划开始，系统会帮你记录每一次推荐与选择。</p>
          <el-button class="soft-green-button" type="primary" @click="openAdd">开始第一份种植计划</el-button>
        </div>
        <div class="empty-preview">
          <span class="empty-preview-title">未来你会在这里看到：</span>
          <div class="empty-preview-list">
            <span>春夏秋冬的种植方案</span>
            <span>不同空间下的推荐变化</span>
            <span>可重复使用的组合记录</span>
          </div>
        </div>
      </el-empty>

      <div v-else class="archive-grid">
        <article v-for="item in archiveList" :key="item.taskId" class="archive-card">
          <div class="archive-cover">
            <div class="crop-stack">
              <div v-for="preview in item.previews" :key="`${item.taskId}-${preview.cropId}`" class="crop-thumb">
                <img v-if="preview.visual.type === 'image'" :src="preview.visual.value" :alt="preview.cropName">
                <span v-else>{{ preview.visual.value }}</span>
              </div>
              <div v-if="!item.previews.length" class="crop-thumb placeholder">
                <span>🌱</span>
              </div>
            </div>
          </div>

          <div class="archive-body">
            <span class="archive-date">{{ parseTime(item.createTime, '{y}-{m}-{d}') }}</span>
            <h3>{{ item.taskName }}</h3>
            <p>{{ item.archiveSummary }}</p>

            <div class="archive-metrics">
              <div>
                <span>核心指标</span>
                <strong>{{ item.metricText }}</strong>
              </div>
              <div>
                <span>方案条目</span>
                <strong>{{ item.resultCount }} 条</strong>
              </div>
            </div>

            <div class="archive-actions">
              <el-button link type="primary" @click="viewResult(item)">查看方案</el-button>
              <el-button link type="warning" @click="openEdit(item)">重新规划</el-button>
              <el-button link type="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </div>
        </article>
      </div>
    </section>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <task-wizard
      v-model="wizardOpen"
      :task="wizardTask"
      :selected-land-ids="wizardLandIds"
      :lands="landOptions"
      :crops="cropOptions"
      :containers="containerOptions"
      :submitting="wizardSubmitting"
      @submit="handleWizardSubmit"
    />

    <optimize-loading v-model="executeLoading" :title="loadingTitle" />

    <agri-suggestion-dialog
      v-model="suggestionOpen"
      :task-name="suggestionTaskName"
      :detail="suggestionDetail"
    />
  </div>
</template>

<script setup name="PortalTasks">
import { Search, Refresh } from '@element-plus/icons-vue'
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { parseTime } from '@/utils/ruoyi'
import {
  addPortalTask,
  addPortalTaskLand,
  delPortalTask,
  delPortalTaskLand,
  executePortalTask,
  getPortalTask,
  listPortalContainer,
  listPortalCrop,
  listPortalLand,
  listPortalResultByTask,
  listPortalTask,
  listPortalTaskLand,
  updatePortalTask
} from '@/api/portal/agriculture'
import TaskWizard from '@/components/agriculture/TaskWizard.vue'
import OptimizeLoading from '@/components/agriculture/OptimizeLoading.vue'
import AgriSuggestionDialog from '@/components/agriculture/AgriSuggestionDialog.vue'
import { getCropVisual, resolveAgriErrorGuide, saveTaskFeedback } from '@/utils/agriculture'

const { proxy } = getCurrentInstance()
const router = useRouter()

const loading = ref(false)
const total = ref(0)
const taskList = ref([])
const landOptions = ref([])
const containerOptions = ref([])
const cropOptions = ref([])
const portalMetrics = ref({})

const wizardOpen = ref(false)
const wizardSubmitting = ref(false)
const wizardTask = ref({})
const wizardLandIds = ref([])
const currentTaskLandIds = ref([])

const executeLoading = ref(false)
const loadingTitle = ref('正在生成你的种植方案')
const suggestionOpen = ref(false)
const suggestionTaskName = ref('')
const suggestionDetail = ref({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 9,
  taskName: ''
})

const archiveList = computed(() =>
  taskList.value.map((item) => {
    const metric = portalMetrics.value[item.taskId] || {}
    const metricText = Number(item.mode) === 1
      ? `${Number(metric.totalProfit || 0).toFixed(2)} 元`
      : `${Number(metric.totalExperience || 0).toFixed(0)} 分`
    return {
      ...item,
      previews: metric.previews || [],
      resultCount: metric.resultCount || 0,
      metricText,
      archiveSummary: buildArchiveSummary(item, metric)
    }
  })
)

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.taskName = ''
  handleQuery()
}

async function loadOptions() {
  const [landRes, containerRes, cropRes] = await Promise.all([
    listPortalLand({ pageNum: 1, pageSize: 1000 }),
    listPortalContainer({ pageNum: 1, pageSize: 1000 }),
    listPortalCrop({ pageNum: 1, pageSize: 1000 })
  ])
  landOptions.value = landRes.rows || []
  containerOptions.value = containerRes.rows || []
  cropOptions.value = cropRes.rows || []
}

async function loadMetrics(list) {
  const cropMap = new Map(cropOptions.value.map((item) => [item.cropId, item]))
  const metricsEntries = await Promise.all(
    list.map(async (item) => {
      const resultRes = await listPortalResultByTask(item.taskId)
      const rows = resultRes.rows || []
      const previews = rows.slice(0, 3).map((row) => {
        const crop = cropMap.get(row.cropId)
        return {
          cropId: row.cropId,
          cropName: crop?.cropName || '作物',
          visual: getCropVisual(crop)
        }
      })
      return [
        item.taskId,
        {
          totalProfit: rows.reduce((sum, row) => sum + Number(row.profitAmount || 0), 0),
          totalExperience: rows.reduce((sum, row) => sum + Number(row.experienceScore || 0), 0),
          resultCount: rows.length,
          previews
        }
      ]
    })
  )
  portalMetrics.value = Object.fromEntries(metricsEntries)
}

async function getList() {
  loading.value = true
  try {
    const response = await listPortalTask({
      ...queryParams,
      status: '1'
    })
    taskList.value = response.rows || []
    total.value = response.total || 0
    await loadMetrics(taskList.value)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  wizardTask.value = {}
  wizardLandIds.value = []
  currentTaskLandIds.value = []
  wizardOpen.value = true
}

async function openEdit(row) {
  const [taskRes, taskLandRes] = await Promise.all([
    getPortalTask(row.taskId),
    listPortalTaskLand({ pageNum: 1, pageSize: 1000, taskId: row.taskId })
  ])
  wizardTask.value = taskRes.data || {}
  wizardLandIds.value = (taskLandRes.rows || []).map((item) => item.landId)
  currentTaskLandIds.value = [...wizardLandIds.value]
  wizardOpen.value = true
}

async function persistTask(taskPayload) {
  if (taskPayload.taskId) {
    await updatePortalTask(taskPayload)
    return { ...taskPayload }
  }
  const response = await addPortalTask(taskPayload)
  const savedTask = response.data || {}
  const taskId = savedTask.taskId || response.taskId
  if (!taskId) {
    throw new Error('新增任务后未获取到 taskId，请确认后端返回值')
  }
  return { ...taskPayload, ...savedTask, taskId }
}

async function syncTaskLand(taskId, landIds) {
  if (currentTaskLandIds.value.length > 0) {
    await delPortalTaskLand(taskId)
  }
  for (const landId of landIds) {
    await addPortalTaskLand({ taskId, landId })
  }
  currentTaskLandIds.value = [...landIds]
}

function buildSuggestion(error, taskSnapshot) {
  const payload = error.data || error.responseData?.data || {}
  const reasonCode = payload.infeasibleReason || 'ERR_INTERNAL'
  const guide = resolveAgriErrorGuide(reasonCode, taskSnapshot.taskSeason)
  return {
    code: reasonCode,
    message: payload.infeasibleReasonDesc || payload.message || error.message,
    ...guide
  }
}

async function runTask(taskId, taskSnapshot) {
  executeLoading.value = true
  loadingTitle.value = `${taskSnapshot.taskName || '当前方案'} 正在生成中`
  try {
    const response = await executePortalTask(taskId)
    const payload = response.data || {}
    saveTaskFeedback(taskId, {
      ...payload,
      taskName: taskSnapshot.taskName,
      taskSeason: taskSnapshot.taskSeason
    })
    await router.push({ path: '/portal/results', query: { taskId } })
  } catch (error) {
    const detail = buildSuggestion(error, taskSnapshot)
    suggestionTaskName.value = taskSnapshot.taskName
    suggestionDetail.value = detail
    suggestionOpen.value = true
    saveTaskFeedback(taskId, {
      ...(error.data || error.responseData?.data || {}),
      taskName: taskSnapshot.taskName,
      taskSeason: taskSnapshot.taskSeason,
      message: detail.message
    })
    await getList()
  } finally {
    executeLoading.value = false
  }
}

async function handleWizardSubmit(payload) {
  wizardSubmitting.value = true
  try {
    const savedTask = await persistTask(payload.task)
    await syncTaskLand(savedTask.taskId, payload.landIds)
    wizardOpen.value = false

    if (payload.execute) {
      await runTask(savedTask.taskId, savedTask)
      return
    }

    proxy.$modal.msgSuccess('方案草稿已保存')
    await getList()
  } catch (error) {
    ElMessage.error(error.message || '任务保存失败')
  } finally {
    wizardSubmitting.value = false
  }
}

function viewResult(row) {
  router.push({ path: '/portal/results', query: { taskId: row.taskId } })
}

function handleDelete(row) {
  proxy.$modal.confirm(`确认删除方案 ${row.taskName} 吗？`).then(async () => {
    await delPortalTask(row.taskId)
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function buildArchiveSummary(task, metric) {
  if (Number(task.mode) === 1) {
    return `这份方案更偏收益导向，当前累计预期利润约 ${Number(metric.totalProfit || 0).toFixed(2)} 元。`
  }
  return `这份方案更偏布局与体验，当前累计体验得分约 ${Number(metric.totalExperience || 0).toFixed(0)} 分。`
}

onMounted(async () => {
  await Promise.all([loadOptions(), getList()])
})
</script>

<style scoped lang="scss">
.portal-task-page {
  display: grid;
  gap: 20px;
  min-height: 100%;
  padding-bottom: 12px;
  background: #f7f8f2;
}

.portal-task-hero,
.archive-intro,
.portal-search-line,
.archive-card {
  border-radius: 24px;
}

.portal-task-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 38px 40px;
  background:
    radial-gradient(circle at top right, rgba(147, 197, 135, 0.16), transparent 30%),
    linear-gradient(135deg, #fffdf8 0%, #f4fbf2 55%, #ebf7ee 100%);
  border: 1px solid rgba(164, 191, 157, 0.22);
}

.hero-copy {
  max-width: 660px;
}

.eyebrow {
  display: inline-block;
  color: #7a8f71;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 12px;
}

.portal-task-hero h1 {
  margin: 12px 0 10px;
  color: #314637;
  font-size: 40px;
}

.portal-task-hero p {
  margin: 0;
  max-width: 640px;
  color: #65725f;
  line-height: 1.8;
}

.hero-action {
  display: grid;
  gap: 14px;
  justify-items: end;
  max-width: 320px;
}

.action-note {
  color: #7e8f79;
  line-height: 1.7;
  font-size: 13px;
  text-align: right;
}

.launch-button {
  position: relative;
  overflow: hidden;
  min-width: 260px;
  min-height: 64px;
  padding: 0 28px;
  border: none;
  border-radius: 999px;
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  box-shadow: 0 14px 28px rgba(111, 179, 123, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.launch-glow {
  position: absolute;
  inset: auto auto -28px -10px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.28), rgba(255, 255, 255, 0));
}

.portal-search-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0 2px;
}

.archive-search {
  max-width: 460px;
}

.archive-search :deep(.el-input__wrapper) {
  border-radius: 40px;
  padding: 0 18px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 0 0 1px rgba(160, 185, 154, 0.18) inset;
}

.icon-trigger {
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #789273;
  cursor: pointer;
  display: grid;
  place-items: center;
  transition: background 0.2s ease, transform 0.2s ease;
}

.icon-trigger:hover {
  background: rgba(241, 248, 239, 0.72);
  transform: translateY(-1px);
}

.archive-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 18px;
}

.archive-card {
  overflow: hidden;
  border: 1px solid rgba(163, 184, 156, 0.14);
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 16px 34px rgba(122, 139, 116, 0.08);
}

.archive-cover {
  min-height: 144px;
  padding: 22px;
  background:
    radial-gradient(circle at top right, rgba(149, 199, 139, 0.18), transparent 36%),
    linear-gradient(135deg, rgba(255, 253, 248, 0.96), rgba(241, 248, 239, 0.92));
}

.crop-stack {
  display: flex;
  align-items: center;
}

.crop-thumb {
  width: 64px;
  height: 64px;
  margin-right: -10px;
  border-radius: 20px;
  border: 4px solid rgba(255, 255, 255, 0.92);
  background: rgba(255, 255, 255, 0.88);
  display: grid;
  place-items: center;
  box-shadow: 0 10px 24px rgba(122, 139, 116, 0.14);
}

.crop-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
}

.crop-thumb span {
  font-size: 28px;
}

.archive-body {
  display: grid;
  gap: 14px;
  padding: 22px;
}

.archive-date {
  color: #8a8f79;
  font-size: 13px;
}

.archive-body h3 {
  margin: 0;
  color: #314637;
  font-size: 28px;
}

.archive-body p {
  margin: 0;
  color: #687061;
  line-height: 1.8;
}

.archive-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.archive-metrics div {
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(241, 248, 239, 0.72);
}

.archive-metrics span {
  display: block;
  color: #7d8a75;
  font-size: 12px;
}

.archive-metrics strong {
  display: block;
  margin-top: 8px;
  color: #314637;
  font-size: 22px;
}

.archive-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.launch-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 32px rgba(111, 179, 123, 0.24);
}

.archive-intro {
  padding: 24px 28px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.78), rgba(241, 248, 239, 0.72));
  border: 1px solid rgba(163, 184, 156, 0.14);
}

.section-kicker {
  display: inline-block;
  color: #89a083;
  font-size: 11px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.archive-intro h2 {
  margin: 10px 0 8px;
  color: #314637;
  font-size: 26px;
}

.archive-intro p {
  margin: 0;
  color: #6f7868;
  line-height: 1.8;
}

.archive-shell :deep(.el-empty) {
  padding: 54px 0 40px;
}

.archive-shell :deep(.el-empty__description) {
  margin-top: 14px;
}

.archive-shell :deep(.el-empty__description p) {
  color: #314637;
  font-size: 28px;
  font-weight: 600;
  line-height: 1.35;
}

.empty-illustration {
  position: relative;
  width: 160px;
  height: 120px;
  margin: 0 auto 8px;
}

.empty-illustration span {
  position: absolute;
  bottom: 0;
  border-radius: 999px 999px 28px 28px;
  background: linear-gradient(180deg, rgba(223, 239, 220, 0.92), rgba(170, 209, 160, 0.92));
}

.empty-illustration span:nth-child(1) {
  left: 24px;
  width: 34px;
  height: 74px;
}

.empty-illustration span:nth-child(2) {
  left: 62px;
  width: 40px;
  height: 94px;
}

.empty-illustration span:nth-child(3) {
  left: 108px;
  width: 28px;
  height: 62px;
}

.empty-copy {
  display: grid;
  justify-items: center;
  gap: 16px;
  margin-top: 10px;
}

.empty-subtitle {
  max-width: 480px;
  margin: 0;
  color: #7a8678;
  line-height: 1.8;
}

.soft-green-button {
  min-width: 220px;
  border: none;
  color: #fff;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  box-shadow: 0 14px 24px rgba(111, 179, 123, 0.2);
}

.soft-green-button:hover,
.soft-green-button:focus {
  color: #fff;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
}

.empty-preview {
  display: grid;
  gap: 14px;
  max-width: 560px;
  margin: 24px auto 0;
  padding: 22px 24px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.78), rgba(241, 248, 239, 0.72));
  border: 1px solid rgba(163, 184, 156, 0.12);
}

.empty-preview-title {
  color: #6f7868;
  font-size: 14px;
}

.empty-preview-list {
  display: grid;
  gap: 10px;
}

.empty-preview-list span {
  position: relative;
  padding-left: 18px;
  color: #425244;
  line-height: 1.7;
}

.empty-preview-list span::before {
  content: '';
  position: absolute;
  top: 11px;
  left: 0;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
}

@media (max-width: 768px) {
  .portal-task-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-action {
    width: 100%;
    justify-items: stretch;
  }

  .action-note {
    text-align: left;
  }

  .portal-search-line {
    flex-wrap: wrap;
  }

  .archive-metrics {
    grid-template-columns: 1fr;
  }

  .launch-button {
    width: 100%;
  }
}
</style>
