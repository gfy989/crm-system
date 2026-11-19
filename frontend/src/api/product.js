import request from '../api/request'

// ============ 产品分类 ============
// 后端路径：/api/product-category（vite 代理到 8080）
export function getCategoryTree(params = {}) {
  return request({ url: '/product-category/tree', method: 'get', params })
}

export function getCategory(id) {
  return request({ url: `/product-category/${id}`, method: 'get' })
}

export function createCategory(data) {
  return request({ url: '/product-category', method: 'post', data })
}

export function updateCategory(id, data) {
  return request({ url: `/product-category/${id}`, method: 'put', data })
}

export function changeCategoryStatus(id, status) {
  return request({ url: `/product-category/${id}/status`, method: 'patch', data: { status } })
}

export function deleteCategory(id) {
  return request({ url: `/product-category/${id}`, method: 'delete' })
}
