<template>
  <div class="plan-detail-page">
    <template v-if="plan">
      <section class="hero-summary">
        <div class="hero-copy">
          <span class="section-kicker">Plan Detail</span>
          <h1>{{ plan.name }}</h1>
          <div class="tag-row">
            <span v-for="tag in plan.tags" :key="tag" class="detail-tag">{{ tag }}</span>
          </div>
          <p class="hero-summary-text">{{ heroSummary }}</p>
        </div>

        <div class="hero-side">
          <div class="metric-grid">
            <article
              v-for="(metric, index) in heroMetrics"
              :key="metric.label"
              class="metric-card"
              :class="{ 'metric-card-primary': index === 0 }"
            >
              <span>{{ metric.label }}</span>
              <strong>{{ metric.value }}</strong>
            </article>
          </div>
          <div class="hero-actions">
            <el-button class="primary-button" @click="showFullDetail = true">查看剩余方案</el-button>
            <el-button class="back-button" @click="router.push('/portal/results')">返回画廊</el-button>
          </div>
        </div>
      </section>

      <section class="detail-card">
        <div class="section-head">
          <span class="section-kicker">Crop Notes</span>
          <h2>作物组合</h2>
        </div>
        <div class="crop-note-grid">
          <article v-for="item in cropNoteCards" :key="item.name" class="crop-note-card">
            <div class="crop-note-top">
              <div class="crop-note-name">
                <span class="crop-note-icon">{{ item.icon }}</span>
                <strong>{{ item.name }}</strong>
              </div>
              <span class="role-tag">{{ item.role }}</span>
            </div>
            <p>{{ item.note }}</p>
          </article>
        </div>
      </section>

      <section class="detail-card">
        <div class="section-head">
          <span class="section-kicker">Action Tips</span>
          <h2>操作建议</h2>
        </div>
        <div class="tips-grid">
          <article v-for="item in actionGroups" :key="item.title" class="tip-card">
            <div class="tip-head">
              <span class="step-index">{{ item.step }}</span>
              <span class="reason-label">{{ item.title }}</span>
            </div>
            <p>{{ item.text }}</p>
          </article>
        </div>
      </section>

      <el-dialog
        v-model="showFullDetail"
        append-to-body
        destroy-on-close
        :show-close="false"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
        class="plan-detail-modal"
        modal-class="plan-detail-modal-mask"
        width="60%"
      >
        <template #header>
          <div class="modal-head">
            <div>
              <span class="section-kicker">Remaining Plan</span>
              <h2>剩余方案</h2>
            </div>
            <button class="modal-close" type="button" @click="showFullDetail = false">×</button>
          </div>
        </template>

        <div class="modal-body">
          <section class="detail-card modal-section">
            <div class="section-head">
              <span class="section-kicker">Explanation</span>
              <h2>方案解读</h2>
            </div>
            <div class="explanation-block">
              <p class="explanation-lead">{{ explanationLead }}</p>
              <p class="section-copy">{{ explanationBody }}</p>
            </div>
          </section>

          <section class="detail-card modal-section layout-card">
            <div class="section-head">
              <div>
                <span class="section-kicker">Layout Preview</span>
                <h2>空间布局</h2>
              </div>
              <p class="layout-caption">{{ plan.layoutTitle }}</p>
            </div>

            <div class="layout-stage">
              <div class="layout-shell">
                <article
                  v-for="block in plan.layoutBlocks"
                  :key="`${block.label}-${block.crop}`"
                  class="layout-block"
                >
                  <span>{{ block.label }}</span>
                  <strong>{{ block.crop }}</strong>
                </article>
              </div>
            </div>
          </section>

          <section class="detail-card modal-section">
            <div class="section-head">
              <span class="section-kicker">Why This Plan</span>
              <h2>推荐原因</h2>
            </div>
            <div class="reason-grid">
              <article v-for="item in reasonCards" :key="item.title" class="reason-card">
                <span class="reason-label">{{ item.title }}</span>
                <p>{{ item.text }}</p>
              </article>
            </div>
          </section>
        </div>
      </el-dialog>
    </template>

    <section v-else class="detail-card empty-card">
      <h1>没有找到这份种植方案</h1>
      <p class="section-copy">这张卡片还没有对应的详情内容，你可以先返回结果画廊查看其他方案。</p>
      <el-button class="back-button" @click="router.push('/portal/results')">返回结果画廊</el-button>
    </section>
  </div>
</template>

<script setup name="PortalPlanDetail">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMockPlanById, mockPlanCards } from './mockPlans'
import { resolveMockExplanation } from './explanationAdapter'

const route = useRoute()
const router = useRouter()
const showFullDetail = ref(false)

const plan = computed(() => getMockPlanById(route.params.planId) || mockPlanCards[0] || null)
const explanation = computed(() => resolveMockExplanation(plan.value))

