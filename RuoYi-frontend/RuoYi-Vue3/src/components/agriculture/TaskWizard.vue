<template>
  <el-drawer
    :model-value="modelValue"
    size="920px"
    append-to-body
    destroy-on-close
    :with-header="false"
    class="task-wizard"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div class="wizard-shell">
      <section class="step-brief">
        <span>步骤 {{ activeStep + 1 }} / {{ stepTitles.length }}</span>
        <strong>{{ currentStepMeta.title }}</strong>
        <p>{{ currentStepMeta.description }}</p>
      </section>

      <div v-show="activeStep === 0" class="panel">
        <div class="mode-grid">
          <button
            v-for="option in sceneOptions"
            :key="option.value"
            type="button"
            class="mode-card"
            :class="{ active: Number(form.mode) === option.value }"
            @click="form.mode = option.value"
          >
            <span v-if="Number(form.mode) === option.value" class="mode-selected-tag">已选择</span>
            <span v-if="Number(form.mode) === option.value" class="mode-selected-check">✓</span>
            <span class="mode-card__eyebrow">{{ option.kicker }}</span>
            <strong>{{ option.title }}</strong>
            <small>{{ option.subtitle }}</small>
            <div class="mode-feature-list">
              <p>
                <span>适合谁</span>
                <b>{{ option.audience }}</b>
              </p>
              <p>
                <span>关注重点</span>
                <b>{{ option.focus }}</b>
              </p>
              <p>
                <span>结果偏向</span>
                <b>{{ option.result }}</b>
              </p>
            </div>
          </button>
        </div>

        <div class="mode-hint-card">
          <strong>{{ currentModeHint.title }}</strong>
          <p>{{ currentModeHint.description }}</p>
        </div>

        <div class="question-stack">
          <section class="question-card">
            <span class="question-kicker">计划名称</span>
            <h4>你想怎么称呼这份计划？</h4>
            <p>起一个你自己一眼就能认出的名字，后面查看方案会更轻松。</p>
            <el-input v-model="form.taskName" maxlength="60" placeholder="例如：春季阳台蔬菜计划" />
          </section>

          <section class="question-card">
            <span class="question-kicker">计划季节</span>
            <h4>这份计划更接近哪个季节？</h4>
            <p>季节会影响系统对作物生长期和环境匹配的判断。</p>
            <div class="season-pills">
              <button
                v-for="season in AGRI_SEASON_OPTIONS"
                :key="season.value"
                type="button"
                class="season-pill"
                :class="{ active: form.taskSeason === season.value }"
                @click="form.taskSeason = season.value"
              >
                {{ season.label }}
              </button>
            </div>
          </section>

          <section class="question-card">
            <span class="question-kicker">预算预期</span>
            <h4>你打算投入多少预算？</h4>
            <p>这会影响系统给出的推荐范围，以及不同方案的可行性判断。</p>
            <el-input-number v-model="form.totalBudget" :min="0" :precision="2" :step="100" controls-position="right" />
          </section>
        </div>
      </div>

      <div v-show="activeStep === 1" class="panel">
        <div class="section-copy">
          <h3>{{ spaceStepTitle }}</h3>
          <p>{{ spaceStepDescription }}</p>
        </div>

        <div v-if="!hasAvailableSpaces" class="empty-space-state">
          <div class="empty-space-visual">
            <span class="visual-sun"></span>
            <span class="visual-plot"></span>
            <span class="visual-sprout"></span>
          </div>
          <h4>你还没有添加任何种植空间</h4>
          <p>从地块、阳台或种植区域开始，建立你的第一份种植基础资料。</p>
          <el-button class="primary-btn" @click="goToSpacePage">去添加我的地块</el-button>
          <small v-if="isGardenMode">后续你还可以继续添加花盆、种植箱或立体架。</small>
        </div>

        <template v-else>
          <div class="space-tip-card">
            <strong>{{ isGardenMode ? '空间会直接影响容器与布局建议' : '空间会直接影响面积与收益测算' }}</strong>
            <p>{{ selectionHint }}</p>
          </div>

          <div class="land-grid">
            <button
              v-for="land in lands"
              :key="land.landId"
              type="button"
              class="land-card"
              :class="{ active: form.selectedLandIds.includes(land.landId) }"
              @click="toggleLand(land.landId)"
            >
              <div class="thumbnail" :style="getLandThumbnailStyle(land)">
                <span>{{ land.landCode }}</span>
              </div>
              <div class="land-card-top">
                <strong>{{ land.landCode }}</strong>
                <em>{{ form.selectedLandIds.includes(land.landId) ? '已选择' : '点击选择' }}</em>
              </div>
              <div class="land-card-meta">
                <div>
                  <span>面积</span>
                  <b>{{ resolveLandAreaSqm(land).toFixed(2) }} sqm</b>
                </div>
                <div>
                  <span>光照</span>
                  <b>{{ getLightLevelLabel(land.lightLevel) }}</b>
                </div>
                <div>
                  <span>类型</span>
                  <b>{{ getLandTypeLabel(land.landType) }}</b>
                </div>
              </div>
            </button>
          </div>
        </template>
      </div>

      <div v-show="activeStep === 2" class="panel">
        <div class="section-copy">
          <h3>补充你的偏好，让推荐更贴近你</h3>
          <p>这些偏好不会改变你的基础条件，但会帮助系统更理解你想要的种植感受。</p>
        </div>

        <div class="slider-grid">
          <div class="slider-card">
            <span>美观度</span>
            <strong>{{ form.weightAesthetic }}%</strong>
            <el-slider v-model="form.weightAesthetic" :min="0" :max="100" />
          </div>
          <div class="slider-card">
            <span>收获感</span>
            <strong>{{ form.weightYield }}%</strong>
            <el-slider v-model="form.weightYield" :min="0" :max="100" />
          </div>
          <div class="slider-card">
            <span>上手轻松度</span>
            <strong>{{ form.weightDifficulty }}%</strong>
            <el-slider v-model="form.weightDifficulty" :min="0" :max="100" />
          </div>
        </div>

        <div class="constraints-grid">
          <section class="form-card">
            <div class="card-copy">
              <span>结构性偏好</span>
              <h4>如果你想保留豆类搭配，可以在这里设定比例</h4>
            </div>
            <el-form label-position="top">
              <el-form-item label="豆类最小比例">
                <el-slider v-model="form.minPulseRatio" :min="0" :max="100" show-input />
              </el-form-item>
            </el-form>
          </section>

          <div class="hint-card">
            <h4>轻提示</h4>
            <ul>
              <li>优先作物和排除作物不能同时包含同一项。</li>
              <li>如果设置了豆类比例，请确保候选作物里包含豆类。</li>
              <li>阳台与庭院场景会更依赖容器数量、深度和光照条件。</li>
            </ul>
          </div>
        </div>

        <div class="crop-selector-stack">
          <crop-card-selector
            v-model="form.cropWhitelistIds"
            :crops="crops"
            title="优先考虑的作物"
            eyebrow="Preferred"
            description="点选你更想尝试的作物，系统会优先考虑这些方向。"
            search-placeholder="按名称搜索你偏好的作物"
          />
          <crop-card-selector
            v-model="form.cropBlacklistIds"
            :crops="crops"
            title="暂时不考虑的作物"
            eyebrow="Avoid"
            description="把当前不想种的作物排除掉，让推荐结果更聚焦。"
            search-placeholder="按名称搜索要排除的作物"
          />
        </div>
      </div>

      <div v-show="activeStep === 3" class="panel summary-panel">
        <div class="section-copy">
          <h3>准备生成这份方案</h3>
          <p>最后确认一次你的场景、空间和偏好设置，然后就可以开始生成结果。</p>
        </div>
        <div class="summary-grid">
          <article>
            <span>种植方式</span>
            <strong>{{ getAgriModeLabel(form.mode) }}</strong>
          </article>
          <article>
            <span>已选空间</span>
            <strong>{{ form.selectedLandIds.length }} 处</strong>
          </article>
          <article>
            <span>预算</span>
            <strong>{{ Number(form.totalBudget || 0).toFixed(2) }} 元</strong>
          </article>
          <article>
            <span>季节</span>
            <strong>{{ getAgriSeasonLabel(form.taskSeason) }}</strong>
          </article>
        </div>
        <div class="chip-row">
          <el-tag v-for="land in selectedLands" :key="land.landId" round effect="plain">{{ land.landCode }}</el-tag>
        </div>
      </div>

      <footer class="footer">
        <div>
          <el-button v-if="activeStep > 0" class="soft-btn" @click="activeStep -= 1">返回</el-button>
        </div>
        <div class="footer-actions">
          <span v-if="footerHint" class="footer-hint">{{ footerHint }}</span>
          <el-button class="soft-btn" @click="emit('update:modelValue', false)">取消</el-button>
          <el-button
            v-if="activeStep < stepTitles.length - 1"
            class="primary-btn"
            :disabled="isNextStepDisabled"
            @click="nextStep"
          >
            继续下一步
          </el-button>
          <template v-else>
            <el-button class="soft-btn" :loading="submitting" @click="submit(false)">先保存这份计划</el-button>
            <el-button class="primary-btn" :loading="submitting" @click="submit(true)">开始生成方案</el-button>
          </template>
        </div>
      </footer>
    </div>
  </el-drawer>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CropCardSelector from '@/components/agriculture/CropCardSelector.vue'
