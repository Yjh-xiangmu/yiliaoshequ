// pages/patient-list/patient-list.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    patientList: [],
    loading: true,
    showModal: false,
    editingId: null,
    relations: ['本人', '父母', '子女', '配偶', '其他'],
    relationIndex: 0,
    form: {
      name: '',
      gender: 1,
      age: '',
      phone: '',
      idCard: '',
      relation: '本人'
    }
  },

  onLoad() {
    this.loadPatients();
  },

  onShow() {
    this.loadPatients();
  },

  loadPatients() {
    this.setData({ loading: true });
    api.getPatientList()
      .then(res => {
        this.setData({
          patientList: res.data || [],
          loading: false
        });
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  showAddModal() {
    this.setData({
      showModal: true,
      editingId: null,
      relationIndex: 0,
      form: { name: '', gender: 1, age: '', phone: '', idCard: '', relation: '本人' }
    });
  },

  hideModal() {
    this.setData({ showModal: false });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  setGender(e) {
    this.setData({ 'form.gender': e.currentTarget.dataset.gender });
  },

  onRelationChange(e) {
    const index = e.detail.value;
    this.setData({
      relationIndex: index,
      'form.relation': this.data.relations[index]
    });
  },

  editPatient(e) {
    const item = e.currentTarget.dataset.item;
    const relationIndex = this.data.relations.indexOf(item.relation);
    this.setData({
      showModal: true,
      editingId: item.id,
      relationIndex: relationIndex >= 0 ? relationIndex : 0,
      form: {
        name: item.name,
        gender: item.gender,
        age: item.age ? String(item.age) : '',
        phone: item.phone || '',
        idCard: item.idCard || '',
        relation: item.relation
      }
    });
  },

  savePatient() {
    if (!this.data.form.name) {
      wx.showToast({ title: '请输入姓名', icon: 'none' });
      return;
    }

    const data = {
      ...this.data.form,
      age: this.data.form.age ? parseInt(this.data.form.age) : null
    };

    wx.showLoading({ title: '保存中...', mask: true });

    const request = this.data.editingId
      ? api.updatePatient({ ...data, id: this.data.editingId })
      : api.addPatient(data);

    request
      .then(() => {
        wx.hideLoading();
        wx.showToast({ title: '保存成功', icon: 'success' });
        this.setData({ showModal: false });
        setTimeout(() => this.loadPatients(), 1000);
      })
      .catch(() => {
        wx.hideLoading();
      });
  },

  deletePatient(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.showModal({
      title: '删除就诊人',
      content: `确定删除"${name}"吗？`,
      success: (res) => {
        if (res.confirm) {
          api.deletePatient(id)
            .then(() => {
              wx.showToast({ title: '删除成功', icon: 'success' });
              setTimeout(() => this.loadPatients(), 1000);
            });
        }
      }
    });
  },

  setDefault(e) {
    const id = e.currentTarget.dataset.id;
    api.setDefaultPatient(id)
      .then(() => {
        wx.showToast({ title: '设置成功', icon: 'success' });
        setTimeout(() => this.loadPatients(), 1000);
      });
  }
});