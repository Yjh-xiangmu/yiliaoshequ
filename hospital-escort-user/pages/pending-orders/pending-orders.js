// pages/pending-orders/pending-orders.js
const app = getApp();
const api = require('../../utils/api.js');
const util = require('../../utils/util.js');

Page({
  data: {
    orderList: [],
    totalCount: 0,
    loading: true
  },

  onLoad() {
    console.log('待接单列表页加载');
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
   * 加载待接订单列表
   */
  loadOrders() {
    this.setData({ loading: true });

    return api.getPendingOrders(1, 50)
      .then(res => {
        console.log('待接订单列表：', res.data);
        
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
          totalCount: res.data.total || 0,
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
    return dateTime.substring(0, 16); // 截取到分钟
  },

  /**
   * 查看详情
   */
  viewDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/escort-order-detail/escort-order-detail?id=${id}`
    });
  },

  /**
   * 接单
   */
  acceptOrder(e) {
    const id = e.currentTarget.dataset.id;
    const orderNo = e.currentTarget.dataset.no;

    wx.showModal({
      title: '确认接单',
      content: `确定要接单 ${orderNo} 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doAcceptOrder(id);
        }
      }
    });
  },

  /**
   * 执行接单操作
   */
  doAcceptOrder(orderId) {
    wx.showLoading({
      title: '接单中...',
      mask: true
    });

    api.acceptOrder(orderId)
      .then(res => {
        wx.hideLoading();
        console.log('接单成功：', res);
        
        wx.showToast({
          title: '接单成功',
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
        console.error('接单失败：', err);
      });
  }
});