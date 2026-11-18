<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { getCoverUrl, getBannerUrl } from '@/utils/image.js'

const router = useRouter()

const comics = ref([])
const loading = ref(true)
const keyword = ref('')
const category = ref('')

const imgUrl = (prompt, size) => {
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`
}

const coverImages = Array.from({ length: 6 }, getCoverUrl)

const mockComics = [
  { comicid: 1, comictitle: '航海王', author: '尾田荣一郎', category: '冒险', coverimg: coverImages[0], intro: '少年路飞立志成为海贼王的冒险故事' },
  { comicid: 2, comictitle: '龙珠', author: '鸟山明', category: '热血', coverimg: coverImages[1], intro: '孙悟空寻找龙珠、保卫地球的经典热血漫画' },
  { comicid: 3, comictitle: '火影忍者', author: '岸本齐史', category: '热血', coverimg: coverImages[2], intro: '漩涡鸣人成为火影的忍者成长之路' },
  { comicid: 4, comictitle: '光明之刃', author: '林一', category: '奇幻', coverimg: coverImages[3], intro: '少年持剑觉醒，与龙族缔结契约拯救世界' },
  { comicid: 5, comictitle: '樱花下的约定', author: '苏浅', category: '恋爱', coverimg: coverImages[4], intro: '校园里的青涩恋爱与樱花树下的约定' },
  { comicid: 6, comictitle: '暗月', author: '墨染', category: '悬疑', coverimg: coverImages[5], intro: '红月之夜接连发生的离奇失踪事件' },
]

const announcements = ref([
  {
    id: 1,
    title: '未选择的梦 | 3.4版本内容说明',
    content: '3.4版本已完成开发，新增分类筛选、评论互动与公告栏功能，欢迎体验。',
    image: getBannerUrl(),
    type: '公告',
    date: '2026-07-04',
    featured: true,
  },
  {
    id: 2,
    title: '暑期漫画狂欢节开启',
    content: '暑期期间平台将上线专题推荐位，优质原创作品可获得首页曝光机会。',
    image: getCoverUrl(),
    type: '活动',
    date: '2026-07-03',
    featured: false,
  },
  {
    id: 3,
    title: '新番连载《星际回响》正式上线',
    content: '全新科幻题材连载作品上线，每周五更新一章。',
    image: getCoverUrl(),
    type: '更新',
    date: '2026-07-02',
    featured: false,
  },
  {
    id: 4,
    title: '作者招募计划：圆你的漫画梦',
    content: '平台长期招募原创漫画作者，入驻即享作品发布与数据反馈服务。',
    image: getCoverUrl(),
    type: '招募',
    date: '2026-07-01',
    featured: false,
  },
])

// 公告详情弹窗
const selectedNotice = ref(null)
const openNotice = (item) => {
  if (item) selectedNotice.value = item
}
const closeNotice = () => { selectedNotice.value = null }

// 查询后端已发布公告，替换首页公告栏
const fetchNotices = async () => {
  try {
    const res = await axios.get('/api/notice/published')
    const list = res.data.data || []
    if (list.length > 0) {
      announcements.value = list.map((item, index) => ({
        id: item.noticeid ?? index,
        title: item.title,
        content: item.content || '暂无公告正文内容',
        image: index === 0 ? getBannerUrl() : getCoverUrl(),
        type: item.noticetype || '公告',
        date: (item.publishdate || '').slice(0, 10),
        featured: index === 0,
      }))
    }
  } catch (err) {
    console.error('获取公告失败，使用默认公告', err)
  }
}

const categories = ['热血', '冒险', '恋爱', '奇幻', '悬疑']

const fetchComics = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/comic/page', {
      params: { pageNum: 1, pageSize: 12, keyword: keyword.value, category: category.value }
    })
    const list = res.data.data?.records || res.data.data || []
    if (list.length > 0) {
      comics.value = list.map((item, index) => ({
        comicid: item.comicId ?? item.comicid,
        comictitle: item.comicTitle ?? item.comictitle,
        author: item.author,
        category: item.category,
        intro: item.intro,
        coverimg: item.coverImg || item.coverimg || coverImages[index % coverImages.length]
      }))
    } else {
      comics.value = mockComics
    }
  } catch (err) {
    console.error('获取漫画失败', err)
    comics.value = mockComics
  } finally {
    loading.value = false
  }
}

const search = () => fetchComics()
const selectCategory = (cat) => {
  category.value = cat
  fetchComics()
}

onMounted(() => {
  fetchComics()
  fetchNotices()
})
</script>

<template>
  <div class="home-layout">
    <!-- 左侧公告栏：参考鸣潮公告风格 -->
    <aside class="sidebar">
      <div class="sidebar-title">
        <span class="gold-dot"></span>
        公告栏
      </div>

      <div
        v-if="announcements.length > 0"
        class="featured-announcement"
        @click="openNotice(announcements[0])"
      >
        <div class="featured-image-wrap">
          <img :src="announcements[0].image" :alt="announcements[0].title">
          <div class="featured-overlay">
            <span class="featured-tag">{{ announcements[0].type }}</span>
            <h3>{{ announcements[0].title }}</h3>
            <span class="notice-more">点击查看详情 &gt;</span>
          </div>
        </div>
      </div>

      <ul class="announcement-list">
        <li
          v-for="item in announcements.slice(1)"
          :key="item.id"
          class="announcement-item"
          @click="openNotice(item)"
        >
          <img :src="item.image" :alt="item.title" class="announcement-thumb">
          <div class="announcement-body">
            <span class="announcement-tag" :class="item.type">{{ item.type }}</span>
            <h4>{{ item.title }}</h4>
            <time>{{ item.date }}</time>
          </div>
        </li>
      </ul>
    </aside>

    <!-- 公告详情弹窗 -->
    <div v-if="selectedNotice" class="notice-mask" @click.self="closeNotice">
      <div class="notice-modal">
        <div class="notice-modal-header">
          <span class="notice-modal-tag">{{ selectedNotice.type }}</span>
          <button class="notice-close" @click="closeNotice" aria-label="关闭">×</button>
        </div>
        <h2 class="notice-modal-title">{{ selectedNotice.title }}</h2>
        <p class="notice-modal-time">发布时间：{{ selectedNotice.date }}</p>
        <div class="notice-modal-body">{{ selectedNotice.content }}</div>
        <div class="notice-modal-footer">
          <button class="notice-btn" @click="closeNotice">我知道了</button>
        </div>
      </div>
    </div>

    <!-- 右侧漫画列表 -->
    <section class="content-area">
      <div class="content-header">
        <h2 class="section-title">
          <span class="title-icon"></span>
          热门漫画
        </h2>

        <div class="filter-bar">
          <div class="category-tabs">
            <button
              v-for="cat in categories"
              :key="cat"
              :class="['tab', { active: category === cat }]"
              @click="selectCategory(cat)"
            >
              {{ cat }}
            </button>
            <button class="tab all-tab" @click="router.push('/category')">
              全部 <span class="arrow">&gt;</span>
            </button>
          </div>

          <div class="search-bar">
            <input v-model="keyword" type="text" placeholder="搜索漫画、作者" @keyup.enter="search">
            <button class="search-submit" @click="search">搜索</button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-state">加载中...</div>

      <div v-else class="comic-grid">
        <router-link
          v-for="comic in comics"
          :key="comic.comicid"
          :to="`/comic/${comic.comicid}`"
          class="comic-card"
        >
          <div class="cover-wrap">
            <img :src="comic.coverimg" :alt="comic.comictitle">
            <span class="category-badge">{{ comic.category }}</span>
          </div>
          <div class="comic-info">
            <h3 class="comic-title">{{ comic.comictitle }}</h3>
            <p class="comic-author">{{ comic.author }}</p>
            <p class="comic-intro">{{ comic.intro }}</p>
          </div>
        </router-link>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* 左侧公告栏 */
.sidebar {
  width: 320px;
  flex-shrink: 0;
  background: #141419;
  border-radius: var(--radius);
  padding: 20px;
  color: white;
  box-shadow: var(--shadow-lg);
  position: sticky;
  top: calc(var(--nav-height) + 24px);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  font-family: 'Noto Serif SC', serif;
}

.gold-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--gold);
  box-shadow: 0 0 8px var(--gold);
}

/* 鸣潮风格大公告 */
.featured-announcement {
  border: 1px solid var(--gold);
  border-radius: var(--radius);
  overflow: hidden;
  margin-bottom: 16px;
  position: relative;
  cursor: pointer;
  transition: var(--transition);
}

.featured-announcement:hover {
  border-color: var(--gold-light);
  box-shadow: 0 0 18px rgba(212, 175, 55, 0.35);
}

.notice-more {
  display: inline-block;
  margin-top: 8px;
  font-size: 12px;
  color: var(--gold-light);
  opacity: 0.85;
}

.featured-announcement::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid transparent;
  box-shadow: inset 0 0 20px rgba(212, 175, 55, 0.15);
  pointer-events: none;
  z-index: 2;
}

.featured-image-wrap {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.featured-image-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 400ms ease;
}

.featured-announcement:hover .featured-image-wrap img {
  transform: scale(1.05);
}

.featured-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 16px 16px;
  background: linear-gradient(to top, rgba(0,0,0,0.9), transparent);
  z-index: 1;
}

.featured-tag {
  display: inline-block;
  padding: 2px 8px;
  background: var(--gold);
  color: #141419;
  font-size: 12px;
  font-weight: 700;
  border-radius: 4px;
  margin-bottom: 8px;
}

.featured-overlay h3 {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--gold-light);
}

/* 小公告列表 */
.announcement-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(255,255,255,0.04);
  transition: var(--transition);
  cursor: pointer;
}

.announcement-item:hover {
  background: rgba(255,255,255,0.08);
}

.announcement-thumb {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.announcement-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.announcement-tag {
  align-self: flex-start;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 3px;
  font-weight: 500;
}

.announcement-tag.活动 { background: var(--primary); color: white; }
.announcement-tag.更新 { background: var(--accent); color: white; }
.announcement-tag.招募 { background: var(--gold); color: #141419; }

.announcement-body h4 {
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
  color: rgba(255,255,255,0.9);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-body time {
  font-size: 11px;
  color: rgba(255,255,255,0.45);
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  min-width: 0;
}

.content-header {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
  font-family: 'Noto Serif SC', serif;
}

.title-icon {
  width: 6px;
  height: 24px;
  background: var(--primary);
  border-radius: 3px;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tab {
  padding: 6px 16px;
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

.all-tab {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--text-muted);
}

.all-tab:hover {
  color: var(--primary);
  border-color: var(--primary);
}

.arrow {
  font-size: 12px;
}

.search-bar {
  display: flex;
  gap: 8px;
}

.search-bar input {
  width: 200px;
  padding: 8px 14px;
  border: 1px solid var(--border);
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: var(--transition);
}

.search-bar input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.search-submit {
  padding: 8px 18px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition);
}

.search-submit:hover {
  background: var(--primary-hover);
}

.loading-state {
  text-align: center;
  padding: 60px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

/* 漫画卡片网格 */
.comic-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.comic-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  cursor: pointer;
}

.comic-card:hover {
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

.comic-card:hover .cover-wrap img {
  transform: scale(1.05);
}

.category-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  background: var(--primary);
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
}

.comic-info {
  padding: 12px;
}

.comic-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comic-author {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 6px;
}

.comic-intro {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 响应式 */
@media (max-width: 1024px) {
  .home-layout {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    position: static;
  }
}

@media (max-width: 640px) {
  .comic-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  .search-bar input {
    flex: 1;
    width: auto;
  }
}

/* 公告详情弹窗 */
.notice-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.notice-modal {
  width: 520px;
  max-width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 26px 28px;
  box-shadow: var(--shadow-lg);
  animation: noticeIn 200ms ease;
}

@keyframes noticeIn {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notice-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.notice-modal-tag {
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #8a6d1a;
  background: rgba(212, 175, 55, 0.18);
}

.notice-close {
  border: none;
  background: none;
  font-size: 24px;
  line-height: 1;
  color: var(--text-muted);
  cursor: pointer;
}

.notice-close:hover {
  color: var(--primary);
}

.notice-modal-title {
  font-size: 21px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
  line-height: 1.4;
  margin-bottom: 8px;
}

.notice-modal-time {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 18px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}

.notice-modal-body {
  font-size: 15px;
  line-height: 1.9;
  color: var(--text-main);
  white-space: pre-wrap;
  margin-bottom: 22px;
}

.notice-modal-footer {
  display: flex;
  justify-content: flex-end;
}

.notice-btn {
  padding: 9px 24px;
  border: none;
  border-radius: 20px;
  background: var(--primary);
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition);
}

.notice-btn:hover {
  background: var(--primary-hover);
}
</style>
