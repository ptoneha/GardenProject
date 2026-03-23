<template>
  <div class="garden-gallery">
    <div v-if="!containers.length" class="empty-block">
      <el-empty description="当前任务关联的地块下暂无容器，无法渲染园艺布局。" />
    </div>

    <section v-for="group in groupedLands" :key="group.land.landId" class="land-section">
      <div class="land-header">
        <div>
          <span class="eyebrow">Spatial Simulator</span>
          <h3>{{ group.land.landCode }}</h3>
        </div>
        <div class="land-meta">
          <el-tag effect="plain">{{ getLightLevelLabel(group.land.lightLevel) }}</el-tag>
          <span>{{ group.containers.length }} 个容器模块</span>
        </div>
      </div>

      <div class="land-board">
        <el-popover
          v-for="container in group.containers"
          :key="container.containerId"
          placement="top"
          :width="280"
          trigger="hover"
        >
          <template #reference>
            <article
              class="container-tile"
              :class="getAgriContainerTypeMeta(container.containerType).className"
              :style="{ background: tileGradient(primaryResult(container.containerId)?.cropId) }"
            >
              <div class="tile-header">
                <strong>{{ container.containerName }}</strong>
                <span>#{{ container.containerId }}</span>
              </div>

              <div class="tile-body">
                <div class="tile-plant">
                  <template v-if="cropVisual(primaryResult(container.containerId)?.cropId).type === 'image'">
                    <img :src="cropVisual(primaryResult(container.containerId)?.cropId).value" :alt="cropName(primaryResult(container.containerId)?.cropId)">
                  </template>
                  <template v-else>
                    <span>{{ cropVisual(primaryResult(container.containerId)?.cropId).value }}</span>
                  </template>
                </div>
                <div>
                  <p>{{ cropName(primaryResult(container.containerId)?.cropId) }}</p>
                  <small>{{ plantCountText(primaryResult(container.containerId)) }}</small>
                </div>
              </div>
            </article>
          </template>

          <div class="popover-content">
            <div class="popover-head">
              <strong>{{ container.containerName }}</strong>
              <span>{{ getAgriContainerTypeMeta(container.containerType).label }}</span>
            </div>
            <p class="scheme-line">种植方案：{{ cropName(primaryResult(container.containerId)?.cropId) }} × {{ primaryResult(container.containerId)?.plantCount || 0 }} 株</p>

            <div class="fit-grid">
              <div class="fit-item">
                <el-icon :class="fitDepth(container, group.land).match ? 'fit-good' : 'fit-warn'">
                  <component :is="fitDepth(container, group.land).match ? CircleCheckFilled : WarningFilled" />
                </el-icon>
                <div>
                  <span>根系深度匹配</span>
                  <strong>{{ fitDepth(container, group.land).text }}</strong>
                </div>
              </div>
              <div class="fit-item">
                <el-icon :class="fitLight(container, group.land).match ? 'fit-good' : 'fit-warn'">
                  <component :is="fitLight(container, group.land).match ? Sunny : WarningFilled" />
                </el-icon>
                <div>
                  <span>光照环境匹配</span>
                  <strong>{{ fitLight(container, group.land).text }}</strong>
                </div>
              </div>
            </div>

            <div class="time-window">
              <span>预计生长期</span>
              <strong>{{ growthWindow(primaryResult(container.containerId)?.cropId) }}</strong>
            </div>
          </div>
        </el-popover>
      </div>
    </section>
  </div>
</template>

<script setup>
import { CircleCheckFilled, Sunny, WarningFilled } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { getAgriContainerTypeMeta, getCropVisual, getLightLevelLabel } from '@/utils/agriculture'

const props = defineProps({
  lands: {
    type: Array,
    default: () => []
  },
  containers: {
    type: Array,
    default: () => []
  },
  crops: {
    type: Array,
    default: () => []
  },
  results: {
    type: Array,
    default: () => []
  }
})

const cropMap = computed(() => new Map(props.crops.map((item) => [item.cropId, item])))
const resultsMap = computed(() => {
  const map = new Map()
  props.results.forEach((item) => {
    const list = map.get(item.containerId) || []
    list.push(item)
    map.set(item.containerId, list)
  })
  return map
})

const groupedLands = computed(() => props.lands.map((land) => ({
  land,
  containers: props.containers.filter((item) => item.landId === land.landId)
})))

function containerResults(containerId) {
  return resultsMap.value.get(containerId) || []
}

function primaryResult(containerId) {
  return containerResults(containerId)[0] || null
}

function cropName(cropId) {
  if (!cropId) {
    return '尚未分配植物'
  }
  return cropMap.value.get(cropId)?.cropName || `作物 ${cropId}`
}

function cropVisual(cropId) {
  return getCropVisual(cropMap.value.get(cropId))
}

function plantCountText(result) {
  if (!result) {
    return '等待分配'
  }
  return `${result.plantCount || 0} 株`
}

