// utils/util.js

/**
 * 格式化时间
 */
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

/**
 * 格式化日期（不含时间）
 */
const formatDate = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()

  return `${[year, month, day].map(formatNumber).join('-')}`
}

/**
 * 数字补零
 */
const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

/**
 * 订单状态文本
 */
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待接单',
    1: '已接单',
    2: '服务中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

/**
 * 订单状态颜色类
 */
const getOrderStatusClass = (status) => {
  const classMap = {
    0: 'text-warning',
    1: 'text-primary',
    2: 'text-success',
    3: 'text-muted',
    4: 'text-danger'
  }
  return classMap[status] || 'text-muted'
}

/**
 * 显示Toast
 */
const showToast = (title, icon = 'none', duration = 2000) => {
  wx.showToast({
    title,
    icon,
    duration
  })
}

/**
 * 显示确认对话框
 */
const showConfirm = (content, title = '提示') => {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title,
      content,
      success(res) {
        if (res.confirm) {
          resolve(true)
        } else {
          reject(false)
        }
      }
    })
  })
}

module.exports = {
  formatTime,
  formatDate,
  formatNumber,
  getOrderStatusText,
  getOrderStatusClass,
  showToast,
  showConfirm
}