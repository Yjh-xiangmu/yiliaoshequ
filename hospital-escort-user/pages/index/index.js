// pages/index/index.js
const app = getApp();

Page({
  data: {
    userInfo: {},
    orderCount: {
      all: 0,
      pending: 0,
      accepted: 0,
      inService: 0,
      completed: 0
    }
  },

  onLoad() {
    console.log('===== 用户首页 onLoad =====');
    
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
    if (userInfo.userType === 'escort') {
      console.log('是陪诊员，跳转陪诊员首页');
      wx.redirectTo({
        url: '/pages/escort-home/escort-home'
      });
      return;
    }

    this.setData({
      userInfo: userInfo
    });

    // 加载订单统计数据
    this.loadOrderCount();
  },

  onShow() {
    // 每次显示时刷新用户信息和订单统计
    if (app.isLogin()) {
      // 重新读取最新用户信息
      const userInfo = app.globalData.userInfo || {};
      this.setData({
        userInfo: userInfo
      });
      this.loadOrderCount();
    }
  },

  /**
   * 加载订单统计（模拟数据，后续可接入真实接口）
   */
  loadOrderCount() {
    // TODO: 调用后端接口获取订单统计
    // 暂时用模拟数据
    this.setData({
      'orderCount.all': 0,
      'orderCount.pending': 0,
      'orderCount.accepted': 0,
      'orderCount.inService': 0,
      'orderCount.completed': 0
    });
  },

  /**
   * 选择医院
   */
  goHospitalList() {
    wx.navigateTo({
      url: '/pages/hospital/hospital'
    });
  },

  /**
   * 订单列表
   */
  goOrderList(e) {
    const status = e.currentTarget?.dataset?.status;
    console.log('跳转订单列表，状态:', status);
    
    let url = '/pages/order-list/order-list';
    if (status !== undefined && status !== '') {
      url += `?status=${status}`;
    }
    
    wx.navigateTo({
      url: url
    });
  },

  /**
   * 服务评价
   */
  goEvaluation() {
    wx.showToast({
      title: '请到已完成订单中评价',
      icon: 'none'
    });
  },

  /**
   * 个人中心
   */
  goUserCenter() {
    wx.navigateTo({
      url: '/pages/user-center/user-center'
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
            wx.redirectTo({
              url: '/pages/login/login'
            });
          }, 1500);
        }
      }
    });
  }
});