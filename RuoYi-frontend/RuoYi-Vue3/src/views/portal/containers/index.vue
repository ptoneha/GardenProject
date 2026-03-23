<template>
  <div class="portal-container-page">
    <header class="page-header">
      <p class="page-tip">
        <span>已记录的容器将用于生成更贴近实际的种植方案</span>
      </p>
      <div class="page-actions">
        <el-button class="primary-action" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新增容器</span>
        </el-button>
      </div>
    </header>

    <section class="list-shell" v-loading="loading">
      <div class="content-area">
        <div class="list-toolbar">
          <span class="toolbar-count">容器数量 {{ total }}</span>
          <div class="toolbar-actions">
            <transition name="toolbar-search-expand">
              <div v-if="searchOpen" class="toolbar-search">
                <el-input
                  v-model="queryParams.containerName"
                  placeholder="搜索容器名称"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </div>
            </transition>
            <el-button class="search-trigger" circle @click="toggleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
            <el-button class="toolbar-btn" :disabled="!containerList.length">筛选</el-button>
            <el-button class="toolbar-btn" :disabled="!containerList.length">排序</el-button>
          </div>
        </div>

        <div v-if="!containerList.length" class="inline-empty-state">
          <div class="empty-state-main">
            <div class="empty-illustration">
              <div class="empty-circle circle-a"></div>
              <div class="empty-circle circle-b"></div>
              <div class="empty-shape">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
            <h3>还没有任何容器</h3>
            <p>创建一个容器，开始规划你的种植空间</p>
            <el-button class="empty-primary-action" @click="handleAdd">创建容器</el-button>
          </div>

          <div class="future-preview">
            <button
              v-for="preview in previewContainerCards"
              :key="preview.name"
              type="button"
              class="preview-card"
              @click="openPreviewContainer(preview)"
            >
              <em>{{ preview.icon }}</em>
              <span class="preview-type">{{ preview.typeLabel }}</span>
              <strong>{{ preview.name }}</strong>
              <p>{{ preview.description }}</p>
              <div class="preview-meta">
                <small>容量：{{ preview.capacityLabel }}</small>
                <small>空间：{{ preview.spaceLabel }}</small>
              </div>
              <small class="preview-cta">{{ preview.cta }}</small>
            </button>
          </div>

          <div v-if="false" class="future-preview">
            <div class="preview-card">
              <em>🪴</em>
              <span class="preview-type">单体盆</span>
              <strong>窗边罗勒盆</strong>
              <p>少量香草或叶菜日常种植</p>
              <div class="preview-meta">
                <small>容量：少量</small>
                <small>空间：窗边种植区</small>
              </div>
            </div>
            <div class="preview-card">
              <em>🧺</em>
              <span class="preview-type">长条种植箱</span>
              <strong>阳台叶菜箱</strong>
              <p>适合多株组合和连续采收</p>
              <div class="preview-meta">
                <small>容量：中等</small>
                <small>空间：南侧阳台</small>
              </div>
            </div>
            <div class="preview-card">
              <em>🪜</em>
              <span class="preview-type">立体种植架</span>
              <strong>角落草莓架</strong>
              <p>分层摆放，提升立体利用</p>
              <div class="preview-meta">
                <small>容量：较多</small>
                <small>空间：庭院角落架区</small>
              </div>
            </div>
          </div>
        </div>

        <el-row v-else :gutter="18">
          <el-col
            v-for="item in containerList"
            :key="item.containerId"
            :xs="24"
            :sm="12"
            :lg="8"
            :xl="6"
          >
            <BaseCard
              :title="item.containerName"
              :tag="getContainerTypeDisplayLabel(item.containerType)"
              :status="getContainerStatusMeta(item).key"
              :meta="`适合：${getContainerUseHint(item.containerType)} · 示例用途：${getRecommendedPlanting(item)}`"
              :actions="managementCardActions"
              clickable
              @card-click="openContainerDetail(item)"
              @action="handleContainerCardAction(item, $event)"
            >
              <template #body>
                <div class="card-info-rows">
                  <div class="card-info-row">
                    <span>容量</span>
                    <strong>{{ getContainerCapacityLabel(item.plantingSites) }}</strong>
                  </div>
                  <div class="card-info-row">
                    <span>空间</span>
                    <strong>{{ getLandName(item.landId) }}</strong>
                  </div>
                  <div class="card-info-row">
                    <span>类型补充</span>
                    <strong>{{ getContainerTypeSummary(item.containerType) }}</strong>
                  </div>
                </div>
              </template>
            </BaseCard>
          </el-col>
        </el-row>
      </div>
    </section>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer
      v-model="detailDrawerOpen"
      :with-header="false"
      size="420px"
      append-to-body
      class="card-detail-drawer"
    >
      <template v-if="detailContainer">
        <div class="detail-sheet">
          <div class="detail-sheet__header">
            <div>
              <span class="detail-sheet__eyebrow">容器详情</span>
              <h3>{{ detailContainer.containerName }}</h3>
            </div>
            <span class="detail-sheet__status" :class="`status-${getContainerStatusMeta(detailContainer).key}`">
              {{ getContainerStatusMeta(detailContainer).label }}
            </span>
          </div>

          <div class="detail-sheet__grid">
            <div class="detail-sheet__row">
              <span>容器类型</span>
              <strong>{{ getContainerTypeDisplayLabel(detailContainer.containerType) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>容量</span>
              <strong>{{ getContainerCapacityLabel(detailContainer.plantingSites) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>所属空间</span>
              <strong>{{ getLandName(detailContainer.landId) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>适合用途</span>
              <strong>{{ getRecommendedPlanting(detailContainer) }}</strong>
            </div>
          </div>

          <p class="detail-sheet__hint">详情页尚未独立实现，当前先用详情抽屉承接卡片主体点击。</p>

          <div class="detail-sheet__actions">
            <el-button class="detail-edit-btn" @click="openContainerEditFromDetail">编辑</el-button>
            <el-button class="detail-delete-btn" @click="handleDeleteFromDetail">删除</el-button>
          </div>
        </div>
      </template>
    </el-drawer>

    <el-dialog :title="title" v-model="open" width="760px" append-to-body class="container-dialog">
      <el-form ref="containerRef" :model="form" :rules="rules" label-width="0">
        <section
          class="dialog-section dialog-step"
          :class="getStepStateClass(1)"
          :ref="(el) => setStepSectionRef(1, el)"
        >
          <div class="dialog-heading">
            <span>01</span>
            <div>
              <h4>这个容器要放到哪里？</h4>
              <p>你可以把它放进已有空间里，也可以先记下容器，稍后再补充空间归属。</p>
            </div>
          </div>
          <el-form-item class="compact-form-item">
            <div v-if="landOptions.length" class="space-grid">
              <button
                v-for="land in landOptions"
                :key="land.landId"
                type="button"
                class="space-card"
                :class="{ active: spaceBindingMode === 'land' && Number(form.landId) === Number(land.landId) }"
                @click="selectLandOption(land.landId)"
              >
                <strong>{{ land.landCode }}</strong>
                <span>{{ resolveSpaceDescription(land) }}</span>
              </button>
              <button
                type="button"
                class="space-card"
                :class="{ active: spaceBindingMode === 'skip' }"
                @click="selectSkipBinding"
              >
                <strong>暂时不绑定，稍后再补</strong>
                <span>先记录容器本身，之后在编辑容器时再补上空间归属</span>
              </button>
            </div>
            <div v-else class="space-choice-grid">
              <button type="button" class="space-card" @click="goToLands">
                <strong>去创建空间</strong>
                <span>先去创建一个地块或种植空间，再回来放这个容器</span>
              </button>
              <button
                type="button"
                class="space-card"
                :class="{ active: spaceBindingMode === 'skip' }"
                @click="selectSkipBinding"
              >
                <strong>暂时不绑定，稍后再补</strong>
                <span>先记录容器本身，之后在编辑容器时再补上空间归属</span>
              </button>
            </div>
          </el-form-item>
        </section>

        <section
          class="dialog-section dialog-step"
          :class="getStepStateClass(2)"
          :ref="(el) => setStepSectionRef(2, el)"
        >
          <div class="dialog-heading">
            <span>02</span>
            <div>
              <h4>这个容器你想怎么称呼？</h4>
              <p>给它起一个你自己容易识别的名字，后续查看方案会更轻松。</p>
            </div>
          </div>
          <el-form-item prop="containerName" class="compact-form-item">
            <el-input v-model="form.containerName" placeholder="比如窗边罗勒盆、番茄种植箱、角落架子" />
          </el-form-item>
        </section>

        <section
          class="dialog-section dialog-step"
          :class="getStepStateClass(3)"
          :ref="(el) => setStepSectionRef(3, el)"
        >
          <div class="dialog-heading">
            <span>03</span>
            <div>
              <h4>是什么类型</h4>
              <p>选择最接近真实情况的容器类型，系统会据此理解摆放方式和可用空间。</p>
            </div>
          </div>
          <el-form-item prop="containerType" class="compact-form-item">
            <div class="type-grid">
              <button
                v-for="dict in bus_container_type"
                :key="dict.value"
                type="button"
                class="type-card"
                :class="{ active: Number(form.containerType) === parseInt(dict.value) }"
                @click="form.containerType = parseInt(dict.value)"
              >
                <em>{{ getContainerTypeIcon(parseInt(dict.value)) }}</em>
                <strong>{{ getContainerTypeDisplayLabel(parseInt(dict.value)) }}</strong>
                <span>{{ getContainerTypeSummary(parseInt(dict.value)) }}</span>
              </button>
            </div>
          </el-form-item>
        </section>

        <section
          class="dialog-section dialog-step"
          :class="getStepStateClass(4)"
          :ref="(el) => setStepSectionRef(4, el)"
        >
          <div class="dialog-heading">
            <span>04</span>
            <div>
              <h4>{{ capacityStepTitle }}</h4>
              <p>不确定可以不填</p>
            </div>
          </div>
          <el-form-item class="compact-form-item">
            <div class="capacity-grid">
              <button
                v-for="option in capacityPresetOptions"
                :key="option.key"
                type="button"
                class="capacity-option"
                :class="{ active: getPlantingCapacityPreset(form.plantingSites) === option.key }"
                @click="togglePlantingSitesPreset(option)"
              >
                <strong>{{ option.label }}</strong>
                <span>{{ option.range }}</span>
              </button>
            </div>
          </el-form-item>
        </section>

        <section class="dialog-section dialog-section-secondary advanced-section">
          <button type="button" class="advanced-toggle" @click="showAdvancedSettings = !showAdvancedSettings">
            {{ showAdvancedSettings ? '收起补充信息' : '+ 补充更多信息（可选）' }}
          </button>
          <div v-if="showAdvancedSettings" class="advanced-panel">
            <div class="advanced-panel-copy">
              <strong>容器尺寸</strong>
              <p>这些信息属于补充项，后面再填也可以。</p>
            </div>
            <div class="size-grid">
              <el-form-item class="compact-form-item">
                <el-input v-model="form.depthCm" placeholder="深度">
                  <template #append>cm</template>
                </el-input>
              </el-form-item>
              <el-form-item class="compact-form-item">
                <el-input v-model="form.widthCm" placeholder="宽度">
                  <template #append>cm</template>
                </el-input>
              </el-form-item>
              <el-form-item class="compact-form-item">
                <el-input v-model="form.heightCm" placeholder="高度">
                  <template #append>cm</template>
                </el-input>
              </el-form-item>
            </div>
          </div>
        </section>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="secondary-action" @click="submitWithoutSupplement">跳过补充</el-button>
          <el-button class="primary-action" @click="submitForm">完成创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="PortalContainers">
import { computed, getCurrentInstance, nextTick, reactive, ref, toRefs, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search } from '@element-plus/icons-vue'
import BaseCard from '@/components/portal/BaseCard.vue'
import {
  addPortalContainer,
  delPortalContainer,
  getPortalContainer,
  listPortalContainer,
  listPortalLand,
  listPortalResult,
  updatePortalContainer
} from '@/api/portal/agriculture'
import { DEFAULT_MANAGEMENT_ACTIONS } from '@/utils/cardSystem'

const { proxy } = getCurrentInstance()
const router = useRouter()
const { bus_container_type } = proxy.useDict('bus_container_type')

const containerList = ref([])
const resultRows = ref([])
const landOptions = ref([])
const open = ref(false)
const loading = ref(true)
const searchOpen = ref(false)
const total = ref(0)
const title = ref('')
const detailDrawerOpen = ref(false)
const detailContainer = ref(null)
const spaceBindingMode = ref('')
const showAdvancedSettings = ref(false)
const stepSectionRefs = ref({})

const capacityPresetOptions = [
  { key: 'small', label: '少量', range: '1-3', value: 3 },
  { key: 'medium', label: '中等', range: '4-8', value: 6 },
  { key: 'large', label: '较多', range: '8+', value: 9 }
]
const previewContainerCards = [
  {
    icon: '🌿',
    name: '窗边罗勒盆',
    type: 1,
    typeLabel: '单体盆',
    description: '少量香草或叶菜日常种植',
    capacityLabel: '少量',
    capacityKey: 'small',
    spaceLabel: '窗边种植区',
    cta: '点击创建类似容器'
  },
  {
    icon: '🧺',
    name: '阳台叶菜箱',
    type: 2,
    typeLabel: '长条种植箱',
    description: '适合多株组合和连续采收',
    capacityLabel: '中等',
    capacityKey: 'medium',
    spaceLabel: '南侧阳台',
    cta: '点击创建类似容器'
  },
  {
    icon: '🪜',
    name: '角落草莓架',
    type: 3,
    typeLabel: '立体种植架',
    description: '分层摆放，提升立体利用',
    capacityLabel: '较多',
    capacityKey: 'large',
    spaceLabel: '庭院角落架区',
    cta: '点击创建类似容器'
  }
]
const managementCardActions = DEFAULT_MANAGEMENT_ACTIONS

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    containerName: null
  },
  rules: {
    containerName: [{ required: true, message: '请给容器起个名字', trigger: 'blur' }],
    containerType: [{ required: true, message: '请选择容器类型', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const isBindingStepCompleted = computed(() => {
  if (spaceBindingMode.value === 'land') {
    return form.value.landId != null && form.value.landId !== ''
  }
  return spaceBindingMode.value === 'skip'
})
const isNameCompleted = computed(() => Boolean(String(form.value.containerName || '').trim()))
const isContainerTypeCompleted = computed(() => form.value.containerType !== null && form.value.containerType !== undefined && form.value.containerType !== '')
const activeStep = computed(() => {
  if (!isBindingStepCompleted.value) {
    return 1
  }
  if (!isNameCompleted.value) {
    return 2
  }
  if (!isContainerTypeCompleted.value) {
    return 3
  }
  return 4
})
const capacityStepTitle = computed(() => `大概能放多少${usesPlantCountLabel(form.value.containerType) ? '株' : '盆'}？`)

const occupancyMap = computed(() => {
  const map = new Map()
  resultRows.value.forEach((item) => {
    const current = map.get(item.containerId) || 0
    map.set(item.containerId, current + Number(item.plantCount || 0))
  })
  return map
})

const landMap = computed(() => new Map(landOptions.value.map((item) => [item.landId, item.landCode])))

function getLandName(landId) {
  if (landId == null || landId === '') {
    return '未绑定空间'
  }
  return landMap.value.get(landId) || `空间 ${landId}`
}

function setStepSectionRef(step, el) {
  if (el) {
    stepSectionRefs.value[step] = el
  }
}

function selectLandOption(landId) {
  spaceBindingMode.value = 'land'
  form.value.landId = landId
}

function selectSkipBinding() {
  spaceBindingMode.value = 'skip'
  form.value.landId = null
}

function getContainerTypeIcon(type) {
  const iconMap = {
    1: '🪴',
    2: '🧺',
    3: '🪜',
    4: '吊'
  }
  return iconMap[Number(type)] || '容'
}

function getContainerTypeSummary(type) {
  const summaryMap = {
    1: '少量种植',
    2: '多株组合',
    3: '分层种植',
    4: '垂直种植'
  }
  return summaryMap[Number(type)] || '选择最接近真实情况的一项'
}

function getContainerTypeDisplayLabel(type) {
  const labelMap = {
    1: '单体盆',
    2: '长条种植箱',
    3: '立体种植架',
    4: '悬挂式 / 壁挂式'
  }
  return labelMap[Number(type)] || '容器类型'
}

function getContainerStatusMeta(item) {
  if (String(item.isActive) === '1') {
    return { key: 'disabled', label: '停用' }
  }
  if (item.landId == null || item.landId === '') {
    return { key: 'unbound', label: '未绑定空间' }
  }
  if (usedSites(item) > 0) {
    return { key: 'active', label: '使用中' }
  }
  return { key: 'idle', label: '空闲' }
}

function getContainerCapacityLabel(value) {
  const preset = getPlantingCapacityPreset(value)
  const labelMap = {
    small: '少量',
    medium: '中等',
    large: '较多'
  }
  return labelMap[preset] || '未填写'
}

function getContainerUseHint(type) {
  const hintMap = {
    1: '单株养护或香草组合',
    2: '多株混种',
    3: '分层摆放',
    4: '垂直利用'
  }
  return hintMap[Number(type)] || '按实际场景使用'
}

function handleContainerCardAction(item, action) {
  detailDrawerOpen.value = false
  if (action.key === 'edit') {
    handleUpdate(item)
    return
  }
  if (action.key === 'delete') {
    handleDelete(item)
  }
}

function openContainerDetail(item) {
  detailContainer.value = item
  detailDrawerOpen.value = true
}

function openContainerEditFromDetail() {
  if (!detailContainer.value) {
    return
  }
  const current = detailContainer.value
  detailDrawerOpen.value = false
  handleUpdate(current)
}

function handleDeleteFromDetail() {
  if (!detailContainer.value) {
    return
  }
  const current = detailContainer.value
  detailDrawerOpen.value = false
  handleDelete(current)
}

function getPlantingCapacityPreset(value) {
  const count = Number(value)
  if (!count) {
    return ''
  }
  if (count <= 3) {
    return 'small'
  }
  if (count <= 8) {
    return 'medium'
  }
  return 'large'
}

function togglePlantingSitesPreset(option) {
  const currentPreset = getPlantingCapacityPreset(form.value.plantingSites)
  form.value.plantingSites = currentPreset === option.key ? null : option.value
}

function usesPlantCountLabel(type) {
  return false
}

function getStepStateClass(step) {
  if (step < activeStep.value) {
    return 'is-complete'
  }
  if (step === activeStep.value) {
    return 'is-active'
  }
  return 'is-upcoming'
}

function scrollToStep(step) {
  nextTick(() => {
    const target = stepSectionRefs.value[step]
    if (!target || !open.value) {
      return
    }
    target.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
  })
}

function resolveSpaceDescription(land) {
  if (land?.remark) {
    return land.remark
  }
  if (land?.areaSqm != null) {
    return `可用面积约 ${Number(land.areaSqm).toFixed(0)} ㎡`
  }
  return '选择这个空间来放置你的容器'
}

function toggleSearch() {
  searchOpen.value = !searchOpen.value
  if (!searchOpen.value && !queryParams.value.containerName) {
    getList()
  }
}

function usedSites(item) {
  return Math.min(occupancyMap.value.get(item.containerId) || 0, Number(item.plantingSites || 0))
}

function getRecommendedPlanting(item) {
  const depth = Number(item.depthCm || 0)
  const type = Number(item.containerType || 0)
  if (type === 3) {
    return '香草、草莓、叶菜分层组合'
  }
  if (depth >= 20) {
    return '樱桃番茄、辣椒、四季豆'
  }
  if (depth >= 12) {
    return '生菜、芝麻菜、矮生豆'
  }
  return '薄荷、罗勒、小白菜'
}

function goToLands() {
  open.value = false
  router.push('/portal/lands')
}

function openPreviewContainer(preview) {
  handleAdd()
  form.value.containerType = preview.type
  form.value.containerName = preview.name
  form.value.plantingSites = capacityPresetOptions.find((item) => item.key === preview.capacityKey)?.value || null
  scrollToStep(3)
}

async function getList() {
  loading.value = true
  try {
    const [containerRes, resultRes, landRes] = await Promise.all([
      listPortalContainer(queryParams.value),
      listPortalResult({ pageNum: 1, pageSize: 1000 }),
      listPortalLand({ pageNum: 1, pageSize: 1000 })
    ])
    containerList.value = containerRes.rows || []
    total.value = containerRes.total || 0
    resultRows.value = resultRes.rows || []
    landOptions.value = landRes.rows || []
  } finally {
    loading.value = false
  }
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  detailDrawerOpen.value = false
  spaceBindingMode.value = ''
  showAdvancedSettings.value = false
  form.value = {
    containerId: null,
    landId: null,
    containerName: null,
    containerType: null,
    plantingSites: null,
    depthCm: null,
    widthCm: null,
    heightCm: null,
    isActive: '0'
  }
  proxy.resetForm('containerRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '创建一个容器'
  scrollToStep(1)
}

function handleUpdate(row) {
  reset()
  getPortalContainer(row.containerId).then((response) => {
    form.value = response.data
    spaceBindingMode.value = response.data?.landId != null ? 'land' : 'skip'
    showAdvancedSettings.value = [response.data?.depthCm, response.data?.widthCm, response.data?.heightCm].some((value) => value != null && value !== '')
    open.value = true
    title.value = '调整这个容器'
    scrollToStep(activeStep.value)
  })
}

function submitForm() {
  if (!isBindingStepCompleted.value) {
    proxy.$modal.msgError('请选择空间归属方式')
    scrollToStep(1)
    return
  }
  proxy.$refs.containerRef.validate((valid) => {
    if (!valid) {
      return
    }
    const payload = {
      ...form.value,
      landId: spaceBindingMode.value === 'land' ? form.value.landId : null
    }
    const action = payload.containerId != null ? updatePortalContainer(payload) : addPortalContainer(payload)
    action.then(() => {
      proxy.$modal.msgSuccess(payload.containerId != null ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function submitWithoutSupplement() {
  form.value.depthCm = null
  form.value.widthCm = null
  form.value.heightCm = null
  showAdvancedSettings.value = false
  submitForm()
}

function handleDelete(row) {
  proxy.$modal.confirm(`确定要移除这个容器吗：${row.containerName}`).then(() => {
    return delPortalContainer(row.containerId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()

watch(isBindingStepCompleted, (value, oldValue) => {
  if (value && !oldValue) {
    scrollToStep(2)
  }
})

watch(isNameCompleted, (value, oldValue) => {
  if (value && !oldValue) {
    scrollToStep(3)
  }
})

watch(isContainerTypeCompleted, (value, oldValue) => {
  if (value && !oldValue) {
    scrollToStep(4)
  }
})
</script>

<style scoped lang="scss">
.portal-container-page {
  display: grid;
  gap: 20px;
  padding: 4px 0 12px;
  background: #f4f7f5;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 12px 24px 0;
  margin: 0;
}

.page-tip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  color: #5b6b63;
  font-size: 14px;
  line-height: 1.6;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.intro-shell {
  padding: 16px 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid #e3eae6;
}

.intro-copy strong {
  display: block;
  color: #2f3a34;
  font-size: 18px;
}

.intro-copy p {
  margin: 8px 0 0;
  color: #5b6b63;
  line-height: 1.7;
}

.search-trigger {
  border-color: #e3eae6;
  background: #ffffff;
  color: #5b6b63;
}

.primary-action,
.empty-primary-action {
  border: none;
  border-radius: 999px;
  height: 44px;
  padding: 0 18px;
  background: #4c9e68;
  color: #fff;
  box-shadow: 0 10px 24px rgba(76, 158, 104, 0.2);
}

.primary-action:hover,
.primary-action:focus,
.empty-primary-action:hover,
.empty-primary-action:focus {
  background: #3f8b5a;
  color: #fff;
  box-shadow: 0 14px 28px rgba(76, 158, 104, 0.24);
}

.empty-primary-action {
  min-width: 176px;
}

.secondary-action {
  border-radius: 999px;
  border-color: #c9d8cf;
  background: transparent;
  color: #5b6b63;
}

.secondary-action:hover,
.secondary-action:focus {
  border-color: #5faf7a;
  color: #4c9e68;
  background: rgba(95, 175, 122, 0.06);
}

.list-shell {
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e3eae6;
}

.content-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px 24px 24px;
  min-height: clamp(560px, 68vh, 760px);
}

.list-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e3eae6;
}

.toolbar-count {
  color: #5b6b63;
  font-size: 14px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.toolbar-search {
  width: 240px;
}

.toolbar-btn {
  border-radius: 999px;
  border-color: #d7e0db;
  background: #ffffff;
  color: #5b6b63;
}

.toolbar-btn.is-disabled,
.toolbar-btn.is-disabled:hover {
  border-color: #e3eae6;
  background: #f8fbf9;
  color: #a3b0a9;
}

.inline-empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 24px;
  width: 100%;
  min-height: 0;
  padding: 4px 0 0;
}

.empty-state-main {
  flex: 1;
  display: grid;
  align-content: start;
  justify-items: center;
  gap: 14px;
  text-align: center;
  padding-top: 26px;
}

.empty-illustration {
  position: relative;
  width: 220px;
  height: 180px;
  display: grid;
  place-items: center;
}

.empty-circle {
  position: absolute;
  border-radius: 50%;
}

.circle-a {
  width: 120px;
  height: 120px;
  background: rgba(95, 175, 122, 0.1);
}

.circle-b {
  width: 170px;
  height: 170px;
  background: rgba(95, 175, 122, 0.06);
}

.empty-shape {
  position: relative;
  z-index: 1;
  width: 110px;
  height: 132px;
  padding: 14px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid #e3eae6;
  display: grid;
  gap: 10px;
}

.empty-shape span {
  border-radius: 14px;
  background: rgba(95, 175, 122, 0.12);
}

.empty-shape span:first-child {
  height: 44px;
}

.empty-shape span:not(:first-child) {
  height: 24px;
}

.empty-state-main h3 {
  margin: 0;
  color: #2f3a34;
  font-size: 22px;
  font-weight: 600;
}

.empty-state-main p {
  max-width: 560px;
  margin: 0;
  color: #5b6b63;
  line-height: 1.75;
}

.future-preview {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  width: 100%;
  align-self: stretch;
  margin-top: auto;
}

.preview-card {
  min-width: 0;
  width: 100%;
  padding: 14px;
  border: 1px solid #e3eae6;
  border-radius: 16px;
  background: #fafcfb;
  font: inherit;
  color: inherit;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.preview-card:hover {
  background: #f4faf6;
  border-color: #9fc7ad;
  transform: translateY(-2px);
}

.preview-card em {
  display: inline-flex;
  margin-bottom: 8px;
  font-style: normal;
  font-size: 18px;
}

.preview-type {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(95, 175, 122, 0.12);
  color: #5faf7a;
  font-size: 12px;
}

.preview-card strong {
  display: block;
  margin-top: 10px;
  color: #2f3a34;
  font-size: 15px;
}

.preview-card p {
  margin: 6px 0 0;
  color: #8a9a92;
  line-height: 1.55;
  font-size: 13px;
}

.preview-meta {
  display: grid;
  gap: 4px;
  margin-top: 10px;
}

.preview-card small {
  display: block;
  color: #61716a;
  font-size: 12px;
}

.preview-cta {
  margin-top: 12px;
  color: #5faf7a;
  font-weight: 600;
}

.card-info-rows {
  display: grid;
  gap: 10px;
}

.card-info-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(227, 234, 230, 0.9);
}

.card-info-row:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.card-info-row span {
  color: #8a9a92;
  font-size: 13px;
}

.card-info-row strong {
  color: #2f3a34;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

:deep(.card-detail-drawer .el-drawer__body) {
  padding: 0;
}

.detail-sheet {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.detail-sheet__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.detail-sheet__eyebrow {
  display: inline-block;
  color: #7da98a;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.detail-sheet__header h3 {
  margin: 8px 0 0;
  color: #2f3a34;
  font-size: 24px;
}

.detail-sheet__status {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  white-space: nowrap;
}

.detail-sheet__status.status-active {
  background: rgba(95, 175, 122, 0.12);
  color: #4c9e68;
}

.detail-sheet__status.status-idle {
  background: rgba(226, 184, 76, 0.14);
  color: #ad7c12;
}

.detail-sheet__status.status-unbound,
.detail-sheet__status.status-disabled {
  background: rgba(126, 139, 132, 0.12);
  color: #6d7b74;
}

.detail-sheet__grid {
  display: grid;
  gap: 12px;
  padding: 16px;
  border-radius: 18px;
  background: #f8fbf9;
  border: 1px solid #e3eae6;
}

.detail-sheet__row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.detail-sheet__row span {
  color: #8a9a92;
  font-size: 13px;
}

.detail-sheet__row strong {
  color: #2f3a34;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.detail-sheet__hint {
  margin: 0;
  color: #8f9b95;
  line-height: 1.7;
}

.detail-sheet__actions {
  display: flex;
  gap: 12px;
}

.detail-edit-btn,
.detail-delete-btn {
  min-width: 96px;
  height: 38px;
  border-radius: 999px;
}

.detail-edit-btn {
  border: none;
  background: rgba(95, 175, 122, 0.12);
  color: #4c9e68;
}

.detail-delete-btn {
  border-color: #d7e0db;
  color: #7a8780;
}

:deep(.container-dialog .el-dialog) {
  border-radius: 24px;
}

:deep(.container-dialog .el-dialog__header) {
  padding: 24px 24px 0;
}

:deep(.container-dialog .el-dialog__body) {
  padding: 20px 24px 12px;
}

:deep(.container-dialog .el-dialog__footer) {
  padding: 8px 24px 24px;
}

.dialog-intro {
  margin-bottom: 18px;
  padding: 16px 18px;
  border-radius: 18px;
  background: #f8fbf9;
}

.dialog-intro strong {
  display: block;
  color: #2f3a34;
  font-size: 18px;
}

.dialog-intro p {
  margin: 8px 0 0;
  color: #5b6b63;
  line-height: 1.7;
}

.dialog-section + .dialog-section {
  margin-top: 14px;
}

.dialog-section {
  padding: 13px 0;
  border-top: 1px solid rgba(227, 234, 230, 0.58);
}

.dialog-section:first-of-type {
  border-top: none;
  padding-top: 0;
}

.dialog-section-secondary {
  opacity: 0.88;
}

.dialog-step {
  scroll-margin-top: 88px;
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.dialog-step.is-active {
  opacity: 1;
}

.dialog-step.is-active .dialog-heading > span {
  background: rgba(95, 175, 122, 0.18);
  color: #3f8b5a;
}

.dialog-step.is-complete {
  opacity: 0.86;
}

.dialog-step.is-upcoming {
  opacity: 0.58;
}

.dialog-step.is-upcoming .dialog-heading h4 {
  color: #7f8d86;
}

.dialog-step.is-upcoming .dialog-heading p {
  color: #a0aca6;
}

.advanced-section {
  padding-top: 10px;
}

.advanced-toggle {
  border: none;
  background: transparent;
  padding: 0;
  color: #5b6b63;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.18s ease;
}

.advanced-toggle:hover,
.advanced-toggle:focus {
  color: #4c9e68;
}

.advanced-panel {
  margin-top: 10px;
  padding: 10px 0 0;
  border-top: 1px solid rgba(227, 234, 230, 0.62);
  background: transparent;
}

.advanced-panel-copy strong {
  display: block;
  color: #2f3a34;
  font-size: 14px;
  font-weight: 600;
}

.advanced-panel-copy p {
  margin: 4px 0 0;
  color: #a1ada7;
  line-height: 1.5;
  font-size: 12px;
}

.dialog-heading {
  display: flex;
  gap: 14px;
  margin-bottom: 12px;
}

.dialog-heading > span {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(95, 175, 122, 0.1);
  color: #5faf7a;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}

.dialog-heading h4 {
  margin: 0;
  color: #2f3a34;
  font-size: 17px;
}

.dialog-heading p {
  margin: 4px 0 0;
  color: #8a9a92;
  line-height: 1.65;
}

.compact-form-item {
  margin-bottom: 0;
}

.space-grid,
.capacity-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.space-choice-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.space-card,
.type-card,
.capacity-option,
.status-option {
  border: 1px solid #e3eae6;
  background: #fff;
  color: #5b6b63;
  transition: 0.18s ease;
  cursor: pointer;
}

.space-card,
.type-card {
  padding: 14px;
  border-radius: 16px;
  text-align: left;
}

.capacity-option {
  padding: 13px 15px;
  border-radius: 16px;
  text-align: left;
}

.space-card strong,
.type-card strong,
.capacity-option strong {
  display: block;
  color: #2f3a34;
  font-size: 16px;
}

.space-card span,
.type-card span,
.capacity-option span {
  display: block;
  margin-top: 8px;
  color: #8a9a92;
  line-height: 1.6;
  font-size: 13px;
}

.type-card em {
  display: inline-flex;
  margin-bottom: 8px;
  font-style: normal;
  font-size: 18px;
}

.type-card strong {
  font-size: 15px;
}

.type-card span {
  margin-top: 6px;
  line-height: 1.45;
}

.space-card.active,
.type-card.active,
.capacity-option.active,
.status-option.active {
  border-color: #5faf7a;
  background: rgba(95, 175, 122, 0.1);
  box-shadow: 0 10px 20px rgba(95, 175, 122, 0.12);
  color: #2f3a34;
}

.empty-select-hint {
  padding: 16px 18px;
  border: 1px dashed #cfd9d3;
  border-radius: 16px;
  background: #fff;
}

.empty-select-hint strong {
  display: block;
  color: #2f3a34;
  font-size: 15px;
}

.empty-select-hint p {
  margin: 8px 0 0;
  color: #8a9a92;
  line-height: 1.65;
}

.inline-link-action {
  margin-top: 2px;
  border: none;
  background: transparent;
  color: #5faf7a;
  padding: 0;
}

.field-tip {
  display: block;
  margin-top: 10px;
  color: #8a9a92;
  line-height: 1.6;
}

.size-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.status-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-option {
  min-width: 126px;
  padding: 10px 16px;
  border-radius: 999px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  align-items: center;
  padding-top: 4px;
}

.dialog-footer .secondary-action {
  min-width: 116px;
  height: 44px;
  padding: 0 20px;
  border: none;
  border-radius: 999px;
  background: #f7faf8;
  color: #6b7972;
  box-shadow: none;
}

.dialog-footer .secondary-action:hover,
.dialog-footer .secondary-action:focus {
  border: none;
  background: #eef4f0;
  color: #5b6b63;
}

.dialog-footer .primary-action {
  min-width: 126px;
  height: 44px;
  padding: 0 22px;
  border-radius: 999px;
  background: #4c9e68;
  box-shadow: 0 10px 22px rgba(76, 158, 104, 0.18);
}

.dialog-footer .secondary-action,
.dialog-footer .primary-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

:deep(.advanced-panel .el-input) {
  --el-input-height: 38px;
}

:deep(.advanced-panel .el-input__wrapper) {
  padding: 0 10px;
  background: #fff;
  box-shadow: 0 0 0 1px rgba(217, 225, 220, 0.95) inset;
}

:deep(.advanced-panel .el-input__inner) {
  font-size: 13px;
  color: #4b5a53;
}

:deep(.advanced-panel .el-input__inner::placeholder) {
  color: #b8c2bd;
}

:deep(.advanced-panel .el-input-group__append) {
  padding: 0 10px;
  color: #a0aba5;
  font-size: 12px;
  background: #fafcfb;
  box-shadow: 0 0 0 1px rgba(217, 225, 220, 0.95) inset;
}

.toolbar-search-expand-enter-active,
.toolbar-search-expand-leave-active {
  transition: all 0.2s ease;
}

.toolbar-search-expand-enter-from,
.toolbar-search-expand-leave-to {
  width: 0;
  opacity: 0;
  transform: translateX(8px);
}

@media (max-width: 768px) {
  .content-area {
    min-height: auto;
    padding: 18px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-tip {
    max-width: none;
  }

  .page-actions {
    width: 100%;
    justify-content: space-between;
  }

  .list-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar-actions {
    width: 100%;
    flex-wrap: wrap;
    margin-left: 0;
  }

  .toolbar-search {
    width: 100%;
  }

  .inline-empty-state {
    gap: 18px;
  }

  .space-grid,
  .space-choice-grid,
  .type-grid,
  .capacity-grid,
  .size-grid,
  .future-preview,
  .info-grid {
    grid-template-columns: 1fr;
  }

  .dialog-step.is-upcoming {
    opacity: 0.72;
  }
}
</style>