import {
  AGRI_MODE_OPTIONS,
  AGRI_SEASON_OPTIONS,
  formatCropIdList,
  getAgriModeLabel,
  getAgriSeasonLabel,
  getLandThumbnailStyle,
  getLightLevelLabel,
  parseCropIdList,
  resolveLandAreaSqm
} from '@/utils/agriculture'

const router = useRouter()
const route = useRoute()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  task: {
    type: Object,
    default: () => ({})
  },
  selectedLandIds: {
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
  containers: {
    type: Array,
    default: () => []
  },
  submitting: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'submit'])

const stepTitles = ['你的种植方式', '你的空间', '你的偏好', '生成方案']
const activeStep = ref(0)

const form = reactive({
  taskId: null,
  taskName: '',
  mode: null,
  totalBudget: 1000,
  taskSeason: 'SPRING',
  weightAesthetic: 45,
  weightYield: 35,
  weightDifficulty: 20,
  minPulseRatio: 0,
  cropWhitelistIds: [],
  cropBlacklistIds: [],
  selectedLandIds: [],
  status: '0',
  ownerUserId: null
})

const sceneOptions = computed(() => AGRI_MODE_OPTIONS.map((mode) => {
  if (mode.value === 1) {
    return {
      ...mode,
      kicker: '收益导向场景',
      title: '我有耕地，想关注收益',
      subtitle: '农户模式',
      audience: '农户 / 小规模种植',
      focus: '预算、面积、收益效率',
      result: '更偏向收益与资源利用',
      description: '更适合有地块面积和预算约束的规划，系统会更重视投入与产出的效率。'
    }
  }
  return {
    ...mode,
    kicker: '居家种植场景',
    title: '我用阳台或庭院种植',
    subtitle: '园艺模式',
    audience: '家庭园艺 / 空间规划',
    focus: '容器布局、体验、可视化',
    result: '更偏向空间适配与观感体验',
    description: '更适合容器、阳台和庭院种植，系统会更重视布置体验与空间适配。'
  }
}))

