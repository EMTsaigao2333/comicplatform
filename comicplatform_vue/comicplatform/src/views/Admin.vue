<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'
import { getCoverUrl } from '@/utils/image.js'

const router = useRouter()
const userStore = useUserStore()

// 非管理员访问时给出提示，但当前不做强制跳转，方便演示
const isAdmin = computed(() => userStore.isAdmin)

const keyword = ref('')
const loading = ref(false)
const comics = ref([])
const message = ref('')
const messageType = ref('')

// 预留接口：搜索漫画（可按标题/作者/关键词搜索）
const searchComics = async () => {
  loading.value = true
  message.value = ''
  try {
    const res = await axios.get('/api/comic/page', {
      params: {
        keyword: keyword.value.trim(),
        pageNum: 1,
        pageSize: 100,
      },
    })
    const list = res.data.data?.records || res.data.data || []
    comics.value = list.map(item => ({
      comicid: item.comicId ?? item.comicid,
      comictitle: item.comicTitle ?? item.comictitle ?? '未命名',
      author: item.author ?? '',
      category: item.category ?? '',
      status: item.status ?? '已发布',
      isviolation: item.isViolation ?? item.isviolation ?? 0,
      coverimg: item.coverImg ?? item.coverimg ?? getCoverUrl(),
    }))
  } catch (err) {
    console.error('搜索漫画失败', err)
    useMockData()
  } finally {
    loading.value = false
  }
}

// 假数据：接口未通时用于演示
const useMockData = () => {
  comics.value = [
    { comicid: 1, comictitle: '航海王', author: '尾田荣一郎', category: '冒险', status: '已发布', isviolation: 0, coverimg: getCoverUrl() },
    { comicid: 2, comictitle: '违规测试漫画', author: '某作者', category: '其他', status: '已发布', isviolation: 1, coverimg: getCoverUrl() },
    { comicid: 3, comictitle: '龙珠', author: '鸟山明', category: '热血', status: '已下架', isviolation: 0, coverimg: getCoverUrl() },
  ].filter(item => !keyword.value || item.comictitle.includes(keyword.value) || item.author.includes(keyword.value))
}

// 预留接口：标记/取消标记违规漫画
const toggleViolation = async (comic) => {
  const newValue = comic.isviolation ? 0 : 1
  try {
    await axios.put(`/api/comic/${comic.comicid}/violation`, { isViolation: newValue })
    comic.isviolation = newValue
    showMessage(newValue ? '已标记为违规漫画' : '已取消违规标记', 'success')
  } catch (err) {
    console.error('标记违规失败', err)
    // 接口未通时模拟成功
    comic.isviolation = newValue
    showMessage((newValue ? '已标记为违规漫画' : '已取消违规标记') + '（模拟）', 'success')
  }
}

// 预留接口：下架/重新上架漫画
const toggleStatus = async (comic) => {
  const newStatus = comic.status === '已下架' ? '已发布' : '已下架'
  try {
    await axios.put(`/api/comic/${comic.comicid}/status`, { status: newStatus })
    comic.status = newStatus
    showMessage(newStatus === '已下架' ? '已下架该漫画' : '已重新上架', 'success')
  } catch (err) {
    console.error('下架操作失败', err)
    // 接口未通时模拟成功
    comic.status = newStatus
    showMessage((newStatus === '已下架' ? '已下架该漫画' : '已重新上架') + '（模拟）', 'success')
  }
}

const showMessage = (text, type) => {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2500)
}

