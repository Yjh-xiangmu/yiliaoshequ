// pages/hospital-detail/hospital-detail.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    hospitalId: null,
    hospital: {},
    selectedDepartment: '',
    loading: true
  },

  onLoad(options) {
    console.log('医院详情页加载，参数：', options);
    
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
      hospitalId: id
    });

    this.loadHospitalDetail();
  },

  /**
   * 加载医院详情
   */
  loadHospitalDetail() {
    this.setData({ loading: true });

    api.getHospitalDetail(this.data.hospitalId)
      .then(res => {
        console.log('医院详情：', res.data);
        
        this.setData({
          hospital: res.data,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载医院详情失败：', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  /**
   * 选择科室
   */
  selectDepartment(e) {
    const department = e.currentTarget.dataset.department;
    console.log('选择科室：', department);
    
    this.setData({
      selectedDepartment: department
    });
  },

  /**
   * 拨打电话
   */
  callHospital() {
    const phone = this.data.hospital.phone;
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
   * 跳转创建订单
   */
  goCreateOrder() {
    if (!this.data.selectedDepartment) {
      wx.showToast({
        title: '请先选择科室',
        icon: 'none'
      });
      return;
    }

    // 跳转到订单创建页面，携带医院和科室信息
    wx.navigateTo({
      url: `/pages/order-create/order-create?hospitalId=${this.data.hospitalId}&hospitalName=${this.data.hospital.name}&department=${this.data.selectedDepartment}`
    });
  }
});