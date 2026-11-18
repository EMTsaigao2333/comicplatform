import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // 判断当前用户是否为管理员
  const isAdmin = computed(() => user.value?.role === 'admin')

  // 判断当前用户是否为作者
  const isAuthor = computed(() => user.value?.role === '作者')

  const setUser = (data) => {
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
  }

  const clearUser = () => {
    user.value = null
    localStorage.removeItem('user')
  }

  const isLoggedIn = () => !!user.value

  return { user, setUser, clearUser, isLoggedIn, isAdmin, isAuthor }
})
