<template>
  <div class="portal-land-page">
    <header class="page-header">
      <p class="page-tip">
        <el-icon><CollectionTag /></el-icon>
        <span>整理你的种植空间，让推荐更贴近实际环境</span>
      </p>
      <div class="page-actions">
        <el-button class="primary-action" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新增地块</span>
        </el-button>
      </div>
    </header>

    <section class="list-shell" v-loading="loading">
      <div class="content-area">
        <div class="list-toolbar">
          <span class="toolbar-count">空间数量：{{ total }}</span>
          <div class="toolbar-actions">
            <transition name="toolbar-search-expand">
              <div v-if="searchOpen" class="toolbar-search">
                <el-input
                  v-model="queryParams.landCode"
                  placeholder="搜索空间名称"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </div>
            </transition>
            <el-button class="search-trigger" circle @click="toggleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
            <el-button class="toolbar-btn" :disabled="!landList.length">筛选</el-button>
            <el-button class="toolbar-btn" :disabled="!landList.length">排序</el-button>
          </div>
        </div>

        <div v-if="!landList.length" class="inline-empty-state">
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
            <h3>先把第一个种植空间加进来</h3>
            <p>只要先补上一个阳台角落、庭院地块或容器区域，后面的种植方案就能开始真正围绕你的环境来推荐。</p>
            <el-button class="empty-primary-action" @click="handleAdd">创建第一个空间</el-button>
          </div>

          <div class="future-preview">
            <button
              v-for="preview in previewLandCards"
              :key="preview.name"
              type="button"
              class="preview-card"
              @click="openPreviewLand(preview)"
            >
              <em>{{ preview.icon }}</em>
              <strong>{{ preview.name }}</strong>
              <span>{{ preview.description }}</span>
              <small class="preview-cta">{{ preview.cta }}</small>
            </button>
          </div>

          <div v-if="false" class="future-preview">
            <div class="preview-card">
              <em>🌿</em>
              <strong>阳台角落</strong>
              <span>适合香草、叶菜和小型混种组合。</span>
              <small>可以从这里开始</small>
            </div>
            <div class="preview-card">
              <em>🪴</em>
              <strong>窗边容器区</strong>
              <span>适合花盆、种植箱等容器型种植空间。</span>
              <small>适合轻量起步</small>
            </div>
            <div class="preview-card">
              <em>🌾</em>
              <strong>院子菜地</strong>
              <span>适合成片规划与更完整的季节安排。</span>
              <small>适合做主空间</small>
            </div>
          </div>
        </div>

        <el-row v-else :gutter="18">
          <el-col
            v-for="item in landList"
            :key="item.landId"
            :xs="24"
            :sm="12"
            :lg="8"
            :xl="6"
          >
            <BaseCard
              :title="item.landCode"
              :tag="getLandTypeLabel(item.landType)"
              :status="getLandCardStatusMeta(item).key"
              :meta="getLandCardMeta(item)"
              :actions="managementCardActions"
              clickable
              @card-click="openLandDetail(item)"
              @action="handleLandCardAction(item, $event)"
            >
              <template #body>
                <div class="card-info-rows">
                  <div class="card-info-row">
                    <span>光照</span>
                    <strong>{{ findDictLabel(bus_light_level, item.lightLevel) }}</strong>
                  </div>
                  <div class="card-info-row">
                    <span>{{ isGroundType(item.landType) ? '面积' : '容量' }}</span>
                    <strong>{{ formatOriginalArea(item) }}</strong>
                  </div>
                  <div class="card-info-row">
                    <span>推荐作物</span>
                    <strong>{{ getRecommendedCrops(item) }}</strong>
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
      <template v-if="detailLand">
        <div class="detail-sheet">
          <div class="detail-sheet__header">
            <div>
              <span class="detail-sheet__eyebrow">空间详情</span>
              <h3>{{ detailLand.landCode }}</h3>
            </div>
            <span class="detail-sheet__status" :class="`status-${getLandCardStatusMeta(detailLand).key}`">
              {{ getLandCardStatusMeta(detailLand).label }}
            </span>
          </div>

          <div class="detail-sheet__grid">
            <div class="detail-sheet__row">
              <span>空间类型</span>
              <strong>{{ getLandTypeLabel(detailLand.landType) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>光照</span>
              <strong>{{ findDictLabel(bus_light_level, detailLand.lightLevel) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>{{ isGroundType(detailLand.landType) ? '面积' : '容量' }}</span>
              <strong>{{ formatOriginalArea(detailLand) }}</strong>
            </div>
            <div class="detail-sheet__row">
              <span>推荐作物</span>
              <strong>{{ getRecommendedCrops(detailLand) }}</strong>
            </div>
          </div>

          <p class="detail-sheet__hint">详情页尚未独立实现，当前先用详情抽屉承接地块卡片主体点击。</p>

          <div class="detail-sheet__actions">
            <el-button class="detail-edit-btn" @click="openLandEditFromDetail">编辑</el-button>
            <el-button class="detail-delete-btn" @click="handleDeleteLandFromDetail">删除</el-button>
          </div>
        </div>
      </template>
    </el-drawer>

    <el-dialog :title="title" v-model="open" width="760px" append-to-body class="land-dialog">
      <el-form ref="landRef" :model="form" :rules="rules" label-width="0">
        <div class="dialog-progress">步骤2/3</div>

        <section class="dialog-section">
          <div class="dialog-heading">
            <span>01</span>
            <div>
              <h4>先确认你的种植类型，再选择空间类型</h4>
              <p>先选“家庭种植”还是“农户种植”，再从对应的空间类型里选最接近真实情况的一项。</p>
            </div>
          </div>
          <el-form-item prop="landType" class="compact-form-item">
            <div class="type-step-shell">
              <div class="planting-mode-grid">
                <button
                  v-for="option in plantingTypeOptions"
                  :key="option.value"
                  type="button"
                  class="mode-pick-card"
                  :class="{ active: selectedPlantingCategory === option.value }"
                  @click="selectPlantingCategory(option.value)"
                >
                  <span>{{ option.kicker }}</span>
                  <strong>{{ option.label }}</strong>
                  <small>{{ option.description }}</small>
                </button>
              </div>

              <div class="type-selection-panel">
                <div class="type-selection-head">
                  <strong>{{ currentPlantingTypeMeta.label }}空间类型</strong>
                </div>

                <div v-if="visibleLandTypes.length" class="type-grid">
                  <button
                    v-for="item in visibleLandTypes"
                    :key="item.key"
                    type="button"
                    class="type-card"
                    :class="{ active: selectedDisplayLandTypeKey === item.key }"
                    @click="handleLandTypeSelect(item)"
                  >
                    <em>{{ item.icon }}</em>
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.description }}</span>
                    <small>{{ item.sceneHint }}</small>
                  </button>
                </div>
                <p v-else class="type-empty-hint">当前种植类型下暂无可选空间类型。</p>
              </div>
            </div>
          </el-form-item>
        </section>

        <section class="dialog-section">
          <div class="form-group-shell">
            <div class="group-heading">
              <span>基础信息</span>
              <h4>这个空间你想怎么称呼它？</h4>
            </div>
            <el-form-item prop="landCode" class="compact-form-item">
              <el-input v-model="form.landCode" placeholder="例如：南侧阳台、窗边种植区" />
            </el-form-item>
          </div>
        </section>

        <section class="dialog-section">
          <div class="form-group-shell">
            <div class="group-heading">
              <span>核心决策</span>
              <h4>先完成这两个最关键的信息</h4>
            </div>

            <div class="field-stack">
              <div class="field-block field-block-primary">
                <div class="field-copy">
                  <strong>这个空间的光照情况</strong>
                </div>
                <el-form-item prop="lightLevel" class="compact-form-item">
                  <div class="status-group light-group">
                    <button
                      v-for="dict in bus_light_level"
                      :key="dict.value"
                      type="button"
                      class="status-option light-option"
                      :class="{ active: Number(form.lightLevel) === Number(dict.value) }"
                      @click="form.lightLevel = Number(dict.value)"
                    >
                      <span v-if="Number(form.lightLevel) === Number(dict.value)" class="light-option-check">
                        <el-icon><Check /></el-icon>
                      </span>
                      <strong>{{ getLightTitle(Number(dict.value)) }}</strong>
                      <span>{{ getLightDescription(Number(dict.value)) }}</span>
                    </button>
                  </div>
                </el-form-item>
              </div>

              <div class="field-block field-block-secondary">
                <div class="field-copy">
                  <strong>{{ sizeFieldTitle }}</strong>
                  <span>{{ sizeFieldSubtitle }}</span>
                </div>
                <div class="size-input-shell">
                  <el-form-item prop="inputValue" class="compact-form-item size-input-item">
                    <el-input v-model="form.inputValue" :placeholder="sizeFieldPlaceholder">
                      <template v-if="showContainerCountInput" #append>盆 / 容器</template>
                    </el-input>
                  </el-form-item>
                  <div v-if="showAreaInput" class="unit-switcher">
                    <button
                      v-for="dict in bus_area_unit"
                      :key="dict.value"
                      type="button"
                      class="unit-option"
                      :class="{ active: String(form.unit) === String(dict.value) }"
                      @click="form.unit = dict.value"
                    >
                      {{ dict.label }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="dialog-section dialog-section-optional">
          <div class="form-group-shell">
            <button type="button" class="expand-trigger" @click="extraInfoOpen = !extraInfoOpen">
              {{ extraInfoOpen ? '收起环境细节' : '+ 添加环境细节（可选）' }}
            </button>
            <div v-if="extraInfoOpen" class="field-block field-block-optional">
              <div class="field-copy">
                <strong>补充说明</strong>
                <span>如果有遮阴、通风、水源等细节，可以在这里补充。</span>
              </div>
              <el-form-item prop="remark" class="compact-form-item">
                <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="比如下午会被遮阴、靠近水源、适合叶菜或香草" />
              </el-form-item>
            </div>
          </div>
        </section>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="secondary-action" @click="cancel">跳过这一步</el-button>
          <el-button class="primary-action" @click="submitForm">完成创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="PortalLands">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import { Check, CollectionTag, Plus, Search } from '@element-plus/icons-vue'
import BaseCard from '@/components/portal/BaseCard.vue'
import { addPortalLand, delPortalLand, getPortalLand, listPortalLand, updatePortalLand } from '@/api/portal/agriculture'
import { DEFAULT_MANAGEMENT_ACTIONS } from '@/utils/cardSystem'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { bus_light_level, bus_area_unit, bus_land_type } = proxy.useDict('bus_light_level', 'bus_area_unit', 'bus_land_type')

const landList = ref([])
const open = ref(false)
const loading = ref(true)
const searchOpen = ref(false)
const total = ref(0)
const title = ref('')
const detailDrawerOpen = ref(false)
const detailLand = ref(null)
const selectedPlantingCategory = ref('household')
const selectedDisplayLandTypeKey = ref('')
const extraInfoOpen = ref(false)
const previewLandCards = [
  {
    icon: '🌶',
    name: '阳台角落',
    key: 'household_ground',
    description: '适合香草、叶菜和小型混种组合。',
    cta: '点击创建类似空间'
  },
  {
    icon: '🌿',
    name: '窗边容器区',
    key: 'household_container',
    description: '适合花盆、种植箱等容器型种植空间。',
    cta: '点击创建类似空间'
  },
  {
    icon: '🌵',
    name: '院子菜地',
    key: 'farmer_dry',
    description: '适合成片规划与更完整的季节安排。',
    cta: '点击创建类似空间'
  }
]
const managementCardActions = DEFAULT_MANAGEMENT_ACTIONS

const plantingTypeOptions = [
  {
    value: 'household',
    kicker: 'Home Growing',
    label: '家庭种植',
    description: '适合阳台、露台、庭院和墙面等日常种植空间。'
  },
  {
    value: 'farmer',
    kicker: 'Farm Growing',
    label: '农户种植',
    description: '适合成片地块、农田与设施农业等生产型场景。'
  }
]

const landTypeDisplayPresets = [
  {
    key: 'household_container',
    category: 'household',
    title: '花盆 / 种植箱',
    icon: '🪴',
    description: '适用于阳台花盆、长条种植箱、窗边盆栽等常见家庭容器种植场景。',
    sceneHint: '适合：阳台花盆、种植箱、窗边盆栽',
    rawLabels: ['容器种植', '花盆', '种植箱'],
    preferredRawLabels: ['容器种植'],
    fallbackBackendLabels: ['阳台地面'],
    measurement: 'capacity'
  },
  {
    key: 'household_ground',
    category: 'household',
    title: '地面种植区',
    icon: '🌱',
    description: '适用于院子里的一小块地、房前屋后开垦的小菜地、固定种植池等直接在地面土壤中种植的场景。',
    sceneHint: '适合：院子小菜地、固定种植池、房前屋后土壤区',
    rawLabels: ['小型土壤区', '地面种植区', '庭院空间', '阳台地面'],
    preferredRawLabels: ['阳台地面', '小型土壤区'],
    measurement: 'area'
  },
  {
    key: 'household_vertical',
    category: 'household',
    title: '立体 / 墙面种植区',
    icon: '🧱',
    description: '适用于挂盆、洞洞板、立体架、墙面种植等垂直空间利用场景。',
    sceneHint: '适合：挂盆、洞洞板、立体架、墙面种植',
    rawLabels: ['垂直墙面', '立体种植区', '墙面种植区'],
    preferredRawLabels: ['垂直墙面'],
    measurement: 'capacity'
  },
  {
    key: 'farmer_dry',
    category: 'farmer',
    title: '旱地',
    icon: '🌾',
    description: '适合露天地块成片种植，更关注面积、投入与产出效率。',
    sceneHint: '适合：露天耕地、成片旱地、平整菜地',
    rawLabels: ['旱地'],
    preferredRawLabels: ['旱地'],
    measurement: 'area'
  },
  {
    key: 'farmer_paddy',
    category: 'farmer',
    title: '水田',
    icon: '💧',
    description: '适合具备蓄水与排水条件的农田环境，重点考虑水分管理。',
    sceneHint: '适合：稻田、低洼湿田、灌溉条件稳定地块',
    rawLabels: ['水田'],
    preferredRawLabels: ['水田'],
    measurement: 'area'
  },
  {
    key: 'farmer_greenhouse',
    category: 'farmer',
    title: '大棚',
    icon: '🏡',
    description: '适合设施农业空间，便于控制环境并安排更稳定的生产节奏。',
    sceneHint: '适合：塑料棚、日光温室、设施栽培区',
    rawLabels: ['大棚'],
    preferredRawLabels: ['大棚'],
    measurement: 'area'
  }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    landCode: null
  },
  rules: {
    landCode: [{ required: true, message: '请给这个空间起个名字', trigger: 'blur' }],
    landType: [{ required: true, message: '请选择种植环境', trigger: 'change' }],
    inputValue: [{ validator: validateInputValue, trigger: ['blur', 'change'] }],
    lightLevel: [{ required: true, message: '请选择光照情况', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const landTypeOptions = computed(() => bus_land_type.value || [])
const areaUnitOptions = computed(() => bus_area_unit.value || [])
const displayLandTypeOptions = computed(() => (
  landTypeDisplayPresets
    .map((preset) => {
      const backendValue = resolveBackendLandTypeValue(preset)
      return backendValue == null ? null : { ...preset, backendValue }
    })
    .filter(Boolean)
))
const currentPlantingTypeMeta = computed(() => {
  return plantingTypeOptions.find((item) => item.value === selectedPlantingCategory.value) || plantingTypeOptions[0]
})
const visibleLandTypes = computed(() => displayLandTypeOptions.value.filter((item) => item.category === selectedPlantingCategory.value))
const selectedLandType = computed(() => Number(form.value.landType || 0))
const activeDisplayLandType = computed(() => {
  if (selectedDisplayLandTypeKey.value) {
    return displayLandTypeOptions.value.find((item) => item.key === selectedDisplayLandTypeKey.value) || null
  }
  return resolveDisplayLandType(selectedLandType.value)
})
const showAreaInput = computed(() => isGroundType(activeDisplayLandType.value?.key ? activeDisplayLandType.value : selectedLandType.value))
const showContainerCountInput = computed(() => !showAreaInput.value)
const sizeFieldTitle = computed(() => (
  showAreaInput.value ? '这块空间的面积是多少' : '这个空间大概能放多少盆 / 容器？'
))
const sizeFieldSubtitle = computed(() => (
  showAreaInput.value ? '按你平时习惯的单位填写即可。' : '不确定可以先不填'
))
const sizeFieldPlaceholder = computed(() => (
  showAreaInput.value ? '例如：3㎡、10㎡ 或 0.5亩' : '例如：3盆、6盆、10盆'
))

function normalizeLabel(label) {
  return String(label || '').replace(/\s+/g, '')
}

function getLandTypeRawLabel(landType) {
  return findDictLabel(landTypeOptions.value, landType)
}

function getPresetMatchedLabel(preset, label) {
  const normalizedLabel = normalizeLabel(label)
  if (!normalizedLabel) {
    return ''
  }
  return preset.rawLabels.find((item) => normalizedLabel.includes(normalizeLabel(item))) || ''
}

function resolveBackendLandTypeValue(preset) {
  const labels = [...preset.preferredRawLabels, ...preset.rawLabels, ...(preset.fallbackBackendLabels || [])]
  for (const rawLabel of labels) {
    const option = landTypeOptions.value.find((item) => normalizeLabel(item.label) === normalizeLabel(rawLabel))
    if (option) {
      return Number(option.value)
    }
  }
  return null
}

function resolveDisplayLandType(landType) {
  const rawLabel = getLandTypeRawLabel(landType)
  return displayLandTypeOptions.value.find((preset) => Boolean(getPresetMatchedLabel(preset, rawLabel))) || null
}

function resolveDisplayLandTypeKey(landType) {
  return resolveDisplayLandType(landType)?.key || ''
}

function resolvePlantingCategoryByLandType(landType) {
  return resolveDisplayLandType(landType)?.category || null
}

function getLandTypeLabel(landType) {
  return resolveDisplayLandType(landType)?.title || getLandTypeRawLabel(landType)
}

function getLandTypeMeta(landType) {
  return resolveDisplayLandType(landType) || {}
}

function getDefaultPlantingCategory() {
  const sceneMode = getCurrentSceneMode()
  if (sceneMode === 1) {
    return 'farmer'
  }
  if (sceneMode === 2) {
    return 'household'
  }
  return 'household'
}

function validateInputValue(rule, value, callback) {
  if (!showAreaInput.value) {
    callback()
    return
  }
  if (String(value ?? '').trim()) {
    callback()
    return
  }
  callback(new Error('请输入面积'))
}

function isGroundType(landType) {
  const displayMeta = typeof landType === 'object' && landType !== null ? landType : getLandTypeMeta(landType)
  if (!displayMeta.key) {
    return selectedPlantingCategory.value === 'farmer'
  }
  return displayMeta.measurement === 'area'
}

function toggleSearch() {
  searchOpen.value = !searchOpen.value
  if (!searchOpen.value && !queryParams.value.landCode) {
    getList()
  }
}

function findDictLabel(options, value) {
  const target = value == null ? '' : String(value)
  const list = Array.isArray(options) ? options : options?.value || []
  return list.find((item) => String(item.value) === target)?.label || '未设置'
}

function formatOriginalArea(land) {
  if (land?.inputValue == null) {
    return '--'
  }
  if (isGroundType(land.landType)) {
    const unitLabel = findDictLabel(areaUnitOptions.value, land?.unit)
    return `${land.inputValue} ${unitLabel}`
  }
  return `${land.inputValue} 容量参考`
}

function getCurrentSceneMode() {
  const queryMode = Number(route.query.mode || '')
  if ([1, 2].includes(queryMode)) {
    return queryMode
  }
  const cachedMode = Number(window.localStorage.getItem('portalAgriModeDraft') || '')
  if ([1, 2].includes(cachedMode)) {
    return cachedMode
  }
  return null
}

function getDefaultAreaUnit(category = '') {
  const sceneMode = getCurrentSceneMode()
  const plantingCategory = category || (sceneMode === 1 ? 'farmer' : sceneMode === 2 ? 'household' : '')
  if (plantingCategory === 'farmer') {
    return areaUnitOptions.value.find((item) => /亩/u.test(item.label))
      ?.value || areaUnitOptions.value.find((item) => /平方|㎡/u.test(item.label))?.value || areaUnitOptions.value[0]?.value || null
  }
  if (plantingCategory === 'household') {
    return areaUnitOptions.value.find((item) => /平方|㎡/u.test(item.label))
      ?.value || areaUnitOptions.value.find((item) => /亩/u.test(item.label))?.value || areaUnitOptions.value[0]?.value || null
  }
  return areaUnitOptions.value[0]?.value || null
}

function getLandMood(land) {
  const lightLevel = Number(land?.lightLevel || 0)
  if (lightLevel >= 3) {
    return '阳光充足'
  }
  if (lightLevel === 2) {
    return '日照适中'
  }
  return '更适合耐阴组合'
}

function getRecommendedCrops(land) {
  const lightLevel = Number(land?.lightLevel || 0)
  const landType = Number(land?.landType || 0)
  if (!isGroundType(landType) && lightLevel <= 1) {
    return '生菜、薄荷、香菜'
  }
  if (lightLevel >= 3) {
    return '番茄、辣椒、黄瓜'
  }
  if (lightLevel === 2) {
    return '四季豆、茄子、叶菜'
  }
  return '小白菜、芝麻菜、罗勒'
}

function getLandTypeDescription(landType) {
  return getLandTypeMeta(landType).description || '选择最接近真实情况的一类，后续推荐会更贴近你的环境。'
}

function getLandTypeSceneHint(landType) {
  return getLandTypeMeta(landType).sceneHint || '适合从真实使用场景来判断'
}

function getLandTypeIcon(landType) {
  return getLandTypeMeta(landType).icon || '🌱'
}

function getLightDescription(lightLevel) {
  const map = {
    1: '一天几乎没有直射阳光',
    2: '有一段时间能晒到太阳',
    3: '大部分时间都有阳光直射'
  }
  return map[lightLevel] || '按平时最接近的状态来选择'
}

function getLightTitle(lightLevel) {
  const map = {
    1: '弱光（背阴）',
    2: '中等光（半日照）',
    3: '强光（全日照）'
  }
  return map[lightLevel] || '请选择光照'
}

function getAreaReferenceText(landType) {
  if (isGroundType(landType)) {
    return '后续方案会结合这块地的面积，安排更合适的种植比例。'
  }
  return '后续方案会结合这里的容器容量感，安排更适合的种植密度。'
}

function getLandCardStatusMeta(item) {
  if (String(item?.isActive) === '1') {
    return { key: 'disabled', label: '停用' }
  }
  if (!item?.lightLevel || !String(item?.inputValue ?? '').trim()) {
    return { key: 'idle', label: '空闲' }
  }
  return { key: 'active', label: '使用中' }
}

function getLandCardMeta(item) {
  return `推荐：${getRecommendedCrops(item)} · ${item.remark || getAreaReferenceText(item.landType)}`
}

function handleLandCardAction(item, action) {
  detailDrawerOpen.value = false
  if (action.key === 'edit') {
    handleUpdate(item)
    return
  }
  if (action.key === 'delete') {
    handleDelete(item)
  }
}

function openLandDetail(item) {
  detailLand.value = item
  detailDrawerOpen.value = true
}

function openLandEditFromDetail() {
  if (!detailLand.value) {
    return
  }
  const current = detailLand.value
  detailDrawerOpen.value = false
  handleUpdate(current)
}

function handleDeleteLandFromDetail() {
  if (!detailLand.value) {
    return
  }
  const current = detailLand.value
  detailDrawerOpen.value = false
  handleDelete(current)
}

function normalizeInputValue(value) {
  const text = String(value ?? '').trim()
  if (!text) {
    return null
  }
  const matched = text.match(/\d+(\.\d+)?/)
  return matched ? matched[0] : null
}

async function getList() {
  loading.value = true
  try {
    const response = await listPortalLand(queryParams.value)
    landList.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function cancel() {
  open.value = false
  reset()
}

function selectPlantingCategory(category) {
  selectedPlantingCategory.value = category
  if (resolvePlantingCategoryByLandType(form.value.landType) !== category) {
    form.value.landType = null
    selectedDisplayLandTypeKey.value = ''
    form.value.inputValue = null
    form.value.unit = category === 'farmer' ? getDefaultAreaUnit(category) : null
    proxy.$refs.landRef?.clearValidate?.(['inputValue'])
  }
}

function handleLandTypeSelect(option) {
  selectedDisplayLandTypeKey.value = option.key
  form.value.landType = Number(option.backendValue)
  form.value.inputValue = null
  form.value.unit = option.measurement === 'area' ? (form.value.unit || getDefaultAreaUnit(option.category)) : null
  proxy.$refs.landRef?.clearValidate?.(['inputValue'])
}

function openPreviewLand(preview) {
  handleAdd()
  const matchedPreset = displayLandTypeOptions.value.find((item) => item.key === preview.key)
  if (!matchedPreset) {
    return
  }
  selectedPlantingCategory.value = matchedPreset.category
  handleLandTypeSelect(matchedPreset)
  form.value.landCode = preview.name
}

function reset() {
  detailDrawerOpen.value = false
  selectedPlantingCategory.value = getDefaultPlantingCategory()
  selectedDisplayLandTypeKey.value = ''
  extraInfoOpen.value = false
  form.value = {
    landId: null,
    landCode: null,
    landType: null,
    inputValue: null,
    unit: selectedPlantingCategory.value === 'farmer' ? getDefaultAreaUnit(selectedPlantingCategory.value) : null,
    areaSqm: null,
    lightLevel: null,
    remark: null
  }
  proxy.resetForm('landRef')
  proxy.$refs.landRef?.clearValidate?.(['inputValue'])
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '创建一个空间'
}

function handleUpdate(row) {
  reset()
  getPortalLand(row.landId).then((response) => {
    form.value = response.data
    selectedPlantingCategory.value = resolvePlantingCategoryByLandType(response.data?.landType) || getDefaultPlantingCategory()
    selectedDisplayLandTypeKey.value = resolveDisplayLandTypeKey(response.data?.landType)
    extraInfoOpen.value = Boolean(response.data?.remark)
    open.value = true
    title.value = '调整这个空间'
  })
}

function submitForm() {
  proxy.$refs.landRef.validate((valid) => {
    if (!valid) {
      return
    }
    form.value.inputValue = normalizeInputValue(form.value.inputValue)
    form.value.unit = form.value.inputValue && showAreaInput.value
      ? (form.value.unit || getDefaultAreaUnit(resolvePlantingCategoryByLandType(form.value.landType) || selectedPlantingCategory.value))
      : null
    const action = form.value.landId != null ? updatePortalLand(form.value) : addPortalLand(form.value)
    action.then(() => {
      proxy.$modal.msgSuccess(form.value.landId != null ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  proxy.$modal.confirm(`确定要移除这块空间吗？${row.landCode}`).then(() => delPortalLand(row.landId)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>

<style scoped lang="scss">
.portal-land-page {
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

.page-tip .el-icon {
  color: #7da98a;
  font-size: 15px;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 12px;
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

.search-trigger {
  border-color: #e3eae6;
  background: #fff;
  color: #5b6b63;
}

.list-shell {
  border-radius: 12px;
  background: #fff;
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
  background: #fff;
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
  padding: 18px 16px;
  border: 1px dashed #cfd9d3;
  border-radius: 12px;
  background: #fbfdfb;
  font: inherit;
  color: inherit;
  text-align: left;
  width: 100%;
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
  margin-bottom: 10px;
  font-style: normal;
  font-size: 18px;
}

.preview-card strong {
  display: block;
  color: #2f3a34;
  font-size: 15px;
}

.preview-card span {
  display: block;
  margin-top: 8px;
  color: #8a9a92;
  line-height: 1.6;
  font-size: 13px;
}

.preview-card small {
  display: inline-flex;
  margin-top: 12px;
  color: #5faf7a;
  font-size: 12px;
}

.preview-cta {
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

:deep(.land-dialog .el-dialog) {
  border-radius: 24px;
}

:deep(.land-dialog .el-dialog__header) {
  padding: 24px 24px 0;
}

:deep(.land-dialog .el-dialog__body) {
  padding: 16px 24px 12px;
}

:deep(.land-dialog .el-dialog__footer) {
  padding: 8px 24px 24px;
}

.dialog-progress {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  margin-bottom: 18px;
  padding: 0 14px;
  border-radius: 999px;
  background: rgba(95, 175, 122, 0.1);
  color: #4c9e68;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.dialog-section + .dialog-section {
  margin-top: 24px;
}

.dialog-section {
  padding: 0;
  border-top: 1px solid rgba(227, 234, 230, 0.92);
}

.dialog-section:first-of-type {
  border-top: none;
  padding-top: 0;
}

.dialog-section-optional {
  opacity: 1;
}

.dialog-heading {
  display: flex;
  gap: 14px;
  margin-bottom: 14px;
}

.dialog-heading > span {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(95, 175, 122, 0.08);
  color: #7da98a;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}

.dialog-heading h4 {
  margin: 0;
  color: #2f3a34;
  font-size: 17px;
}

.dialog-heading h4 small {
  margin-left: 6px;
  color: #8a9a92;
  font-size: 12px;
  font-weight: 500;
}

.dialog-heading p {
  margin: 5px 0 0;
  color: #8a9a92;
  line-height: 1.65;
}

.compact-form-item {
  margin-bottom: 0;
}

.form-group-shell {
  display: grid;
  gap: 24px;
}

.group-heading span {
  display: inline-block;
  color: #7da98a;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.group-heading h4 {
  margin: 8px 0 0;
  color: #2f3a34;
  font-size: 22px;
  line-height: 1.35;
}

.field-stack {
  display: grid;
  gap: 28px;
}

.field-block {
  display: grid;
  gap: 14px;
}

.field-block-primary {
  padding: 18px 18px 16px;
  border-radius: 22px;
  background: rgba(95, 175, 122, 0.05);
  border: 1px solid rgba(95, 175, 122, 0.12);
}

.field-block-secondary,
.field-block-optional {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid #e3eae6;
}

.size-input-shell {
  display: grid;
  gap: 12px;
}

.field-copy {
  display: grid;
  gap: 6px;
}

.field-copy strong {
  color: #2f3a34;
  font-size: 18px;
  line-height: 1.4;
}

.field-copy span {
  color: #8a9a92;
  font-size: 13px;
  line-height: 1.65;
}

.type-step-shell {
  display: grid;
  gap: 24px;
}

.planting-mode-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.mode-pick-card {
  display: grid;
  gap: 8px;
  padding: 18px 20px;
  border: 1px solid #dbe6df;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #f8fbf9);
  color: #5b6b63;
  text-align: left;
  cursor: pointer;
  transition: 0.18s ease;
}

.mode-pick-card span {
  color: #7da98a;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.mode-pick-card strong {
  color: #2f3a34;
  font-size: 20px;
}

.mode-pick-card small {
  color: #7a8b83;
  line-height: 1.6;
}

.mode-pick-card.active {
  border-color: #5faf7a;
  background: linear-gradient(180deg, rgba(95, 175, 122, 0.12), rgba(95, 175, 122, 0.06));
  box-shadow: 0 16px 28px rgba(95, 175, 122, 0.12);
}

.type-selection-panel {
  display: grid;
  gap: 14px;
  padding: 16px;
  border: 1px solid #e3eae6;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.74);
}

.type-selection-head strong {
  display: block;
  color: #2f3a34;
  font-size: 16px;
}

.type-selection-head span {
  display: block;
  margin-top: 6px;
  color: #8a9a92;
  font-size: 13px;
  line-height: 1.6;
}

.type-empty-hint {
  margin: 0;
  color: #8a9a92;
  line-height: 1.6;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.type-card,
.unit-option,
.status-option {
  border: 1px solid #e3eae6;
  background: #fff;
  color: #5b6b63;
  transition: 0.18s ease;
  cursor: pointer;
}

.type-card {
  display: grid;
  gap: 6px;
  padding: 16px;
  border-radius: 18px;
  text-align: left;
}

.type-card em {
  display: inline-flex;
  margin-bottom: 2px;
  font-style: normal;
  font-size: 18px;
}

.type-card strong {
  display: block;
  color: #2f3a34;
  font-size: 16px;
}

.type-card span {
  display: block;
  color: #5b6b63;
  line-height: 1.55;
  font-size: 13px;
}

.type-card small {
  color: #8a9a92;
  font-size: 12px;
  line-height: 1.5;
}

.type-card.active,
.unit-option.active,
.status-option.active {
  border-color: #5faf7a;
  background: rgba(95, 175, 122, 0.12);
  box-shadow: 0 10px 20px rgba(95, 175, 122, 0.12);
  color: #2f3a34;
}

.unit-switcher,
.status-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.unit-option {
  min-width: 112px;
  padding: 10px 16px;
  border-radius: 999px;
}

.light-group {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.light-option {
  position: relative;
  display: grid;
  gap: 6px;
  min-height: 116px;
  padding: 16px;
  border-radius: 18px;
  text-align: left;
}

.light-option strong {
  color: #2f3a34;
  font-size: 14px;
}

.light-option span {
  color: #8a9a92;
  font-size: 12px;
  line-height: 1.55;
}

.light-option-check {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #64c784 0%, #3f9f62 100%);
  box-shadow: 0 8px 18px rgba(79, 168, 107, 0.28);
  color: #ffffff;
  font-size: 13px;
  font-weight: 800;
}

.light-option-check :deep(svg) {
  width: 14px;
  height: 14px;
  color: #ffffff;
  stroke-width: 3;
}

.light-option.active {
  border-color: #4c9e68;
  background: rgba(95, 175, 122, 0.16);
  box-shadow: 0 12px 24px rgba(95, 175, 122, 0.14);
}

.light-option.active strong {
  color: #1f3f28;
}

.field-tip {
  display: block;
  margin-top: 2px;
  color: #8a9a92;
  line-height: 1.6;
}

.expand-trigger {
  justify-self: start;
  border: none;
  padding: 0;
  background: transparent;
  color: #5b6b63;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.18s ease;
}

.expand-trigger:hover {
  color: #4c9e68;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button {
  height: 44px;
}

.dialog-footer .secondary-action {
  border: none;
  background: transparent;
  box-shadow: none;
  color: #5b6b63;
  padding: 0 6px;
}

.dialog-footer .secondary-action:hover,
.dialog-footer .secondary-action:focus {
  border: none;
  background: transparent;
  box-shadow: none;
  color: #2f3a34;
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
    padding: 12px 18px 0;
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

  .future-preview,
  .planting-mode-grid,
  .type-grid,
  .land-info-grid,
  .light-group {
    grid-template-columns: 1fr;
  }
}
</style>
