<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  usertype: '读者',
  phone: '',
  email: '',
})
const loading = ref(false)
const errorMsg = ref('')

const imgUrl = (prompt, size) => {
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`
}

const mascotImage = imgUrl('cute anime mascot characters group, blue hair girl and white hair girl, cheerful welcoming pose, chibi style, comic platform registration decoration, pastel background', 'square')

const register = async () => {
  errorMsg.value = ''

  if (!form.value.username || !form.value.password || !form.value.confirmPassword) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  if (!form.value.phone) {
    errorMsg.value = '请输入手机号'
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/user/register', {
      username: form.value.username,
      password: form.value.password,
      usertype: form.value.usertype,
      phone: form.value.phone,
      email: form.value.email,
    })
    if (res.data.code === 200) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      errorMsg.value = res.data.message || '注册失败'
    }
  } catch (err) {
    errorMsg.value = err.response?.data?.message || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="card-left">
        <img :src="mascotImage" alt="欢迎">
        <p class="welcome-text">加入漫阅<br>发现更多精彩漫画</p>
      </div>

      <div class="card-right">
        <h2 class="title">注册漫阅账号</h2>

        <div class="form">
          <div class="input-group">
            <span class="input-label">用户名</span>
            <input v-model="form.username" type="text" placeholder="请输入用户名">
          </div>

          <div class="input-group">
            <span class="input-label">密码</span>
            <input v-model="form.password" type="password" placeholder="请输入密码">
          </div>

          <div class="input-group">
            <span class="input-label">确认密码</span>
            <input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码">
          </div>

          <div class="input-group role-group">
            <span class="input-label">身份</span>
            <div class="role-options">
              <label class="role-option">
                <input v-model="form.usertype" type="radio" value="读者">
                <span>读者</span>
              </label>
              <label class="role-option">
                <input v-model="form.usertype" type="radio" value="作者">
                <span>作者</span>
              </label>
            </div>
          </div>

          <div class="input-group">
            <span class="input-label">手机号</span>
            <input v-model="form.phone" type="tel" placeholder="请输入手机号">
          </div>

          <div class="input-group">
            <span class="input-label">邮箱</span>
            <input v-model="form.email" type="email" placeholder="请输入邮箱（选填）">
          </div>
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <button class="register-btn" :disabled="loading" @click="register">
          {{ loading ? '注册中...' : '立即注册' }}
        </button>

        <p class="login-link">
          已有账号？<router-link to="/login">去登录</router-link>
        </p>

        <p class="agreement">
          注册即代表你同意 <a href="javascript:;">用户协议</a> 和 <a href="javascript:;">隐私政策</a>
        </p>
      </div>

      <router-link to="/" class="close-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: calc(100vh - var(--nav-height) - 48px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #fff5f7 0%, #f0f7ff 100%);
  border-radius: var(--radius);
}

.register-card {
  position: relative;
  display: flex;
  width: 760px;
  background: white;
  border-radius: 20px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.card-left {
  width: 280px;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #e0f2fe 0%, #fce7f3 100%);
}

.card-left img {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-md);
}

.welcome-text {
  text-align: center;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-main);
  line-height: 1.6;
}

.card-right {
  flex: 1;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 28px;
  font-family: 'Noto Serif SC', serif;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 16px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  transition: var(--transition);
}

.input-group:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.input-label {
  width: 70px;
  font-size: 14px;
  color: var(--text-main);
  font-weight: 500;
  white-space: nowrap;
}

.input-group input[type="text"],
.input-group input[type="password"],
.input-group input[type="tel"],
.input-group input[type="email"] {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--text-main);
  background: transparent;
}

.input-group input::placeholder {
  color: #c0c4cc;
}

.role-group {
  align-items: center;
}

.role-options {
  flex: 1;
  display: flex;
  gap: 20px;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-main);
  cursor: pointer;
}

.role-option input[type="radio"] {
  width: 16px;
  height: 16px;
  accent-color: var(--accent);
}

.error-msg {
  color: var(--primary);
  font-size: 13px;
  margin-bottom: 12px;
  text-align: center;
}

.register-btn {
  width: 100%;
  padding: 13px 0;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  margin-bottom: 16px;
}

.register-btn:hover:not(:disabled) {
  background: #1d4ed8;
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.login-link a {
  color: var(--accent);
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

.agreement {
  margin-top: auto;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.6;
}

.agreement a {
  color: var(--accent);
}

.agreement a:hover {
  text-decoration: underline;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  border-radius: 50%;
  transition: var(--transition);
}

.close-btn:hover {
  background: #f3f4f6;
  color: var(--text-main);
}

.close-btn svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 768px) {
  .register-card {
    flex-direction: column;
    width: 100%;
    max-width: 420px;
  }
  .card-left {
    width: 100%;
    padding: 24px;
  }
  .card-left img {
    width: 120px;
    height: 120px;
  }
  .card-right {
    padding: 30px 24px;
  }
}
</style>
