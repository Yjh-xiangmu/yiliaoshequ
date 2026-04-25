// pages/escort-evaluations/escort-evaluations.js
const api = require('../../utils/api.js');

Page({
  data: {
    evaluationList: [],
    averageRating: '5.0',
    loading: true
  },

  onLoad() {
    this.loadEvaluations();
  },

  loadEvaluations() {
    this.setData({ loading: true });

    api.getEscortEvaluations()
      .then(res => {
        const list = (res.data || []).map(item => {
          return {
            ...item,
            tagList: item.tags ? item.tags.split(',') : [],
            createTime: this.formatDateTime(item.createTime)
          };
        });

        // 计算平均分
        let avg = 5.0;
        if (list.length > 0) {
          const total = list.reduce((sum, item) => sum + item.rating, 0);
          avg = (total / list.length).toFixed(1);
        }

        this.setData({
          evaluationList: list,
          averageRating: avg,
          loading: false
        });
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  formatDateTime(str) {
    if (!str) return '';
    return str.replace('T', ' ').substring(0, 16);
  }
});