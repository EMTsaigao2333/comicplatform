<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const categories = ref([])
const loading = ref(false)
const message = ref('')
const messageType = ref('')

const showForm = ref(false)
const editingId = ref(null)
const form = ref({ categoryname: '', description: '', sortorder: 1, status: '启用' })

const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2500)
}

// 查询分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/category')
    categories.value = res.data.data || []
  } catch (err) {
    console.error('查询分类失败', err)
    showMessage('查询分类失败，请检查后端服务', 'error')
  } finally {
    loading.value = false
  }
}

// 打开新增表单
const openAdd = () => {
  editingId.value = null
  form.value = { categoryname: '', description: '', sortorder: categories.value.length + 1, status: '启用' }
  showForm.value = true
}

// 打开编辑表单
const openEdit = (item) => {
  editingId.value = item.categoryid
  form.value = {
    categoryname: item.categoryname,
    description: item.description,
    sortorder: item.sortorder,
    status: item.status
  }
  showForm.value = true
}

// 保存（新增或修改）
const save = async () => {
  if (!form.value.categoryname.trim()) {
    showMessage('分类名称不能为空', 'error')
    return
  }
  try {
    if (editingId.value) {
      await axios.put(`/api/category/${editingId.value}`, form.value)
      showMessage('修改成功')
    } else {
      await axios.post('/api/category', form.value)
      showMessage('新增成功')
    }
    showForm.value = false
    fetchCategories()
  } catch (err) {
    console.error('保存分类失败', err)
    showMessage('保存失败，请重试', 'error')
  }
}

// 删除分类
const remove = async (item) => {
  if (!confirm(`确定删除分类「${item.categoryname}」吗？`)) return
  try {
    await axios.delete(`/api/category/${item.categoryid}`)
    showMessage('删除成功')
    fetchCategories()
  } catch (err) {
    console.error('删除分类失败', err)
    showMessage('删除失败，请重试', 'error')
  }
}

onMounted(fetchCategories)
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">分类管理</h2>
        <p class="page-desc">维护平台漫画题材分类，供首页筛选与作者发布漫画时使用</p>
      </div>
      <button class="btn btn-primary" @click="openAdd">+ 新增分类</button>
    </div>

    <div v-if="message" class="form-message" :class="messageType">{{ message }}</div>

    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="categories.length === 0" class="empty-state">暂无分类数据</div>
    <div v-else class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 70px">ID</th>
            <th style="width: 140px">分类名称</th>
            <th>分类描述</th>
            <th style="width: 90px">排序号</th>
            <th style="width: 100px">状态</th>
            <th style="width: 160px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in categories" :key="item.categoryid">
            <td>{{ item.categoryid }}</td>
            <td class="strong">{{ item.categoryname }}</td>
            <td class="muted">{{ item.description || '—' }}</td>
            <td>{{ item.sortorder }}</td>
            <td>
              <span :class="['badge', item.status === '启用' ? 'badge-success' : 'badge-muted']">
                {{ item.status }}
              </span>
            </td>
            <td>
              <button class="action-btn" @click="openEdit(item)">编辑</button>
              <button class="action-btn danger" @click="remove(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showForm" class="modal-mask" @click.self="showForm = false">
      <div class="modal">
        <h3 class="modal-title">{{ editingId ? '编辑分类' : '新增分类' }}</h3>
        <div class="form-item">
          <label>分类名称</label>
          <input v-model="form.categoryname" type="text" placeholder="如：热血">
        </div>
        <div class="form-item">
          <label>分类描述</label>
          <textarea v-model="form.description" rows="3" placeholder="简要说明该分类的题材特征"></textarea>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label>排序号</label>
            <input v-model.number="form.sortorder" type="number" min="1">
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
              <option value="启用">启用</option>
              <option value="停用">停用</option>
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
.form-message {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  margin-bottom: 16px;
}
.form-message.success { background: rgba(7,193,96,0.1); color: #07c160; }
.form-message.error { background: rgba(225,29,72,0.1); color: var(--primary); }
.table-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th {
  text-align: left;
  padding: 14px 16px;
  background: #fafafa;
  color: var(--text-muted);
  font-weight: 600;
  border-bottom: 1px solid var(--border);
}
.data-table td { padding: 14px 16px; border-bottom: 1px solid #f3f4f6; }
.data-table tr:last-child td { border-bottom: none; }
.strong { font-weight: 600; }
.muted { color: var(--text-muted); }
.badge { padding: 3px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.badge-success { background: rgba(7,193,96,0.1); color: #07c160; }
.badge-muted { background: rgba(107,114,128,0.12); color: var(--text-muted); }
.action-btn {
  padding: 5px 12px;
  margin-right: 8px;
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
/* 弹窗 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  width: 480px;
  background: white;
  border-radius: var(--radius);
  padding: 24px;
  box-shadow: var(--shadow-lg);
}
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
.btn-ghost-dark {
  background: white;
  color: var(--text-main);
  border: 1px solid var(--border);
}
</style>
