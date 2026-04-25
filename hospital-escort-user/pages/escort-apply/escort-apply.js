// pages/escort-apply/escort-apply.js
const api = require('../../utils/api.js');

Page({
  data: {
    applyInfo: null,
    form: {
      realName: '',
      phone: '',
      idCardNo: '',
      password: '',
      intro: '',
      idCardFront: '',
      idCardBack: '',
      healthCert: '',
      escortCert: ''
    }
  },

  onLoad() {
    // 这是公开页面，无需登录
  },

  onShow() {
    this.checkApplyStatus();
  },

  /**
   * 检查申请状态
   */
  checkApplyStatus() {
    const phone = this.data.form.phone;
    if (!phone) return;

    api.getApplyStatus(phone)
      .then(res => {
        if (res.data) {
          this.setData({ applyInfo: res.data });
        }
      })
      .catch(() => {});
  },

  /**
   * 重新申请
   */
  reApply() {
    this.setData({ applyInfo: null });
  },

  /**
   * 表单输入
   */
  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

/**
 * 上传图片（转Base64）
 */
uploadImage(e) {
  const field = e.currentTarget.dataset.field;

  wx.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0];
      console.log('选择图片：', tempFilePath);

      wx.showLoading({ title: '处理中...', mask: true });

      // 读取图片文件并转Base64
      const fs = wx.getFileSystemManager();
      fs.readFile({
        filePath: tempFilePath,
        encoding: 'base64',
        success: (fileRes) => {
          wx.hideLoading();
          
          // 保存Base64数据（带data URI前缀，方便前端显示）
          const base64Data = 'data:image/jpeg;base64,' + fileRes.data;
          this.setData({ [`form.${field}`]: base64Data });
          
          wx.showToast({ title: '上传成功', icon: 'success' });
          console.log('Base64长度：', fileRes.data.length);
        },
        fail: (err) => {
          wx.hideLoading();
          console.error('读取文件失败：', err);
          wx.showToast({ title: '读取文件失败', icon: 'none' });
        }
      });
    }
  });
},

  /**
   * 表单验证
   */
  validateForm() {
    const { realName, phone, idCardNo, password, idCardFront, idCardBack, healthCert } = this.data.form;

    if (!realName) {
      wx.showToast({ title: '请输入真实姓名', icon: 'none' });
      return false;
    }
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return false;
    }
    if (!idCardNo || idCardNo.length !== 18) {
      wx.showToast({ title: '请输入正确的身份证号', icon: 'none' });
      return false;
    }
    if (!password || password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return false;
    }
    if (!idCardFront) {
      wx.showToast({ title: '请上传身份证正面', icon: 'none' });
      return false;
    }
    if (!idCardBack) {
      wx.showToast({ title: '请上传身份证背面', icon: 'none' });
      return false;
    }
    if (!healthCert) {
      wx.showToast({ title: '请上传健康证', icon: 'none' });
      return false;
    }
    return true;
  },

  /**
   * 提交申请
   */
  submitApply() {
    if (!this.validateForm()) return;

    wx.showModal({
      title: '确认提交',
      content: '请确认所有信息真实有效，提交后等待审核',
      success: (res) => {
        if (res.confirm) {
          this.doSubmit();
        }
      }
    });
  },

  doSubmit() {
    wx.showLoading({ title: '提交中...', mask: true });

    api.submitEscortApply(this.data.form)
      .then(() => {
        wx.hideLoading();
        wx.showModal({
          title: '提交成功',
          content: '您的申请已提交，请等待审核（1-3个工作日）',
          showCancel: false,
          success: () => {
            this.setData({
              applyInfo: {
                status: 0,
                phone: this.data.form.phone
              }
            });
          }
        });
      })
      .catch(() => {
        wx.hideLoading();
      });
  }
});