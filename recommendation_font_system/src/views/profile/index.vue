<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><User /></el-icon>
            <span>基本信息</span>
          </div>
          <el-tag :type="roleTagType">{{ profileData.roleLabel }}</el-tag>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="avatarUrl" />
            <div class="avatar-name">{{ profileData.username || '-' }}</div>
            <el-tag v-if="profileData.status === 1" type="success">状态：启用</el-tag>
            <el-tag v-else type="danger">状态：禁用</el-tag>
          </div>
        </el-col>
        <el-col :span="16">
          <el-descriptions column="2" border>
            <el-descriptions-item label="用户编号">
              {{ profileData.userId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="角色">
              {{ profileData.roleLabel || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="学生编号">
              {{ profileData.studentId || (profileData.role === 'student' ? '未绑定' : '-') }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ profileData.createTime || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="最近登录时间">
              {{ profileData.lastLoginTime || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系方式">
              {{ profileData.phone || '暂未填写' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="info-row">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon><Tickets /></el-icon>
                <span>账号信息</span>
              </div>
            </div>
          </template>
          <el-descriptions column="1" border>
            <el-descriptions-item label="账号状态">
              <el-tag v-if="profileData.status === 1" type="success">正常</el-tag>
              <el-tag v-else type="danger">已禁用</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最近操作">
              {{ profileData.lastAction || '暂无记录' }}
            </el-descriptions-item>
            <el-descriptions-item label="所属部门">
              {{ profileData.department || '未设置' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon><Notebook /></el-icon>
                <span>安全信息</span>
              </div>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in securityLogs"
              :key="index"
              :timestamp="item.time"
              size="large"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { User, Tickets, Notebook } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProfileInfo } from '@/api/profile'

const profileData = ref({
  username: '',
  role: '',
  roleLabel: '',
  userId: '',
  studentId: '',
  status: 1,
  createTime: '',
  lastLoginTime: '',
  phone: '',
  lastAction: '',
  department: ''
})

const securityLogs = ref([
  { time: '2025-12-01 10:18:00', content: '密码修改成功' },
  { time: '2025-11-28 15:42:18', content: '从Windows客户端登录系统' },
  { time: '2025-11-25 09:13:54', content: '绑定二次验证' }
])

const avatarUrl = ref('https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/2025/02/2e17d44a-66a1-47c4-951a-7c4616f2b3f6.jpg')

const roleTagType = computed(() => {
  if (profileData.value.role === 'admin') return 'success'
  if (profileData.value.role === 'student') return 'warning'
  return 'info'
})

const loadProfileInfo = async () => {
  try {
    const res = await getProfileInfo()
    if (res.code === 200 && res.data) {
      profileData.value = {
        ...profileData.value,
        ...res.data,
        roleLabel: roleMap[res.data.role] || '未知角色'
      }
    } else {
      ElMessage.error(res.message || '获取个人信息失败')
    }
  } catch (error) {
    console.error('获取个人中心数据失败', error)
    ElMessage.error('获取个人信息失败，请稍后重试')
  }
}

const roleMap = {
  admin: '超级管理员',
  common: '普通角色',
  student: '学生角色'
}

onMounted(() => {
  loadProfileInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 10px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
}

.avatar-name {
  font-size: 18px;
  font-weight: 600;
}

.info-row {
  margin-top: 10px;
}

.el-descriptions {
  margin-bottom: 0;
}

.el-timeline-item {
  font-size: 14px;
}
</style>

