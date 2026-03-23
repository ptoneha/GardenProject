<template>
  <div class="app-container agri-task-page">
    <section class="hero-panel">
      <div>
        <span class="eyebrow">Heirloom Planning</span>
        <h1>种植优化任务中心</h1>
        <p>把普通的任务管理，升级成一套可发起、可计算、可解释、可展示的种植规划门户。</p>
      </div>
      <el-button v-if="canManageTask" type="primary" size="large" @click="openAdd">
        新建规划任务
      </el-button>
    </section>

    <div class="summary-grid">
      <article v-for="card in summaryCards" :key="card.label">
        <span>{{ card.label }}</span>
        <strong>{{ card.value }}</strong>
        <p>{{ card.description }}</p>
      </article>
    </div>

    <el-card shadow="never" class="query-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.taskName" placeholder="搜索任务名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="模式">
          <el-select v-model="queryParams.mode" placeholder="全部模式" clearable>
            <el-option v-for="item in AGRI_MODE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
            <el-option v-for="item in AGRI_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table v-loading="loading" :data="taskList" class="task-table" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column label="任务名称" min-width="220">
        <template #default="{ row }">
          <div class="task-name">
            <strong>{{ row.taskName }}</strong>
            <span>#{{ row.taskId }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="模式" width="120">
        <template #default="{ row }">
          <el-tag effect="plain">{{ getAgriModeLabel(row.mode) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="季节" width="110">
        <template #default="{ row }">{{ getAgriSeasonLabel(row.taskSeason) }}</template>
      </el-table-column>
      <el-table-column label="预算" width="120">
        <template #default="{ row }">{{ Number(row.totalBudget || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="豆类比例" width="120">
        <template #default="{ row }">{{ toPercent(row.minPulseRatio || 0) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getAgriStatusMeta(row.status).tagType || 'info'">{{ getAgriStatusMeta(row.status).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ parseTime(row.createTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="300" fixed="right">
        <template #default="{ row }">
          <el-button v-if="canManageTask" link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="canManageTask" link type="success" @click="handleExecute(row)">执行优化</el-button>
          <el-button link type="warning" @click="viewResult(row)">查看结果</el-button>
          <el-button v-if="canDeleteTask" link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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

<script setup name="Task">
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { parseTime } from '@/utils/ruoyi'
import { addTaskLand, delTaskLand, listTaskLand } from '@/api/agriculture/taskLand'
import { listLand } from '@/api/agriculture/land'
import { listContainer } from '@/api/agriculture/container'
import { listCrop } from '@/api/agriculture/crop'
import { addTask, delTask, executeTask, getTask, listTask, updateTask } from '@/api/agriculture/task'
import TaskWizard from '@/components/agriculture/TaskWizard.vue'
import OptimizeLoading from '@/components/agriculture/OptimizeLoading.vue'
import AgriSuggestionDialog from '@/components/agriculture/AgriSuggestionDialog.vue'
import {
  AGRI_MODE_OPTIONS,
  AGRI_STATUS_OPTIONS,
  getAgriModeLabel,
  getAgriSeasonLabel,
  getAgriStatusMeta,
  resolveAgriErrorGuide,
  saveTaskFeedback,
  toPercent
} from '@/utils/agriculture'

const { proxy } = getCurrentInstance()
const router = useRouter()

const canManageTask = computed(() => proxy.$auth.hasPermi('agriculture:task:add') || proxy.$auth.hasPermi('agriculture:task:edit'))
const canDeleteTask = computed(() => proxy.$auth.hasPermi('agriculture:task:remove'))

const loading = ref(false)
const total = ref(0)
const taskList = ref([])
const landOptions = ref([])
const containerOptions = ref([])
const cropOptions = ref([])
const ids = ref([])

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
  pageSize: 10,
  taskName: '',
  mode: null,
  status: null
})

const summaryCards = computed(() => {
  const success = taskList.value.filter((item) => String(item.status) === '1').length
  const infeasible = taskList.value.filter((item) => String(item.status) === '2').length
  const running = taskList.value.filter((item) => String(item.status) === '4').length
  return [
    { label: '当前列表任务', value: taskList.value.length, description: '本页分页结果中的总任务数' },
    { label: '已计算成功', value: success, description: '可直接进入结果页查看' },
    { label: '待调优任务', value: infeasible, description: '存在约束冲突或资源不足' },
    { label: '执行中', value: running, description: '后台正在调用 Python 求解器' }
  ]
})

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.taskId)
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.taskName = ''
  queryParams.mode = null
  queryParams.status = null
  handleQuery()
}

