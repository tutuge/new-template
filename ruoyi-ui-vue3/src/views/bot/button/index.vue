<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="标签文本" prop="buttonText">
        <el-input
            v-model="queryParams.buttonText"
            placeholder="请输入按钮上的标签文本"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="网址" prop="url">
        <el-input
            v-model="queryParams.url"
            placeholder="请输入按钮对应的网址"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="enable">
        <el-input
            v-model="queryParams.enable"
            placeholder="请输入是否启用"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序" prop="buttonSort">
        <el-input
            v-model="queryParams.buttonSort"
            placeholder="请输入排序"
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
            v-hasPermi="['bot:button:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['bot:button:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['bot:button:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['bot:button:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="buttonList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center"/>-->
      <el-table-column label="按钮ID" align="center" prop="buttonId" v-if="true"/>
      <el-table-column label="按钮上的标签文本" align="center" prop="buttonText"/>
      <el-table-column label="按钮对应的网址" align="center" prop="url"/>
      <el-table-column label="是否启用" align="center" prop="enable" :formatter="(row)=>statusFormatter(row.enable)"/>
      <el-table-column label="排序" align="center" prop="buttonSort"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bot:button:edit']">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['bot:button:remove']">删除
          </el-button>
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

    <!-- 添加或修改机器人按钮管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="buttonRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="标签文本" prop="buttonText">
          <el-input v-model="form.buttonText" placeholder="请输入按钮上的标签文本"/>
        </el-form-item>
        <el-form-item label="网址" prop="url">
          <el-input v-model="form.url" placeholder="请输入按钮对应的网址"/>
        </el-form-item>
        <el-form-item label="是否启用" prop="enable">
<!--          <el-input v-model="form.enable" placeholder="请输入是否启用"/>-->
          <el-select v-model="form.enable" placeholder="请选择是否启用">
            <el-option
                v-for="(item, index) in yesOrNo"
                :key="index"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="buttonSort">
          <el-input-number :min="1" v-model="form.buttonSort" placeholder="请输入排序"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Button">
import {addButton, delButton, getButton, listButton, updateButton} from "@/api/bot/button";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {statusFormatter, yesOrNo} from "@/utils/format";

const {proxy} = getCurrentInstance();

const buttonList = ref([]);
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    buttonText: undefined,
    url: undefined,
    enable: undefined,
    buttonSort: undefined,
  },
  rules: {
    buttonId: [
      {required: true, message: "按钮ID不能为空", trigger: "blur"}
    ],
    buttonText: [
      {required: true, message: "按钮上的标签文本不能为空", trigger: "blur"}
    ],
    url: [
      {required: true, message: "按钮对应的网址不能为空", trigger: "blur"}
    ],
    enable: [
      {required: true, message: "是否启用不能为空", trigger: "blur"}
    ],
    buttonSort: [
      {required: true, message: "排序不能为空", trigger: "blur"}
    ],
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询机器人按钮管理列表 */
function getList() {
  loading.value = true;
  listButton(queryParams.value).then(response => {
    buttonList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    buttonId: null,
    buttonText: null,
    url: null,
    enable: null,
    buttonSort: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null
  };
  proxy.resetForm("buttonRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.buttonId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加机器人按钮管理";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset();
  const _buttonId = row.buttonId || ids.value
  getButton(_buttonId).then(response => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改机器人按钮管理";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["buttonRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.buttonId != null) {
        updateButton(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addButton(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _buttonIds = row.buttonId || ids.value;
  proxy.$modal.confirm('是否确认删除机器人按钮管理编号为"' + _buttonIds + '"的数据项？').then(function () {
    loading.value = true;
    return delButton(_buttonIds);
  }).then(() => {
    loading.value = true;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  }).finally(() => {
    loading.value = false;
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('bot/button/export', {
    ...queryParams.value
  }, `button_${new Date().getTime()}.xlsx`)
}

getList();
</script>
