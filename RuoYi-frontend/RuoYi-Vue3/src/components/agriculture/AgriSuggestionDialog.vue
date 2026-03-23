<template>
  <el-dialog
    :model-value="modelValue"
    width="520px"
    append-to-body
    destroy-on-close
    class="agri-suggestion-dialog"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <template #header>
      <div class="header">
        <span class="eyebrow">调优建议</span>
        <h3>{{ guide.title }}</h3>
        <p v-if="taskName">{{ taskName }}</p>
      </div>
    </template>

    <div class="dialog-body">
      <div class="signal-card">
        <span class="label">错误标识</span>
        <strong>{{ detail?.code || 'ERR_INTERNAL' }}</strong>
      </div>
      <div class="copy-block">
        <span class="label">问题判断</span>
        <p>{{ guide.description }}</p>
      </div>
      <div class="copy-block emphasis">
        <span class="label">系统建议</span>
        <p>{{ guide.suggestion }}</p>
      </div>
      <div v-if="detail?.message" class="copy-block muted">
        <span class="label">接口消息</span>
        <p>{{ detail.message }}</p>
      </div>
    </div>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">我知道了</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  taskName: {
    type: String,
    default: ''
  },
  detail: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue'])

const guide = computed(() => ({
  title: props.detail?.title || '规划未完成',
  description: props.detail?.description || '当前任务未能生成可执行的种植方案。',
  suggestion: props.detail?.suggestion || '请检查约束条件后重新尝试。'
}))
</script>

<style scoped lang="scss">
.agri-suggestion-dialog {
  :deep(.el-dialog) {
    border-radius: 24px;
    overflow: hidden;
    background:
      radial-gradient(circle at top right, rgba(203, 146, 92, 0.18), transparent 35%),
      linear-gradient(180deg, #fffaf1, #f6eee3);
  }
}

.header h3 {
  margin: 6px 0 4px;
  font-size: 26px;
  color: #3f3025;
}

.header p,
.eyebrow,
.label {
  color: #87664d;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.24em;
  text-transform: uppercase;
}

.dialog-body {
  display: grid;
  gap: 16px;
}

.signal-card,
.copy-block {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(112, 83, 56, 0.12);
}

.signal-card strong {
  display: block;
  margin-top: 8px;
  font-size: 20px;
  color: #3b4435;
}

.copy-block p {
  margin: 8px 0 0;
  line-height: 1.75;
  color: #4d4136;
}

.copy-block.emphasis {
  background: linear-gradient(135deg, rgba(212, 188, 145, 0.35), rgba(255, 255, 255, 0.85));
}

.copy-block.muted p {
  color: #736152;
}
</style>
