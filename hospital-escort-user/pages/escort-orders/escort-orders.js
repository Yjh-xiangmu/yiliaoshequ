// pages/escort-orders/escort-orders.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    tabs: [
      { name: '已接单', status: 1, count: 0 },
      { name: '服务中', status: 2, count: 0 },
      { name: '已完成', status: 3, count: 0 }
    ],
    currentTab: 0,
    currentStatus: 1,
    orderList: [],
    loading: true
  },

  onLoad(options) {
    console.log('陪诊员订单列表页加载，参数：', options);
    
    // 如果有传入tab参数，切换到对应tab
    if (options.tab !== undefined) {
      const tabIndex = parseInt(options.tab);
      if (tabIndex >= 0 && tabIndex < this.data.tabs.length) {
        this.setData({
          currentTab: tabIndex,
          currentStatus: this.data.tabs[tabIndex].status
        });
      }
    }
    
    this.loadOrders();
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadOrders().then(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 切换Tab
   */
  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    const status = this.data.tabs[index].status;
    
    this.setData({
      currentTab: index,
      currentStatus: status
    });

    this.loadOrders();
  },

  /**
   * 加载订单列表
   */
  loadOrders() {
    this.setData({ loading: true });

    return api.getEscortOrders(this.data.currentStatus, 1, 50)
      .then(res => {
        console.log('陪诊员订单列表：', res.data);
        
        const orders = res.data.records || [];
        
        // 格式化数据
        const formattedOrders = orders.map(order => {
          return {
            ...order,
            appointmentTime: this.formatDateTime(order.appointmentTime)
          };
        });

        // 更新各tab的数量
        this.updateTabCounts();

        this.setData({
          orderList: formattedOrders,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载订单列表失败：', err);
        this.setData({ loading: false });
      });
  },

  /**
   * 更新Tab数量（模拟，实际应该从后端获取）
   */
  updateTabCounts() {
    // TODO: 从后端获取各状态订单数量
    // 这里暂时不更新，保持为0
  },

  /**
   * 格式化日期时间
   */
  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    const dateTime = dateTimeStr.replace('T', ' ');
    return dateTime.substring(0, 16);
  },

  /**
   * 拨打患者电话
   */
  callPatient(e) {
    const phone = e.currentTarget.dataset.phone;
    if (!phone) {
      wx.showToast({
        title: '暂无联系电话',
        icon: 'none'
      });
      return;
    }

    wx.makePhoneCall({
      phoneNumber: phone,
      fail: () => {
        wx.showToast({
          title: '拨号失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 开始服务
   */
  startService(e) {
    const id = e.currentTarget.dataset.id;
    const orderNo = e.currentTarget.dataset.no;

    wx.showModal({
      title: '开始服务',
      content: `确定开始服务订单 ${orderNo} 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.updateOrderStatus(id, 2, '开始服务');
        }
      }
    });
  },

  /**
   * 完成服务
   */
  finishService(e) {
    const id = e.currentTarget.dataset.id;
    const orderNo = e.currentTarget.dataset.no;

    wx.showModal({
      title: '完成服务',
      content: `确定完成服务订单 ${orderNo} 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.updateOrderStatus(id, 3, '完成服务');
        }
      }
    });
  },

  /**
   * 更新订单状态
   */
  updateOrderStatus(orderId, status, actionName) {
    wx.showLoading({
      title: '处理中...',
      mask: true
    });

    api.updateOrderStatus(orderId, status)
      .then(res => {
        wx.hideLoading();
        console.log('订单状态更新成功：', res);
        
        wx.showToast({
          title: `${actionName}成功`,
          icon: 'success',
          duration: 1500
        });

        // 刷新列表
        setTimeout(() => {
          this.loadOrders();
        }, 1500);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('订单状态更新失败：', err);
      });
  },

  /**
   * 查看详情
   */
  viewDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/escort-order-detail/escort-order-detail?id=${id}`
    });
  }
  
});