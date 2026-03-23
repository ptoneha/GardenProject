import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  const userStore = useUserStore()
  const settingsStore = useSettingsStore()
  const permissionStore = usePermissionStore()

  if (getToken()) {
    to.meta.title && settingsStore.setTitle(to.meta.title)
    if (to.path === '/login') {
      next({ path: userStore.roles.length ? userStore.homePath : '/' })
      NProgress.done()
      return
    }

    if (isWhiteList(to.path)) {
      next()
      return
    }

    if (userStore.roles.length === 0) {
      isRelogin.show = true
      userStore.getInfo().then(() => {
        isRelogin.show = false
        permissionStore.generateRoutes().then((accessRoutes) => {
          accessRoutes.forEach((route) => {
            if (!isHttp(route.path)) {
              router.addRoute(route)
            }
          })
          if (userStore.isPortalRole && (to.path === '/' || to.path === '/index')) {
            next({ path: '/portal/dashboard', replace: true })
            return
          }
          next({ ...to, replace: true })
        })
      }).catch((err) => {
        userStore.logOut().then(() => {
          ElMessage.error(err)
          next({ path: '/' })
        })
      })
      return
    }

    if (userStore.isPortalRole && (to.path === '/' || to.path === '/index')) {
      next({ path: userStore.homePath, replace: true })
      return
    }

    if (userStore.isAdminRole && to.path.startsWith('/portal')) {
      next({ path: '/index', replace: true })
      return
    }

    next()
    return
  }

  if (isWhiteList(to.path)) {
    next()
    return
  }

  next(`/login?redirect=${to.fullPath}`)
  NProgress.done()
})

router.afterEach(() => {
  NProgress.done()
})
