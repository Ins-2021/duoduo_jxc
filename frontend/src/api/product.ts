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
    url: '/product/categories/tree',
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
    url: `/product/${id}`,
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
    url: '/product/check-code',
    method: 'get',
    params: { productCode, excludeId }
  })
}

export function getBrandList(params: PageQuery & { keyword?: string }) {
  return request({
    url: '/product/brand/page',
    method: 'get',
    params
  })
}

export function addBrand(data: { brandName: string; brandCode?: string; remark?: string }) {
  return request({
    url: '/product/brand',
    method: 'post',
    data
  })
}

export function updateBrand(data: { brandId: number; brandName: string; brandCode?: string; remark?: string }) {
  return request({
    url: `/product/brand/${data.brandId}`,
    method: 'put',
    data
  })
}

export function deleteBrand(id: number) {
  return request({
    url: `/product/brand/${id}`,
    method: 'delete'
  })
}
