// utils/request.js
const app = getApp();

const request = {
  get(url, params) {
    return this._request('GET', url, params);
  },

  post(url, data) {
    return this._request('POST', url, data);
  },

  put(url, data) {
    return this._request('PUT', url, data);
  },

  delete(url, data) {
    return this._request('DELETE', url, data);
  },

  _request(method, url, data) {
    const baseUrl = app.globalData.baseUrl;
    const token = wx.getStorageSync('token');
    const fullUrl = baseUrl + url;

    console.log('=== 开始请求 ===');
    console.log('完整URL:', fullUrl);
    console.log('请求方法:', method);
    console.log('请求数据:', data);
    console.log('Token:', token);

    return new Promise((resolve, reject) => {
      wx.request({
        url: fullUrl,
        method: method,
        data: data,
        header: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : ''
        },
        timeout: 60000,
        success: (res) => {
          console.log('=== 请求成功 ===');
          console.log('响应状态码:', res.statusCode);
          console.log('响应数据:', res.data);

          if (res.statusCode === 200) {
            const result = res.data;
            if (result.code === 200) {
              resolve(result);
            } else if (result.code === 401) {
              wx.removeStorageSync('token');
              wx.removeStorageSync('userInfo');
              wx.redirectTo({
                url: '/pages/login/login'
              });
              reject(result);
            } else {
              wx.showToast({
                title: result.message || '请求失败',
                icon: 'none'
              });
              reject(result);
            }
          } else {
            reject({ message: '网络请求失败' });
          }
        },
        fail: (err) => {
          console.error('=== 请求失败 ===', err);
          wx.showToast({
            title: '网络连接失败',
            icon: 'none'
          });
          reject(err);
        },
        complete: () => {
          console.log('=== 请求结束 ===');
        }
      });
    });
  }
};

module.exports = request;