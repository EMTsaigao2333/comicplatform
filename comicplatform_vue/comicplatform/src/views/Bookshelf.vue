<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'
import { getCoverUrl } from '@/utils/image.js'

const router = useRouter()
const userStore = useUserStore()

const bookshelf = ref([])
const loading = ref(true)
const activeTab = ref('all') // all | reading | finished

// 预留接口：获取用户书架列表
const fetchBookshelf = async () => {
  if (!userStore.user) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    const userId = userStore.user.userid
    // 后端接口：GET /api/bookshelf/user/{userId}
    const res = await axios.get(`/api/bookshelf/user/${userId}`)

    const list = res.data.data || []
    bookshelf.value = list.map(normalizeItem)
  } catch (err) {
    console.error('获取书架失败', err)
    useMockData()
  } finally {
    loading.value = false
  }
}

// 统一字段名
const normalizeItem = (item) => ({
  bookshelfid: item.bookshelfId ?? item.bookshelfid,
  userid: item.userId ?? item.userid,
  comicid: item.comicId ?? item.comicid,
  comictitle: item.comicTitle ?? item.comictitle ?? '未命名漫画',
  author: item.author ?? '未知作者',
  coverimg: item.coverImg ?? item.coverimg ?? getCoverUrl(),
  category: item.category ?? '其他',
  lastchapterid: item.lastChapterId ?? item.lastchapterid,
  lastchaptertitle: item.lastChapterTitle ?? item.lastchaptertitle,
  updatetime: item.updateTime ?? item.updatetime,
  unread: item.unread ?? 0,
})

// 假数据：未登录或接口未通时占位
const useMockData = () => {
  bookshelf.value = [
    {
      bookshelfid: 1,
      userid: 1,
      comicid: 1,
      comictitle: '航海王',
      author: '尾田荣一郎',
      coverimg: getCoverUrl(),
      category: '冒险',
      lastchapterid: 2,
      lastchaptertitle: '第2话：伙伴',
      updatetime: '2026-07-06 10:00',
      unread: 1,
    },
    {
      bookshelfid: 2,
      userid: 1,
      comicid: 2,
      comictitle: '龙珠',
      author: '鸟山明',
      coverimg: getCoverUrl(),
      category: '热血',
      lastchapterid: 1,
      lastchaptertitle: '第1话：悟空出世',
      updatetime: '2026-07-05 18:30',
      unread: 0,
    },
    {
      bookshelfid: 3,
      userid: 1,
      comicid: 3,
      comictitle: '火影忍者',
      author: '岸本齐史',
      coverimg: getCoverUrl(),
      category: '热血',
      lastchapterid: null,
      lastchaptertitle: '',
      updatetime: '2026-07-04 12:00',
      unread: 3,
    },
  ]
}

// 按标签过滤
const filteredList = computed(() => {
  if (activeTab.value === 'reading') {
    return bookshelf.value.filter(item => item.lastchapterid)
  }
  if (activeTab.value === 'finished') {
    return bookshelf.value.filter(item => !item.lastchapterid)
  }
  return bookshelf.value
})

// 继续阅读：有最近章节则跳转，否则去漫画详情
const continueReading = (item) => {
  if (item.lastchapterid) {
    router.push(`/chapter/${item.lastchapterid}`)
  } else {
    router.push(`/comic/${item.comicid}`)
  }
}

// 预留接口：删除书架项
const removeItem = async (item, event) => {
  event.stopPropagation()
  if (!confirm(`确定将《${item.comictitle}》从书架移除？`)) return

  try {
    await axios.delete('/api/bookshelf', {
      params: {
        userId: userStore.user?.userid,
        comicId: item.comicid,
      }
    })
    bookshelf.value = bookshelf.value.filter(i => i.bookshelfid !== item.bookshelfid)
  } catch (err) {
    console.error('移除书架失败', err)
    // 接口未通时也在前端移除，方便演示
    bookshelf.value = bookshelf.value.filter(i => i.bookshelfid !== item.bookshelfid)
  }
}

onMounted(fetchBookshelf)
</script>

