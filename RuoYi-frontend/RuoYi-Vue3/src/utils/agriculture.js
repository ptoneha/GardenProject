const MU_TO_SQM = 666.67
const FEEDBACK_KEY_PREFIX = "agri:task-feedback:"

export const AGRI_MODE_OPTIONS = [
  { label: "农户模式", value: 1, alias: "我是农户" },
  { label: "园艺模式", value: 2, alias: "我是园艺家" }
]

export const AGRI_STATUS_OPTIONS = [
  { label: "未计算", value: "0", tagType: "info" },
  { label: "计算成功", value: "1", tagType: "success" },
  { label: "无可行解", value: "2", tagType: "warning" },
  { label: "计算失败", value: "3", tagType: "danger" },
  { label: "执行中", value: "4", tagType: "" }
]

export const AGRI_SEASON_OPTIONS = [
  { label: "春季", value: "SPRING" },
  { label: "夏季", value: "SUMMER" },
  { label: "秋季", value: "AUTUMN" },
  { label: "冬季", value: "WINTER" }
]

export const AGRI_CONTAINER_TYPE_OPTIONS = [
  { label: "单体盆", value: 1, className: "pot" },
  { label: "长条种植箱", value: 2, className: "trough" },
  { label: "立体种植架", value: 3, className: "rack" },
  { label: "悬挂壁挂架", value: 4, className: "hanging" }
]

export const AGRI_LIGHT_LEVEL_OPTIONS = [
  { label: "弱光", value: 1 },
  { label: "中等光照", value: 2 },
  { label: "强光", value: 3 }
]

const FALLBACK_ERROR_GUIDE = {
  title: "规划未完成",
  description: "当前任务未能生成可执行的种植方案。",
  suggestion: "请检查预算、季节、豆类约束和资源配置后重新尝试。"
}

export const AGRI_ERROR_GUIDE = {
  ERR_BUDGET_TOO_LOW: {
    title: "预算不足",
    description: "当前预算无法覆盖满足约束条件所需的最低成本。",
    suggestion: "提高预算上限，或减少参与地块和作物约束。"
  },
  ERR_PULSE_AREA_TOO_SMALL: {
    title: "豆类面积不足",
    description: "当前地块总面积不足以满足豆类作物的最低种植要求。",
    suggestion: "扩大地块面积，或下调豆类最小比例后重新规划。"
  },
  ERR_NO_PULSE_CROP_AVAILABLE: {
    title: "缺少豆类候选",
    description: "任务要求配置豆类比例，但当前候选作物中没有豆类。",
    suggestion: "将至少一种豆类加入白名单，或放宽作物筛选条件。"
  },
  ERR_SEASON_MISMATCH: {
    title: "季节不匹配",
    description: "当前季节与候选作物的生长窗口不重合。",
    suggestion: "建议将任务调整到适宜季节，或更换耐寒/耐热品种。"
  },
  ERR_NO_CONTAINERS: {
    title: "缺少可用容器",
    description: "园艺模式下未找到可供分配的容器资源。",
    suggestion: "先为所选地块补充种植箱、花盆或立体架。"
  },
  ERR_NO_MATCHING_CONFIG: {
    title: "缺少作物配置",
    description: "当前地块类型没有匹配的作物配置数据。",
    suggestion: "补充作物配置表，或更换适用的地块类型。"
  },
  ERR_NO_GARDEN_CANDIDATES: {
    title: "园艺条件不匹配",
    description: "容器深度、光照或环境条件无法满足候选作物。",
    suggestion: "调整光照条件，或选择根系更浅、耐阴性更强的作物。"
  },
  ERR_CONFLICTING_CROP_RULES: {
    title: "黑白名单冲突",
    description: "同一作物同时出现在白名单和黑名单中。",
    suggestion: "请重新整理作物筛选条件，确保同一作物只保留一种约束。"
  },
  ERR_CONSTRAINT_CONFLICT: {
    title: "约束冲突",
    description: "当前规划条件之间存在冲突，算法无法找到可行解。",
    suggestion: "适当放宽预算、季节或豆类比例等限制。"
  },
  ERR_TASK_RUNNING: {
    title: "任务执行中",
    description: "该任务已经在计算队列中，暂时不能重复触发。",
    suggestion: "请等待当前计算完成后再查看结果。"
  },
  ERR_NO_LANDS: {
    title: "未选择地块",
    description: "当前任务没有关联任何地块或阳台资源。",
    suggestion: "请先在向导第二步选择至少一个地块。"
  },
  ERR_NO_CROPS: {
    title: "作物库为空",
    description: "系统中没有可用于优化的作物基础数据。",
    suggestion: "先补充作物百科，再重新发起任务。"
  },
  ERR_NO_CROP_CONFIGS: {
    title: "配置库为空",
    description: "系统中没有任何作物配置可供算法计算。",
    suggestion: "先维护作物配置库，再重新执行规划。"
  },
  ERR_PYTHON_SCRIPT_MISSING: {
    title: "算法脚本缺失",
    description: "后端未找到可执行的优化脚本。",
    suggestion: "请检查 Python 脚本路径和部署文件是否完整。"
  },
  ERR_PYTHON_BAD_RESPONSE: {
    title: "算法响应异常",
    description: "Python 返回了无法解析的内容。",
    suggestion: "请检查脚本输出是否为标准 JSON。"
  },
  ERR_PYTHON_TIMEOUT: {
    title: "算法执行超时",
    description: "优化任务在限定时间内未返回结果。",
    suggestion: "减少任务规模，或调整后端超时配置。"
  },
  ERR_PYTHON_EXECUTION_FAILED: {
    title: "算法执行失败",
    description: "Python 进程执行过程中发生异常。",
    suggestion: "请检查脚本日志、依赖库和输入数据。"
  },
  ERR_PYTHON_UNAVAILABLE: {
    title: "算法服务不可用",
    description: "当前无法启动 Python 求解进程。",
    suggestion: "请检查 Python 环境和服务可用性。"
  },
  ERR_INVALID_INPUT: {
    title: "任务参数非法",
    description: "任务参数中存在无效值，无法进入求解。",
    suggestion: "请检查预算、豆类比例和作物名单格式。"
  },
  ERR_TASK_FORBIDDEN: {
    title: "无权执行任务",
    description: "当前登录用户不拥有该任务的执行权限。",
    suggestion: "请切换到任务归属账号，或联系管理员授权。"
  },
  ERR_TASK_NOT_FOUND: {
    title: "任务不存在",
    description: "系统中找不到对应的优化任务。",
    suggestion: "请刷新列表后重新选择任务。"
  },
  ERR_INTERNAL: {
    title: "系统内部错误",
    description: "执行过程中发生了未分类异常。",
    suggestion: "请查看服务日志，并稍后重试。"
  }
}

