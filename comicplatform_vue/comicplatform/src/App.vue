<script setup>
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()
const logout = () => userStore.clearUser()
</script>

<template>
  <div class="app-wrapper">
    <!-- 顶部导航栏：参考哔哩哔哩漫画风格 -->
    <header class="top-nav">
      <div class="nav-inner">
        <!-- Logo -->
        <router-link to="/" class="brand">
          <span class="brand-icon">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.41.21.75-.19.75-.65V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z"/>
            </svg>
          </span>
          <span class="brand-text">漫阅</span>
        </router-link>

        <!-- 主导航 -->
        <nav class="main-nav">
          <router-link to="/" exact-active-class="active">首页</router-link>
          <router-link to="/category" active-class="active">分类</router-link>
          <router-link to="/bookshelf" exact-active-class="active">书架</router-link>
          <!-- 登录后才显示创作入口 -->
          <router-link v-if="userStore.user" to="/author/comic/edit" exact-active-class="active">创作</router-link>
          <!-- 作者（非管理员）登录后显示修改入口 -->
          <router-link v-if="userStore.isAuthor && !userStore.isAdmin" to="/author/comics" exact-active-class="active">修改</router-link>
          <!-- 仅管理员显示管理入口 -->
          <router-link v-if="userStore.isAdmin" to="/admin" exact-active-class="active">管理</router-link>
        </nav>

        <!-- 右侧操作区 -->
        <div class="nav-actions">
          <div class="search-box">
            <input type="text" placeholder="搜索漫画、作者">
            <button class="search-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
            </button>
          </div>

          <template v-if="userStore.user">
            <span class="user-name">{{ userStore.user.username }}</span>
            <button class="btn btn-primary" @click="logout">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-ghost">登录</router-link>
            <router-link to="/register" class="btn btn-primary">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 页面内容 -->
    <main class="page-content">
      <router-view />
    </main>
  </div>
</template>

<style>
:root {
  --nav-bg: #0d0d12;
  --nav-height: 64px;
  --primary: #E11D48;
  --primary-hover: #BE123C;
  --accent: #2563EB;
  --bg-page: #f5f5f7;
  --bg-card: #ffffff;
  --text-main: #1f2937;
  --text-muted: #6b7280;
  --border: #e5e7eb;
  --gold: #d4af37;
  --gold-light: #f3e5ab;
  --radius: 12px;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.08);
  --shadow-lg: 0 12px 24px rgba(0,0,0,0.12);
  --transition: all 200ms ease;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--bg-page);
  color: var(--text-main);
}

a {
  text-decoration: none;
  color: inherit;
}

.app-wrapper {
  min-height: 100vh;
}

/* 顶部导航 */
.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  height: var(--nav-height);
  background: var(--nav-bg);
  box-shadow: 0 2px 8px rgba(0,0,0,0.25);
}

.nav-inner {
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.brand-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary), #fb7185);
  border-radius: 10px;
  color: white;
}

.brand-icon svg {
  width: 22px;
  height: 22px;
}

.brand-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: white;
  letter-spacing: 2px;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 28px;
  flex: 1;
}

.main-nav a {
  color: rgba(255,255,255,0.75);
  font-size: 15px;
  font-weight: 500;
  padding: 8px 4px;
  position: relative;
  transition: var(--transition);
}

.main-nav a:hover,
.main-nav a.active {
  color: white;
}

.main-nav a.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary);
  border-radius: 2px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.1);
  border-radius: 20px;
  padding: 4px 4px 4px 16px;
  width: 220px;
  transition: var(--transition);
}

.search-box:focus-within {
  background: rgba(255,255,255,0.15);
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  color: white;
  font-size: 14px;
  outline: none;
}

.search-box input::placeholder {
  color: rgba(255,255,255,0.5);
}

.search-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
}

.search-btn:hover {
  background: var(--primary-hover);
}

.search-btn svg {
  width: 16px;
  height: 16px;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  border: none;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-primary:hover {
  background: var(--primary-hover);
}

.btn-ghost {
  background: transparent;
  color: rgba(255,255,255,0.85);
  border: 1px solid rgba(255,255,255,0.25);
}

.btn-ghost:hover {
  border-color: rgba(255,255,255,0.5);
  color: white;
}

.user-name {
  color: rgba(255,255,255,0.9);
  font-size: 14px;
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}
</style>
