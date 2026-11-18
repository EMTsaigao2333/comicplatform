<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { getContentUrl, getCoverUrl } from '@/utils/image.js'

const route = useRoute()
const router = useRouter()
const chapterId = computed(() => route.params.chapterId)

// 当前章节与漫画信息
const chapter = ref({})
const comic = ref({})
const chapters = ref([])
const loading = ref(true)
const errorMsg = ref('')

// 阅读模式：scroll 滚动阅读 | page 单页翻页
const readMode = ref('scroll')
// 单页模式下当前页码
const currentPage = ref(0)

// 预留接口：获取章节详情（含内容页图片）
const fetchChapter = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await axios.get(`/api/chapter/${chapterId.value}`)
    const data = res.data?.data

    // 接口暂未实现或返回空数据时，用假数据占位
    if (!data || !data.chapterid) {
      useMockData()
      return
    }

    chapter.value = normalizeChapter(data)

    // 同时获取漫画信息和其他章节用于上一章/下一章导航
    if (chapter.value.comicid) {
      await fetchComicAndChapters(chapter.value.comicid)
    }
  } catch (err) {
    console.error('获取章节失败', err)
    // 接口未通时使用假数据占位，方便继续开发 UI
    useMockData()
  } finally {
    loading.value = false
  }
}

// 预留接口：获取漫画详情与章节列表
const fetchComicAndChapters = async (comicId) => {
  try {
    const [comicRes, chapterRes] = await Promise.all([
      axios.get(`/api/comic/${comicId}`),
      axios.get(`/api/chapter/comic/${comicId}`)
    ])
    comic.value = comicRes.data.data || {}
    chapters.value = (chapterRes.data.data || []).map(normalizeChapter).sort((a, b) => a.chapterno - b.chapterno)
  } catch (err) {
    console.error('获取漫画信息失败', err)
  }
}

// 统一字段名（后端可能返回驼峰/全小写）
const normalizeChapter = (item) => ({
  chapterid: item.chapterId ?? item.chapterid,
  comicid: item.comicId ?? item.comicid,
  chapterno: item.chapterNo ?? item.chapterno,
  chaptertitle: item.chapterTitle ?? item.chaptertitle,
  content: item.content,
  releasedate: item.releaseDate ?? item.releasedate,
  status: item.status,
})

// 假数据：用于前端独立开发时占位
const useMockData = () => {
  const id = Number(chapterId.value) || 1
  const titles = {
    1: '第1话：冒险的开始',
    2: '第2话：伙伴',
    3: '第3话：风暴',
  }
  chapter.value = {
    chapterid: id,
    comicid: 1,
    chapterno: id,
    chaptertitle: titles[id] || `第${id}话：新的篇章`,
    content: JSON.stringify(Array.from({ length: 5 }, getContentUrl)),
    releasedate: '2026-07-06T00:00:00',
    status: '发布',
  }
  comic.value = {
    comicId: 1,
    comicTitle: '航海王',
    coverImg: getCoverUrl(),
    author: '尾田荣一郎',
  }
  chapters.value = [
    { chapterid: 1, comicid: 1, chapterno: 1, chaptertitle: '第1话：冒险的开始', content: '[]', status: '发布' },
    { chapterid: 2, comicid: 1, chapterno: 2, chaptertitle: '第2话：伙伴', content: '[]', status: '发布' },
    { chapterid: 3, comicid: 1, chapterno: 3, chaptertitle: '第3话：风暴', content: '[]', status: '发布' },
  ]
}

// 解析章节内容页图片数组
const pages = computed(() => {
  const content = chapter.value.content
  if (!content) return [getContentUrl()]
  try {
    const parsed = JSON.parse(content)
    if (Array.isArray(parsed) && parsed.length > 0) return parsed
    return [parsed]
  } catch {
    // content 不是 JSON 时按字符串/逗号分隔处理
    return content.split(',').filter(Boolean).length > 0
      ? content.split(',').filter(Boolean)
      : [getContentUrl()]
  }
})

// 当前章节在列表中的索引
const currentIndex = computed(() => {
  return chapters.value.findIndex(c => c.chapterid === Number(chapterId.value))
})

const hasPrev = computed(() => currentIndex.value > 0)
const hasNext = computed(() => currentIndex.value >= 0 && currentIndex.value < chapters.value.length - 1)

const prevChapter = () => {
  if (hasPrev.value) {
    const prev = chapters.value[currentIndex.value - 1]
    router.push(`/chapter/${prev.chapterid}`)
  }
}

const nextChapter = () => {
  if (hasNext.value) {
    const next = chapters.value[currentIndex.value + 1]
    router.push(`/chapter/${next.chapterid}`)
  }
}

// 单页翻页
const nextPage = () => {
  if (readMode.value === 'page' && currentPage.value < pages.value.length - 1) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (readMode.value === 'page' && currentPage.value > 0) {
    currentPage.value--
  }
}

// 切换阅读模式
const setMode = (mode) => {
  readMode.value = mode
  currentPage.value = 0
}

// 键盘快捷键：左右翻页，上/下切换章节
const onKeyDown = (e) => {
  if (readMode.value === 'page') {
    if (e.key === 'ArrowRight' || e.key === ' ') nextPage()
    if (e.key === 'ArrowLeft') prevPage()
  }
  if (e.key === 'ArrowUp') prevChapter()
  if (e.key === 'ArrowDown') nextChapter()
}

onMounted(() => {
  fetchChapter()
  window.addEventListener('keydown', onKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown)
})

// 路由参数变化时重新加载章节（例如从第1话跳到第2话）
watch(chapterId, () => {
  currentPage.value = 0
  fetchChapter()
})
</script>

