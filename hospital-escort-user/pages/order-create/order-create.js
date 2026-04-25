// pages/order-create/order-create.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    hospitalId: null,
    hospitalName: '',
    department: '',
    
    // 就诊人列表
    patientList: [],
    selectedPatientId: null,
    inputDisabled: false,
    
    formData: {
      patientName: '',
      patientPhone: '',
      patientIdCard: '',
      serviceType: '普通陪诊',
      specialRequirements: ''
    },
    
    appointmentDate: '',
    appointmentTime: '',
    minDate: '',
    
    serviceTypes: ['普通陪诊', '全程陪诊', '专家陪诊'],
    serviceTypeIndex: 0,
    
    agreed: false
  },

  onLoad(options) {
    console.log('订单创建页加载，参数：', options);
    
    const { hospitalId, hospitalName, department } = options;
    
    if (!hospitalId || !hospitalName || !department) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }

    // 设置最小日期为今天
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    this.setData({
      hospitalId,
      hospitalName,
      department,
      minDate: `${year}-${month}-${day}`
    });

    // 加载常用就诊人
    this.loadPatients();
  },

  /**
   * 加载常用就诊人
   */
  loadPatients() {
    api.getPatientList()
      .then(res => {
        const list = res.data || [];
        this.setData({ patientList: list });

        // 如果有默认就诊人，自动选中
        const defaultPatient = list.find(p => p.isDefault === 1);
        if (defaultPatient) {
          this.selectPatientData(defaultPatient);
        }
      })
      .catch(err => {
        console.error('加载就诊人失败：', err);
      });
  },

  /**
   * 选择就诊人
   */
  selectPatient(e) {
    const item = e.currentTarget.dataset.item;
    this.selectPatientData(item);
  },

  /**
   * 填充就诊人数据
   */
  selectPatientData(item) {
    this.setData({
      selectedPatientId: item.id,
      inputDisabled: true,
      'formData.patientName': item.name,
      'formData.patientPhone': item.phone || '',
      'formData.patientIdCard': item.idCard || ''
    });
  },

  /**
   * 选择手动输入
   */
  selectManual() {
    this.setData({
      selectedPatientId: 0,
      inputDisabled: false,
      'formData.patientName': '',
      'formData.patientPhone': '',
      'formData.patientIdCard': ''
    });
  },

  /**
   * 跳转就诊人管理
   */
  goPatientList() {
    wx.navigateTo({
      url: '/pages/patient-list/patient-list'
    });
  },

  /**
   * 表单输入
   */
  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`formData.${field}`]: e.detail.value });
  },

  /**
   * 日期选择
   */
  onDateChange(e) {
    this.setData({ appointmentDate: e.detail.value });
  },

  /**
   * 时间选择
   */
  onTimeChange(e) {
    this.setData({ appointmentTime: e.detail.value });
  },

  /**
   * 服务类型选择
   */
  onServiceTypeChange(e) {
    const index = e.detail.value;
    this.setData({
      serviceTypeIndex: index,
      'formData.serviceType': this.data.serviceTypes[index]
    });
  },

  /**
   * 协议勾选
   */
  onAgreementChange(e) {
    this.setData({ agreed: e.detail.value.length > 0 });
  },

  /**
   * 表单验证
   */
  validateForm() {
    const { patientName, patientPhone } = this.data.formData;
    const { appointmentDate, appointmentTime } = this.data;

    if (!patientName) {
      wx.showToast({ title: '请输入就诊人姓名', icon: 'none' });
      return false;
    }
    if (!patientPhone) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' });
      return false;
    }
    if (!/^1[3-9]\d{9}$/.test(patientPhone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' });
      return false;
    }
    if (!appointmentDate) {
      wx.showToast({ title: '请选择就诊日期', icon: 'none' });
      return false;
    }
    if (!appointmentTime) {
      wx.showToast({ title: '请选择就诊时间', icon: 'none' });
      return false;
    }
    return true;
  },

  /**
   * 提交订单
   */
  submitOrder() {
    if (!this.validateForm()) return;

    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意服务协议', icon: 'none' });
      return;
    }

    const orderData = {
      hospitalId: parseInt(this.data.hospitalId),
      hospitalName: this.data.hospitalName,
      department: this.data.department,
      appointmentTime: `${this.data.appointmentDate}T${this.data.appointmentTime}:00`,
      patientName: this.data.formData.patientName,
      patientPhone: this.data.formData.patientPhone,
      patientIdCard: this.data.formData.patientIdCard,
      serviceType: this.data.formData.serviceType,
      specialRequirements: this.data.formData.specialRequirements
    };

    console.log('提交订单数据：', orderData);

    wx.showLoading({ title: '提交中...', mask: true });

    api.createOrder(orderData)
      .then(res => {
        wx.hideLoading();
        console.log('订单创建成功：', res.data);
        
        wx.showModal({
          title: '订单提交成功',
          content: '订单已提交，等待陪诊员接单',
          showCancel: false,
          success: () => {
            wx.redirectTo({
              url: `/pages/order-detail/order-detail?id=${res.data.id}`
            });
          }
        });
      })
      .catch(err => {
        wx.hideLoading();
        console.error('订单创建失败：', err);
      });
  }
});