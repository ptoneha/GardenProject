<template>
  <div class="portal-shell">
    <aside class="portal-rail">
      <button type="button" class="rail-user-card" @click="openProfileDrawer">
        <el-avatar :src="avatarDisplay" :size="46">{{ userInitial }}</el-avatar>
        <div class="rail-user-copy">
          <strong class="rail-user-name">{{ displayUserName }}</strong>
        </div>
        <el-icon class="rail-user-entry"><ArrowRight /></el-icon>
      </button>

      <nav class="portal-nav">
        <button
          v-for="item in navItems"
          :key="item.path"
          type="button"
          class="nav-item"
          :class="{ active: route.path === item.path }"
          @click="router.push(item.path)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </button>
      </nav>

      <div class="rail-footer">
        <button v-if="userStore.isAdminRole" type="button" class="rail-footer-link" @click="router.push('/index')">
          进入后台
        </button>
        <div class="season-context rail-footer-season">
          当前规划季：{{ currentSeasonLabel }} {{ currentYear }}
        </div>
        <button type="button" class="rail-footer-link logout-action" @click="handleLogout">退出登录</button>
      </div>
    </aside>

    <div class="portal-stage">
      <main class="portal-main">
        <router-view />
      </main>
    </div>

    <el-drawer
      v-model="profileDrawerVisible"
      title="个人资料"
      size="420px"
      append-to-body
      class="portal-profile-drawer"
      :before-close="handleProfileDrawerBeforeClose"
    >
      <div class="profile-panel">
        <section class="profile-section profile-basic">
          <div class="profile-avatar-wrap">
            <div class="profile-avatar-stack" @click="openAvatarEditor">
              <el-avatar :src="avatarDisplay" :size="72">{{ userInitial }}</el-avatar>
              <button type="button" class="profile-avatar-button" :disabled="avatarUploading" @click.stop="openAvatarEditor">
                修改头像
              </button>
            </div>
            <div class="profile-avatar-actions">
              <strong>基础资料</strong>
              <span>点击进入头像编辑与预览</span>
            </div>
          </div>

          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-position="top" status-icon>
            <el-form-item label="用户名" prop="nickName">
              <el-input
                v-model="profileForm.nickName"
                maxlength="20"
                show-word-limit
                placeholder="请输入 2-20 位昵称，支持中文、字母、数字"
              />
            </el-form-item>
          </el-form>
        </section>

        <section class="profile-section">
          <div class="profile-section-head">
            <strong>账户设置</strong>
            <el-button class="profile-password-button" @click="openPasswordDialog">修改密码</el-button>
          </div>
          <p class="profile-section-note">你可以在这里更新登录密码，提升账户安全性。</p>
        </section>

        <section class="profile-section">
          <div class="profile-section-head">
            <strong>偏好信息</strong>
          </div>
          <el-form label-position="top">
            <el-form-item label="默认模式">
              <p class="profile-field-note">影响推荐策略</p>
              <el-select v-model="profileForm.defaultMode" placeholder="请选择">
                <el-option label="农户" value="farm" />
                <el-option label="园艺" value="garden" />
              </el-select>
            </el-form-item>
            <el-form-item label="默认面积单位">
              <p class="profile-field-note">系统会自动换算</p>
              <el-select v-model="profileForm.defaultAreaUnit" placeholder="请选择">
                <el-option label="亩" value="mu" />
                <el-option label="平方米" value="sqm" />
              </el-select>
            </el-form-item>
            <el-form-item label="当前规划季">
              <div class="profile-static-text">当前规划季：{{ currentSeasonLabel }} {{ currentYear }}</div>
            </el-form-item>
          </el-form>
        </section>

        <div class="profile-actions">
          <el-button class="profile-cancel-button" @click="requestCloseProfileDrawer">取消</el-button>
          <el-button class="profile-save-button" type="primary" :disabled="!hasProfileChanges || profileSubmitting" @click="applyProfileDraft">
            {{ profileSubmitting ? '保存中...' : '保存更改' }}
          </el-button>
        </div>
      </div>
    </el-drawer>

    <el-dialog
      v-model="avatarEditorVisible"
      title="更换头像"
      width="760px"
      append-to-body
      class="portal-avatar-dialog"
      @close="closeAvatarEditor"
    >
      <div class="avatar-editor">
        <div class="avatar-editor-main">
          <div class="avatar-crop-area">
            <div v-if="avatarEditorImage" class="avatar-cropper-shell">
              <vue-cropper
                ref="avatarCropperRef"
                :img="avatarEditorImage"
                :info="true"
                :autoCrop="true"
                :autoCropWidth="240"
                :autoCropHeight="240"
                :fixedBox="true"
                outputType="png"
                @realTime="handleAvatarRealtime"
              />
            </div>
            <div v-else class="avatar-upload-empty">
              <strong>上传头像图片</strong>
              <p>支持 JPG / PNG，文件不超过 2MB</p>
            </div>

            <div class="avatar-editor-toolbar">
              <button type="button" class="profile-cancel-button avatar-tool-button" @click="triggerAvatarFileSelect">选择图片</button>
              <button type="button" class="profile-cancel-button avatar-tool-button" :disabled="!avatarEditorImage" @click="scaleAvatarCrop(1)">放大</button>
              <button type="button" class="profile-cancel-button avatar-tool-button" :disabled="!avatarEditorImage" @click="scaleAvatarCrop(-1)">缩小</button>
            </div>
          </div>

          <div class="avatar-preview-panel">
            <span class="avatar-preview-label">头像预览</span>
            <div class="avatar-preview-circle">
              <img v-if="avatarPreviewData" :src="avatarPreviewData" alt="头像预览" class="avatar-preview-image">
              <el-avatar v-else :src="avatarDisplay" :size="140">{{ userInitial }}</el-avatar>
            </div>
          </div>
        </div>

        <div class="profile-actions avatar-editor-actions">
          <el-button class="profile-cancel-button" @click="closeAvatarEditor">取消</el-button>
          <el-button class="profile-save-button" type="primary" :disabled="!avatarEditorImage || avatarUploading" @click="confirmAvatarCrop">
            {{ avatarUploading ? '上传中...' : '确认使用' }}
          </el-button>
        </div>
      </div>
      <input ref="avatarInputRef" type="file" accept="image/png,image/jpeg" class="avatar-input-hidden" @change="handleAvatarFileSelect">
    </el-dialog>

    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="420px"
      append-to-body
      class="portal-password-dialog"
    >
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top" status-icon>
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入 6-20 位新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="profile-actions password-actions">
          <el-button class="profile-cancel-button" @click="passwordDialogVisible = false">取消</el-button>
          <el-button class="profile-save-button" type="primary" :disabled="passwordSubmitting" @click="submitPasswordChange">
            {{ passwordSubmitting ? '提交中...' : '确认修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight, Box, DataLine, House, Location, Operation } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, updateUserPwd, uploadAvatar } from '@/api/system/user'
import "vue-cropper/dist/index.css"
import { VueCropper } from 'vue-cropper'
import useUserStore from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const profileDrawerVisible = ref(false)
const passwordDialogVisible = ref(false)
const avatarEditorVisible = ref(false)
const avatarInputRef = ref(null)
const avatarCropperRef = ref(null)
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const avatarPreview = ref('')
const avatarUploading = ref(false)
const profileSubmitting = ref(false)
const passwordSubmitting = ref(false)
const profileRaw = ref({})
const profileBaseline = ref('')
const avatarEditorImage = ref('')
const avatarPreviewData = ref('')
const avatarObjectUrl = ref('')
const profileForm = reactive({
  userName: userStore.name || '',
  nickName: userStore.nickName || userStore.name || 'Garden User',
  defaultMode: 'garden',
  defaultAreaUnit: 'sqm'
})
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const navItems = [
  { path: '/portal/dashboard', label: '门户首页', icon: House },
  { path: '/portal/tasks', label: '种植计划', icon: Operation },
  { path: '/portal/results', label: '结果画廊', icon: DataLine },
  { path: '/portal/lands', label: '我的地块', icon: Location },
  { path: '/portal/containers', label: '我的容器', icon: Box }
]

const currentYear = new Date().getFullYear()
const currentSeasonLabel = computed(() => {
  const month = new Date().getMonth() + 1
  if (month >= 3 && month <= 5) {
    return 'Spring'
  }
  if (month >= 6 && month <= 8) {
    return 'Summer'
  }
  if (month >= 9 && month <= 11) {
    return 'Autumn'
  }
  return 'Winter'
})
const prefsStorageKey = computed(() => `portal-profile-prefs:${userStore.id || 'default'}`)
const avatarDisplay = computed(() => avatarPreview.value || userStore.avatar)
const displayUserName = computed(() => userStore.nickName || userStore.name || 'Garden User')
const userInitial = computed(() => displayUserName.value.slice(0, 1).toUpperCase())
const hasProfileChanges = computed(() => profileBaseline.value !== buildProfileSnapshot())

const profileRules = {
  nickName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度需在 2 到 20 个字符之间', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5A-Za-z0-9_-]+$/, message: '仅支持中文、字母、数字、下划线和短横线', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度需在 6 到 20 个字符之间', trigger: 'blur' },
    { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > \" \' \\ |', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

function loadProfilePrefs() {
  try {
    const raw = localStorage.getItem(prefsStorageKey.value)
    if (!raw) {
      return
    }
    const parsed = JSON.parse(raw)
    profileForm.defaultMode = parsed.defaultMode || 'garden'
    profileForm.defaultAreaUnit = parsed.defaultAreaUnit || 'sqm'
  } catch {
    profileForm.defaultMode = 'garden'
    profileForm.defaultAreaUnit = 'sqm'
  }
}

function saveProfilePrefs() {
  localStorage.setItem(
    prefsStorageKey.value,
    JSON.stringify({
      defaultMode: profileForm.defaultMode,
      defaultAreaUnit: profileForm.defaultAreaUnit
    })
  )
}

function buildProfileSnapshot() {
  return JSON.stringify({
    nickName: profileForm.nickName,
    defaultMode: profileForm.defaultMode,
    defaultAreaUnit: profileForm.defaultAreaUnit
  })
}

function buildAvatarUrl(path, bustCache = false) {
  if (!path) {
    return userStore.avatar
  }
  let url = path
  if (/^https?:\/\//.test(path)) {
    url = path
  } else {
    url = `${import.meta.env.VITE_APP_BASE_API}${path}`
  }
  if (!bustCache) {
    return url
  }
  return `${url}${url.includes('?') ? '&' : '?'}t=${Date.now()}`
}

async function syncProfileForm() {
  try {
    const response = await getUserProfile()
    profileRaw.value = response.data || {}
    profileForm.userName = profileRaw.value.userName || userStore.name || ''
    profileForm.nickName = profileRaw.value.nickName || userStore.nickName || userStore.name || 'Garden User'
  } catch {
    profileRaw.value = {}
    profileForm.userName = userStore.name || ''
    profileForm.nickName = userStore.nickName || userStore.name || 'Garden User'
  }
  loadProfilePrefs()
  profileBaseline.value = buildProfileSnapshot()
}

async function openProfileDrawer() {
  await syncProfileForm()
  profileDrawerVisible.value = true
  await nextTick()
  profileFormRef.value?.clearValidate()
}

async function confirmDiscardIfNeeded() {
  if (!hasProfileChanges.value) {
    return true
  }
  try {
    await ElMessageBox.confirm('还有未保存的修改，确认离开吗？', '未保存更改', {
      confirmButtonText: '离开',
      cancelButtonText: '继续编辑',
      type: 'warning'
    })
    return true
  } catch {
    return false
  }
}

async function requestCloseProfileDrawer() {
  const canClose = await confirmDiscardIfNeeded()
  if (!canClose) {
    return
  }
  profileDrawerVisible.value = false
}

async function handleProfileDrawerBeforeClose(done) {
  const canClose = await confirmDiscardIfNeeded()
  if (!canClose) {
    return
  }
  done()
}

function resetAvatarPreview() {
  avatarPreviewData.value = ''
}

function revokeAvatarObjectUrl() {
  if (!avatarObjectUrl.value) {
    return
  }
  URL.revokeObjectURL(avatarObjectUrl.value)
  avatarObjectUrl.value = ''
}

function openAvatarEditor() {
  avatarEditorVisible.value = true
  avatarEditorImage.value = avatarDisplay.value || ''
  resetAvatarPreview()
}

function closeAvatarEditor() {
  avatarEditorVisible.value = false
  avatarEditorImage.value = ''
  resetAvatarPreview()
  revokeAvatarObjectUrl()
}

function triggerAvatarFileSelect() {
  avatarInputRef.value?.click()
}

function refreshAvatarPreview() {
  if (!avatarCropperRef.value || !avatarEditorImage.value) {
    return
  }
  avatarCropperRef.value.getCropData((data) => {
    avatarPreviewData.value = data
  })
}

function handleAvatarRealtime() {
  refreshAvatarPreview()
}

function scaleAvatarCrop(step) {
  avatarCropperRef.value?.changeScale(step)
}

function handleAvatarFileSelect(event) {
  const file = event.target.files?.[0]
  if (!file) {
    return
  }
  const isSupported = ['image/jpeg', 'image/png'].includes(file.type)
  if (!isSupported) {
    ElMessage.error('仅支持上传 JPG 或 PNG 图片')
    event.target.value = ''
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB')
    event.target.value = ''
    return
  }
  revokeAvatarObjectUrl()
  avatarObjectUrl.value = URL.createObjectURL(file)
  avatarEditorImage.value = avatarObjectUrl.value
  avatarEditorVisible.value = true
  resetAvatarPreview()
  nextTick(() => {
    refreshAvatarPreview()
  })
  event.target.value = ''
}

async function confirmAvatarCrop() {
  if (!avatarCropperRef.value) {
    return
  }
  avatarUploading.value = true
  try {
    const blob = await new Promise((resolve) => {
      avatarCropperRef.value.getCropBlob((data) => resolve(data))
    })
    const formData = new FormData()
    formData.append('avatarfile', blob, 'avatar.png')
    const response = await uploadAvatar(formData)
    const nextAvatar = buildAvatarUrl(response.imgUrl, true)
    avatarPreview.value = nextAvatar
    userStore.avatar = nextAvatar
    ElMessage.success('头像已更新')
    closeAvatarEditor()
  } catch {
    ElMessage.error('头像上传失败，请稍后重试')
  } finally {
    avatarUploading.value = false
  }
}

function openPasswordDialog() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
  nextTick(() => {
    passwordFormRef.value?.clearValidate()
  })
}

async function submitPasswordChange() {
  if (!passwordFormRef.value) {
    return
  }
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }
  passwordSubmitting.value = true
  try {
    await updateUserPwd(passwordForm.oldPassword, passwordForm.newPassword)
    passwordDialogVisible.value = false
    ElMessage.success('密码修改成功')
  } catch {
    ElMessage.error('密码修改失败，请稍后重试')
  } finally {
    passwordSubmitting.value = false
  }
}

async function applyProfileDraft() {
  if (!profileFormRef.value) {
    return
  }
  try {
    await profileFormRef.value.validate()
  } catch {
    return
  }
  if (!hasProfileChanges.value) {
    return
  }
  profileSubmitting.value = true
  try {
    await updateUserProfile({
      nickName: profileForm.nickName,
      phonenumber: profileRaw.value.phonenumber || '',
      email: profileRaw.value.email || '',
      sex: profileRaw.value.sex ?? '0'
    })
    userStore.nickName = profileForm.nickName
    saveProfilePrefs()
    profileBaseline.value = buildProfileSnapshot()
    ElMessage.success('个人资料已更新')
  } catch {
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    profileSubmitting.value = false
  }
}

function handleLogout() {
  ElMessageBox.confirm('确认退出当前账号？', '退出登录', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'portal-logout-message-box',
    confirmButtonClass: 'portal-logout-confirm',
    cancelButtonClass: 'portal-logout-cancel',
    closeOnClickModal: false,
    closeOnPressEscape: false
  }).then(() => {
    userStore.logOut().then(() => {
      router.push('/login')
    })
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
.portal-shell {
  --portal-title-font: "Cormorant Garamond", "Times New Roman", Georgia, serif;
  height: 100vh;
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(211, 173, 117, 0.22), transparent 26%),
    linear-gradient(180deg, #f6f2ea 0%, #f2ecdf 100%);
}

.portal-shell :deep(.el-card) {
  border-radius: 24px;
  border: 1px solid rgba(110, 91, 68, 0.08);
  box-shadow: 0 20px 42px rgba(98, 82, 60, 0.08);
}

.portal-shell :deep(.el-card__header) {
  padding: 24px 28px 10px;
}

.portal-shell :deep(.el-card__body) {
  padding: 18px 28px 28px;
}

.portal-shell :deep(h1),
.portal-shell :deep(h2),
.portal-shell :deep(h3),
.portal-shell :deep(h4),
.portal-shell :deep(.el-dialog__title) {
  font-family: var(--portal-title-font);
  letter-spacing: 0.02em;
}

.portal-rail {
  display: flex;
  flex-direction: column;
  gap: 14px;
  height: 100vh;
  padding: 28px 18px 20px;
  overflow-y: auto;
  background: rgba(247, 244, 237, 0.82);
  border-right: 1px solid rgba(103, 85, 61, 0.1);
  backdrop-filter: blur(18px);
}

.rail-user-role,
.season-context {
  color: #8a7257;
  font-size: 13px;
}

.rail-user-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 14px 14px 12px;
  border: 1px solid rgba(111, 143, 101, 0.12);
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.7), rgba(239, 247, 235, 0.78));
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.season-context {
  padding: 0 4px 8px;
  line-height: 1.5;
}

.rail-footer-season {
  margin-top: 10px;
  margin-bottom: 8px;
}

.rail-user-card:hover {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.82), rgba(231, 243, 232, 0.9));
  border-color: rgba(111, 143, 101, 0.2);
  transform: translateY(-1px);
}

