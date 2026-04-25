// pages/order-evaluate/order-evaluate.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    orderId: null,
    order: {},
    rating: 0,
    tagsList: [
      { name: '服务态度好', selected: false },
      { name: '专业靠谱', selected: false },
      { name: '耐心细致', selected: false },
      { name: '准时守信', selected: false },
      { name: '热情周到', selected: false },
      { name: '经验丰富', selected: false }
    ],
    comment: '',
    anonymous: false
  },

  onLoad(options) {
    console.log('评价页面加载，参数：', options);
    
    const orderId = options.orderId;
    if (!orderId) {
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
      orderId: orderId
    });

    this.loadOrderDetail();
  },

  /**
   * 加载订单详情
   */
  loadOrderDetail() {
    api.getOrderDetail(this.data.orderId)
      .then(res => {
        console.log('订单详情：', res.data);
        
        const order = res.data;
        
        // 格式化日期
        if (order.appointmentTime) {
          order.appointmentTime = this.formatDateTime(order.appointmentTime);
        }

        this.setData({
          order: order
        });
      })
      .catch(err => {
        console.error('加载订单详情失败：', err);
      });
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
   * 选择评分
   */
  selectRating(e) {
    const rating = e.currentTarget.dataset.rating;
    this.setData({
      rating: rating
    });
  },

  /**
   * 切换标签
   */
  toggleTag(e) {
    const index = e.currentTarget.dataset.index;
    const tagsList = this.data.tagsList;
    tagsList[index].selected = !tagsList[index].selected;
    
    this.setData({
      tagsList: tagsList
    });
  },

  /**
   * 评价内容输入
   */
  onCommentInput(e) {
    this.setData({
      comment: e.detail.value
    });
  },

  /**
   * 匿名选项变化
   */
  onAnonymousChange(e) {
    const anonymous = e.detail.value.length > 0;
    this.setData({
      anonymous: anonymous
    });
  },

  /**
   * 提交评价
   */
  submitEvaluation() {
    // 验证评分
    if (this.data.rating === 0) {
      wx.showToast({
        title: '请先评分',
        icon: 'none'
      });
      return;
    }

    // 获取选中的标签
    const selectedTags = this.data.tagsList
      .filter(tag => tag.selected)
      .map(tag => tag.name)
      .join(',');

    // 组装评价数据
    const evaluationData = {
      orderId: this.data.orderId,
      rating: this.data.rating,
      tags: selectedTags,
      comment: this.data.comment,
      anonymous: this.data.anonymous ? 1 : 0
    };

    console.log('提交评价数据：', evaluationData);

    wx.showLoading({
      title: '提交中...',
      mask: true
    });

    api.createEvaluation(evaluationData)
      .then(res => {
        wx.hideLoading();
        console.log('评价提交成功：', res);
        
        wx.showModal({
          title: '评价成功',
          content: '感谢您的评价！',
          showCancel: false,
          success: () => {
            // 返回订单列表
            wx.navigateBack();
          }
        });
      })
      .catch(err => {
        wx.hideLoading();
        console.error('评价提交失败：', err);
      });
  }
});