onMounted(() => {
  searchComics()
})
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <h2 class="page-title">管理员后台</h2>
      <p class="page-desc">搜索并标记违规漫画，或执行下架操作</p>
    </div>

    <!-- 其他管理模块入口 -->
    <div class="manage-nav">
      <router-link to="/admin/category" class="manage-entry">
        <span class="entry-title">分类管理</span>
        <span class="entry-desc">维护漫画题材分类</span>
      </router-link>
      <router-link to="/admin/notice" class="manage-entry">
        <span class="entry-title">公告管理</span>
        <span class="entry-desc">维护首页公告内容</span>
      </router-link>
      <router-link to="/admin/comment" class="manage-entry">
        <span class="entry-title">评论管理</span>
        <span class="entry-desc">审核屏蔽违规评论</span>
      </router-link>
    </div>

    <div v-if="!isAdmin" class="form-message warning">
      当前账号不是管理员，以下功能仅用于界面演示。
    </div>

    <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

    <!-- 搜索区 -->
    <div class="search-section">
      <div class="admin-search-box">
        <input
          v-model="keyword"
          type="text"
          placeholder="输入漫画标题或作者进行搜索"
          @keyup.enter="searchComics"
        >
        <button class="btn btn-primary" :disabled="loading" @click="searchComics">
          {{ loading ? '搜索中...' : '搜索' }}
        </button>
      </div>
    </div>

    <!-- 漫画列表 -->
    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="comics.length === 0" class="empty-state">未找到匹配的漫画</div>
    <div v-else class="comic-grid">
      <div
        v-for="comic in comics"
        :key="comic.comicid"
        class="comic-card"
        :class="{ violation: comic.isviolation, offline: comic.status === '已下架' }"
      >
        <div class="cover-wrap">
          <img :src="comic.coverimg" :alt="comic.comictitle">
          <span v-if="comic.isviolation" class="badge badge-danger">违规</span>
          <span v-if="comic.status === '已下架'" class="badge badge-muted">已下架</span>
        </div>
        <div class="comic-info">
          <h3 class="comic-title" :title="comic.comictitle">{{ comic.comictitle }}</h3>
          <p class="comic-meta">{{ comic.author }} · {{ comic.category }}</p>
          <div class="comic-actions">
            <button
              class="action-btn"
              :class="{ danger: !comic.isviolation }"
              @click="toggleViolation(comic)"
            >
              {{ comic.isviolation ? '取消违规' : '标记违规' }}
            </button>
            <button
              class="action-btn"
              :class="{ warning: comic.status !== '已下架' }"
              @click="toggleStatus(comic)"
            >
              {{ comic.status === '已下架' ? '重新上架' : '下架' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  padding-bottom: 40px;
}

.page-header {
  margin-bottom: 24px;
}

/* 其他管理模块入口 */
.manage-nav {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.manage-entry {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px 20px;
  background: var(--bg-card);
  border-radius: var(--radius);
  border-left: 3px solid var(--primary);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.manage-entry:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.entry-title {
  font-size: 16px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
}

.entry-desc {
  font-size: 13px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .manage-nav {
    grid-template-columns: 1fr;
  }
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
  margin-bottom: 6px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-muted);
}

.form-message {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
}

.form-message.success {
  background: #dcfce7;
  color: #166534;
}

.form-message.error {
  background: #fee2e2;
  color: #991b1b;
}

.form-message.warning {
  background: #fef3c7;
  color: #92400e;
}

/* 搜索区 */
.search-section {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
}

.admin-search-box {
  display: flex;
  gap: 12px;
  max-width: 600px;
}

.admin-search-box input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: var(--transition);
}

.admin-search-box input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  border: none;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-hover);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

/* 漫画网格 */
.comic-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.comic-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  border: 2px solid transparent;
}

.comic-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.comic-card.violation {
  border-color: var(--primary);
}

.comic-card.offline {
  opacity: 0.75;
}

.cover-wrap {
  position: relative;
  aspect-ratio: 3 / 4;
  overflow: hidden;
  background: #f3f4f6;
}

.cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.badge {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.badge-danger {
  background: var(--primary);
}

.badge-muted {
  background: var(--text-muted);
}

.comic-info {
  padding: 14px;
}

.comic-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.comic-meta {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.comic-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 8px;
  border: 1px solid var(--border);
  background: white;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-main);
  cursor: pointer;
  transition: var(--transition);
}

.action-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.action-btn.danger:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.action-btn.warning:hover {
  border-color: #d97706;
  color: #d97706;
}

/* 响应式 */
@media (max-width: 640px) {
  .admin-search-box {
    flex-direction: column;
  }

  .comic-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
