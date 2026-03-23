<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="位置名称" prop="landCode">
        <el-input
          v-model="queryParams.landCode"
          placeholder="请输入位置名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地块类型" prop="landType">
        <el-select v-model="queryParams.landType" placeholder="请选择地块类型" clearable>
          <el-option v-for="dict in bus_land_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="输入单位" prop="unit">
        <el-select v-model="queryParams.unit" placeholder="请选择输入单位" clearable>
          <el-option v-for="dict in bus_area_unit" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="canManageLand" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-if="canManageLand" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-if="canDeleteLand" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['agriculture:land:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="landList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="地块ID" align="center" prop="landId" />
      <el-table-column label="位置名称" align="center" prop="landCode" />
      <el-table-column label="地块类型" align="center" prop="landType">
        <template #default="scope">
          <dict-tag :options="bus_land_type" :value="scope.row.landType" />
        </template>
      </el-table-column>
      <el-table-column label="用户输入数值" align="center" prop="inputValue" />
      <el-table-column label="输入单位" align="center" prop="unit">
        <template #default="scope">
          <dict-tag :options="bus_area_unit" :value="scope.row.unit" />
        </template>
      </el-table-column>
      <el-table-column label="算法基准面积(平方米)" align="center" prop="areaSqm" />
      <el-table-column label="光照等级" align="center" prop="lightLevel">
        <template #default="scope">
          <dict-tag :options="bus_light_level" :value="scope.row.lightLevel" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-if="canManageLand" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="canDeleteLand" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="landRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="位置名称" prop="landCode">
          <el-input v-model="form.landCode" placeholder="请输入位置名称" />
        </el-form-item>
        <el-form-item label="地块类型" prop="landType">
          <el-select v-model="form.landType" placeholder="请选择地块类型">
            <el-option v-for="dict in bus_land_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户输入数值" prop="inputValue">
          <el-input v-model="form.inputValue" placeholder="请输入用户输入数值" />
        </el-form-item>
        <el-form-item label="输入单位" prop="unit">
          <el-select v-model="form.unit" placeholder="请选择输入单位">
            <el-option v-for="dict in bus_area_unit" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="光照等级" prop="lightLevel">
          <el-select v-model="form.lightLevel" placeholder="请选择光照等级">
            <el-option v-for="dict in bus_light_level" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Land">
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { parseTime } from '@/utils/ruoyi'
import { listLand, getLand, delLand, addLand, updateLand } from '@/api/agriculture/land'

const { proxy } = getCurrentInstance()
const { bus_light_level, bus_area_unit, bus_land_type } = proxy.useDict('bus_light_level', 'bus_area_unit', 'bus_land_type')

const canManageLand = proxy.$auth.hasPermi('agriculture:land:add') || proxy.$auth.hasPermi('agriculture:land:edit')
const canDeleteLand = proxy.$auth.hasPermi('agriculture:land:remove')

const landList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const daterangeCreateTime = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    landCode: null,
    landType: null,
    unit: null
  },
  rules: {
    landCode: [{ required: true, message: '位置名称不能为空', trigger: 'blur' }],
    landType: [{ required: true, message: '地块类型不能为空', trigger: 'change' }],
    inputValue: [{ required: true, message: '用户输入数值不能为空', trigger: 'blur' }],
    unit: [{ required: true, message: '输入单位不能为空', trigger: 'change' }],
    lightLevel: [{ required: true, message: '光照等级不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  queryParams.value.params = {}
  if (daterangeCreateTime.value?.length) {
    queryParams.value.params.beginCreateTime = daterangeCreateTime.value[0]
    queryParams.value.params.endCreateTime = daterangeCreateTime.value[1]
  }
  listLand(queryParams.value).then((response) => {
    landList.value = response.rows || []
    total.value = response.total || 0
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    landId: null,
    landCode: null,
    landType: null,
    inputValue: null,
    unit: null,
    areaSqm: null,
    lightLevel: null,
    createBy: null,
    createTime: null,
    remark: null
  }
  proxy.resetForm('landRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  daterangeCreateTime.value = []
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.landId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加地块资源管理'
}

function handleUpdate(row) {
  reset()
  const landId = row.landId || ids.value
  getLand(landId).then((response) => {
    form.value = response.data
    open.value = true
    title.value = '修改地块资源管理'
  })
}

function submitForm() {
  proxy.$refs.landRef.validate((valid) => {
    if (!valid) {
      return
    }
    const action = form.value.landId != null ? updateLand(form.value) : addLand(form.value)
    action.then(() => {
      proxy.$modal.msgSuccess(form.value.landId != null ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const landIds = row.landId || ids.value
  proxy.$modal
    .confirm(`是否确认删除地块资源管理编号为 "${landIds}" 的数据项？`)
    .then(() => delLand(landIds))
    .then(() => {
      getList()
      proxy.$modal.msgSuccess('删除成功')
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download(
    'agriculture/land/export',
    {
      ...queryParams.value
    },
    `land_${new Date().getTime()}.xlsx`
  )
}

getList()
</script>
