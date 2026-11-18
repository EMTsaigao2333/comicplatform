<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const comicId = route.params.id

const comic = ref({})
const chapters = ref([])
const loading = ref(true)
const activeTab = ref('chapters') // chapters | detail
const isBookshelf = ref(false)
const adding = ref(false)

// 评论区数据
const comments = ref([])
const commentContent = ref('')
const commentRating = ref(5)
const avgRating = ref(0)
const submitting = ref(false)

// 查询评论列表与平均分
const fetchComments = async () => {
  try {
    const [listRes, ratingRes] = await Promise.all([
      axios.get(`/api/comment/comic/${comicId}`),
      axios.get(`/api/comment/rating/${comicId}`)
    ])
    comments.value = listRes.data.data || []
    avgRating.value = ratingRes.data.data || 0
  } catch (err) {
    console.error('获取评论失败', err)
  }
}

// 发表评论
const submitComment = async () => {
  if (!userStore.user) {
    router.push('/login')
    return
  }
  if (!commentContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    await axios.post('/api/comment', {
      comicid: parseInt(comicId),
      userid: userStore.user.userid,
      username: userStore.user.username,
      content: commentContent.value.trim(),
      rating: commentRating.value
    })
    commentContent.value = ''
    commentRating.value = 5
    await fetchComments()
  } catch (err) {
    console.error('发表评论失败', err)
    alert('评论发表失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 删除自己的评论（管理员可删除任意评论）
const removeComment = async (item) => {
  const isOwner = userStore.user && item.userid === userStore.user.userid
  const isAdmin = userStore.isAdmin
  if (!isOwner && !isAdmin) return
  if (!confirm('确定删除这条评论吗？')) return
  try {
    await axios.delete(`/api/comment/${item.commentid}`)
    await fetchComments()
  } catch (err) {
    console.error('删除评论失败', err)
    alert('删除失败，请重试')
  }
}

const formatTime = (t) => (t ? t.replace('T', ' ').slice(0, 16) : '')

const imgUrl = (prompt, size) => {
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`
}

const defaultCover = imgUrl('anime manga default cover placeholder, colorful abstract comic art style, mystery adventure theme, professional illustration', 'portrait_4_3')

const fetchData = async () => {
  loading.value = true
  try {
    const [comicRes, chapterRes] = await Promise.all([
      axios.get(`/api/comic/${comicId}`),
      axios.get(`/api/chapter/comic/${comicId}`)
    ])
    comic.value = comicRes.data.data || {}
    chapters.value = chapterRes.data.data || []

    if (userStore.user) {
      // const statusRes = await axios.get('/api/bookshelf/status', {
      const statusRes = await axios.get('/api/bookshelf/check', {
        params: { userId: userStore.user.userid, comicId }
      })
      isBookshelf.value = statusRes.data.data === true
    }
  } catch (err) {
    console.error('获取漫画详情失败', err)
  } finally {
    loading.value = false
  }
}

const toggleBookshelf = async () => {
  if (!userStore.user) {
    router.push('/login')
    return
  }
  adding.value = true
  try {
    if (isBookshelf.value) {
      await axios.delete('/api/bookshelf', {
        params: { userId: userStore.user.userid, comicId }
      })
      isBookshelf.value = false
    } else {
      //修改原因：字段名必须和后端 Bookshelf 实体一致：userid / comicid
      //Jackson 反序列化时大小写敏感， userId 无法自动映射到userid ，
      //导致后端接收到的 Bookshelf 对象两个字段都是 null ，然后 addToBookshelf(null, null) 就报错了。
      await axios.post('/api/bookshelf', {
        // userId: userStore.user.userid,
        // comicId: parseInt(comicId)
        userid: userStore.user.userid,
        comicid: parseInt(comicId)
      })
      isBookshelf.value = true
    }
  } catch (err) {
    alert('操作失败，请重试')
  } finally {
    adding.value = false
  }
}

const readChapter = (chapterId) => {
  router.push(`/chapter/${chapterId}`)
}

const startReading = () => {
  if (chapters.value.length > 0) {
    const sorted = [...chapters.value].sort((a, b) => (a.chapterno || 0) - (b.chapterno || 0))
    const firstPublished = sorted.find(c => c.status === '发布')
    router.push(`/chapter/${firstPublished ? firstPublished.chapterid : sorted[0].chapterid}`)
  }
}

onMounted(() => {
  fetchData()
  fetchComments()
})
</script>

<template>
  <div v-if="loading" class="loading-state">加载中...</div>

  <div v-else class="comic-detail">
    <!-- 顶部信息区：B站漫画详情风格 -->
    <section class="hero-section">
      <div class="hero-bg" :style="{ backgroundImage: `url(${comic.coverImg || defaultCover})` }"></div>
      <div class="hero-content">
        <div class="cover-wrap">
          <img :src="comic.coverImg || defaultCover" :alt="comic.comicTitle" @error="$event.target.src = defaultCover">
        </div>
        <div class="info-wrap">
          <h1 class="comic-title">{{ comic.comicTitle }}</h1>
          <p class="comic-meta">
            <span class="author">{{ comic.author }}</span>
            <span class="divider">|</span>
            <span class="category">{{ comic.category }}</span>
            <span class="divider">|</span>
            <span class="country">{{ comic.country || '日本' }}</span>
          </p>
          <p class="comic-tags">
            <span v-for="tag in (comic.keywords ? comic.keywords.split(';') : ['热血', '冒险'])" :key="tag" class="tag">
              {{ tag.trim() }}
            </span>
          </p>
          <p class="comic-intro">{{ comic.intro }}</p>
          <div class="action-btns">
            <button class="btn btn-read" @click="startReading">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
              开始阅读
            </button>
            <button class="btn btn-bookshelf" :disabled="adding" @click="toggleBookshelf">
              <svg v-if="!isBookshelf" viewBox="0 0 24 24" fill="currentColor"><path d="M19 21V5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v16l7-3 7 3z"/></svg>
              <svg v-else viewBox="0 0 24 24" fill="currentColor"><path d="M17 3H7c-1.1 0-2 .9-2 2v16l7-3 7 3V5c0-1.1-.9-2-2-2z"/></svg>
              {{ isBookshelf ? '已收藏' : '加入书架' }}
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 内容区 -->
    <div class="detail-layout">
      <main class="main-content">
        <!-- 标签切换 -->
        <div class="tabs">
          <button :class="['tab', { active: activeTab === 'chapters' }]" @click="activeTab = 'chapters'">章节列表</button>
          <button :class="['tab', { active: activeTab === 'detail' }]" @click="activeTab = 'detail'">作品详情</button>
        </div>

        <!-- 章节列表 -->
        <div v-if="activeTab === 'chapters'" class="chapter-list">
          <div class="list-header">
            <span>共 {{ chapters.length }} 章</span>
            <span class="sort-hint">按章节编号正序</span>
          </div>
          <div
            v-for="chapter in chapters"
            :key="chapter.chapterid"
            :class="['chapter-item', { draft: chapter.status === '草稿' }]"
            @click="chapter.status === '发布' ? readChapter(chapter.chapterid) : null"
          >
            <div class="chapter-info">
              <span class="chapter-num">第{{ chapter.chapterno }}话</span>
              <span class="chapter-title">{{ chapter.chaptertitle }}</span>
            </div>
            <div class="chapter-meta">
              <span class="chapter-date">{{ chapter.releasedate ? chapter.releasedate.replace('T', ' ').slice(0, 16) : '' }}</span>
              <span :class="['chapter-status', chapter.status]">{{ chapter.status }}</span>
            </div>
          </div>
        </div>

        <!-- 作品详情 -->
        <div v-else class="detail-info">
          <h3>作品简介</h3>
          <p class="detail-text">{{ comic.intro }}</p>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">漫画标题</span>
              <span class="value">{{ comic.comicTitle }}</span>
            </div>
            <div class="detail-item">
              <span class="label">作者</span>
              <span class="value">{{ comic.author }}</span>
            </div>
            <div class="detail-item">
              <span class="label">类别</span>
              <span class="value">{{ comic.category }}</span>
            </div>
            <div class="detail-item">
              <span class="label">国家</span>
              <span class="value">{{ comic.country || '日本' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">关键字</span>
              <span class="value">{{ comic.keywords || '暂无' }}</span>
            </div>
          </div>
        </div>
      </main>

      <!-- 侧边栏 -->
      <aside class="detail-sidebar">
        <div class="sidebar-block">
          <h4>最新章节</h4>
          <p v-if="chapters.length > 0" class="latest-chapter">
            {{ chapters[chapters.length - 1].chaptertitle }}
          </p>
          <p v-else class="latest-chapter">暂无章节</p>
        </div>

        <div class="sidebar-block">
          <h4>作者其他作品</h4>
          <p class="empty-tip">敬请期待</p>
        </div>
      </aside>
    </div>

    <!-- 评论区 -->
    <section class="comment-section">
      <div class="comment-header">
        <h3>读者评论</h3>
        <span class="rating-summary">
          平均评分 <strong>{{ avgRating }}</strong> 分 · 共 {{ comments.length }} 条评论
        </span>
      </div>

      <!-- 发表评论 -->
      <div class="comment-form">
        <textarea
          v-model="commentContent"
          rows="3"
          placeholder="说说你对这部漫画的看法吧..."
        ></textarea>
        <div class="comment-form-footer">
          <div class="rating-picker">
            <span class="rating-label">评分：</span>
            <button
              v-for="n in 5"
              :key="n"
              :class="['star', { active: n <= commentRating }]"
              @click="commentRating = n"
            >★</button>
          </div>
          <button class="btn btn-primary" :disabled="submitting" @click="submitComment">
            {{ submitting ? '发表中...' : '发表评论' }}
          </button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div v-if="comments.length === 0" class="empty-tip comment-empty">暂无评论，快来抢沙发~</div>
      <div v-else class="comment-list">
        <div v-for="item in comments" :key="item.commentid" class="comment-item">
          <div class="comment-avatar">{{ (item.username || '匿').charAt(0) }}</div>
          <div class="comment-body">
            <div class="comment-top">
              <span class="comment-user">{{ item.username || '匿名用户' }}</span>
              <span class="comment-stars">{{ '★'.repeat(item.rating || 0) }}</span>
              <span class="comment-time">{{ formatTime(item.createtime) }}</span>
              <button
                v-if="userStore.isAdmin || (userStore.user && item.userid === userStore.user.userid)"
                class="comment-del"
                @click="removeComment(item)"
              >删除</button>
            </div>
            <p class="comment-text">{{ item.content }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.comic-detail {
  padding-bottom: 40px;
}

.loading-state {
  text-align: center;
  padding: 80px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

/* 顶部信息区 */
.hero-section {
  position: relative;
  border-radius: var(--radius);
  overflow: hidden;
  margin-bottom: 24px;
  min-height: 320px;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: blur(20px) brightness(0.4);
  transform: scale(1.1);
}

.hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(0,0,0,0.85) 0%, rgba(0,0,0,0.4) 100%);
}

.hero-content {
  position: relative;
  display: flex;
  gap: 32px;
  padding: 40px;
  z-index: 1;
}

.cover-wrap {
  width: 220px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0,0,0,0.4);
}

.cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  aspect-ratio: 3 / 4;
}

.info-wrap {
  flex: 1;
  color: white;
  display: flex;
  flex-direction: column;
}

.comic-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 14px;
  font-family: 'Noto Serif SC', serif;
}

.comic-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  margin-bottom: 14px;
}

.author {
  font-weight: 500;
}

.divider {
  color: rgba(255,255,255,0.4);
}

.comic-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.comic-tags .tag {
  padding: 4px 10px;
  background: rgba(255,255,255,0.15);
  border-radius: 4px;
  font-size: 12px;
  color: rgba(255,255,255,0.9);
}

.comic-intro {
  font-size: 14px;
  line-height: 1.7;
  color: rgba(255,255,255,0.75);
  margin-bottom: 24px;
  max-width: 700px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.action-btns {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  border: none;
}

.btn svg {
  width: 18px;
  height: 18px;
}

.btn-read {
  background: var(--accent);
  color: white;
}

.btn-read:hover {
  background: #1d4ed8;
}

.btn-bookshelf {
  background: rgba(255,255,255,0.15);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
}

.btn-bookshelf:hover {
  background: rgba(255,255,255,0.25);
}

.btn-bookshelf:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 内容布局 */
.detail-layout {
  display: flex;
  gap: 24px;
}

.main-content {
  flex: 1;
  min-width: 0;
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.tabs {
  display: flex;
  gap: 24px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 20px;
}

.tab {
  padding: 10px 4px;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-muted);
  background: none;
  border: none;
  cursor: pointer;
  position: relative;
  transition: var(--transition);
}

.tab:hover {
  color: var(--text-main);
}

.tab.active {
  color: var(--primary);
  font-weight: 600;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary);
}

/* 章节列表 */
.chapter-list .list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.chapter-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: var(--transition);
  border-bottom: 1px solid #f3f4f6;
}

.chapter-item:hover {
  background: #f9fafb;
}

.chapter-item.draft {
  cursor: not-allowed;
  opacity: 0.6;
}

.chapter-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chapter-num {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
  min-width: 60px;
}

.chapter-title {
  font-size: 14px;
  color: var(--text-main);
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chapter-date {
  font-size: 13px;
  color: var(--text-muted);
}

.chapter-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.chapter-status.发布 {
  background: rgba(7, 193, 96, 0.1);
  color: #07c160;
}

.chapter-status.草稿 {
  background: rgba(107, 114, 128, 0.1);
  color: var(--text-muted);
}

/* 作品详情 */
.detail-info h3 {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 12px;
  font-family: 'Noto Serif SC', serif;
}

.detail-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-main);
  margin-bottom: 24px;
}

.detail-grid {
  display: grid;
  gap: 14px;
}

.detail-item {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.detail-item .label {
  width: 70px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.detail-item .value {
  color: var(--text-main);
}

/* 侧边栏 */
.detail-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-block {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}

.sidebar-block h4 {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 12px;
  font-family: 'Noto Serif SC', serif;
}

.latest-chapter {
  font-size: 14px;
  color: var(--accent);
  font-weight: 500;
}

.empty-tip {
  font-size: 13px;
  color: var(--text-muted);
}

/* 评论区 */
.comment-section {
  margin-top: 24px;
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.comment-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  border-bottom: 1px solid var(--border);
  padding-bottom: 14px;
  margin-bottom: 18px;
}

.comment-header h3 {
  font-size: 17px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
}

.rating-summary {
  font-size: 13px;
  color: var(--text-muted);
}

.rating-summary strong {
  color: var(--gold);
  font-size: 16px;
}

.comment-form {
  background: #fafafa;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 20px;
}

.comment-form textarea {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  outline: none;
  background: white;
}

.comment-form textarea:focus {
  border-color: var(--accent);
}

.comment-form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.rating-picker {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating-label {
  font-size: 13px;
  color: var(--text-muted);
}

.star {
  background: none;
  border: none;
  font-size: 20px;
  line-height: 1;
  color: #d1d5db;
  cursor: pointer;
  transition: var(--transition);
}

.star.active {
  color: var(--gold);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #f0f0f2;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-avatar {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), #fb7185);
  color: white;
  font-size: 15px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.comment-user {
  font-size: 14px;
  font-weight: 600;
}

.comment-stars {
  color: var(--gold);
  font-size: 13px;
  letter-spacing: 1px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-muted);
}

.comment-del {
  margin-left: auto;
  border: none;
  background: none;
  color: var(--text-muted);
  font-size: 12px;
  cursor: pointer;
}

.comment-del:hover {
  color: var(--primary);
}

.comment-text {
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-main);
  word-break: break-word;
}

.comment-empty {
  text-align: center;
  padding: 30px 0;
}

@media (max-width: 1024px) {
  .detail-layout {
    flex-direction: column;
  }
  .detail-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .cover-wrap {
    width: 160px;
  }
  .comic-title {
    font-size: 24px;
  }
  .comic-meta,
  .comic-tags,
  .action-btns {
    justify-content: center;
  }
}
</style>