const currentModeHint = computed(() => {
  if (Number(form.mode) === 2) {
    return {
      title: '你当前选择的是居家种植场景',
      description: '系统会重点关注容器条件、光照环境和空间布局，帮助你生成更适合阳台或庭院的种植组合。'
    }
  }
  return {
    title: '你当前选择的是收益型种植场景',
    description: '系统会重点关注预算、面积和收益效率，帮助你更快得到适合耕地使用的方案。'
  }
})

const selectedLands = computed(() => props.lands.filter((item) => form.selectedLandIds.includes(item.landId)))
const isGardenMode = computed(() => Number(form.mode) === 2)
const hasAvailableSpaces = computed(() => props.lands.length > 0)
const isStepZeroReady = computed(() => Boolean(form.mode) && Boolean(form.taskName.trim()) && Number(form.totalBudget) >= 0)
const spaceStepTitle = computed(() => (isGardenMode.value ? '选择这次计划会使用到的种植空间' : '选择这次计划会使用到的地块'))
const spaceStepDescription = computed(() => (
  isGardenMode.value
    ? '你的地块、阳台或种植区域，会决定这份方案能怎么种、种多少。'
    : '先告诉我们你有哪些地块，系统才能判断哪些作物更适合你。'
))
const selectionHint = computed(() => (
  isGardenMode.value
    ? '先挑出这次会用到的阳台、庭院或露台区域，后续结果会基于这些空间继续匹配容器。'
    : '先挑出本次会参与规划的地块，系统才能继续评估面积、光照和预算条件。'
))