async function loadOptions() {
  const [landRes, containerRes, cropRes] = await Promise.all([
    listLand({ pageNum: 1, pageSize: 1000 }),
    listContainer({ pageNum: 1, pageSize: 1000 }),
    listCrop({ pageNum: 1, pageSize: 1000 })
  ])
  landOptions.value = landRes.rows || []
  containerOptions.value = containerRes.rows || []
  cropOptions.value = cropRes.rows || []
}

async function getList() {
  loading.value = true
  try {
    const response = await listTask(queryParams)
    taskList.value = response.rows || []
    total.value = response.total || 0
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
  const taskId = row?.taskId || ids.value[0]
  if (!taskId) {
    ElMessage.warning('请先选择一个任务')
    return
  }
  const [taskRes, taskLandRes] = await Promise.all([
    getTask(taskId),
    listTaskLand({ pageNum: 1, pageSize: 1000, taskId })
  ])
  wizardTask.value = taskRes.data || {}
  wizardLandIds.value = (taskLandRes.rows || []).map((item) => item.landId)
  currentTaskLandIds.value = [...wizardLandIds.value]
  wizardOpen.value = true
}

async function persistTask(taskPayload) {
  if (taskPayload.taskId) {
    await updateTask(taskPayload)
    return { ...taskPayload }
  }
  const response = await addTask(taskPayload)
  const savedTask = response.data || {}
  const taskId = savedTask.taskId || response.taskId
  if (!taskId) {
    throw new Error('新增任务后未获取到 taskId，请确认后端返回值')
  }
  return { ...taskPayload, ...savedTask, taskId }
}

async function syncTaskLand(taskId, landIds) {
  if (currentTaskLandIds.value.length > 0) {
    await delTaskLand(taskId)
  }
  for (const landId of landIds) {
    await addTaskLand({ taskId, landId })
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
    const response = await executeTask(taskId)
    const payload = response.data || {}
    saveTaskFeedback(taskId, {
      ...payload,
      taskName: taskSnapshot.taskName,
      taskSeason: taskSnapshot.taskSeason
    })
    await router.push({ path: '/agriculture/result', query: { taskId } })
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

async function handleExecute(row) {
  await runTask(row.taskId, row)
}

function viewResult(row) {
  router.push({ path: '/agriculture/result', query: { taskId: row.taskId } })
}

function handleDelete(row) {
  const taskId = row?.taskId || ids.value.join(',')
  if (!taskId) {
    ElMessage.warning('请先选择要删除的任务')
    return
  }
  proxy.$modal.confirm(`确认删除方案 ${taskId} 吗？`).then(async () => {
    await delTask(taskId)
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

onMounted(async () => {
  await Promise.all([loadOptions(), getList()])
})
</script>

<style scoped lang="scss">
.agri-task-page {
  display: grid;
  gap: 20px;
}

.hero-panel,
.query-card,
.summary-grid article,
.task-table {
  border-radius: 24px;
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 26px 28px;
  background:
    radial-gradient(circle at top right, rgba(190, 138, 85, 0.18), transparent 30%),
    linear-gradient(135deg, #fffaf2, #efe1c9 80%);
}

.eyebrow {
  display: inline-block;
  color: #9f714e;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 12px;
}

.hero-panel h1 {
  margin: 12px 0 10px;
  color: #394339;
  font-size: 40px;
}

.hero-panel p {
  margin: 0;
  max-width: 640px;
  color: #6f6457;
  line-height: 1.8;
}

.summary-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.summary-grid article {
  padding: 18px 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(244, 235, 220, 0.92));
  border: 1px solid rgba(117, 94, 68, 0.1);
}

.summary-grid span {
  color: #92735a;
}

.summary-grid strong {
  display: block;
  margin: 10px 0 6px;
  font-size: 28px;
  color: #334633;
}

.summary-grid p {
  margin: 0;
  color: #6f6458;
}

.query-card {
  background: rgba(255, 255, 255, 0.84);
}

.task-name strong {
  display: block;
  color: #394339;
}

.task-name span {
  color: #8d7158;
  font-size: 13px;
}

.task-table :deep(.el-table__row) {
  --el-table-tr-bg-color: rgba(255, 255, 255, 0.86);
}

@media (max-width: 992px) {
  .summary-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .hero-panel {
    flex-direction: column;
    align-items: flex-start;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