<template>
  <div class="bookshelf-page">
    <!-- 未登录提示 -->
    <div v-if="!userStore.user" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 21V5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v16l7-3 7 3z"/></svg>
      </div>
      <h2>登录后查看书架</h2>
      <p>收藏喜欢的漫画，随时继续阅读</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>

    <!-- 已登录 -->
    <template v-else>
      <div class="page-header">
        <h2 class="page-title">我的书架</h2>
        <div class="tab-bar">
          <button :class="['tab', { active: activeTab === 'all' }]" @click="activeTab = 'all'">
            全部 <span class="count">{{ bookshelf.length }}</span>
          </button>
          <button :class="['tab', { active: activeTab === 'reading' }]" @click="activeTab = 'reading'">
            在读
          </button>
          <button :class="['tab', { active: activeTab === 'finished' }]" @click="activeTab = 'finished'">
            未读
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-state">加载中...</div>

      <div v-else-if="filteredList.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 21V5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v16l7-3 7 3z"/></svg>
        </div>
        <h2>书架还是空的</h2>
        <p>去首页发现喜欢的漫画吧</p>
        <router-link to="/" class="btn btn-primary">去首页</router-link>
      </div>

      <div v-else class="bookshelf-grid">
        <div
          v-for="item in filteredList"
          :key="item.bookshelfid"
          class="bookshelf-card"
          @click="continueReading(item)"
        >
          <div class="cover-wrap">
            <img :src="item.coverimg" :alt="item.comictitle">
            <span v-if="item.unread > 0" class="unread-badge">+{{ item.unread }}</span>
            <button class="remove-btn" title="移除" @click="removeItem(item, $event)">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>
            </button>
          </div>

          <div class="card-body">
            <h3 class="comic-title">{{ item.comictitle }}</h3>
            <p class="comic-meta">{{ item.author }} · {{ item.category }}</p>
            <p class="last-read">
              <span v-if="item.lastchaptertitle">读至 {{ item.lastchaptertitle }}</span>
              <span v-else>尚未开始阅读</span>
            </p>
            <p class="update-time">{{ item.updatetime ? item.updatetime.replace('T', ' ').slice(0, 16) : '' }}</p>
          </div>

          <div class="card-footer">
            <span class="continue-text">继续阅读</span>
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8.59 16.59 13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.bookshelf-page {
  padding-bottom: 40px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
}

.tab-bar {
  display: flex;
  gap: 8px;
}

.tab {
  padding: 8px 18px;
  border: 1px solid var(--border);
  background: white;
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-main);
  cursor: pointer;
  transition: var(--transition);
}

.tab:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.tab.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
}

.count {
  font-size: 12px;
  margin-left: 4px;
  opacity: 0.8;
}

.loading-state {
  text-align: center;
  padding: 80px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 100px 24px;
  background: var(--bg-card);
  border-radius: var(--radius);
  text-align: center;
}

.empty-icon {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(225, 29, 72, 0.08);
  color: var(--primary);
  border-radius: 50%;
}

.empty-icon svg {
  width: 36px;
  height: 36px;
}

.empty-state h2 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
}

.empty-state p {
  font-size: 14px;
  color: var(--text-muted);
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 28px;
  background: var(--primary);
  color: white;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  transition: var(--transition);
}

.btn-primary:hover {
  background: var(--primary-hover);
}

/* 书架网格 */
.bookshelf-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.bookshelf-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  cursor: pointer;
}

.bookshelf-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.cover-wrap {
  position: relative;
  aspect-ratio: 3 / 4;
  overflow: hidden;
}

.cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 300ms ease;
}

.bookshelf-card:hover .cover-wrap img {
  transform: scale(1.05);
}

.unread-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  background: var(--primary);
  color: white;
  font-size: 12px;
  font-weight: 700;
  border-radius: 12px;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.5);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  opacity: 0;
  transition: var(--transition);
}

.bookshelf-card:hover .remove-btn {
  opacity: 1;
}

.remove-btn:hover {
  background: var(--primary);
}

.remove-btn svg {
  width: 18px;
  height: 18px;
}

.card-body {
  padding: 14px;
}

.comic-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comic-meta {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.last-read {
  font-size: 13px;
  color: var(--accent);
  margin-bottom: 6px;
}

.update-time {
  font-size: 12px;
  color: var(--text-muted);
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-top: 1px solid var(--border);
  color: var(--primary);
  font-size: 14px;
  font-weight: 500;
}

.card-footer svg {
  width: 18px;
  height: 18px;
}

/* 响应式 */
@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .bookshelf-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .remove-btn {
    opacity: 1;
  }
}
</style>
