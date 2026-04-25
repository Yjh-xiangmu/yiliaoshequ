// pages/settings/settings.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: {},
    orderNotify: true,
    showNicknameModal: false,
    showPhoneModal: false,
    newNickname: '',
    newPhone: ''
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    this.loadUserInfo();
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({ userInfo });
  },

  /**
   * 编辑用户名
   */
  editNickname() {
    this.setData({
      showNicknameModal: true,
      newNickname: this.data.userInfo.nickname || ''
    });
  },

  hideNicknameModal() {
    this.setData({ showNicknameModal: false });
  },

  onNicknameInput(e) {
    this.setData({ newNickname: e.detail.value });
  },

  saveNickname() {
    const nickname = this.data.newNickname.trim();
    if (!nickname) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '保存中...', mask: true });

    api.updateUserInfo({ nickname })
      .then(() => {
        wx.hideLoading();

        // 更新本地缓存
        const userInfo = { ...app.globalData.userInfo, nickname };
        app.setUserInfo(userInfo, wx.getStorageSync('token'));

        this.setData({
          showNicknameModal: false,
          userInfo
        });

        wx.showToast({ title: '修改成功', icon: 'success' });
      })
      .catch(() => {
        wx.hideLoading();
      });
  },

  /**
   * 编辑手机号
   */
  editPhone() {
    this.setData({
      showPhoneModal: true,
      newPhone: this.data.userInfo.phone || ''
    });
  },

  hidePhoneModal() {
    this.setData({ showPhoneModal: false });
  },

  onPhoneInput(e) {
    this.setData({ newPhone: e.detail.value });
  },

  savePhone() {
    const phone = this.data.newPhone.trim();
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }

    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '保存中...', mask: true });

    api.updateUserInfo({ phone })
      .then(() => {
        wx.hideLoading();

        // 更新本地缓存
        const userInfo = { ...app.globalData.userInfo, phone };
        app.setUserInfo(userInfo, wx.getStorageSync('token'));

        this.setData({
          showPhoneModal: false,
          userInfo
        });

        wx.showToast({ title: '修改成功', icon: 'success' });
      })
      .catch(() => {
        wx.hideLoading();
      });
  },

  /**
   * 订单通知开关
   */
  onOrderNotifyChange(e) {
    this.setData({ orderNotify: e.detail.value });
    wx.showToast({
      title: e.detail.value ? '已开启通知' : '已关闭通知',
      icon: 'none'
    });
  },

  /**
   * 清除缓存
   */
  clearCache() {
    wx.showModal({
      title: '清除缓存',
      content: '确定清除本地缓存吗？',
      success: (res) => {
        if (res.confirm) {
          // 只清除缓存数据，保留登录信息
          const token = wx.getStorageSync('token');
          const userInfo = wx.getStorageSync('userInfo');
          wx.clearStorageSync();
          wx.setStorageSync('token', token);
          wx.setStorageSync('userInfo', userInfo);

          wx.showToast({ title: '清除成功', icon: 'success' });
        }
      }
    });
  },

  /**
   * 退出登录
   */
  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearUserInfo();
          wx.reLaunch({ url: '/pages/login/login' });
        }
      }
    });
  }
});