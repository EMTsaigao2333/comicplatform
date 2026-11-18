<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 登录成功后跳转回之前被拦截的页面，默认回首页
const redirectPath = route.query.redirect || '/'

const loginType = ref('password') // password | sms
const form = ref({ username: '', password: '', phone: '', smsCode: '' })
const loading = ref(false)
const errorMsg = ref('')

const imgUrl = (prompt, size) => {
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`
}

const qrImage = imgUrl('anime style login QR code illustration, cute manga characters around digital frame, pink and blue gradient, comic platform login decoration', 'square')
const leftMascot = imgUrl('cute blue hair anime mascot girl, cheerful expression, chibi style, white and blue color, comic platform mascot character', 'portrait_4_3')
const rightMascot = imgUrl('cute white hair anime mascot girl with pink highlights, cheerful expression, chibi style, pink and white color, comic platform mascot character', 'portrait_4_3')

const login = async () => {
  errorMsg.value = ''
  if (loginType.value === 'password' && (!form.value.username || !form.value.password)) {
    errorMsg.value = '请输入账号和密码'
    return
  }
  if (loginType.value === 'sms' && (!form.value.phone || !form.value.smsCode)) {
    errorMsg.value = '请输入手机号和验证码'
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/user/login', null, {
      params: {
        username: form.value.username,
        password: form.value.password,
      }
    })
    if (res.data.code === 200) {
      // res.data.data 现在包含 { userid, username, role, phone, email }
      userStore.setUser(res.data.data)
      router.push(redirectPath)
    } else {
      // 企业标准：不暴露具体是用户名错还是密码错
      errorMsg.value = res.data.message || '账号或密码错误'
    }
  } catch (err) {
    errorMsg.value = err.response?.data?.message || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- 左侧吉祥物 -->
    <img :src="leftMascot" class="side-mascot left" alt="吉祥物">

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-left">
        <h3 class="qr-title">扫描二维码登录</h3>
        <div class="qr-box">
          <img :src="qrImage" alt="二维码">
        </div>
        <p class="qr-tip">
          请使用 <span class="highlight">漫阅客户端</span><br>
          扫码登录或扫码下载APP
        </p>
      </div>

      <div class="card-divider"></div>

      <div class="card-right">
        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <button
            :class="['tab', { active: loginType === 'password' }]"
            @click="loginType = 'password'"
          >密码登录</button>
          <button
            :class="['tab', { active: loginType === 'sms' }]"
            @click="loginType = 'sms'"
          >短信登录</button>
        </div>

        <!-- 密码登录表单 -->
        <div v-if="loginType === 'password'" class="form">
          <div class="input-group">
            <span class="input-label">账号</span>
            <input v-model="form.username" type="text" placeholder="请输入账号">
          </div>
          <div class="input-group">
            <span class="input-label">密码</span>
            <input v-model="form.password" type="password" placeholder="请输入密码">
            <a class="forgot-link" href="javascript:;">忘记密码?</a>
          </div>
        </div>

        <!-- 短信登录表单 -->
        <div v-else class="form">
          <div class="input-group">
            <span class="input-label phone-prefix">+86</span>
            <input v-model="form.phone" type="tel" placeholder="请输入手机号">
            <button class="sms-btn" type="button">获取验证码</button>
          </div>
          <div class="input-group">
            <span class="input-label">验证码</span>
            <input v-model="form.smsCode" type="text" placeholder="请输入验证码">
          </div>
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <div class="btn-group">
          <router-link to="/register" class="register-btn">注册</router-link>
          <button class="login-btn" :disabled="loading" @click="login">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>

        <!-- 第三方登录 -->
        <div class="third-login">
          <span class="third-title">其他方式登录</span>
          <div class="third-icons">
            <div class="third-icon wechat">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.01-.269-.03-.407-.03zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.969-.982z"/></svg>
            </div>
            <div class="third-icon weibo">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.737 5.439l-.002.004zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.194.573zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.405-.649.388-1.067.428-1.987.003-2.643-.793-1.229-2.96-1.164-5.407-.034 0 0-.775.34-.577-.275.381-1.206.324-2.215-.27-2.8-1.348-1.327-4.932.045-8.013 3.063C1.351 10.435 0 12.636 0 14.516c0 3.598 4.613 5.788 9.127 5.788 5.916 0 9.856-3.44 9.856-6.174 0-1.652-1.39-2.587-2.924-3.081zM20.084 2.2c-1.387-1.537-3.435-2.133-5.327-1.78a.746.746 0 0 0-.597.86.75.75 0 0 0 .86.597c1.37-.27 2.905.18 3.95 1.34 1.046 1.16 1.34 2.72.945 4.137a.75.75 0 0 0 .538.914.747.747 0 0 0 .914-.538c.52-1.847.13-3.89-1.283-5.53zm-2.053 2.274c-.69-.765-1.71-1.061-2.65-.885a.562.562 0 1 0 .208 1.104c.564-.105 1.176.08 1.589.538.413.46.546 1.085.374 1.65a.563.563 0 0 0 .39.692.56.56 0 0 0 .691-.389c.28-.96.045-2.02-.602-2.71z"/></svg>
            </div>
            <div class="third-icon qq">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12.003 2c-2.265 0-6.29 1.364-6.29 7.325v1.195S3.55 14.96 3.55 17.474c0 .665.17 1.025.281 1.025.114 0 .902-.484 1.748-2.072 0 0-.18 2.197 1.904 3.967 0 0-1.77.495-1.77 1.182 0 .686 4.078.43 6.29.43 2.212 0 6.29.256 6.29-.43 0-.687-1.77-1.182-1.77-1.182 2.085-1.77 1.904-3.967 1.904-3.967.846 1.588 1.634 2.072 1.748 2.072.111 0 .281-.36.281-1.025 0-2.514-2.164-6.954-2.164-6.954V9.325C18.293 3.364 14.268 2 12.003 2z"/></svg>
            </div>
          </div>
        </div>
      </div>

      <router-link to="/" class="close-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6 6 18M6 6l12 12"/>
        </svg>
      </router-link>
    </div>

    <!-- 右侧吉祥物 -->
    <img :src="rightMascot" class="side-mascot right" alt="吉祥物">

    <!-- 底部协议 -->
    <p class="agreement">
      未注册过漫阅的手机号，我们将自动帮你注册账号<br>
      登录或完成注册即代表你同意 <a href="javascript:;">用户协议</a> 和 <a href="javascript:;">隐私政策</a>
    </p>
  </div>
</template>

<style scoped>
.login-page {
  min-height: calc(100vh - var(--nav-height) - 48px);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px 20px;
  background: linear-gradient(135deg, #fff5f7 0%, #f0f7ff 100%);
  border-radius: var(--radius);
}

.side-mascot {
  position: absolute;
  bottom: 60px;
  width: 180px;
  height: auto;
  object-fit: contain;
  pointer-events: none;
}

.side-mascot.left {
  left: 40px;
}

.side-mascot.right {
  right: 40px;
}

.login-card {
  position: relative;
  display: flex;
  width: 800px;
  background: white;
  border-radius: 20px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.card-left {
  width: 300px;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fafafa;
}

.qr-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-main);
  margin-bottom: 20px;
}

.qr-box {
  width: 180px;
  height: 180px;
  padding: 10px;
  background: white;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}

.qr-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.qr-tip {
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.6;
}

.qr-tip .highlight {
  color: var(--accent);
  font-weight: 500;
}

.card-divider {
  width: 1px;
  background: var(--border);
}

.card-right {
  flex: 1;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
}

.login-tabs {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 30px;
}

.tab {
  font-size: 18px;
  font-weight: 500;
  color: var(--text-muted);
  background: none;
  border: none;
  padding: 4px 0;
  cursor: pointer;
  position: relative;
  transition: var(--transition);
}

.tab:hover {
  color: var(--text-main);
}

.tab.active {
  color: var(--accent);
  font-weight: 600;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--accent);
  border-radius: 1px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  transition: var(--transition);
  position: relative;
}

.input-group:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.input-label {
  font-size: 14px;
  color: var(--text-main);
  font-weight: 500;
  white-space: nowrap;
}

.input-label.phone-prefix {
  color: var(--text-muted);
  font-weight: 400;
}

.input-group input {
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

.forgot-link {
  font-size: 13px;
  color: var(--accent);
  white-space: nowrap;
}

.forgot-link:hover {
  text-decoration: underline;
}

.sms-btn {
  padding: 4px 10px;
  background: transparent;
  border: 1px solid var(--accent);
  color: var(--accent);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: var(--transition);
}

.sms-btn:hover {
  background: rgba(37, 99, 235, 0.05);
}

.error-msg {
  color: var(--primary);
  font-size: 13px;
  margin-bottom: 12px;
  text-align: center;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.register-btn,
.login-btn {
  flex: 1;
  padding: 12px 0;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  text-align: center;
  transition: var(--transition);
  border: none;
}

.register-btn {
  background: white;
  border: 1px solid var(--border);
  color: var(--text-main);
}

.register-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.login-btn {
  background: var(--accent);
  color: white;
}

.login-btn:hover:not(:disabled) {
  background: #1d4ed8;
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.third-login {
  margin-top: auto;
  text-align: center;
}

.third-title {
  display: block;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 12px;
}

.third-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.third-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: transform 200ms ease;
}

.third-icon:hover {
  transform: scale(1.1);
}

.third-icon svg {
  width: 20px;
  height: 20px;
}

.third-icon.wechat { background: #07c160; }
.third-icon.weibo { background: #e6162d; }
.third-icon.qq { background: #12b7f5; }

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

.agreement {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
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

@media (max-width: 1024px) {
  .side-mascot {
    display: none;
  }
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
    max-width: 420px;
  }
  .card-left {
    width: 100%;
    padding: 24px;
  }
  .card-divider {
    width: 100%;
    height: 1px;
  }
  .card-right {
    padding: 30px 24px;
  }
}
</style>
