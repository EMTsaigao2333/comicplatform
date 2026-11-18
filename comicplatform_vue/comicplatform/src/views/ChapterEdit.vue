<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { getContentUrl } from '@/utils/image.js'

const route = useRoute()
const router = useRouter()

const comicId = route.params.comicId
const chapterId = route.query.id
const isEdit = !!chapterId

const loading = ref(false)
const saving = ref(false)
const message = ref('')
const messageType = ref('')

const comics = ref([])

const form = ref({
  chapterid: null,
  comicid: Number(comicId) || null,
  chapterno: 1,
  chaptertitle: '',
  content: JSON.stringify([getContentUrl()]),
  status: '草稿',
})

const statusOptions = ['草稿', '发布']

// 解析内容页数组
const pages = computed({
  get() {
    try {
      const parsed = JSON.parse(form.value.content)
      return Array.isArray(parsed) ? parsed : [parsed]
    } catch {
      return []
    }
  },
  set(val) {
    form.value.content = JSON.stringify(val)
  }
})

// 预留接口：获取漫画列表（用于选择所属漫画）
const fetchComics = async () => {
  try {
    const res = await axios.get('/api/comic/page', {
      params: { pageNum: 1, pageSize: 100 }
    })
    const list = res.data.data?.records || res.data.data || []
    comics.value = list.map(item => ({
      comicid: item.comicId ?? item.comicid,
      comictitle: item.comicTitle ?? item.comictitle ?? '未命名'
    }))
  } catch (err) {
    console.error('获取漫画列表失败', err)
    comics.value = [
      { comicid: 1, comictitle: '航海王' },
      { comicid: 2, comictitle: '龙珠' },
      { comicid: 3, comictitle: '火影忍者' },
    ]
  }
}

// 预留接口：获取章节详情（编辑模式）
const fetchChapter = async () => {
  if (!isEdit) return

  loading.value = true
  try {
    const res = await axios.get(`/api/chapter/${chapterId}`)
    const data = res.data?.data
    if (data) {
      form.value = {
        chapterid: data.chapterId ?? data.chapterid,
        comicid: data.comicId ?? data.comicid,
        chapterno: data.chapterNo ?? data.chapterno ?? 1,
        chaptertitle: data.chapterTitle ?? data.chaptertitle ?? '',
        content: data.content || JSON.stringify([getContentUrl()]),
        status: data.status || '草稿',
      }
    } else {
      useMockData()
    }
  } catch (err) {
    console.error('获取章节详情失败', err)
    useMockData()
  } finally {
    loading.value = false
  }
}

// 假数据：编辑模式且接口未通时占位
const useMockData = () => {
  form.value = {
    chapterid: Number(chapterId),
    comicid: Number(comicId) || 1,
    chapterno: 2,
    chaptertitle: '第2话：伙伴',
    content: JSON.stringify([getContentUrl(), getContentUrl()]),
    status: '草稿',
  }
}

// 内容页操作
const addPage = () => {
  pages.value = [...pages.value, getContentUrl()]
}

const removePage = (index) => {
  const list = [...pages.value]
  list.splice(index, 1)
  if (list.length === 0) list.push(getContentUrl())
  pages.value = list
}

const replacePage = (index) => {
  message.value = '图片上传功能后续接入，现在先用占位图'
  messageType.value = 'info'
  const list = [...pages.value]
  list[index] = getContentUrl()
  pages.value = list
  setTimeout(() => { message.value = '' }, 2000)
}

// 表单验证
const validate = () => {
  if (!form.value.comicid) return '请选择所属漫画'
  if (!form.value.chaptertitle.trim()) return '请输入章节标题'
  if (pages.value.length === 0) return '请至少添加一页内容'
  return ''
}

