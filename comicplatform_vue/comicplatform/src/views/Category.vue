<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const comics = ref([])
const loading = ref(true)

const imgUrl = (prompt, size) => {
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`
}

const coverImages = [
  imgUrl('One Piece anime manga cover, pirate ship on ocean, straw hat, vibrant colors, adventure shonen theme, professional comic book cover art', 'portrait_4_3'),
  imgUrl('Dragon Ball anime manga cover, young Goku flying on nimbus cloud, seven dragon balls, orange gi, blue sky, classic shonen manga style', 'portrait_4_3'),
  imgUrl('Naruto style anime manga cover, young ninja with headband, action pose, dramatic orange and black colors, professional shonen illustration', 'portrait_4_3'),
  imgUrl('fantasy anime manga cover, young hero with glowing sword, magical dragon, epic battle scene, blue and gold colors, professional comic art', 'portrait_4_3'),
  imgUrl('romance anime manga cover, cherry blossom petals, school uniform couple, soft pink and white colors, shojo manga style', 'portrait_4_3'),
  imgUrl('horror anime manga cover, dark mysterious figure, red moon, gothic atmosphere, psychological thriller comic art', 'portrait_4_3'),
  imgUrl('wuxia xianxia chinese fantasy manga cover, swordsman in ancient costume, misty mountains, ink wash style, professional comic art', 'portrait_4_3'),
  imgUrl('urban modern anime manga cover, city skyline at night, young protagonist, neon lights, contemporary drama style', 'portrait_4_3'),
]

const mockComics = [
  { comicid: 1, comictitle: '航海王', author: '尾田荣一郎', category: '冒险', region: '日本', status: '连载', coverimg: coverImages[0], intro: '少年路飞立志成为海贼王的冒险故事', updateTo: '第1088话' },
  { comicid: 2, comictitle: '龙珠', author: '鸟山明', category: '热血', region: '日本', status: '完结', coverimg: coverImages[1], intro: '孙悟空寻找龙珠、保卫地球的经典热血漫画', updateTo: '全42卷' },
  { comicid: 3, comictitle: '火影忍者', author: '岸本齐史', category: '热血', region: '日本', status: '完结', coverimg: coverImages[2], intro: '漩涡鸣人成为火影的忍者成长之路', updateTo: '全72卷' },
  { comicid: 4, comictitle: '光明之刃', author: '林一', category: '奇幻', region: '大陆', status: '连载', coverimg: coverImages[3], intro: '少年持剑觉醒，与龙族缔结契约拯救世界', updateTo: '第127话' },
  { comicid: 5, comictitle: '樱花下的约定', author: '苏浅', category: '恋爱', region: '大陆', status: '连载', coverimg: coverImages[4], intro: '校园里的青涩恋爱与樱花树下的约定', updateTo: '第45话' },
  { comicid: 6, comictitle: '暗月', author: '墨染', category: '悬疑', region: '大陆', status: '连载', coverimg: coverImages[5], intro: '红月之夜接连发生的离奇失踪事件', updateTo: '第88话' },
  { comicid: 7, comictitle: '剑仙传说', author: '青云', category: '武侠仙侠', region: '大陆', status: '连载', coverimg: coverImages[6], intro: '凡人少年踏入修仙之路，一剑开天门', updateTo: '第256话' },
  { comicid: 8, comictitle: '霓虹都市', author: '夜航', category: '都市', region: '韩国', status: '完结', coverimg: coverImages[7], intro: '在繁华都市中追寻真相的年轻侦探', updateTo: '全36话' },
]

const filterGroups = {
  theme: { label: '题材', options: ['全部', '热血', '古风', '玄幻', '奇幻', '悬疑', '都市', '历史', '武侠仙侠', '游戏竞技', '恋爱', '科幻'] },
  region: { label: '地区', options: ['全部', '大陆', '日本', '韩国', '其他'] },
  status: { label: '进度', options: ['全部', '连载', '完结'] },
  price: { label: '收费', options: ['全部', '免费', '付费'] },
}

const sortOptions = ['人气推荐', '更新时间', '上架时间']

const activeFilters = ref({
  theme: '全部',
  region: '全部',
  status: '全部',
  price: '全部',
  sort: '人气推荐',
})

const setFilter = (key, value) => {
  activeFilters.value[key] = value
  fetchComics()
}

const fetchComics = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/comic/page', {
      params: {
        pageNum: 1,
        pageSize: 24,
        category: activeFilters.value.theme === '全部' ? '' : activeFilters.value.theme,
      }
    })
    const list = res.data.data?.records || res.data.data || []
    if (list.length > 0) {
      comics.value = list.map((item, index) => ({
        comicid: item.comicId ?? item.comicid,
        comictitle: item.comicTitle ?? item.comictitle,
        author: item.author,
        category: item.category,
        intro: item.intro,
        coverimg: item.coverImg || item.coverimg || coverImages[index % coverImages.length],
        updateTo: item.updateTo || `更新至第${Math.floor(Math.random() * 200) + 1}话`
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

onMounted(fetchComics)
</script>

<template>
  <div class="category-page">
    <!-- 顶部深色快捷导航栏（参考第四张图） -->
    <div class="top-nav-bar">
      <button
        v-for="theme in filterGroups.theme.options.slice(1, 15)"
        :key="theme"
        :class="['top-nav-item', { active: activeFilters.theme === theme }]"
        @click="setFilter('theme', theme)"
      >
        {{ theme }}
      </button>
      <button class="top-nav-item all-link" @click="setFilter('theme', '全部')">
        全部 <span class="arrow">&gt;</span>
      </button>
    </div>

    <!-- 筛选面板（参考第二张图） -->
    <div class="filter-panel">
      <div v-for="(group, key) in filterGroups" :key="key" class="filter-row">
        <span class="filter-label">{{ group.label }}</span>
        <div class="filter-options">
          <button
            v-for="opt in group.options"
            :key="opt"
            :class="['filter-option', { active: activeFilters[key] === opt }]"
            @click="setFilter(key, opt)"
          >
            {{ opt }}
          </button>
        </div>
      </div>

      <div class="filter-row sort-row">
        <span class="filter-label">排序</span>
        <div class="filter-options">
          <button
            v-for="opt in sortOptions"
            :key="opt"
            :class="['filter-option', { active: activeFilters.sort === opt }]"
            @click="setFilter('sort', opt)"
          >
            {{ opt }}
          </button>
        </div>
      </div>
    </div>

    <!-- 漫画列表 -->
    <div class="result-header">
      <h2 class="result-title">全部漫画</h2>
      <span class="result-count">共 {{ comics.length }} 部</span>
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
          <p class="comic-meta">{{ comic.author }} · {{ comic.region }}</p>
          <p class="comic-update">{{ comic.updateTo }}</p>
        </div>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.category-page {
  padding-bottom: 40px;
}

/* 顶部深色快捷导航栏 */
.top-nav-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #1a1a20;
  border-radius: var(--radius);
  padding: 14px 20px;
  margin-bottom: 20px;
  overflow-x: auto;
  scrollbar-width: none;
}

.top-nav-bar::-webkit-scrollbar {
  display: none;
}

.top-nav-item {
  flex-shrink: 0;
  padding: 8px 16px;
  background: transparent;
  border: none;
  color: rgba(255,255,255,0.75);
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: var(--transition);
}

.top-nav-item:hover {
  color: white;
  background: rgba(255,255,255,0.08);
}

.top-nav-item.active {
  color: white;
  background: var(--primary);
}

.all-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  color: rgba(255,255,255,0.6);
}

.all-link:hover {
  color: var(--gold-light);
}

.arrow {
  font-size: 12px;
}

/* 筛选面板 */
.filter-panel {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}

.filter-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.filter-row:last-child {
  border-bottom: none;
}

.filter-label {
  width: 48px;
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
  padding-top: 6px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-option {
  padding: 6px 14px;
  background: transparent;
  border: none;
  border-radius: 16px;
  font-size: 14px;
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition);
}

.filter-option:hover {
  color: var(--primary);
}

.filter-option.active {
  background: rgba(225, 29, 72, 0.1);
  color: var(--primary);
  font-weight: 500;
}

.sort-row .filter-option.active {
  background: var(--accent);
  color: white;
}

/* 结果区 */
.result-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16px;
}

.result-title {
  font-size: 20px;
  font-weight: 700;
  font-family: 'Noto Serif SC', serif;
}

.result-count {
  font-size: 13px;
  color: var(--text-muted);
}

.loading-state {
  text-align: center;
  padding: 60px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

/* 漫画网格 */
.comic-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
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
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comic-meta {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 6px;
}

.comic-update {
  font-size: 12px;
  color: var(--accent);
  font-weight: 500;
}

@media (max-width: 640px) {
  .comic-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .filter-row {
    flex-direction: column;
    gap: 10px;
  }
  .filter-label {
    padding-top: 0;
  }
}
</style>
