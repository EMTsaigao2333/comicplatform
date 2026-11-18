<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()

const notices = ref([])
const loading = ref(false)
const message = ref('')
const messageType = ref('')

const showForm = ref(false)
const editingId = ref(null)
const form = ref({ title: '', content: '', noticetype: '公告', publisher: '', status: '已发布' })

const types = ['公告', '活动', '更新', '招募']

const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2500)
}

// 查询公告列表（管理员看全部）
const fetchNotices = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/notice')
    notices.value = res.data.data || []
  } catch (err) {
    console.error('查询公告失败', err)
    showMessage('查询公告失败，请检查后端服务', 'error')
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  editingId.value = null
  form.value = {
    title: '',
    content: '',
    noticetype: '公告',
    publisher: userStore.user?.username || '管理员',
    status: '已发布'
  }
  showForm.value = true
}

const openEdit = (item) => {
  editingId.value = item.noticeid
  form.value = {
    title: item.title,
    content: item.content,
    noticetype: item.noticetype,
    publisher: item.publisher,
    status: item.status
  }
  showForm.value = true
}

const save = async () => {
  if (!form.value.title.trim()) {
    showMessage('公告标题不能为空', 'error')
    return
  }
  try {
    if (editingId.value) {
      await axios.put(`/api/notice/${editingId.value}`, form.value)
      showMessage('修改成功')
    } else {
      await axios.post('/api/notice', form.value)
      showMessage('发布成功')
    }
    showForm.value = false
    fetchNotices()
  } catch (err) {
    console.error('保存公告失败', err)
    showMessage('保存失败，请重试', 'error')
  }
}

const remove = async (item) => {
  if (!confirm(`确定删除公告「${item.title}」吗？`)) return
  try {
    await axios.delete(`/api/notice/${item.noticeid}`)
    showMessage('删除成功')
    fetchNotices()
  } catch (err) {
    console.error('删除公告失败', err)
    showMessage('删除失败，请重试', 'error')
  }
}

// 上线/下线切换
const toggleStatus = async (item) => {
  const newStatus = item.status === '已发布' ? '已下线' : '已发布'
  try {
    await axios.put(`/api/notice/${item.noticeid}`, { ...item, status: newStatus })
    showMessage(newStatus === '已发布' ? '已上线' : '已下线')
    fetchNotices()
  } catch (err) {
    console.error('切换公告状态失败', err)
    showMessage('操作失败，请重试', 'error')
  }
}

const formatTime = (t) => (t ? t.replace('T', ' ').slice(0, 16) : '—')

onMounted(fetchNotices)
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">公告管理</h2>
        <p class="page-desc">维护首页公告栏内容，已发布的公告会显示在平台首页</p>
      </div>
      <button class="btn btn-primary" @click="openAdd">+ 发布公告</button>
    </div>

    <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="notices.length === 0" class="empty-state">暂无公告数据</div>
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th style="width: 260px">公告标题</th>
            <th style="width: 90px">类型</th>
            <th>内容摘要</th>
            <th style="width: 90px">发布人</th>
            <th style="width: 140px">发布时间</th>
            <th style="width: 90px">状态</th>
            <th style="width: 190px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in notices" :key="item.noticeid">
            <td>{{ item.noticeid }}</td>
            <td class="strong">{{ item.title }}</td>
            <td><span class="badge badge-type">{{ item.noticetype }}</span></td>
            <td class="muted clamp">{{ item.content || '—' }}</td>
            <td>{{ item.publisher || '—' }}</td>
            <td class="muted">{{ formatTime(item.publishdate) }}</td>
            <td>
              <span :class="['badge', item.status === '已发布' ? 'badge-success' : 'badge-muted']">
                {{ item.status }}
              </span>
            </td>
            <td>
              <button class="action-btn" @click="openEdit(item)">编辑</button>
              <button class="action-btn" @click="toggleStatus(item)">
                {{ item.status === '已发布' ? '下线' : '上线' }}
              </button>
              <button class="action-btn danger" @click="remove(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="modal-mask" @click.self="showForm = false">
      <div class="modal">
        <h3 class="modal-title">{{ editingId ? '编辑公告' : '发布公告' }}</h3>
        <div class="form-item">
          <label>公告标题</label>
          <input v-model="form.title" type="text" placeholder="如：暑期漫画狂欢节开启">
        </div>
        <div class="form-item">
          <label>公告内容</label>
          <textarea v-model="form.content" rows="4" placeholder="填写公告正文内容"></textarea>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label>公告类型</label>
            <select v-model="form.noticetype">
              <option v-for="t in types" :key="t" :value="t">{{ t }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>发布人</label>
            <input v-model="form.publisher" type="text" placeholder="管理员">
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
              <option value="已发布">已发布</option>
              <option value="已下线">已下线</option>
            </select>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-ghost-dark" @click="showForm = false">取消</button>
          <button class="btn btn-primary" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.manage-page { padding-bottom: 40px; }
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title { font-size: 22px; font-weight: 700; font-family: 'Noto Serif SC', serif; margin-bottom: 6px; }
.page-desc { font-size: 14px; color: var(--text-muted); }
.form-message { padding: 10px 16px; border-radius: 8px; font-size: 14px; margin-bottom: 16px; }
.form-message.success { background: rgba(7,193,96,0.1); color: #07c160; }
.form-message.error { background: rgba(225,29,72,0.1); color: var(--primary); }
.table-card { background: var(--bg-card); border-radius: var(--radius); box-shadow: var(--shadow-sm); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th {
  text-align: left;
  padding: 14px 16px;
  background: #fafafa;
  color: var(--text-muted);
  font-weight: 600;
  border-bottom: 1px solid var(--border);
}
.data-table td { padding: 14px 16px; border-bottom: 1px solid #f3f4f6; vertical-align: middle; }
.data-table tr:last-child td { border-bottom: none; }
.strong { font-weight: 600; }
.muted { color: var(--text-muted); }
.clamp { max-width: 320px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.badge { padding: 3px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.badge-success { background: rgba(7,193,96,0.1); color: #07c160; }
.badge-muted { background: rgba(107,114,128,0.12); color: var(--text-muted); }
.badge-type { background: rgba(37,99,235,0.1); color: var(--accent); }
.action-btn {
  padding: 5px 10px;
  margin-right: 6px;
  border: 1px solid var(--border);
  background: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
}
.action-btn:hover { border-color: var(--accent); color: var(--accent); }
.action-btn.danger:hover { border-color: var(--primary); color: var(--primary); }
.loading-state, .empty-state {
  text-align: center;
  padding: 60px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: var(--radius);
}
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal { width: 560px; background: white; border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow-lg); }
.modal-title { font-size: 18px; font-weight: 700; margin-bottom: 18px; font-family: 'Noto Serif SC', serif; }
.form-item { margin-bottom: 14px; }
.form-item label { display: block; font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
.form-item input, .form-item select, .form-item textarea {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  outline: none;
}
.form-item input:focus, .form-item select:focus, .form-item textarea:focus { border-color: var(--accent); }
.form-row { display: flex; gap: 14px; }
.form-row .form-item { flex: 1; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }
.btn-ghost-dark { background: white; color: var(--text-main); border: 1px solid var(--border); }
</style>
