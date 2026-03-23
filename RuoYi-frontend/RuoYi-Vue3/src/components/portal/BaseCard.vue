<template>
  <article
    class="base-card"
    :class="[`status-${statusUI.key}`, { 'is-selected': selected, 'is-clickable': clickable }]"
    @click="handleCardClick"
  >
    <header class="base-card__header">
      <slot name="header" :status-ui="statusUI">
        <div class="base-card__header-main">
          <h3 class="base-card__title">{{ title }}</h3>
          <span v-if="tag" class="base-card__tag">{{ tag }}</span>
        </div>
        <span class="base-card__status" :class="`status-${statusUI.key}`">{{ statusUI.label }}</span>
      </slot>
    </header>

    <section class="base-card__body">
      <slot name="body" />
    </section>

    <section v-if="$slots.meta || meta" class="base-card__meta">
      <slot name="meta">{{ meta }}</slot>
    </section>

    <footer class="base-card__actions">
      <slot name="actions" :actions="actions" :emit-action="emitAction">
        <button
          v-for="action in actions"
          :key="action.key || action.label"
          type="button"
          class="base-card__action"
          :class="`type-${action.type || 'secondary'}`"
          @click.stop="emitAction(action)"
        >
          {{ action.label }}
        </button>
      </slot>
    </footer>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { getStatusUI } from '@/utils/cardSystem'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  tag: {
    type: String,
    default: ''
  },
  status: {
    type: String,
    default: 'idle'
  },
  meta: {
    type: String,
    default: ''
  },
  actions: {
    type: Array,
    default: () => []
  },
  selected: {
    type: Boolean,
    default: false
  },
  clickable: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['action', 'card-click'])

const statusUI = computed(() => getStatusUI(props.status))

function emitAction(action) {
  emit('action', action)
}

function handleCardClick() {
  if (props.clickable) {
    emit('card-click')
  }
}
</script>

<style scoped lang="scss">
.base-card {
  min-height: 296px;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 18px;
  border-radius: 18px;
  border: 1px solid #e3eae6;
  background: #fff;
  overflow: hidden;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.base-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 28px rgba(47, 58, 52, 0.08);
}

.base-card.is-selected,
.base-card.status-active {
  border-color: rgba(95, 175, 122, 0.55);
}

.base-card.is-clickable {
  cursor: pointer;
}

.base-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.base-card__header-main {
  min-width: 0;
}

.base-card__title {
  margin: 0;
  color: #2f3a34;
  font-size: 20px;
  line-height: 1.35;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.base-card__tag,
.base-card__status {
  display: inline-flex;
  align-items: center;
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  white-space: nowrap;
}

.base-card__tag {
  margin-top: 10px;
  background: rgba(95, 175, 122, 0.12);
  color: #5faf7a;
}

.base-card__status {
  flex-shrink: 0;
}

.base-card__status.status-active {
  background: rgba(95, 175, 122, 0.12);
  color: #4c9e68;
}

.base-card__status.status-idle {
  background: rgba(226, 184, 76, 0.14);
  color: #ad7c12;
}

.base-card__status.status-unbound {
  background: rgba(126, 139, 132, 0.12);
  color: #6d7b74;
}

.base-card__status.status-disabled {
  background: rgba(160, 169, 163, 0.14);
  color: #7a8780;
}

.base-card__body {
  flex: 1;
  display: grid;
  gap: 10px;
  margin-top: 16px;
  overflow: hidden;
}

.base-card__meta {
  margin-top: 12px;
  color: #8f9b95;
  font-size: 13px;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.base-card__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid rgba(227, 234, 230, 0.9);
}

.base-card__action {
  min-width: 88px;
  height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #d7e0db;
  background: #fff;
  font-size: 13px;
  line-height: 1;
  cursor: pointer;
  transition: 0.18s ease;
}

.base-card__action.type-primary {
  border-color: transparent;
  background: transparent;
  color: #4c9e68;
  font-weight: 600;
}

.base-card__action.type-primary:hover,
.base-card__action.type-primary:focus {
  color: #3f8b5a;
}

.base-card__action.type-secondary:hover,
.base-card__action.type-secondary:focus {
  border-color: #c9d8cf;
  background: #f7faf8;
  color: #5b6b63;
}

.base-card__action.type-danger {
  color: #7a8780;
}

.base-card__action.type-danger:hover,
.base-card__action.type-danger:focus {
  border-color: #d7b8b8;
  background: #fffafa;
  color: #c35f5f;
}
</style>