.rail-user-copy {
  min-width: 0;
}

.rail-user-name {
  display: block;
  color: #2e4637;
  font-weight: 600;
  margin-bottom: 3px;
}

.rail-user-entry {
  color: #8ea092;
  font-size: 16px;
}

.portal-nav {
  display: grid;
  gap: 6px;
  padding-top: 6px;
}

.nav-item,
.rail-footer-link {
  border: 0;
  cursor: pointer;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  min-height: 42px;
  padding: 10px 14px;
  border-radius: 14px;
  color: #53604d;
  background: transparent;
  transition: background 0.18s ease, color 0.18s ease;
}

.nav-item:hover,
.nav-item.active {
  color: #1f3f31;
  background: rgba(223, 240, 225, 0.92);
}

.rail-footer {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding-top: 8px;
}

.rail-footer-link {
  padding: 6px 4px;
  background: transparent;
  color: #8a7257;
  font-size: 13px;
  line-height: 1.5;
  transition: color 0.18s ease, opacity 0.18s ease;
}

.rail-footer-link:hover {
  color: #2f5f4a;
}

.logout-action {
  width: 100%;
  padding: 12px 14px;
  border-radius: 16px;
  color: #8d593f;
  background: rgba(201, 139, 68, 0.16);
}

.profile-panel {
  display: grid;
  gap: 22px;
}

