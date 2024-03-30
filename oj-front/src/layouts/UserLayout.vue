<template>
  <div id="userlayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img alt="OJ" class="logo" src="../assets/logo.png" />
          <div>OJ</div>
        </a-space>
      </a-layout-header>

      <a-layout-content class="content">
        <router-view />
      </a-layout-content>

      <a-layout-footer class="footer">
        <a href="http://www.shiyixi.icu"> 小兮打卡 by shiyixi </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userlayout {
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")
    0% 0% / 100% 100%;
}

#userlayout .header {
  margin-top: 16px;
}

#userlayout .header .logo {
  width: 66px;
  height: 64px;
}

#userlayout .content {
  padding: 20px;
}

#userlayout .footer {
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
<script lang="ts" setup></script>
