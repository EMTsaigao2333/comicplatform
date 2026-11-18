<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'
import { getCoverUrl } from '@/utils/image.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const comicId = route.query.id
const isEdit = !!comicId

const loading = ref(false)
const saving = ref(false)
const message = ref('')
const messageType = ref('')

const form = ref({
  comicid: null,
  comictitle: '',
  author: '',
  category: '',
  country: '日本',
  keywords: '',
  intro: '',
  coverimg: '',
})

const categories = ['热血', '冒险', '恋爱', '奇幻', '悬疑', '武侠仙侠', '都市', '科幻', '历史', '其他']
const countries = ['日本', '大陆', '韩国', '其他']

// 预留接口：获取漫画详情（编辑模式）
const fetchComic = async () => {
  if (!isEdit) {
    form.value.author = userStore.user?.username || ''
    return
  }

  loading.value = true
  try {
    const res = await axios.get(`/api/comic/${comicId}`)
    const data = res.data?.data
    if (data) {
      form.value = {
        comicid: data.comicId ?? data.comicid,
        comictitle: data.comicTitle ?? data.comictitle ?? '',
        author: data.author ?? '',
        category: data.category ?? '',
        country: data.country ?? '日本',
        keywords: data.keywords ?? '',
        intro: data.intro ?? '',
        coverimg: data.coverImg ?? data.coverimg ?? '',
      }
    } else {
      useMockData()
    }
  } catch (err) {
    console.error('获取漫画详情失败', err)
    useMockData()
  } finally {
    loading.value = false
  }
}

// 假数据：编辑模式且接口未通时占位
const useMockData = () => {
  form.value = {
    comicid: Number(comicId),
    comictitle: '航海王',
    author: userStore.user?.username || '尾田荣一郎',
    category: '冒险',
    country: '日本',
    keywords: '热血;冒险;海贼',
    intro: '少年路飞立志成为海贼王的冒险故事',
    coverimg: getCoverUrl(),
  }
}

// 图片上传占位：现在只做选择提示，后续接入真实上传
const onCoverChange = () => {
  message.value = '图片上传功能后续接入，现在先用占位图'
  messageType.value = 'info'
  form.value.coverimg = getCoverUrl()
  setTimeout(() => { message.value = '' }, 2000)
}

// 表单验证
const validate = () => {
  if (!form.value.comictitle.trim()) return '请输入漫画标题'
  if (!form.value.author.trim()) return '请输入作者'
  if (!form.value.category) return '请选择分类'
  if (!form.value.intro.trim()) return '请输入作品简介'
  return ''
}

// 预留接口：保存漫画
const saveComic = async () => {
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
      comicTitle: form.value.comictitle,
      author: form.value.author,
      category: form.value.category,
      country: form.value.country,
      keywords: form.value.keywords,
      intro: form.value.intro,
      coverImg: form.value.coverimg || getCoverUrl(),
    }

    if (isEdit) {
      await axios.put(`/api/comic/${comicId}`, { ...payload, comicId: Number(comicId) })
    } else {
      await axios.post('/api/comic', payload)
    }

    message.value = isEdit ? '修改成功' : '发布成功'
    messageType.value = 'success'
    setTimeout(() => {
      router.push('/')
    }, 800)
  } catch (err) {
    console.error('保存漫画失败', err)
    // 接口未通时模拟成功，方便演示
    message.value = isEdit ? '修改成功（模拟）' : '发布成功（模拟）'
    messageType.value = 'success'
    setTimeout(() => {
      router.push('/')
    }, 800)
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  form.value = {
    comicid: null,
    comictitle: '',
    author: userStore.user?.username || '',
    category: '',
    country: '日本',
    keywords: '',
    intro: '',
    coverimg: '',
  }
  message.value = ''
}

onMounted(fetchComic)
</script>

<template>
  <div class="comic-edit-page">
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? '编辑漫画' : '发布新漫画' }}</h2>
      <p class="page-desc">{{ isEdit ? '修改漫画基本信息' : '填写信息，发布你的原创漫画' }}</p>
    </div>

    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else class="edit-form">
      <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

      <div class="form-grid">
        <!-- 左侧封面 -->
        <div class="cover-section">
          <div class="cover-preview">
            <img :src="form.coverimg || getCoverUrl()" alt="漫画封面">
          </div>
          <button type="button" class="upload-btn" @click="onCoverChange">
            {{ form.coverimg ? '更换封面' : '上传封面' }}
          </button>
          <p class="upload-tip">推荐 300 x 400，后续接入真实上传</p>
        </div>

        <!-- 右侧表单 -->
        <div class="form-section">
          <div class="form-row">
            <label class="form-label">漫画标题 <span class="required">*</span></label>
            <input v-model="form.comictitle" type="text" placeholder="请输入漫画标题">
          </div>

          <div class="form-row">
            <label class="form-label">作者 <span class="required">*</span></label>
            <input v-model="form.author" type="text" placeholder="请输入作者笔名">
          </div>

          <div class="form-row">
            <label class="form-label">分类 <span class="required">*</span></label>
            <div class="category-options">
              <button
                v-for="cat in categories"
                :key="cat"
                :class="['category-btn', { active: form.category === cat }]"
                @click="form.category = cat"
              >
                {{ cat }}
              </button>
            </div>
          </div>

          <div class="form-row">
            <label class="form-label">地区</label>
            <div class="radio-group">
              <label v-for="c in countries" :key="c" class="radio-item">
                <input v-model="form.country" type="radio" :value="c">
                <span>{{ c }}</span>
              </label>
            </div>
          </div>

          <div class="form-row">
            <label class="form-label">关键词</label>
            <input v-model="form.keywords" type="text" placeholder="用分号分隔，如：热血;冒险;海贼">
          </div>

          <div class="form-row">
            <label class="form-label">作品简介 <span class="required">*</span></label>
            <textarea v-model="form.intro" rows="5" placeholder="介绍一下你的作品..."></textarea>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" @click="resetForm">重置</button>
        <button type="button" class="btn btn-primary" :disabled="saving" @click="saveComic">
          {{ saving ? '保存中...' : (isEdit ? '保存修改' : '立即发布') }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comic-edit-page {
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

.form-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 32px;
  margin-bottom: 28px;
}

/* 封面区 */
.cover-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cover-preview {
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: var(--radius);
  overflow: hidden;
  background: #f3f4f6;
  box-shadow: var(--shadow-md);
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-btn {
  width: 100%;
  padding: 12px;
  border: 1px dashed var(--border);
  background: white;
  border-radius: 8px;
  color: var(--text-main);
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition);
}

.upload-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.upload-tip {
  font-size: 12px;
  color: var(--text-muted);
  text-align: center;
}

/* 表单区 */
.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
textarea {
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: var(--transition);
  font-family: inherit;
}

input[type="text"]:focus,
textarea:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

/* 分类选项 */
.category-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-btn {
  padding: 8px 18px;
  border: 1px solid var(--border);
  background: white;
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-main);
  cursor: pointer;
  transition: var(--transition);
}

.category-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.category-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: white;
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
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .cover-section {
    max-width: 260px;
    margin: 0 auto;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
