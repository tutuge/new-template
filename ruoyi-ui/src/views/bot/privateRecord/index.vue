<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="用户昵称" prop="userName">
        <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户昵称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="聊天id" prop="chatId">
        <el-input
            v-model="queryParams.chatId"
            placeholder="请输入聊天id"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户id" prop="userId">
        <el-input
            v-model="queryParams.userId"
            placeholder="请输入用户id"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息id" prop="messageId">
        <el-input
            v-model="queryParams.messageId"
            placeholder="请输入消息id"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息时间时间戳形式" prop="messageDate">
        <el-input
            v-model="queryParams.messageDate"
            placeholder="请输入消息时间时间戳形式"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息内容" prop="messageText">
        <el-input
            v-model="queryParams.messageText"
            placeholder="请输入消息内容"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ai的回复" prop="aiReply">
        <el-input
            v-model="queryParams.aiReply"
            placeholder="请输入ai的回复"
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
            v-hasPermi="['bot:privateRecord:add']"
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
            v-hasPermi="['bot:privateRecord:edit']"
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
            v-hasPermi="['bot:privateRecord:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['bot:privateRecord:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="privateRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="消息的id" align="center" prop="id" v-if="true"/>
      <el-table-column label="用户昵称" align="center" prop="userName"/>
      <el-table-column label="聊天id" align="center" prop="chatId"/>
      <el-table-column label="用户id" align="center" prop="userId"/>
      <el-table-column label="消息id" align="center" prop="messageId"/>
      <el-table-column label="消息时间时间戳形式" align="center" prop="messageDate"/>
      <el-table-column label="消息内容" align="center" prop="messageText"/>
      <el-table-column label="ai的回复" align="center" prop="aiReply"/>
      <el-table-column label="消息回复成功还是失败" align="center" prop="replayStatus"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['bot:privateRecord:edit']">修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['bot:privateRecord:remove']">删除
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

    <!-- 添加或修改私人提问对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="privateRecordRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户昵称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户昵称"/>
        </el-form-item>
        <el-form-item label="聊天id" prop="chatId">
          <el-input v-model="form.chatId" placeholder="请输入聊天id"/>
        </el-form-item>
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户id"/>
        </el-form-item>
        <el-form-item label="消息id" prop="messageId">
          <el-input v-model="form.messageId" placeholder="请输入消息id"/>
        </el-form-item>
        <el-form-item label="消息时间时间戳形式" prop="messageDate">
          <el-input v-model="form.messageDate" placeholder="请输入消息时间时间戳形式"/>
        </el-form-item>
        <el-form-item label="消息内容" prop="messageText">
          <el-input v-model="form.messageText" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="ai的回复" prop="aiReply">
          <el-input v-model="form.aiReply" type="textarea" placeholder="请输入内容"/>
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

<script setup name="PrivateRecord">
import {
  addPrivateRecord,
  delPrivateRecord,
  getPrivateRecord,
  listPrivateRecord,
  updatePrivateRecord
} from "@/api/bot/privateRecord";
import {getCurrentInstance, reactive, ref} from "vue";

const {proxy} = getCurrentInstance();

const privateRecordList = ref([]);
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
    userName: undefined,
    chatId: undefined,
    userId: undefined,
    messageId: undefined,
    messageDate: undefined,
    messageText: undefined,
    aiReply: undefined,
    replayStatus: undefined,
  },
  rules: {
    id: [
      {required: true, message: "消息的id不能为空", trigger: "blur"}
    ],
    userName: [
      {required: true, message: "用户昵称不能为空", trigger: "blur"}
    ],
    chatId: [
      {required: true, message: "聊天id不能为空", trigger: "blur"}
    ],
    userId: [
      {required: true, message: "用户id不能为空", trigger: "blur"}
    ],
    messageId: [
      {required: true, message: "消息id不能为空", trigger: "blur"}
    ],
    messageDate: [
      {required: true, message: "消息时间时间戳形式不能为空", trigger: "blur"}
    ],
    messageText: [
      {required: true, message: "消息内容不能为空", trigger: "blur"}
    ],
    aiReply: [
      {required: true, message: "ai的回复不能为空", trigger: "blur"}
    ],
    replayStatus: [
      {required: true, message: "消息回复成功还是失败不能为空", trigger: "change"}
    ],
  }
});

const {queryParams, form, rules} = toRefs(data);

/** 查询私人提问列表 */
function getList() {
  loading.value = true;
  listPrivateRecord(queryParams.value).then(response => {
    privateRecordList.value = response.rows;
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
    id: null,
    userName: null,
    chatId: null,
    userId: null,
    messageId: null,
    messageDate: null,
    messageText: null,
    aiReply: null,
    replayStatus: null,
    createTime: null,
    createBy: null,
    updateTime: null,
    updateBy: null
  };
  proxy.resetForm("privateRecordRef");
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
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加私人提问";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset();
  const _id = row.id || ids.value
  getPrivateRecord(_id).then(response => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改私人提问";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["privateRecordRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id != null) {
        updatePrivateRecord(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addPrivateRecord(form.value).then(response => {
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
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除私人提问编号为"' + _ids + '"的数据项？').then(function () {
    loading.value = true;
    return delPrivateRecord(_ids);
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
  proxy.download('bot/privateRecord/export', {
    ...queryParams.value
  }, `privateRecord_${new Date().getTime()}.xlsx`)
}

getList();
</script>
