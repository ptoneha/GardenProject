<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联作物" prop="cropId">
        <el-input
          v-model="queryParams.cropId"
          placeholder="请输入关联作物ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="适用环境" prop="landType">
        <el-select v-model="queryParams.landType" placeholder="请选择适用环境" clearable>
          <el-option
            v-for="dict in bus_land_type"
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
          v-hasPermi="['agriculture:config:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['agriculture:config:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['agriculture:config:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['agriculture:config:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配置ID" align="center" prop="configId" />
      <el-table-column label="关联作物ID" align="center" prop="cropId" />
      <el-table-column label="适用环境" align="center" prop="landType">
        <template #default="scope">
          <dict-tag :options="bus_land_type" :value="scope.row.landType"/>
        </template>
      </el-table-column>
      <el-table-column label="建议播种月份" align="center" prop="startMonth" />
      <el-table-column label="预计收获月份" align="center" prop="endMonth" />
      <el-table-column label="生长周期/天" align="center" prop="growthDays" />
      <el-table-column label="建议株距" align="center" prop="plantSpacingCm" />
      <el-table-column label="建议行距" align="center" prop="rowSpacingCm" />
      <el-table-column label="原始数据单位" align="center" prop="dataUnit">
        <template #default="scope">
          <dict-tag :options="bus_area_unit" :value="scope.row.dataUnit"/>
        </template>
      </el-table-column>
      <el-table-column label="产量数值" align="center" prop="yieldVal" />
      <el-table-column label="市场单价" align="center" prop="marketPrice" />
      <el-table-column label="综合体验评分" align="center" prop="utilityScore" />
      <el-table-column label="光照需求等级" align="center" prop="sunlightReq">
        <template #default="scope">
          <dict-tag :options="bus_light_level" :value="scope.row.sunlightReq"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['agriculture:config:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['agriculture:config:remove']">删除</el-button>
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

    <!-- 添加或修改作物指标配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="configRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="关联作物ID" prop="cropId">
          <el-input v-model="form.cropId" placeholder="请输入关联作物ID" />
        </el-form-item>
        <el-form-item label="适用环境" prop="landType">
          <el-select v-model="form.landType" placeholder="请选择适用环境">
            <el-option
              v-for="dict in bus_land_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建议播种月份" prop="startMonth">
          <el-input v-model="form.startMonth" placeholder="请输入建议播种月份" />
        </el-form-item>
        <el-form-item label="预计收获月份" prop="endMonth">
          <el-input v-model="form.endMonth" placeholder="请输入预计收获月份" />
        </el-form-item>
        <el-form-item label="生长周期/天" prop="growthDays">
          <el-input v-model="form.growthDays" placeholder="请输入生长周期/天" />
        </el-form-item>
        <el-form-item label="建议株距" prop="plantSpacingCm">
          <el-input v-model="form.plantSpacingCm" placeholder="请输入建议株距" />
        </el-form-item>
        <el-form-item label="建议行距" prop="rowSpacingCm">
          <el-input v-model="form.rowSpacingCm" placeholder="请输入建议行距" />
        </el-form-item>
        <el-form-item label="原始数据单位" prop="dataUnit">
          <el-select v-model="form.dataUnit" placeholder="请选择原始数据单位">
            <el-option
              v-for="dict in bus_area_unit"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产量数值" prop="yieldVal">
          <el-input v-model="form.yieldVal" placeholder="请输入产量数值" />
        </el-form-item>
        <el-form-item label="成本数值" prop="costVal">
          <el-input v-model="form.costVal" placeholder="请输入成本数值" />
        </el-form-item>
        <el-form-item label="市场单价" prop="marketPrice">
          <el-input v-model="form.marketPrice" placeholder="请输入市场单价" />
        </el-form-item>
        <el-form-item label="综合体验评分" prop="utilityScore">
          <el-input v-model="form.utilityScore" placeholder="请输入综合体验评分" />
        </el-form-item>
        <el-form-item label="光照需求等级" prop="sunlightReq">
          <el-select v-model="form.sunlightReq" placeholder="请选择光照需求等级">
            <el-option
              v-for="dict in bus_light_level"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="根系最低深度" prop="minDepthReq">
          <el-input v-model="form.minDepthReq" placeholder="请输入根系最低深度" />
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

<script setup name="Config">
import { listConfig, getConfig, delConfig, addConfig, updateConfig } from "@/api/agriculture/config"

const { proxy } = getCurrentInstance()
const { bus_light_level, bus_area_unit, bus_land_type } = proxy.useDict('bus_light_level', 'bus_area_unit', 'bus_land_type')

const configList = ref([])
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
    cropId: null,
    landType: null,
  },
  rules: {
    cropId: [
      { required: true, message: "关联作物ID不能为空", trigger: "blur" }
    ],
    landType: [
      { required: true, message: "适用环境不能为空", trigger: "change" }
    ],
    startMonth: [
      { required: true, message: "建议播种月份不能为空", trigger: "blur" }
    ],
    endMonth: [
      { required: true, message: "预计收获月份不能为空", trigger: "blur" }
    ],
    growthDays: [
      { required: true, message: "生长周期/天不能为空", trigger: "blur" }
    ],
    plantSpacingCm: [
      { required: true, message: "建议株距不能为空", trigger: "blur" }
    ],
    dataUnit: [
      { required: true, message: "原始数据单位不能为空", trigger: "change" }
    ],
    yieldVal: [
      { required: true, message: "产量数值不能为空", trigger: "blur" }
    ],
    costVal: [
      { required: true, message: "成本数值不能为空", trigger: "blur" }
    ],
    marketPrice: [
      { required: true, message: "市场单价不能为空", trigger: "blur" }
    ],
    utilityScore: [
      { required: true, message: "综合体验评分不能为空", trigger: "blur" }
    ],
    sunlightReq: [
      { required: true, message: "光照需求等级不能为空", trigger: "change" }
    ],
    minDepthReq: [
      { required: true, message: "根系最低深度不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询作物指标配置列表 */
function getList() {
  loading.value = true
  listConfig(queryParams.value).then(response => {
    configList.value = response.rows
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
    configId: null,
    cropId: null,
    landType: null,
    startMonth: null,
    endMonth: null,
    growthDays: null,
    plantSpacingCm: null,
    rowSpacingCm: null,
    dataUnit: null,
    yieldVal: null,
    costVal: null,
    marketPrice: null,
    utilityScore: null,
    sunlightReq: null,
    minDepthReq: null
  }
  proxy.resetForm("configRef")
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
  ids.value = selection.map(item => item.configId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加作物指标配置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _configId = row.configId || ids.value
  getConfig(_configId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改作物指标配置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["configRef"].validate(valid => {
    if (valid) {
      if (form.value.configId != null) {
        updateConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addConfig(form.value).then(response => {
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
  const _configIds = row.configId || ids.value
  proxy.$modal.confirm('是否确认删除作物指标配置编号为"' + _configIds + '"的数据项？').then(function() {
    return delConfig(_configIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('agriculture/config/export', {
    ...queryParams.value
  }, `config_${new Date().getTime()}.xlsx`)
}

getList()
</script>
