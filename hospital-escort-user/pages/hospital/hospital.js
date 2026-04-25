// pages/hospital/hospital.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    keyword: '',
    hospitalList: [],
    loading: true
  },

  onLoad() {
    console.log('医院列表页加载');
    this.loadHospitals();
  },

  /**
   * 搜索输入
   */
  onSearchInput(e) {
    this.setData({
      keyword: e.detail.value
    });
  },

  /**
   * 执行搜索
   */
  onSearch() {
    console.log('搜索医院：', this.data.keyword);
    this.loadHospitals();
  },

  /**
   * 加载医院列表
   */
  loadHospitals() {
    this.setData({ loading: true });

    api.getAllHospitals()
      .then(res => {
        console.log('医院列表：', res.data);
        
        const hospitals = res.data || [];
        
        // 过滤搜索关键词
        let filteredList = hospitals;
        if (this.data.keyword) {
          const keyword = this.data.keyword.toLowerCase();
          filteredList = hospitals.filter(hospital => {
            return hospital.name.toLowerCase().includes(keyword) ||
                   hospital.address.toLowerCase().includes(keyword);
          });
        }

        this.setData({
          hospitalList: filteredList,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载医院列表失败：', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  /**
   * 跳转医院详情
   */
  goHospitalDetail(e) {
    const id = e.currentTarget.dataset.id;
    console.log('跳转医院详情：', id);
    
    wx.navigateTo({
      url: `/pages/hospital-detail/hospital-detail?id=${id}`
    });
  }
});