<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="群名字" prop="title">
        <el-input
            v-model="queryParams.title"
            placeholder="请输入群名字"
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
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['bot:group:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['bot:group:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="groupList" @selection-change="handleSelectionChange">
      <!--      <el-table-column type="selection" width="55" align="center"/>-->
      <el-table-column label="群ID" align="center" prop="groupId"/>
      <el-table-column label="群名" align="center" prop="title"/>
      <el-table-column label="关闭按钮" align="center" prop="button"
                       :formatter="(row)=>statusFormatter(row.button)"/>
      <el-table-column label="冻结" align="center" prop="freeze" :formatter="(row)=>statusFormatter(row.freeze)"/>
      <el-table-column label="模型" align="center" prop="openApiModel"/>
      <el-table-column label="是否独立token" align="center" prop="independentToken"
                       :formatter="(row)=>statusFormatter(row.independentToken)"/>
      <el-table-column label="token" align="center" prop="openApiToken"/>
      <el-table-column label="当日提问次数" align="center" prop="questionNum"/>
      <el-table-column label="群总限制" align="center" prop="globalConfig"
                       :formatter="(row)=>statusFormatter(!row.globalConfig)"/>
      <el-table-column label="群总限制每日提问次数" align="center" prop="dailyHighestQuestion"/>
      <el-table-column label="群个人限制" align="center" prop="globalUserConfig"
                       :formatter="(row)=>statusFormatter(row.globalUserConfig)"/>
      <el-table-column label="群个人限制每日提问次数" align="center" prop="globalUserNum"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bot:group:edit']">
            修改
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

    <!-- 添加或修改电报群组对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="groupRef" :model="form" :rules="rules" label-width="200px">
        <el-form-item label="群名字" prop="title">
          <el-input disabled v-model="form.title" placeholder="请输入群名字"/>
        </el-form-item>
        <el-form-item label="关闭按钮" prop="button">
          <el-switch
              v-model="form.button"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="冻结" prop="freeze">
          <el-switch
              v-model="form.freeze"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>

        <el-form-item label="模型" prop="openApiModel">
          <el-input v-model="form.openApiModel" placeholder="请输入模型"/>
        </el-form-item>

        <el-form-item label="是否独立token" prop="independentToken">
          <el-switch
              v-model="form.independentToken"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="独立的token" prop="openApiToken">
          <el-input v-model="form.openApiToken" placeholder="请输入独立的openapi的token"/>
        </el-form-item>
        <el-form-item label="群总限制" prop="globalConfig">
          <el-switch
              v-model="form.globalConfig"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="" prop="dailyHighestQuestion">
          <el-input-number :disabled="form.globalConfig===false" :min="0" v-model="form.dailyHighestQuestion"
                           placeholder="请输入每日最高提问次数"/>
        </el-form-item>
        <el-form-item label="群个人限制" prop="globalUserConfig">
          <el-switch
              v-model="form.globalUserConfig"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="" prop="globalUserNum">
          <el-input-number :disabled="form.globalUserConfig===false" :min="0" v-model="form.globalUserNum"
                           placeholder="请输入群人员发言次数"/>
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

<script setup name="Group">
import {addGroup, delGroup, getGroup, listGroup, updateGroup} from "@/api/bot/group";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {statusFormatter} from "@/utils/format";

const {proxy} = getCurrentInstance();

const groupList = ref([]);
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
    title: undefined,
    button: undefined,
    freeze: undefined,
    independentToken: undefined,
    openApiToken: undefined,
  },
  rules: {
    groupId: [
      {required: true, message: "主键不能为空", trigger: "blur"}
    ],
    button: [
      {required: true, message: "是否关闭回复消息中的按钮不能为空", trigger: "blur"}
    ],
    freeze: [
      {required: true, message: "是否冻结该群 0是不冻结不能为空", trigger: "blur"}
    ],
    independentToken: [
      {required: true, message: "是否是设置了独立的openapi的token不能为空", trigger: "blur"}
    ]
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询电报群组列表 */
function getList() {
  loading.value = true;
  listGroup(queryParams.value).then(response => {
    groupList.value = response.rows;
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
    groupId: null,
    title: null,
    button: null,
    freeze: null,
    independentToken: null,
    openApiModel: null,
    openApiToken: null,
    globalConfig: null,
    dailyHighestQuestion: null,
    globalUserConfig: null,
    globalUserNum: null,
    createTime: null,
    createBy: null,
    updateTime: null,
    updateBy: null
  };
  proxy.resetForm("groupRef");
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
  ids.value = selection.map(item => item.groupId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加电报群组";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset();
  const _groupId = row.groupId || ids.value
  getGroup(_groupId).then(response => {
    loading.value = false;
    form.value = response.data;
    form.value.globalConfig = !form.value.globalConfig
    open.value = true;
    title.value = "修改电报群组";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["groupRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      form.value.globalConfig = !form.value.globalConfig;
      if (form.value.groupId != null) {
        updateGroup(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addGroup(form.value).then(response => {
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
  const _groupIds = row.groupId || ids.value;
  proxy.$modal.confirm('是否确认删除电报群组编号为"' + _groupIds + '"的数据项？').then(function () {
    loading.value = true;
    return delGroup(_groupIds);
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
  proxy.download('bot/group/export', {
    ...queryParams.value
  }, `group_${new Date().getTime()}.xlsx`)
}

getList();
</script>
