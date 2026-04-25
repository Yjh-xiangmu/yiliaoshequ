// pages/order-list/order-list.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    tabs: [
      { name: '全部', status: null },
      { name: '待接单', status: 0 },
      { name: '已接单', status: 1 },
      { name: '服务中', status: 2 },
      { name: '已完成', status: 3 }
    ],
    currentTab: 0,
    currentStatus: null,
    orderList: [],
    loading: true
  },

  onLoad(options) {
    console.log('订单列表页加载，参数：', options);
    
    // 如果有传入状态参数，自动切换到对应tab
    if (options.status !== undefined) {
      const status = parseInt(options.status);
      const tabIndex = this.data.tabs.findIndex(tab => tab.status === status);
      if (tabIndex !== -1) {
        this.setData({
          currentTab: tabIndex,
          currentStatus: status
        });
      }
    }

    this.loadOrders();
  },

  onShow() {
    // 每次显示时刷新列表
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

    return api.getMyOrders(this.data.currentStatus, 1, 50)
      .then(res => {
        console.log('订单列表：', res.data);
        
        const orders = res.data.records || [];
        
        // 格式化数据
        const formattedOrders = orders.map(order => {
          return {
            ...order,
            appointmentTime: this.formatDateTime(order.appointmentTime)
          };
        });

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
   * 格式化日期时间
   */
  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    // 将 "2026-02-13T10:00:00" 格式化为 "2026-02-13 10:00"
    const dateTime = dateTimeStr.replace('T', ' ');
    return dateTime.substring(0, 16);
  },

  /**
   * 取消订单
   */
  cancelOrder(e) {
    const id = e.currentTarget.dataset.id;
    const orderNo = e.currentTarget.dataset.no;

    wx.showModal({
      title: '取消订单',
      content: `确定要取消订单 ${orderNo} 吗？`,
      placeholderText: '请输入取消原因',
      editable: true,
      success: (res) => {
        if (res.confirm) {
          const reason = res.content || '用户主动取消';
          this.doCancelOrder(id, reason);
        }
      }
    });
  },

  /**
   * 执行取消订单
   */
  doCancelOrder(orderId, reason) {
    wx.showLoading({
      title: '取消中...',
      mask: true
    });

    api.cancelOrder(orderId, reason)
      .then(res => {
        wx.hideLoading();
        console.log('订单取消成功：', res);
        
        wx.showToast({
          title: '订单已取消',
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
        console.error('订单取消失败：', err);
      });
  },

  /**
   * 查看订单详情
   */
  goOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    });
  }
});