.profile-section {
  padding: 20px 20px 10px;
  border-radius: 18px;
  background: rgba(247, 244, 237, 0.62);
  border: 1px solid rgba(110, 91, 68, 0.05);
}

.profile-basic {
  padding-top: 16px;
}

.profile-avatar-stack {
  display: grid;
  justify-items: center;
  gap: 10px;
  cursor: pointer;
}

.profile-avatar-wrap,
.profile-section-head,
.profile-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.profile-avatar-wrap {
  margin-bottom: 14px;
}

.profile-avatar-actions strong,
.profile-section-head strong {
  color: #2e4637;
}

.profile-avatar-actions {
  flex: 1;
  display: grid;
  gap: 4px;
}

.profile-avatar-actions span {
  color: #8a7257;
  font-size: 13px;
  line-height: 1.5;
}

.profile-section-note {
  margin: 8px 0 0;
  color: #8a7257;
  font-size: 13px;
  line-height: 1.6;
}

.profile-actions {
  justify-content: space-between;
  gap: 12px;
}

.profile-cancel-button,
.profile-save-button {
  min-width: 112px;
  height: 42px;
  border: none;
  border-radius: 999px;
  padding: 0 20px;
  font-size: 14px;
}

.profile-cancel-button {
  background: #eef1ee;
  color: #4d5f56;
}

