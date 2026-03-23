<template>
  <div class="crop-card-selector">
    <div class="selector-head">
      <div>
        <span class="eyebrow">{{ eyebrow }}</span>
        <h4>{{ title }}</h4>
        <p>{{ description }}</p>
      </div>
      <el-tag effect="plain">{{ selectedCrops.length }} 已选</el-tag>
    </div>

    <el-input
      v-model="keyword"
      clearable
      :placeholder="searchPlaceholder"
      class="search-input"
    />

    <div class="selected-chip-row" v-if="selectedCropItems.length">
      <el-tag
        v-for="crop in selectedCropItems"
        :key="crop.cropId"
        round
        effect="light"
        closable
        @close="toggleCrop(crop.cropId)"
      >
        {{ crop.cropName }}
      </el-tag>
    </div>

    <el-row :gutter="16" class="crop-grid">
      <el-col
        v-for="crop in filteredCrops"
        :key="crop.cropId"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="8"
        :xl="6"
      >
        <button
          type="button"
          class="crop-card"
          :class="{ active: selectedCrops.includes(crop.cropId) }"
          @click="toggleCrop(crop.cropId)"
        >
          <div class="media-shell">
            <img v-if="crop.imageUrl" :src="crop.imageUrl" :alt="crop.cropName">
            <div v-else class="fallback-media">{{ crop.cropName.slice(0, 1) }}</div>
          </div>
          <div class="card-copy">
            <strong>{{ crop.cropName }}</strong>
            <span>{{ crop.imageUrl || '未配置图片' }}</span>
          </div>
        </button>
      </el-col>
    </el-row>

    <el-empty
      v-if="!filteredCrops.length"
      :description="keyword ? '没有匹配的作物卡片' : '当前没有可选作物'"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  crops: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: '作物选择'
  },
  eyebrow: {
    type: String,
    default: 'Crop Selection'
  },
  description: {
    type: String,
    default: '点击卡片即可选择作物。'
  },
  searchPlaceholder: {
    type: String,
    default: '按名称搜索作物'
  }
})

const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const selectedCrops = ref([])

watch(() => props.modelValue, (value) => {
  selectedCrops.value = Array.isArray(value) ? [...value] : []
}, { immediate: true, deep: true })

const filteredCrops = computed(() => {
  const search = keyword.value.trim().toLowerCase()
  if (!search) {
    return props.crops
  }
  return props.crops.filter((crop) => String(crop.cropName || '').toLowerCase().includes(search))
})

const selectedCropItems = computed(() => props.crops.filter((crop) => selectedCrops.value.includes(crop.cropId)))

function toggleCrop(cropId) {
  if (selectedCrops.value.includes(cropId)) {
    selectedCrops.value = selectedCrops.value.filter((item) => item !== cropId)
  } else {
    selectedCrops.value = [...selectedCrops.value, cropId]
  }
  emit('update:modelValue', [...selectedCrops.value])
}
</script>

<style scoped lang="scss">
.crop-card-selector {
  display: grid;
  gap: 16px;
}

.selector-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.eyebrow {
  display: inline-block;
  color: #8d6d55;
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.selector-head h4 {
  margin: 8px 0 6px;
  font-size: 24px;
  color: #364337;
}

.selector-head p {
  margin: 0;
  color: #736453;
  line-height: 1.7;
}

.search-input :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
}

.selected-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.crop-grid {
  margin-bottom: -16px;
}

.crop-card {
  width: 100%;
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid rgba(111, 90, 66, 0.12);
  border-radius: 22px;
  background:
    radial-gradient(circle at top right, rgba(142, 174, 108, 0.2), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(245, 238, 226, 0.96));
  text-align: left;
  cursor: pointer;
  transition: transform 0.24s ease, box-shadow 0.24s ease, border-color 0.24s ease;
}

.crop-card:hover,
.crop-card.active {
  transform: translateY(-2px);
  border-color: rgba(74, 118, 88, 0.42);
  box-shadow: 0 18px 34px rgba(82, 108, 89, 0.16);
}

.crop-card.active {
  background:
    radial-gradient(circle at top right, rgba(101, 146, 96, 0.28), transparent 34%),
    linear-gradient(180deg, rgba(244, 252, 242, 0.96), rgba(236, 245, 230, 0.98));
}

.media-shell {
  height: 176px;
  border-radius: 18px;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: linear-gradient(145deg, #dde5d0, #f5efdf);
}

.media-shell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fallback-media {
  width: 82px;
  height: 82px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 34px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #57845e, #cb8e5d);
}

.card-copy {
  padding: 14px 4px 4px;
}

.card-copy strong {
  display: block;
  margin-bottom: 6px;
  font-size: 18px;
  color: #39433b;
}

.card-copy span {
  display: block;
  color: #85725f;
  font-size: 12px;
  line-height: 1.5;
  word-break: break-all;
}
</style>