const heroMetrics = computed(() => {
  if (!plan.value) {
    return []
  }

  if (plan.value.type === 'farmer') {
    return [
      { label: '总收益', value: plan.value.metrics.profit },
      { label: '总成本', value: plan.value.metrics.cost },
      { label: '收益率', value: plan.value.metrics.roi }
    ]
  }

  return [
    { label: '空间利用率', value: plan.value.metrics.utilization },
    { label: '种植数量', value: plan.value.metrics.quantity },
    { label: '观赏评分', value: plan.value.metrics.score }
  ]
})

const heroSummary = computed(() => {
  if (!plan.value) {
    return ''
  }

  if (plan.value.type === 'farmer') {
    return '这份方案会优先保证收益结构清晰，让你更容易按主作物和补充作物分步落地。'
  }

  return '这份方案会优先保证组合关系清楚，让你先看懂种什么，再决定怎么摆放和执行。'
})

const explanationParts = computed(() => {
  const text = explanation.value?.summaryText || ''
  const match = text.match(/^.*?[。！？]/)

  if (!match) {
    return { lead: text, body: '' }
  }

  return {
    lead: match[0],
    body: text.slice(match[0].length).trim()
  }
})

const explanationLead = computed(() => explanationParts.value.lead)
const explanationBody = computed(() => explanationParts.value.body || '这份组合会围绕空间条件、作物搭配和实际执行节奏展开，让你更容易照着方案开始种植。')

const reasonCards = computed(() => {
  const reasons = explanation.value?.reasonList || []
  const labels = ['核心策略', '辅助优化', '空间利用']
  return labels.map((title, index) => ({
    title,
    text: reasons[index] || '这部分会在真实结果接入后显示更具体的推荐原因。'
  }))
})

const actionGroups = computed(() => {
  const advice = explanation.value?.adviceList || []
  const labels = ['准备阶段', '种植阶段', '后续维护']
  return labels.map((title, index) => ({
    title,
    step: index + 1,
    text: advice[index] || '后续可以根据植物长势再微调这份方案，让节奏更适合你。'
  }))
})

const cropNoteCards = computed(() => {
  const notes = plan.value?.cropNotes || []
  return notes.map((note, index) => {
    const parts = note.split('：')
    const rawTitle = parts[0] || note
    const iconMatch = rawTitle.match(/^(\S+)\s+/)
    const icon = iconMatch ? iconMatch[1] : '🌱'
    const name = rawTitle.replace(/^\S+\s+/, '')
    const content = parts.length > 1 ? parts.slice(1).join('：') : note

    let role = '辅助'
    if (index === 0) {
      role = '主植物'
    } else if (index === notes.length - 1) {
      role = '点缀'
    }

    return { icon, name, note: content, role }
  })
})
</script>

<style scoped lang="scss">
.plan-detail-page {
  display: grid;
  gap: 28px;
  color: #4f5b4c;
}

.hero-summary,
.detail-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(245, 249, 241, 0.9));
  border: 1px solid rgba(140, 171, 118, 0.12);
  border-radius: 24px;
  box-shadow: 0 14px 34px rgba(105, 112, 83, 0.08);
}

.hero-summary {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(300px, 0.95fr);
  gap: 28px;
  padding: 34px;
}

.detail-card {
  padding: 30px;
}

.section-kicker {
  display: inline-block;
  color: #b08e58;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-size: 12px;
}

.hero-copy h1,
.section-head h2,
.empty-card h1,
.modal-head h2 {
  margin: 12px 0 0;
  color: #445040;
  font-family: Georgia, 'Times New Roman', serif;
}

.hero-copy h1 {
  font-size: 42px;
  line-height: 1.12;
}

.section-head h2,
.empty-card h1,
.modal-head h2 {
  font-size: 30px;
}

.hero-summary-text,
.section-copy,
.layout-caption {
  color: #6f6b5f;
  line-height: 1.9;
}

.hero-summary-text {
  max-width: 640px;
  margin: 18px 0 0;
  font-size: 16px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 18px;
}

.detail-tag,
.reason-label,
.role-tag {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
}

.detail-tag,
.reason-label {
  background: rgba(241, 248, 239, 0.9);
  color: #608567;
}

.role-tag {
  background: rgba(248, 243, 229, 0.96);
  color: #9b7a47;
}

