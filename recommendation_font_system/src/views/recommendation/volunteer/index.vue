<template>
  <div class="volunteer-recommendation-container">
    <div class="page-header">
      <h2>智能志愿推荐</h2>
      <p class="description">
        基于您的高考成绩和历年录取数据，为您推荐最适合的院校和专业组合
      </p>
    </div>
    
    <div class="search-section">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="学生编号">
          <el-input 
            v-model="queryParams.studentId" 
            placeholder="请输入学生编号" 
            clearable 
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div v-if="studentInfo" class="student-info">
      <el-card shadow="never">
        <template #header>
          <div class="info-header">
            <div class="info-title">
              <el-icon><User /></el-icon>
              <span>学生信息</span>
            </div>
            <el-tag type="primary">{{ studentInfo.studentName || '未填写姓名' }}</el-tag>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">学生编号</span>
              <span class="value">{{ studentInfo.studentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">高考分数</span>
              <span class="value score">{{ studentInfo.examScore ?? '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">省市排名</span>
              <span class="value">{{ studentInfo.provinceRank ?? '-' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">志愿数量</span>
              <span class="value">{{ studentInfo.volunteerCount ?? 0 }}</span>
            </div>
            <div class="info-item">
              <span class="label">匹配历史数据</span>
              <span class="value">{{ studentInfo.admissionMatchCount ?? 0 }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
    
    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      style="width: 100%; margin-top: 20px">
      <el-table-column prop="university_name" label="院校名称" min-width="180" />
      <el-table-column prop="major_name" label="专业名称" min-width="180" />
      <el-table-column prop="last_year_admission" label="去年录取分数" min-width="120" />
      <el-table-column prop="score_difference" label="分数差距" min-width="100">
        <template #default="scope">
          <span :class="getScoreDifferenceClass(scope.row.score_difference)">
            {{ formatScoreDifference(scope.row.score_difference) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="推荐指数" min-width="150">
        <template #default="scope">
          <el-rate
            v-model="scope.row.recommendationRate"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button 
            type="primary" 
            link 
            @click="handleAddToVolunteer(scope.row)">
            加入志愿
          </el-button>
          <el-button 
            type="success" 
            link 
            @click="handleViewDetails(scope.row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="empty-block" v-if="!loading && tableData.length === 0">
      <el-empty description="暂无推荐数据，请输入学生编号进行查询" />
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      title="志愿推荐详情"
      width="700px"
      destroy-on-close
    >
      <el-skeleton v-if="detailLoading" :rows="6" animated />
      <template v-else>
        <el-descriptions column="2" border>
          <el-descriptions-item label="院校名称">{{ detailData?.universityName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专业名称">{{ detailData?.majorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="去年录取分数">{{ detailData?.lastYearScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="分数差距">{{ formatScoreDifference(detailData?.scoreDifference || 0) }}</el-descriptions-item>
          <el-descriptions-item label="录取概率">
            <el-progress
              v-if="detailData?.admissionProbability !== undefined"
              :percentage="detailData.admissionProbability"
              :color="getProbabilityColor(detailData.admissionProbability)"
              :stroke-width="12"
              :format="percentageFormat"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="推荐等级">
            <el-rate v-if="detailData?.recommendationRate"
              v-model="detailData.recommendationRate"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
        <el-divider />
        <el-descriptions column="1" border>
          <el-descriptions-item label="优势分析">
            {{ detailData?.advantageAnalysis || '暂无数据' }}
          </el-descriptions-item>
          <el-descriptions-item label="风险提示">
            {{ detailData?.riskAdvice || detailData?.riskTips || '暂无数据' }}
          </el-descriptions-item>
          <el-descriptions-item label="录取建议">
            {{ detailData?.suggestion || '暂无数据' }}
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, LocationInformation, School, Collection } from '@element-plus/icons-vue'
import { getVolunteerRecommendation, getRecommendationStudentInfo, getVolunteerDetail } from '@/api/recommendation'
import { saveApplication } from '@/api/application'

// 查询参数
const queryParams = reactive({
  studentId: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const studentInfo = ref(null)
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref(null)

// 获取学生信息概览
const fetchStudentInfo = async () => {
  try {
    const res = await getRecommendationStudentInfo(queryParams.studentId)
    if (res.code === 200 && res.data) {
      studentInfo.value = res.data
    } else {
      studentInfo.value = null
      ElMessage.warning(res.message || '未获取到学生信息')
    }
  } catch (error) {
    studentInfo.value = null
    console.error('获取学生信息失败', error)
    ElMessage.error('获取学生信息失败，请检查网络或服务器状态')
  }
}

// 查询数据
const handleQuery = async () => {
  if (!queryParams.studentId) {
    ElMessage.warning('请输入学生编号')
    return
  }
  
  loading.value = true
  try {
    await fetchStudentInfo()
    const res = await getVolunteerRecommendation(queryParams.studentId)
    
    if (res.code === 200) {
      const recommendations = res.data?.recommendations || []
      tableData.value = recommendations.map(item => {
        const rawDiff = item.scoreDifference ?? item.score_difference ?? 0
        const scoreDiff = Number(rawDiff)

        const hasRate =
          item.recommendationRate !== undefined &&
          item.recommendationRate !== null

        const hasRateSnake =
          item.recommendation_rate !== undefined &&
          item.recommendation_rate !== null

        let recommendationRate = hasRate
          ? Number(item.recommendationRate)
          : hasRateSnake
            ? Number(item.recommendation_rate)
            : undefined

        if (recommendationRate === undefined || Number.isNaN(recommendationRate)) {
          recommendationRate = 5
          if (Math.abs(scoreDiff) > 50) recommendationRate = 1
          else if (Math.abs(scoreDiff) > 30) recommendationRate = 2
          else if (Math.abs(scoreDiff) > 20) recommendationRate = 3
          else if (Math.abs(scoreDiff) > 10) recommendationRate = 4
        }

        return {
          ...item,
          university_name: item.universityName || item.university_name,
          major_name: item.majorName || item.major_name,
          last_year_admission: item.lastYearAdmission || item.last_year_admission,
          score_difference: scoreDiff,
          recommendationRate
        }
      })
    } else {
      ElMessage.error(res.message || '查询失败')
      tableData.value = []
    }
  } catch (error) {
    console.error('获取推荐数据失败', error)
    ElMessage.error('获取推荐数据失败，请检查网络或服务器状态')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 格式化分数差距
const formatScoreDifference = (diff) => {
  if (diff > 0) {
    return `+${diff}分`
  } else if (diff < 0) {
    return `${diff}分`
  } else {
    return '0分'
  }
}

// 获取分数差距的样式类
const getScoreDifferenceClass = (diff) => {
  if (diff > 20) return 'score-high'
  if (diff < -20) return 'score-low'
  return 'score-medium'
}

// 添加到志愿
const handleAddToVolunteer = (row) => {
  if (!queryParams.studentId) {
    ElMessage.warning('请先输入学生编号并查询')
    return
  }

  ElMessageBox.confirm(`确认将 ${row.university_name} 的 ${row.major_name} 专业添加到志愿中吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    const universityId = row.universityId || row.university_id || row.universityCode || extractId(row.university_name)
    const majorId = row.majorId || row.major_id || row.majorCode || extractId(row.major_name)
    const payload = {
      studentId: queryParams.studentId,
      universityId,
      universityName: row.university_name,
      majorId,
      majorName: row.major_name,
      recommendationId: row.recommendationId || row.id,
      volunteerOrder: row.recommendationOrder || 1
    }
    
    saveApplication(payload).then(res => {
      if (res.code === 200) {
        ElMessage.success('已成功添加到志愿')
      } else {
        ElMessage.error(res.message || '添加志愿失败')
      }
    }).catch(error => {
      console.error('添加志愿错误', error)
      ElMessage.error('添加志愿失败，请检查网络或服务器状态')
    })
  }).catch(() => {})
}

// 从名称中提取ID（如果后端没有返回ID）
const extractId = (name) => {
  // 简单实现，实际应该调用后端接口获取ID
  return name.substring(0, 6).replace(/\D/g, '')
}

// 查看详情
const handleViewDetails = (row) => {
  const universityId = row.universityId || row.university_id || row.universityCode
  const majorId = row.majorId || row.major_id || row.majorCode
  if (!queryParams.studentId || !universityId || !majorId) {
    ElMessage.warning('缺少必要的查询参数，无法获取详情')
    return
  }

  detailDialogVisible.value = true
  detailLoading.value = true
  detailData.value = null

  getVolunteerDetail({
    studentId: queryParams.studentId,
    universityId,
    majorId
  }).then(res => {
    if (res.code === 200 && res.data) {
      detailData.value = res.data
    } else {
      ElMessage.error(res.message || '获取志愿详情失败')
      detailDialogVisible.value = false
    }
  }).catch(error => {
    console.error('获取志愿详情失败', error)
    ElMessage.error('获取志愿详情失败，请检查网络或服务器状态')
    detailDialogVisible.value = false
  }).finally(() => {
    detailLoading.value = false
  })
}

const getProbabilityColor = (probability) => {
  if (probability < 50) return '#F56C6C'
  if (probability < 80) return '#E6A23C'
  return '#67C23A'
}

const percentageFormat = (percentage) => `${percentage}%`
</script>

<style scoped>
.volunteer-recommendation-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
  color: #303133;
}

.description {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.search-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.student-info {
  margin-bottom: 20px;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  color: #606266;
}

.info-item .label {
  color: #909399;
}

.info-item .value {
  font-weight: 600;
  color: #303133;
}

.info-item .score {
  color: #409eff;
}

.score-high {
  color: #67c23a;
  font-weight: bold;
}

.score-medium {
  color: #e6a23c;
  font-weight: bold;
}

.score-low {
  color: #f56c6c;
  font-weight: bold;
}

.empty-block {
  margin-top: 40px;
}
</style>