.profile-save-button {
  background: #4c9e68;
  color: #fff;
  box-shadow: 0 10px 22px rgba(76, 158, 104, 0.22);
}

.profile-cancel-button:hover,
.profile-cancel-button:focus {
  background: #e5ebe7;
  color: #32443b;
}

.profile-cancel-button.is-disabled,
.profile-save-button.is-disabled {
  opacity: 0.6;
}

.profile-save-button:hover,
.profile-save-button:focus {
  background: #438f5d;
}

.profile-password-button,
.profile-avatar-button {
  height: 34px;
  padding: 0 14px;
  border: none;
  border-radius: 999px;
  background: #eef1ee;
  color: #4d5f56;
}

.profile-avatar-button {
  min-width: 92px;
  cursor: pointer;
}

.profile-password-button:hover,
.profile-password-button:focus,
.profile-avatar-button:hover,
.profile-avatar-button:focus {
  background: #e5ebe7;
  color: #32443b;
}

.profile-field-note,
.profile-static-text {
  margin: 0 0 8px;
  color: #8a7257;
  font-size: 13px;
  line-height: 1.5;
}

.avatar-input-hidden {
  display: none;
}

.avatar-editor {
  display: grid;
  gap: 18px;
}

.avatar-editor-main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 220px;
  gap: 20px;
  align-items: start;
}

