<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <side-bar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @setLayout="setLayout"/>
        <tags-view v-if="needTagsView"/>
      </div>
      <app-main/>
      <settings ref="settingRef"/>
    </div>
    <el-dialog
        title="启用Google"
        v-model="dialogVisible"
        :show-close="false"
        :center="true"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        class="google_dialog"
        width="25%">
      <div class="des">请在手机中打开Google Authenticator 扫描如下二维码输入6位动态码</div>
      <div class="qr_code">
        <img :src="qrCode" alt="">
      </div>
      <div class="input_container">
        <input v-model="codeValue" type="text" placeholder="动态码">
        <img src="../assets/logo/google.jpg" alt="">
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="bindCode">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import {useWindowSize} from '@vueuse/core'
import SideBar from './components/Sidebar/index.vue'
import {AppMain, Navbar, Settings, TagsView} from './components'
import {bind, checkUserBindGoogle} from "@/api/system/index";
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import {computed, getCurrentInstance, ref, watchEffect} from "vue";
import useUserStore from "@/store/modules/user";

const settingsStore = useSettingsStore()
const theme = computed(() => settingsStore.theme);
const sideTheme = computed(() => settingsStore.sideTheme);
const sidebar = computed(() => useAppStore().sidebar);
const device = computed(() => useAppStore().device);
const needTagsView = computed(() => settingsStore.tagsView);
const fixedHeader = computed(() => settingsStore.fixedHeader);

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}))


const dialogVisible = ref(false)
const googleKey = ref('')
const qrCode = ref('')
const codeValue = ref('')

init()

function init() {
  checkUserBindGoogle().then(response => {
    console.log(response)
    let status = response.data.status;
    if (status === "0") {
      console.log(status)
      dialogVisible.value = true;
      googleKey.value = response.data.googleKey;
      qrCode.value = response.data.url;
    }
  });
}

const {proxy} = getCurrentInstance();

function bindCode() {
  // 提交数据
  console.log(codeValue.value);
  console.log(googleKey.value);
  bind(googleKey.value, codeValue.value).then(response => {
    if (response.data.status === "1") {
      // 成功关闭
      proxy.$modal.msgSuccess("绑定Google身份认证成功");
      dialogVisible.value = false
      //退出登录
      useUserStore().logOut().then(() => {
        location.reload()
      })
    } else {
      proxy.$modal.msgError("验证码输入错误，请重新填写");
    }
  });
}

const {width, height} = useWindowSize();
const WIDTH = 992; // refer to Bootstrap's responsive design

watchEffect(() => {
  if (device.value === 'mobile' && sidebar.value.opened) {
    useAppStore().closeSideBar({withoutAnimation: false})
  }
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile')
    useAppStore().closeSideBar({withoutAnimation: true})
  } else {
    useAppStore().toggleDevice('desktop')
  }
})

function handleClickOutside() {
  useAppStore().closeSideBar({withoutAnimation: false})
}

const settingRef = ref(null);

function setLayout() {
  settingRef.value.openSetting();
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/mixin.scss";
@import "@/assets/styles/variables.module.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}

.google_dialog {
  .des {
    text-align: center;
  }

  .qr_code {
    width: 200px;
    height: 200px;
    margin: 20px auto;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .input_container {
    position: relative;
    width: 60%;
    margin: 0 auto;
    border: 1px solid #ccc;

    input {
      width: 100%;
      outline: none;
      border: none;
      padding: 6px 10px 6px 36px;
    }

    img {
      position: absolute;
      top: 0;
      left: 6px;
      bottom: 0;
      margin: auto;
      width: 15px;
      height: 15px;
    }
  }
}
</style>
