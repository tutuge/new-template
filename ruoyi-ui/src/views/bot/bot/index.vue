<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="机器人名字" prop="botName">
        <el-input
            v-model="queryParams.botName"
            placeholder="请输入机器人名字"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机器人token" prop="botToken">
        <el-input
            v-model="queryParams.botToken"
            placeholder="请输入机器人token"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="enable">
        <el-select v-model="queryParams.enable">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
            v-hasPermi="['bot:bot:add']"
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
            v-hasPermi="['bot:bot:edit']"
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
            v-hasPermi="['bot:bot:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['bot:bot:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="botList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="机器人ID" align="center" prop="botId" v-if="true"/>
      <el-table-column label="机器人名字" align="center" prop="botName"/>
      <el-table-column label="机器人token" align="center" prop="botToken"/>
      <el-table-column label="是否启用" align="center" prop="enable"
                       :formatter="(row) => {return row.enable?'是':'否'}"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bot:bot:edit']">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['bot:bot:remove']">
            删除
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

    <!-- 添加或修改机器人管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="botRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="机器人名字" prop="botName">
          <el-input v-model="form.botName" placeholder="请输入机器人名字"/>
        </el-form-item>
        <el-form-item label="机器人token" prop="botToken">
          <el-input v-model="form.botToken" placeholder="请输入机器人token"/>
        </el-form-item>
        <el-form-item label="是否启用" prop="enable">
          <el-select v-model="form.enable" placeholder="请选择是否启用">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
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

<script setup name="Bot">
import {addBot, delBot, getBot, listBot, updateBot} from "@/api/bot/bot";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";

const {proxy} = getCurrentInstance();

const botList = ref([]);
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
    botName: undefined,
    botToken: undefined,
    enable: undefined,
  },
  options: [{label: "是", value: true}, {label: "否", value: false}],
  rules: {
    botId: [
      {required: true, message: "机器人ID不能为空", trigger: "blur"}
    ],
    botName: [
      {required: true, message: "机器人名字不能为空", trigger: "blur"}
    ],
    botToken: [
      {required: true, message: "机器人token不能为空", trigger: "blur"}
    ],
    enable: [
      {required: true, message: "是否启用不能为空", trigger: "blur"}
    ],
  }
});

const {queryParams, options, form, rules} = toRefs(data);

/** 查询机器人管理列表 */
function getList() {
  loading.value = true;
  listBot(queryParams.value).then(response => {
    botList.value = response.rows;
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
    botId: null,
    botName: null,
    botToken: null,
    enable: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null
  };
  proxy.resetForm("botRef");
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
  ids.value = selection.map(item => item.botId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加机器人管理";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset();
  const _botId = row.botId || ids.value
  getBot(_botId).then(response => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改机器人管理";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["botRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.botId != null) {
        updateBot(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addBot(form.value).then(response => {
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
  const _botIds = row.botId || ids.value;
  proxy.$modal.confirm('是否确认删除机器人管理编号为"' + _botIds + '"的数据项？').then(function () {
    loading.value = true;
    return delBot(_botIds);
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
  proxy.download('bot/bot/export', {
    ...queryParams.value
  }, `bot_${new Date().getTime()}.xlsx`)
}

getList();
</script>
