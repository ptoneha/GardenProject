<template>
  <article class="plan-card" @click="handleView">
    <div class="plan-card__header">
      <div class="plan-card__title-wrap">
        <h4>{{ plan.name }}</h4>
        <div class="plan-card__tags">
          <span class="plan-chip season">{{ plan.season }}</span>
          <span class="plan-chip mode">{{ plan.mode }}</span>
        </div>
      </div>
    </div>

    <div class="plan-card__metrics">
      <template v-if="plan.type === 'farmer'">
        <div class="metric-item">
          <span>收益</span>
          <strong>{{ plan.metrics.profit }}</strong>
        </div>
        <div class="metric-item">
          <span>成本</span>
          <strong>{{ plan.metrics.cost }}</strong>
        </div>
        <div class="metric-item">
          <span>收益率</span>
          <strong>{{ plan.metrics.roi }}</strong>
        </div>
      </template>
      <template v-else>
        <div class="metric-item">
          <span>空间利用率</span>
          <strong>{{ plan.metrics.utilization }}</strong>
        </div>
        <div class="metric-item">
          <span>种植数量</span>
          <strong>{{ plan.metrics.quantity }}</strong>
        </div>
        <div class="metric-item">
          <span>观赏评分</span>
          <strong>{{ plan.metrics.score }}</strong>
        </div>
      </template>
    </div>

    <div class="plan-card__crops">
      <span
        v-for="crop in plan.crops"
        :key="crop"
        class="crop-pill"
      >
        {{ crop }}
      </span>
    </div>

    <div class="plan-card__footer">
      <el-button class="plan-card__button" @click.stop="handleView">查看方案</el-button>
    </div>
  </article>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  plan: {
    type: Object,
    required: true
  }
})

const router = useRouter()

function handleView() {
  console.log('[PlanArchiveCard] view plan', props.plan)
  router.push(`/portal/results/detail/${props.plan.id}`)
}
</script>

<style scoped lang="scss">
.plan-card {
  display: grid;
  gap: 18px;
  min-height: 100%;
  padding: 22px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 24px rgba(121, 108, 79, 0.08);
  border: 1px solid rgba(201, 168, 106, 0.12);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(121, 108, 79, 0.12);
}

.plan-card__title-wrap {
  display: grid;
  gap: 10px;
}

.plan-card h4 {
  margin: 0;
  color: #445040;
  font-size: 20px;
  line-height: 1.4;
}

.plan-card__tags,
.plan-card__crops {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.plan-chip,
.crop-pill {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 13px;
}

.plan-chip.season {
  background: rgba(111, 179, 123, 0.14);
  color: #5f8b67;
}

.plan-chip.mode {
  background: rgba(201, 168, 106, 0.14);
  color: #9c7a45;
}

.plan-card__metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.metric-item {
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(249, 245, 237, 0.88);
}

.metric-item span {
  display: block;
  color: #98835c;
  font-size: 12px;
}

.metric-item strong {
  display: block;
  margin-top: 6px;
  color: #465141;
  font-size: 18px;
}

.crop-pill {
  background: rgba(241, 248, 239, 0.88);
  color: #65735f;
}

.plan-card__footer {
  display: flex;
  justify-content: flex-start;
}

.plan-card__button {
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #8acb95 0%, #6fb37b 100%);
  color: #fff;
  box-shadow: 0 10px 20px rgba(108, 160, 115, 0.18);
}

.plan-card__button:deep(span) {
  font-weight: 600;
}

@media (max-width: 768px) {
  .plan-card__metrics {
    grid-template-columns: 1fr;
  }
}
</style>
