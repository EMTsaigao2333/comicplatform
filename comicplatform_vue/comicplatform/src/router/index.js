//createRouter：用来创建一个全局路由对象，管理所有跳转规则
//createWebHistory：history 路由模式，浏览器地址不带 #，
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import ComicDetail from '@/views/ComicDetail.vue'
import ChapterReader from '@/views/ChapterReader.vue'
import Bookshelf from '@/views/Bookshelf.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ComicEdit from '@/views/ComicEdit.vue'
import ChapterEdit from '@/views/ChapterEdit.vue'
import Category from '@/views/Category.vue'
import Admin from '@/views/Admin.vue'
import AuthorComics from '@/views/AuthorComics.vue'
import CategoryManage from '@/views/CategoryManage.vue'
import NoticeManage from '@/views/NoticeManage.vue'
import CommentManage from '@/views/CommentManage.vue'

const routes = [
  // 1. 首页：访问根路径 / 打开Home首页组件
  { path: '/', name: 'home', component: Home },

  // 2. 漫画详情页 :id是动态路径参数，代表漫画ID
  // 访问 /comic/123 → id=123，打开123号漫画详情
  { path: '/comic/:id', name: 'comicDetail', component: ComicDetail },

  // 3. 章节阅读页 :chapterId 章节ID，打开对应章节漫画
  { path: '/chapter/:chapterId', name: 'chapterReader', component: ChapterReader },

  // 4. 我的书架 固定路径
  { path: '/bookshelf', name: 'bookshelf', component: Bookshelf },

  // 5. 登录页
  { path: '/login', name: 'login', component: Login },

  // 6. 注册页
  { path: '/register', name: 'register', component: Register },

  // 7. 分类页：多维度筛选漫画
  { path: '/category', name: 'category', component: Category },

  // 8. 作者编辑漫画（发布新漫画/修改已有漫画）
  { path: '/author/comic/edit', name: 'comicEdit', component: ComicEdit },

  // 作者作品列表（作者查看/管理自己的漫画）
  { path: '/author/comics', name: 'authorComics', component: AuthorComics },

  // 8. 编辑章节 :comicId 要编辑章节所属的漫画ID
  { path: '/author/chapter/edit/:comicId', name: 'chapterEdit', component: ChapterEdit },

  // 9. 管理员后台：搜索漫画、标记违规、下架
  { path: '/admin', name: 'admin', component: Admin },

  // 10. 管理员-分类管理：题材分类的增删改查
  { path: '/admin/category', name: 'categoryManage', component: CategoryManage },

  // 11. 管理员-公告管理：首页公告的增删改查
  { path: '/admin/notice', name: 'noticeManage', component: NoticeManage },

  // 12. 管理员-评论管理：评论审核、屏蔽、删除
  { path: '/admin/comment', name: 'commentManage', component: CommentManage },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫：创作页和管理页需要登录，管理页还需要管理员权限
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const isLoggedIn = !!user
  const isAdmin = user?.role === 'admin'

  // 创作相关页面需要登录
  if (to.path.startsWith('/author') && !isLoggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // 管理员页面需要管理员权限（包含 /admin 下的分类、公告、评论管理）
  if (to.path.startsWith('/admin') && !isAdmin) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
