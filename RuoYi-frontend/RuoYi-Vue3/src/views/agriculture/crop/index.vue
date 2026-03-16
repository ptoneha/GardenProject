<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛题编号" prop="cropCode">
        <el-input
          v-model="queryParams.cropCode"
          placeholder="请输入赛题编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作物名称" prop="cropName">
        <el-input
          v-model="queryParams.cropName"
          placeholder="请输入作物名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作物分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择作物分类" clearable>
          <el-option
            v-for="dict in bus_crop_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="主要种植目的" prop="plantPurpose">
        <el-select v-model="queryParams.plantPurpose" placeholder="请选择主要种植目的" clearable>
          <el-option
            v-for="dict in bus_plant_purpose"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['agriculture:crop:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['agriculture:crop:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['agriculture:crop:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['agriculture:crop:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cropList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="作物ID" align="center" prop="cropId" />
      <el-table-column label="赛题编号" align="center" prop="cropCode" />
      <el-table-column label="作物名称" align="center" prop="cropName" />
      <el-table-column label="作物分类" align="center" prop="category">
        <template #default="scope">
          <dict-tag :options="bus_crop_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="主要种植目的" align="center" prop="plantPurpose">
        <template #default="scope">
          <dict-tag :options="bus_plant_purpose" :value="scope.row.plantPurpose"/>
        </template>
      </el-table-column>
      <el-table-column label="是否为豆类" align="center" prop="isPulse">
        <template #default="scope">
          <dict-tag :options="sys_yes_no" :value="scope.row.isPulse"/>
        </template>
      </el-table-column>
      <el-table-column label="作物图片" align="center" prop="imageUrl" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.imageUrl" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['agriculture:crop:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['agriculture:crop:remove']">删除</el-button>
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

    <!-- 添加或修改作物百科管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="cropRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="赛题编号" prop="cropCode">
          <el-input v-model="form.cropCode" placeholder="请输入赛题编号" />
        </el-form-item>
        <el-form-item label="作物名称" prop="cropName">
          <el-input v-model="form.cropName" placeholder="请输入作物名称" />
        </el-form-item>
        <el-form-item label="作物分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择作物分类">
            <el-option
              v-for="dict in bus_crop_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="主要种植目的" prop="plantPurpose">
          <el-radio-group v-model="form.plantPurpose">
            <el-radio
              v-for="dict in bus_plant_purpose"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否为豆类" prop="isPulse">
          <el-radio-group v-model="form.isPulse">
            <el-radio
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="作物图片" prop="imageUrl">
          <image-upload v-model="form.imageUrl"/>
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

<script setup name="Crop">
import { listCrop, getCrop, delCrop, addCrop, updateCrop } from "@/api/agriculture/crop"

const { proxy } = getCurrentInstance()
const { bus_plant_purpose, sys_yes_no, bus_crop_category } = proxy.useDict('bus_plant_purpose', 'sys_yes_no', 'bus_crop_category')

const cropList = ref([])
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
    cropCode: null,
    cropName: null,
    category: null,
    plantPurpose: null,
  },
  rules: {
    cropName: [
      { required: true, message: "作物名称不能为空", trigger: "blur" }
    ],
    category: [
      { required: true, message: "作物分类不能为空", trigger: "change" }
    ],
    isPulse: [
      { required: true, message: "是否为豆类不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询作物百科管理列表 */
function getList() {
  loading.value = true
  listCrop(queryParams.value).then(response => {
    cropList.value = response.rows
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
    cropId: null,
    cropCode: null,
    cropName: null,
    category: null,
    plantPurpose: null,
    isPulse: null,
    imageUrl: null
  }
  proxy.resetForm("cropRef")
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
  ids.value = selection.map(item => item.cropId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加作物百科管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _cropId = row.cropId || ids.value
  getCrop(_cropId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改作物百科管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["cropRef"].validate(valid => {
    if (valid) {
      if (form.value.cropId != null) {
        updateCrop(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCrop(form.value).then(response => {
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
  const _cropIds = row.cropId || ids.value
  proxy.$modal.confirm('是否确认删除作物百科管理编号为"' + _cropIds + '"的数据项？').then(function() {
    return delCrop(_cropIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('agriculture/crop/export', {
    ...queryParams.value
  }, `crop_${new Date().getTime()}.xlsx`)
}

getList()
</script>
