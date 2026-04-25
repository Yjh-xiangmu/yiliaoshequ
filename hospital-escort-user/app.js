// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://192.168.10.105:8081/api'  // 改成你的IP
  },

  onLaunch() {
    console.log('小程序启动');
    console.log('接口地址：', this.globalData.baseUrl);
    this.checkLogin();
  },

  checkLogin() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
      console.log('已登录，用户信息:', userInfo);
    } else {
      console.log('未登录');
    }
  },

  setUserInfo(userInfo, token) {
    this.globalData.userInfo = userInfo;
    this.globalData.token = token;
    wx.setStorageSync('userInfo', userInfo);
    wx.setStorageSync('token', token);
    console.log('用户信息已保存');
  },

  clearUserInfo() {
    this.globalData.userInfo = null;
    this.globalData.token = null;
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('token');
    console.log('用户信息已清除');
  },

  getToken() {
    return this.globalData.token;
  },

  isLogin() {
    return !!this.globalData.token;
  }
});