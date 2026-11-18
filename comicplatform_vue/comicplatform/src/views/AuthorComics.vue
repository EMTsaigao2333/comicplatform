<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()

const comics = ref([])
const loading = ref(true)
const message = ref('')
const messageType = ref('')

const fetchAuthorComics = async () => {
  loading.value = true
  try {
    const author = userStore.user.username
    const res = await axios.get('/api/comic/author', { params: { author } })
    const list = res.data.data || []
    comics.value = list.map(item => ({
      comicid: item.comicId ?? item.comicid,
      comictitle: item.comicTitle ?? item.comictitle ?? '未命名',
      author: item.author,
      category: item.category,
      status: item.status ?? '已发布',
      coverimg: item.coverImg ?? item.coverimg ?? '/images/cover-placeholder.svg',
      createtime: item.createtime,
    }))
  } catch (err) {
    console.error('获取作者漫画失败', err)
  } finally {
    loading.value = false
  }
}

const editComic = (comic) => {
  router.push(`/author/comic/edit?id=${comic.comicid}`)
}

const editChapters = (comic) => {
  router.push(`/author/chapter/edit/${comic.comicid}`)
}

const deleteComic = async (comic) => {
  if (!confirm(`确定删除《${comic.comictitle}》吗？此操作不可撤销。`)) return
  try {
    await axios.delete(`/api/comic/${comic.comicid}`)
    comics.value = comics.value.filter(c => c.comicid !== comic.comicid)
    showMessage(`已删除《${comic.comictitle}》`, 'success')
  } catch (err) {
    console.error('删除失败', err)
    showMessage('删除失败，请重试', 'error')
  }
}

const showMessage = (msg, type) => {
  message.value = msg
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2000)
}

onMounted(fetchAuthorComics)
</script>

<template>
  <div class="author-comics-page">
    <div class="page-header">
      <h2 class="page-title">我的作品</h2>
      <p class="page-desc">管理你发布的所有漫画</p>
    </div>

    <div v-if="loading" class="empty-state">加载中...</div>

    <div v-else-if="comics.length === 0" class="empty-state">
      <p>你还没有发布漫画，去<a href="javascript:void(0)" @click="router.push('/author/comic/edit')">创作</a>吧</p>
    </div>

    <div v-else class="comic-table-wrap">
      <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

      <table class="comic-table">
        <thead>
          <tr>
            <th>漫画</th>
            <th>分类</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comic in comics" :key="comic.comicid">
            <td>
              <div class="comic-cell">
                <img :src="comic.coverimg" class="thumb">
                <span class="comic-name">{{ comic.comictitle }}</span>
              </div>
            </td>
            <td>{{ comic.category }}</td>
            <td>{{ comic.status }}</td>
            <td>{{ comic.createtime ? comic.createtime.replace('T', ' ').slice(0, 16) : '' }}</td>
            <td>
              <div class="action-btns">
                <button class="act-btn edit" @click="editComic(comic)">编辑信息</button>
                <button class="act-btn chapter" @click="editChapters(comic)">编辑章节</button>
                <button class="act-btn delete" @click="deleteComic(comic)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.author-comics-page {
  padding-bottom: 40px;
}

.page-header {
  margin-bottom: 24px;
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

.empty-state {
  text-align: center;
  padding: 80px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

.empty-state a {
  color: var(--accent);
  font-weight: 500;
}

.form-message {
  padding: 10px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
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

.comic-table-wrap {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.comic-table {
  width: 100%;
  border-collapse: collapse;
}

.comic-table th {
  text-align: left;
  padding: 12px 16px;
  font-size: 13px;
  color: var(--text-muted);
  border-bottom: 1px solid var(--border);
  font-weight: 600;
}

.comic-table td {
  padding: 14px 16px;
  font-size: 14px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.comic-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.thumb {
  width: 48px;
  height: 64px;
  border-radius: 6px;
  object-fit: cover;
}

.comic-name {
  font-weight: 500;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.act-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
}

.act-btn.edit {
  background: var(--accent);
  color: white;
}

.act-btn.edit:hover {
  background: #1d4ed8;
}

.act-btn.chapter {
  background: #10b981;
  color: white;
}

.act-btn.chapter:hover {
  background: #059669;
}

.act-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.act-btn.delete:hover {
  background: #fecaca;
}

@media (max-width: 768px) {
  .comic-table-wrap {
    padding: 12px;
  }
  .comic-table th:nth-child(3),
  .comic-table td:nth-child(3),
  .comic-table th:nth-child(4),
  .comic-table td:nth-child(4) {
    display: none;
  }
  .action-btns {
    flex-direction: column;
  }
}
</style>
