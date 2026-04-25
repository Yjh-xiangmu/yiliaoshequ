// utils/api.js
const request = require('./request.js');

/**
 * API接口定义
 */
const api = {
  // ========== 用户相关 ==========
  userLogin: (code, nickname, avatar) => {
    return request.post('/user/login', { code, nickname, avatar });
  },
  
  testUserLogin: () => {
    return request.post('/test/user-login');
  },
  
  getUserInfo: () => {
    return request.get('/user/info');
  },
  
  updateUser: (data) => {
    return request.put('/user/update', data);
  },

  // ========== 陪诊员相关 ==========
  escortLogin: (phone, password) => {
    return request.post('/escort/login', { phone, password });
  },

  // ========== 医院相关 ==========
  getHospitalList: (current = 1, size = 10, keyword = '') => {
    return request.get('/hospital/list', { current, size, keyword });
  },
  
  getAllHospitals: () => {
    return request.get('/hospital/all');
  },
  
  getHospitalDetail: (id) => {
    return request.get(`/hospital/detail/${id}`);
  },

  // ========== 订单相关 ==========
  createOrder: (data) => {
    return request.post('/order/create', data);
  },
  
  cancelOrder: (orderId, reason) => {
    return request.post(`/order/cancel/${orderId}`, { reason });
  },
  
  getMyOrders: (status = null, current = 1, size = 10) => {
    const params = { current, size };
    if (status !== null) {
      params.status = status;
    }
    return request.get('/order/my-orders', params);
  },
  
  getOrderDetail: (orderId) => {
    return request.get(`/order/detail/${orderId}`);
  },

  // ========== 陪诊员订单相关 ==========
  getPendingOrders: (current = 1, size = 10) => {
    return request.get('/escort/pending-orders', { current, size });
  },

  acceptOrder: (orderId) => {
    return request.post(`/escort/accept/${orderId}`);
  },

  // 获取陪诊员的订单列表
  getEscortOrders: (status = null, current = 1, size = 10) => {
    const params = { current, size };
    if (status !== null) {
      params.status = status;
    }
    return request.get('/escort/my-orders', params);
  },

  // 更新订单状态
  updateOrderStatus: (orderId, status) => {
    return request.post(`/order/update-status/${orderId}`, { status });
  },
  createEvaluation: (data) => {
    return request.post('/evaluation/create', data);
  },
  // 更新用户信息
updateUserInfo: (data) => {
  return request.post('/user/update-info', data);
},

// 获取我的评价列表
getMyEvaluations: () => {
  return request.get('/evaluation/my-list');
},
  // 获取订单评价
  getEvaluation: (orderId) => {
    return request.get(`/evaluation/order/${orderId}`);
  },
  // ========== 就诊人相关 ==========
getPatientList: () => {
  return request.get('/patient/list');
},

addPatient: (data) => {
  return request.post('/patient/add', data);
},

updatePatient: (data) => {
  return request.post('/patient/update', data);
},

deletePatient: (id) => {
  return request.delete(`/patient/delete/${id}`);
},

setDefaultPatient: (id) => {
  return request.post(`/patient/set-default/${id}`);
},
// ========== 陪诊员申请相关 ==========
submitEscortApply: (data) => {
  return request.post('/escort-apply/submit', data);
},

getApplyStatus: (phone) => {
  return request.get('/escort-apply/status', { phone });
},
// ========== 支付相关 ==========
createPayment: (orderId) => {
  return request.post(`/payment/create/${orderId}`);
},

mockPay: (paymentId) => {
  return request.post(`/payment/mock-pay/${paymentId}`);
},

getPaymentStatus: (orderId) => {
  return request.get(`/payment/status/${orderId}`);
},
// ========== 陪诊员收入相关 ==========
getEscortIncome: () => {
  return request.get('/escort-income/list');
},

getEscortTotalIncome: () => {
  return request.get('/escort-income/total');
},
getEscortStatistics: () => {
  return request.get('/escort/statistics');
},
// 获取陪诊员收到的评价
getEscortEvaluations: () => {
  return request.get('/evaluation/escort-list');
},
};

module.exports = api;

