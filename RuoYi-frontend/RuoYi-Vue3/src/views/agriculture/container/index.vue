<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属位置" prop="landId">
        <el-input
          v-model="queryParams.landId"
          placeholder="请输入所属位置ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="容器名称" prop="containerName">
        <el-input
          v-model="queryParams.containerName"
          placeholder="请输入容器名称"
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
          v-hasPermi="['agriculture:container:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['agriculture:container:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['agriculture:container:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['agriculture:container:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="containerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="容器ID" align="center" prop="containerId" />
      <el-table-column label="所属位置ID" align="center" prop="landId" />
      <el-table-column label="容器名称" align="center" prop="containerName" />
      <el-table-column label="容器类型" align="center" prop="containerType">
        <template #default="scope">
          <dict-tag :options="bus_container_type" :value="scope.row.containerType"/>
        </template>
      </el-table-column>
      <el-table-column label="可种植位置数" align="center" prop="plantingSites" />
      <el-table-column label="容器深度" align="center" prop="depthCm" />
      <el-table-column label="状态" align="center" prop="isActive">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.isActive"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['agriculture:container:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['agriculture:container:remove']">删除</el-button>
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

    <!-- 添加或修改种植容器管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="containerRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属位置ID" prop="landId">
          <el-input v-model="form.landId" placeholder="请输入所属位置ID" />
        </el-form-item>
        <el-form-item label="容器名称" prop="containerName">
          <el-input v-model="form.containerName" placeholder="请输入容器名称" />
        </el-form-item>
        <el-form-item label="容器类型" prop="containerType">
          <el-select v-model="form.containerType" placeholder="请选择容器类型">
            <el-option
              v-for="dict in bus_container_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可种植位置数" prop="plantingSites">
          <el-input v-model="form.plantingSites" placeholder="请输入可种植位置数" />
        </el-form-item>
        <el-form-item label="容器深度" prop="depthCm">
          <el-input v-model="form.depthCm" placeholder="请输入容器深度" />
        </el-form-item>
        <el-form-item label="宽度" prop="widthCm">
          <el-input v-model="form.widthCm" placeholder="请输入宽度" />
        </el-form-item>
        <el-form-item label="高度" prop="heightCm">
          <el-input v-model="form.heightCm" placeholder="请输入高度" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
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

<script setup name="Container">
import { listContainer, getContainer, delContainer, addContainer, updateContainer } from "@/api/agriculture/container"

const { proxy } = getCurrentInstance()
const { bus_container_type, sys_normal_disable } = proxy.useDict('bus_container_type', 'sys_normal_disable')

const containerList = ref([])
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
    landId: null,
    containerName: null,
  },
  rules: {
    landId: [
      { required: true, message: "所属位置ID不能为空", trigger: "blur" }
    ],
    containerName: [
      { required: true, message: "容器名称不能为空", trigger: "blur" }
    ],
    containerType: [
      { required: true, message: "容器类型不能为空", trigger: "change" }
    ],
    plantingSites: [
      { required: true, message: "可种植位置数不能为空", trigger: "blur" }
    ],
    depthCm: [
      { required: true, message: "容器深度不能为空", trigger: "blur" }
    ],
    isActive: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询种植容器管理列表 */
function getList() {
  loading.value = true
  listContainer(queryParams.value).then(response => {
    containerList.value = response.rows
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
    containerId: null,
    landId: null,
    containerName: null,
    containerType: null,
    plantingSites: null,
    depthCm: null,
    widthCm: null,
    heightCm: null,
    isActive: null
  }
  proxy.resetForm("containerRef")
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
  ids.value = selection.map(item => item.containerId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加种植容器管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _containerId = row.containerId || ids.value
  getContainer(_containerId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改种植容器管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["containerRef"].validate(valid => {
    if (valid) {
      if (form.value.containerId != null) {
        updateContainer(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addContainer(form.value).then(response => {
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
  const _containerIds = row.containerId || ids.value
  proxy.$modal.confirm('是否确认删除种植容器管理编号为"' + _containerIds + '"的数据项？').then(function() {
    return delContainer(_containerIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('agriculture/container/export', {
    ...queryParams.value
  }, `container_${new Date().getTime()}.xlsx`)
}

getList()
</script>