const currentStepMeta = computed(() => {
  const steps = [
    {
      title: '选择你的种植场景',
      description: '这会影响系统如何理解你的空间、推荐逻辑与结果展示方式。'
    },
    {
      title: isGardenMode.value ? '选择你的种植空间' : '选择你的计划地块',
      description: '系统需要知道你有哪些可用空间，才能继续判断能怎么种、种多少。'
    },
    {
      title: '补充你的种植偏好',
      description: '这些偏好会帮助系统更贴近你的习惯和目标来组织方案。'
    },
    {
      title: '确认并生成方案',
      description: '最后确认一次基础信息，系统就会开始为你组合这份种植方案。'
    }
  ]
  return steps[activeStep.value]
})

const isNextStepDisabled = computed(() => {
  if (activeStep.value === 0) {
    return !isStepZeroReady.value
  }
  if (activeStep.value === 1) {
    return !hasAvailableSpaces.value || !form.selectedLandIds.length
  }
  if (activeStep.value === 2) {
    return form.cropWhitelistIds.some((item) => form.cropBlacklistIds.includes(item))
  }
  return false
})

const footerHint = computed(() => {
  if (activeStep.value === 0 && !form.mode) {
    return '先选一个种植场景，我们才能为你生成更合适的方案。'
  }
  if (activeStep.value === 0 && !form.taskName.trim()) {
    return '继续之前，先给这份计划起一个名字。'
  }
  if (activeStep.value === 1 && !hasAvailableSpaces.value) {
    return '继续之前，先添加至少一个种植空间。'
  }
  if (activeStep.value === 1 && !form.selectedLandIds.length) {
    return '继续之前，先选中至少一个会参与这次计划的空间。'
  }
  if (activeStep.value === 2 && form.cropWhitelistIds.some((item) => form.cropBlacklistIds.includes(item))) {
    return '优先作物和排除作物不能重复。'
  }
  return ''
})

function resetForm() {
  activeStep.value = 0
  form.taskId = props.task?.taskId || null
  form.taskName = props.task?.taskName || ''
  form.mode = props.task?.mode ?? null
  form.totalBudget = Number(props.task?.totalBudget ?? 1000)
  form.taskSeason = props.task?.taskSeason || 'SPRING'
  form.weightAesthetic = Math.round(Number(props.task?.weightAesthetic ?? 0.45) * 100)
  form.weightYield = Math.round(Number(props.task?.weightYield ?? 0.35) * 100)
  form.weightDifficulty = Math.round(Number(props.task?.weightDifficulty ?? 0.2) * 100)
  form.minPulseRatio = Math.round(Number(props.task?.minPulseRatio ?? 0) * 100)
  form.cropWhitelistIds = parseCropIdList(props.task?.cropWhitelist)
  form.cropBlacklistIds = parseCropIdList(props.task?.cropBlacklist)
  form.selectedLandIds = [...props.selectedLandIds]
  form.status = props.task?.status || '0'
  form.ownerUserId = props.task?.ownerUserId || null
}

watch(() => [props.modelValue, props.task, props.selectedLandIds], () => {
  if (props.modelValue) {
    resetForm()
  }
}, { deep: true, immediate: true })

