import request from '@/utils/request'
import type {
  ProductSpuDTO, ProductCategoryDTO, ProductCategoryTreeNode, PageQuery,
  ProductSkuSelectDTO, ProductSkuSelectQuery,
} from '@/types'

export function getProductList(params: PageQuery & { spuName?: string; productCode?: string; categoryId?: number; status?: number }) {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

export function getProductCategoryTree() {
  return request({
    method: 'get'
  })
}

export function addProductCategory(data: ProductCategoryDTO) {
  return request({
    url: '/product/categories',
    method: 'post',
    data
  })
}

export function updateProductCategory(data: ProductCategoryDTO) {
  return request({
    url: '/product/categories',
    method: 'put',
    data
  })
}

export function deleteProductCategory(id: number) {
  return request({
    url: `/product/categories/${id}`,
    method: 'delete'
  })
}

export function getProductSkuPage(params: ProductSkuSelectQuery) {
  return request({
    url: '/product/sku/page',
    method: 'get',
    params
  })
}

export function getProductDetail(id: number) {
  return request({
    method: 'get'
  })
}

export function addProduct(data: ProductSpuDTO) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

export function updateProduct(data: ProductSpuDTO) {
  return request({
    url: '/product',
    method: 'put',
    data
  })
}

export function deleteProduct(id: number) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

export function checkProductCode(productCode: string, excludeId?: number) {
  return request({
    method: 'get',
    params: { productCode, excludeId }
  })
}
