<template>
  <el-dialog
    :model-value="modelValue"
    width="420px"
    top="16vh"
    append-to-body
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="optimize-loading"
  >
    <div class="loading-shell">
      <div class="orbits">
        <span class="orbit orbit-a"></span>
        <span class="orbit orbit-b"></span>
        <span class="core">LP</span>
      </div>
      <h3>{{ title }}</h3>
      <p>{{ subtitle }}</p>
      <div class="scanline"></div>
      <div class="metrics">
        <div>求解器预热</div>
        <div>约束装载</div>
        <div>方案回写</div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '正在进行科学规划'
  },
  subtitle: {
    type: String,
    default: '系统正在调用线性规划引擎，请稍候片刻。'
  }
})
</script>

<style scoped lang="scss">
.optimize-loading {
  :deep(.el-dialog) {
    border-radius: 24px;
    background:
      radial-gradient(circle at top, rgba(84, 122, 109, 0.18), transparent 38%),
      linear-gradient(180deg, #f4f2e8, #e7e0d2 70%);
    overflow: hidden;
  }
}

.loading-shell {
  padding: 18px 8px 10px;
  text-align: center;
}

.orbits {
  position: relative;
  width: 168px;
  height: 168px;
  margin: 0 auto 20px;
}

.orbit {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 1px solid rgba(66, 93, 75, 0.22);
}

.orbit-a {
  animation: spin 5.6s linear infinite;
}

.orbit-b {
  inset: 16px;
  border-style: dashed;
  animation: spin-reverse 4.2s linear infinite;
}

.core {
  position: absolute;
  inset: 40px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #5f7f66, #b98550);
  color: #fffef9;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 0.08em;
  box-shadow: 0 18px 32px rgba(95, 127, 102, 0.24);
}

.loading-shell h3 {
  margin: 0;
  font-size: 28px;
  color: #334336;
}

.loading-shell p {
  margin: 10px auto 18px;
  max-width: 280px;
  line-height: 1.7;
  color: #715a45;
}

.scanline {
  height: 6px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent, #587b62 25%, #c98b55 70%, transparent);
  background-size: 180% 100%;
  animation: pulse 2s ease-in-out infinite;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 18px;
  color: #6f604e;
  font-size: 13px;
}

.metrics div {
  padding: 10px 8px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.58);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes spin-reverse {
  from { transform: rotate(360deg); }
  to { transform: rotate(0deg); }
}

@keyframes pulse {
  0% { background-position: 100% 0; opacity: 0.5; }
  50% { background-position: 0 0; opacity: 1; }
  100% { background-position: -100% 0; opacity: 0.5; }
}
</style>
