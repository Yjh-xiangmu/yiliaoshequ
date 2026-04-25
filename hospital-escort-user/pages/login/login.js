// pages/login/login.js
const app = getApp();
const api = require('../../utils/api.js');

Page({
  data: {
    loginType: 'user',  // user: 用户, escort: 陪诊员
    isDev: true,        // 是否开发环境
    phone: '',
    password: ''
  },

  onLoad(options) {
    console.log('登录页加载');
    
    // 检查是否已登录
    if (app.isLogin()) {
      console.log('已登录，跳转到首页');
      const userType = app.globalData.userInfo.userType;
      if (userType === 'escort') {
        wx.redirectTo({
          url: '/pages/escort-home/escort-home'
        });
      } else {
        wx.redirectTo({
          url: '/pages/index/index'
        });
      }
    }
  },

  /**
   * 切换登录类型
   */
  switchRole(e) {
    const type = e.currentTarget.dataset.type;
    console.log('切换角色：', type);
    this.setData({
      loginType: type,
      phone: '',
      password: ''
    });
  },

  /**
   * 手机号输入
   */
  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    });
  },

  /**
   * 密码输入
   */
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },

  /**
   * 微信授权登录（用户）
   */
  handleWxLogin() {
    const that = this;
    
    console.log('开始微信授权登录');
    
    // 获取用户信息
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        console.log('获取用户信息成功：', res.userInfo);
        
        // 获取微信登录凭证code
        wx.login({
          success: (loginRes) => {
            if (loginRes.code) {
              console.log('获取code成功：', loginRes.code);
              
              // 调用后端登录接口
              that.doUserLogin(loginRes.code, res.userInfo.nickName, res.userInfo.avatarUrl);
            } else {
              console.error('获取code失败');
              wx.showToast({
                title: '登录失败，请重试',
                icon: 'none'
              });
            }
          },
          fail: (err) => {
            console.error('wx.login失败：', err);
            wx.showToast({
              title: '登录失败',
              icon: 'none'
            });
          }
        });
      },
      fail: (err) => {
        console.log('用户取消授权', err);
        wx.showToast({
          title: '需要授权才能使用',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 测试登录（开发用）
   */
  handleTestLogin() {
    console.log('测试登录开始');
    
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    api.testUserLogin()
      .then(res => {
        wx.hideLoading();
        console.log('测试登录成功：', res.data);
        
        // 保存用户信息，标记为用户类型
        const userInfo = {
          ...res.data,
          userType: 'user'
        };
        app.setUserInfo(userInfo, res.data.token);
        
        wx.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 1500
        });

        // 跳转到用户首页
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/index/index'
          });
        }, 1500);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('测试登录失败：', err);
      });
  },

  /**
   * 陪诊员登录
   */
  handleEscortLogin() {
    const { phone, password } = this.data;

    // 验证
    if (!phone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return;
    }

    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return;
    }

    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }

    // 调用陪诊员登录接口
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    api.escortLogin(phone, password)
      .then(res => {
        wx.hideLoading();
        console.log('陪诊员登录成功：', res.data);
        
        // 保存用户信息，标记为陪诊员类型
        const userInfo = {
          ...res.data,
          userType: 'escort'
        };
        app.setUserInfo(userInfo, res.data.token);
        
        wx.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 1500
        });

        // 跳转到陪诊员首页
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/escort-home/escort-home'
          });
        }, 1500);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('陪诊员登录失败：', err);
      });
  },
/**
 * 跳转申请页面
 */
goApply() {
  wx.navigateTo({
    url: '/pages/escort-apply/escort-apply'
  });
},
  /**
   * 忘记密码
   */
  handleForgotPassword() {
    wx.showModal({
      title: '忘记密码',
      content: '请联系管理员重置密码\n客服电话：400-123-4567',
      showCancel: false
    });
  },

  /**
   * 用户登录接口
   */
  doUserLogin(code, nickname, avatar) {
    console.log('调用后端用户登录接口，code:', code);
    
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    api.userLogin(code, nickname, avatar)
      .then(res => {
        wx.hideLoading();
        console.log('用户登录成功：', res.data);
        
        // 保存用户信息，标记为用户类型
        const userInfo = {
          ...res.data,
          userType: 'user'
        };
        app.setUserInfo(userInfo, res.data.token);
        
        wx.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 1500
        });

        // 跳转到用户首页
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/index/index'
          });
        }, 1500);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('用户登录失败：', err);
      });
  }
});
