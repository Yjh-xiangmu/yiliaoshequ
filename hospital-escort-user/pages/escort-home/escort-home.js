// pages/escort-home/escort-home.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: {},
    isOnline: true,
    orderCount: {
      accepted: 0,
      inService: 0,
      completed: 0
    },
    todayData: {
      orderCount: 0,
      income: '0.00'
    },
    totalIncome: '0.00'
  },

  onLoad() {
    console.log('===== 陪诊员首页 onLoad =====');
    
    // 检查登录状态
    if (!app.isLogin()) {
      console.log('未登录，跳转登录页');
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }

    // 检查是否是陪诊员
    const userInfo = app.globalData.userInfo || {};
    if (userInfo.userType !== 'escort') {
      console.log('不是陪诊员，跳转用户首页');
      wx.redirectTo({
        url: '/pages/index/index'
      });
      return;
    }

    this.setData({
      userInfo: userInfo
    });

    // 加载数据
    this.loadStatistics();
    this.loadIncome();
  },

  onShow() {
    // 每次显示时刷新数据
    if (app.isLogin()) {
      const userInfo = app.globalData.userInfo || {};
      this.setData({ userInfo });
      
      // 刷新统计数据
      this.loadStatistics();
      this.loadIncome();
    }
  },

  /**
   * 加载订单统计
   */
  /**
 * 加载订单统计
 */
loadStatistics() {
  api.getEscortStatistics()
    .then(res => {
      const stats = res.data || {};
      this.setData({
        'orderCount.accepted': stats.acceptedCount || 0,
        'orderCount.inService': stats.inServiceCount || 0,
        'orderCount.completed': stats.completedCount || 0,
        'todayData.orderCount': stats.todayOrderCount || 0,
        'todayData.income': (stats.todayIncome || 0).toFixed(2)
      });
      console.log('统计数据：', stats);
    })
    .catch(err => {
      console.error('加载统计失败：', err);
    });
},

  /**
   * 加载收入数据
   */
  loadIncome() {
    api.getEscortIncome()
      .then(res => {
        const incomeList = res.data || [];
        const total = incomeList.reduce((sum, item) => {
          return sum + parseFloat(item.amount || 0);
        }, 0);
        
        this.setData({
          totalIncome: total.toFixed(2)
        });

        console.log('累计收入：', total.toFixed(2));
      })
      .catch(err => {
        console.error('加载收入失败：', err);
      });
  },

  /**
   * 切换在线状态
   */
  toggleOnlineStatus() {
    const newStatus = !this.data.isOnline;
    this.setData({
      isOnline: newStatus
    });

    wx.showToast({
      title: newStatus ? '已上线' : '已离线',
      icon: 'success',
      duration: 1500
    });
  },

  /**
   * 待接订单
   */
  goPendingOrders() {
    wx.navigateTo({
      url: '/pages/pending-orders/pending-orders'
    });
  },

  /**
   * 已接订单
   */
  goAcceptedOrders() {
    wx.navigateTo({
      url: '/pages/escort-orders/escort-orders?tab=0'
    });
  },

  /**
   * 服务中订单
   */
  goInServiceOrders() {
    wx.navigateTo({
      url: '/pages/escort-orders/escort-orders?tab=1'
    });
  },

  /**
   * 已完成订单
   */
  goCompletedOrders() {
    wx.navigateTo({
      url: '/pages/escort-orders/escort-orders?tab=2'
    });
  },

  /**
   * 我的订单
   */
  goMyOrders() {
    wx.navigateTo({
      url: '/pages/escort-orders/escort-orders'
    });
  },

  /**
   * 收入明细
   */
  goIncomeDetail() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  /**
   * 服务评价
   */
  goEvaluations() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  /**
   * 个人信息
   */
  goEscortProfile() {
    wx.navigateTo({
      url: '/pages/escort-profile/escort-profile'
    });
  },
/**
 * 收入明细
 */
goIncomeDetail() {
  wx.navigateTo({
    url: '/pages/escort-income/escort-income'
  });
},

/**
 * 服务评价
 */
goEvaluations() {
  wx.navigateTo({
    url: '/pages/escort-evaluations/escort-evaluations'
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
  }
  
});