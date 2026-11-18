<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const comments = ref([])
const loading = ref(false)
const message = ref('')
const messageType = ref('')
const keyword = ref('')

const showForm = ref(false)
const editingId = ref(null)
const form = ref({ content: '', rating: 5, status: '正常' })

const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2500)
}

// 查询全部评论
const fetchComments = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/comment')
    comments.value = res.data.data || []
  } catch (err) {
    console.error('查询评论失败', err)
    showMessage('查询评论失败，请检查后端服务', 'error')
  } finally {
    loading.value = false
  }
}

// 前端按评论内容/用户名过滤
const filtered = () => {
  const k = keyword.value.trim()
  if (!k) return comments.value
  return comments.value.filter(c =>
    (c.content || '').includes(k) || (c.username || '').includes(k)
  )
}

const openEdit = (item) => {
  editingId.value = item.commentid
  form.value = { content: item.content, rating: item.rating, status: item.status }
  showForm.value = true
}

const save = async () => {
  if (!form.value.content.trim()) {
    showMessage('评论内容不能为空', 'error')
    return
  }
  try {
    await axios.put(`/api/comment/${editingId.value}`, form.value)
    showMessage('修改成功')
    showForm.value = false
    fetchComments()
  } catch (err) {
    console.error('修改评论失败', err)
    showMessage('修改失败，请重试', 'error')
  }
}

// 屏蔽/恢复评论
const toggleStatus = async (item) => {
  const newStatus = item.status === '正常' ? '已屏蔽' : '正常'
  try {
    await axios.put(`/api/comment/${item.commentid}/status`, { status: newStatus })
    showMessage(newStatus === '正常' ? '已恢复显示' : '已屏蔽该评论')
    fetchComments()
  } catch (err) {
    console.error('屏蔽评论失败', err)
    showMessage('操作失败，请重试', 'error')
  }
}

const remove = async (item) => {
  if (!confirm('确定删除这条评论吗？')) return
  try {
    await axios.delete(`/api/comment/${item.commentid}`)
    showMessage('删除成功')
    fetchComments()
  } catch (err) {
    console.error('删除评论失败', err)
    showMessage('删除失败，请重试', 'error')
  }
}

const formatTime = (t) => (t ? t.replace('T', ' ').slice(0, 16) : '—')

onMounted(fetchComments)
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">评论管理</h2>
        <p class="page-desc">审核漫画评论内容，可屏蔽违规评论或删除评论</p>
      </div>
      <div class="search-box-light">
        <input v-model="keyword" type="text" placeholder="搜索评论内容或用户名">
      </div>
    </div>

    <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="filtered().length === 0" class="empty-state">暂无评论数据</div>
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th style="width: 80px">漫画ID</th>
            <th style="width: 110px">评论用户</th>
            <th>评论内容</th>
            <th style="width: 90px">评分</th>
            <th style="width: 140px">评论时间</th>
            <th style="width: 90px">状态</th>
            <th style="width: 190px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filtered()" :key="item.commentid">
            <td>{{ item.commentid }}</td>
            <td>{{ item.comicid }}</td>
            <td class="strong">{{ item.username || '匿名' }}</td>
            <td class="clamp">{{ item.content }}</td>
            <td class="rating">{{ item.rating }} 分</td>
            <td class="muted">{{ formatTime(item.createtime) }}</td>
            <td>
              <span :class="['badge', item.status === '正常' ? 'badge-success' : 'badge-danger']">
                {{ item.status }}
              </span>
            </td>
            <td>
              <button class="action-btn" @click="openEdit(item)">编辑</button>
              <button class="action-btn" @click="toggleStatus(item)">
                {{ item.status === '正常' ? '屏蔽' : '恢复' }}
              </button>
              <button class="action-btn danger" @click="remove(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="modal-mask" @click.self="showForm = false">
      <div class="modal">
        <h3 class="modal-title">编辑评论</h3>
        <div class="form-item">
          <label>评论内容</label>
          <textarea v-model="form.content" rows="4"></textarea>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label>评分（1-5 分）</label>
            <input v-model.number="form.rating" type="number" min="1" max="5">
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
              <option value="正常">正常</option>
              <option value="已屏蔽">已屏蔽</option>
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
.search-box-light input {
  width: 260px;
  padding: 9px 14px;
  border: 1px solid var(--border);
  border-radius: 20px;
  font-size: 14px;
  outline: none;
}
.search-box-light input:focus { border-color: var(--accent); }
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
.clamp { max-width: 360px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rating { color: var(--gold); font-weight: 600; }
.badge { padding: 3px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.badge-success { background: rgba(7,193,96,0.1); color: #07c160; }
.badge-danger { background: rgba(225,29,72,0.1); color: var(--primary); }
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
.modal { width: 520px; background: white; border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow-lg); }
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
.form-row { display: flex; gap: 14px; }
.form-row .form-item { flex: 1; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }
.btn-ghost-dark { background: white; color: var(--text-main); border: 1px solid var(--border); }
</style>