<template>
  <div class="reader-page">
    <!-- 顶部工具栏 -->
    <header class="reader-header">
      <div class="header-left">
        <router-link :to="`/comic/${comic.comicId || chapter.comicid || 1}`" class="back-link">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/></svg>
          返回详情
        </router-link>
        <h1 class="chapter-title">{{ comic.comicTitle || '漫画' }} · {{ chapter.chaptertitle || `第${chapter.chapterno}话` }}</h1>
      </div>

      <div class="header-right">
        <div class="mode-switch">
          <button :class="['mode-btn', { active: readMode === 'scroll' }]" @click="setMode('scroll')">滚动</button>
          <button :class="['mode-btn', { active: readMode === 'page' }]" @click="setMode('page')">翻页</button>
        </div>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="reader-state">章节加载中...</div>

    <!-- 错误/空状态 -->
    <div v-else-if="errorMsg" class="reader-state error">{{ errorMsg }}</div>

    <!-- 阅读区 -->
    <div v-else class="reader-body">
      <!-- 滚动模式：连续展示所有图片 -->
      <div v-if="readMode === 'scroll'" class="scroll-view">
        <div class="page-list">
          <div v-for="(url, index) in pages" :key="index" class="page-item">
            <img :src="url" :alt="`第${index + 1}页`">
            <span class="page-num">{{ index + 1 }} / {{ pages.length }}</span>
          </div>
        </div>

        <!-- 章节底部导航 -->
        <div class="chapter-nav">
          <button class="nav-btn" :disabled="!hasPrev" @click="prevChapter">上一章</button>
          <router-link class="nav-btn outline" :to="`/comic/${comic.comicId || chapter.comicid || 1}`">目录</router-link>
          <button class="nav-btn" :disabled="!hasNext" @click="nextChapter">下一章</button>
        </div>
      </div>

      <!-- 翻页模式：一次展示一页，两侧点击翻页 -->
      <div v-else class="page-view">
        <div class="page-stage">
          <img :src="pages[currentPage]" :alt="`第${currentPage + 1}页`">
          <div class="click-zone prev" @click="prevPage"></div>
          <div class="click-zone next" @click="nextPage"></div>
        </div>

        <div class="page-bar">
          <button class="nav-btn" :disabled="!hasPrev" @click="prevChapter">上一章</button>
          <span class="page-indicator">{{ currentPage + 1 }} / {{ pages.length }}</span>
          <button class="nav-btn" :disabled="!hasNext" @click="nextChapter">下一章</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 阅读页占满视口，深色背景沉浸阅读 */
.reader-page {
  min-height: calc(100vh - var(--nav-height));
  background: #0d0d12;
  color: white;
}

.reader-header {
  position: sticky;
  top: var(--nav-height);
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 24px;
  background: rgba(13, 13, 18, 0.95);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 0;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: rgba(255,255,255,0.7);
  font-size: 14px;
  flex-shrink: 0;
  transition: var(--transition);
}

.back-link:hover {
  color: white;
}

.back-link svg {
  width: 18px;
  height: 18px;
}

.chapter-title {
  font-size: 16px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: rgba(255,255,255,0.9);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.mode-switch {
  display: inline-flex;
  background: rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 3px;
}

.mode-btn {
  padding: 6px 16px;
  border: none;
  background: transparent;
  color: rgba(255,255,255,0.65);
  font-size: 13px;
  border-radius: 17px;
  cursor: pointer;
  transition: var(--transition);
}

.mode-btn.active {
  background: var(--primary);
  color: white;
}

.reader-state {
  text-align: center;
  padding: 80px 24px;
  color: rgba(255,255,255,0.6);
}

.reader-state.error {
  color: #fb7185;
}

/* 滚动模式 */
.scroll-view {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 16px 60px;
}

.page-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-item {
  position: relative;
  background: #141419;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.4);
}

.page-item img {
  width: 100%;
  display: block;
  min-height: 300px;
  object-fit: contain;
}

.page-num {
  position: absolute;
  bottom: 12px;
  right: 12px;
  padding: 4px 10px;
  background: rgba(0,0,0,0.6);
  color: rgba(255,255,255,0.8);
  font-size: 12px;
  border-radius: 12px;
}

.chapter-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 40px;
}

/* 翻页模式 */
.page-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px 60px;
}

.page-stage {
  position: relative;
  max-width: 900px;
  width: 100%;
  background: #141419;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0,0,0,0.5);
}

.page-stage img {
  width: 100%;
  display: block;
  min-height: 60vh;
  object-fit: contain;
}

.click-zone {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 40%;
  cursor: pointer;
}

.click-zone.prev {
  left: 0;
}

.click-zone.next {
  right: 0;
}

.page-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
}

.page-indicator {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
  min-width: 60px;
  text-align: center;
}

/* 通用按钮 */
.nav-btn {
  padding: 10px 22px;
  border: none;
  border-radius: 8px;
  background: var(--primary);
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
}

.nav-btn:hover:not(:disabled) {
  background: var(--primary-hover);
}

.nav-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.nav-btn.outline {
  background: transparent;
  border: 1px solid rgba(255,255,255,0.25);
}

.nav-btn.outline:hover:not(:disabled) {
  background: rgba(255,255,255,0.08);
}

/* 响应式 */
@media (max-width: 768px) {
  .reader-header {
    flex-wrap: wrap;
    padding: 10px 16px;
  }

  .chapter-title {
    font-size: 14px;
    max-width: 180px;
  }

  .scroll-view,
  .page-stage {
    max-width: 100%;
  }

  .page-stage img {
    min-height: 40vh;
  }
}
</style>