export function getAgriModeMeta(value) {
  return AGRI_MODE_OPTIONS.find((item) => Number(item.value) === Number(value)) || AGRI_MODE_OPTIONS[0]
}

export function getAgriModeLabel(value) {
  return getAgriModeMeta(value).label
}

export function getAgriStatusMeta(value) {
  return AGRI_STATUS_OPTIONS.find((item) => String(item.value) === String(value)) || AGRI_STATUS_OPTIONS[0]
}

export function getAgriSeasonLabel(value) {
  const season = AGRI_SEASON_OPTIONS.find((item) => item.value === value)
  return season ? season.label : "未设置"
}

export function getAgriContainerTypeMeta(value) {
  return AGRI_CONTAINER_TYPE_OPTIONS.find((item) => Number(item.value) === Number(value)) || AGRI_CONTAINER_TYPE_OPTIONS[0]
}

export function getLightLevelLabel(value) {
  const level = AGRI_LIGHT_LEVEL_OPTIONS.find((item) => Number(item.value) === Number(value))
  return level ? level.label : "未知光照"
}

export function parseCropIdList(raw) {
  if (!raw) {
    return []
  }
  if (Array.isArray(raw)) {
    return raw.map((item) => Number(item)).filter((item) => Number.isFinite(item))
  }
  return String(raw)
    .split(",")
    .map((item) => Number(item.trim()))
    .filter((item) => Number.isFinite(item))
}

export function formatCropIdList(ids) {
  return parseCropIdList(ids).join(",")
}

export function resolveLandAreaSqm(land) {
  if (!land) {
    return 0
  }
  if (land.areaSqm != null) {
    return Number(land.areaSqm) || 0
  }
  if (land.inputValue == null) {
    return 0
  }
  return String(land.unit).toLowerCase() === "mu"
    ? Number(land.inputValue) * MU_TO_SQM
    : Number(land.inputValue)
}

export function getCropVisual(crop) {
  if (crop?.imageUrl) {
    return { type: "image", value: crop.imageUrl }
  }
  const category = String(crop?.category || "").toLowerCase()
  if (String(crop?.isPulse).toUpperCase() === "Y") {
    return { type: "emoji", value: "🫘" }
  }
  if (category.includes("leaf") || category.includes("菜")) {
    return { type: "emoji", value: "🥬" }
  }
  if (category.includes("fruit") || category.includes("果")) {
    return { type: "emoji", value: "🍅" }
  }
  if (category.includes("flower") || category.includes("花")) {
    return { type: "emoji", value: "🌼" }
  }
  return { type: "emoji", value: "🌱" }
}

export function getLandThumbnailStyle(land) {
  const light = Number(land?.lightLevel || 1)
  const palettes = {
    1: ["#6b8454", "#bcc49b"],
    2: ["#6a8b63", "#d6c39f"],
    3: ["#b77f36", "#f0d88f"]
  }
  const [primary, secondary] = palettes[light] || palettes[2]
  return {
    background: `linear-gradient(145deg, ${primary}, ${secondary})`
  }
}

export function resolveAgriErrorGuide(reasonCode, taskSeason) {
  const normalizedCode = reasonCode || "ERR_INTERNAL"
  const guide = AGRI_ERROR_GUIDE[normalizedCode] || FALLBACK_ERROR_GUIDE
  if (normalizedCode === "ERR_SEASON_MISMATCH" && taskSeason) {
    return {
      ...guide,
      description: `当前季节（${getAgriSeasonLabel(taskSeason)}）与候选作物的生长窗口不匹配。`
    }
  }
  return guide
}

export function saveTaskFeedback(taskId, payload) {
  if (!taskId || typeof window === "undefined") {
    return
  }
  window.sessionStorage.setItem(`${FEEDBACK_KEY_PREFIX}${taskId}`, JSON.stringify(payload))
}

export function loadTaskFeedback(taskId) {
  if (!taskId || typeof window === "undefined") {
    return null
  }
  const raw = window.sessionStorage.getItem(`${FEEDBACK_KEY_PREFIX}${taskId}`)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw)
  } catch (error) {
    return null
  }
}

export function clearTaskFeedback(taskId) {
  if (!taskId || typeof window === "undefined") {
    return
  }
  window.sessionStorage.removeItem(`${FEEDBACK_KEY_PREFIX}${taskId}`)
}

export function toPercent(value, fractionDigits = 0) {
  return `${(Number(value || 0) * 100).toFixed(fractionDigits)}%`
}

export function toFixedNumber(value, fractionDigits = 2) {
  return Number(value || 0).toFixed(fractionDigits)
}