.avatar-crop-area,
.avatar-preview-panel {
  padding: 18px;
  border-radius: 18px;
  background: rgba(247, 244, 237, 0.62);
  border: 1px solid rgba(110, 91, 68, 0.05);
}

.avatar-cropper-shell {
  height: 360px;
  overflow: hidden;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.62);
}

.avatar-upload-empty {
  display: grid;
  place-items: center;
  height: 360px;
  border-radius: 16px;
  border: 1px dashed rgba(141, 115, 93, 0.22);
  color: #8a7257;
  text-align: center;
}

.avatar-upload-empty strong {
  color: #2e4637;
  margin-bottom: 6px;
}

.avatar-upload-empty p {
  margin: 0;
}

.avatar-editor-toolbar {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.avatar-tool-button {
  min-width: 88px;
}

.avatar-preview-panel {
  display: grid;
  justify-items: center;
  gap: 16px;
}

.avatar-preview-label {
  color: #8a7257;
  font-size: 13px;
}

.avatar-preview-circle {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 0 0 1px rgba(110, 91, 68, 0.06);
}

.avatar-preview-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-editor-actions {
  justify-content: flex-end;
}

:deep(.portal-profile-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding-bottom: 8px;
}

:deep(.portal-profile-drawer .el-drawer__body) {
  padding-top: 8px;
}

:deep(.portal-profile-drawer .el-form-item) {
  margin-bottom: 18px;
}

:deep(.portal-avatar-dialog .el-dialog__body) {
  padding-top: 10px;
}

:deep(.portal-password-dialog .el-dialog__body) {
  padding-top: 10px;
}

.password-actions {
  width: 100%;
}

.portal-stage {
  min-width: 0;
  min-height: 0;
  height: 100vh;
  overflow: hidden;
}

.portal-main {
  min-width: 0;
  min-height: 0;
  height: 100%;
  padding: 32px 36px 40px;
  overflow-y: auto;
  overflow-x: hidden;
  overscroll-behavior: contain;
  scrollbar-gutter: stable;
}

@media (max-width: 1080px) {
  .portal-shell {
    grid-template-columns: 1fr;
    grid-template-rows: auto minmax(0, 1fr);
  }

  .portal-rail {
    gap: 12px;
    height: auto;
    max-height: 260px;
    padding-bottom: 18px;
    overflow-y: auto;
    border-right: 0;
    border-bottom: 1px solid rgba(103, 85, 61, 0.1);
  }

  .portal-stage {
    height: auto;
  }

  .portal-nav {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .portal-nav {
    grid-template-columns: 1fr;
  }

  .avatar-editor-main {
    grid-template-columns: 1fr;
  }

  .portal-main {
    padding: 12px 12px 24px;
  }
}
</style>

<style lang="scss">
.portal-logout-message-box {
  width: min(92vw, 520px);
  padding: 10px 10px 6px;
  border: 1px solid rgba(110, 91, 68, 0.08);
  border-radius: 24px;
  box-shadow: 0 24px 48px rgba(86, 68, 49, 0.14);
  background: linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(247, 242, 234, 0.98));
}

.portal-logout-message-box .el-message-box__header {
  padding: 8px 14px 2px;
}

.portal-logout-message-box .el-message-box__title {
  color: #2f4334;
  font-family: "Cormorant Garamond", "Times New Roman", Georgia, serif;
  font-size: 24px;
  letter-spacing: 0.02em;
}

.portal-logout-message-box .el-message-box__headerbtn {
  top: 18px;
  right: 18px;
}

.portal-logout-message-box .el-message-box__content {
  padding: 16px 18px 10px;
}

.portal-logout-message-box .el-message-box__container {
  align-items: center;
  gap: 12px;
}

.portal-logout-message-box .el-message-box__status {
  position: static;
  margin-top: 0;
  color: #d8a03f !important;
  font-size: 28px !important;
}

.portal-logout-message-box .el-message-box__message p {
  color: #685c4f;
  font-size: 16px;
  line-height: 1.7;
}

.portal-logout-message-box .el-message-box__btns {
  padding: 10px 18px 16px;
}

.portal-logout-message-box .el-message-box__btns button {
  min-width: 92px;
  height: 40px;
  border: none;
  border-radius: 999px;
  font-size: 14px;
}

.portal-logout-message-box .portal-logout-cancel {
  background: #eef1ee;
  color: #4d5f56;
}

.portal-logout-message-box .portal-logout-cancel:hover,
.portal-logout-message-box .portal-logout-cancel:focus {
  background: #e5ebe7;
  color: #32443b;
}

.portal-logout-message-box .portal-logout-confirm {
  background: #4c9e68;
  color: #fff;
  box-shadow: 0 10px 22px rgba(76, 158, 104, 0.22);
}

.portal-logout-message-box .portal-logout-confirm:hover,
.portal-logout-message-box .portal-logout-confirm:focus {
  background: #438f5d;
}
 
</style>
