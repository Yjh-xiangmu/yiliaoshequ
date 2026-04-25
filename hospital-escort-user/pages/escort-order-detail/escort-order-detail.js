// pages/escort-order-detail/escort-order-detail.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    orderId: null,
    order: {},
    loading: true
  },

  onLoad(options) {
    console.log('陪诊员订单详情页加载，参数：', options);
    
    const orderId = options.id;
    if (!orderId) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }

    this.setData({ orderId });
    this.loadOrderDetail();
  },

  onShow() {
    // 每次显示时刷新数据
    if (this.data.orderId) {
      this.loadOrderDetail();
    }
  },

  /**
   * 加载订单详情
   */
  loadOrderDetail() {
    this.setData({ loading: true });

    api.getOrderDetail(this.data.orderId)
      .then(res => {
        console.log('订单详情：', res.data);
        
        const order = res.data;
        order.appointmentTime = this.formatDateTime(order.appointmentTime);
        
        this.setData({
          order: order,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载订单详情失败：', err);
        this.setData({ loading: false });
      });
  },

  /**
   * 开始服务
   */
  startService() {
    wx.showModal({
      title: '开始服务',
      content: '确认开始为患者提供陪诊服务吗？',
      success: (res) => {
        if (res.confirm) {
          this.updateOrderStatus(2);
        }
      }
    });
  },

  /**
   * 完成服务
   */
  completeService() {
    wx.showModal({
      title: '完成服务',
      content: '确认已完成陪诊服务吗？',
      success: (res) => {
        if (res.confirm) {
          this.updateOrderStatus(3);
        }
      }
    });
  },

  /**
   * 更新订单状态
   */
  updateOrderStatus(status) {
    wx.showLoading({ title: '处理中...', mask: true });

    api.updateOrderStatus(this.data.orderId, status)
      .then(() => {
        wx.hideLoading();
        wx.showToast({
          title: status === 2 ? '已开始服务' : '已完成服务',
          icon: 'success'
        });
        this.loadOrderDetail();
      })
      .catch(() => {
        wx.hideLoading();
      });
  },

  /**
   * 联系患者
   */
  contactPatient() {
    const phone = this.data.order.patientPhone;
    wx.showModal({
      title: '联系患者',
      content: `患者电话：${phone}`,
      confirmText: '拨打电话',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: phone
          });
        }
      }
    });
  },

  formatDateTime(str) {
    if (!str) return '';
    return str.replace('T', ' ').substring(0, 16);
  }
});