.hero-side {
  display: grid;
  gap: 18px;
  align-content: start;
  justify-items: stretch;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  width: 100%;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.metric-card,
.reason-card,
.crop-note-card,
.tip-card,
.layout-block {
  border-radius: 18px;
  border: 1px solid rgba(140, 171, 118, 0.12);
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 10px 24px rgba(112, 113, 87, 0.05);
}

.metric-card {
  padding: 18px 18px 20px;
}

.metric-card span {
  display: block;
  color: #998059;
  font-size: 12px;
}

.metric-card strong {
  display: block;
  margin-top: 10px;
  color: #445040;
  font-size: 24px;
}

.metric-card-primary {
  background: linear-gradient(135deg, rgba(245, 250, 239, 0.96), rgba(255, 252, 244, 0.96));
}

.metric-card-primary strong {
  font-size: 32px;
}

.section-head {
  display: grid;
  gap: 10px;
}

.crop-note-grid,
.tips-grid,
.reason-grid {
  display: grid;
  gap: 16px;
  margin-top: 22px;
}

.crop-note-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.tips-grid,
.reason-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.crop-note-card,
.tip-card,
.reason-card {
  padding: 20px;
}

.crop-note-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.crop-note-name {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #445040;
}

.crop-note-icon {
  display: inline-grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(241, 248, 239, 0.95);
  font-size: 18px;
}

.crop-note-card p,
.tip-card p,
.reason-card p {
  margin: 14px 0 0;
  color: #5f5b50;
  line-height: 1.8;
}

.tip-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.step-index {
  display: inline-grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(111, 179, 123, 0.14);
  color: #51795a;
  font-size: 12px;
  font-weight: 700;
}

.explanation-block {
  margin-top: 20px;
  display: grid;
  gap: 14px;
}

.explanation-lead {
  margin: 0;
  color: #445040;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.55;
}

.layout-card {
  background: linear-gradient(180deg, rgba(254, 252, 247, 0.96), rgba(242, 248, 238, 0.94));
}

.layout-caption {
  margin: 12px 0 0;
  max-width: 540px;
}

.layout-stage {
  margin-top: 22px;
  padding: 18px;
  border-radius: 22px;
  background:
    linear-gradient(rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)),
    repeating-linear-gradient(
      0deg,
      rgba(180, 191, 170, 0.12),
      rgba(180, 191, 170, 0.12) 1px,
      transparent 1px,
      transparent 28px
    ),
    repeating-linear-gradient(
      90deg,
      rgba(180, 191, 170, 0.1),
      rgba(180, 191, 170, 0.1) 1px,
      transparent 1px,
      transparent 28px
    );
  border: 1px solid rgba(140, 171, 118, 0.12);
}

.layout-shell {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  padding: 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.55);
}

.layout-block {
  min-height: 180px;
  padding: 18px;
  display: grid;
  align-content: space-between;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(243, 248, 239, 0.9));
}

.layout-block span {
  color: #998059;
  font-size: 12px;
}

.layout-block strong {
  color: #445040;
  font-size: 20px;
  line-height: 1.4;
}

.back-button {
  border-radius: 999px;
  border: 1px solid rgba(111, 179, 123, 0.2);
  background: rgba(255, 255, 255, 0.84);
  color: #5a785f;
}

.primary-button {
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  color: #fff;
  box-shadow: 0 12px 24px rgba(111, 179, 123, 0.2);
}

.empty-card {
  display: grid;
  gap: 16px;
}

.modal-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.modal-head h2 {
  color: #445040;
}

.modal-close {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: rgba(241, 248, 239, 0.92);
  color: #5f7c65;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}

.modal-body {
  display: grid;
  gap: 20px;
  max-height: min(72vh, 820px);
  overflow-y: auto;
  padding-right: 4px;
}

.modal-section {
  padding: 24px;
}

:deep(.plan-detail-modal) {
  max-width: 900px;
  border-radius: 24px;
  overflow: hidden;
}

:deep(.plan-detail-modal .el-dialog) {
  max-width: 900px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(245, 249, 241, 0.96));
  box-shadow: 0 18px 44px rgba(77, 88, 61, 0.18);
}

:deep(.plan-detail-modal .el-dialog__header) {
  padding: 24px 24px 0;
}

:deep(.plan-detail-modal .el-dialog__body) {
  padding: 18px 24px 24px;
}

:deep(.plan-detail-modal .el-overlay-dialog) {
  display: grid;
  place-items: center;
}

:deep(.plan-detail-modal-mask) {
  background: rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(6px);
}

:deep(.dialog-fade-enter-active),
:deep(.dialog-fade-leave-active) {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

:deep(.dialog-fade-enter-from),
:deep(.dialog-fade-leave-to) {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 1080px) {
  .hero-summary {
    grid-template-columns: 1fr;
  }

  .hero-side {
    justify-items: stretch;
  }

  .hero-actions {
    justify-content: flex-start;
  }

  .metric-grid,
  .crop-note-grid,
  .tips-grid,
  .reason-grid,
  .layout-shell {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-summary,
  .detail-card {
    padding: 24px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .section-head h2,
  .empty-card h1,
  .modal-head h2 {
    font-size: 26px;
  }

  .explanation-lead {
    font-size: 20px;
  }

  :deep(.plan-detail-modal .el-dialog) {
    width: calc(100vw - 24px) !important;
    margin: 0 12px;
  }
}
</style>