// 预留接口：保存章节
const saveChapter = async () => {
  const error = validate()
  if (error) {
    message.value = error
    messageType.value = 'error'
    return
  }

  saving.value = true
  message.value = ''
  try {
    const payload = {
      comicid: Number(form.value.comicid),
      chapterno: Number(form.value.chapterno),
      chaptertitle: form.value.chaptertitle,
      content: form.value.content,
      status: form.value.status,
    }

    if (isEdit) {
      await axios.put(`/api/chapter/${chapterId}`, { ...payload, chapterid: Number(chapterId) })
    } else {
      await axios.post('/api/chapter', payload)
    }

    message.value = isEdit ? '修改成功' : '发布成功'
    messageType.value = 'success'
    setTimeout(() => {
      router.push(`/comic/${form.value.comicid}`)
    }, 800)
  } catch (err) {
    console.error('保存章节失败', err)
    // 接口未通时模拟成功，方便演示
    message.value = isEdit ? '修改成功（模拟）' : '发布成功（模拟）'
    messageType.value = 'success'
    setTimeout(() => {
      router.push(`/comic/${form.value.comicid}`)
    }, 800)
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  form.value = {
    chapterid: null,
    comicid: Number(comicId) || null,
    chapterno: 1,
    chaptertitle: '',
    content: JSON.stringify([getContentUrl()]),
    status: '草稿',
  }
  message.value = ''
}

onMounted(async () => {
  await fetchComics()
  await fetchChapter()
})
</script>

<template>
  <div class="chapter-edit-page">
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? '编辑章节' : '发布新章节' }}</h2>
      <p class="page-desc">{{ isEdit ? '修改章节内容和状态' : '为你的漫画添加新章节' }}</p>
    </div>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else class="edit-form">
      <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

      <div class="form-section">
        <div class="form-row">
          <label class="form-label">所属漫画 <span class="required">*</span></label>
          <select v-model="form.comicid" class="form-select">
            <option :value="null" disabled>请选择漫画</option>
            <option v-for="comic in comics" :key="comic.comicid" :value="comic.comicid">
              {{ comic.comictitle }}
            </option>
          </select>
        </div>

        <div class="form-row inline-row">
          <div class="inline-item">
            <label class="form-label">章节编号 <span class="required">*</span></label>
            <input v-model.number="form.chapterno" type="number" min="1" placeholder="如：1">
          </div>
          <div class="inline-item flex-1">
            <label class="form-label">章节标题 <span class="required">*</span></label>
            <input v-model="form.chaptertitle" type="text" placeholder="如：第1话：冒险的开始">
          </div>
        </div>

        <div class="form-row">
          <label class="form-label">发布状态</label>
          <div class="radio-group">
            <label v-for="s in statusOptions" :key="s" class="radio-item">
              <input v-model="form.status" type="radio" :value="s">
              <span>{{ s }}</span>
            </label>
          </div>
        </div>
      </div>

      <!-- 内容页编辑 -->
      <div class="content-section">
        <div class="content-header">
          <h3 class="section-title">章节内容</h3>
          <button type="button" class="btn btn-sm" @click="addPage">+ 添加一页</button>
        </div>

        <div class="page-list">
          <div v-for="(url, index) in pages" :key="index" class="page-editor">
            <div class="page-preview">
              <img :src="url" :alt="`第${index + 1}页`">
              <span class="page-index">第 {{ index + 1 }} 页</span>
            </div>
            <div class="page-actions">
              <button type="button" class="action-btn" @click="replacePage(index)">替换图片</button>
              <button type="button" class="action-btn danger" @click="removePage(index)">删除</button>
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" @click="resetForm">重置</button>
        <button type="button" class="btn btn-primary" :disabled="saving" @click="saveChapter">
          {{ saving ? '保存中...' : (isEdit ? '保存修改' : '发布章节') }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chapter-edit-page {
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

.loading-state {
  text-align: center;
  padding: 80px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}

.edit-form {
  background: var(--bg-card);
  border-radius: var(--radius);
  padding: 28px;
  box-shadow: var(--shadow-sm);
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

.form-message.info {
  background: #dbeafe;
  color: #1e40af;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 28px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row.inline-row {
  flex-direction: row;
  gap: 16px;
}

.inline-item {
  width: 160px;
}

.inline-item.flex-1 {
  flex: 1;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.required {
  color: var(--primary);
}

input[type="text"],
input[type="number"],
.form-select {
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: var(--transition);
  font-family: inherit;
  background: white;
}

input[type="text"]:focus,
input[type="number"]:focus,
.form-select:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.form-select {
  cursor: pointer;
}

/* 单选组 */
.radio-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  cursor: pointer;
}

.radio-item input {
  width: 16px;
  height: 16px;
  accent-color: var(--primary);
}

/* 内容区 */
.content-section {
  margin-bottom: 28px;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
}

.btn-sm {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: var(--accent);
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
}

.btn-sm:hover {
  background: #1d4ed8;
}

.page-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.page-editor {
  background: #f9fafb;
  border-radius: var(--radius);
  padding: 12px;
  border: 1px solid var(--border);
}

.page-preview {
  position: relative;
  aspect-ratio: 3 / 4;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  margin-bottom: 12px;
}

.page-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.page-index {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 8px;
  background: rgba(0,0,0,0.6);
  color: white;
  font-size: 12px;
  border-radius: 10px;
}

.page-actions {
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
  border-color: var(--primary);
  color: var(--primary);
}

.action-btn.danger:hover {
  border-color: var(--primary);
  color: var(--primary);
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.btn {
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 15px;
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

.btn-secondary {
  background: white;
  border: 1px solid var(--border);
  color: var(--text-main);
}

.btn-secondary:hover {
  border-color: var(--primary);
  color: var(--primary);
}

/* 响应式 */
@media (max-width: 640px) {
  .form-row.inline-row {
    flex-direction: column;
  }

  .inline-item {
    width: 100%;
  }

  .page-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
