// pages/order-detail/order-detail.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    orderId: null,
    order: {},
    loading: true
  },

  onLoad(options) {
    console.log('订单详情页，参数:', options);
    
    const id = options.id;
    if (!id) {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }

    this.setData({
      orderId: id
    });

    this.loadOrderDetail();
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
        
        // 格式化日期
        if (order.appointmentTime) {
          order.appointmentTime = this.formatDateTime(order.appointmentTime);
        }
        if (order.createTime) {
          order.createTime = this.formatDateTime(order.createTime);
        }

        this.setData({
          order: order,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载订单详情失败：', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
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
   * 拨打陪诊员电话
   */
  callEscort() {
    const phone = this.data.order.escortPhone;
    if (!phone) {
      wx.showToast({
        title: '暂无联系电话',
        icon: 'none'
      });
      return;
    }

    wx.showModal({
      title: '拨打电话',
      content: `确定拨打 ${phone} 吗？`,
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: phone,
            fail: () => {
              wx.showToast({
                title: '拨号失败',
                icon: 'none'
              });
            }
          });
        }
      }
    });
  },

  /**
   * 取消订单
   */
  cancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      placeholderText: '请输入取消原因',
      editable: true,
      success: (res) => {
        if (res.confirm) {
          const reason = res.content || '用户主动取消';
          this.doCancelOrder(reason);
        }
      }
    });
  },

  /**
   * 执行取消订单
   */
  doCancelOrder(reason) {
    wx.showLoading({
      title: '取消中...',
      mask: true
    });

    api.cancelOrder(this.data.orderId, reason)
      .then(res => {
        wx.hideLoading();
        console.log('订单取消成功：', res);
        
        wx.showModal({
          title: '订单已取消',
          content: '订单已成功取消',
          showCancel: false,
          success: () => {
            // 返回订单列表
            wx.navigateBack();
          }
        });
      })
      .catch(err => {
        wx.hideLoading();
        console.error('订单取消失败：', err);
      });
  },
  /**
 * 跳转评价页面
 */
goEvaluate() {
  wx.navigateTo({
    url: `/pages/order-evaluate/order-evaluate?orderId=${this.data.orderId}`
  });
},
/**
 * 去支付
 */
goPayment() {
  wx.navigateTo({
    url: `/pages/order-payment/order-payment?orderId=${this.data.orderId}`
  });
},
});