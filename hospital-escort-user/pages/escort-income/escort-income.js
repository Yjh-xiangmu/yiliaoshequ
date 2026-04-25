// pages/escort-income/escort-income.js
const api = require('../../utils/api.js');

Page({
  data: {
    incomeList: [],
    totalIncome: '0.00',
    loading: true
  },

  onLoad() {
    this.loadIncome();
  },

  loadIncome() {
    this.setData({ loading: true });

    api.getEscortIncome()
      .then(res => {
        const list = (res.data || []).map(item => {
          return {
            ...item,
            createTime: this.formatDateTime(item.createTime)
          };
        });

        const total = list.reduce((sum, item) => {
          return sum + parseFloat(item.amount || 0);
        }, 0);

        this.setData({
          incomeList: list,
          totalIncome: total.toFixed(2),
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