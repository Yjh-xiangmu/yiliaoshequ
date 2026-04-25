// pages/my-evaluations/my-evaluations.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    evaluationList: [],
    loading: true
  },

  onLoad() {
    this.loadEvaluations();
  },

  /**
   * 加载评价列表
   */
  loadEvaluations() {
    this.setData({ loading: true });

    api.getMyEvaluations()
      .then(res => {
        console.log('评价列表：', res.data);

        const list = (res.data || []).map(item => {
          return {
            ...item,
            tagList: item.tags ? item.tags.split(',') : [],
            createTime: this.formatDateTime(item.createTime)
          };
        });

        this.setData({
          evaluationList: list,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载评价列表失败：', err);
        this.setData({ loading: false });
      });
  },

  formatDateTime(str) {
    if (!str) return '';
    return str.replace('T', ' ').substring(0, 16);
  }
});