<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="用户first name" prop="firstName">
        <el-input
            v-model="queryParams.firstName"
            placeholder="请输入用户first name"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户last name" prop="lasteName">
        <el-input
            v-model="queryParams.lasteName"
            placeholder="请输入用户last name"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户昵称" prop="userName">
        <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户昵称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提问的次数" prop="questionNum">
        <el-input
            v-model="queryParams.questionNum"
            placeholder="请输入提问的次数"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否冻结" prop="freeze">
        <el-input
            v-model="queryParams.freeze"
            placeholder="请输入是否冻结"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="每日最高提问次数" prop="dailyHighestQuestion">
        <el-input
            v-model="queryParams.dailyHighestQuestion"
            placeholder="请输入每日最高提问次数"
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
            v-hasPermi="['bot:private:edit']"
        >修改
        </el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['bot:private:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="privateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="用户的id" align="center" prop="privateId" v-if="true"/>
      <el-table-column label="用户first name" align="center" prop="firstName"/>
      <el-table-column label="用户last name" align="center" prop="lasteName"/>
      <el-table-column label="用户昵称" align="center" prop="userName"/>
      <!--      <el-table-column label="提问的次数" align="center" prop="questionNum" />-->
      <el-table-column label="冻结" align="center" prop="freeze" :formatter="(row)=>statusFormatter(row.freeze)"/>
      <el-table-column label="个人设置" align="center" prop="separateConfig"
                       :formatter="(row)=>statusFormatter(row.separateConfig)"/>
      <el-table-column label="每日最高提问次数" align="center" prop="dailyHighestQuestion"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['bot:private:edit']">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['bot:private:remove']">删除
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

    <!-- 添加或修改电报私人对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="privateRef" :model="form" :rules="rules" label-width="128px">
        <el-form-item label="用户first name" prop="firstName">
          <el-input disabled v-model="form.firstName" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="用户last name" prop="lasteName">
          <el-input disabled v-model="form.lasteName" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="用户昵称" prop="userName">
          <el-input disabled v-model="form.userName" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="是否冻结" prop="freeze">
          <el-switch
              v-model="form.freeze"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="个人设置" prop="separateConfig">
          <el-switch
              v-model="form.separateConfig"
              size="large"
              active-text="是"
              inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="" prop="dailyHighestQuestion">
          <el-input-number :disabled="form.separateConfig===false" :min="0" v-model="form.dailyHighestQuestion"
                           placeholder="请输入每日最高提问次数"/>
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

<script setup name="Private">
import {addPrivate, delPrivate, getPrivate, listPrivate, updatePrivate} from "@/api/bot/private";
import {getCurrentInstance, reactive, ref, toRefs} from "vue";
import {statusFormatter} from "@/utils/format";

const {proxy} = getCurrentInstance();

const privateList = ref([]);
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
    firstName: undefined,
    lasteName: undefined,
    userName: undefined,
    questionNum: undefined,
    freeze: undefined,
    dailyHighestQuestion: undefined,
  },
  rules: {
    privateId: [
      {required: true, message: "用户的id不能为空", trigger: "blur"}
    ],
    freeze: [
      {required: true, message: "是否冻结不能为空", trigger: "blur"}
    ]
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询电报私人列表 */
function getList() {
  loading.value = true;
  listPrivate(queryParams.value).then(response => {
    privateList.value = response.rows;
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
    privateId: null,
    firstName: null,
    lasteName: null,
    userName: null,
    questionNum: null,
    freeze: null,
    separateConfig: null,
    dailyHighestQuestion: null,
    createTime: null,
    createBy: null,
    updateTime: null,
    updateBy: null
  };
  proxy.resetForm("privateRef");
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
  ids.value = selection.map(item => item.privateId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加电报私人";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset();
  const _privateId = row.privateId || ids.value
  getPrivate(_privateId).then(response => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改电报用户设置";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["privateRef"].validate(valid => {
    if (valid) {
      if (form.value.separateConfig && (form.value.dailyHighestQuestion === null || form.value.dailyHighestQuestion === undefined)) {
        proxy.$modal.msgError("不遵从群设置情况下必须配置每日最高提问次数");
        return
      }
      buttonLoading.value = true;
      if (form.value.privateId != null) {
        updatePrivate(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addPrivate(form.value).then(response => {
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
  const _privateIds = row.privateId || ids.value;
  proxy.$modal.confirm('是否确认删除电报用户编号为"' + _privateIds + '"的数据项？').then(function () {
    loading.value = true;
    return delPrivate(_privateIds);
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
  proxy.download('bot/private/export', {
    ...queryParams.value
  }, `private_${new Date().getTime()}.xlsx`)
}

getList();
</script>