function toggleLand(landId) {
  if (form.selectedLandIds.includes(landId)) {
    form.selectedLandIds = form.selectedLandIds.filter((item) => item !== landId)
    return
  }
  form.selectedLandIds = [...form.selectedLandIds, landId]
}

function getLandTypeLabel(value) {
  const mapping = {
    1: '露天地块',
    2: '半阴种植区',
    3: '庭院空间',
    4: '阳台空间'
  }
  return mapping[Number(value)] || '种植空间'
}

function goToSpacePage() {
  const target = route.path.startsWith('/portal') ? '/portal/lands' : '/agriculture/land'
  emit('update:modelValue', false)
  if (route.path.startsWith('/portal') && form.mode) {
    window.localStorage.setItem('portalAgriModeDraft', String(form.mode))
    router.push({ path: target, query: { mode: String(form.mode) } })
    return
  }
  router.push(target)
}

function validateStep() {
  if (activeStep.value === 0) {
    if (!form.taskName.trim()) {
      ElMessage.warning('请先填写计划名称')
      return false
    }
    if (!form.mode) {
      ElMessage.warning('请先选择适合你的种植场景')
      return false
    }
    if (form.totalBudget < 0) {
      ElMessage.warning('预算不能是负数')
      return false
    }
  }
  if (activeStep.value === 1 && !form.selectedLandIds.length) {
    ElMessage.warning('请至少选择一个地块或种植空间')
    return false
  }
  if (activeStep.value === 2) {
    const overlap = form.cropWhitelistIds.filter((item) => form.cropBlacklistIds.includes(item))
    if (overlap.length) {
      ElMessage.warning('优先作物和排除作物不能重复')
      return false
    }
  }
  return true
}

function nextStep() {
  if (!validateStep()) {
    return
  }
  activeStep.value += 1
}

function submit(execute) {
  if (!validateStep()) {
    return
  }
  emit('submit', {
    task: {
      taskId: form.taskId,
      taskName: form.taskName.trim(),
      mode: form.mode,
      totalBudget: Number(form.totalBudget || 0).toFixed(2),
      taskSeason: form.taskSeason,
      weightAesthetic: (form.weightAesthetic / 100).toFixed(2),
      weightYield: (form.weightYield / 100).toFixed(2),
      weightDifficulty: (form.weightDifficulty / 100).toFixed(2),
      minPulseRatio: (form.minPulseRatio / 100).toFixed(4),
      cropWhitelist: formatCropIdList(form.cropWhitelistIds),
      cropBlacklist: formatCropIdList(form.cropBlacklistIds),
      status: form.status,
      ownerUserId: form.ownerUserId
    },
    landIds: [...form.selectedLandIds],
    execute
  })
}
</script>

<style scoped lang="scss">
.task-wizard {
  :deep(.el-drawer) {
    background:
      radial-gradient(circle at top left, rgba(141, 186, 131, 0.18), transparent 28%),
      linear-gradient(180deg, #fffef9, #eef5ea);
  }
}

.wizard-shell {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel,
.footer {
  background: rgba(255, 255, 255, 0.78);
  border-radius: 24px;
  border: 1px solid rgba(111, 143, 101, 0.12);
}

.step-brief {
  padding: 18px 22px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid rgba(111, 143, 101, 0.1);
}

.step-brief span {
  display: inline-block;
  color: #829281;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.step-brief strong {
  display: block;
  margin-top: 8px;
  color: #35503a;
  font-size: 22px;
}

.step-brief p,
.section-copy p,
.space-tip-card p,
.mode-hint-card p,
.empty-space-state p {
  margin: 8px 0 0;
  color: #647265;
  line-height: 1.7;
}

.panel {
  flex: 1;
  padding: 26px;
  overflow: auto;
}

.section-copy {
  margin-bottom: 18px;
}

.section-copy h3 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #2f4736;
}

.mode-grid,
.slider-grid,
.constraints-grid,
.summary-grid,
.land-grid {
  display: grid;
  gap: 16px;
}

.mode-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin-bottom: 18px;
}

