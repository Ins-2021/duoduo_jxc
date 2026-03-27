package com.duoduo.jxc.mapper;

import com.duoduo.jxc.dto.product.ProductSkuSelectDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductSelectMapper {

    @Select("""
            <script>
            SELECT
              sku.sku_id AS id,
              spu.spu_id AS spuId,
              spu.spu_name AS name,
              CASE
                WHEN (sku.attr1 IS NULL OR sku.attr1 = '') AND (sku.attr2 IS NULL OR sku.attr2 = '') THEN ''
                WHEN (sku.attr1 IS NULL OR sku.attr1 = '') THEN sku.attr2
                WHEN (sku.attr2 IS NULL OR sku.attr2 = '') THEN sku.attr1
                ELSE CONCAT(sku.attr1, '|', sku.attr2)
              END AS attributes,
              '' AS spec,
              COALESCE(brand.brand_name, '无') AS brand,
              COALESCE(spu.unit, '') AS unit,
              sku.wholesale_price AS wholesalePrice,
              sku.purchase_price AS purchasePrice,
              sku.retail_price AS retailPrice,
              COALESCE(SUM(inv.qty), 0) AS stock,
              COALESCE(cat.category_name, '') AS category,
              COALESCE(sku.sku_code, '') AS barcode,
              '' AS productCode,
              '' AS defaultWarehouse,
              '' AS location,
              0 AS exchangePoints,
              COALESCE(spu.unit, '') AS stockUnit,
              0 AS stockValue,
              '' AS remark
            FROM jxc_product_sku sku
            JOIN jxc_product_spu spu ON spu.spu_id = sku.spu_id AND spu.deleted = 0
            LEFT JOIN jxc_product_category cat ON cat.category_id = spu.category_id AND cat.deleted = 0
            LEFT JOIN jxc_product_brand brand ON brand.brand_id = spu.brand_id AND brand.deleted = 0
            LEFT JOIN jxc_inventory inv ON inv.sku_id = sku.sku_id
            WHERE sku.deleted = 0
            <if test="keyword != null and keyword != ''">
              AND (
                spu.spu_name LIKE CONCAT('%', #{keyword}, '%')
                OR sku.sku_code LIKE CONCAT('%', #{keyword}, '%')
                OR sku.attr1 LIKE CONCAT('%', #{keyword}, '%')
                OR sku.attr2 LIKE CONCAT('%', #{keyword}, '%')
              )
            </if>
            <if test="categoryId != null">
              AND spu.category_id = #{categoryId}
            </if>
            GROUP BY sku.sku_id, spu.spu_id, spu.spu_name, sku.attr1, sku.attr2, brand.brand_name, spu.unit,
                     sku.wholesale_price, sku.purchase_price, sku.retail_price, cat.category_name, sku.sku_code
            ORDER BY spu.spu_id DESC, sku.sku_id DESC
            LIMIT #{offset}, #{limit}
            </script>
            """)
    List<ProductSkuSelectDTO> selectSkuPage(@Param("keyword") String keyword,
                                           @Param("categoryId") Long categoryId,
                                           @Param("offset") long offset,
                                           @Param("limit") long limit);

    @Select("""
            <script>
            SELECT COUNT(1) FROM (
              SELECT sku.sku_id
              FROM jxc_product_sku sku
              JOIN jxc_product_spu spu ON spu.spu_id = sku.spu_id AND spu.deleted = 0
              WHERE sku.deleted = 0
              <if test="keyword != null and keyword != ''">
                AND (
                  spu.spu_name LIKE CONCAT('%', #{keyword}, '%')
                  OR sku.sku_code LIKE CONCAT('%', #{keyword}, '%')
                  OR sku.attr1 LIKE CONCAT('%', #{keyword}, '%')
                  OR sku.attr2 LIKE CONCAT('%', #{keyword}, '%')
                )
              </if>
              <if test="categoryId != null">
                AND spu.category_id = #{categoryId}
              </if>
              GROUP BY sku.sku_id
            ) t
            </script>
            """)
    long countSku(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}

