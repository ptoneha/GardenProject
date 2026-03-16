<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入任务ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['agriculture:result:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['agriculture:result:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['agriculture:result:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['agriculture:result:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="resultList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="结果ID" align="center" prop="resultId" />
      <el-table-column label="位置ID" align="center" prop="landId" />
      <el-table-column label="容器ID" align="center" prop="containerId" />
      <el-table-column label="作物ID" align="center" prop="cropId" />
      <el-table-column label="分配面积" align="center" prop="allocatedAreaSqm" />
      <el-table-column label="显示数值" align="center" prop="displayAreaVal" />
      <el-table-column label="建议株树" align="center" prop="plantCount" />
      <el-table-column label="收益" align="center" prop="profitAmount" />
      <el-table-column label="体验评分" align="center" prop="experienceScore" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['agriculture:result:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['agriculture:result:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改种植决策方案对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="resultRef" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Result">
import { listResult, getResult, delResult, addResult, updateResult } from "@/api/agriculture/result"

const { proxy } = getCurrentInstance()

const resultList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    taskId: null,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询种植决策方案列表 */
function getList() {
  loading.value = true
  listResult(queryParams.value).then(response => {
    resultList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    resultId: null,
    taskId: null,
    landId: null,
    containerId: null,
    cropId: null,
    allocatedAreaSqm: null,
    displayAreaVal: null,
    plantCount: null,
    profitAmount: null,
    experienceScore: null,
    createTime: null
  }
  proxy.resetForm("resultRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.resultId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加种植决策方案"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _resultId = row.resultId || ids.value
  getResult(_resultId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改种植决策方案"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["resultRef"].validate(valid => {
    if (valid) {
      if (form.value.resultId != null) {
        updateResult(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addResult(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _resultIds = row.resultId || ids.value
  proxy.$modal.confirm('是否确认删除种植决策方案编号为"' + _resultIds + '"的数据项？').then(function() {
    return delResult(_resultIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('agriculture/result/export', {
    ...queryParams.value
  }, `result_${new Date().getTime()}.xlsx`)
}

getList()
</script>
