// pages/user-center/user-center.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: {},
    orderStats: {
      total: 0,
      pending: 0,
      accepted: 0,
      inService: 0,
      completed: 0
    }
  },

  onLoad() {
    console.log('个人中心页面加载');
    
    // 检查登录状态
    if (!app.isLogin()) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }

    this.loadUserInfo();
    this.loadOrderStats();
  },

  onShow() {
    // 每次显示时刷新数据
    if (app.isLogin()) {
      this.loadOrderStats();
    }
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({
      userInfo: userInfo
    });
  },

  /**
   * 加载订单统计
   */
  loadOrderStats() {
    // TODO: 调用后端接口获取订单统计
    // 暂时用模拟数据
    api.getMyOrders(null, 1, 100)
      .then(res => {
        const orders = res.data.records || [];
        
        const stats = {
          total: orders.length,
          pending: orders.filter(o => o.status === 0).length,
          accepted: orders.filter(o => o.status === 1).length,
          inService: orders.filter(o => o.status === 2).length,
          completed: orders.filter(o => o.status === 3).length
        };

        this.setData({
          orderStats: stats
        });
      })
      .catch(err => {
        console.error('加载订单统计失败：', err);
      });
  },

  /**
   * 跳转订单列表
   */
  goOrderList(e) {
    const status = e.currentTarget.dataset.status;
    let url = '/pages/order-list/order-list';
    if (status !== undefined && status !== '') {
      url += `?status=${status}`;
    }
    wx.navigateTo({
      url: url
    });
  },

  /**
   * 常用就诊人
   */
  goPatientList() {
    wx.navigateTo({
      url: '/pages/patient-list/patient-list'
    });
  },

  /**
   * 我的评价
   */
  goMyEvaluations() {
    wx.navigateTo({
      url: '/pages/my-evaluations/my-evaluations'
    });
  },

  /**
   * 设置
   */
  goSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings'
    });
  },

  /**
   * 帮助中心
   */
  goHelp() {
    wx.showModal({
      title: '帮助中心',
      content: '客服电话：400-123-4567\n工作时间：9:00-18:00',
      showCancel: false
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
          wx.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 1500
          });
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            });
          }, 1500);
        }
      }
    });
  }
});