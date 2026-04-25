// pages/escort-profile/escort-profile.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: {},
    stats: {
      rating: '5.0',
      serviceCount: 0,
      totalIncome: '0.00'
    }
  },

  onLoad() {
    console.log('陪诊员个人中心页面加载');
    
    if (!app.isLogin()) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }

    this.loadUserInfo();
    this.loadStats();
  },

  onShow() {
    this.loadStats();
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({ userInfo });
  },

  loadStats() {
    // 加载统计数据
    Promise.all([
      api.getEscortStatistics(),
      api.getEscortIncome(),
      api.getEscortEvaluations()
    ])
      .then(([statsRes, incomeRes, evalRes]) => {
        const stats = statsRes.data || {};
        const incomeList = incomeRes.data || [];
        const evalList = evalRes.data || [];

        // 累计收入
        const totalIncome = incomeList.reduce((sum, item) => {
          return sum + parseFloat(item.amount || 0);
        }, 0);

        // 平均评分
        let avgRating = 5.0;
        if (evalList.length > 0) {
          const total = evalList.reduce((sum, item) => sum + item.rating, 0);
          avgRating = (total / evalList.length).toFixed(1);
        }

        this.setData({
          'stats.rating': avgRating,
          'stats.serviceCount': stats.completedCount || 0,
          'stats.totalIncome': totalIncome.toFixed(2)
        });
      })
      .catch(err => {
        console.error('加载统计失败：', err);
      });
  },

  goMyEvaluations() {
    wx.navigateTo({
      url: '/pages/escort-evaluations/escort-evaluations'
    });
  },

  goIncomeDetail() {
    wx.navigateTo({
      url: '/pages/escort-income/escort-income'
    });
  },

  goSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings'
    });
  },

  goHelp() {
    wx.showModal({
      title: '帮助中心',
      content: '客服电话：400-123-4567\n工作时间：9:00-18:00',
      showCancel: false
    });
  },

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