.mode-card,
.land-card {
  border: 1px solid rgba(111, 143, 101, 0.14);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(242, 248, 239, 0.86));
  border-radius: 22px;
  cursor: pointer;
  transition: 0.24s ease;
  text-align: left;
}

.mode-card {
  position: relative;
  padding: 20px;
}

.mode-card.active,
.land-card.active {
  transform: translateY(-2px);
  border-color: rgba(111, 179, 123, 0.78);
  background: linear-gradient(180deg, rgba(242, 250, 239, 0.96), rgba(233, 246, 228, 0.96));
  box-shadow: 0 18px 34px rgba(95, 135, 105, 0.18);
}

.mode-card__eyebrow {
  color: #73916f;
  font-size: 12px;
  letter-spacing: 0.08em;
}

.mode-selected-tag,
.mode-selected-check {
  position: absolute;
}

.mode-selected-tag {
  top: 14px;
  right: 44px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(111, 179, 123, 0.14);
  color: #478252;
  font-size: 12px;
  font-weight: 600;
}

.mode-selected-check {
  top: 14px;
  right: 14px;
  width: 22px;
  height: 22px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
}

.mode-card strong {
  display: block;
  margin: 10px 0 8px;
  font-size: 23px;
  line-height: 1.35;
  color: #324536;
}

.mode-card.active strong {
  color: #23452d;
}

.mode-card small {
  display: block;
  color: #8f8575;
  margin-bottom: 10px;
}

.mode-card p,
.hint-card li {
  color: #617064;
  line-height: 1.7;
}

.mode-feature-list {
  display: grid;
  gap: 10px;
  margin-top: 12px;
}

.mode-feature-list p {
  margin: 0;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.62);
}

.mode-feature-list span {
  display: block;
  color: #80907c;
  font-size: 12px;
  margin-bottom: 4px;
}

.mode-feature-list b {
  color: #37513c;
  font-weight: 600;
}

.mode-hint-card,
.form-card,
.hint-card,
.slider-card,
.summary-grid article,
.space-tip-card,
.empty-space-state,
.question-card {
  border-radius: 22px;
  border: 1px solid rgba(111, 143, 101, 0.1);
}

.mode-hint-card {
  padding: 18px 20px;
  margin-bottom: 18px;
  background: linear-gradient(135deg, rgba(241, 248, 239, 0.78), rgba(255, 255, 255, 0.86));
}

.mode-hint-card strong,
.card-copy h4,
.hint-card h4,
.space-tip-card strong,
.empty-space-state h4 {
  color: #35503a;
}

.constraints-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.form-card,
.hint-card {
  display: grid;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(241, 248, 239, 0.72));
}

.card-copy span,
.summary-grid span,
.land-card-meta span {
  color: #83917f;
  font-size: 13px;
}

.card-copy h4 {
  margin: 8px 0 0;
  font-size: 22px;
  line-height: 1.35;
}

.question-stack {
  display: grid;
  gap: 16px;
  margin-top: 18px;
}

.question-card {
  padding: 22px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(241, 248, 239, 0.72));
}

.question-kicker {
  display: inline-block;
  color: #829281;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.question-card h4 {
  margin: 10px 0 8px;
  color: #35503a;
  font-size: 24px;
}

.question-card p {
  margin: 0 0 16px;
  color: #617064;
  line-height: 1.75;
}

.season-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.season-pill {
  border: 1px solid rgba(111, 143, 101, 0.18);
  border-radius: 999px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.72);
  color: #506451;
  cursor: pointer;
  transition: 0.2s ease;
}

.season-pill.active {
  border-color: rgba(111, 179, 123, 0.76);
  background: linear-gradient(135deg, rgba(138, 203, 149, 0.18), rgba(111, 179, 123, 0.18));
  color: #2d4b34;
  box-shadow: 0 10px 20px rgba(111, 179, 123, 0.14);
}

.empty-space-state {
  display: grid;
  justify-items: center;
  text-align: center;
  gap: 12px;
  padding: 34px 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(241, 248, 239, 0.78));
}

.empty-space-visual {
  position: relative;
  width: 156px;
  height: 118px;
  margin-bottom: 6px;
}

