// pages/order-payment/order-payment.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    orderId: null,
    order: {},
    payment: {},
    paymentRecord: null,
    isPaid: false,
    paying: false
  },

  onLoad(options) {
    console.log('支付页面加载，参数：', options);

    const orderId = options.orderId;
    if (!orderId) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }

    this.setData({ orderId });
    this.loadData();
  },

  /**
   * 加载数据
   */
  loadData() {
    wx.showLoading({ title: '加载中...', mask: true });

    // 并行加载订单信息和支付信息
    Promise.all([
      api.getOrderDetail(this.data.orderId),
      api.getPaymentStatus(this.data.orderId)
    ])
      .then(([orderRes, paymentRes]) => {
        wx.hideLoading();

        const order = orderRes.data;
        const paymentRecord = paymentRes.data;

        // 如果已有支付记录且已支付
        if (paymentRecord && paymentRecord.status === 1) {
          this.setData({
            order,
            payment: {
              amount: order.price,
              orderNo: order.orderNo
            },
            paymentRecord,
            isPaid: true
          });
          return;
        }

        // 创建支付记录
        return api.createPayment(this.data.orderId)
          .then(res => {
            this.setData({
              order,
              payment: res.data,
              isPaid: false
            });
          });
      })
      .catch(err => {
        wx.hideLoading();
        console.error('加载支付信息失败：', err);
      });
  },

  /**
   * 确认支付
   */
  confirmPay() {
    const payment = this.data.payment;
    if (!payment.id) {
      wx.showToast({ title: '支付信息错误', icon: 'none' });
      return;
    }

    wx.showModal({
      title: '确认支付',
      content: `确认支付 ¥${payment.amount} 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doPay();
        }
      }
    });
  },

  /**
   * 执行支付
   */
  doPay() {
    this.setData({ paying: true });

    wx.showLoading({ title: '支付中...', mask: true });

    api.mockPay(this.data.payment.id)
      .then(res => {
        wx.hideLoading();
        this.setData({
          paying: false,
          isPaid: true,
          paymentRecord: res.data
        });

        wx.showModal({
          title: '支付成功 🎉',
          content: `支付流水号：${res.data.paymentNo}`,
          showCancel: false,
          confirmText: '查看订单',
          success: () => {
            this.goOrderDetail();
          }
        });
      })
      .catch(() => {
        wx.hideLoading();
        this.setData({ paying: false });
      });
  },

  /**
   * 查看订单详情
   */
  goOrderDetail() {
    wx.redirectTo({
      url: `/pages/order-detail/order-detail?id=${this.data.orderId}`
    });
  }
});