function cropNeedDepth(crop) {
  const category = String(crop?.category || '').toLowerCase()
  if (String(crop?.isPulse).toUpperCase() === 'Y') {
    return 16
  }
  if (category.includes('果') || category.includes('fruit')) {
    return 18
  }
  if (category.includes('leaf') || category.includes('菜')) {
    return 10
  }
  return 12
}

function cropNeedLight(crop) {
  const category = String(crop?.category || '').toLowerCase()
  if (category.includes('果') || category.includes('flower') || category.includes('花')) {
    return 3
  }
  return 2
}

function fitDepth(container) {
  const crop = cropMap.value.get(primaryResult(container.containerId)?.cropId)
  const required = cropNeedDepth(crop)
  const actual = Number(container.depthCm || 0)
  return {
    match: actual >= required,
    text: actual >= required ? `深度 ${actual}cm，适配` : `建议至少 ${required}cm`
  }
}

function fitLight(container, land) {
  const crop = cropMap.value.get(primaryResult(container.containerId)?.cropId)
  const required = cropNeedLight(crop)
  const actual = Number(land.lightLevel || 0)
  return {
    match: actual >= required,
    text: actual >= required ? `${getLightLevelLabel(actual)}，匹配` : `建议提升到 ${required} 级光照`
  }
}

function growthWindow(cropId) {
  const crop = cropMap.value.get(cropId)
  const category = String(crop?.category || '').toLowerCase()
  if (String(crop?.isPulse).toUpperCase() === 'Y') {
    return '约 75 天可收割'
  }
  if (category.includes('leaf') || category.includes('菜')) {
    return '约 45 天可收割'
  }
  if (category.includes('果') || category.includes('fruit')) {
    return '约 60 天可收割'
  }
  return '约 55 天可收割'
}

function tileGradient(cropId) {
  const palette = [
    ['#f4dcc3', '#e6f0df'],
    ['#ead8f0', '#f7ead9'],
    ['#dceee0', '#f4dfd0'],
    ['#f4e2d7', '#dde8f7']
  ]
  const pair = palette[Number(cropId || 0) % palette.length]
  return `linear-gradient(145deg, ${pair[0]}, ${pair[1]})`
}
</script>

<style scoped lang="scss">
.garden-gallery {
  display: grid;
  gap: 22px;
}

.land-section {
  padding: 24px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(116, 147, 101, 0.12), transparent 32%),
    linear-gradient(180deg, #fff8ee, #f4ead7);
  border: 1px solid rgba(100, 81, 57, 0.12);
}

.land-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.eyebrow {
  display: block;
  color: #8b6a51;
  font-size: 12px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
}

.land-header h3 {
  margin: 8px 0 0;
  font-size: 28px;
  color: #304032;
}

.land-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #6e624f;
}

.land-board {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.42);
  border: 1px dashed rgba(100, 81, 57, 0.16);
}

.container-tile {
  min-height: 180px;
  padding: 16px;
  border-radius: 22px;
  border: 1px solid rgba(122, 94, 63, 0.12);
  display: grid;
  align-content: space-between;
  gap: 18px;
  cursor: pointer;
}

.container-tile.rack {
  background-image:
    repeating-linear-gradient(180deg, transparent, transparent 42px, rgba(88, 104, 82, 0.12) 42px, rgba(88, 104, 82, 0.12) 44px);
}

.container-tile.hanging::before {
  content: '';
  width: 50px;
  height: 18px;
  margin: 0 auto;
  border: 2px solid rgba(101, 85, 69, 0.36);
  border-bottom: none;
  border-radius: 999px 999px 0 0;
}

.tile-header,
.tile-body {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.tile-header strong {
  color: #314637;
}

.tile-header span,
.tile-body small {
  color: #7b6b58;
}

.tile-body {
  align-items: center;
}

.tile-plant {
  width: 62px;
  height: 62px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.82);
  flex-shrink: 0;
}

.tile-plant img {
  width: 100%;
  height: 100%;
  border-radius: 18px;
  object-fit: cover;
}

.tile-plant span {
  font-size: 34px;
}

.tile-body p {
  margin: 0 0 6px;
  color: #304435;
  font-weight: 600;
}

.popover-content {
  display: grid;
  gap: 14px;
}

.popover-head strong {
  display: block;
  color: #304435;
}

.popover-head span {
  color: #7b6b58;
  font-size: 13px;
}

.scheme-line {
  margin: 0;
  color: #64594d;
}

.fit-grid {
  display: grid;
  gap: 12px;
}

.fit-item {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 16px;
  background: rgba(247, 244, 238, 0.92);
}

.fit-item span {
  display: block;
  color: #8a6d55;
  font-size: 12px;
}

.fit-item strong {
  color: #304435;
}

.fit-good {
  color: #4f9368;
}

.fit-warn {
  color: #d06f5f;
}

.time-window {
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(234, 243, 232, 0.82);
}

.time-window span {
  display: block;
  color: #8a6d55;
  font-size: 12px;
}

.time-window strong {
  display: block;
  margin-top: 6px;
  color: #304435;
}

.empty-block {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 249, 241, 0.9);
}
</style>