.visual-sun,
.visual-plot,
.visual-sprout {
  position: absolute;
  display: block;
}

.visual-sun {
  top: 8px;
  right: 18px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(243, 206, 128, 0.72);
  box-shadow: 0 0 0 10px rgba(243, 206, 128, 0.14);
}

.visual-plot {
  left: 22px;
  right: 22px;
  bottom: 20px;
  height: 46px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(216, 186, 148, 0.6), rgba(193, 145, 92, 0.42));
}

.visual-sprout {
  left: 50%;
  top: 26px;
  width: 36px;
  height: 52px;
  transform: translateX(-50%);
}

.visual-sprout::before,
.visual-sprout::after {
  content: '';
  position: absolute;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
}

.visual-sprout::before {
  left: 16px;
  bottom: 0;
  width: 4px;
  height: 32px;
  border-radius: 999px;
}

.visual-sprout::after {
  left: 4px;
  top: 4px;
  width: 28px;
  height: 18px;
  border-radius: 100% 0 100% 0;
  transform: rotate(-12deg);
}

.empty-space-state small {
  color: #849181;
  line-height: 1.7;
}

.space-tip-card {
  padding: 16px 18px;
  margin-bottom: 18px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.88), rgba(241, 248, 239, 0.72));
}

.space-tip-card strong {
  display: block;
  margin-bottom: 6px;
}

.crop-selector-stack {
  display: grid;
  gap: 18px;
  margin-top: 18px;
}

.land-grid {
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.land-card {
  padding: 16px;
}

.thumbnail {
  height: 110px;
  border-radius: 18px;
  display: flex;
  align-items: flex-end;
  padding: 14px;
  margin-bottom: 14px;
  color: #fffef7;
  font-weight: 600;
}

.land-card-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.land-card strong {
  display: block;
  color: #324537;
  font-size: 20px;
}

.land-card em {
  font-style: normal;
  color: #6da676;
  font-size: 13px;
}

.land-card-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.land-card-meta div {
  padding: 12px 12px 10px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.56);
}

.land-card-meta b {
  display: block;
  margin-top: 6px;
  color: #415544;
  font-weight: 600;
  line-height: 1.5;
}

.slider-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-bottom: 18px;
}

.slider-card {
  padding: 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(241, 248, 239, 0.76));
}

.slider-card span {
  color: #80907c;
}

.slider-card strong {
  display: block;
  margin: 8px 0 12px;
  font-size: 26px;
  color: #314736;
}

.task-wizard :deep(.el-slider__bar) {
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
}

.task-wizard :deep(.el-slider__button) {
  border-color: #6fb37b;
}

.task-wizard :deep(.el-input__wrapper),
.task-wizard :deep(.el-textarea__inner),
.task-wizard :deep(.el-input-number),
.task-wizard :deep(.el-input-number .el-input__wrapper) {
  border-radius: 18px;
}

.task-wizard :deep(.el-input-number) {
  width: 100%;
}

.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-bottom: 20px;
}

.summary-grid article {
  padding: 18px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(241, 248, 239, 0.8));
}

.summary-grid strong {
  display: block;
  margin-top: 10px;
  font-size: 28px;
  color: #35503b;
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.footer {
  padding: 18px 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
}

.footer-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-hint {
  color: #6f7f6f;
  font-size: 13px;
  margin-right: 4px;
}

.soft-btn,
.primary-btn {
  border-radius: 999px;
  padding-inline: 22px;
  font-weight: 600;
}

.soft-btn {
  color: #43614a;
  border: 1px solid rgba(111, 143, 101, 0.24);
  background: rgba(255, 255, 255, 0.84);
}

.primary-btn {
  color: #f8fff6;
  border: 0;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  box-shadow: 0 14px 28px rgba(111, 179, 123, 0.24);
}

@media (max-width: 960px) {
  .mode-grid,
  .constraints-grid,
  .slider-grid,
  .summary-grid,
  .land-card-meta {
    grid-template-columns: 1fr;
  }

  .footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .footer-actions {
    justify-content: flex-end;
    flex-wrap: wrap;
  }
}
</style>
